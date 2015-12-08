<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.search.dto.ItemIndexDTO" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    ItemIndexDTO itemIndex = (ItemIndexDTO) request.getAttribute("command");
    List<ItemIndexDTO> itemIndexList = (List<ItemIndexDTO>) request.getAttribute("itemIndexList");
    String pageNo = itemIndex.getPageNo();
    String issubmit = request.getAttribute("issubmit") == null ? "false" : (String) request.getAttribute("issubmit");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <title>车辆历史查询</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up3<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/carSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/carHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
    <style>
        #ui-datepicker-div, .ui-datepicker {
            font-size: 100%;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/morehistory<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<style type="text/css">
    .tableStyle{
        border-color:#BBBBBB;
        border-style:solid;
        border-width:0 1px 1px;
    }
</style>
    <script type="text/javascript">
        $(function () {
            tableUtil.tableStyle('.tableStyle','.table_title');
            $(document).keydown(function (e) {
                checkHistoryEvent(e);
            });

            document.getElementById("div_close").onclick = closeWindow;
            var issubmit = "<%=issubmit%>";
            if (issubmit == 'true') {
                document.thisform.submit();
            }
            var pn = parseInt("<%=pageNo%>", 10);
            $("#pageNo_id").html(pn);

//            $("#searchCar_id").click();

            jQuery("#searchCar_id").bind("click", function () {
                var endDateStr = jQuery("#startDateStr").val();
                var endDateStr2 = jQuery("#endDateStr").val();

                // 滤除时间字符串内非数字的字符
                var regGetTimeNum = /[^\d]/g;
                if (endDateStr
                        && endDateStr2
                        && endDateStr.replace(regGetTimeNum, "") > endDateStr2.replace(regGetTimeNum, "")) {
                    alert("起始时间 不能大于 结束时间！");
                    return;
                }
                document.thisform.pageNo.value = 1;
                document.thisform.submit();
            });

            function closeWindow() {
                window.parent.document.getElementById("mask").style.display = "none";
                if (window.parent.document.getElementById("iframe_PopupBox") != null) {
                    window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox").src = "";
                }
                if (window.parent.document.getElementById("iframe_PopupBox1") != null) {
                    window.parent.document.getElementById("iframe_PopupBox1").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox1").src = "";
                }
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }
        });
        $().ready(function () {
            $("#startDateStr,#endDateStr")
                    .datetimepicker({
                        "numberOfMonths":1,
                        "showButtonPanel":true,
                        "changeYear":true,
                        "changeMonth":true,
                        "yearRange":"c-100:c+100",
                        "yearSuffix":""
                    })
                    .bind("click", function() {
                        $(this).blur();
                    });
        });
    </script>
