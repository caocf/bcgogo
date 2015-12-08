var nextPageNo = 1;
var nextPageNo1 = 1;
var nextPageNo2 = 1;
var nextPageNo3 = 1;
var nextPageNo4 = 1;
var isTheLastPage = false;
var isTheLastPage1 = false;
var isTheLastPage2 = false;
var isTheLastPage3 = false;


$(document).ready(function () {
    $(".J_closeSendMsgPrompt").bind("click", function (e) {
        e.preventDefault();
        $("#sendMsgPrompt").dialog("close");
    });

    $("#sendMsgPromptForm").find("input[name='smsFlag'],input[name='appFlag']").bind("click", function (e) {
        if ($("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").length < 2) {
            $("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").attr("disabled", "disabled");
        } else {
            $("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").removeAttr("disabled");
        }
    });


    $("#sendMsgPromptBtn").bind("click", function (e) {
        e.preventDefault();
        if (checkSendMsgPromptMobile()) {
            $("#sendMsgPromptForm").find("input[name='smsFlag']").removeAttr("disabled");
            $("#sendMsgPromptForm").find("input[name='appFlag']").removeAttr("disabled");
            $("#sendMsgPromptForm").find("input[name='mobile']").removeAttr("disabled");

            var paramForm = $("#sendMsgPromptForm").serializeArray();
            var param = {};
            $.each(paramForm, function (index, val) {
                param[val.name] = val.value;
            });

            param['remindEventId'] = $("#sendMsgPromptForm").find("input[name='remindEventId']").val();

            $("#sendMsgPrompt").dialog("close");
            APP_BCGOGO.Net.asyncPost({
                url: "customer.do?method=sendVehicleMsg",
                dataType: "json",
                data: param,
                success: function (json) {
                    if (G.isNotEmpty(json)) {
                        if (json.success) {
                            nsDialog.jAlert("短信发送成功！");
                            window.location.reload();
                        } else {
                            nsDialog.jAlert(json.msg);
                        }
                    } else {
                        nsDialog.jAlert("网络异常，请联系客服");
                    }
                },
                error: function () {
                    nsDialog.jAlert("网络异常，请联系客服");
                }
            });
        }
    });

    $(".replyBtn").live("click", function() {
       var appUserNo=$(this).attr("appUserNo");
       var fromUserName=$(this).attr("fromUserName");
        if(!appUserNo) return;
        $("#talkDialog").dialog({
            resizable: true,
            title:"对话",
            height:550,
            width:510,
            modal: true,
            draggable:false,
            position:['left','bottom'],
            closeOnEscape: false,
            open:function() {
                app_user_no=appUserNo;
                from_user_name=fromUserName;
               $("#contentBody .dialogue").remove();
                $("#moreBtn").click();
                connect();
            },
            close:function(){

            }
        });
    });
});


//故障类
function ajaxGetImpactVideo() {
//  var remindType = $("#remindType").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=searchShopFaultInfoList",
        data: {
            remindType: remindType
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initFault(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#fault_page").show();
            }
            initPage(jsonStr, "dynamical_fault", "remind.do?method=searchShopFaultInfoList", '', "initFault", '', '', '', '');
        }
    });
}

//故障类
function initFault(json) {
    $("#tab_fault tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var faultCodeReportTimeStr = G.normalize(data[i].faultCodeReportTimeStr, "");
            var faultAlertTypeValue = G.normalize(data[i].faultAlertTypeValue, "");
            var faultCodeDescription = G.normalize(data[i].faultCodeDescription, "");
            var vehicleNo = G.normalize(data[i].vehicleNo, "");
            var vehicleBrand = G.normalize(data[i].vehicleBrand, "");
            var vehicleModel = G.normalize(data[i].vehicleModel, "");
            var customerMobile = G.normalize(data[i].customerMobile, "");
            var customerName = G.normalize(data[i].customerName, "");
            var customerIdStr = G.normalize(data[i].customerIdStr, "");
            var mobile = G.normalize(data[i].mobile, "");
            var appUserNo = G.normalize(data[i].appUserNo, "");
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '</span></td>';
            tr += '<td><span class="deadline">' + faultCodeReportTimeStr + '</span></td>';
            tr += '<td><span>' + faultAlertTypeValue + '</span></td>';
            tr += '<td><span>' + faultCodeDescription + '</span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerIdStr + '"><span>' +vehicleNo+' '+ vehicleBrand + ' ' + vehicleModel + '</span></a></span></td>';
            tr += '<td><span>' + mobile + '</span></td>';
            tr += '<td><span>' + customerName + '</span></td>';
            tr += '<td><a  href="appoint.do?method=createAppointOrderByCustomerIdAndAppUserNo&customerId='+customerIdStr+'&appUserNo='+appUserNo+'"><span>' + "生成预约" + '</span></a></td>';
            tr += '</tr >';
            $("#tab_fault").append($(tr));
            tableUtil.tableStyle('#tab_fault', '.title');
        }
    }
}


//SOS类
function ajaxGetSosVideo() {
//  var remindType = $("#remindType").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=searchSosList",
        data: {
//            remindType: remindType
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initSos(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#sos_page").show();
            }
            initPage(jsonStr, "dynamical_sos", "remind.do?method=searchSosList", '', "initSos", '', '', '', '');
        }
    });
}

//SOS类
function initSos(json) {
    $("#tab_sos tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var uploadTime = G.normalize(data[i].uploadTimeStr, "");
            var appUserNo = G.normalize(data[i].appUserNo, "");
            var id = G.normalize(data[i].idStr, "");

            var vehicleNo = G.normalize(data[i].vehicleNo, "");
            var vehicleBrand = G.normalize(data[i].vehicleBrand, "");
            var vehicleModel = G.normalize(data[i].vehicleModel, "");

            var vehicleContact = G.normalize(data[i].vehicleContact, "");
            var vehicleMobile = G.normalize(data[i].vehicleMobile, "");

            var customerId = G.normalize(data[i].customerId, "");
            var customerName = G.normalize(data[i].customerName, "");
            var customerMobile = G.normalize(data[i].customerMobile, "");

            var addr = G.normalize(data[i].addr, "");
            var addrShort = G.normalize(data[i].addrShort, "");
            var lat = G.normalize(data[i].lat, "");
            var lon = G.normalize(data[i].lon, "");

            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '</span></td>';
            tr += '<td><span class="deadline">' + uploadTime + '</span></td>';
            tr += '<td><span>' + vehicleNo+' '+vehicleBrand+' '+vehicleModel + '</span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + vehicleContact +" " +vehicleMobile + '</span></a></span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + customerName + ' ' + customerMobile + '</span></a></span></td>';
            tr += '<td><span>' + addr + '</span></td>';
            tr += '<td><a  href="appoint.do?method=createAppointOrderByCustomerIdAndAppUserNo&customerId='+customerId+'&appUserNo='+appUserNo+'"><span>' + "生成预约" + '</span></a>&nbsp;<span><a class="blue_col detailSos" sosId="' + id + '">已处理</a></span></td>';
            tr += '</tr >';
            $("#tab_sos").append($(tr));
            tableUtil.tableStyle('#tab_sos', '.title');
        }
    }
}

//里程类
function ajaxGetMileage() {
//  var remindType = $("#remindType").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=searchMileageList",
        data: {
//            remindType: remindType
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            init_aaa(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#mileage_page").show();
            }
            initPage(jsonStr, "dynamical_mileage", "remind.do?method=searchMileageList", '', "init_aaa", '', '', '', '');
        }
    });
}

