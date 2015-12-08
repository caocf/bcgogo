var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$().ready(function () {

   /* $(".businessTip").click(function(){
        var ee=$("#map_container_iframe").css("src");
        alert(ee);
    })*/

    //绑定地区选择框
    $(".area-province,.area-city").bind("change", function () {
        var $me = $(this);
        if ($me.hasClass("area-province")) {
            $(".area-city").text("");
        }
        $(".area-region").text("");
        if (G.isEmpty($(this).val())) {
            $(".area-city").append("<option value=''>--城市--</option>");
            $(".area-region").append("<option value=''>--区--</option>");
        } else {
            getAreaInfoByParentNo($(this).val(), function (areaList) {
                if (G.isEmpty(areaList)) {
                    return;
                }
                var areaOptStr = "";
                for (var i = 0; i < areaList.length; i++) {
                    var area = areaList[i];
                    areaOptStr += '<option value="' + area.no + '" name="' + area.name + '">' + area.name + '</option>';
                }
                if ($me.hasClass("area-province")) {
                    areaOptStr = "<option value=''>--城市--</option>" + areaOptStr;
                    $(".area-city").append(areaOptStr);
                } else if ($me.hasClass("area-city")) {
                    areaOptStr = "<option value=''>--区--</option>" + areaOptStr;
                    $(".area-region").append(areaOptStr);
                    var r = APP_BCGOGO.Net.asyncGet({
                        "url": "product.do?method=searchlicenseNo&localArea=" + $(".area-city option:selected").val(),
                        "dataType": "json",
                        success: function (result) {
                            if (!G.isEmpty(result)) {
                                var platecarno = G.normalize(result[0].platecarno);
                                $(".shopData input[field='licencePlate']").val(platecarno);
                            }
                        },
                        error: function () {
                            nsDialog.jAlert("网络异常！");
                        }
                    });

                }
            });
        }
    });

    $("#startDate,#endDate").bind("click",
        function () {
            $(this).blur();
        }).datetimepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "showHour": true,
            "showMinute": true,
            "dateFormat": "yy-mm-dd",
            "yearSuffix": "",
            "onClose": function (dateText, inst) {
                $("#addMediumCommentScore").val("");
                $("#addBadCommentScore").val("");
                $("#addGoodCommentScore").val("");
                $("#commentTimeStart").val($("#startDate").val());
                $("#commentTimeEnd").val($("#endDate").val());
            }
        });

    //记录选择的日期
    $("#my_date_yesterday,#my_date_today,#my_date_thismonth,#my_date_thisyear").bind("click", function () {
        $("#addMediumCommentScore").val("");
        $("#addBadCommentScore").val("");
        $("#addGoodCommentScore").val("");
        $("#commentTimeStart").val($("#startDate").val());
        $("#commentTimeEnd").val($("#endDate").val());
        searchAppUserCommentRecord();
    });
    //记录选择的评价类型
    $('input[name="evaluation"]').bind("click", function () {
        var evaluationListValue = "";
        $('input[name="evaluation"]:checked').each(function () {
            evaluationListValue += $(this).val() + ",";
        })
        $("#commentScore").val(evaluationListValue);
        $("#addMediumCommentScore").val("");
        $("#addBadCommentScore").val("");
        $("#addGoodCommentScore").val("");
        searchAppUserCommentRecord();
    })

    $("#goodCommentA").live("click", function () {
        $("#addMediumCommentScore").val("");
        $("#addBadCommentScore").val("");
        $("#addGoodCommentScore").val("4,5,");
        searchAppUserCommentRecord();
    })
    $("#mediumCommentA").live("click", function () {
        $("#addGoodCommentScore").val("");
        $("#addBadCommentScore").val("");
        $("#addMediumCommentScore").val("3,");
        searchAppUserCommentRecord();
    })
    $("#badCommentA").live("click", function () {
        $("#addGoodCommentScore").val("");
        $("#addMediumCommentScore").val("");
        $("#addBadCommentScore").val("1,2,");
        searchAppUserCommentRecord();
    })

    //记录选择的单据类型
    $('input[name="orderType"]').bind("click", function () {
        var orderTypeListValue = "";
        $('input[name="orderType"]:checked').each(function () {
            orderTypeListValue += $(this).val() + ",";
        })
        $("#orderType").val(orderTypeListValue);
        $("#addMediumCommentScore").val("");
        $("#addBadCommentScore").val("");
        $("#addGoodCommentScore").val("");
        searchAppUserCommentRecord();
    })
    $("#receiptNo").bind("click", function () {
        $("#receiptNo").css({color: "#000000"});
        if ($("#receiptNo").val() == "单据号") {
            $("#receiptNo").val("");
        }

    })
    //查询按钮响应事件
    $("#searchCommentData").bind("click", function () {
        searchAppUserCommentRecord();
    })
    $("#cleanCondition").bind("click", function () {
        $("input[type='checkbox']").attr("checked", false);
        $(".clicked").removeClass("clicked");
        $("#customerInfoText").val("手机号/车牌号/客户名");
        $("#customerInfoText").css({width: "195px", color: "#ADADAD"});
        $("#receiptNo").val("单据号");
        $("#receiptNo").css({color: "#ADADAD"});
        $("#startDate").val("");
        $("#endDate").val("");
        $("#commentTimeStart").val("");
        $("#commentTimeEnd").val("");
        $("#commentScore").val("");
        $("#addGoodCommentScore").val("");
        $("#addMediumCommentScore").val("");
        $("#addBadCommentScore").val("");
        $("#orderType").val("");
    })


    $(".unbind-btn").click(function() {
        var _$me = $(this);
        APP_BCGOGO.Net.asyncAjax({
            url: "shopData.do?method=unBindAccidentSpecial",
            data: {
                openId:$(this).attr("openId")
            },
            type: "POST",
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    _$me.closest(".j_accident_item").remove();
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
            }
        });
    });

});

