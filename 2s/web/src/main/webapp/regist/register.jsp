<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-9-10
  Time: 下午1:41
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>注册</title>

<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.css" />
<link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    .tabRegister td {
        text-align: left;
    }

</style>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=nh73VgKTDOS1LnxhSPvpz9DM"></script>

<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/register/register<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/register/standardVehicleBrandModel<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/businessScope<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-qqInvoker<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialogTree<%=ConfigController.getBuildVersion()%>.js"></script>
<%@ include file="/WEB-INF/views/image_script.jsp" %>
<script type="text/javascript" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>
<%--.......................................................................................................................--%>

<script type="text/javascript">
APP_BCGOGO.Permission.Version.WholesalerVersion=${empty isWholesalerVersion?false:isWholesalerVersion};
var shopVersionMap={
    "WASH_SHOP":"SHOP",
    "REPAIR_SHOP":"SHOP",
    "INTEGRATED_SHOP":"SHOP",
    "TXN_SHOP":"SHOP",
    "WHOLESALER_SHOP":"WHOLESALER",
    "LARGE_WHOLESALER_SHOP":"WHOLESALER",
    "SMALL_WHOLESALER_SHOP":"WHOLESALER"
};
//样式
$(function(){
    $(".tabRegisterInfo tr").not(".tabTitle").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tabRegisterInfo tr:nth-child(odd)").not(".tabTitle").css("background","#eaeaea");

    $(".tabRegisterInfo tr").not(".tabTitle").hover(
            function () {
                $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px"});

                $(this).css("cursor","pointer");
            },
            function () {
                $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px"});
                $(".tabRegisterInfo tr:nth-child(odd)").not(".tabTitle" ).find("td").css("background","#eaeaea");
            }
    );

    $("#tabInfo tr:gt(0)").not("#tabInfo tr:eq(7)").not("#tabInfo tr:last-child").hide();
    $(".more").toggle(
            function(){
                $("#tabInfo tr").show();
                $(".more").html("收起");
                $(".more").css("background-image","url(../images/upArrow.png)");
            },
            function(){
                $("#tabInfo tr:gt(0)").not("#tabInfo tr:eq(7)").not("#tabInfo tr:last-child").hide();
                $(".more").html("更多");
                $(".more").css("background-image","url(../images/downArrow.png)");
            }
    );
    $("#tab_new tr:gt(2)").not("#tab_new tr:eq(5)").not("#tab_new tr:last-child").hide();
    $(".add_new").click(
            function(){
                //$("#tab_new tr:lt(4)").not("#tab_new tr:lt(2)").clone().insertAfter("#tab_new tr:eq(3)");
                $("#tab_new tr:gt(2)").show();
            }
    );
});
//图片
$(document).ready(function(){
    var shopImageUrlData = [];
    var imageUploader = new App.Module.ImageUploader();
    imageUploader.init({
        "selector":"#shop_imageUploader",
        "flashvars":{
            "debug":"off",
            "maxFileNum":4,
            "width":61,
            "height":24,
            "buttonBgUrl":"images/imageUploader.png",
            "buttonOverBgUrl":"images/imageUploader.png",
            "url":APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL+"/"+APP_BCGOGO.UpYun.UP_YUN_BUCKET+"/",
            "ext":{
                "policy":$("#policy").val(),
                "signature":$("#signature").val()
            }
        },

        "startUploadCallback":function(message) {
            // 设置 视图组件 uploading 状态
            imageUploaderView.setState("uploading");
        },
        "uploadCompleteCallback":function(message, data) {
            var dataInfoJson = JSON.parse(data.info);
            shopImageUrlData.push(dataInfoJson.url);
        },
        "uploadErrorCallback":function(message, data) {
            var errorData = JSON.parse(data.info);
            errorData["content"] = data.info;
            saveUpLoadImageErrorInfo(errorData);
            nsDialog.jAlert("上传图片失败！");
        },
        "uploadAllCompleteCallback":function() {
            //更新input
            $("input[id^='imageCenterDTO.shopImagePaths']").each(function(index){
                $(this).val(G.Lang.normalize(shopImageUrlData[index],""));
            });
            // 设置 视图组件  idle 状态
            imageUploaderView.setState("idle");
            imageUploaderView.update(getTotelUrlToData(shopImageUrlData));
        }
    });

    /**
     * 视图组建的 样例代码
     * */
    var imageUploaderView = new App.Module.ImageUploaderView();
    imageUploaderView.init({
        // 你所需要注入的 dom 节点
        selector:"#shop_imageUploaderView",
        width:540,
        height:160,
        iWidth:100,
        iHeight:80,
        maxFileNum:4,
        // 当删除某张图片时会触发此回调
        onDelete: function (event, data, index) {
            imageUploader.getFlashObject().deleteFile(index);

            // 从已获得的图片数据池中删除 图片数据
            shopImageUrlData.splice(index, 1);
            //更新input

            $("input[id^='imageCenterDTO.shopImagePaths']").each(function(index){
                $(this).val(G.Lang.normalize(shopImageUrlData[index],""));
            });
            // 设置 视图组件  idle 状态
            imageUploaderView.setState("idle");
            imageUploaderView.update(getTotelUrlToData(shopImageUrlData));
        }
    });
    function getTotelUrlToData(inUrlData) {
        var outData = [];
        for (var i = 0; i < inUrlData.length; i++) {
            var outDataItem = {
                "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + inUrlData[i] + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.SHOP_REGISTER,
                "name": i==0?"*主图":"辅图"
            };
            outData.push(outDataItem);
        }
        return outData;
    };

    var shopBusinessLicenseImageUploader = new App.Module.ImageUploader();
    shopBusinessLicenseImageUploader.init({
        "selector":"#shopBusinessLicenseImageUploader",
        "flashvars":{
            "debug":"off",
            "maxFileNum":1,
            "width":90,
            "height":24,
            "buttonBgUrl":"images/upload-btn1.jpg",
            "buttonOverBgUrl":"images/upload-btn1.jpg",
            "url":APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL+"/"+APP_BCGOGO.UpYun.UP_YUN_BUCKET+"/",
            "ext":{
                "policy":$("#policy").val(),
                "signature":$("#signature").val()
            }
        },

        "startUploadCallback":function(message) {
            // 设置 视图组件 uploading 状态
            businessLicenseImageUploaderView.setState("uploading");
        },
        "uploadCompleteCallback":function(message, data) {
            var dataInfoJson = JSON.parse(data.info);
            $("#imageCenterDTO\\.shopBusinessLicenseImagePath").val(dataInfoJson.url);
        },
        "uploadErrorCallback":function(message, data) {
            var errorData = JSON.parse(data.info);
            errorData["content"] = data.info;
            saveUpLoadImageErrorInfo(errorData);
            nsDialog.jAlert("上传图片失败！");
        },
        "uploadAllCompleteCallback":function() {
            businessLicenseImageUploaderView.setState("idle");
            var outData = [{
                "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + $("#imageCenterDTO\\.shopBusinessLicenseImagePath").val() + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.SHOP_REGISTER
            }];
            businessLicenseImageUploaderView.update(outData);
        }
    });
    /**
     * 视图组建的 样例代码
     * */
    var businessLicenseImageUploaderView = new App.Module.ImageUploaderView();
    businessLicenseImageUploaderView.init({
        // 你所需要注入的 dom 节点
        selector:"#shopBusinessLicenseImageUploaderView",
        width:285,
        height:182,
        iWidth:285,
        iHeight:180,
        paddingTop:0,
        paddingBottom:0,
        paddingLeft:0,
        paddingRight:0,
        horizontalGap:0,
        verticalGap:0,
        waitingInfo:"加载中...",
        showWaitingImage:false,
        maxFileNum:1,
        // 当删除某张图片时会触发此回调
        onDelete: function (event, data, index) {
            shopBusinessLicenseImageUploader.getFlashObject().deleteFile(index);

            // 从已获得的图片数据池中删除 图片数据
            $("#imageCenterDTO\\.shopBusinessLicenseImagePath").val("");
        }
    });

    if($("#registerType").val()=="SALES_REGISTER"){
        $("#shopVersionSelector").live("change",function(){
            APP_BCGOGO.Permission.Version.WholesalerVersion=(shopVersionMap[$(this).find("option:selected").attr("versionType")]=='WHOLESALER');
            drawPageByVersion(APP_BCGOGO.Permission.Version.WholesalerVersion);
            $("#shopVersionId").val($(this).val());
        })
        $("#shopVersionSelector").val($("#shopVersionId").val());
        $("#shopVersionSelector").change();
    }else if($("#registerType").val()=="SUPPLIER_REGISTER"){
        var province = "${customerDTO.province}";
        var city = "${customerDTO.city}";
        var region = "${customerDTO.region}";
        if(!G.isEmpty(province)){
            $("#select_province").val(province).change();
            if(!G.isEmpty(city)){
                $("#select_city").val(city).change();
            }
            if(!G.isEmpty(region)){
                $("#select_township").val(region).change();
            }
        }
    }

    if ($("#businessScopeDiv").length > 0) {
        var multiSelectTwoDialogTree = new App.Module.MultiSelectTwoDialogTree();
        App.namespace("components.multiSelectTwoDialogTree");
        App.components.multiSelectTwoDialogTree = multiSelectTwoDialogTree;
        APP_BCGOGO.Net.asyncPost({
            url:"businessScope.do?method=getAllBusinessScope",
            success:function(data){
                if (G.isEmpty(data)) {
                    return;
                }
                var ensureDataList = [];
                if(!G.Lang.isEmpty($("#thirdCategoryNodeListJson").val())){
                    ensureDataList = JSON.parse(decodeURIComponent(G.Lang.normalize($("#thirdCategoryNodeListJson").val())));
                }
                multiSelectTwoDialogTree.init({
                    "startLevel":2,
                    "data": data,
                    "ensureDataList":ensureDataList,
                    "selector": "#businessScopeDiv",
                    "onSearch":function(searchWord, event) {
                        return App.Net.syncPost({
                            url:"businessScope.do?method=getAllBusinessScope",
                            data:{"searchWord":searchWord},
                            dataType:"json"
                        });
                    },
                    "onDelete":function(itemData, event) {
//                        console.log(itemData);
                        if(G.isEmpty(multiSelectTwoDialogTree.getAddedTreeNodeDataList())){
                            $("[node-type='businessScopes_tips']").html(register['businessScopes'].empty);
                        }else{
                            $("[node-type='businessScopes_tips']").html(register['businessScopes'].right);
                        }
                    },
                    "onSelect":function(itemData, event) {
                        if(G.isEmpty(multiSelectTwoDialogTree.getAddedTreeNodeDataList())){
                            $("[node-type='businessScopes_tips']").html(register['businessScopes'].empty);
                        }else{
                            $("[node-type='businessScopes_tips']").html(register['businessScopes'].right);
                        }
                    },
                    "onClear":function(event) {
                        $("[node-type='businessScopes_tips']").html(register['businessScopes'].empty);

                    }
                });


            }
        });
    }
    drawPageByVersion(APP_BCGOGO.Permission.Version.WholesalerVersion);
    initServiceCategory();
    initQQTalk($(".register-qq"));
});

