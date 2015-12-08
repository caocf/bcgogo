<%@ page import="com.bcgogo.common.WebUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>公告中心</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/sysAnnouncement<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/sysAnnouncement<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
      $(document).ready(function(){
          App.Menu.Function.doNavigate("公告中心",{"href":"user.do?method=createmain","label":"首页"});
      });
  </script>
</head>


<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">

  <div class="mainTitles">
    <div class="titleWords">公告中心</div>
  </div>
  <div class="clear"></div>
  <div class="systemBody">
    <h2 id="nTitle">${nAnnouncement.title}</h2>
    <span style="float:right; padding-right:250px;" id="nReleaseDate">(${nAnnouncement.releaseDate})</span>
    <div class="height"></div>
    <div style="font-size:14px;" class="sysAnnouncement_content" id="nContent">${nAnnouncement.content}</div>
    <div class="height"></div>
    <div class="systemTime">
      <div class="sys" style="padding-left:10px;">
        <a class="sysList sysList1"></a>
        <a class="sysList sysList2"></a>
        <a class="sysList sysList3"></a>
        <a class="sysList sysList4"></a>
      </div>
      <div class="sys"  style="border:none;">
        <a class="sysList sysList5"></a>
        <a class="sysList sysList6"></a>
        <a class="sysList sysList7"></a>
        <a class="sysList sysList8"></a>
      </div>

      <div class="clear i_height"></div>
      <!--分页-->
      <div class="hidePageAJAX">
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="sysReminder.do?method=getAnnoucementTitleList"></jsp:param>
          <jsp:param name="data" value="{startPageNo:1,pageSize:8}"></jsp:param>
          <jsp:param name="jsHandleJson" value="initAnnouncements"></jsp:param>
          <jsp:param name="hide" value="hideComp"></jsp:param>
          <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        </jsp:include>
      </div>
      <div class="height"></div>
    </div>
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>


</html>