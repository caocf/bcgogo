<%@ page import="com.bcgogo.config.dto.ShopDTO" %>
<%@ page import="com.bcgogo.utils.DateUtil" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理—店面审核</title>
    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.url.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/searchDefault.js"></script>
    <script type="text/javascript" src="js/shopaudit.js"></script>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/uploadPreview.js"></script>
    <script type="text/javascript">
        function regedit(state,method){
              window.location=state+"beshop.do?method="+method;
        }
    </script>
    <script type="text/javascript">
        var $ = jQuery.noConflict();
        $().ready(function () {
            $("#imgfile").uploadPreview({width:100, height:100, imgDiv:"#imgDiv", imgType:["bmp", "gif", "png", "jpg"], maxwidth:800, maxheight:600, photo:"#photo"});

            $('#register').click(function () {
                if (!$("#input_established").val()) {
                    alert("请选择成立时间");
                    return;
                }
                $('#form_save').ajaxSubmit(function (data) {
                    if (data == "maxOver") {
                        alert("上传照片大于2M，无法上传！")
                    }
                    else if (data == "success") {
                        alert("注册成功！请耐心等待后台人员审核！");
                        window.location.assign("login.jsp");
                    }
                    else {
                        alert("网络错误！注册失败！");
                    }
                });
                return false;
            });
        });

        (function () {

            $().ready(function () {

                if ($("#hidden_busTime").val()) {
                    if ($("#hidden_busTime").val() == "24小时营业") {
                        $("#input_starttime_hour").val("00");
                        $("#input_starttime_minu").val("00");
                        $("#input_endtime_hour").val("23");
                        $("#input_endtime_minu").val("59");
                    } else {
                        var str = $("#hidden_busTime").val();
                        str = str.substring(0, str.length - 2);
                        var str1 = str.split("~");
                        var str2 = str1[0].split("：");
                        var str3 = str1[1].split("：");
                        $("#input_starttime_hour").val(str2[0]);
                        $("#input_starttime_minu").val(str2[1]);
                        $("#input_endtime_hour").val(str3[0]);
                        $("#input_endtime_minu").val(str3[1]);
                    }
                }

                //绑定选中的复选框
                if ($("#hidden_busScope").val()) {
                    var buscope = $("#hidden_busScope").val().split(",");

                    if (buscope && buscope.length > 0) {
                        for (var i = 0, l = buscope.length; i < l; i++) {
                            check(buscope[i]);
                        }
                    }
                }

                if ($("#hidden_radio").val()) {
                    var radios = document.getElementsByName("radio");
                    for (var i = 0, l = radios.length; i < l; i++) {
                        if ($("#hidden_radio").val() == radios[i].parentNode.lastChild.nodeValue) {
                            radios[i].checked = true;
                        }
                    }

                    if ($("#hidden_radio").val().substring(0, 3) == "专卖店") {
                        $("#radio_check3")[0].checked = true;
                        $("#input_check3").val( $("#hidden_radio").val().substring(4, $("#hidden_radio").val().length - 2) );
                    }

                    if ($("#hidden_radio").val().substring(0, 2) == "其他") {
                        $("#radio_check5")[0].checked = true;
                        $("#input_check5").val( $("#hidden_radio").val().substring(3, $("#hidden_radio").val().length - 2) );
                    }
                }

                if ($("#hidden_relatedBusiness").val()) {
                    var str = $("#hidden_relatedBusiness").val().split(",");

                    var checks = document.getElementsByName("checkbox_relatedBusiness");
                    for (var i = 0, l = str.length; i < l; i++) {
                        if (!checkFeature(checks, str[i])) {
                            $("#input_checkbox_relatedBusiness").val( str[i] );
                            $("#checkbox_relatedBusiness")[0].checked = true;
                        }
                    }
                }

                if ($("#hidden_feature").val()) {
                    var str = $("#hidden_feature").val().split(",");

                    var checks = document.getElementsByName("checkbox_feature");

                    for (var i = 0, l = str.length; i < l; i++) {
                        if (checkFeature(checks, str[i])) {
                        }
                        else {
                            if (str[i].substring(0, 2) == "车型") {
                                $("#checkbox_feature_model")[0].checked = true;
                                $("#input_feature_model").val(str[i].substr(2));
                            }
                            else if (str[i].substring(0, 2) == "其他") {
                                $("#checkbox_feature_other")[0].checked = true;
                                $("#input_feature_other").val(str[i].substr(2));
                            }
                        }
                    }
                }

                //  input_confirm
                if ($("#input_confirm")[0]) {
                    $("#input_confirm").click( function () {
                       $("#input_confirm").css('display','none');
                       var shopId = $.url(window.location.search).param("shopId");
                       window.location.assign("beshop.do?method=activateshop&shopId=" +shopId);
                    });
                }

            });

            function checkFeature(checks, value) {
                for (var i = 0, l = checks.length; i < l; i++) {
                    if (checks[i].parentNode.lastChild.nodeValue == value) {
                        checks[i].checked = true;
                        return true;
                    }
                }
            }

            function check(value) {
                var checks = document.getElementsByName("checkbox");

                for (var i = 0, l = checks.length; i < l; i++) {
                    if (value == checks[i].value) {
                        checks[i].checked = true;
                    }
                    else if (value.length > 2) {
                        if (value.substring(0, 2) == 33) {
                            $("#checkbox_other")[0].checked = true;
                            $("#input_other").val(value.substr(2));
                        }
                        else if (value.substring(0, 2) == 35) {
                            $("#check_mainproduct")[0].checked = true;
                            $("#input_mainproduct").val(value.substr(2));
                        }
                    }
                }
            }
        })();
    </script>