function initShopInfo(shop) {
    if (G.isEmpty(shop)) return;
    //init shop base info
    $(".shopData [field='name']").text(G.normalize(shop.name));
    $(".shopData [field='address']").text(G.normalize(shop.address)).val(G.normalize(shop.address));
    $(".shopData [field='registrationDateStr']").text(G.normalize(shop.registrationDateStr));
    $(".shopData [field='legalRep']").text(G.normalize(shop.legalRep));
    $(".shopData [field='url']").text(G.normalize(shop.url)).val(G.normalize(shop.url));
    $(".shopData span[field='operationMode']").text(G.normalize(shop.operationModeStr));
    $(".shopData select[field='operationMode']").val(G.normalize(shop.operationModeStr));
    $(".shopData [field='licencePlate']").text(G.normalize(shop.licencePlate)).val(G.normalize(shop.licencePlate));
    $(".shopData [field='memo']").text(G.normalize(shop.memo)).val(G.normalize(shop.memo));
    $(".shopData [field='shopVehicleBrandModel']").text(G.normalize(shop.shopVehicleBrandModelStr));
    $(".shopData [field='businessScopeStr']").text(G.normalize(shop.businessScopeStr));
    $("#shop_province").val(G.normalize(shop.province));
    $("#shop_city").val(G.normalize(shop.city));
    $("#shop_region").val(G.normalize(shop.region));
    //init contact
    var contacts = shop.contacts;
    if (!G.isEmpty(contacts)) {
        for (var i = 0; i < contacts.length; i++) {
            var contact = contacts[i];
            if (G.isEmpty(contact)) continue;
            var contactStr = ".contact-" + (i + 1);
            $(contactStr + " [field='contact-name']").text(G.normalize(contact.name)).val(G.normalize(contact.name));
            $(contactStr + " [field='contact-mobile']").text(G.normalize(contact.mobile)).val(G.normalize(contact.mobile));
            $(contactStr + " [field='contact-email']").text(G.normalize(contact.email)).val(G.normalize(contact.email));
            $(contactStr + " [field='contact-qq']").text(G.normalize(contact.qq)).val(G.normalize(contact.qq));         //例：$(".contact-1 .contact-qq")
        }
    }

    //init RQ image 二维码图片
    var imageCenterDTO = shop.imageCenterDTO;
    if (imageCenterDTO != null) {
        var shopRQImageDetailDTO = imageCenterDTO.shopRQImageDetailDTO;
        if (shopRQImageDetailDTO != null) {
            var imageURL = shopRQImageDetailDTO.imageURL;
            $("#rq").attr("src", imageURL);
        }
    }


    //initShopRegisterProductTable
    var trStr = "";
    var products = shop.productDTOs;
    if (G.isEmpty(products)) {
        trStr = "<tr><td colspan='8' style='text-align: center;'>没有主营商品信息</td></tr>";
    } else {
        var trStr = "";
        for (var i = 0; i < products.length; i++) {
            var product = products[i];
            trStr += '<tr>';
            trStr += '<td>' + G.normalize(product.commodityCode) + '</td>';
            trStr += '<td>' + G.normalize(product.name) + '</td>';
            trStr += '<td>' + G.normalize(product.brand) + '</td>';
            trStr += '<td>' + G.normalize(product.spec) + '</td>';
            trStr += '<td>' + G.normalize(product.model) + '</td>';
            trStr += '<td>' + G.normalize(product.vehicleModel) + '</td>';
            trStr += '<td>' + G.normalize(product.vehicleBrand) + '</td>';
            trStr += '<td>' + G.normalize(product.sellUnit) + '</td>';
            trStr += '</tr>';
        }
    }
    $("#shopRegisterProductTable").append(trStr);
}