function drawPageByVersion(isWholesalerVersion){
    if(isWholesalerVersion){
        $(".service-category-div").hide();
        $(".agent-product-div").hide();
    }else{
        $(".service-category-div").show();
        $(".agent-product-div").show();
    }
    reset4SShopVersion();
}



</script>


</head>
<body class="bodyMain">
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<div class="clear m_topMain">
    <div class="l_top">
        <div class="l_topBorder"></div>
        <div class="home"></div>
        <div class="l_topBorder"></div>
        <div class="l_topTitle">欢迎您注册一发EasyPower软件</div>
        <div class="l_topBorder"></div>
        <div class="downloadClient"><a href="http://mail.bcgogo.com:8088/client32/" target="_blank">客户端下载</a></div>
    </div>
</div>

<div class="i_main clear">
<div id="register">
<h1 class="user">用户注册</h1>
<%--<input name="" type="button" class="register-qq" />--%>
<a class="register-qq" ></a>
<div class="register-phone"></div>
<form:form id="registerShopForm" commandName="shopDTO" method="post" action="shopRegister.do?method=saveShopInfo">
<input type="hidden" id="shopVersionId" name="shopVersionId" value="${toRegisterShopVersionId}">
<input type="hidden" id="registerShopType" name="registerShopType" value="${registerType}">
<input type="hidden" name="agent" id="agent" value="${userDTO.name}"/>
<input type="hidden" name="agentId" id="agentId" value="${userDTO.userNo}"/>
<input type="hidden" id="fourSShopVersion" value="${fourSShopVersions}">
<%--<input type="hidden" name="agentMobile" id="agentMobile" value="${userDTO.mobile}"/>--%>

