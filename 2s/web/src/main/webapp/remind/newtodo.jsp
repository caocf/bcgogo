<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    String access = (String) request.getAttribute("access");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>待办事项</title>

    <link rel="stylesheet" type="text/css" href="styles/base<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/base_mirror<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <%--<link rel="stylesheet" type="text/css" href="styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css"/>--%>

    <style type="text/css">
        .todo_center {
            font-family: "宋体";
            font-weight: bold;
        }

        .todo_center label {
            color: #363636;
            font-weight: normal;
        }

        .todo_remind label:hover, .todo_remind label:hover .count, .todo_center label:hover, .todo_center label:hover .count {
            color: #FD5300;
            text-decoration: underline;
        }

        .count {
            color: #0060ba;
        }

        .todo_tab a {
            display: inline-block;
            height: 16px;
        }

        .red_color td.red_color {
            color: #CB0000;
        }

        .pay:hover span {
            color: #FFFFFF;
        }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dropdown<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/newtodo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/permission<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/addPlan<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">

        function detailsArrears(customerId) {
            toReceivableSettle(customerId);
        }

        var objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr,objEnterAppointName;
        //    var objEnterPhoneDebtIdStr,objEnterPhoneServiceIdStr;
        var objEnterPhoneRemindEventIdStr;

        function enterPhoneSendSms(objEnterPhoneMobile) {
            smsSend(objEnterPhoneMobile, objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr, objEnterPhoneRemindEventIdStr, objEnterAppointName);
        }
        // type  0 保险  1 验车  2 生日
        function smsSend(mobile, type, arrears, licenceNo, date, name, customerIdStr, remindEventId, serviceName, isVehicleInfo, contactId) {
            if (mobile == null || $.trim(mobile) == "") {
                if ('RETURN' == serviceName) {
                    $("#enterPhoneSupplierId").val(customerIdStr);
                } else {
                    $("#enterPhoneCustomerId").val(customerIdStr);
                    $('#licenceNo').val(licenceNo);
                    $("#enterPhoneScene").val("customer_remind_sms");
                }
                Mask.Login();
                $("#enterPhoneSetLocation").fadeIn("slow");
                objEnterPhoneType = type;
                objEnterPhoneArrears = arrears;
                objEnterPhoneLicenceNo = licenceNo;
                objEnterPhoneDate = date;
                objEnterPhoneName = name;
                objEnterPhoneCustomerIdStr = customerIdStr;
                objEnterPhoneRemindEventIdStr = remindEventId;
                objEnterAppointName = serviceName;
                return;
            }

            var dates = date.split("-");
            var year = dates[0];
            var month = dates[1];
            var day = dates[2];
            var url = encodeURI("sms.do?method=smswrite&mobile=" +
                    $.trim(mobile) + "&type=" + type + "&money=" + arrears + "&licenceNo=" + licenceNo + "&year=" + year +
                    "&month=" + month + "&day=" + day + "&name=" + name + '&remindEventId=' + remindEventId +
                    "&serviceName=" + serviceName + "&appointName=" + serviceName + "&contactIds=" + G.Lang.normalize(contactId));
            window.open(url);
        }
        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
        APP_BCGOGO.Permission.CustomerManager.SmsSend =${WEB_CUSTOMER_MANAGER_SMS_SEND};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam resourceType="menu" permissions="WEB.TXN.SALE_MANAGE.SALE,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.TXN.PURCHASE_MANAGE.PURCHASE,WEB.TXN.PURCHASE_MANAGE.STORAGE">
        APP_BCGOGO.Permission.Txn.SaleManage.Sale =${WEB_TXN_SALE_MANAGE_SALE};
        APP_BCGOGO.Permission.VehicleConstruction.Construct.Base =${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE};
        APP_BCGOGO.Permission.Txn.PurchaseManage.Storage =${WEB_TXN_PURCHASE_MANAGE_STORAGE};
        APP_BCGOGO.Permission.Txn.PurchaseManage.Purchase =${WEB_TXN_PURCHASE_MANAGE_PURCHASE};
        </bcgogo:permissionParam>
        $(function() {
            // 直接写入inputHidden会导致“刷新页面后乱数据”问题
            $("#goodsBuyPermission").val(APP_BCGOGO.Permission.Txn.PurchaseManage.Purchase);
            $("#goodsStoragePermission").val(APP_BCGOGO.Permission.Txn.PurchaseManage.Storage);
            $("#goodsSalePermission").val(APP_BCGOGO.Permission.Txn.SaleManage.Sale);
            $("#repairOrderPermission").val(APP_BCGOGO.Permission.VehicleConstruction.Construct.Base);
            $("#smsSendPermission").val(APP_BCGOGO.Permission.CustomerManager.SmsSend);
        });
        function getCarHistory(flag) { //flag判断查询昨天还是今天的车辆历史
            bcgogo.checksession({
                "parentWindow":window.parent,
                'iframe_PopupBox':$("#iframe_PopupBox1")[0],
                'src':"goodsHistory.do?method=createCarHistory&issubmit=true&orderType=REPAIR"
                        + "&searchflag=" + flag});
        }
        /**
         *
         *弹出当天新增车辆历史记录
         */
        function getCarHistoryByTodayNewCustomer(flag) {
            bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox1")[0],
                'src':"goodsHistory.do?method=createCarHistoryByTodayNewVehicle&issubmit=true&searchflag=" + flag});
        }
        defaultStorage.setItem(storageKey.MenuUid, "SCHEDULE");
    </script>