function initSupplierCommentRecord(data) {

//  $("#supplierCommentRecordTable tr:not(:first)").remove();
    $("#supplierCommentRecordTable tr:gt(1)").remove();
    if (data == null || data[0] == null || data[0].recordDTOList == null || data[0].recordDTOList == 0) {
        $("#noSupplierCommentRecord").css("display", "block");
        return;
    } else {
        $("#noSupplierCommentRecord").css("display", "none");
    }
    var trStr = "";
    $.each(data[0].recordDTOList, function (index, order) {
        var commentTimeStr = (!order.commentTimeStr ? "---" : order.commentTimeStr);
        var commentContent = (!order.commentContent ? "" : order.commentContent);
        var firstCommentContent = (!order.firstCommentContent ? "" : order.firstCommentContent);
        var addCommentContent = (!order.addCommentContent ? "" : order.addCommentContent);
        var customer = (!order.customer ? "---" : order.customer);
        var qualityScore = G.normalize(order.qualityScore);
        var performanceScore = G.normalize(order.performanceScore);
        var speedScore = G.normalize(order.speedScore);
        var attitudeScore = G.normalize(order.attitudeScore);

        trStr += '<tr>';
        trStr += '<td class="col-no txtl"><div class="item-td item-td-first">' + (index + 1) + "</div></td>";
        trStr += '<td class="col-time txtl"><div class="item-td">' + commentTimeStr + "</div></td>";

        trStr += '<td class="col-score txtl">';
        trStr += '<div class="item-td"><ul>';
//            '<div class="shopTit" ><label >货品质量</label><a class="bigStar" style="background-position:0px ' + '-'+ order.qualityScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.qualityScore +'</span>分</div>' +
//            '<div class="shopTit" ><label >货品性价比</label><a class="bigStar" style="background-position:0px ' + '-'+ order.performanceScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.performanceScore +'</span>分</div>' +
//            '<div class="shopTit" ><label >发货速度</label><a class="bigStar" style="background-position:0px ' + '-'+ order.speedScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.speedScore +'</span>分</div>' +
//            '<div class="shopTit" ><label >服务态度</label><a class="bigStar" style="background-position:0px ' + '-'+ order.attitudeScoreSpan  + 'px;"' +'></a>&nbsp;<span class="color_yellow">'+ order.attitudeScore +'</span>分</div>' +
//            '</td>';
        trStr += '<li><div class="rate-name fl">货品质量</div>';
        trStr += '<div class="rate-star fl normal-light-star-level-' + qualityScore * 2 + '"></div>';
        trStr += '<div class="rate-score fl"><span class="number-red">' + qualityScore + '</span>分</div>';
        trStr += '<div class="cl"></div></li>';

        trStr += '<li><div class="rate-name fl">货品性价比</div>';
        trStr += '<div class="rate-star fl normal-light-star-level-' + performanceScore * 2 + '"></div>';
        trStr += '<div class="rate-score fl"><span class="number-red">' + order.performanceScore + '</span>分</div>';
        trStr += '<div class="cl"></div></li>';

        trStr += '<li><div class="rate-name fl">发货速度</div>';
        trStr += '<div class="rate-star fl normal-light-star-level-' + speedScore * 2 + '"></div>';
        trStr += '<div class="rate-score fl"><span class="number-red">' + speedScore + '</span>分</div>';
        trStr += '<div class="cl"></div></li>';

        trStr += '<li><div class="rate-name fl">服务速度</div>';
        trStr += '<div class="rate-star fl normal-light-star-level-' + attitudeScore * 2 + '"></div>';
        trStr += '<div class="rate-score fl"><span class="number-red">' + attitudeScore + '</span>分</div>';
        trStr += '<div class="cl"></div></li>';

        trStr += '</ul></div></td>';

        trStr += '<td class="col-comment txtl"><div class="item-td"><div class="comment-main">' + firstCommentContent + '</div>';
        trStr += '<div class="comment-additional">' + addCommentContent + '</div></div></td>';


        trStr += '<td class="col-customer txtl"><div class="item-td item-td-last">' + customer + "</div></td>";
        trStr += "</tr>";
    });
    $("#supplierCommentRecordTable").empty().append($(trStr));
}
var commentHolder = {};
function initShopCommentDetail(shopInfo) {
    $(".shop-name").text(G.normalize(shopInfo.name));
    var supplierCommentStat = shopInfo.commentStatDTO;
    if (G.isEmpty(supplierCommentStat)) {
        $("#shopTotalScore").text("暂无分数");
        $("#shopTotalScoreStar").addClass("normal-light-star-level-0");
        $("#qualityTotalScore").text("暂无分数");
        $("#performanceTotalScore").text("暂无分数");
        $("#speedTotalScore").text("暂无分数");
        $("#attitudeTotalScore").text("暂无分数");
    } else {
        var totalScore = G.rounding(supplierCommentStat.totalScore);
        $("#shopTotalScore").text(totalScore == 0 ? "暂无分数" : (supplierCommentStat.totalScore + "分"));
        var totalScoreWidth = supplierCommentStat.totalScoreWidth;

        $("#shopTotalScoreStar").addClass("normal-light-star-level-" + totalScoreWidth);
        var qualityTotalScore = G.rounding(supplierCommentStat.qualityTotalScore);
        $("#qualityTotalScore").text(qualityTotalScore == 0 ? "暂无分数" : (supplierCommentStat.qualityTotalScore + "分"));
        var performanceTotalScore = G.rounding(supplierCommentStat.performanceTotalScore);
        $("#performanceTotalScore").text(performanceTotalScore == 0 ? "暂无分数" : (supplierCommentStat.performanceTotalScore + "分"));
        var speedTotalScore = G.rounding(supplierCommentStat.speedTotalScore);
        $("#speedTotalScore").text(speedTotalScore == 0 ? "暂无分数" : (supplierCommentStat.speedTotalScore + "分"));
        var attitudeTotalScore = G.rounding(supplierCommentStat.attitudeTotalScore);
        $("#attitudeTotalScore").text(attitudeTotalScore == 0 ? "暂无分数" : (supplierCommentStat.attitudeTotalScore + "分"));
        $(".recordAmountSpan").text(G.rounding(supplierCommentStat.recordAmount));
        commentHolder["quality"] = {};
        commentHolder["quality"].totalScore = G.rounding(supplierCommentStat.qualityTotalScore);
        commentHolder["quality"].fiveAmountPer = G.normalize(supplierCommentStat.qualityFiveAmountPer);
        commentHolder["quality"].fourAmountPer = G.normalize(supplierCommentStat.qualityFourAmountPer);
        commentHolder["quality"].threeAmountPer = G.normalize(supplierCommentStat.qualityThreeAmountPer);
        commentHolder["quality"].twoAmountPer = G.normalize(supplierCommentStat.qualityTwoAmountPer);
        commentHolder["quality"].oneAmountPer = G.normalize(supplierCommentStat.qualityOneAmountPer);

        commentHolder["performance"] = {};
        commentHolder["performance"].totalScore = G.rounding(supplierCommentStat.performanceTotalScore);
        commentHolder["performance"].fiveAmountPer = G.normalize(supplierCommentStat.performanceFiveAmountPer);
        commentHolder["performance"].fourAmountPer = G.normalize(supplierCommentStat.performanceFourAmountPer);
        commentHolder["performance"].threeAmountPer = G.normalize(supplierCommentStat.performanceThreeAmountPer);
        commentHolder["performance"].twoAmountPer = G.normalize(supplierCommentStat.performanceTwoAmountPer);
        commentHolder["performance"].oneAmountPer = G.normalize(supplierCommentStat.performanceOneAmountPer);

        commentHolder["speed"] = {};
        commentHolder["speed"].totalScore = G.rounding(supplierCommentStat.speedTotalScore);
        commentHolder["speed"].fiveAmountPer = G.normalize(supplierCommentStat.speedFiveAmountPer);
        commentHolder["speed"].fourAmountPer = G.normalize(supplierCommentStat.speedFourAmountPer);
        commentHolder["speed"].threeAmountPer = G.normalize(supplierCommentStat.speedThreeAmountPer);
        commentHolder["speed"].twoAmountPer = G.normalize(supplierCommentStat.speedTwoAmountPer);
        commentHolder["speed"].oneAmountPer = G.normalize(supplierCommentStat.speedOneAmountPer);

        commentHolder["attitude"] = {};
        commentHolder["attitude"].totalScore = G.rounding(supplierCommentStat.attitudeTotalScore);
        commentHolder["attitude"].fiveAmountPer = G.normalize(supplierCommentStat.attitudeFiveAmountPer);
        commentHolder["attitude"].fourAmountPer = G.normalize(supplierCommentStat.attitudeFourAmountPer);
        commentHolder["attitude"].threeAmountPer = G.normalize(supplierCommentStat.attitudeThreeAmountPer);
        commentHolder["attitude"].twoAmountPer = G.normalize(supplierCommentStat.attitudeTwoAmountPer);
        commentHolder["attitude"].oneAmountPer = G.normalize(supplierCommentStat.attitudeOneAmountPer);
    }
}

