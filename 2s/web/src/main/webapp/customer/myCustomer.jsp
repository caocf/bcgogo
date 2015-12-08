<%@ page import="com.bcgogo.user.service.IUserService" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //手机号部分隐藏开关
    boolean mobileHiddenTag = ServiceManager.getService(IUserService.class).isMobileSwitchOn((Long) request.getSession().getAttribute("shopId"));
    //当前用户的用户组名称
    String userGroup = (String) request.getSession().getAttribute("userGroupName");
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户查询</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/customData<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/mergeCustomer<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/customerTooltip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_DATA");

        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
        APP_BCGOGO.Permission.CustomerManager.CustomerArrears =${WEB_CUSTOMER_MANAGER_CUSTOMER_ARREARS};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
        APP_BCGOGO.Permission.Version.RelationCustomer =${WEB_VERSION_RELATION_CUSTOMER};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
        APP_BCGOGO.Permission.CustomerManager.UpdateCustomer =${WEB_CUSTOMER_MANAGER_CUSTOMER_UPDATE};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam permissions="WEB.VERSION.PRODUCT.THROUGH_DETAIL">
        APP_BCGOGO.Permission.Version.ProductThroughDetail = ${WEB_VERSION_PRODUCT_THROUGH_DETAIL};
        </bcgogo:permissionParam>

        APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};

    </script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/page/customer/customerData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/mergeCustomer<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/myCustomer<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/myCustomerOrSupplier<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>
    <script type="text/javascript">
        APP_BCGOGO.UserGuide.currentPageIncludeGuideStep = "CONTRACT_CUSTOMER_GUIDE_CUSTOMER_DATA,CONTRACT_CUSTOMER_GUIDE_RECOMMEND_CUSTOMER,CONTRACT_CUSTOMER_GUIDE_SINGLE_APPLY";
        APP_BCGOGO.UserGuide.currentPage = "customerData";
        var fromUserGuideStep = '${param.fromUserGuideStep}';
        <c:choose>
        <c:when test="${pageTip=='data'}">
        window.parent.location = "customer.do?method=customerdata";
        </c:when>
        </c:choose>

        //author:zhangjuntao
        var objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr;
        function enterPhoneSendSms(objEnterPhoneMobile) {
            sendSms(objEnterPhoneMobile, objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo,
                    objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr);
        }

        function sendSms(mobile, type, arrears, licenceNo, date, name, customerIdStr) {        // type <!-- 0 保险  1 验车  2 生日-->
            if (mobile == null || $.trim(mobile) == "") {
                $("#enterPhoneCustomerId").val(customerIdStr);
                Mask.Login();
                $("#enterPhoneSetLocation").fadeIn("slow");
                objEnterPhoneType = type;
                objEnterPhoneArrears = arrears;
                objEnterPhoneLicenceNo = licenceNo;
                objEnterPhoneDate = date;
                objEnterPhoneName = name;
                objEnterPhoneCustomerIdStr = customerIdStr;
                return;
            }

            if (arrears == 0.0) {
                window.location = encodeURI("sms.do?method=smswrite&mobile=" + $.trim(mobile)+"&customerId=" + customerIdStr);
            } else {
                var dates = date.split("-");
                var month = dates[1];
                var day = dates[2];
                window.location = encodeURI("sms.do?method=smswrite&mobile=" + $.trim(mobile) + "&type=" + type + "&money=" + arrears + "&licenceNo=" + licenceNo + "&month=" + month + "&day=" + day + "&name=" + name + "&customerId=" + customerIdStr);
            }
        }

        $().ready(function () {
            // 权限设定，手机号码部分隐藏
            var mobileHiddenTag = <%=mobileHiddenTag%>;
            var userGroup = "<%=userGroup%>";
            if (mobileHiddenTag == true && userGroup != "BCGOGO管理员" && userGroup != "老板/财务") {
                APP_BCGOGO.Permission.isMobileHidden = true;
            } else {
                APP_BCGOGO.Permission.isMobileHidden = false;
            }
        var filter = GLOBAL.Util.getUrlParameter("filter");
        searchBtnClick(filter);

        });

    </script>
    <script type="text/javascript">
        $(function() {
            $(".tabSlip tr").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
            $(".tabSlip tr:nth-child(odd)").css("background", "#eaeaea");
            $(".tabSlip tr").not(".titleBg").hover(
                    function () {
                        $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px","color":"#ff4800"});

                        $(this).css("cursor", "pointer");
                    },
                    function () {
                        $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px","color":"#272727"});
                        $(".tabSlip tr:nth-child(odd)").not(".titleBg").find("td").css("background", "#eaeaea");
                    }
            );

            $("#addUp").click(function() {
                location.href = "add_innerPicking.html";
            })

            $(".alert").hide();

            $(".hover").hover(function(event) {
                var _currentTarget = $(event.target).parent().find(".alert");
                _currentTarget.show();

                //因为有2px的空隙,所以绑定在parent上.
                _currentTarget.parent().mouseleave(function(event) {
                    event.stopImmediatePropagation();

                    if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                        _currentTarget.hide();
                    }
                });

            }, function(event) {
                var _currentTarget = $(event.target).parent().find(".alert");

                if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                    $(event.target).parent().find(".alert").hide();
                }

            });


            $(".icon").hover(function(event) {
                var _currentTarget = $(event.target).parent().find(".alert");
                _currentTarget.show();

                //因为有2px的空隙,所以绑定在parent上.
                _currentTarget.parent().mouseleave(function(event) {
                    event.stopImmediatePropagation();

                    if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                        _currentTarget.hide();
                    }
                });

            }, function(event) {
                var _currentTarget = $(event.target).parent().find(".alert");

                if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                    $(event.target).parent().find(".alert").hide();
                }

            });

        })
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
    <input type="hidden" id="smsSendPermission" value="${permissionParam1}"/>
