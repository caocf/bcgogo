<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>宣传管理</title>

  <%@ include file="/WEB-INF/views/includes.jsp" %>
  <%@include file="/WEB-INF/views/header_script.jsp" %>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>


  <link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>


  <%@ include file="/WEB-INF/views/image_script.jsp" %>

  <script type="text/javascript" src="js/shopAdvert/advertList<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.config<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.all<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.parse<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" charset="utf-8" src="js/extension/jquery/plugin/scrollbar<%=ConfigController.getBuildVersion()%>.js"></script>


  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB_CUSTOMER_MANAGER_ADVERT_MANAGE");
  </script>


</head>


<body class="bodyMain">

<%@ include file="/WEB-INF/views/header_html.jsp" %>

<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<input type="hidden" id="beginDate" value="${beginDateStr}">

<div class="i_main clear">
    <%@ include file="previewAdvert.jsp" %>
  <div class="mainTitles">
    <div class="titleWords">宣传管理</div>
  </div>
  <div class="clear"></div>
  <form id="shopAdvertSearchForm" name="shopAdvertSearchForm"
        action="advert.do?method=searchShopAdvert"
        method="post">

    <input type="hidden" name="startPageNo" id="startPageNo" value="1">

    <div id="todo_content">
      <div class="divTit" style="width:100%;">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
          <colgroup>
            <col width="100"/>
            <col/>
          </colgroup>
          <tr>
            <th>日期：</th>
            <td id="selectDate"><a class="btnList" id="my_date_yesterday" pagetype="advertList" name="my_date_select">昨天</a>&nbsp;<a class="btnList"
                                                                                               id="my_date_today"
                                                                                               name="my_date_select" pagetype="advertList">今天</a>&nbsp;<a
                class="btnList" id="my_date_thismonth" name="my_date_select" pagetype="advertList">本月</a>&nbsp;<a
                class="btnList" id="my_date_thisyear" name="my_date_select" pagetype="advertList">今年</a>
              <input type="text" id="startDate" readonly="readonly" name="startTimeStr"
                     class="textbox txt my_startdate"/>
              至
              <input type="text" id="endDate" readonly="readonly" name="endTimeStr" class="textbox txt my_enddate"/>
            </td>
          </tr>
          <tr>
            <th>单据类型：</th>
            <td><label class="rad">
              <input type="checkbox" id="WAIT_PUBLISH" name="advertStatus" checked="checked" value="WAIT_PUBLISH"/>
              待发布</label>
              <label class="rad">
                <input type="checkbox" id="ACTIVE" name="advertStatus" checked="checked" value="ACTIVE"/>
                已生效 </label>
              <label class="rad">
                <input type="checkbox" id="OVERDUE" name="advertStatus" checked="checked" value="OVERDUE"/>
                已过期 </label>
              <label class="rad">
                <input type="checkbox" name="advertStatus" value="REPEALED"/>
                已作废 </label></td>
          </tr>
        </table>
        <div class="search_div">
          <div id="searchBtn" class="search_btn">查 询</div>
          <div id="clearBtn" class="empty_btn">清空条件</div>
          <div class="clear"></div>
        </div>
        <div class="clear height"></div>
      </div>
      <div class="score_list">
        <div class="clear">
          <div id="addAdvert" style="float: right;" class="empty_btn">新增宣传</div>
        </div>
        <div class="i_height clear"></div>
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="score_table" id="advertTable">
          <tr>
            <th>序号</th>
            <th>日期</th>
            <th>标题</th>
            <th>有效期</th>
            <th>发布人</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </table>
        <div id="noAdvertList" style="display:none;text-align:center;color: #444443;margin-top: 10px;">对不起！没有符合条件的宣传！</div>
        <div class="height"></div>

        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="advert.do?method=searchShopAdvert"></jsp:param>
          <jsp:param name="dynamical" value="dynamicalAdvert"></jsp:param>
          <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
          <jsp:param name="jsHandleJson" value="initShopAdvert"></jsp:param>
          <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
      </div>
    </div>

  </form>

</div>

<%@ include file="addAdvert.jsp" %>

<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>
