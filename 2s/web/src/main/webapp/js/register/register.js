    $(function(){

    //---------------------------------------------------------------------------------------------------
    $("#searchInMapTxt").click(function(){
        var province = $("#select_province").find("option:selected").text();     //省
        var city = $("#select_city").find("option:selected").text();              //市
        var township = $("#select_township").find("option:selected").text();      //区（县）
        var downtown = $("#input_address_detail").val();                          //（路）街道
        var addrDetail = province + city + township + downtown;
        if(!downtown){
            alert("请输入详细地址!")
        }
        else{
            $("#allmap").show();
            $("#closemap").show();
            $("#sureBtn").show();
            $("#clearBtn").show();
            $("#tips").show();
            // 百度地图API功能
            var map = new BMap.Map("allmap");
            //地图默认显示为北京
            var point = new BMap.Point(116.331398,39.897445);
            map.centerAndZoom(point,15);
            // 创建地址解析器实例
            var myGeo = new BMap.Geocoder();
            // 将地址解析结果显示在地图上,并调整地图视野
            myGeo.getPoint(addrDetail, function(point){
                if (point) {
                    map.centerAndZoom(point, 16);
                    map.addOverlay(new BMap.Marker(point));
                }
            }, city);

            var mapType1 = new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP,BMAP_HYBRID_MAP]});
            var mapType2 = new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT});

            var overView = new BMap.OverviewMapControl();
            var overViewOpen = new BMap.OverviewMapControl({isOpen:true, anchor: BMAP_ANCHOR_BOTTOM_RIGHT});
            map.addControl(mapType1);          //2D图，卫星图
            map.addControl(mapType2);          //左上角，默认地图控件
            map.setCurrentCity("北京");        //由于有3D图，需要设置城市哦
            map.addControl(overView);          //添加默认缩略地图控件
            map.addControl(overViewOpen);      //右下角，打开


            $("#closemap").click(function(){
                    $(this).hide();
                    $("#allmap").hide();
                    $("#sureBtn").hide();
                    $("#clearBtn").hide();
                    $("#tips").hide();
                })
                $("#sureBtn").click(function(){
                    $(this).hide();
                    $("#allmap").hide();
                    $("#closemap").hide();
                    $("#clearBtn").hide();
                    $("#tips").hide();
//                    $("#input_coordinateLon").val(e.point.lng);
//                    $("#input_coordinateLat").val(e.point.lat);
                })
                $("#clearBtn").click(function(){
                    $(this).hide();
                    $("#allmap").hide();
                    $("#closemap").hide();
                    $("#sureBtn").hide();
                    $("#tips").hide();
                    $("#input_coordinateLon").val("");
                    $("#input_coordinateLat").val("");
                })


            //单击获取点击的经纬度
            map.addEventListener("click", function(e) {
                map.clearOverlays();
                $("#input_coordinateLon").val("");
                $("#input_coordinateLat").val("");
                point = new BMap.Point(e.point.lng, e.point.lat);

                // 创建标注
                marker = new BMap.Marker(point);
                map.addOverlay(marker);

                myGeo.getLocation(e.point, function(rs){
                var addComp = rs.addressComponents;

                $("#select_province").find("option:selected").text(addComp.province);
                $("#select_city").find("option:selected").text(addComp.city);
                $("#select_township").find("option:selected").text(addComp.district);
                $("#input_address_detail").val(addComp.street + addComp.streetNumber);


                $("#input_coordinateLon").val(e.point.lng);
                $("#input_coordinateLat").val(e.point.lat);


		    });


        });




    }
})



})


var mustValidate={};
var validateResult = {};
Bcgogo = {
    MessageEventListenerFlag: false
};