</bcgogo:permissionParam>
<input type="hidden" id="quanxian" value="${userGroupName}">
<%--存放选择客户的id--%>
<div id="selectedIdArray">
</div>

<div class="title">
    <div class="title_label">
        <ul>
        </ul>
    </div>
</div>
<div class="i_main clear">
<div class="i_search">

<div class="i_main clear">
<div class="mainTitles">
    <div class="cusTitle">客户查询</div>
    <c:if test="${wholesalerVersion}">
        <div style="float: right;width: 212px;margin-top: 12px;"><a href="apply.do?method=getApplyCustomersIndexPage"><img style="cursor: pointer;" src="images/lookingCustomers.gif"></a></div>
    </c:if>
</div>
<div class="i_mainRight">
<div class="titBody">
    <div class="lineTitle">客户搜索</div>
    <div class="lineTop"></div>
    <div class="lineBody lineAll">
        <div class="i_height"></div>
        <div class="divTit">
            <span class="spanName">客户</span>&nbsp;<input type="text" class="txt" style="width:195px;color: #ADADAD;"
                                                         id="customerInfoText"
                                                         pagetype="customerdata"
                                                         initialValue="客户/联系人/手机/车牌号/会员号" value="客户/联系人/手机/车牌号/会员号"/>

            <input type="hidden" value="" id="customerId">
            <input type="hidden" value="${customerIds}" id="customerIds">
        </div>

        <c:if test="${wholesalerVersion}" var="isWholesalerVersion">
            <div class="divTit">
                所属区域&nbsp;<select class="txt" style="color: #ADADAD;" id="provinceNo" name="province">
                <option class="default" value="">--所有省--</option>
            </select>&nbsp;<select class="txt" style="color: #ADADAD;" id="cityNo" name="city">
                <option class="default" value="">--所有市--</option>
            </select>&nbsp;<select class="txt" style="color: #ADADAD;" id="regionNo" name="region">
                <option class="default" value="">--所有区--</option>
            </select>
            </div>
        </c:if>

        <c:if test="${!wholesalerVersion}">
            <div class="divTit">
                <span class="spanName">车辆信息</span>&nbsp;
                <input type="text" id="vehicleBrandSearch" class="txt" style="width:100px;color: #ADADAD;"
                       pagetype="customerdata"
                       initialValue="车辆品牌" value="车辆品牌"/>
                <input type="text" id="vehicleModelSearch" class="txt" style="width:100px;color: #ADADAD;"
                       pagetype="customerdata"
                       initialValue="车型" value="车型"/>
                <input type="text" id="vehicleColorSearch" class="txt" style="width:100px;color: #ADADAD;"
                       pagetype="customerdata"
                       initialValue="车身颜色" value="车身颜色"/>
            </div>
        </c:if>

        <div class="divTit">
            <span class="spanName">交易商品</span>&nbsp;
            <%--<input type="text" class="txt J-productSuggestion J-initialCss J_clear_input txt" id="searchWord"--%>
            <%--name="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆"--%>
            <%--style="width:190px; margin-left:-6px;"/>--%>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productName"
                   name="productName" searchField="product_name" initialValue="品名"
                   style="width:70px;margin-left:-6px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand"
                   name="productBrand" searchField="product_brand" initialValue="品牌/产地" style="width:70px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec"
                   name="productSpec" searchField="product_spec" initialValue="规格" style="width:70px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel"
                   name="productModel" searchField="product_model" initialValue="型号" style="width:70px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand"
                   name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌"
                   style="width:70px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel"
                   name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型"
                   style="width:70px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode"
                   name="commodityCode" searchField="commodity_code" initialValue="商品编号"
                   style="text-transform: uppercase; width:70px;"/>
        </div>

        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">

                <input type="hidden" id="allCardName" value="${cardNames}"/>

                <div class="divTit divWarehouse member">
                    <span class="spanName">是否会员</span>

                    <div class="warehouseList">
                        <a class="btnList" id="noMemberRadio" name="memberSelect"/>非会员</a>
                        <a class="btnList" id="memberRadio" name="memberSelect"/>会员</a>
                        <c:if test="${memberCardTypes!=null}">
                            <c:forEach items="${memberCardTypes}" var="memberCard" varStatus="status">
                                <a class="btnList" name="memberCardTypes" value='${memberCard}'/>${memberCard}</a>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>

            </bcgogo:hasPermission>
        </bcgogo:hasPermission>

        <div class="divTit">
            <span class="spanName">最后交易时间</span>&nbsp;
            <a class="btnList" id="my_date_yesterday" pagetype="customerdata" name="my_date_select">昨天</a>&nbsp;
            <a class="btnList" id="my_date_today" pagetype="customerdata" name="my_date_select">今天</a>&nbsp;
            <a class="btnList" id="my_date_thisweek" pagetype="customerdata" name="my_date_select">最近一周</a>&nbsp;
            <a class="btnList" id="my_date_thismonth" pagetype="customerdata" name="my_date_select">最近一月</a>&nbsp;
            <a class="btnList" id="my_date_thisyear" pagetype="customerdata" name="my_date_select">最近一年</a>&nbsp;
            <input
                type="text" id="startDate" name="startDateStr" class="my_startdate txt"/>&nbsp;至&nbsp;<input id="endDate"
                                                                                                name="endDateStr"
                                                                                                type="text"
                                                                                                class="my_enddate txt"/>
        </div>

        <div class="divTit button_condition">
            <a class="blue_color clean" id="clearConditionBtn">清空条件</a>
            <a class="button" id="customerSearchBtn">搜 索</a>
            <input type="hidden" id="resetSearchCondition" value="${resetSearchCondition}"/>
        </div>
    </div>
    <div class="lineBottom"></div>
    <div class="clear i_height"></div>
