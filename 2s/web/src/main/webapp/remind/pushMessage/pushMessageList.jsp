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
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/page/remind/pushMessageList<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"MESSAGE_CENTER");
        <bcgogo:permissionParam  permissions="WEB.SCHEDULE.MESSAGE_CENTER.RECEIVER.DELETE">
            APP_BCGOGO.Permission.Schedule.MessageCenter.ReceiverDelete = ${WEB_SCHEDULE_MESSAGE_CENTER_RECEIVER_DELETE};
        </bcgogo:permissionParam>
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
            <jsp:param name="currPage" value="${currPage}"/>
        </jsp:include>
        <div class="right" style="height: 100%">
            <div class="request">
                <div class="top"></div>
                <div class="body line">
                    <form id="searchPushMessageForm" method="post" name="thisform">
                        <input type="hidden" name="relatedObjectId" id="relatedObjectId" value="${searchMessageCondition.relatedObjectId}"/>
                        <input type="hidden" name="category" id="category" value="${searchMessageCondition.category}"/>
                        <input type="hidden" name="topCategory" id="topCategory" value="${searchMessageCondition.topCategory}"/>
                        <table width="99%" border="0">
                            <tr>
                                <td width="6%" align="center"><label for="selectAll"><input id="selectAll" type="checkbox" style="vertical-align: bottom;margin-right: 3px;"/>全选</label></td>
                                <td width="5%">
                                    <bcgogo:hasPermission permissions="WEB.SCHEDULE.MESSAGE_CENTER.RECEIVER.DELETE">
                                        <div id="batchDeletePushMessageBtn"><img style="cursor:pointer;vertical-align: bottom; margin-right: 3px;" src="images/cha.png" /><span class="span_a" style="">删除</span></div>
                                    </bcgogo:hasPermission>
                                </td>
                                <td width="8%" align="center"><div id="markMultiReadBtn"><img src="images/wei_open.png" style="cursor:pointer;vertical-align: bottom; margin-right: 3px;"/><span class="span_a">标记已读</span></div></td>
                                <td width="11%" align="center"><div id="markAllReadBtn"><img src="images/wei_all_open.png" style="cursor:pointer;vertical-align: bottom; margin-right: 3px;"/><span class="span_a">全部标记已读</span></div></td>
                                <td width="22%" align="right">
                                    <select class="txt" style="color: #000000" name="dayRange" id="dayRange">
                                        <option value="">显示全部</option>
                                        <c:forEach items="${messageDayRanges}" var="messageDayRange">
                                            <option value="${messageDayRange}">${messageDayRange.name}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td width="13%">
                                    <input type="text" style="color: #000000" class="txt" id="keyWord" name="keyWord" placeholder="请输入关键字" />
                                </td>
                                <td width="15%" align="right">
                                    <label for="receiverStatus"><input type="checkbox" style="vertical-align: bottom;margin-right: 3px;" ${searchMessageCondition.receiverStatus=="UNREAD"?"checked":""} id="receiverStatus" name="receiverStatus" value="UNREAD" /><span class="gray_color">只显示未处理/未读</span></label>
                                </td>
                                <td width="10%" align="right">
                                    <div class="search-btn" style="cursor: pointer" id="searchPushMessageBtn"><img src="images/magnifier.png" />搜索</div></td>
                            </tr>
                        </table>
                    </form>
                    <div id="pushMessageTables">
                    </div>

                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="pushMessage.do?method=searchReceiverPushMessage"></jsp:param>
                        <jsp:param name="jsHandleJson" value="drawPushMessageTable"></jsp:param>
                        <jsp:param name="dynamical" value="_pushMessage"></jsp:param>
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
<div id="refuse_msg_dialog" style="display: none;">
    您是否确定拒绝请求，若是则请填写拒绝理由：
    <textarea id="refuse_msg" placeholder="拒绝理由" maxLength=70 style="width:270px;height: 63px;margin-top: 7px;"></textarea>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

<%@include file="/remind/sendMaintainMessage.jsp" %>


</body>
</html>