var register = {
    common: {E01: "<a class='right'></a>",
        E02: "<a class='wrong'></a><span class='red_color font12'>网络异常</span>"},
    name: {E01: "",
        init: "<span class='gray_color font12'>字数最多不超过25个字</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写单位名称!</span>",
        duplicate: "<a class='wrong'></a><span class='red_color font12'>该单位名称已存在！</span>",
        right: "<a class='right'></a>"},
    storeManager: {E01: "",
        init: "<span class='gray_color font12'>字数最多不超过10个字</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写管理员姓名！</span>",
        right: "<a class='right'></a>"},
    storeManagerMobile: {E01: "",
        init: "<span class='gray_color font12'>请填写手机号！</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写手机号！</span>",
        duplicate: "<a class='alerts'></a><span class='blue_color font12'>此手机号码已被注册</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>手机号格式不正确！</span>",
        right: "<a class='right'></a>"},
    address: {E01: "",
        init: "<span class='gray_color font12'>请选择地址！</span>",
        empty: "<div style='float:left;padding:6px 0px 0px 0px;'><a class='wrong'></a></div><span class='red_color font12'>便于您在百度地图中推广，请填写详细地址！</span>",
        right: "<a class='right'></a>"},
    businessScopes: {E01: "",
        init: "<span class='gray_color font12'>请选择您的经营产品！</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请选择您的经营产品！</span>",
        right: "<a class='right'></a>"
    },
    serviceCategory: {E01: "",
        init: "",
        empty: '<a class="wrong" href="#"></a><span class="red_color font12">请选择您的服务范围</span>',
        right: ""
    },
    businessLicense: {E01: "",
        init: "<span class='gray_color font12'>图片大小不超过2M！</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>图片格式不正确！</span>",
        maxOver: "<a class='wrong'></a><span class='red_color font12'>图片大小不超过2M！</span>",
        right: "<a class='right'></a>"},
    shopPhoto: {E01: "",
        init: "<span class='gray_color font12'>图片大小不超过2M！</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>图片格式不正确！</span>",
        maxOver: "<a class='wrong'></a><span class='red_color font12'>图片大小不超过2M！</span>",
        right: "<a class='right'></a>"},
    invitationCode: {E01: "",
        init: "<span class='gray_color font12'>请输入邀请码！</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请输入邀请码！</span>",
        overdue: "<a class='wrong'></a><span class='red_color font12'>邀请码已过期！</span><a class='get getSystemInvite'>获取邀请码</a>",
        error: "<a class='wrong'></a><span class='red_color font12'>无效的邀请码！</span>",
        already: "<a class='already'>您已获取过</a>",
        right: "<a class='right'></a>"},
    agent: {E01: "",
        init: "<span class='gray_color font12'>字数最多不超过10个字</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写本店业务员！</span>",
        right: "<a class='right'></a>"},
    agentMobile: {E01: "",
        init: "<span class='gray_color font12'>此处为业务员联系方式</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写业务员联系方式！</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>手机号格式不正确！</span>",
        right: "<a class='right'></a>"},
    shopVersionId: {E01: "",
        init: "<span class='gray_color font12'>请选择注册店铺类型</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请选择店铺类型！</span>",
        right: "<a class='right'></a>"},
    contact_owner: {E01: "",
        init: "<span class='gray_color font12'>字数最多不超过10个字</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写姓名！</span>",
        right: "<a class='right'></a>"
    },
    contact_name:{E01: "",
        init: "<span class='gray_color font12'>字数最多不超过10个字</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>请填写姓名！</span>",
        right: "<a class='right'></a>"
    },
    contact_phone: {E01: "",
        init: "<span class='gray_color font12'>请填写手机号</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>手机号为空！</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>手机号不正确！</span>",
        right: "<a class='right'></a>",
        duplicate: "<a class='alerts'></a><span class='blue_color'>此号码已被注册</span>"
    },
    contact_mobile: {E01: "",
        init: "<span class='gray_color font12'>请填写手机号</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>手机号不正确！</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>手机号为空！</span>",
        right: "<a class='right'></a>",
        duplicate: "<a class='alerts'></a><span class='blue_color font12'>此号码已被注册</span>"
    },
    contact_email: {E01: "",
        init: "<span class='gray_color font12'>请填写Email</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>Email不正确！</span>",
        right: "<a class='right'></a>"
    },
    product: {E01: "",
        init: "<span class='gray_color font12'>请填写主营产品</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>产品信息输入不正确</span>",
        right: "<a class='right'></a>"
    },
    contact_qq: {E01: "",
        init: "<span class='gray_color font12'>请填写QQ号</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>QQ号不正确！</span>",
        right: "<a class='right'></a>"
    },
    softPrice: {E01:"",
        init: "<span class='gray_color font12'>请填写销售价格</span>",
        error: "<a class='wrong'></a><span class='red_color font12'>销售价格不正确！</span>",
        empty: "<a class='wrong'></a><span class='red_color font12'>销售价格为空！</span>",
        right: "<a class='right'></a>"
    }
};
//if(GLOBAL.Util.getUrlParameter("paramNeedVerify")!=null && GLOBAL.Util.getUrlParameter("paramNeedVerify")=='true'){
//    var mustValidate = ["name", 'address','businessScopes','invitationCode','contact_owner', 'contact_mobile'];
//}else{
//    var mustValidate = ["name", 'address','businessScopes','contact_owner', 'contact_mobile'];
//}
//var validateResult = {};

