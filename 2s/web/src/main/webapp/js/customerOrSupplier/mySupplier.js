/**
 * 我的供应商列表专用js
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-23
 * Time: 下午6:48
 * To change this template use File | Settings | File Templates.
 */

var defaultSortStatus = " last_inventory_time desc ";

$().ready(function () {
    var hasHover = false;
    $(".J_supplier_sort").each(function () {
        if ($(this).hasClass("hover")) {
            hasHover = true;
        }
    });
    if (!hasHover) {
        $("#lastInventoryTimeSort").addClass("hover");
    }

    $("#clearConditionBtn").bind("click", function() {
        $("a[name='my_date_select']").not(this).removeClass("clicked");
        $("#supplierId").val("");
        $("#supplierIds").val("");

        $("#provinceNo option").not(".default").remove();
        $("#cityNo option").not(".default").remove();
        $("#regionNo option").not(".default").remove();


        $("#supplierInfoText").val("");
        $("#supplierInfoText").blur();

        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");

        $("#date_self_define").click();

        $("a[name='date_select']").removeClass("clicked");
        $("#startDate").val("");
        $("#endDate").val("");

        $("#totalTradeAmountStart,#totalTradeAmountEnd,#totalReceivableStart,#totalReceivableEnd,#debtAmountStart,#debtAmountEnd").val("");
        $("#relationType").val("");
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        provinceBind();

        $("#provinceNo").css({"color": "#ADADAD"});
        $("#cityNo").css({"color": "#ADADAD"});
        $("#regionNo").css({"color": "#ADADAD"});
        $(".J_supplier_sort").each(function () {
            $(this).removeClass("hover");
        });

        $("#sortStatus").val(defaultSortStatus);
        $("#lastInventoryTimeSort").attr("currentSortStatus", "Desc");


    });

    $("#lastInventoryTimeSort").click(function(e) {
        var sortStr = "";
        if ($("#lastInventoryTimeSortSpan").hasClass("arrowDown")) {
            $("#lastInventoryTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " last_inventory_time asc ";
        } else {
            $("#lastInventoryTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " last_inventory_time desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();

    });

    $("#totalTradeAmountSort").click(function(e) {
        var sortStr = "";
        if ($("#totalTradeAmountSortSpan").hasClass("arrowDown")) {
            $("#totalTradeAmountSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " total_trade_amount asc ";
        } else {
            $("#totalTradeAmountSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " total_trade_amount desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();

    });

    $("#totalDebtSpan").click(function(e) {
        $("#hasDebt").val("true");
        $("#rowStart").val(0);
        $("#hasDeposit").val("");
//    $("#relationType").val("");
        $("#searchStrategy").val("");
        searchSupplierDataActionFilter();
        $("#hasDebt").val("");

    });

    $("#totalNumSpan").click(function(e) {
        $("#rowStart").val(0);
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
//    $("#relationType").val("");
        $("#searchStrategy").val("");
        searchSupplierDataActionFilter();
    });

    $("#totalDepositSpan").click(function(e) {
        $("#hasDeposit").val("true");
        $("#rowStart").val(0);
        $("#hasDebt").val("");
//    $("#relationType").val("");
        $("#searchStrategy").val("");
        searchSupplierDataActionFilter();
        $("#hasDeposit").val("");
    });

    $("#relatedSupplier").click(function() {
//    $("#relationType").val('APPLY_RELATED');
        $("#searchStrategy").val('customerOrSupplierShopIdNotEmpty');
        $("#rowStart").val(0);
        $("#hasDebt").val("");
        $("#hasDeposit").val("");
        searchSupplierDataActionFilter();
//    $("#relationType").val("");
        $("#searchStrategy").val("");
    });


    $("#totalReceivableSort").click(function(e) {
        var sortStr = "";
        if ($("#totalReceivableSortSpan").hasClass("arrowDown")) {
            $("#totalReceivableSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " total_return_debt asc ";
        } else {
            $("#totalReceivableSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " total_return_debt desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();

    });


    $("#totalPayableSort").click(function(e) {
        var sortStr = "";
        if ($("#totalPayableSortSpan").hasClass("arrowDown")) {
            $("#totalPayableSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " total_debt asc ";
        } else {
            $("#totalPayableSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " total_debt desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();
    });

});

function searchBtnClick(filter) {
    $("#rowStart").val(0);
    searchSupplierDataAction();
    $("#hasDebt").val("");
    $("#hasDeposit").val("");

    if(!G.isEmpty(filter)){
        setTimeout(function(){$("#relatedSupplier").trigger("click")}, 500);
    }
}

function beforeSearchSupplier() {
    var totalTradeAmountUp,totalTradeAmountDown,totalDebtUp,totalDebtDown,lastInventoryTimeStart,lastInventoryTimeEnd, totalReceivableUp,totalReceivableDown,
            $supplierIds = $("#supplierIds"),
            supplierIds = $supplierIds.val();

    if ($("#supplierId").val() != "" && $("#supplierId").val()) {
        supplierIds = $("#supplierId").val();
    }
    $supplierIds.val("");

    if ($("#totalTradeAmountStart").val() != "") {
        totalTradeAmountDown = $("#totalTradeAmountStart").val();
    }
    if ($("#totalTradeAmountEnd").val() != "") {
        totalTradeAmountUp = $("#totalTradeAmountEnd").val();
    }

    if ($("#totalReceivableStart").val() != "") {
        totalReceivableDown = $("#totalReceivableStart").val();
    }
    if ($("#totalReceivableEnd").val() != "") {
        totalReceivableUp = $("#totalReceivableEnd").val();
    }
    var hasDebt = $("#hasDebt").val();

    if ($("#debtAmountStart").val() != "") {
        hasDebt = true;
        totalDebtDown = $("#debtAmountStart").val();
    }
    if ($("#debtAmountEnd").val() != "") {
        hasDebt = true;
        totalDebtUp = $("#debtAmountEnd").val();
    }

    var hasDeposit = $("#hasDeposit").val();

    lastInventoryTimeStart = $("#startDate").val();
    lastInventoryTimeEnd = $("#endDate").val();
    if (lastInventoryTimeStart && lastInventoryTimeStart.length != 0) {
        lastInventoryTimeStart = GLOBAL.Util.getDate(lastInventoryTimeStart).getTime();
    }
    if (lastInventoryTimeEnd && lastInventoryTimeEnd.length != 0) {
        lastInventoryTimeEnd = GLOBAL.Util.getDate(lastInventoryTimeEnd).getTime() + 1000 * 60 * 60 * 24 - 1;
    }
    var searchWord = "";
    var $supplierInfoText = $("#supplierInfoText");
    var initialValue = $supplierInfoText.attr("initialValue");
    if (initialValue != null && initialValue != "") {
        if ($supplierInfoText.val() == initialValue) {
            searchWord = "";
        } else {
            searchWord = $supplierInfoText.val();
        }
    }

    //产品条件

    var $productSearchWord = $("#searchWord");
    var productSearchWord = $productSearchWord.val();
    if (productSearchWord == $productSearchWord.attr("initialvalue")) {
        productSearchWord = "";
    }

    var $productName = $("#productName");
    var productName = $productName.val();
    if (productName == $productName.attr("initialvalue")) {
        productName = "";
    }

    var productBrand = $("#productBrand").val() == $("#productBrand").attr("initialvalue") ? "" : $("#productBrand").val();
    var productSpec = $("#productSpec").val() == $("#productSpec").attr("initialvalue") ? "" : $("#productSpec").val();
    var productModel = $("#productModel").val() == $("#productModel").attr("initialvalue") ? "" : $("#productModel").val();
    var productVehicleBrand = $("#productVehicleBrand").val() == $("#productVehicleBrand").attr("initialvalue") ? "" : $("#productVehicleBrand").val();
    var productVehicleModel = $("#productVehicleModel").val() == $("#productVehicleModel").attr("initialvalue") ? "" : $("#productVehicleModel").val();
    var commodityCode = $("#commodityCode").val() == $("#commodityCode").attr("initialvalue") ? "" : $("#commodityCode").val();


    var supplierInfo = $.parseJSON($supplierInfoText.attr("supplierInfo"));
    //如果 选中了下拉选项
    var name = "",contact = "",mobile = "",address = "";
    if (supplierInfo) {
        for (var j = 0; j < supplierInfo.length; j++) {
            if (supplierInfo[j][0] == "name" && supplierInfo[j][1]) {
                name = supplierInfo[j][1];
            }
            if (supplierInfo[j][0] == "contact" && supplierInfo[j][1]) {
                contact = supplierInfo[j][1];
            }
            if (supplierInfo[j][0] == "mobile" && supplierInfo[j][1]) {
                mobile = supplierInfo[j][1];
            }
            if (supplierInfo[j][0] == "address" && supplierInfo[j][1]) {
                address = supplierInfo[j][1];
            }
        }
        searchWord = "";
    } else {
        searchWord = $.trim(searchWord);
    }
    var ajaxData = null;
    ajaxData = {
        ids:supplierIds,
        searchWord:searchWord,
        productSearchWord:productSearchWord,
        customerOrSupplier: "supplier",

        province:$("#provinceNo").val(),
        city:$("#cityNo").val(),
        region:$("#regionNo").val(),

        totalTradeAmountDown: totalTradeAmountDown,
        totalTradeAmountUp: totalTradeAmountUp,

        hasDebt: hasDebt,
        totalDebtUp: totalDebtUp,    //应付
        totalDebtDown: totalDebtDown,  //应付

        totalReceivableUp: totalReceivableUp,//应收
        totalReceivableDown: totalReceivableDown,  //应收


        productName: productName,
        productBrand:productBrand,
        productSpec:productSpec,
        productModel:productModel,
        productVehicleBrand:productVehicleBrand,
        productVehicleModel:productVehicleModel,
        commodityCode:commodityCode,

        province:$("#provinceNo").val(),
        city: $("#cityNo").val(),
        region:$("#regionNo").val(),
        name: name,
        contact: contact,
        mobile: mobile,
        address: address,
        lastInventoryTimeStart: lastInventoryTimeStart,
        lastInventoryTimeEnd: lastInventoryTimeEnd,
        sort:$("#sortStatus").val(),
        maxRows:$("#maxRows").val(),
        relationType:$("#relationType").val(),
        hasDeposit:hasDeposit
    };
    return ajaxData;
}


function initSupplierList(json, resetStatNum) {
    $("#supplierDataTable tr:not(:first)").remove();

    if (json == null || json[0] == null) {
        $("#totalRows").val("0");
        $("#totalDeposit").html("0");
        $("#totalDebt").html("0");
        $("#totalNum").html("0");
        $("#relatedNum").html("0");
        return;
    }

    json = json[0];

    if (resetStatNum) {
        $("#totalRows").val(json.numFound);
        var totalDeposit = App.StringFilter.priceFilter(json.totalDeposit);
        var totalDebt = App.StringFilter.priceFilter(json.totalDebt);
        $("#totalDeposit").html(totalDeposit ? totalDeposit : 0);
        $("#totalDebt").html(totalDebt ? totalDebt : 0);
        $("#totalNum").html(json.numFound);
        $("#relatedNum").html(json.relatedNum);
        $('#totalConsumption').html(json.totalConsumption);
    }

    if (!json.supplierDTOs) return;
    initSupplierTable(json);
    initAndBindSelectCheckBoxs();
    $("#relationType").val('');
    $("#resetSearchCondition").val(''); //清除数据
}

function initSupplierTable(json) {
    var suppliers = json.supplierDTOs;
    var tr = '';

    var str = '<tr class="space"><td colspan="10"></td></tr>';
    $("#supplierDataTable").append($(str));
    for (var i = 0,max = suppliers.length; i < max; i++) {
        var supplier = suppliers[i];
        var supplierId = supplier.idString;
        var name = G.normalize(supplier.name);
        var contactMethod = G.normalize(supplier.mobile, G.normalize(supplier.landLine));
        var address = G.normalize(supplier.address);
        var areaInfo = G.normalize(supplier.areaInfo);
        var totalTradeAmount = supplier.totalTradeAmount ? App.StringFilter.priceFilter(supplier.totalTradeAmount, 2) : "0";
        var totalReturnAmount = supplier.totalReturnAmount ? App.StringFilter.priceFilter(supplier.totalReturnAmount, 2) : "0";
        var debt = (supplier.totalDebt ? App.StringFilter.priceFilter(supplier.totalDebt, 2) : "0");
        var deposit = supplier.deposit ? App.StringFilter.priceFilter(supplier.deposit, 2) : "0";
        var lastInventoryTime = supplier.lastInventoryTimeStr ? supplier.lastInventoryTimeStr : "";
        var totalReturnDebt = supplier.totalReturnDebt ? App.StringFilter.priceFilter(supplier.totalReturnDebt, 2) : "0";
        var supplierShopId = supplier.supplierShopIdString || '';
        var mobileList = new Array();
        var contactList = new Array();
        var mobileTitleList = new Array();
        var emptyStr = "--";
        if (supplier.contactDTOList != null) {
            for (var index in supplier.contactDTOList) {
                var contactDTO = supplier.contactDTOList[index];
                contactList.push(contactDTO.name ? contactDTO.name :emptyStr);
                if (APP_BCGOGO.Permission["isMobileHidden"]) {
                    if (contactDTO.mobile) {
                        mobileList.push(contactDTO.mobile.substr(0, 3) + "****" + contactDTO.mobile.substr(7, 4));
                    } else {
                        mobileList.push(emptyStr);
                    }

                } else {
                    mobileList.push(contactDTO.mobile ? contactDTO.mobile : emptyStr);
                }
                mobileTitleList.push(contactDTO.mobile ? contactDTO.mobile : emptyStr);
            }
        } else {
            contactList.push(emptyStr);
            mobileTitleList.push(emptyStr);
        }
        tr = '<tr class="titBody_Bg">';
        tr += '<td style="padding-left:10px;">' +
                '<input type="checkbox" class="check" style="margin-right:1px;" deposit="' + deposit + '" supplierShopId="'
                + supplierShopId + '" name="selectSupplier" value="' + supplierId + '"  id=check' + (i + 1) + '/>'
                + '</td>';

        if (G.Lang.isNotEmpty(supplierShopId)) {
            var totalAverageScore = (supplier.totalAverageScore ? App.StringFilter.priceFilter(supplier.totalAverageScore, 1) : "0");
            var qualityAverageScore = (supplier.qualityAverageScore ? App.StringFilter.priceFilter(supplier.qualityAverageScore, 1) : "0");
            var performanceAverageScore = (supplier.performanceAverageScore ? App.StringFilter.priceFilter(supplier.performanceAverageScore, 1) : "0");
            var speedAverageScore = (supplier.speedAverageScore ? App.StringFilter.priceFilter(supplier.speedAverageScore, 1) : "0");
            var attitudeAverageScore = (supplier.attitudeAverageScore ? App.StringFilter.priceFilter(supplier.attitudeAverageScore, 1) : "0");
            var commentRecordCount = (supplier.commentRecordCount ? App.StringFilter.priceFilter(supplier.commentRecordCount, 1) : "0");
            var totalAverageScoreStr;
            if (totalAverageScore == 0 || totalAverageScore == "0") {
                totalAverageScoreStr = "暂无";
            } else {
                totalAverageScoreStr = totalAverageScore + '分';
            }
            var totalAverageSpan = 0 - parseInt((5 - totalAverageScore) / 0.5) * 19;

            var idStr = "relatedSupplier" + i;

            tr += '<td>'
                    + '<div class="line">'
                    + '<a  class="blue_color" id="' + supplierId + '_connector" onmouseover="showConnector(this)" href="unitlink.do?method=supplier&supplierId='
                    + supplierId + '&fromPage=supplierData">' + name + '</a>&nbsp;'
//          + '<span class="customer_or_supplier_connect" supplierId="' + supplierId + '" >在线店铺</span>'
                    + '<br><a supplierId="' + supplierId + '" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + supplierShopId + '"><img src="images/icon_online_shop.png"></a>'
                    + '</div>';
            tr += '<div class="prompt" id="' + supplierId + '_prompt" style="display:none;">'
                    + '<div class="promptTop"></div>'
                    + '<div class="promptBody"><a onclick="hiddenConnector(this)" id="' + supplierId + '_a" class="icon_close"></a>';

            tr += '<div class="divStar">'
                    + '<h4>' + name + '</h4>&nbsp;'
                    + '</div>'

                    + '<div class="divStar" id="' + idStr + '" onmouseover="showSupplierCommentScore(this' + ',' + totalAverageScore + ',' + commentRecordCount + ',' + qualityAverageScore + ',' + performanceAverageScore + ',' + speedAverageScore + ',' + attitudeAverageScore
                    + ');" onmouseout="scorePanelHide();" onclick="redirectShopCommentDetail(\'' + supplierShopId + '\')">'
                    + '<span class="picStar"  style="background-position: 0px ' + totalAverageSpan + 'px;"></span>'
                    + '<a class="yellow_color">' + totalAverageScoreStr + '</a>&nbsp;'
                    + '<span class="gray_color">共' + commentRecordCount + '人评分</span>'
                    + '</div> ';
            tr += '<div class="clear i_height"></div>';

        } else {
            tr += '<td>'
                    + '<a  class="blue_color" id="' + supplierId + '_connector" onmouseover="showConnector(this)" href="unitlink.do?method=supplier&supplierId=' + supplierId + '&fromPage=supplierData">' + name + '</a>&nbsp;'


            tr += '<div class="prompt" id="' + supplierId + '_prompt" style="display:none;">'
                    + '<div class="promptTop"></div>'
                    + '<div class="promptBody"><a onclick="hiddenConnector(this)" id="' + supplierId + '_a" class="icon_close"></a>';
            tr += '<div class="divStar">'
                    + '<h4>' + name + '</h4>&nbsp;'
                    + '</div>'
            tr += '<div class="clear i_height"></div>';

        }

        if (contactList) {
            for (var index in contactList) {
                if (!((G.isEmpty(mobileList[index]) || mobileList[index] == emptyStr) && (G.isEmpty(contactList[index]) || contactList[index] == emptyStr))) {
                    tr += '<div class="lineList">';
                    if (G.isNotEmpty(contactList[index]) && contactList[index] != emptyStr) {
                        tr += contactList[index] + '&nbsp;';
                        var mobileStr="";
                        if(mobileList[index]!="--"){
                            mobileStr = '&nbsp;<a class="phone" href="javascript:smsHistory(\'' + supplierId + '\',\'' + mobileList[index] + '\')"></a>';
                        }
                        if (mobileTitleList[index] != "暂无手机号码") {
                            tr += mobileTitleList[index] + mobileStr;
                        } else {
                            tr += mobileTitleList[index];
                        }
                    }
                    tr += '</div>';
                }
            }
        }

        tr += '<a class="blue_color info" href="unitlink.do?method=supplier&fromPage=supplierData&supplierId=' + supplierId + '">详细信息>></a>' + '</div>' + '<div class="promptBottom"></div>  ' + '</div> ' + '</td>  ';
        tr += "<td>";

        if (contactList[0] && contactList[0] != emptyStr) {
            tr += '<div class="line lineConnect">' + contactList[0] + '</div>';
        }
        if (mobileList[0] && APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobileList[0])) {
            tr += '<div class="line lineConnect">' + mobileList[0] + '<a class="phone" title="点击后可发送短信" href="javascript:smsHistory(\'' + supplierId + '\',\'' + mobileList[0] + '\')"></a></div>';
        }
        if (!contactList[0] && !mobileList[0]) {
            tr += emptyStr;
        }
        if (contactList[0] == emptyStr && mobileTitleList[0] == emptyStr) {
          tr += emptyStr;
        }

        tr += "</td>";
        if (G.isEmpty(areaInfo)) {
          tr += '<td>' + emptyStr + '</td>';
        } else {
          tr += '<td title="' + areaInfo + '">' + areaInfo + '</td>';
        }
        tr += '<td><span class="arialFont">¥</span>' + totalTradeAmount + '</td>';
        tr += '<td> <span class="arialFont">¥</span>' + deposit + '</td>';

        tr += '<td><span class="arialFont">¥</span>' + totalReturnAmount + '</td>';
        var receiveCss = "pays";
        receiveCss += totalReturnDebt > 0 ? " red_color" : " black_color";
        var payableCss = "pays";
        payableCss += debt > 0 ? " green_color" : " black_color";
        tr += '<td class="income" onclick="toCreateStatementOrder(\'' + supplierId + '\', \'SUPPLIER_STATEMENT_ACCOUNT\') ">'
                + '  <a class="blue_color line"><span class="' + receiveCss + '">应收&nbsp;<span class="arialFont" style="display: inline;">¥' + totalReturnDebt + '</span></span>' +
                '<span class="' + payableCss + '">应付&nbsp;<span class="arialFont" style="display: inline;">¥' + debt + '</span></span></a></td>';

        tr += '<td>' + (G.isEmpty(lastInventoryTime) ? emptyStr : lastInventoryTime) + '</td>';

        tr += '<td>';

        if (APP_BCGOGO.Permission.Version.ProductThroughDetail) {
            tr += '<a class="blue_color" onclick="showProductThroughDetail(\'' + supplierId + '\',\'supplier\')">交易明细</a>&nbsp;';
        }

        if (supplier.supplierShopId == null) {

            if (APP_BCGOGO.Permission.Version.RelationSupplier) {
                tr += '<a class="sentInvitationCodeSmsBtn blue_color line" data-supplier-id="' + supplierId + '" data-mobile="' + mobileList[0] + '">推荐使用</a>&nbsp;';
            }
        }
        tr += "</td>";

        tr += '</tr>';
        tr += ' <tr class="titBottom_Bg"><td colspan="11"></td></tr>';

        $("#supplierDataTable").append($(tr));
    }

}


function showConnector(domObj) {
    $(".prompt").css("display", "none");
    var idStr = domObj.id;
    if (idStr) {
        var temp = idStr.split("_");
        var offset = $(domObj).offset();
        var offsetMainDiv = $(".i_main").eq(0).offset() || $(".shoppingCart").eq(0).offset();

        $("#" + temp[0] + "_prompt").css({
            position:'absolute',
            left:offset.left - offsetMainDiv.left + 'px',
            top:offset.top - offsetMainDiv.top + 13 + 'px'
        });
        $("#" + temp[0] + "_prompt").css("display", "block");
    }
}
function hiddenConnector(domObj) {
    var idStr = domObj.id;
    if (idStr) {
        var temp = idStr.split("_");
        $("#" + temp[0] + "_prompt").css("display", "none");
    }
}

function smsHistory(supplierId,mobile) {
    if (mobile == null || jQuery.trim(mobile) == "") {
        jQuery("#enterPhoneSupplierId").val(jQuery("#supplierId").val());
        Mask.Login();
        jQuery("#enterPhoneSetLocation").fadeIn("slow");
        return;
    }
    window.location = encodeURI("sms.do?method=smswrite&supplierId="+supplierId+"&mobile=" + mobile);
}