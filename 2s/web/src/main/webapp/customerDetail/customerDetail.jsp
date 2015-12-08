<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 14-1-13Time: 下午4:47
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>客户管理--客户资料</title>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist.css"/>
<link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>

<link rel="stylesheet" type="text/css"
      href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css"
      href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    div[data-node-type="prev"], div[data-node-type="next"] {
        cursor: pointer;
        width: 10px;
        margin-top: 5px;
        height: 27px;
        float: left;
        color: #000000
    }

    div[data-readonly=true] {
        color: #D3D3D3;
    }
</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript">
    <bcgogo:permissionParam resourceType="render" permissions="WEB.TXN.APPOINT_ORDER_MANAGER">
            APP_BCGOGO.Permission.VehicleConstruction.AppointOrder.Manager = ${WEB_TXN_APPOINT_ORDER_MANAGER};
    </bcgogo:permissionParam>
    <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DELETE,WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            APP_BCGOGO.Permission.CustomerManager.CustomerDelete =${WEB_CUSTOMER_MANAGER_CUSTOMER_DELETE};
    APP_BCGOGO.Permission.CustomerManager.CustomerModify =${WEB_CUSTOMER_MANAGER_CUSTOMER_MODIFY};
    userGuide.currentPageIncludeGuideStep = "CUSTOMER_APPLY_GUIDE_SUCCESS";
    </bcgogo:permissionParam>

    <bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
            APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}; // 客户多联系人
    APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS};
    </bcgogo:permissionParam>

    <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION" resourceType="logic">
            APP_BCGOGO.Permission.Version.VehicleConstruction = true;
    </bcgogo:hasPermission>
</script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerDetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehiclelicenceNo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<!--add by zhuj-->
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript"
        src="js/page/txn/appointOrderList<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript"
        src="js/customerDetail/customerDetailInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/statementAccount/customerBill<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript"
        src="js/page/search/inquirySystemOrder<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>
<script type="text/javascript"
        src="js/page/customer/vehicle/customerVehicleBasicFunction<%=ConfigController.getBuildVersion()%>.js"></script>


<%@ include file="/WEB-INF/views/image_script.jsp" %>
<script type="text/javascript"
        src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>
<style type="text/css">
    .customerSMS {
        background: url("images/phone.jpg") no-repeat scroll right center transparent;
        height: 16px;
        margin: 3px 2px 0 5px;
        width: 22px;
        float: right;
    }

    .lineTitle span {
        float: left;
    }

    .lineTitle span a {
        margin: 0 5px;
    }

    .lineTitle {
        width: 988px;
        height: 32px;
        float: left;
        color: #272727;
        font-size: 14px;
        font-weight: bold;
        line-height: 32px;
        padding-left: 10px;
        border: #dddddd 1px solid;
    }

    .customerSMS:hover {
        background: url("images/hover_phone.png") no-repeat scroll right center transparent;
        height: 21px;
        margin: 1px 0 0 5px;
        width: 22px;
        float: right;
    }

</style>
<script type="text/javascript">
APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_DATA");
$(document).ready(function () {
    var identificationImageUploader, identificationImageUploaderView;
    initImageUploader();

    <c:if test="${not empty vehicleId}">
            window.location.hash = "#customerDetailVehicle";
    </c:if>
    <c:if test="${not empty vehicleId && vehicleEdit}">
            $("#customerVehicleContainer").find(".J_customerVehicleDiv").each(function () {
                if ($(this).is(":visible")) {
                    $(this).find(".J_editCustomerVehicle").click();
                    return;
                }
            });
    </c:if>

    $(".j_printCustomerIdentificationImage").live("click", function (e) {
        e.preventDefault();
        var imagePath = $("#customerIdentificationImagePaths" + $(this).attr("data-index")).val();
        if (!G.Lang.isEmpty(imagePath)) {
            window.open("customer.do?method=printCustomerIdentificationImage&imagePath=" + imagePath);
        }
    });


//开始时间不能早于今天
    $("#fromTimeStr").bind("change", function () {
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#fromTimeStr").val();
        var endDateStr = $("#toTimeStr").val();
        var startDateLong;
        var endDateLong;
        if (startDateStr.length > 0) {
            var year = startDateStr.substr(0, 4);
            var month = startDateStr.substr(5, 2);
            var day = startDateStr.substr(8, 2);
            var startDate = new Date(year + "/" + month + "/" + day);
            startDateLong = startDate.getTime();
            var nowDateLong = new Date().getTime();
            if (startDateLong - nowDateLong > 0) {
                nsDialog.jAlert("开始时间不能晚于当前时间！", null, function () {
                    $("#fromTimeStr").val("");
                    return;
                });
            }
            if (endDateStr.length > 0) {
                var year = endDateStr.substr(0, 4);
                var month = endDateStr.substr(5, 2);
                var day = endDateStr.substr(8, 2);
                var endDate = new Date(year + "/" + month + "/" + day);
                endDateLong = endDate.getTime();
                if (startDateLong - endDateLong > 0) {
                    nsDialog.jAlert("开始时间不能晚于结束时间！", null, function () {
                        $("#fromTimeStr").val("");
                        return;
                    });
                }
            }
        }
    });
    //结束时间不能晚于开始时间
    $("#toTimeStr").bind("change", function () {
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#fromTimeStr").val();
        var endDateStr = $("#toTimeStr").val();
        var startDateLong;
        var endDateLong;
        if (startDateStr.length > 0 && endDateStr.length > 0) {
            var year = startDateStr.substr(0, 4);
            var month = startDateStr.substr(5, 2);
            var day = startDateStr.substr(8, 2);
            var startDate = new Date(year + "/" + month + "/" + day);
            startDateLong = startDate.getTime();

            year = endDateStr.substr(0, 4);
            month = endDateStr.substr(5, 2);
            day = endDateStr.substr(8, 2);
            var endDate = new Date(year + "/" + month + "/" + day);
            endDateLong = endDate.getTime();

            if (startDateLong - endDateLong > 0) {
                nsDialog.jAlert("结束时间不能早于开始时间！", null, function () {
                    $("#toTimeStr").val("");
                    return;
                });
            }
        }
    });
    $("#fromTimeStr,#toTimeStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix": "",
        "showButtonPanel": true
    });
    $("#memberSearchBtn").click(function () {
        searchMemberConsumptionHistory();
    });

    $(".image-item").live("click", function () {
        if ($(this).hasClass("button-delete")) {
            return;
        }
        var _this = $(this);//将当前的pimg元素作为_this传入函数
        imgShow("#outerdiv", "#innerdiv", "#bigimg", _this);
    });

});


function saveCustomerImage(operate, identificationImagePathData) {
    $('#identificationImageForm').ajaxSubmit(function (result) {
        if (result.success) {
            var customerIdentificationImageDetailDTOs = result.data;
            $("input[id^='customerIdentificationImagePaths']").each(function (index) {
                if (!G.Lang.isEmpty(customerIdentificationImageDetailDTOs[index])) {
                    $(this).val(customerIdentificationImageDetailDTOs[index].imagePath);
                    $(this).attr("reset-value", customerIdentificationImageDetailDTOs[index].imagePath);

                } else {
                    $(this).val("");
                    $(this).attr("reset-value", "");
                }
            });

            if (operate == 'save') {
                showMessage.fadeMessage("40%", "20%", 2000, 2000, "保存图片成功！");
            } else if (operate == 'delete') {
                showMessage.fadeMessage("40%", "20%", 2000, 2000, "删除图片成功！");
            }

        } else {
            if (operate == 'save') {
                showMessage.fadeMessage("40%", "20%", 2000, 2000, "保存图片失败！");
            } else if (operate == 'delete') {
                showMessage.fadeMessage("40%", "20%", 2000, 2000, "删除图片失败！");
            }
        }
    });

    var index = 0;
    $("#identificationImageUploaderView").find("li").each(function () {
        if (G.Lang.isNotEmpty(identificationImagePathData[index])) {
            $(this).append('<div class="padding10 photo-upload" style="margin-top:10px;">' +
                    '<input type="button" class="btn j_printCustomerIdentificationImage" data-index="' + index + '"value="打 印"/>' +
                    '</div>');
            index++;
        }
    });
}

function initImageUploader() {
    var identificationImagePathData = [];
    $("input[id^='customerIdentificationImagePaths']").each(function (index) {
        if (!G.Lang.isEmpty($(this).val())) {
            identificationImagePathData.push($(this).val());
        }
    });
    identificationImageUploader = new App.Module.ImageUploader();
    identificationImageUploader.init({
        "selector": "#identificationImageUploader",
        "flashvars": {
            "debug": "off",
            "maxFileNum": 6,
            "currentItemNum": identificationImagePathData.length,
            "width": 61,
            "height": 24,
            "buttonBgUrl": "images/imageUploader.png",
            "buttonOverBgUrl": "images/imageUploader.png",
            "fileType": {
                "description": "Image Files",
                "extension": "*.jpeg;*.png;*.jpg;"
            },
            "url": APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL + "/" + APP_BCGOGO.UpYun.UP_YUN_BUCKET + "/",
            "ext": {
                "policy": $("#policy").val(),
                "signature": $("#signature").val()
            }
        },

        "startUploadCallback": function (message) {
            // 设置 视图组件 uploading 状态
            identificationImageUploaderView.setState("uploading");
        },
        "uploadCompleteCallback": function (message, data) {
            var dataInfoJson = JSON.parse(data.info);
            identificationImagePathData.push(dataInfoJson.url);
        },
        "uploadErrorCallback": function (message, data) {
            var errorData = JSON.parse(data.info);
            errorData["content"] = data.info;
            saveUpLoadImageErrorInfo(errorData);

            if (errorData.code == 403) {
                showMessage.fadeMessage("40%", "20%", 2000, 2000, "格式不正确,上传图片失败！");
            } else {
                showMessage.fadeMessage("40%", "20%", 2000, 2000, "上传图片失败！");
            }
        },
        "uploadAllCompleteCallback": function () {
            //更新input
            $("input[id^='customerIdentificationImagePaths']").each(function (index) {
                $(this).val(G.Lang.normalize(identificationImagePathData[index], ""));
            });
            // 设置 视图组件  idle 状态
            identificationImageUploaderView.setState("idle");
            identificationImageUploaderView.update(getTotelUrlToData(identificationImagePathData));
            saveCustomerImage('save', identificationImagePathData);
        }
    });

    /**
     * 视图组建的 样例代码
     * */
    identificationImageUploaderView = new App.Module.ImageUploaderView();
    identificationImageUploaderView.init({
        // 你所需要注入的 dom 节点
        selector: "#identificationImageUploaderView",
        width: 990,
        height: 160,
        iWidth: 145,
        iHeight: 100,
        maxFileNum: 6,
        borderVisible: false,
        paddingLeft: 0,
        paddingRight: 0,
        // 当删除某张图片时会触发此回调
        onDelete: function (event, data, index) {
            identificationImageUploader.getFlashObject().deleteFile(index);

            // 从已获得的图片数据池中删除 图片数据
            identificationImagePathData.splice(index, 1);
            //更新input
            $("input[id^='customerIdentificationImagePaths']").each(function (index) {
                $(this).val(G.Lang.normalize(identificationImagePathData[index], ""));
            });

            saveCustomerImage('delete', identificationImagePathData);

        }
    });

    // 设置 视图组件  idle 状态
    identificationImageUploaderView.setState("idle");
    identificationImageUploaderView.update(getTotelUrlToData(identificationImagePathData));


    $("#identificationImageUploaderView .imageuploader-view").css("width", "985px");
    $("#identificationImageUploaderView .frame-image").css("height", "150px");

    var index = 0;
    $("#identificationImageUploaderView").find("li").each(function () {
        if (G.Lang.isNotEmpty(identificationImagePathData[index])) {
            $(this).append('<div class="padding10 photo-upload" style="margin-top:10px;">' +
                    '<input type="button" class="btn j_printCustomerIdentificationImage" data-index="' + index + '"value="打 印"/>' +
                    '</div>');
            index++;
        }
    });
}


