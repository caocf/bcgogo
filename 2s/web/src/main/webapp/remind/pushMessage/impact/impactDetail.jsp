<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <meta charset="UTF-8"/>
     <link rel="stylesheet" type="text/css" href="styles/talk-dialog<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/talkDialog<%=ConfigController.getBuildVersion()%>.js"></script>
</head>

<div id="impactDetail" class="content" style="display: none;">
     <input id="wsUrl" type="hidden" value="${wsUrl}">
     <input id="shopId" type="hidden" value="${shopId}">
    <input id="talk_start" type="hidden" value="0">
    <input id="talk_limit" type="hidden" value="5">
    <%--<div class="trajectory">--%>
        <%--<div class="date_w">--%>
            <%--<div class="date">2015.4.16 14:20:30</div>--%>
            <%--<div class="history"><a id="moreBtn">查看历史消息</a></div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div id="contentBody" class="padding10">
        <form action="" id="impactDetailForm" method="post">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>碰撞时间：</td>
                    <td>
                        <div  style="width:220px;"  id="impactTime"></div>
                    </td>

                </tr>
                <tr>
                    <td colspan="3" height="5" style="line-height:normal">&nbsp;</td>
                </tr>
                <tr>
                    <td>碰撞地点：</td>
                    <td>
                        <div  style="width:220px;"   id="impactAddress"></div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" height="5" style="line-height:normal">&nbsp;</td>
                </tr>
                <tr>
                    <td>故障码：</td>
                    <td>
                        <div  style="width:220px;"   id="impactFaultCode"></div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" height="5" style="line-height:normal">&nbsp;</td>
                </tr>
                <tr>
                    <td>碰撞车速：</td>
                    <td>
                        <div  style="width:220px;"   id="vss"></div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" height="5" style="line-height:normal">&nbsp;</td>
                </tr>
                <%--<tr>--%>
                    <%--<td>碰撞力度：</td>--%>
                    <%--<td>--%>
                        <%--<div  style="width:220px;"   id="impactF"></div>--%>
                    <%--</td>--%>
                <%--</tr>--%>
            </table>
            </form >
            <div class="clear"></div>
        <%--<div class="dialogue">--%>
            <%--<div class="sj_relative">--%>
                <%--<div class="lan_absolute"><img src="./images/wx_mirror_images/lan_sj.png"></div>--%>
                <%--<div class="w76 fl">--%>
                    <%--<div class="a1 fr">你好，请问你现在的车开到哪边了，--%>
                        <%--陈总让我来接你！--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="w23  fr tr">--%>
                <%--<div class="w99"><img width="59" src="./images/wx_mirror_images/tou.jpg"></div>--%>
            <%--</div>--%>
            <%--<div class="clr"></div>--%>
        <%--</div>--%>



        <%--<div class="dialogue">--%>
            <%--<div class="sj_relative">--%>
                <%--<div class="bai_absolute"><img src="./images/wx_mirror_images/bai_sj.png"></div>--%>
                <%--<div class="w76 fr">--%>
                    <%--<div class="a2 fl">你好，我现在刚刚过宝带西路，你--%>
                        <%--微信多少，我加你下。--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="w23  fl">--%>
                <%--<div class="w99"><img width="59" src="./images/wx_mirror_images/tou2.jpg"></div>--%>
            <%--</div>--%>
            <%--<div class="clr"></div>--%>
        <%--</div>--%>

        <%--<div class="add_txt">--%>
            <%--<a href="#" class="w20 fl"><img width="24" src="./images/wx_mirror_images/xiao.jpg"/></a>--%>

            <%--<div class="add_txt_input w50 fl">--%>
                <%--<input id="content" name="" type="text">--%>
            <%--</div>--%>
            <%--<div class="fr" style="margin-top:4px;"><a id="sendBtn" class="blue_radius">发送</a></div>--%>
        <%--</div>--%>
        <div class="clr"></div>
    </div>
</div>