</head>

<body>
<input type="hidden" name="shopId" value="${shopDTO.id}"/>

<div class="main">
<%@include file="/WEB-INF/views/header.jsp" %>
<div class="body">
<%@include file="/WEB-INF/views/left.jsp" %>
<div class="bodyRight">
<div class="rightText">
    <div class="textLeft"></div>
    <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
    <div class="textRight"></div>
</div>
<div class="rightText">
    <div class="textLeft"></div>
    <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
    <div class="textRight"></div>
</div>
<div class="rightText">
    <div class="textLeft"></div>
    <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
    <div class="textRight"></div>
</div>
<div class="rightText">
    <div class="textLeft"></div>
    <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
    <div class="textRight"></div>
</div>
<input type="button" class="rightSearch" value="搜 索"/>

<div class="rightMain clear">
<div class="rightTitle">
    <div class="rightLeft"></div>
    <div class="rightBody">
        <div class="titleHover">立即注册</div>
        <div class="title" onclick="regedit('<%=basePath%>','shoplist2')">待注册</div>
        <div class="title" onclick="regedit('<%=basePath%>','shoplist1')">已注册</div>
        <div class="title" onclick="regedit('<%=basePath%>','smsRecharge')">短信费用</div>
    </div>
</div>
<div class="rightTime">
    <div class="timeLeft"></div>
    <div class="timeBody">
        店面注册时间：<span>${shopDTO.creationDateStr}</span>
    </div>
    <div class="timeRight"></div>
