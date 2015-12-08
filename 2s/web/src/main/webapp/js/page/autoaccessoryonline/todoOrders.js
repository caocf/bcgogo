//待办销售单
var TODO_SALE_ORDERS = "TODO_SALE_ORDERS";
//待办销售退货单
var TODO_SALE_RETURN_ORDERS = "TODO_SALE_RETURN_ORDERS";
//待办采购单
var TODO_PURCHASE_ORDERS = "TODO_PURCHASE_ORDERS";
//待办入库退货单
var TODO_PURCHASE_RETURN_ORDERS = "TODO_PURCHASE_RETURN_ORDERS";

$().ready(function() {
    $(".reciptNo").live("mouseover",function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    });
    $(".reciptNo").live("mouseout",function(){
        $(this).css({"color":"#006ECA","textDecoration":"none"});
    });
    $(".J-priceInfo").live("mouseenter", function(event) {
        event.stopImmediatePropagation();

        var _currentTarget = $(event.target).find(".alert");
        _currentTarget.show();

        _currentTarget.mouseleave(function(event) {
            if($(event.relatedTarget)[0] != $(event.target).parents(".J-priceInfo")[0]) {
                _currentTarget.hide();
            }
        });

    }).live("mouseleave", function(event) {
            event.stopImmediatePropagation();
            var _currentTarget = $(event.target).find(".alert");
            if(event.relatedTarget != _currentTarget[0]) {
                _currentTarget.hide();
            }
        });
    //点击tab按钮跳转对应的页面
    $("li").live("click",function(){
        if($(this).attr("class")!="hover_yinye"){
            //待办销售单
            if(this.id=="todoSaleOrders"){
                window.location.href = "orderCenter.do?method=getTodoOrders&type="+TODO_SALE_ORDERS;
            }
            //待办销售退货单
            if(this.id=="todoSaleReturnOrders"){
                window.location.href = "orderCenter.do?method=getTodoOrders&type="+TODO_SALE_RETURN_ORDERS;
            }
            //待办采购单
            if(this.id=="todoPurchaseOrders"){
                window.location.href = "orderCenter.do?method=getTodoOrders&type="+TODO_PURCHASE_ORDERS;
            }
            //待办入库退货单
            if(this.id=="todoPurchaseReturnOrders"){
                window.location.href = "orderCenter.do?method=getTodoOrders&type="+TODO_PURCHASE_RETURN_ORDERS;
            }
        }
    });

    $("#startTimeStr,#endTimeStr").datepicker({
        "numberOfMonths" : 1,
        "changeYear":true,
        "changeMonth":true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix":"",
        "showButtonPanel":true
    });

    //时间逻辑检验
    //开始时间不能早于今天
    $("#startTimeStr").bind("change",function(){
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#startTimeStr").val();
        var endDateStr = $("#endTimeStr").val();
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
                    $("#startTimeStr").val("");
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
                        $("#startTimeStr").val("");
                        return;
                    });
                }
            }
        }
    });
    //结束时间不能晚于开始时间
    $("#endTimeStr").bind("change",function(){
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#startTimeStr").val();
        var endDateStr = $("#endTimeStr").val();
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
                    $("#endTimeStr").val("");
                    return;
                });
            }
        }
    });

    //条件查询待办单据
    $("#todoOrdersSearchBtn").bind("click",function(){
        var type = $("#type").val();
        var startTimeStr = $("#startTimeStr").val();
        var endTimeStr = $("#endTimeStr").val();
        var customerName = $.trim($("#customerName").val());
        var supplierName = $.trim($("#supplierName").val());
        var receiptNo = $.trim($("#receiptNo").val());
        var orderStatus = $("#select_orderStatus").val();
        window.location.href = "orderCenter.do?method=getTodoOrders&type="+type+
            "&startTimeStr="+startTimeStr+"&endTimeStr="+endTimeStr+
            "&customerName="+customerName+"&supplierName="+supplierName+
            "&receiptNo="+receiptNo+"&orderStatus="+orderStatus;
    });

	$("#definedDate").datepicker({
		"numberOfMonths" : 1,
		"changeYear":true,
		"changeMonth":true,
		"dateFormat": "yy-mm-dd",
		"minDate":0,
		"yearRange": "c:c+1",
		"yearSuffix":"",
		"showButtonPanel":true
	}).bind("click", function() {
        $("#salesAcceptForm input[name='dispatchDateRadio']").eq(3).attr("checked", "checked");
    });

//    //拒绝退货
//    $("#refuseBtn").bind("click", function () {
//        $("#refuseReasonDialog").dialog({ width: 458 });
//    });
//    $("#refuseReturnConfirmBtn").bind("click", function () {
//        $("#refuseReason").val($("#refuseReasonTextarea").val());
//        $("#salesReturnForm").attr("action", "salesReturn.do?method=refuseSalesReturnOrder&refuseMsg="+$("#refuseReasonTextarea").val());
//        $("#salesReturnForm").attr("target", "_blank");
//        $("#salesReturnForm").submit();
//    });
//    $("#refuseReturnCancelBtn").bind("click", function () {
//        $("#refuseReasonTextarea").val("");
//        $("#refuseReason").val("");
//        $("#refuseReasonDialog").dialog("close");
//    });

//  else if($("#isWarehouse").val()=="true"){
//
//    }