function saveShopBaseInfo() {
    if (G.isEmpty($(".area-province option:selected").val())) {
        nsDialog.jAlert("请选择省份。");
        return;
    }
    if (G.isEmpty($(".area-city option:selected").val())) {
        nsDialog.jAlert("请选择城市。");
        return;
    }
    var landline = $(".shopData input[field='landline']").val();
    if (!G.isEmpty(G.trim(landline))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(landline)) {
            nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
            return false;
        }
    }
    var accidentMobile = $(".shopData input[field='accidentMobile']").val();
    if (!G.isEmpty(G.trim(accidentMobile))) {
        if (!(APP_BCGOGO.Validator.stringIsMobile(accidentMobile)
            || APP_BCGOGO.Validator.stringIsTelephoneNumber(accidentMobile))) {
            nsDialog.jAlert("事故专员手机号码校验错误，请确认后重新输入！");
            return false;
        }
    }

    if (!APP_BCGOGO.Permission.WholesalerVersion && validateServiceCategoryNotEmpty()) {
        nsDialog.jAlert("请选择服务范围。");
        return;
    }
    var region = "";
    if (!G.isEmpty($(".area-region option:selected").val())) {
        region = $(".area-region option:selected").text()
    }
    var address = $(".shopData input[field='address']").val();
    var url = $(".shopData input[field='url']").val();
    var licencePlate = $(".shopData input[field='licencePlate']").val();
    var memo = $(".shopData input[field='memo']").val();
    var operationMode = $(".shopData select[field='operationMode']").val();
    var data = {};
    if ($("#serviceCategoryCheck")[0]) {
        var serviceCategoryIds = '';
        $("#serviceCategoryCheck :checkbox").each(function () {
            if ($(this).attr("checked")) {
                serviceCategoryIds += $(this).val() + ',';
            }
        });
        if (serviceCategoryIds != '') {
            serviceCategoryIds = serviceCategoryIds.substring(0, serviceCategoryIds.length - 1);
        }
        data = {
            address: address,
            province: $(".area-province option:selected").val(),
            city: $(".area-city option:selected").val(),
            region: $(".area-region option:selected").val(),
            url: url,
            licencePlate: licencePlate,
            operationMode: operationMode,
            landline: landline,
            memo: memo,
            serviceCategoryIdStr: serviceCategoryIds,
            accidentMobile: accidentMobile
        }
    } else {
        data = {
            address: address,
            province: $(".area-province option:selected").val(),
            city: $(".area-city option:selected").val(),
            region: $(".area-region option:selected").val(),
            url: url,
            licencePlate: licencePlate,
            operationMode: operationMode,
            landline: landline,
            memo: memo,
            accidentMobile: accidentMobile
        }
    }

    APP_BCGOGO.Net.asyncAjax({
        url: "shopData.do?method=saveShopInfo",
        data: data,
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (result) {
            if (!G.isEmpty(result) && !result.success) {
                nsDialog.jAlert(result.msg);
                return;
            } else {
                toEditingStatus('.info-basic', false);
                $(".shopData span[field='address']").text(address);
                $(".shopData span[field='url']").text($(".shopData input[field='url']").val());
                $(".shopData span[field='operationMode']").text($(".shopData select[field='operationMode']").val());
                $(".shopData span[field='licencePlate']").text($(".shopData input[field='licencePlate']").val());
                $(".shopData span[field='memo']").text($(".shopData input[field='memo']").val());
                $(".shopData span[field='landline']").text($(".shopData input[field='landline']").val());
                $(".shopData span[field='address']").text($(".shopData input[field='address']").val());
                $(".shopData span[field='accidentMobile']").text($(".shopData input[field='accidentMobile']").val());
                $("#shop_province").val($(".area-province option:selected").val());
                $("#shop_city").val($(".area-city option:selected").val());
                $("#shop_region").val($(".area-region option:selected").val());
                generateServiceCategoryStr();
                nsDialog.jAlert("更新成功！");
            }
        },
        error: function () {
            cancelSaveShopBaseInfo();
            nsDialog.jAlert("网络异常！");
        }
    });
}