$(function(){

    if($("#registerType").val()=="SALES_REGISTER"){
        mustValidate['step1']=["name", 'address','storeManager','storeManagerMobile','contact_owner', 'contact_mobile'];
        mustValidate['step2']=['businessScopes'];
        mustValidate['step3']=['softPrice'];
    }else if($("#registerType").val()=="SUPPLIER_REGISTER"){
        mustValidate['step1']= ["name", 'address','agent','agentMobile','contact_owner', 'contact_mobile'];
        mustValidate['step2']=['businessScopes'];
    }else if($("#registerType").val()=="SUPPLIER"||$("#registerType").val()=="CUSTOMER"){
        mustValidate['step1']=["name", 'address','invitationCode','contact_owner', 'contact_mobile'];
        mustValidate['step2']=['businessScopes'];
    }

    $("#nextStep1").click(function(){
        if(is4SShopVersion()){
            $("#contacts0\\.name").val($("#input_storeManager").val());
            $("#contacts0\\.mobile").val($("#input_storeManagerMobile").val());

        }
        if(!validateShopBase()){
            return;
        }
        showStep('second');
    });

    $("#nextStep2").click(function(){
        if(!validateShopBusiness()){
            return;
        }
        showStep('third');
    });

//    if($("#registerType").val()=="SALES_REGISTER"){
//        $('[name="shopVersionId"]').live("change",function(){
//
//        });
//    }

    $("#registerBtn").bind("click",function(){
        if($(this).attr("lock")){
            return;
        }
        $(this).attr("lock",true);
        var isValidate=true;
        if($("#oneTime").attr("checked")) {
            $(mustValidate['step3']).each(function(){
                $("[node-type='" + this + "']").blur();
            });
            $(mustValidate['step3']).each(function(){
                if(GLOBAL.isEmpty(validateResult[this])){
                    isValidate = false;
                    $("[node-type*='" + this + "_tips']").html(register[this].empty);
                }else if(!validateResult[this]){
                    isValidate = false;
                }
            });
            if(!is4SShopVersion() && $("[node-type='softPrice']").val() == 0) {
                nsDialog.jAlert("软件销售价不能为0");
                $(this).removeAttr("lock");
                return;
            }
        }

        if(!$("#licenseChk").attr("checked")){
            nsDialog.jAlert("请遵守《统购信息软件服务条款》");
            isValidate=false;
        }
        if(!isValidate){
            $(this).removeAttr("lock");
            return;
        }
        resetThirdCategoryIdStr();
        var paramJson = {};
        //汽修有服务范围和代理产品
        if(!APP_BCGOGO.Permission.Version.WholesalerVersion){
            var serviceCategoryIdArr=new Array();
            $(".service-category-item:checked").each(function(){
                serviceCategoryIdArr.push($(this).val());
            });
            paramJson.serviceCategoryIds=serviceCategoryIdArr.toString();
            var agentProductIdArr=new Array();
            $(".agent-product-item:checked").each(function(){
                agentProductIdArr.push($(this).val());
            });
            paramJson.agentProductIds=agentProductIdArr.toString();
        }
        if($("#vehicleBrandModelDiv")[0]){
            arrayObjectAppendToData(paramJson,"shopVehicleBrandModelDTOs", App.components.multiSelectTwoDialog.getAddedData());
        }
        var thirdCategoryIdStr = "";
        if($("#businessScopeDiv")[0] && App.components.multiSelectTwoDialogTree){
            var addedData =  App.components.multiSelectTwoDialogTree.getAddedLeafDataList();
            if (addedData) {
                $.each(addedData,function(index,val){
                    thirdCategoryIdStr+=val.idStr+",";
                });
            }
            if(!G.Lang.isEmpty(thirdCategoryIdStr)){
                thirdCategoryIdStr = thirdCategoryIdStr.substr(0,thirdCategoryIdStr.length-1);
            }
            paramJson.thirdCategoryIdStr = thirdCategoryIdStr;
        }
        $("#registerShopForm").ajaxSubmit({
            url:"shopRegister.do?method=saveShopInfo",
            data:paramJson,
            success : function(result){
                result = $.parseJSON(result);
                if(!result.success){
                    for (var k in result.data) {
                        $("div[node-type='" + k + "_tips']").html(register[k][result.data[k]]);
                    }
                    $(this).removeAttr("lock");
                }else{
                    nsDialog.jAlert(result.msg,null,function(){
                        if ($("#registerShopType").val() == "UPDATE") {
                            window.location = "user.do?method=createmain";
                        } else {
                            window.location.assign("j_spring_security_logout");
                        }
                    })
                }
                $(this).removeAttr("lock");
            },
            error:function(){
                $(this).removeAttr("lock");
                nsDialog.jAlert("网络异常。");
            }
        });

    });

    function searchCoordinate() {
        var $iframe = $("#map_container_iframe");
        var province = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML,
            city = $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML ,
            region = $("#select_township")[0].options[$("#select_township")[0].selectedIndex].innerHTML ,
            addressDetail = $("#input_address_detail").val();
        if (!$("#select_city").val()) {
            nsDialog.jAlert("请选择城市。");
            return;
        }
        if (!addressDetail) {
            nsDialog.jAlert("请输入详细地址。");
            return;
        }
        if (city == "市辖区" || city == "县") {
            city = province;
        }
        $iframe[0].src = "api/proxy/baidu/map/shop/register?city=" + encodeURI(city) + "&region" + (region ? encodeURI(region) : "")
            + "&addressDetail=" + (addressDetail ? encodeURI(addressDetail) : "") + "&origin=" + encodeURIComponent(window.location.origin);
        Mask.Login();
        $("#map_container_iframe_div").show();
        addCoordinateResultEventListener();
    }

    $("[action=searchInMap]").click(function(){
        searchCoordinate();
    });

    $("[action=cancle_map_container_iframe]").click(function(){
        setCoordinateResult("","");
        $("#map_container_iframe_div").hide();
        Mask.Logout();
    });
    $("[action=close_map_container_iframe]").click(function(){
        $("#map_container_iframe_div").hide();
        Mask.Logout();
    });

    function addCoordinateResultEventListener () {
        if (!Bcgogo.MessageEventListenerFlag) {
            window.addEventListener("message", function (event) {
                if ("coordinate" === event.data['type']) {
                    setCoordinateResult(event.data['lng'], event.data['lat']);
                }/* else if ("noresult" === event.data['type'] && (!event.data.lat || !event.data.lng)) {
                    nsDialog.jAlert("地图中未找到,请手动在地图中标示！");
                }*/
            }, false);
            Bcgogo.MessageEventListenerFlag = true;
        }
    }
});

function reset4SShopVersion(){
    if(is4SShopVersion()){
        $(".J_4s_shop_hidden").hide();
    }else{
        $(".J_4s_shop_hidden").show();
    }
}

function is4SShopVersion() {
    var fourSShopVersion = $("#fourSShopVersion").val();
    var selectShopVersion = $("#shopVersionSelector").val();
    if (G.Lang.isNotEmpty(fourSShopVersion) && G.Lang.isNotEmpty(selectShopVersion) && fourSShopVersion.contains(selectShopVersion)) {
        return true;
    }
    return false;
}

function resetSoftVersion(){
    if($("#registerType").val()=="SALES_REGISTER"){
        $('.register-content-business').hide();
        $('.register-content-image').hide();
        $('.register-content-base').show();
    }else if($("#registerType").val()=="SUPPLIER_REGISTER"){
        var customerId=GLOBAL.Util.getUrlParameter("customerId");
        window.location.href="shopRegister.do?method=registerMain&registerType="+$("#registerType").val()+"&customerId="+customerId;
    }else{
        window.location.href="shopRegister.do?method=registerMain&registerType="+$("#registerType").val();
    }
}