<input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="">

<div class="register-content register-content-base">
    <div class=" clear i_height"></div>
    <div class="step-01">
        <ul>
            <li><span>1、填写基本信息</span></li>
            <li>2、经营范围</li>
            <li>3、店铺照片</li>
        </ul>
    </div>
    <div class="title"> <span>基本信息</span> </div>
    <div class="clear"></div>
    <div class="i_height"></div>
        <%--<input type="hidden" id="from_source" value="${from}" />--%>
    <input type="hidden" id="registerType" value="${registerType}" />
    <table cellpadding="0" cellspacing="0" class="tabRegister">
        <colgroup>
            <col width="100">
            <col width="100">
            <col width="100">
            <col >
            <col width="180">
        </colgroup>
        <c:if test="${registerType=='SUPPLIER'||registerType=='CUSTOMER'}">
            <tr>
                <td><span class="red_color">*</span> 邀请码</td>
                <td colspan="3">
                    <input type="text" class="txt gray_color" placeholder="请输入邀请码" maxlength="6"
                           id="invitationCode" name="invitationCode" node-type="invitationCode"/>
                <td node-type="invitationCode_tips" style="text-align:left;">
                </td>
            </tr>
        </c:if>
        <c:if test="${registerType=='SALES_REGISTER'}">
            <tr>
                <td><span class="red_color">*</span> 软件版本</td>
                <td colspan="3">
                    <select id="shopVersionSelector" class="txt" style="width: 100px">
                        <option value="">--请选择--</option>
                        <c:forEach var='shopVersionDTO' items='${shopVersionDTOList}'>
                            <option value="${shopVersionDTO.id}" versionType="${shopVersionDTO.name}">${shopVersionDTO.value}</option>
                        </c:forEach>
                    </select>
                </td>
                <td style="text-align:left;">&nbsp;</td>
            </tr>
        </c:if>
        <tr>
            <td><span class="red_color">* </span>单位名称</td>
            <td colspan="3"><input id="input_name" class="txt" autocomplete="off" type="text" node-type="name" name="name" maxlength="25"/></td>
            <td style="text-align:left;" node-type="name_tips"></td>
        </tr>
        <tr>
            <td><span class="red_color">*</span> 地址</td>
            <td colspan="3" style="padding:0px;">
                <select  id="select_province" name="province" class="txt" style="width:85px; margin-right:8px;">
                    <option value="">省份</option>
                </select>
                <select id="select_city"  name="city" class="txt" style="width:89px; margin-right:8px;">
                    <option value="">城市</option>
                </select>
                <select id="select_township"  name="region" class="txt" style="width:90px; ">
                    <option value="">区</option>
                </select>
            </td>
            <td style="text-align:left;" node-type="address_tips"></td>
        </tr>
        <tr class="description">
            <td></td>
            <td colspan="3">
                <input id="input_areaId" type="hidden" name="areaId" value="">
                <input id="input_address" type="hidden" name="address"/>
                <input id="input_coordinateLon" type="hidden" name="coordinateLon"/>
                <input id="input_coordinateLat" type="hidden" name="coordinateLat"/>
                <input id="input_address_detail" class="txt" type="text"  maxlength="50" node-type="address"  maxlength="50" name="detailAddress"/>
            </td>
            <td style="text-align:left;"><a class="map_position" style="cursor: pointer;"  <%--action="searchInMap"--%>></a><span style="cursor: pointer;" class="red_color font12" <%--action="searchInMap"--%> id="searchInMapTxt">定位</span></td>
        </tr>
        <tr>
            <td>网址</td>
            <td colspan="3"><input id="input_url" class="txt" type="text" maxlength="75" name="url" value=""/></td>
            <td></td>
        </tr>
        <tr>
            <td>经营方式</td>
            <td width="105">
                <select class="txt" name="operationMode" id="operationMode">
                    <option value="">-请选择-</option>
                    <option value="加盟连锁">加盟连锁</option>
                    <option value="有限公司">有限公司</option>
                    <option value="专卖店">专卖店</option>
                    <option value="个体">个体</option>
                    <option value="其他">其他</option>
                </select>
            </td>
            <td colspan="2">
                <input type="text" class="txt gray_color" placeholder="请输入品牌"  style="display: none;color: #999999"
                       id="operationModeBrand" name="operationModeBrand"/>
            </td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>所在地车牌</td>
            <td colspan="3"><input id="licencePlate" class="txt" type="text" maxlength="2" name="licencePlate" value=""/></td>
            <td></td>
        </tr>
        <tr>
            <td>固定电话</td>
            <td width="105"><input class="txt" type="text" maxlength="20" name="landline" d="input_landLine" value=""/></td>
            <td  style="text-align:left" colspan="2"><span class="gray_color font12">（例如：0512-12345678）</span></td>
            <td></td>
        </tr>
        <c:if test="${registerType=='SUPPLIER_REGISTER'}">
            <tr>
                <td><span class="red_color">*</span>本店业务员</td>
                <td colspan="3"><input id="input_agent" class="txt" type="text" value="${userDTO.name}" name="agent" node-type="agent" maxlength="10"></td>
                <td node-type="agent_tips" style="text-align:left;"></td>
            </tr>
            <tr>
                <td><span class="red_color">*</span>本店业务员联系方式</td>
                <td colspan="3">
                    <input class="txt" type="text" value="${userDTO.mobile}" id="input_agentMobile" name="agentMobile"
                           maxlength="11" node-type="agentMobile">
                </td>
                <td node-type="agentMobile_tips" style="text-align:left;"></td>
            </tr>
        </c:if>
        <c:if test="${registerType=='SALES_REGISTER'}">
            <tr>
                <td><span class="red_color">*</span>店面管理员</td>
                <td colspan="3">
                    <input id="input_storeManager" name="storeManager" type="text" class="txt" node-type="storeManager">
                </td>
                <td node-type="storeManager_tips" style="text-align:left"></td>
            </tr>
            <tr>
                <td><span class="red_color">*</span>店面管理员手机</td>
                <td colspan="3">
                    <input id="input_storeManagerMobile" name="storeManagerMobile" type="text" class="txt" node-type="storeManagerMobile" maxlength="11">
                </td>
                <td node-type="storeManagerMobile_tips" style="text-align:left"></td>
            </tr>
        </c:if>
    </table>
    <div class="height"></div>
    <div class="title J_4s_shop_hidden"><div class="add"><a id="addContact" >新增联系人</a></div><span>联系人</span> </div>
    <div class="clear J_4s_shop_hidden"></div>
    <div class="i_height J_4s_shop_hidden"></div>
    <div id="contactContainer" class="J_4s_shop_hidden">
        <c:forEach items="${contacts}" var="contact" varStatus="status">
            <c:if test="${contact!=null}">
                <table cellpadding="0" cellspacing="0" class="contact-info tabRegister">
                    <colgroup>
                        <col width="100">
                        <col width="100">
                        <col width="100">
                        <col >
                        <col width="165">
                    </colgroup>
                    <tr>
                        <td><span class="red_color">*</span>
                            <c:choose>
                                <c:when test="${contact.mainContact == 1}">
                                    负责人/店主
                                </c:when>
                                <c:otherwise>联系人</c:otherwise>
                            </c:choose>
                        </td>
                        <td colspan="3">
                            <input type="text" id="contacts${status.index}.name" value="${contact.name}" name="contacts[${status.index}].name" class="txt" node-type="<c:choose><c:when test="${contact.mainContact == 1}">contact_owner</c:when><c:otherwise>contact_name</c:otherwise></c:choose>">
                        </td>
                        <td style="text-align:left;" node-type="contact_owner_tips"></td>
                            <%--<td style="text-align:left;"><a class="wrong"></a> <span class="red_color font12">字数最多不超过10个字</span></td>--%>
                    </tr>
                    <tr>
                        <td><span class="red_color">*</span> 手机</td>
                        <td colspan="3">
                            <input type="text" class="txt" id="contacts${status.index}.mobile" value="${contact.mobile}" name="contacts[${status.index}].mobile" maxlength="11"
                                   node-type="<c:choose><c:when test="${contact.mainContact == 1}">contact_mobile</c:when><c:otherwise>contact_phone</c:otherwise></c:choose>">
                        </td>
                        <td style="text-align:left;" node-type="contact_mobile_tips"></td>
                    </tr>
                    <tr>
                        <td>EMALL</td>
                        <td colspan="3"><input type="text" class="txt" id="contacts${status.index}.email" value="${contact.email}" name="contacts[${status.index}].email" node-type="contact_email"></td>
                        <td style="text-align:left;" node-type="contact_email_tips"></td>
                    </tr>
                    <tr>
                        <td>QQ</td>
                        <td colspan="3"><input type="text" class="txt" id="contacts${status.index}.qq" value="${contact.qq}" name="contacts[${status.index}].qq" node-type="contact_qq"></td>
                        <td style="text-align:left;">
                            <div>
                                <span class="btnSure" style="padding-left:5px;"><a href="http://wp.qq.com/consult.html" target="_blank">免费开通QQ在线</a></span>
                                <span node-type="contact_qq_tips"></span>
                            </div>
                        </td>
                    </tr>
                </table>
            </c:if>
        </c:forEach>
    </div>
    <div class="line-dashed"></div>
    <div class="i_height"></div>

    <div class="register-bar"><input type="button" id="nextStep1" class="register-btn" value="下一步" /></div>

    <%---------------------------------------------------------------------------------------------------%>
     <div id="allmap" style="display: none;width: 80%;position:relative;top:-550px;height: 350px; left:70px;
                                 z-index:9999;border:5px solid green;" title="鼠标单击可手动定位">&nbsp;

    </div>
    <div id="closemap" style="width:30px;height:30px;border-radius:15px;background:blue;display:none;font-size:17px;
                                 position:relative;top:-865px;left:640px;text-align:center;cursor:pointer;
                                 line-height:30px;-moz-opacity:0.5;opacity:0.5;font-weight:bold;" title="关闭">
      ×
    </div>
    <div id="sureBtn" style="width:50px;height:20px;background:blue;display:none;
                                 position:relative;top:-630px;left:320px;text-align:center;cursor:pointer;
                                 line-height:20px;font-weight:bold;-moz-opacity:0.5;opacity:0.5;" title="确定">
      确定
    </div>
    <div id="clearBtn" style="width:50px;height:20px;background:blue;display:none;
                                 position:relative;top:-650px;left:390px;text-align:center;cursor:pointer;
                                 line-height:20px;font-weight:bold;-moz-opacity:0.5;opacity:0.5;" title="返回">
      取消
    </div>
    <div id="tips" style="width:180px;height:20px;background:blue;display:none;
                                 position:relative;top:-965px;left:290px;text-align:center;
                                 line-height:20px;font-weight:bold;-moz-opacity:0.3;opacity:0.3;">
      鼠标点击可手动定位
    </div>
    <%---------------------------------------------------------------------------------------------------%>
