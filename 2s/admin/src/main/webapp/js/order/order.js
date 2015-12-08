function initOrderListTable(jsonStr) {

    $("#tableList tr:not(:first)").remove();
    if (jsonStr.length > 0)
    {
        for (var i = 0 ; i < jsonStr.length-1; i++)
        {
            var orderNumber = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var orderStatus = jsonStr[i].adminStatus == null ? " " : jsonStr[i].adminStatus;
            var customerName = jsonStr[i].userName == null ? " " : jsonStr[i].userName;
            var telNumber = jsonStr[i].mobile == null ? " " : jsonStr[i].mobile;
            var vehicleNumber = jsonStr[i].vehicleNo == null ? " " : jsonStr[i].vehicleNo;
            var goodsName = jsonStr[i].product == null ? " " : jsonStr[i].product;
            var orderMoney = jsonStr[i].sumMoney == null ? " " : jsonStr[i].sumMoney;//订单总金额
            var couponMoney = jsonStr[i].coupon == null ? " " : jsonStr[i].coupon;//代金券抵用金额
            var actuallyMoney = jsonStr[i].actuallyPay == null ? " " : jsonStr[i].actuallyPay;//实际所付金额

            var tr = '<tr>';
            tr += '<td class="orderNo">' + orderNumber + '</td>';                        //订单号
            tr += '<td title= \' '+orderStatus+' \'>' + orderStatus + '</td>';          //订单状态
            tr += '<td title= \' '+customerName+' \'>' + customerName +'</td>';          //客户名
            tr += '<td title= \' '+telNumber+' \'>' + telNumber +'</td>';                //手机号
            tr += '<td title= \' '+vehicleNumber+' \'>' + vehicleNumber +'</td>';        //车牌号
            tr += '<td title= \' '+goodsName+' \'>' + goodsName + '</td>>';              //商品名
            tr += '<td title= \' 代金券抵用'+couponMoney+'元,实付'+actuallyMoney+'元 \'>'
            + orderMoney + '元' +'<br/>'
            +'(代金券:' + couponMoney + '元)' +'<br/>'
            +'(支付宝:' + actuallyMoney + '元)' + '</td>>';                               //订单金额
            if(orderStatus == "已确认"){
                tr += '<td> <span class="confirm"></span> </td>';                        //操作
            }else{
                tr += '<td> <span class="confirm">确认</span> </td>';                        //操作
            }
            tr += '</tr >';
            $("#tableList").append(jQuery(tr));
        }

    }
    $(".confirm").each(function(index,element){
        $(element).click(function(){
            jQuery.ajax({
                type : "POST",
                url : "order.do?method=confirmOrder",
                data : { orderId : $("#tableList tr").eq(index+1).find(".orderNo").text() },
                dataType : "json",
                success : function(jsonStr)
                {
                    if(jsonStr.isSuccess=="true") {
                        $("#tableList tr").eq(index+1).find(".orderNo").next().text("已确认").addClass("statusChange");
                        $(element).text("");
                    }
                }

            });
        })


    });


}




function searchOrderList(){

    var customer_name = $("#customerName").val();
    var tel_number = $("#telNumber").val();
    var vehicle_number = $("#vehicleNumber").val();
    var order_number = $("#orderNumber").val();
    var goods_name = $("#goodsName").val();
    var order_status = $("#orderStatus option:selected").text();
    if( order_status == "客户提交" ){
        order_status = "ADMIN_ORDER_SUBMIT";
    }
    if( order_status == "已确认" ){
        order_status = "ADMIN_ORDER_CONFIRM";
    }
    var data = {  customerName:customer_name,telNumber:tel_number,vehicleNumber:vehicle_number,
        orderNumber:order_number, goodsName:goods_name,orderStatus:order_status,startPageNo:1};
    jQuery.ajax({
        type : "POST",
        url : "order.do?method=toOrderList",
        data : data,
        dataType : "json",
        success : function(jsonStr)
        {
            initOrderListTable(jsonStr);
            initfenye(  jsonStr, "dynamical1", "order.do?method=toOrderList", '', "initOrderListTable", '',
                '',data, '');
        }
    });
}
