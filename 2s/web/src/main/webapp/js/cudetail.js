//TODO 关闭窗口
function closeWindow() {
    window.parent.document.getElementById("mask").style.display = "none";
    window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
        ;
    }
}

//TODO 方法已改 可以删除
function thisformsubmit(domObj) {
    thisform.pageStatus.value = domObj.id;
    searchSubmit(1);
}


//TODO  可以删除
function showPurchasePrice() {
    //该逻辑已经作废，为避免语法错误，暂时不删除function
}
//TODO   可以删除
function hidePurchasePrice() {
    //该逻辑已经作废，为避免语法错误，暂时不删除function
}

//TODO 处理库存明细中的排序，首先处理上下箭头的样式，最后提交表单
function switchStyle(dom, field) {
    var className = dom.className;
    var sortStr = "Asc";
    if (className == "ascending") {
        dom.className = "descending";
        sortStr = "Desc";
    } else if (className == "descending") {
        dom.className = "ascending";
        sortStr = "Asc";
    }
    showSortField(dom);
    document.getElementById("sortStatus").value = field + sortStr;
    thisform.pageStatus.value = "currentPage";
    thisform.submit();
}

//TODO 当某列单击了排序箭头，为该列加上背景颜色，以示强调
function showSortField(dom) {
    var dp = dom.parentElement.parentElement.getElementsByTagName("td");
    for (var i = 0; i < dp.length; i++)
        dp[i].style.cssText = "";
    dom.parentElement.style.cssText = "background-color:#ECF5FF";
}

//TODO  页面加载完后，根据“sortStatus”设置排序列的背景颜色和样式
function sortStyle() {
    var sortStatus = document.getElementById("sortStatus").value;
    if (sortStatus == "" || sortStatus == null)return;
    if (sortStatus.indexOf("Asc") != -1) {
        var sortField = sortStatus.substring(0, sortStatus.indexOf("Asc"));
        var sortClass = getSortClass("Asc");
        setSortStyle(sortField, sortClass);
    } else if (sortStatus.indexOf("Desc") != -1) {
        var sortField = sortStatus.substring(0, sortStatus.indexOf("Desc"));
        var sortClass = getSortClass("Desc");
        setSortStyle(sortField, sortClass);
    }
}

//TODO 根据状态获得对应的样式名称
function getSortClass(status) {
    if (status == "Asc")
        return "ascending";
    else
        return "descending";
}

//TODO 将field列的排序箭头设置为sortClass,同时加上背景颜色
function setSortStyle(field, sortClass) {
    var fieldName = undefined;
    if (field == "name")
        fieldName = "nid";
    else if (field == "brand")
        fieldName = "bid";
    else if (field == "spec")
        fieldName = "sid";
    else if (field == "model")
        fieldName = "mid";
    else if (field == "vehicleModel")
        fieldName = "vid";
    document.getElementById(fieldName).className = sortClass;
    showSortField(document.getElementById(fieldName));
}

$(function() {
    //TODO 当前页面中最新入库价的隐藏与显示
    $(document).keydown(function(e) {
        var e = e || event;
        var eventKeyCode = e.witch || e.keyCode;
        if (eventKeyCode == 17) {
	          var tradePriceFlag = $("#tradePriceTag").val();
	          var obj;
            if (tradePriceFlag == "true") {
		          obj = $(".span_purchase_price,.span_trade_price");
            } else {
		          obj = $(".span_purchase_price");
	          }
//            var ip = document.getElementById("iframe_PopupBox");
//            if (ip) {
//                var ipCss = $(ip).css("display");
//                var contentDoc = ip.contentWindow.document;
//                if (ipCss == "block")
//	                if (tradePriceFlag == "true") {
//		                obj = $(contentDoc).find(".span_purchase_price,.span_trade_price");
//	                } else {
//		                obj = $(contentDoc).find(".span_purchase_price");
//	                }
//            }
	           var cssStyle = obj.eq(0).css("display");
	          cssStyle = (cssStyle == "none") ? "block" : (cssStyle == "block") ? "none" : cssStyle;
            obj.each(function(i) {
                $(this).css("display", cssStyle);
            });
        }
    });
    sortStyle();
});

//TODO 排除特定的字符串
function cuDetailValidate() {
    var thisform = document.thisform;
    if (thisform.productName.value == "品名") thisform.productName.value = "";
    if (thisform.productBrand.value == "品牌") thisform.productBrand.value = "";
    if (thisform.productSpec.value == "规格") thisform.productSpec.value = "";
    if (thisform.productModel.value == "型号") thisform.productModel.value = "";
    if (thisform.brand.value == "车辆品牌") thisform.brand.value = "";
    if (thisform.model.value == "车型") thisform.model.value = "";
}
//TODO 排除特定的字符串
function removeIllegalStr(param) {
    if (param == "品名" || param == "品牌" || param == "规格" || param == "型号" || param == "车辆品牌" || param == "车型")
        return "";
    return param;
}

