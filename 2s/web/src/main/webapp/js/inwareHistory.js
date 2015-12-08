var supplierNameLastVal;
jQuery(document).ready(function() {
    jQuery(document).keydown(function(e) {
                checkHistoryEvent(e);
            });
//	   window.parent.addHandle(document.getElementById('div_purchaseReturnSearch'), window);
    getChecked();

    jQuery("#returan_search").bind("click", function() {
        jQuery("#pageNo").val(1);
        jQuery("#thisform").submit();
    });

    jQuery("#create_btn").bind("click", function() {
        jQuery("#thisform").attr("action", "goodsReturn.do?method=createReturnStorage");
        jQuery("#thisform").attr("target", "_parent");
        jQuery("#thisform").submit();
    });

    jQuery(".click_detail").bind("click", function() {
        jQuery(jQuery(this).parent().parent().next()).toggle();
    });

    jQuery("#div_close").bind("click", function() {
        jQuery("#mask", parent.document).hide();
        if (jQuery("#iframe_PopupBox", parent.document)) {
            jQuery("#iframe_PopupBox", parent.document).hide();
            jQuery("#iframe_PopupBox", parent.document).attr("src", "");
        }
        if (jQuery("#iframe_PopupBox_1", parent.document)) {
            jQuery("#iframe_PopupBox_1", parent.document).hide();
            jQuery("#iframe_PopupBox_1", parent.document).attr("src", "");
        }
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });

    jQuery(".indexNo").bind("click", function() {
        if (!jQuery(this).attr("checked")) {   //去掉选中状态
            var checkVal = jQuery(this).val();
            jQuery(".selectedItemDTOCheckId").each(function() {
                var selectVal = jQuery(this).val();
                if (checkVal == selectVal) {
                    jQuery(this).parent().remove();
//                    return false;
                }
            });
        } else {        //变成状态
            var idprefix = jQuery(this).attr("id").split(".")[0];
            var supplierId = jQuery("#" + idprefix + "\\.supplierId").val();
            var isSameSupplier = true;
            jQuery(".selectedItemDTOCheckId").each(function() {
                var selectedIdPrefix = jQuery(this).attr("id").split(".")[0];
                var selectedSupplierId = jQuery("#" + selectedIdPrefix + "\\.supplierId").val();
                if (supplierId != selectedSupplierId) {
                    isSameSupplier = false;
                    return false;
                }
            });
            if (isSameSupplier) {
                jQuery(".indexNo").each(function() {
                    if (jQuery(this).attr("checked")) {
                        var selectedIdPrefix = jQuery(this).attr("id").split(".")[0];
                        var selectedSupplierId = jQuery("#" + selectedIdPrefix + "\\.supplierId").val();
                        if (supplierId != selectedSupplierId) {

                            isSameSupplier = false;
                            return false;
                        }
                    }
                });
            }
            if (!isSameSupplier) {
                showMessage.fadeMessage("8%", "24%", "fast", 3000, "请选择同一供应商的商品!");
                jQuery(this).attr("checked", false);
            }
            if (isSameSupplier) {
                var supplierName = jQuery("#" + idprefix + "\\.supplierName").val();
                jQuery("#customerOrSupplierName").val(supplierName);
            }
        }
        checkAllChecked();
        checkAllNotChecked();
    });

    jQuery("#checkAlls").bind("click", function() {
        if (jQuery(this).attr("checked")) {      //选上的时候
            var supplierId;
            var isCanSelectAll = true;
            if (jQuery(".selectedItemDTOCheckId").get(0) != null) {
                var idprefix = jQuery(".selectedItemDTOCheckId").eq(0).attr("id");
                idprefix = idprefix.split(".")[0];
                supplierId = jQuery("#" + idprefix + "\\.supplierId").val();
            }
            if (supplierId != '' && supplierId != null) {
                jQuery(".indexNo").each(function() {
                    var selectedIdPrefix = jQuery(this).attr("id").split(".")[0];
                    var selectedSupplierId = jQuery("#" + selectedIdPrefix + "\\.supplierId").val();
                    if (supplierId != selectedSupplierId) {
                        isCanSelectAll = false;
                        return false;
                    }
                });
            }
            jQuery(".indexNo").each(function() {
                var selectedIdPrefix = jQuery(this).attr("id").split(".")[0];
                var selectedSupplierId = jQuery("#" + selectedIdPrefix + "\\.supplierId").val();
                jQuery(".indexNo").each(function() {
                    var selectedIdPrefix2 = jQuery(this).attr("id").split(".")[0];
                    var selectedSupplierId2 = jQuery("#" + selectedIdPrefix2 + "\\.supplierId").val();
                    if (selectedSupplierId != selectedSupplierId2) {
                        isCanSelectAll = false;
                        return false;
                    }
                });
                if (!isCanSelectAll) {
                    return false;
                }
            });
            if (isCanSelectAll) {
                var supplierName;
                jQuery(".indexNo").each(function() {
                    jQuery(this).attr("checked", true);
                });
                var selectedIdPrefix3 = jQuery(".indexNo").attr("id").split(".")[0];
                supplierName = jQuery("#" + selectedIdPrefix3 + "\\.supplierName").val();
                jQuery("#customerOrSupplierName").val(supplierName);
            } else {
                showMessage.fadeMessage("8%", "24%", "fast", 3000, "请选择同一供应商的商品!");
                jQuery(this).attr("checked", false);
            }
        } else {            //去掉勾选的时候
            jQuery(".indexNo").each(function() {
                if (jQuery(this).attr("checked")) {
                    jQuery(this).attr("checked", false);
                    var checkVal = jQuery(this).val();
                    jQuery(".selectedItemDTOCheckId").each(function() {
                        var selectVal = jQuery(this).val();
                        if (checkVal == selectVal) {
                            jQuery(this).parent().remove();
//                                   return false;
                        }
                    });
                }
            });
        }
    });

    //供应商信息下拉建议
    jQuery("#customerOrSupplierName").bind("keyup",
        function(e) {
            customerOrSupplierNameKeyUp(this, e);
            supplierNameLastVal = jQuery(this).val();
        }).bind("focus", function() {
            supplierNameLastVal = jQuery(this).val();
        });
    //下拉框隐藏事件
    jQuery("#div_show").bind("click", function() {
        if (jQuery("#div_show").not(jQuery("#customerOrSupplierName"), jQuery("#div_brandvehiclelicenceNo"))) {
            jQuery("#div_brandvehiclelicenceNo").hide();
            jQuery("#div_brand").hide();
        }
    });

    jQuery("#itemName,#itemBrand,#itemSpec,#itemModel").bind("click",
        function() {
            inwareHistoryDomClick(this);
        }).live("keyup", function(e) {
            inwareHistoryDomKeyUp(this, e);
        });

    jQuery(".table2 .opera2").bind("click", function() {       //减号
        var idprefix = jQuery(this).attr("id");
        idprefix = idprefix.split(".")[0];
        var amount = dataTransition.simpleRounding(jQuery("#" + idprefix + "\\.amount").val(), 1) * 1 - 1;
        if (amount < 0.0001) {
            amount = 0;
        }
        jQuery("#" + idprefix + "\\.amount").val(dataTransition.simpleRounding(amount, 1));
    });

    jQuery(".table2 .opera1").bind("click", function() {            //加号
        var idprefix = jQuery(this).attr("id");
        idprefix = idprefix.split(".")[0];
        var amount = dataTransition.simpleRounding(jQuery("#" + idprefix + "\\.amount").val(), 1) * 1 + 1;
        var returnAbleAmout = dataTransition.simpleRounding(jQuery("#" + idprefix + "\\.returnAbleAmount").val(), 1) * 1;
        if (amount > returnAbleAmout + 0.001) {
            amount = returnAbleAmout;
        }
        jQuery("#" + idprefix + "\\.amount").val(dataTransition.simpleRounding(amount, 1));
    });

    jQuery(".table2 input[id$='.amount']").bind("blur", function() {
       var amount = jQuery(this).val();
        amount = dataTransition.rounding(amount, 1) * 1;
        var idprefix = jQuery(this).attr("id");
        idprefix = idprefix.split(".")[0];
        var returnAbleAmount =  jQuery("#" + idprefix + "\\.returnAbleAmount").val() * 1;
        returnAbleAmount = dataTransition.rounding(returnAbleAmount, 1) * 1;
        if (amount < 0.001) {
            amount = 0;
        }
        if (amount > returnAbleAmount + 0.001) {
            amount = returnAbleAmount;
        }
	    jQuery(this).val(amount);
    });

    jQuery("#sure_btna").bind("click", function() {
       jQuery("#div_close").click();
    });
});