</head>
<body class="bodyMain">
<!--头部-->
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/sms/enterPhone.jsp" %>
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<input type="hidden" id="count" value="${inventoryRemindSize}"/>
<input type="hidden" id="flagTrNum"/>

<input type="hidden" id="goodsBuyPermission"/>
<input type="hidden" id="goodsStoragePermission"/>
<input type="hidden" id="goodsSalePermission"/>
<input type="hidden" id="repairOrderPermission"/>
<input type="hidden" id="smsSendPermission"/>
<input type="hidden" id="licenceNo"/>

<div class="i_main clear">
<div class="mainTitles">
    <div class="titleWords">待办事项</div>
    <jsp:include page="remindNavi.jsp">
        <jsp:param name="currPage" value="newToDoNaviMenu"/>
    </jsp:include>
</div>

<div class="i_mainNewTo clear">

<%--<!--客户服务类-->--%>
<%--<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.VEHICLE_CONSTRUCTION_BEAUTY">
    <c:if test='<%=(access==null || "undefined".equals(access) || "customer".equals(access))%>'>
        <input type="hidden" id="uPage"
               value="<%=(request.getAttribute("uPage")==null?"no":request.getAttribute("uPage"))%>"/>
        <input type="hidden" id="isOverdue_customerRemind" value=""/>
        <input type="hidden" id="hasRemind_customerRemind" value=""/>

        <div class="new_title service_reminders distance1 clear">
            <span class="todo_left"></span>

            <div class="todo_center todo_remind">客户服务提醒
                <label style="cursor:pointer;" id="allCustomerRemind">共&nbsp;
                    <strong class="qian_blue count" id="customerServiceJobNumberHint">
                    </strong>&nbsp;条记录</label>&nbsp;&nbsp;
                (<label style="cursor:pointer;" id="customerRemindIsOverdue">过期提醒&nbsp;
                    <strong class="qian_blue count" id="countCustomerRemindIsOverdueHint">
                    </strong>&nbsp;条</label>&nbsp;&nbsp;
                <label style="cursor:pointer;" id="customerRemindIsNotOverdue">提醒未过期&nbsp;
                    <strong class="qian_blue count" id="countCustomerRemindIsNotOverdueHint">
                    </strong>&nbsp;条</label>&nbsp;&nbsp;
                <label style="cursor:pointer;" id="customerRemindHasRemind">已提醒&nbsp;
                    <strong class="qian_blue count" id="countCustomerRemindHasRemindHint">
                    </strong>&nbsp;条</label>&nbsp;)
            </div>
            <a id="exportCustomerRemind" style="margin-right: 42px;background: none;" class="blue_col">导出</a>
            <img id="exportCustomerCover" src="images/loadinglit.gif" alt="正在导出" title="正在导出"
                 style="position: absolute;width: 21px;right: 60px;display: none;">
            <a href="javascript:void(0)" id="chref" class="blue_col J_more_or_less" style="width: 40px">更多</a>
            <span class="todo_right"></span>
        </div>
        <div id="show4">
            <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_4">
                <col width="20">
                <col width="100"/>
                <col width="80">
                <col width="180"/>
                <col width="180">
                <col width="150">
                <col width="80">
                <col width="100">
                <col width="60">
                <col width="60">
                <tr class="title">
                    <td>No</td>
                    <td>提醒项目</td>
                    <td>车牌号</td>
                    <td>车主信息</td>
                    <td style="text-align: left;padding-left: 30px;">所属客户</td>
                    <td style="text-align: center;">下次提醒(日期/里程)</td>
                    <td>当前里程</td>
                    <td>距保养里程</td>
                    <td>状态</td>
                    <td>操作</td>
                </tr>
            </table>
        </div>
        <!--分页-->
        <div class="hidePageAJAX" id="customerRemind_page">
            <bcgogo:ajaxPaging url="remind.do?method=customerRemind" postFn="initTr4" hide="hideIt"
                               dynamical="dynamical4"
                               data='{isOverdue:$(\"#isOverdue_arrearsRemind\").val(),hasRemind:$(\"#hasRemind_arrearsRemind\").val()}'/>
        </div>
        <div class="height clear"></div>
    </c:if>
</bcgogo:hasPermission>--%>

