<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-categorySelector<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" charset="utf-8" src="js/components/ui/bcgogo-categorySelector<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        //分类选择
        var categorySelector = new App.Module.CategorySelector();
        APP_BCGOGO.Net.syncAjax({
            url: "searchInventoryIndex.do?method=getAllProductCategorySelectorData",
            type: "POST",
            dataType: "json",
            success:function (data) {
                categorySelector.init({
                    "selector":"#productCategorySelectorDiv",
                    "data":data,
                    "isHighlight":true,
                    "highlightClassName":"yellow_color",
                    "onInput":function(event, index, value, viewData,parentIdArr) {
                        if(categorySelector.xhr) {
                            categorySelector.xhr.abort();
                        }
                        if(G.isEmpty(value)){
                            categorySelector.resetCategory(index,viewData);
                            return viewData;
                        }

                        var url = "searchInventoryIndex.do?method=getProductCategorySuggestion";
                        if(index === 0) {
                            categorySelector.xhr = APP_BCGOGO.Net.asyncAjax({
                                url:url,
                                type: "POST",
                                data:{searchWord:value,productCategoryType:"FIRST_CATEGORY",parentId:-1},
                                dataType: "json",
                                success:function (result) {
                                    var resultData = [];
                                    if(!G.isEmpty(result)&&!G.isEmpty(result.data)){
                                        $.each(result.data,function(index,val){
                                            if(!G.isEmpty(val) && !G.isEmpty(val.details)){
                                                resultData.push({id:val.details["id"],name:val.details["name"]});
                                            }
                                        });
                                    }
                                    categorySelector.resetCategory(index,resultData,value);
                                }
                            });
                        } else if(index === 1) {
                            categorySelector.xhr = APP_BCGOGO.Net.asyncAjax({
                                url:url,
                                type: "POST",
                                data:{searchWord:value,productCategoryType:"SECOND_CATEGORY",parentId:parentIdArr[index-1]["id"]},
                                dataType: "json",
                                success:function (result) {
                                    var resultData = [];
                                    if(!G.isEmpty(result)&&!G.isEmpty(result.data)){
                                        $.each(result.data,function(index,val){
                                            if(!G.isEmpty(val) && !G.isEmpty(val.details)){
                                                resultData.push({id:val.details["id"],name:val.details["name"]});
                                            }
                                        });
                                    }
                                    categorySelector.resetCategory(index,resultData,value);
                                }
                            });
                        } else if(index === 2) {
                            categorySelector.xhr = APP_BCGOGO.Net.asyncAjax({
                                url:url,
                                type: "POST",
                                data:{searchWord:value,productCategoryType:"THIRD_CATEGORY",parentId:parentIdArr[index-1]["id"]},
                                dataType: "json",
                                success:function (result) {
                                    var resultData = [];
                                    if(!G.isEmpty(result)&&!G.isEmpty(result.data)){
                                        $.each(result.data,function(index,val){
                                            if(!G.isEmpty(val) && !G.isEmpty(val.details)){
                                                resultData.push({id:val.details["id"],name:val.details["name"]});
                                            }
                                        });
                                    }
                                    categorySelector.resetCategory(index,resultData,value);
                                }
                            });
                        }
                    },
                    onSelect:function(event, data, index) {
                        $("#productCategorySelector #usedProductCategorySelect").val("");
                        var selectDataInfo = "";
                        if(index === 0) {
                            selectDataInfo = data[0]["level_1_name"];
                            $("#productCategorySelector #_productCategorySelectedId").val("");
                            $("#productCategorySelector #_productCategorySelectedName").val("");
                            $("#productCategorySelector #_productCategorySelectedType").val("");
                        } else if(index === 1) {
                            selectDataInfo = data[0]["level_1_name"]+" >> "+data[0]["level_2_name"];
                            $("#productCategorySelector #_productCategorySelectedId").val(data[0]["level_2_id"]);
                            $("#productCategorySelector #_productCategorySelectedName").val(data[0]["level_2_name"]);
                            $("#productCategorySelector #_productCategorySelectedType").val("SECOND_CATEGORY");
                        } else if(index === 2) {
                            selectDataInfo = data[0]["level_1_name"]+" >> "+data[0]["level_2_name"]+" >> "+data[0]["level_3_name"];
                            $("#productCategorySelector #_productCategorySelectedId").val(data[0]["level_3_id"]);
                            $("#productCategorySelector #_productCategorySelectedName").val(data[0]["level_3_name"]);
                            $("#productCategorySelector #_productCategorySelectedType").val("THIRD_CATEGORY");
                        }
                        $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                    }
                });

            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });

        var $currentClickBtnObject;
        if($(".batchSetProductCategory")){
            //批量设置商品分类
            $(".batchSetProductCategory").bind("click",function(){
                var obj = this
                if($(".itemChk:checked").length<=0){
                    nsDialog.jAlert("请选择要设置分类的商品!");
                    return false;
                }
                $("#productCategorySelector").dialog({
                    width: "auto",
                    modal: true,
                    resizable:false,
                    title: "批量设置商品分类",
                    beforeclose: function(event, ui) {
                        resetProductCategorySelector();
                        categorySelector.reset();
                        $currentClickBtnObject = null;
                        return true;
                    },
                    open: function () {
                        $currentClickBtnObject = $(obj);
                        return true;
                    }
                });
            });
        }

        $(".J_selectProductCategory").bind("click",function(e){
            var obj = this
            $("#productCategorySelector").dialog({
                width: "auto",
                modal: true,
                resizable:false,
                title: "选择商品分类",
                beforeclose: function(event, ui) {
                    resetProductCategorySelector();
                    categorySelector.reset();
                    $currentClickBtnObject = null;
                    return true;
                },
                open: function () {
                    $currentClickBtnObject = $(obj);
                    var $parent = $currentClickBtnObject.parent();

                    var productCategoryType = $parent.find("input[id$='productCategoryType']").val();
                    var productCategoryId = $parent.find("input[name$='productCategoryId']").val();
                    if(!G.isEmpty(productCategoryId) && !G.isEmpty(productCategoryType)){
                        var productCategoryName = $parent.find("input[name$='productCategoryName']").val();
                        var selectDataInfo = $parent.find(".J_productCategoryInfoSpan").text();
                        if("SECOND_CATEGORY" == productCategoryType){
                            categorySelector.reset({"level_2_id":productCategoryId},2);
                        }else if("THIRD_CATEGORY" == productCategoryType){
                            categorySelector.reset({"level_3_id":productCategoryId},3);
                        }
                        $("#productCategorySelector #_productCategorySelectedId").val(productCategoryId);
                        $("#productCategorySelector #_productCategorySelectedName").val(productCategoryName);
                        $("#productCategorySelector #_productCategorySelectedType").val(productCategoryType);
                        $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                    }

                    return true;
                }
            });
        });
        //快速搜索
        $("#productCategorySelector #searchProductCategoryBtn").bind("click",function(e){
            e.preventDefault();
            var searchWord = $("#productCategorySelector #searchWord").val().replace(/[\ |\\]/g, "");
            if(G.isEmpty(searchWord)){
                nsDialog.jAlert("请输入搜索关键字！");
                return;
            }
            APP_BCGOGO.Net.asyncAjax({
                url: "searchInventoryIndex.do?method=getProductCategoryDetailList",
                type: "POST",
                data:{searchWord:searchWord},
                dataType: "json",
                success:function (data) {
                    $("#productCategorySelector #usedProductCategorySelect").val("");
                    $("#productCategorySelector #productCategorySelectorDiv").hide();

                    $("#productCategorySelector #_productCategorySelectedId").val("");
                    $("#productCategorySelector #_productCategorySelectedName").val("");
                    $("#productCategorySelector #_productCategorySelectedType").val("");
                    $("#productCategorySelector #_productCategorySelectedInfo").text("");
                    $("#productCategorySelector #searchResultCount").text("0");
                    if(G.isEmpty(data)){
                        $("#productCategorySelector #hasSearchResultDiv").hide();
                        $("#productCategorySelector #hasNotSearchResultDiv").show();
                    }else{
                        var searchResultHtml = "<ul>";
                        $.each(data,function(index,val){
                            var itemData={};
                            itemData["firstCategoryName"] = val["firstCategoryName"];
                            itemData["firstCategoryId"] = val["firstCategoryIdStr"];
                            itemData["secondCategoryName"] = val["secondCategoryName"];
                            itemData["secondCategoryId"] = val["secondCategoryIdStr"];
                            itemData["name"] = val["name"];
                            itemData["id"] = val["idStr"];
                            var itemTextHtml = (index+1)+"&nbsp;&nbsp;&nbsp;"+val["firstCategoryName"]+" >> <span class='J_highlight'>"+val["secondCategoryName"];
                            if("THIRD_CATEGORY" == val["categoryType"]){
                                itemData["thirdCategoryName"] = val["thirdCategoryName"];
                                itemData["thirdCategoryId"] = val["thirdCategoryIdStr"];
                                itemTextHtml += " >> "+val["thirdCategoryName"];
                            }
                            itemTextHtml +="</span>";

                            searchResultHtml += "<li class='item-li J_productCategoryItemData' item-data='"+encodeURIComponent(JSON.stringify(itemData))+"'>";
                            searchResultHtml +=itemTextHtml;
                            searchResultHtml +="</li>"
                        });
                        searchResultHtml +="</ul>";
                        $("#productCategorySelector #searchResultCount").text(data.length);
                        $("#productCategorySelector #searchResultDiv").html(searchResultHtml);
                        $("#productCategorySelector .J_highlight").highlight(searchWord, {className: "yellow_color"});
                        $("#productCategorySelector #hasNotSearchResultDiv").hide();
                        $("#productCategorySelector #hasSearchResultDiv").show();
                    }
                },
                error:function(){
                    nsDialog.jAlert("网络异常！");
                }
            });
        });
        //搜索结果单击
        $("#productCategorySelector .J_productCategoryItemData").live("click",function(e){
            e.preventDefault();
            var selectDataStr = $(this).attr("item-data");
            var selectData = JSON.parse(decodeURIComponent(selectDataStr));
            $("#productCategorySelector #_productCategorySelectedId").val(selectData["id"]);
            $("#productCategorySelector #_productCategorySelectedName").val(selectData["name"]);
            $("#productCategorySelector #_productCategorySelectedType").val(selectData["categoryType"]);
            var selectDataInfo = selectData["firstCategoryName"]+" >> "+selectData["secondCategoryName"];
            if(G.isNotEmpty(selectData["thirdCategoryName"])){
                selectDataInfo +=" >> "+selectData["thirdCategoryName"];
            }
            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
            $(this).parent("ul").find("li").removeClass("actived");
            $(this).addClass("actived");
        });
        //搜索结果双击
        $("#productCategorySelector .J_productCategoryItemData").live("dblclick",function(e){
            e.preventDefault();
            var selectDataStr = $(this).attr("item-data");
            var selectData = JSON.parse(decodeURIComponent(selectDataStr));
            $("#productCategorySelector #_productCategorySelectedId").val(selectData["id"]);
            $("#productCategorySelector #_productCategorySelectedName").val(selectData["name"]);
            $("#productCategorySelector #_productCategorySelectedType").val(selectData["categoryType"]);
            var selectDataInfo = selectData["firstCategoryName"]+" >> "+selectData["secondCategoryName"];
            if(G.isNotEmpty(selectData["thirdCategoryName"])){
                selectDataInfo +=" >> "+selectData["thirdCategoryName"];
            }
            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
            if($.isFunction(window.batchSetSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject) && $currentClickBtnObject.hasClass("batchSetProductCategory")){
                batchSetSelectedProductCategoryData(selectDataInfo,selectData["id"],selectData["name"],selectData["categoryType"],$currentClickBtnObject.closest("form"));
            }else if($.isFunction(window.setSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject)){
                setSelectedProductCategoryData(selectDataInfo,selectData["id"],selectData["name"],selectData["categoryType"],$currentClickBtnObject.parent());
            }
            $("#productCategorySelector").dialog("close");
        });
        //确定按钮
        $("#productCategorySelector #productCategorySelectorConfirmBtn").bind("click",function(e){
            e.preventDefault();
            if($("#productCategorySelector #productCategorySelectorConfirmBtn").attr("locked")=="true"){
                return;
            }
            var selectDataInfo = $("#productCategorySelector #_productCategorySelectedInfo").text();
            var categoryId = $("#productCategorySelector #_productCategorySelectedId").val();
            var categoryName = $("#productCategorySelector #_productCategorySelectedName").val();
            var categoryType = $("#productCategorySelector #_productCategorySelectedType").val();
            if(!G.isEmpty(categoryName)){
                if($.isFunction(window.batchSetSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject) && $currentClickBtnObject.hasClass("batchSetProductCategory")){
                    batchSetSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType,$currentClickBtnObject.closest("form"));
                }else if($.isFunction(window.setSelectedProductCategoryData) && G.isNotEmpty($currentClickBtnObject)){
                    setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType,$currentClickBtnObject.parent());
                }
            }else{
                nsDialog.jAlert("请选择二级或者三级商品分类！");
                return;
            }
            $("#productCategorySelector").dialog("close");
        });

        $("#productCategorySelector #categoryName").bind("blur",function(e){
            var categoryName = $(this).val().replace(/[\ |\\]/g, "");
            $("#productCategorySelector #productCategorySelectorConfirmBtn").attr("locked","true");
            if(!G.isEmpty(categoryName)){
                APP_BCGOGO.Net.syncAjax({
                    url: "searchInventoryIndex.do?method=getProductCategoryExactByName",
                    type: "POST",
                    data:{name:categoryName},
                    dataType: "json",
                    success:function (data) {
                        if(!G.isEmpty(data)){
                            if("FIRST_CATEGORY" == data["categoryType"]){
                                $("#productCategorySelector #_productCategorySelectedId").val("");
                                $("#productCategorySelector #_productCategorySelectedName").val("");
                                $("#productCategorySelector #_productCategorySelectedType").val("");
                                $("#productCategorySelector #_productCategorySelectedInfo").text("");
                                nsDialog.jConfirm("您输入的内容为标准一级分类，是否返回到标准分类重新选择？",null,function(flag){
                                    if(flag){
                                        resetProductCategorySelector();
                                        $("#productCategorySelector #_productCategorySelectedInfo").text(categoryName);
                                        categorySelector.reset({"level_1_id":data["idStr"]},1);
                                        $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                                    }else{
                                        $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                                    }
                                });
                            }else if("SECOND_CATEGORY" == data["categoryType"]){
                                $("#productCategorySelector #_productCategorySelectedId").val(data["idStr"]);
                                $("#productCategorySelector #_productCategorySelectedName").val(data["name"]);
                                $("#productCategorySelector #_productCategorySelectedType").val(data["categoryType"]);
                                var selectDataInfo = data["firstCategoryName"]+" >> "+data["secondCategoryName"];
                                $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                                $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                            }else if("THIRD_CATEGORY" == data["categoryType"]){
                                $("#productCategorySelector #_productCategorySelectedId").val(data["idStr"]);
                                $("#productCategorySelector #_productCategorySelectedName").val(data["name"]);
                                $("#productCategorySelector #_productCategorySelectedType").val(data["categoryType"]);
                                var selectDataInfo = data["firstCategoryName"]+" >> "+data["secondCategoryName"]+" >> "+data["thirdCategoryName"];
                                $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                                $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                            }else{
                                $("#productCategorySelector #_productCategorySelectedId").val("");
                                $("#productCategorySelector #_productCategorySelectedName").val(categoryName);
                                $("#productCategorySelector #_productCategorySelectedType").val("");
                                var selectDataInfo = "其他 >> "+categoryName;
                                $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                                $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                            }
                        }else{
                            $("#productCategorySelector #_productCategorySelectedId").val("");
                            $("#productCategorySelector #_productCategorySelectedName").val(categoryName);
                            $("#productCategorySelector #_productCategorySelectedType").val("");
                            var selectDataInfo = "其他 >> "+categoryName;
                            $("#productCategorySelector #_productCategorySelectedInfo").text(selectDataInfo);
                            $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                        }
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                        $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
                    }
                });
            }else{
                $("#productCategorySelector #_productCategorySelectedId").val("");
                $("#productCategorySelector #_productCategorySelectedName").val("");
                $("#productCategorySelector #_productCategorySelectedType").val("");
                $("#productCategorySelector #_productCategorySelectedInfo").text("");
                $("#productCategorySelector #productCategorySelectorConfirmBtn").removeAttr("locked");
            }
        });

        //关闭 返回分类
        $("#productCategorySelector .J_closeBackProductCategory").bind("click",function(e){
            e.preventDefault();
            resetProductCategorySelector();
            categorySelector.reset();
        });
        //最近使用选择
        $("#productCategorySelector #usedProductCategorySelect").bind("change",function(e){
            e.preventDefault();
            categorySelector.reset();
            if(G.isNotEmpty($(this).val())){
                var $selectOption =$(this).find("option:selected");
                $("#productCategorySelector #_productCategorySelectedId").val($(this).val());
                $("#productCategorySelector #_productCategorySelectedName").val($selectOption.attr("data-name"));
                $("#productCategorySelector #_productCategorySelectedType").val($selectOption.attr("data-type"));
                $("#productCategorySelector #_productCategorySelectedInfo").text($selectOption.text());
            }else{
                $("#productCategorySelector #_productCategorySelectedId").val("");
                $("#productCategorySelector #_productCategorySelectedName").val("");
                $("#productCategorySelector #_productCategorySelectedType").val("");
                $("#productCategorySelector #_productCategorySelectedInfo").text("");
            }
        });
        function resetProductCategorySelector(){
            $("#productCategorySelector #searchWord").val("");
            $("#productCategorySelector #_productCategorySelectedId").val("");
            $("#productCategorySelector #_productCategorySelectedName").val("");
            $("#productCategorySelector #_productCategorySelectedType").val("");
            $("#productCategorySelector #_productCategorySelectedInfo").text("");
            $("#productCategorySelector #hasNotSearchResultDiv").hide();
            $("#productCategorySelector #categoryName").val("");
            $("#productCategorySelector #hasSearchResultDiv").hide();
            $("#productCategorySelector #searchResultDiv").html("");
            $("#productCategorySelector #searchResultCount").text("0");
            $("#productCategorySelector #productCategorySelectorDiv").show();
            $("#productCategorySelector #usedProductCategorySelect").val("");
        }
    });
