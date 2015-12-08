<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ include file="/WEB-INF/views/includes.jsp" %>--%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>用户注册</title>
    <%@include file="/WEB-INF/views/style_thirdparty_extension_core.jsp" %>
    <%@include file="/WEB-INF/views/style_ui_components.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/script_thirdparty_extension_core.jsp" %>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/basecommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <%@include file="/WEB-INF/views/script_ui_components.jsp" %>
    <script type="text/javascript" src="js/page/config/registerShopInfo<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body class="bodyMain registerBg">
<div class="clear m_topMain">
    <div class="l_top">
        <div class="l_topBorder"></div>
        <div class="home"></div>
        <div class="l_topBorder"></div>
        <div class="l_topTitle">欢迎您注册一发EasyPower软件</div>
        <div class="l_topBorder"></div>
    </div>
</div>
<div class="i_main clear">
    <h1 class="user">用户注册</h1>

    <div class="height"></div>
    <form id="form_save" action="shop.do?method=saveshop" method="post" <%--enctype="multipart/form-data"--%>>
        <table cellpadding="0" cellspacing="0" class="tabRegister">
            <col width="110">
            <col width="105">
            <col>
            <col width="110">
            <tr class="regTitle">
                <td colspan="4">请输入您的邀请码</td>
            </tr>
            <tr>
                <td>邀请码<span class="red_color">*</span></td>
                <td colspan="3"><input type="text" class="txt" value="请输入邀请码"/></td>
            </tr>
            <tr class="regTitle">
                <td colspan="4">请输入您的基本信息</td>
            </tr>
            <tr>
                <td>负责人/店主<span class="red_color">*</span></td>
                <td colspan="3"><input type="text" class="txt" value=""/></td>
            </tr>
            <tr>
                <td>手机号码<span class="red_color">*</span></td>
                <td><input type="text" class="txt" value=""/></td>
                <td>固定电话</td>
                <td><input type="text" class="txt" value=""/></td>
            </tr>
            <tr>
                <td>QQ</td>
                <td><input type="text" class="txt" value=""/></td>
                <td>Email</td>
                <td><input type="text" class="txt" value=""/></td>
            </tr>
            <tr class="regTitle">
                <td colspan="4">请输入您的公司（店铺）信息</td>
            </tr>
            <tr>
                <td>单位名称<span class="red_color">*</span></td>
                <td colspan="3"><input type="text" class="txt" id="input_name" value=""/></td>
            </tr>
            <tr>
                <td>地区<span class="red_color">*</span></td>
                <td colspan="3">
                    <input type="hidden" name="areaId" id="input_areaId" value="1"/>
                    <select class="txt" id="select_province"  style="width:81px; margin-right:8px;">
                        <option selected="selected">请选择</option>
                    </select>
                    <select class="txt" id="select_city"  style="width:81px; margin-right:8px;display:none;">
                        <option selected="selected">请选择</option>
                    </select>
                    <select class="txt" id="select_township"  style="width:81px; margin-right:8px;display:none;">
                        <option selected="selected">请选择</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>地址<span class="red_color">*</span></td>
                <td colspan="3"><input type="text" id="input_address" class="txt" value=""/></td>
            </tr>
            <tr>
                <td>所在地车牌前缀</td>
                <td colspan="3"><input type="text" id="licencePlate" class="txt" value=""/></td>
            </tr>
           <%-- <tr>
                <td>经营产品<span class="red_color">*</span></td>
                <td colspan="3">
                    <label class="lbl"><input type="checkbox"/>汽车维修</label> <label class="lbl"><input type="checkbox"/>汽车装潢</label>
                    <label class="lbl"><input type="checkbox"/>批发</label>
                    <input type="text" class="txt" style="border-width:0px 0px 1px 0px; width:70px; margin:8px 0px 0px -10px;"/>
                </td>
            </tr>--%>
            <tr>
                <td>经营方式</td>
                <td>
                    <select class="txt" id="operationModes" name="operationModes">
                        <option>--请选择--</option>
                        <option value="加盟连锁">加盟连锁</option>
                        <option value="有限公司">有限公司</option>
                        <option value="专卖店">专卖店</option>
                        <option value="个体">个体</option>
                        <option value="其它">其它</option>
                    </select>
                </td>
                <td colspan="2"><input type="text" id="otherOperationMode" name="otherOperationMode" class="txt" style="display: none"/></td>
            </tr>

            <tr>
                <td>店面照片</td>
                <td colspan="3">
                    <input type="text" class="txt" value="" style="width:70%;"/><input type="button" class="btnFile" value="浏&nbsp;览"/>
                </td>
            </tr>
            <tr>
                <td style="vertical-align:top;">备注</td>
                <td colspan="3">
                    <textarea class="txt"></textarea>
                </td>
            </tr>
            <tr class="softType">
                <td style="vertical-align:top;">软件版本<span class="red_color">*</span></td>
                <td colspan="3">
                    <label class="radType"><input name="shopVersionName" value="初级版" type="radio"/>初级版（适用小型汽修美容店）</label>
                    <label class="radType"><input name="shopVersionName" value="综合版" type="radio"/>综合版（适用中小型一站式维修厂）</label>
                    <label class="radType"><input name="shopVersionName" value="高级版" type="radio"/>高级版（适用大型汽修店及4S店）</label>
                    <label class="radType"><input name="shopVersionName" value="批发商版" type="radio"/>批发商版（适用配件批发商）</label>
                </td>
            </tr>
            <tr>
                <td>软件销售价</td>
                <td colspan="3">
                    <span class="yellow_color" id="softPriceInfo"></span>
                    <input type="hidden" name="softPrice" id="softPrice">
                </td>
            </tr>
            <tr>
                <td>验证码<span class="red_color">*</span></td>
                <td colspan="3"><input type="text" class="txt" value=""/></td>
            </tr>
            <tr class="description" style="border: none;float: none;">
                <td></td>
                <td colspan="3">请输入图中字符，不区分大小写</td>
            </tr>
            <tr>
                <td></td>
                <td colspan="3">
                    <a class="yanzh"></a> <a class="click">看不清，换一张</a>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="3"><input type="button" class="regBtn" value="同意用户协议并注册" onfocus="this.blur();"/></td>
            </tr>
            <tr class="description" style="border: none;float: none;">
                <td></td>
                <td colspan="2">
                    <a href="shop.do?method=shopagreement" class="blue_color" target="_blank">《统购信息软件服务条款》</a>
                </td>
                <td></td>
            </tr>
        </table>
    </form>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