<%--<!--维修美容-->--%>
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.VEHICLE_CONSTRUCTION_BEAUTY">
    <c:if test='<%=(access==null || "undefined".equals(access) || "repair".equals(access))%>'>
        <input type="hidden" id="remindType"/>
        <input type="hidden" id="rPage"
               value="<%=(request.getAttribute("rPage")==null?"no":request.getAttribute("rPage"))%>"/>

        <div class="distance new_title clear">
            <span class="todo_left"></span>

            <div class="todo_center">维修美容类
                <label style="cursor:pointer;" id="allRepair">共
                    <strong class="qian_blue count" id="repairRemindSizeHint">
                    </strong>&nbsp;条历史记录
                </label>
                (<c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}"><label style="cursor:pointer;" id="lack">缺料待修
                    <strong class="qian_blue count" id="lackHint">
                    </strong>&nbsp;条</label>
                    <label style="cursor:pointer;" id="incoming">来料待修
                        <strong class="qian_blue count" id="incomingHint">
                        </strong>&nbsp;条</label>
                </c:if><label style="cursor:pointer;" id="pending">待交付
                    <strong class="qian_blue count" id="pendingHint">
                    </strong>&nbsp;条</label>
                <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">
                    <label style="cursor:pointer;" id="waitOutStorage">待领料
                        <strong class="qian_blue count" id="waitOutStorageHint">
                        </strong>&nbsp;条</label>
                </bcgogo:hasPermission>

                <label style="cursor:pointer;" onclick="getCarHistory('-1')">昨日服务
                    <strong class="qian_blue count" id="serviceYesterdayTimesHint">
                    </strong>&nbsp;辆</label>
                <label style="cursor:pointer;" onclick="getCarHistory('0')">今日服务
                    <strong class="qian_blue count" id="serviceTodayTimesHint">
                    </strong>&nbsp;辆</label>
                <label style="cursor:pointer;" onclick="getCarHistoryByTodayNewCustomer('0')">其中新增
                    <strong class="qian_blue count" id="todayNewUserNumberHint">
                    </strong>&nbsp;辆</label>
                )

            </div>
            <a href="javascript:void(0)" id="rhref" class="blue_col J_more_or_less">更多</a>
            <span class="todo_right"></span>
        </div>
        <div id="show1">
            <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_1">
                <col width="40px">
                <col width="100px">
                <col width="80px">
                <col width="80px">
                <col width="80px">
                <col width="80px">
                <col width="80px">
                <col width="140px">
                <col width="160px">
                <col width="90px">
                <tr class="title">
                    <td>No</td>
                    <td>单据编号</td>
                    <td>提醒类型</td>
                    <td>客户名</td>
                    <td>联系方式</td>
                    <td>车牌号</td>
                    <td>车型</td>
                    <td>材料品名</td>
                    <td>内容</td>
                    <td>预计出厂时间</td>
                </tr>
            </table>
        </div>
        <!--分页-->
        <div class="hidePageAJAX" id="repairRemind_page">
            <bcgogo:ajaxPaging url="remind.do?method=repairRemind" postFn="initTr1" hide="hideIt"
                               dynamical="dynamical1"/>
        </div>
        <div class="height clear"></div>
    </c:if>
</bcgogo:hasPermission>


<%--<!--欠款待办事项-->--%>
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.ARREARS">
    <c:if test='<%=(access==null || "undefined".equals(access) || "customer".equals(access))%>'>
        <input type="hidden" id="aPage"
               value="<%=(request.getAttribute("aPage")==null?"no":request.getAttribute("aPage"))%>"/>
        <input type="hidden" id="isOverdue_arrearsRemind" value=""/>
        <input type="hidden" id="hasRemind_arrearsRemind" value=""/>

        <div class="distance new_title qiankuan clear">
            <span class="todo_left"></span>

            <div class="todo_center todo_remind">欠款提醒类
                <label style="cursor:pointer;" id="allArrears">共
                    <strong class="qian_blue count" id="countArrearsHint">
                    </strong>&nbsp;条历史记录</label>&nbsp;&nbsp;
                (<label style="cursor:pointer;" id="arrearsRemindIsOverdue">过期提醒&nbsp;
                    <strong class="qian_blue count" id="countArrearsRemindIsOverdueHint">
                    </strong>&nbsp;条</label>&nbsp;&nbsp;
                <label style="cursor:pointer;" id="arrearsRemindIsNotOverdue">提醒未过期&nbsp;
                    <strong class="qian_blue count" id="countArrearsRemindIsNotOverdueHint">
                    </strong>&nbsp;条</label>&nbsp;&nbsp;
                <label style="cursor:pointer;" id="arrearsRemindHasRemind">已提醒&nbsp;
                    <strong class="qian_blue count" id="countArrearsRemindHasRemindHint">
                    </strong>&nbsp;条</label>&nbsp;)
            </div>
            <a href="javascript:void(0)" class="blue_col J_more_or_less">更多</a>
            <span class="todo_right"></span>
        </div>
        <div id="show2">
            <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_2">
                <col width="50"/>
                <col width="150"/>
                <col width="180"/>
                <col width="120"/>
                <col width="150"/>
                <col width="100"/>
                <col width="100"/>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
                    <col width="70"/>
                </bcgogo:hasPermission>
                <col width="50"/>
                <tr class="title">
                    <td>No</td>
                    <td>客户/供应商</td>
                    <td>联系人</td>
                    <td>联系方式</td>
                    <td class="text_justified">应收</td>
                    <td>预计日期</td>
                    <td>状态</td>
                    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
                        <td>提醒方式</td>
                    </bcgogo:hasPermission>
                    <td>操作</td>
                </tr>
            </table>
        </div>
        <div class="hidePageAJAX" id="arrearsRemind_page">
            <bcgogo:ajaxPaging url="remind.do?method=arrearsRemind" postFn="initTr2" hide="hideIt"
                               dynamical="dynamical2"
                               data='{isOverdue:$(\"#isOverdue_arrearsRemind\").val(),hasRemind:$(\"#hasRemind_arrearsRemind\").val()}'/>
        </div>
        <div class="height clear"></div>
    </c:if>