function doEditShopBaseInfo() {
    toEditingStatus('.info-basic', true);

    $(".area-province").val($("#shop_province").val());
    $(".area-province").change();
    $(".area-city").val($("#shop_city").val());
    $(".area-city").change();
    $(".area-region ").val($("#shop_region").val());
    $(".shopData input[field='address']").val($(".shopData span[field='address']").text());
}

function cancelSaveShopBaseInfo() {
    toEditingStatus('.info-basic', false);
    $(".shopData input[field='address']").val($(".shopData span[field='address']").text());
    $(".shopData input[field='url']").val($(".shopData span[field='url']").text());
    $(".shopData select[field='operationMode']").val($(".shopData span[field='operationMode']").text());
    $(".shopData input[field='licencePlate']").val($(".shopData span[field='licencePlate']").text());
    $(".shopData input[field='memo']").val($(".shopData span[field='memo']").text());
    $(".shopData input[field='accidentMobile']").val($(".shopData span[field='accidentMobile']").text());
    if ($("#serviceCategoryIds")[0]) {
        generateServiceCategoryCheck($("#serviceCategoryIds").val());
    }
}

/**
 * 校验新增的手机列表里面号码是否有重复
 * 重复返回true 否则false
 * @param mobiles
 */
function isMobileDuplicate(mobiles) {

    var mobilesTemp = new Array();
    for (var index in  mobiles) {
        if (!G.isEmpty(mobiles[index])) {
            if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
                nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
                return true;
            }
            mobilesTemp.push(mobiles[index]);
        }
    }
    return false;
}

function saveShopContact() {
    var errorMsg = "";
    $(".j_info_contract input[field='contact-mobile']").each(function (i) {
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        if (!GLOBAL.isEmpty(thisVal) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(_thisDom.val(), null, null)) {
            errorMsg += "第" + (i + 2) + "行手机号格式错误；";
        }
    });
    if (!G.isEmpty(errorMsg)) {
        nsDialog.jAlert(errorMsg);
        return;
    }
    var contactMobiles = new Array();
    contactMobiles.push($('.j_info_contract .contact-1 span[field="contact-mobile"]').text());
    contactMobiles.push($('.j_info_contract .contact-2 input[field="contact-mobile"]').val());
    contactMobiles.push($('.j_info_contract .contact-3 input[field="contact-mobile"]').val());

    if (isMobileDuplicate(contactMobiles)) {
        nsDialog.jAlert("联系人手机号不能重复。");
        return;
    }
    $("input[field='contact-email']").each(function (i) {
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        if (!GLOBAL.isEmpty(thisVal) && !APP_BCGOGO.Validator.stringIsEmail(_thisDom.val(), null, null)) {
            errorMsg += "第" + (i + 1) + "行email号格式错误；";
        }
    });
    if (!G.isEmpty(errorMsg)) {
        nsDialog.jAlert(errorMsg);
        return;
    }
    $(".j_info_contract input[field='contact-qq']").each(function (i) {
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        if (!GLOBAL.isEmpty(thisVal) && !APP_BCGOGO.Validator.stringIsQq(_thisDom.val(), null, null)) {
            errorMsg += "第" + (i + 1) + "行QQ号格式错误；";
        }
    });
    if (!G.isEmpty(errorMsg)) {
        nsDialog.jAlert(errorMsg);
        return;
    }
    var data = {};
    $(".j_contract_item").each(function (i) {
        var cName = "";
        var cMobile = "";
        if ($(this).hasClass("contact-1")) {
            cName = $(this).find("span[field='contact-name']").text();
            cMobile = $(this).find("span[field='contact-mobile']").text();
        } else {
            cName = $(this).find("input[field='contact-name']").val();
            cMobile = $(this).find("input[field='contact-mobile']").val();
        }

        var cEmail = $(this).find("input[field='contact-email']").val();
        var cQQ = $(this).find("input[field='contact-qq']").val();
        if (!(G.isEmpty(cName) && G.isEmpty(cMobile) && G.isEmpty(cEmail) && G.isEmpty(cQQ))) {
            data["contacts[" + i + "].name"] = cName;
            data["contacts[" + i + "].mobile"] = cMobile;
            data["contacts[" + i + "].email"] = cEmail;
            data["contacts[" + i + "].qq"] = cQQ;
            data["contacts[" + i + "].level"] = $(this).attr("level");
            data["contacts[" + i + "].isMainContact"] = $(this).attr("isMainContact");
            data["contacts[" + i + "].isShopOwner"] = $(this).attr("isShopOwner");
        }
    });

    APP_BCGOGO.Net.asyncAjax({
        url: "shopData.do?method=saveShopContacts",
        data: data,
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (result) {
            if (!G.isEmpty(result) && !result.success) {
                nsDialog.jAlert(result.msg);
                return;
            } else {
                toEditingStatus('.info-contract', false)
                $(".contract-item").each(function () {
                    $(this).find("span[field='contact-name']").text($(this).find("input[field='contact-name']").val());
                    $(this).find("span[field='contact-mobile']").text($(this).find("input[field='contact-mobile']").val());
                    $(this).find("span[field='contact-email']").text($(this).find("input[field='contact-email']").val());
                    var qq = $(this).find("input[field='contact-qq']").val();
                    $(this).find("span[field='contact-qq']").text(qq);
                    if (APP_BCGOGO.Permission.WholesalerVersion) {
                        var $qqIcon = $(this).find("[field='contact-qq-icon']");
                        $qqIcon.multiQQInvoker({
                            QQ: [qq], QQIcoStyle: 51, callBack: function () {
                                if ($qqIcon.height() == 24 && $qqIcon.width() == 78) {    //判断未开启qq商家的qq号，目前没发现更好的方法来判断
                                    $qqIcon.attr("href", "http://wp.qq.com/consult.html");
                                    $qqIcon.find("img").attr("title", "开通qq商家");
                                }
                            }
                        });

                    }
                });
                nsDialog.jAlert("更新成功！");
            }
        },
        error: function () {
            cancelSaveShopContact();
            nsDialog.jAlert("网络异常！");
        }
    });
}