function imgShow(outerdiv, innerdiv, bigimg, _this) {
    var bigSrc = _this.find("img").attr("src").split("!")[0];//获取当前点击的pimg元素中的src属性
    if (G.Lang.isEmpty(bigSrc) || bigSrc.indexOf("bg_none_uploaded_pic.png") != -1) {
        return;
    }

    $(bigimg).attr("src", bigSrc);//设置#bigimg元素的src属性

    /*获取当前点击图片的真实大小，并显示弹出层及大图*/
    $("<img/>").attr("src", bigSrc).load(function () {
        var windowW = $(window).width();//获取当前窗口宽度
        var windowH = $(window).height();//获取当前窗口高度
        var realWidth = this.width;//获取图片真实宽度
        var realHeight = this.height;//获取图片真实高度
        var imgWidth, imgHeight;
        var scale = 0.8;//缩放尺寸，当图片真实宽度和高度大于窗口宽度和高度时进行缩放

        if (realHeight > windowH * scale) {//判断图片高度
            imgHeight = windowH * scale;//如大于窗口高度，图片高度进行缩放
            imgWidth = imgHeight / realHeight * realWidth;//等比例缩放宽度
            if (imgWidth > windowW * scale) {//如宽度扔大于窗口宽度
                imgWidth = windowW * scale;//再对宽度进行缩放
            }
        } else if (realWidth > windowW * scale) {//如图片高度合适，判断图片宽度
            imgWidth = windowW * scale;//如大于窗口宽度，图片宽度进行缩放
            imgHeight = imgWidth / realWidth * realHeight;//等比例缩放高度
        } else {//如果图片真实高度和宽度都符合要求，高宽不变
            imgWidth = realWidth;
            imgHeight = realHeight;
        }
        $(bigimg).css("width", imgWidth);//以最终的宽度对图片缩放

        var w = (windowW - imgWidth) / 2;//计算图片与窗口左边距
        var h = (windowH - imgHeight) / 2;//计算图片与窗口上边距
        $(innerdiv).css({"top": h, "left": w});//设置#innerdiv的top和left属性
        $(outerdiv).fadeIn("fast");//淡入显示#outerdiv及.pimg
    });

    $(outerdiv).click(function () {//再次点击淡出消失弹出层
        $(this).fadeOut("fast");
    });
}


function getTotelUrlToData(inUrlData) {
    var outData = [];
    for (var i = 0; i < inUrlData.length; i++) {
        var outDataItem = {
            "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + inUrlData[i] + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD
        };
        outData.push(outDataItem);
    }
    return outData;
}
;

//查询会员卡消费记录
function searchMemberConsumptionHistory() {
    var customerId = $("#customerId").val();
    var fromTimeStr = $("#fromTimeStr").val();
    var toTimeStr = $("#toTimeStr").val();
    var customerName = $.trim($('#unitSpan').html());
    var data = {
        startPageNo: 1,
        maxRows: 5,
        customerId: customerId,
        customerName: customerName,
        startTimeStr: fromTimeStr,
        endTimeStr: toTimeStr
    };
    APP_BCGOGO.Net.asyncPost({
        url: "member.do?method=getSingleMemberConsume",
        data: data,
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            initMemberConsumeHistoryByJson(jsonStr);
            initPages(jsonStr, "dynamical7", "member.do?method=getSingleMemberConsume", '', "initMemberConsumeHistoryByJson", '', '', data, '');
        }
    });
}

function initMemberConsumeHistoryByJson(data) {
    $("#memberHistoryTable tr:not(:first)").remove();
    $("#member_record_count").text(data[1].totalRows);
    var totalAmount = 0;
    $.each(data[0], function (index, order) {
        var orderId = order.orderIdStr;
        var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
        var orderTypeValue = (!order.orderTypeValue ? "--" : order.orderTypeValue);
        var orderType = (!order.orderType ? "--" : order.orderType);
        var url = "";
        totalAmount += (order.amount == null ? 0 : order.amount);
        if (orderType == "REPAIR") {
            url = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + orderId;
        } else if (orderType == "WASH_BEAUTY") {
            url = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + orderId;
        } else if (orderType == "SALE") {
            url = "sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" + orderId;
        }
        var vestDateStr = (!order.vestDateStr ? "" : order.vestDateStr);
        var vehicle = (!order.vehicle ? "--" : order.vehicle);
        var memberNo = (!order.memberNo ? "--" : order.memberNo);
        var memberType = (!order.memberType ? "--" : order.memberType);
        var orderContentShort = order.orderContentShort;
        var orderContent = order.orderContent;
        var consumeType = (!order.consumeType ? "--" : order.consumeType);
        var tr = '<tr class="titBody_Bg">';
        tr += '<td style="border-left:none;padding-left:10px;">' + (index + 1) + '</td>';
        tr += '<td>' + vestDateStr + '</td>';
        tr += '<td><a target="_blank" class="blue_color" href=' + url + '>' + receiptNo + '</a></td>';
        tr += '<td>' + orderTypeValue + '</td>';
        tr += '<td>' + vehicle + '</td>';
        tr += '<td>' + memberNo + '</td>';
        tr += '<td>' + memberType + '</td>';
        tr += '<td title="' + orderContent + '">' + orderContentShort + '</td>';
        tr += '<td title="' + consumeType + '">' + consumeType + '</td>';
        tr += '</tr>';
        $("#memberHistoryTable").append($(tr));
        $("#memberHistoryTable").append($('<tr class="titBottom_Bg"><td colspan="9"></td></tr>'));
    });
    $("#memberConsumeAll").text(totalAmount);
}

</script>
</head>
<body class="bodyMain">
<input type="hidden" value="uncleUser" id="pageName">
<input type="hidden" value="clientInfo" id="orderType">
<input type="hidden" id="secondCategoryName" value="">
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">

<div class="mainTitles">
    <div class="titleWords">客户资料</div>
    <c:if test="${fromPage =='customerData'}">
        <div class="title-r" style="padding-top:48px;"><a href="customer.do?method=customerdata">返回列表></a></div>
    </c:if>
</div>


<div class="customer_nav">
    <ul>
        <li><a id="customerInfoTitle" href="#" class="arrer">详细信息</a></li>

        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST" resourceType="menu">
            <li><a id="customerAppointTitle" href="#">预约服务</a></li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
            <li><a id="customerStatementTitle" href="#">客户对账单</a></li>
        </bcgogo:hasPermission>
        <li><a id="customerPhotoTitle" href="#">证件照片</a></li>
    </ul>

    <div class="setting-relative">
        <div class="setting-absolute J_customerOptDetail" style="display: none">
            <ul>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <c:if test="${isMemberSwitchOn}">
                        <bcgogo:hasPermission
                                permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER.BUY_CARD">
                            <li><a class="default_a" style="cursor: pointer" onclick="selectCard()">购卡续卡</a></li>
                            <c:if test="${memberStatus=='ENABLED' || memberStatus =='PARTENABLED'}">
                                <li><a class="default_a" style="cursor: pointer" id="returnCardBtn"
                                       onclick="returnCard();">退卡</a></li>
                            </c:if>
                        </bcgogo:hasPermission>
                    </c:if>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="carWashBeauty()">洗车美容</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="redirectRepairOrder()">车辆施工</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="redirectSalesOrder()">购买商品</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN" resourceType="menu">
                    <li><a class="default_a" style="cursor: pointer" onclick="redirectSalesReturn()">销售退货</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill"
                                      resourceType="menu">
                    <li><a class="blue_color" id="duizhan" style="cursor: pointer">财务对账</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DELETE">
                    <li><a class="default_a" id="deleteCustomerButton" onclick="deleteCustomer()"
                           style="cursor: pointer">删除客户</a>
                    </li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                    <li><a class="default_a" id="updateToShopBtn" onclick="updateToShop()" style="cursor: pointer">升级客户</a>
                    </li>
                </bcgogo:hasPermission>
            </ul>
        </div>
    </div>

    <div class="setting J_customerOpt">操 作</div>
</div>

<input type="hidden" id="isUncleUser"/>
<input id="customerId" type="hidden" value="${customerId}"/>
<input id="customerName" type="hidden" value="${customerDTO.name}"/>
<input id="id" name="id" type="hidden" value="${customerDTO.id}"/>
<input type="hidden" value="${today}" id="today">

<input type="hidden" value="" id="modifyAll">

<div class="booking-management">


<div class="titBody" id="customerDetailInfo">
<div class="lineTitle"><span>基本信息</span>

    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
        <div class="editButton" id="editCustomerInfo">编 辑</div>
    </bcgogo:hasPermission>
    <span
            class="font12-normal"><a
            style="cursor: pointer"
            onclick="customerConsume('totalConsume');">累计消费: ${customerDTO.consumeTimes}次&nbsp;${customerDTO.totalAmount}元</a>
      <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
          | <a style="cursor: pointer"
               onclick="customerConsume('salesReturn');">累计销售退货: ${customerDTO.totalReturnAmount}元</a>
      </bcgogo:hasPermission>
      <bcgogo:permission>
          <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
              | <a style="cursor: pointer"
                   onclick="customerConsume('totalReceivable');">应收: ${customerDTO.totalReceivable}元</a>
              <a style="cursor: pointer"
                 onclick="customerConsume('totalReturn');">应付: ${customerDTO.totalReturnDebt}元</a>
          </bcgogo:if>
          <bcgogo:else>
              | <a style="cursor: pointer"
                   onclick="customerConsume('totalReturn');">应付: ${customerDTO.totalReturnDebt}元</a>
              <th>&nbsp;</th>
              <td>&nbsp;</td>
          </bcgogo:else>
      </bcgogo:permission></span>
    <bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
        <div class="editButton" id="duizhang">对 账</div>
    </bcgogo:hasPermission>
</div>
<div class="clear"></div>


<div class="customer" id="customerBasicInfoShow">
    <table width="100%" border="0" class="order-table" id="customerDetailTable">
        <colgroup>
            <col width="200"/>
            <col width="200"/>
            <col width="200"/>
            <col width="200"/>
        </colgroup>
        <tr class="J_showCustomerOtherInfo">
            <td>客户名称：<span class="J_customerBasicSpan"
                           data-key="name">${(customerDTO.name ==null || customerDTO.name =='')?'--':customerDTO.name}</span>
            </td>
            <td>联系人：<span class="J_customerBasicSpan"
                          data-key="contact">${(customerDTO.contact ==null || customerDTO.contact =='')?'--':customerDTO.contact}</span>
            </td>
            <td><span class="fl">手机：</span>
                            <span class="J_customerBasicSpan fl" data-key="mobile"
                                  id="customerMobileSpan">${(customerDTO.mobile ==null || customerDTO.mobile =='')?'--':customerDTO.mobile}</span>


                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
                        <c:if test="${customerDTO.mobile!=null &&customerDTO.mobile!=''}">
                            <a href="#" style="float:left;" class="customerSMS"
                               onclick='smsHistory($("#customerMobileSpan").text(),"${customerId}")'></a>
                        </c:if>
                        <c:if test="${customerDTO.mobile==null ||customerDTO.mobile==''}">
                            <a href="#" style="float:left;display: none;" class="customerSMS"
                               onclick='smsHistory($("#customerMobileSpan").text(),"${customerId}")'></a>
                        </c:if>
                    </bcgogo:if>
                </bcgogo:permission>

            </td>
            <td>座机：<span class="J_customerBasicSpan"
                         data-key="landLine">${(customerDTO.landLine ==null || customerDTO.landLine =='')?'--':customerDTO.landLine}</span>
            </td>
        </tr>
        <tr class="titBottom_Bg">
            <td colspan="4">
                <div class="div_Btn"><a id="showDetailInfo" onclick="showDetailInfo();"
                                        class="btnDown"></a></div>
            </td>
        </tr>
    </table>
</div>
<div class="clear height"></div>