</head>
<body>
<div class="i_supplierInfo more_supplier" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter i_two">
        <div class="i_note more_title" id="getmorehistory">车辆历史查询</div>
        <div class="i_closehistory" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody clear">
        <div class="i_main clear">
            <!--历史搜素-->
            <form:form commandName="command" action="goodsHistory.do?method=searchCarHistory"
                       method="post" name="thisform">
                <form:hidden path="vehicle"/>
                <form:hidden path="orderType"/>
                <form:hidden path="pageNo"/>
                <div class="his_search">
                    <div class="goods_chk clear">
                        <ul>
                            <li class="search_user clear"><label>车牌号:</label>
                                <input id="licenceNo_id" name="licenceNo" value="${command.vehicle}"/></li>
                            <li class="clear"><label>施工项目:</label>
                                <form:input path="services"/></li>
                            <li><label>材料品名:</label>
                                <form:input path="itemName"/></li>
                        </ul>
                    </div>
                    <div class="his_time goods_main">
                        <div class="time_search good_his">
                            <label>时 间</label>

                            <div class="i_searchTime">
                                    <%--<form:input id="endDateStr" path="endDateStr" readonly="readonly"/></div>--%>
                                <input id="startDateStr" name="startDateStr" readonly="readonly"
                                       value="${command.startDateStr}"/></div>
                            <label>至</label>

                            <div class="i_searchTime">
                                    <%--<form:input id="endDateStr2" path="endDateStr2" readonly="readonly"/></div>--%>
                                <input id="endDateStr" name="endDateStr" readonly="readonly"
                                       value="${command.endDateStr}"/></div>

                        </div>
                            <%--<div class="his_list">--%>

                            <%--<div class="today_list his_first">--%>
                            <%--<a id="a_today" class="his_now">今天</a>--%>
                            <%--</div>--%>
                            <%--<div class="today_list">--%>
                            <%--<a id="a_week" class="his_now">本周</a>--%>
                            <%--</div>--%>
                            <%--<div class="today_list">--%>
                            <%--<a id="a_month" class="his_now">本月</a>--%>
                            <%--</div>--%>
                            <%--<div class="today_list">--%>
                            <%--<a id="a_year" class="his_now">今年</a>--%>
                            <%--</div>--%>
                        <div class="search_his clear" id="searchCar_id">
                            <label>搜索</label>
                                <%--</div>--%>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            </form:form>

            <!--历史搜素结束-->
            <!--数据显示-->
            <p></p>
            <table cellpadding="0" cellspacing="0" class="table2 tableStyle">
                <col width="20"/>
                <col width="70"/>
                <col width="70"/>
                <col width="80"/>
                <col width="120"/>
                <col/>
                <col width="50"/>
                <col width="50"/>
                <col width="85"/>
                <col width="50"/>
                <col width="70"/>
                <tr class="table_title">
                    <td style="border-left:none;">NO</td>
                    <td>时间</td>
                    <td>车牌号</td>
                    <td>内容</td>
                    <td>施工项目</td>
                    <td>材料品名</td>
                    <td class="txt_right">金额</td>
                    <td class="txt_right">欠款</td>
                    <td>预计还款时间</td>
                    <td>状态</td>
                    <td style="border-right:none;">操作</td>
                </tr>
                <c:forEach items="${itemIndexList}" var="itemDTO" varStatus="status">
                    <tr class="table-row-original">
                        <td style="border-left:none;">${status.index+1}</td>
                        <td>${itemDTO.orderTimeCreatedStr}</td>
                        <td>${itemDTO.vehicle}</td>
                        <td>${itemDTO.orderTypeStr}</td>
                        <td style="text-overflow:ellipsis" title="${itemDTO.services}">${itemDTO.services}</td>
                        <td style="text-overflow:ellipsis" title="${itemDTO.itemName}">${itemDTO.itemName}</td>
                        <td class="txt_right" style="text-overflow:ellipsis"
                            title="${itemDTO.orderTotalAmount}">${itemDTO.orderTotalAmount}</td>
                        <td class="qian_red txt_right">${itemDTO.arrears}</td>
                        <td>${itemDTO.paymentTimeStr}</td>
                        <td>${itemDTO.orderStatusStr}</td>
                        <td style="border-right:none;" class="qian_blue">
                            <c:if test='${itemDTO.orderType=="REPAIR"}'>
                                <a href="<%=basePath%>txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=${itemDTO.orderId}"
                                   target="_parent">点击详细</a>
                            </c:if>
                            <c:if test='${itemDTO.orderType=="WASH_BEAUTY"}'>
                                <a href="<%=basePath%>washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=${itemDTO.orderId}"
                                   target="_parent">点击详情</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <c:if test="${pager != null}">
                <jsp:include page="/common/paging.jsp">
                    <jsp:param name="url" value="goodsHistory.do?method=searchCarHistory"></jsp:param>
                    <jsp:param name="submit" value="thisform"></jsp:param>
                </jsp:include>
            </c:if>
            <%--<div class="i_leftBtn clear" id="i_leftBtn_id">--%>
            <%--<%if (Integer.parseInt(pageNo) > 1) {%>--%>
            <%--<div class="lastPage">上一页</div>--%>
            <%--<%}%>--%>
            <%--<div class="onlin_his" id="pageNo_id">1</div>--%>
            <%--<%if (itemIndexList != null && itemIndexList.size() >= 5) {%>--%>
            <%--<div class="nextPage">下一页</div>--%>
            <%--<%}%>--%>
            <%--</div>--%>

            <!--数据显示结束-->
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>