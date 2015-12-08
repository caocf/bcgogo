<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>我的粉丝</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/wx/wxFanList<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">微信管理</div>
    </div>
    <div class="messageContent">

        <jsp:include page="wxNavi.jsp">
            <jsp:param name="currPage" value="wxFan"/>
        </jsp:include>

        <div class="messageRight">
            <form action="weChat.do?method=toWxSendMessage" class="J_user_container" method="post" style="display: none"></form>

            <div class="messageRight_radius">
                <div class="d-search">
                    <div class="d-search-border">
                        <input type="text" autocomplete="off" placeholder="备注名/昵称" id="keyword_input">

                        <div class="i_search" id="searchBtn"></div>
                    </div>
                </div>
                <%--<div class="stat-group">--%>
                <%--共&nbsp;<b id="inventoryCount" class="blue_color">636</b>&nbsp;种&nbsp;&nbsp;--%>
                <%--数量&nbsp;<b id="inventoryProductAmount" class="yellow_color">5859</b>&nbsp;--%>
                <%--</div>--%>
                <div>

                    <table id="table_FanJob" class="news-table">
                        <col width="40">
                        <col width="110">
                        <col width="200">
                        <col width="200">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <tr class="t-title">
                            <td><input type="checkbox" id="allCheckBox" onclick="allCheckOrNot()"></td>
                            <td>头像</td>
                            <td>昵称</td>
                            <td>备注名</td>
                            <td>绑定车牌号</td>
                            <td>车辆品牌</td>
                            <td>车型</td>
                            <td>本店客户名</td>
                            <td>电话</td>
                        </tr>
                    </table>

                </div>
                <div>
                    <div class="i_pageBtn" style="float:right">
                        <bcgogo:ajaxPaging
                                url="weChat.do?method=initFanList"
                                postFn="initTable_Fan"
                                dynamical="_initTable_Fan"
                                display="none"
                                />
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="d-opr">
            <a id="sendWXMsgBtn">发送微信</a>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

<div id="wxUserEditor" class="wx-user-editor" style="display: none;padding-left:50px">
    <div class="height"></div>
    <div class="d-user-info">
        <div class="user-info-left">
            <img id="headImg" height="80" width="80"
                 src="http://wx.qlogo.cn/mmopen/J6BwNibiapBetFv8eVJFib2fvzOA0IrptiaicVTT1KG20gLHXNNyfz75icCuIdiazYBNshrSNssNsIwaPV3Q8gBJClkzbAMzrq3wCpN/0">
        </div>
        <div class="user-info-right">
            <div>
                昵称:<span id="nickName"></span>
            </div>
            <div>
                地区:<span id="city"></span>
            </div>
        </div>
    </div>
    <div>
        备注名:<input id="remark" class="i-input" placeholder="请输入用户备注名" maxlength="10"/>
    </div>
</div>

</body>
</html>