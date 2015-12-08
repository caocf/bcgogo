<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/login<%=ConfigController.getBuildVersion()%>.css"/>

<bcgogo:hasPermission permissions="WEB.AD_SHOW">
<%--<script type="text/javascript" src="js/advertisement<%=ConfigController.getBuildVersion()%>.js"></script>--%>
</bcgogo:hasPermission>
<script type="text/javascript">
    $().ready(function () {

        if ($(".newAnnouncement").find(".border").size() > 1) {
            $(".newAnnouncement .border").not(":last").css("border-bottom", "1px solid #FFB562");
        }

        $(".J-newtodo").live("click", function () {
            window.location = "remind.do?method=newtodo&access=" + $(this).attr("access");
        });

        //导航栏提醒数字
        APP_BCGOGO.Net.asyncGet({
            url:"remind.do?method=getTitlePromptNums",
            data:{
                "now":new Date().getTime()
            },
            dataType:"json",
            success:function(result) {
                if(!result || !result.success || result.data == null){
                    return;
                }
                var data = result.data;
                for(var p in data){
                    if(data[p] > 0){
                        var css = ".j_"+p;
                        $(css).text(data[p]).show();
                        if(p === "todoRemindAmount"){
                          $(".J_todoRemindNaviAmount").text(data[p]).show();
                        }
                    }
                }
            },
            error:function(){
                GLOBAL.error("remind.do?method=getTitlePromptNums error");
            }
        });

    });

    function closeRemider(type, lastReleaseDate, dom) {
        $.ajax({
            type: "POST",
            url: "sysReminder.do?method=updateUserReadRecord",
            async: false,
            data: {reminderType: type, lastReadDate: lastReleaseDate},
            cache: false,
            dataType: "json"
        });
        var reminderNum = $(dom).closest(".newAnnouncement").find(".ti_body").size();
        if (reminderNum > 1) {
            $(dom).closest(".border").remove();
        } else {
            $(".newAnnouncement").children().remove()
        }
        $(".newAnnouncement .border:last").css("border-bottom", "none");
    }

    function toSmsSend(lastReleaseDate, dom) {
        window.open("sms.do?method=smswrite");
        closeRemider("FESTIVAL", lastReleaseDate, dom);
    }

    function hideAnnouncement() {
        $(".newAnnouncement").hide();
    }

</script>

<bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.BASE">
    <input value="${WEB_CUSTOMER_MANAGER_BASE}" type="hidden" id="customerPermission"/>
</bcgogo:permissionParam>

<bcgogo:permissionParam permissions="WEB.TXN.BASE" resourceType="menu">
    <input value="${WEB_TXN_BASE}" type="hidden" id="txnPermission"/>
</bcgogo:permissionParam>
<%@ include file="/common/customerQQService.jsp" %>
<bcgogo:hasPermission permissions="WEB.SYS.REMINDER_ANNOUNCEMENT">
    <bcgogo:hasNewReminder>
        <c:if test="${newAnnouncementFlag || festivals!=null || (trialUseDays!=null && trialUseDays < 30)}">
        <div class="newAnnouncement topItem" style="display: none;">
            <div class="ti_top topItem"></div>
            <div class="ti_body topItem">
                <c:if test="${newAnnouncementFlag}">
                    <div class="border topItem" style="border-bottom:none;">
                        <span action-type="toSysAnnouncement" url="sysReminder.do?method=toSysAnnouncement">新版本公告</span>
                        <a class="close"
                           onclick="closeRemider('ANNOUNCEMENT',${announce_lastReleaseDate},this)"></a>
                    </div>
                </c:if>
                <c:if test="${festivals!=null}">
                    <div class="border topItem" style="border-bottom:none;">
                        <span onclick="toSmsSend(${festival_lastReleaseDate})"> ${festivals}快到了，赶紧发短信祝福您的客户吧！</span>
                        <a class="close"
                           onclick="closeRemider('FESTIVAL',${festival_lastReleaseDate},this)"></a></div>
                </c:if>
                <c:if test="${trialUseDays!=null  && trialUseDays < 30}">
                    <div class="border not_underline topItem" style="border-bottom:none;">
                        <c:if test="${chargeType == 'ONE_TIME'}">
                            <a href="bcgogoReceivable.do?method=bcgogoReceivableOrderList" style="color:#FD5300;">软件试用期还剩${trialUseDays}天，请联系尽快交费！如有疑问请联系客服！</a>
                        </c:if>

                        <c:if test="${chargeType == 'YEARLY'}">
                            <span>软件一年免费使用期还剩${trialUseDays}天，请尽快缴年费！如有疑问请联系客服！</span>
                        </c:if>
                        <a class="close"
                           onclick="closeRemider('TRIAL_USE_DAYS',${trialUseDays_lastReleaseDate},this)"></a>
                    </div>
                </c:if>
            </div>
            <div class="ti_bottom topItem"></div>
        </div>
        </c:if>
    </bcgogo:hasNewReminder>
