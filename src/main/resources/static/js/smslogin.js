function phoneCheck(){
    var phone=$("#phone").val();
    var pattern=/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/
    if ($.trim(phone) ==''){
        $("#phoneTip").text("电话号码不能为空");
        return false;
    }else if(!pattern.test(phone)){
        $("#phoneTip").text("电话号码格式不正确");
        return false;
    }else{
        $("#phoneTip").text('');
    }
    return true;

}

function smsCodeCheck(){
    var smsCode=$("#smsCode").val();
    if ($.trim(smsCode) ==''){
        $("#smsCodeTip").text("验证码不能为空");
        return false;
    }
    else{
        $("#smsCode").text('');
    }
    return true;
}

var wait=30;//全局变量
//验证码按钮30s发送一次
function timeClick(o) {

    if (wait==0){
        $(o).attr("disabled", false);
        $(o).removeClass("layui-btn-disabled");
        $(o).val("发送短信");
        wait=30;
    }else {
        $(o).attr("disabled", true);
        $(o).val("重新发送("+wait+")");

        wait--;
        setTimeout(function () {
            timeClick(o);
        },1000);
    }
}


function getSmsCode() {
    var flag = phoneCheck();
    if(flag){
        var phone=$("#phone").val();
        // var rememberMe=$("#remember-me").prop("checked");
        $.ajax({
            type: "GET",
            contentType:"application/json",
            url: "/smscode",
            data: {
                "phone": phone
            },
            success: function (json) {
                if(json.code==200){
                    var smsBtn=$("#sms-btn");
                    smsBtn.addClass("layui-btn-disabled");
                    timeClick(smsBtn);

                }else{
                    alert(json.message)
                }
            },
            error: function (e) {
                console.log(e.responseText);
            }
        });


    }else{
        return false;
    }

}


function smsLoginAndCheck() {

    var flag = phoneCheck();
    var smsFlag=smsCodeCheck();
    if(flag && smsFlag){
        var phone=$("#phone").val();
        var smsCode=$("#smsCode").val();
        // var rememberMe=$("#remember-me").prop("checked");
        $.ajax({
            type:"POST",
            url:"/smslogin",
            data:{
                "phone":phone,
                "smsCode":smsCode,
            },
            success:function (json) {
                console.log(json)
                if (json.code === 200){
                    location.href='/'; //会在后端写入将要跳转的链接

                }else{
                    $("#loginError").text(json.message);

                }
            },
            error:function (e) {
                console.log(e.responseText);

            }
        });


    }else{
        return false;
    }

}

$('#login-div').bind('keyup', function(event) {

    var theEvent = window.event;
    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;

    if (code === 13) {
        //回车执行登录
        smsLoginAndCheck();
    }

});