//里程类
function init_aaa(json) {
    $("#tab_mileage tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var vehicleNo = G.normalize(data[i].vehicleNo, "");
            var mobile = G.normalize(data[i].mobile, "");
            var contact = G.normalize(data[i].contact, "");
            var customerName = G.normalize(data[i].customerName, "");
            var customerMobile = G.normalize(data[i].customerMobile, "");
            var nextMaintainMileage = G.normalize(data[i].nextMaintainMileage, "");
            var currentMileage = G.normalize(data[i].currentMileage, "");
            var toNextMaintainMileage = G.normalize(data[i].toNextMaintainMileage, "");
            var customerId = G.normalize(data[i].customerId, "");
            var appUserNo = G.normalize(data[i].appUserNo, "");


            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '</span></td>';
            tr += '<td><span class="deadline">' + vehicleNo + '</span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + contact+' '+mobile + '</span></a></span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + customerName+' '+customerMobile + '</span></a></span></td>';
            tr += '<td><span class="deadline">' + nextMaintainMileage + '</span></td>';
            tr += '<td><span>' + currentMileage + '</span></td>';
            tr += '<td><span>' + toNextMaintainMileage + '</span></td>';
            tr += '<td><a  href="appoint.do?method=createAppointOrderByCustomerIdAndAppUserNo&customerId='+customerId+'&appUserNo='+appUserNo+'"><span>' + "通知" + '</span></a>&nbsp;<span><a class="blue_col updateShopMileageInfo" appUserNo="' + appUserNo + '">已处理</a></span></td>';
            tr += '</tr >';
            $("#tab_mileage").append($(tr));
            tableUtil.tableStyle('#tab_mileage', '.title');
        }
    }
}

//互动类
function initTalkMessage(json) {
    $("#tab_talk_message tr:not(:first)").remove();
    var datas = json.results;
    if (datas && datas.length > 0) {
        for (var i = 0; i < datas.length; i++) {
            var data = datas[i];
            var content = G.normalize(data.content);
            var sendTime = data.sendTime;
            var fromUserName = data.fromUserName;
            var appUserNo = data.appUserNo;
            var vehicleNo = data.vehicleNo;
            var vehicleContact = G.normalize(data.vehicleContact);
            var vehicleMobile = G.normalize(data.vehicleMobile);
            var customerId = data.customerIdStr;
            var customer = data.customer;
            var customerName = G.normalize(data.customerName);
            var customerMobile = G.normalize(data.customerMobile);
            var sendTimeStr = data.sendTimeStr;
            var replyTimeStr = data.replyTimeStr;
            var replyContent = data.replyContent;

            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '</span></td>';
            tr += '<td><span class="deadline">' + vehicleNo + '</span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + vehicleContact+' '+vehicleMobile + '</span></a></span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + customerName+' '+customerMobile + '</span></a></span></td>';
            tr += '<td><span>' + sendTimeStr + '</span></td>';
            tr += '<td><span>' + content + '</span></td>';
            tr += '<td><span>' + replyTimeStr + '</span></td>';
            tr += '<td><span>' + replyContent + '</span></td>';
            tr += '<td><span><a class="blue_col replyBtn" appUserNo="'+appUserNo+'" fromUserName="'+fromUserName+'">回复</a></span></td>';
            tr += '</tr >';
            $("#tab_talk_message").append($(tr));
            tableUtil.tableStyle('#tab_talk_message', '.title');
        }
    }
}


//条件获取欠款提醒
function ajaxGetArrearsRemind() {
    var isOverdue = jQuery("#isOverdue_arrearsRemind").val();
    var hasRemind = jQuery("#hasRemind_arrearsRemind").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=arrearsRemind",
        data: {
            isOverdue: isOverdue,
            hasRemind: hasRemind
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initTr2(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#arrearsRemind_page").show();
            }
            initPage(jsonStr, "dynamical2", "remind.do?method=arrearsRemind", '', "initTr2", '', '', {
                isOverdue: isOverdue,
                hasRemind: hasRemind
            }, '');
        }
    });
}

//条件获取客户服务提醒
function ajaxGetCustomerRemind() {
    var isOverdue = jQuery("#isOverdue_customerRemind").val();
    var hasRemind = jQuery("#hasRemind_customerRemind").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=customerRemind",
        data: {
            isOverdue: isOverdue,
            hasRemind: hasRemind
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initTr4(jsonStr);
            if (jsonStr != null && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#customerRemind_page").show();
            }
            initPage(jsonStr, "dynamical4", "remind.do?method=customerRemind", '', "initTr4", '', '', {
                isOverdue: isOverdue,
                hasRemind: hasRemind
            }, '');
            //为删除按钮加一个属性，用于删除记录后扣减相应入口超链接的数值
            //如果是
            if (isOverdue == "" && hasRemind == "") {
                $(".closeImg1").each(function(i) {
                    $(".closeImg1").eq(i).attr("access", "1");
                })
            } else if (isOverdue == "true" && hasRemind == "false") {
                $(".closeImg1").each(function(i) {
                    $(".closeImg1").eq(i).attr("access", "2");
                })
            } else if (isOverdue == "false" && hasRemind == "false") {
                $(".closeImg1").each(function(i) {
                    $(".closeImg1").eq(i).attr("access", "3");
                })
            } else if (isOverdue == "" && hasRemind == "true") {
                $(".closeImg1").each(function(i) {
                    $(".closeImg1").eq(i).attr("access", "4");
                })
            }
        }
    });
}

//进销存类
function ajaxGetInvoicing() {
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=invoicing",
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initTr3(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#invoicing_page").show();
            }
            initPage(jsonStr, "dynamical3", "remind.do?method=invoicing", '', "initTr3", '', '', '', '');
        }
    });
}

//碰撞视频类
function ajaxGetImpactVideo() {
//  var remindType = $("#remindType").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=impactVideo",
        data: {
//            remindType: remindType
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initImpact(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#impactVideo_page").show();
            }
            initPage(jsonStr, "dynamical_impact", "remind.do?method=impactVideo", '', "initImpact", '', '', '', '');
        }
    });
}

//碰撞视频
function initImpact(json) {
    $("#tab_impact tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var uploadTimeDateStr = G.normalize(data[i].uploadTimeDateStr, "");
          var uploadTimeStr = G.normalize(data[i].uploadTimeStr, "");
            var address = G.normalize(data[i].address, "");
            var url = G.normalize(data[i].url, "");
            var vehicleNo = G.normalize(data[i].vehicleNo, "");
            var vehicleBrand = G.normalize(data[i].vehicleBrand, "");
            var vehicleModel = G.normalize(data[i].vehicleModel, "");
            var customerName = G.normalize(data[i].customerName, "");
            var customerMobile = G.normalize(data[i].customerMobile, "");
            var appUserNo = G.normalize(data[i].appUserNo, "");
            var customerId = G.normalize(data[i].customerId, "");
            var impactVideoId = G.normalize(data[i].impactVideoId, "");
          var impactIdStr = G.normalize(data[i].impactIdStr, "");

            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '</span></td>';
            tr += '<td><span class="deadline">' + uploadTimeDateStr + '</span></td>';
            tr += '<td><span>' + address + '</span></td>';
            tr += '<td><span><a class="blue_col watchImpactVideo" url="' + url + '">查看视频</a></span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + vehicleNo+' '+vehicleBrand+' '+vehicleModel + '</span></a></span></td>';
            tr += '<td><span><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + customerName+' '+customerMobile + '</span></a></span></td>';
            tr += '<td><a  href="appoint.do?method=createAppointOrderByCustomerIdAndAppUserNo&customerId='+customerId+'&appUserNo='+appUserNo+'"><span>' + "生成预约" + '</span></a>&nbsp;<span><a class="blue_col deleteImpact" impactVideoId="' + impactVideoId + '">已处理</a></span>' +
                '&nbsp;<span><a class="blue_col findImpactDetail" impactId="' + impactIdStr + '"  uploadTime="' + uploadTimeStr + '">碰撞详情</a></span></td>';
            tr += '</tr >';
            $("#tab_impact").append($(tr));
            tableUtil.tableStyle('#tab_impact', '.title');
        }
    }
}