</bcgogo:hasPermission>
<div class="topBg">
    <div class="top">
        <div class="icon_TongGou topItem" onclick="openOrAssign('user.do?method=createmain')"></div>
        <div id="headerHomepageLink" class="topTit topItem" onclick="openOrAssign('user.do?method=createmain')" style="cursor: pointer;">首页</div>
        <span class="topBorder"></span>
        <span id="headerWelcomeLink" class="wordTit" onclick="openOrAssign('user.do?method=createmain')" style="cursor: pointer;">欢迎使用一发软件</span>
        <span class="wordTit">${shopName}</span>
        <span class="wordTit">${userName}【${userGroupName}】</span>
        <div class="right_top topItem">

            <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.SHOPPINGCART">
                <div id="headerShoppingCartDiv" class="shoppingCart topItem" url="shoppingCart.do?method=shoppingCartManage" action-type="menu-click" action-type="menu-click" menu-name="AUTO_ACCESSORY_ONLINE_SHOPPINGCART">
                    <div class="content-scb"><img src="images/cartIcon.png">购物车(<span id="shoppingCartNumber">0</span>)</div>
                    <div class="detailed-scb"></div>
                </div>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.SCHEDULE.MESSAGE_CENTER.RECEIVER.BASE">
                <div class="message" id="messageCenterNumber"  url="navigator.do?method=messageCenter" menu-name="MESSAGE_CENTER">
                    <div id="_messageCenterNumberImgFace" style="display:none;background:url(images/message_face.gif) no-repeat -1px -3px; float: left;width: 14px;height: 11px; margin:9px 5px 0 0;"></div>
                    <div id="_messageCenterNumberImg" style="background:url(images/message.png) no-repeat; float: left;width: 14px;height: 11px; margin:9px 5px 0 0;"></div>
                    消息(<span id="noticeTotalNumber">0</span>)
                </div>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                <a class="wordTit" href="shopRegister.do?method=registerMain&registerType=SUPPLIER_REGISTER">注册店铺</a>
            </bcgogo:hasPermission>
            <a class="wordTit" id="changeUserPassword">修改密码</a>
            <a class="wordTit" action-type="show_customer_service_info">客服</a>
            <a class="qqHead" ></a>
            <bcgogo:hasPermission permissions="WEB.SYS.REMINDER_ANNOUNCEMENT">
                <a class="wordTit" id="toSysAnnouncement" action-type="toSysAnnouncement" url="sysReminder.do?method=toSysAnnouncement"
                   menu-name="TO_SYS_ANNOUNCEMENT">公告</a>
                <a class="wordTit lastTit" url="help.do?method=toHelper" id="toHelper" menu-name="TO_HELPER">帮助</a>
                <a class="wordTit lastTit" url="http://www.bcgogo.com/bbs" onclick="toBBS()" menu-name="TO_HELPER">论坛</a>
            </bcgogo:hasPermission>
            <span class="topBorder"></span>
            <a class="icon_exist" id="j_logout">退出</a>
        </div>
    </div>