function getChecked() {  //获得选中项目
    var isSelect = false;

    jQuery(".indexNo").each(function() {
        isSelect = false;
        var checkVal = jQuery(this).val();
        jQuery(".selectedItemDTOCheckId").each(function() {
            var selectVal = jQuery(this).val();
            if (checkVal == selectVal) {
                isSelect = true;
                return false;
            }
        });
        if (isSelect) {
            jQuery(this).attr("checked", true);
        }
    });
    checkAllChecked();
		checkAllNotChecked();
}

function checkAllChecked() {
    var isAllChecked = true;
    jQuery(".indexNo").each(function() {
        if (!jQuery(this).attr("checked")) {
            isAllChecked = false;
            return false;
        }
    });
    if (isAllChecked) {
        jQuery("#checkAlls").attr("checked", true);
    }
}

function checkAllNotChecked() {
    var isAllNotChecked = false;
    jQuery(".indexNo").each(function() {
        if (!jQuery(this).attr("checked")) {
            isAllNotChecked = true;
            return false;
        }
    });
    if (!jQuery(".indexNo")[0]) {
			isAllNotChecked = true;
		}
    if (isAllNotChecked) {
        jQuery("#checkAlls").attr("checked", false);
    }
}

function customerOrSupplierNameKeyUp(dom, e) {
    var currentSupplierVal = jQuery(dom).val();
    var eve = e || event;
    var eventKeyCode = eve.which || eve.keyCode;
    if (eventKeyCode == 108 || eventKeyCode == 13) {
        if (currentSupplierVal != supplierNameLastVal) {
            supplierNameLastVal = currentSupplierVal;
        } else {
            if (jQuery("#Scroller-Container_idlicenceNo > a").eq(0) != null) {
                jQuery(dom).val(jQuery("#Scroller-Container_idlicenceNo > a").eq(0).attr("title"));
            }
            jQuery("#returan_search").click();
            return;

        }
    }

    if (!checkKeyUp(dom, e)) {
        return;
    }
    var supplierName = jQuery(dom).val();
    if (supplierName == '' || !supplierName) {
        jQuery("#div_brandvehiclelicenceNo").css({'display':'none'});
    }
    else {
        supplierName = supplierName.replace(/[\ |\\]/g, "");
        jQuery(dom).val(supplierName);
        var domObject = jQuery(dom);
        jQuery.ajax({
                type:"POST",
                url:"RFSupplier.do?method=ajaxSearchSupplierName",
                async:true,
                data:{
                    name:supplierName
                },
                cache:false,
                dataType:"json",
                error:function(XMLHttpRequest, error, errorThrown) {
                    jQuery("#div_brandvehiclelicenceNo").css({'display':'none'});
                },
                success:function(jsonStr) {
                    var offset = jQuery(domObject).offset();
                    var offsetHeight = jQuery(domObject).height();
                    var offsetWidth = jQuery(domObject).width();
                    var x = offset.left;
                    var y = offset.top;
                    var selectmore = jsonStr.length;
                    var height = jsonStr.length * 20;
                    var selectValue;
                    var selectItemNum;
                    if (selectmore <= 0) {
                        jQuery("#div_brandvehiclelicenceNo").css({'display':'none'});
                    }
                    else {
                        jQuery("#div_brandvehiclelicenceNo").css({
                            'display':'block','position':'absolute',
                            'left':x + 'px',
                            'top':y + offsetHeight + 'px' ,
                            'width':offsetWidth + 'px',
                            'overflow-x':"hidden" ,
                            'overflow-y':"hidden" ,
                            'height':height + 'px' ,
                            'padding-left': 0 + 'px'
                        });
                        jQuery("#Scroller-Container_idlicenceNo").html("");

                        for (var i = 0; i < jsonStr.length; i++) {
                            var a = jQuery("<a id='selectItem" + i + "'></a>");
                            if (typeof(jsonStr[i].name) != "undefined" && jsonStr[i].name != "" && jsonStr[i].name != null) {
                                if (jsonStr[i].name.length > 15) {
                                    a.html(jsonStr[i].name.substring(0, 14) + "...");
                                }
                                else {
                                    a.html((jsonStr[i].name));
                                }
                            }

                            a.attr("title", jsonStr[i].name);
                            a.css({"overflow": "hidden","width":"290px"});
                            if (typeof(jsonStr[i].contract) != "undefined" && jsonStr[i].contract != "" && jsonStr[i].contract != null) {
                                //截取联系人长度
                                if (jsonStr[i].contract.length > 8) {
                                    a.append("+" + jsonStr[i].contract.substring(0, 7) + "...");
                                }
                                else {
                                    a.append("+" + jsonStr[i].contract);
                                }
                            }
                            if (typeof(jsonStr[i].mobile) != "undefined" && jsonStr[i].mobile != "" && jsonStr[i].mobile != null) {
                                if (jsonStr[i].mobile.length > 11) {
                                    a.append("+" + jsonStr[i].mobile.substring(0, 10) + "...");
                                }
                                else {
                                    a.append("+" + jsonStr[i].mobile);
                                }
                            }
                            a.mouseover(function() {
                                jQuery("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                                jQuery(this).attr("class", "hover");
                                selectValue = jsonStr[jQuery("#Scroller-Container_idlicenceNo > a").index(jQuery(this)[0])].name;// jQuery(this).html();
                                selectItemNum = parseInt(this.id.substring(10));
                            });
                            a.mouseout(function() {
                                selectValue = "";
                            });
                            a.click(function() {
                                jQuery(domObject).val(selectValue = jsonStr[jQuery("#Scroller-Container_idlicenceNo > a").index(jQuery(this)[0])].name);     //取的第一字符串
                                jQuery("#supplierId").val(selectValue = jsonStr[jQuery("#Scroller-Container_idlicenceNo > a").index(jQuery(this)[0])].idStr);
                                selectItemNum = -1;
                                jQuery("#div_brandvehiclelicenceNo").css({'display':'none'});
                                jQuery("#Scroller-Container_idlicenceNo").children().remove();
                            });
                            jQuery("#Scroller-Container_idlicenceNo").append(a);
                        }
                    }
                }
            }
        );
    }
}

function inwareHistoryDomClick(dom) {
    var productValue = jQuery("#itemName").val();
    var brandValue = jQuery("#itemBrand").val();
    var specValue = jQuery("#itemSpec").val();
    var modelValue = jQuery("#itemModel").val();
    var idStr = jQuery(dom).attr("id");
    var positionNum = "";
    if (idStr == "itemName") {
        positionNum = "product_name";
        productValue = "";
        brandValue = "";
        specValue = "";
        modelValue = "";
    } else if (idStr == "itemBrand") {
        positionNum = "product_brand";
        brandValue = "";
        specValue = "";
        modelValue = "";
    } else if (idStr == "itemSpec") {
        positionNum = "product_spec";
        specValue = "";
        modelValue = "";
    } else if (idStr == "itemModel") {
        positionNum = "product_model";
        modelValue = "";
    }
    searchSuggestionForWaitReturn(dom, positionNum, productValue, brandValue, specValue, modelValue, 1, 0, 'click');
    if (idStr == "itemBrand") {
        var offsetWidth = jQuery(dom).width();
        setTimeout(function() {
            jQuery("#div_brand").css({
                'width': offsetWidth + 'px'
            });
        }, 100);
    }
}

var isTheOne = false;
function searchSuggestionForWaitReturn(domObject, searchField, productValue, brandValue, specValue, modelValue, trcount, functionStatus, eventStr) { //车辆信息查询
    var searchValue = "";
    if (functionStatus == 1 || eventStr == "notClick") {
        searchValue = domObject.value.replace(/[\ |\\]/g, "");
    }
    var ajaxUrl = "product.do?method=searchmaterialforgoodsbuy";
    var ajaxData = {
        searchWord:searchValue,searchField:searchField,
        productName:searchField == 'product_name' ? '' : productValue,
        productBrand:searchField == 'product_brand' ? '' : brandValue,
        productSpec:searchField == 'product_spec' ? '' : specValue,
        productModel:searchField == 'product_model' ? '' : modelValue,functionStatus:functionStatus
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
        OrderDropDownList.productNameAndBrand(domObject, searchField, jsonStr, trcount - 1, "setInwareHistoryAssociateInputValue");
        isTheOne = false;
    });
}

