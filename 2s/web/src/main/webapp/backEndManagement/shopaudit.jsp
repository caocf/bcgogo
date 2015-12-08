<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.config.dto.ShopDTO" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-25
  Time: 下午10:07
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理—店面审核</title>
    <link rel="stylesheet" type="text/css" href="styles/backstage<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript">
        function regedit(state, method) {
            window.location = state + "beshop.do?method=" + method;
        }
    </script>
    <script type="text/javascript">
        (function () {
            $().ready(function () {
                if ($("#hidden_busTime")[0].value) {
                    if ($("#hidden_busTime")[0].value == "24小时营业") {
                        $("#input_starttime_hour")[0].value = "00";
                        $("#input_starttime_minu")[0].value = "00";
                        $("#input_endtime_hour")[0].value = "23";
                        $("#input_endtime_minu")[0].value = "59";
                    }
                    else {
                        var str = $("#hidden_busTime")[0].value;
                        str = str.substring(0, str.length - 2);
                        var str1 = str.split("~");
                        var str2 = str1[0].split("：");
                        var str3 = str1[1].split("：");
                        $("#input_starttime_hour")[0].value = str2[0];
                        $("#input_starttime_minu")[0].value = str2[1];
                        $("#input_endtime_hour")[0].value = str3[0];
                        $("#input_endtime_minu")[0].value = str3[1];
                    }
                }

                //绑定选中的复选框
                if ($("#hidden_busScope")[0].value) {
                    var buscope = $("#hidden_busScope")[0].value.split(",");

                    if (buscope && buscope.length > 0) {
                        for (var i = 0, l = buscope.length; i < l; i++) {
                            check(buscope[i]);
                        }
                    }
                }

                if ($("#hidden_radio")[0].value) {
                    var radios = document.getElementsByName("radio");
                    for (var i = 0, l = radios.length; i < l; i++) {
                        if ($("#hidden_radio")[0].value == radios[i].parentNode.lastChild.nodeValue) {
                            radios[i].checked = true;
                        }
                    }

                    if ($("#hidden_radio")[0].value.substring(0, 3) == "专卖店") {
                        $("#radio_check3")[0].checked = true;
                        $("#input_check3")[0].value = $("#hidden_radio")[0].value.substring(4, $("#hidden_radio")[0].value.length - 2);
                    }

                    if ($("#hidden_radio")[0].value.substring(0, 2) == "其他") {
                        $("#radio_check5")[0].checked = true;
                        $("#input_check5")[0].value = $("#hidden_radio")[0].value.substring(3, $("#hidden_radio")[0].value.length - 2);
                    }
                }

                if ($("#hidden_relatedBusiness")[0].value) {
                    var str = $("#hidden_relatedBusiness")[0].value.split(",");

                    var checks = document.getElementsByName("checkbox_relatedBusiness");
                    for (var i = 0, l = str.length; i < l; i++) {
                        if (!checkFeature(checks, str[i])) {
                            $("#input_checkbox_relatedBusiness")[0].value = str[i];
                            $("#checkbox_relatedBusiness")[0].checked = true;
                        }
                    }
                }

                if ($("#hidden_feature")[0].value) {
                    var str = $("#hidden_feature")[0].value.split(",");

                    var checks = document.getElementsByName("checkbox_feature");

                    for (var i = 0, l = str.length; i < l; i++) {
                        if (checkFeature(checks, str[i])) {
                        }
                        else {
                            if (str[i].substring(0, 2) == "车型") {
                                $("#checkbox_feature_model")[0].checked = true;
                                $("#input_feature_model")[0].value = str[i].substr(2);
                            }
                            else if (str[i].substring(0, 2) == "其他") {
                                $("#checkbox_feature_other")[0].checked = true;
                                $("#input_feature_other")[0].value = str[i].substr(2);
                            }
                        }
                    }
                }

                //  input_confirm
                if ($("#input_confirm")[0]) {
                    $("#input_confirm")[0].onclick = function () {
                        var p = js.string.parsesearch(window.location.search);
                        window.location.assign("beshop.do?method=activateshop&shopId=" + p["shopId"]);
                    }
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
                            $("#input_other")[0].value = value.substr(2);
                        }
                        else if (value.substring(0, 2) == 35) {
                            $("#check_mainproduct")[0].checked = true;
                            $("#input_mainproduct")[0].value = value.substr(2);
                        }
                    }
                }
            }
        })();
    </script>
</head>

<body>
<%!
    public String ConvertLongToString(String format, Long time) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Timestamp now = new Timestamp(time);
        return df.format(now);
    }
%>
<%
    String serverPath = request.getSession().getServletContext().getRealPath("/");

%>
<input type="hidden" name="shopId" value="${shopDTO.id}"/>

