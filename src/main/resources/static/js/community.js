
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