//维修美容类
function ajaxGetRepairRemind() {
    var remindType = $("#remindType").val();
    APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=repairRemind",
        data: {
            remindType: remindType
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initTr1(jsonStr);
            if (null != jsonStr && jsonStr["results"] != null && jsonStr["results"].length >= 10) {
                $("#repairRemind_page").show();
            }
            initPage(jsonStr, "dynamical1", "remind.do?method=repairRemind", '', "initTr1", '', '', {
                remindType: remindType
            }, '');
        }
    });
}

//维修美容
function initTr1(json) {
    $("#tab_1 tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var productIdsLack = "";
            var url = "";
            var remindType = G.normalize(data[i].remindType, "");
            var receiptNo = G.normalize(data[i].receiptNo, "");
            var productIds1 = G.normalize(data[i].productIds1, "");
            var repairOrderId = G.normalize(data[i].repairOrderIdStr, "");
            var licenceNo = G.normalize(data[i].licenceNo, "");
            var model = G.normalize(data[i].model, "");
            var productName = G.normalize(data[i].productName, "");
            var name = G.normalize(data[i].name, "");
            var mobile = G.normalize(data[i].mobile, "");
            var content = G.normalize(data[i].content, "");
            var estimateTimeStr = G.normalize(data[i].estimateTimeStr, "");
            var estimateTime = G.normalize(data[i].estimateTime, "");
            var productContent = "";
            var displayModel = "";
            var displayRemindType = "";
            var isRed = dateUtil.isBetweenTodayAndTomorrow(estimateTime);

            if (remindType != null && $.trim(remindType) != "") {
                if (productIds1.charAt(productIds1.length - 1) == ",") {
                    productIds1 = productIds1.substr(0, productIds1.length - 1);
                }
                productIdsLack = productIdsLack + "&productIds=" + productIds1;
                if ($.trim(remindType) == "缺料待修") {
//                    url = "storage.do?method=getProducts&repairOrderId=" + repairOrderId + productIdsLack;
//                    if($("#goodsStoragePermission").val()=="true"){
//                        productContent = '<a class="lack_materials" repairOrderId="'+repairOrderId+'" productIdsLack="'+productIdsLack+'" href="javascript:void(0);">' +
//                            '<div class="ellipsis" style="width:160px;" title="'+productName+'">' + productName + '</div></a>';
//                    }else{
                    productContent = '<div class="ellipsis" style="width:160px;" title="' + productName + '">' + productName + '</div>';
//                    }
                    displayModel = '<span>' + model + '</span>';
                    displayRemindType = '<span>' + remindType + '</span>';
                } else if (jQuery.trim(remindType) == "来料待修") {
                    url = "txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=" + licenceNo + "&orderId=" + repairOrderId;
                    productContent = '<div class="ellipsis" style="width:160px;" title="' + productName + '">' + productName + '</div>';
                    displayModel = '<span>' + model + '</span>';
                    displayRemindType = '<span>' + remindType + '</span>';
                } else {
                    url = "txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=" + licenceNo + "&orderId=" + repairOrderId;
                    productContent = '<div class="ellipsis" style="width:160px;" title="' + productName + '">' + productName + '</div>';
                    displayModel = '<span>' + model + '</span>';
                    displayRemindType = '<span>' + remindType + '</span>';
                }
            } else {
                url = "txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=" + licenceNo + "&orderId=" + repairOrderId;
                remindType = "待交付";
                productContent = '<div class="ellipsis" style="width:160px;" title="' + productName + '">' + productName + '</div>';
                displayModel = '<span>' + model + '</span>';
                displayRemindType = '<span>' + remindType + '</span>';
            }
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '</span></td>';
            tr += '<td><a href="javascript:void(0);" class="invoiceNum" orderid="' + repairOrderId + '"><span>' + receiptNo + '</span></a>';
            tr += '<td class="font">' + displayRemindType + '</td>';
            tr += '<td><span>' + name + '</span></td>';
            tr += '<td><span>' + mobile + '</span></td>';
            tr += '<td><span>' + licenceNo + '</span></td>';
            tr += '<td><span>' + displayModel + '</span></td>';
            tr += '<td>' + productContent + '</td>';
            tr += '<td><div class="ellipsis" style="width:160px;" title="' + content + '">' + content + '</div></td>';
            tr += '<td><span class="deadline">' + estimateTimeStr + '</span></td>';
            tr += '</tr >';
            $("#tab_1").append($(tr));

        }
        /*行点击打开单据页面*/
        $("#tab_1 .table-row-original td").bind('click', function (e) {
            if (e.target == $(e.target).closest('tr').find('.lack_materials')[0] || e.target == $(e.target).closest('tr').find('.lack_materials span')[0]) {
                var _repairOrderId = $(e.target).closest('tr').find('.lack_materials').attr('repairOrderId');
                var _productIdsLack = $(e.target).closest('tr').find('.lack_materials').attr('productIdsLack');
                window.open('storage.do?method=getProducts&repairOrderId=' + _repairOrderId + _productIdsLack, '_repair');
                return;
            }
            var _orderid = $(e.target).closest('tr').find('.invoiceNum').attr('orderid');
            if ($("#repairOrderPermission").val() == "true") {
                window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + _orderid, '_repair');
            }
        });
    }
    privilegeVerifier.repairOrderPermission(jQuery("#repairOrderPermission").val());
    privilegeVerifier.goodsStoragePermission(jQuery("#goodsStoragePermission").val());
    tableUtil.tableStyle('#tab_1', '.title');
}