</bcgogo:hasPermission>


<%--<!--进销存-->--%>
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.TXN">
    <c:if test='<%=(access==null || "undefined".equals(access) || "txn".equals(access))%>'>
        <div class="new_title  purchase distance clear">
            <span class="todo_left"></span>

            <div class="todo_center">进销存类
                <label id="allInvoicing" style="cursor:pointer;">共
                    <strong class="qian_blue count" id="inventoryRemindSizeHint">
                    </strong>&nbsp;条历史记录
                </label>
            </div>
            <a href="javascript:void(0)" id="ihref" class="blue_col J_more_or_less">更多</a>
            <span class="todo_right"></span>
        </div>
        <div id="show3">
            <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_3">
                <col width="40">
                <col width="100">
                <col width="80">
                <col width="150">
                <col width="200">
                <col width="80">
                <col width="105">
                <col width="150">
                <col width="100">
                <tr class="title">
                    <td>No</td>
                    <td>单据号</td>
                    <td>提醒类型</td>
                    <td>供应商</td>
                    <td>采购商品</td>
                    <td>采购种类</td>
                    <td>总金额</td>
                    <td>预计交货日期</td>
                    <td>操作</td>
                </tr>
            </table>
        </div>
        <div class="hidePageAJAX" id="invoicing_page">
            <bcgogo:ajaxPaging url="remind.do?method=invoicing" postFn="initTr3" hide="hideIt" dynamical="dynamical3"/>
        </div>
        <div class="height clear"></div>
    </c:if>
</bcgogo:hasPermission>

<%--<!--客户服务类-->--%>
<bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
    <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.CUSTOMER_SERVICE">
            <c:if test='<%=(access==null || "undefined".equals(access) || "customer".equals(access))%>'>
                <input type="hidden" id="uPage"
                       value="<%=(request.getAttribute("uPage")==null?"no":request.getAttribute("uPage"))%>"/>
                <input type="hidden" id="isOverdue_customerRemind" value=""/>
                <input type="hidden" id="hasRemind_customerRemind" value=""/>

                <div class="new_title service_reminders distance1 clear">
                    <span class="todo_left"></span>

                    <div class="todo_center todo_remind">客户服务提醒
                        <label style="cursor:pointer;" id="allCustomerRemind">共&nbsp;
                            <strong class="qian_blue count" id="customerServiceJobNumberHint">
                            </strong>&nbsp;条记录</label>&nbsp;&nbsp;
                        (<label style="cursor:pointer;" id="customerRemindIsOverdue">过期提醒&nbsp;
                            <strong class="qian_blue count" id="countCustomerRemindIsOverdueHint">
                            </strong>&nbsp;条</label>&nbsp;&nbsp;
                        <label style="cursor:pointer;" id="customerRemindIsNotOverdue">提醒未过期&nbsp;
                            <strong class="qian_blue count" id="countCustomerRemindIsNotOverdueHint">
                            </strong>&nbsp;条</label>&nbsp;&nbsp;
                        <label style="cursor:pointer;" id="customerRemindHasRemind">已提醒&nbsp;
                            <strong class="qian_blue count" id="countCustomerRemindHasRemindHint">
                            </strong>&nbsp;条</label>&nbsp;)
                    </div>
                    <a id="exportCustomerRemind" style="margin-right: 42px;background: none;" class="blue_col">导出</a>
                    <img id="exportCustomerCover" src="images/loadinglit.gif" alt="正在导出" title="正在导出"
                         style="position: absolute;width: 21px;right: 60px;display: none;">
                    <a href="javascript:void(0)" id="chref" class="blue_col J_more_or_less" style="width: 40px">更多</a>
                    <span class="todo_right"></span>
                </div>
                <div id="show4">
                    <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_4">
                        <col width="20">
                        <col width="100"/>
                        <col width="80">
                        <col width="180"/>
                        <col width="180">
                        <col width="150">
                        <col width="80">
                        <col width="100">
                        <col width="60">
                        <col width="60">
                        <tr class="title">
                            <td>No</td>
                            <td>提醒项目</td>
                            <td>车牌号</td>
                            <td>车主信息</td>
                            <td style="text-align: left;padding-left: 30px;">所属客户</td>
                            <td style="text-align: center;">下次提醒(日期/里程)</td>
                            <td>当前里程</td>
                            <td>距保养里程</td>
                            <td>状态</td>
                            <td>操作</td>
                        </tr>
                    </table>
                </div>
                <!--分页-->
                <div class="hidePageAJAX" id="customerRemind_page">
                    <bcgogo:ajaxPaging url="remind.do?method=customerRemind" postFn="initTr4" hide="hideIt"
                                       dynamical="dynamical4"
                                       data='{isOverdue:$(\"#isOverdue_arrearsRemind\").val(),hasRemind:$(\"#hasRemind_arrearsRemind\").val()}'/>
                </div>
                <div class="height clear"></div>
            </c:if>
        </bcgogo:hasPermission>
    </c:if>