</script>
<div class="order-container" id="productCategorySelector" style="display: none">
    <div class="order-content">
        <p></p>
        <table width="100%" border="0" class="order-table" id="searchProductCategoryTable">
            <tr>
                <th style="width:100px">商品分类搜索</th>
                <td style="width:270px"><input id="searchWord" style="font-size:12px;line-height:22px;height:22px" type="text"  class="txt"/></td>
                <td><div class="add_search"><a class="add_search" id="searchProductCategoryBtn" style="cursor: pointer">快速找到分类</a></div></td>
            </tr>
        </table>
        <div class="classification-content"></div>
        <table width="100%" border="0" class="order-table">
            <tr>
                <th style="width:100px">您最近使用的分类</th>
                <td style="width:270px">
                    <select id="usedProductCategorySelect" style="font-size:12px;line-height:22px;height:22px" class="txt">
                        <option value="">----请选择----</option>
                        <c:forEach var="usedProductCategoryDTO" items="${usedProductCategoryDTOList}">
                            <option value="${usedProductCategoryDTO.idStr}" data-name="${usedProductCategoryDTO.name}" data-type="${usedProductCategoryDTO.categoryType}">${usedProductCategoryDTO.firstCategoryName} >> ${usedProductCategoryDTO.secondCategoryName}<c:if test="${not empty usedProductCategoryDTO.thirdCategoryName}"> >> ${usedProductCategoryDTO.thirdCategoryName}</c:if></option>
                        </c:forEach>
                    </select>
                </td>
                <td>
                </td>
            </tr>
        </table>
        <div id="productCategorySelectorDiv" style="height: 300px"></div>
        <div id="hasSearchResultDiv" style="width:540px;height:300px;height: 300px;display: block;border:1px solid #ccc; padding:6px;display: none">
            <div style="width: 100%;height: 22px"><strong>匹配到<span class="yellow_color" id="searchResultCount">0</span>个商品分类</strong><span class="gay_color">（双击商品分类可直接进入下一步哦！）</span><div class="gray550 J_closeBackProductCategory" style="padding:0 0 0 8px;line-height:24px;margin:0;width:100px;float: right;cursor: pointer">关闭,返回分类</div></div>
            <div id="searchResultDiv" style="width: 100%;height: 270px;overflow-y:scroll;border:1px solid #ccc;"></div>
        </div>
        <div id="hasNotSearchResultDiv" style="width:540px;height:300px;height: 300px;display: block;border:1px solid #ccc; padding:6px;display: none">
            对不起，未找到您所需的上架分类，请在此输入商品品名:<input id="categoryName" style="font-size:12px;width:110px;line-height:22px;height:22px" type="text" class="txt"/>
            <div class="gray550 J_closeBackProductCategory" style="padding:0 0 0 8px;line-height:24px;margin:0;width:100px;float: right;cursor: pointer">关闭,返回分类</div>
        </div>
        <input type="hidden" id="_productCategorySelectedId"/>
        <input type="hidden" id="_productCategorySelectedName"/>
        <input type="hidden" id="_productCategorySelectedType"/>
        <div class="gray550" style="padding: 0 5px;width: auto">您当前选择的是：<span id="_productCategorySelectedInfo"></span></div>
        <div class="padding10">
            <a class="confirm-button" id="productCategorySelectorConfirmBtn">确定</a>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
</div>
</body>
