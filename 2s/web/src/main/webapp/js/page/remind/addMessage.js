$(document).ready(function () {
    var editor = $('#content').xheditor({
        tools:"Fontface,Bold,Italic,FontColor",
        disableContextmenu:true,
        forcePtag:false,
        cleanPaste:3
    });

    $("#checkAllCustomerCheckBox").click(function () {
        $('input[name="subCustomerCheckBox"]').attr("checked", this.checked);
    });

    $("input[name='subCustomerCheckBox']").live("click", function () {
        $("#checkAllCustomerCheckBox").attr("checked",$("input[name='subCustomerCheckBox']").length == $("input[name='subCustomerCheckBox']:checked").length ? true : false);
    });

    $("#checkAllProductCheckBox").click(function () {
        $('input[name="subProductCheckBox"]').attr("checked", this.checked);
    });

    $("input[name='subProductCheckBox']").live("click", function () {
        $("#checkAllProductCheckBox").attr("checked",$("input[name='subProductCheckBox']").length == $("input[name='subProductCheckBox']:checked").length ? true : false);
    });

    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });

    $("#selectCustomer").bind("click", function () {
        $("#selectCustomerDialog").dialog({
            width: 830,
            zIndex: 20,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#div_brand_head").hide();
                $("#customer_supplierInfoText").val("");
                $(".j_clear_span").text("0");
                $("#customerTable tr").remove();
                $("#checkAllCustomerCheckBox").attr("checked",false);
                return true;
            },
            open: function() {
                $(this).find('input').first().blur();
                $("#searchCustomerBtn").click();
            }
        });
    });

    $("#searchCustomerBtn").bind("click", function () {
        var paramJson={startPageNo:1,maxRows:10};
        if($("#customerInfoText").val() == $("#customerInfoText").attr("initialValue")){
            paramJson["searchWord"]="";
        }else{
            paramJson["searchWord"]=$("#customerInfoText").val();
        }

        APP_BCGOGO.Net.syncPost({
            url:"message.do?method=selectCustomer",
            data:paramJson,
            dataType:"json",
            success:function (data) {
                drawCustomerTable(data);
                initPages(data, "dynamical1", "message.do?method=selectCustomer", '', "drawCustomerTable", '', '', paramJson, '');
            },
            error:function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    });

    $("#selectCustomerConfirmBtn").bind("click", function () {
        var messageReceivers = $("#messageReceivers").val();
        var nodeStr = "";
        $("input[name='subCustomerCheckBox']:checked").each(function(i){
            if(messageReceivers.indexOf($(this).val()+",")<0){
                messageReceivers+=$(this).val()+",";
                nodeStr += "<li>";
                nodeStr += "<div class='divNumber'>";
                nodeStr += "<span class='data-info'>";
                nodeStr += $(this).attr("data-name");
                var mobile = $(this).attr("data-mobile");
                if (GLOBAL.Lang.isEmpty(mobile)) {
                    mobile = "无手机号";
                }
                nodeStr += ":" + mobile;
                nodeStr += "</span>";
                nodeStr += "<a data-customer-id='"+$(this).val()+"' class='close j_customer_delete'></a>";
                nodeStr += "</div>";
                nodeStr += "</li>";
            }
        });
        $("#messageReceivers").val(messageReceivers);
        $("#detailCustomers").append(nodeStr);
        $("#selectCustomerDialog").dialog("close");
    });
    $(".j_customer_delete").live("click", function () {
        $(this).parent().parent().remove();
        var messageReceivers = $("#messageReceivers").val();
        messageReceivers = messageReceivers.replace(($(this).attr("data-customer-id") + ","), "");
        $("#messageReceivers").val(messageReceivers);
    });

    $("#selectCustomerCloseBtn").bind("click", function () {
        $("#selectCustomerDialog").dialog("close");
    });


    $("#selectProduct").bind("click", function () {
        //处理productIds
        var productIds = "";
        if(!GLOBAL.Lang.isEmpty(editor.getSource())){
            var spanStrs = editor.getSource().match(/\<span[^\>]+\s\S*\>/g);
            if(spanStrs!=null){
                $.each(spanStrs,function(n,span) {
                    var productId = $(span).attr("data-productlocalinfo-id");
                    if(!GLOBAL.Lang.isEmpty(productId)){
                        productIds+=productId+",";
                    }
                });
            }
        }
        $("#productIds").val(productIds);

        $("#selectProductDialog").dialog({
            width: 830,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#div_brand_head").hide();
                $(".j_clear_input").val("");
                $(".j_clear_input").blur();
                $("#productTable tr").remove();
                $("#checkAllProductCheckBox").attr("checked",false);
                return true;
            },
            open: function() {
                $(this).find('input').first().blur();
                $("#searchProductBtn").click();
            }
        });
    });

    $("#searchProductBtn").bind("click", function () {
        var paramJson = {
            commodityCode:$("#product_commodity_code").val() == $("#product_commodity_code").attr("initialValue") ? "" : $("#product_commodity_code").val(),
            productName:$("#product_name2_id").val() == $("#product_name2_id").attr("initialValue") ? "" : $("#product_name2_id").val(),
            productBrand:$("#product_brand_id").val() == $("#product_brand_id").attr("initialValue") ? "" : $("#product_brand_id").val(),
            productSpec:$("#product_spec_id").val() == $("#product_spec_id").attr("initialValue") ? "" : $("#product_spec_id").val(),
            productModel:$("#product_model_id").val() == $("#product_model_id").attr("initialValue") ? "" : $("#product_model_id").val(),
            productVehicleBrand:$("#pv_brand_id").val() == $("#pv_brand_id").attr("initialValue") ? "" : $("#pv_brand_id").val(),
            productVehicleModel:$("#pv_model_id").val() == $("#pv_model_id").attr("initialValue") ? "" : $("#pv_model_id").val(),
            startPageNo:1,
            maxRows:10,
            includeBasic:false
        };
        APP_BCGOGO.Net.syncPost({
            url:"message.do?method=selectProduct",
            data:paramJson,
            dataType:"json",
            success:function (data) {
                drawProductTable(data);
                initPages(data, "dynamical2", "message.do?method=selectProduct", '', "drawProductTable", '', '', paramJson, '');
            },
            error:function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    });

    $("#selectProductConfirmBtn").bind("click", function () {
        var productIds = $("#productIds").val();
        var shopId = $("#shopId").val();
        var nodeStr = "";
        $("input[name='subProductCheckBox']:checked").each(function(i){
            if(productIds.indexOf($(this).val()+",")<0){
                productIds+=$(this).val()+",";
                nodeStr+="&nbsp;<span class='blue_color J_showProductDetailBtn' data-productshop-id='"+shopId+"' data-productlocalinfo-id='"+$(this).val()+"'>";

                var brand = $(this).attr("data-brand");
                if (!GLOBAL.Lang.isEmpty(brand)) {
                    nodeStr += brand;
                }
                nodeStr += $(this).attr("data-name");
                nodeStr += "</span>&nbsp;";
            }
        });
        $("#productIds").val(productIds);
        editor.pasteHTML(nodeStr);
        $("#selectProductDialog").dialog("close");
    });

    $("#selectProductCloseBtn").bind("click", function () {
        $("#selectProductDialog").dialog("close");
    });

    $("#confirmBtn").live("click", function () {
        if (GLOBAL.Lang.isEmpty($("#content").val())) {
            nsDialog.jAlert("请填写消息内容！");
            return;
        }else{
            if (removeHTMLTag(editor.getSource()).length > 200) {
                nsDialog.jAlert("消息内容不能超过200个字!");
                return;
            }
        }
        if (GLOBAL.Lang.isEmpty($("#messageReceivers").val())) {
            nsDialog.jAlert("请添加客户！");
            return;
        }
        var productIds = "";
        if(!GLOBAL.Lang.isEmpty(editor.getSource())){
            var spanStrs = editor.getSource().match(/\<span[^\>]+\s\S*\>/g);
            if(spanStrs!=null){
                $.each(spanStrs,function(n,span) {
                    var productId = $(span).attr("data-productlocalinfo-id");
                    if(!GLOBAL.Lang.isEmpty(productId)){
                        productIds+=productId+",";
                    }
                });
            }
        }
        $("#productIds").val(productIds);
        $("#messageForm").submit();
    });


});

function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    return str;
}
/**
 * 封装table
 */
