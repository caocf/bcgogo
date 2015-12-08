<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>用户注册</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.css" />

    <link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>


    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/register/register<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/register/standardVehicleBrandModel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/businessScope<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
          src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
          src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
          src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <%@ include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>

    <style type="text/css">

      .ui-autocomplete {
        max-height: 250px;
        overflow-y: auto;
        overflow-x: hidden;
      }
    </style>
    <script type="text/javascript">
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
                width:100,
                height:82,
                iWidth:100,
                iHeight:80,
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
        });
    </script>
</head>
<body class="bodyMain" style="width:100%;height: 100%">
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<div class="clear m_topMain">
    <div class="l_top">
        <div class="l_topBorder"></div>
        <div class="home"></div>
        <div class="l_topBorder"></div>
        <div class="l_topTitle_register">欢迎您注册一发EasyPower软件</div>
        <div class="l_topBorder"></div>

    </div>
</div>

<div class="i_main clear register_image">
     <h1 class="user">用户注册</h1>

    <div class="height"></div>
    <div class="leftBody">
    <form id="form_reg" method="post" enctype="multipart/form-data" action="shopRegister.do?method=saveShopInfo">
        <input type="hidden" id="shopVersionId" name="shopVersionId" value="${param.shopVersionId}">
        <input type="hidden" id="registerShopType" name="shopVersionId" value="${param.registerShopType}">
        <input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="">

        <table cellpadding="0" cellspacing="0" class="tabRegister">
            <col width="80">
            <col width="80">
            <col>
            <col width="80">
            <col width="165">
            <tr class="regTitle">
                <td colspan="5">基本信息</td>
            </tr>
            <tr>
                <td>单位名称<span class="red_color">*</span></td>
                <td colspan="3">
                    <input type="text" class="txt" maxlength="25" id="input_name" name="name" node-type="name"/>
                </td>
                <td style="text-align:left;">
                    <div class="tips" node-type="name_tips">
                    </div>
                </td>
            </tr>
            <tr>
                <td>地址<span class="red_color">*</span></td>
                <td colspan="3" style="padding:0px;">
                    <select id="select_province" name="province" class="txt" style="width:65px; margin-right:8px;padding-left: 0px">
                        <option value="">省份</option>
                    </select>
                    <select id="select_city" name="city" class="txt" style="width:69px; margin-right:8px;padding-left: 0px">
                        <option value="">城市</option>
                    </select>
                    <select id="select_township" name="region"  class="txt" style="width:70px; padding-left: 0px">
                        <option value="">区</option>
                    </select>
                </td>
                <td style="text-align:left;line-height: 15px">
                    <div class="tips" node-type="address_tips">
                    </div>
                </td>
            </tr>
            <tr class="registerDescription">
                <td></td>
                <td colspan="3">
                    <input id="input_areaId" type="hidden" value="" name="areaId">
                    <input id="input_address" name = "address" type="text" class="txt" maxlength="50"
                           style="border-width:0px 0px 1px 0px; margin-top:-10px;" node-type="address"/>
                </td>
                <td style="text-align:left;">
                </td>
            </tr>
            <%--<tr>--%>
                <%--<td style="vertical-align:top;">经营产品<span class="red_color">*</span></td>--%>
                <%--<td colspan="4" class="tabChk">--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="发动机" node-type="businessScopes"/>发动机</label>--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="底盘及车身" node-type="businessScopes"/>底盘及车身</label>--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="电器" node-type="businessScopes"/>电器</label>--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="材料及通用件" node-type="businessScopes"/>材料及通用件</label>--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="汽保设备及工具" node-type="businessScopes"/>汽保设备及工具</label>--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="油品(油品、油脂、添加剂)" node-type="businessScopes"/>油品(油品、油脂、添加剂)</label>--%>
                    <%--<label class="lbl"><input type="checkbox" name="businessScopes" value="汽车用品(美容护理、坐垫脚垫、汽车电子、汽车精品)" node-type="businessScopes"/>汽车用品(美容护理、坐垫脚垫、汽车电子、汽车精品)</label>--%>
                <%--</td>--%>
            <%--</tr>--%>
            <%--<tr>--%>
                <%--<td></td>--%>
                <%--<td colspan="3">--%>
                    <%--<label class="lbl"><input id="check_otherBusinessScope" type="checkbox" value=""/>其他</label>--%>
                    <%--<input id="input_otherBusinessScope" type="text" class="txt" maxlength="10"--%>
                           <%--style=" margin-top:9px; width:45%; float:left;" name="otherBusinessScope" node-type="businessScopes"/>--%>
                <%--</td>--%>
                <%--<td style="text-align:left;">--%>
                    <%--<div class="tips" node-type="businessScopes_tips">--%>
                    <%--</div>--%>
                <%--</td>--%>
            <%--</tr>--%>
            <tr>
                <td>网址</td>
                <td colspan="3"><input type="text" class="txt" value="" id="input_url" name="url" maxlength="75"/></td>
            </tr>

            <tr>
              <td style="vertical-align:top;">主营车型</td>
              <td style="vertical-align:top;">
                <div style="width:150px;">
                  <label><input type="radio" id="allBrandModel" value="allBrandModel" name="model_select"/>全部车型</label>&nbsp;
                  <label><input type="radio" id="partBrandModel" value="partBrandModel" name="model_select"/>部分车型</label>&nbsp;
                </div>
                <div id="vehicleBrandModelDiv" style="display:none;"></div>
              </td>
            </tr>


            <tr>
              <td style="vertical-align:top;">已选经营产品</td>
              <td id="businessScopeSelectdTd" colspan="4" class="rangeList"></td>
              <%--<td id="businessScopes_tips_td" style="text-align:left;">--%>
                <%--<div class="tips" node-type="businessScopes_tips"></div>--%>
              <%--</td>--%>
            </tr>

            <tr>
              <td style="vertical-align:top;">经营产品<span class="red_color">*</span></td>
              <td>
                  <div style="margin:0;padding:0;width:260px;">
                      <img src="images/register/icon_search.png" style="width:22px;float:left;"/>
                      <input id="businessScopeText" style="width:220px;" type="text" class="txt" />
                  </div>
              </td>
            </tr>

            <tr id="businessScopeTr">
              <td style="vertical-align:top; line-height:normal; padding-top:10px;"></td>
              <td id="businessScopeTd" colspan="4" class="rangeList"></td>
            </tr>

        </table>
        <div class="i_height"></div>
        <b style="line-height:22px; color:#000000;">主营产品（最多<span>10</span>条&nbsp;最少<span>5</span>条）<span style="font-weight: normal;margin-left:178px;" node-type="product_tips"></span></b>

        <table cellpadding="0" cellspacing="0" class="tabRegisterInfo" id="productTable">
          <col width="80">
          <col>
          <col width="60">
          <col width="60">
          <col width="60">
          <col width="60">
          <col width="60">
          <col width="60">
          <col width="60">
          <tr class="tabTitle">
            <td style="padding-left:5px;">商品编号</td>
            <td>品名</td>
            <td>品牌</td>
            <td>规格</td>
            <td>型号</td>
            <td>车牌</td>
            <td>车型</td>
            <td>单位</td>
            <td>操作</td>
          </tr>

          <tr class="productItem">
            <td style="padding-left:5px;"><input type="text" id="productDTOs0.commodityCode" name="productDTOs[0].commodityCode"
                                                 class="txt"/></td>
            <td><input type="text" id="productDTOs0.name" name="productDTOs[0].name" class="txt"/></td>
            <td><input type="text" id="productDTOs0.brand" name="productDTOs[0].brand" class="txt"/></td>
            <td><input type="text" id="productDTOs0.spec" name="productDTOs[0].spec" class="txt"/></td>
            <td><input type="text" id="productDTOs0.model" name="productDTOs[0].model" class="txt"/></td>
            <td><input type="text" id="productDTOs0.productVehicleBrand" name="productDTOs[0].productVehicleBrand" searchField="brand" cacheField="productVehicleBrandSource" class="txt"/>
            </td>
            <td><input type="text" id="productDTOs0.productVehicleModel" name="productDTOs[0].productVehicleModel" searchField="model" cacheField="productVehicleModelSource" class="txt"/>
            </td>
            <td><input type="text" id="productDTOs0.sellUnit" name="productDTOs[0].sellUnit" class="txt"/>
            </td>
            <td><a id="productDTOs0.deleteProduct" class="blue_color deleteProduct">删除</a>&nbsp;<a id="productDTOs0.addNewProduct" class="blue_color addNewProduct">新增</a></td>
          </tr>
        </table>
        <div class="i_height"></div>
        <table cellpadding="0" cellspacing="0" class="tabRegister" id="contact_table">
            <colgroup>
                <col width="80">
                <col width="100">
                <col width="45">
                <col width="100">
                <col width="135">
            </colgroup>
            <tbody>
            <tr class="regTitle">
                <td colspan="5">店主/联系人信息<a class="add_new" id="addContact">新增联系人</a></td>
            </tr>
            <tr>
                <td>负责人/店主<span class="red_color">*</span></td>
                <td><input type="text" id="contacts0.name" name="contacts[0].name" class="txt" node-type="contact_owner"></td>
                <td>手机<span class="red_color">*</span></td>
                <td><input type="text" class="txt" id="contacts0.mobile" name="contacts[0].mobile" node-type="contact_mobile"></td>
                <%--<td style="text-align:left; line-height:15px;">
                    <div style="float:left;"><a class="wrong"></a></div>
                    <span class="red_color">手机号格式不正确！</span></td>--%>
                <td style="text-align:left;">
                    <div class="tips" node-type="contact_mobile_tips|contact_owner_tips"></div>
                </td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type="text" class="txt" id="contacts0.email" name="contacts[0].email" node-type="contact_email" maxlength="50"></td>
                <td>QQ</td>
                <td><input type="text" class="txt" id="contacts0.qq" name="contacts[0].qq" node-type="contact_qq"></td>
                <td style="text-align:left;">
                    <div class="tips" node-type="contact_email_tips|contact_qq_tips"></div>
                </td>
            </tr>
            </tbody>
        </table>
        <c:if test="${param.paramNeedVerify != null && param.paramNeedVerify == 'true'}">
        <table cellpadding="0" cellspacing="0" class="tabRegister" id="invitation_table">
            <colgroup>
                <col width="80">
                <col width="100">
                <col width="45">
                <col width="100">
                <col width="135">
            </colgroup>
            <tbody>
            <tr>
                <td>邀请码<span class="red_color">*</span></td>
                <td colspan="2">
                    <input type="text" class="txt gray_color" init_word="请输入邀请码" maxlength="6" value="请输入邀请码"
                           id="invitationCode" name="invitationCode" node-type="invitationCode"
                           style="width:135px;color: #999999;ime-mode:disabled"/>
                </td>
                <td colspan="2" style="text-align:left;">
                    <div class="tips" node-type="invitationCode_tips">
                    </div>
                </td>
            </tr>
            <tr class="registerDescription">
                <td></td>
                <td colspan="3"><span class="gray_color">（邀请码如有任何问题,请联系客服！）</span></td>
                <td></td>
            </tr>
            </tbody>
        </table>
        </c:if>
        <div class="height"></div>
        <table cellpadding="0" cellspacing="0" class="tabRegister">
            <col width="70">
            <col width="110">
            <col>
            <col width="100">
            <col width="125">
            <tr class="regTitle">
                <td colspan="5">详细信息<a class="more">更多</a></td>
            </tr>
            <tr class="moreRegisterInfo">
                <td>经营方式</td>
                <td><select class="txt" name="operationMode" id="operationMode">
                    <option value="">-请选择-</option>
                    <option value="加盟连锁">加盟连锁</option>
                    <option value="有限公司">有限公司</option>
                    <option value="专卖店">专卖店</option>
                    <option value="个体">个体</option>
                    <option value="其他">其他</option>
                </select></td>
                <td colspan="2">
                    <input type="text" class="txt gray_color" init_word="请输入品牌"  style="display: none;color: #999999"
                            id="operationModeBrand" name="operationModeBrand" value="请输入品牌"/></td>
                <td></td>
            </tr>
            <tr class="moreRegisterInfo">
                <td>所在地车牌</td>
                <td><input type="text" class="txt" value="" id="licencePlate" name="licencePlate" maxlength="2"/></td>
                <td>固定电话</td>
                <td><input type="text" class="txt" value="" d="input_landLine" name="landline" maxlength="20"/></td>
                <td style="text-align:left;"><span class="gray_color">（如：0512-66733331）</span></td>
            </tr>
            <%--<tr class="moreRegisterInfo">--%>
                <%--<td>营业执照</td>--%>
                <%--<td colspan="3">--%>
                    <%--<div class="file-box">--%>
                        <%--<input type="text" class="txt" value="" id="businessLicenseInfo" style="width:65%;"/>--%>
                        <%--<input type="button" class="btnFile" id="businessLicenseBtn"  onfocus="this.blur()"--%>
                               <%--onclick="$('#businessLicense').click()" value="浏&nbsp;览"/>--%>
                        <%--<input type="file" class="file" id="businessLicense" name="businessLicenseFile"--%>
                                <%--node-type = "businessLicense"/>--%>
                    <%--</div>--%>
                <%--</td>--%>
                <%--<td style="text-align:left;">--%>
                    <%--<div class="tips" node-type="businessLicense_tips">--%>
                    <%--</div>--%>
                <%--</td>--%>
            <%--</tr>--%>
            <tr class="moreRegisterInfo">
                <td style="vertical-align:top">营业执照</td>
                <td colspan="4">
                    <div id="shopBusinessLicenseImageUploaderView" style="float: left;width:120px;height:90px;position: relative;"></div>

                    <input type="hidden" id="imageCenterDTO.shopBusinessLicenseImagePath" name="imageCenterDTO.shopBusinessLicenseImagePath"/>
                    <div style="padding-left:5px;float: left;width: 160px;height: 50px" id="shopBusinessLicenseImageUploader"></div>
                    <div style="float: left;height: 50px">
                        <div style="float: left;color: red;line-height: 18px;padding-left: 5px;height: 50px">提示： </div>
                        <div style="float: left; line-height: 18px; text-align: left;height: 50px">营业执照上传后不能修改！</div>
                    </div>
                </td>
            </tr>
            <tr class="moreRegisterInfo">
                <td style="vertical-align:top">店面照片</td>
                <td colspan="4">
                    <input type="hidden" id="imageCenterDTO.shopImagePaths0" name="imageCenterDTO.shopImagePaths[0]"/>
                    <input type="hidden" id="imageCenterDTO.shopImagePaths1" name="imageCenterDTO.shopImagePaths[1]"/>
                    <input type="hidden" id="imageCenterDTO.shopImagePaths2" name="imageCenterDTO.shopImagePaths[2]"/>
                    <input type="hidden" id="imageCenterDTO.shopImagePaths3" name="imageCenterDTO.shopImagePaths[3]"/>

                    <div style="width:550px;height: 80px">
                        <div style="float: left;width: 160px;height: 80px" id="shop_imageUploader"></div>
                        <div style="float: left;color: red;line-height: 18px;padding-left: 5px">提示： </div>
                        <div style="float: left; line-height: 18px; text-align: left;">1. 如果您要上传店面图片，请点击以上按钮。<br>2. 所选图片都必须是 jpg、png、gif 或 jpeg 格式。<br>3. 每张图片的大小不得超过5M。<br> 4. 您可一次选择多张图片哦！最多上传 4 张图片。</div>
                    </div>
                    <div id="shop_imageUploaderView" style="width:550px;height:180px;position: relative;"></div>
                </td>
            </tr>
            <%--<tr class="moreRegisterInfo">--%>
                <%--<td>店面照片</td>--%>
                <%--<td colspan="3">--%>
                    <%--<div class="file-box">--%>
                        <%--<input type="text" class="txt" value="" id="shopPhotoInfo"  style="width:65%;"/>--%>
                        <%--<input type="button" class="btnFile" id="shopPhotoBtn"  onfocus="this.blur()"--%>
                               <%--onclick="$('#shopPhoto').click()"  value="浏&nbsp;览"/>--%>
                        <%--<input type="file" class="file" id="shopPhoto" name="shopPhotoFile" node-type = "shopPhoto"/>--%>
                    <%--</div>--%>
                <%--</td>--%>
                <%--<td style="text-align:left;">--%>
                    <%--<div class="tips" node-type="shopPhoto_tips">--%>
                    <%--</div>--%>
                <%--</td>--%>
            <%--</tr>--%>
            <tr class="registerTextarea moreRegisterInfo">
                <td style="vertical-align:top;">备注</td>
                <td colspan="3">
                    <textarea class="txt" id="input_memo" name="memo"  maxlength="100" ></textarea>
                </td>
                <td></td>
            </tr>
            <tr>
                <td colspan="5" class="btnSure">
                    <a id="registerBtn">确认注册</a>
                    <a href="shopRegister.do?method=registerMain&registerType=${param.paramRegisterType}&needVerify=${param.paramNeedVerify}">返回重新选择版本</a>
                </td>
            </tr>
            <tr class="registerDescription">
                <td></td>
                <td colspan="3">
                    <a class="lbl blue_color" target="_blank" href="shop.do?method=shopagreement">
                        <input type="checkbox" checked="checked"/>《统购信息软件服务条款》</a>
                </td>
                <td></td>
            </tr>
        </table>
    </form>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
