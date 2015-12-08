<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", -10);
%>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2015-1-6
  Time: 11:44
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>在线预约</title>
    <meta content="text/html;charset=utf-8" http-equiv="Content-Type">
    <meta content="wangchunpeng" name="author">
    <meta content="True" name="HandheldFriendly">
    <meta content="width=device-width,user-scalable=no" name="viewport">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <meta content="on" http-equiv="cleartype">
    <%--mobiscroll 要写在前面，解决jquery版本问题--%>
    <link href="/web/js/extension/jquery/plugin/mobiscroll/css/mobiscroll.widget.css" rel="stylesheet"
          type="text/css"/>
    <link href="/web/js/extension/jquery/plugin/mobiscroll/css/mobiscroll.scroller.css" rel="stylesheet"
          type="text/css"/>
    <script src="/web/js/extension/jquery/jquery-1.11.0.min.js"></script>

    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.core.js"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.widget.js" type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.scroller.js" type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/i18n/mobiscroll.i18n.zh.js"
            type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.util.datetime.js"
            type="text/javascript"></script>
    <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.datetime.js" type="text/javascript"></script>

    <script type="text/javascript">
        var $11 = jQuery.noConflict(true);
    </script>
    <script type="text/javascript">
        $11(function () {
            $11("#appointDate").mobiscroll().date({
                lang: 'zh',
//                dateFormat: 'yyyy-MM-dd',
                display: 'bottom'
            });
        });
        $11(function () {
            $11("#appointTime").mobiscroll().time({
                lang: 'zh',
                mode: 'scroller',
                display: 'bottom'
            });
        });
    </script>

    <link rel="stylesheet" type="text/css" href="/web/styles/mobile/wx<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css"
          href="/web/styles/mobile/wx-appoint<%=ConfigController.getBuildVersion()%>.css">
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/wx/wx_appoint<%=ConfigController.getBuildVersion()%>.js"></script>


</head>
<body>
<div class="container">
    <input type="hidden" id="openId" value="${openId}" autocomplete="off"/>

    <div class="td">
        <p class="td-first-p">
            <label>服务店面<span class="red_txt">*</span> </label>
           <span class="select select-area">
               <select id="shop">
                   <c:choose>
                       <c:when test="${not empty shopDTOs}">
                           <c:forEach items="${shopDTOs}" var="shopDTO" varStatus="status">
                               <option value="${shopDTO.idStr}" landline="${shopDTO.landline}"
                                       mobile="${shopDTO.mobile}">${shopDTO.name}</option>
                           </c:forEach>
                       </c:when>
                       <c:otherwise>
                           <option value="">您没有关注的店面</option>
                       </c:otherwise>
                   </c:choose>
               </select>
           </span>
        </p>
        <p><label>车牌号码<span class="red_txt">*</span></label>
           <span class="select select-area">
               <select id="vehicleNo" class="upper-case">
                   <c:choose>
                       <c:when test="${not empty vehicleDTOs}">
                           <c:forEach items="${vehicleDTOs}" var="vehicle" varStatus="status">
                               <option>${vehicle.vehicleNo}</option>
                           </c:forEach>
                       </c:when>
                       <c:otherwise>
                           <option value="">您还没有绑定车辆</option>
                       </c:otherwise>
                   </c:choose>
               </select>
           </span>
        </p>
        <p><label>服务类型<span class="red_txt">*</span></label>
           <span class="select select-area">
               <select id="serviceCategoryId">
                   <option value="10000010001000002">保养</option>
                   <option value="10000010001000001">维修</option>
                   <option value="10000010002000001">美容</option>
               </select>
           </span>
        </p>
        <p><label>预约时间<span class="red_txt">*</span></label>
                   <span class="select select-area" style="width:36%;margin-bottom:5px">
                       <input id="appointDate" value="${date}" placeholder="选日期" class="j_reg_date"
                              type="text" style="width:100%">
                   </span>
            <span class="select select-area" style="width:26%;margin-bottom:5px">
                   <input id="appointTime" value="${time}" placeholder="选时间" class="j_reg_date"
                          type="text" style="width:100%">
            </span>
        </p>

        <p><label>&nbsp;&nbsp;&nbsp;联系人<span class="red_txt">*</span></label>
            <input id="contact" value="${not empty userDTO.name?userDTO.name:userDTO.nickname}" placeholder="请输入联系人"
                   type="text" autocomplete="off">
        </p>

        <p><label>联系方式<span class="red_txt">*</span></label>
            <input id="mobile" value="${userDTO.mobile}" placeholder="请输入联系方式" class="roundinput" type="number"
                   autocomplete="off">
        </p>

        <p><label>&nbsp;备注内容</label>
            <textarea id="remark" class="text-area" type="text" placeholder="请输入备注内容" autocomplete="off"></textarea>
        </p>

        <div class="sub-b-p" style="text-align:center">
            <label id="errorMsg" class="error-msg"></label>

            <div class="center-div">
                <button id="submitBtn" class="submit-btn">提交</button>
            </div>
            <div style="height: 50px;"></div>
            <div class="clear"></div>
        </div>
    </div>
    <div class="bottom">
        <a id="contact_mobile" style="text-decoration:none; "
           href="tel:${not empty shopDTO.landline?shopDTO.landline:shopDTO.mobile}">
            <div class="bottomzuo">
                <div class="bottomzuoup">
                    <div class="b-img">
                        <img width="25" height="25" border="0" alt="" src="/web/images/dianhua.png">
                    </div>
                    <div class="b-doc">
                        <span class="doc">电话咨询</span>
                    </div>
                </div>
            </div>
        </a>

        <div class="fenxian"></div>
        <a id="contact_sms" style="text-decoration:none; " href="sms:${shopDTO.mobile}">
            <div class="bottomzuo">
                <div class="bottomzuoup">
                    <div class="b-img">
                        <img width="25" height="25" border="0" alt="" src="/web/images/email.png">
                    </div>
                    <div class="b-doc">
                        <span class="doc">短信咨询</span>
                    </div>
                </div>
            </div>
        </a>
    </div>
    <div class="clear"></div>
</div>
</body>
</html>
