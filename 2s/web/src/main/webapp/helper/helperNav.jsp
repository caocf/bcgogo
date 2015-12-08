<%--
  User: ndong
  Date: 13-2-6
  Time: 上午7:29
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<script type="text/javascript">
    $(document).ready(function(){
        App.Menu.Function.doNavigate("帮助中心",{"href":"user.do?method=createmain","label":"首页"});
    });
</script>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>" />
<div class="clear left">
  <div class="titles">
    <div class="leftTit">帮助公告</div>
    <div class="${currPage eq 'importDataHelper' ? 'hover' :''} blue_col" onclick="toHelper('importDataHelper')">数据导入功能说明</div>
    <div class="${currPage eq 'transferHelper' ? 'hover' :''} blue_col" onclick="toHelper('transferHelper')">银联转账功能说明</div>
    <div class="${currPage eq 'staffConfigHelper' ? 'hover' :''} blue_col" onclick="toHelper('staffConfigHelper')">人员权限配置说明</div>
    <div class="${currPage eq 'printHelper' ? 'hover' :''} blue_col" onclick="toHelper('printHelper')">打印机配置说明</div>
    <div class="${currPage eq 'appInstallHelper' ? 'hover' :''} blue_col" onclick="toHelper('appInstallHelper')">手机APP安装说明</div>
  </div>

</div>