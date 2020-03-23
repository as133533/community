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
    else if(ajaxCheck(username,1) ==1){
        $("#nameTip").text("该用户名已经被注册");
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




//邮箱检查主要逻辑
function emailCheck(){
    var email=$("#email").val();
    var pattern=/^\s*\w+(?:\.{0,1}[\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\.[a-zA-Z]+\s*$/;

    if ($.trim(email) ===''){
        $("#emailTip").text("邮箱不能为空");
        return 1;
    }else if (!pattern.test(email)){
        $("#emailTip").text("邮箱格式错误");
        return 1;
    }
    else if(ajaxCheck(email,2) ==1){
        $("#emailTip").text("该邮箱已经被注册");
        return 1;
    }else {
        $("#emailTip").text("");
        return 0;
    }
}

//电话号码检查主要逻辑
function phoneCheck(){
    var phone=$("#phone").val();
    var pattern=/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/

    if ($.trim(phone) ===''){
        $("#phoneTip").text("电话号码不能为空");
        return 1;
    }else if (!pattern.test(phone)){
        $("#phoneTip").text("电话号码格式错误");
        return 1;
    }
    else if(ajaxCheck(phone,3) ==1){
        $("#phoneTip").text("该电话号码已经被注册");
        return 1;
    }else {
        $("#phoneTip").text("");
        return 0;
    }
}

//向后端发送ajax请求进行用户名,邮箱，phone的校验
function ajaxCheck(str,type){
    var dupNum=0;
    $.ajax(
        {
            type:"POST",
            url:"/registercheck",
            contentType: "application/json",
            dataType:"text",
            async:false,//注意，这里必须要是同步的，否则如果是异步的话，ajax在进行异步执行结果还没有返回的时候，dupNum就已经返回了。
            data:JSON.stringify({
                "checkName":str,
                "type":type
            }),
            success:function (json) {
                dupNum=json;


            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(errorThrown);
            },

        }
    );
    return dupNum;
}

function submitCheck() {
    var a=usernameCheck()+pwdCheck()+pwdConfirmCheck()+phoneCheck()+emailCheck();
    var username=$("#username").val();
    var email=$("#email").val();
    var phone=$("#phone").val();
    var password=$("#password").val();
    var password1=$("#password1").val();

    if (a==0){
        $.ajax({
            type:"POST",
            url: "/register",
            data: {
              "username":username,
              "email":email,
              "phone":phone,
              "password":password,
              "password1":password1
            },
            success:function (json) {
                if (json.code ==200){
                    location.href='/login';
                }
                else{
                    $("#registerError").text(json.message);
                }

            }
        });
    }

}