(function () {
    APP_BCGOGO.namespace("Module.wjl.invoiceCommon");

    var fieldNames = ["commodityCode", "productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel", "vehicleYear", "vehicleEngine"];
    var invoiceCommon = {};
    invoiceCommon.excludeSpaceSlash = function (idPrefix) {
        for (var i = 0; i < fieldNames.length; i++) {
            var fooId = "#" + idPrefix + "\\." + fieldNames[i];
            if ($(fooId).val()) {
                $(fooId).val($(fooId).val().replace(/[\s|\\]/g, ""));
        }
    }
    }

    invoiceCommon.populateSearchParams = function (idPrefix, idSuffix) {
        var valueArrName = ["commodityCodeValue", "productValue", "brandValue", "specValue", "modelValue", "vehicleBrandValue", "vehicleModelValue", "vehicleYearValue", "vehicleEngineValue"];

        function setValueByElementId(valueNameArr, destinationValueNameArr) {
            var retObject = {};
            for (var i = 0, len = valueNameArr.length; i < len; i++) {
                if ($("#" + idPrefix + "\\." + valueNameArr[i])&&idSuffix!=valueNameArr[i]) {
		           retObject[destinationValueNameArr[i]] = $("#" + idPrefix + "\\." + valueNameArr[i]).val();
	            }
            }
            return retObject;
        }
        var valueObject = setValueByElementId(fieldNames, valueArrName);

        function setValueToEmptyString(nameArray, obj) {
            return; //新需求 不清除
            for (var i = 0, len = nameArray.length; i < len; i++) {
                obj[nameArray[i]] = "";
            }
        }
        var positionNum = "";
        if (idSuffix == "commodityCode") {
		       positionNum = "commodity_code";
            setValueToEmptyString(["productValue", "brandValue", "specValue", "modelValue"], valueObject);   //todo commodityCode"commodityCodeValue",
        } else if (idSuffix == "productName") {
            positionNum = "product_info";
            setValueToEmptyString(["productValue", "brandValue", "specValue", "modelValue"], valueObject);
        } else if (idSuffix == "brand") {
            positionNum = "product_brand";
            setValueToEmptyString(["brandValue", "specValue", "modelValue"], valueObject);
        } else if (idSuffix == "spec") {
            positionNum = "product_spec";
            setValueToEmptyString(["specValue", "modelValue"], valueObject);
        } else if (idSuffix == "model") {
            positionNum = "product_model";
            setValueToEmptyString(["modelValue"], valueObject);
        } else if (idSuffix == "vehicleBrand") {
            positionNum = "product_vehicle_brand";
            setValueToEmptyString(["vehicleBrandValue", "vehicleModelValue", "vehicleYearValue", "vehicleEngineValue"], valueObject);
        } else if (idSuffix == "vehicleModel") {
            positionNum = "product_vehicle_model";
            setValueToEmptyString([ "vehicleModelValue", "vehicleYearValue", "vehicleEngineValue"], valueObject);
        } else if (idSuffix == "vehicleYear") {
            positionNum = "year";
            setValueToEmptyString(["vehicleYearValue", "vehicleEngineValue"], valueObject);
        } else if (idSuffix == "vehicleEngine") {
            positionNum = "engine";
            setValueToEmptyString(["vehicleEngineValue"], valueObject);
        }
        return {
            "positionNum":positionNum,
            "valueObject":valueObject
        };
    }

    invoiceCommon.getItemStrSplice = function (str, index) {
        for (var i = 0, len = fieldNames.length; i < len; i++) {
            str += $("#itemDTOs" + index + "\\." + fieldNames[i]).val() + "|";
        }
        return str;
    }

//node:$对象
    invoiceCommon.selectAndFocusByNode = function ($node) {
        $node.select();
        $node.focus();
    }

    invoiceCommon.keyBoardSelectTarget = function (dom, keyName) {

        var domMap = {"deliveryDateStr":0, "saveBtn":1, "printBtn":2, "cancelBtn":3};
        var domArray = ["#deliveryDateStr", "#saveBtn", "#printBtn", "#cancelBtn"];

        /**
         * 根据键盘名，返回操作状态
         * @param keyName  键盘名
         */
        var eventRules = function (keyName) {
            if (keyName.search(/enter|right|down/g) != -1) {
                return 1;
            } else if (keyName.search(/left|up/g) != -1) {
                return -1;
            }
            return 0;
        };

        var selectAndOnfocus = function (nextDom) {
            $(nextDom).click();
            nextDom.select();
            nextDom.focus();
        };

        /**
         * 用于处理供应商栏和材料单的键盘选择事件
         */
        var materialsSelect = function () {
            if (eventRules(keyName) > 0) {
                nextDom = $(dom).parent().next().find("input[type='text']");
                if (nextDom.length <= 0) {
                    return;
                }
                var idSuffix = nextDom.attr("id").split(".")[1];
                var idPrefix = nextDom.attr("id").split(".")[0];
                if (idSuffix == "vehicleYear") {
                    nextDom = $("#" + idPrefix + "\\." + "purchasePrice");
                } else if (idSuffix == "total") {
                    nextDom = $("#" + idPrefix + "\\." + "lowerLimit");
            }
            } else if (eventRules(keyName) < 0) {
                nextDom = $(dom).parent().prev().find("input[type='text']");
                if (nextDom[0]) {
                    var idSuffix = nextDom.attr("id").split(".")[1];
                    var idPrefix = nextDom.attr("id").split(".")[0];
                    if (idSuffix == "vehicleEngine") {
                        nextDom = $("#" + idPrefix + "\\." + "vehicleBrand");
            }
                } else {
                    nextDom = $("#" + dom.id.split(".")[0] + "\\." + "unit");
                }
            }
            selectAndOnfocus(nextDom);
        };


        /**
         * 用于处理交货日期栏的键盘选择事件
         */
        var deliveryDateSelect = function () {
            if (eventRules(keyName) > 0) {
                nextDom = $(domArray[domMap[dom.id] + 1]);
            } else if (eventRules(keyName) < 0) {
                nextDom = $(domArray[domMap[dom.id] - 1]);
            }
            selectAndOnfocus(nextDom);
        };

        var domId = dom.id;
        var nextDom;
        if (eventRules(keyName) > 0) {
            if ($(".s_tabelBorder").find("input[type = 'text']").last().attr("id") == domId) {
                nextDom = $(".item:first").find("input[type = 'text']").first();
            } else if ($(".item:last").find("input[type = 'text']").last().attr("id") == domId) {
                nextDom = $("#deliveryDateStr");
            } else if (domId == "cancelBtn") {
                nextDom = $(".s_tabelBorder").find("input[type = 'text']").eq(1);
            } else if ($(dom).parents(".item").find("input[type = 'text']").last().attr("id") == domId) {
                nextDom = $(dom).parent().parent().next().find("input[type = 'text']").first();
            }
        } else if (eventRules(keyName) < 0) {
            if ($(".s_tabelBorder").find("input[type = 'text']").eq(1).attr("id") == domId)
                nextDom = $("#cancelBtn");
            else if (domId == "deliveryDateStr")
                nextDom = $(".item:last").find("input[type = 'text']").last();
            else if ($(".item:first").find("input[type = 'text']").first().attr("id") == domId)
                nextDom = $(".s_tabelBorder").find("input[type = 'text']").last();
            else if ($(dom).parents(".item").find("input[type = 'text']").first().attr("id") == domId) {
                nextDom = $(dom).parent().parent().prev().find("input[type = 'text']").last();
            }
        }
        if (nextDom) {
            selectAndOnfocus(nextDom);
            return;
        }
        var parents = $(dom).parents("#table_productNo")[0];
        if (parents)
            materialsSelect();
        else {
            deliveryDateSelect();
        }
    };

    var ItemSet = APP_BCGOGO.wjl.Collection.Set;

    invoiceCommon.checkSameItemForOrder = function (trClassName) {
        var orderItems = $("." + trClassName);
	      var productIdSet = new ItemSet();
	      var productIdNumber = 0;
        orderItems.each(function (i) {
		     var productId = $(this).find("input[id$='.productId']").val();
            if (productId) {
                productIdNumber++;
			     productIdSet.add(productId);
		      }
	      });
        if (productIdNumber != productIdSet.size()) {
		      return true;
	      }
        var itemNum = orderItems.size();
        var properties = ["commodityCode", "productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel", "vehicleYear", "vehicleEngine"];
        var newItemSet = new ItemSet();
        orderItems.each(function (i) {
            var itemInfo = "";
            for (var i = 0, len = properties.length; i < len; i++) {
                itemInfo += $(this).find("input[id$='." + properties[i] + "']").val() + "__&&__$$";
            }
            newItemSet.add(itemInfo);
        });
        if (itemNum != newItemSet.size()) {
            return true;
        }
        return false;
    }
		invoiceCommon.checkSameCommodityCode = function (trClassName) {
			var properties = ["commodityCode"];
			var orderItems = $("." + trClassName);
			var newItemSet = new ItemSet();
			var commodityCodeSize = 0;
        orderItems.each(function () {
				var itemInfo = $(this).find("input[id$='." + properties[0] + "']").val();
				if (itemInfo) {
                commodityCodeSize++;
					newItemSet.add(itemInfo);
				}
			});
			if (commodityCodeSize != newItemSet.size()) {
				return true;
			} else {
				return false;
			}
		}

    var searchFields = ["product_name", "product_brand", "product_spec", "product_model", "brand", "model", "year", "engine"];
    invoiceCommon.bcgogoFields = {};
    for (var i = 0, len = fieldNames.length; i < len; i++) {
        invoiceCommon.bcgogoFields[fieldNames[i]] = searchFields[i];
    }


//如果dom的值等于target,则将其重设为新的newValue
    invoiceCommon.reSetDomVal = function (dom, target, newValue) {
        if (dom && $.trim(dom.value) == target) {
            dom.value = newValue;
        }
    }

    APP_BCGOGO.Module.wjl.invoiceCommon = invoiceCommon;
})();