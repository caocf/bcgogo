<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>

<%-- js components,  you need import styles manually "style_ui_components.jsp"
     dependency: jquery-1.4.2+ , jquery-ui-1.7.2+
--%>
<script type="text/javascript" src="js/components/ui/bcgogo-autocomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-detailsList<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-searchBar<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-searchcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-autocomplete-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-detailsList-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-searchcomplete-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-messagePopup<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-menupanel<%=ConfigController.getBuildVersion()%>.js"></script>
<%--<script type="text/javascript" src="js/components/ui/bcgogo-shadow<%=ConfigController.getBuildVersion()%>.js"></script>--%>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-paging<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>

<%-- add kiss foucs, 功能说明： 第一次 click 全选， 第二次点击操作， 则不全选 --%>
<script type="text/javascript" src="js/components/ui/bcgogo-kissFocus<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-bubbletips<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-messageBottomPush<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/components/ui/bcgogo-qqInvoker<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/components/ui/bcgogo-activityChecker<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-wait-mask<%=ConfigController.getBuildVersion()%>.js"></script>