</div>
<div class="supplier group_list listStyle" style="background:url(images/cu_Body_v2.png) no-repeat;width970px;height: 40px;float: left;line-height: 20px;padding: 5px 15px 5px 15px;">
    <div style="float: left;width:auto">
        <div style="height: 20px;margin: 0px;">
            <strong>客户统计：</strong>
            <span style="cursor:pointer;" id="totalNumSpan">共有：<b class="blue_color" id="totalNum">0</b>名</span>&nbsp;&nbsp;
            <span style="cursor:pointer;" id="todayCustomerSpan">今日新增：<b class="blue_color" id="todayCustomer">0</b>名</span>&nbsp;&nbsp;
            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <span style="cursor:pointer;" id="memberNumSpan">会员：<b class="blue_color" id="memberNum">0</b>名</span>&nbsp;&nbsp;
                </bcgogo:hasPermission>
            </bcgogo:hasPermission>
            <span style="cursor:pointer;" id="mobileNumSpan">手机客户：<b class="blue_color" id="mobileNum">0</b>名<a class="phone" style="float: none;" title="群发短信" id="sendMulSms"></a></span>&nbsp;&nbsp;
            <c:if test="${!wholesalerVersion}">
                <span id="totalAppSpan">App客户：<b class="blue_color" id="totalApp">0</b>名</span>&nbsp;&nbsp;
                <span id="totalOBDSpan">OBD客户：<b class="blue_color" id="totalOBD">0</b>名</span><a class="phone" style="float: none;" title="群发短信" id="sendMulSmsODB"></a>&nbsp;&nbsp;
            </c:if>
            <span style="cursor:pointer;" id="debtCustomerCountSpan">应收客户：<b class="blue_color" id="debtCustomerCount">0</b>名</span>&nbsp;&nbsp;
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
                <a id="relatedNumSpan"><b style="color: #FF6600;">在线店铺:<span id="relatedNum">0</span>家</b></a>
            </bcgogo:hasPermission>
        </div>
        <div style="height: 20px;margin: 0px;">
            <strong>收入统计：</strong>
            <span style="cursor:pointer;" id="totalConsumptionSpan">累计消费：<b class="blue_color" id="totalConsumption">0</b>元</span>&nbsp;&nbsp;
            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <span style="cursor:pointer;" id="totalBalanceSpan">会员储值：<b class="blue_color" id="totalBalance">0</b>元</span>&nbsp;&nbsp;
                </bcgogo:hasPermission>
            </bcgogo:hasPermission>
            <span style="cursor:pointer;" id="totalDebtStatSpan">应收总额：<b class="yellow_color" id="totalDebtStat">0</b>元</span>&nbsp;&nbsp;
            <span style="cursor:pointer;" id="totalReturnDebtSpan">应付总额：<b class="yellow_color" id="totalReturnDebt">0</b>元</span>&nbsp;&nbsp;
            <bcgogo:hasPermission permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
                <span style="cursor:pointer;" id="totalDepositStatSpan">预收款：<b class="yellow_color" id="totalDepositStat">0</b>元</span>&nbsp;&nbsp;
            </bcgogo:hasPermission>
        </div>
    </div>
    <div style="float: right;width:auto;line-height: 40px;">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <a class="addNewSup blue_color" id="input_addUser">新增客户</a>&nbsp;
        </bcgogo:hasPermission>

        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
            <a class="recommended blue_color" style="display: none;" id="sentInvitationCodePromotionalSms">推荐使用软件</a>
            <div class="tixing alert" style="left: 820px; margin-top:28px; display: none;">
                <div class="ti_top"></div>
                    <div class="ti_body alertBody" style="color: #FF5E04;">
                        <div>您可推荐未使用一发软件的供应商使用:</div>
                        <div>1、成功推荐1家，即可获得200元短信返利！</div>
                        <div>2、一站式比价采购，节省成本！</div>
                        <div>3、海量供应商供您选择！</div>
                    </div>
                <div class="ti_bottom"></div>
            </div>
        </bcgogo:hasPermission>

    </div>
    <input id="filterType" type="hidden">