function drawCustomerTable(data) {
    $(".j_clear_span").text("0");
    $("#customerTable tr").remove();
    $("#checkAllCustomerCheckBox").attr("checked",false);
    if (data == null || data[0] == null || data[0].customerSuppliers == null || data[0].customerSuppliers == 0) {
        return;
    }

    $.each(data[0].customerSuppliers, function(index, customer) {
        var customerId = (!customer.idStr ? "" : customer.idStr);
        var customerName = (!customer.name ? "--" : customer.name);
        var contact = (!customer.contact ? "--" : customer.contact);
        var mobile = (!customer.mobile ? "--" : customer.mobile);

        var tr = '<tr class="table-row-original">';
        tr += '<td><input type="checkbox" name="subCustomerCheckBox" data-name="'+customer.name+'" data-mobile="'+customer.mobile+'" value="'+customerId+'"/>' + (index + 1) + '</td>';
        tr += '<td title="' + customerName + '">' + customerName + '</td>';
        tr += '<td title="' + contact + '">' + contact + '</td>';
        tr += '<td title="' + mobile + '">' + mobile + '</td>';
        tr += '</tr>';
        $("#customerTable").append($(tr));
    });
    tableUtil.tableStyle('#customerTable','.tab_title');
    $("#hasMobileCustomerNum").text(data[0].hasMobileNumFound==null?0:data[0].hasMobileNumFound);
    $("#allCustomerNum").text(data[0].numFound==null?0:data[0].numFound);
}