function validateShopBase(){
    $(mustValidate['step1']).each(function(){
        $("[node-type='" + this + "']").blur();
    });
    var isValidate=true;
    $(mustValidate['step1']).each(function(){
        if(GLOBAL.isEmpty(validateResult[this])){
            isValidate = false;
            $("[node-type*='" + this + "_tips']").html(register[this].empty);
        }else if(!validateResult[this]){
            isValidate = false;
        }
    });
    //校验联系人
    $("input[node-type='contact_phone']").each(function(){      //不能填错，可不填
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        var thisNodeType = _thisDom.attr("node-type");
        if (!GLOBAL.isEmpty(thisVal) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(_thisDom.val(), null, null)) {
            getShareUsedTips(_thisDom).html(register[thisNodeType].error);
            isValidate = false;
        }
    });

    var contactMobiles = new Array();
    $("input[name$='mobile']").each(function (index) {
        contactMobiles.push($(this).val());
    });
    if (isMobileDuplicate(contactMobiles)) {
        nsDialog.jAlert("联系人手机号重复。");
        isValidate = false;
    }

    $("input[node-type='contact_qq']").each(function(){
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        var thisNodeType = _thisDom.attr("node-type");
        if (!GLOBAL.isEmpty(thisVal)&& !APP_BCGOGO.Validator.stringIsQq(_thisDom.val(), null, null)) {
            getShareUsedTips(_thisDom).html(register[thisNodeType].error);
            isValidate = false;
        }
    });

    $("input[node-type='contact_email']").each(function(){
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        var thisNodeType = _thisDom.attr("node-type");
        if (!GLOBAL.isEmpty(thisVal)&& !APP_BCGOGO.Validator.stringIsEmail(_thisDom.val(), null, null)) {
            getShareUsedTips(_thisDom).html(register[thisNodeType].error);
            isValidate = false;
        }
    });
    if (!$("#input_coordinateLat").val() || !$("#input_coordinateLat").val()) {
        if (isValidate)nsDialog.jAlert("请在地图中定位。");
        isValidate = false;
    }
    return isValidate;
}
//--------step2
function validateShopBusiness(){
    var isValidate=true;
    //经营范围
    if(!APP_BCGOGO.Permission.Version.WholesalerVersion){
        if($(".service-category-item:checked").length==0){
            $("[node-type='serviceCategory_tips']").html(register['serviceCategory'].empty);
//            validateResult['serviceCategory'] = false;
            isValidate=false;
        }
    }
    //经营产品        mustValidate['step2']
    if(G.isEmpty(App.components.multiSelectTwoDialogTree.getAddedTreeNodeDataList()) && !is4SShopVersion()){//经营产品 为空 处理校验逻辑
        $("[node-type='businessScopes_tips']").html(register['businessScopes'].empty);
        isValidate=false;
    }

//    if($("#businessScopeSelectdTd").find("div").size()< 1) {
//        $("#businessScopeSelectdTd").attr("colSpan", 3);
//        if ($("#businessScopes_tips_td").length < 1) {
//            $("#businessScopeSelectdTd").after('<td id="businessScopes_tips_td" style="text-align:left;"><div class="tips" node-type="businessScopes_tips"></div></td>');
//        }
//        $("div[node-type='businessScopes_tips']").html(register["businessScopes"].empty);
//        validateResult["businessScopes"] = false;
//    } else {
//        $("#businessScopeSelectdTd").attr("colSpan", 4);
//        $("#businessScopes_tips_td").remove();
//        validateResult["businessScopes"] = true;
//    }
    //主营产品
    if (!is4SShopVersion()) {
        var length = $(".productItem").size();
        if (length > 10) {
            showProductErrorMessage(false, "最多只能添加10条主营产品");
            isValidate = false;
        } else if (length < 5) {
            showProductErrorMessage(false, "最少要添加5条主营产品");
            isValidate = false;
        }
        if (length > 1 && checkProductSame()) {
            showProductErrorMessage(false, "产品信息有重复内容，请修改或删除。");
            isValidate = false;
        }

        if (length > 1 && checkCommodityCodeSame()) {
            showProductErrorMessage(false, "产品编码有重复内容，请修改或删除。");
            isValidate = false;
        }
        var checkInfo = checkProductInfo();
        $("input[id$='.commodityCode']").each(function () {
            var idPrefix = $(this).attr("id").split(".")[0];
            var index = (idPrefix.split("s")[1]) * 1 + 1;
            var productName = $("#" + idPrefix + "\\.name").val();
            var sellUnit = $("#" + idPrefix + "\\.sellUnit").val();
            if ((productName.trim() == "" || sellUnit.trim() == ""  ) && $("#" + idPrefix + "\\.deleteProduct").length > 0) {
                $("#" + idPrefix + "\\.deleteProduct").click();
            }
        });
        if (checkInfo && checkInfo != null) {
            isValidate = false;
        }
    }
    return isValidate;
}

function showStep(stepName){
    if(G.isEmpty(stepName)){
        return;
    }
    $('.register-content').hide();
    if(stepName=="second"){
        $('.register-content-business').show();
    }else if(stepName=="third"){
        $('.register-content-image').show();
    }else if(stepName=="first"){
        $('.register-content-base').show();
    }
}

function focusAndSelect(idName) {
    $("#" + idName).focus().select();
}