</div>
<div class="register-content register-content-business" style="display: none">
    <div class=" clear i_height"></div>
    <div class="step-02">
        <ul>
            <li>1、填写基本信息</li>
            <li><span>2、经营范围</span></li>
            <li>3、店铺照片</li>
        </ul>
    </div>
    <div class="title"> <span>经营范围</span> </div>
    <div class="clear"></div>
    <div class="i_height"></div>
    <div class="scopeBusiness">
        <div class="service-category-div">
            <div class="left select-t" align="right">
                <span class="red_color">* </span>服务范围&nbsp;
            </div>
            <div class="right">
                <span class="gray_color font12">（最少选择1种）</span>
                <span class="select-t" style="padding-left: 60px" node-type="serviceCategory_tips"></span>
            </div>
            <div class="right" style="margin-top: 5px">
                <div class="select-t service-category-container"></div>
            </div>
        </div>
        <div class="clear height"></div>
        <div>
            <div class="left select-t" align="right">主营车型&nbsp;</div>
            <div class="right">
                <div class="select-t">
                    <span><input type="radio" id="allBrandModel" value="allBrandModel" name="model_select"/>所有车型</span>
                    <span><input type="radio" id="partBrandModel" value="partBrandModel" name="model_select"/>部分车型</span>
                </div>
                <div id="vehicleBrandModelDiv" style="display:none;"></div>
            </div>
        </div>
        <div class="clear height J_4s_shop_hidden"></div>
        <div class="left select-t J_4s_shop_hidden" align="right"><span class="red_color">*</span> 经营产品&nbsp;</div>
        <div class="right J_4s_shop_hidden">
            <div class="select-t" node-type="businessScopes_tips"></div>
            <div id="businessScopeDiv"></div>
        </div>
        <div class="clear height J_4s_shop_hidden"></div>
        <div class="left select-t J_4s_shop_hidden" align="right"><span class="red_color">*</span> 主营产品&nbsp;</div>
        <div class="right J_4s_shop_hidden">
            <div class="select-t">
                <span class="gray_color font12">（最少5条，最多10条）</span>
                <span node-type="product_tips" class="red_color font12"></span>
            </div>
        </div>
        <div class="clear height J_4s_shop_hidden"></div>
        <div class="register-bg J_4s_shop_hidden">
            <table id="productTable" cellspacing="0" cellpadding="0" class="tab_cuSearch" border="0">
                <colgroup>
                    <col width="80">
                    <col>
                    <col width="80">
                    <col width="80">
                    <col width="80">
                    <col width="80">
                    <col width="80">
                    <col width="60">
                    <col width="60">
                </colgroup>
                <tbody>
                <tr class="titleBg">
                    <td style="padding-left:10px;">商品编号</td>
                    <td>品名</td>
                    <td>品牌</td>
                    <td>规格</td>
                    <td>型号</td>
                    <td>车牌</td>
                    <td>车型</td>
                    <td>单位</td>
                    <td>操作</td>
                </tr>
                <tr class="space"><td colspan="9"></td></tr>
                <tr class="productItem titBody_Bg">
                    <td style="padding-left:5px;"><input type="text" id="productDTOs0.commodityCode" name="productDTOs[0].commodityCode"
                                                         class="txt"/></td>
                    <td><input type="text" id="productDTOs0.name" name="productDTOs[0].name" class="txt"/></td>
                    <td><input type="text" id="productDTOs0.brand" name="productDTOs[0].brand" class="txt"/></td>
                    <td><input type="text" id="productDTOs0.spec" name="productDTOs[0].spec" class="txt"/></td>
                    <td><input type="text" id="productDTOs0.model" name="productDTOs[0].model" class="txt"/></td>
                    <td><input type="text" id="productDTOs0.productVehicleBrand"  searchField="brand" cacheField="productVehicleBrandSource" name="productDTOs[0].productVehicleBrand" class="txt"/>
                    </td>
                    <td><input type="text" id="productDTOs0.productVehicleModel" searchField="model" cacheField="productVehicleModelSource" name="productDTOs[0].productVehicleModel" class="txt"/>
                    </td>
                    <td><input type="text" id="productDTOs0.sellUnit" name="productDTOs[0].sellUnit" class="txt"/>
                    </td>
                    <td><a id="productDTOs0.deleteProduct" class="deleteProduct a-link">删除</a>&nbsp;<a id="productDTOs0.addNewProduct" class="addNewProduct a-link">新增</a></td>

                </tr>
                <tr class="titBottom_Bg"><td colspan="10"></td></tr>
                </tbody></table>
            <div class="clear"></div>
        </div>
        <div class="clear height"></div>
        <div class="agent-product-div">
            <div class="left select-t" align="right">代理产品&nbsp;</div>
            <div class="right">
                <div class="select-t">
                    <c:forEach items="${agentProducts}" var="agentProduct" varStatus="status">
                        <c:if test="${agentProduct!=null}">
                            <label class="lbl"><input class="agent-product-item" type="checkbox" value="${agentProduct.id}">${agentProduct.name}</label>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <div class="height"></div>
    <div class="register-bar"><input onclick="showStep('first')" type="button" class="register-btn2" value="上一步" /><input id="nextStep2" type="button" class="register-btn" value="下一步" /></div>