</bcgogo:permissionParam>


<!--救援类-->
<bcgogo:hasPermission permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
    <%--<c:if test='<%=(access==null || "undefined".equals(access) || "repair".equals(access))%>'>--%>
    <%--<input type="hidden" id="remindType" />--%>
    <%--<input type="hidden" id="rPage" value="<%=(request.getAttribute("rPage")==null?"no":request.getAttribute("rPage"))%>"/>--%>
    <div class="new_title service_reminders distance1 clear">
        <span class="todo_left"></span>

        <div class="todo_center todo_remind">客户救援提醒
                <%--<label style="cursor:pointer;" id="allRepair">共--%>
                <%--<strong class="qian_blue count" id="repairRemindSizeHint">--%>
                <%--</strong>&nbsp;条历史记录--%>
                <%--</label>--%>
                <%--(<c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}"><label style="cursor:pointer;" id="lack">缺料待修--%>
                <%--<strong class="qian_blue count" id="lackHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<label style="cursor:pointer;" id="incoming">来料待修--%>
                <%--<strong class="qian_blue count" id="incomingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</c:if><label style="cursor:pointer;" id="pending">待交付--%>
                <%--<strong class="qian_blue count" id="pendingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">--%>
                <%--<label style="cursor:pointer;" id="waitOutStorage">待领料--%>
                <%--<strong class="qian_blue count" id="waitOutStorageHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</bcgogo:hasPermission>--%>

                <%--<label style="cursor:pointer;"  onclick="getCarHistory('-1')">昨日服务--%>
                <%--<strong class="qian_blue count" id="serviceYesterdayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistory('0')">今日服务--%>
                <%--<strong class="qian_blue count" id="serviceTodayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistoryByTodayNewCustomer('0')">其中新增--%>
                <%--<strong class="qian_blue count" id="todayNewUserNumberHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--)--%>

        </div>
        <a href="javascript:void(0)" id="rhref_sos" class="blue_col J_more_or_less">更多</a>
        <span class="todo_right"></span>
    </div>
    <div id="show_sos">
        <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_sos">
            <col width="40px">
            <col width="100px">
            <col width="100px">
            <col width="120px">
            <col width="120px">
            <col width="100px">
            <col width="100px">
            <tr class="title">
                <td>No</td>
                <td>求救时间</td>
                <td>车辆信息</td>
                <td>车主信息</td>
                <td>客户信息</td>
                <td>车辆位置</td>
                <td>操作</td>
            </tr>
        </table>
    </div>
    <!--分页-->
    <div class="hidePageAJAX" id="sos_page">
        <bcgogo:ajaxPaging url="remind.do?method=searchSosList" postFn="initSos" hide="hideIt"
                           dynamical="dynamical_sos"/>
    </div>
    <div class="height clear"></div>
    <%--</c:if>--%>
</bcgogo:hasPermission>


<!--碰撞视频类-->
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.VEHICLE_IMPACT_VIDEO">
    <%--<c:if test='<%=(access==null || "undefined".equals(access) || "repair".equals(access))%>'>--%>
    <%--<input type="hidden" id="remindType" />--%>
    <%--<input type="hidden" id="rPage" value="<%=(request.getAttribute("rPage")==null?"no":request.getAttribute("rPage"))%>"/>--%>
    <div class="distance new_title qiankuan clear">
        <span class="todo_left"></span>

        <div class="todo_center">客户碰撞提醒
                <%--<label style="cursor:pointer;" id="allRepair">共--%>
                <%--<strong class="qian_blue count" id="repairRemindSizeHint">--%>
                <%--</strong>&nbsp;条历史记录--%>
                <%--</label>--%>
                <%--(<c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}"><label style="cursor:pointer;" id="lack">缺料待修--%>
                <%--<strong class="qian_blue count" id="lackHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<label style="cursor:pointer;" id="incoming">来料待修--%>
                <%--<strong class="qian_blue count" id="incomingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</c:if><label style="cursor:pointer;" id="pending">待交付--%>
                <%--<strong class="qian_blue count" id="pendingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">--%>
                <%--<label style="cursor:pointer;" id="waitOutStorage">待领料--%>
                <%--<strong class="qian_blue count" id="waitOutStorageHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</bcgogo:hasPermission>--%>

                <%--<label style="cursor:pointer;"  onclick="getCarHistory('-1')">昨日服务--%>
                <%--<strong class="qian_blue count" id="serviceYesterdayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistory('0')">今日服务--%>
                <%--<strong class="qian_blue count" id="serviceTodayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistoryByTodayNewCustomer('0')">其中新增--%>
                <%--<strong class="qian_blue count" id="todayNewUserNumberHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--)--%>

        </div>
        <a href="javascript:void(0)" id="rhref_impact" class="blue_col J_more_or_less">更多</a>
        <span class="todo_right"></span>
    </div>
    <div id="show_impact">
        <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_impact">
            <col width="40px">
            <col width="100px">
            <col width="80px">
            <col width="50px">
            <col width="100px">
            <col width="90px">
            <col width="120px">
            <tr class="title">
                <td>No</td>
                <td>碰撞时间</td>
                <td>碰撞地点</td>
                <td>碰撞视频</td>
                <td>车辆信息</td>
                <td>客户信息</td>
                <td>操作</td>
            </tr>
        </table>
    </div>
    <!--分页-->
    <div class="hidePageAJAX" id="impact_page">
        <bcgogo:ajaxPaging url="remind.do?method=impactVideo" postFn="initImpact" hide="hideIt"
                           dynamical="dynamical_impact"/>
    </div>
    <div class="height clear"></div>
    <%--</c:if>--%>