$(function(){

//        var r = bcgogo.get("shop.do?method=getname");
//        $("span_userName").innerHTML = r.userName;
    var regC = /[^\u4E00-\u9FA5]/g;//过滤掉非汉字字符
    var regC2 = /[\u4E00-\u9FA5]/g;//过滤掉汉字字符
    var regNum = /[0-9]/g;//过滤掉数字字符
    var regNum2 = /[^0-9]/g;//过滤掉非数字字符

    var regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

    var regSy = new RegExp("[`~!@#$^&()=|\\\\{\\}%_\\+\"':;',\\[\\]<>/?~！@#￥……&（）——|{}【】‘；：”“'。，、？·～《》]", "g");

    //非电子邮件符号的验证
    var regSy2 = new RegExp("[`~!#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\]<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");

    //过滤掉所有特殊符号
    var regS = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");

    $("#input_name").live("focus",function () {
        mustInputFocus($(this));
    }).live("blur", function () {
            $(this).val($.trim($(this).val()));
            var thisVal = $(this).val();
            var thisNodeType = $(this).attr("node-type");
            if (GLOBAL.isEmpty(thisVal)) {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
                validateResult[thisNodeType] = false;
            } else {
                $.ajax({
                    type: "POST",
                    url: "shop.do?method=checkshopname",
                    async: true,
                    data: {name: $("#input_name").val()},
                    cache: false,
                    dataType: "json",
                    success: function (jsonStr) {
                        if (typeof jsonStr == "string") {
                            if (jsonStr == "false") {
                                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].duplicate);
                                validateResult[thisNodeType] = false;
                            } else {
                                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
                                validateResult[thisNodeType] = true;
                            }
                        } else {
                            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].E01);
                            validateResult[thisNodeType] = false;
                        }
                    }, error: function () {
                        $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].E01);
                        validateResult[thisNodeType] = false;
                    }
                });
            }
        });

    $("#contacts0\\.name").live("focus",function () {
        mustInputFocus($(this));
    }).live("blur", function () {
            var _thisDom = $("#contacts0\\.name");
            var thisVal = _thisDom.val();
            var thisNodeType = _thisDom.attr("node-type");
            if(is4SShopVersion()){
                validateResult[thisNodeType] = true;
            }else{
                if (GLOBAL.isEmpty(thisVal)) {
                    getShareUsedTips(_thisDom).html(register[thisNodeType].empty);
                    validateResult[thisNodeType] = false;
                }else{
                    getShareUsedTips(_thisDom).html(register[thisNodeType].right);
                    validateResult[thisNodeType] = true;
                }
            }

        });

    $("#input_storeManager").bind("focus",function () {
        mustInputFocus($(this));
    }).bind("blur", function () {
            var _thisDom = $("#input_storeManager");
            var thisVal = _thisDom.val();
            var thisNodeType = _thisDom.attr("node-type");
            if (GLOBAL.isEmpty(thisVal)) {
                getShareUsedTips(_thisDom).html(register[thisNodeType].empty);
                validateResult[thisNodeType] = false;
            }else{
                getShareUsedTips(_thisDom).html(register[thisNodeType].right);
                validateResult[thisNodeType] = true;
            }
        });

    //非必填的联系人手机
    $("input[node-type='contact_phone']").live("blur", function(){
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        var thisNodeType = _thisDom.attr("node-type");
        if (!GLOBAL.isEmpty(thisVal)&& !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(_thisDom.val(), null, null)) {
            getShareUsedTips(_thisDom).html(register[thisNodeType].error);
            validateResult[thisNodeType] = false;
        }else{
            getShareUsedTips(_thisDom).html(register[thisNodeType].right);
        }
    });

    $("input[node-type='contact_qq']").live("blur", function(){
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        var thisNodeType = _thisDom.attr("node-type");
        if (!GLOBAL.isEmpty(thisVal)&& !APP_BCGOGO.Validator.stringIsQq(_thisDom.val(), null, null)) {
            getShareUsedTips(_thisDom).html(register[thisNodeType].error);
            validateResult[thisNodeType] = false;
        }else{
            getShareUsedTips(_thisDom).html(register[thisNodeType].right);
        }
    });

    $("input[node-type='contact_email']").live("blur", function(){
        var _thisDom = $(this);
        var thisVal = _thisDom.val();
        var thisNodeType = _thisDom.attr("node-type");
        if (!GLOBAL.isEmpty(thisVal)&& !APP_BCGOGO.Validator.stringIsEmail(_thisDom.val(), null, null)) {
            getShareUsedTips(_thisDom).html(register[thisNodeType].error);
            validateResult[thisNodeType] = false;
        }else{
            getShareUsedTips(_thisDom).html(register[thisNodeType].right);
        }
    });

    $("#contacts0\\.mobile").bind("focus",function () {
        mustInputFocus($(this));
    }).bind("blur", function () {
            var _thisDom = $("#contacts0\\.mobile");
            var thisVal = _thisDom.val();
            var thisNodeType = _thisDom.attr("node-type");
            if(is4SShopVersion()){
                validateResult[thisNodeType] = true;
            }else{
                if (GLOBAL.isEmpty(thisVal)) {
                    getShareUsedTips(_thisDom).html(register[thisNodeType].empty);
                    validateResult[thisNodeType] = false;
                } else if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(_thisDom.val(), null, null)) {
                    getShareUsedTips(_thisDom).html(register[thisNodeType].error);
                    validateResult[thisNodeType] = false;
                } else {
                    validateResult[thisNodeType] = true;
                    getShareUsedTips(_thisDom).html(register[thisNodeType].right);
                }
            }

        });

    $("#input_storeManagerMobile").bind("focus",function () {
        mustInputFocus($(this));
    }).bind("blur", function () {
            var _thisDom = $("#input_storeManagerMobile");
            var thisVal = _thisDom.val();
            var thisNodeType = _thisDom.attr("node-type");
            if (GLOBAL.isEmpty(thisVal)) {
                getShareUsedTips(_thisDom).html(register[thisNodeType].empty);
                validateResult[thisNodeType] = false;
            } else if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(_thisDom.val(), null, null)) {
                getShareUsedTips(_thisDom).html(register[thisNodeType].error);
                validateResult[thisNodeType] = false;
            } else {
                $.ajax({
                    type: "POST",
                    url: "shop.do?method=checkStoreManagerMobile",
                    async: true,
                    data: {mobile: thisVal},
                    cache: false,
                    dataType: "json",
                    success: function (jsonStr) {
                        if (typeof jsonStr == "string") {
                            if (jsonStr == "true") {
                                getShareUsedTips(_thisDom).html(register[thisNodeType].duplicate);
                                validateResult[thisNodeType] = true;
                            } else {
                                getShareUsedTips(_thisDom).html(register[thisNodeType].right);
                                validateResult[thisNodeType] = true;
                            }
                        } else {
                            getShareUsedTips(_thisDom).html(register[thisNodeType].E01);
                            validateResult[thisNodeType] = false;
                        }
                    }, error: function () {
                        getShareUsedTips(_thisDom).html(register[thisNodeType].E01);
                        validateResult[thisNodeType] = false;
                    }
                });
            }
        });

    $("#input_address").bind("focus",function () {
        mustInputFocus($(this));
    }).bind("blur", function () {
            var selectAddName = getSelectAddress();
            if (selectAddName && $(this).val().indexOf(selectAddName) != 0) {
                $(this).val(selectAddName);
            }
            var thisVal = $(this).val();
            var thisNodeType = $(this).attr("node-type");
            if (GLOBAL.isEmpty(thisVal) || GLOBAL.isEmpty($("#select_city").val())) {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
                validateResult[thisNodeType] = false;
                return;
            }else {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
                validateResult[thisNodeType] = true;
            }
        }).bind("keyup", function () {
            var selectAddName = getSelectAddress();
            if (selectAddName && $(this).val().indexOf(selectAddName) != 0) {
                $(this).val(selectAddName);
            }
        });

    $(".service-category-item").live("click",function(){
        var thisNodeType ='serviceCategory';
        if( $(".service-category-item:checked").length==0){
            $("[node-type='serviceCategory_tips']").html(register[thisNodeType].empty);
            validateResult[thisNodeType] = false;
        }else{
            $("[node-type='serviceCategory_tips']").html(register[thisNodeType].right);
            validateResult[thisNodeType] = true;
        }
    });

