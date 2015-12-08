<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户详情</title>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <%--<link rel="stylesheet" type="text/css" href="styles/uncleUser<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <!--add by zhuj-->
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
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
    <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <!--add by zhuj-->
    <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/page/txn/appointOrderList<%=ConfigController.getBuildVersion()%>.js"></script>

    <%@ include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>
    <style type="text/css">
        .customerSMS {
            background: url("images/phone.jpg") no-repeat scroll right center transparent;
            display: inline-block;
            height: 16px;
            margin: 3px 2px 0 5px;
            width: 22px;
            float: right;
        }
        .customerSMS:hover{
            background: url("images/hover_phone.png") no-repeat scroll right center transparent;
            height: 21px;
            margin: 1px 0 0 5px;
            width: 22px;
            float: right;
        }
    </style>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_DATA");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"客户详情");
        $(document).ready(function(){
            var identificationImageUploader, identificationImageUploaderView;

            $("#cancelIdentificationImageEditBtn").bind("click", function (e) {
                e.preventDefault();
                $("#identificationImageForm").find(".J_formreset").each(function () {
                    $(this).val(G.Lang.normalize($(this).attr("reset-value")));
                });

                identificationImageUploader.remove();
                identificationImageUploader = null;
                identificationImageUploaderView.remove();
                identificationImageUploaderView = null;
                $("#identificationImageShow").show();
                $("#identificationImageBtn").show();
                $("#identificationImageEdit").hide();
            });
            $("#saveIdentificationImageEditBtn").bind("click", function (e) {
                e.preventDefault();
                $('#identificationImageForm').ajaxSubmit(function (result) {
                    if (result.success) {
                        var liHtmls = '';
                        var customerIdentificationImageDetailDTOs = result.data;
                        $("input[id^='customerIdentificationImagePaths']").each(function (index) {
                            if (!G.Lang.isEmpty(customerIdentificationImageDetailDTOs[index])) {
                                $(this).val(customerIdentificationImageDetailDTOs[index].imagePath);
                                $(this).attr("reset-value", customerIdentificationImageDetailDTOs[index].imagePath);
                                liHtmls += ' <li>';
                                liHtmls += ' <div class="img"><img src="' + customerIdentificationImageDetailDTOs[index].imageURL + '"/></div>';
                                liHtmls += ' <div class="padding10">';
                                liHtmls += ' <input type="button" class="btn j_printCustomerIdentificationImage" data-index="' + index + '" value="打 印"/>';
                                liHtmls += ' </div>';
                                liHtmls += ' </li>';
                            } else {
                                $(this).val("");
                                $(this).attr("reset-value", "");
                            }
                        });

                        liHtmls += ' <div class="clear"></div>';
                        $("#identificationImageShow_ul").html(liHtmls);
                        identificationImageUploader.remove();
                        identificationImageUploader = null;
                        identificationImageUploaderView.remove();
                        identificationImageUploaderView = null;
                        $("#identificationImageShow").show();
                        $("#identificationImageBtn").show();
                        $("#identificationImageEdit").hide();
                    } else {
                        nsDialog.jAlert("保存图片失败！");
                    }
                });

            });
            $("#identificationImageBtn").bind("click", function (e) {
                e.preventDefault();
                $(this).hide();
                $("#identificationImageShow").hide();
                $("#identificationImageEdit").show();

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
                        nsDialog.jAlert("上传图片失败！");
                    },
                    "uploadAllCompleteCallback": function () {
                        //更新input
                        $("input[id^='customerIdentificationImagePaths']").each(function (index) {
                            $(this).val(G.Lang.normalize(identificationImagePathData[index], ""));
                        });
                        // 设置 视图组件  idle 状态
                        identificationImageUploaderView.setState("idle");
                        identificationImageUploaderView.update(getTotelUrlToData(identificationImagePathData));
                    }
                });

                /**
                 * 视图组建的 样例代码
                 * */
                identificationImageUploaderView = new App.Module.ImageUploaderView();
                identificationImageUploaderView.init({
                    // 你所需要注入的 dom 节点
                    selector: "#identificationImageUploaderView",
                    width: 880,
                    height: 160,
                    iWidth: 120,
                    iHeight: 100,
                    maxFileNum: 6,
                    // 当删除某张图片时会触发此回调
                    onDelete: function (event, data, index) {
                        identificationImageUploader.getFlashObject().deleteFile(index);

                        // 从已获得的图片数据池中删除 图片数据
                        identificationImagePathData.splice(index, 1);
                        //更新input
                        $("input[id^='customerIdentificationImagePaths']").each(function (index) {
                            $(this).val(G.Lang.normalize(identificationImagePathData[index], ""));
                        });
                    }
                });

                // 设置 视图组件  idle 状态
                identificationImageUploaderView.setState("idle");
                identificationImageUploaderView.update(getTotelUrlToData(identificationImagePathData));

                function getTotelUrlToData(inUrlData) {
                    var outData = [];
                    for (var i = 0; i < inUrlData.length; i++) {
                        var outDataItem = {
                            "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + inUrlData[i] + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.CUSTOMER_IDENTIFICATION_IMAGE_UPLOAD
                        };
                        outData.push(outDataItem);
                    }
                    return outData;
                };
            });

            $(".j_printCustomerIdentificationImage").live("click", function (e) {
                e.preventDefault();
                var imagePath = $("#customerIdentificationImagePaths" + $(this).attr("data-index")).val();
                if (!G.Lang.isEmpty(imagePath)) {
                    window.open("customer.do?method=printCustomerIdentificationImage&imagePath=" + imagePath);
                }
            });