function saveAccident() {
    var errorMsg = "";
    //var $nameInput = $(".j_info_accident input[field='accident-name']");
    //$nameInput.each(function (i) {
    //    var _thisDom = $(this);
    //    if (GLOBAL.isEmpty(_thisDom.val())) {
    //        errorMsg += "第" + (i + 1) + "行专员名不能为空；";
    //    }
    //});
    //if (!G.isEmpty(errorMsg)) {
    //    nsDialog.jAlert(errorMsg);
    //    return;
    //}
    var $nameMobile = $(".j_info_accident input[field='accident-mobile']");
    $nameMobile.each(function (i) {
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        if (!GLOBAL.isEmpty(thisVal) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(_thisDom.val(), null, null)) {
            errorMsg += "第" + (i + 1) + "行手机号格式错误；";
        }
    });
    if (!G.isEmpty(errorMsg)) {
        nsDialog.jAlert(errorMsg);
        return;
    }
    var mobiles = new Array();
    mobiles.push($('.accident-1 span[field="accident-mobile"]').text());
    mobiles.push($('.accident-2 input[field="accident-mobile"]').val());
    mobiles.push($('.accident-3 input[field="accident-mobile"]').val());

    if (isMobileDuplicate(mobiles)) {
        nsDialog.jAlert("专员手机号不能重复。");
        return;
    }
    var data = {};
    $(".j_accident_item").each(function (i) {
        var name = $(this).find("input[field='accident-name']").val();
        var mobile = $(this).find("input[field='accident-mobile']").val();
        var openId = $(this).find("input[field='accident-openId']").val();
        var id = $(this).find("input[field='accident-id']").val();
        data["specialistDTOs[" + i + "].name"] = name;
        data["specialistDTOs[" + i + "].mobile"] = mobile;
        data["specialistDTOs[" + i + "].openId"] = openId;
        data["specialistDTOs[" + i + "].id"] = id;
    });

    APP_BCGOGO.Net.asyncAjax({
        url: "shopData.do?method=saveAccidentSpecialist",
        data: data,
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (result) {
            if (!G.isEmpty(result) && !result.success) {
                nsDialog.jAlert(result.msg);
                return;
            } else {
                toEditingStatus('.j_info_accident', false)
                $(".j_accident_item").each(function () {
                    $(this).find("span[field='accident-name']").text($(this).find("input[field='accident-name']").val());
                    $(this).find("span[field='accident-mobile']").text($(this).find("input[field='accident-mobile']").val());
                });
                nsDialog.jAlert("更新成功！");
            }
        },
        error: function () {
            cancelSaveAccidentSpecialist();
            nsDialog.jAlert("网络异常！");
        }
    });
}


function cancelSaveShopContact() {
    toEditingStatus('.j_info_contract', false);
    var $contracts = $(".j_contract_item");
    if (!G.isEmpty($contracts)) {
        for (var i = 0; i < $contracts.size(); i++) {
            var $contact = $($contracts.get(i));
            $contact.find("input[field='contact-name']").val($contact.find("span[field='contact-name']").text());
            $contact.find("input[field='contact-mobile']").val($contact.find("span[field='contact-mobile']").text());
            $contact.find("input[field='contact-email']").val($contact.find("span[field='contact-email']").text());
            $contact.find("input[field='contact-qq']").val($contact.find("span[field='contact-qq']").text());
        }
    }
}

function cancelSaveAccidentSpecialist() {
    toEditingStatus('.j_info_accident', false);
    var $accidents = $(".j_accident_item");
    if (!G.isEmpty($accidents)) {
        for (var i = 0; i < $accidents.size(); i++) {
            var $accident = $($accidents.get(i));
            $accident.find("input[field='accident-name']").val($contact.find("span[field='accident-name']").text());
            $accident.find("input[field='accident-mobile']").val($contact.find("span[field='accident-mobile']").text());
        }
    }
}


function toEditingStatus(node, isEditing) {
    var $node = $(node);
    if ($node[0]) {
        $node.find(".block-saved").toggle(!isEditing);
        $node.find(".block-editing").toggle(isEditing);
        $node.find(".button-info-edit").toggle(!isEditing);
        $node.find(".button-info-save").toggle(isEditing);
    }
}


function toManageShopData() {
    window.location.href = "shopData.do?method=toManageShopData";
}

function toShopComment() {
    window.location.href = "shopData.do?method=toShopComment";
}

