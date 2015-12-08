<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-8-14
  Time: 下午2:41
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
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/shopData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.ONLINE.SHOP_DATA_MENU");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"店铺评价");

        $().ready(function(){

            $(".shop-rate .vtab-item").live("mouseover",function(){
                $(".vtab-item").removeClass("actived");
                $(this).addClass("actived");
                var comment=commentHolder[$(this).attr("rateType")];
                if(!G.isEmpty(comment)){
                    $("#totalScore").text(comment.totalScore+"分");
                    $("#totalScoreStar").attr("starLever","normal-light-star-level-"+comment.totalScore*2);
                    $(".rate-percent-fiveAmountPer .percent-value").css("width",comment.fiveAmountPer);
                    $(".rate-percent-fiveAmountPer .percent-number").text(comment.fiveAmountPer);
                    $(".rate-percent-fourAmountPer .percent-value").css("width",comment.fourAmountPer);
                    $(".rate-percent-fourAmountPer .percent-number").text(comment.fourAmountPer);
                    $(".rate-percent-threeAmountPer .percent-value").css("width",comment.threeAmountPer);
                    $(".rate-percent-threeAmountPer .percent-number").text(comment.threeAmountPer);
                    $(".rate-percent-twoAmountPer .percent-value").css("width",comment.twoAmountPer);
                    $(".rate-percent-twoAmountPer .percent-number").text(comment.twoAmountPer);
                    $(".rate-percent-oneAmountPer .percent-value").css("width",comment.oneAmountPer);
                    $(".rate-percent-oneAmountPer .percent-number").text(comment.oneAmountPer);
                }

            });
             //获取店铺评分详细信息
            getShopComment(function(shopInfo){
                initShopCommentDetail(shopInfo);
                $(".vtab-item-default").mouseover();
                $(".vtab-item-default").addClass("actived")
            });


        });

    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<input type="hidden" id="paramShopId" name="paramShopId" value="${paramShopId}"/>
<div class="clear i_height"></div>
<div class="titBody">
<jsp:include page="../supplyCenterLeftNavi.jsp">
    <jsp:param name="currPage" value="promotions"/>
</jsp:include>
<div class="content-main">
<dl class="shop-rate">
<dt class="content-title">
<div class="bg-top-hr"></div>
<div class="bar-tab">
    <span onclick="toManageShopData()" class="label" onselectstart="return false;">本店资料</span>
    <span onclick="toShopComment()" class="label actived" onselectstart="return false;">本店评价</span>
    <div class="cl"></div>
</div>
</dt>
<div class="cl"></div>

<dd class="content-details">
<div class="info-basic fl">
    <div class="shop-name"></div>
    <div class="rate">
        <div id="shopTotalScoreStar" class="rate-star fl"></div>
        <div id="shopTotalScore" class="rate-score fl"></div>
        <div class="cl"></div>
    </div>
    <div class="rate-note">共<span class="recordAmountSpan">0</span>名客户参与评价</div>