//欠款提醒
function initTr2(json) {
    $("#tab_2 tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var licenceNo = G.normalize(data[i].licenceNo, "");
            var clientName = G.normalize(data[i].clientName, "");
            var mobile = G.normalize(data[i].mobile, "");
            var totalArrears = dataTransition.rounding(data[i].totalArrears, 2);
            var totalReturnDebt = dataTransition.rounding(data[i].totalReturnDebt, 2);
            var customerId = G.normalize(data[i].customerIdStr, "");
            var contactId = G.normalize(data[i].contactIdStr, "");
            var supplierId = G.normalize(data[i].supplierIdStr, "");
            var contact = G.normalize(data[i].contact, "");
            var status = G.normalize(data[i].remindStatus, "");
            var debtIdStr = G.normalize(data[i].debtIdStr, "");
            //yyyy-MM--dd
            var repayDateStr = G.normalize(data[i].repayDateStr, "");
            //Long型日期
            var repayDate = data[i].repayDate;
            //用于判断是否需要在页面标红
            var isRed = dateUtil.isBetweenTodayAndTomorrow(repayDate) && status != "已提醒";

            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;padding-left:10px;"><span>' + (i + 1) + '&nbsp;</span></td>';
            if (!G.isEmpty(customerId)) {
                tr += '<td class="qian_blue"><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + clientName + '</span></a>&nbsp;</td>';
                tr += '<td class="qian_blue"><a href="unitlink.do?method=customer&customerId=' + customerId + '"><span>' + contact + '</span></a>&nbsp;</td>';
            } else if (!G.isEmpty(supplierId)) {
                tr += '<td class="qian_blue"><a href="unitlink.do?method=supplier&supplierId=' + supplierId + '"><span>' + clientName + '</span></a>&nbsp;</td>';
                tr += '<td class="qian_blue"><a href="unitlink.do?method=supplier&supplierId=' + supplierId + '"><span>' + contact + '</span></a>&nbsp;</td>';
            }

            tr += '<td><span>' + mobile + '</span></td>';

            if (!G.isEmpty(customerId)) {
                tr += '<td class="qian_red"><div class="pay1" onclick="toCreateStatementOrder(\'' + customerId + '\', \'CUSTOMER_STATEMENT_ACCOUNT\') ">';
            } else if (!G.isEmpty(supplierId)) {
                tr += '<td class="qian_red"><div class="pay1" onclick="toCreateStatementOrder(\'' + supplierId + '\', \'SUPPLIER_STATEMENT_ACCOUNT\') ">'
            }

            if (totalArrears > 0) {
                tr += '<span ' +
                    'class="red_color payMoney">应收¥' + totalArrears + '</span>';
            }


            tr += '<td><span class="deadline">' + repayDateStr + '</span></td>';
            tr += '<td><span class="status">' + status + '</span></td>';
            var smsSendPermission = jQuery("#smsSendPermission").val();
            if (smsSendPermission == "true" || smsSendPermission == "1") {
                if (!G.isEmpty(customerId)) {
                    tr += '<td><a onclick="smsSend(\'' + mobile + '\',\'' + 3 + '\',' + totalArrears + ',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + clientName + '\',\'' + customerId + '\',\'' + debtIdStr + '\',\'\',\'\',\'' + contactId + '\');" class="phone tabPhone" style="margin-left:15px;"></a></td>';
                } else if (!G.isEmpty(supplierId)) {
                    tr += '<td><a onclick="smsSend(\'' + mobile + '\',\'' + 3 + '\',' + totalArrears + ',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + clientName + '\',\'' + supplierId + '\',\'' + debtIdStr + '\',\'RETURN\',\'\',\'' + contactId + '\');" class="phone tabPhone" style="margin-left:15px;"></a></td>'
                }

            }
            if (!G.isEmpty(customerId)) {
                tr += '<td><a class="blue_col deleteDebtRemind" customerOrSupplierId="' + customerId + '" type="customer">删除</span></td>';
            } else if (!G.isEmpty(supplierId)) {
                tr += '<td><a class="blue_col deleteDebtRemind" customerOrSupplierId="' + supplierId + '" type="supplier">删除</span></td>';
            }

            tr += '</tr >';
            $("#tab_2").append($(tr));

            /*行点击打开单据页面*/
            $('#tab_2 .btn_cont').bind('click', function(event) {
                event.stopImmediatePropagation();
                var _customerId = $(event.target).closest('.btn_cont').attr('customerId');
                detailsArrears(_customerId);
            });
//            $("#tab_2 .table-row-original td").bind('click', function (event) {
//                if (event.target == $(event.target).closest('tr').find('.btn_cont')[0]||event.target.tagName == "SPAN"||event.target.tagName == "IMG") {
//                    return;
//                }
//                event.stopImmediatePropagation();
//                var _customerId=$(event.target).closest('tr').find('.btn_cont').attr('customerId');
//                detailsArrears(_customerId);
//            });
        }
    }
    tableUtil.tableStyle('#tab_2', '.title');
}

//进销存提醒
function initTr3(json) {
    $("#tab_3 tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var remindType = G.normalize(data[i].remindType, "待入库");
            var receiptNo = G.normalize(data[i].receiptNo, "");
            var supplier = G.normalize(data[i].supplier, "");
            var productName = G.normalize(data[i].productName, "");
            var productNameShort = productName;
            if (productName.length > 30) {
                productNameShort = productName.substr(0, 25) + "...";
            }
            var number = G.normalize(data[i].number, "");
            var totalPrice = G.normalize(data[i].totalPrice, "");
            var supplierId = G.normalize(data[i].supplierId, "");
            var purchaseOrderId = G.normalize(data[i].purchaseOrderIdStr, "");
            var estimateTimeStr = G.normalize(data[i].estimateTimeStr, "");
            var tr = '<tr class="table-row-original">';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td><a class="purchaseOrderLink" target="_blank" href="RFbuy.do?method=show&id=' + purchaseOrderId + '"><span>' + receiptNo + '</span></a></td>';
            tr += '<td>' + remindType + '&nbsp;</td>';
            tr += '<td>' + supplier + '&nbsp;</td>';
            tr += '<td title="' + productName + '">' + productNameShort + '</td>';
            tr += '<td>' + number + '&nbsp;</td>';
            tr += '<td>' + totalPrice + '&nbsp;</td>';
            tr += '<td>' + estimateTimeStr + '&nbsp;</td>';
            tr += '<td><a href="javascript:void(0);" supplierId="' + supplierId + '" purchaseOrderId="' + purchaseOrderId + '"><span>' + "生成入库" + '</span></a>&nbsp;<a class="blue_col deleteTxnRemind" href="javascript:;" orderId="' + purchaseOrderId + '" >删除</a></td>';
            tr += '</tr >';
            $("#tab_3").append($(tr));
        }
        /*行点击打开单据页面*/
        $("#tab_3 .table-row-original td").bind('click', function (event) {
            var purchaseOrderLink = $(event.target).closest("tr").find(".purchaseOrderLink");
            if (purchaseOrderLink[0] == $(event.target).parent(".purchaseOrderLink")[0] || purchaseOrderLink[0] == event.target) {
                return;
            }
            var _supplierId = $(event.target).closest('tr').find('a[href*="javascript"]').attr('supplierId'),
                _purchaseOrderId = $(event.target).closest('tr').find('a[href*="javascript"]').attr('purchaseOrderId');
            window.open('storage.do?method=getProducts&type=txn&supplierId=' + _supplierId + '&purchaseOrderId=' + _purchaseOrderId, '_storage');
        });

        $("#tab_3 .deleteTxnRemind").bind("click", function(event) {
            event.stopPropagation();
            var orderId = $(this).attr("orderId");
            nsDialog.jConfirm("确定要删除本行提醒吗？", "友情提示", function(val) {
                if (val) {
                    APP_BCGOGO.Net.asyncPost({
                        url: "remind.do?method=deleteTxnRemind",
                        data: {
                            orderId: orderId
                        },
                        cache: false,
                        dataType: "json",
                        success: function(result) {
                            if (result && result.success) {
                                ajaxGetInvoicing();
                                initNewTodoCounts();
                            }

                        }
                    });
                }
            });

        });
    }
    tableUtil.tableStyle('#tab_3', '.title');
}