function initAppUserCommentRecord(data) {
    $("#appUserCommentTable tr:not(:first)").remove();
    if (!data || !data.appUserCommentRecordDTOs || data.appUserCommentRecordDTOs.length == 0) {
        $("#averageComment").css("display", "none");
        $("#noComment").css("display", "block");
        $("#appUserCommentTableDiv").css("display", "none");
        return;
    } else {
        $("#averageComment").css("display", "block");
        $("#noComment").css("display", "none");
        $("#appUserCommentTableDiv").css("display", "");
    }
    //综合评分
    if (data.commentStatDTO) {
        var averageScore = data.commentStatDTO.averageScore;


        var totalScoreWidth = data.commentStatDTO.totalScoreWidth;

        $("#averageScoreAmount").text(averageScore == 0 ? "暂无分数" : averageScore + "分");
        $("#averageCommentStar").addClass("normal-light-star-level-" + totalScoreWidth);
        $("#recordAmount").text(data.commentStatDTO.recordAmount);
    }
    //评分列表
    for (var i = 0; i < data.appUserCommentRecordDTOs.length; i++) {
        var html = '';
        var appUserCommentRecord = data.appUserCommentRecordDTOs[i];
        var commentTimeStr = appUserCommentRecord.commentTimeStr == null ? "" : appUserCommentRecord.commentTimeStr;
        var commentScore = appUserCommentRecord.commentScore = null ? "0" : appUserCommentRecord.commentScore;
        var commentContent = appUserCommentRecord.commentContent == null ? "" : appUserCommentRecord.commentContent;
        var commentator = appUserCommentRecord.commentator == null ? "" : appUserCommentRecord.commentator;
        var receiptNo = appUserCommentRecord.receiptNo == null ? "" : appUserCommentRecord.receiptNo;
        var orderType = appUserCommentRecord.orderType;
        var orderIdStr = appUserCommentRecord.orderIdStr == null ? "" : appUserCommentRecord.orderIdStr;
        html += '<tr class="space"><td colspan="5"></td></tr>';
        html += '<tr class="offerBg"><td style="padding-left:10px;">' + (i + 1) + '</td>';
        html += '<td>' + commentTimeStr + '</td>';
        html += '<td><div class="shopTit"><a class="normal-light-star-level-' + G.rounding(commentScore, 0) * 2 + '"></a>&nbsp;<span class="yellow_color">' + commentScore + '</span>分</div></td>';
        html += '<td>' + commentContent + '</td>';
        html += '<td>' + commentator + '</td>';
        if ('WASH_BEAUTY' == orderType) {
            html += '<td><a class="blue_color" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderIdStr + '">' + receiptNo + '</a></td>';
        } else {
            html += '<td><a class="blue_color" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderIdStr + '">' + receiptNo + '</a></td>';
        }

        html += '</tr>';
        $("#appUserCommentTable").append(html);
    }
}

//初始化带搜索条件的评价列表
function initAppCommentRecord(data) {
    $("#appUserCommentTable tr:not(:first)").remove();
    if ($("#evaluationResultDiv").length > 0) {
        $("#evaluationResultDiv").remove();
    }
    if ($("#noCommentDiv").length > 0) {
        $("#noCommentDiv").remove();
    }
    if ($("#recordTitleDiv").length > 0) {
        $("#recordTitleDiv").remove();
    }
    //评价结果统计行
    var evaluationResult = "";
    if (data.commentStatDTO == null) {
        evaluationResult = '<div id="evaluationResultDiv"><p><strong>共0条记录：</strong>';
        evaluationResult += '<span><a>差评（<b class="yellow_color">0</b>）</a>&nbsp;</span>&nbsp;&nbsp;';
        evaluationResult += ' <span><a>中评（<b class="yellow_color">0</b>）</a>&nbsp;</span>&nbsp;&nbsp;';
        evaluationResult += ' <span><a>好评（<b class="yellow_color">0</b>）</a>&nbsp;</span></p></div>';
    } else {
        evaluationResult = '<div id="evaluationResultDiv"><p><strong>共' + (data.commentStatDTO.goodCommentAmount + data.commentStatDTO.mediumCommentAmount + data.commentStatDTO.badCommentAmount) + '条记录：</strong>';
        evaluationResult += '<span><a id="badCommentA">差评（<b class="yellow_color">' + data.commentStatDTO.badCommentAmount + '</b>）</a>&nbsp;</span>&nbsp;&nbsp;';
        evaluationResult += '<span><a id="mediumCommentA">中评（<b class="yellow_color">' + data.commentStatDTO.mediumCommentAmount + '</b>）</a>&nbsp;</span>&nbsp;&nbsp;';
        evaluationResult += '<span><a id="goodCommentA">好评（<b class="yellow_color">' + data.commentStatDTO.goodCommentAmount + '</b>）</a>&nbsp;</span></p></div>';
        //综合评分
        if (data.commentStatDTO) {
            var averageScore = data.commentStatDTO.averageScore;
            var totalScoreWidth = data.commentStatDTO.totalScoreWidth;
            var recordTitle = '<div id="recordTitleDiv"><div class="lineTop"></div><div class="lineBody"><div class="shopevaluation" id="averageComment"><b>本店综合评分 </b><a id="averageCommentStar" class="star normal-light-star-level-' + totalScoreWidth + '"></a>';
            recordTitle += '<strong class="yellow_color" id="averageScoreAmount"> ';
            if (averageScore == 0) {
                recordTitle += '暂无分数 ';
            } else {
                recordTitle += averageScore + '分 ';
            }
            recordTitle += '</strong><span style="color:#999999;">共<span id="recordAmount">' + data.commentStatDTO.recordAmount + '</span>次服务被评价</span></div></div><div class="lineBottom"></div></div>';
            $("#recordTitle").append(recordTitle);
        }
    }

    $("#evaluationResult").append(evaluationResult);
    var html = "";
    if (!data || data.appUserCommentRecordDTOs == null || data.appUserCommentRecordDTOs.length == 0) {
        html += '<div id="noCommentDiv"><div class="shopevaluation" style="display:block;color:#999999;" id="noComment">暂无评价！</div></div>';
    } else {
        //评分列表
        for (var i = 0; i < data.appUserCommentRecordDTOs.length; i++) {
            var appUserCommentRecord = data.appUserCommentRecordDTOs[i];
            var commentTimeStr = appUserCommentRecord.commentTimeStr == null ? "" : appUserCommentRecord.commentTimeStr;
            var commentScore = appUserCommentRecord.commentScore = null ? "0" : appUserCommentRecord.commentScore;
            var commentContent = appUserCommentRecord.commentContent == null ? "" : appUserCommentRecord.commentContent;
            var customerName = appUserCommentRecord.customerName == null ? "" : appUserCommentRecord.customerName;
            var mobile = appUserCommentRecord.mobile == null ? "" : appUserCommentRecord.mobile;
            var receiptNo = appUserCommentRecord.receiptNo == null ? "" : appUserCommentRecord.receiptNo;
            var vechicle = appUserCommentRecord.vechicle == null ? "" : appUserCommentRecord.vechicle;
            var orderType = appUserCommentRecord.orderType;
            var orderIdStr = appUserCommentRecord.orderIdStr == null ? "" : appUserCommentRecord.orderIdStr;
            html += '<tr class="space"><td colspan="7"></td></tr><tr class="titBody_Bg">';  //小空行
            if ('WASH_BEAUTY' == orderType) {
                html += '<td><a class="blue_color" target="_blank" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderIdStr + '">' + receiptNo + '</a></td>';//单据号
                html += '<td>洗车美容</td>';   //单据类型
            } else if ('SALE' == orderType) {
                html += '<td><a class="blue_color" target="_blank" href="sale.do?method=toSalesOrder&salesOrderId=' + orderIdStr + '">' + receiptNo + '</a></td>';//单据号
                html += '<td>销售单</td>';   //单据类型
            } else {
                html += '<td><a class="blue_color" target="_blank" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderIdStr + '">' + receiptNo + '</a></td>';//单据号
                html += '<td>施工单</td>';   //单据类型
            }
            html += '<td>' + vechicle + '</td>';   //车牌号
            html += '<td>' + customerName + '</br>' + mobile + '</td>';//客户
            html += '<td><div><a class="normal-light-star-level-' + G.rounding(commentScore, 0) * 2 + '">';//评分
            if (commentScore >= 1 && commentScore <= 2) {
                html += '差评(';
            } else if (commentScore == 3) {
                html += '中评(';
            } else if (commentScore >= 4 && commentScore <= 5) {
                html += '好评(';
            }
            html += '<span class="yellow_color">' + commentScore + '</span>分)</a></div></td>';
            html += '<td>' + commentContent + '</td>';//详细评价
            html += '<td>' + commentTimeStr + '</td>';   //日期
            html += '</tr>';
        }
    }
    $("#appUserCommentTable").append(html);


}