<div class="customer" id="customerBasicInfoEdit" style="display: none;">
    <form id="customerBasicForm" method="post">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="80"/>
                <col width="200"/>
                <col width="80"/>
                <col width="200"/>
                <col width="80"/>
                <col width="200"/>
                <col width="80"/>
                <col width="200"/>
            </colgroup>

            <tr>
                <th class="test1"><span class="red_color">*</span>客户名称</th>
                <td>：<input type="text" maxlength="50" id="name" name="name"
                            reset-value="${customerDTO.name}"
                            value="${customerDTO.name}" class="txt J_formreset"></td>
                <th class="test1">联系人</th>
                <td>：<input type="text" maxlength="50" id="contacts[0].name" name="contacts[0].name"
                            reset-value="${customerDTO.contact}" value="${customerDTO.contact}"
                            class="txt J_formreset"/>
                    <input type="hidden" id="contacts[0].mainContact" name="contacts[0].mainContact"
                           value="1"/>
                    <input type="hidden" class="J_formreset" id="contacts[0].id" name="contacts[0].id"
                           reset-value="${customerDTO.contactId}" value="${customerDTO.contactId}"/>
                </td>
                <th class="test1">手 机</th>
                <td>：<input type="text" maxlength="11" id="contacts[0].mobile" name="contacts[0].mobile"
                            reset-value="${customerDTO.mobile}" value="${customerDTO.mobile}"
                            class="txt J_formreset"/></td>
                <th class="test1">座 机</th>
                <td>：<input type="text" maxlength="20" id="landLine" name="landLine"
                            reset-value="${customerDTO.landLine}"
                            value="${customerDTO.landLine}" class="txt J_formreset" style="width:88%;"/>
                </td>
            </tr>
            <tr>
                <th class="test1">Email</th>
                <td>：<input type="text" maxlength="50" id="contacts[0].email" name="contacts[0].email"
                            reset-value="${customerDTO.email}" value="${customerDTO.email}"
                            class="txt J_formreset"/></td>
                <th class="test1">传 真</th>
                <td>：<input type="text" maxlength="20" id="fax" name="fax" reset-value="${customerDTO.fax}"
                            value="${customerDTO.fax}" class="txt J_formreset"/></td>
                <th class="test1">地 址</th>
                <td colspan="3">：<input type="text" maxlength="50" id="address" name="address"
                                        style="width:94.8%;"
                                        reset-value="${customerDTO.address}" value="${customerDTO.address}"
                                        class="txt J_formreset"/></td>
            </tr>
            <tr>
                <th class="test1">QQ</th>
                <td>：<input type="text" maxlength="10" id="contacts[0].qq" name="contacts[0].qq"
                            reset-value="${customerDTO.qq}" value="${customerDTO.qq}"
                            class="txt J_formreset"/></td>
                <th class="test1">生 日</th>
                <td>：<input type="text" readonly="true" id="birthdayString" name="birthdayString"
                            reset-value="${customerDTO.birthdayString}"
                            value="${customerDTO.birthdayString}"
                            class="txt J_formreset"/></td>
                <th class="test1">简 称</th>
                <td>：<input type="text" maxlength="50" id="shortName" name="shortName"
                            reset-value="${customerDTO.shortName}"
                            value="${customerDTO.shortName}" class="txt J_formreset"/></td>
                <th class="test1">客户类别</th>
                <td>：<select name="customerKind" id="customerKind"
                             reset-value="${customerDTO.customerKind}" style="width:120px;"
                             class="txt J_formreset">
                    <c:forEach items="${customerTypeMap}" var="customerType" varStatus="status">
                        <option
                                value="${customerType.key}" ${customerType.key eq customerDTO.customerKind?'selected':''}>${customerType.value}</option>
                    </c:forEach>
                </select>
                </td>
            </tr>
            <tr>
                <th class="test1">开户行</th>
                <td>：<input type="text" maxlength="20" id="bank" name="bank"
                            reset-value="${customerDTO.bank}"
                            value="${customerDTO.bank}" class="txt J_formreset"/></td>
                <th class="test1">开户名</th>
                <td>：<input type="text" maxlength="20" id="bankAccountName" name="bankAccountName"
                            reset-value="${customerDTO.bankAccountName}"
                            value="${customerDTO.bankAccountName}"
                            class="txt J_formreset"/></td>
                <th class="test1">账 号</th>
                <td>：<input type="text" maxlength="20" id="account" name="account"
                            reset-value="${customerDTO.account}"
                            value="${customerDTO.account}" class="txt J_formreset"/></td>
                <th class="test1">结算类型</th>
                <td>：<select name="settlementType" id="settlementType"
                             reset-value="${customerDTO.settlementType}" style="width:120px;"
                             class="txt J_formreset">
                    <c:forEach items="${settlementTypeMap}" var="settlementType" varStatus="status">
                        <option
                                value="${settlementType.key}" ${settlementType.key eq customerDTO.settlementType?'selected':''}>${settlementType.value}</option>
                    </c:forEach>
                </select>
                </td>
            </tr>
            <tr>
                <th class="test1" valign="top">发票类型</th>
                <td valign="top">：<select name="invoiceCategory" id="invoiceCategory"
                                          reset-value="${customerDTO.invoiceCategory}"
                                          class="txt J_formreset">
                    <c:forEach items="${invoiceCatagoryMap}" var="invoiceCategory" varStatus="status">
                        <option
                                value="${invoiceCategory.key}" ${invoiceCategory.key eq customerDTO.invoiceCategory?'selected':''}>${invoiceCategory.value}</option>
                    </c:forEach>
                </select>
                </td>
                <th class="test1" valign="top">备 注</th>
                <td valign="top" colspan="5">
                    <span class="fl">：</span>
                    <input type="text" maxlength="400" style="width:94%;" id="memo" name="memo"
                           reset-value="${customerDTO.memo}" value="${customerDTO.memo}"
                           class="txt J_formreset"/>
                </td>
            </tr>

        </table>
    </form>
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
        <div class="padding10">
            <input type="button" class="query-btn" id="saveCustomerBasicBtn" value="保存"/>
            <input type="button" class="query-btn" id="cancelCustomerBasicBtn" value="取消"/>
        </div>

    </bcgogo:hasPermission>


    <div class="clear"></div>
</div>
</div>


<c:if test="${isMemberSwitchOn && not empty memberDTO}">
    <div class="titBody" id="customerDetailMember">
        <div class="lineTitle"><span>会员信息</span>
            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER.BUY_CARD">
                <div class="editButton" onclick="selectCard()">购卡续卡</div>
            </bcgogo:hasPermission>
      <span class="font12-normal"><a
              style="cursor: pointer"
              onclick="customerConsume('memberConsume')">累计消费: ${customerDTO.memberConsumeTimes}次 ${customerDTO.memberConsumeTotal}元 </a> </span>
        </div>
        <div class="clear"></div>
        <div class="customer">
            <div class="member-left">
                <input type="hidden" id="memberId" value="${memberDTO.id}"/>
                <table width="100%" border="0" class="order-table">
                    <colgroup>
                        <col width="200"/>
                        <col width="200"/>
                    </colgroup>
                    <tr>
                        <td>储蓄卡余额：${memberDTO.balance}元</td>
                        <td>状态:${memberStatus.status}</td>
                    </tr>
                    <tr>
                        <td>入会日期：${memberDTO.joinDateStr}</td>
                        <td>失效日期：${memberDTO.serviceDeadLineStr}</td>
                    </tr>
                    <tr>
                        <td>会员级别：${memberDTO.type}</td>
                        <td>会员会龄：${memberDTO.dateKeep}</td>
                    </tr>
                    <tr>
                        <td><span style="float:left;">会员卡号：<span
                                id="customerMemberNoSpan">${memberDTO.memberNo}</span></span>

                            <div class="editButton" id="changePassword" style="margin-top:0">修改密码</div>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                </table>

                <div id="chPasswordShow" class="i_scroll_percentage" style="width:284px;" title="修改密码">
                    <table cellpadding="0" cellspacing="0" class="table2">
                        <col width="85"/>
                        <col/>
                        <tr>
                            <td style="border:0;">原密码：</td>
                            <td style="border:0;padding:2px;"><input type="password" id="oldPw" value=""
                                                                     style="border:1px solid #416885;padding:2px;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="border:0;">新密码：</td>
                            <td style="border:0;padding:2px;"><input type="password" id="newPw" value=""
                                                                     style="border:1px solid #416885;padding:2px;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="border:0;">确认新密码：</td>
                            <td style="border:0;padding:2px;"><input type="password" id="cfNewPw" value=""
                                                                     style="border:1px solid #416885;padding:2px;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="border:0;"></td>
                            <td style="border:0;padding:2px;">
                                <label><input type="checkbox" name="sendSms" value="true"
                                              id="sendSms"/>发送短信</label>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="member-right">
                <table width="100%" border="0" class="member-table">
                    <tr>
                        <th align="left">项目名称</th>
                        <th align="left">剩余次数</th>
                        <th align="left">可用车牌</th>
                        <th align="left">失效日期</th>
                    </tr>
                    <c:if test="${not empty memberDTO.memberServiceDTOs}">
                        <c:forEach items="${memberDTO.memberServiceDTOs}" var="memberServiceDTO">
                            <tr>
                                <td>${memberServiceDTO.serviceName}</td>
                                <td>${memberServiceDTO.timesStr}</td>
                                <td>${(memberServiceDTO.vehicles ==null || memberServiceDTO.vehicles =='')?'--':memberServiceDTO.vehicles}</td>
                                <td>${(memberServiceDTO.deadlineStr ==null || memberServiceDTO.deadlineStr =='')?'--':memberServiceDTO.deadlineStr}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                </table>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear i_height"></div>
    </div>
</c:if>
<c:set var="currentVehicleIndex" value="0"/>
<c:if test="${not empty customerVehicleResponseList && not empty vehicleId}">
    <c:forEach items="${customerVehicleResponseList}" var="customerVehicle" varStatus="status">
        <c:if test="${vehicleId eq customerVehicle.vehicleId}">
            <c:set var="currentVehicleIndex" value="${status.index}"/>
        </c:if>
    </c:forEach>
</c:if>
<div class="titBody" id="customerDetailVehicle">
    <div class="lineTitle" id="customerVehicleTitle"><span>车辆信息&#160;</span>
    <div style="width:
                80px;overflow: hidden;height: 33px;position: relative;float: left;">
    <div style="position: absolute;width:${(fn:length(customerVehicleResponseList))*80}px;height: 33px;"
         data-node-type="slider">
        <c:if test="${not empty customerVehicleResponseList}">
            <c:forEach items="${customerVehicleResponseList}" var="customerVehicle" varStatus="status">
                <a id="customerVehicleId${customerVehicle.vehicleId}" data-index="${status.index}"
                   class="${status.index == currentVehicleIndex ? 'normal_btn2' : 'hover_btn2'} J_customerVehicleTitle"
                   style="margin: 5px 0px 0px 0px;">${customerVehicle.licenceNo}</a>
            </c:forEach>
        </c:if>
    </div>
</div>
<div style="<c:if test="${fn:length(customerVehicleResponseList) <= 10}">display:none;</c:if>"
data-node-type="next">
</div>

<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
    <a id="addCustomerVehicleBtn"
       class="${not empty customerVehicleResponseList?'hover_btn2':'normal_btn2'}"
       href="javascript:void(0)" style="margin:5px 0 0 0px;display: inline-block;float: left;">新增车辆</a>
</bcgogo:hasPermission>

</div>
<div class="clear"></div>
<div class="customer" id="customerVehicleContainer">
<c:if test="${not empty customerVehicleResponseList}">
<c:forEach items="${customerVehicleResponseList}" var="customerVehicle" varStatus="status">
<div id="customerVehicleDiv${status.index}"
     style="${status.index==currentVehicleIndex?'':'display:none;'}"
     class="J_customerVehicleDiv" data-index="${status.index}">