</div>
<div class="topMainBg">
    <div class="top">
        <ul class="topMain" id="head_menu">
            <bcgogo:permission>
                <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_SCHEDULE">
                </bcgogo:if>
                <bcgogo:else>
                    <bcgogo:hasPermission permissions="WEB.SCHEDULE.BASE" resourceType="menu">
                        <li url="navigator.do?method=schedule" menu-name="SCHEDULE">
                            <div class="topItem">
                                <a class="icon_newTodo"></a>
                                    <span class="navi_num j_todoRemindAmount"></span>
                                <a class="titleName">待办事项</a>
                            </div>
                        </li>
                    </bcgogo:hasPermission>
                </bcgogo:else>
            </bcgogo:permission>

            <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
                <li url="navigator.do?method=vehicleConstruction" menu-name="VEHICLE_CONSTRUCTION">
                    <div class="topItem">
                        <a class="icon_vehicle_construction"></a>
                        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.VEHICLE_CONSTRUCTION_BEAUTY">
                            <span class="navi_num J-newtodo j_todoRepairRemindAmount" access="repair"></span>
                        </bcgogo:hasPermission>
                        <a class="titleName">车辆施工</a>
                    </div>
                </li>
            </bcgogo:hasPermission>

            <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.DISABLE_TXN">
                <c:if test="${!WEB_VERSION_DISABLE_TXN}">
                    <bcgogo:hasPermission permissions="WEB.TXN.BASE" resourceType="menu">
                        <li url="navigator.do?method=txnNavigator" menu-name="TXN">
                            <div class="topItem">
                                <a class="icon_invoicing"></a>
                                <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.TXN">
                                    <span class="navi_num J-newtodo j_todoTxnRemindAmountNavi" access="txn"></span>
                                </bcgogo:hasPermission>
                                <a class="titleName">进销存</a>
                            </div>
                        </li>
                    </bcgogo:hasPermission>
                </c:if>
            </bcgogo:permissionParam>

            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE" resourceType="menu">
                <li url="navigator.do?method=customerManager" id="customerManagerLi" menu-name="CUSTOMER_MANAGER">
                    <div class="topItem">
                        <a class="icon_customerManage"></a>
                        <bcgogo:hasPermission
                                permissions="WEB.SCHEDULE.REMIND_TODO.ARREARS&&WEB.SCHEDULE.REMIND_TODO.CUSTOMER_SERVICE">
                            <span class="navi_num J-newtodo j_todoCustomerAmountNavi" access="customer"></span>
                        </bcgogo:hasPermission>
                        <a class="titleName">客户管理</a>
                    </div>
                </li>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE" resourceType="menu">
                <li url="navigator.do?method=supplierManager" id="supplierManagerLi" menu-name="SUPPLIER_MANAGER">
                    <div class="topItem">
                        <a class="icon_supplierManage"></a>
                        <a class="titleName">供应商管理</a>
                    </div>
                </li>
            </bcgogo:hasPermission>


            <bcgogo:hasPermission permissions="WEB.STAT.BASE" resourceType="menu">
                <li url="navigator.do?method=stat" menu-name="STAT">
                    <div class="topItem">
                        <a class="icon_financialStatistics"></a>
                        <a class="titleName">财务统计</a>
                    </div>
                </li>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.BASE" resourceType="menu">
                <li url="supplyDemand.do?method=toSupplyDemand"  userGuideTartet="PRODUCT_ONLINE_GUIDE_TXN" menu-name="AUTO_ACCESSORY_ONLINE">
                    <div class="topItem">
                        <a class="icon_orderCenter"></a>
                        <bcgogo:hasPermission permissions="WEB.TXN.ORDER_CENTER.BASE">
                            <span class="navi_num j_todoOrderAmount"></span>
                        </bcgogo:hasPermission>
                        <a class="titleName">供求中心</a>
                    </div>
                </li>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.BASE" resourceType="menu">
                <li url="navigator.do?method=systemSetting" menu-name="SYSTEM_SETTINGS">
                    <div class="topItem">
                        <a class="icon_systemManage"></a>
                        <a class="titleName">系统管理</a>
                    </div>
                </li>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission resourceType="menu" permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE||WEB.TXN.PURCHASE_MANAGE.STORAGE||WEB.TXN.SALE_MANAGE.SALE||WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE||WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE||WEB.TXN.PURCHASE_MANAGE.RETURN||WEB.TXN.SALE_MANAGE.RETURN||WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                <%--<li onclick="openIFrameOfInquiryCenter({pageType:'all'})" class="noBorder">--%>
                <li url="navigator.do?method=inquiryCenter" open-target="_blank" class="noBorder" menu-name="INQUIRY_CENTER">
                   <div class="topItem">
                       <a class="icon_documentsQuery"></a>
                       <a class="titleName">查询中心</a>
                   </div>
                </li>
            </bcgogo:hasPermission>
            <%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CHECK_SMS_BALANCE">--%>
                <li class="noBorder">
                    <div class="topItem">
                        <a class="balance" id="show_sms" style="display:none">余额不足5元</a>
                        <a class="balance" id="show_smsBalance">余额<span id="smsBalance"></span>元</a>
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_RECHARGE">
                            <a class="recharge" id="div_smsBalance" href="smsrecharge.do?method=smsrecharge&rechargeamount=1000" action-type="menu-click" menu-name="WEB.CUSTOMER_MANAGER.SMS_RECHARGE">短信充值</a>
                        </bcgogo:hasPermission>
                    </div>
                </li>
            <%--</bcgogo:hasPermission>--%>
            <li id="scanningGroup">
            </li>
           <%-- <li class="noBorder scanning">
                <span class="hov"><a class="rad"></a>扫描枪</span>
                <span class="line"></span>
                <span class="nor"><a class="rad"></a>打印机</span>
            </li>--%>
        </ul>
    </div>
