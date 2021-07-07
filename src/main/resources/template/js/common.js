$(function (){
    var scroolTop = $(window).scroll(function () {
        //当滚动条的位置处于距顶部100像素以下时，跳转链接出现，否则消失
        console.log($(window).scrollTop())
        if ($(window).scrollTop() > 100) {
            $("#toTop").fadeIn(1500);
            $("#toTop").hover(function() {
                $("#upArrow").hide();
                $("#upText").show();
            }, function () {
                $("#upArrow").show();
                $("#upText").hide();
            })
        } else {
            $("#toTop").fadeOut(1500)
        }
        
    });

    // 当点击跳转链接后，回到页面顶部位置
    $("#toTop").click(function () {
        $("body, html").animate({scrollTop:0}, 1000);
        return false
    })
})()