</div>
<div class="clear i_height"></div>
<div class="cuSearch">
<div class="cartTop"></div>
<div class="cartBody">

<div class="line_develop list_develop sort_title_width">
    <div class="sort_label">排序方式：</div>
    <a class="J_supplier_sort" style="padding:0px 5px 0px 0px;" id="createdTimeSort" currentSortStatus="Desc" ascContact="点击后按录入时间升序排列！" descContact="点击后按录入时间降序排列！">
        录入时间<span id="createdTimeSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span>
        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>
            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按录入时间升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>

    <a class="J_supplier_sort" id="lastInventoryTimeSort" currentSortStatus="Desc" ascContact="点击后按最后消费日期升序排列！" descContact="点击后按最后消费日期降序排列！">
        最后消费日期<span id="lastInventoryTimeSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span>
        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>
            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按最后入库日期升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>

    <a class="J_supplier_sort accumulative" currentSortStatus="Desc" ascContact="点击后按累计交易金额升序排列！" descContact="点击后按累计交易金额降序排列！">
        <span id="totalTradeAmountSort" style="margin-right: 0px;">累计交易金额<span id="totalTradeAmountSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>
        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>
            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按累计交易金额升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>

    <span class="txtTransaction" style="margin-right: 0px; padding-right: 5px;">
        <input type="text" id="totalTradeAmountStart" class="txt" style="width:30px; height:17px;"/>&nbsp;至
        <input type="text" id="totalTradeAmountEnd" class="txt" style="width:30px; height:17px;"/>
    </span>

    <div class="txtList" id="totalTradeAmountDiv"
         style=" left:353px; padding-top:30px; display:none; width: 87px;">
        <span style="cursor: pointer;" class="clean" id="totalTradeAmountClear">清除</span>
        <span class="btnSure" id="totalTradeAmountSure">确定</span>

        <div class="listNum" id="totalTradeListNum">
            <span class="blue_color" id="totalTradeAmount_1">1千以下</span>
            <span class="blue_color" id="totalTradeAmount_2">1千~5千</span>
            <span class="blue_color" id="totalTradeAmount_3">5千~1万</span>
            <span class="blue_color" id="totalTradeAmount_4">1万以上</span>
        </div>
    </div>

    <a class="J_supplier_sort accumulative" style="padding:0px 5px 0px 5px;" currentSortStatus="Desc" ascContact="点击后按应收金额升序排列！" descContact="点击后按应收金额金额降序排列！">
        <span id="totalReceivableSort" style="margin-right:0px;">应收金额<span id="totalReceivableSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>
        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>
            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按应收金额金额升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>
    <span class="txtTransaction" style="margin-right: 0px; padding-right: 5px;">
        <input type="text" class="txt" id="totalReceivableStart" style="width:30px; height:17px;"/>&nbsp;至
        <input type="text" class="txt" id="totalReceivableEnd" style="width:30px; height:17px;"/>
    </span>

    <div class="txtList" id="totalReceivableDiv" style=" left:527px; padding-top:30px; display:none; width:87px;">
        <span style="cursor: pointer;" class="clean" id="totalReceivableClear">清除</span>
        <span class="btnSure" id="totalReceivableSure">确定</span>

        <div class="listNum" id="totalReceivableListNum">
            <span class="blue_color" id="totalReceivable_1">1千以下</span>
            <span class="blue_color" id="totalReceivable_2">1千~5千</span>
            <span class="blue_color" id="totalReceivable_3">5千~1万</span>
            <span class="blue_color" id="totalReceivable_4">1万以上</span>
        </div>
    </div>

    <%--<a class="accumulative"><span id="totalPayableSort">应付金额<span id="totalPayableSortSpan" class="arrowDown"></span></span></a>--%>

    <a class="J_supplier_sort accumulative" style="padding:0px 5px 0px 5px;" currentSortStatus="Desc" ascContact="点击后按应付金额升序排列！" descContact="点击后按应付金额金额降序排列！">
        <span id="totalPayableSort" style="margin-right: 0px;">应付金额<span id="totalPayableSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>
        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>
            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按应付金额升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>

    <span class="txtTransaction" style="margin-right: 0px; padding-right: 5px;">
        <input type="text" id="debtAmountStart" class="txt" style="width:30px; height:17px;"/>&nbsp;至
        <input type="text" id="debtAmountEnd" class="txt" style="width:30px; height:17px;"/>
    </span>

    <div class="txtList" id="debtAmountDiv" style=" left:701px; padding-top:30px; display:none; width:87px;">
        <span style="cursor: pointer;" class="clean" id="debtAmountClear">清除</span>
        <span class="btnSure" id="debtAmountSure">确定</span>

        <div class="listNum" id="debtAmountListNum">
            <span class="blue_color" id="debtAmount_1">1千以下</span>
            <span class="blue_color" id="debtAmount_2">1千~5千</span>
            <span class="blue_color" id="debtAmount_3">5千~1万</span>
            <span class="blue_color" id="debtAmount_4">1万以上</span>
        </div>
    </div>

    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
        <a class="J_supplier_sort" style="margin-top: 0px;padding:0px 5px 0px 5px;" currentSortStatus="Desc" ascContact="点击后按预收款升序排列！" descContact="点击后按预收款降序排列！">
            <span id="depositSort" style="margin-right: 0px;">预收款<span id="depositSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>
            <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                <span class="arrowTop" style="margin-left:20px;"></span>
                <div class="alertAll">
                    <div class="alertLeft"></div>
                    <div class="alertBody">
                        点击后按预收款升序排列！
                    </div>
                    <div class="alertRight"></div>
                </div>
            </div>
        </a>
    </bcgogo:hasPermission>

    <div style="display: inline-block;float: right;margin-right: 10px;">
        <img style="margin: 5px;float: left;" src="images/mergeIcon.png">
             <a class="blue_color" style="padding: 0px;border-right-width: 0px;" href="customer.do?method=toMergeRecord" target="_blank">查看合并记录</a>
    </div>

