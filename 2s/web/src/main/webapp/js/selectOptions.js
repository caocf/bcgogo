//单据下拉框
var OrderDropDownList = function () {
    var offset = null;
    var offsetHeight = 0;
    var offsetWidth = 0;
    var setPositionParams = function (domObject) {
        offset = $(domObject).offset();
        offsetHeight = $(domObject).height();
        offsetWidth = $(domObject).width();
    }

    var setSuggestionStyle = function (divId, styleComplement) {
        $("#" + divId).css({
            'display':'block', 'position':'absolute',
            'left':offset.left + 'px',
            'top':offset.top + offsetHeight + 3 + 'px',
            'color':"#000000",
            'overflow-x':"hidden",
            'overflow-y':"auto",
            'padding-left':0 + 'px'
        });
        if (styleComplement)
            $("#" + divId).css(styleComplement);
    }

    var setItemStyle = function (jqueryObj, value) {
        if (!value['width']) {
            GLOBAL.error("下拉建议宽度未设!");
            return;
        }
        jqueryObj.css({'padding-left':'5px', 'color':'#6699cc', 'font-style':'italic',
            'font-size':'10px', 'width':value['width'] + 'px', 'height':15 + 'px',
            'background-color':'#e5e5e5', 'font-weight':'bold'});
    }
    //设置关联input 的值
    var setDefaultAssociateInputValue = function (domObject,dataJsonObjItem) {
        var tr = $(domObject).parent().parent();
        if (dataJsonObjItem[0] == "product_name") {
            tr.find("input[id$='.productName']").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_brand") {
            tr.find("input[id$='.brand']").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_model") {
            tr.find("input[id$='.model']").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_spec") {
            tr.find("input[id$='.spec']").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_vehicle_brand") {
            tr.find("input[id$='.vehicleBrand']").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_vehicle_model") {
            tr.find("input[id$='.vehicleModel']").val(dataJsonObjItem[1]);
        }
    }

    //待退货商品查询  条件设置
    var setInwareHistoryAssociateInputValue = function (dataJsonObjItem) {
        if (dataJsonObjItem[0] == "product_name") {
            $("#itemName").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_brand") {
            $("#itemBrand").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_spec") {
            $("#itemSpec").val(dataJsonObjItem[1]);
        } else if (dataJsonObjItem[0] == "product_model") {
            $("#itemModel").val(dataJsonObjItem[1]);
        }
    }

    //采购、入库、销售品名
    return{
        productNameAndBrand:function (domObject, field, jsonStr, trcount,functionName) {
            setPositionParams(domObject);
            var maxLength = 0;
            setSuggestionStyle("div_brand");

            // 下拉菜单 字段为产品、品牌
            if (field == "product_brand" || field == "product_name") {
                var widthCorrect = ((field == "product_brand" && offsetWidth * 1 < 100) ? 64 : 0);
                $("#div_brand").css({
                    'height':300 + 'px',
                    'width':offsetWidth + widthCorrect + 'px'
                });
                $("#Scroller-Container_id").html("");
                var wc = (field == "product_brand" ? 51 : -13);
                for (var i = 0; i < jsonStr.length; i++) {
                    var a;
                    //校验<A>或<汉字>
                    var pattern = /^<[A-Z]|[\u4e00-\u9fa5]>$/;
                    if (pattern.test(jsonStr[i])) {
                        a = $("<div id='selectItem" + i + "' style=\"overflow: hidden;\" ></div>");
                        jsonStr[i] = jsonStr[i].replace(/</g, "").replace(/>/g, "");
                        a.html(jsonStr[i]);
                        setItemStyle(a, {width:offsetWidth + wc});
                    } else {
                        var product_info = "";
                        for (var j = 0; j < jsonStr[i].suggestionEntry.length; j++) {
                            product_info += jsonStr[i].suggestionEntry[j][1];
                            if (j < jsonStr[i].suggestionEntry.length - 1) {
                                product_info += " ";
                            }
                        }
                        a = $("<a id='selectItem" + i + "' product-data='"+JSON.stringify(jsonStr[i])+"' style='overflow: hidden;color:#00000;'></a>");
                        a
                            .mouseover(function () {
                                $("#Scroller-Container_id > a").removeAttr("class");
                                $(this).attr("class", "hover");
                                $(this).css({ 'width':offsetWidth + wc + 'px'});
                            })
                            .click(function () {
                                var valflag = domObject.value != $(this).html() ? true : false;
                                new clearItemUtil().clearByFlag(domObject, valflag);
                                var dataJsonObj = $.parseJSON($(this).attr("product-data"));
                                for (var i = 0; i < dataJsonObj.suggestionEntry.length; i++) {
                                     if(functionName=="setInwareHistoryAssociateInputValue"){
                                          setInwareHistoryAssociateInputValue(dataJsonObj.suggestionEntry[i]);
                                     }else{
                                         //Default
                                         setDefaultAssociateInputValue(domObject,dataJsonObj.suggestionEntry[i]);
                                     }
                                }
                                $(domObject).blur();
                                $("#div_brand").css({'display':'none'});
                            })
                            .html(product_info)
                            .attr('title',product_info);
                        $("#div_brand").css({'color':'#000'});
                    }
                    if ($(a).html()) {
                        $("#Scroller-Container_id").append(a);
                    }
                }
            } else {      // 下拉菜单 字段为其他情况
                $("#Scroller-Container_id").html("");
                for (var i = 0; i < jsonStr.length; i++) {
                    var product_info = "";
                    for (var j = 0; j < jsonStr[i].suggestionEntry.length; j++) {
                        product_info += jsonStr[i].suggestionEntry[j][1];
                        if (j < jsonStr[i].suggestionEntry.length - 1) {
                            product_info += " ";
                        }
                    }
                    var a = $("<a id='selectItem" + i + "' style=\"overflow: hidden;\"></a>");
                    a
                        .html(product_info)
                        .mouseover(function () {
                            $("#Scroller-Container_id > a").removeAttr("class");
                            $(this).attr("class", "hover");
                        })
                        .click(function () {
                            var valflag = domObject.value != $(this).html() ? true : false;
                            $(domObject).val($(this).html());
                            new clearItemUtil().clearByFlag(domObject, valflag);
                            $(domObject).blur();
                            $("#div_brand").css({'display':'none'});
                        });
                    if (jsonStr[i].length > maxLength) {
                        maxLength = jsonStr[i].length;
                    }
                    if ($(a).html()) {
                        $("#Scroller-Container_id").append(a);
                    }
                }
                var ow = (offsetWidth < 113 ? 80 : 40);
                $("#div_brand").css({
                    'height':250 + 'px',
                    'width':offsetWidth + ow + 'px'
                });
            }
        },

        //采购、入库 车辆 品牌
        vehicleBrand: function(domObject, searchField, jsonStr, trcount,brandValue,domTitleType) {
            setPositionParams(domObject);
            var sc = (searchField == "brand" ? {'height':300 + 'px', 'width':offsetWidth + 80 + 'px'} :
            {'height':230 + 'px', 'width':offsetWidth + 80 + 'px'}); //样式补充
            setSuggestionStyle("div_brand", sc);

            if (searchField == 'brand'||searchField == 'product_vehicle_brand') {
                $("#Scroller-Container_id").html("");
                var pattern = /^<[A-Z]|[\u4e00-\u9fa5]>$/;
                for (var i = 0; i < jsonStr.length; i++) {
                    if(G.isEmpty(jsonStr[i])) continue;
                    var a;
                    if (pattern.test(jsonStr[i])) {
                        a = $("<div id='selectItem" + i + "' style=\"color:#000000;overflow: hidden;white-space:nowrap;margin:0;\" ></div>");
                        jsonStr[i] = jsonStr[i].replace(/</g, "").replace(/>/g, "");
                        a.html(jsonStr[i]);
                        setItemStyle(a, {width:offsetWidth + 50});
                    } else {
                        a = $("<a id='selectItem" + i + "' style=\"color:#000000;overflow: hidden;\"></a>");
                        a
                            .html(jsonStr[i])
                            .mouseover(function () {
                                $("#Scroller-Container_id > a").removeAttr("class");
                                $(this).attr("class", "hover").css({ 'width':offsetWidth + 50 + 'px'});
                            })
                            .click(function () {
                                var valflag = domObject.value != $(this).html() ? true : false;
                                $(domObject).val($(this).html());
                                if(domTitleType=="clientInfo"){
                                    if(valflag) $("#model"+trcount).val("");
                                }else{
                                    var domIdPrefix = $(domObject).attr("id").split(".")[0];
                                    if ($("#" + domIdPrefix + "\\.productId").val()) {
                                        new clearItemUtil().clearByFlag(domObject, valflag);
                                        if($.isFunction(window.setTotal)){
                                            setTotal();
                                        }
                                    }
                                    $(domObject).blur();
                                    $("#div_brand").css({'display':'none'});
                                    $(domObject).parent().nextAll(":visible:first").find("input[type='text']").focus();
                                }
                            })
                            .html(jsonStr[i])
                            .attr('title', jsonStr[i]);
                    }
                    $("#Scroller-Container_id").append(a);
                }
            } else {
                $("#Scroller-Container_id").html("");
                var showmore = false;
                for (var i = 0; i < jsonStr.length; i++) {
                    var a = $("<a id='selectItem" + i + "' style=\"color:#000000;overflow: hidden;\"></a>");
                    a.html(jsonStr[i])
                        .mouseover(function () {
                            $("#Scroller-Container_id > a").removeAttr("class");
                            $(this).attr("class", "hover").css({ 'width':offsetWidth + 65 + 'px'});
                        })
                        .click(function () {
                            var valflag = domObject.value != $(this).html() ? true : false;
                            $(domObject).val($(this).html());
                            if(domTitleType =="clientInfo"){
                                searchVehicleOfModelForCustomerVehicle(domObject,trcount);
                            }else{
                                //      选择车型后自动将车辆品牌填充
                                searchVehicleOfModel(domObject);
                                var domIdPrefix = $(domObject).attr("id").split(".")[0];
                                if($("#"+domIdPrefix+"\\.productId").val()){
                                    new clearItemUtil().clearByFlag(domObject, valflag);
                                    setTotal();
                                }
                                $(domObject).blur();
                                $("#div_brand").css("display", "none");
                                $("#itemDTOs" + trcount + ".productVehicleStatus").val(3);
                                $("#itemDTOs" + trcount + ".vehicleModel"
                                    + "," + "#itemDTOs" + trcount + ".vehicleYear"
                                    + "," + "#itemDTOs" + trcount + ".vehicleEngine").attr("disabled", false);
                                $(domObject).parent().next().find("input[type='text']").focus();
                            }

                        });
                    $("#Scroller-Container_id").append(a);
                    if (i == 9) {
                        showmore = true;
                        break;
                    }
                }
                //弹出复选框的最后一项
                if (( searchField == 'model') && showmore) {
                    var a = $("<a id='selectItem" + (jsonStr.length) + "' style=\"color:#000000;\"></a>");
                    a.html("更多")
                        .mouseover(function () {
                            $("#Scroller-Container_id > a").removeAttr("class");
                            $(this)
                                .attr("class", "hover")
                                .css("width", offsetWidth + 65 + "px");
                        })
                        .click(function () {
                            $("#div_brand").css("display", "none");

                            $("#iframe_PopupBox")
                                .attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle=" + searchField + domTitleType + trcount + "&brandvalue=" + brandValue + "&inputPosition=" + trcount))
                                .css("display", "block");
                            Mask.Login();
                        });
                    $("#Scroller-Container_id").append(a);
                }

            }
        }
    }
}();
 /**
* 选择车型后自动将车辆品牌填充
*/
 function searchVehicleOfModel(domObject) { //车辆信息查询
     var searchWord = domObject.value;
     var brandVal = "",modelVal = "";
     if(!searchWord)
         return;
     var brandId = domObject.id.split(".")[0];
     var suffixn = domObject.id.split(".")[1];
     brandVal = $("#" + brandId + "\\.vehicleBrand").val();
     modelVal = $("#" + brandId + "\\.vehicleModel").val();
     if(suffixn === "vehicleBrand"){
         if($.trim(brandVal) == ""){
             return;
         }
     }
     var data = {
         searchWord:'',
         searchField:"brand",
         brandValue:'',
         modelValue: modelVal ,
         yearValue:'',
         engineValue:''
     };
     APP_BCGOGO.Net.syncAjax({
         url: "product.do?method=searchBrandSuggestion",
         type: "POST",
         cache: false,
         data:data,
         dataType: "json",
         success: function (jsonStr) {
             if (jsonStr.length > 0) {
                 var flag = false;
                 var flagIndex = 0;
                 for (var i = 0,len = jsonStr.length; i < len; i++) {
                     if (jsonStr[i].name == brandVal) {
                         flag = true;
                         flagIndex = i;
                         break;
                     }
                 }
                 if (suffixn == "vehicleModel") {
                     $("#" + brandId + "\\.vehicleBrand").val(jsonStr[flagIndex].name);
                     document.getElementById(brandId + ".vehicleBrand").lastvalue = jsonStr[flagIndex].name;
                     document.getElementById(brandId + ".vehicleBrand").title = jsonStr[flagIndex].name;
                     if ($("#" + brandId + "\\.hidden_vehicleBrand").length > 0)
                         $("#" + brandId + "\\.hidden_vehicleBrand").val(jsonStr[flagIndex].name);
                 }
//                 else if (!flag) {
//                     $("#" + brandId + "\\.vehicleModel").val("");
//                     document.getElementById(brandId + ".vehicleModel").lastvalue = "";
//                     document.getElementById(brandId + ".vehicleModel").title = "";
//                     if ($("#" + brandId + "\\.hidden_vehicleModel").length > 0) {
//                         $("#" + brandId + "\\.hidden_vehicleModel").val("");
//                     }
//                 }
             }else if (jsonStr.length == 0) {
                 return;
             }
         },
         error:function(){
             nsDialog.jAlert("网络异常！");
         }
     });
 }

function searchVehicleOfModelForCustomerVehicle(domObject,rowIndex) { //车辆信息查询
    var searchWord = domObject.value;
    var brandVal = "",modelVal = "";
    if(!searchWord)
        return;
    brandVal = $("#brand"+rowIndex).val();
    modelVal = $("#model"+rowIndex).val();

    var ajaxUrl = "product.do?method=searchBrandSuggestion";
    var ajaxData = {
        searchWord:'',
        searchField:"brand",
        brandValue:'',
        modelValue: modelVal ,
        yearValue:'',
        engineValue:''
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        if (jsonStr.length > 0) {
            var flag = false;
            var flagIndex = 0;
            for (var i = 0,len = jsonStr.length; i < len; i++) {
                if (jsonStr[i].name == brandVal) {
                    flag = true;
                    flagIndex = i;
                    break;
                }
            }
            if(!flag){
                $("#brand"+rowIndex).val(jsonStr[flagIndex].name);
            }
        }
        else if (jsonStr.length == 0) {
            return;
        }
    });
}