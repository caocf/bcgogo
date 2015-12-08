<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>车价估估</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <%
        response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
        response.setHeader("Pragma", "no-cache"); //HTTP 1.0
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    %>
    <c:if test="${iPhone!=true}">
        <%--mobiscroll 要写在前面，解决jquery版本问题--%>
        <link href="/web/js/extension/jquery/plugin/mobiscroll/css/mobiscroll.widget.css" rel="stylesheet" type="text/css" />
        <link href="/web/js/extension/jquery/plugin/mobiscroll/css/mobiscroll.scroller.css" rel="stylesheet" type="text/css" />
        <script src="/web/js/extension/jquery/jquery-1.11.0.min.js"></script>

        <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.core.js"></script>
        <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.util.datetime.js"></script>
        <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.widget.js" type="text/javascript"></script>
        <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.scroller.js" type="text/javascript"></script>
        <script src="/web/js/extension/jquery/plugin/mobiscroll/mobiscroll.datetime.js" type="text/javascript"></script>
        <script src="/web/js/extension/jquery/plugin/mobiscroll/i18n/mobiscroll.i18n.zh.js" type="text/javascript"></script>
        <script type="text/javascript">
            var $11 = jQuery.noConflict(true);
        </script>
        <script type="text/javascript">
            $11(function(){
                $11(".j_reg_date").mobiscroll().date({
                    lang: 'zh',
                    display: 'bottom',
                    dateOrder: 'yyMM',
                    dateFormat: 'yyyy-MM'
                });
            });
        </script>
    </c:if>

    <link rel="stylesheet" type="text/css" href="/web/styles/wechat<%=ConfigController.getBuildVersion()%>.css">
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/devUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/wx/v_evaluate<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body>
<div class="v-container">
    <input type="hidden" id="openId" value="${openId}" autocomplete="off"/>
    <div class="exline clearfix">
        <p class="banner_title">精准估值您的爱车</p>
    </div>
    <div class="exline clearfix">
        <label class="label-left">车牌号</label>
        <div class="labelright">
            <input  id="vehicleNo" value="${vehicleNo}"  placeholder="车牌号" class="roundinput upper-case" type="text" style="width:90%;" autocomplete="off">
        </div>
    </div>
    <div class="exline clearfix">
        <label class="label-left">选择车系<span class="red_txt">*</span></label>

        <div class="labelright">
            <div id="d-brand-select" class="brand-select">

                <select name="lstype" id="s_brand" class="h-select" autocomplete="off">
                    <option value="">选择品牌</option>
                    <c:forEach items="${brandDTOs}" var="brand" varStatus="status">
                        <option value="${brand.id}">${brand.name}</option>
                    </c:forEach>
                </select>
                <select name="lstype" id="s_series" class="h-select" autocomplete="off">
                    <option value="">选择车系</option>
                </select>

            </div>

        </div>
    </div>
    <div class="exline clearfix">
        <label class="label-left">具体车型<span class="red_txt">*</span></label>

        <div class="labelright">
            <div id="d-model-select" class="brand-select">
                <select name="lstype" id="s_model" class="h-select">
                    <option value="">选择车型</option>
                </select>
            </div>
        </div>
    </div>
    <div class="exline clearfix">
        <label class="label-left">上牌时间<span class="red_txt">*</span></label>
        <div class="labelright">
            <c:choose>
                <c:when test="${iPhone==true}">
                    <input id="regDate"  class="roundinput wx_ipt" type="month"  autocomplete="off" />
                </c:when>
                <c:otherwise>
                    <input id="regDate" placeholder="选择年份" class="roundinput j_reg_date" type="text"  style="width:90%;" autocomplete="off">
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="exline clearfix">
        <label class="label-left">所在地区<span class="red_txt">*</span></label>
        <div class="labelright" id="orgright">
            <select name="lstype" id="provinceNo" autocomplete="off" class="h-select" style="width: 46%">
                <option value="">—省份—</option>
                <c:forEach items="${provAreaDTOs}" var="area" varStatus="status">
                    <option value="${area.no}">${area.name}</option>
                </c:forEach>
            </select>
            <select name="lstype" id="cityNo" autocomplete="off" class="h-select" style="width: 47%;">
                <option value="">—城市—</option>
            </select>
        </div>
    </div>
    <div class="exline clearfix">
        <label class="label-left">行驶里程<span class="red_txt">*</span></label>
        <div class="labelright d-mile">
            <input  id="mile" type="number" class="roundinput"  autocomplete="off">
            <label>公里</label>
        </div>
    </div>
    <div class="mtop5">

        <label id="errorMsg" class="error-msg"></label>
        <div>
            <input name="query" id="submitBtn" value="提交" class="btn" type="button">
        </div>
    </div>
    <div class="eval-result-d" style="display: none">
        <%--<div> <span id="rModel"></span> </div>--%>
        <div style="width:100%;height:10px;border-top:1px dashed #e6e6e6;"></div>
        <div> <label  class="r-left-msg">您的爱车估值:&nbsp;&nbsp;&nbsp;&nbsp;</label> <span id="evalPrice" class="r-right-msg"></span> </div>
        <div> <label  class="r-left-msg">车况一般的估值:</label> <span id="lowPrice" class="r-right-msg"></span> </div>
        <div> <label  class="r-left-msg">车况良好的估值:</label> <span id="goodPrice" class="r-right-msg"></span> </div>
        <div> <label  class="r-left-msg">车况优秀的估值:</label> <span id="highPrice" class="r-right-msg"></span> </div>
        <div> <label  class="r-left-msg">新车指导价:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label> <span id="price" class="r-right-msg"></span> </div>
        <div>
            <img src="/web/images/che300_logo.png" width="60" height="20"/> 评估价由“车三百”评估认证
        </div>
    </div>
    <div style="height:80px"></div>
</div>
</body>
</html>