</bcgogo:hasPermission>


<!--故障类-->
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.VEHICLE_FAULT_CODE">
    <%--<c:if test='<%=(access==null || "undefined".equals(access) || "repair".equals(access))%>'>--%>
    <%--<input type="hidden" id="remindType" />--%>
    <%--<input type="hidden" id="rPage" value="<%=(request.getAttribute("rPage")==null?"no":request.getAttribute("rPage"))%>"/>--%>
    <div class="new_title  purchase distance clear">
        <span class="todo_left"></span>

        <div class="todo_center">客户故障提醒
                <%--<label style="cursor:pointer;" id="allRepair">共--%>
                <%--<strong class="qian_blue count" id="repairRemindSizeHint">--%>
                <%--</strong>&nbsp;条历史记录--%>
                <%--</label>--%>
                <%--(<c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}"><label style="cursor:pointer;" id="lack">缺料待修--%>
                <%--<strong class="qian_blue count" id="lackHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<label style="cursor:pointer;" id="incoming">来料待修--%>
                <%--<strong class="qian_blue count" id="incomingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</c:if><label style="cursor:pointer;" id="pending">待交付--%>
                <%--<strong class="qian_blue count" id="pendingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">--%>
                <%--<label style="cursor:pointer;" id="waitOutStorage">待领料--%>
                <%--<strong class="qian_blue count" id="waitOutStorageHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</bcgogo:hasPermission>--%>

                <%--<label style="cursor:pointer;"  onclick="getCarHistory('-1')">昨日服务--%>
                <%--<strong class="qian_blue count" id="serviceYesterdayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistory('0')">今日服务--%>
                <%--<strong class="qian_blue count" id="serviceTodayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistoryByTodayNewCustomer('0')">其中新增--%>
                <%--<strong class="qian_blue count" id="todayNewUserNumberHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--)--%>

        </div>
        <a href="javascript:void(0)" id="rhref_fault" class="blue_col J_more_or_less">更多</a>
        <span class="todo_right"></span>
    </div>
    <div id="show_fault">
        <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_fault">
            <col width="40px">
            <col width="100px">
            <col width="40px">
            <col width="160px">
            <col width="140px">
            <col width="80px">
            <col width="80px">
            <col width="80px">
            <tr class="title">
                <td>No</td>
                <td>发生时间</td>
                <td>类型</td>
                <td>详细描述</td>
                <td>车辆信息</td>
                <td>客户端手机号</td>
                <td>客户信息</td>
                <td>操作</td>
            </tr>
        </table>
    </div>
    <!--分页-->
    <div class="hidePageAJAX" id="fault_page">
        <bcgogo:ajaxPaging url="remind.do?method=searchShopFaultInfoList" postFn="initFault" hide="hideIt"
                           dynamical="dynamical_fault"/>
    </div>
    <div class="height clear"></div>
    <%--</c:if>--%>
</bcgogo:hasPermission>

