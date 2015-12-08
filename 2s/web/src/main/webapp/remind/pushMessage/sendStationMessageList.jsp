<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>消息中心——消息中心</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/page/remind/message/stationMessage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"MESSAGE_CENTER");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="i_search">
        <div class="i_searchTitle">消息中心</div>
    </div>
    <div class="i_mainRight" id="i_mainRight">
        <jsp:include page="pushMessageNavi.jsp">
            <jsp:param name="currPage" value="SenderMessage"/>
        </jsp:include>
        <div class="right">
            <div class="request">
                <div class="top"></div>
                <div class="body line">
                    <form id="searchSenderPushMessageForm" method="post" name="thisform">
                        <table width="99%" border="0">
                            <tr>
                                <td width="4%" align="center"><label for="selectAll"><input id="selectAll" type="checkbox" style="vertical-align: bottom;margin-right: 3px;"/>全选</label></td>
                                <td width="5%"><div id="batchDeleteSenderPushMessageBtn"><img style="cursor:pointer;vertical-align: bottom; margin-right: 3px;" src="images/cha.png" /><span class="span_a" style="">删除</span></div></td>
                                <td width="22%" align="right">
                                    <select class="txt" style="color: #000000" name="dayRange" id="dayRange">
                                        <option value="">显示全部</option>
                                        <c:forEach items="${messageDayRanges}" var="messageDayRange">
                                            <option value="${messageDayRange}">${messageDayRange.name}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td width="13%">
                                    <input type="text" style="color: #000000" class="txt" id="receiver" name="receiver" placeholder="收件人" />
                                </td>
                                <td width="10%" align="right">
                                    <div class="search-btn" style="cursor: pointer" id="searchSenderPushMessageBtn"><img src="images/magnifier.png" />搜索</div>
                                </td>
                                <td width="10%" align="right">
                                    <input class="btnDelete J_addNewStationMessage" style="margin: 0 0 3px 10px;float: right" type="button" value="新增消息">
                                </td>
                            </tr>
                        </table>
                    </form>
                    <div id="senderPushMessageTables">
                    </div>

                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="stationMessage.do?method=searchSenderStationMessages"></jsp:param>
                        <jsp:param name="jsHandleJson" value="drawSenderPushMessageTable"></jsp:param>
                        <jsp:param name="dynamical" value="_senderPushMessage"></jsp:param>
                        <jsp:param name="display" value="none"></jsp:param>
                    </jsp:include>
                </div>
                <div class="bottom"></div>
            </div>
            <div style="display:none;border:1px solid #fa981a; background:#f6f0af; float:left; padding:8px 20px 8px 20px;bottom:0px;width: 775px;text-align:center" id="bottomFloatBar">
                <a class="blue_color J_showAllPushMessage">显示所有消息</a>&nbsp;
            </div>
            <div class="height"></div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>