//  //气泡是否闪动
//  $(".num").each(function(){
//     if($(this).attr("isFlash")=="true"){
//         $(this).css("background","url('/web/images/paopao.gif') no-repeat scroll 0 5px transparent");
//     }
//  });

    // $(".tabSlip tr").not(".titleBg").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    // $(".tabSlip tr:nth-child(odd)").not(".titleBg").css("background","#eaeaea");

    // $(".tabSlip tr").not(".titleBg").hover(
    // 	function () {
    // 		$(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px","color":"#FF5E04"});
    // 		$(this).css("cursor","pointer");
    // 	},
    // 	function () {
    // 	   $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px","color":"#272727"});
    // 	   $(".tabSlip tr:nth-child(odd)").not(".titleBg" ).find("td").css("background","#eaeaea");
    // 	}
    // );

    tableUtil.tableStyle('table[id^="tab_slip"]','.titleBg');

    $("#tab_slip tr:gt(1)").css("display","none");
    $("#tab_Remark tr:gt(1)").css("display","none");

    $(".up").toggle(
        function(){
            $(".down").click();

            var index = this.id.substr(10,1);
            $(".data_tr_"+index).css("display","");
            $(this).html("收拢");
            $(this).removeClass().addClass("down");

            $("tr[class^='data_tr_'] .limit-span").each(function(i){
                $("tr[class^='data_tr_'] .limit-span").eq(i).width($("tr[class^='data_tr_'] .limit-span").eq(i).closest('td').width()-10);
            });
        },
        function(){
            var index = this.id.substr(10,1);
            $(".data_tr_"+index).css("display","none");
            $(this).html("更多");
            $(this).removeClass().addClass("up");
        }
    );

    $(".up").eq(0).click();

    $(".J-orderDiv").each(function(){
        var $orderTable = $(this);
        var supplierProductIdArr=new Array();
        $orderTable.find(".item").each(function(){
            supplierProductIdArr.push($(this).find(".j_supplierProductId").text());
        });
        if($.isFunction(window.initOrderPromotionsDetailForShowPage)){
            initOrderPromotionsDetailForShowPage(supplierProductIdArr, $orderTable);
        }
    });


    $(".J_showContactsTip").live("mouseover", function(){
        $(".prompt").css("display", "none");
        var _dom = $(this);
        var idStr = _dom.attr("shop_id");
        if (idStr) {
            var offset = _dom.offset();
            var offsetMainDiv = $(".i_main").eq(0).offset();
            $("#" + idStr + "_prompt").css({
                position:'absolute',
                left:offset.left - offsetMainDiv.left - 15 + 'px',
                top:offset.top - offsetMainDiv.top + 14 + 'px'
            });
            $("#" + idStr + "_prompt").css("display", "block");
        }
    }).live("mouseout", function(event){
        if($(event.relatedTarget).attr("class").search(/prompt/)!=-1){
            return;
        }
        var _dom = $(this);
        var idStr = _dom.attr("shop_id");
        if (idStr) {
            $("#" + idStr + "_prompt").hide();
        }
    });

    $(".prompt").live("mouseout", function(event){
        //移动到prompt class下的子元素，不隐藏
        if(!$(event.relatedTarget).parents(".prompt")[0]){
            $(this).hide();
        }
    });

});

function generateContactTip(shopDTO){
    //根据传进来的参数是shopDTO还是customerDTO,来设置店面的ID
    if(shopDTO.customerShopId) {
        var html = '<div style="display: none;" id="' + shopDTO.customerShopId + '_prompt" class="prompt">';
        html += '<div class="promptTop"></div>';
        html += '<div class="promptBody">' +
            '<a class="icon_close" id="' + shopDTO.customerShopId + '_a" onclick="hideContact(this)"></a>';
    } else {
        var html = '<div style="display: none;" id="' + shopDTO.idStr + '_prompt" class="prompt">';
        html += '<div class="promptTop"></div>';
        html += '<div class="promptBody">' +
            '<a class="icon_close" id="' + shopDTO.idStr + '_a" onclick="hideContact(this)"></a>';
    }


    if(G.isEmpty(shopDTO.contacts)){
        html += '暂无联系方式';
    }else{
        for(var i in shopDTO.contacts){
            var contact = shopDTO['contacts'][i];
            if(contact != null){
                html += '<div class="lineList">' + (G.isEmpty(contact.name) ? "暂无联系人":contact.name) + '&nbsp;' + (G.isEmpty(contact.mobile) ? "暂无手机":contact.mobile)
                    + '&nbsp;<a href="javascript:smsHistory(\'' + contact.name + '\',\'' + contact.mobile + '\')" class="phone"></a></div>';
            }
        }
    }
    html += '</div><div class="promptBottom"></div>  ' +
    '</div>';
    return html;
}

function hideContact(dom){
    $(dom).parents(".prompt").hide();
}

function smsHistory(name,mobile) {
    window.location = encodeURI("sms.do?method=smswrite&contactName="+name+"&mobile=" + mobile);
}

function getShopContactQQ(shopDTO){
    var qqArray = [];
    if(shopDTO && shopDTO["contacts"]){
        var contacts =   shopDTO["contacts"];
        for(var i= 0,len = contacts.length;i<len;i++){
           if(contacts[i] && !G.Lang.isEmpty(contacts[i]["qq"])){
               qqArray.push(contacts[i]["qq"]);
           }
        }
    }
    return qqArray;
}

function getSupplierContactQQ(supplierDTO){
    var qqArray = [];
    if(supplierDTO && supplierDTO["contacts"]){
        var contacts =   supplierDTO["contacts"];
        for(var i= 0,len = contacts.length;i<len;i++){
           if(contacts[i] && !G.Lang.isEmpty(contacts[i]["qq"])){
               qqArray.push(contacts[i]["qq"]);
           }
        }
    }
    return qqArray;
}