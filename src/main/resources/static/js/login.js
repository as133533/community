function usernameCheck(){

    var username = $("#username").val();

    if( $.trim(username) === "" ){

        $("#nameTip").text("*请填写用户名");
        return 1;

    }else{

        $("#nameTip").text('');
        return 0;

    }
}
function changeKaptcha(){
    $("#kaptcha").attr("src","/kaptcha?"+Math.floor(Math.random()*100)); //这里加随机数的目的主要是为了防止缓存而导致图片未更换
}

function loginAndCheck() {

    var flag = usernameCheck();
    if(!flag){
        // $("#loginForm").submit();

        var username=$("#username").val();
        var password=$("#password").val();
        var captchaCode=$("#captchaCode").val();
        var rememberMe=$("#remember-me").prop("checked");
        $.ajax({
            type:"POST",
            url:"/login",
            data:{
                "username":username,
                "password":password,
                "captchaCode":captchaCode,
                "remember-me":rememberMe
            },
            success:function (json) {
                if (json.code === 200){
                    location.href=json.data; //会在后端写入将要跳转的链接

                }else{
                    $("#kaptcha").click();
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
        loginAndCheck();
    }

});
