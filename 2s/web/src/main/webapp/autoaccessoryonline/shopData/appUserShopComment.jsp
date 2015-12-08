<%--
  Created by IntelliJ IDEA.
  User: king
  Date: 13-9-12
  Time: 下午2:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>店铺评价</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/shopData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.ONLINE.SHOP_DATA_MENU");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"店铺评价");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <input type="hidden" id="paramShopId" name="paramShopId" value="${paramShopId}"/>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="promotions"/>
        </jsp:include>
        <div class="store_right2">
            <div class="store_introduce" style="width: 100%;">
                <span class="store_title">
                    <a onclick="toManageShopData()" class="shopevaluation-tab1" onselectstart="return false;">本店资料</a>
                    <a onclick="toShopComment()"  class="shopevaluation-tab2" onselectstart="return false;">本店评价</a>
                    <div class="cl"></div>
                </span>
                <div class="shopevaluation" id="averageComment"><b>店铺综合评分</b> <a  class="star" id="averageCommentStar"></a> <strong class="yellow_color" id="averageScoreAmount">0分</strong> <span style="color:#999999;">共<span id="recordAmount">0</span>次服务被评价</span></div>
                <div class="shopevaluation" style="display:none;color:#999999;" id="noComment">暂无评价！</div>
                <div class="shoppingCart cuSearch storeCart" id="appUserCommentTableDiv">
                    <div class="cartTop"></div>
                    <div class="gray-radius">
                        <table cellpadding="0" cellspacing="0" class="tabCart" style="width:771px;" id="appUserCommentTable">
                            <col width="50">
                            <col width="80">
                            <col width="180">
                            <col width="220">
                            <col>
                            <col  width="80">
                            <tr class="titleBg">
                                <td style="padding-left:10px;">No</td>
                                <td>评价时间</td>
                                <td>评分</td>
                                <td>详细评论</td>
                                <td>客户</td>
                                <td>单据号</td>
                            </tr>
                        </table>
                        <div class="i_height"></div>
                        <div class="i_pageBtn" style="float:right;margin: 10px 0 10px 0">
                            <bcgogo:ajaxPaging url="supplier.do?method=getAppUserCommentRecord" postFn="initAppUserCommentRecord" dynamical="initAppUserCommentRecord" data="{startPageNo:'1',maxRows:5,paramShopId:$('#paramShopId').val()}"/>
                        </div>
                        </div>
                    <div class="cartBottom"></div>
                </div>
            </div>
            <div class="height"></div>
        </div>
    </div>
    <div class="height"></div>
    <!----------------------------页脚----------------------------------->
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>
</div>
<div id="mask"  style="display:block;position: absolute;"> </div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
</body>
</html>