</div>


<div class="register-content register-content-image" style="display: none">
    <div class="step-03">
        <ul>
            <li>1、填写基本信息</li>
            <li>2、经营范围</li>
            <li><span>3、店铺照片</span></li>
        </ul>
    </div>
    <div class="clear"></div>
    <div class="scopeBusiness">
        <div class="tip-content">
            <div class="left">提示：</div>
            <div class="right">
                <%--1. 如果您要发布相关的图片，请点击以上按钮。<br />--%>
                 图片格式支持JPEG、JPG、PNG，大小不得超过5M。
                <%--3. 每张图片的大小不得超过5M。 <br />--%>
                <%--4. 您可以一次选择多张图片，最多上传 5 张图片。--%>
            </div>
        </div>
        <div class="clear height"></div>
    </div>
    <div class="title"> <span>营业执照</span> </div>
    <div class="clear"></div>
    <div class="uploadPhoto-content">
        <div id="shopBusinessLicenseImageUploaderView"></div>
        <input type="hidden" id="imageCenterDTO.shopBusinessLicenseImagePath" name="imageCenterDTO.shopBusinessLicenseImagePath"/>

        <div class="uploadPhoto-bar">
            <div id="shopBusinessLicenseImageUploader" class="uploadPhoto-btn"></div>
        </div>
    </div>
    <div class="title"> <span>店铺照片</span> </div>
    <div class="clear"></div>
    <div class="storePhotos">
        <div class="top">
            <div class="upload">选择本地图片：</div>
            <div style="float: left;width: 160px;height: 80px" id="shop_imageUploader"></div>
            <div class="clear"></div>
            <div>图片将显示在店铺介绍页面，其中主图将会在手机APP店铺展示页面供车主浏览哦！</div>
            <input type="hidden" id="imageCenterDTO.shopImagePaths0" name="imageCenterDTO.shopImagePaths[0]"/>
            <input type="hidden" id="imageCenterDTO.shopImagePaths1" name="imageCenterDTO.shopImagePaths[1]"/>
            <input type="hidden" id="imageCenterDTO.shopImagePaths2" name="imageCenterDTO.shopImagePaths[2]"/>
            <input type="hidden" id="imageCenterDTO.shopImagePaths3" name="imageCenterDTO.shopImagePaths[3]"/>
            <div id="shop_imageUploaderView" style="width:550px;height:180px;position: relative;"></div>
        </div>
            <%--<div class="bottom">共上传3张图片，正在上传第一张...</div>--%>
    </div>
    <div class="clear height"></div>
    <div class="title"> <span>其他信息</span> </div>
    <div class="clear"></div>
    <table cellpadding="0" cellspacing="0" class="tabRegister">
        <c:if test="${registerType=='SALES_REGISTER'}">
            <%--<tr>--%>
                <%--<td><span class="red_color">* </span>软件销售价</td>--%>
                <%--<td><input id="input_softPrice" class="txt" type="text" node-type="softPrice" name="softPrice" style="width:100px;"/><span></span></td>--%>
                <%--<td style="text-align:left;" node-type="softPrice_tips"></td>--%>
            <%--</tr>--%>
            <tr class="J_4s_shop_hidden">
                <td><span class="red_color">* </span>软件销售价：</td>
                <td style="width:90px;"><input type="radio" checked="true" name="chargeType" value="ONE_TIME" id="oneTime"/>一次性收费</td>
                <td><input id="input_softPrice"  type="text" node-type="softPrice" name="softPrice" style="width:100px;"/>元</td>
                <td style="text-align:left;" node-type="softPrice_tips"></td>
            </tr>
            <tr class="J_4s_shop_hidden">
                <td></td>
                <td style="width:90px;"><input type="radio" name="chargeType" value="YEARLY"/>按年收费</td>
                <td>（第1年免费，之后每年年费1000元）</td>
                <td></td>
            </tr>
        </c:if>
        <tr>
            <td width="15%" valign="top" style="padding-left:55px;">备注：</td>
            <td colspan="3"><textarea id="input_memo" name="memo"  maxlength="100" type="text" class="txt" style="height:100px; width:461px;margin-top: 5px;" ></textarea></td>
        </tr>
    </table>
    <div class="clear"></div>

    <div class="register-bar">
        <input type="button" value="上一步" class="register-btn2" onclick="showStep('second')">
        <input id="registerBtn" type="button" class="register-btn" value="确认注册" />
        <a class="blue_color" onclick="resetSoftVersion()" style="cursor: pointer">重新选择版本</a>
        <div class="clear height"></div>
        <div>
            <input id="licenseChk" type="checkbox" checked="true">
            <a class="blue_color font12 height" href="shop.do?method=shopagreement" target="_blank">
                《统购信息软件服务条款》</a>
        </div>
    </div>

</div>

</form:form>
</div>
</div>
<div style="width: 725px; margin: 0 auto; padding-top:100px;">
    <div class="lay-main lay-map" style="z-index: 4; position: absolute;display:none;padding-top:150px;" id="map_container_iframe_div">
        <div class="hd">
            <a action="close_map_container_iframe" class="close" style="right: 1px;top: 165px;"></a>
            <div class="lay-con" style="width: 710px;">
                <div class="map">
                    <div class="map-cont" style=" width:700px; height:380px; overflow: hidden;">
                        <iframe src="" id="map_container_iframe" style="width: 700px;height: 550px;" scrolling="no" frameborder="0" allowtransparency="true"></iframe>
                    </div>
                    <div class="padding10">
                        <input name="" type="button" action="close_map_container_iframe"  class="query-btn" value="确定"/>
                        <input name="" type="button" action="cancle_map_container_iframe" class="query-btn" value="取消"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"/>

</body>
</html>