<div class="padding5" id="customerVehicleInfoShow${status.index}">
<div class="member-left" id="customerVehicleBasicInfoShow${status.index}">
    <table width="100%" border="0" class="order-table">

        <colgroup>
            <col width="200"/>
            <col width="220"/>
        </colgroup>
        <tr>
            <td>联系人：<span class="J_customerVehicleSpan"
                          data-key="contact">${(customerVehicle.contact ==null || customerVehicle.contact =='')?'--':customerVehicle.contact}</span>
            </td>
            <td>联系方式：<span class="J_customerVehicleSpan"
                           data-key="mobile">${(customerVehicle.mobile ==null || customerVehicle.mobile =='')?'--':customerVehicle.mobile}</span>
            </td>
        </tr>
        <tr>


            <c:choose>
                <c:when
                        test="${(customerVehicle.brand ==null || customerVehicle.brand =='') &&(customerVehicle.model ==null || customerVehicle.model =='') }">
                    <td>品牌车型：<span class="J_customerVehicleSpan"
                                   data-key="brand"></span><span
                            class="J_customerVehicleSpan"
                            data-key="model">--
                      </span></td>
                </c:when>
                <c:otherwise>
                    <td>品牌车型：<span class="J_customerVehicleSpan" data-key="brand">
                        ${(customerVehicle.brand ==null || customerVehicle.brand =='')?'--':customerVehicle.brand}</span>/<span
                            class="J_customerVehicleSpan"
                            data-key="model">${(customerVehicle.model ==null || customerVehicle.model =='')? '--':customerVehicle.model}
                    </span></td>
                </c:otherwise>
            </c:choose>


            <c:choose>
                <c:when
                        test="${(customerVehicle.year ==null || customerVehicle.year =='') &&(customerVehicle.engine ==null || customerVehicle.engine =='')
                 &&(customerVehicle.color ==null || customerVehicle.color =='') }">
                    <td>年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">
                              </span><span
                            class="J_customerVehicleSpan"
                            data-key="engine"></span><span
                            class="J_customerVehicleSpan"
                            data-key="color">--
                     </span></td>
                </c:when>
                <c:otherwise>
                    <td>年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">
                        ${(customerVehicle.year ==null || customerVehicle.year =='')?'--':(customerVehicle.year)}</span>/<span
                            class="J_customerVehicleSpan"
                            data-key="engine">${(customerVehicle.engine ==null || customerVehicle.engine =='')?'--':customerVehicle.engine}</span>/<span
                            class="J_customerVehicleSpan"
                            data-key="color">${(customerVehicle.color ==null || customerVehicle.color =='')?'--':customerVehicle.color}
                    </span></td>
                </c:otherwise>
            </c:choose>

        </tr>
        <tr>
            <td>发动机号：<span class="J_customerVehicleSpan"
                           data-key="engineNo">${(customerVehicle.engineNo ==null || customerVehicle.engineNo =='')?'--':(customerVehicle.engineNo)}</span>
            </td>
            <td>车架号：<span class="J_customerVehicleSpan"
                          data-key="vin">${(customerVehicle.vin ==null || customerVehicle.vin =='')?'--':(customerVehicle.vin)}</span>
            </td>
        </tr>
        <tr>
            <td>购买日期：<span class="J_customerVehicleSpan"
                           data-key="carDateStr">${(customerVehicle.carDateStr ==null || customerVehicle.carDateStr =='')?'--':(customerVehicle.carDateStr)}</span>
            </td>
            <td><a class="blue_color"
                   onclick="customerConsume('vehicleConsume')">累计消费：<span
                    class="J_customerVehicleSpan"
                    data-key="consumeTimes">${customerVehicle.consumeTimes}</span>次
                  <span class="J_customerVehicleSpan"
                        data-key="totalConsume">${customerVehicle.totalConsume}</span>元</a></td>


        </tr>
        <tr>
            <td>当前里程：<span class="J_customerVehicleSpan"
                           data-key="obdMileage">${(customerVehicle.obdMileageStr ==null || customerVehicle.obdMileageStr =='')?'--':(customerVehicle.obdMileageStr)}</span>${(customerVehicle.obdMileageStr ==null || customerVehicle.obdMileageStr =='')?'':'公里'}
                <a id="registerMaintainBtn" data-index="${status.index}"
                   class="blue_color">保养登记</a>
            </td>
            <td>上次保养：<span class="J_customerVehicleSpan"
                           data-key="lastMaintainMileage">${(customerVehicle.lastMaintainMileage ==null || customerVehicle.lastMaintainMileage =='')?'--':(customerVehicle.lastMaintainMileage)}${(customerVehicle.lastMaintainMileage ==null || customerVehicle.lastMaintainMileage =='')?'':'公里'}</span>/<span
                    class="J_customerVehicleSpan"
                    data-key="lastMaintainTimeStr">${(customerVehicle.lastMaintainTimeStr ==null || customerVehicle.lastMaintainTimeStr =='')?'--':(customerVehicle.lastMaintainTimeStr)}</span>
            </td>
        </tr>
        <tr>
            <td>保养周期：<span class="J_customerVehicleSpan"
                           data-key="maintainMileagePeriod">${(customerVehicle.maintainMileagePeriod ==null || customerVehicle.maintainMileagePeriod =='')?'--':(customerVehicle.maintainMileagePeriod)}${(customerVehicle.maintainMileagePeriod ==null || customerVehicle.maintainMileagePeriod =='')?'':'公里'}</span>
            </td>
            <td>距下次保养：<span class="J_customerVehicleSpan"
                            data-key="nextMaintainMileageAccessStr">${(customerVehicle.nextMaintainMileageAccessStr ==null || customerVehicle.nextMaintainMileageAccessStr =='')?'--':(customerVehicle.nextMaintainMileageAccessStr)}</span>
            </td>
        </tr>

    </table>

    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
        <div class="editButton J_editCustomerVehicle" data-index="${status.index}">编 辑
        </div>
        <div class="editButton J_deleteCustomerVehicle" data-index="${status.index}">删
            除
        </div>
    </bcgogo:hasPermission>

    <div class="editButton J_createAppointOrder" data-index="${status.index}">新增预约</div>
</div>
<%--<c:if test="${fourSShopVersions}">--%>
    <div class="member-right" style="width:450px;overflow-y: auto;height:70px;"
         id="customerVehicleOBDInfoShow${status.index}">
                   <span id="bindSpan${status.index}" class="bind_text" style="margin:18px 100px;
                   <c:choose>
                   <c:when test="${empty customerVehicle.gsmObdImei && empty customerVehicle.gsmObdImeiMoblie}"> display:block;</c:when>
        <c:otherwise>display:none;</c:otherwise></c:choose>">
        暂时未绑定OBD/后视镜，我要<a class="blue_color bind_obd_btn" data-index="${status.index}">马上绑定</a>！
        </span>

        <table width="100%" border="0" class="member-table"
               id="vehicleODBTableShow${status.index}" style="<c:if
                test='${empty customerVehicle.gsmObdImei && empty customerVehicle.gsmObdImeiMoblie}'>display:none;</c:if>">
            <tr>
                <th><span class="fl">OBD/后视镜</span></th>
                <td><a class="blue_color edit_obd_btn" data-index="${status.index}"
                       style="float:right;margin-right: 15px">修改OBD/后视镜</a></td>
            </tr>
            <tr>
                <td>IMEI号：<span class="J_customerVehicleSpan imei_span"
                                data-key="gsmObdImei">${(customerVehicle.gsmObdImei ==null || customerVehicle.gsmObdImei =='')?'--':customerVehicle.gsmObdImei}</span>
                </td>
                <td>SIM卡号：<span class="J_customerVehicleSpan sim_no_span"
                                data-key="gsmObdImeiMoblie">${(customerVehicle.gsmObdImeiMoblie ==null || customerVehicle.gsmObdImeiMoblie =='')?'--':customerVehicle.gsmObdImeiMoblie}</span>
                </td>
            </tr>

        </table>
    </div>
<%--</c:if>--%>


<div class="member-right"
     style="width:450px;overflow-y: auto;height:${fourSShopVersions?100:155}px;"
     id="customerVehicleAppointInfoShow${status.index}">
    <table width="100%" border="0" class="member-table"
           id="appointServiceTableShow${status.index}">
        <tr>
            <th><span class="fl">提醒服务</span>
            </th>
            <th>&nbsp;</th>
        </tr>
        <tr>
            <td>保养里程：<span class="J_customerVehicleAppointSpan"
                           data-key="maintainMileage">${(customerVehicle.maintainMileage ==null || customerVehicle.maintainMileage =='')?'--':customerVehicle.maintainMileage}</span>
                                    <span class="J_maintainMileageUnitSpan"
                                          style="display: ${empty customerVehicle.maintainMileage?'none':''}">公里</span>
            </td>
            <td>保险时间：<span class="J_customerVehicleAppointSpan"
                           data-key="insureTimeStr">${(customerVehicle.insureTimeStr ==null || customerVehicle.insureTimeStr =='')?'--':customerVehicle.insureTimeStr}</span>
            </td>
        </tr>
        <tr>
            <td>保养时间：<span class="J_customerVehicleAppointSpan"
                           data-key="maintainTimeStr">${(customerVehicle.maintainTimeStr ==null || customerVehicle.maintainTimeStr =='')?'--':customerVehicle.maintainTimeStr}</span>
            </td>
            <td>验车时间：<span class="J_customerVehicleAppointSpan"
                           data-key="examineTimeStr">${(customerVehicle.examineTimeStr ==null || customerVehicle.examineTimeStr =='')?'--':customerVehicle.examineTimeStr}</span>
            </td>
        </tr>
        <c:if test="${not empty customerVehicle.appointServiceDTOs}">
            <c:forEach items="${customerVehicle.appointServiceDTOs}"
                       var="appointServiceDTO"
                       varStatus="appointStatus">
                <c:if test="${appointStatus.index%2==0}">
                    <tr class="J_appointServiceTrShow">
                </c:if>
                <td>${appointServiceDTO.appointName}：${appointServiceDTO.appointDate}</td>
                <c:if test="${appointStatus.index%2==0 && appointStatus.last}">
                    <td></td>
                </c:if>
                <c:if test="${appointStatus.index%2==1 || appointStatus.last}">
                    </tr>
                </c:if>
            </c:forEach>
        </c:if>
    </table>
</div>

<form action="customer.do?method=ajaxAddOrUpdateCustomerVehicle"
      id="customerVehicleForm${status.index}"
      method="post">
<input type="hidden" id="vehicleId${status.index}" name="vehicleId"
       value="${customerVehicle.vehicleId}"/>
<input type="hidden" id="obdId${status.index}" name="obdId"
       value="${customerVehicle.obdId}"/>
<input type="hidden" name="customerId" value="${customerId}"/>
<input type="hidden" value="${customerVehicle.gsmObdImeiMoblie}"
       name="gsmObdImeiMoblie" id="gsmObdImeiMoblie${status.index}">
<input type="hidden" value="${customerVehicle.gsmObdImei}" name="gsmObdImei"
       id="gsmObdImei${status.index}">
<input type="hidden" value="${customerVehicle.maintainTimePeriodStr}"
       id="maintainTimePeriodStr${status.index}">

<div class="member-left" id="customerVehicleBasicInfoEdit${status.index}"
     style="display: none;">
    <table width="100%" border="0" class="order-table">
        <colgroup>
            <col width="80"/>
            <col width="220"/>
            <col width="80"/>
            <col width="220"/>
        </colgroup>

        <tr>
            <th class="test1"><span class="red_color">*</span>车牌号</th>
            <td>：<input type="text" maxlength="10" id="licenceNo${status.index}"
                        name="licenceNo"
                        reset-value="${customerVehicle.licenceNo}"
                        value="${customerVehicle.licenceNo}"
                        class="txt J_formreset"/></td>
            <th class="test1">联系人</th>
            <td>：<input type="text" maxlength="20" id="contact${status.index}"
                        name="contact"
                        reset-value="${customerVehicle.contact}"
                        value="${customerVehicle.contact}"
                        class="txt J_formreset"/></td>
        </tr>
        <tr>

            <th class="test1">联系方式</th>
            <td>：<input type="text" maxlength="11" id="mobile${status.index}"
                        name="mobile"
                        reset-value="${customerVehicle.mobile}"
                        value="${customerVehicle.mobile}"
                        class="txt J_formreset J_customerVehicleMobile"/></td>

            <th class="test1">购买日期</th>
            <td>：<input type="text" onclick="showDatePicker(this);" maxlength="10"
                        readonly="readonly"
                        id="dateString${status.index}" name="dateString"
                        reset-value="${customerVehicle.carDateStr}"
                        value="${customerVehicle.carDateStr}"
                        class="J_customerVehicleBuyDate txt J_formreset"/></td>

        </tr>
        <tr>
            <th class="test1">车辆品牌</th>
            <td>：<input type="text" maxlength="10" id="brand${status.index}"
                        name="brand"
                        reset-value="${customerVehicle.brand}"
                        value="${customerVehicle.brand}"
                        class="txt J_formreset J_checkVehicleBrandModel"/></td>
            <th class="test1">车 型</th>
            <td>：<input type="text" maxlength="10" id="model${status.index}"
                        name="model"
                        reset-value="${customerVehicle.model}"
                        value="${customerVehicle.model}"
                        class="txt J_formreset J_checkVehicleBrandModel"/></td>
        </tr>


        <tr>
            <th class="test1">发动机号</th>
            <td>：<input type="text" maxlength="20" id="engineNo${status.index}"
                        name="engineNo"
                        reset-value="${customerVehicle.engineNo}"
                        value="${customerVehicle.engineNo}"
                        class="txt J_formreset"/></td>
            <th class="test1">车架号</th>
            <td>：<input type="text" maxlength="17" id="vin${status.index}"
                        name="vin"
                        reset-value="${customerVehicle.vin}"
                        value="${customerVehicle.vin}"
                        class="txt J_formreset chassisNumber"
                        style="text-transform:uppercase;"/></td>
        </tr>


        <tr>
            <th class="test1">年 代</th>
            <td>：<input type="text" maxlength="4" id="year${status.index}"
                        name="year"
                        reset-value="${customerVehicle.year}"
                        value="${customerVehicle.year}"
                        class="txt J_formreset"/></td>
            <th class="test1">排 量</th>
            <td>：<input type="text" maxlength="6" id="engine${status.index}"
                        name="engine"
                        reset-value="${customerVehicle.engine}"
                        value="${customerVehicle.engine}"
                        class="txt J_formreset"/></td>
        </tr>
        <tr>
            <th class="test1">车身颜色</th>
            <td>：<input type="text" maxlength="10" id="color${status.index}"
                        name="color"
                        reset-value="${customerVehicle.color}"
                        value="${customerVehicle.color}"
                        class="txt J_formreset"/></td>

            <th class="test1">当前里程</th>
            <td>：<input type="text" maxlength="10" id="obdMileage${status.index}"
                        name="obdMileage"
                        reset-value="${customerVehicle.obdMileageStr}"
                        value="${customerVehicle.obdMileageStr}"
                        class="txt J_formreset"/></td>
        </tr>

        <tr>
            <th class="test1">保养周期</th>
            <td>：<input type="text" maxlength="10"
                        id="maintainMileagePeriod${status.index}"
                        name="maintainMileagePeriod"
                        reset-value="${customerVehicle.maintainMileagePeriod}"
                        value="${customerVehicle.maintainMileagePeriod}"
                        class="txt J_formreset"/>
                <th></th>
            <td></td>
        </tr>

    </table>
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
        <div class="padding10">
            <input type="button" class="query-btn J_saveCustomerVehicleBtn"
                   data-index="${status.index}"
                   value="保存"/>
            <input type="button" class="query-btn J_cancelCustomerVehicleBtn"
                   data-index="${status.index}"
                   value="取消"/>
        </div>
    </bcgogo:hasPermission>