//客户服务
function initTr4(json) {
    $("#tab_4 tr:not(:first)").remove();
    var data = json.results;
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var smsSendPermission = jQuery("#smsSendPermission").val();
            var statusStr = data[i].status === "reminded" ? "已提醒" : "未提醒";
            var idStr = G.normalize(data[i].idStr, "");
            var customerIdStr = G.normalize(data[i].customerIdStr, "");
            var appointName = G.normalize(data[i].appointName, "");
            var licenceNo = G.normalize(data[i].licenceNo, "");
            var vehicleCustomerName = G.normalize(data[i].vehicleCustomerName, "");
            var vehicleMobile = G.normalize(data[i].vehicleMobile, '');
            var vehicleIdStr = G.normalize(data[i].vehicleIdStr, '');
            var contactIdStr = G.normalize(data[i].contactIdStr, '');
            var customerName = G.normalize(data[i].customerName, "");
            var mobile = data[i].mobile == null || data[i].mobile.length < 2 ? '' : data[i].mobile;
            var remindTimeStr = G.normalize(data[i].remindTimeStr, "");
            var remindTime = G.normalize(data[i].remindTime, "");
            var contact = G.normalize(data[i].contact, "");
            var remindType = G.normalize(data[i].remindType, "");
            var remindMileage = G.normalize(data[i].remindMileage, "");
            var remindMileageStr = "";
            var currentMileage = G.normalize(data[i].currentMileage, "");
            var currentMileageStr = "";
            if (!G.Lang.isEmpty(remindMileage)) {
                remindMileageStr = remindMileage + "km";
            }
            if (!G.isEmpty(currentMileage)) {
                currentMileageStr = currentMileage + "km";
            }

            var remainMileage = (G.isEmpty(currentMileage) || G.isEmpty(remindMileage)) ? "" : (currentMileage - remindMileage);

            var remainMileageStr = G.isEmpty(remainMileage) ? "" : ( remainMileage > 0 ? ("超出" + remainMileage + "km") : ("还剩" + (0 - remainMileage) + "km"));

            var isRed = dateUtil.isBetweenTodayAndTomorrow(remindTime) && statusStr != "已提醒";

            //l	点击发送短信，若车主无手机号，客户有手机号，显示客户手机号；
            //l	点击发送短信，若车主有手机号，客户无手机号，显示车主手机号；
            //l	点击发送短信，若车主和客户都有手机号，显示车主手机号；
            //l	点击发送短信，若车主和客户都无手机号，提示填写手机号，并保存未车主手机号；

            var smsRelateIdStr = vehicleIdStr;
            var smsMobile = vehicleMobile;
            var smsName = vehicleCustomerName;
            var isVehicleInfo = "1";
            if (G.isEmpty(smsMobile)) {
                smsRelateIdStr = contactIdStr;
                smsMobile = mobile;
                smsName = customerName;
                isVehicleInfo = "";
            }
            var tr = '<tr class="table-row-original" remindType="' + remindType + '" licenceNo="' + licenceNo + '" mobile="' + mobile + '">';
            tr += '<td style="border-left:none;"><span>' + (i + 1) + '&nbsp;</span></td>';
            tr += '<td class="font"><span>' + appointName + '&nbsp;</span></td>';
            tr += '<td><span>' + licenceNo + '&nbsp;</span></td>';
            tr += '<td><span>' + (vehicleCustomerName == '' ? '<span style="color:#999999;">暂无</span>' : vehicleCustomerName) + ' / ';
            if (smsSendPermission) {
                tr += (vehicleMobile != '' ? vehicleMobile : '<span style="color:#999999;display: inline-block;width: 66px;text-align: left;">暂无</span>') + '&nbsp;</span></td>';
            } else {
                tr += vehicleMobile + '&nbsp;</span></td>';
            }
            tr += '<td style="text-align: left;padding-left: 30px;"><span>' + customerName + ' / ';
            if (smsSendPermission) {
                tr += (mobile != '' ? mobile : '<span style="color:#999999;display: inline-block;width: 66px;text-align: left;">暂无</span>') + '&nbsp;</span></td>';
            } else {
                tr += mobile + '</span></td>';
            }

            if (G.isEmpty(remindTimeStr) && G.isEmpty(remindMileage)) {
                tr += '<td style="text-align: center;"><span style="color:#999999;">暂无</span></td>';

            } else if (!G.isEmpty(remindTimeStr) && !G.isEmpty(remindMileage)) {
                tr += '<td style="text-align: center;"><span class="deadline">' + (G.isEmpty(remindTimeStr) ? '<span style="color:#999999;">暂无</span>' : remindTimeStr) + '</span> / ' +
                    '<span class="J_remindMileage" remainMileage="' + remainMileage + '" remindMileage="' + remindMileage + '">' + (G.isEmpty(remindMileageStr) ? '<span style="color:#999999;">暂无</span>' : remindMileageStr) + '</span>' +
                    '</td>';
            } else {
                tr += '<td style="text-align: center;"><span class="deadline">' + remindTimeStr + '</span>' +
                    '<span class="J_remindMileage" remainMileage="' + remainMileage + '" remindMileage="' + remindMileage + '">' + remindMileageStr + '</span>' +
                    '</td>';
            }
            tr += '<td><span class="J_currentMileage" currentMileage="' + currentMileage + '">' + currentMileageStr + '</span></td>';

            tr += '<td style="text-align: center;"><span>' + (G.isEmpty(remainMileageStr) ? '<span style="color:#999999;">暂无</span>' : remainMileageStr) + '</span></td> ';

            tr += '<td><span class="status">' + statusStr + '</span></td>';
            tr += '<td>';
            if (smsSendPermission) {
                tr += '<img name="smsSendImg" style="margin-right:5px" src="images/phone.jpg"'
                    + 'smsMobile=' + '"' + smsMobile + '"remindType=' + '"' + remindType + '"'
                    + 'licenceNo=' + '"' + licenceNo + '"remindTimeStr=' + '"' + remindTimeStr + '"'
                    + 'smsName=' + '"' + smsName + '"customerIdStr=' + '"' + customerIdStr + '"smsRelateIdStr=' + '"' + smsRelateIdStr + '"'
                    + 'idStr=' + '"' + idStr + '"appointName=' + '"' + appointName + '" isVehicleInfo="' + isVehicleInfo + '">';
            }

            tr += '<span class="closeImg1" access="1" type="' + remindType + '" id="' + idStr + '">删除</span></td>';
            tr += '</tr>';
            $("#tab_4").append($(tr));
        }
        $('#tab_4 .table-row-original img[name=smsSendImg]').click(function () {
            var $smsSendImg = $(this);
            var dates = $smsSendImg.attr("remindTimeStr").split("-");
            var year = dates[0];
            var month = dates[1];
            var day = dates[2];

            var sendMobile = $smsSendImg.attr("smsMobile");
            var licenceNo = $smsSendImg.attr("licenceNo");
            var remindEventId = $smsSendImg.attr("idStr");
            var result = APP_BCGOGO.Net.syncGet({"url": "sms.do?method=getMobileMsgContent",
                data:
                { "licenceNo": licenceNo,
                    "mobile": $smsSendImg.attr("smsMobile"),
                    "type": $smsSendImg.attr("remindType"),
                    "money": 0,
                    "year": year,
                    "month": month,
                    "day": day,
                    "name": $smsSendImg.attr("smsName"),
                    "remindEventId":$smsSendImg.attr("idStr"),
                    "serviceName": $smsSendImg.attr("appointName"),
                    "appointName": $smsSendImg.attr("appointName"),
                    "contactIds":$smsSendImg.attr("smsRelateIdStr")
                }, dataType: "json"});
            if (G.isNotEmpty(result) && G.isNotEmpty(result.content)) {
                $("#sendMsgPrompt").dialog({
                    width: 430,
                    modal: true,
                    resizable: false,
                    position: 'center',
                    open: function () {

                        $("#sendMsgPromptForm").find("input[name='type']").val($smsSendImg.attr("remindType"));
                        $("#sendMsgPromptForm").find("input[name='remindEventId']").val(remindEventId);
                        $("#sendMsgPromptForm").find("div[id='vehicleMsgContent']").html(result.content);
                        $("#sendMsgPromptForm").find("input[name='licenceNo']").val(licenceNo);
                        $("#sendMsgPromptForm").find("input[name='year']").val(year);
                        $("#sendMsgPromptForm").find("input[name='month']").val(month);
                        $("#sendMsgPromptForm").find("input[name='day']").val(day);
                        $(".ui-dialog-titlebar", $(this).parent()).hide();
                        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
                        if (G.isNotEmpty(sendMobile)) {
                            $("#sendMsgPromptForm").find("input[name='mobile']").val(sendMobile);
                            $("#sendMsgPromptForm").find("input[name='mobile']").attr("disabled", "disabled");
                        }
                        checkSendMsgPromptMobile();
                    },
                    close: function () {
                        $("#sendMsgPromptForm").find("input[name='smsFlag']").attr("disabled", "disabled");
                        $("#sendMsgPromptForm").find("input[name='appFlag']").removeAttr("disabled");
                        $("#sendMsgPromptForm").find("input[name='mobile']").removeAttr("disabled");
                        $("#sendMsgPromptForm")[0].reset();
                    }
                });
            } else {
                nsDialog.jAlert("数据异常，请刷新页面！");
            }


        });
    }
    tableUtil.tableStyle('#tab_4', '.title');
}