</div>
<div class="registerInfo">
<div class="leftBorder"></div>
<table cellpadding="0" cellspacing="0" class="tableRegister">
    <col width="100"/>
    <col/>
    <col width="100"/>
    <col width="130"/>
    <col width="100"/>
    <col width="110"/>
    <col width="90"/>
    <tr>
        <td>单位名称</td>
        <td style="color:#000000;" ondblclick="editInfo('nameSpan','name')" id="td_name">
        <span id = "nameSpan">${shopDTO.name}</span>
        <input type="text" id="name" value="${shopDTO.name}" style="display:none;">

        </td>
        <td>店面简称</td>
        <td style="color:#000000;" ondblclick="editInfo('shortnameSpan','shortname')" id="td_shortName">
         <span id = "shortnameSpan"> ${shopDTO.shortname}</span>
        <input type="text" id="shortname" value="${shopDTO.shortname}" style="display:none;">
        </td>
        <td>业务员</td>
        <td style="color:#000000;">${shopDTO.agent}</td>
        <td rowspan="5" >
            <form action="<%=basePath%>beshop.do?method=updateShopPhoto" method="post" enctype="multipart/form-data">
                    <div class="d_photo">
                    <img src="<%=basePath%>beshop.do?method=getShopPhoto&shopId=${shopDTO.id}" width="100px" height="100px" id="photo"/>
                         <input type="hidden" name="shopId" value="${shopDTO.id}">
                            <div style="width: 100px; height: 100px; overflow: hidden; " id="parentImgDiv">
                                <div style="width: 100px; height: 100px; " id="imgDiv">
                                </div>
                            </div>

           </div>
            <div class="imageBtn"><input type="file" id="imgfile" name="input_fileLoad" target="iframe_fileLoad" style="width: 70px" value="修改" />
                <input type="submit" value="修改"/>
            </div>
       </form>
        </td>
    </tr>
    <tr>
        <td>负责人/店主</td>
        <td  style="color:#000000;"  ondblclick="editInfo('legalRepSpan','legalRep')" id="td_legalRep">
            <span id="legalRepSpan"> ${shopDTO.legalRep}</span>
            <input type="text" id="legalRep" value="${shopDTO.legalRep}" style="display:none;">
        </td>
        <td>车牌默认前缀</td>
        <td style="color:#000000;" ondblclick="editInfo('licencePlateSpan','licencePlate')" id="td_licencePlate">
             <span id="licencePlateSpan"> ${shopDTO.licencePlate}</span>
            <input type="text" id="licencePlate" style="display: none;" value="${shopDTO.licencePlate}">
         </td>
        <td>业务员ID</td>
        <td style="color:#000000;">${shopDTO.agentId}</td>
    </tr>
    <tr>
        <td>联系手机</td>
        <td style="color:#000000;" ondblclick="editInfo('mobileSpan','mobile')" id="td_mobile">
             <span id="mobileSpan">${shopDTO.mobile}</span>
            <input type="text" id="mobile" style="display: none;" value="${shopDTO.mobile}">
         </td>
        <td>固定电话</td>
        <td style="color:#000000;" ondblclick="editInfo('landlineSpan','landline')" id="td_landline">
             <span id="landlineSpan">${shopDTO.landline}</span>
            <input type="text" id="landline" style="display: none;" value="${shopDTO.landline}">
         </td>
        <td>业务员手机号</td>
        <td style="color:#000000;">${shopDTO.agentMobile}</td>
    </tr>
    <tr>
        <td>店面管理员</td>
        <td style="color:#000000;" id="td_storeManager" ondblclick="editInfo('storeManagerSpan','storeManager')">
            <span id="storeManagerSpan"> ${shopDTO.storeManager}</span>
            <input type="text" id="storeManager" value="${shopDTO.storeManager}" style="display:none;">
       </td>
        <td>QQ</td>
         <td style="color:#000000;" id="td_qq" ondblclick="editInfo('qqSpan','qq')">
            <span id="qqSpan">${shopDTO.qq}</span>
            <input type="text" id="qq" value="${shopDTO.qq}" style="display:none;">
         </td>
        <td>代理商</td>
        <td style="color:#000000;">苏州统购</td>
    </tr>
    <tr>
        <td>联系电话</td>
        <td style="color:#000000;" id="td_storeManagerMobile" ondblclick="editInfo('storeManagerMobileSpan','storeManagerMobile')">
            <span id="storeManagerMobileSpan">${shopDTO.storeManagerMobile}</span>
            <input type="text" id="storeManagerMobile" value="${shopDTO.storeManagerMobile}" style="display:none;">
        </td>
        <td>Email</td>
         <td style="color:#000000;" id="td_email" ondblclick="editInfo('emailSpan','email')">
            <span id="emailSpan">${shopDTO.email}</span>
            <input type="text" id="email" value="${shopDTO.email}" style="display:none;">
        </td>
        <td>软件销售价</td>
        <td style="color:#000000;" id="td_softPrice" ondblclick="editInfo('softPriceSpan','softPrice')">
            <span id="softPriceSpan">${shopDTO.softPrice}</span>
            <input type="text" id="softPrice" value="${shopDTO.softPrice}" style="display:none;">
        </td>
    </tr>
    <tr>
        <td>地址</td>
        <td style="color:#000000;" id="td_address" ondblclick="editAddressInfo('addressSpan','address')" colspan="3">
            <span id="addressSpan">${shopDTO.address}</span>
            <input type="text" id="address" value="${shopDTO.address}" style="display:none;width: 300px;">
                <select class="register_position" id="select_province"  style="display:none;float:left;">
                        <option selected="selected">请选择</option>
                    </select>
                    <select class="register_position" id="select_city" style="display:none;float:left;">
                        <option selected="selected">请选择</option>
                    </select>
                    <select class="register_position" id="select_township" style="display:none;float:left;">
                        <option selected="selected">请选择</option>
                    </select>
            <input type="hidden" id="areaId" value="${shop.areaId}">
           <input type="hidden" id="hiddenareaId" value="${shop.areaId}">
        </td>
      <td>软件版本</td>
      <td>
        ${shopDTO.type.value}
      </td>
    </tr>