</div>


<div class="member-right"
     style="width:450px;overflow-y: auto;height:${fourSShopVersions?100:155}px;display: none;width: 450px;"
     id="customerVehicleAppointInfoEdit${status.index}">
    <table width="100%" border="0" class="member-table J_vehicleAppointTable"
           id="appointServiceTableEdit${status.index}">
        <tr>
            <th align="left">提醒服务</th>
            <th>&nbsp;</th>
        </tr>
        <tr>
            <td class="test2">保养里程：</td>
            <td><input type="text" style="width:75px" maxlength="6"
                       id="maintainMileage${status.index}"
                       name="maintainMileage"
                       reset-value="${customerVehicle.maintainMileage}"
                       value="${customerVehicle.maintainMileage}"
                       class="txt J_formreset"/>公里
            </td>
            <td class="test2">保险时间：</td>
            <td><input type="text" style="width:75px"
                       onclick="showDatePicker(this);" readonly="readonly"
                       id="bx${status.index}" name="bx"
                       reset-value="${customerVehicle.insureTimeStr}"
                       value="${customerVehicle.insureTimeStr}"
                       class="txt J_formreset"/></td>
        </tr>
        <tr>
            <td class="test2">保养时间：</td>
            <td><input type="text" style="width:75px"
                       onclick="showDatePicker(this);" readonly="readonly"
                       id="by${status.index}" name="by"
                       reset-value="${customerVehicle.maintainTimeStr}"
                       value="${customerVehicle.maintainTimeStr}"
                       class="txt J_formreset"/></td>
            <td class="test2">验车时间：</td>
            <td><input type="text" style="width:75px"
                       onclick="showDatePicker(this);" readonly="readonly"
                       id="yc${status.index}" name="yc"
                       reset-value="${customerVehicle.examineTimeStr}"
                       value="${customerVehicle.examineTimeStr}"
                       class="txt J_formreset"/>
                <c:if test="${empty customerVehicle.appointServiceDTOs}">
                    <a class="J_addAppointService" data-index="0"><img
                            src="images/opera2.png"/></a>
                </c:if>

            </td>
        </tr>

        <c:if test="${not empty customerVehicle.appointServiceDTOs}">
            <c:forEach items="${customerVehicle.appointServiceDTOs}"
                       var="appointServiceDTO"
                       varStatus="appointStatus">
                <c:if test="${appointStatus.index%2==0}">
                    <tr class="J_appointServiceTrEdit">
                </c:if>
                <td class="test2">
                    <input type="hidden"
                           name="appointServiceDTOs[${appointStatus.index}].appointName"
                           value="${appointServiceDTO.appointName}"/>${appointServiceDTO.appointName}：
                </td>
                <td class="J_appointServiceDateTd">
                    <input type="hidden"
                           name="appointServiceDTOs[${appointStatus.index}].id"
                           value="${appointServiceDTO.id}">
                    <input type="hidden"
                           name="appointServiceDTOs[${appointStatus.index}].operateType"
                           value="${appointServiceDTO.operateType}">

                    <input type="text" style="width:75px"
                           onclick="showDatePicker(this);" readonly="readonly"
                           name="appointServiceDTOs[${appointStatus.index}].appointDate"
                           reset-value="${appointServiceDTO.appointDate}"
                           value="${appointServiceDTO.appointDate}"
                           class="txt J_formreset"/>
                    <a data-index="${appointStatus.index}"
                       class="J_deleteAppointService"><img src="images/opera1.png"/></a>

                    <c:if test="${appointStatus.last}">
                        <a class="J_addAppointService"
                           data-index="${appointStatus.index}"><img
                                src="images/opera2.png"/></a>
                    </c:if>
                </td>

                <c:if test="${appointStatus.index%2==1 || appointStatus.last}">
                    </tr>
                </c:if>
            </c:forEach>
        </c:if>

    </table>
</div>
<div class="clear"></div>
</form>

<div class="clear"></div>
</div>
<div class="clear i_height"></div>
</div>
</c:forEach>
</c:if>
</div>
<div class="clear height"></div>

</div>


<div class="titBody" id="customerDetailConsume">

<div class="lineTitle"><span>消费信息</span>

    <div class="title-r" style="float:left; margin-left:10px;">
        <a style="cursor: pointer;" id="moreConsumeInfo">查询条件<img src="images/rightArrow.png"
                                                                  style="float:right; margin:12px 0 0 5px;"/></a>
    </div>
</div>


<div class="lineBody" style="border:#ccc 1px solid; background:none; width:968px; border-top:0;">
<div class="cuSearch">
<div id="queryCondition" style="display: none;">

<div class="gray-radius" style="margin:0;width:950px;">

<form id="inquiryCenterSearchForm" commandName="inquiryCenterInitialDTO"
      name="inquiryCenterSearchForm"
      action="inquiryCenter.do?method=inquiryCenterSearchOrderAction" method="post">
<input type="hidden" name="maxRows" id="pageRows" value="5">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" id="sortStatus" name="sort" value="created_time desc"/>
<input type="hidden" name="customerOrSupplierId" id="customerOrSupplierId"
       value="${customerId}">
<input type="hidden" name="debtType" id="debtType" value="">
<input type="hidden" name="queryPageType" id="queryPageType"
       value="customerOrSupplierDetail">

<input type="text" style="display: none;" name="accountMemberNo" id="accountMemberNo"
       autocomplete="off" initValue="会员卡号" value="">

<div class="divTit" style="float:none"><span class="spanName">日期：</span>&nbsp;
    <a class="btnList" id="my_date_yesterday" name="my_date_select">昨天</a>&nbsp;
    <a class="btnList" id="my_date_today" name="my_date_select">今天</a>&nbsp;
    <a class="btnList" id="my_date_lastmonth" name="my_date_select">上月</a>&nbsp;
    <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
    <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
    <input id="startDate" type="text" value="" readonly="readonly" name="startTimeStr"
           class="my_startdate txt"/>&nbsp;至&nbsp;
    <input id="endDate" type="text" value="" readonly="readonly" name="endTimeStr"
           class='my_enddate txt'/>&nbsp;&nbsp;
</div>

<div class="clear"></div>
<bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition"
                                    orderNameAndResource="[purchase_order,WEB.TXN.PURCHASE_MANAGE.PURCHASE];
            [storage_order,WEB.TXN.PURCHASE_MANAGE.STORAGE];[sale_order,WEB.TXN.SALE_MANAGE.SALE];[vehicle_construction_order,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE];
            [wash_beauty_order,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE];[purchase_return_order,WEB.TXN.PURCHASE_MANAGE.RETURN];
            [sale_return_order,WEB.TXN.SALE_MANAGE.RETURN];[buy_card_order,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER];
            [return_card,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER]">
    <c:if test="${!order_type_condition_has_none_of_the_order_group}">
        <div style="padding: 0 0 5px;" class="divTit divWarehouse member">
            <span class="spanName">单据类型：</span>

            <div class="warehouseList" id="orderTypes" style="width: auto;">
                <label class="rad" id="orderTypeAllLabel"><input type="checkbox"
                                                                 id="orderTypeAll"
                                                                 data-name="all"/>所有</label>&nbsp;

                <c:choose>
                    <c:when test="${order_type_condition_sale_order}">
                        <label class="rad" id="saleLabel"><input type="checkbox"
                                                                 name="orderType"
                                                                 value="SALE"
                                                                 data-name="sale"/>销售单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
                            <input type="hidden" name="orderType" origValue="SALE"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_vehicle_construction_order}">
                        <label class="rad" id="repairLabel"><input type="checkbox"
                                                                   name="orderType"
                                                                   value="REPAIR"
                                                                   data-name="construction"/>施工单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission
                                permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                            <input type="hidden" name="orderType" origValue="REPAIR"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_wash_beauty_order}">
                        <label class="rad" id="washLabel"><input type="checkbox"
                                                                 name="orderType"
                                                                 value="WASH_BEAUTY"
                                                                 data-name="beauty"/>洗车美容</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission
                                permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                            <input type="hidden" name="orderType"
                                   origValue="WASH_BEAUTY"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_sale_return_order}">
                        <label class="rad" id="saleReturnLabel"><input type="checkbox"
                                                                       name="orderType"
                                                                       value="SALE_RETURN"
                                                                       data-name="saleReturn"/>销售退货单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
                            <input type="hidden" name="orderType"
                                   origValue="SALE_RETURN"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_buy_card_order}">
                        <label class="rad" id="memberLabel"><input type="checkbox"
                                                                   name="orderType"
                                                                   value="MEMBER_BUY_CARD"
                                                                   data-name="buyCard"/>会员购卡续卡</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission
                                permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                            <input type="hidden" name="orderType"
                                   origValue="MEMBER_RETURN_CARD"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_return_card}">
                        <label class="rad" id="memberReturnLabel"><input type="checkbox"
                                                                         name="orderType"
                                                                         value="MEMBER_RETURN_CARD"
                                                                         data-name="returnCard"/>会员退卡</label>
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission
                                permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                            <input type="hidden" name="orderType"
                                   origValue="MEMBER_RETURN_CARD"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>

            </div>

            <bcgogo:orderPageConfigurationParam orderGroupName="order_other_condition"
                                                orderNameAndResource="order_status_repeal">
                <c:if test="${order_other_condition_order_status_repeal}">
                    <label class="rad" id="orderStatusRepealLabel"><input
                            type="checkbox" id="orderStatusRepeal"
                            name="orderStatusRepeal" value="YES"/><span
                            class="red_color">包含作废</span></label>
                </c:if>
            </bcgogo:orderPageConfigurationParam>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>

