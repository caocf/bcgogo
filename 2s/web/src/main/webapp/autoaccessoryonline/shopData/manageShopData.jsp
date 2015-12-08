<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-8-7
  Time: 上午11:28
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>本店资料</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <style type="text/css">
        #rq:hover {
            cursor: pointer;
        }
    </style>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=nh73VgKTDOS1LnxhSPvpz9DM"></script>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/page/autoaccessoryonline/shopData<%=ConfigController.getBuildVersion()%>.js"></script>
    <%@ include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="baidu/map"></script>--%>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.ONLINE.SLEF_SHOP_DATA_MENU");

        APP_BCGOGO.Permission.WholesalerVersion =${isWholesalerVersion};
        $().ready(function () {



            var shopImageUrlData = [];

            APP_BCGOGO.Net.asyncAjax({
                url: "shopData.do?method=getShopData",
                type: "POST",
                cache: false,
                dataType: "json",
                success: function (shop) {
                    initShopInfo(shop);
                },
                error: function () {
                    nsDialog.jAlert("网络异常！");
                }
            });
            $("#sendInActiveLocateStatus").click(function () {
                APP_BCGOGO.Net.asyncAjax({
                    url: "shopData.do?method=sendInActiveLocateStatus",
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        nsDialog.jAlert(result['msg']);
                    },
                    error: function () {
                        nsDialog.jAlert("网络异常！");
                    }
                });
            });

            function initShopInfo(shop) {
                bindShopImageInfo(shop);
                initLocationMap(shop);
                if (G.isEmpty(shop)) return;
                //init shop base info
                $(".shopData [field='name']").text(G.normalize(shop.name));
                $(".shopData [field='address']").text(G.normalize(shop.address)).val(G.normalize(shop.address));
                $(".shopData [field='registrationDateStr']").text(G.normalize(shop.registrationDateStr));
                $(".shopData [field='legalRep']").text(G.normalize(shop.legalRep));
                $(".shopData [field='url']").text(G.normalize(shop.url)).val(G.normalize(shop.url));
                $(".shopData span[field='operationMode']").text(G.normalize(shop.operationModeStr));
                $(".shopData select[field='operationMode']").val(G.normalize(shop.operationModeStr));
                $(".shopData [field='landline']").text(G.normalize(shop.landline)).val(G.normalize(shop.landline));
                $(".shopData [field='licencePlate']").text(G.normalize(shop.licencePlate)).val(G.normalize(shop.licencePlate));
                $(".shopData [field='memo']").text(G.normalize(shop.memo)).val(G.normalize(shop.memo));
                $(".shopData [field='accidentMobile']").text(G.normalize(shop.accidentMobile)).val(G.normalize(shop.accidentMobile));
                $(".shopData [field='shopVehicleBrandModel']").text(G.normalize(shop.shopVehicleBrandModelStr));
                $(".shopData [field='businessScopeStr']").text(G.normalize(shop.businessScopeStr));
                $("#shop_province").val(G.normalize(shop.province));
                $("#shop_city").val(G.normalize(shop.city));
                $("#shop_region").val(G.normalize(shop.region));
                //init serviceCategory
                if (shop.serviceCategory && shop.serviceCategory[0]) {
                    var serviceCategoryName = '';
                    var html = '';
                    for (var i = 0; i < shop.serviceCategory.length; i++) {
                        var serviceCategory = shop.serviceCategory[i];
                        if (shop.serviceCategoryIdStr && shop.serviceCategoryIdStr.indexOf(serviceCategory.idStr) != -1) {
                            serviceCategoryName += serviceCategory.text + ',';
                            html += '<input type="checkbox" checked="checked" fieldName="' + serviceCategory.text + '" value="' + serviceCategory.idStr + '"/> ' + serviceCategory.text + '&nbsp;';
                        } else {
                            html += '<input type="checkbox" fieldName="' + serviceCategory.text + '" value="' + serviceCategory.idStr + '"/> ' + serviceCategory.text + '&nbsp;';
                        }

                        if (i == 11) {
                            html += '<br />'
                        }
                    }
                    if (serviceCategoryName != '') {
                        $(".shopData [field='shopServiceCategory']").text(G.normalize(serviceCategoryName.substring(0, serviceCategoryName.length - 1)));
                    }
                    $("#serviceCategoryCheck").append(html);
                    $("#serviceCategoryIds").val(shop.serviceCategoryIdStr);
                }
                //init contact
                var contacts = shop.contacts;
                if (!G.isEmpty(contacts)) {
                    for (var i = 0; i < contacts.length; i++) {
                        var contact = contacts[i];
                        if (G.isEmpty(contact)) continue;
                        var contactStr = ".contact-" + (i + 1);
                        $(contactStr + " [field='contact-name']").text(G.normalize(contact.name)).val(G.normalize(contact.name));
                        $(contactStr + " [field='contact-mobile']").text(G.normalize(contact.mobile)).val(G.normalize(contact.mobile));
                        $(contactStr + " [field='contact-email']").text(G.normalize(contact.email)).val(G.normalize(contact.email)).attr("title", G.normalize(contact.email));
                        $(contactStr + " [field='contact-qq']").text(G.normalize(contact.qq)).val(G.normalize(contact.qq));         //例：$(".contact-1 .contact-qq")
                        if (APP_BCGOGO.Permission.WholesalerVersion && !G.isEmpty(contact.qq)) {
                            var $qqIcon = $(contactStr + " [field='contact-qq-icon']");
                            $qqIcon.multiQQInvoker({
                                QQ: [G.normalize(contact.qq)], QQIcoStyle: 51, callBack: function () {
                                    if ($qqIcon.height() == 24 && $qqIcon.width() == 78) {    //判断未开启qq商家的qq号，目前没发现更好的方法来判断
                                        $qqIcon.attr("href", "http://wp.qq.com/consult.html");
                                        $qqIcon.find("img").attr("title", "开通qq商家");
                                    }
                                }
                            });

                        }

                    }
                }
                //init AccidentSpecialist
                var specialistDTOs = shop.specialistDTOs;
                if (!G.isEmpty(specialistDTOs)) {
                    var i = 0;
                    for (; i < specialistDTOs.length; i++) {
                        var specialistDTO = specialistDTOs[i];
                        if (G.isEmpty(specialistDTO)) continue;
                        var specialistDTOStr = ".accident-" + (i + 1);
                        $(specialistDTOStr + " [field='accident-name']").text(G.normalize(specialistDTO.name)).val(G.normalize(specialistDTO.name));
                        $(specialistDTOStr + " [field='accident-mobile']").text(G.normalize(specialistDTO.mobile)).val(G.normalize(specialistDTO.mobile));
                        $(specialistDTOStr + " [field='accident-wxName']").text(G.normalize(specialistDTO.wxName)).val(G.normalize(specialistDTO.wxName));
                        $(specialistDTOStr + " [field='accident-wxNickName']").text(G.normalize(specialistDTO.wxNickName)).val(G.normalize(specialistDTO.wxNickName));
                        $(specialistDTOStr + " [field='accident-openId']").val(G.normalize(specialistDTO.openId));
                        $(specialistDTOStr).find(".unbind-btn").attr("openId",G.normalize(specialistDTO.openId));
                        $(specialistDTOStr + " [field='accident-id']").val(G.normalize(specialistDTO.idStr));
                        $(specialistDTOStr).show();
                    }
                    for (; i < 3; i++) {
                        var specialistDTOStr = ".accident-" + (i + 1);
                        $(specialistDTOStr).remove();
                    }
                } else {
                    $(".j_accident_item").remove();
                }
                //init RQ image 二维码图片
                var imageCenterDTO = shop.imageCenterDTO;
                if (imageCenterDTO != null) {
                    var shopRQImageDetailDTO = imageCenterDTO.shopRQImageDetailDTO;
                    if (shopRQImageDetailDTO != null) {
                        var imageURL = shopRQImageDetailDTO.imageURL;
                        $("#rq").attr("src", imageURL);
                    }
                }
                //initShopRegisterProductTable
                var trStr = "";
                var products = shop.productDTOs;
                if (G.isEmpty(products)) {
                    trStr = "<tr><td colspan='8' style='text-align: center;'>没有主营商品信息</td></tr>";
                } else {
                    var trStr = "";
                    for (var i = 0; i < products.length; i++) {
                        var product = products[i];
                        trStr += '<tr>';
                        trStr += '<td>' + G.normalize(product.commodityCode) + '</td>';
                        trStr += '<td>' + G.normalize(product.name) + '</td>';
                        trStr += '<td>' + G.normalize(product.brand) + '</td>';
                        trStr += '<td>' + G.normalize(product.spec) + '</td>';
                        trStr += '<td>' + G.normalize(product.model) + '</td>';
                        trStr += '<td>' + G.normalize(product.vehicleModel) + '</td>';
                        trStr += '<td>' + G.normalize(product.vehicleBrand) + '</td>';
                        trStr += '<td>' + G.normalize(product.sellUnit) + '</td>';
                        trStr += '</tr>';
                    }
                }
                $("#shopRegisterProductTable").append(trStr);
            }

            $(".area-select").change(function () {
                //    var address=$(".area-province option:selected").text()+$(".area-city option:selected").text()+region;
                var city = "";
                if (!G.isEmpty($(".area-city option:selected").val())) {
                    city = $(".area-city option:selected").text()
                }
                var province = "";
                if (!G.isEmpty($(".area-province option:selected").val())) {
                    province = $(".area-province option:selected").text()
                }
                var region = "";
                if (!G.isEmpty($(".area-region option:selected").val())) {
                    region = $(".area-region option:selected").text()
                }
                $(".shopData input[field='address']").val(province + city + region);
            });


            function bindShopImageInfo(shop) {
                var currentItemNum = 0;
                var shopImageDetailDTOs;
                if (!G.Lang.isEmpty(shop) && !G.Lang.isEmpty(shop.imageCenterDTO.shopUploadImageDetailDTOs)) {
                    shopImageDetailDTOs = shop.imageCenterDTO.shopUploadImageDetailDTOs;
                    currentItemNum = shopImageDetailDTOs.length;
                }
                var imageUploader = new App.Module.ImageUploader();

                imageUploader.init({
                    "selector": "#shop_imageUploader",
                    "flashvars": {
                        "debug": "off",
                        "maxFileNum": 4,
                        "currentItemNum": currentItemNum,
                        "width": 61,
                        "height": 24,
                        "buttonBgUrl": "images/imageUploader.png",
                        "buttonOverBgUrl": "images/imageUploader.png",
                        "url": APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL + "/" + APP_BCGOGO.UpYun.UP_YUN_BUCKET + "/",
                        "ext": {
                            "policy": $("#policy").val(),
                            "signature": $("#signature").val()
                        }
                    },

                    "startUploadCallback": function (message) {
                        // 设置 视图组件 uploading 状态
                        imageUploaderView.setState("uploading");
                    },
                    "uploadCompleteCallback": function (message, data) {
                        var dataInfoJson = JSON.parse(data.info);
                        shopImageUrlData.push(dataInfoJson.url);
                    },
                    "uploadErrorCallback": function (message, data) {
                        var errorData = JSON.parse(data.info);
                        errorData["content"] = data.info;
                        saveUpLoadImageErrorInfo(errorData);
                        nsDialog.jAlert("上传图片失败！");
                    },
                    "uploadAllCompleteCallback": function () {
                        //更新input
                        $("input[id^='shopImagePaths']").each(function (index) {
                            $(this).val(G.Lang.normalize(shopImageUrlData[index], ""));
                        });
                        var data = {"imagePaths": shopImageUrlData.join(",")};
                        var result = saveOwnShopImageRelation(data);
                        if (G.Lang.isEmpty(result)) {
                            nsDialog.jAlert("网络异常！");
                        } else {
                            if (result.success) {
                                // 设置 视图组件  idle 状态
                                imageUploaderView.setState("idle");
                                imageUploaderView.update(getTotelUrlToData(shopImageUrlData));
                                nsDialog.jAlert("上传图片成功！");
                            } else {
                                nsDialog.jAlert("上传图片失败！");
                            }
                        }
                    }
                });

                /**
                 * 视图组建的 样例代码
                 * */
                var imageUploaderView = new App.Module.ImageUploaderView();
                imageUploaderView.init({
                    // 你所需要注入的 dom 节点
                    selector: "#shop_imageUploaderView",
                    width: 620,
                    height: 180,
                    iWidth: 120,
                    iHeight: 100,
                    maxFileNum: 4,
                    // 当删除某张图片时会触发此回调
                    onDelete: function (event, data, index) {
                        imageUploader.getFlashObject().deleteFile(index);

                        // 从已获得的图片数据池中删除 图片数据
                        shopImageUrlData.splice(index, 1);
                        //更新input

                        $("input[id^='shopImagePaths']").each(function (index) {
                            $(this).val(G.Lang.normalize(shopImageUrlData[index], ""));
                        });
                        var data = {"imagePaths": shopImageUrlData.join(",")};
                        var result = saveOwnShopImageRelation(data);
                        if (G.Lang.isEmpty(result)) {
                            nsDialog.jAlert("网络异常！");
                        } else {
                            if (result.success) {
                                // 设置 视图组件  idle 状态
                                imageUploaderView.setState("idle");
                                imageUploaderView.update(getTotelUrlToData(shopImageUrlData));
                                nsDialog.jAlert("删除图片成功！");
                            } else {
                                nsDialog.jAlert("删除图片失败！");
                            }
                        }

                    }
                });
                if (!G.Lang.isEmpty(shopImageDetailDTOs)) {
                    //更新input
                    $("input[id^='shopImagePaths']").each(function (index) {
                        if (shopImageDetailDTOs[index]) {
                            $(this).val(G.Lang.normalize(shopImageDetailDTOs[index].imagePath, ""));
                            if (!G.Lang.isEmpty($(this).val()))
                                shopImageUrlData.push($(this).val());
                        } else {
                            $(this).val("");
                        }
                    });
                    // 设置 视图组件  idle 状态
                    imageUploaderView.setState("idle");
                    imageUploaderView.update(getTotelUrlToData(shopImageUrlData));
                }

                /**
                 * 视图组建的 样例代码
                 * */
                var businessLicenseImageUploaderView = new App.Module.ImageUploaderView();
                businessLicenseImageUploaderView.init({
                    // 你所需要注入的 dom 节点
                    selector: "#shopBusinessLicenseImageUploaderView",
                    width: 285,
                    height: 182,
                    iWidth: 285,
                    iHeight: 180,
                    paddingTop: 0,
                    paddingBottom: 0,
                    paddingLeft: 0,
                    paddingRight: 0,
                    horizontalGap: 0,
                    verticalGap: 0,
                    maxFileNum: 1
                });

                //营业执照上传后不能修改
                var shopBusinessLicenseImageDetailDTO = shop.imageCenterDTO.shopBusinessLicenseImageDetailDTO;
                if (!G.Lang.isEmpty(shopBusinessLicenseImageDetailDTO)) {
                    $("#shopBusinessLicenseImagePath").val(shopBusinessLicenseImageDetailDTO.imagePath);
                    $("#shopBusinessLicenseImageUploader").remove();
                    businessLicenseImageUploaderView.setIsDeletable(false);
                    businessLicenseImageUploaderView.setState("idle");
                    var outData = [{
                        "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + $("#shopBusinessLicenseImagePath").val() + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.SHOP_BUSINESS_LICENSE_IMAGE
                    }];
                    businessLicenseImageUploaderView.update(outData);
                } else {
                    var shopBusinessLicenseImageUploader = new App.Module.ImageUploader();
                    shopBusinessLicenseImageUploader.init({
                        "selector": "#shopBusinessLicenseImageUploader",
                        "flashvars": {
                            "debug": "off",
                            "maxFileNum": 1,
                            "width": 61,
                            "height": 24,
                            "buttonBgUrl": "images/imageUploader.png",
                            "buttonOverBgUrl": "images/imageUploader.png",
                            "url": APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL + "/" + APP_BCGOGO.UpYun.UP_YUN_BUCKET + "/",
                            "ext": {
                                "policy": $("#policy").val(),
                                "signature": $("#signature").val()
                            }
                        },

                        "startUploadCallback": function (message) {
                            // 设置 视图组件 uploading 状态
                            businessLicenseImageUploaderView.setState("uploading");
                        },
                        "uploadCompleteCallback": function (message, data) {
                            var dataInfoJson = JSON.parse(data.info);
                            $("#shopBusinessLicenseImagePath").val(dataInfoJson.url);
                        },
                        "uploadErrorCallback": function (message, data) {
                            var errorData = JSON.parse(data.info);
                            errorData["content"] = data.info;
                            saveUpLoadImageErrorInfo(errorData);
                            nsDialog.jAlert("上传图片失败！");
                        },
                        "uploadAllCompleteCallback": function () {
                            APP_BCGOGO.Net.asyncAjax({
                                url: "shopData.do?method=saveOwnShopBusinessLicenseImageRelation",
                                data: {"shopBusinessLicenseImagePath": $("#shopBusinessLicenseImagePath").val()},
                                type: "POST",
                                dataType: "json",
                                success: function (result) {
                                    if (result.success) {
                                        // 设置 视图组件  idle 状态
                                        businessLicenseImageUploaderView.setIsDeletable(false);
                                        businessLicenseImageUploaderView.setState("idle");
                                        var outData = [{
                                            "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + $("#shopBusinessLicenseImagePath").val() + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.SHOP_BUSINESS_LICENSE_IMAGE
                                        }];
                                        businessLicenseImageUploaderView.update(outData);
                                        $("#shopBusinessLicenseImageUploader").remove();
                                        nsDialog.jAlert("上传图片成功！");
                                    } else {
                                        nsDialog.jAlert("上传图片失败！");
                                    }
                                },
                                error: function () {
                                    nsDialog.jAlert("网络异常！");
                                }
                            });
                        }
                    });
                }
            }
//----------------------------------------------------------------------------------------------------------------------------
            function initLocationMap(shop) {
                if (!shop['coordinateLat'] || !shop['coordinateLon'])return;
           //     var $iframe = $("#map_container_iframe");
          //      $iframe[0].src = "coordinateLat=" + shop['coordinateLat'] + "&coordinateLon=" + shop['coordinateLon'];
//                $iframe[0].src = "api/proxy/baidu/map/shop?coordinateLat=" + shop['coordinateLat'] + "&coordinateLon=" + shop['coordinateLon'];
 //               $iframe[0].style.display = "block";
                   $("#allmap").show();
      //          var str=  $iframe[0].src;
               // var lon = str.substr(38,9);  //经度
               var lon= shop['coordinateLon']
               var lat= shop['coordinateLat']
              //  var lat = str.substr(14,9);   //纬度

                var map = new BMap.Map("allmap");
                var point = new BMap.Point(lon,lat);
                map.centerAndZoom(point, 17);
                // 创建标注
                var marker = new BMap.Marker(point);
                map.addOverlay(marker);


                 var mapType1 = new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP,BMAP_HYBRID_MAP]});
            var mapType2 = new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT});

            var overView = new BMap.OverviewMapControl();
            var overViewOpen = new BMap.OverviewMapControl({isOpen:true, anchor: BMAP_ANCHOR_BOTTOM_RIGHT});
            map.addControl(mapType1);          //2D图，卫星图
            map.addControl(mapType2);          //左上角，默认地图控件
            map.setCurrentCity("北京");        //由于有3D图，需要设置城市哦
            map.addControl(overView);          //添加默认缩略地图控件
            map.addControl(overViewOpen);      //右下角，打开
            }

            function getTotelUrlToData(inUrlData) {
                var outData = [];
                for (var i = 0; i < inUrlData.length; i++) {
                    var outDataItem = {
                        "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + inUrlData[i] + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.SHOP_MANAGE_UPLOAD_IMAGE,
                        "name": i == 0 ? "*主图" : "辅图"
                    };
                    outData.push(outDataItem);
                }
                return outData;
            };
            function saveOwnShopImageRelation(data) {
                return APP_BCGOGO.Net.syncPost({"url": "shopData.do?method=saveOwnShopImageRelation", "data": data});
            }

        });

    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">