</div>

<input type="hidden" name="rowStart" id="rowStart" value="0">
<input type="hidden" name="pageRows" id="pageRows" value="15">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" name="sortStatus" id="sortStatus" value="">
<input type="hidden" name="sortStr" id="sortStr" value="">
<input type="hidden" name="hasDebt" id="hasDebt" value="">
<input type="hidden" name="hasReturnDebt" id="hasReturnDebt" value="">
<input type="hidden" name="hasDeposit" id="hasDeposit" value="">
<input type="hidden" name="hasBalance" id="hasBalance" value="">
<input type="hidden" name="hasTotalConsumption" id="hasTotalConsumption" value="">


<table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="customerDataTable"></table>
<div class="height"></div>


<jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="customer.do?method=searchCustomerDataAction"></jsp:param>
    <jsp:param name="data" value="{startPageNo:1,maxRows:15,customerOrSupplier:'customer'}"></jsp:param>
    <jsp:param name="jsHandleJson" value="initCustomerDataTr"></jsp:param>
    <jsp:param name="dynamical" value="customerSuggest"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
</jsp:include>

<div class="i_height clear"></div>
<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MERGE">
    <a class="btnMerger" id="mergeCustomerBtn">合并客户</a>
</bcgogo:hasPermission>
<c:if test="${!wholesalerVersion}">
    <a class="btnMerger" id="export">导出会员</a>
    <img id="exporting" style="margin-left: 30px; display: none;" title="正在导出" alt="正在导出"
         src="images/loadinglit.gif">
    <input type="hidden" id="exportMemberClicked"/>