function checkSendMsgPromptMobile() {
    var mobile = $("#sendMsgPromptForm").find("input[name='mobile']").val();
    if (G.isEmpty(mobile)) {
        $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").hide();
        $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").show();
        $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").hide();
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
        $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").show();
        $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").hide();
        $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").hide();
        return false;
    }
    $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").hide();
    $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").hide();
    $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").show();
    return true;
}
//本店计划
//function initTr5(jsonStr) {
//    $("#tab_five tr:not(:first)").remove();
//
//    if(jsonStr.length > 1) {
//        for(var i = 0; i < jsonStr.length - 1; i++) {
//            if(jsonStr[jsonStr.length - 1].isTheLastPage4 == "true") {
//                isTheLastPage4 = true;
//            } else {
//                isTheLastPage4 = false;
//            }
//            var statusStr = jsonStr[i].status == "reminded" ? "已提醒" : "未提醒";
//            var idStr = jsonStr[i].idStr == null ? " " : jsonStr[i].idStr;
//            var remindType = jsonStr[i].remindType == null ? " " : jsonStr[i].remindType;
//            var content = jsonStr[i].content == null ? " " : jsonStr[i].content;
//            var customerNames = jsonStr[i].customerNames == null ? " " : jsonStr[i].customerNames;
//            var remindTimeStr = jsonStr[i].remindTimeStr == null ? " " : jsonStr[i].remindTimeStr;
//            var customerIds = jsonStr[i].customerIds == null ? " " : jsonStr[i].customerIds;
//            var customerType = jsonStr[i].customerType == null ? " " : jsonStr[i].customerType;
//            var tr = '<tr class="table-row-original">';
//            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
//            tr += '<td>' + remindType + '</td>';
//            tr += '<td>' + content + '&nbsp;</td>';
//            tr += '<td>' + customerNames + '<input type="hidden" value="' + customerIds + '" class="Ids"/>&nbsp;</td>';
//            tr += '<td>' + remindTimeStr + '&nbsp;</td>';
//            tr += '<td><span class="qian_blue">' + statusStr + '</span></td>';
//            var smsSendPermission = jQuery("#smsSendPermission").val();
//            if(smsSendPermission == "true") {
//                tr += '<td><img src="images/duan.png" class="sendMsgStyle" onclick="smsSendPlan(this,\'' + customerIds + '\',\'' + content + '\',\'' + customerType + '\',\'' + idStr + '\')"/>&nbsp;&nbsp;<img src="images/opera1.jpg" class="closeImg" id="' + idStr + '"/></td>';
//            } else {
//                tr += '<td><img src="images/opera1.jpg" class="closeImg" id="' + idStr + '"/></td>';
//            }
//            tr += '</tr >';
//            $("#tab_five").append($(tr));
//        }
//    } else {
//        isTheLastPage4 = true;
//    }
//    tableUtil.tableStyle('#tab_five', '.title');
//}

function smsSendPlan(domObj, customerIds, content, customerType, idStr) {
    $.ajax({
        type: "POST",
        url: "remind.do?method=smsSendPlan",
        data: {
            customerIds: customerIds,
            content: content,
            customerType: customerType,
            idStr: idStr
        },
        cache: false,
        async: true,
        success: function() {
            alert("计划已发送！");
            $(domObj).parent().prev().html("已提醒");
        },
        error: function() {
            alert("网络错误，计划发送失败！");
        }
    });
}


function hideIt() {
    $("#tab_1 tr:gt(5)").hide();
    $("#tab_2 tr:gt(5)").hide();
    $("#tab_3 tr:gt(5)").hide();
    $("#tab_4 tr:gt(5)").hide();
//    $("#tab_5 tr:gt(5)").hide();
//    $(".i_leftBtn").hide();
}

function addPlan() {
    //删除所有新增行
    $('.addPlan', '#setLocation').each(function() {
        if (parseInt($(this).attr('id')) > 0) {
            $(this).remove();
        }
    });
    //显示新增行的删除按钮.
    $('.delete_opera1', '#setLocation').next().css({
        "display": "inline"
    });

    trCount = 0;
    $($(".addPlan:first")).attr('id', trCount);
    $($(".addPlan:first")).find("td").first().html(trCount + 1);
    $($(".addPlan:first")).find("input").each(function() {
        var inputId = $(this).attr("id");
        $(this).attr("id", inputId.split("_")[0] + "_" + trCount);
        //清空每行内input的值
        $(this).val('');
    });

    Mask.Login();
    $("#setLocation").show();
}

//function initnewtodo() {
//    if(jQuery("#tab_four").find("tr").not(":first").size() == 0) {
//        jQuery.ajax({
//            type: "POST",
//            url: "remind.do?method=customerRemind",
//            data: {
//                startPageNo: 1
//            },
//            cache: false,
//            dataType: "json",
//            success: function(jsonStr) {
//                initTr2(jsonStr);
//                initPages(jsonStr, "dynamical4", "remind.do?method=customerRemind", '', "initTr4", '', '', {
//                    startPageNo: 1
//                }, '');
//            }
//        });
//    }
//
//    if(jQuery("#tab_four").find("tr").not(":first").size() == 0) {
//        jQuery.ajax({
//            type: "POST",
//            url: "remind.do?method=getPlans",
//            data: {
//                startPageNo: 1
//            },
//            cache: false,
//            dataType: "json",
//            success: function(jsonStr) {
//                initTr4(jsonStr);
//                initPages(jsonStr, "dynamical5", "remind.do?method=getPlans", '', "initTr5", '', '', {
//                    startPageNo: 1
//                }, '');
//            }
//        });
//    }
//}

