function emailCheck(){
    var email=$("#email").val();
    var pattern=/^\s*\w+(?:\.{0,1}[\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\.[a-zA-Z]+\s*$/;

    if ($.trim(email) == ''){
        $("#emailTip").text("邮箱不能为空");
        return false;
    }else if (!pattern.test(email)){
        $("#emailTip").text("邮箱格式错误");
        return false;
    }else{
        $("#emailTip").text("");
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

function sendEmail() {
    var email=$("#email").val();
    if (emailCheck()){
        $.ajax({
            type:"POST",
            url:"/resetemail",
            data:{
                "email":email
            },
            success:function (json) {
                $("#emailTip").text(json.message);
            }

        });
        var emailBtn=$("#email-btn");
        emailBtn.addClass("layui-btn-disabled");
        timeClick(emailBtn);
    }



}