</div>
<div class="bg-vr-grey fl"></div>
<div class="info-details fl">
    <div class="rate fl">
        <div class="rate-average">
            <div id="totalScoreStar" class="rate-star fl" starLever="normal-light-star-level-0"></div>
            <div id="totalScore" class="rate-score fl"></div>
            <div class="rate-note fl">共<span class="recordAmountSpan">0</span>人</div>
            <div class="cl"></div>
        </div>
        <div class="rate-details">
            <div class="rate-item">
                <!--<div class="rate-name fl">5</div>-->
                <div class="rate-star fl lite-light-star-level-10"></div>
                <div class="rate-score fl"><span class="number-red">4.0 - 5.0&nbsp;</span>分</div>
                <div class="rate-percent rate-percent-fiveAmountPer fl">
                    <div class="percent-bar fl">
                        <!--使用 js 设置 width 的百分比-->
                        <div class="percent-value"></div>
                    </div>
                    <div class="percent-number fl"></div>
                    <div class="cl"></div>
                </div>
                <div class="cl"></div>
            </div>
            <div class="rate-item">
                <!--<div class="rate-name fl">4</div>-->
                <div class="rate-star fl lite-light-star-level-8"></div>
                <div class="rate-score fl"><span class="number-red">3.0 - 4.0&nbsp;</span>分</div>
                <div class="rate-percent rate-percent-fourAmountPer fl">
                    <div class="percent-bar fl">
                        <!--使用 js 设置 width 的百分比-->
                        <div class="percent-value"></div>
                    </div>
                    <div class="percent-number fl"></div>
                    <div class="cl"></div>
                </div>
                <div class="cl"></div>
            </div>
            <div class="rate-item">
                <!--<div class="rate-name fl">3</div>-->
                <div class="rate-star fl lite-light-star-level-6"></div>
                <div class="rate-score fl"><span class="number-red">2.0 - 3.0&nbsp;</span>分</div>
                <div class="rate-percent rate-percent-threeAmountPer fl">
                    <div class="percent-bar fl">
                        <!--使用 js 设置 width 的百分比-->
                        <div class="percent-value"></div>
                    </div>
                    <div class="percent-number fl"></div>
                    <div class="cl"></div>
                </div>
                <div class="cl"></div>
            </div>
            <div class="rate-item">
                <!--<div class="rate-name fl">2</div>-->
                <div class="rate-star fl lite-light-star-level-4"></div>
                <div class="rate-score fl"><span class="number-red">1.0 - 2.0&nbsp;</span>分</div>
                <div class="rate-percent rate-percent-twoAmountPer fl">
                    <div class="percent-bar fl">
                        <!--使用 js 设置 width 的百分比-->
                        <div class="percent-value"></div>
                    </div>
                    <div class="percent-number fl"></div>
                    <div class="cl"></div>
                </div>
                <div class="cl"></div>
            </div>
            <div class="rate-item">
                <!--<div class="rate-name fl">1</div>-->
                <div class="rate-star fl lite-light-star-level-2"></div>
                <div class="rate-score fl"><span class="number-red">0.0 - 1.0&nbsp;</span>分</div>
                <div class="rate-percent rate-percent-oneAmountPer fl">
                    <div class="percent-bar fl">
                        <!--使用 js 设置 width 的百分比-->
                        <div class="percent-value"></div>
                    </div>
                    <div class="percent-number fl"></div>
                    <div class="cl"></div>
                </div>
                <div class="cl"></div>
            </div>
            <div class="cl"></div>
        </div>
        <div class="cl"></div>
    </div>
    <div class="vtab">
        <div class="vtab-item" rateType="quality">
            <div class="score-name fl">卖家货品质量:</div>
            <div id="qualityTotalScore" class="score-number fl"></div>
            <div class="cl"></div>
        </div>
        <div class="vtab-item" rateType="performance">
            <div class="score-name fl">货品的性价比:</div>
            <div id="performanceTotalScore" class="score-number fl"></div>
            <div class="cl"></div>
        </div>
        <div class="vtab-item" rateType="speed">
            <div class="score-name fl">卖家发货速度:</div>
            <div id="speedTotalScore" class="score-number fl"></div>
            <div class="cl"></div>
        </div>
        <div class="vtab-item vtab-item-default" rateType="attitude">
            <div class="score-name fl">卖家服务态度:</div>
            <div id="attitudeTotalScore" class="score-number fl"> </div>
            <div class="cl"></div>
        </div>
    </div>
    <div class="cl"></div>
</div><!--end info-details-->

<div class="cl"></div>
</dd>
</dl><!--end shop-rate-->


<!--TODO-ongoing-->
<dl class="customer-rate">
    <dt class="content-title">
    <div class="bg-top-hr"></div>
    <div class="bar-tab">
        客户评价
    </div>
    </dt>

    <dd class="content-details">
        <table id="supplierCommentRecordTable" class="details-table">
            <thead>
            <tr>
                <th class="col-no txtl th-first">No</th>
                <th class="col-time txtl">评价时间</th>
                <th class="col-score txtl">评分</th>
                <th class="col-comment txtl">详细评论</th>
                <th class="col-customer txtl th-last">客户</th>
            </tr>
            </thead>

        </table>
        <div class="i_pageBtn" style="float:right;margin: 10px 0 10px 0">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="supplier.do?method=getSupplierCommentRecord"></jsp:param>
                <jsp:param name="jsHandleJson" value="initSupplierCommentRecord"></jsp:param>
                <jsp:param name="dynamical" value="initSupplierCommentRecord"></jsp:param>
                <jsp:param name="data" value="{startPageNo:'1',maxRows:5,paramShopId:$('#paramShopId').val()}"></jsp:param>
            </jsp:include>
        </div>
    </dd>
</dl>
</div><!--end customer-rate-->
<div class="cl"></div>
</div>
</div>


<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>