$(document).ready(function() {
    //维修美容类超链接
    $("#allRepair").click(function() {
        $("#remindType").val('');
        ajaxGetRepairRemind();
    });
    $("#lack").click(function() {
        $("#remindType").val('lack');
        ajaxGetRepairRemind();
    });
    $("#incoming").click(function() {
        $("#remindType").val('incoming');
        ajaxGetRepairRemind();
    });
    $("#pending").click(function() {
        $("#remindType").val('pending');
        ajaxGetRepairRemind();
    });
    $("#waitOutStorage").click(function() {
        $("#remindType").val('waitOutStorage');
        ajaxGetRepairRemind();
    });
    //欠款提醒超链
    $("#allArrears").live("click", function() {
        $("#isOverdue_arrearsRemind").val("");
        $("#hasRemind_arrearsRemind").val("");
        ajaxGetArrearsRemind();
    });
    $("#arrearsRemindIsOverdue").live("click", function() {
        $("#isOverdue_arrearsRemind").val("true");
        $("#hasRemind_arrearsRemind").val("false");
        ajaxGetArrearsRemind();
    });
    $("#arrearsRemindIsNotOverdue").live("click", function() {
        $("#isOverdue_arrearsRemind").val("false");
        $("#hasRemind_arrearsRemind").val("false");
        ajaxGetArrearsRemind();
    });
    $("#arrearsRemindHasRemind").live("click", function() {
        $("#isOverdue_arrearsRemind").val("");
        $("#hasRemind_arrearsRemind").val("true");
        ajaxGetArrearsRemind();
    });

    //客户服务提醒超链
    $("#allCustomerRemind").live("click", function() {
        $("#isOverdue_customerRemind").val("");
        $("#hasRemind_customerRemind").val("");
        ajaxGetCustomerRemind();
    });
    $("#customerRemindIsOverdue").live("click", function() {
        $("#isOverdue_customerRemind").val("true");
        $("#hasRemind_customerRemind").val("false");
        ajaxGetCustomerRemind();
    });
    $("#customerRemindIsNotOverdue").live("click", function() {
        $("#isOverdue_customerRemind").val("false");
        $("#hasRemind_customerRemind").val("false");
        ajaxGetCustomerRemind();
    });
    $("#customerRemindHasRemind").live("click", function() {
        $("#isOverdue_customerRemind").val("");
        $("#hasRemind_customerRemind").val("true");
        ajaxGetCustomerRemind();
    });

    $("#exportCustomerRemind").click(function() {
        var $this = $(this);
        if ($this.attr("lock")) {
            return;
        } else {
            try {
                $this.attr("lock", true);
                $("#exportCustomerCover").show();
                $("#exportCustomerRemind").hide();
                APP_BCGOGO.Net.asyncPost({
                    url: "export.do?method=exportCustomerRemind",
                    data: {
                        isOverdue: $("#isOverdue_customerRemind").val(),
                        hasRemind: $("#hasRemind_customerRemind").val()
                    },
                    cache: false,
                    dataType: "json",
                    success: function(json) {
                        $this.removeAttr("lock");
                        $("#exportCustomerCover").hide();
                        $("#exportCustomerRemind").show();
                        if (json && json.exportFileDTOList) {
                            if (json.exportFileDTOList.length > 1) {
                                showDownLoadUI(json);
                            } else {
                                window.open("download.do?method=downloadExportFile&exportFileName=客户服务提醒.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                            }
                        }
                    }
                });
            } catch (e) {
                $this.removeAttr("lock");
                $("#exportCustomerCover").hide();
                $("#exportCustomerRemind").show();
            }
        }

    });

    //进销存类超链接
    $("#allInvoicing").click(function() {
        ajaxGetInvoicing();
    });

//    $(".closeImg").live('click', (function() {
//        if(confirm("真的要删除本行提醒吗？")) {
//            document.body.style.cursor = "wait";
//            this.style.display = "none";
//            $.ajax({
//                type: "POST",
//                data: {
//                    idStr: $(this).attr("id")
//                },
//                url: "remind.do?method=dropPlan",
//                success: function(data) {
//                    document.body.style.cursor = "auto";
//                    var temp = ($("#plans").html()) * 1 - 1;;
//                    $("#plans").html(temp);
//                    var currentPage = $("#getPagedynamical5").val() * 1;
//                    if(temp % 10 == 0 && currentPage > 1) {
//                        currentPage = currentPage - 1;
//                    }
//                    initPlan2(currentPage);
//                }
//            });
//        }
//    }));

//    function initPlan2(currentPage) {
//        $.ajax({
//            type: "POST",
//            url: "remind.do?method=getPlans",
//            data: {
//                startPageNo: currentPage
//            },
//            cache: false,
//            dataType: "json",
//            success: function(jsonStr) {
//                initTr5(jsonStr);
//                initPages(jsonStr, "dynamical5", "remind.do?method=getPlans", '', "initTr5", '', '', {
//                    startPageNo: currentPage
//                }, '');
//            }
//        });
//    }

    $(".closeImg1").live('click', function() {
        if (confirm("确定要删除本行提醒吗？")) {
            var type = parseInt($(this).attr("type"));
            var url = "txn.do?method=cancelRemindEventById";
            $.ajax({
                type: "POST",
                data: {
                    idStr: $(this).attr("id")
                },
                url: url,
                success: function(data) {
                    ajaxGetCustomerRemind();
                    initNewTodoCounts();


//                    initCustomerRemind();
//                    //更新统计数值
//                    var nums = data.split(",");
//                    $("#allCustomerRemindCount").html(nums[0]);
//                    $("#customerRemindCountIsOverdue").html(nums[1]);
//                    $("#customerRemindCountIsNotOverdue").html(nums[2]);
//                    $("#customerRemindCountHasRemind").html(nums[3]);
                }
            });
        }
    });

    $("#div_close,#cancleBtn").live('click', function() {
        closeDiv();
    });
    //    window.onload = initnewtodo;

    $("#tab_2 .deleteDebtRemind").live("click", function() {
        var customerOrSupplierId = $(this).attr("customerOrSupplierId");
        var type = $(this).attr("type");
        nsDialog.jConfirm("确定要删除本行提醒吗？", "友情提示", function(val) {
            if (val) {
                APP_BCGOGO.Net.asyncPost({
                    url: "remind.do?method=deleteDebtRemind",
                    data: {
                        customerOrSupplierId: customerOrSupplierId,
                        type: type
                    },
                    cache: false,
                    dataType: "json",
                    success: function(result) {
                        if (result && result.success) {
                            ajaxGetArrearsRemind();
                            initNewTodoCounts();
                        }

                    }
                });
            }
        });

    });

    $("#tab_impact .deleteImpact").live("click", function(event) {
        event.stopPropagation();
        var impactVideoId = $(this).attr("impactVideoId");
        nsDialog.jConfirm("确定要删除本行碰撞提醒吗？", "友情提示", function(val) {
            if (val) {
                APP_BCGOGO.Net.asyncPost({
                    url: "remind.do?method=deleteImpactVideo",
                    data: {
                        impactVideoId: impactVideoId
                    },
                    cache: false,
                    dataType: "json",
                    success: function(result) {
                        if (result && result.success) {
                            ajaxGetImpactVideo();
                        }
                    }
                });
            }
        });
    });

});

//function initPlan() {
//    $.ajax({
//        type: "POST",
//        url: "remind.do?method=getPlans",
//        data: {
//            startPageNo: nextPageNo4
//        },
//        cache: false,
//        dataType: "json",
//        success: function(jsonStr) {
//            initTr4(jsonStr);
//        }
//    });
//}

function initCustomerRemind() {
    $.ajax({
        type: "POST",
        url: "remind.do?method=customerRemind",
        data: {
            startPageNo: nextPageNo2,
            maxRows: 10
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            initTr4(jsonStr);
            if (isTheLastPage2 == true && nextPageNo2 == 1) {
                $("#pageNo_id2>div:eq(1)").css('display', 'none');
            }
            if (isTheLastPage2 == true) {
                $("#pageNo_id2>div:last").css('display', 'none');

            }
            if (nextPageNo2 == 1) {
                $("#pageNo_id2>div:first").css('display', 'none');
            }
        },
        error: function() {
            alert("获取列表失败!! ");
        }
    });
}

function closeDiv() {
    $("#setLocation").hide();
    $("#mask").hide();
    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
        ;
    }
}
//初始化页面上的统计值
function initNewTodoCounts() {
    APP_BCGOGO.Net.asyncGet({
        url: "remind.do?method=getNewTodoCounts",
        data: {},
        type: "json",
        success: function (json) {
            var dataMap = json.data;
            if (json.success && !G.isEmpty(dataMap)) {
                for (var key in dataMap) {
                    $("strong[id='" + key + "Hint']").text(dataMap[key]);
                }
            }
        },
        error: function () {

        }
    });
}


$(document).ready(function() {
    initNewTodoCounts();

    setInterval(function() {
        //维修美容，单据号、提醒类型、车牌号闪动
        $("#tab_1 tr:not(:first)").each(function(i) {
            /*var _span1 = $("#tab_1 tr:not(:first)").eq(i).children().eq(1).children().eq(0).children().eq(0);
             var _span2 = $("#tab_1 tr:not(:first)").eq(i).children().eq(2).children().eq(0).children().eq(0);
             var _span3 = $("#tab_1 tr:not(:first)").eq(i).children().eq(4).children().eq(0).children().eq(0);
             var _td = $("#tab_1 tr:not(:first)").eq(i).children().eq(8);
             var deadlineStr = $.trim(_td.html());
             var deadline = new Date(deadlineStr.replaceAll("-","/")+" 00:00");
             if(dateUtil.isBetweenTodayAndTomorrow(deadline)){
             if(_span1.css("visibility")=="visible"){
             _span1.css("visibility","hidden");
             _span2.css("visibility","hidden");
             _span3.css("visibility","hidden");
             }else{
             _span1.css("visibility","visible");
             _span2.css("visibility","visible");
             _span3.css("visibility","visible");
             }
             }*/
            var _row = $("#tab_1 tr:not(:first)").eq(i);
            var _td = _row.find('.deadline');
            var deadlineStr = $.trim(_td.html());
            var deadline = new Date(deadlineStr.replaceAll("-", "/") + " 00:00");

            if (dateUtil.isBetweenTodayAndTomorrow(deadline)) {
                if (_row.hasClass("blink")) {
                    _row.addClass("blinking").removeClass("blink");
                } else {
                    _row.addClass("blink").removeClass("blinking");
                }
            }
        });
        //欠款提醒，客户名、联系方式闪动
        $("#tab_2 tr:not(:first)").each(function(i) {
            /*            var _span1 = $("#tab_2 tr:not(:first)").eq(i).children().eq(1).children().eq(0).children().eq(0);
             var _span2 = $("#tab_2 tr:not(:first)").eq(i).children().eq(3).children().eq(0);
             var _span3 = $("#tab_2 tr:not(:first)").eq(i).children().eq(5).children().eq(0);
             var _td = $("#tab_2 tr:not(:first)").eq(i).children().eq(6);
             var deadlineStr = $.trim(_span3.html());
             var deadline = new Date(deadlineStr.replaceAll("-","/")+" 00:00");
             if(dateUtil.isBetweenTodayAndTomorrow(deadline) && _td.html()=="未提醒"){
             if(_span1.css("visibility")=="visible"){
             _span1.css("visibility","hidden");
             _span2.css("visibility","hidden");
             }else{
             _span1.css("visibility","visible");
             _span2.css("visibility","visible");
             }
             }*/
            var _row = $("#tab_2 tr:not(:first)").eq(i);
            var _tdDate = _row.find('.deadline');
            var _tdStatus = _row.find('.status');
            var deadlineStr = $.trim(_tdDate.html());
            var deadline = new Date(deadlineStr.replaceAll("-", "/") + " 00:00");

            if (dateUtil.isBetweenTodayAndTomorrow(deadline) && _tdStatus.html() == "未提醒") {
                if (_row.hasClass("blink")) {
                    _row.addClass("blinking").removeClass("blink");
                } else {
                    _row.addClass("blink").removeClass("blinking");
                }
            }
        });
        //客户服务，联系方式、日期、状态闪动
        $("#tab_4 tr:not(:first)").each(function(i) {
            /*            var span1 = $("#tab_4 tr:not(:first)").eq(i).children().eq(5).children().eq(0);
             var span2 = $("#tab_4 tr:not(:first)").eq(i).children().eq(6).children().eq(0);
             var span3 = $("#tab_4 tr:not(:first)").eq(i).children().eq(7).children().eq(0);
             var deadlineStr = $.trim(span2.html());
             var deadline = new Date(deadlineStr.replaceAll("-","/")+" 00:00");
             if(dateUtil.isBetweenTodayAndTomorrow(deadline) && span3.html()=="未提醒"){
             if(span1.css("visibility")=="visible"){
             span1.css("visibility","hidden");
             span2.css("visibility","hidden");
             span3.css("visibility","hidden");
             }else{
             span1.css("visibility","visible");
             span2.css("visibility","visible");
             span3.css("visibility","visible");
             }
             }*/

            var _row = $("#tab_4 tr:not(:first)").eq(i);
            var _tdDate = _row.find('.deadline');
            var _tdStatus = _row.find('.status');
            var deadlineStr = $.trim(_tdDate.html());
            var deadline = new Date(deadlineStr.replaceAll("-", "/") + " 00:00");
            var remainMileage = _row.find('.J_remindMileage').attr("remainMileage");
            var remindMileage = _row.find('.J_remindMileage').attr("remindMileage");
            if (dateUtil.isBetweenTodayAndTomorrow(deadline) && _tdStatus.html() == "未提醒" || _tdStatus.html() == "未提醒"
                && remainMileage * 1 >= -500 && remainMileage * 1 <= 500 && remindMileage * 1 > 0) {
                if (_row.hasClass("blink")) {
                    _row.addClass("blinking").removeClass("blink");
                } else {
                    _row.addClass("blink").removeClass("blinking");
                }
            }
        });
    }, 500);

})


function searchVideo(url) {
    var urlstr = "remind.do?method=toVideo?url=" + url;
    window.open(urlstr, 'newwindow',
        'height=290px,width=657px,top=300,left=500,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no,alwaysRaised=yes,titlebar=no');
}


$("#tab_sos .detailSos").live("click", function(event) {
  event.stopPropagation();
  var id = $(this).attr("sosId");
  nsDialog.jConfirm("确定已经处理本次救援了吗？", "友情提示", function(val) {
    if (val) {
      APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=detailSos",
        data: {
          id: id
        },
        cache: false,
        dataType: "json",
        success: function(result) {
          if (result && result.success) {
            ajaxGetSosVideo();
          }
        }
      });
    }
  });
});



