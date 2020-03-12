//用户名检查主要逻辑
function usernameCheck(){
    var username=$("#username").val();
    if ($.trim(username) ===''){
        $("#nameTip").text("用户名不能为空");
        return 1;
    }else if (username.length<3 || username.length >15){
        $("#nameTip").text("用户名必须是3-15个字符");
        return 1;
    }
    else {
        $("#nameTip").text("");
        return 0;
    }
}

//密码检查主要逻辑
function pwdCheck(){
    var password=$("#password").val();
    var password1=$("#password1").val();

    if ($.trim(password) ===''){
        $("#pwdTip").text("密码不能为空");
        return 1;
    }else if (password.length<6 || password.length >16){
        $("#pwdTip").text("密码必须是6-16个字符");
        return 1;
    }else if(password1 !=='' && password !== password1){
        $("#pwdTip").text("两次密码输入不一致");
        return 1;
    }
    else {
        $("#pwdTip").text("");
        return 0;
    }
}

//确认密码
function pwdConfirmCheck(){
    var password1=$("#password1").val();
    var password=$("#password").val();

    if (password !== password1){
        $("#pwdTip").text("两次密码输入不一致");
        return 1;
    }
    $("#pwdTip").text("");
    return 0;
}

function resetSubmitChick() {
    var checkPwd=pwdCheck();
    var checkName=usernameCheck();
    var urlStr=location.pathname;
    var index=urlStr.lastIndexOf('\/');
    var username=$("#username").val();
    var password=$("#password").val();
    var password1=$("#password1").val();
    urlStr=urlStr.substring(index+1,urlStr.length);
    var tips=$("#resetError");
    if (checkPwd+checkName==0){
        $.ajax({
            type:"POST",
            url:"/resetpassword/"+urlStr,
            data:{
                "username":username,
                "password":password,
                "password1":password1

            },
            success:function (result) {
              if (result.code ==200){
                  location.href="/login"
              }else if (result.code== 2015){
                  tips.text(result.message);
              }else if (result.code=2016){
                  tips.text(result.message)
              }else if (result.code==2017){
                  tips.text(result.message)
              }
            }
        });
    }

}