//    $("input[name='businessScopes']:checkbox").bind("change",function(){
//        var thisNodeType = $(this).attr("node-type");
//       if($("input[name='businessScopes']:checkbox:checked").size()<1 && !$("#input_otherBusinessScope").val()){
//           $("div[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
//           validateResult[thisNodeType] = false;
//       } else {
//           $("div[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
//           validateResult[thisNodeType] = true;
//       }
//    });
//    $("#input_otherBusinessScope").bind("blur",function(){
//        var thisNodeType = $(this).attr("node-type");
//       if($(this).val()){
//         $("#check_otherBusinessScope").attr("checked",true);
//           $("div[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
//           validateResult[thisNodeType] = true;
//       }else{
//           $("#check_otherBusinessScope").attr("checked",false);
//           if($("input[name='businessScopes']:checkbox:checked").size()<1 ){
//               $("div[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
//               validateResult[thisNodeType] = false;
//           } else {
//               $("div[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
//               validateResult[thisNodeType] = true;
//           }
//       }
//    });
    $(".getSystemInvite").live("click", function () {
        var mobile = $("#input_mobile").val();
        var code = $("#invitationCode").val();
        if(!mobile){
            nsDialog.jAlert("请填写手机号！");
            return;
        }else if(!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)){
            nsDialog.jAlert("请填写正确的手机号！");
            return;
        }
        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile) || !code || code.length != 6 || $(this).attr("lock")) {
            return;
        }
        $(this).attr("lock","lock");
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "invitationCodeSms.do?method=getSystemInvite",
            data: { mobile: mobile, code:code },
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jAlert("邀请码发送成功！", "", function () {
                        $("[node-type='invitationCode_tips']").html(register['invitationCode'].already);
                    });
                }else{
                    $("[node-type='invitationCode_tips']").html(register['invitationCode'].error);
                }
                $(this).removeAttr("lock");
            }
        });
    });

    //绑定下拉列表的值        select_province    select_city    select_township
    provinceBind();
    $("#select_province").bind("change",function(){
        cityBind(this);
        $("[node-type='address_tips']").html(register["address"].empty);
        validateResult["address"] = false;
    });
    $("#select_city").bind("change",function(){
        licensePlate(this);
        townshipBind(this);
        if (!$(this).val()) {
            $("[node-type='address_tips']").html(register["address"].empty);
            validateResult["address"] = false;
        }else{
            if (!$("#input_address_detail").val()) {
                $("[node-type='address_tips']").html(register["address"].empty);
                validateResult["address"] = false;
            }else{
                $("[node-type='address_tips']").html(register["address"].right);
                validateResult["address"] = true;
            }
        }
    });
    $("#input_address_detail").bind("keyup click", function () {
        if (!$("#select_city").val() || !$("#select_province").val()) {
            $("[node-type='address_tips']").html(register["address"].empty);
            validateResult["address"] = false;
        } else if (!$(this).val()) {
            $("[node-type='address_tips']").html(register["address"].empty);
            validateResult["address"] = false;
        } else {
            $("[node-type='address_tips']").html(register["address"].right);
            validateResult["address"] = true;
        }
        setAddress();
    });

    $("#select_township")[0].onchange = function () {
        if (this.selectedIndex != 0) {
            $("#input_areaId")[0].value = this.value;
            setAddress();
        }
        else {
            $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;
        }
    };

    $("#input_address").val("");
    $("#input_address_detail").val("");
    $("#operationMode").bind("change",function(){
        if($(this).val() == "专卖店"){
            if(GLOBAL.isEmpty($("#operationModeBrand").val())){
                $("#operationModeBrand").addClass("gray_color").css("color", "#999999");
                $("#operationModeBrand").val($("#operationModeBrand").attr("init_word"));
            }
            $("#operationModeBrand").show();
        }else{
            $("#operationModeBrand").addClass("gray_color").css("color", "#999999");
            $("#operationModeBrand").val($("#operationModeBrand").attr("init_word"));
            $("#operationModeBrand").hide();
        }
    });


    $("#operationModeBrand").bind("keydown",function () {
        if ($(this).hasClass("gray_color") && $(this).val() == $(this).attr("init_word")) {
            $(this).removeClass("gray_color").css("color","#515151").val("");
        }
    }).bind("blur", function () {
            if (!$(this).val()) {
                $(this).addClass("gray_color").css("color","#999999");
                $(this).val($(this).attr("init_word"));
            }
        });

    $("#shopPhoto,#businessLicense").bind("change",function(){
        var imgType = ["bmp" , "gif" , "png" , "jpg"];
        var thisNodeType = $(this).attr("node-type");
        var thisId = $(this).attr("id");
        if(!$(this).val()){
            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
        }else if(!RegExp("\.(" + imgType.join("|") + ")$", "i").test($(this).val().toLowerCase())){
            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].error);
            $(this).val("");
            $("#"+thisId+"Info").val("");
        } else {
            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
            $("#" + thisId + "Info").val($(this).val());
        }
    });
    $("#invitationCode").bind("keydown",function () {
        if ($(this).hasClass("gray_color") && $(this).val() == $(this).attr("init_word")) {
            $(this).removeClass("gray_color").css("color", "#515151").val("");
        }
    }).bind("blur",function () {
            var thisNodeType = $(this).attr("node-type");
            if (!$(this).val()) {
                $(this).addClass("gray_color").css("color", "#999999");
                $(this).val($(this).attr("init_word"));
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
            }else{
                $.ajax({
                    type: "POST",
                    url: "shopRegister.do?method=validateInvitationCode",
                    async: true,
                    data: {invitationCode: $("#invitationCode").val()},
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
                            validateResult[thisNodeType] = true;
                        } else {
                            for (var k in result.data) {
                                $("[node-type='" + thisNodeType + "_tips']").html(register[k][result.data[k]]);
                                validateResult[thisNodeType] = false;
                            }
                        }
                    }, error: function () {
                        $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].E01);
                        validateResult[thisNodeType] = false;
                    }
                });
            }
        }).bind("focus", function () {
            var thisNodeType = $(this).attr("node-type");
            if($(this).val() == $(this).attr("init_word")){
                $(this).val("");
            }
            if (GLOBAL.isEmpty($(this).val()) || $(this).val() == $(this).attr("init_word")) {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].init);
            }
        });

    $("#input_agent").bind("focus",function(){
        mustInputFocus($(this));
    }).bind("blur", function () {
            var thisNodeType = $(this).attr("node-type");
            if (GLOBAL.isEmpty($(this).val())) {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
                validateResult[thisNodeType] = false;
            }else{
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
                validateResult[thisNodeType] = true;
            }
        });

    $("#input_agentMobile").bind("focus",function () {
        mustInputFocus($(this));
    }).bind("blur", function () {
            var thisVal = $(this).val();
            var thisNodeType = $(this).attr("node-type");
            if (GLOBAL.isEmpty(thisVal)) {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
                validateResult[thisNodeType] = false;
            } else if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($(this).val(), null, null)) {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].error);
                validateResult[thisNodeType] = false;
            } else {
                $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
                validateResult[thisNodeType] = true;
            }
        });

    $("input[name='shopVersionId']").bind("change",function(){
        var thisNodeType = $(this).attr("node-type");
        if($("input[name='shopVersionId']:radio:checked").size()<1){
            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
            validateResult[thisNodeType] = false;
        } else {
            $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
            validateResult[thisNodeType] = true;
        }
    });

    $("#input_softPrice").bind("focus",function () {
        mustInputFocus($(this));
    }).bind("blur", function () {
            var thisVal = $(this).val();
            var thisNodeType = $(this).attr("node-type");
            if(!is4SShopVersion()){
                if (GLOBAL.isEmpty(thisVal)) {
                    $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].empty);
                    validateResult[thisNodeType] = false;
                } else if (!APP_BCGOGO.Validator.stringIsInt($(this).val())) {
                    $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].error);
                    validateResult[thisNodeType] = false;
                } else {
                    $("[node-type='" + thisNodeType + "_tips']").html(register[thisNodeType].right);
                    validateResult[thisNodeType] = true;
                }
            }else{
                validateResult[thisNodeType] = true;
            }
        });

    $("input[name='chargeType']").click(function(){
       if($(this).val() == 'YEARLY') {
           $("#input_softPrice").val('').attr("disabled","disabled");
           $("[node-type='softPrice_tips']").html('');
       } else if($(this).val() == 'ONE_TIME') {
           $("#input_softPrice").removeAttr("disabled");
       }
    });

    $(".more").toggle(
        function () {
            $(".moreRegisterInfo").show();
            $(".more").html("收起");
            $(".more").css("background-image", "url(images/upArrow.png)");
        },
        function () {
            $(".moreRegisterInfo").hide();
            $(".more").html("更多");
            $(".more").css("background-image", "url(images/downArrow.png)");
        }
    );

    $("#addContact").bind("click", function(){
        var contactNum=$(".contact-info").size();
        if(contactNum>=3){
            nsDialog.jAlert("最多添加3个联系人。");
            return;
        }
        $("#contactContainer").append($(generateContactLine(++contactNum)));
    });

});