<div class="clear"></div>
<div class="divTit" style="float:none">


    <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION"
                          resourceType="logic">
        <span class="spanName">车牌号：</span>
    </bcgogo:hasPermission>

    <div class="warehouseList">
        <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION"
                              resourceType="logic">
            &nbsp;<input type="text" class="vehicle txt" autocomplete="off" initValue="车牌号"
                         value=""
                         name="vehicle" id="vehicleNumber">
        </bcgogo:hasPermission>

        &nbsp;单据号：<input type="text" value="" autocomplete="off" class="txt"
                         name="receiptNo">


        <bcgogo:orderPageConfigurationParam orderGroupName="order_product_condition"
                                            orderNameAndResource="product_info;commodity_code">
            <c:if test="${!order_product_condition_has_none_of_the_order_group}">

                &nbsp;商品：<input type="text" class="txt J-productSuggestion" id="searchWord" name="searchWord"
                                searchField="product_info" value="品名/品牌/规格/型号/适用车辆" initValue="品名/品牌/规格/型号/适用车辆"
                                style="width:180px;"/>&nbsp;

                <c:if test="${order_product_condition_product_info}">
                    <input type="text" class="txt J-productSuggestion" id="productName"
                           name="productName" searchField="product_name" value="品名"
                           initValue="品名" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productBrand"
                           name="productBrand" searchField="product_brand" value="品牌/产地"
                           initValue="品牌/产地" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productSpec"
                           name="productSpec" searchField="product_spec" value="规格"
                           initValue="规格" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion" id="productModel"
                           name="productModel" searchField="product_model" value="型号"
                           initValue="型号" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion"
                           id="productVehicleBrand" name="productVehicleBrand"
                           searchField="product_vehicle_brand" value="车辆品牌"
                           initValue="车辆品牌" style="width:85px;display: none;"/>
                    <input type="text" class="txt J-productSuggestion"
                           id="productVehicleModel" name="productVehicleModel"
                           searchField="product_vehicle_model" value="车型" initValue="车型"
                           style="width:85px;display: none;"/>
                </c:if>
                <c:if test="${order_product_condition_commodity_code}">
                    <input type="text" class="txt J-productSuggestion"
                           id="commodityCode" name="commodityCode"
                           searchField="commodity_code" value="商品编号" initValue="商品编号"
                           style="text-transform: uppercase;width:85px;display: none;"/>
                </c:if>
            </c:if>
        </bcgogo:orderPageConfigurationParam>


    </div>
</div>
<div class="clear"></div>


<bcgogo:orderPageConfigurationParam orderGroupName="order_pay_method_condition"
                                    orderNameAndResource="cash;bankCard;cheque;deposit;[customer_deposit,WEB.VERSION.CUSTOMER.DEPOSIT.USE];
          [member_balance_pay,WEB.VERSION.VEHICLE_CONSTRUCTION];not_paid;statement_account;expense_amount;[coupon,WEB.VERSION.VEHICLE_CONSTRUCTION]">
    <c:if test="${!order_pay_method_condition_has_none_of_the_order_group}">
        <div class="divTit divWarehouse member more_condition"
             style="float:none;padding: 0 0 5px;" id="settlementMethod">
            <span class="spanName">结算方式：</span>

            <div class="warehouseList">
                <c:if test="${order_pay_method_condition_cash}">
                    <label class="rad"><input type="checkbox" value="CASH" id="cash"
                                              name="payMethod"/>现金</label>&nbsp;
                </c:if>
                <c:if test="${order_pay_method_condition_bankCard}">
                    <label class="rad"><input type="checkbox" value="BANK_CARD"
                                              id="bankCard" name="payMethod"/>银联</label>
                </c:if>
                <c:if test="${order_pay_method_condition_cheque}">
                    <label class="rad"><input type="checkbox" value="CHEQUE" id="cheque"
                                              name="payMethod"/>支票</label>
                </c:if>
                <c:if test="${order_pay_method_condition_customer_deposit}">
                    <label class="rad"><input type="checkbox" value="CUSTOMER_DEPOSIT"
                                              id="customerDeposit" name="payMethod"/>预收款</label>
                </c:if>
                <c:if test="${order_pay_method_condition_deposit}">
                    <label class="rad"><input type="checkbox" value="DEPOSIT"
                                              id="deposit" name="payMethod"/>预付款</label>
                </c:if>
                <c:if test="${order_pay_method_condition_member_balance_pay}">
                    <label class="rad"><input type="checkbox" value="MEMBER_BALANCE_PAY"
                                              id="memberBalancePay"
                                              name="payMethod"/>会员储值</label>
                </c:if>
                <c:if test="${order_pay_method_condition_not_paid}">
                    <label id="debtLabel" class="rad"><input type="checkbox"
                                                             value="true" id="notPaid"
                                                             name="notPaid"/>挂账</label>
                </c:if>
                <c:if test="${order_pay_method_condition_statement_account}">
                    <label class="rad"><input type="checkbox" value="STATEMENT_ACCOUNT"
                                              id="statement_account"
                                              name="payMethod"/>对账</label>
                </c:if>
                <c:if test="${order_pay_method_condition_coupon}">
                    <label class="rad"><input type="checkbox" value="COUPON" id="coupon"
                                              name="payMethod"/>消费券</label>
                    <input type="text" initValue='消费券类型' id="couponType"
                           name="couponType" class="txt"/>
                </c:if>
                <c:if test="${order_pay_method_condition_expense_amount}">
                    消费金额：<input type="text" class="mon_search txt" name="amountLower" id="amountLower"
                                style="width:90px;"/>~<input type="text" class="mon_search txt" name="amountUpper" id="amountUpper" style="width:90px;"/> 元
                </c:if>
            </div>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>

<div class="divTit button_conditon button_search"><a
        class="blue_color clean J_clean_style" id="resetSearchCondition"
        href="javascript:">清空条件</a><a class="button" id="btnSearch">查&nbsp;询</a>
</div>
</form>
</div>

<div class="clear i_height"></div>

</div>

<div class="gray-radius" style="margin:0; padding:5px 10px;">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr id="statisticsInfo">
            <td valign="top">
                <div class="divTit" style="padding: 0;">共&nbsp;<b id="totalNum">0</b>&nbsp;条记录&nbsp;&nbsp;
                </div>
            </td>
            <td valign="top">
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                    <div class="divTit" style="padding: 0;" data-type="construction">施工（<b
                            id="counts_order_total_amount_order_type_repair">0</b>笔&nbsp;
                        应收<b class="red_color"
                             id="amounts_order_total_amount_order_type_repair">0</b>元&nbsp;
                        实收<b class="red_color"
                             id="amounts_order_settled_amount_order_type_repair">0</b>元&nbsp;
                        欠款<b class="red_color"
                             id="amounts_order_debt_amount_order_type_repair">0</b>元）
                    </div>
                </bcgogo:hasPermission>

                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                    <div class="divTit" style="padding: 0;" data-type="sale">销售（<b
                            id="counts_order_total_amount_order_type_sale">0</b>笔&nbsp;
                        应收<b class="red_color" id="amounts_order_total_amount_order_type_sale">0</b>元&nbsp;
                        实收<b class="red_color"
                             id="amounts_order_settled_amount_order_type_sale">0</b>元&nbsp;
                        欠款<b class="red_color" id="amounts_order_debt_amount_order_type_sale">0</b>元）
                    </div>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                    <div class="divTit" style="padding: 0;" data-type="buyCard">购卡（<b
                            id="counts_order_total_amount_order_type_member_buy_card">0</b>笔&nbsp;
                        应收<b class="red_color"
                             id="amounts_order_total_amount_order_type_member_buy_card">0</b>元&nbsp;
                        实收<b class="red_color"
                             id="amounts_order_settled_amount_order_type_member_buy_card">0</b>元&nbsp;
                        欠款<b class="red_color"
                             id="amounts_order_debt_amount_order_type_member_buy_card">0</b>元）
                    </div>
                </bcgogo:hasPermission>


            </td>


            <td valign="top">
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                    <div class="divTit" style="padding: 0;" data-type="beauty">洗车美容（<b
                            id="counts_order_total_amount_order_type_wash_beauty">0</b>笔&nbsp;
                        应收<b class="red_color"
                             id="amounts_order_total_amount_order_type_wash_beauty">0</b>元&nbsp;
                        实收<b class="red_color"
                             id="amounts_order_settled_amount_order_type_wash_beauty">0</b>元&nbsp;
                        欠款<b class="red_color"
                             id="amounts_order_debt_amount_order_type_wash_beauty">0</b>元）
                    </div>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission
                        permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN"
                        resourceType="menu">
                    <div class="divTit" style="padding: 0;" data-type="saleReturn">销售退货（<b
                            id="counts_order_total_amount_order_type_sale_return">0</b>笔&nbsp;
                        应付<b class="green_color"
                             id="amounts_order_total_amount_order_type_sale_return">0</b>元&nbsp;
                        实付<b class="green_color"
                             id="amounts_order_settled_amount_order_type_sale_return">0</b>元&nbsp;
                        欠付<b class="green_color"
                             id="amounts_order_debt_amount_order_type_sale_return">0</b>元）
                    </div>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                    <div class="divTit" style="padding: 0;" data-type="returnCard">退卡（<b
                            id="counts_order_total_amount_order_type_member_return_card">0</b>笔<b
                            class="green_color"
                            id="amounts_debt_and_settled_amount_order_type_member_return_card">0</b>元）
                    </div>
                </bcgogo:hasPermission>
            </td>
        </tr>
    </table>
    <div style="width:949px;border-right:1px solid #C5C5C5" class="line_develop list_develop">
        <span class="fl" style="font-size:14px; color:#333; margin-left:10px;">排序方式：</span>
        <a class="J_order_sort" sortField="receipt_no" currentSortStatus="desc">单据号<span
                class="arrowDown J_sort_span"></span></a>
        <a class="hover J_order_sort" sortField="created_time" currentSortStatus="desc">日期<span
                class="arrowDown J_sort_span"></span></a>
        <a class="J_order_sort" sortField="order_total_amount" currentSortStatus="desc">金额<span
                class="arrowDown J_sort_span"></span></a>

    </div>
    <table class="tab_cuSearch J_tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;">

        <col width="100">
        <col width="80">
        <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION" resourceType="logic">
            <col width="70">
        </bcgogo:hasPermission>
        <col width="90">
        <col>
        <col width="60">
        <col width="70">
        <col width="60">
        <col width="60">
        <col width="70">


        <tr class="titleBg">
            <td style="padding-left:10px;">单据号</td>
            <td>日期</td>
            <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION"
                                  resourceType="logic">
                <td>车牌号</td>
            </bcgogo:hasPermission>
            <td>单据类型</td>
            <td>内容</td>
            <td>总计</td>
            <td>实收/实付</td>
            <td>欠款</td>
            <td>优惠</td>
            <td>状态</td>
        </tr>
        <tr class="space">
            <td colspan="9"></td>
        </tr>
    </table>
    <bcgogo:ajaxPaging url="inquiryCenter.do?method=inquiryCenterSearchOrderAction"
                       postFn="showResponse"
                       dynamical="inquiryCenter" display="none"/>

    <div class="clear i_height"></div>

    <div class="clear i_height"></div>
</div>
</div>
</div>

<div class="clear height"></div>

</div>

