<%--
  Created by IntelliJ IDEA.
  User: XinyuQiu
  Date: 13-12-6
  Time: 下午2:07
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--需要加冒泡数字的话，参考remindNavi.jsp--%>
<div class="titleList J_remind_navi_menu">
    <ul>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
            <li>
                <a class="click" href="customer.do?method=carindex">施工首页</a>
            </li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE" resourceType="menu">
            <li>
                <a href="washBeauty.do?method=createWashBeautyOrder">洗车美容</a>
            </li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE" resourceType="menu">
            <li>
                <a href="txn.do?method=getRepairOrderByVehicleNumber&task=maintain">施工销售</a>
            </li>
            <li>
                <a href="repairOrderSecondary.do?method=inquiryRepairOrderSecondary">结算附表</a>
            </li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.INSURANCE" resourceType="menu">
            <li>
                <a href="insurance.do?method=showInsuranceOrderList">保险理赔</a>
            </li>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.SET_CONSTRUCTION_PROJECT" resourceType="menu">
            <li>
                <a href="category.do?method=toSetCategoryPage">项目设置</a>
            </li>
        </bcgogo:hasPermission>


    </ul>
</div>
