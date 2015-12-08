<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-10-12
  Time: 下午4:52
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>项目设置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/returnsTan<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/danjuCg<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/setService<%=ConfigController.getBuildVersion()%>.css">

</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>


<div class="title">
  <div class="title_left">
  </div>
  <div class="title_center"></div>
  <div class="title_right"></div>
</div>
<div class="i_main clear">
  <div class="mainTitles">
    <div class="titleWords">车辆</div>
    <div class="i_mainTitle stock_search">
      <a id="carWashBeauty" href="washBeauty.do?method=createWashBeautyOrder">洗车美容</a>
      <a id="carMaintain" href="txn.do?method=getRepairOrderByVehicleNumber&task=maintain">施工销售单</a>
      <a id="shoppingSell" class="title_hover" href="category.do?method=getCategoryItemSearch">项目设置</a>
    </div>
  </div>
  <div class="i_mainRight" id="i_mainRight">
    <div class="tuihuo_first">
      <span class="left_tuihuo"></span>
      <table>
        <col width="80"/>
        <col width="80"/>
        <col width="80"/>
        <col width="170"/>
        <col/>
        <tr>
          <td style="text-align:right;">施工内容：</td>
          <td>
            <input type="text"/>
          </td>
          <td>营业分类：</td>
          <td>
            <input type="text"/>

          </td>
          <td><input type="button" value="查询" class="search_btn"/></td>
        </tr>
      </table>
      <span class="right_tuihuo"></span>
    </div>
    <div class="clear"></div>
    <ul class="yinye_title clear">
      <li id="fencount">全部项目</li>
      <li id="first_cont">已分类项目</li>
      <li id="liushui" class="hover_yinye">未分类项目</li>
    </ul>
    <div class="clear"></div>
    <div class="tuihuo_tb">
      <!--<table class="tui_title">
        <col width="80"/>
        <col />
        <tr>
          <td>共<a href="#">4</a>条记录</td>
          <td>其中(施工单<a href="#">4</a>条记录&nbsp;采购单<a href="#">4</a>条记录&nbsp;销售单<a href="#">4</a>条记录&nbsp;退货单<a href="#">4</a>条记录)</td>
          </tr>
      </table>-->
      <table class="clear" id="tb_tui">
        <col/>
        <col width="166"/>
        <col width="166"/>
        <col width="166"/>
        <col width="166"/>
        <col width="166"/>
        <col width="166"/>
        <col/>
        <tr class="tab_title">
          <td class="tab_first"></td>
          <td>施工内容</td>
          <td><input type="button" class="se_xiaoshou" value="营业分类" onfocus="this.blur();" id="setSale"/></td>
          <td>工时</td>
          <td>金额/工时费</td>
          <td><input type="button" class="se_xiaoshou" value="员工提成" onfocus="this.blur();" id="setSale"/></td>

          <td><label>操作</label></td>
          <td class="tab_last"></td>
        </tr>
        <tr>
          <td></td>
          <td style="border-left:none;">镀膜</td>

          <td>美容</td>
          <td></td>
          <td>555</td>
          <td>222</td>
          <td><a href="#">编辑</a>&nbsp;|&nbsp;<a href="#">删除</a></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td style="border-left:none;">镀膜</td>

          <td>美容</td>
          <td></td>
          <td>555</td>
          <td>222</td>
          <td><a href="#">编辑</a>&nbsp;|&nbsp;<a href="#">删除</a></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td style="border-left:none;">镀膜</td>

          <td>美容</td>
          <td></td>
          <td>555</td>
          <td>222</td>
          <td><a href="#">编辑</a>&nbsp;|&nbsp;<a href="#">删除</a></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td style="border-left:none;">镀膜</td>

          <td>美容</td>
          <td></td>
          <td>555</td>
          <td>222</td>
          <td><a href="#">编辑</a>&nbsp;|&nbsp;<a href="#">删除</a></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td style="border-left:none;">镀膜</td>

          <td>美容</td>
          <td></td>
          <td>555</td>
          <td>222</td>
          <td><a href="#">编辑</a>&nbsp;|&nbsp;<a href="#">删除</a></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td style="border-left:none;">镀膜</td>

          <td>美容</td>
          <td></td>
          <td>555</td>
          <td>222</td>
          <td><a href="#">编辑</a>&nbsp;|&nbsp;<a href="#">删除</a></td>
          <td></td>
        </tr>
      </table>
    </div>
  </div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<!--<div  class="tuihuo"></div>
-->
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