/**
 * 封装table
 */
function drawProductTable(data) {
    $("#productTable tr").remove();
    $("#checkAllProductCheckBox").attr("checked",false);
    if (data == null || data[0] == null || data[0].products == null || data[0].products == 0) {
        return;
    }

    $.each(data[0].products, function(index, product) {
        var productLocalInfoIdStr = (!product.productLocalInfoIdStr ? "" : product.productLocalInfoIdStr);
        var commodityCode = (!product.commodityCode ? "--" : product.commodityCode);
        var productName = (!product.name ? "--" : product.name);
        var productBrand = (!product.brand ? "--" : product.brand);
        var productModel = (!product.model ? "--" : product.model);
        var productSpec = (!product.spec ? "--" : product.spec);
        var productVehicleBrand = (!product.productVehicleBrand ? "--" : product.productVehicleBrand);
        var productVehicleModel = (!product.productVehicleModel ? "--" : product.productVehicleModel);

        var tr = '<tr class="table-row-original">';
        tr += '<td><input type="checkbox" name="subProductCheckBox" data-name="'+product.name+'" data-brand="'+product.brand+'" value="'+productLocalInfoIdStr+'"/>' + (index + 1) + '</td>';
        tr += '<td title="' + commodityCode + '">' + commodityCode + '</td>';
        tr += '<td title="' + productName + '">' + productName + '</td>';
        tr += '<td title="' + productBrand + '">' + productBrand + '</td>';
        tr += '<td title="' + productModel + '">' + productModel + '</td>';
        tr += '<td title="' + productSpec + '">' + productSpec + '</td>';
        tr += '<td title="' + productVehicleBrand + '">' + productVehicleBrand + '</td>';
        tr += '<td title="' + productVehicleModel + '">' + productVehicleModel + '</td>';
        tr += '</tr>';
        $("#productTable").append($(tr));
    });
    tableUtil.tableStyle('#productTable','.tab_title');
}