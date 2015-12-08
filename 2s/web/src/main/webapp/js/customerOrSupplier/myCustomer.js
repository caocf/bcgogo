/**
 * 我的供应商列表专用js
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-23
 * Time: 下午6:48
 * To change this template use File | Settings | File Templates.
 */
var defaultSortStatus = " created_time desc ";
var isTodayAdd = GLOBAL.Util.getUrlParameter('todayAdd') === 'true' ? true : false;
$().ready(function () {

    var hasHover = false;
    $(".J_supplier_sort").each(function () {
        if ($(this).hasClass("hover")) {
            hasHover = true;
        }
    });
    if (!hasHover) {
        $("#createdTimeSort").addClass("hover");
    }

    $("a[name='memberCardTypes']").bind("click", function() {

        if (!$(this).hasClass("clicked")) {
            $(this).addClass("clicked");
        } else {
            $(this).removeClass("clicked");
        }

        $("#noMemberRadio").removeClass("clicked");
        $("#memberRadio").removeClass("clicked");
        $("#customerSearchBtn").click();
    });


    $("#clearConditionBtn").bind("click", function() {
        $("#customerId").val("");
        $("#customerIds").val("");

        $("#provinceNo option").not(".default").remove();
        $("#cityNo option").not(".default").remove();
        $("#regionNo option").not(".default").remove();

        $("a[name='memberCardTypes']").removeClass("clicked");
        $("a[name='date_select']").removeClass("clicked");
        $("a[name='memberSelect']").removeClass("clicked");

        $(".btnList").removeClass("clicked");
        $("a[name='memberCardTypes']").removeClass("clicked");
        $("a[name='memberSelect']").removeClass("clicked");
        $("#startDate").val("");
        $("#endDate").val("");

        $("#customerInfoText").val("");
        $("#customerInfoText").blur();

        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");

        $("#date_self_define").click();

        $("#totalTradeAmountStart,#totalTradeAmountEnd,#totalReceivableStart,#totalReceivableEnd,#debtAmountStart,#debtAmountEnd").val("");
        $("#relationType").val("");
        provinceBind();

        $("#provinceNo").css({"color": "#ADADAD"});
        $("#cityNo").css({"color": "#ADADAD"});
        $("#regionNo").css({"color": "#ADADAD"});

        $(".J_supplier_sort").each(function () {
            $(this).removeClass("hover");
        });

        $("#sortStatus").val(defaultSortStatus);
        $("#createdTimeSort").attr("currentSortStatus", "Desc");

        $('#vehicleBrandSearch').add('#vehicleModelSearch').add('#vehicleColorSearch').val('').blur();
        ajaxDataTemp = null;
    });

    $("#customerInfoText").bind("change", function() {
        $("#customerId").val("");
    });


    $("#lastInventoryTimeSort").click(function(e) {
        var sortStr = "";
        if ($("#lastInventoryTimeSortSpan").hasClass("arrowDown")) {
            $("#lastInventoryTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " last_expense_time asc ";
        } else {
            $("#lastInventoryTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " last_expense_time desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();
    });


    $("#totalTradeAmountSort").click(function(e) {
        var sortStr = "";
        if ($("#totalTradeAmountSortSpan").hasClass("arrowDown")) {
            $("#totalTradeAmountSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " total_amount asc ";
        } else {
            $("#totalTradeAmountSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " total_amount desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();

    });

    $("#totalReceivableSort").click(function(e) {
        var sortStr = "";
        if ($("#totalReceivableSortSpan").hasClass("arrowDown")) {
            $("#totalReceivableSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " total_debt asc ";
        } else {
            $("#totalReceivableSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " total_debt desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();

    });

    $("#totalPayableSort").click(function(e) {
        var sortStr = "";
        if ($("#totalPayableSortSpan").hasClass("arrowDown")) {
            $("#totalPayableSortSpan").addClass("arrowUp").removeClass("arrowDown");
            sortStr = " total_return_debt asc ";
        } else {
            $("#totalPayableSortSpan").addClass("arrowDown").removeClass("arrowUp");
            sortStr = " total_return_debt desc ";
        }
        $("#sortStatus").val(sortStr);
        searchBtnClick();
    });


    $("#noMemberRadio").bind("click", function() {
        if (!$(this).hasClass("clicked")) {
            $(this).addClass("clicked");
        } else {
            $(this).removeClass("clicked");
        }
        $("#memberRadio").removeClass("clicked");
        $("a[name='memberCardTypes']").removeClass("clicked");
        $("#customerSearchBtn").click();
    });


    $("#memberRadio").bind("click", function() {
        if (!$(this).hasClass("clicked")) {
            $(this).addClass("clicked");
        } else {
            $(this).removeClass("clicked");
        }
        $("#noMemberRadio").removeClass("clicked");
        $("a[name='memberCardTypes']").removeClass("clicked");
        $("#customerSearchBtn").click();
    });


    //仓库 除了【所有】
    //勾选其他选项时，未全部勾选时，【所有】不自动勾选；全部勾选时，【所有】自动勾选；
    $(".memberCardCheck").click(function (event) {
        var dom = $(".memberChecks > label:not(:first)>input");
        //判断是否选中全部
        var isAllGoodsChecked = true;
        for (var i = 0; i < dom.length; i++) {
            if (!$(dom[i])[0].checked) {
                isAllGoodsChecked = false;
            }
        }

        dom = $(".more_memberChecks > label > input");
        for (var i = 0; i < dom.length; i++) {
            if (!$(dom[i])[0].checked) {
                isAllGoodsChecked = false;
            }
        }

        if (isAllGoodsChecked) {
            $("#allMemberCardCheck")[0].checked = true;
        } else {
            $("#allMemberCardCheck")[0].checked = false;
        }

        $("#noMemberRadio")[0].checked = false;
        $("#memberRadio")[0].checked = true;
    });

    $("#export").click(function() {
        $(this).attr("disabled", true);
        $("#exporting").css("display", "");
        if ($("#memberNum").text() * 1 == 0) {
            nsDialog.jAlert("对不起，暂无会员数据，无法导出！");
            $(this).removeAttr("disabled");
            $("#exporting").css("display", "none");
            return;
        }
        var data = beforeSearchCustomer();
        //导出时，当没有选择会员时，默认只导出会员
        if ((data.memberType == "非会员" || data.memberType == '')) {
            nsDialog.jConfirm("对不起，系统只支持会员信息导出，是否继续导出？若继续则为您导出会员信息！", "友情提示", function(value) {
                if (value) {
                    data.memberType = $("#allCardName").val();
                    data.totalExportNum = $("#memberNum").text();
                    var url = "export.do?method=exportCustomer";
                    bcgogoAjaxQuery.setUrlData(url, data);
                    bcgogoAjaxQuery.ajaxQuery(function (json) {
                        $("#export").removeAttr("disabled");
                        $("#exporting").css("display", "none");
                        if (json && json.exportFileDTOList) {
                            if (json.exportFileDTOList.length > 1) {
                                showDownLoadUI(json);
                            } else {
                                window.open("download.do?method=downloadExportFile&exportFileName=会员信息.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                            }
                        }

                    });
                } else {
                    $("#export").removeAttr("disabled");
                    $("#exporting").css("display", "none");
                }
            });

        } else {
            data.totalExportNum = $("#totalNum").text();
            var url = "export.do?method=exportCustomer";
            bcgogoAjaxQuery.setUrlData(url, data);
            bcgogoAjaxQuery.ajaxQuery(function (json) {
                $("#export").removeAttr("disabled");
                $("#exporting").css("display", "none");
                if (json && json.exportFileDTOList) {
                    if (json.exportFileDTOList.length > 1) {
                        var htm = initDownloadUI(json);
                        $(htm).dialog({
                            autoOpen:true,
                            resizable: false,
                            title:"友情提示",
                            height:200,
                            width:400,
                            modal: true,
                            closeOnEscape: false,
                            showButtonPanel:true
                        });

                    } else {
                        window.open("download.do?method=downloadExportFile&exportFileName=会员信息.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                    }
                }

            });
        }

    });

});


function searchBtnClick(filter) {
    $("#rowStart").val(0);
    //初始化统计数字。。。
    searchCustomerDataAction();

    if(!G.isEmpty(filter)){
        setTimeout(function(){
            searchCustomerDataActionFilter(filter);
        }, 500);
    }
}


function beforeSearchCustomer() {
    var sortStatus = $("#sortStatus").val(),
            $customerIds = $("#customerIds"),
            customerIds = $customerIds.val();

    if ($("#customerId").val() != "" && $("#customerId").val()) {
        customerIds = $("#customerId").val();
    }

    $customerIds.val("");
    sortStatus = sortStatus ? sortStatus : "";
    var searchWord = "";
    var $customerInfoText = $("#customerInfoText");
    var initialValue = $customerInfoText.attr("initialValue");
    if (initialValue != null && initialValue != "") {
        if ($customerInfoText.val() == initialValue) {
            searchWord = "";
        } else {
            searchWord = $customerInfoText.val();
        }
    }
    //如果 选中了下拉选项
    var name = "",contact = "",mobile = "",address = "",license_no = "",member_no = "";
    var customerInfo = $.parseJSON($customerInfoText.attr("customerInfo"));
    if (customerInfo) {
        for (var j = 0; j < customerInfo.length; j++) {
            if (customerInfo[j][0] == "name" && customerInfo[j][1]) {
                name = customerInfo[j][1];
            }
            if (customerInfo[j][0] == "contact" && customerInfo[j][1]) {
                contact = customerInfo[j][1];
            }
            if (customerInfo[j][0] == "mobile" && customerInfo[j][1]) {
                mobile = customerInfo[j][1];
            }
            if (customerInfo[j][0] == "address" && customerInfo[j][1]) {
                address = customerInfo[j][1];
            }
            if (customerInfo[j][0] == "license_no" && customerInfo[j][1]) {
                license_no = customerInfo[j][1];
            }
            if (customerInfo[j][0] == "member_no" && customerInfo[j][1]) {
                member_no = customerInfo[j][1];
            }
        }
        searchWord = "";
    } else {
        searchWord = $.trim(searchWord);
    }

    var memberType = "";

    if ($("#noMemberRadio").hasClass("clicked")) {
        memberType = "非会员";
    } else if ($("a[name='memberCardTypes'].clicked").length > 0) {
        $("a[name='memberCardTypes']").each(function () {
            if ($(this).hasClass("clicked")) {
                memberType += $(this).attr("value") + ",";
            }
        });
        memberType = memberType.substring(0, memberType.length - 1);
    } else if ($("#memberRadio").hasClass("clicked")) {
        memberType = $("#allCardName").val();
    }

    memberType = memberType ? memberType : "";
    var filterType = $("#filterType").val();
    if (filterType == 'memberNum') {
        if (memberType == "") {
            memberType = $("#allCardName").val();
        }
    }

    var totalAmount = $("#totalAmountCondition").attr("value");
    var totalAmountUp,totalAmountDown,hasDebt,totalDebtUp,totalDebtDown,lastExpenseTimeStart,lastExpenseTimeEnd,totalPayableUp,totalPayableDown;


    if ($("#totalTradeAmountStart").val() != "") {
        totalAmountDown = $("#totalTradeAmountStart").val();
    }
    if ($("#totalTradeAmountEnd").val() != "") {
        totalAmountUp = $("#totalTradeAmountEnd").val();
    }

    hasDebt = $("#hasDebt").val();
    if ($("#totalReceivableStart").val() != "") {
        hasDebt = true;
        totalDebtDown = $("#totalReceivableStart").val();
    }
    if ($("#totalReceivableEnd").val() != "") {
        hasDebt = true;
        totalDebtUp = $("#totalReceivableEnd").val();
    }

    if ($("#debtAmountStart").val() != "") {
        totalPayableDown = $("#debtAmountStart").val();
    }
    if ($("#debtAmountEnd").val() != "") {
        totalPayableUp = $("#debtAmountEnd").val();
    }

    lastExpenseTimeStart = $("#startDate").val();
    lastExpenseTimeEnd = $("#endDate").val();
    if (lastExpenseTimeStart && lastExpenseTimeStart.length != 0) {
        lastExpenseTimeStart = GLOBAL.Util.getDate(lastExpenseTimeStart).getTime();
    }
    if (lastExpenseTimeEnd && lastExpenseTimeEnd.length != 0) {
        lastExpenseTimeEnd = GLOBAL.Util.getDate(lastExpenseTimeEnd).getTime() + 1000 * 60 * 60 * 24 - 1;
    }

    searchWord = searchWord ? searchWord : "";


    //产品相关
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
    var vehicleBrandNode = $('#vehicleBrandSearch');
    var vehicleModelNode = $('#vehicleModelSearch');
    var vehicleColorNode = $('#vehicleColorSearch');
    var vehicleBrand = vehicleBrandNode.val() != vehicleBrandNode.attr('initialValue') ? vehicleBrandNode.val() : '';
    var vehicleModel = vehicleModelNode.val() != vehicleModelNode.attr('initialValue') ? vehicleModelNode.val() : '';
    var vehicleColor = vehicleColorNode.val() != vehicleColorNode.attr('initialValue') ? vehicleColorNode.val() : '';

    var productBrand = $("#productBrand").val() == $("#productBrand").attr("initialvalue") ? "" : $("#productBrand").val();
    var productSpec = $("#productSpec").val() == $("#productSpec").attr("initialvalue") ? "" : $("#productSpec").val();
    var productModel = $("#productModel").val() == $("#productModel").attr("initialvalue") ? "" : $("#productModel").val();
    var productVehicleBrand = $("#productVehicleBrand").val() == $("#productVehicleBrand").attr("initialvalue") ? "" : $("#productVehicleBrand").val();
    var productVehicleModel = $("#productVehicleModel").val() == $("#productVehicleModel").attr("initialvalue") ? "" : $("#productVehicleModel").val();
    var commodityCode = $("#commodityCode").val() == $("#commodityCode").attr("initialvalue") ? "" : $("#commodityCode").val();


    var ajaxData = {
        ids:customerIds,
        searchWord:searchWord,
        lastExpenseTimeStart:lastExpenseTimeStart,
        lastExpenseTimeEnd:lastExpenseTimeEnd,
        name: name,
        contact: contact,
        mobile: mobile,
        customerOrSupplier: "customer",
        hasDebt: hasDebt,

        totalDebtUp: totalDebtUp,   //应收
        totalDebtDown: totalDebtDown, //应收


        totalPayableUp:totalPayableUp,  //应付
        totalPayableDown:totalPayableDown, //应付

        totalAmountUp: totalAmountUp,
        totalAmountDown: totalAmountDown,
        maxRows:$("#maxRows").val(),
        sort:sortStatus,
        memberType:memberType,
        relationType:$("#relationType").val(),
        filterType:$("#filterType").val(),

        productSearchWord:productSearchWord,
        productName: productName,
        productBrand:productBrand,
        productSpec:productSpec,
        productModel:productModel,
        productVehicleBrand:productVehicleBrand,
        productVehicleModel:productVehicleModel,
        commodityCode:commodityCode,
        vehicleBrand:vehicleBrand,
        vehicleModel:vehicleModel ,
        vehicleColor:vehicleColor,
        province:$("#provinceNo").val(),
        city: $("#cityNo").val(),
        region:$("#regionNo").val()
    };

    return ajaxData;
}

function initRelatedCustomerInfoTable(json) {
    var sortStr = $("#sortStr").val();
    var tr = '<colgroup>' +
            '<col width="30">' +
            '<col width="200">' +
            '<col width="110">' +
            '<col width="130">' +
            '<col width="70">' +
            '<col width="70">' +
            '<col width="90">' +
            '<col width="80">' +
            '<col width="90">' +
            '<col width="120">' +
            '</colgroup>';
    tr += '<tr class="titleBg">' +
            '<td style="padding-left:10px;"></td>' +
            '<td>客户名</td>' +
            '<td>联系人</td>' +
            '<td>所在区域</td>' +
            '<td>累计消费</td>' +
            '<td>预收款</td>' +
            '<td>退货次数/金额</td>' +
            '<td>对账金额</td>' +
            '<td>最后消费日期</td>' +
            '<td>操作</td>';
    '</tr>';
    $("#customerDataTable").append($(tr));


    if (!json.customerSuppliers) return;

    var emptyStr ="--";
    var str = '<tr class="space"><td colspan="10"></td></tr>';
    $("#supplierDataTable").append($(str));

    var customers = json.customerSuppliers;
    for (var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        var address;
        var customerShopId = customer.customerOrSupplierShopId || '';
        var licenceNo = customer.licenceNo ? customer.licenceNo : "";
        address = customer.address ? customer.address : "——";
        var areaInfo = G.normalize(customer.areaInfo);


        var customerId = customer.idStr;
        var lastBill = customer.lastBill ? customer.lastBill : "";
        var lastDateStr = customer.lastDateStr ? customer.lastDateStr : "";
        var totalReceivable = customer.totalDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalDebt) : '0';
        var totalAmount = customer.totalAmount ? APP_BCGOGO.StringFilter.priceFilter(customer.totalAmount) : '0';
        var mobileList = new Array();
        var contactList = new Array();
        var mobileTitleList = new Array();
        if (customer.contactDTOList != null) {
            for (var index in customer.contactDTOList) {
                var contactDTO = customer.contactDTOList[index];
                contactList.push(contactDTO.name ? contactDTO.name : emptyStr);
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
        }
        var repayDateStr = customer.repayDateStr ? customer.repayDateStr : "";
        var name = customer.name ? customer.name : "";
        var countCustomerReturn = customer.countCustomerReturn == null ? 0 : customer.countCustomerReturn;
        var returnAmount = customer.totalReturnAmount ? APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnAmount) : '0';
        var totalReturnDebt = customer.totalReturnDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnDebt) : '0';

        var deposit = customer.totalDeposit ? App.StringFilter.priceFilter(customer.totalDeposit, 2) : "0";
        var lastExpenseTimeStr = customer.lastExpenseTimeStr ? customer.lastExpenseTimeStr : "";


        tr = '<tr class="titBody_Bg">';
        tr += '<td style="padding-left:10px;"><input type="checkbox" class="check" style="margin-right:1px;" deposit="' + deposit + '" customerShopId="' + customerShopId + '" name="selectCustomer" value="' + customerId + '"  id=check' + (i + 1) + '/>' + '</td>';

    if (G.isNotEmpty(customerShopId)) {
      tr += '<td>'
          + '<div class="line">'
          + '<a id="' + customerId + '_connector" onmouseover="showConnector(this)" class="blue_color" href="unitlink.do?method=customer&customerId=' + customerId + '&fromPage=customerData">' + name + '</a>&nbsp;'
//          + '<a class="customer_or_supplier_connect" customerId="' + customerId + '" >在线店铺</a>'
          + '<br><a customerId="' + customerId + '" href="shopMsgDetail.do?method=renderShopMsgDetail&fromCustomerPage=true&paramShopId='+customerShopId+'" ><img src="images/icon_online_shop.png"></a>'
          + '</div>';

//                    + '<br><a customerId="' + customerId + '" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + customerShopId + '" ><img src="images/icon_online_shop.png"></a>'
//                    + '</div>';

            tr += '<div class="prompt" id="' + customerId + '_prompt" style="display:none;">'
                    + '<div class="promptTop"></div>'
                    + '<div class="promptBody"><a onclick="hiddenConnector(this)" id="' + customerId + '_a" class="icon_close"></a>';

            tr += '<div class="divStar">'
                    + '<h4>' + name + '</h4>&nbsp;'
//          + '<span class="fei_connect" customerId="' + customerId + '" >在线店铺</span>'
                    + '</div>';

        } else {
            tr += '<td>'
                    + '<div class="line">'
                    + '<a id="' + customerId + '_connector" onmouseover="showConnector(this)" class="blue_color" href="unitlink.do?method=customer&customerId=' + customerId + '&fromPage=customerData">' + name + '</a>&nbsp;'
                    + '</div>';

            tr += '<div class="prompt" id="' + customerId + '_prompt" style="display:none;">'
                    + '<div class="promptTop"></div>'
                    + '<div class="promptBody"><a onclick="hiddenConnector(this)" id="' + customerId + '_a" class="icon_close"></a>';

            tr += '<div class="divStar">'
                    + '<h4>' + name + '</h4>&nbsp;'
                    + '</div>';
        }

        tr += '<div class="clear i_height"></div>';

        if (contactList) {
            for (var index in contactList) {
                tr += '<div class="lineList">';
                tr += contactList[index] + '&nbsp;';
                var mobileStr="";
                if(mobileList[index]!="--"){
                    mobileStr = '&nbsp;<a class="phone" href="javascript:smsHistory(\'' + customerId + '\',\'' + mobileList[index] + '\')"></a>';
                }
                if (mobileList[index] != "暂无手机号码" && $("#smsSendPermission").val() == "true") {
                    tr += mobileTitleList[index] + mobileStr;
                } else {
                    tr += mobileTitleList[index];
                }
                tr += '</div>';
            }
        }
        tr += '<a class="blue_color info" href="unitlink.do?method=customer&fromPage=customerData&customerId=' + customerId + '">详细信息>></a>' + '</div>' + '<div class="promptBottom"></div>  ' + '</div> ' + '</td>  ';


        tr += "<td>";
        if (contactList[0] && contactList[0] != emptyStr) {
          tr += '<div class="line lineConnect">' + contactList[0] + '</div>';
        }
        if (mobileTitleList[0] && mobileTitleList[0] != emptyStr) {

            if (APP_BCGOGO.Permission["isMobileHidden"]) {

                if ($("#smsSendPermission").val() == "true") {
                    tr += '<div class="line lineConnect" style="margin-right: 15px;">' + mobileList[0] + '&nbsp;<a class="phone" onclick="sendSms(\'' + mobileTitleList[0] + '\',\'3\',\'' + totalReceivable + '\',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + name + '\',\'' + customerId + '\')">' + '</a></div>';
                } else {
                    tr += '<div class="line lineConnect">' + mobileList[0] + '</div>';
                }
            } else {
                if ($("#smsSendPermission").val() == "true") {
                    tr += '<div class="line lineConnect" style="margin-right: 15px;">' + mobileTitleList[0] + '&nbsp;<a class="phone" onclick="sendSms(\'' + mobileTitleList[0] + '\',\'3\',\'' + totalReceivable + '\',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + name + '\',\'' + customerId + '\')">' + '</a></div>';
                } else {
                    tr += '<div class="line lineConnect">' + mobileTitleList[0] + '</div>';
                }
            }
        }
        if (contactList[0] == emptyStr && mobileTitleList[0] == emptyStr) {
            tr += '<div class="line lineConnect">' + emptyStr + '</div>';
        }
        if (!contactList[0] && !mobileTitleList[0]) {
            tr += '<div class="line lineConnect">' + emptyStr + '</div>';
        }
        tr += '</td>';

        if (G.isEmpty(areaInfo)) {
          tr += '<td>' + emptyStr + '</td>';
        } else {
          tr += '<td title="' + areaInfo + '">' + areaInfo + '</td>';
        }

        tr += ' <td><span class="arialFont">¥</span>' + totalAmount + '</td>';
        tr += '<td><span class="arialFont">¥</span>' + deposit + '</td>';
        tr += '<td>' + countCustomerReturn + ' / <span class="arialFont">¥</span>' + returnAmount + '</td>';

        if (APP_BCGOGO.Permission.CustomerManager.CustomerArrears) {
            tr += '<td class="income" onclick="toCreateStatementOrder(\'' + customerId + '\', \'CUSTOMER_STATEMENT_ACCOUNT\') ">'

        } else {
            tr += '<td class="income">'

        }
        var receiveCss = "pays";
        receiveCss += totalReceivable > 0 ? " red_color" : " black_color";
        var payableCss = "pays";
        payableCss += totalReturnDebt > 0 ? " green_color" : " black_color";
        tr += '  <a class="blue_color line"><span class="' + receiveCss + '">' +
                '应收&nbsp;<span class="arialFont" style="display: inline;">¥' + totalReceivable + '</span></span>'
                + '<span class="' + payableCss + '">应付&nbsp;<span class="arialFont" style="display: inline;">¥' + totalReturnDebt + '</span></span></a></td>';

        tr += '<td>' + (G.isEmpty(lastExpenseTimeStr) ? '--' : lastExpenseTimeStr) + '</td>';

        tr += '<td>';

        if (APP_BCGOGO.Permission.Version.ProductThroughDetail) {
            tr += '<a class="blue_color" onclick="showProductThroughDetail(\'' + customerId + '\',\'customer\')">交易明细</a>&nbsp;';
        }
        tr += '<a class="blue_color" onclick="redirectCustomerBill(\'' + customerId + '\')">对账详细</a>&nbsp;'

        if (!customer.relationType || customer.relationType == "UNRELATED") {
            tr += '<a class="sentInvitationCodeSmsBtn blue_color line" data-customer-id="' + customerId + '" data-mobile="' + mobileTitleList[0] + '">推荐使用</a>&nbsp;';
            if (APP_BCGOGO.Permission.CustomerManager.UpdateCustomer) {
                tr += '<a class="updateCustomer blue_color" data-customer-id="' + customerId + '">注册升级</a>&nbsp;';
            }
            tr += '</td>';

        }

        tr += '</tr>';
        tr += ' <tr class="titBottom_Bg"><td colspan="11"></td></tr>';
        $("#customerDataTable").append($(tr));
    }
//    else {
//      tr += '<div class="line"><a class="supplier_cancel_shop_relation blue_color" customerId="' + customerId + '">取消关联</a></div></td>';
//    }

}


function initCustomerInfoTable(json) {
    var getId = getUniqueId();
    var sortStr = $("#sortStr").val();
    var tr = '<colgroup>' +
            '<col width="30">' +
            '<col width="120">' +
            '<col width="110">' +
            '<col width="250">';
    tr += '<col width="70">' +
            '<col width="80">' +
            '<col width="90">' +
            '<col width="90">';
    if (APP_BCGOGO.Permission.Version.ProductThroughDetail) {
        tr += '<col width="60"> ';
    }

    tr += '</colgroup>';
    tr += '<tr class="titleBg">';
    tr += '<td style="padding-left:10px;"></td>' +
            '<td>客户名</td>' +
            '<td>联系信息</td>';
    tr += '<td>车辆信息</td>';
    tr += '<td>累计消费</td>';
    tr += '<td>退货信息</td>';
    tr += '' +
            '<td>对账金额</td>' +
            '<td>最后消费日期</td>';
    if (APP_BCGOGO.Permission.Version.ProductThroughDetail) {
        tr += '<td>操作</td>';
    }
    tr += '</tr>';
    $("#customerDataTable").append($(tr));
    if (!json.customerSuppliers) return;

    var str = '<tr class="space"><td colspan="13"></td></tr>';
    $("#customerDataTable").append($(str));

    var customers = json.customerSuppliers;
    for (var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        var memberNo = customer.memberNo ? customer.memberNo : "";
        var memberType = customer.memberType ? customer.memberType : "";
        var vehicleCount = customer.vehicleCount ? customer.vehicleCount : "";
        var vehicleDetailList = customer.vehicleDetailList;
        var licenceNo = customer.licenceNo ? customer.licenceNo : "";
        var member = customer.memberDTO;
        var balance = 0;
        if (stringUtil.isNotEmpty(member)) {
            balance = APP_BCGOGO.StringFilter.priceFilter(member.balance);
        }
        var customerShopId = customer.customerOrSupplierShopId || '';
        var customerId = customer.idStr;
        var totalReceivable = customer.totalDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalDebt) : '0';
        var totalAmount = customer.totalAmount ? APP_BCGOGO.StringFilter.priceFilter(customer.totalAmount) : '0';
        var mobile = customer.contactDTOList ? (customer.contactDTOList[0].mobile ? customer.contactDTOList[0].mobile : "") : "";
        var mobileTitle = customer.contactDTOList ? (customer.contactDTOList[0].mobile ? customer.contactDTOList[0].mobile : "") : "";
        if (APP_BCGOGO.Permission["isMobileHidden"]) {
            mobile = mobile.substr(0, 3) + "****" + mobile.substr(7, 4);
        }

        var lastExpenseTimeStr = customer.lastExpenseTimeStr ? customer.lastExpenseTimeStr : "";

        var repayDateStr = customer.repayDateStr ? customer.repayDateStr : "";
        var name = customer.name ? customer.name : "";
        var contact = customer.contactDTOList ? (customer.contactDTOList[0].name ? customer.contactDTOList[0].name : "") : "";
        var totalCounts = json.totalCounts;
        var countCustomerReturn = APP_BCGOGO.StringFilter.priceFilter(customer.countCustomerReturn);
        var returnAmount = customer.totalReturnAmount ? APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnAmount) : '0';
        var totalReturnDebt = customer.totalReturnDebt ? APP_BCGOGO.StringFilter.priceFilter(customer.totalReturnDebt) : '0';
        var isApp = customer.isApp && G.Lang.isEmpty(customer.appVehicleNo) && G.Lang.isEmpty(customer.obdVehicleNo);

        tr = '<tr class="titBody_Bg">';
        tr += '<td style="padding-left:10px;"><input type="checkbox" customerShopId="' + customerShopId + '" name="selectCustomer" value="' + customerId + '"  id=check' + (i + 1) + ' isObdCustomer="' + customer.isObd + '" />' + '</td>';

        var customerInfoHtml = '<div><a class="blue_color" href="unitlink.do?method=customer&customerId=' + customerId + '&fromPage=customerData">' + name + '</a></div><div>'
        var nodeId = null;
        if (stringUtil.isNotEmpty(member) && member.status != 'DISABLED') {
            nodeId = getId();
            customerInfoHtml += '<span id="' + nodeId + '" class="customer-vip"></span>';
        }

        if (isApp) {
          customerInfoHtml += '<span class="customer-app" title="手机APP用户"></span>';
        }

//        if (customer.isObd) {
//          customerInfoHtml += '<span class="customer-obd" title="已安装故障检测仪"></span>';
//        }
        customerInfoHtml += '</div>';
        tr += '<td>' + customerInfoHtml + "</td>";

        tr += "<td>";
        if (contact) {
            tr += '<div class="line lineConnect">' + contact + '</div>';
        }
        if (mobileTitle != "") {

            if (APP_BCGOGO.Permission["isMobileHidden"]) {

                if ($("#smsSendPermission").val() == "true") {
                    tr += '<div class="line lineConnect" style="margin-right:25px;">' + mobile + '<a class="phone" onclick="sendSms(\'' + mobileTitle + '\',\'3\',\'' + totalReceivable + '\',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + name + '\',\'' + customerId + '\')">' + '</a></div>';
                } else {
                    tr += '<div class="line lineConnect">' + mobile + '</div>';
                }
            } else {
                if ($("#smsSendPermission").val() == "true") {
                    tr += '<div class="line lineConnect" style="margin-right:25px;">' + mobileTitle + '<a class="phone" onclick="sendSms(\'' + mobileTitle + '\',\'3\',\'' + totalReceivable + '\',\'' + licenceNo + '\',\'' + repayDateStr + '\',\'' + name + '\',\'' + customerId + '\')">' + '</a></div>';
                } else {
                    tr += '<div class="line lineConnect">' + mobileTitle + '</div>';
                }
            }
        }
        if (contact == '' && mobileTitle == '') {
            tr += '<span class="gray_color">--</span>';
        }
        tr += '</td>';

        //车辆信息
        var vehicleHtml = '';
        if (G.isEmpty(vehicleDetailList)) {
            vehicleHtml = '<span class="gray_color">--</span>';
        } else {
            var h = 20 * vehicleDetailList.length;
            vehicleHtml += '<div style="line-height: 20px;height: ' + h + 'px;">';
            $.each(vehicleDetailList, function(index, val) {
                var isApp = false,isObd = false;
                if(G.Lang.isNotEmpty(customer.appVehicleNo)){
                    $.each(customer.appVehicleNo, function(index, vehicleNo) {
                      if(G.Lang.strContains(vehicleNo,val)){//if(val.contains(vehicleNo)){
                            isApp = true;
                        }
                    });
                }
                if(G.Lang.isNotEmpty(customer.obdVehicleNo)){
                    $.each(customer.obdVehicleNo, function(index, vehicleNo) {
                      if(G.Lang.strContains(vehicleNo,val)){//if(val.contains(vehicleNo)){
                            isObd = true;
                        }
                    });
                }
                vehicleHtml += '<div style="height: 20px;">';
                if(isObd){
                    vehicleHtml += '<span class="customer-obd" title="已安装故障检测仪"></span>';
                }else if(isApp){
                    vehicleHtml += '<span class="customer-app" title="手机APP用户"></span>';
                }else{
                    vehicleHtml += '<div style="width: 32px;height: 15px;float: left;"></div>';
                }
                vehicleHtml += '<span class="J_vehicleDetailHighlight">'+val+'</span>'
                vehicleHtml += '</div>';
            });
            vehicleHtml += '</div>';
        }

        tr += '<td>' + vehicleHtml + '</td>';

        tr += ' <td><span class="' + (totalAmount<=0? 'gray_color' : '') + '"><span class="arialFont">¥</span>' + totalAmount + '</span></td>';

        if (countCustomerReturn == null || countCustomerReturn == '') {
            tr += '<td><span class="gray_color">--</span></td>';
        } else {
            tr += '<td>退货' + countCustomerReturn + '次<br>累计<span class="arialFont">¥</span>' + returnAmount + '</td>';
        }

        if (APP_BCGOGO.Permission.CustomerManager.CustomerArrears) {
            tr += '<td class="income" onclick="toCreateStatementOrder(\'' + customerId + '\', \'CUSTOMER_STATEMENT_ACCOUNT\')">';
        } else {
            tr += '<td class="income">';
        }

        if (totalReceivable <= 0 && totalReturnDebt <= 0) {
            tr += '<span class="gray_color">--</span>';
        } else {
            var receiveCss = "pays";
            receiveCss += totalReceivable > 0 ? " red_color" : " gray_color";
            var payableCss = "pays";
            payableCss += totalReturnDebt > 0 ? " green_color" : " gray_color";
            tr += '<a class="blue_color line"><span  class="' + receiveCss + '">应收<span class="arialFont" style="display: inline;">¥' + totalReceivable + '</span></span>' +
                    '<span class="' + payableCss + '">应付<span class="arialFont" style="display: inline;">¥' + totalReturnDebt + '</span></span></a></td>';
        }

        if(G.isEmpty(lastExpenseTimeStr)){
          tr += '<td>--</td><td>';
        }else{
          tr += '<td>' + lastExpenseTimeStr + '</td><td>';
        }
        if (APP_BCGOGO.Permission.Version.ProductThroughDetail && !APP_BCGOGO.Permission.Version.FourSShopVersion) {
            tr += '<a class="blue_color" onclick="showProductThroughDetail(\'' + customerId + '\',\'customer\')">交易明细</a>&nbsp;';
        }
        tr += '<a class="blue_color" onclick="redirectCustomerBill(\'' + customerId + '\')">对账详细</a>&nbsp;';
        tr += '</td></tr>';

        tr += '<tr class="titBottom_Bg"><td colspan="13"></td></tr>';
        $("#customerDataTable").append($(tr));
        nodeId && tooltip(nodeId, customer);
    }
     var vehicleBrand = $('#vehicleBrandSearch').val() != $('#vehicleBrandSearch').attr('initialValue') ? $('#vehicleBrandSearch').val() : '';
     var vehicleModel = $('#vehicleModelSearch').val() != $('#vehicleModelSearch').attr('initialValue') ? $('#vehicleModelSearch').val() : '';
     var vehicleColor = $('#vehicleColorSearch').val() != $('#vehicleColorSearch').attr('initialValue') ? $('#vehicleColorSearch').val() : '';

    !G.isEmpty(vehicleBrand) && $(".J_vehicleDetailHighlight").highlight(vehicleBrand, {className:"red_color"});
    !G.isEmpty(vehicleModel) && $(".J_vehicleDetailHighlight").highlight(vehicleModel, {className:"red_color"});
    !G.isEmpty(vehicleColor) && $(".J_vehicleDetailHighlight").highlight(vehicleColor, {className:"red_color"});
}

function redirectCustomerBill(customerId) {
    var url = "unitlink.do?method=customer&fromPage=customerData&title=statementAccount&customerId=" + customerId;
    window.open(url);
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
//  var idStr = domObj.id;
//  if (idStr) {
//    var temp = idStr.split("_");
//    $("#" + temp[0] + "_prompt").css("display", "none");
//  }
}

function smsHistory(customerId,mobile) {
    if (mobile == null || jQuery.trim(mobile) == "") {
        jQuery("#enterPhoneSupplierId").val(jQuery("#supplierId").val());
        Mask.Login();
        jQuery("#enterPhoneSetLocation").fadeIn("slow");
        return;
    }
    window.location = encodeURI("sms.do?method=smswrite&customerId="+customerId+"&mobile=" + mobile);
}

function memberCheck(result) {
    var dom = $(".memberChecks > label:not(:first)>input");
    var more_dom = $(".more_memberChecks > label > input");
    for (var i = 0; i < dom.length; i++) {
        $(dom[i])[0].checked = result;
    }
    for (var i = 0; i < more_dom.length; i++) {
        $(more_dom[i])[0].checked = result;
    }
}

$(function() {
    var droplist = APP_BCGOGO.Module.droplist;
    $('#vehicleBrandSearch').add('#vehicleModelSearch').add('#vehicleColorSearch').focus(function(e) {
        if ($(this).val() == $(this).attr('initialValue')) {
            $(this).val('');
        }
        $(this).css('color','#000000');
    }).blur(function(e) {
        if ($(this).val() == '') {
            $(this).css('color','#ADADAD');
            $(this).val($(this).attr('initialValue'));
        }
    });

    var search = function(dom, searchWord, searchField) {
        droplist.setUUID(GLOBAL.Util.generateUUID());
        $.post('product.do?method=searchVehicleSuggestionForGoodsBuy', {
              now:new Date().getTime(),
              searchWord:searchWord,
              searchField:searchField,
              uuid: droplist.getUUID()
        }, function (result) {
            droplist.show({
                "selector": $(dom),
                "data": result,
                "onSelect": function (event, index, data) {
                    $(dom).val(data.label);
                    $(dom).css({"color": "#000000"});
                    droplist.hide();
                    $("#customerSearchBtn").click();
                }
            });

                }, 'json')
            }

            $('#vehicleBrandSearch').focus(
            function (e) {
                        search(this, $(this).val(), 'brand');
            }).bind('input', function (e) {
                        search($('#vehicleBrandSearch')[0], $('#vehicleBrandSearch').val(), 'brand');
                    })

            $('#vehicleModelSearch').focus(
            function (e) {
                        search(this, $(this).val(), 'model');
            }).bind('input', function () {
                        search($('#vehicleModelSearch')[0], $('#vehicleModelSearch').val(), 'model');
                    });

    });

function tooltip(id, customer){
    var memberInfo = customer.memberDTO;
    var node = setTooltipHtml(id, customer.idStr);
    setTooltipContent(node,memberInfo);
}

function setTooltipHtml(id , customerId){
    var root = $('#'+id);
    var body = $('<div class="tooltipBody"></div>').append('<a class="icon_close"></a>') .append('<div class="title"><div style="float: left;width: 60px;"><strong>会员信息</strong></div><div style="float: right;width: 72px;"><a href="javascript:void(0)">查看更多资料</a></div></div>').append('<div class="prompt-left"></div>').append('<div class="prompt-right"></div>')
    var node = $('<div class="tooltip" style="margin:0px 0px 0px -12px;display: none;"></div>').append('<div class="tooltipTop"></div>').append(body).append('<div class="tooltipBottom"></div>')
    root.append(node).bind('mouseover',function(e){
        node.show();
    }).bind('mouseout',function(){
        node.hide();
    });
    $('.icon_close',node).click(function(){
        node.hide();
    });
    $('a', node).last().click(function(){
        window.open('unitlink.do?method=customer&fromPage=customerData&customerId=' + customerId);
    });
    return node;
}

function setTooltipContent(node,memberInfo){
    var leftList = [{name:'卡号：',val:'memberNo'}, {name:'卡类型：',val:'type'}, {name:'入会日期：',val:'joinDateStr'}, {name:'过期日期：',val:'serviceDeadLineStr'}, {name:'会员储值：',val:'balanceStr'}];
    var left = $('.prompt-left',node);
    var right = $('.prompt-right',node);
    $.each(leftList,function(i,n){
        var val = memberInfo[n.val];
        if(val && val.length>11){
            val = '<span title="' + val + '">' + val.substr(0,8) + '...</span>';
        } else {
            val = G.normalize(val);
        }
        left.append('<div class="clear"><div class="left">' + n.name + '</div><div class="right">' + val + '</div></div>');
    });
    var memberServices = memberInfo.memberServiceDTOs;
    if (memberServices) {
        right.append('<div>服务项目(共' + memberServices.length + '项)</div>');
        $.each(memberServices, function (i, memberService) {
            var name = memberService.serviceName && memberService.serviceName.length > 11 ? '<span title="' + memberService.serviceName + '">' + memberService.serviceName.substr(0, 8) + '...</span>' : memberService.serviceName;
            right.append('<div style="overflow: hidden;"><div class="div left">'+ name +'</div><div class="div right">' + memberService.timesStr + '</div></div>');
        });
    }else{
        right.append('<div class="gray_color">暂无服务项目）</div>');
    }
}

function getUniqueId() {
    var key = 'bcgogo_';
    var i = 1000;
    return function () {
        return key + i++;
    }
}