<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="promotions"/>
        </jsp:include>
        <div class="content-main">

            <dl class="shop-content">
                <dt class="content-title">
                <div class="bg-top-hr"></div>
                <div class="bar-tab">
                    <span onclick="toManageShopData()" class="label actived" onselectstart="return false;">本店资料</span>
                    <%--    <span onclick="toShopComment()" class="label" onselectstart="return false;">本店评价</span>--%>
                    <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
                        <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
                            <a class="blue_color to-ShopDetail"
                               href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${sessionScope.shopId}">查看本店店铺展示</a>
                        </c:if>
                    </bcgogo:permissionParam>

                    <div class="cl"></div>
                </div>
                </dt>
                <dd class="shopData content-details">

                    <!--start info-basic-->
                    <dl class="info-basic">
                        <dt><span class="label-title">基本信息</span></dt>
                        <dd>
                            <span class="button-info-edit " onselectstart="return false;"
                                  onclick="doEditShopBaseInfo()">修改信息</span>
                            <span class="button-info-save" onselectstart="return false;" onclick="saveShopBaseInfo()"
                                  style="display: none">确认修改</span>
                            <span class="button-info-save" onselectstart="return false;"
                                  onclick="cancelSaveShopBaseInfo()" style="display: none">取消</span>
                        </dd>
                        <div class="cl"></div>
                        <div class="col-1">
                            <dt>公司名称</dt>
                            <dd>
                                <span field="name"></span>
                            </dd>
                            <div class="cl"></div>
                            <dt>地址</dt>
                            <dd>
                                <span field="address"<%-- class="block-saved"--%>></span>
            <span style="display: none" <%--class="block-editing"--%>>
               <span id="areaInfo"></span>
                <select class="area-province txt area-select" style="width:76px;">
                    <option value="">--省份--</option>
                    <c:if test="${not empty provinces}">
                        <c:forEach items="${provinces}" var="province">
                            <option value="${province.no}">${province.name}</option>
                        </c:forEach>
                    </c:if>
                </select>
                <select autocomplete="off" class="area-city txt area-select" style="width:76px;">
                    <option value="">--城市--</option>
                </select>
                <select autocomplete="off" class="area-region txt area-select" style="width:70px;">
                    <option value="">--区--</option>
                </select>
                <input autocomplete="off" type="hidden" id="shop_province"/>
                <input autocomplete="off" type="hidden" id="shop_city"/>
                <input autocomplete="off" type="hidden" id="shop_region"/>
            </span>
                            </dd>
                            <div class="cl"></div>
                            <dt style="display: none" <%--class="block-editing"--%>>详细地址</dt>
                            <dd style="display: none" <%--class="block-editing"--%>>
                                <span><input autocomplete="off" field="address" class="txt w250" type="text"/></span>
                            </dd>
                            <div class="cl"></div>
                            <dt>注册时间</dt>
                            <dd>
                                <span field="registrationDateStr"></span>
                            </dd>
                            <div class="cl"></div>
                            <%--<dt>备注</dt>--%>
                            <%--<dd>--%>
                            <%--<span field="memo" class="block-saved">暂无</span>--%>
                            <%--<span style="display: none" class="block-editing"><input field="memo" class="txt w250" type="text" /></span>--%>
                            <%--</dd>--%>
                            <%--<div class="cl"></div>   --%>
                            <dt>固定电话</dt>
                            <dd style="width:120px;">
                                <span field="landline" class="block-saved"></span>
                                <span style="display: none" class="block-editing"><input autocomplete="off"
                                                                                         field="landline" class="txt"
                                                                                         type="text"/></span>
                            </dd>
                            <div class="cl"></div>
                        </div>

                        <div class="col-2" style="width:250px;">
                            <dt>网址</dt>
                            <dd style="width:120px;margin-left: 20px;">
                                <span field="url" class="block-saved"></span>
                                <span style="display: none" class="block-editing"><input autocomplete="off" field="url"
                                                                                         class="txt"
                                                                                         type="text"/></span>
                            </dd>
                            <div class="cl"></div>
                            <dt>经营方式</dt>
                            <dd style="width:120px;margin-left: 20px;">
                                <span field="operationMode" class="block-saved"></span>
            <span style="display: none" class="block-editing">
                <select class="text txt" autocomplete="off" field="operationMode" id="operationMode"
                        style="width: 80px">
                    <option value="">-请选择-</option>
                    <option value="加盟连锁">加盟连锁</option>
                    <option value="有限公司">有限公司</option>
                    <option value="专卖店">专卖店</option>
                    <option value="个体">个体</option>
                    <option value="其他">其他</option>
                </select>
            </span>
                            </dd>
                            <div class="cl"></div>
                            <dt>所在地车牌</dt>
                            <dd style="width:120px;margin-left: 20px;">
                                <span field="licencePlate" class="block-saved"></span>
                                <span style="display: none" class="block-editing"><input autocomplete="off"
                                                                                         field="licencePlate"
                                                                                         class="txt"
                                                                                         type="text"/></span>
                            </dd>
                            <div class="cl"></div>
                            <dt style="width:75px">事故专员号码</dt>
                            <dd style="width:120px;">
                                <span field="accidentMobile" class="block-saved"></span>
                                <span style="display: none" class="block-editing"><input autocomplete="off"
                                                                                         field="accidentMobile"
                                                                                         class="txt"
                                                                                         type="text"/></span>
                            </dd>
                            <div class="cl"></div>
                        </div>

                        <div class="cl"></div>

                        <div style="width: 700px">
                            <dt>备注</dt>
                            <div style="margin-left:60px;">
                                <span field="memo" class="block-saved"></span>
                                <span style="display: none" class="block-editing"><input autocomplete="off" field="memo"
                                                                                         class="txt w250" type="text"/></span>
                            </div>
                        </div>
                        <div class="cl"></div>
                        <c:if test="${!isWholesalerVersion}">
                            <div style="width: 700px">
                                <dt>服务范围</dt>
        <span class="block-saved">
            <span field="shopServiceCategory"></span>
        </span>
        <span style="display: none" class="block-editing" id="serviceCategoryCheck">
        <input autocomplete="off" type="hidden" id="serviceCategoryIds"/>
        </span>
                            </div>
                        </c:if>

                        <div class="cl"></div>
                        <div style="width: 700px">
                            <dt>主营车型</dt>
                            <div style="margin-left:60px;">
                                <span field="shopVehicleBrandModel"></span>
                            </div>
                        </div>
                        <div class="cl"></div>
                        <div style="width: 700px">
                            <dt>经营产品</dt>
                            <div style="margin-left:60px;">
                                <span field="businessScopeStr"></span>
                            </div>
                        </div>

                        <div class="cl"></div>

                    </dl>
                    <!--end info-basic-->
                    <div class="cl"></div>

                    <div class="hr"></div>

                    <!--start info-contract-->
                    <dl class="j_info_contract info-contract">
                        <div class="contract-title">
                            <dt>联系人信息</dt>
                            <dd class="button-info-edit" onclick="toEditingStatus('.j_info_contract',true)"
                                onselectstart="return false;">修改信息
                            </dd>
                            <span class="button-info-save" style="display: none" onclick="saveShopContact()"
                                  onselectstart="return false;">确认修改</span>
                            <span class="button-info-save" style="display: none" onclick="cancelSaveShopContact()"
                                  onselectstart="return false;">取消</span>
                        </div>
                        <div class="cl"></div>

                        <!--联系人1-->
                        <dl class="j_contract_item contract-item contact-1" level="0" isMainContact="1" isShopOwner="1">
                            <dt><a class="icon_connacter"></a>店主</dt>
                            <dd>
                                <span field="contact-name"></span>
                            </dd>
                            <dt><a class="icon_phone"></a>手机</dt>
                            <dd>
                                <span field="contact-mobile"></span>
                            </dd>
                            <dt><a class="icon_email"></a>Email</dt>
                            <dd style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                <span field="contact-email" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-email"
                                                                                         class="txt" type="text"
                                                                                         style="width:80px;"
                                                                                         maxlength="50"/></span>
                            </dd>
                            <dt><a class="icon_QQ"></a>QQ</dt>
                            <dd style="width: 150px">
                                <span field="contact-qq" class="block-saved" style="vertical-align: top"></span>
                                <c:if test="${isWholesalerVersion}">
                                    <a field="contact-qq-icon" class="block-saved" style="float: right"></a>
                                </c:if>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-qq" class="txt"
                                                                                         type="text"/></span>
                            </dd>
                        </dl>
                        <div class="cl"></div>

                        <!--联系人2-->
                        <dl class="contract-item contact-2" level="1" isMainContact="1" isShopOwner="0">
                            <dt><a class="icon_grayconnacter"></a>联系人</dt>
                            <dd>
                                <span field="contact-name" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-name"
                                                                                         class="txt" type="text"
                                                                                         maxlength="20"/></span>
                            </dd>
                            <dt><a class="icon_phone"></a>手机</dt>
                            <dd>
                                <span field="contact-mobile" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-mobile"
                                                                                         class="txt"
                                                                                         type="text"/></span>
                            </dd>
                            <dt><a class="icon_email"></a>Email</dt>
                            <dd style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                <span field="contact-email" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-email"
                                                                                         class="txt" type="text"
                                                                                         maxlength="50"
                                                                                         style="width:80px;"/></span>
                            </dd>
                            <dt><a class="icon_QQ"></a>QQ</dt>
                            <dd style="width: 150px">
                                <span field="contact-qq" class="block-saved" style="vertical-align: top"></span>
                                <c:if test="${isWholesalerVersion}">
                                    <a field="contact-qq-icon" class="block-saved" style="float: right"></a>
                                </c:if>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-qq" class="txt"
                                                                                         type="text"/></span>
                            </dd>
                        </dl>
                        <div class="cl"></div>

                        <!--联系人3-->
                        <dl class="contract-item contact-3" level="2" isMainContact="1" isShopOwner="0">
                            <dt><a class="icon_grayconnacter"></a>联系人</dt>
                            <dd>
                                <span field="contact-name" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-name"
                                                                                         class="txt" type="text"
                                                                                         maxlength="20"/></span>
                            </dd>
                            <dt><a class="icon_phone"></a>手机</dt>
                            <dd>
                                <span field="contact-mobile" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-mobile"
                                                                                         class="txt"
                                                                                         type="text"/></span>
                            </dd>
                            <dt><a class="icon_email"></a>Email</dt>
                            <dd style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                <span field="contact-email" class="block-saved"></span>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-email"
                                                                                         class="txt" type="text"
                                                                                         maxlength="50"
                                                                                         style="width:80px;"/></span>
                            </dd>
                            <dt><a class="icon_QQ"></a>QQ</dt>
                            <dd style="width: 150px">
                                <span field="contact-qq" class="block-saved" style="vertical-align: top"></span>
                                <c:if test="${isWholesalerVersion}">
                                    <a field="contact-qq-icon" class="block-saved" style="float: right"></a>
                                </c:if>
                                <span class="block-editing" style="display: none"><input autocomplete="off"
                                                                                         field="contact-qq" class="txt"
                                                                                         type="text"/></span>
                            </dd>
                        </dl>
                        <div class="cl"></div>

                    </dl>
                    <!--end info-contract-->
                    <div class="cl"></div>
                    <div class="hr"></div>

                    <div class="info-contract j_info_accident">
                        <div class="contract-title">
                            <span class="s-dt">事故专员信息</span>
                            <span class="s-dd button-info-edit" onclick="toEditingStatus('.j_info_accident',true)"
                                  onselectstart="return false;">修改信息
                            </span>
                            <span class="button-info-save" style="display: none" onclick="saveAccident()"
                                  onselectstart="return false;">确认修改</span>
                            <span class="button-info-save" style="display: none"
                                  onclick="cancelSaveAccidentSpecialist()"
                                  onselectstart="return false;">取消</span>
                        </div>
                        <div class="cl"></div>

                        <div>
                            <div class="acc-left">
                                <!--事故专员1-->
                                <div class="j_accident_item accident-item accident-1" style="display: none;">
                                    <span class="s-dt"><a class="icon_grayconnacter"></a>专员1</span>
                                    <span class="s-dd"
                                          style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                        <span field="accident-name" class="block-saved"></span>
                                        <span class="block-editing"
                                              style="display: none">
                                            <input autocomplete="off"
                                                   field="accident-name"
                                                   class="txt"
                                                   type="text"
                                                   style="width:80px;"
                                                   maxlength="50"/></span>
                                    </span>
                                    <span class="s-dt"><a class="icon_phone"></a>手机</span>
                                    <span class="s-dd"
                                          style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                        <span field="accident-mobile" class="block-saved"></span>
                                        <span class="block-editing"
                                              style="display: none"><input
                                                autocomplete="off"
                                                field="accident-mobile"
                                                class="txt"
                                                type="text"
                                                style="width:80px;"
                                                maxlength="50"/></span>
                                    </span>
                                    <span class="s-dt"><a class=""></a>微信号</span>
                                    <span class="s-dd">
                                        <input type="hidden" field="accident-openId">
                                        <input type="hidden" field="accident-id">
                                        <span field="accident-wxName"></span>
                                    </span>
                                    <span class="s-dt"><a class=""></a>昵称</span>
                                    <span class="s-dd">
                                        <span field="accident-wxNickName"></span>
                                        <span class="unbind-btn"  onselectstart="return false;">解除绑定</span>
                                    </span>

                                </div>
                                <div class="cl"></div>
                                <!--事故专员2-->
                                <div class="j_accident_item accident-item accident-2" style="display: none;">
                                    <span class="s-dt"><a class="icon_grayconnacter"></a>专员2</span>
                                                                   <span class="s-dd"
                                                                         style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                                                       <span field="accident-name"
                                                                             class="block-saved"></span>
                                                                       <span class="block-editing"
                                                                             style="display: none">
                                                                           <input autocomplete="off"
                                                                                  field="accident-name"
                                                                                  class="txt"
                                                                                  type="text"
                                                                                  style="width:80px;"
                                                                                  maxlength="50"/></span>
                                                                   </span>
                                    <span class="s-dt"><a class="icon_phone"></a>手机</span>
                                                                   <span class="s-dd"
                                                                         style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                                                       <span field="accident-mobile"
                                                                             class="block-saved"></span>
                                                                       <span class="block-editing"
                                                                             style="display: none"><input
                                                                               autocomplete="off"
                                                                               field="accident-mobile"
                                                                               class="txt"
                                                                               type="text"
                                                                               style="width:80px;"
                                                                               maxlength="50"/></span>
                                                                   </span>
                                    <span class="s-dt"><a class=""></a>微信号</span>
                                                                   <span class="s-dd">
                                                                       <input type="hidden" field="accident-openId">
                                                                       <input type="hidden" field="accident-id">
                                                                       <span field="accident-wxName"></span>
                                                                   </span>
                                    <span class="s-dt"><a class=""></a>昵称</span>
                                                                   <span class="s-dd">
                                                                       <span field="accident-wxNickName"></span>
                                                                       <span class="unbind-btn"   onselectstart="return false;">解除绑定</span>
                                                                   </span>
                                </div>
                                <div class="cl"></div>
                                <!--事故专员3-->
                                <div class="j_accident_item accident-item accident-3" style="display: none;">
                                    <span class="s-dt"><a class="icon_grayconnacter"></a>专员3</span>
                                                                   <span class="s-dd"
                                                                         style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                                                       <span field="accident-name"
                                                                             class="block-saved"></span>
                                                                       <span class="block-editing"
                                                                             style="display: none">
                                                                           <input autocomplete="off"
                                                                                  field="accident-name"
                                                                                  class="txt"
                                                                                  type="text"
                                                                                  style="width:80px;"
                                                                                  maxlength="50"/></span>
                                                                   </span>
                                    <span class="s-dt"><a class="icon_phone"></a>手机</span>
                                                                   <span class="s-dd"
                                                                         style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                                                       <span field="accident-mobile"
                                                                             class="block-saved"></span>
                                                                       <span class="block-editing"
                                                                             style="display: none"><input
                                                                               autocomplete="off"
                                                                               field="accident-mobile"
                                                                               class="txt"
                                                                               type="text"
                                                                               style="width:80px;"
                                                                               maxlength="50"/></span>
                                                                   </span>
                                    <span class="s-dt"><a class=""></a>微信号</span>
                                                                   <span class="s-dd">
                                                                       <input type="hidden" field="accident-openId">
                                                                       <input type="hidden" field="accident-id">
                                                                       <span field="accident-wxName"></span>
                                                                   </span>
                                    <span class="s-dt"><a class=""></a>昵称</span>
                                                                   <span class="s-dd">
                                                                       <span field="accident-wxNickName"></span>
                                                                       <span class="unbind-btn"  onselectstart="return false;">解除绑定</span>
                                                                   </span>
                                </div>
                                <div class="cl"></div>
                            </div>
                            <div class="acc-right">
                                扫扫成为事故专员
                            <span class="j_accident_qr accident-qr"><img width="100" height="100"
                                                                         src="${qr_code_show_url}"> </span>
                            </div>
                        </div>

                    </div>


                    <div class="cl"></div>
                    <%--<div>--%>
                    <%--扫描添加成为事故专业<img src="${qr_code_show_url}" />--%>
                    <%--</div>--%>
                    <div class="hr"></div>

                    <!--start products-main-->
                    <dl class="products-main">
                        <dt>主营产品</dt>
                        <dd>
                            <div class="cl"></div>
                            <table id="shopRegisterProductTable" class="products-info" cellspacing="0" cellpadding="0">
                                <colgroup>
                                    <col width="110">
                                    <col>
                                    <col width="90">
                                    <col width="90">
                                    <col width="90">
                                    <col width="80">
                                    <col width="80">
                                    <col width="80">
                                </colgroup>
                                <tr class="bg-grey-gradient">
                                    <td style="padding-left:5px;">商品编号</td>
                                    <td>品名</td>
                                    <td>品牌</td>
                                    <td>规格</td>
                                    <td>型号</td>
                                    <td>车牌</td>
                                    <td>车型</td>
                                    <td>单位</td>
                                </tr>
                            </table>
                        </dd>
                    </dl>
                    <!--end products-main-->
                    <div class="cl"></div>

                    <div class="hr"></div>

                    <!--start shop Image-->
                    <dl class="products-main">
                        <dt>店面照片</dt>
                        <dd>
                            <input autocomplete="off" type="hidden" id="shopImagePaths0" name="shopImagePaths[0]"/>
                            <input autocomplete="off" type="hidden" id="shopImagePaths1" name="shopImagePaths[1]"/>
                            <input autocomplete="off" type="hidden" id="shopImagePaths2" name="shopImagePaths[2]"/>
                            <input autocomplete="off" type="hidden" id="shopImagePaths3" name="shopImagePaths[3]"/>

                            <div style="height: 80px">
                                <div style="line-height:24px;float: left;width: 80px;height: 80px">选择本地图片</div>
                                <div style="float: left;width: 90px;height: 80px" id="shop_imageUploader"></div>
                                <div style="float: left;color: red;padding-left: 5px">提示：</div>
                                <div style="float: left">1. 如果您要上传店面图片，请点击以上按钮。<br>2. 所选图片都必须是 jpg、png 或 jpeg 格式。<br>3.
                                    每张图片的大小不得超过5M。<br> 4. 您可一次选择多张图片哦！最多上传 4 张图片。
                                </div>
                            </div>
                            <div id="shop_imageUploaderView" style="width:620px;height:180px;position: relative;"></div>
                        </dd>
                    </dl>
                    <!--end products-main-->
                    <div class="cl"></div>

                    <div class="hr"></div>
                    <!--start shop Image-->
                    <dl class="products-main">
                        <dt>营业执照 <span class="businessTip">友情提示：请上传营业执照进行资质认证哦。</span></dt>
                        <dd>
                            <div id="shopBusinessLicenseImageUploaderView"
                                 style="float: left;width:300px;height:200px;position: relative;"></div>

                            <input autocomplete="off" type="hidden" id="shopBusinessLicenseImagePath"
                                   name="shopBusinessLicenseImagePath"/>

                            <div style="padding-left:5px;float: left;width: 160px;height: 80px"
                                 id="shopBusinessLicenseImageUploader"></div>
                        </dd>
                    </dl>
                    <div class="hr"></div>
                    <div class="cl"></div>
                    <dl class="info-basic">
                        <dt><span class="label-title">店面地图</span></dt>
                        <div class="map_right"><img src="images/question.png" style="float:left; margin:6px 5px 0 0;"/>地图定位不准？<a
                                class="get" id="sendInActiveLocateStatus">一键反馈</a></div>
                        <div class="cl"></div>
                        <dd>

                            <%--<iframe src="" id="map_container_iframe" style="width: 376px;height: 187px;display:none;"--%>
                                    <%--scrolling="no"--%>
                                    <%--frameborder="0" allowtransparency="true"></iframe>--%>
                            <div id="allmap" style="width: 580px;height: 250px; display:none;border:5px green solid;"></div>

                        </dd>
                        <div class="cl"></div>
                    </dl>
                    <!--div.info-business-license-->

                </dd>
                <!--end content-details-->
                <div class="cl"></div>

            </dl>

        </div>
    </div>
</div>


<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>