//TODO 当前页面的搜索提交 “stockQuery”表示放大镜搜索，其余是分页
function searchSubmit(flag) { //flag:0回首页，1:页码不变
    if (flag == 0)
        document.thisform.pageStatus.value = "Home";
    cuDetailValidate();
    if (document.thisform.productName.value != "" || document.thisform.productBrand.value != "" || document.thisform.productSpec.value != "" ||
        document.thisform.productModel.value != "" || document.thisform.brand.value != "" || document.thisform.model.value != "")
        document.thisform.stockQuery.value = "stockQuery";
    else
        document.thisform.stockQuery.value = "";
    document.thisform.submit();
}

$(function() {
    //TODO 单击下拉建议区域外，隐藏下拉建议
    $(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_brand") {
            $("#div_brand")[0].style.display = "none";
        }
    });

    //TODO 为库存明细的搜索条件添加单击和键盘事件
    $("#productName,#productBrand,#productSpec,#productModel,#brand,#model").live("click keyup", function(e) {
        stockSearch(this, e.type);
    });
});

//TODO 用于判断是查询产品还是车辆信息的下拉建议
function stockSearch(dom, eventType) {
    var id = dom.id;
    var searchField = id;
    var searchProduct = false;
    if (id == "productName") {
        searchField = "product_name";
        searchProduct = true;
    }
    else if (id == "productBrand") {
        searchField = "product_brand";
        searchProduct = true;
    }
    else if (id == "productSpec") {
        searchField = "product_spec";
        searchProduct = true;
    }
    else if (id == "productModel") {
        searchField = "product_model";
        searchProduct = true;
    }
    if (searchProduct) {
        productSearch(dom, searchField, eventType);
    }
    else {
        vehicleSearch(dom, searchField, eventType);
    }
}

//TODO AJAX搜索产品，用于下拉建议
function productSearch(dom, searchField, eventType) {
    var searchWords = getProductRestrictions(searchField);
    var sw = (eventType == "click" ? "" : dom.value);
    $.ajax({
            type:"POST",
            url:"product.do?method=searchProductInfoForStockSearch",
            async:true,
            data:{
                searchWord:sw,searchField:searchField,
                productValue:searchField == 'product_name' ? '' : searchWords[0],
                brandValue:searchField == 'product_brand' ? '' : searchWords[1],
                specValue:searchField == 'product_spec' ? '' : searchWords[2],
                modelValue:searchField == 'product_model' ? '' : searchWords[3]
            },
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                ajaxStyleForProduct(dom, jsonStr);
            }
        }
    );
}

//TODO AJAX搜索车辆信息，用于下拉建议
function vehicleSearch(dom, searchField, eventType) {
    var searchWords = getVehicleRestrictions(searchField);
    var sw = (eventType == "click" ? "" : dom.value);
    $.ajax({
            type:"POST",
            url:"product.do?method=searchProductForStockSearch",
            async:true,
            data:{
                searchWord:sw,searchField:searchField,
                productValue:searchField == 'product_name' ? '' : removeIllegalStr($("#productName").val()),
                brandValue:searchField == 'brand' ? '' : searchWords[0],
                modelValue:searchField == 'model' ? '' : searchWords[1]
            },
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                ajaxStyle(dom, jsonStr);
            }
        }
    );
}

//TODO keyup搜索带条件查询，click查询所有。此处用于判断搜索条件  （针对车辆）
function getVehicleRestrictions(searchField) {
    var brand = removeIllegalStr($("#brand").val());
    var model = removeIllegalStr($("#model").val());
    switch (searchField) {
        case "brand":
            model = ""
            break;
    }
    var array = new Array();
    array[0] = brand;
    array[1] = model;
    return array;
}

//TODO keyup搜索带条件查询，click查询所有。此处用于判断搜索条件 （针对产品）
function getProductRestrictions(searchField) {
    var pn = removeIllegalStr($("#productName").val());
    var pb = removeIllegalStr($("#productBrand").val());
    var ps = removeIllegalStr($("#productSpec").val());
    var pm = removeIllegalStr($("#productModel").val());
    switch (searchField) {
        case "product_name":
            pb = ""
            ps = ""
            pm = ""
            break;
        case "product_brand":
            ps = ""
            pm = ""
            break;
        case "product_spec":
            pm = ""
            break;
    }
    var array = new Array();
    array[0] = pn;
    array[1] = pb;
    array[2] = ps;
    array[3] = pm;
    return array;
}