function initServiceCategory(){
    APP_BCGOGO.Net.asyncGet({
        "url": "shop.do?method=getAllServiceCategoryLeaf",
        type: "POST",
        cache: false,
        "dataType": "json",
        success: function (nodeList) {
            if(G.isEmpty(nodeList)){
                return;
            }
            var nodeStr='';
            for(var i=0;i<nodeList.length;i++){
                var node=nodeList[i];
                var name=G.normalize(node.text)
                nodeStr+='<label class="lbl"><input class="service-category-item" type="checkbox" value="'+G.normalize(node.idStr)+'"><span style="">'+name+'</span></label>';
            }
            $(".service-category-container").append(nodeStr);
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}


/**
 * 校验新增的手机列表里面号码是否有重复
 * 重复返回true 否则false
 * @param mobiles
 */
function isMobileDuplicate(mobiles) {

    var mobilesTemp = new Array();
    for (var index in  mobiles) {
        if(!G.isEmpty(mobiles[index])){
            if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
                nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
                return true;
            }
            mobilesTemp.push(mobiles[index]);
        }
    }
    return false;
}

function mustInputFocus(dom) {
    var thisVal = $(dom).val();
    var thisNodeType = $(dom).attr("node-type");
    if (GLOBAL.isEmpty(thisVal)) {
        $(dom).closest("tr").find("[node-type*='" + thisNodeType + "_tips']").html(register[thisNodeType].init);
    }
}

//第一级菜单 select_province
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({
        url:"shop.do?method=selectarea",
        data:{
            parentNo:1
        },
        dataType:"json"
    });
    if (!r||r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $("#select_province")[0].appendChild(option);
        }
    }
}

