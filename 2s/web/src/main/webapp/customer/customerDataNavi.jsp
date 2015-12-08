<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>

<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SEARCH.APPLY.CUSTOMER">
    <div class="titleList">
        <a class="${currPage eq 'customerData' ? 'click' :''}" action-type="menu-click"
           menu-name="WEB.CUSTOMER_MANAGER.BASE"
           href="customer.do?method=customerdata">本店客户</a>
        <a class="${currPage eq 'applierCustomerData' ? 'click' :''}" action-type="menu-click"
           menu-name="CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER"
           href="apply.do?method=getApplyCustomersPage"
           id="applierCustomerA">推荐客户</a>
    </div>
</bcgogo:hasPermission>