//开始时间不能早于今天
            $("#fromTimeStr").bind("change",function(){
                //所选时间的0点时刻与当前时间、结束时间对比
                var startDateStr = $("#fromTimeStr").val();
                var endDateStr = $("#toTimeStr").val();
                var startDateLong;
                var endDateLong;
                if(startDateStr.length>0){
                    var year = startDateStr.substr(0,4);
                    var month = startDateStr.substr(5,2);
                    var day = startDateStr.substr(8,2);
                    var startDate = new Date(year+"/"+month+"/"+day);
                    startDateLong = startDate.getTime();
                    var nowDateLong = new Date().getTime();
                    if(startDateLong - nowDateLong > 0){
                        nsDialog.jAlert("开始时间不能晚于当前时间！",null,function(){
                            $("#fromTimeStr").val("");
                            return;
                        });
                    }
                    if(endDateStr.length>0){
                        var year = endDateStr.substr(0,4);
                        var month = endDateStr.substr(5,2);
                        var day = endDateStr.substr(8,2);
                        var endDate = new Date(year+"/"+month+"/"+day);
                        endDateLong = endDate.getTime();
                        if(startDateLong - endDateLong > 0){
                            nsDialog.jAlert("开始时间不能晚于结束时间！",null,function(){
                                $("#fromTimeStr").val("");
                                return;
                            });
                        }
                    }
                }
            });
            //结束时间不能晚于开始时间
            $("#toTimeStr").bind("change",function(){
                //所选时间的0点时刻与当前时间、结束时间对比
                var startDateStr = $("#fromTimeStr").val();
                var endDateStr = $("#toTimeStr").val();
                var startDateLong;
                var endDateLong;
                if(startDateStr.length>0 && endDateStr.length>0){
                    var year = startDateStr.substr(0,4);
                    var month = startDateStr.substr(5,2);
                    var day = startDateStr.substr(8,2);
                    var startDate = new Date(year+"/"+month+"/"+day);
                    startDateLong = startDate.getTime();

                    year = endDateStr.substr(0,4);
                    month = endDateStr.substr(5,2);
                    day = endDateStr.substr(8,2);
                    var endDate = new Date(year+"/"+month+"/"+day);
                    endDateLong = endDate.getTime();

                    if(startDateLong - endDateLong > 0){
                        nsDialog.jAlert("结束时间不能早于开始时间！",null,function(){
                            $("#toTimeStr").val("");
                            return;
                        });
                    }
                }
            });
            $("#fromTimeStr,#toTimeStr").datepicker({
                "numberOfMonths" : 1,
                "changeYear":true,
                "changeMonth":true,
                "dateFormat": "yy-mm-dd",
                "yearRange": "c-100, c",
                "yearSuffix":"",
                "showButtonPanel":true
            });
            $("#memberSearchBtn").click(function(){
                searchMemberConsumptionHistory();
            });
            <c:if test="${isMemberSwitchOn && not empty memberDTO}">
                searchMemberConsumptionHistory();
            </c:if>

        });


        //查询会员卡消费记录
        function searchMemberConsumptionHistory(){
            var customerId = $("#customerId").val();
            var fromTimeStr = $("#fromTimeStr").val();
            var toTimeStr = $("#toTimeStr").val();
            var customerName = $.trim($('#unitSpan').html());
            var data =  {
                startPageNo: 1,
                maxRows:5,
                customerId: customerId,
                customerName: customerName,
                startTimeStr: fromTimeStr,
                endTimeStr: toTimeStr
            };
            APP_BCGOGO.Net.asyncPost({
                url: "member.do?method=getSingleMemberConsume",
                data:data,
                cache: false,
                dataType: "json",
                success: function(jsonStr) {
                    initMemberConsumeHistoryByJson(jsonStr);
                    initPages(jsonStr, "dynamical7", "member.do?method=getSingleMemberConsume", '', "initMemberConsumeHistoryByJson", '', '', data, '');
                }
            });
        }

        function initMemberConsumeHistoryByJson(data) {
            $("#memberHistoryTable tr:not(:first)").remove();
            $("#member_record_count").text(data[1].totalRows);
            var totalAmount = 0;
            $.each(data[0], function(index, order) {
                var orderId = order.orderIdStr;
                var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
                var orderTypeValue = (!order.orderTypeValue ? "--" : order.orderTypeValue);
                var orderType = (!order.orderType ? "--" : order.orderType);
                var url = "";
                totalAmount += (order.amount==null?0:order.amount);
                if(orderType=="REPAIR"){
                    url = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + orderId;
                }else if(orderType=="WASH_BEAUTY"){
                    url = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + orderId;
                }else if(orderType=="SALE"){
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
<div class="cusTitle">客户详情</div>
<div class="titBodys">
    <a class="hover_btn" href="#" onclick="redirectUncleUser('customer')">客户详细信息</a>
    <a class="normal_btn" href="#" onclick="redirectCustomerBill('customer')">客户对账单</a>
    <div class="setting-relative">
        <div class="setting-absolute J_customerOptDetail" style="right: 94px;display: none">
            <ul>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <c:if test="${isMemberSwitchOn}">
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER.BUY_CARD">
                            <li><a class="default_a" style="cursor: pointer" onclick="selectCard()">购卡续卡</a></li>
                            <c:if test="${memberStatus=='ENABLED' || memberStatus =='PARTENABLED'}">
                                <li><a class="default_a" style="cursor: pointer" id="returnCardBtn" onclick="returnCard();">退卡</a></li>
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
                <li><a class="default_a" id="duizhan" style="cursor: pointer">财务对账</a></li>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DELETE">
                    <li><a class="default_a" id="deleteCustomerButton" onclick="deleteCustomer()" style="cursor: pointer">删除客户</a></li>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                    <li><a class="default_a" id="updateToShopBtn" onclick="updateToShop()" style="cursor: pointer">升级客户</a></li>
                </bcgogo:hasPermission>
            </ul>
        </div>
    </div>
    <div class="title-r" style="line-height:25px;"><a class="default_a" href="customer.do?method=customerdata">返回客户列表></a></div>
    <div class="setting J_customerOpt"><img src="images/setting_r2_c6.jpg" />操 作 </div>
</div>
<div class="i_mainRight" id="i_mainRight">
<input type="hidden" id="isUncleUser"/>
<input id="customerId" type="hidden" value="${customerId}"/>
<input id="customerName" type="hidden" value="${customerDTO.name}"/>
<input id="id" name="id" type="hidden" value="${customerDTO.id}"/>

<input type="hidden" value="" id="modifyAll">

<div class="booking-management">
<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="title-r"><a class="default_a" style="cursor: pointer" id="editCustomerInfo"><img src="images/edit.png"/> 编辑</a></div>
        </bcgogo:hasPermission>
        基本信息
    </div>

    <div class="clear"></div>
    <div class="customer" id="customerBasicInfoShow">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="7%"/>
                <col width="13%"/>
                <col width="7%"/>
                <col width="10%"/>
                <col width="7%"/>
                <col width="10%"/>
                <col width="7%"/>
                <col width="10%"/>
                <col width="7%"/>
                <col width="10%"/>
            </colgroup>
            <tr>
                <th>客户名称：</th>
                <td><span class="J_customerBasicSpan" data-key="name">${customerDTO.name}</span></td>
                <th>简称：</th>
                <td><span class="J_customerBasicSpan" data-key="shortName">${customerDTO.shortName}</span></td>
                <th>地址：</th>
                <td colspan="5"><span class="J_customerBasicSpan" data-key="address">${customerDTO.address}</span></td>
            </tr>
            <tr>
                <th>联系人：</th>
                <td><span class="J_customerBasicSpan" data-key="contact">${customerDTO.contact}</span></td>
                <th>座机：</th>
                <td><span class="J_customerBasicSpan" data-key="landLine">${customerDTO.landLine}</span></td>
                <th>手机：</th>
                <td>
                    <span class="J_customerBasicSpan" data-key="mobile" id="customerMobileSpan">${customerDTO.mobile}</span>
                    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
                        <a href="#" class="customerSMS" onclick='smsHistory($("#customerMobileSpan").text(),"${customerId}")'></a>
                    </bcgogo:hasPermission>
                </td>
                <th>传真：</th>
                <td><span class="J_customerBasicSpan" data-key="fax">${customerDTO.fax}</span></td>
                <th>EMAIL:</th>
                <td><span class="J_customerBasicSpan" data-key="email">${customerDTO.email}</span></td>
            </tr>
            <tr>
                <th>QQ：</th>
                <td><span class="J_customerBasicSpan" data-key="qq">${customerDTO.qq}</span></td>
                <th>生日：</th>
                <td><span class="J_customerBasicSpan" data-key="birthdayString">${customerDTO.birthdayString}</span></td>
                <th>客户类别：</th>
                <td><span class="J_customerBasicSpan" data-key="customerKindStr">${customerDTO.customerKindStr}</span></td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>
        </table>
    </div>
    <div class="customer" id="customerBasicInfoEdit" style="display: none;">
        <form id="customerBasicForm" method="post">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="7%"/>
                <col width="13%"/>
                <col width="7%"/>
                <col width="10%"/>
                <col width="7%"/>
                <col width="10%"/>
                <col width="7%"/>
                <col width="10%"/>
                <col width="7%"/>
                <col width="10%"/>
            </colgroup>
            <tr>
                <th>客户名称：</th>
                <td><input type="text" maxlength="50" id="name" name="name" reset-value="${customerDTO.name}" value="${customerDTO.name}" class="txt J_formreset"></td>
                <th>简称：</th>
                <td><input type="text" maxlength="50" id="shortName" name="shortName" reset-value="${customerDTO.shortName}" value="${customerDTO.shortName}" class="txt J_formreset"/></td>
                <th>地址：</th>
                <td colspan="5"><input type="text" maxlength="50" id="address" name="address" reset-value="${customerDTO.address}" value="${customerDTO.address}" class="txt J_formreset"/></td>
            </tr>
            <tr>
                <th>联系人：</th>
                <td>
                    <input type="text" maxlength="50" id="contacts[0].name" name="contacts[0].name" reset-value="${customerDTO.contact}" value="${customerDTO.contact}" class="txt J_formreset"/>
                    <input type="hidden" id="contacts[0].mainContact" name="contacts[0].mainContact" value="1"/>
                    <input type="hidden" class="J_formreset" id="contacts[0].id" name="contacts[0].id" reset-value="${customerDTO.contactId}" value="${customerDTO.contactId}" />
                </td>
                <th>座机：</th>
                <td><input type="text" maxlength="20" id="landLine" name="landLine" reset-value="${customerDTO.landLine}" value="${customerDTO.landLine}" class="txt J_formreset"/></td>
                <th>手机：</th>
                <td><input type="text" maxlength="11" id="contacts[0].mobile" name="contacts[0].mobile" reset-value="${customerDTO.mobile}" value="${customerDTO.mobile}" class="txt J_formreset"/></td>
                <th>传真：</th>
                <td><input type="text" maxlength="20" id="fax" name="fax" reset-value="${customerDTO.fax}" value="${customerDTO.fax}" class="txt J_formreset"/></td>
                <th>EMAIL：</th>
                <td><input type="text" maxlength="50" id="contacts[0].email" name="contacts[0].email" reset-value="${customerDTO.email}" value="${customerDTO.email}" class="txt J_formreset"/></td>
            </tr>
            <tr>
                <th>QQ：</th>
                <td><input type="text" maxlength="10" id="contacts[0].qq" name="contacts[0].qq" reset-value="${customerDTO.qq}" value="${customerDTO.qq}" class="txt J_formreset"/></td>
                <th>生日：</th>
                <td><input type="text" readonly="true" id="birthdayString" name="birthdayString" reset-value="${customerDTO.birthdayString}" value="${customerDTO.birthdayString}" class="txt J_formreset"/></td>
                <th>客户类别：</th>
                <td>
                    <select style="height:21px;width: 94%;" name="customerKind" id="customerKind" reset-value="${customerDTO.customerKind}" class="txt J_formreset">
                        <c:forEach items="${customerTypeMap}" var="customerType" varStatus="status">
                            <option value="${customerType.key}" ${customerType.key eq customerDTO.customerKind?'selected':''}>${customerType.value}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>

        </table>
        </form>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="padding10">
                <input type="button"  class="query-btn" id="saveCustomerBasicBtn" value="确认"/>
                <input type="button"  class="query-btn" id="cancelCustomerBasicBtn" value="取消"/>
            </div>
        </bcgogo:hasPermission>

        <div class="clear"></div>
    </div>
</div>
<div class="clear i_height"></div>
<div class="shelvesed clear"  style="width:600px">
    <div class="topTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="title-r"><a class="default_a" style="cursor: pointer" id="editCustomerAccountInfo"><img src="images/edit.png"/> 编辑</a></div>
        </bcgogo:hasPermission>
        账户信息
    </div>
    <div class="customer" style="border:0" id="customerAccountInfoShow">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="18%"/>
                <col width="30%"/>
                <col width="18%"/>
                <col width="30%"/>
            </colgroup>
            <tr>
                <th>开户行：</th>
                <td><span class="J_customerAccountSpan" data-key="bank">${customerDTO.bank}</span></td>
                <th>开户名：</th>
                <td><span class="J_customerAccountSpan" data-key="bankAccountName">${customerDTO.bankAccountName}</span></td>
            </tr>
            <tr>
                <th>账号：</th>
                <td><span class="J_customerAccountSpan" data-key="account">${customerDTO.account}</span></td>
                <th>结算类型：</th>
                <td><span class="J_customerAccountSpan" data-key="settlementTypeStr">${customerDTO.settlementTypeStr}</span></td>
            </tr>
            <tr>
                <th>发票类型：</th>
                <td><span class="J_customerAccountSpan" data-key="invoiceCategoryStr">${customerDTO.invoiceCategoryStr}</span></td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <th>备注：</th>
                <td colspan="3"><span class="J_customerAccountSpan" data-key="memo">${customerDTO.memo}</span></td>
            </tr>
        </table>
    </div>
    <div class="customer" style="border:0;display: none" id="customerAccountInfoEdit">
        <form id="customerAccountForm" method="post">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="18%"/>
                <col width="30%"/>
                <col width="18%"/>
                <col width="30%"/>
            </colgroup>
            <tr>
                <th>开户行：</th>
                <td><input type="text" maxlength="20" id="bank" name="bank" reset-value="${customerDTO.bank}" value="${customerDTO.bank}" class="txt J_formreset"/></td>
                <th>开户名：</th>
                <td><input type="text" maxlength="20" id="bankAccountName" name="bankAccountName" reset-value="${customerDTO.bankAccountName}" value="${customerDTO.bankAccountName}" class="txt J_formreset"/></td>
            </tr>
            <tr>
                <th>账号：</th>
                <td><input type="text" maxlength="20" id="account" name="account" reset-value="${customerDTO.account}" value="${customerDTO.account}" class="txt J_formreset"/></td>
                <th>结算类型：</th>
                <td>
                    <select style="height:21px;width: 95%;" name="settlementType" id="settlementType" reset-value="${customerDTO.settlementType}" class="txt J_formreset">
                        <c:forEach items="${settlementTypeMap}" var="settlementType" varStatus="status">
                            <option value="${settlementType.key}" ${settlementType.key eq customerDTO.settlementType?'selected':''}>${settlementType.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>发票类型：</th>
                <td>
                    <select style="height:21px;width: 95%;" name="invoiceCategory" id="invoiceCategory" reset-value="${customerDTO.invoiceCategory}" class="txt J_formreset">
                        <c:forEach items="${invoiceCatagoryMap}" var="invoiceCategory" varStatus="status">
                            <option value="${invoiceCategory.key}" ${invoiceCategory.key eq customerDTO.invoiceCategory?'selected':''}>${invoiceCategory.value}</option>
                        </c:forEach>
                    </select>
                </td>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <th>备注：</th>
                <td colspan="3"><input type="text" maxlength="250" id="memo" name="memo" reset-value="${customerDTO.memo}" value="${customerDTO.memo}" class="txt J_formreset"/></td>
            </tr>
        </table>
        </form>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="padding10">
                <input type="button"  class="query-btn" id="saveCustomerAccountBtn" value="确认"/>
                <input type="button"  class="query-btn" id="cancelCustomerAccountBtn" value="取消"/>
            </div>
        </bcgogo:hasPermission>
        <div class="clear"></div>
    </div>
</div>

<div class="shelvesed shelves"  style="width:381px">
    <div class="topTitle" style="text-align:left;color: #444443">
        <div class="record-relative">
            <div class="record-absolute"><a href="#" class="J_customerConsumeHistory">点击这里可查看消费记录</a></div>
        </div>
        <div class="title-r"><a class="default_a" style="cursor: pointer" id="customerConsumerHistoryBtn">历史消费></a></div>
        消费信息</div>
    <div class="customer" style="border:0">
        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="28%"/>
                <col width="22%"/>
                <col width="28%"/>
                <col width="22%"/>
            </colgroup>
            <tr>
                <th>累计消费：</th>
                <td>${customerDTO.totalAmount}元</td>
                <th>累计销售退货：</th>
                <td>${customerDTO.totalReturnAmount}元 </td>
            </tr>
            <tr>
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
                        <th>应收：</th>
                        <td>${customerDTO.totalReceivable}元</td>
                        <th>应付：</th>
                        <td>${customerDTO.totalReturnDebt}元</td>
                    </bcgogo:if>
                    <bcgogo:else>
                        <th>应付：</th>
                        <td>${customerDTO.totalReturnDebt}元</td>
                        <th>&nbsp;</th>
                        <td>&nbsp;</td>
                    </bcgogo:else>
                </bcgogo:permission>
            </tr>
            <tr>
                <th>&nbsp;</th>
                <td>&nbsp;</td>
                <th>&nbsp;</th>
                <td align="right"><div id="duizhang" style="cursor: pointer" class="title-r default_a">对账 &gt;</div></td>
            </tr>
        </table>
    </div>
</div>
<div class="clear i_height"></div>
<c:if test="${isMemberSwitchOn && not empty memberDTO}">
    <div class="titBody">
        <div class="lineTitle" style="text-align:left;color: #444443">
            <div class="title-r"></div>
            会员信息 </div>
        <div class="clear"></div>
        <div class="customer">
            <div class="member-left">
                <input type="hidden" id="memberId" value="${memberDTO.id}"/>
                <table width="100%" border="0" class="order-table">
                    <colgroup>
                        <col width="12%"/>
                        <col width="15%"/>
                        <col width="12%"/>
                        <col width="15%"/>
                        <col width="12%"/>
                        <col width="15%"/>
                    </colgroup>
                    <tr>
                        <th>会员级别：</th>
                        <td>${memberDTO.type}</td>
                        <th>会员卡号：</th>
                        <td>${memberDTO.memberNo}</td>
                        <th>入会日期：</th>
                        <td>${memberDTO.joinDateStr}</td>
                    </tr>
                    <tr>
                        <th>失效日期：</th>
                        <td>${memberDTO.serviceDeadLineStr}</td>
                        <th>会员会龄：</th>
                        <td>${memberDTO.dateKeep}</td>
                        <th>状态：</th>
                        <td>${memberStatus.status}</td>
                    </tr>
                    <tr>
                        <th>储蓄卡余额：</th>
                        <td>${memberDTO.balance}元</td>
                        <th>累计消费：</th>
                        <td>${memberDTO.memberConsumeTotal}元</td>
                        <th>&nbsp;</th>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <th>&nbsp;</th>
                        <td>&nbsp;</td>
                        <th>&nbsp;</th>
                        <td>&nbsp;</td>
                        <th>&nbsp;</th>
                        <td align="right"><div class="title-r"><a class="default_a" id="changePassword" style="cursor: pointer;">修改密码 &gt;</a></div></td>
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
                                <label><input type="checkbox" name="sendSms" value="true" id="sendSms"/>发送短信</label>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="member-right">
                <table width="100%" border="0" class="member-table">
                    <colgroup>
                        <col width="30%"/>
                        <col width="30%"/>
                        <col width="30%"/>
                    </colgroup>
                    <tr>
                        <th>项目名称</th>
                        <th>剩余次数</th>
                        <th>失效日期</th>
                    </tr>
                    <c:if test="${not empty memberDTO.memberServiceDTOs}">
                        <c:forEach items="${memberDTO.memberServiceDTOs}" var="memberServiceDTO">
                            <tr>
                                <td>${memberServiceDTO.serviceName}</td>
                                <td>${memberServiceDTO.timesStr} </td>
                                <td>${memberServiceDTO.deadlineStr}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                </table>
            </div>
            <div class="clear"></div>
        </div>
    </div>
    <div class="clear i_height"></div>

    <div class="titBody">
        <div class="lineTitle" style="text-align:left;color: #444443">会员卡消费历史记录查询</div>
        <div class="lineBody bodys">
            <div style="width: 100%">
                <div style="float: left; width: 30px; vertical-align: middle; height: 22px; line-height: 22px;">时间</div>
                <div style="float: left">
                    <input type="text" class="txt" readonly="readonly" id="fromTimeStr" name="toTimeStr"/>
                    &nbsp;到&nbsp;
                    <input type="text" class="txt" id="toTimeStr" name="toTimeStr"/>
                </div>
                <div style="float: left;clear:none;margin: 0;padding: 0" class="divTit button_conditon button_search"><a class="button" id="memberSearchBtn">查 询</a> </div>
            </div>
            <div class="cuSearch">
                <div class="gray-radius" style="margin:0;">
                    <div class="more_his uncle_cont clear" style="font-size: 13px; margin-bottom: 4px;">
                        会员卡消费历史记录：<span class="hover" id="member_record_count">0</span>条&nbsp;&nbsp;
                        会员卡累计消费：¥<span class="hover" id="memberConsumeAll" style="margin-left:3px;margin-right:3px;">0</span>元
                    </div>
                    <table class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;" id="memberHistoryTable">
                        <colgroup>
                            <col width="40px"/>
                            <col width="100px"/>
                            <col width="100px"/>
                            <col width="100px"/>
                            <col width="100px"/>
                            <col width="100px"/>
                            <col width="100px"/>
                            <col width="200px"/>
                            <col width="120px"/>
                        </colgroup>
                        <tr class="titleBg">
                            <td style="border-left:none;padding-left:10px;">NO</td>
                            <td>消费时间</td>
                            <td>单据号</td>
                            <td>单据类型</td>
                            <td>车牌</td>
                            <td>会员号</td>
                            <td>会员级别</td>
                            <td>消费金额</td>
                            <td>消费方式</td>
                        </tr>
                        <tr class="space">
                            <td colspan="9"></td>
                        </tr>
                    </table>
                    <div class="clear i_height"></div>
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="member.do?method=getSingleMemberConsume"></jsp:param>
                        <jsp:param name="data" value="{startPageNo:1,customerId:$('#customerId').val()}"></jsp:param>
                        <jsp:param name="jsHandleJson" value="initMemberConsumeHistoryByJson"></jsp:param>
                        <jsp:param name="dynamical" value="dynamical7"></jsp:param>
                        <jsp:param name="display" value="none"></jsp:param>
                    </jsp:include>
                    <div class="clear i_height"></div>
                </div>
                <div class="clear i_height"></div>
            </div>
        </div>
        <div class="lineBottom"></div>
    </div>
    <div class="clear i_height"></div>
</c:if>

<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div class="title-r"><a class="default_a" style="cursor: pointer" id="addCustomerVehicleBtn"><img src="images/car_add.png"/> 新增车辆></a></div>
        </bcgogo:hasPermission>
        车辆管理 </div>
    <div class="clear"></div>
    <div class="customer" id="customerVehicleContainer">
        <c:if test="${not empty customerVehicleResponseList}">
            <c:forEach items="${customerVehicleResponseList}" var="customerVehicle" varStatus="status">
                <div id="customerVehicleDiv${status.index}" class="J_customerVehicleDiv" data-index="${status.index}">
                    <div class="padding5 ${status.index%2==0?'':'gray-bg'}" id="customerVehicleInfoShow${status.index}">
                        <div class="member-left">
                            <table width="100%" border="0" class="order-table">
                                <colgroup>
                                    <col width="12%"/>
                                    <col width="15%"/>
                                    <col width="12%"/>
                                    <col width="15%"/>
                                    <col width="12%"/>
                                    <col width="20%"/>
                                </colgroup>
                                <tr>
                                    <th>车牌号：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="licenceNo">${customerVehicle.licenceNo}</span></td>
                                    <th>联系人：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="contact">${customerVehicle.contact}</span></td>
                                    <th>联系方式：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="mobile">${customerVehicle.mobile}</span></td>
                                </tr>
                                <tr>
                                    <th>车辆品牌：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="brand">${customerVehicle.brand}</span></td>
                                    <th>车型：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="model">${customerVehicle.model}</span></td>
                                    <th>年代：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="year">${customerVehicle.year}</span></td>
                                </tr>
                                <tr>
                                    <th>排量：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="engine">${customerVehicle.engine}</span></td>
                                    <th>购买日期：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="carDateStr">${customerVehicle.carDateStr}</span></td>
                                    <th>车架号：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="vin">${customerVehicle.vin}</span></td>
                                </tr>
                                <tr>
                                    <th>发动机号：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="engineNo">${customerVehicle.engineNo}</span></td>
                                    <th>当前里程：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="obdMileage">${customerVehicle.obdMileageStr}</span></td>
                                    <th>车身颜色：</th>
                                    <td><span class="J_customerVehicleSpan" data-key="color">${customerVehicle.color}</span></td>
                                </tr>
                            </table>
                        </div>
                        <h1 style="width:32%; float:right;">
                            <div class="title-r">
                                <div class="title-r"><a style="cursor: pointer" data-index="${status.index}" class="default_a J_CustomerVehicleConsumerHistory">消费历史</a></div>
                                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                                    <div class="title-r"><a style="cursor: pointer" data-index="${status.index}" class="default_a J_deleteCustomerVehicle">删除</a></div>
                                    <div class="title-r"><a style="cursor: pointer" data-index="${status.index}" class="default_a J_editCustomerVehicle">编辑</a></div>
                                </bcgogo:hasPermission>
                            </div>
                            下次预约服务</h1>
                        <div class="member-right" style="overflow-y: auto">
                            <table width="100%" border="0" class="member-table" id="appointServiceTableShow${status.index}">
                                <colgroup>
                                    <col width="25%"/>
                                    <col width="20%"/>
                                    <col width="25%"/>
                                    <col width="20%"/>
                                </colgroup>
                                <tr>
                                    <th align="right">保养里程:</th><td align="left"><span class="J_customerVehicleSpan" data-key="maintainMileage">${customerVehicle.maintainMileage}</span><span class="J_maintainMileageUnitSpan" style="width: 30px;float: right;display: ${empty customerVehicle.maintainMileage?'none':''}">公里</span></td>
                                    <th align="right">保养时间:</th><td align="left"><span class="J_customerVehicleSpan" data-key="maintainTimeStr">${customerVehicle.maintainTimeStr}</span></td>
                                </tr>
                                <tr>
                                    <th align="right">保险时间:</th><td align="left"><span class="J_customerVehicleSpan" data-key="insureTimeStr">${customerVehicle.insureTimeStr}</span></td>
                                    <th align="right">验车时间:</th><td align="left"><span class="J_customerVehicleSpan" data-key="examineTimeStr">${customerVehicle.examineTimeStr}</span></td>
                                </tr>
                                <c:if test="${not empty customerVehicle.appointServiceDTOs}">
                                    <c:forEach items="${customerVehicle.appointServiceDTOs}" var="appointServiceDTO" varStatus="appointStatus">
                                        <c:if test="${appointStatus.index%2==0}">
                                            <tr class="J_appointServiceTrShow">
                                        </c:if>
                                        <th align="right">${appointServiceDTO.appointName}:</th><td align="left">${appointServiceDTO.appointDate}</td>
                                        <c:if test="${appointStatus.index%2==0 && appointStatus.last}">
                                            <th></th><td></td>
                                        </c:if>
                                        <c:if test="${appointStatus.index%2==1 || appointStatus.last}">
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                            </table>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="padding5 ${status.index%2==0?'':'gray-bg'}" id="customerVehicleInfoEdit${status.index}" style="display: none;">
                        <form action="customer.do?method=ajaxAddOrUpdateCustomerVehicle" id="customerVehicleForm${status.index}" method="post">
                            <input type="hidden" id="vehicleId${status.index}" name="vehicleId" value="${customerVehicle.vehicleId}"/>
                            <input type="hidden" id="startMileage${status.index}" name="startMileage" value="${customerVehicle.startMileage}"/>
                            <input type="hidden" name="customerId" value="${customerId}"/>
                            <div class="member-left">
                                <table width="100%" border="0" class="order-table">
                                    <colgroup>
                                        <col width="12%"/>
                                        <col width="15%"/>
                                        <col width="12%"/>
                                        <col width="15%"/>
                                        <col width="12%"/>
                                        <col width="20%"/>
                                    </colgroup>
                                    <tr>
                                        <th>车牌号：</th>
                                        <td><input type="text" maxlength="10" id="licenceNo${status.index}" name="licenceNo" reset-value="${customerVehicle.licenceNo}" value="${customerVehicle.licenceNo}" class="txt J_formreset"/></td>
                                        <th>联系人：</th>
                                        <td><input type="text" maxlength="20" id="contact${status.index}" name="contact" reset-value="${customerVehicle.contact}" value="${customerVehicle.contact}" class="txt J_formreset"/></td>
                                        <th>联系方式：</th>
                                        <td><input type="text" maxlength="11" id="mobile${status.index}" name="mobile" reset-value="${customerVehicle.mobile}" value="${customerVehicle.mobile}" class="txt J_formreset J_customerVehicleMobile"/></td>
                                    </tr>
                                    <tr>
                                        <th>车辆品牌：</th>
                                        <td><input type="text" maxlength="10" id="brand${status.index}" name="brand" reset-value="${customerVehicle.brand}" value="${customerVehicle.brand}" class="txt J_formreset"/></td>
                                        <th>车型：</th>
                                        <td><input type="text" maxlength="10" id="model${status.index}" name="model" reset-value="${customerVehicle.model}" value="${customerVehicle.model}" class="txt J_formreset"/></td>
                                        <th>年代：</th>
                                        <td><input type="text" maxlength="4" id="year${status.index}" name="year" reset-value="${customerVehicle.year}" value="${customerVehicle.year}" class="txt J_formreset"/></td>
                                    </tr>
                                    <tr>
                                        <th>排量：</th>
                                        <td><input type="text" maxlength="6" id="engine${status.index}" name="engine" reset-value="${customerVehicle.engine}" value="${customerVehicle.engine}" class="txt J_formreset"/></td>
                                        <th>购买日期：</th>
                                        <td><input type="text" onclick="showDatePicker(this);" maxlength="10" readonly="readonly" id="dateString${status.index}" name="dateString" reset-value="${customerVehicle.carDateStr}" value="${customerVehicle.carDateStr}" class="J_customerVehicleBuyDate txt J_formreset"/></td>
                                        <th>车架号：</th>
                                        <td><input type="text" maxlength="17" id="vin${status.index}" name="vin" reset-value="${customerVehicle.vin}" value="${customerVehicle.vin}" class="txt J_formreset chassisNumber" style="text-transform:uppercase;"/></td>
                                    </tr>
                                    <tr>
                                        <th>发动机号：</th>
                                        <td><input type="text" maxlength="20" id="engineNo${status.index}" name="engineNo" reset-value="${customerVehicle.engineNo}" value="${customerVehicle.engineNo}" class="txt J_formreset"/></td>
                                        <th>当前里程：</th>
                                        <td><input type="text" maxlength="10" id="obdMileage${status.index}" name="obdMileage" reset-value="${customerVehicle.obdMileageStr}" value="${customerVehicle.obdMileageStr}" class="txt J_formreset"/></td>
                                        <th>车身颜色：</th>
                                        <td><input type="text" maxlength="10" id="color${status.index}" name="color" reset-value="${customerVehicle.color}" value="${customerVehicle.color}" class="txt J_formreset"/></td>
                                    </tr>
                                </table>
                                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                                    <div class="padding10">
                                        <input type="button"  class="query-btn J_saveCustomerVehicleBtn" data-index="${status.index}" value="确认"/>
                                        <input type="button"  class="query-btn J_cancelCustomerVehicleBtn" data-index="${status.index}" value="取消"/>
                                    </div>
                                </bcgogo:hasPermission>
                            </div>
                            <h1 style="width:32%; float:right;"> 下次预约服务</h1>
                            <div class="member-right">
                                <table width="100%" border="0" class="member-table" id="appointServiceTableEdit${status.index}">
                                    <colgroup>
                                        <col width="25%"/>
                                        <col width="20%"/>
                                        <col width="25%"/>
                                        <col width="20%"/>
                                    </colgroup>
                                    <tr>
                                        <th align="right">保养里程：</th>
                                        <td><input type="text" style="width:50px" maxlength="6" id="maintainMileage${status.index}" name="maintainMileage" reset-value="${customerVehicle.maintainMileage}" value="${customerVehicle.maintainMileage}" class="txt J_formreset"/>公里</td>
                                        <th align="right">保养时间：</th>
                                        <td><input type="text" style="width:75px" onclick="showDatePicker(this);"  readonly="readonly" id="by${status.index}" name="by" reset-value="${customerVehicle.maintainTimeStr}" value="${customerVehicle.maintainTimeStr}" class="txt J_formreset"/></td>
                                    </tr>
                                    <tr>
                                        <th align="right">保险时间：</th>
                                        <td><input type="text"  style="width:75px" onclick="showDatePicker(this);" readonly="readonly" id="bx${status.index}" name="bx" reset-value="${customerVehicle.insureTimeStr}" value="${customerVehicle.insureTimeStr}" class="txt J_formreset"/></td>
                                        <th align="right">验车时间：</th>
                                        <td><input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly" id="yc${status.index}" name="yc" reset-value="${customerVehicle.examineTimeStr}" value="${customerVehicle.examineTimeStr}" class="txt J_formreset"/></td>
                                    </tr>
                                    <c:if test="${not empty customerVehicle.appointServiceDTOs}">
                                        <c:forEach items="${customerVehicle.appointServiceDTOs}" var="appointServiceDTO" varStatus="appointStatus">
                                            <c:if test="${appointStatus.index%2==0}">
                                                <tr class="J_appointServiceTrEdit">
                                            </c:if>
                                            <th align="right">${appointServiceDTO.appointName}:</th>
                                            <td align="left">
                                                <input type="hidden" name="appointServiceDTOs[${appointStatus.index}].id" value="${appointServiceDTO.id}">
                                                <input type="hidden" name="appointServiceDTOs[${appointStatus.index}].appointName" value="${appointServiceDTO.appointName}">
                                                <input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly" name="appointServiceDTOs[${appointStatus.index}].appointDate" reset-value="${appointServiceDTO.appointDate}" value="${appointServiceDTO.appointDate}" class="txt J_formreset"/>
                                            </td>
                                            <c:if test="${appointStatus.index%2==0 && appointStatus.last}">
                                                <th>${appointStatus.index%2}</th><td>${appointStatus.last}</td>
                                            </c:if>
                                            <c:if test="${appointStatus.index%2==1 || appointStatus.last}">
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </table>
                            </div>
                            <div class="clear"></div>
                        </form>
                    </div>
                    <div class="clear i_height"></div>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>

<div class="clear i_height"></div>
<div class="titBody">
    <div class="lineTitle" style="text-align:left;color: #444443">预约服务</div>
    <div class="lineBody bodys">
        <form id="appointOrderListForm">
            <input type="hidden" id="customerIds" value="${customerId}">
            <input type="hidden" id="maxRows" value="5">
            <table  border="0" cellpadding="0" cellspacing="0" class="order-management">
                <tr>
                    <td>车牌号</td>
                    <td colspan="2"><input type="text" class="txt txt-long" id="vehicleNo" name="vehicleNo" maxlength="10"/></td>
                    <td>单据号</td>
                    <td><input type="text" class="txt txt-long" id="receiptNo" name="receiptNo" maxlength="20"/></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>预约方式</td>
                    <td><select class="txt txt_color" id="appointWay" name="appointWay">
                        <option value="">所有</option>
                        <option value="APP">在线预约</option>
                        <option value="SHOP">现场预约</option>
                        <option value="PHONE">电话预约</option>
                    </select></td>
                    <td>状态
                        <select class="txt txt_color" id="appointOrderStatus" name="appointOrderStatus">
                            <option value="">所有</option>
                            <option value="PENDING">待确认</option>
                            <option value="ACCEPTED">已接受</option>
                            <option value="TO_DO_REPAIR">待施工</option>
                            <option value="HANDLED">已施工</option>
                            <option value="REFUSED">已拒绝</option>
                            <option value="CANCELED">已取消</option>
                        </select>
                    </td>
                    <td>下单时间</td>
                    <td>
                        <input type="text" class="txt" id="createTimeStartStr" name="createTimeStartStr"/>
                        &nbsp;到&nbsp;
                        <input type="text" class="txt" id="createTimeEndStr" name="createTimeEndStr"/>
                    </td>
                    <td>预计时间</td>
                    <td>
                        <input type="text" class="txt" id="appointTimeStartStr" name="appointTimeStartStr"/>
                        &nbsp;到&nbsp;
                        <input type="text" class="txt" id="appointTimeEndStr" name="appointTimeEndStr"/>
                    </td>
                </tr>
            </table>
            <table width="100%" border="0" style="line-height:20px;">
                <tr>
                    <td width="8%" valign="top">服务类型</td>
                    <td style="word-break:break-all">
                        <span class="margin-right">
                            <label class="lbl">
                                <input style="vertical-align: middle;" id="allServiceCategory" type="checkbox"/>
                                所有
                            </label>
                        </span>
                        <c:forEach items="${serviceScope}" var="item">
                                <span class="margin-right">
                                <label class="lbl">
                                    <input style="vertical-align: middle;" name="serviceCategoryIds" type="checkbox" value="${item.key}"/>
                                        ${item.value}
                                </label>
                                </span>
                        </c:forEach>
                    </td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div class="divTit button_conditon button_search"> <a class="blue_color clean" id="clearSearchCondition" myCustomerDetail="true">清空条件</a> <a class="button" id="searchAppointOrderBtn">查 询</a> </div>
        </form>
        <div class="cuSearch">
            <div class="gray-radius" style="margin:0;">
                <table class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;" id="appointOrderListTb">
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
                                   data='{startPageNo:1,maxRows:5,customerIds:\"${customerId}\"}' postFn="drawAppointOrderList"/>
                <div class="clear i_height"></div>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
    <div class="lineBottom"></div>
</div>
<div class="clear i_height"></div>
<div class="titBody">
    <form action="customer.do?method=saveCustomerIdentificationImageRelation" id="identificationImageForm" method="post">
    <input type="hidden" name="customerId" value="${customerId}"/>
    <div class="lineTitle" style="text-align:left;color: #444443">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
        <div class="title-r"><a class="default_a" style="cursor: pointer;" id="identificationImageBtn"><img src="images/edit.png"/> 编辑</a></div>
        </bcgogo:hasPermission>
        证件照片 </div>
    <div class="clear"></div>
    <div class="customer" id="identificationImageShow">
        <div class="photo-upload">
            <ul id="identificationImageShow_ul">
                <c:if test="${not empty customerDTO.imageCenterDTO && not empty customerDTO.imageCenterDTO.customerIdentificationImageDetailDTOs}">
                    <c:forEach items="${customerDTO.imageCenterDTO.customerIdentificationImageDetailDTOs}" var="customerIdentificationImageDetailDTO" varStatus="status">
                        <li>
                            <div class="img"><img src="${customerIdentificationImageDetailDTO.imageURL}"/></div>
                            <div class="padding10">
                                <input type="button" class="btn j_printCustomerIdentificationImage" data-index="${status.index}" value="打 印"/>
                            </div>
                        </li>
                    </c:forEach>
                </c:if>
                <div class="clear"></div>
            </ul>
        </div>
    </div>
    <c:set var="customerIdentificationImageCount" value="0"/>
    <c:if test="${not empty customerDTO.imageCenterDTO && not empty customerDTO.imageCenterDTO.customerIdentificationImageDetailDTOs}">
        <c:forEach items="${customerDTO.imageCenterDTO.customerIdentificationImageDetailDTOs}" var="customerIdentificationImageDetailDTO" varStatus="status">
            <input type="hidden" class="J_formreset" id="customerIdentificationImagePaths${status.index}"  name="customerIdentificationImagePaths" reset-value="${customerIdentificationImageDetailDTO.imagePath}" value="${customerIdentificationImageDetailDTO.imagePath}">
            <c:set var="customerIdentificationImageCount" value="${customerIdentificationImageCount+1}"/>
        </c:forEach>
    </c:if>
    <c:forEach begin="${customerIdentificationImageCount}" end="5" step="1" varStatus="status">
        <input type="hidden" class="J_formreset" id="customerIdentificationImagePaths${status.index}" name="customerIdentificationImagePaths" value="">
    </c:forEach>

    <div class="customer" id="identificationImageEdit" style="display: none">
        <div class="storePhotos" style="width:auto">
            <div class="upload" style="margin-left: 10px">选择本地图片：</div>
            <div style="margin:10px;float: left; height: 70px;" id="identificationImageUploader"></div>
            <div class="tip-content" style="margin:10px;float: left; height: 70px;">
                <div class="left">提示：</div>
                <div class="right"> 1. 如果您要发布相关的图片，请点击以上按钮。<br />
                    2. 所选图片都必须是 jpeg、jpg和png 格式。 <br />
                    3. 每张图片的大小不得超过5M。 <br />
                    4. 您可以一次选择多张图片，最多上传 6 张图片。 </div>
            </div>
            <div class="clear i_height"></div>
            <div id="identificationImageUploaderView" style="width:auto;height:180px;position: relative;margin-left: 10px;"></div>
        </div>
        <div class="padding10" style="width:auto;">
            <input type="button"  class="query-btn" id="saveIdentificationImageEditBtn" value="确认"/>
            <input type="button"  class="query-btn" id="cancelIdentificationImageEditBtn" value="取消"/>
        </div>
        <div class="clear i_height"></div>
    </div>
    </form>
</div>
<div class="clear i_height"></div>
<div class="shopping_btn">
    <div class="divImg" id="returnCustomerListBtn">
        <img src="images/return.png" />
        <div class="sureWords" style="font-size:12px">返回客户列表</div>
    </div>
</div>
</div>
<div class="clear i_height"></div>
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
            <div class="padding5 gray-bg" id="customerVehicleInfoShow" style="display: none;">
                <div class="member-left">
                    <table width="100%" border="0" class="order-table">
                        <colgroup>
                            <col width="12%"/>
                            <col width="15%"/>
                            <col width="12%"/>
                            <col width="15%"/>
                            <col width="12%"/>
                            <col width="15%"/>
                        </colgroup>
                        <tr>
                            <th>车牌号：</th>
                            <td><span class="J_customerVehicleSpan" data-key="licenceNo"></span></td>
                            <th>联系人：</th>
                            <td><span class="J_customerVehicleSpan" data-key="contact"></span></td>
                            <th>联系方式：</th>
                            <td><span class="J_customerVehicleSpan" data-key="mobile"></span></td>
                        </tr>
                        <tr>
                            <th>车辆品牌：</th>
                            <td><span class="J_customerVehicleSpan" data-key="brand"></span></td>
                            <th>车型：</th>
                            <td><span class="J_customerVehicleSpan" data-key="model"></span></td>
                            <th>年代：</th>
                            <td><span class="J_customerVehicleSpan" data-key="year"></span></td>
                        </tr>
                        <tr>
                            <th>排量：</th>
                            <td><span class="J_customerVehicleSpan" data-key="engine"></span></td>
                            <th>购买日期：</th>
                            <td><span class="J_customerVehicleSpan" data-key="carDateStr"></span></td>
                            <th>车架号：</th>
                            <td><span class="J_customerVehicleSpan" data-key="vin"></span></td>
                        </tr>
                        <tr>
                            <th>发动机号：</th>
                            <td><span class="J_customerVehicleSpan" data-key="engineNo"></span></td>
                            <th>当前里程：</th>
                            <td><span class="J_customerVehicleSpan" data-key="obdMileage"></span></td>
                            <th>车身颜色：</th>
                            <td><span class="J_customerVehicleSpan" data-key="color"></span></td>
                        </tr>
                    </table>
                </div>
                <h1 style="width:32%; float:right;">
                    <div class="title-r">
                        <div class="title-r"><a style="cursor: pointer" data-index="${status.index}" class="default_a J_CustomerVehicleConsumerHistory">消费历史</a></div>
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                            <div class="title-r"><a style="cursor: pointer" data-index="" class="default_a J_deleteCustomerVehicle">删除</a></div>
                            <div class="title-r"><a style="cursor: pointer" data-index="" class="default_a J_editCustomerVehicle">编辑</a></div>
                        </bcgogo:hasPermission>
                    </div>
                    下次预约服务</h1>
                <div class="member-right" style="overflow-y: auto">
                    <table width="100%" border="0" class="member-table">
                        <colgroup>
                            <col width="25%"/>
                            <col width="20%"/>
                            <col width="25%"/>
                            <col width="20%"/>
                        </colgroup>
                        <tr>
                            <th align="right">保养里程:</th><td align="left"><span class="J_customerVehicleSpan" data-key="maintainMileage"></span><span class="J_maintainMileageUnitSpan" style="width: 30px;float: right;display: none">公里</span></td>
                            <th align="right">保养时间:</th><td align="left"><span class="J_customerVehicleSpan" data-key="maintainTimeStr"></span></td>
                        </tr>
                        <tr>
                            <th align="right">保险时间:</th><td align="left"><span class="J_customerVehicleSpan" data-key="insureTimeStr"></span></td>
                            <th align="right">验车时间:</th><td align="left"><span class="J_customerVehicleSpan" data-key="examineTimeStr"></span></td>
                        </tr>
                    </table>
                </div>
                <div class="clear"></div>
            </div>
            <div class="padding5 gray-bg" id="customerVehicleInfoEdit">
                <form action="customer.do?method=ajaxAddOrUpdateCustomerVehicle" id="customerVehicleForm" method="post">
                    <input type="hidden" id="vehicleId" name="vehicleId" value=""/>
                    <input type="hidden" id="startMileage" name="startMileage" value=""/>
                    <input type="hidden" name="customerId" value="${customerId}"/>
                    <div class="member-left">
                        <table width="100%" border="0" class="order-table">
                            <colgroup>
                                <col width="12%"/>
                                <col width="15%"/>
                                <col width="12%"/>
                                <col width="15%"/>
                                <col width="12%"/>
                                <col width="15%"/>
                            </colgroup>
                            <tr>
                                <th>车牌号：</th>
                                <td><input type="text" maxlength="10" id="licenceNo" name="licenceNo" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>联系人：</th>
                                <td><input type="text" maxlength="20" id="contact" name="contact" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>联系方式：</th>
                                <td><input type="text" maxlength="11" id="mobile" name="mobile" reset-value="" value="" class="txt J_formreset J_customerVehicleMobile"/></td>
                            </tr>
                            <tr>
                                <th>车辆品牌：</th>
                                <td><input type="text" maxlength="10" id="brand" name="brand" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>车型：</th>
                                <td><input type="text" maxlength="10" id="model" name="model" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>年代：</th>
                                <td><input type="text" maxlength="4" id="year" name="year" reset-value="" value="" class="txt J_formreset"/></td>
                            </tr>
                            <tr>
                                <th>排量：</th>
                                <td><input type="text" maxlength="6" id="engine" name="engine" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>购买日期：</th>
                                <td><input type="text" onclick="showDatePicker(this);" maxlength="10" readonly="readonly" id="dateString" name="dateString" reset-value="" value="" class="J_customerVehicleBuyDate txt J_formreset"/></td>
                                <th>车架号：</th>
                                <td><input type="text" maxlength="17" id="vin" name="vin" reset-value="" value="" class="txt J_formreset"/></td>
                            </tr>
                            <tr>
                                <th>发动机号：</th>
                                <td><input type="text" maxlength="20" id="engineNo" name="engineNo" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>当前里程：</th>
                                <td><input type="text" maxlength="10" id="obdMileage" name="obdMileage" reset-value="" value="" class="txt J_formreset"/></td>
                                <th>车身颜色：</th>
                                <td><input type="text" maxlength="10" id="color" name="color" reset-value="" value="" class="txt J_formreset"/></td>
                            </tr>
                        </table>
                        <div class="padding10">
                            <input type="button"  class="query-btn J_saveCustomerVehicleBtn" data-index="" value="确认"/>
                            <input type="button"  class="query-btn J_cancelCustomerVehicleBtn" data-index="" value="取消"/>
                        </div>
                    </div>
                    <h1 style="width:32%; float:right;"> 下次预约服务</h1>
                    <div class="member-right">
                        <table width="100%" border="0" class="member-table">
                            <colgroup>
                                <col width="25%"/>
                                <col width="20%"/>
                                <col width="25%"/>
                                <col width="20%"/>
                            </colgroup>
                            <tr>
                                <th align="right">保养里程：</th>
                                <td><input type="text" style="width:50px" maxlength="6" id="maintainMileage" name="maintainMileage" reset-value="" value="" class="txt J_formreset"/>公里</td>
                                <th align="right">保养时间：</th>
                                <td><input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly" id="by" name="by" reset-value="" value="" class="txt J_formreset"/></td>
                            </tr>
                            <tr>
                                <th align="right">保险时间：</th>
                                <td><input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly" id="bx" name="bx" reset-value="" value="" class="txt J_formreset"/></td>
                                <th align="right">验车时间：</th>
                                <td><input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly" id="yc" name="yc" reset-value="" value="" class="txt J_formreset"/></td>
                            </tr>
                        </table>
                    </div>
                    <div class="clear"></div>
                </form>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
</bcgogo:hasPermission>
<%@ include file="/txn/appointOrder/appointOrderDialog.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>