//第二级菜单 select_city
function cityBind(select) {
    while ($("#select_city")[0].options.length > 1) {
        $("#select_city")[0].remove(1);
    }
    while ($("#select_township")[0].options.length > 1) {
        $("#select_township")[0].remove(1);
    }
    if (select.selectedIndex == 0) {
        $("#input_areaId")[0].value = "";
        $("#input_address")[0].value = "";
    } else {
        $("#input_areaId")[0].value = select.value;
        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
        if (r === null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city")[0].appendChild(option);
            }
        }
    }
}

function initQQTalk($qq) {
    var qqInvoker = new App.Module.QQInvokerStatic();
    qqInvoker.init($qq);
}

function licensePlate(select) {
    if (select.selectedIndex == 0) {
        $("#licencePlate")[0].value = "";
        return;
    }

    var r = APP_BCGOGO.Net.syncGet({"url":"product.do?method=searchlicenseNo&localArea=" + select.value, "dataType":"json"});
    if (r === null) {
        return;
    }
    else {
        $("#licencePlate")[0].value = r[0].platecarno;
    }
}

function setCoordinateResult (lng, lat) {
    $('#input_coordinateLon').val(lng);
    $('#input_coordinateLat').val(lat);
    if (lng && lat)
        $("#searchInMapTxt").html("重新定位");
}

function setAddress() {
    if($("#select_township").val()){
        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
            + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML
            + $("#select_township")[0].options[$("#select_township")[0].selectedIndex].innerHTML + $("#input_address_detail").val();
    } else{
        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
            + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML + $("#input_address_detail").val();
    }

    setCoordinateResult("","");

}

//第三级菜单 select_township
function townshipBind(select) {
    if (select.selectedIndex == 0) {
//        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;
        setAddress();
        return;
    }
    $("#input_areaId")[0].value = select.value;
//    $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
//        + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;
    setAddress();
    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
    if (r === null || typeof(r) == "undefined") {
        return;
    }
    else {
        while ($("#select_township")[0].options.length > 1) {
            $("#select_township")[0].remove(1);
        }
        if (typeof(r) != "undefined" && r.length > 0) {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_township")[0].appendChild(option);
            }
        }
    }
}

function getSelectAddress(){
    var selectAddress = '';
    if($("select[name='province']").eq(0).val()){
        selectAddress += $("select[name='province']").find("option:selected").text();
    }
    if($("select[name='city']").eq(0).val()){
        selectAddress += $("select[name='city']").find("option:selected").text();
    }
    if($("select[name='region']").eq(0).val()){
        selectAddress += $("select[name='region']").find("option:selected").text();
    }
    return selectAddress;
}

function getShareUsedTips(dom){
    var thisVal = $(dom).val();
    var thisNodeType = $(dom).attr("node-type");
    return $(dom).closest("tr").find("[node-type*='" + thisNodeType + "_tips']");
}

function generateContactLine(num){
    return '<div class="line-dashed"></div>'+
        '<table cellpadding="0" cellspacing="0" class="contact-info tabRegister">'+
        '<col width="100">'+
        '<col width="100">'+
        '<col width="100">'+
        '<col>'+
        '<col width="150">'+
        '<tr>'+
        '<td>联系人</td>'+
        '<td colspan="3"><input type="text" id="contacts'+num+'.name" name="contacts['+num+'].name" class="txt" node-type="contact_name"></td>'+
        '<td style="text-align:left;" node-type="contact_name_tips"></td>'+
        '</tr>'+
        '<tr>'+
        '<td>手机</td>'+
        '<td colspan="3"><input type="text" maxlength="11" class="txt" id="contacts'+num+'.mobile" name="contacts['+num+'].mobile" node-type="contact_phone"></td>'+
        '<td style="text-align:left;" node-type="contact_phone_tips"></td>'+
        '</tr>'+
        '<tr>'+
        '<td>EMALL</td>'+
        '<td colspan="3"><input type="text" class="txt" id="contacts'+num+'.email" name="contacts['+num+'].email" node-type="contact_email"></td>'+
        '<td style="text-align:left;" node-type="contact_email_tips"></td>'+
        '</tr>'+
        '<tr>'+
        '<td>QQ</td>'+
        '<td colspan="3"><input type="text" class="txt" id="contacts'+num+'.qq" name="contacts['+num+'].qq" node-type="contact_qq"></td>'+
        '<td style="text-align:left;" style="text-align:left;"><div><span class="btnSure" style="padding-left:5px;"><a>免费开通QQ在线</a></span><span node-type="contact_qq_tips"></span></div></td>'+
        '</tr>'+
        '</table>';

//    return '<tr>' +
//        '<td>联系人</td>' +
//        '<td><input type="text" id="contacts'+num+'.name" name="contacts['+num+'].name" class="txt" node-type="contact_name"></td>' +
//        '<td>手机</td>' +
//        '<td><input type="text" class="txt" id="contacts'+num+'.mobile" name="contacts['+num+'].mobile" node-type="contact_phone"></td>' +
//        '<td style="text-align:left;">' +
//        '<div class="tips" node-type="contact_phone_tips|contact_name_tips"></div>' +
//        '</td>' +
//        '</tr>' +
//        '<tr>' +
//        '<td>Email</td>' +
//        '<td><input type="text" class="txt" id="contacts'+num+'.email" name="contacts['+num+'].email" node-type="contact_email"></td>' +
//        '<td>QQ</td>' +
//        '<td><input type="text" class="txt" id="contacts'+num+'.qq" name="contacts['+num+'].qq" node-type="contact_qq"></td>' +
//        '<td style="text-align:left;">' +
//        '<div class="tips" node-type="contact_email_tips|contact_qq_tips"></div>' +
//        '</td>' +
//        '</tr>';
}