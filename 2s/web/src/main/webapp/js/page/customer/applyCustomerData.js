var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(function() {
    provinceBind();
    $("#provinceNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
        $("#cityNo option").not(".default").remove();
        $("#regionNo option").not(".default").remove();
        cityBind();
        $("#cityNo").change();
    });
    $("#cityNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
        $("#regionNo option").not(".default").remove();
        regionBind();
        $("#regionNo").change();
    });
    $("#regionNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
    });

    $("#searchBtn").bind("click",function(){
      searchApplyCustomer();
    });

    $(".J_customerOnlineSuggestion")
        .bind('click', function () {
            getCustomerOrSupplierOnlineSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerOrSupplierOnlineSuggestion($(this));
            }
        });
    function getCustomerOrSupplierOnlineSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            customerOrSupplier:"customerOnline",
            shopRange:"notRelated",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierOnlineSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector":$domObject,
                "data":result,
                "onSelect":function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color":"#000000"});
                    dropList.hide();
                }
            });
        });

    }

    $(".applyCustomer").live("click", function () {
        if ($(this).attr("lock") || !$(this).attr("shopId")) {
            return;
        }
        $(this).attr("lock", true);
        var $thisDom =  $(this);
        var url = "apply.do?method=applyCustomerRelation";
        var data = {"customerShopId":$(this).attr("shopId")};
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
            userGuide.clear();
            if (result.success) {
                nsDialog.jAlert( "您的申请提交成功，请等待对方同意！","", function () {
                    $thisDom.addClass("showCustomerGuideSingleApplySuccess").removeClass("applyCustomer");
                    userGuideInvoker("CONTRACT_CUSTOMER_GUIDE_SINGLE_APPLY_SUCCESS,CONTRACT_CUSTOMER_GUIDE_BATCH_APPLY_SUCCESS");
                });
                $thisDom.removeClass("blue_color")
                .addClass("gray_color")
                .html("已提交关联申请");
                $thisDom.parent().parent().find("input:checkbox[name$='customerShopId']").parent().html('<img src="images/disabledSelect.png">');
            } else {
                nsDialog.jAlert(result.msg);
                $thisDom.removeAttr("lock");
            }
        });
    });
    $(".checkAll").bind("change",function(){
       var isChecked = $(this).attr("checked");
        $("input[name$='customerShopId']").attr("checked",isChecked);
    });
    $("input[name$='customerShopId']").live("click",function(){
       checkAllCheckBox();
    });
    $("#applyCustomerBtn").bind("click",function(){
       if($("input:checkbox[name$='customerShopId'][checked=true]").size() == 0){
           nsDialog.jAlert("请选择想要关联的客户！");
           return;
       }
        if($(this).attr("lock")){
            return;
        }
        $(this).attr("lock",true);
        var customerShopId = "";
        $("input:checkbox[name$='customerShopId'][checked=true]").each(function(index, check){
            if($(this).val()){
                customerShopId += $(this).val();
                customerShopId += ",";
            }
        });
        var url = "apply.do?method=applyCustomerRelation";
        var data = {"customerShopId":customerShopId};
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
            if (result.success) {
                userGuide.clear();
                shadow.clear();
               nsDialog.jAlert("您的申请提交成功，请等待对方同意！","",function(){
               });
                pagingAjaxPostForUpAndDownFunction["_ApplyCustomer"]["flush"]();
            } else {
                nsDialog.jAlert(result.msg);
            }
            $("#applyCustomerBtn").removeAttr("lock");
        },function(){
            nsDialog.jAlert("网络异常，请联系客服！")
            $("#applyCustomerBtn").removeAttr("lock");
        });
    });



    //init
    if(!G.Lang.isEmpty($("#initProvinceNo").val())){
        $("#provinceNo").val($("#initProvinceNo").val());
    }
    $("#provinceNo").change();
    resetCheckedClassByThirdCategoryIdStr($("#thirdCategoryIdStr").val());
    searchApplyCustomer();
})

function searchApplyCustomer() {
  resetThirdCategoryIdStr();
  var ajaxUrl = "apply.do?method=searchApplyCustomers";
  var data = getSearchData();
  APP_BCGOGO.Net.syncPost({
    url: ajaxUrl,
    data: data,
    dataType: "json",
    success: function (json) {
      initApplyCustomerDataTr(json);
      initUpAndDownPage(json, "_ApplyCustomer", ajaxUrl, 'initApplyCustomerDataTr', data);
    }
  });
}


function checkAllCheckBox() {
    if($("input[name$='customerShopId']").size() == 0){
        $(".checkAll").attr("checked", false);
    }else if ($("input:checkbox[name$='customerShopId'][checked=true]").size() < $("input[name$='customerShopId']").size()) {
        $(".checkAll").attr("checked", false);
    } else if ($("input:checkbox[name$='customerShopId'][checked=true]").size() > 0) {
        $(".checkAll").attr("checked", true);
    }
}

function cityBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {"parentNo": $("#provinceNo").val()}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.style.color="#000000";
            option.innerHTML = r[i].name;
            $("#cityNo")[0].appendChild(option);
        }
    }
}

function regionBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {"parentNo": $("#cityNo").val()}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.style.color="#000000";
            option.innerHTML = r[i].name;
            $("#regionNo")[0].appendChild(option);
        }
    }
}
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {"parentNo":"1"}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.style.color="#000000";
            option.innerHTML = r[i].name;
            $("#provinceNo")[0].appendChild(option);
        }
    }
}
function initApplyCustomerDataTr(json){
    $("#applyCustomerData tr").not('.titleBg').remove();
    var showCustomerGuideSingleApplySuccess = true;
    var showCustomerGuideSingleApply = true;
    if (json && json.shopDTOs && json.shopDTOs.length > 0) {
        for (var i = 0, len = json.shopDTOs.length; i < len; i++) {
            var shopDTO = json.shopDTOs[i];
            var shopId = G.Lang.normalize(shopDTO.shopIdStr, "");
            var tr = '<tr class="titBody_Bg">';
            if (shopDTO.inviteStatus && shopDTO.inviteStatus == 'PENDING') {
                tr += '<td style="padding-left:10px;"><img src="images/disabledSelect.png"></td>';
            } else {
                tr += '<td style="padding-left:10px;"><input name="customerShopId" type="checkbox" value="' + shopId + '"></td>'
            }
            tr += '<td style="padding-left:10px;"><a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId='+shopId+'" class="blue_color">' + shopDTO.name+ '</a></td>';
            tr += '<td style="padding-left:10px;">' + shopDTO.address + '</td>';

            var businessScopeStr = stringUtil.isEmpty(shopDTO.businessScope) ? "暂无信息" : shopDTO.businessScope;
            var shortBusinessScope = businessScopeStr.length > 55 ? businessScopeStr.substring(0, 55) + '...' : businessScopeStr;

            tr += '<td style="padding-left:10px;" title="' + businessScopeStr + '">' + shortBusinessScope + '</td>';
            if(APP_BCGOGO.Permission.CustomerManager.CustomerApplyAction){
                if (shopDTO.inviteStatus && shopDTO.inviteStatus == 'PENDING') {
                    tr += '<td style="padding-left:10px;"><a class="gray_color ' + (showCustomerGuideSingleApplySuccess ? " showCustomerGuideSingleApplySuccess " : "") + '">已提交关联申请</a></td>';
                    showCustomerGuideSingleApplySuccess = false;
                }else if(shopDTO.inviteStatus && shopDTO.inviteStatus == 'OPPOSITES_PENDING'){
                    tr += '<td style="padding-left:10px;"><a class="blue_color applyCustomer OPPOSITES_PENDING' + '" shopId = "' + shopId + '">申请建立关联</a></td>';
                } else {
                    tr += '<td style="padding-left:10px;"><a class="blue_color applyCustomer ' + (showCustomerGuideSingleApply ? " showCustomerGuideSingleApply " : "") + '" shopId = "' + shopId + '">申请建立关联</a></td>';
                }
            }else{
                if (shopDTO.inviteStatus && shopDTO.inviteStatus == 'PENDING'){
                    tr += '<td style="padding-left:10px;"><a class="blue_color">已提交关联申请</a></td>';
                } else{
                    tr += '<td style="padding-left:10px;"><a class="blue_color">申请建立关联</a></td>';
                }

            }
            tr += '</tr>';
            $("#applyCustomerData").append($(tr));
            $("#applyCustomerData").append('<tr class="titBottom_Bg"><td colspan="5"></td></tr>');
        }
    }else{
        var tr = '<tr class="titBody_Bg"><td colspan="5" style="padding-left:10px;">对不起，本区域暂无与您合适的推荐客户！</td></tr>';
        $("#applyCustomerData").append($(tr));
        $("#applyCustomerData").append('<tr class="titBottom_Bg"><td colspan="5"></td></tr>');
    }
    checkAllCheckBox();
    userGuideInvoker("CONTRACT_CUSTOMER_GUIDE_SINGLE_APPLY_SUCCESS,CONTRACT_CUSTOMER_GUIDE_BATCH_APPLY_SUCCESS,PRODUCT_ONLINE_GUIDE_BEGIN");
}
function getSearchData() {
    var $_pushMessageId = $("#pushMessageId"), data = {
        "currentPage": 1,
        "name": $("#name").val(),
        "provinceNo": $("#provinceNo").val(),
        "pushMessageId": $_pushMessageId.val(),
        "cityNo": $("#cityNo").val(),
        "regionNo": $("#regionNo").val(),
        "thirdCategoryIdStr":$("#thirdCategoryIdStr").val()
    };
    $_pushMessageId.val("");
    return data;
}