$("#tab_mileage .updateShopMileageInfo").live("click", function(event) {
  event.stopPropagation();
  var appUserNo = $(this).attr("appUserNo");
  nsDialog.jConfirm("确定已处理即将到期里程保养了吗？", "友情提示", function(val) {
    if (val) {
      APP_BCGOGO.Net.asyncPost({
        url: "remind.do?method=updateShopMileageInfo",
        data: {
          appUserNo: appUserNo
        },
        cache: false,
        dataType: "json",
        success: function(result) {
          if (result && result.success) {
            ajaxGetMileage();
          }
        }
      });
    }
  });
});



$("#tab_impact .watchImpactVideo").live('click', function() {
  var url = $(this).attr("url");
  window.open(url,'newwindow',
      'height=290px,width=657px,top=300,left=500,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no,alwaysRaised=yes,titlebar=no');
});

$("#tab_impact .findImpactDetail").live("click", function() {
  var impactId = $(this).attr('impactId');
  var uploadTime_=$(this).attr("uploadTime");
  if(!impactId) return;
//  if(!uploadTime_) return;
  var result = APP_BCGOGO.Net.syncGet({"url": "shopImpactInfo.do?method=getImpactDetail",
    data:
    { "impactId": impactId,
      "uploadTime": uploadTime_
    }, dataType: "json"});
  if (G.isNotEmpty(result)) {
  $("#impactDetail").dialog({
    resizable: true,
    title:"碰撞详情",
    height:290,
    width:525,
    modal: true,
    closeOnEscape: false,
    open:function() {
      $("#impactDetailForm").find("div[id='impactTime']").html(result.uploadTime);
      $("#impactDetailForm").find("div[id='impactAddress']").html(result.address);
      $("#impactDetailForm").find("div[id='impactFaultCode']").html(result.rdtc);
      $("#impactDetailForm").find("div[id='vss']").html(result.vss);
      $("#impactDetailForm").find("div[id='impactF']").html(result.vss);
//      app_user_no=appUserNo;
//      $("#moreBtn").click();
//      connect();
    },
    close:function(){
    }
  }); }else{
    nsDialog.jAlert("未采集到碰撞信息！");
  }
});