<div class="main">
<div class="top">
    <div class="top_left">
        <div class="top_name">统购后台管理系统</div>
        <div class="top_image"></div>
        你好，<span>张三</span>|<a href="j_spring_security_logout">退出</a></div>
    <div class="top_right"><span>2011.11.23 14:01 星期三</span></div>
</div>
<div class="body">
<div class="bodyLeft">
    <ul class="leftTitle">
        <li class="left_hover"><a href="#" class="left_register">注册</a><input type="button" class="btnNum" value="1"/>
        </li>
        <li><a href="#" class="left_shopping">商品</a><input type="button" class="btnNum" value="1"/></li>
        <li><a href="#" class="left_vehicle">车辆</a><input type="button" class="btnNum" value="1"/></li>
        <li><a href="#" class="left_recruit">招聘</a><input type="button" class="btnNum" value="1"/></li>
        <li><a href="#" class="left_recharge">充值</a><input type="button" class="btnNum" value="1"/></li>
        <li><a href="#" class="left_manage">管理</a><input type="button" class="btnNum" value="1"/></li>
    </ul>
</div>
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
        <div class="title">短信费用</div>
    </div>
</div>
<div class="rightTime">
    <div class="timeLeft"></div>
    <div class="timeBody">
        店面注册时间：2011.11.23 <span>17:35</span>
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
        <td style="color:#000000;">${shopDTO.name}</td>
        <td>店面简称</td>
        <td style="color:#000000;">${shopDTO.shortname}</td>
        <td>业务员</td>
        <td style="color:#000000;">${shopDTO.agent}</td>
        <td rowspan="5">

            <c:if test="${shopDTO.photo == null}">
                <img src="images/image.jpg"/>
            </c:if>
            <c:if test="${shopDTO.photo != null}">
                <img src="<%=serverPath + (((ShopDTO)request.getAttribute("shopDTO")).getPhoto())%>"/>
            </c:if>

            <div class="imageBtn"><a href="#">修 改</a><a href="#">上 传</a></div>
        </td>
    </tr>
    <tr>
        <td>负责人/店主</td>
        <td style="color:#000000;">${shopDTO.legalRep}</td>
        <td>车牌默认前缀</td>
        <td style="color:#000000;">

            <c:if test="${shopDTO.state == null}">
                <input type="text" style="width:35px;" value=${shopDTO.licencePlate}>
            </c:if>
            <c:if test="${shopDTO.state != null}">
                ${shopDTO.licencePlate}

            </c:if>
        </td>
        <td>业务员ID</td>
        <td style="color:#000000;">${shopDTO.agentId}</td>
    </tr>
    <tr>
        <td>联系手机</td>
        <td style="color:#000000;">${shopDTO.mobile}</td>
        <td>固定电话</td>
        <td style="color:#000000;">${shopDTO.landline}</td>
        <td>业务员手机号</td>
        <td style="color:#000000;">${shopDTO.agentMobile}</td>
    </tr>
    <tr>
        <td>店面管理员</td>
        <td style="color:#000000;">${shopDTO.storeManager}</td>
        <td>QQ</td>
        <td style="color:#000000;">${shopDTO.qq}</td>
        <td>代理商</td>
        <td style="color:#000000;">苏州统购</td>
    </tr>
    <tr>
        <td>联系电话</td>
        <td style="color:#000000;">${shopDTO.storeManagerMobile}</td>
        <td>Email</td>
        <td style="color:#000000;">${shopDTO.email}</td>
        <td>软件销售价</td>
        <td style="color:#000000;">${shopDTO.softPrice}</td>
    </tr>
    <tr>
        <td>地址</td>
        <td colspan="5" style="color:#000000;">${shopDTO.address}</td>
    </tr>
</table>
<div class="registerMsg">

    <%
        if (request.getAttribute("shopDTO") != null && ((ShopDTO) request.getAttribute("shopDTO")).getEstablished() != null) {
    %>
    <div class="msg_time">成立时间<input type="text"
                                     value="<%=ConvertLongToString("yyyy-MM-dd", ((ShopDTO)request.getAttribute("shopDTO")).getEstablished()) %>"/>
        <%} else {%>
        <div class="msg_time">成立时间<input type="text"
                                         value=""/>
            <%}%>
        </div>
        <div class="msg_time">资 质<input type="text" value="${shopDTO.qualification}"/></div>
        <div class="msg_time">人 员<input type="text" value="${shopDTO.personnel}"/></div>
        <div class="msg_time">面 积<input type="text" value="${shopDTO.area}"/></div>
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
            <label><input type="radio" name="radio" id="radio_check5"/>其他</label><input type="text" class="carModels"
                                                                                        id="input_check5"/>
        </div>
    </div>
</div>
<div class="registerInfo">
    <div class="leftBorder3"></div>
    <div class="car_repair">
        <div class="car_Name">账号分配</div>
        <div class="car_shopInfo">店面管理员用户名（手机号）：<span>${shopDTO.storeManagerMobile}</span>密码：<span>******</span></div>
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