//TODO 处理包含商品和车辆信息的JSON，组装成下拉建议
function ajaxStyleForProduct(domObject, jsonStr) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();

    $("#div_brand").css({
        'display':'block','position':'absolute',
        'left':offset.left + 'px',
        'top':offset.top + offsetHeight + 3 + 'px'
    });

    $("#Scroller-Container_id").html("");
    for (var i = 0; i < jsonStr.length; i++) {
        var a = $("<a style='overflow:hidden;color:#000000;height:20px;line-height:20px' id='selectItem" + i + "'></a>");
        var product = "";
        for (var j = 0; j < jsonStr[i].suggestionEntry.length; j++) {
            product += jsonStr[i].suggestionEntry[j][1] + " ";
        }
        a.html($.trim(product));
        a.attr("title", $.trim(product));
        $(a).attr("productInfo", JSON.stringify(jsonStr[i].suggestionEntry));
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
//            $(domObject).val($(this).html());
            var product = $.parseJSON($(this).attr("productInfo"));
            $(domObject).blur();
            $("#div_brand").css({'display':'none'});
            setToProductSearchInputs(product);
        });
        $("#Scroller-Container_id").append(a);
    }
}
//库存对应filed填充
function setToProductSearchInputs(product) {
    for (var j = 0; j < product.length; j++) {
        if (product[j][0] == "product_name") {
            $("#productName").val(product[j][1]);
        }
        if (product[j][0] == "product_brand") {
            $("#productBrand").val(product[j][1]);
        }
        if (product[j][0] == "product_spec") {
            $("#productSpec").val(product[j][1]);
            $("#productSpec").change();
        }
        if (product[j][0] == "product_model") {
            $("#productModel").val(product[j][1]);
        }
        if (product[j][0] == "product_vehicle_brand") {
            $("#brand").val(product[j][1]);
        }
        if (product[j][0] == "product_vehicle_model") {
            $("#model").val(product[j][1]);
        }
    }
}
//TODO 处理包含商品和车辆信息的JSON，组装成下拉建议
function ajaxStyle(domObject, jsonStr) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();

    $("#div_brand").css({
        'display':'block','position':'absolute',
        'left':offset.left + 'px',
        'top':offset.top + offsetHeight + 3 + 'px'
    });

    $("#Scroller-Container_id").html("");
    for (var i = 0; i < jsonStr.length; i++) {
        var a = $("<a style='overflow:hidden;color:#000000;height:20px;line-height:20px' id='selectItem" + i + "'></a>");
        a.html(jsonStr[i].name);
        a.attr("title", jsonStr[i].name);
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            $(domObject).val($(this).html());
            $(domObject).blur();
            $("#div_brand").css({'display':'none'});
        });
        $("#Scroller-Container_id").append(a);
    }
}

//TODO 下拉建议的上下键盘事件，用于上下键的快速选择
$().ready(function() {
  //权限控制入库价
    if (jQuery("#goodsBuyPermission").val() == "true") {
      jQuery(".table2 .purchasePrice_Num").show();
  }
    var selectNum = -1;
    var domObj;
    $(document).keydown(function(e) {
        var e = e || event;
        var eventKeyCode = e.witch || e.keyCode;
        var target = e.target;
        $(target).blur(function() {
            selectNum = -1;
        });
        if ($("#div_brand").css("display") == "block") {
            var size = $("#Scroller-Container_id>a").size();
            $("#Scroller-Container_id > a").removeAttr("class");
            if (eventKeyCode == 38) {
                selectNum = selectNum <= 0 ? size - 1 : selectNum - 1;
                if (selectNum == (size - 1)) {
                    domObj = $("#Scroller-Container_id>a:last");
                    domObj.attr("class", "hover");
                } else {
                    domObj = domObj.prev();
                    domObj.attr("class", "hover");
                }

            } else if (eventKeyCode == 40) {
                selectNum = selectNum >= (size - 1) ? 0 : selectNum + 1;
                if (selectNum == 0) {
                    domObj = $("#Scroller-Container_id>a:first");
                    domObj.attr("class", "hover");
                } else {
                    domObj = domObj.next();
                    domObj.attr("class", "hover");
                }
            }
            if (eventKeyCode == 13) {
                $("#selectItem" + (selectNum)).click();
                selectNum = -1;
            }
        }
    });
});