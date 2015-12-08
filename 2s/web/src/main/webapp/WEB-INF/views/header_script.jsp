<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="style_thirdparty_extension_core.jsp" %>
<%@include file="style_ui_components.jsp" %>
<link rel="stylesheet" href="<%=basePath%>styles/customerService<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="<%=basePath%>styles/head<%=ConfigController.getBuildVersion()%>.css"/>
<style>
	#ui-datepicker-div, .ui-datepicker {
		font-size: 100%;
	}

		/*闪动颜色*/
	.blink {
		color: #0066CC;
	}

	.blinking {
		color: #FF0000;
	}
</style>

<%@include file="script_thirdparty_extension_core.jsp" %>

<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/basecommon<%=ConfigController.getBuildVersion()%>.js"></script>
<%@include file="script_ui_components.jsp" %>
<jsp:include page="/user/userGuide/userGuideMain.jsp"/>

<script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogoValidate<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-pageVisible<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/pushMessage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclenosolrheader<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/suggestionBase<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/productSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/customerSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/dateUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/jsonUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/dataStruct<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/arrayUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/jumpUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/help<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/menu<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/client<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
    bcClient.clearClientRedirect();
    <bcgogo:permissionParam permissions="WEB.VEHICLE_CONSTRUCTION.BASE,WEB.SUPPLIER_MANAGER.SUPPLIER_DATA,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.CUSTOMER_MANAGER.BASE,WEB.CUSTOMER_MANAGER.CHECK_SMS_BALANCE,WEB.CUSTOMER_MANAGER.CUSTOMER_DATA">
    //施工单 刷卡权限控制
    APP_BCGOGO.Permission.VehicleConstruction.Base = ${WEB_VEHICLE_CONSTRUCTION_BASE};
    var headerScript = {
        supplierData:${WEB_SUPPLIER_MANAGER_SUPPLIER_DATA},
        bRepairOrder:${!WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE},
        bCustomer:${!WEB_CUSTOMER_MANAGER_BASE},
        <%--bCheckSmsBalance:${WEB_CUSTOMER_MANAGER_CHECK_SMS_BALANCE},--%>
        customerData:${WEB_CUSTOMER_MANAGER_CUSTOMER_DATA}
    };
    </bcgogo:permissionParam>
    <bcgogo:permissionParam resourceType="menu" permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH">
    headerScript['bStockSearch'] = ${!WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH};
    </bcgogo:permissionParam>
    <bcgogo:permissionParam permissions="WEB.SCHEDULE.MESSAGE_CENTER.RECEIVER.BASE">
    APP_BCGOGO.Permission.Schedule.MessageCenter.Base = ${WEB_SCHEDULE_MESSAGE_CENTER_RECEIVER_BASE};
    </bcgogo:permissionParam>
    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.STOREHOUSE,WEB.VERSION.IGNORE_VERIFIER_INVENTORY,WEB.VERSION.DISABLE_SEARCH_CUSTOMER,WEB.VERSION.DISABLE_SEARCH_PRODUCT,WEB.VERSION.DISABLE_SEARCH_VEHICLE,WEB.VERSION.DISABLE.SEARCH.SUPPLIER.ONLINE,WEB.VERSION.DISABLE.SEARCH.ACCESSORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.INVENTORY.ONLINE,WEB.VERSION.DISABLE.SEARCH.CUSTOMER.ONLINE,WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
    APP_BCGOGO.Permission.Version.StoreHouse = ${WEB_VERSION_STOREHOUSE};
    APP_BCGOGO.Permission.Version.IgnorVerifierInventory = ${WEB_VERSION_IGNORE_VERIFIER_INVENTORY};
    APP_BCGOGO.Permission.Version.SearchVehicle = ${!WEB_VERSION_DISABLE_SEARCH_VEHICLE};
    APP_BCGOGO.Permission.Version.SearchProduct = ${!WEB_VERSION_DISABLE_SEARCH_PRODUCT};
    APP_BCGOGO.Permission.Version.SearchCustomerSupplier = ${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER};
    APP_BCGOGO.Permission.Version.SearchAccessoryOnline = ${!WEB_VERSION_DISABLE_SEARCH_ACCESSORY_ONLINE};
    APP_BCGOGO.Permission.Version.SearchCustomerInventoryOnline = ${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER_INVENTORY_ONLINE};
    APP_BCGOGO.Permission.Version.SearchCustomerOnline = ${!WEB_VERSION_DISABLE_SEARCH_CUSTOMER_ONLINE};
    APP_BCGOGO.Permission.Version.SearchSupplierOnline = ${!WEB_VERSION_DISABLE_SEARCH_SUPPLIER_ONLINE};
    APP_BCGOGO.Permission.Version.SearchSupplierOnline = ${!WEB_VERSION_DISABLE_SEARCH_SUPPLIER_ONLINE};
    APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier = ${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER};

    </bcgogo:permissionParam>

    <bcgogo:permissionParam  permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.CUSTOMER_MANAGER.BASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH,WEB.SUPPLIER_MANAGER.SEARCH.APPLY.SUPPLIER,WEB.CUSTOMER_MANAGER.SEARCH.APPLY.CUSTOMER,WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE,WEB.AUTOACCESSORYONLINE.RELATEDCUSTOMERSTOCK.BASE">
    APP_BCGOGO.Permission.VehicleConstruction.Construct.Base = ${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE};
    APP_BCGOGO.Permission.CustomerManager.Base = ${WEB_CUSTOMER_MANAGER_BASE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Base = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH};
    APP_BCGOGO.Permission.AutoAccessoryOnline.ApplySupplier = ${WEB_SUPPLIER_MANAGER_SEARCH_APPLY_SUPPLIER};
    APP_BCGOGO.Permission.AutoAccessoryOnline.ApplyCustomer = ${WEB_CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER};
    APP_BCGOGO.Permission.AutoAccessoryOnline.CommodityQuotations = ${WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE};
    APP_BCGOGO.Permission.AutoAccessoryOnline.RelatedCustomerStock = ${WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE};
    </bcgogo:permissionParam>

    <bcgogo:permissionParam  permissions="WEB.AUTOACCESSORYONLINE.PRE_BUY_ORDER_MESSAGE">
    APP_BCGOGO.Permission.AutoAccessoryOnline.PreBuyOrderMessage = ${WEB_AUTOACCESSORYONLINE_PRE_BUY_ORDER_MESSAGE};
    </bcgogo:permissionParam>

    userGuide.currentPageIncludeGuideStep = "SUPPLIER_APPLY_GUIDE_BEGIN,CUSTOMER_APPLY_GUIDE_BEGIN,CONTRACT_CUSTOMER_GUIDE_BEGIN,PRODUCT_ONLINE_GUIDE_BEGIN,PRODUCT_PRICE_GUIDE_BEGIN,CONTRACT_SUPPLIER_GUIDE_BEGIN,CONTRACT_MESSAGE_NOTICE_BEGIN";
