var PAGE_CUSTOMIZER_CONFIG_PRODUCT_INFO_ARRAY = [
    {"product_info": "LT-0012903WD 轮胎 锦湖 KU31 195/55R15", "inventory": "54个", "average_price": "¥250", "new_storage_price": "¥200", "storage_bin": "1", "sale_price": "300", "trade_price": "290", "product_classify": "轮胎"},
    {"product_info": "LT-0012903WD 轮胎 邓禄普 AT2 1285/75R16", "inventory": "54个", "average_price": "¥350", "new_storage_price": "¥300", "storage_bin": "2", "sale_price": "400", "trade_price": "390", "product_classify": "轮胎"},
    {"product_info": "LT-0012903WD 轮胎 双钱 RR9 315/80R22.5-14", "inventory": "54个", "average_price": "¥550", "new_storage_price": "¥500", "storage_bin": "3", "sale_price": "600", "trade_price": "590", "product_classify": "轮胎"},
    {"product_info": "LT-0012903WD 轮胎 倍耐力 P7 (速度:V) 195/60R15", "inventory": "54个", "average_price": "¥250", "new_storage_price": "¥200", "storage_bin": "4", "sale_price": "300", "trade_price": "290", "product_classify": "轮胎"},
    {"product_info": "LT-0012903HB 轮胎 横滨 AD07 (速度:Z) 245/40R18", "inventory": "54个", "average_price": "¥130", "new_storage_price": "¥100", "storage_bin": "5", "sale_price": "200", "trade_price": "190", "product_classify": "轮胎"}
];
$(function () {
    $(window).bind("beforeunload unload", function(event) {
        if (isAnyConfigsChanged()) {
            return "是否保存所有设置？";
        }
    });

    ajaxCustomizerPageConfigInvoker();

    $("#select-page-config-scene").change(function () {
        var oldValue = $(this).attr("oldvalue");
        if (isAnyConfigsChanged(oldValue)) {
            nsDialog.jConfirm("是否保存所有设置？", "提醒", function (val) {
                if (val) {
                    getForm(oldValue).submit();
                }
                chooseConfigScene($(this));
            })
        } else {
            chooseConfigScene($(this));
        }
        $(this).attr("oldvalue", this.value)
    });

    //单击条件
    $("[action-type=handle-condition]")
        .live("mouseenter", function () {
            var $this = $(this), $tip = $this.find(".alert").show();
        })
        .live("mouseleave", function () {
            $(this).find(".alert").hide();
        })
        .live("click", function () {
            var $this = $(this), data_target_name = $this.attr("data-target-name");
            if ($this.hasClass("btnGray")) {
                $this.removeClass("btnGray");
                $("[name='" + data_target_name + ".checked']").val("true");
                $this.find("[div-type=tip]").html("点击隐藏该条件");

            } else {
                $this.addClass("btnGray");
                $("[name='" + data_target_name + ".checked']").val("false");
                $this.find("[div-type=tip]").html("点击启用该条件");
            }
            isAnyConfigsChanged();
        });

    //form 提交
    $("#order-page-config-form,#order-product-config-form").submit(function (e) {
        e.preventDefault();
        var param = $(this).serializeArray();
        var data = {}, url = this.action;
        console.log(param)
        $.each(param, function (index, val) {
            if (!G.Lang.isEmpty(data[val.name])) {
                data[val.name] = data[val.name] + "," + val.value;
            } else {
                data[val.name] = val.value;
            }
        });
        APP_BCGOGO.Net.syncPost({
            url: url,
            dataType: "json",
            data: data,
            success: function (result) {
                nsDialog.jAlert(result["msg"], null, function () {
                    if (result["success"]) {
                        ajaxCustomizerPageConfigInvoker();
                    }
                });
            },
            error: function () {
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });
    });

    //取消
    $("#cancel-page-customizer-config").click(function () {
        if (isAnyConfigsChanged()) {
            ajaxCustomizerPageConfigInvoker();
        }
    });


    $("#save-page-customizer-config").click(function () {
        if (isAnyConfigsChanged()) {
            getForm().submit();
        }
    });

    $("#restore-order-config,#restore-product-config").click(function () {
        restoreConfig({scene: $(this).attr("scene")});
    });

    $('[action-type="change-product-info-right-sort"]').live("click", function () {
        var rightTargetDomName = $(this).attr("right-target-dom-name"),
            targetDomName = $(this).attr("target-dom-name"),
            infoName = $(this).attr("product-info-name"),
            rightInfoName = $(this).attr("right-product-info-name"),
            $targetDom = $('[name="' + targetDomName + '.sort"]'),
            $rightTargetDom = $('[name="' + rightTargetDomName + '.sort"]'),
            targetDomAttrSortValue = Number($targetDom.val()) ,
            rightTargetAttrDomSortValue = Number($rightTargetDom.val()),
            result = getPageProductConfig() ,
            configInfoList = result["contentDto"]['configInfoList'],
            info;
        $targetDom.val(rightTargetAttrDomSortValue);
        $rightTargetDom.val(targetDomAttrSortValue);
        for (var i = 0; i < configInfoList.length; i++) {
            info = configInfoList[i];
            if (info['name'] == infoName) {
                info['sort'] = rightTargetAttrDomSortValue;
            }
            if (info['name'] == rightInfoName) {
                info['sort'] = targetDomAttrSortValue;
            }
        }
        sortArray(configInfoList);
        setPageProductConfig(result);
        initCustomizerProductPageConfigHtml(result);
        isAnyConfigsChanged();
    });

    $('[action-type="change-product-info-left-sort"]').live("click", function () {
        var leftTargetDomName = $(this).attr("left-target-dom-name"),
            targetDomName = $(this).attr("target-dom-name"),
            infoName = $(this).attr("product-info-name"),
            leftInfoName = $(this).attr("left-product-info-name"),
            $targetDom = $('[name="' + targetDomName + '.sort"]'),
            $leftTargetDom = $('[name="' + leftTargetDomName + '.sort"]'),
            targetDomAttrSortValue = Number($targetDom.val()),
            leftTargetDomAttrSortValue = Number($leftTargetDom.val()),
            result = getPageProductConfig() ,
            configInfoList = result["contentDto"]['configInfoList'],
            info;
        $targetDom.val(leftTargetDomAttrSortValue);
        $leftTargetDom.val(targetDomAttrSortValue);
        for (var i = 0; i < configInfoList.length; i++) {
            info = configInfoList[i];
            if (info['name'] == infoName) {
                info['sort'] = leftTargetDomAttrSortValue;
            }
            if (info['name'] == leftInfoName) {
                info['sort'] = targetDomAttrSortValue;
            }
        }
        sortArray(configInfoList);
        setPageProductConfig(result);
        initCustomizerProductPageConfigHtml(result);
        isAnyConfigsChanged();
    });

    $('[action-type="delete-product-info"]').live("click", function () {
        var targetDomName = $(this).attr("target-dom-name"),
            infoName = $(this).attr("product-info-name"),
            result = getPageProductConfig() ,
            configInfoList = result["contentDto"]['configInfoList'],
            info;
        $('[name="' + targetDomName + '.checked"]').val("false");
        for (var i = 0; i < configInfoList.length; i++) {
            info = configInfoList[i];
            if (info['name'] == infoName) {
                info['checked'] = false;
                break;
            }
        }
        setPageProductConfig(result);
        initCustomizerProductPageConfigHtml(result);
        isAnyConfigsChanged();
    });
    //从小到大排序
    function sortArray(array) {
        array.sort(function (a, b) {
            return a['sort'] > b['sort'] ? 1 : -1
        });
    }

    function initCustomizerProductPageConfigHtml(data) {
        if (!data || !data["contentDto"])return;
        var content = data["contentDto"], configInfoList, totalWidth = 973, totalWeight = 0, html = "", i, c, j,
            $productConditionArea = $("#product_condition_area"), info, checkedInfoList = [], uncheckedInfoList = [];
        configInfoList = content['configInfoList'];
        if (!configInfoList || configInfoList.length == 0) return;
        $productConditionArea.children().remove();
        for (i = 0, c = 0,j=0; i < configInfoList.length; i++) {
            info = configInfoList[i];
            if (!info['checked']) {
                uncheckedInfoList[j++] = info;
                continue;
            }
            checkedInfoList[c++] = info;
            totalWeight += info['weight'];
        }
        html += '<colgroup>';
        for (i = 0; i < checkedInfoList.length; i++) {
            info = checkedInfoList[i];
            html += '<col width="' + ((info['weight'] / totalWeight) * totalWidth) + '">';
        }
        html += '</colgroup>';
        html += '<tr class="titleBg" >';
        for (i = 0; i < checkedInfoList.length; i++) {
            info = checkedInfoList[i];
            if (i == 0) {
                html += '<td style="padding-left:10px;">' + info["value"] + '</td>';
            } else {
                html += '<td>' + info["value"] + '</td>';
            }
        }
        html += ' </tr>';
        html += '<tr class="icon_opera" >';
        for (i = 0; i < checkedInfoList.length; i++) {
            info = checkedInfoList[i];
            if (i == 0) {
                html += '<td style="padding-left:10px;">';
            } else {
                html += '<td>';
            }
            if (!$.isEmptyObject(checkedInfoList[i - 1])) {
                html += '<a class="icon_left" title="点击可调整列显示顺序" product-info-name="' + info['name'] + '" left-product-info-name="' + checkedInfoList[i - 1]['name'] + '" target-dom-name="contentDto.configInfoList[' + i + ']" action-type="change-product-info-left-sort" left-target-dom-name="contentDto.configInfoList[' + (i - 1) + ']"></a>';
            }
            if (!info['necessary']) {
                html += '<a class="icon_delete" title="点击可隐藏该列" product-info-name="' + info['name'] + '" action-type="delete-product-info" target-dom-name="contentDto.configInfoList[' + i + ']"></a>';
            } else {
                html += '&nbsp;';
            }
            if (!$.isEmptyObject(checkedInfoList[i + 1])) {
                html += '<a class="icon_right" title="点击可调整列显示顺序" product-info-name="' + info['name'] + '" right-product-info-name="' + checkedInfoList[i + 1]['name'] + '"  action-type="change-product-info-right-sort" right-target-dom-name="contentDto.configInfoList[' + ( i + 1) + ']" target-dom-name="contentDto.configInfoList[' + i + ']"></a>';
            }
            html += '</td>';
        }
        html += ' </tr>';
        $productConditionArea.append($(html));
        $productConditionArea.append($(initProductInfoHtml(configInfoList, PAGE_CUSTOMIZER_CONFIG_PRODUCT_INFO_ARRAY[0])));
        $productConditionArea.append($(initProductInfoHtml(configInfoList, PAGE_CUSTOMIZER_CONFIG_PRODUCT_INFO_ARRAY[1])));
        $productConditionArea.append($(initProductInfoHtml(configInfoList, PAGE_CUSTOMIZER_CONFIG_PRODUCT_INFO_ARRAY[2])));
        $productConditionArea.append($(initProductInfoHtml(configInfoList, PAGE_CUSTOMIZER_CONFIG_PRODUCT_INFO_ARRAY[3])));
        $productConditionArea.append($(initProductInfoHtml(configInfoList, PAGE_CUSTOMIZER_CONFIG_PRODUCT_INFO_ARRAY[4])));
        html = '<input type="hidden" name="scene" value="' + data['scene'] + '">';
        html += '<input type="hidden" name="status" value="' + data['status'] + '">';
        html += '<input type="hidden" name="id" value="' + data['idStr'] + '">';
        html += '<input type="hidden" name="shopId" value="' + data['shopIdStr'] + '">';
        html += '  <input type="hidden" name="contentDto.name" value="' + content['name'] + '">';
        html += '  <input type="hidden" name="contentDto.checked" value="' + content['checked'] + '">';
        html += '  <input type="hidden" name="contentDto.value" value="' + content['value'] + '">';
        html += '  <input type="hidden" name="contentDto.necessary" value="' + content['necessary'] + '">';
        html += '  <input type="hidden" name="contentDto.sort" value="' + content['sort'] + '">';
        for (i = 0; i < checkedInfoList.length; i++) {
            info = checkedInfoList[i];
            html += '  <div style="display: none;">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].checked" value="' + info['checked'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].sort" value="' + info['sort'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].resourceName" value="' + info['resourceName'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].name" value="' + info['name'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].value" value="' + info['value'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].weight" value="' + info['weight'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + i + '].necessary" value="' + info['necessary'] + '">';
            html += '  </div>';
        }
        c = checkedInfoList.length;
        for (i = 0; i < uncheckedInfoList.length; i++) {
            info = uncheckedInfoList[i];
            html += '  <div style="display: none;">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].checked" value="' + info['checked'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].sort" value="' + info['sort'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].resourceName" value="' + info['resourceName'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].name" value="' + info['name'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].value" value="' + info['value'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].weight" value="' + info['weight'] + '">';
            html += '     <input type="hidden" name="contentDto.configInfoList[' + (c + i) + '].necessary" value="' + info['necessary'] + '">';
            html += '  </div>';
        }
        $productConditionArea.append($(html));
    }

    function initProductInfoHtml(configInfoList, productInfo) {
        var info, html = "";
        html += '<tr class="titBody_Bg">';
        for (var i = 0; i < configInfoList.length; i++) {
            info = configInfoList[i];
            if (!info['checked']) continue;
            if (i == 0) {
                html += '<td style="padding-left:10px;">';
            } else {
                html += '<td>';
            }
            switch (info['name']) {
                case "alarm_settings":
                    html += '<input type="text" class="txt txt_color" value="5" style="width:30px;"/>&nbsp;/&nbsp;<input type="text" value="20" class="txt" style="width:30px;"/>';
                    break;
                case "product_classify":
                case "trade_price":
                case "sale_price":
                case "storage_bin":
                    html += '<input type="text" class="txt txt_color" value="' + productInfo[info['name']] + '"/>';
                    break;
                default:
                    html += productInfo[info['name']];
            }
            html += '</td>';
        }
        html += ' </tr>';
        html += '<tr class="titBottom_Bg"><td colspan="' + configInfoList.length + '"></td></tr>';
        return html;
    }

    function initCustomizerOrderPageConfigHtml(data) {
        if (!data || !data["contentDto"]) return;
        var content = data["contentDto"], configResult, conditionHtml = "", configInfoList,
            $orderConditionArea = $("#order_condition_area"), info, tipContent;
        $orderConditionArea.children().remove();
        for (var i = 0; i < content.length; i++) {
            configResult = content[i];
            configInfoList = configResult['configInfoList'];
            if (configInfoList && configInfoList.length > 0) {
                conditionHtml = '<div class="divTit divWarehouse divCondition">';
                conditionHtml += '  <span class="spanName"><b>' + configResult['value'] + '：</b></span>';
                conditionHtml += '  <div class="warehouseList">';
                conditionHtml += '     <div style="display: none;">';
                conditionHtml += '          <input type="hidden" name="contentDto[' + i + '].name" value="' + configResult['name'] + '" orig-value="' + configResult['name'] + '"/>';
                conditionHtml += '          <input type="hidden" name="contentDto[' + i + '].value" value="' + configResult['value'] + '" orig-value="' + configResult['value'] + '"/>';
                conditionHtml += '          <input type="hidden" name="contentDto[' + i + '].sort" value="' + configResult['sort'] + '" orig-value="' + configResult['sort'] + '"/>';
                conditionHtml += '          <input type="hidden" name="contentDto[' + i + '].necessary" value="' + configResult['necessary'] + '" orig-value="' + configResult['necessary'] + '"/>';
                conditionHtml += '     </div>';
                for (var j = 0; j < configInfoList.length; j++) {
                    info = configInfoList[j];
                    conditionHtml += '<a class="btnList ';
                    if (info['checked']) {
                        tipContent = "点击隐藏该条件";
                    } else {
                        conditionHtml += ' btnGray ';
                        tipContent = "点击启用该条件";
                    }
                    conditionHtml += '" action-type="handle-condition" data-target-name="contentDto[' + i + '].configInfoList[' + j + ']">';
                    conditionHtml += info['value'] +
                        '   <div class="alert" style="display: none;"><span class="arrowTop"></span>' +
                        '       <div class="alertAll">' +
                        '           <div class="alertLeft"></div>' +
                        '           <div class="alertBody" div-type="tip">' + tipContent + '</div>' +
                        '           <div class="alertRight"></div>' +
                        '       </div>' +
                        '   </div>';
                    conditionHtml += '  <div style="display: none;">';
                    conditionHtml += '      <input type="hidden" name="contentDto[' + i + '].configInfoList[' + j + '].name" value="' + info['name'] + '" orig-value="' + info['name'] + '"/>';
                    conditionHtml += '      <input type="hidden" name="contentDto[' + i + '].configInfoList[' + j + '].value" value="' + info['value'] + '" orig-value="' + info['value'] + '"/>';
                    conditionHtml += '      <input type="hidden" name="contentDto[' + i + '].configInfoList[' + j + '].checked" value="' + info['checked'] + '" orig-value="' + info['checked'] + '"/>';
                    conditionHtml += '      <input type="hidden" name="contentDto[' + i + '].configInfoList[' + j + '].resourceName" value="' + info['resourceName'] + '"/>';
                    conditionHtml += '      <input type="hidden" name="contentDto[' + i + '].configInfoList[' + j + '].necessary" value="' + info['necessary'] + '"/>';
                    conditionHtml += '      <input type="hidden" name="contentDto[' + i + '].configInfoList[' + j + '].sort" value="' + info['sort'] + '" orig-value="' + info['sort'] + '"/>';
                    conditionHtml += '  </div>';
                    conditionHtml += '</a>  ';
                }
                conditionHtml += '  </div>';
                conditionHtml += '</div>';
                $orderConditionArea.append($(conditionHtml));
            }
        }
        conditionHtml = '<input type="hidden" id="order-scene" name="scene" value="' + data['scene'] + '">';
        conditionHtml += '<input type="hidden" id="order-status" name="status" value="' + data['status'] + '">';
        conditionHtml += '<input type="hidden" id="order-id" name="id" value="' + data['idStr'] + '">';
        conditionHtml += '<input type="hidden" id="order-shop-id" name="shopId" value="' + data['shopIdStr'] + '">';
        $orderConditionArea.append($(conditionHtml));
    }


    function isAnyConfigsChanged(val) {
        var $selectDom = $("#select-page-config-scene") ,
            isAnyChanged = false, i;
        val = val || $selectDom.find("option:selected").val();
        if (val == "order") {
            var inputs = getForm(val).find("input[type=hidden]"), origValue;
            for (i = 0; i < inputs.length; i++) {
                origValue = $(inputs[i]).attr("orig-value");
                if (origValue && origValue != $(inputs[i]).val()) {
                    isAnyChanged = true;
                    break;
                }
            }
        } else if (val == "product") {
            var defaultConfig = getDefaultPageProductConfig(),
                currentConfig = getPageProductConfig(),
                defaultConfigInfoList = defaultConfig["contentDto"]['configInfoList'],
                configInfoList = currentConfig["contentDto"]['configInfoList'];
            for (i = 0; i < configInfoList.length; i++) {
                if (JSON.stringify(defaultConfigInfoList[i]) != JSON.stringify(configInfoList[i])) {
                    isAnyChanged = true;
                    break;
                }
            }
        }
        if (isAnyChanged) {
            enableConfigButton();
        } else {
            disableConfigButton();
        }
        return  isAnyChanged;
    }

    function enableConfigButton() {
        $("#cancel-page-customizer-config").removeClass("gray_button");
        $("#save-page-customizer-config").removeClass("gray_button");
    }

    function disableConfigButton() {
        $("#cancel-page-customizer-config").addClass("gray_button");
        $("#save-page-customizer-config").addClass("gray_button");
    }

    function restoreConfig(config) {
        APP_BCGOGO.Net.syncPost({
            url: "pageCustomizerConfig.do?method=restorePageConfig",
            dataType: "json",
            data: config,
            success: function (result) {
                nsDialog.jAlert(result["msg"], null, function () {
                    if (result["success"]) {
                        ajaxCustomizerPageConfigInvoker()
                    }
                });
            },
            error: function () {
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });
    }

    function getForm(val) {
        var $form = null;
        val = val || $("#select-page-config-scene").find("option:selected").val();
        if (val == "order") {
            $form = $("#order-page-config-form");
        } else if (val == "product") {
            $form = $("#order-product-config-form");
        }
        return $form;
    }

    function ajaxCustomizerPageConfigInvoker($selectDom) {
        $selectDom = $("#select-page-config-scene") || $selectDom;
        var val = $selectDom.find("option:selected").val(),
            url, invoker;
        if (val == "order") {
            url = "pageCustomizerConfig.do?method=getOrderPageConfig";
            invoker = initCustomizerOrderPageConfigHtml;
        } else if (val == "product") {
            url = "pageCustomizerConfig.do?method=getProductPageConfig";
            invoker = initCustomizerProductPageConfigHtml
        }
        APP_BCGOGO.Net.syncAjax({
            type: "POST",
            url: url,
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result && result.success) {
                    if (val == "product") {
                        setPageProductConfig(result['data']);
                        setDefaultPageProductConfig(result['data']);
                        defaultStorage.setItem(storageKey.PageCustomizerProductConfig, JSON.stringify(result['data']['contentDto']['configInfoList']));
//                        defaultStorage.removeItem(storageKey.PageCustomizerProductConfig);
                    }
                    invoker(result['data']);
                    disableConfigButton();
                } else {
                    G.error(" ");
                }
            },
            error: function () {
                G.error(url + " error response!");
            }
        });
    }

    function chooseConfigScene($selectDom) {
        $selectDom = $("#select-page-config-scene") || $selectDom;
        var val = $selectDom.find("option:selected").val();
        if (val == "order") {
            $("[area-name=order]").show();
            $("[area-name=product]").hide();
        } else if (val == "product") {
            $("[area-name=order]").hide();
            $("[area-name=product]").show();
        }
        ajaxCustomizerPageConfigInvoker($selectDom);
    }

    function getPageProductConfig() {
        return  jQuery.parseJSON($("#page_customizer_of_product_config").val());
    }

    function setPageProductConfig(config) {
        $("#page_customizer_of_product_config").val(JSON.stringify(config));
    }

    function getDefaultPageProductConfig() {
        return  jQuery.parseJSON($("#default_page_customizer_of_product_config").val());
    }


    function setDefaultPageProductConfig(config) {
        $("#default_page_customizer_of_product_config").val(JSON.stringify(config));
    }
});