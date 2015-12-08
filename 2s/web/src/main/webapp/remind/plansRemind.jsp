
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-9-10
  Time: 下午3:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>待办事项-计划提醒</title>

    <link rel="stylesheet" type="text/css" href="styles/returnsTan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/plansRemind<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/danjuCg<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/plansRemind<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/plansRemind<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-customerSms-input<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"SCHEDULE_REMIND_PLAN");

        function limit_textarea_input() {
            $("textarea[maxlength]").bind('input propertychange', function() {
                var maxLength = $(this).attr('maxlength');
                if ($(this).val().length > maxLength) {
                    $(this).val($(this).val().substring(0, maxLength));
                }
            })
        }
        $().ready(function() {
//            $("#remindContext").textarealimit();
            limit_textarea_input();
        });
    </script>
</head>
<body class="bodyMain" >
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="tableStatus" value="totalRows"/>

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">自定义提醒</div>
        <%--<jsp:include page="remindNavi.jsp">--%>
            <%--<jsp:param name="currPage" value="plansRemindNaviMenu"/>--%>
        <%--</jsp:include>--%>
    </div>
    <div class="i_mainRight" id="i_mainRight">
        <div class="tuihuo_first">
            <span class="left_tuihuo"></span>

            <table class="jihua_tb tixing_tt">
                <col width="11%"/>
                <col width="39%"/>
                <col width="11%"/>
                <col width="39%"/>
                <tr style="padding-top:10px">
                    <td class="title">
                        <img src="images/xinhao.png" />
                        提醒项目：
                    </td>
                    <td>
                        <input id="remindType" maxlength="20" type="text" style="width:180px;" class="textbox" />
                        <label>(最多20个字)</label>
                    </td>
                    <td class="title">
                        <img src="images/xinhao.png" />
                        提醒时间：
                    </td>
                    <td>
                        <input id="remindTime" type="text" class="isDatepickerInited textbox" value="" onclick="showDatepicker(this)" name="inDatess">
                    </td>
                </tr>
                <tr style="padding-bottom:10px">
                    <td class="title pb-10">内容：</td>
                    <td colspan="3" class="pb-10">
                        <textarea id="remindContext" maxLength="400" style="width:780px;" class="textarea"></textarea>
                        <label>(最多400个字)</label>
                    </td>
                </tr>
            </table>

            <span class="right_tuihuo"></span>
        </div>
        <div id="testDiv" class="clearfix" style="padding-bottom:10px;"></div>
        <input type="hidden" id="addContent">

        <div class="clear"></div>
        <div class="tuihuo_tb tixing_tb">
            <table class="tui_title">
                <col width="80"/>
                <col/>
                <tr>
                    <td>共<span style="cursor: pointer;color:#0067C2" id="totalRows">${totalRows}</span>条记录</td>
                    <td>其中(提醒未过期<span style="cursor: pointer;color:#0067C2"
                                      id="activityNoExpired">${activityNoExpired}</span>条&nbsp;提醒过期
                        <span style="cursor: pointer;color:#0067C2" id="activityExpired">${activityExpired}</span>条&nbsp;已提醒
                        <span style="cursor: pointer;color:#0067C2" id="remindedNum">${reminded}</span>条)
                    </td>
                </tr>
            </table>
            <table class="clear shopPlans" id="tb_tui" style="width:950px;table-layout:fixed">
                <col width="70"/>
                <col width="122"/>
                <col width="122"/>
                <col width="122"/>
                <col width="122"/>
                <col width="132"/>
                <col width="132"/>
                <col width="138"/>
                <tr class="tab_title">
                    <td>NO</td>
                    <td>提醒项目</td>
                    <td>内容</td>
                    <td>相关人</td>
                    <td>联系方式</td>
                    <td>提醒时间</td>
                    <td>状态</td>
                    <td>操作</td>
                </tr>

            </table>
        </div>
        <div class="clearfix">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="remind.do?method=getPlans"></jsp:param>
                <jsp:param name="jsHandleJson" value="initTr4"></jsp:param>
                <jsp:param name="data"
                           value="{startPageNo:1,maxRows:10,tableStatus:$.trim($(\'#tableStatus\').val())}"></jsp:param>
                <jsp:param name="dynamical" value="dynamical5"></jsp:param>
            </jsp:include>

        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<div class="tuihuo"></div>
<iframe id="iframe_PopupBox" scrolling="no" style="position:absolute;z-index:5; left:400px; top:350px; display:none;"
        allowtransparency="true" width="850px" height="500px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>