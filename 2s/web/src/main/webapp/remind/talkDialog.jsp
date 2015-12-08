<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" type="text/css" href="styles/talk-dialog<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/talkDialog<%=ConfigController.getBuildVersion()%>.js"></script>
</head>

<div id="talkDialog" class="content" style="display: none;">
    <input id="wsUrl" type="hidden" value="${wsUrl}">
    <input id="shopId" type="hidden" value="${shopId}">
    <input id="talk_start" type="hidden" value="0">
    <input id="talk_limit" type="hidden" value="5">

    <div class="trajectory">
        <div class="date_w">
            <div class="history">
                <a id="moreBtn">查看历史消息</a>
            </div>
        </div>
    </div>
    <div id="contentBody" class="padding10">


    </div>
    <div class="add_txt">
        <a href="#" class="w20 fl"><img width="24" src="./images/wx_mirror_images/xiao.jpg"/></a>

        <div class="add_txt_input w50 fl">
            <input id="content" name="" type="text">
        </div>
        <div class="fr" style="margin-top:4px;"><a id="sendBtn" class="blue_radius">发送</a></div>
    </div>
    <div class="clr"></div>
</div>