</div>
<div class="locationBg">
    <div class="top">
        <div id="menu-navigate"></div>
        <%--<bcgogo:permissionParam resourceType="logic"--%>
                                <%--permissions="WEB.VERSION.DISABLE_SEARCH_CUSTOMER,WEB.VERSION.DISABLE_SEARCH_PRODUCT,WEB.VERSION.DISABLE_SEARCH_VEHICLE,WEB.VERSION.DISABLE.SEARCH.SUPPLIER.ONLINE,WEB.VERSION.DISABLE.SEARCH.ACCESSORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.INVENTORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.ONLINE">--%>
        <bcgogo:permissionParam  permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.CUSTOMER_MANAGER.BASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH,WEB.SUPPLIER_MANAGER.SEARCH.APPLY.SUPPLIER,WEB.CUSTOMER_MANAGER.SEARCH.APPLY.CUSTOMER,WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE,WEB.AUTOACCESSORYONLINE.RELATEDCUSTOMERSTOCK.BASE">
            <c:if test="${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE || WEB_CUSTOMER_MANAGER_BASE || WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH || WEB_SUPPLIER_MANAGER_SEARCH_APPLY_SUPPLIER || WEB_CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER || WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE || WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE}">
                <div class="main_search topItem">
                    <div class="search_left topItem"></div>
                    <div class="search_body topItem">
                        <div class="listName topItem" id="_searchNameSelect">
                            <div id="_searchNameBar" class="hoverButton topItem">
                                <span id="_searchName" style="padding-left: 6px;">加载中...</span>
                                <a class="icon_listUp"></a>
                            </div>
                            <div class="listInfo topItem" id="_searchNameList" style="display:none;">
                                <c:if test="${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE}">
                                    <a class="J-selectOption" searchMethod="licenceNo"  menu-name="VEHICLE_CONSTRUCTION">车牌号</a>
                                </c:if>
                                <c:if test="${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH}">
                                    <a class="J-selectOption" searchMethod="accessoryName"  menu-name="TXN_INVENTORY_MANAGE_STOCK_SEARCH">商品库存</a>
                                </c:if>
                                <c:if test="${WEB_CUSTOMER_MANAGER_BASE}">
                                    <a class="J-selectOption" searchMethod="customerOrSupplier">客户/供应商</a>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE}">
                                    <a class="J-selectOption" searchMethod="accessoryOnline" menu-name="AUTO_ACCESSORY_ONLINE_COMMODITYQUOTATIONS">配件报价</a>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE}">
                                    <a class="J-selectOption" searchMethod="customerInventoryOnline" menu-name="AUTO_ACCESSORY_ONLINE_RELATEDCUSTOMERSTOCK">客户库存</a>
                                </c:if>
                                <c:if test="${WEB_CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER}">
                                    <a class="J-selectOption" menu-name="CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER" searchMethod="customerOnline">推荐客户</a>
                                </c:if>
                                <c:if test="${WEB_SUPPLIER_MANAGER_SEARCH_APPLY_SUPPLIER}">
                                    <a class="J-selectOption" searchMethod="supplierOnline" menu-name="APPLY_GET_APPLY_SUPPLIERS">推荐供应商</a>
                                </c:if>
                            </div>
                        </div>
                        <input type="text" kissfocus="on" id="_searchInputText" class="txtKeyword topItem"/>
                        <input type="hidden" id="_searchMethod"/>
                        <a id="_searchInputButton" class="btn_So"></a>
                    </div>
                    <div class="search_right topItem"></div>
                </div>
            </c:if>
        </bcgogo:permissionParam>
    </div>
</div>

<iframe id="iframe_tippage" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="400px" height="300px" frameborder="0" src=""></iframe>

<div id="mask" style="display:block;position: absolute;">
</div>

<div id="div_rechange" class="selRechange" style="display:none;">
    <a>100</a>
    <a>200</a>
    <a>500</a>
    <a>1000</a>
    <a>2000</a>
</div>

<div id="div_brandvehicleheader" class="i_scroll" style="display:none;width:132px;">
    <div class="Container" style="width:132px;">
        <div id="Scroller-1header" style="width:132px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idheader">
            </div>
        </div>
    </div>
</div>
<div id="div_brand_head" class="i_scroll" style="display:none;z-index: 2000">
    <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:100%;padding:0;margin:0;"></div>
</div>
<div id="change-password-form" style="display: none;" title="修改密码">
    <form>
        <fieldset>
            <table class="change-password-table">
                <tr>
                    <td>
                        旧密码:
                    </td>
                    <td>
                        <input type="password" name="oldPassword" id="oldUserPassword"
                               class="text ui-widget-content ui-corner-all change-password-input"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        新密码:
                    </td>
                    <td>
                        <input type="password" name="newPassword" id="newUserPassword"
                               class="text ui-widget-content ui-corner-all change-password-input"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        确认新密码:
                    </td>
                    <td>
                        <input type="password" name="passwordAgain" id="userPasswordAgain"
                               class="text ui-widget-content ui-corner-all change-password-input"/>
                    </td>
                </tr>
            </table>
        </fieldset>
    </form>
</div>

<iframe id="iframe_PopupBox_inquiry_center" style="position:absolute;z-index:5; left:20%; top:10px; display:none;"
        allowtransparency="true" width="1050px" height="800px" frameborder="0" src="" scrolling="no"></iframe>