</c:if>

</div>
<div class="cartBottom"></div>
</div>
</div>


</div>

</div>

</div>
<c:if test="${shopIdTip!=null && shopIdTip=='lostShopId'}">
    <script type="text/javascript">
        nsDialog.jAlert("店铺Id缺失!");
    </script>
</c:if>

<c:if test="${nameLicenceNoTip!=null && nameLicenceNoTip=='lostNameLicenceNo'}">
    <script type="text/javascript">
        nsDialog.jAlert("客户名必须填写!");
    </script>
</c:if>

<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5;margin:140px 0 0 30px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6; display:none;"
        allowtransparency="true" width="730px" height="450px" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<%@ include file="/sms/enterPhone.jsp" %>

<div id="deleteCustomer_dialog">
    <div id="deleteReceiptNo"></div>
</div>

<!-- 合并普通客户弹出框-->
<div id="mergeCustomerDetail" style="display: none;">
    <jsp:include page="merge/mergeCustomerDetail.jsp"></jsp:include>
</div>

<!-- 合并关联客户弹出框-->
<div id="mergeRelatedCustomerDetail" style="display: none;">
    <jsp:include page="merge/mergeRelatedCustomerDetail.jsp"></jsp:include>
</div>
<div pop-window-name="input-mobile" style="display: none;">
    <div style="margin-left: 10px;margin-top: 10px">
        <label>手机号：</label>
        <input type="text" pop-window-input-name="mobile" maxlength="11" style="width:125px;height: 20px">
    </div>
</div>
<div id="cancelShopRelationDialog" style="display: none;">
    该用户是您的关联客户，取消关联后您将无法在线处理订单，是否确认取消？若确认，请填写取消理由。
    <textarea id="cancel_msg" init_word="取消关联理由" maxLength=70 style="width:270px;height: 63px;margin-top: 7px;"
              class="gray_color">取消关联理由</textarea>
</div>
<div class="alert" id="payableReceivableAlert" style="display: none;">
    点击后对账
</div>
<div id="multi_alert" class="tixing alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top"></div>
    <div class="ti_body" style="color: #FF5E04;">
        <div>您可推荐未使用一发软件的客户使用，推荐成功使用，您可拥有:</div>
        <div>1、200元短信返利！</div>
        <div>2、追踪客户商品销售情况！</div>
        <div>3、在线处理订单，简单快捷！</div>
        <div>4、促销商品消息，营销便利！</div>
        <div>5、海量客户！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<div id="single_alert" class="tixing alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top tiTop"></div>
    <div class="ti_body" style="color: #FF5E04;">
        <div>您可推荐未使用一发软件的客户使用，推荐成功，您可拥有:</div>
        <div>1、200元短信返利！</div>
        <div>2、追踪客户商品销售情况！</div>
        <div>3、在线处理订单，简单快捷！</div>
        <div>4、促销商品消息，营销便利！</div>
        <div>5、海量客户！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>