function generateServiceCategoryCheck(serviceCategoryId) {
    $("#serviceCategoryCheck :checkbox").attr("checked", false);
    if (serviceCategoryId == '') {
        return;
    }
    var serviceCategoryIdArray = serviceCategoryId.split(",");
    for (var i = 0; i < serviceCategoryIdArray.length; i++) {
        $("#serviceCategoryCheck :checkbox").each(function () {
            if ($(this).val() == serviceCategoryIdArray[i]) {
                $(this).attr("checked", true);
            }
        });
    }
}

function generateServiceCategoryStr() {
    var serviceCategoryStr = '';
    var serviceCategoryIdStr = '';
    $("#serviceCategoryCheck :checkbox").each(function () {
        if ($(this).attr("checked")) {
            serviceCategoryStr += $(this).attr("fieldName") + ',';
            serviceCategoryIdStr += $(this).val() + ',';
        }
    });
    $(".shopData [field='shopServiceCategory']").text(G.normalize(serviceCategoryStr.substring(0, serviceCategoryStr.length - 1)));
    $("#serviceCategoryIds").val(G.normalize(serviceCategoryIdStr.substring(0, serviceCategoryIdStr.length - 1)));
}

function validateServiceCategoryNotEmpty() {
    var allNotSelect = true;
    $("#serviceCategoryCheck :checkbox").each(function () {
        if ($(this).attr("checked")) {
            allNotSelect = false;
            return false;
        }
    });
    return allNotSelect;
}

function searchAppUserCommentRecord() {
    var ajaxData = beforeSearchAppUserCommentRecord();
    var ajaxUrl = "supplier.do?method=getAppCommentRecordByKeyword";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (data) {
        initAppCommentRecord(data);
        initPage(data, "appShopComment", ajaxUrl, '', "initAppCommentRecord", '', '', ajaxData, '');
    })
}

function beforeSearchAppUserCommentRecord() {
    var ajaxCommentRecord = null,
        commentTimeStart = $("#commentTimeStart").val(),
        commentTimeEnd = $("#commentTimeEnd").val(),
        commentScoreStr = $("#commentScore").val(),
        addGoodCommentScoreStr = $("#addGoodCommentScore").val(),
        addMediumCommentScoreStr = $("#addMediumCommentScore").val(),
        addBadCommentScoreStr = $("#addBadCommentScore").val(),
        orderTypeStr = $("#orderType").val(),
        customerName = $("#customerInfoText").val() == "手机号/车牌号/客户名" ? "" : $("#customerInfoText").val(),
        receiptNo = $("#receiptNo").val() == "单据号" ? "" : $("#receiptNo").val(),
        paramShopId = $("#paramShopId").val();
    ajaxCommentRecord = {
        commentTimeStartStr: commentTimeStart,
        commentTimeEndStr: commentTimeEnd,
        commentScoreStr: commentScoreStr,
        addGoodCommentScoreStr: addGoodCommentScoreStr,
        addMediumCommentScoreStr: addMediumCommentScoreStr,
        addBadCommentScoreStr: addBadCommentScoreStr,
        orderTypeStr: orderTypeStr,
        customerName: customerName,
        receiptNo: receiptNo,
        startPageNo: 1,
        maxRows: 15,
        paramShopId: paramShopId
    }
    return ajaxCommentRecord;
}
