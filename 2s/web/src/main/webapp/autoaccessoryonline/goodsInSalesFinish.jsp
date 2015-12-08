<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-8-25
  Time: 下午2:07
  To change this template use File | Settings | File Templates.
--%>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-7-31
  Time: 上午9:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>商品上架</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/goodsInOffSales<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_GOODS_IN_OFF_SALES_MANAGE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"上架");

        function toRenderShopMsgDetail(){
            var paramShopId=$("#paramShopId").val();
            if(!G.isEmpty(paramShopId)){
                window.location.href="shopMsgDetail.do?method=renderShopMsgDetail&shopMsgTabFlag=productList&paramShopId="+paramShopId;
            }
        }

        function getShopProductDetail(){
            var fromSource=$("#fromSource").val();
            if(fromSource=="batchGoodsInSalesEditor"){
//                toShopProductDetail($("#paramShopId").val(),$("#productIds").val());
                window.open("shopMsgDetail.do?method=renderShopMsgDetail" +
                        "&paramShopId="+$("#paramShopId").val()+"&productIds="+$("#productIds").val()+"&fromSource=batchGoodsInSalesEditor");
            }else if(fromSource=="goodsInSalesEditor"){
                toShopProductDetail($("#paramShopId").val(),$("#productIds").val(),"goodsInSalesEditor");
            }
        }

        //        function toShopProductDetail(paramShopId,productLocalId){
        //            var paramShopId=$("#paramShopId").val();
        //               if(!G.isEmpty(paramShopId)){
        //                   window.location.href="shopProductDetail.do?method=toShopProductDetail&paramShopId="+paramShopId+"&productLocalId="+$("#productId").val();
        //               }
        //        }

    </script>
</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="fromSource" value="${fromSource}"/>
<input type="hidden" id="productIds" value="${productIds}"/>
<input type="hidden" id="paramShopId" value="${paramShopId}"/>


<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="inSalingGoodsList"/>
        </jsp:include>
        <!-- TODO上架信息请关注此处 -->
        <div class="content-main">
            <div class="add-product-follow">
                <span style="margin-left:83px;">选择上架商品</span>
                <span style="margin-left:163px;">编辑商品详细信息</span>
                <span style="margin-left:176px;">上架成功</span>
            </div>

            <div class="add-finish">
                <img style="float: left;margin: 5px" src="../web/images/inSales-succ.png">
                <div style="float: left" class="content-titile">
                    <%--<a class="icon"></a>--%>
                    <span>恭喜您，您的商品上架成功！</span>
                </div>
                <div class="cl"></div>
                <div class="content-details">
                    <div style="margin-bottom: 50px">
                        <a class="blue_color" onclick="getShopProductDetail()">查看商品详细>></a>
                        <a onclick="toUnInSalingGoodsList()()" class="blue_color">继续发布商品>></a>
                        <a onclick="toInSalingGoodsList()" class="blue_color">返回已上架商品列表>></a>
                    </div>
                    <div style="margin-bottom: 7px">您还可以：</div>

                    <div style="margin-bottom: 7px">1.进入“<a href="autoAccessoryOnline.do?method=toCommodityQuotations" target="_blank" class="blue_color">配件报价</a>”查看商品报价。</div>
                    <div style="margin-bottom: 7px">2.进入“<a onclick="toRenderShopMsgDetail()" class="blue_color">您的店铺</a>”查看商品。</div>
                    <div style="margin-bottom: 7px">3.进入“<a onclick="toPromotionsManager()" class="blue_color">促销管理</a>”对商品进行促销设置。</div>


                </div>
            </div>
        </div>
    </div>

</body>
</html>