<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>促销管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="promotions"/>
            <jsp:param name="biMenu" value="promotionManager"/>
        </jsp:include>

        <div class="bodyLeft bodyCenter">


            <div class="cuSearch" style="float:left;">
                <div class="lineTop"></div>
                <div class="cartBody lineBody createPromotion">
                    <span class="createTitle">创建流程</span>
                    <div class="chartStep blue_color">
                        <span class="yellow_color">1、促销设置</span>
                        <a class="stepImg"></a>
                        <span>2、添加上架商品</span>
                        <a class="stepImg"></a>
                        <span>3、促销中的商品</span>
                        <a class="stepImg"></a>
                        <span>4、推广您的促销</span>
                    </div>
                    <div class="div_alert">友情提示：同一时间段，同一商品不可同时参与满立减、满就送或特价活动哦！</div>
                </div>
                <div class="lineBottom"></div>
                <div class="clear i_height"></div>
                <div class="create">
                    <h3 style="color:#000000;">您可以创建打折、特价、送货上门等促销活动哦！</h3>
                    <div class="createList">
                        <span class="createPromotionsBtn promotion_title_icon icon_minus " promotions_type="MLJ"></span>
                        <div class="minus">
                            <b class="title">帮助您轻松提示客单价！</b>
                            <a promotions_type="MLJ" class="createPromotionsBtn blue_color">立即创建>></a>
                        </div>
                    </div>
                    <div class="createList">
                        <span class="createPromotionsBtn promotion_title_icon icon_special" promotions_type="BARGAIN"></span>
                        <div class=" minus special">
                            <b class="title">推新品，清库存，快速出单！</b>
                            <a promotions_type="BARGAIN" class="createPromotionsBtn blue_color">立即创建>></a>
                        </div>
                    </div>
                    <div class="createList">
                        <span class="createPromotionsBtn promotion_title_icon icon_send" promotions_type="MJS"></span>
                        <div class="minus send">
                            <b class="title">快速刺激买家下单！</b>
                            <a promotions_type="MJS" class="createPromotionsBtn blue_color">立即创建>></a>
                        </div>
                    </div>
                    <div class="createList">
                        <span class="createPromotionsBtn promotion_title_icon icon_avoid" promotions_type="FREE_SHIPPING" ></span>
                        <div class="minus avoid">
                            <b class="title">吸引客户下单，积累客户数量！</b>
                            <a promotions_type="FREE_SHIPPING" class="createPromotionsBtn blue_color">立即创建>></a>
                        </div>
                    </div>
                </div>

            </div>



        </div>
        <div class="bodyRight">
            <img src="../../web/images/registerImg.png" style="width:178px;" />
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>