</table>
<div class="registerMsg">

    <%
        if (request.getAttribute("shopDTO") != null && ((ShopDTO) request.getAttribute("shopDTO")).getEstablished() != null) {
    %>
    <div class="msg_time"><span>成立时间：</span>
        <input type="text" id="established" value="<%=DateUtil.convertDateLongToDateString("yyyy-MM-dd", ((ShopDTO)request.getAttribute("shopDTO")).getEstablished()) %>" >
        <%} else {%>
        <div class="msg_time"><span>成立时间：</span>
           <input type="text" id="established" value="" >
            <%}%>
        </div>
        <div class="msg_time">资 质<input type="text" id="qualification" value="${shopDTO.qualification}"/></div>
        <div class="msg_time">人 员<input type="text" id="personnel" value="${shopDTO.personnel}"/></div>
        <div class="msg_time">面 积<input type="text" id="area" value="${shopDTO.area}"/></div>
        <div class="area">㎡</div>
        <div class="msg_time">
            <span>营业时间<input id="hidden_busTime" type="hidden" value="${shopDTO.businessHours}"/></span>

            <div class="make_starttime">
                <input id="input_starttime_hour" type="text" style="width:23px;">
                ：
                <input id="input_starttime_minu" type="text" style="width:23px;">
            </div>
            <span>~</span>

            <div class="make_endtime">
                <input id="input_endtime_hour" type="text" style="width:23px;">
                ：
                <input id="input_endtime_minu" type="text" style="width:23px;">
            </div>
        </div>
         <div class="suer_xiu">
            <input type="button" class="suer_a" id="editCfm" style="display:none;" value="确定修改"
           onclick="editCfm('${shopDTO.id}')"/>
            <input type="button"  id="editCancel" style="display:none;" value="取消" onclick="editCancel()"  />
     </div>
    </div>
