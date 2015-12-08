/**
 *  customerSuggestion productSuggestion invoicingSuggestion 父类
 */
$(document).ready(function () {
    $.fn.selectRange = function (start, end) {
        return this.each(function () {
            if (this.setSelectionRange) {
                this.focus();
                this.setSelectionRange(start, end);
            } else if (this.createTextRange) {
                var range = this.createTextRange();
                range.collapse(true);
                range.moveEnd('character', end);
                range.moveStart('character', start);
                range.select();
            }
        });
    };
    $(document).bind("click", function (event) {
        // hack IE 8, jquery-1.4.2 bug
        try {
            if (event.target === document.lastChild) {
                return;
            }
        } catch (e) {
            ;
        }

        //计次收费项目 div_brand
        //销售人 施工人 div_brand
        var selectorArray = [$("#div_brand"), "[id='invoicingItem'],[id='serviceWorker'],[id='saler'],[id='operator']"];

        // hack IE 8, jquery-1.4.2 bug
        try {
            if ($(event.target).closest(selectorArray).length == 0) {
                $("#div_brand").hide();
            }
        } catch (e) {
            ;
        }

        // hack IE 8, jquery-1.4.2 bug
        try {
            selectorArray = [$("#div_brandvehicleheader"), "[id='vehicleNumber']"];
            if ($(event.target).closest(selectorArray).length == 0) {
                $("#div_brandvehicleheader").hide();
            }
        } catch (e) {
            ;
        }

        // hack IE 8, jquery-1.4.2 bug
        try {
            //会员类型 memberCardTypesPanel
            selectorArray = [$("#memberCardType")];
            if ($(event.target).closest(selectorArray).length == 0) {
                $("#memberCardTypesPanel").hide();
            }
        } catch (e) {
            ;
        }

        // hack IE 8, jquery-1.4.2 bug
        try {
            //商品 使用 div_brand_head div
            //客户供应商 使用 div_brand_head div
            selectorArray = ["[id='input_search_pName'],[id='product_name2_id'],[id='product_commodity_code'],[id='product_brand_id'],[id='product_spec_id'],[id='product_model_id'],[id='pv_brand_id'],[id='pv_model_id']," +
                "[id='customerMemberNo'],[id='customerInfoText'],[id='supplierInfoText'],[id='customer_supplierInfoText']"];
            if ($(event.target).closest(selectorArray).length == 0) {
                $("#div_brand_head").hide();
            }
        } catch (e) {
            ;
        }

        // hack IE 8, jquery-1.4.2 bug
        try {
            //brandvehicleheader div
            selectorArray = [$("#vehicleNumber")];
            if ($(event.target).closest(selectorArray).length == 0) {
                $("#brandvehicleheader").hide();
            }
        } catch (e) {
            ;
        }
    });


    try {
        //关闭浏览器的 自动补全功能
        $("input[type='text']").attr("autocomplete", "off");
    } catch (e) {
        throw new Error("suggestionBase.js ");
    }

});