<!--里程-->
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.VEHICLE_MILEAGE">
    <%--<c:if test='<%=(access==null || "undefined".equals(access) || "repair".equals(access))%>'>--%>
    <%--<input type="hidden" id="remindType" />--%>
    <%--<input type="hidden" id="rPage" value="<%=(request.getAttribute("rPage")==null?"no":request.getAttribute("rPage"))%>"/>--%>
    <div class="distance new_title clear">
        <span class="todo_left"></span>

        <div class="todo_center">客户里程提醒
                <%--<label style="cursor:pointer;" id="allRepair">共--%>
                <%--<strong class="qian_blue count" id="repairRemindSizeHint">--%>
                <%--</strong>&nbsp;条历史记录--%>
                <%--</label>--%>
                <%--(<c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}"><label style="cursor:pointer;" id="lack">缺料待修--%>
                <%--<strong class="qian_blue count" id="lackHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<label style="cursor:pointer;" id="incoming">来料待修--%>
                <%--<strong class="qian_blue count" id="incomingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</c:if><label style="cursor:pointer;" id="pending">待交付--%>
                <%--<strong class="qian_blue count" id="pendingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">--%>
                <%--<label style="cursor:pointer;" id="waitOutStorage">待领料--%>
                <%--<strong class="qian_blue count" id="waitOutStorageHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</bcgogo:hasPermission>--%>

                <%--<label style="cursor:pointer;"  onclick="getCarHistory('-1')">昨日服务--%>
                <%--<strong class="qian_blue count" id="serviceYesterdayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistory('0')">今日服务--%>
                <%--<strong class="qian_blue count" id="serviceTodayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistoryByTodayNewCustomer('0')">其中新增--%>
                <%--<strong class="qian_blue count" id="todayNewUserNumberHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--)--%>

        </div>
        <a href="javascript:void(0)" id="rhref_mileage" class="blue_col J_more_or_less">更多</a>
        <span class="todo_right"></span>
    </div>
    <div id="show_mileage">
        <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_mileage">
            <col width="40px">
            <col width="60px">
            <col width="120px">
            <col width="120px">
            <col width="70px">
            <col width="70px">
            <col width="70px">
            <col width="80px">
            <tr class="title">
                <td>No</td>
                <td>车牌号</td>
                <td>车主信息</td>
                <td>客户信息</td>
                <td>下次保养里程</td>
                <td>当前里程</td>
                <td>距保养里程</td>
                <td>操作</td>
            </tr>
        </table>
    </div>
    <!--分页-->
    <div class="hidePageAJAX" id="mileage_page">
        <bcgogo:ajaxPaging url="remind.do?method=searchMileageList" postFn="init_aaa" hide="hideIt"
                           dynamical="dynamical_mileage"/>
    </div>
    <div class="height clear"></div>
    <%--</c:if>--%>
</bcgogo:hasPermission>


<!--互动类-->
<bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.MIRROR_TALK_MSG">
    <%--<c:if test='<%=(access==null || "undefined".equals(access) || "repair".equals(access))%>'>--%>
    <%--<input type="hidden" id="remindType" />--%>
    <%--<input type="hidden" id="rPage" value="<%=(request.getAttribute("rPage")==null?"no":request.getAttribute("rPage"))%>"/>--%>
    <div class="distance new_title qiankuan clear">
        <span class="todo_left"></span>

        <div class="todo_center">客户互动提醒
                <%--<label style="cursor:pointer;" id="allRepair">共--%>
                <%--<strong class="qian_blue count" id="repairRemindSizeHint">--%>
                <%--</strong>&nbsp;条历史记录--%>
                <%--</label>--%>
                <%--(<c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}"><label style="cursor:pointer;" id="lack">缺料待修--%>
                <%--<strong class="qian_blue count" id="lackHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<label style="cursor:pointer;" id="incoming">来料待修--%>
                <%--<strong class="qian_blue count" id="incomingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</c:if><label style="cursor:pointer;" id="pending">待交付--%>
                <%--<strong class="qian_blue count" id="pendingHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">--%>
                <%--<label style="cursor:pointer;" id="waitOutStorage">待领料--%>
                <%--<strong class="qian_blue count" id="waitOutStorageHint">--%>
                <%--</strong>&nbsp;条</label>--%>
                <%--</bcgogo:hasPermission>--%>

                <%--<label style="cursor:pointer;"  onclick="getCarHistory('-1')">昨日服务--%>
                <%--<strong class="qian_blue count" id="serviceYesterdayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistory('0')">今日服务--%>
                <%--<strong class="qian_blue count" id="serviceTodayTimesHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--<label style="cursor:pointer;" onclick="getCarHistoryByTodayNewCustomer('0')">其中新增--%>
                <%--<strong class="qian_blue count" id="todayNewUserNumberHint">--%>
                <%--</strong>&nbsp;辆</label>--%>
                <%--)--%>

        </div>
        <a href="javascript:void(0)" id="rhref_message" class="blue_col J_more_or_less">更多</a>
        <span class="todo_right"></span>
    </div>
    <div id="show_message">
        <table cellpadding="0" cellspacing="0" class="todo_tab" id="tab_talk_message">
            <col width="40px">
            <col width="100px">
            <col width="120px">
            <col width="120px">
            <col width="100px">
            <col width="80px">
            <col width="100px">
            <col width="80px">
            <col width="80px">
            <tr class="title">
                <td>No</td>
                <td>车牌号</td>
                <td>车主信息</td>
                <td>所属客户</td>
                <td>车主消息时间</td>
                <td>车主最近一次消息</td>
                <td>回复时间</td>
                <td>最后一次回复内容</td>
                <td>操作</td>
            </tr>
        </table>
    </div>
    <!--分页-->
    <div class="hidePageAJAX" id="message_page">
        <bcgogo:ajaxPaging url="remind.do?method=getShopTalkMessageList" postFn="initTalkMessage" hide="hideIt"
                           dynamical="dynamical_talk_message"/>
    </div>
    <div class="height clear"></div>
    <%--</c:if>--%>
</bcgogo:hasPermission>
<!--本店计划类-->
<%--<c:if test="<%=getPlans%>">--%>
<%--<div class="new_title shop_item distance1 clear">--%>
<%--<span class="todo_left"></span>--%>