<div class="titBody" style="display: none;" id="customerDetailAppoint">
    <div class="lineTitle" style="text-align:left;color: #444443">预约服务</div>
    <div class="lineBody bodys">
        <form id="appointOrderListForm">
            <input type="hidden" id="customerIds" value="${customerId}">
            <input type="hidden" id="maxRows" value="5">

            <table border="0" cellpadding="0" cellspacing="0" class="order-management">
                <tr>
                    <td>车牌号：</td>
                    <td colspan="2">
                        <select autocomplete="off" id="vehicleNo" name="vehicleNo" class="txt txt_color">
                            <option selected value="">全部</option>
                            <c:if test="${not empty customerVehicleResponseList}">
                                <c:forEach items="${customerVehicleResponseList}" var="customerVehicle"
                                           varStatus="status">
                                    <option value="${customerVehicle.licenceNo}">${customerVehicle.licenceNo}</option>
                                </c:forEach>
                            </c:if>
                        </select>
                    </td>
                    <td>预计服务时间：</td>
                    <td>
                        <input autocomplete="off" type="text" class="txt" id="appointTimeStartStr"
                               name="appointTimeStartStr"/>
                        &nbsp;到&nbsp;
                        <input autocomplete="off" type="text" class="txt" id="appointTimeEndStr"
                               name="appointTimeEndStr"/>
                    </td>
                    <td>状态：
                        <select class="txt txt_color" id="appointOrderStatus" name="appointOrderStatus">
                            <option value="">所有</option>
                            <option value="PENDING">待确认</option>
                            <option value="ACCEPTED">已接受</option>
                            <option value="TO_DO_REPAIR">待施工</option>
                            <option value="HANDLED">已施工</option>
                            <option value="REFUSED">已拒绝</option>
                            <option value="CANCELED">已取消</option>
                        </select></td>
                </tr>
            </table>

            <div class="clear height"></div>
            <div class="divTit button_conditon button_search"><a class="blue_color clean"
                                                                 id="clearSearchCondition"
                                                                 myCustomerDetail="true">清空条件</a> <a
                    class="button"
                    id="searchAppointOrderBtn">查
                询</a></div>
        </form>
        <div class="supplier group_list listStyle" style="width:940px;">
            <bcgogo:hasPermission resourceType="render" permissions="WEB.TXN.APPOINT_ORDER_MANAGER">
                <a class="addNewSup blue_color J_add_newOrder">新增预约</a>
            </bcgogo:hasPermission>
        </div>
        <div class="cuSearch">
            <div class="gray-radius" style="margin:0;">
                <table class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;"
                       id="appointOrderListTb">
                    <colgroup>
                        <col width="100">
                        <col>
                        <col width="80">
                        <col width="100">
                        <col width="100">
                        <col width="116">
                        <col width="116">
                        <col width="80">
                        <col width="80">
                        <col width="100">
                    </colgroup>
                    <tr class="titleBg">
                        <td style="padding-left:10px;">单据号</td>
                        <td>客户名</td>
                        <td>车牌号</td>
                        <td>手机号</td>
                        <td>服务类型</td>
                        <td>下单时间</td>
                        <td>预计服务时间</td>
                        <td>预约方式</td>
                        <td>状态</td>
                        <td>操作</td>
                    </tr>
                    <tr class="space">
                        <td colspan="10"></td>
                    </tr>
                </table>
                <div class="clear i_height"></div>
                <bcgogo:ajaxPaging url="appoint.do?method=searchAppointOrder" dynamical="appointOrderList"
                                   data='{startPageNo:1,maxRows:5,customerIds:\"${customerId}\"}'
                                   postFn="drawAppointOrderList"/>
                <div class="clear i_height"></div>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
    <div class="lineBottom"></div>

    <div class="clear i_height"></div>
</div>

<bcgogo:hasPermission permissions="web.statement.order.redirectSearchCustomerBill" resourceType="menu">
    <div id="customerDetailStatement" style="display: none;">
        <%@include file="/customerDetail/customerDetailBill.jsp" %>
    </div>
</bcgogo:hasPermission>


<div class="titBody" style="display: none;" id="customerDetailPhoto">
    <form action="customer.do?method=saveCustomerIdentificationImageRelation" id="identificationImageForm"
          method="post">
        <input type="hidden" name="customerId" value="${customerId}"/>

        <div class="lineTitle" style="text-align:left;color: #444443">
            <span>证件照片</span>

            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                <div style="margin:10px;float: left; height: 70px;margin-top:5px;"
                     id="identificationImageUploader"></div>
            </bcgogo:hasPermission>

        </div>
        <div class="clear"></div>
        <c:set var="customerIdentificationImageCount" value="0"/>
        <c:if
                test="${not empty customerDTO.imageCenterDTO && not empty customerDTO.imageCenterDTO.customerIdentificationImageDetailDTOs}">
            <c:forEach items="${customerDTO.imageCenterDTO.customerIdentificationImageDetailDTOs}"
                       var="customerIdentificationImageDetailDTO" varStatus="status">
                <input type="hidden" class="J_formreset" id="customerIdentificationImagePaths${status.index}"
                       name="customerIdentificationImagePaths"
                       reset-value="${customerIdentificationImageDetailDTO.imagePath}"
                       value="${customerIdentificationImageDetailDTO.imagePath}">
                <c:set var="customerIdentificationImageCount" value="${customerIdentificationImageCount+1}"/>
            </c:forEach>
        </c:if>
        <c:forEach begin="${customerIdentificationImageCount}" end="5" step="1" varStatus="status">
            <input type="hidden" class="J_formreset" id="customerIdentificationImagePaths${status.index}"
                   name="customerIdentificationImagePaths" value="">
        </c:forEach>

        <div class="customer" id="identificationImageEdit">
            <div class="storePhotos" style="margin:0px; background:none; border:none; width:993px;">
                <%--<div style="margin:10px;float: left; height: 70px;" id="identificationImageUploader"></div>--%>
                <div class="tip-content" style="margin:10px;float: left; height: 70px;">
                    <div class="left">提示：</div>
                    <div class="right"> 1. 所选图片都必须是 jpeg 、jpg 和 png 格式；<br/>
                        2. 每张图片的大小不得超过5M；<br/>
                        3. 您可以一次选择多张图片，最多上传6张图片。<br/>
                    </div>
                </div>
                <div class="clear i_height"></div>
                <div id="identificationImageUploaderView"
                     style="width:auto;height:180px;position: relative;margin-left: 10px;"></div>
            </div>
            <input type="hidden" class="query-btn" id="saveIdentificationImageEditBtn" value="确认"/>
        </div>
    </form>
</div>
<div class="clear i_height"></div>

<c:if test="${fromPage =='customerData'}">

    <div class="shopping_btn">
        <div class="divImg" id="returnCustomerListBtn">
            <img src="images/return.png"/>

            <div class="sureWords" style="font-size:12px">返回客户列表</div>
        </div>
    </div>
</c:if>
</div>
<div class="clear i_height"></div>
<div id="customerOtherInfo" class="customerOtherInfo customer"
     style="position:relative; z-index:1; display:none;width: 994px;border:#ccc 1px solid; background:#eee;box-shadow:3px 3px 8px #bbb; border-top:0">
    <table width="100%" border="0" class="order-table">
        <colgroup>
            <col width="200"/>
            <col width="200"/>
            <col width="200"/>
            <col width="200"/>
        </colgroup>
        <tr>
            <td>Email：<span class="J_customerBasicSpan"
                            data-key="email">${(customerDTO.email ==null || customerDTO.email =='')?'--':customerDTO.email}</span>
            </td>
            <td>传真：<span class="J_customerBasicSpan"
                         data-key="fax">${(customerDTO.fax ==null || customerDTO.fax =='')?'--':customerDTO.fax}</span>
            </td>
            <td colspan="2">地址：<span class="J_customerBasicSpan"
                                     data-key="address">${(customerDTO.address ==null || customerDTO.address =='')?'--':customerDTO.address}</span>
            </td>

        </tr>
        <tr>
            <td>QQ：<span class="J_customerBasicSpan"
                         data-key="qq">${(customerDTO.qq ==null || customerDTO.qq =='')?'--':customerDTO.qq}</span>
            </td>
            <td>生日：<span class="J_customerBasicSpan"
                         data-key="birthdayString">${(customerDTO.birthdayString ==null || customerDTO.birthdayString =='')?'--':customerDTO.birthdayString}</span>
            </td>
            <td>简称：<span class="J_customerBasicSpan"
                         data-key="shortName">${(customerDTO.shortName ==null || customerDTO.shortName =='')?'--':customerDTO.shortName}</span>
            </td>
            <td>客户类别：<span class="J_customerBasicSpan"
                           data-key="customerKindStr">${(customerDTO.customerKindStr ==null || customerDTO.customerKindStr =='')?'--':customerDTO.customerKindStr}</span>
            </td>
        </tr>
        <tr>
            <td>开户行：<span class="J_customerBasicSpan"
                          data-key="bank">${(customerDTO.bank ==null || customerDTO.bank =='')?'--':customerDTO.bank}</span>
            </td>
            <td>开户名：<span class="J_customerBasicSpan"
                          data-key="bankAccountName">${(customerDTO.bankAccountName ==null || customerDTO.bankAccountName =='')?'--':customerDTO.bankAccountName}</span>
            </td>
            <td>账号：<span class="J_customerBasicSpan"
                         data-key="account">${(customerDTO.account ==null || customerDTO.account =='')?'--':customerDTO.account}</span>
            </td>
            <td>结算类型：<span class="J_customerBasicSpan"
                           data-key="settlementTypeStr">${(customerDTO.settlementTypeStr ==null || customerDTO.settlementTypeStr =='')?'--':customerDTO.settlementTypeStr}</span>
            </td>
        </tr>
        <tr>
            <td>发票类型：<span class="J_customerBasicSpan"
                           data-key="invoiceCategoryStr">${(customerDTO.invoiceCategoryStr ==null || customerDTO.invoiceCategoryStr =='')?'--':customerDTO.invoiceCategoryStr}</span>
            </td>
            <td colspan="3">备注:<span class="J_customerBasicSpan"
                                     data-key="memo"
                                     title="${customerDTO.memo}">${(customerDTO.memo==''|| customerDTO.memo==null)?'--':customerDTO.shortMemo}</span>
            </td>
        </tr>
        <tr class="titBottom_Bg">
            <td colspan="4">
                <div class="div_Btn">
                    <a onclick="showDetailInfo();" class="btnUp" style="margin-bottom:-5px;"></a>
                </div>
            </td>
        </tr>

    </table>
</div>
</div>

<input id="isAllMakeTime" type="hidden">
<input id="memberCardId" type="hidden">
<input id="addService" type="hidden">

<div id="mask" style="display:block;position: absolute;"></div>
<div id="deleteCustomer_dialog">
    <div id="deleteReceiptNo"></div>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:7; left:200px; top:800px; display:none;overflow:hidden;"
        allowtransparency="true" width="840px" height="700px" scrolling="no" frameborder="0" src=""></iframe>

<iframe id="iframe_PopupBox_1"
        style="position:absolute;z-index:5; left:200px; top:200px; display:none;background:#FFFFFF;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:8;top:210px;left:87px;display:none; "
        allowtransparency="true" width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
        allowtransparency="true" width="743px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" scrolling="no" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" frameborder="0" src=""></iframe>
<iframe id="iframe_returnCard" scrolling="no" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" frameborder="0" src=""></iframe>
<iframe id="iframe_addService" style="position:absolute;z-index:10; left:300px; top:30px; display:none;"
        allowtransparency="true" width="780px" height="650px" frameborder="0" src=""></iframe>

<%@ include file="/sms/enterPhone.jsp" %>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
<iframe id="iframe_PopupBox_2" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<input type="hidden" id="parentPageType" value="uncleUser"/>

<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">

<div id="customerVehicleTemplate" style="display: none;">

<div id="customerVehicleDiv" class="J_customerVehicleDiv" data-index="">
<div class="padding5" id="customerVehicleInfoShow">
<div class="member-left" id="customerVehicleBasicInfoShow" style="display: none;">
    <table width="100%" border="0" class="order-table">

        <colgroup>
            <col width="200"/>
            <col width="220"/>
        </colgroup>
        <tr>
            <td>联系人：<span class="J_customerVehicleSpan" data-key="contact"></span></td>
            <td>联系方式：<span class="J_customerVehicleSpan" data-key="mobile"></span></td>
        </tr>
        <tr>
            <td>品牌车型：<span class="J_customerVehicleSpan" data-key="brand"></span>/<span
                    class="J_customerVehicleSpan"
                    data-key="model">
                  </span>
            </td>
            <td>年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year"></span>/<span
                    class="J_customerVehicleSpan"
                    data-key="engine"></span>/<span
                    class="J_customerVehicleSpan" data-key="color">
            </span></td>
        </tr>
        <tr>
            <td>发动机号：<span class="J_customerVehicleSpan" data-key="engineNo"></span></td>
            <td>车架号：<span class="J_customerVehicleSpan" data-key="vin"></span></td>
        </tr>
        <tr>
            <td>购买日期：<span class="J_customerVehicleSpan" data-key="carDateStr"></span>
            </td>
            <td><a class="blue_color">累计消费：<span class="J_customerVehicleSpan"
                                                 data-key="consumeTimes"></span>次
              <span class="J_customerVehicleSpan"
                    data-key="totalConsume"></span>元</a></td>
            <td>&nbsp;</td>

        </tr>
        <tr>
            <td>当前里程：<span class="J_customerVehicleSpan"
                           data-key="obdMileage">
            </span>公里
            </td>
            <td>上次保养：<span class="J_customerVehicleSpan"
                           data-key="lastMaintainMileage"></span>/<span class="J_customerVehicleSpan"
                                                                        data-key="lastMaintainTimeStr"></span>
            </td>
        </tr>
        <tr>
            <td>保养周期：<span class="J_customerVehicleSpan"
                           data-key="maintainMileagePeriod"></span>
            </td>
            <td>距下次保养：<span class="J_customerVehicleSpan"
                            data-key="nextMaintainMileageAccessStr"></span>
            </td>
        </tr>

    </table>

    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
        <div class="editButton J_editCustomerVehicle" data-index="">编 辑</div>
        <div class="editButton J_deleteCustomerVehicle" data-index="">删 除</div>
    </bcgogo:hasPermission>

    <div class="editButton J_createAppointOrder" data-index="">新增预约</div>
