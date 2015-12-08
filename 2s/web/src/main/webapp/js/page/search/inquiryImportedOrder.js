var LazySearcher = APP_BCGOGO.wjl.LazySearcher;
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
//order 对应的 items
var ordersStoreMap = {};
getOrderDetailsByOrderId = function (key) {
    return ordersStoreMap[key];
};
$(function () {
    tableUtil.tableStyle('#tabList');
    $(".good_his > .today_list").hover(function(event){
        if($(event.currentTarget).hasClass("hoverList")) {
            $(event.currentTarget).addClass("hover_jy");
        } else {
            $(event.currentTarget).addClass("hoverList");
        }

    },function(event){
        if(!$(event.currentTarget).hasClass("hover_jy")) {
            $(event.currentTarget).removeClass("hoverList");
        }

    });
    //时间 初始化
    function initTimeOfMonth() {
        if ($("#vehicleNumber").val() != $("#vehicleNumber").attr("initialvalue") || $("#vehicleModel").val() != $("#vehicleModel").attr("initialvalue") || $("#vehicleModel").val() != $("#vehicleModel").attr("initialvalue")) {
            $("#startDate").val("本月第一天");
            $("#endDate").val("今天");
            $(".good_his > .today_list").removeClass("hoverList");
        }
    }

    function initTimeOfDay() {
        $(".good_his > .today_list").removeClass("hoverList");
        $("#date_today").parent().addClass("hoverList");
        $("#startDate").val("今天");
        $("#endDate").val("今天");
    }

    function initTimeOfYear() {
        $("#startDate").val("今年第一天");
        $("#endDate").val("今天");
        $(".good_his > .today_list").removeClass("hoverList");
        $("#date_this_year").parent().addClass("hoverList");
    }

    $("#moreConditionBtn").attr("details", "false");

    //显示更过搜索 更多条件
    $("#moreConditionBtn").click(function (event) {
        if ($(event.target).attr("details") == "true") {
            if (pageType == "inventory" || pageType == "purchase" || pageType == "return") {
                $(".vehicle,.member,#shigong,#settlementMethod").hide();
                $("#cash,#bankCard,#cheque,#deposit,#notPaid,#memberBalancePay,.searchDetail").attr("disabled", true);
            } else if (pageType == "wash_beauty") {
                $("#productProperty,#saler").hide();
            } else if (pageType == "sale") {
                $("#invoicingItem,#invoicingDepartment,#serviceWorker,#memberWouldCharge,#vehicleNumber,#vehicleModel,#vehicleBrand,#shigong").hide().attr("disabled", true);
            }
            $(event.target).css("background", "url('images/rightArrow.png') no-repeat right");
            $(event.target).attr("details", "false");
        } else {
            if (pageType == "inventory" || pageType == "purchase" || pageType == "return") {
                $(".vehicle,.member,#shigong,#settlementMethod").show();
                $("#cash,#bankCard,#cheque,#deposit,#notPaid,#memberBalancePay,.searchDetail").removeAttr("disabled");
            } else if (pageType == "wash_beauty") {
                $("#productProperty,#saler").show();
            } else if (pageType == "sale") {
                $("#invoicingItem,#invoicingDepartment,#serviceWorker,#memberWouldCharge,#vehicleNumber,#vehicleModel,#vehicleBrand,#shigong").show().removeAttr("disabled");
            }
            $(event.target).css("background", "url('images/rightTop.png') no-repeat right");
            $(event.target).attr("details", "true");
        }
    });

    //reset
    $("#resetSearchCondition").click(function () {
        //reset form
        $("#inquiryCenterSearchForm").resetForm();
        //checkbox 单据类型   欠款
        if (pageType == "inventory") {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            $("#inventoryLabel").removeClass("chk_off").addClass("chk_on");
            initTimeOfYear();
        } else if (pageType == "sale") {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            $("#saleLabel").removeClass("chk_off").addClass("chk_on");
            initTimeOfYear();
        } else if (pageType == "repair") {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            $("#repairLabel").removeClass("chk_off").addClass("chk_on");
            initTimeOfYear();
        } else if (pageType == "wash") {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            $("#washLabel").removeClass("chk_off").addClass("chk_on");
            initTimeOfYear();
        } else if (pageType == "return") {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            $("#returnLabel").removeClass("chk_off").addClass("chk_on");
            initTimeOfYear();
        } else if (pageType == "member") {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            $("#memberLabel").removeClass("chk_off").addClass("chk_on");
            initTimeOfYear();
        } else {
            $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
            initTimeOfDay();
        }
        //checkbox 结算方式
        $(".chk_choose > label").removeClass("chk_on").addClass("chk_off");
        $("#inquiryCenterSearchForm> div>input,#operator").each(function (index, domObject) {
            if ($(domObject).attr("initialValue")) {
                if ($(domObject).val() != $(domObject).attr("initialValue")) {
                    $(domObject).css({"color":"#000000"});
                } else {
                    $(domObject).css({"color":"#ADADAD"});
                }
            }
        });
        //reset
        var inputs = $("#inquiryCenterSearchForm> div>input,#operator");
        var initialValue;
        var value;
        for (var i = 0; i < inputs.length; i++) {
            initialValue = $(inputs[i]).attr("initialValue");
            value = inputs[i].value;
            if (!initialValue) {
                continue;
            }
            $(inputs[i]).val(initialValue).css({"color":"#ADADAD"});
        }
    });

    // 会员类型
    $("#memberCardType")
        .click(function () {
            var offset = $("#memberCardType").offset();
            var offsetHeight = $("#memberCardType").height();
            var offsetWidth = $("#memberCardType").width();
            $("#memberCardTypesPanel").css({
                'display':'block', 'position':'absolute',
                'left':offset.left + 'px', 'top':offset.top + offsetHeight + 3 + 'px',
                'overflow-x':"hidden", 'overflow-y':"hidden",
                'color':'#000000', 'padding-left':0 + 'px',
                'width':offsetWidth
            });
        })
        .blur(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == '') {
                    event.target.value = initialValue;
                    $(this).css({"color":"#ADADAD"});
                }
            } else {
                $(this).css({"color":"#000000"});
            }
        })
        .focus(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == initialValue) {
                    event.target.value = "";
                }
                $(this).css({"color":"#000000"});
            }
        });

    $("#memberCardTypesPanel > div")
        .click(function (e) {
            $('#memberCardType').val($(e.target).html()).focus();
            $("#memberCardTypesPanel").hide();
        })
        .mouseover(function (e) {
            $("#memberCardTypesPanel > div").css({'background-color':'#FFFFFF', 'color':'#000000'});
            $(e.target).css({"background-color":"#397DF3", "color":"#FFFFFF", "cursor":"pointer"});
        });


    //时间段
    $("#startDate,#endDate")
        .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":false,
            "changeYear":true,
            "showHour":false,
            "showMinute":false,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":""
        })
        .blur(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (startDate == "" || endDate == "") return;
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (transformToDateStr(startDate) > transformToDateStr(endDate)) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        })
        .bind("click", function () {
            $(this).blur();
        })
        .change(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            $(".good_his > .today_list").removeClass("hoverList");
            if (endDate == "" || startDate == "") {
                return;
            }
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (transformToDateStr(startDate) > transformToDateStr(endDate)) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        });

    //结算复选框
    $(".chk_choose").change(function (event) {
        checkBoxSelect(event.target);
    });

    //单据类型 除了【所有】
    //勾选其他选项时，未全部勾选时，【所有】不自动勾选；全部勾选时，【所有】自动勾选；
    $(".goods_chk > label:not(:first)").change(function (event) {
        checkBoxSelect(event.target);
        var dom = $(".goods_chk > label:not(:first)>input");
        //判断是否选中全部
        var isAllGoodsChecked = true;
        for (var i = 0; i < dom.length; i++) {
            if ($(dom[i]).parent().hasClass("chk_off")) {
                isAllGoodsChecked = false;
            }
        }
        if (isAllGoodsChecked) {
            $("#orderTypeAll").parent().removeClass("chk_off").addClass("chk_on");
        } else {
            $("#orderTypeAll").parent().removeClass("chk_on").addClass("chk_off");
        }
        searchOrderImmediately();
    });

    //单据类型【所有】
    $("#orderTypeAll").change(function () {
        if ($(this).parent().hasClass("chk_off")) {
            //把【所有】勾选取消时，其他选项也都自动勾选
            $(".goods_chk > label:not(:first)").removeClass("chk_off").addClass("chk_on");
            $(this).parent().removeClass("chk_off").addClass("chk_on");
        } else {
            //把【所有】勾选取消时，其他选项也都自动取消勾选
            $(".goods_chk > label:not(:first)").removeClass("chk_on").addClass("chk_off");
            $(this).parent().removeClass("chk_on").addClass("chk_off");
        }
    });

    //日期条件
    $(".good_his > .today_list").click(function (event) {
        $(".good_his > .today_list").removeClass("hoverList");
        $(".good_his > .today_list").removeClass("hover_jy");
        $(event.currentTarget).addClass("hoverList");
        $(event.currentTarget).addClass("hover_jy");
        var date = $(event.currentTarget).children().attr("id");
        if (date == "date_yesterday") {
            $("#startDate").val("昨天");
            $("#endDate").val("今天");
        } else if (date == "date_today") {
            $("#startDate").val("今天");
            $("#endDate").val("今天");
        } else if (date == "date_last_month") {
            $("#startDate").val("上月第一天");
            $("#endDate").val("上月最后一天");
        } else if (date == "date_this_month") {
            $("#startDate").val("本月第一天");
            $("#endDate").val("今天");
        } else if (date == "date_this_year") {
            $("#startDate").val("今年第一天");
            $("#endDate").val("今天");
        } else {
            GLOBAL.error("inquiry center today_list selecting Exception!");
        }
        searchOrderImmediately();
    });

    //相当于 单击“搜索”
    function searchOrderImmediately() {
        $("#rowStart").val(0);
        initData("inquiryCenter");
        $("#inquiryCenterSearchForm").submit();
    }

    //消费金额过滤
    $(".mon_search").keyup(function (event) {
        if (!APP_BCGOGO.Validator.stringIsPrice(event.target.value)) {
            event.target.value = "";
        }
    });

    //input输入框 聚焦与失去焦点的时候 自动补充提示信息
    $("#inquiryCenterSearchForm>div>input[type='text'],#operator,#memberCardType")
        .blur(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == '') {
                    event.target.value = initialValue;
                    $(this).css({"color":"#ADADAD"});
                }
            } else {
                $(this).css({"color":"#000000"});
                stockSearchBoxsAdjust(event.target);
            }
        })
        .focus(function (event) {
            $(event.target).attr("lastValue", event.target.value);

            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == initialValue) {
                    event.target.value = "";
                }
                $(this).css({"color":"#000000"});
            }
        })
        .change(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value != initialValue) {
                    $(this).css({"color":"#000000"});
                } else {
                    $(this).css({"color":"#ADADAD"});
                }
            }
        })
        .each(function (index, domObject) {
            if ($(domObject).attr("initialValue")) {
                if ($(domObject).val() != $(domObject).attr("initialValue")) {
                    $(this).css({"color":"#000000"});
                } else {
                    $(this).css({"color":"#ADADAD"});
                }
            }
        });

    $("#inquiryCenterSearchForm").submit(function () {
        checkRequest();
        //checkbox 与 图片 同步
        syncCheckBoxSelect($(".goods_chk > label >input:not(:first)"));
        syncCheckBoxSelect($(".chk_choose > label >input"));
        //过滤掉所有提示
        var inputs = $("#inquiryCenterSearchForm> div>input,#operator");
        var initialValue;
        var value;
        for (var i = 0, max = inputs.length; i < max; i++) {
            initialValue = $(inputs[i]).attr("initialValue");
            value = inputs[i].value;
            if (!initialValue) {
                continue;
            }
            if (value == initialValue) {
                $(inputs[i]).val("");
            }
        }
        var options = {
            target:'#inquiryCenterSearchForm', success:showResponse, error:addHint,
            type:'post', dataType:"json", url:"inquiryCenter.do?method=inquiryImportedOrder"
        };
        $(this).ajaxSubmit(options);
        return false;
    });

    //校验
    function checkRequest() {
        var dom = $(".goods_chk > label >input");
        //判断是否选中全部
        var isAllOrderTypeNotChecked = true;
        for (var i = 0,max = dom.length; i < max; i++) {
            if ($(dom[i]).parent().hasClass("chk_on")) {
                isAllOrderTypeNotChecked = false;
            }
        }
        if (isAllOrderTypeNotChecked) {
            $(dom).parent().removeClass("chk_off").addClass("chk_on");
        }
    }

    //ajax 请求后显示结果
    function showResponse(json) {
        addHint(null);
        getInquiryCenterTr(json);
        setOrderCountsAndAmounts(json);
        //set pager totalRows
        if (json != null) {
            $("#totalRows").val(json.numFound);
        }
        //分页dynamical formId
        initPagesForSolr("inquiryCenter", "#inquiryCenterSearchForm", null);

        tableUtil.tableStyle('#tabList');
    }

    //添加所有提示
    function addHint(e) {
        var inputs = $("#inquiryCenterSearchForm> div>input,#operator");
        var initialValue;
        var value;
        for (var i = 0; i < inputs.length; i++) {
            initialValue = $(inputs[i]).attr("initialValue");
            value = inputs[i].value;
            if (!initialValue) {
                continue;
            }
            if (value == "") {
                $(inputs[i]).val(initialValue);
            }
        }
        if (e) {
            GLOBAL.error("inquiryCenter.js inquiryCenterSearchForm error:" + e);
        }
    }

    //时间名词转换成标准时间
    function transformToDateStr(str) {
        if (!APP_BCGOGO.Validator.stringIsZhCn(str)) return str;
        var date = new Date();
        var day = date.getDate();  //getDay 是星期
        var mouth = date.getMonth() + 1;  //+1代表本月
        var year = date.getFullYear();
        var time = null;
        if (str == "今天") {
            time = year + "-" + addZero(mouth) + "-" + addZero(day);
        } else if (str == "昨天") {
            time = year + "-" + addZero(mouth) + "-" + addZero((day - 1));
        } else if (str == "上月第一天") {
            time = year + "-" + addZero((mouth - 1)) + "-01";
        } else if (str == "本月第一天") {
            time = year + "-" + addZero(mouth) + "-01";
        } else if (str == "上月最后一天") {
            var lastMonthLastDay = new Date(new Date(year, mouth, 1).getTime() - 1000 * 60 * 60 * 24);
            time = lastMonthLastDay.getFullYear() + "-" + addZero(lastMonthLastDay.getMonth()) + "-" + addZero(lastMonthLastDay.getDate());
        } else if (str == "今年第一天") {
            time = year + "-01-01";
        }
        return time;
    }

    //把8转换成08
    function addZero(data) {
        if (data < 10) return "0" + data;
        else return data;
    }

    //选中复选框
    function checkBoxSelect(dom) {
        if ($(dom).parent().hasClass("chk_off")) {
            $(dom).parent().removeClass("chk_off").addClass("chk_on");
        } else {
            $(dom).parent().removeClass("chk_on").addClass("chk_off");
        }
    }

    //同步图片与select
    function syncCheckBoxSelect(dom) {
        for (var i = 0, max = dom.length; i < max; i++) {
            if ($(dom[i]).parent().hasClass("chk_off")) {
                $(dom[i]).attr("checked", false);
            } else {
                $(dom[i]).attr("checked", true);
            }
        }
    }

    //拼接order结果
    function getInquiryCenterTr(json) {
        $("#tabList tr").remove();
        var tr;
        if (json == null || json.orders == null || json.orders == 0) {
            tr = '<tr class="table-row-original" id="tabStorage[0]"><td colspan="9" style="border-left:none;border-right:none;text-align: center;" class="txt_right">对不起，没有找到您要的单据信息！</td></tr >';
            $("#tabList").append($(tr));
            return;
        }
        $("#tableDiv").css("height","300px");
        var orders = json.orders;
        var inputtingTimerId = 0;
        ordersStoreMap = {};
        for (var i = 0, max = orders.length; i < max; i++) {
            var order = orders[i];
            var orderId = (!order.orderIdStr ? "" : order.orderIdStr);
            if (orderId) {
                ordersStoreMap[orderId] = order;
            }
            var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
            var orderType = (!order.orderType ? "--" : order.orderType);
            var createdTimeStr = (!order.createdTimeStr ? "--" : order.createdTimeStr);
            var customerOrSupplierName = (!order.customerOrSupplierName ? "--" : order.customerOrSupplierName);
            var vehicle = (!order.vehicle ? "--" : order.vehicle);
            var orderContent = (!(order.orderContent) ? "--" : order.orderContent)
            var orderTypeValue = (!(order.orderTypeValue) ? "--" : order.orderTypeValue);
            var amount;
            if (order.orderType && order.orderType == 'MEMBER_RETURN_CARD') { //退卡显示为负
                amount = (!(order.amount) ? "" : (-order.amount + '元'));
            } else {
                amount = (!(order.amount) ? ("WASH_SETTLED" == order.orderStatus ? "计次消费" : "--") : (order.amount + '元'));
            }
            var orderStatusValue = (!order.orderStatusValue ? "--" : order.orderStatusValue)
            tr = '<tr class="table-row-original" id="searchResultOrder[' + i + ']">';
            var customerStatus = (!order.customerStatus ? "" : order.customerStatus);
            tr += '<td style="border-left:none;" class="txt_right">' + (i + 1) + '</td>';
            tr += '<td style="text-align: center;" title="' + receiptNo + '">'+receiptNo + '</td>';
            if ("DISABLED" == customerStatus) {
                tr += '<td style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;color:#999999" title="' + customerOrSupplierName + '">' + customerOrSupplierName + '</td>';
            }
            else {
                tr += '<td style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" title="' + customerOrSupplierName + '">' + customerOrSupplierName + '</td>';
            }
            tr += '<td style="text-align: center;" title="' + createdTimeStr + '">' + createdTimeStr + '</td>';
            tr += '<td style="text-align: center;" title="' + vehicle + '">' + vehicle + '</td>';
            tr += '<td style="text-align: center;" title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
            tr += '<td style="text-overflow: ellipsis;white-space: nowrap;overflow: hidden;" title="' + orderContent + '">' + orderContent + '</td>';
            tr += '<td title="' + amount + '">' + amount + '</td>';
            tr += '<td style="border-right:none;text-align: center;" title="' + orderStatusValue + '">' + orderStatusValue + '</td>';
            tr += '<input type="hidden" class="orderIds" value="' + order.orderIdStr + '" name="' + order.orderIdStr + '">' +
                '<input type="hidden" class="orderTypes" value="' + order.orderType + '" name="' + order.orderType.toLowerCase() + '">' +
                '</tr>';
            var $tr = $(tr);
            $tr.attr("orderInfo", JSON.stringify({ orderType:order.orderType.toLowerCase(), orderIdStr:order.orderIdStr}));
            $tr.mouseover(function (e) {
                var foo = this;
                inputtingTimerId = setTimeout(function () {
                    showOrderItems.searchOrderItemDetails(e, foo);
                }, 1000);
            })
                .mouseout(function (e) {
                    if (!inputtingTimerId) return;
                    clearTimeout(inputtingTimerId);
                })
                .click(function (e) {
                    showOrderItems.searchOrderItemDetails(e, this);
                })
            $("#tabList").append($tr);
        }
    }

    //order item details
    var showOrderItems = function () {
        return{
            searchOrderItemDetails:function (e, domObj) {
                var order = $.parseJSON($(domObj).attr("orderInfo"));
                $(".up_storage").hide();
                var repealed = false;
                var jsonStr = getOrderDetailsByOrderId(order.orderIdStr)
                if (!jsonStr) {
                    GLOBAL.error("items is null!");
                    return;
                } else if (!order.orderType) {
                    GLOBAL.error("order type is null!");
                    return;
                }
                if (order.orderType == "sale") {
                    getSaleItems(jsonStr);
                    repealed = (jsonStr.orderStatus == "SALE_REPEAL");
                } else if (order.orderType == "repair") {
                    getRepairItems(jsonStr);
                    repealed = (jsonStr.orderStatus == "REPAIR_REPEAL");
                } else if (order.orderType == "inventory") {
                    getInventoryItems(jsonStr);
                    repealed = (jsonStr.orderStatus == "PURCHASE_INVENTORY_REPEAL");
                } else if (order.orderType == "wash_beauty") {
                    getWashBeautyItems(jsonStr);
                    repealed = (jsonStr.orderStatus == "WASH_REPEAL");
                } else if (order.orderType == "return") {
                    getReturnItems(jsonStr);
                } else if (order.orderType == "member_buy_card") {
                    getMemberBuyCardItems(jsonStr);
                } else if (order.orderType == "member_return_card") {
                    getMemberReturnCardItems(jsonStr);
                } else if (order.orderType == "purchase") {
                    getPurchaseItems(jsonStr);
                    repealed = (jsonStr.orderStatus == "PURCHASE_ORDER_REPEAL");
                } else {
                    GLOBAL.error("inquiry center mouseover action, orderType[" + order.orderType + "] is illegal !");
                }
                //定位 操作 单据详细
                var offsetHeight = $(e.target).height();
                var top = $(e.target).offset().top;
                var itemsHeight = $("#" + order.orderType).height();
                var scrollTop = $("#" + order.orderType).scrollTop();
                if (itemsHeight < 750 / 2) {
                    if (document.documentElement.clientHeight / 2 < top) {
                        top = top - itemsHeight - offsetHeight - 17;
                    } else {
                        top = top - offsetHeight + 10
                    }
                } else {
                    window.parent.document.getElementById("iframe_PopupBox_inquiry_center").style.height = ((itemsHeight + 100) > 750 ? (itemsHeight + 100) : 750) + "px";
                    top = 0;
                }
                $("#" + order.orderType).css({'position':'absolute', 'top':(top) + 'px'}).show();
                if (repealed) {
                    $("#" + order.orderType + " input[value='作废']").attr("disabled", true).css("color", "#c4c4c4");
                } else {
                    $("#" + order.orderType + " input[value='作废']").removeAttr("disabled").css("color", "#6699CC");
                }
            }
        }
    }();
    //set order counts amounts
    function setOrderCountsAndAmounts(json) {
        //每次show 数据之前 先清空
        $(".statisticalData").find("span[id^='counts_']").each(function () {
            $(this).html("0");
        });
        $(".statisticalData").find("span[id^='amounts_']").each(function () {
            $(this).html("0.0");
        });
        //
        if (json == null || json.orders == null)return;
        if (json.orderTypeStat) {
          var orderTypeStat = json.orderTypeStat;
          for(var i=0;i<orderTypeStat.length;i++){
              $("#counts_" + orderTypeStat[i][0].toLowerCase()).html(APP_BCGOGO.StringFilter.intFilter(orderTypeStat[i][1]));
             $("#counts_" + orderTypeStat[i][0].toLowerCase()).attr("title", APP_BCGOGO.StringFilter.intFilter(orderTypeStat[i][1]));

          }
        }
        if (json.totalAmounts) {
            var amounts = json.totalAmounts;
            for (var p in amounts) {
                var total;
                if (p == 'MEMBER_RETURN_CARD') {
                    total = "-" + APP_BCGOGO.StringFilter.priceFilter(amounts[p]);
                } else {
                    total = APP_BCGOGO.StringFilter.priceFilter(amounts[p]);
                }
                $("#amounts_" + p.toLowerCase()).html(total);
                $("#amounts_" + p.toLowerCase()).attr("title", total);
            }
        }
    }

    //入库单详情
    function getInventoryItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#inventoryHistory tr:not(:first)").remove();
        var items = order.itemIndexDTOs;
        $("#inventorySupplierName > span").html(order.customerOrSupplierName);//供应商名
        $("#inventorySupplierContact > span").html(order.contact);  //联系人
        $("#inventoryId > span").html(order.receiptNo);                //单据号
        $("#inventoryStatus > span").html(order.orderStatusValue);    //状态
        $("#inventoryDate > span").html(order.vestDateStr);  //归属时间
        $("#inventoryOrderTotalAmount > span").html(order.amount); //单据总计
        $("#inventoryOrderSettledAmount > span").html(order.settled);    //实收
        $("#inventoryOrderDebt > span").html(order.debt);   //欠款
        $("#inventorySupplierId").val(order.customerOrSupplierId);// supplier id
        $("#purchaseInventoryId").val(order.orderIdStr);      //order id
        if (order.memo) {
            $("#inventoryOrderMemo >span").html(order.memo);
        }
        for (var i = 0, max = items.length; i < max; i++) {
            var item = items[i];
            var tr = '<tr class="table-row-original" id="inventoryHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (!item.commodityCode ? "" : item.commodityCode) + '</td>';
            tr += '<td>' + (!item.itemName ? "" : item.itemName) + '</td>';
            tr += '<td>' + (!item.itemBrand ? "" : item.itemBrand) + '</td>';
            tr += '<td>' + (!item.itemSpec ? "" : item.itemSpec) + '</td>';
            tr += '<td>' + (!item.itemModel ? "" : item.itemModel) + '</td>';
            tr += '<td>' + (!item.vehicleBrand ? "" : item.vehicleBrand) + '</td>';
            tr += '<td>' + (!item.vehicleModel ? "" : item.vehicleModel) + '</td>';
            tr += '<td>' + (!item.itemPrice ? "" : item.itemPrice) + '</td>';
            tr += '<td>' + (!item.itemCount ? "" : item.itemCount) + '</td>';
            tr += '<td>' + (!item.unit ? "" : item.unit) + '</td>';
            var orderTotalAmount = APP_BCGOGO.StringFilter.priceFilter(Number(item.itemPrice) * Number(item.itemCount));
            tr += '<td style="border-right:none;">' + (!orderTotalAmount ? "" : orderTotalAmount) + '</td>';
            tr += '<input type="hidden" class="inventoryProductIds" id="inventoryProductId[' + i + ']" value="' + item.productIdStr + '">';
            tr += '</tr>';
            tr = $(tr);
            $("#inventoryHistory").append(tr);
        }
    }

    //采购单详情
    function getPurchaseItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#purchaseHistory tr:not(:first)").remove();
        var items = order.itemIndexDTOs;
        $("#purchaseId > span").html("");
        $("#purchaseSupplierName > span").html(order.customerOrSupplierName);
        $("#purchaseSupplierContact > span").html(order.contact);  //联系人
        $("#purchaseId > span").html(order.receiptNo);
        $("#purchaseStatus > span").html(order.orderStatusValue);
        $("#purchaseDate > span").html(order.vestDateStr);
        $("#purchaseOrderTotalAmount > span").html(order.amount); //单据总计
        if (order.memo) {
            $("#purchaseOrderMemo > span").html(order.memo);
        }
        $("#purchaseSupplierId").val(order.customerOrSupplierId);
        $("#purchaseOrderId").val(order.orderIdStr);
        for (var i = 0, max = items.length; i < max; i++) {
            var item = items[i];
            var tr = '<tr class="table-row-original" id="purchaseHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (!item.commodityCode ? "" : item.commodityCode) + '</td>';
            tr += '<td>' + (!item.itemName ? "" : item.itemName) + '</td>';
            tr += '<td>' + (!item.itemBrand ? "" : item.itemBrand) + '</td>';
            tr += '<td>' + (!item.itemSpec ? "" : item.itemSpec) + '</td>';
            tr += '<td>' + (!item.itemModel ? "" : item.itemModel) + '</td>';
            tr += '<td>' + (!item.vehicleBrand ? "" : item.vehicleBrand) + '</td>';
            tr += '<td>' + (!item.vehicleModel ? "" : item.vehicleModel) + '</td>';
            tr += '<td>' + (!item.itemPrice ? "" : item.itemPrice) + '</td>';
            tr += '<td>' + (!item.itemCount ? "" : item.itemCount) + '</td>';
            tr += '<td>' + (!item.unit ? "" : item.unit) + '</td>';
            var orderTotalAmount = APP_BCGOGO.StringFilter.priceFilter(Number(item.itemPrice) * Number(item.itemCount));
            tr += '<td style="border-right:none;">' + (!orderTotalAmount ? "" : orderTotalAmount) + '</td>';
            tr += '<input type="hidden" class="purchaseProductIds" id="purchaseProductId[' + i + ']" value="' + item.productIdStr + '" name="' + item.productIdStr + '">';
            tr += '</tr>';
            tr = $(tr);
            $("#purchaseHistory").append(tr);
        }
    }

    //销售单详情
    function getSaleItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#saleHistory tr:not(:first)").remove();
        var items = order.itemIndexDTOs;
        $("#saleId > span").html("");
        if ("DISABLED" == order.customerStatus) {
            $("#saleCustomerName > span").css("color", "#999999");
        } else {
            $("#saleCustomerName > span").css("color", "#000000");
        }
        $("#saleCustomerName > span").html(order.customerOrSupplierName);
        $("#saleCustomerContact > span").html(order.contact);
        $("#saleId > span").html(order.receiptNo);
        $("#saleStatus > span").html(order.orderStatusValue);
        $("#saleDate > span").html(order.vestDateStr);
        $("#saleCustomerId").val(order.customerOrSupplierId);
        $("#saleOrderTotalAmount > span").html(order.amount);//总计
        $("#saleOrderSettledAmount > span").html(order.settled);    //实收
        $("#saleOrderDebt > span").html(order.debt);   //欠款
        if (order.memo) {
            $("#saleOrderMemo >span").html(order.memo);
        }
        $("#saleOrderId").val(order.orderIdStr);
        for (var i = 0, max = items.length; i < max; i++) {
            var item = items[i];
            var tr = '<tr class="table-row-original" id="saleHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (!item.commodityCode ? "" : item.commodityCode) + '</td>';
            tr += '<td>' + (!item.itemName ? "" : item.itemName) + '</td>';
            tr += '<td>' + (!item.itemBrand ? "" : item.itemBrand) + '</td>';
            tr += '<td>' + (!item.itemSpec ? "" : item.itemSpec) + '</td>';
            tr += '<td>' + (!item.itemModel ? "" : item.itemModel) + '</td>';
            tr += '<td>' + (!item.vehicleBrand ? "" : item.vehicleBrand) + '</td>';
            tr += '<td>' + (!item.vehicleModel ? "" : item.vehicleModel) + '</td>';
            tr += '<td>' + (!item.itemPrice ? "" : item.itemPrice) + '</td>';
            tr += '<td>' + (!item.itemCount ? "" : item.itemCount) + '</td>';
            tr += '<td>' + (!item.unit ? "" : item.unit) + '</td>';
//            tr += '<td style="border-right:none;">' + (!item.orderTotalAmount ? "" : item.orderTotalAmount) + '</td>';   todo :item保存逻辑有问题
            var orderTotalAmount = APP_BCGOGO.StringFilter.priceFilter(Number(item.itemPrice) * Number(item.itemCount)); //todo :保存不正确
            tr += '<td style="border-right:none;">' + (!orderTotalAmount ? "" : orderTotalAmount) + '</td>';
            tr += '<input type="hidden" class="saleProductIds" id="saleProductId[' + i + ']" value="' + item.productIdStr + '" name="' + item.productIdStr + '">';
            tr += '</tr>';
            tr = $(tr);
            $("#saleHistory").append(tr);
        }
    }

    //退货单
    function getReturnItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#returnHistory tr:not(:first)").remove();
        var items = order.itemIndexDTOs;
        $("#returnSupplierName > span").html(order.customerOrSupplierName);
        $("#returnSupplierContact > span").html(order.contact);
        $("#returnId > span").html(order.receiptNo); //单据号
        $("#returnStatus > span").html(order.orderStatusValue);
        $("#returnDate > span").html(order.vestDateStr);
        $("#returnOrderTotalAmount > span").html(order.amount); //总计
        if (order.memo) {
            $("#returnOrderMemo >span").html(order.memo);
        }
        $("#returnSupplierId").val(order.customerOrSupplierId);
        $("#purchaseReturnId").val(order.orderIdStr);
        for (var i = 0, max = items.length; i < max; i++) {
            var item = items[i];
            var tr = '<tr class="table-row-original" id="returnHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (!item.commodityCode ? "" : item.commodityCode) + '</td>';
            tr += '<td>' + (!item.itemName ? "" : item.itemName) + '</td>';
            tr += '<td>' + (!item.itemBrand ? "" : item.itemBrand) + '</td>';
            tr += '<td>' + (!item.itemSpec ? "" : item.itemSpec) + '</td>';
            tr += '<td>' + (!item.itemModel ? "" : item.itemModel) + '</td>';
            tr += '<td>' + (!item.vehicleBrand ? "" : item.vehicleBrand) + '</td>';
            tr += '<td>' + (!item.vehicleModel ? "" : item.vehicleModel) + '</td>';
            tr += '<td>' + (!item.itemPrice ? "" : item.itemPrice) + '</td>';
            tr += '<td>' + (!item.itemCount ? "" : item.itemCount) + '</td>';
            tr += '<td>' + (!item.unit ? "" : item.unit) + '</td>';
            var orderTotalAmount = APP_BCGOGO.StringFilter.priceFilter(Number(item.itemPrice) * Number(item.itemCount));
            tr += '<td style="border-right:none;">' + (!orderTotalAmount ? "" : orderTotalAmount) + '</td>';
            tr += '<input type="hidden" class="returnProductIds" id="returnProductId[' + i + ']" value="' + item.productIdStr + '" name="' + item.productIdStr + '">';
            tr += '</tr>';
            tr = $(tr);
            $("#returnHistory").append(tr);
        }
    }

    //会员购卡续卡
    function getMemberBuyCardItems(order) {
        if (order == null)return;
        $("#memberHistory tr:not(:first)").remove();
        $("#memberName > span").html(order.customerOrSupplierName);
        if ("DISABLED" == order.customerStatus) {
            $("#memberName > span").css("color", "#999999")
        }
        else {
            $("#memberName > span").css("color", "#000");
        }

        if (order.memberNo) {
            $("#memberNo > span").html(order.memberNo);
            $("#memberStatusStr > span").html(order.memberType); //购卡/续卡类型
        }
        $("#memberOrderNum > span").html(order.receiptNo);//单据号
        $("#memberSupplierId").val(order.customerOrSupplierId);

        $("#memberOrderWorth > span").html(Number(order.worth).toFixed(2));  //储值新增金额
        $("#memberBalance > span").html(Number(order.memberBalance).toFixed(2)); //储值余额
        $("#memberOrderOriginal > span").html(Number(order.memberBalance - order.worth).toFixed(2));   //储值原有金额

        $("#memberTotal > span").html(order.amount);       //总计
        $("#memberSettledAmount > span").html(order.settled);    //实收
        $("#memberDebt").html(order.debt);   //欠款
        if (order.memo) {
            $("#memberMemo > span").html(order.memo);
        }
        var items = order.itemIndexDTOs;
        var tr;
        if (!items) {
            tr = '<tr class="table-row-original" id="memberHistory0"><td colspan="6" style="border-left:none;border-right:none;text-align: center;" class="txt_right">对不起，该单据无服务项目！</td></tr>';
            $("#memberHistory").append($(tr));
            return;
        }
        for (var i = 0, max = items.length; i < max; i++) {
            //卡的每项服务
            var item = items[i];
            tr = '<tr class="table-row-original" id="memberHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (!item.itemName ? "" : item.itemName) + '</td>';
            tr += '<td>' + (!item.oldTimes ? 0 : (Number(item.oldTimes) == -1 ? "不限次" : item.oldTimes)) + '</td>';
            //新增无限次 逻辑
            tr += '<td>' + (!item.increasedTimes ? "" : ((Number(item.increasedTimes) ) == -1 && (item.increasedTimesLimitType == "UNLIMITED") ? "不限次" : item.increasedTimes)) + '</td>';
            tr += '<td>' + (!item.balanceTimes ? "" : (Number(item.balanceTimes) == -1 ? "不限次" : item.balanceTimes)) + '</td>';
            //限制车辆
            tr += '<td>' + (!item.vehicles ? "无" : (item.vehicles)) + '</td>';
            tr += '<td style="border-right:none;">' + (item.deadlineStr ? item.deadlineStr : "--") + '</td>';
            tr += '</tr>';
            tr = $(tr);
            $("#memberHistory").append(tr);
        }
    }

    //会员退卡
    function getMemberReturnCardItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#returnMemberHistory tr:not(:first)").remove();
        var items = order.itemIndexDTOs;
        if ("DISABLED" == order.customerStatus) {
            $("#returnMemberName > span").css("color", "#999999")
        }
        else {
            $("#returnMemberName > span").css("color", "#000");
        }
        $("#returnMemberName > span").html(order.customerOrSupplierName);
        $("#returnMemberSupplierId").val(order.customerOrSupplierId);
        $("#returnMemberNo > span").html(order.memberNo);
        $("#returnMemberType > span").html(order.memberType);
        $("#returnMemberTotal > span").html(order.amount);       //总计
        $("#returnMemberSettledAmount > span").html(Math.abs(order.settled));    //实付
        $("#returnMemberOrderNum > span").html("");
        $("#returnMemberLastBuyTotal > span").html(Number(order.memberLastBuyTotal).toFixed(2));
        $("#returnMemberLastBuyDate > span").html(order.memberLastBuyDateStr);
        $("#returnMemberLastRecharge > span").html(GLOBAL.Lang.isEmpty(order.memberLastRecharge) ? 0 : Number(order.memberLastRecharge).toFixed(1));
        $("#returnMemberBalance > span").html(GLOBAL.Lang.isEmpty(order.memberBalance) ? 0 : Number(order.memberBalance).toFixed(2));
        for (var i = 0, max = items.length; i < max; i++) {
            //卡的每项服务
            var item = items[i];
            var tr = '<tr class="table-row-original" id="returnMemberHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (!item.itemName ? "" : item.itemName) + '</td>';
            tr += '<td>' + (!item.oldTimes ? "" : (Number(item.oldTimes) == -1 ? "不限次" : item.oldTimes)) + '</td>';
            tr += '<td>' + (!item.balanceTimes ? "" : (Number(item.balanceTimes) == -1 ? "不限次" : item.balanceTimes)) + '</td>';
            tr += '</tr>';
            tr = $(tr);
            $("#returnMemberHistory").append(tr);
        }
    }

    //洗车美容单
    function getWashBeautyItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#washHistory tr:not(:first)").remove();
        var items = order.itemIndexDTOs;
        if ("DISABLED" == order.customerStatus) {
            $("#washCustomerName > span").css("color", "#999999");
        }
        else {
            $("#washCustomerName > span").css("color", "#000");
        }
        $("#washCustomerName > span").html(order.customerOrSupplierName);
        if (order.memberNo) {
            $("#washMemberCard > span").html(order.memberNo);
        } else {
            $("#washMemberCard > span").html("非会员");
        }

        $("#washLicence > span").html(order.vehicle);
        $("#washDate > span").html(order.vestDateStr); //归属时间
        $("#washTotal > span").html(order.amount); //总计
        if (order.memo) {
            $("#washMemo > span").html(order.memo);
        }
        $("#washSettledAmount > span").html(order.settled);    //实收
        $("#washDebt").html(order.debt);                                 //欠款
        $("#washOrderId").val(order.orderIdStr);
        for (var i = 0, max = items.length; i < max; i++) {
            var item = items[i];
            var tr = '<tr class="table-row-original" id="washHistory[' + i + ']">';
            tr += '<td style="border-left:none;">' + (i + 1) + '</td>';
            tr += '<td>' + (!item.services ? "" : item.services) + '</td>';
            tr += '<td>' + (!item.serviceWorker ? "未填写" : (item.serviceWorker)) + '</td>';  //todo
            tr += '<td>' + (!item.itemPrice ? ((!item.consumeType ? "" : "计次消费")) : ("金额:" + item.itemPrice) + "元") + '</td>';
            tr += '</tr>';
            tr = $(tr);
            $("#washHistory").append(tr);
        }
    }

    //车辆施工单
    function getRepairItems(order) {
        if (order == null || order.itemIndexDTOs == null)return;
        $("#repairServiceHistory tr:not(:first)").remove();
        $("#repairMaterialHistory tr:not(:first)").remove();
        if (order.memberNo) {
            $("#repairMemberNo > span").html(order.memberNo);
        } else {
            $("#repairMemberNo > span").html("非会员");
        }

        if ("DISABLED" == order.customerStatus) {
            $("#repairCustomerName > span").css("color", "#999999");
        }
        else {
            $("#repairCustomerName > span").css("color", "#000000");
        }

        $("#repairCustomerName > span").html(order.customerOrSupplierName);
        $("#repairLicenceNo > span").html(order.vehicle);
        $("#repairOrderStatus > span").html(order.orderStatusValue);
        $("#repairStatus > span").html(order.orderStatusValue);
        $("#startDateStr > span").html(order.inTimeStr); //进厂时间
        $("#endDateStr").html(order.outTimeStr);          //出厂时间
        //施工人 销售人
        $("#repairServiceWorker > span").html(order.serviceWorkers);
        $("#repairProductSalers > span").html(order.salesMans);
        // 结算
        $("#repairSettledAmount > span").html(order.settled);    //实收
        $("#repairDebt").html(order.debt);                                 //欠款
        $("#repairTotal > span").html(order.amount);                     //总计
        if (order.memo) {
            $("#repairMemo >span").html(order.memo);
        }
        $("#repairOrderId").val(order.orderIdStr);
        var items = order.itemIndexDTOs;
        for (var i = 0, serviceIndex = 1, itemIndex = 1, max = items.length; i < max; i++) {
            var item = items[i];
            var tr = "";
          var temp=item.itemType;
            if (null!=item.itemType&&item.itemType.toLowerCase() == "service") {
                tr = '<tr class="table-row-original" id="repairHistory[' + i + ']">';
                tr += '<td style="border-left:none;">' + (serviceIndex++) + '</td>';
                tr += '<td>' + (!item.services ? "" : item.services) + '</td>';
                tr += '<td>' + (!item.itemPrice ? ((!item.consumeType ? "" : "计次消费")) : ("金额:" + item.itemPrice) + "元") + '</td>';
                tr += '<td style="border-right:none;">' + (!item.itemMemo ? "无" : (item.itemMemo)) + '</td>';
                tr += '</tr>';
                tr = $(tr);
                $("#repairServiceHistory").append(tr);
            } else {
                itemIndex++;
                tr = '<tr class="table-row-original" id="repairHistory[' + i + ']">';
                tr += '<td style="border-left:none;">' + (!item.commodityCode ? "" : item.commodityCode) + '</td>';
                tr += '<td>' + (!item.itemName ? "" : item.itemName) + '</td>';
                tr += '<td>' + (!item.itemBrand ? "" : item.itemBrand) + '</td>';
                tr += '<td>' + (!item.itemSpec ? "" : item.itemSpec) + '</td>';
                tr += '<td>' + (!item.itemModel ? "" : item.itemModel) + '</td>';
                tr += '<td>' + (!item.itemPrice ? "" : item.itemPrice) + '</td>';
                tr += '<td>' + (!item.itemCount ? "" : item.itemCount) + '</td>';
                tr += '<td>' + (!item.unit ? "" : item.unit) + '</td>';
                var orderTotalAmount = APP_BCGOGO.StringFilter.priceFilter(Number(item.itemPrice) * Number(item.itemCount));
                tr += '<td style="border-right:none;">' + (!orderTotalAmount ? "" : orderTotalAmount) + '</td>';
                tr += '</tr>';
                tr = $(tr);
                $("#repairMaterialHistory").append(tr);
            }
        }
        if (serviceIndex == 1) {
            tr = '<tr class="table-row-original" id="repairHistory"><td colspan="4" style="border-left:none;border-right:none;text-align: center;" class="txt_right">对不起，该单据无服务项目！</td></tr>';
            $("#repairServiceHistory").append($(tr));
        }
        if (itemIndex == 1) {
            tr = '<tr class="table-row-original" id="repairHistory1"><td colspan="9" style="border-left:none;border-right:none;text-align: center;" class="txt_right">对不起，该单据无材料！</td></tr>';
            $("#repairMaterialHistory").append($(tr));
        }
    }

    //超过固定范围 hide items
    $("#inventory,#purchase,#wash_beauty,#member_buy_card,#sale,#return,#repair,#itemsArea").bind("mouseleave", function (e) {
        $(".orderItems").hide();
    });

    $(".orderHandle>.i_operate").click(function (e) {
        var $handle = $(e.target);
        var $orderHandle = $handle.parent().children(".orderHandleId");
        var id = $orderHandle.val();
        var url = $handle.attr("url");
        if (id && url) {
            if ($handle.val() == "打印") {
                var handleType = $handle.attr("handletype");
                if (handleType && $handle.attr("handletype").indexOf("wash") != -1) {
                    window.showModalDialog(url + id + "&now=" + new Date(), '', "dialogWidth=250px;dialogHeight=768px");
                } else if (handleType && $handle.attr("handletype").indexOf("repair") != -1) {
                    var debt = $orderHandle.attr("debt");
                    var settledAmount = $orderHandle.attr("settledAmount");
                    window.showModalDialog(url + id + "&settledAmount=" + settledAmount + "&debt=" + debt + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
                } else {
                    window.showModalDialog(url + id + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
                }
            }
        }
    });

    function openWindow(url) {
        window.open(encodeURI(url));
    }

    $("#div_close,#closeInquiry").click(function () {
        if (window.parent) {
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_inquiry_center").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_inquiry_center").src = "";
            try {
                $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
            } catch (e) {
            }
        } else {
            window.close();
        }
    });

    //根据页面控制 查询中心
    var pageType = $("#inquiryCenterPageType").val();
    $(".goods_chk > label").removeClass("chk_on").addClass("chk_off");
    if (pageType == "all") {
    } else {
        if (pageType == "inventory" || pageType == "purchase" || pageType == "return") {
            $("#" + pageType + "Label").removeClass("chk_off").addClass("chk_on");
            $("#orderTypeAllLabel").addClass("chk_off");
            $(".vehicle,.member,#shigong,#settlementMethod").hide();
            $("#cash,#bankCard,#cheque,#deposit,#notPaid,#memberBalancePay").attr("disabled", "disabled");
            initTimeOfYear();
        } else if (pageType == "wash_beauty") {
            $("#washLabel").removeClass("chk_off").addClass("chk_on");
            $("#orderTypeAllLabel").addClass("chk_off");
            $("#productProperty,#saler").hide();
            initTimeOfYear();
        } else if (pageType == "repair") {
            $("#repairLabel").removeClass("chk_off").addClass("chk_on");
            $("#orderTypeAllLabel").addClass("chk_off");
            initTimeOfYear();
        } else if (pageType == "sale") {
            $("#saleLabel").removeClass("chk_off").addClass("chk_on");
            $("#orderTypeAllLabel").addClass("chk_off");
            $("#invoicingItem,#invoicingDepartment,#serviceWorker,#memberWouldCharge,#vehicleNumber,#vehicleModel,#vehicleBrand").hide().attr("disabled", "disabled");
            initTimeOfYear();
        }

        var inputs = $("#inquiryCenterSearchForm> div>input,#operator");
        var initialValue,value;
        var searchFlag = false;
        for (var i = 0; i < inputs.length; i++) {
            initialValue = $(inputs[i]).attr("initialValue");
            value = inputs[i].value;
            if (!initialValue) {
                continue;
            }
            if (initialValue != value) {
                searchFlag = true;
                break;
            }
            $(inputs[i]).val(initialValue).css({"color":"#ADADAD"});
        }
        if (searchFlag) {
            searchOrderImmediately();
        }
    }
});