function inwareHistoryDomKeyUp(dom, e) {
    var data = dataTransition.stripHTML(jQuery(dom).val());
    jQuery(dom).val(data);

    var pos = getCursorPosition(dom);
    if (!checkKeyUp(dom, e)) {
        return;
    }


    jQuery("#itemName").val(dataTransition.stripHTML(jQuery("#itemName").val()));
    jQuery("#itemBrand").val(dataTransition.stripHTML(jQuery("#itemBrand").val()));
    jQuery("#itemSpec").val(dataTransition.stripHTML(jQuery("#itemSpec").val()));
    jQuery("#itemModel").val(dataTransition.stripHTML(jQuery("#itemModel").val()));

    var productValue = jQuery("#itemName").val();
    var brandValue = jQuery("#itemBrand").val();
    var specValue = jQuery("#itemSpec").val();
    var modelValue = jQuery("#itemModel").val();


    var idStr = jQuery(dom).attr("id");
    var positionNum = "";
    if (idStr == "itemName") {
        positionNum = "product_name";
        productValue = "";
        brandValue = "";
        specValue = "";
        modelValue = "";
    } else if (idStr == "itemBrand") {
        positionNum = "product_brand";
        brandValue = "";
        specValue = "";
        modelValue = "";
    } else if (idStr == "itemSpec") {
        positionNum = "product_spec";
        specValue = "";
        modelValue = "";
    } else if (idStr == "itemModel") {
        positionNum = "product_model";
        modelValue = "";
    }

    searchSuggestionForWaitReturn(dom, positionNum, productValue, brandValue, specValue, modelValue, 1, 1, 'notClick');
    setCursorPosition(dom, pos);

//    if (idStr == "itemBrand") {
//        var offsetWidth = jQuery(dom).width();
//        setTimeout(function() {
//            jQuery("#div_brand").css({
//                'width': offsetWidth + 'px'
//            });
//        }, 80);
//    }
}

function getCursorPosition(ctrl) {//获取光标位置函数
    var CaretPos = 0;
    // IE Support
    if (ctrl.type != "text") {
        return;
    }
    if (document.selection) {
        ctrl.focus();
        var Sel = document.selection.createRange();
        Sel.moveStart('character', -ctrl.value.length);
        CaretPos = Sel.text.length;
    }
    // Firefox support
    else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0')
    {
//        ctrl.focus();
        CaretPos = ctrl.selectionStart;
    }
    return (CaretPos);
}

function setCursorPosition(ctrl, pos) {//设置光标位置函数
    if (ctrl.type != "text") {
        return;
    }
    if (ctrl.setSelectionRange) {
        ctrl.focus();
        ctrl.setSelectionRange(pos, pos);
    }
    else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}

function closeWindow() {
    window.parent.document.getElementById("mask").style.display = "none";
    if (window.parent.document.getElementById("iframe_PopupBox") != null) {
        window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox").src = "";
    }
    if (window.parent.document.getElementById("iframe_PopupBox_1") != null) {
        window.parent.document.getElementById("iframe_PopupBox_1").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_1").src = "";
    }
    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
        ;
    }
}