<%--<div class="todo_center">本店计划项目 <label>共<strong class="qian_blue" id="plans">${plans}</strong>条记录</label></div>--%>
<%--<a href="javascript:void(0)">更多</a>--%>
<%--<span class="todo_right"></span>--%>
<%--</div>--%>
<%--<table cellpadding="0" cellspacing="0" class="todo_tab shop_tab tab_five" id="tab_five">--%>
<%--<col width="50">--%>
<%--<col width="140">--%>
<%--<col/>--%>
<%--<col width="163">--%>
<%--<col width="120">--%>
<%--<col width="60">--%>
<%--<col width="60" style="*width:50px;">--%>
<%--<!--<col width="42">-->--%>
<%--<tr class="title">--%>
<%--<td>No</td>--%>
<%--<td>提醒项目</td>--%>
<%--<td>内容</td>--%>
<%--<td>对象</td>--%>
<%--<td>预计时间</td>--%>
<%--<td>状态</td>--%>
<%--<c:choose>--%>
<%--<c:when test="<%=smsSend%>">--%>
<%--<td>提醒</td>--%>
<%--</c:when>--%>
<%--<c:otherwise>--%>
<%--<td>操作</td>--%>
<%--</c:otherwise>--%>
<%--</c:choose>--%>
<%--</tr>--%>
<%--</table>--%>
<%--<!--分页-->--%>
<%--<div class="hidePageAJAX">--%>
<%--<jsp:include page="/common/pageAJAX.jsp">--%>
<%--<jsp:param name="url" value="remind.do?method=getPlans"></jsp:param>--%>

<%--<jsp:param name="jsHandleJson" value="initTr5"></jsp:param>--%>
<%--<jsp:param name="hide" value="hideIt"></jsp:param>--%>
<%--<jsp:param name="dynamical" value="dynamical5"></jsp:param>--%>
<%--</jsp:include>--%>

<%--</div>--%>
<%--</c:if>--%>
</div>

<%--<c:if test="<%=addPlan%>">--%>
<%--<a href="javascript:addPlan()" class="new_plan qian_blue">新增计划</a>--%>
<%--</c:if>--%>
</div>
<!--弹出框-->
<div id="mask" style="display:block;position: absolute;"></div>
<input id="isAllMakeTime" type="hidden" value="0">
<input type="button" style="display:none" id="hidBtn"/>
<input id="huankuanTime" type="hidden">

<iframe id="iframe_PopupBox" style="position:absolute;z-index:9; left:400px; top:400px; display:none;"
        allowtransparency="true" width="930px" height="900px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:4; left:200px; top:200px; display:none;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:4; left:200px; top:200px; display:none;"
        allowtransparency="true" width="1000px" height="1850px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:10;top:50px;left:100px;display:none; "
        allowtransparency="true" width="350px" height="150px" frameborder="0" src="" scrolling="no"></iframe>

<div class="i_searchBrand" style="position:fixed; left:50%; top:50%; margin-left:-410px;display:none;z-index:8"
     id="setLocation">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">新增计划</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <table cellpadding="0" cellspacing="0" class="table2" id="planRemindTable">
            <col width="30" style="*width:30px;">
            <col width="85" style="*width:85px;">
            <col/>
            <col width="95" style="*width:95px;">
            <col width="110" style="*width:110px;">
            <col width="40">
            <tr class="table_title">
                <td style="-borderleft:none;">No</td>
                <td>提醒项目</td>
                <td>内容</td>
                <td>客户名</td>
                <td>预计时间</td>
                <td style="border-right:none;">操作</td>
            </tr>
            <tr class="addPlan" id="0">
                <td style="border-left:none;" class="addPlan_num">1</td>
                <td class="font">
                    <input type="text" id="remindType_0" maxlength="20"/>
                </td>
                <td>
                    <input type="text" id="content_0" style="width:98%;*width:95%;" maxlength="500"/>
                </td>
                <td class="pone_plan">
                    <input type="hidden" id="customerType_0"/>
                    <input type="hidden" id="customerIds_0"/>
                    <input type="text" id="customerNames_0" readonly=""/>
                    <img src="images/phone.jpg" class="addCusBtn"/>
                </td>
                <td class="time_plan">
                    <input readonly="" type="text" id="remindTime_0"/>
                </td>
                <td style="border-right:none;">
                    <img class="delete_opera1" src="images/opera1.jpg" onclick="deleteRow(this)"/>
                    <img src="images/opera2.jpg" onclick="addRow()"/>
                </td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="more_his">
            <input type="button" value="确认" onfocus="this.blur();" class="btn" id="submitBtn"/>
            <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
<%@ include file="/remind/talkDialog.jsp" %>
<%@ include file="/remind/pushMessage/impact/impactDetail.jsp" %>

<iframe id="iframe_PopupBox1" style="position:absolute;z-index:5; left:200px; top:200px; display:none;"
        allowtransparency="true" width="900px" height="600px" scrolling="no" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>


<%@include file="/remind/sendMaintainMessage.jsp" %>

</body>
</html>