</div>
<div class="registerInfo">
    <div class="leftBorder2"></div>
    <div class="registerName">经营产品</div>
    <div class="car_repair">
        <div class="car_repairName">汽车维修</div>
        <div class="car_decorateInfo">

            <input type="hidden" id="hidden_busScope" value="${value}"/>
            <c:forEach var="businessDTOlt" items="${businessDTOList2}" varStatus="status">
                <label><input type="checkbox" name="checkbox" value="${businessDTOlt.id}"/><c:out
                        value="${businessDTOlt.content}"/></label>
            </c:forEach>

            <!--<label><input type="checkbox"/>汽车保养</label>
           <label><input type="checkbox"/>钣金喷漆</label>
           <label><input type="checkbox"/>轮胎</label>
           <label><input type="checkbox"/>汽车快修</label>
           <label><input type="checkbox"/>汽车小修</label>
           <label><input type="checkbox"/>汽车中修</label>
           <label><input type="checkbox"/>汽车大修</label>-->
        </div>
    </div>
    <div class="car_repair">
        <div class="car_decorateName">汽车装潢</div>
        <div class="car_decorateInfo">

            <c:forEach var="businessDTOlt" items="${businessDTOList10}" varStatus="status">
                <c:choose>
                    <c:when test="${businessDTOlt.no != 33}">
                        <label><input type="checkbox" name="checkbox" value="${businessDTOlt.id}"/><c:out
                                value="${businessDTOlt.content}"/></label>
                    </c:when>
                    <c:when test="${businessDTOlt.no == 33}">
                        <label><input type="checkbox" id="checkbox_other" value="${businessDTOlt.id}"/><c:out
                                value="${businessDTOlt.content}"/></label>
                        <input type="text" class="carOther" id="input_other"/>
                    </c:when>
                </c:choose>
            </c:forEach>

            <!--<label><input type="checkbox"/>电脑洗车</label>
          <label><input type="checkbox"/>人工洗车</label>
          <label><input type="checkbox"/>车身彩贴</label>
          <label><input type="checkbox"/>新车开腊</label>
          <label><input type="checkbox"/>封釉美容</label>
          <label><input type="checkbox"/>漆面打蜡</label>
          <label><input type="checkbox"/>漆面抛光</label>
          <label><input type="checkbox"/>漆面划痕修复</label>
          <label><input type="checkbox"/>内部装饰</label>
          <label><input type="checkbox"/>内饰桑拿</label>
          <label><input type="checkbox"/>真皮座椅</label>
          <label><input type="checkbox"/>中央门锁</label>
          <label><input type="checkbox"/>DVD导航</label>
          <label><input type="checkbox"/>车内真皮制品护理</label>
          <label><input type="checkbox"/>便携导航</label>
          <label><input type="checkbox"/>倒车雷达</label>
          <label><input type="checkbox"/>汽车隔音</label>
          <label><input type="checkbox"/>底盘装甲</label>
          <label><input type="checkbox"/>汽车防爆膜</label>
          <label><input type="checkbox"/>光触媒杀菌消毒</label>
          <label><input type="checkbox"/>轮胎翻新</label>
          <label><input type="checkbox"/>防盗器</label>
          <label><input type="checkbox"/>其他</label>
          <input type="text" class="carOther"/>-->
        </div>
    </div>
    <div class="car_repair">
        <div class="car_wholesaleName">批发零售</div>
        <div class="car_decorateInfo">
            <c:forEach var="businessDTOlt" items="${businessDTOList34}" varStatus="status">
                <label><input type="checkbox" id="check_mainproduct" value="${businessDTOlt.id}"/><c:out
                        value="${businessDTOlt.content}"/></label>
                <input id="input_mainproduct" type="text" class="carOther"/>
            </c:forEach>
            <!--<label><input type="checkbox"/>主要产品</label>
          <input type="text" class="carOther"/>-->
        </div>
    </div>
    <div class="car_repair">
        <div class="car_Name">店面特色</div>
        <div class="car_decorateInfo">
            <input type="hidden" id="hidden_feature" value="${shopDTO.feature}"/>
            <label><input type="checkbox" name="checkbox_feature"/>高档车</label>
            <label><input type="checkbox" name="checkbox_feature"/>中档车</label>
            <label><input type="checkbox" name="checkbox_feature"/>低档车</label>
            <label><input type="checkbox" name="checkbox_feature" id="checkbox_feature_model"/>车型</label><input
                id="input_feature_model" type="text" class="carModels"/>
            <label><input type="checkbox" name="checkbox_feature"/>上门服务</label>
            <label><input type="checkbox" name="checkbox_feature"/>免费车接送</label>
            <label><input type="checkbox" name="checkbox_feature" id="checkbox_feature_other"/>其他</label><input
                id="input_feature_other" type="text" class="carModels"/>
        </div>
    </div>
    <div class="car_repair">
        <div class="car_Name">相关业务</div>
        <div class="car_decorateInfo">
            <input id="hidden_relatedBusiness" type="hidden" value="${shopDTO.relatedBusiness}"/>
            <label><input type="checkbox" name="checkbox_relatedBusiness"/>救援</label>
            <label><input type="checkbox" name="checkbox_relatedBusiness"/>保险定损理赔</label>
            <label><input type="checkbox" name="checkbox_relatedBusiness"/>二手车</label>
            <label><input type="checkbox" name="checkbox_relatedBusiness"
                          id="checkbox_relatedBusiness"/>其他</label><input type="text"
                                                                          class="writeother"
                                                                          id="input_checkbox_relatedBusiness"/>
        </div>
    </div>
    <div class="car_repair">
        <div class="car_Name">经营方式</div>
        <div class="car_decorateInfo">
            <input type="hidden" id="hidden_radio" value="${shopDTO.operationMode}"/>
            <label><input type="radio" name="radio" id="radio_check1"/>加盟连锁</label>
            <label><input type="radio" name="radio" id="radio_check2"/>有限公司</label>
            <label><input type="radio" name="radio" id="radio_check3"/>专卖店</label>
            <input type="text" class="carModels" id="input_check3"/><span>品牌</span>
            <label><input type="radio" name="radio" id="radio_check4"/>个体</label>
            <label><input type="radio" name="radio" id="radio_check5"/>其他</label><input type="text" class="carModels" id="input_check5"/>
        </div>
    </div>
</div>
<div class="registerInfo">
    <div class="leftBorder3"></div>
    <div class="car_repair">
        <div class="car_Name">账号分配</div>
        <div class="car_shopInfo">版本类型：<span>${shopDTO.type.value}</span>店面管理员用户名（手机号）：<span>${shopDTO.storeManagerMobile}</span>密码：<span>******</span>
        </div>
    </div>
</div>
<div class="registerButton">
    <c:if test="${shopDTO.state == null}">
        <input type="button" value="确认注册" id="input_confirm"/>
        <input type="button" value="取　消" onclick="document.location.assign('beshop.do?method=shoplist')"/>
    </c:if>
</div>

</div>
</div>
</div>
</div>
</div>
</body>

</html>