</div>


<%--<c:if test="${fourSShopVersions}">--%>
    <div class="member-right" style="width:450px;overflow-y: auto;height:70px;display: none"
         id="customerVehicleOBDInfoShow${status.index}">
                   <span id="bindSpan${status.index}" class="bind_text" style="margin:18px 100px;;display: none">
                       暂时未绑定OBD/后视镜，我要<a class="blue_color bind_obd_btn" data-index="${status.index}">马上绑定</a>！
                   </span>

        <table width="100%" border="0" class="member-table" id="vehicleODBTableShow${status.index}"
               style=";display: none">
            <tr>
                <th><span class="fl">OBD/后视镜</span></th>
                <td><a class="blue_color edit_obd_btn" data-index="${status.index}"
                       style="float:right;margin-right: 15px">修改OBD/后视镜2</a></td>
            </tr>
            <tr>
                <td>IMEI号：<span class="J_customerVehicleSpan imei_span"
                                data-key="gsmObdImei">${(customerVehicle.gsmObdImei ==null || customerVehicle.gsmObdImei =='')?'--':customerVehicle.gsmObdImei}</span>
                </td>
                <td>SIM卡号：<span class="J_customerVehicleSpan sim_no_span"
                                data-key="gsmObdImeiMoblie">${(customerVehicle.gsmObdImeiMoblie ==null || customerVehicle.gsmObdImeiMoblie =='')?'--':customerVehicle.gsmObdImeiMoblie}</span>
                </td>
            </tr>
        </table>

    </div>
<%--</c:if>--%>

<div class="member-right"
     style="width:450px;overflow-y: auto;height:${fourSShopVersions?100:155}px;display: none;"
     id="customerVehicleAppointInfoShow">
    <table width="100%" border="0" class="member-table" id="appointServiceTableShow">
        <tr>
            <th><span class="fl">提醒服务</span>
            </th>
            <th>&nbsp;</th>
        </tr>
        <tr>
            <td>保养里程：<span class="J_customerVehicleAppointSpan"
                           data-key="maintainMileage"></span>
                <span class="J_maintainMileageUnitSpan">公里</span>
            </td>
            <td>保险时间：<span class="J_customerVehicleAppointSpan"
                           data-key="insureTimeStr"></span></td>
        </tr>
        <tr>
            <td>保养时间：<span class="J_customerVehicleAppointSpan"
                           data-key="maintainTimeStr"></span></td>
            <td>验车时间：<span class="J_customerVehicleAppointSpan"
                           data-key="examineTimeStr"></span></td>
        </tr>
    </table>
</div>

<form action="customer.do?method=ajaxAddOrUpdateCustomerVehicle" id="customerVehicleForm" method="post">
    <input type="hidden" id="obdId" name="obdId" value=""/>
    <input type="hidden" name="customerId" value="${customerId}"/>
    <input type="hidden" id="vehicleId" name="vehicleId" value=""/>

    <div class="member-left" id="customerVehicleBasicInfoEdit">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="80"/>
                <col width="220"/>
                <col width="80"/>
                <col width="220"/>
            </colgroup>

            <tr>
                <th class="test1"><span class="red_color">*</span>车牌号</th>
                <td>：<input type="text" maxlength="10" id="licenceNo" name="licenceNo"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
                <th class="test1">联系人</th>
                <td>：<input type="text" maxlength="20" id="contact" name="contact"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
            </tr>
            <tr>

                <th class="test1">联系方式</th>
                <td>：<input type="text" maxlength="11" id="mobile" name="mobile"
                            reset-value="" value=""
                            class="txt J_formreset J_customerVehicleMobile"/></td>

                <th class="test1">购买日期</th>
                <td>：<input type="text" onclick="showDatePicker(this);" maxlength="10"
                            readonly="readonly"
                            id="dateString" name="dateString"
                            reset-value="" value=""
                            class="J_customerVehicleBuyDate txt J_formreset"/></td>
            </tr>
            <tr>
                <th class="test1">车辆品牌</th>
                <td>：<input type="text" maxlength="10" id="brand" name="brand"
                            reset-value="" value=""
                            class="txt J_formreset J_checkVehicleBrandModel"/></td>
                <th class="test1">车 型</th>
                <td>：<input type="text" maxlength="10" id="model" name="model"
                            reset-value="" value=""
                            class="txt J_formreset J_checkVehicleBrandModel"/></td>
            </tr>


            <tr>
                <th class="test1">发动机号</th>
                <td>：<input type="text" maxlength="20" id="engineNo" name="engineNo"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
                <th class="test1">车架号</th>
                <td>：<input type="text" maxlength="17" id="vin" name="vin"
                            reset-value="" value=""
                            class="txt J_formreset chassisNumber" style="text-transform:uppercase;"/>
                </td>
            </tr>


            <tr>
                <th class="test1">年 代</th>
                <td>：<input type="text" maxlength="4" id="year" name="year"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
                <th class="test1">排 量</th>
                <td>：<input type="text" maxlength="6" id="engine" name="engine"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
            </tr>
            <tr>
                <th class="test1">车身颜色</th>
                <td>：<input type="text" maxlength="10" id="color" name="color"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
                <th class="test1">当前里程</th>
                <td>：<input type="text" maxlength="10" id="obdMileage" name="obdMileage"
                            reset-value="" value=""
                            class="txt J_formreset"/></td>
            </tr>

            <tr>
                <th class="test1">保养周期</th>
                <td>：<input type="text" maxlength="10" id="maintainMileagePeriod"
                            name="maintainMileagePeriod"
                            reset-value=""
                            value=""
                            class="txt J_formreset"/>
                    <th></th>
                <td></td>
            </tr>


        </table>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="padding10">
                <input type="button" class="query-btn J_saveCustomerVehicleBtn" data-index=""
                       value="保存"/>
                <input type="button" class="query-btn J_cancelCustomerVehicleBtn" data-index=""
                       value="取消"/>
            </div>
        </bcgogo:hasPermission>
    </div>

    <%--<c:if test="${fourSShopVersions}">--%>
        <div class="member-right" style="width:450px;overflow-y: auto;height:55px;"
             id="customerVehicleOBDInfoEdit">
            <table width="100%" border="0" class="member-table" id="vehicleOBDTableEdit">
                <tr>
                    <th align="left">OBD/后视镜</th>
                    <th>&nbsp;</th>
                </tr>
                <tr>
                    <td class="test1">IMEI号：</td>
                    <td><input type="text" style="width:75px" maxlength="20" id="gsmObdImei"
                               name="gsmObdImei" value="${customerVehicle.gsmObdImei}" reset-value=""
                               value="" class="txt J_formreset imei_input"/>
                    </td>
                    <td class="test1">SIM卡号：</td>
                    <td><input type="text" style="width:75px" id="gsmObdImeiMoblie"
                               name="gsmObdImeiMoblie" value="${customerVehicle.gsmObdImeiMoblie}"
                               reset-value="" value="" class="txt J_formreset sim_no_input"/></td>
                </tr>
            </table>
        </div>
    <%--</c:if>--%>

    <div class="member-right"
         style="width:450px;overflow-y: auto;height:${fourSShopVersions?100:155}px;"
         id="customerVehicleAppointInfoEdit">
        <table width="100%" border="0" class="member-table J_vehicleAppointTable"
               id="appointServiceTableEdit">
            <tr>
                <th align="left">提醒服务</th>
                <th>&nbsp;</th>
            </tr>
            <tr>
                <td class="test1">保养里程：</td>
                <td><input type="text" style="width:75px" maxlength="6" id="maintainMileage"
                           name="maintainMileage" reset-value=""
                           value="" class="txt J_formreset"/>公里
                </td>
                <td class="test1">保险时间：</td>
                <td><input type="text" style="width:75px" onclick="showDatePicker(this);"
                           readonly="readonly"
                           id="bx" name="bx" reset-value=""
                           value="" class="txt J_formreset"/></td>
            </tr>
            <tr>
                <td class="test1">保养时间：</td>
                <td><input type="text" style="width:75px" onclick="showDatePicker(this);"
                           readonly="readonly"
                           id="by" name="by" reset-value=""
                           value="" class="txt J_formreset"/></td>
                <td class="test1">验车时间：</td>
                <td><input type="text" style="width:75px" onclick="showDatePicker(this);"
                           readonly="readonly"
                           id="yc" name="yc" reset-value=""
                           value="" class="txt J_formreset"/>
                    <a class="J_addAppointService" data-index=""><img src="images/opera2.png"/></a></td>
            </tr>

        </table>
    </div>
    <div class="clear"></div>
</form>

<div class="clear"></div>
</div>
<div class="clear i_height"></div>
</div>

</div>

</bcgogo:hasPermission>


<div id="outerdiv"
     style="position:fixed;top:0;left:0;background:rgba(0,0,0,0.7);z-index:3;width:100%;height:100%;display:none;">
    <div id="innerdiv" style="position:absolute;"><img id="bigimg" style="border:5px solid #fff;" src=""/></div>
</div>

<%--<c:if test="${fourSShopVersions}">--%>
    <div id="obd_bind_div" class="prompt_box" style="width:320px;display: none">
        <div class="content">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <input type="hidden" id="bind_vehicle_id"/>
                    <td align="right">绑定OBD/后视镜：</td>
                    <td><input type="text" class="txt imei_input" placeholder="IMEI号" style="width: 120px"/></td>
                    <td><input type="text" class="txt sim_no_input" placeholder="SIM卡号"/></td>
                </tr>

            </table>
            <div class="clear"></div>
            <div class="wid275" style="margin-left:50px;">
                <div class="addressList">
                    <div id="vehicle_bind_okBtn" class="search_btn">确 定</div>
                    <div id="vehicle_bind_cancelBtn" class="empty_btn">取 消</div>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>

    <div id="obd_edit_div" class="prompt_box" style="width:380px;display: none">

        <div class="content">
            <input type="hidden" id="edit_data_index"/>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td height="24" align="right">IMIE 号：</td>
                    <td><span class="imei_span"></span></td>
                </tr>
                <tr>
                    <td height="24" align="right">SIM卡号：</td>
                    <td><span class="sim_no_span"></span></td>
                </tr>
                <tr>
                    <td height="24" align="right">类型：</td>
                    <td>
                        <input class="change_radio" name="s_radio" type="radio" id="radio" value="radio"
                               checked="checked"/>
                        <label for="radio"></label>OBD/后视镜故障，更换OBD/后视镜
                    </td>
                </tr>
                <tr>
                    <td height="24" align="right">&nbsp;</td>
                    <td>
                        <input class="delete_radio" type="radio" name="s_radio" id="radio2" value="radio"/>
                        <label for="radio2"></label>登记错误，本车不需要安装OBD/后视镜
                    </td>
                </tr>
                <tr id="change_obd_tr">
                    <td height="24" align="right">更换OBD/后视镜信息：</td>
                    <td>
                        <input type="text" class="txt imei_input" style="width:120px; margin-right:5px;"/>
                        <input type="text" class="txt sim_no_input"/>
                    </td>
                </tr>
            </table>

            <div class="clear"></div>
            <div id="change_opr_div" class="wid275" style="margin-left:50px;display: none">
                <div class="addressList">
                    <div id="change_ok_opr_btn" class="search_btn">确 定</div>
                    <div class="cancel_opr_btn empty_btn">取 消</div>
                </div>
            </div>
            <div id="delete_opr_div" class="wid275" style="margin-left:50px;">
                <div class="addressList">
                    <div id="delete_ok_opr_btn" class="search_btn">清空信息</div>
                    <div class="cancel_opr_btn empty_btn">取 消</div>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
<%--</c:if>--%>


<%@include file="/customer/vehicle/vehicleMaintainRegister.jsp" %>

<%@ include file="/txn/appointOrder/appointOrderDialog.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
