<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>微信分享</title>
    <%--′⌒`′⌒`不知道什么原因，这里引用了css和js但是完全没有效果，没有编译？路径不对？′⌒`′⌒`--%>
    <%--′⌒`′⌒`最古老的方法：css js 直接放到页面文件里一起。。。。。。。。现在jsp搞得好大了′⌒`′⌒`--%>
    <link rel="stylesheet" type="text/css" href="css/wxShare.css">
    <script src="http://api.map.baidu.com/api?v=2.0&ak=nh73VgKTDOS1LnxhSPvpz9DM"></script>
    <script src="http://apps.bdimg.com/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="js/jgestures.js"></script>
    <script src="js/wxShare.js"></script>
</head>
<style>
    *{
        padding: 0;
        margin: 0;
    }

    /*header*/
    div.header_title{
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 40px;
        padding-top: 10px;
        font-weight: bold;
        font-size: 1.8em;
        background-color: black;
        text-align: center;
        color: white;
        line-height: 30px;
    }

    /*wrap1*/
    div.wrap1{
        position: relative;
        margin-top: 40px;
        width: 100%;
        height: 100%;
        overflow: hidden;
        /*background-color: red;*/
        background: url(images/temp/wrap1_bg.jpg) no-repeat;
        /*background: url(img/wrap1_bg.jpg) no-repeat;*/
        background-size: 100% 100%;
    }
    div.wrap1 div.filling{
        width: 100%;
        height: 2%;
        background-color: black;
    }
    div.wrap1 div.img_big img{
        position: absolute;
        top: 5%;
        right: 15%;
        width: 60%;
        height: 35%;
        -webkit-animation: wrap1_img_big 1.5s ease-out;
        -moz-animation: wrap1_img_big 1.5s ease-out;
        -o-animation: wrap1_img_big 1.5s ease-out;
        animation: wrap1_img_big 1.5s ease-out;
    }
    @-webkit-keyframes wrap1_img_big{ from{ opacity: 0; right: -15%; }to{ right: 15%; opacity: 1; } }
    @-moz-keyframes wrap1_img_big{ from{ opacity: 0; right: -15%; } to { right: 15%; opacity: 1; } }
    @-o-keyframes wrap1_img_big{ from{ opacity: 0; right: -15%; }to{ right: 15%; opacity: 1; } }
    @keyframes wrap1_img_big{ from{ opacity: 0; right: -15%; }to{ right: 15%; opacity: 1; } }
    div.wrap1 div.img_small img{
        position: absolute;
        top: 41%;
        left: 5%;
        width: 25%;
        height: 15%;
        -webkit-animation: wrap1_img_small 1.5s ease-out;
        -moz-animation:  wrap1_img_small 1.5s ease-out;
        -o-animation:  wrap1_img_small 1.5s ease-out;
        animation: wrap1_img_small 1.5s ease-out;
    }
    @-webkit-keyframes wrap1_img_small{ from{ left: -15%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    @-moz-keyframes wrap1_img_small{ from{ left: -15%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    @-o-keyframes wrap1_img_small{ from{ left: -15%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    @keyframes wrap1_img_small{ from{ left: -15%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    div.wrap1 div.content_title{
        position: absolute;
        top: 60%;
        left: 5%;
        width: 95%;
        font-size: 20px;
        font-weight: bold;
        color: white;
    }
    div.wrap1 div.content{
        position: absolute;
        top: 68%;
        left: 5%;
        width: 90%;
        color: white;
        font-size: 18px;
        line-height: 130%;
        font-weight: bold;
        margin-right: 10px;
        margin-bottom: 5px;
        text-indent: 9%;
        -webkit-animation: wrap1_content 1.5s ease-out;
        -moz-animation: wrap1_content 1.5s ease-out;
        -o-animation: wrap1_content 1.5s ease-out;
        animation: wrap1_content 1.5s ease-out;
    }
    @-webkit-keyframes wrap1_content{ from{ left: -50%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    @-moz-keyframes wrap1_content{ from{ left: -50%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    @-o-keyframes wrap1_content{ from{ left: -50%; opacity: 0; }to{ left: 5%; opacity: 1; } }
    @keyframes wrap1_content{ from{ left: -50%; opacity: 0; }to{ left: 5%; opacity: 1; } }

    /*wrap2*/
    div.wrap2{
        position: relative;
        margin-top: 40px;
        width: 100%;
        height: 100%;
        font-weight: bold;
        overflow: hidden;
        /*background-color: red;*/
        background: url(images/temp/wrap2_bg.jpg) no-repeat;
        /*background: url(img/wrap2_bg.jpg) no-repeat;*/
        background-size: 100% 100%;
    }
    div.wrap2 div.filling{
        width: 100%;
        height: 2%;
        background-color: #000000;
    }
    div.wrap2 div.sub_title{
        position: absolute;
        top: 3%;
        left: 2%;
        width: 100%;
        color: white;
        -webkit-animation: wrap2_subtitle 1.5s ease-out 1s;
        -moz-animation: wrap2_subtitle 1.5s ease-out 1s;
        -o-animation: wrap2_subtitle 1.5s ease-out 1s;
        animation: wrap2_subtitle 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap2_subtitle{ from{ opacity: 0; top: -5%; } to{ opacity: 1; top: 3%; } }
    @-moz-keyframes wrap2_subtitle{ from{ opacity: 0; top: -5%; } to{ opacity: 1; top: 3%; } }
    @-o-keyframes wrap2_subtitle{ from{ opacity: 0; top: -5%; } to{ opacity: 1; top: 3%; } }
    @keyframes wrap2_subtitle{ from{ opacity: 0; top: -5%; } to{ opacity: 1; top: 3%; } }
    div.wrap2 div.sub_title .line_1{
        font-size: 1.5em;
    }
    div.wrap2 div.sub_title .line_2{
        font-size: 1.4em;
        line-height: 35px;
        margin-left: 15%;
    }
    div.wrap2 div.img_day img{
        position: absolute;
        top: 18%;
        width: 100%;
        height: 33%;
        -webkit-animation: wrap2_img_day 1.5s ease-out 1s;
        -moz-animation: wrap2_img_day 1.5s ease-out 1s;
        -o-animation: wrap2_img_day 1.5s ease-out 1s;
        animation: wrap2_img_day 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap2_img_day{ from{  top: 0; opacity: 0; } to{  top: 18%; opacity: 1; } }
    @-moz-keyframes wrap2_img_day{ from{  top: 0; opacity: 0; } to{  top: 18%; opacity: 1; } }
    @-o-keyframes wrap2_img_day{ from{  top: 0; opacity: 0; } to{  top: 18%; opacity: 1; } }
    @keyframes wrap2_img_day{ from{  top: 0; opacity: 0; } to{  top: 18%; opacity: 1; } }
    div.wrap2 div.day{
        position: absolute;
        left: 35%;
        top: 52%;
        height: 5%;
        width: 35%;
        text-align: center;
        font-size: 1.5em;
        color: white;
        -webkit-animation: wrap2_day 1.5s ease-out 1s;
        -moz-animation: wrap2_day 1.5s ease-out 1s;
        -o-animation: wrap2_day 1.5s ease-out 1s;
        animation: wrap2_day 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap2_day{ from{top:95%;left:0;opacity:0;} to{top:52%;left:35%;opacity:1;}}
    @-moz-keyframes wrap2_day{ from{top:95%;left:0;opacity:0;} to{top:52%;left:35%;opacity:1;}}
    @-o-keyframes wrap2_day{ from{top:95%;left:0;opacity:0;} to{top:52%;left:35%;opacity:1;}}
    @keyframes wrap2_day{ from{top:95%;left:0;opacity:0;} to{top:52%;left:35%;opacity:1;}}
    div.wrap2 div.img_night img{
        position: absolute;
        top: 58%;
        width: 100%;
        height: 33%;
        -webkit-animation: wrap2_img_night 1.5s ease-out 1s;
        -moz-animation: wrap2_img_night 1.5s ease-out 1s;
        -o-animation: wrap2_img_night 1.5s ease-out 1s;
        animation: wrap2_img_night 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap2_img_night{ from{ top: 40%; opacity: 0;} to{ top: 58%; opacity: 1;} }
    @-moz-keyframes wrap2_img_night{ from{ top: 40%; opacity: 0;} to{ top: 58%; opacity: 1;} }
    @-o-keyframes wrap2_img_night{ from{ top: 40%; opacity: 0;} to{ top: 58%; opacity: 1;} }
    @keyframes wrap2_img_night{ from{ top: 40%; opacity: 0;} to{ top: 58%; opacity: 1;} }
    div.wrap2 div.night{
        position: absolute;
        top: 92%;
        left: 35%;
        width: 35%;
        height: 5%;
        text-align: center;
        font-size: 1.5em;
        color: white;
        -webkit-animation: wrap2_night 1.5s ease-out 1s;
        -moz-animation: wrap2_night 1.5s ease-out 1s;
        -o-animation: wrap2_night 1.5s ease-out 1s;
        animation: wrap2_night 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap2_night{from{top:95%;left:0;opacity:0;} to{top:92%;left:35%;opacity:1;}}
    @-moz-keyframes wrap2_night{from{top:95%;left:0;opacity:0;} to{top:92%;left:35%;opacity:1;}}
    @-o-keyframes wrap2_night{from{top:95%;left:0;opacity:0;} to{top:92%;left:35%;opacity:1;}}
    @keyframes wrap2_night{from{top:95%;left:0;opacity:0;} to{top:92%;left:35%;opacity:1;}}


    /*wrap3*/
    div.wrap3{
        position: relative;
        margin-top: 40px;
        width: 100%;
        height: 100%;
        font-weight: bold;
        overflow: hidden;
        /*background-color: red;*/
        background: url(images/temp/wrap3_bg.png) no-repeat;
        /*background: url(img/wrap3_bg.png) no-repeat;*/
        background-size: 100% 100%;
    }
    div.wrap3 div.filling{
        width: 100%;
        height: 2%;
        background-color: black;
    }
    div.wrap3 div.drive{
        position: absolute;
        top: 3%;
        left: 2%;
        color: white;
        -webkit-animation: wrap3_drive 1.5s ease-out 1s;
        -moz-animation: wrap3_drive 1.5s ease-out 1s;
        -o-animation: wrap3_drive 1.5s ease-out 1s;
        animation: wrap3_drive 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap3_drive{ from{ top: -20%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    @-moz-keyframes wrap3_drive{ from{ top: -20%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    @-o-keyframes wrap3_drive{ from{ top: -20%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    @keyframes wrap3_drive{ from{ top: -20%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    div.wrap3 div.drive .line_1{
        font-size: 1.2em;
    }
    div.wrap3 div.drive .line_2{
        margin-left: 10%;
        font-size: 1.1em;
    }
    div.wrap3 div.img_drive img{
        position: absolute;
        top: 22%;
        width: 100%;
        height: 25%;
        -webkit-animation: wrap3_img_drive 1.5s ease-out 1s;
        -moz-animation: wrap3_img_drive 1.5s ease-out 1s;
        -o-animation: wrap3_img_drive 1.5s ease-out 1s;
        animation: wrap3_img_drive 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap3_img_drive{ from{left: -100%; opacity: 0;} to{left: 0; opacity: 1;} }
    @-moz-keyframes wrap3_img_drive{ from{left: -100%; opacity: 0;} to{left: 0; opacity: 1;} }
    @-o-keyframes wrap3_img_drive{ from{left: -100%; opacity: 0;} to{left: 0; opacity: 1;} }
    @keyframes wrap3_img_drive{ from{left: -100%; opacity: 0;} to{left: 0; opacity: 1;} }
    div.wrap3 div.stop{
        position: absolute;
        top: 48%;
        left: 2%;
        color: white;
        -webkit-animation: wrap3_stop 1.5s ease-out 1s;
        -moz-animation: wrap3_stop 1.5s ease-out 1s;
        -o-animation: wrap3_stop 1.5s ease-out 1s;
        animation: wrap3_stop 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap3_stop{ from{ top: 25%; opacity: 0; } to{ top: 48%; opacity: 1; } }
    @-moz-keyframes wrap3_stop{ from{ top: 25%; opacity: 0; } to{ top: 48%; opacity: 1; } }
    @-o-keyframes wrap3_stop{ from{ top: 25%; opacity: 0; } to{ top: 48%; opacity: 1; } }
    @keyframes wrap3_stop{ from{ top: 25%; opacity: 0; } to{ top: 48%; opacity: 1; } }
    div.wrap3 div.stop .line_1{
        font-size: 1.2em;
    }
    div.wrap3 div.stop .line_2{
        margin-left: 10%;
        font-size: 1.1em;
    }
    div.wrap3 div.img_stop img{
        position: absolute;
        top: 71%;
        width: 100%;
        height: 25%;
        -webkit-animation: wrap3_img_stop 1.5s ease-out 1s;
        -moz-animation: wrap3_img_stop 1.5s ease-out 1s;
        -o-animation: wrap3_img_stop 1.5s ease-out 1s;
        animation: wrap3_img_stop 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap3_img_stop{ from{ left: -100%;opacity: 0; } to{ left: 0;opacity: 1; } }
    @-moz-keyframes wrap3_img_stop{ from{ left: -100%;opacity: 0; } to{ left: 0;opacity: 1; } }
    @-o-keyframes wrap3_img_stop{ from{ left: -100%;opacity: 0; } to{ left: 0;opacity: 1; } }
    @keyframes wrap3_img_stop{ from{ left: -100%;opacity: 0; } to{ left: 0;opacity: 1; } }


    /*wrap4*/
    div.wrap4{
        position: relative;
        margin-top: 40px;
        width: 100%;
        height: 100%;
        font-weight: bold;
        overflow: hidden;
        /*background-color: red;*/
        background: url(images/temp/wrap4_bg.jpg) no-repeat;
        /*background: url(img/wrap4_bg.jpg) no-repeat;*/
        background-size: 100% 100%;
    }
    div.wrap4 div.filling{
        width: 100%;
        height: 2%;
        background-color: black;
    }
    div.wrap4 div.up_title{
        position: absolute;
        top: 7%;
        left: 8%;
        font-size: 1.5em;
        color: white;
        -webkit-animation: wrap4_up_title 1.5s ease-out 1s;
        -moz-animation: wrap4_up_title 1.5s ease-out 1s;
        -o-animation: wrap4_up_title 1.5s ease-out 1s;
        animation: wrap4_up_title 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap4_up_title{from{top:-10%;left:-10%;opacity:0} to{top:8%; left:7%;opacity:1}}
    @-moz-keyframes wrap4_up_title{from{top:-10%;left:-10%;opacity:0} to{top:8%;left:7%;opacity:1}}
    @-o-keyframes wrap4_up_title{from{top:-10%;left:-10%;opacity:0} to{top:8%;left:7%;opacity:1}}
    @keyframes wrap4_up_title{from{top:-10%;left:-10%;opacity:0} to{top:8%;left:7%;opacity:1}}
    div.wrap4 div.img_center img{
        position: absolute;
        top: 18%;
        width: 100%;
        height: 45%;
        -webkitanimation: wrap4_img_center 1.5s ease-out 1s;
        -moz-animation: wrap4_img_center 1.5s ease-out 1s;
        -o-animation: wrap4_img_center 1.5s ease-out 1s;
        animation: wrap4_img_center 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap4_img_center{ from{ top: -10%; opacity: 0; } to{ top: 18%; opacity: 1; } }
    @-moz-keyframes wrap4_img_center{ from{ top: -10%; opacity: 0; } to{ top: 18%; opacity: 1; } }
    @-o-keyframes wrap4_img_center{ from{ top: -10%; opacity: 0; } to{ top: 18%; opacity: 1; } }
    @keyframes wrap4_img_center{ from{ top: -10%; opacity: 0; } to{ top: 18%; opacity: 1; } }
    div.wrap4 div.down_title{
        position: absolute;
        top: 72%;
        left: 5%;
        font-size: 1.4em;
        color: white;
        -webkit-animation: wrap4_down_title 1.5s ease-out 1s;
        -moz-animation: wrap4_down_title 1.5s ease-out 1s;
        -oanimation: wrap4_down_title 1.5s ease-out 1s;
        animation: wrap4_down_title 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap4_down_title{ from{ -webkit-transform:   rotateX(0deg); color:  white; }
        to{ -webkit-transform: rotateX(360deg); color: orange; } }
    @-moz-keyframes wrap4_down_title{ from{ -moz-transform:   rotateX(0deg); color:  white; }
        to{ -moz-transform: rotateX(360deg); color: orange; } }
    @-o-keyframes wrap4_down_title{ from{ -o-transform:   rotateX(0deg); color:  white; }
        to{ -o-transform: rotateX(360deg); color: orange; } }
    @keyframes wrap4_down_title{ from{ transform:   rotateX(0deg); color:  white; }
        to{ transform: rotateX(360deg); color: orange; } }
    div.wrap4 div.down_title .line_1{
        position: relative;
        left: 70%;
        display: block;
        width: 100%;
    }
    div.wrap4 div.down_title .line_2{
        position: relative;
        left: 130%;
        display: block;
        margin-top: 20px;
        width: 100%;
    }


    /*wrap5*/
    div.wrap5{
        position: relative;
        margin-top: 40px;
        width: 100%;
        height: 100%;
        font-weight: bold;
        overflow: hidden;
        /*background-color: red;*/
        background: url(images/temp/wrap5_bg.png) no-repeat;
        /*background: url(img/wrap5_bg.png) no-repeat;*/
        background-size: 100% 100%;
    }
    div.wrap5 div.filling{
        width: 100%;
        height: 2%;
        background: black;
    }
    div.wrap5 div.img_up img{
        position: absolute;
        top: 2%;
        width: 100%;
        height: 45%;
        -webkit-animation: wrap5_img_up 1.5s ease-out 1s;
        -moz-animation: wrap5_img_up 1.5s ease-out 1s;
        -o-animation: wrap5_img_up 1.5s ease-out 1s;
        animation: wrap5_img_up 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap5_img_up{ from{ top: -20%; opacity: 0; } to{ top: 2%; opacity: 1; } }
    @-moz-keyframes wrap5_img_up{ from{ top: -20%; opacity: 0; } to{ top: 2%; opacity: 1; } }
    @-o-keyframes wrap5_img_up{ from{ top: -20%; opacity: 0; } to{ top: 2%; opacity: 1; } }
    @keyframes wrap5_img_up{ from{ top: -20%; opacity: 0; } to{ top: 2%; opacity: 1; } }
    div.wrap5 ul.left{
        position: absolute;
        top: 62%;
        left: 10%;
        font-size: 1.5em;
        color: yellow;
    }
    div.wrap5 ul.left li{
        color: white;
        font-size: 0.8em;
        margin-top: 20%;
        list-style: none;
        -webkit-animation: wrap5_li_left 1.5s ease-out 1s;
        -moz-animation: wrap5_li_left 1.5s ease-out 1s;
        -o-animation: wrap5_li_left 1.5s ease-out 1s;
        animation: wrap5_li_left 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap5_li_left{ from{  left: -10%;  -webkit-transform: rotate(0deg);   opacity: 0; }
        to{    left:  10%;  -webkit-transform: rotate(360deg); opacity: 1; } }
    @-moz-keyframes wrap5_li_left{ from{  left: -10%;  -moz-transform:   rotate(0deg);  opacity: 0; }
        to{    left:  10%;  -moz-transform: rotate(360deg);  opacity: 1; } }
    @-o-keyframes wrap5_li_left{ from{  left: -10%;  -o-transform:  rotate(0deg);   opacity: 0; }
        to{  left:  10%;  -o-transform: rotate(360deg);  opacity: 1; } }
    @keyframes wrap5_li_left{ from{  left: -10%;  transform:   rotate(0deg);  opacity: 0; }
        to{  left:  10%;  transform: rotate(360deg);  opacity: 1; } }
    div.wrap5 ul.right{
        position: absolute;
        top: 52%;
        right: 12%;
        font-size: 1.5em;
        color: #C2F44A;
    }
    div.wrap5 ul.right li{
        color: white;
        font-size: 0.8em;
        margin-top: 8%;
        list-style: none;
        -webkit-animation: wrap5_li_left 1.5s ease-out 1s;
        -moz-animation: wrap5_li_left 1.5s ease-out 1s;
        -o-animation: wrap5_li_left 1.5s ease-out 1s;
        animation: wrap5_li_left 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap5_li_right{ from{ transform: rotate(0deg);    opacity: 0; }
        to{ transform: rotate(-360deg); opacity: 1; } }
    @-moz-keyframes wrap5_li_right{ from{ transform: rotate(0deg);    opacity: 0; }
        to{ transform: rotate(-360deg); opacity: 1; } }
    @-o-keyframes wrap5_li_right{ from{ transform: rotate(0deg);    opacity: 0; }
        to{ transform: rotate(-360deg); opacity: 1; } }
    @keyframes wrap5_li_right{ from{ transform: rotate(0deg);    opacity: 0; }
        to{ transform: rotate(-360deg); opacity: 1; } }
    div.wrap5 ul li span{
        display: inline-block;
        width: 12px;
        height: 12px;
        border-radius: 6px;
        background: white;
    }



    /*wrap6*/
    div.wrap6{
        position: relative;
        margin-top: 40px;
        width: 100%;
        height: 100%;
        font-weight: bold;
        font-family: "microsoft yahei";
        overflow: hidden;
        color: #555;
        /*background-color: red;*/
        background: url(images/temp/wrap6_bg.png) no-repeat;
        /*background: url(img/wrap6_bg.png) no-repeat;*/
        background-size: 100% 100%;
    }
    div.wrap6 div.filling{
        position: absolute;
        width: 100%;
        height: 2%;
        background: black;
    }
    div.wrap6 div.up_title{
        /*position: absolute;
        top: 5%;
        left: 5%;*/
    }
    div.wrap6 div.up_title img{
        position: absolute;
        top: 3%;
        left: 10%;
        width: 25%;
        height: 18%;
        -webkit-animation: wrap6_up_title 1.5s ease-out 1s;
        -moz-animation: wrap6_up_title 1.5s ease-out 1s;
        -o-animation: wrap6_up_title 1.5s ease-out 1s;
        animation: wrap6_up_title 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap6_up_title{ from{ top: -10%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    @-moz-keyframes wrap6_up_title{ from{ top: -10%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    @-o-keyframes wrap6_up_title{ from{ top: -10%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    @keyframes wrap6_up_title{ from{ top: -10%; opacity: 0; } to{ top: 3%; opacity: 1; } }
    div.wrap6 div.up_title .title{
        position: absolute;
        top: 9%;
        left: 38%;
        font-size: 1.5em;
        word-spacing: 0.8em;
        vertical-align: 80%;
    }
    div.wrap6 div.buy_method{
        position: absolute;
        top: 25%;
        width: 100%;
        -webkit-animation: wrap6_buy_method 1.5s ease-out 1s;
        -moz-animation: wrap6_buy_method 1.5s ease-out 1s;
        -o-animation: wrap6_buy_method 1.5s ease-out 1s;
        animation: wrap6_buy_method 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap6_buy_method{ from{ top: 10%; opacity: 0; } to{ top: 25%; opacity: 1; } }
    @-moz-keyframes wrap6_buy_method{ from{ top: 10%; opacity: 0; } to{ top: 25%; opacity: 1; } }
    @-o-keyframes wrap6_buy_method{ from{ top: 10%; opacity: 0; } to{ top: 25%; opacity: 1; } }
    @keyframes wrap6_buy_method{ from{ top: 10%; opacity: 0; } to{ top: 25%; opacity: 1; } }
    div.wrap6 div.buy_method .tel{
        font-size: 1.3em;
        font-weight: bold;
        text-align: center;
    }
    div.wrap6 div.buy_method .qq{
        font-size: 1.3em;
        font-weight: bold;
        text-align: center;
        margin: 18px 0;
    }
    div.wrap6 div.buy_method .other{
        font-weight: bold;
        text-align: center;
    }
    div.wrap6 div.buy_addr{
        position: absolute;
        top: 84%;
        -webkit-animation: wrap6_buy_addr 1.5s ease-out 1s;
        -moz-animation: wrap6_buy_addr 1.5s ease-out 1s;
        -o-animation: wrap6_buy_addr 1.5s ease-out 1s;
        animation: wrap6_buy_addr 1.5s ease-out 1s;
    }
    @-webkit-keyframes wrap6_buy_addr{ from{ top: 70%; opacity: 0; } to{ top: 84%; opacity: 1; } }
    @-moz-keyframes wrap6_buy_addr{ from{ top: 70%; opacity: 0; } to{ top: 84%; opacity: 1; } }
    @-o-keyframes wrap6_buy_addr{ from{ top: 70%; opacity: 0; } to{ top: 84%; opacity: 1; } }
    @keyframes wrap6_buy_addr{ from{ top: 70%; opacity: 0; } to{ top: 84%; opacity: 1; } }
    div.wrap6 div.buy_addr .web{
        text-align: center;
        width: 72%;
        margin-left: 9%;
        font-size: 1.1em;
        margin-bottom: 5px;
    }
    div.wrap6 div.buy_addr .addr{
        width: 94%;
        height: 5%;
        margin-top: 5px;
        margin-left: 3%;
        text-align: center;
    }



    /*footer*/
    div.footer div.page{
        position: fixed;
        bottom: 5px;
        right: 2%;
        color: white;
    }
    div.progressbar{
        position: fixed;
        bottom: 0;
        left: 0;
        height: 5px;
        background: orange;
    }
    div#showMap{
        position: absolute;
        top: 53%;
        height: 30%;
        width: 90%;
        margin-left: 5%;
        z-index: 9999;
        opacity: 0;
    }
    .mapLaunch{
        -webkit-animation: mapLaunch 1.5s linear;
        -moz-animation: mapLaunch 1.5s linear;
        -o-animation: mapLaunch 1.5s linear;
        animation: mapLaunch 1.5s linear;
    }
    @-webkit-keyframes mapLaunch {from{height:0;width:0;opacity:0}to{width:90%;height:30%;opacity:1 } }
    @-moz-keyframes mapLaunch {from{height:0;width:0;opacity:0}to{width:90%;height:30%;opacity:1 } }
    @-o-keyframes mapLaunch {from{height:0;width:0;opacity:0}to{width:90%;height:30%;opacity:1 } }
    @keyframes mapLaunch {from{height:0;width:0;opacity:0}to{width:90%;height:30%;opacity:1 } }
    div.turnning div.prevWrap{
        position: absolute;
        top: 7%;
        left: 49%;
        font-weight: bold;
        font-size: 1.1em;
        color: orange;
        -webkit-animation: prevWrap 1.5s linear infinite;
        -moz-animation: prevWrap 1.5s linear infinite;
        -o-animation: prevWrap 1.5s linear infinite;
        animation: prevWrap 1.5s linear infinite;
    }
    @-webkit-keyframes prevWrap { from{ top: 9%; opacity: 0; } to{ top: 7%; opacity: 1; } }
    @-moz-keyframes prevWrap { from{ top: 9%; opacity: 0; } to{ top: 7%; opacity: 1; } }
    @-o-keyframes prevWrap { from{ top: 9%; opacity: 0; } to{ top: 7%; opacity: 1; } }
    @keyframes prevWrap { from{ top: 9%; opacity: 0; } to{ top: 7%; opacity: 1; } }
    div.turnning div.nextWrap{
        position: absolute;
        bottom: 2%;
        right: 49%;
        font-weight: bold;
        font-size: 1.1em;
        color: orange;
        -webkit-animation: nextWrap 1.5s linear infinite;
        -moz-animation: nextWrap 1.5s linear infinite;
        -o-animation: nextWrap 1.5s linear infinite;
        animation: nextWrap 1.5s linear infinite;
    }
    @-webkit-keyframes nextWrap { from{ bottom: 4%; opacity: 0; } to{ bottom: 2%; opacity: 1; } }
    @-moz-keyframes nextWrap { from{ bottom: 4%; opacity: 0; } to{ bottom: 2%; opacity: 1; } }
    @-o-keyframes nextWrap { from{ bottom: 4%; opacity: 0; } to{ bottom: 2%; opacity: 1; } }
    @keyframes nextWrap { from{ bottom: 4%; opacity: 0; } to{ bottom: 2%; opacity: 1; } }
    .wrap2,.wrap3,.wrap4,.wrap5,.wrap6{
        display: none;
    }

</style>
<body>
<div class="container">

    <div class="header_title">利宜行车管家第三代</div>

    <div class="wrap1 box">
        <div class="filling"></div>
        <div class="img_big">
            <img src="images/temp/wrap1_img_big.png">
            <%--<img src="img/wrap1_img_big.png">--%>
        </div>
        <div class="img_small">
            <img src="images/temp/wrap1_img_small.png">
            <%--<img src="img/wrap1_img_small.png">--%>
        </div>
        <div class="content_title">苏州统购信息科技有限公司</div>
        <div class="content">
            利宜行车管家第三代，是一款集成最新车联网技术的行车记录仪。拥有行车记录仪，停车监控，远程定位三大主要功能。车主配合手机APP，可以实时掌握车辆位置，行车轨迹，车辆故障，碰撞视频回放等信息。
        </div>
    </div>

    <div class="wrap2 box">
        <div class="filling"></div>
        <div class="sub_title">
            <span class="line_1">行车记录：</span><br/>
            <span class="line_2">远离碰瓷，从容取证</span>
        </div>
        <div class="img_day">
            <img src="images/temp/wrap2_day.jpg">
            <%--<img src="img/wrap2_day.jpg">--%>
        </div>
        <div class="day">白天图像</div>
        <div class="img_night">
            <img src="images/temp/wrap2_night.jpg">
            <%--<img src="img/wrap2_night.jpg">--%>
        </div>
        <div class="night">夜间图像</div>
    </div>

    <div class="wrap3 box">
        <div class="filling"></div>
        <div class="drive">
            <span class="line_1">行车监控：</span><br/>
			<span class="line_2">内置加速度传感器，发生车辆紧急碰撞时，可自动抓拍碰撞视频，并快速同步到车主手机微信，以及店面后台。
			</span>
        </div>
        <div class="img_drive">
            <img src="images/temp/wrap3_img_drive.jpg">
            <%--<img src="img/wrap3_img_drive.jpg">--%>
        </div>
        <div class="stop">
            <span class="line_1">停车监控：</span><br/>
            <span class="line_2">车辆熄火后自动进入安防监控模式，当车辆发生震动时，自动触发视频拍摄。特有的触发拍摄机制，可以维持7×24小时全天候监护。</span>
        </div>
        <div class="img_stop">
            <img src="images/temp/wrap3_img_stop.jpg">
            <%--<img src="img/wrap3_img_stop.jpg">--%>
        </div>
    </div>

    <div class="wrap4 box">
        <div class="filling"></div>
        <div class="up_title">远程车辆定位</div>
        <div class="img_center">
            <img src="images/temp/wrap4_img.jpg">
            <%--<img src="img/wrap4_img.jpg">--%>
        </div>
        <div class="down_title">
            <span class="line_1">车辆位置</span>
            <span class="line_2">远程获取</span>
        </div>
    </div>

    <div class="wrap5 box">
        <div class="filling"></div>
        <div class="img_up">
            <img src="images/temp/wrap5_img.jpg">
            <%--<img src="img/wrap5_img.jpg">--%>
        </div>
        <ul class="left">三大功能
            <li><span></span> 行车记录仪</li>
            <li><span></span> 停车监控</li>
            <li><span></span> 车辆定位</li>
        </ul>
        <ul class="right">辅助功能
            <li><span></span> 行车轨迹</li>
            <li><span></span> 违章查询</li>
            <li><span></span> 故障查询</li>
            <li><span></span> 车况检查</li>
            <li><span></span> 一键救援</li>
            <li><span></span> 视频回放</li>
            <li><span></span> 在线预约</li>
        </ul>
    </div>

    <div class="wrap6 box">
        <div class="filling"></div>
        <div class="up_title">
            <img src="images/temp/wrap6_img.png" />
            <%--<img src="img/wrap6_img.png" />--%>
            <span class="title">利宜行车管家</span>
        </div>
        <div class="buy_method">
            <div class="tel">购买热线：<span>--</span></div>
            <div class="qq">服务QQ：<span>--</span></div>
            <div class="other">(或前往苏州统购信息指定4S店进行购买)</div>
        </div>
        <div class="buy_addr">
            <div class="web">--</div>
            <div class="addr">地址：--</div>
        </div>
    </div>

    <div class="turnning">
        <div class="prevWrap">↑</div>
        <div class="nextWrap">↓</div>
    </div>

    <div id="showMap"></div>

    <div class="footer">
        <div class="page" style="color: orange;font-weight: bold;"><span class="curPage">1</span>/6</div>
        <div class="progressbar"></div>
    </div>
</div>
<script>


    $(".container").css( "width" , innerWidth);
    $(".container").css( "height" , innerHeight - 45);
    $("div.progressbar").css("width",innerWidth/6);

//        jQuery.ajax({
//            type:"POST",
//            url: " ",
//            dataType:"json",
//            success: function(responseStr){
//                init4sShopInfo(responseStr);
//                init4sShopMap(responseStr);
//            }
//        });
alert(${(wxShareInfo.email).toString()});
    function init4sShopInfo(responseStr){

        $("div.wrap6 div.tel span").text(responseStr.telNo);
        $("div.wrap6 div.qq span").text(responseStr.qq);
        $("div.wrap6 div.addr").text(responseStr.addr);
    }

    function init4sShopMap(responseStr){

        var map = new BMap.Map("showMap");
        //     var point = new BMap.Point(116.331398,39.897445);
        //     map.centerAndZoom(point,15);
        var myGeo = new BMap.Geocoder();
        if(responseStr.addr){
            var detail = responseStr.addr;
            var city = detail.substring(0,2);
            myGeo.getPoint(detail, function(point){
                if (point) {
                    map.centerAndZoom(point, 16);
                    map.addOverlay(new BMap.Marker(point));
                }
            }, city);
        }else{
            $("#showMap").hide();
        }
    }


    var curWrap = 1;
    $(".box").on("swiperight",function(e){
        e.stopPropagation();
        curWrap--;
        if( curWrap <=0){
            curWrap = 6;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });
    $(".box").on("swipeleft",function(e){
        e.stopPropagation();
        curWrap++;
        if( curWrap >=7){
            curWrap = 1;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });
    $(".box").on("swipeup",function(e){
        e.stopPropagation();
        curWrap++;
        if( curWrap >=7){
            curWrap = 1;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });
    $(".box").on("swipedown",function(e){
        e.stopPropagation();
        curWrap--;
        if( curWrap <=0){
            curWrap = 6;
        };
        if( curWrap == 6){
            $("#showMap").css("opacity","1").addClass("mapLaunch");
        }else{
            $("#showMap").css("opacity","0").removeClass("mapLaunch");
        };
        $(".box").hide();
        $("div.progressbar").css("width",innerWidth/6*curWrap);
        $("div.footer span.curPage").text(curWrap);
        $(".box").children().css("opacity","0");
        $(".wrap"+curWrap).slideDown(1000,function(){
            $(".box").children().css("opacity","1");
        });
    });

    var map = new BMap.Map("showMap");
    var point = new BMap.Point(116.331398,39.897445);
    map.centerAndZoom(point,15);
    var myGeo = new BMap.Geocoder();
    var detail = "苏州市工业园区崇文路汉嘉大厦";
    myGeo.getPoint(detail, function(point){
        if (point) {
            map.centerAndZoom(point, 17);
            var marker = new BMap.Marker(point);
            map.addOverlay(marker);
//            map.addOverlay(new BMap.Marker(map.getCenter()));
            marker.setAnimation(BMAP_ANIMATION_BOUNCE);
        }
    }, "苏州");

</script>
</body>
</html>