//    userGuide.currentPage = "main";
</script>

<script type="text/javascript">
	$(document).ready(function () {
        $("[action-type=show_customer_service_info]").click(function () {
            var offset = $(this).offset();
            var offsetHeight = $(this).height();
            var offsetWidth = $(this).width();
            $(".QQ_chat").css({
                "position": "absolute",
                "z-index": '100',
                "left": offset.left + "px",
                "top": offset.top + offsetHeight - 5 + "px",
                "padding-left": 0 + "px"
            }).fadeIn("slow");
        });
        initQQTalk($(".icon_QQchat"));
        initQQTalk($(".qqHead"));
		var flash;
		//权限控制
		if (headerScript.bRepairOrder) {
			$("#vehicleNumber").attr("disabled", true);
		}
		if (headerScript.bCustomer) {
			$("#input_search_Name").attr("disabled", true);
		}
		if (headerScript.bStockSearch) {
			$("#input_search_pName").attr("disabled", true);
		}
//		if (headerScript.bCheckSmsBalance) {
        if(window.location == window.parent.location){      //no need to call if in iframe. not using GLOBAL.Display.isInIframe() considering performance.
			var url = "sms.do?method=checkSmsBalance";
			var paramJson = {time: new Date()};
			APP_BCGOGO.Net.asyncPost({url: url, dataType: "json", data: paramJson, success: function (data) {
				if (Number(data) < 5) {
					$("#show_sms").show();
					$("#show_smsBalance").hide();
					flash = setInterval(function () {
						if ($("#show_sms").hasClass("blink")) {
							$("#show_sms").addClass("blinking").removeClass("blink");
						} else {
							$("#show_sms").addClass("blink").removeClass("blinking");
						}
					}, 250);
				} else {
					$("#show_smsBalance").show();
                    $("#show_sms").hide();
					$("#smsBalance").html(data);
					clearInterval(flash);
				}
			}});
        }
//		} // end if
        $(".i_top ul li").hover(function() {
            $(this).css("color","#FD5300");
        },function(){
            $(this).css("color","#BEBEBE");
        });
        $(".l_topTitle,.kefu,.zhuce,.sysAnnounce,.help,#changeUserPassword,#j_logout").hover(function(){
            $(this).css({"color":"#fd5300","textDecoration":"underline"});
        },function(){
            $(this).css({"color":"#BEBEBE","textDecoration":"none"});
        });
        var currentColor;
        $(".messageTitle").hover(function(){
            currentColor = $(this).css("color");
            $(this).css({"color":"#fd5300","textDecoration":"underline"});
        },function(){
            $(this).css({"color":currentColor,"textDecoration":"none"});
        });
        $(".bcgogo-messagePopup-panel a").live("mouseover",function(){
              $(this).css({"color":"#fd5300","textDecoration":"underline"});
        });
        $(".bcgogo-messagePopup-panel a").live("mouseout",function(){
            $(this).css({"color": "#888888", "textDecoration": "none"});
        });

	});
    window.onload = function () {
        showNewAnnouncement();
    }
    function showNewAnnouncement() {
        var $toSysAnnouncement = $("#toSysAnnouncement"),
                $newAnnouncement = $(".newAnnouncement"),
                offset = {
                    left: G.getX($toSysAnnouncement[0]),
                    top: G.getY($toSysAnnouncement[0]) + $toSysAnnouncement.height()
                };
        $newAnnouncement.css(offset);
        $newAnnouncement.fadeIn();
    }

    function initQQTalk($qq) {
        var qqInvoker = new App.Module.QQInvokerStatic();
        qqInvoker.init($qq);
    }
</script>