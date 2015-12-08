<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>后台管理—店铺设置</title>
    <%-- styles --%>
  <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
  <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style.css"/>
  <link rel="stylesheet" type="text/css" href="styles/shopIndividuation.css"/>
  <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/uploadPreview.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript" src="js/shopIndividuation.js"></script>
  <script type="text/javascript" src="js/searchDefault.js"></script>
  <script type="text/javascript" src="js/bcgogo.js"></script>
  <script type="text/javascript" src="js/mask.js"></script>
</head>
<body>
<div class="main">
  <!--头部-->
  <div class="top">
    <div class="top_left">
      <div class="top_name">统购后台管理系统</div>
      <div class="top_image"></div>
      你好，<span>张三</span>|<a href="j_spring_security_logout">退出</a></div>
    <div class="top_right"><span>2011.11.23 14:01 星期三</span></div>
  </div>
  <!--头部结束-->
  <div class="body">
    <!--左侧列表-->
    <%@include file="/WEB-INF/views/left.jsp" %>
    <!--左侧列表结束-->
    <!--右侧内容-->
    <div class="bodyRight">
      <!--搜索-->
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
      <!--搜索结束-->
      <!--内容-->
      <div class="rightMain clear">
        <div class="rightTitle">
          <div class="rightLeft"></div>
          <div class="rightBody">
            <div class="titleHover"><a>店面个性化</a></div>
          </div>
        </div>

        <input type="hidden" id="hiddenSearchBtnClick"/>
        <input type="hidden" id="hiddenScene">
        <div style="float:left;">
          <span style="color:#000;font-size: 14px;">店面名称:</span>
          <input type="text" id="shopName" style="width:150px;height:20px;line-height: 20px"/>
          <input type="hidden" id="shopId"/>
        </div>


        <div style="float:left;margin-left: 20px;">
          <span style="color:#000;font-size: 14px;" id="type"> 类型:</span>
          <form:select path="scene" cssStyle="width:100px;height:22px;line-height: 22px;" onchange="changeShopConfigScene();">
            <form:options items="${scene}"/>
          </form:select>
        </div>

        <div style="float:left;;margin-left: 20px;">
          <input type="button" id="configSearchBtn" class="rightSearch" onfocus="this.blur();" value="查询"/>
        </div>

        <div style="float:left;;margin-left: 100px;">
          <input type="button" id="addShopConfig" class="rightSearch" onfocus="this.blur();" value="新增"/>
        </div>

        <div class="height"></div>

        <div>
          <table cellpadding="0" cellspacing="0" class="config_table" id="table_config" width="850px">
            <col width="120">
            <col width="170">
            <col width="220" >
            <col width="170" >
            <col width="150">
            <%--<col width="90">--%>
            <tr class="dm_table_title">
              <td style="border-left:none;">编号</td>
              <td>店铺</td>
              <td>类型</td>
              <td>描述</td>
              <td>开关</td>
            </tr>
          </table>
        </div>

        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="shopConfig.do?method=getShopConfigBySceneAndShop"></jsp:param>
          <jsp:param name="jsHandleJson" value="init"></jsp:param>
          <jsp:param name="data" value="{startPageNo:1,maxRows:10,shopId:$('#shopId').val(),shopName:$('#shopName').val(),scene:$('#type').next().val()}"></jsp:param>
          <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        </jsp:include>
        <!--内容结束-->
      </div>



      <!--内容结束-->
      <!--圆角-->
      <div class="bottom_crile clear">
        <div class="crile"></div>
        <div class="bottom_x"></div>
        <div style="clear:both;"></div>
      </div>
      <!--圆角结束-->
    </div>
    <!--右侧内容结束-->
  </div>
</div>
<!-- 店面 chenfanglei-->
<div id="div_shopName" class="i_scroll" style="display:none;width:250px;">

  <div class="Scroller-ContainerShopName" id="Scroller-Container_shopName">
  </div>

</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<iframe name="iframe_PopupBox" id="iframe_PopupBox" scrolling="no" style="position:absolute;z-index:5; left:500px; top:150px; display:none;"
        allowtransparency="true" width="600px" height="100%" frameborder="0" src=""></iframe>
</body>
</html>






</body>