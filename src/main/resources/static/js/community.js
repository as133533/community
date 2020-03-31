
//提交回复
function post() {
    var questionId =$("#question_id").val();
    var content=$("#comment_content").val();
    comment2target(questionId,1,content);
}

//提交二级评论
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content=$("#input-"+commentId).val();
    comment2target(commentId,2,content);
}

function comment2target(targetid,type,content) {
    if (!content){
        alert("不能回复空内容");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({ //转换为json格式的
            "parentId":targetid,
            "content":content,
            "type":type
        }),
        success: function (response) {
            if (response.code == 200){
                window.location.reload();//提交成功页面刷新
                // $("#comment_section").hide();//将对话框隐藏
            }else {
                if (response.code == 2003){//表示如果未登录
                    var isAccepted=confirm(response.message);//confirm会弹出一个带消息的表单确认。如果点确定就返回true
                    if (isAccepted){
                        window.open("http://localhost:8000/login")
                        window.localStorage.setItem("closable",true);//游览器本地存储一个值。用来页面跳转，原先登录成功会跳转到首页，现在希望这里跳转到回复页面，如果将参数传递给服务端判断，势必又要修改很多的东西；
                    }
                } else {
                    alert(response.message);//这个弹出页面上的提示信息
                }

            }

        },
        dataType: "json"
    });

}

//展开二级评论
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.username
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}


function selectTag(e) {
    var value=e.getAttribute("data-tag");
    var previous=$("#tag").val();
    if (previous.indexOf(value) == -1){
        if (previous){
            $("#tag").val(previous +','+value);
        }else{
            $("#tag").val(value);
        }
    }
}


function showSelectTag(){
    $("#select-tag").show();
}


// 弹出气泡
$(function () {
    $('#example').popover({
        html:true
    });
    }
);


function likeCount(e){
    var id = e.getAttribute("data-id");
    var type=e.getAttribute("type-id");
    var isAuth=e.getAttribute("auth-id");

    // anonymousUser这个为spring security在允许登录的页面下给予的默认用户，也就是说，点赞页面是所有人都可以访问的，所以${#authentication.isAuthenticated()}总是为true,只好以这种判断方式来判断用户是否登录
    if (isAuth == "anonymousUser"){
        var isAccepted=confirm("请先登录后再点赞哦！！");//confirm会弹出一个带消息的表单确认。如果点确定就返回true
        if (isAccepted){
            //这里还遗留一个暂时我难以解决的问题，我的目的是登录成功后可以跳转到点赞的网址，但是这个页面的跳转不是拦截性跳转，无法捕捉跳转前的路径，而我的登录是交给spring security的，也就是说，我在代码中正常登录就是跳转到首页
            //要如何从后端获取非拦截性的前端地址呢。
             location.href="/login";
        }
    }

    $.ajax({
        type:"POST",
        url:"/likecount",
        data: {
            "id":id,
            "type":type
        },
        success:function (json) {
            if (json.code==200){
                if (json.data.likeType == "question"){
                    $("#question_like_count").text(json.data.likeCount);
                    if (json.data.isLike){
                        e.classList.add("active");
                    }else {
                        e.classList.remove("active");
                    }
                }else{
                    $("#like_count").text(json.data.likeCount);
                    if (json.data.isLike){
                        e.classList.add("active");
                    }else {
                        e.classList.remove("active");
                    }
                }

            }
        },

        error:function (e) {
            console.log(e.responseText);
        }

    })
}














