
<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-9-12
  Time: 下午7:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>选择人员</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css">

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnsTan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/danjuCg<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up3<%=ConfigController.getBuildVersion()%>.css"/>

    <link rel="stylesheet" type="text/css" href="styles/selectMan<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectMan<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
       <bcgogo:permissionParam permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
        var repairPermission = ${permissionParam1};
        var washPermission = ${permissionParam2};
        var memberPermission = ${permissionParam3};
      </bcgogo:permissionParam>
    </script>
</head>
<body style="width:710px">
<input id="personNum" type="hidden" value="${personNum}"/>

<div class="i_history" style="width:830px;">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">选择人员</div>
        <div class="i_close" id="div_close" style="left:760px;"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div id="div_arrear" class="clear">
            <div class="more_his" style="color:#000">
                <span style="margin-right:20px;">共有<a href="#" id="allUser" class="blue_col">${total}名</a></span>
                <span style="margin-right:10px;">其中有<a href="#"
                                                         id="allCustomer" class="blue_col">客户${customerNum}名</a></span>
                <span style="margin-right:10px;">有<a href="#" id="allHasMobile" class="blue_col">手机${mobileNum}名</a></span>
                <span style="margin-right:10px;">有<a href="#" id="allSupplier" class="blue_col">供应商${supplierNum}名</a></span>&nbsp;
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                    <span>有<a href="#" id="allMember" class="blue_col">会员${memberNum}名</a></span>
                </bcgogo:hasPermission>
                <%--<input type="text" id="keyWords" style="color:#7F7F7F" value="客户/供应商/联系人/车牌号/会员号/手机号" class="txt_more"/>--%>
                <%--<input type="button" class="btnSearch" onfocus="this.blur();" value="查询" />--%>
            </div>

            <div class="clear"></div>
            <div class="tuihuo_tb">
              <bcgogo:permissionParam permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                <table class="tui_title select_title" style="width:748px;font-size: 12px">
                    <col width="80"/>
                    <col/>
                    <tr>
                        <td>
                            <input type="text" id="customer_supplierInfoText" pagetype="remind/selectMan"
                                   initialValue="客户/供应商/联系人/<c:if test="${permissionParam1 || permissionParam2}">车牌号/</c:if><c:if test="${permissionParam3}">会员号/</c:if>手机号"
                                   value="客户/供应商/联系人/<c:if test="${permissionParam1 || permissionParam2}">车牌号/</c:if><c:if test="${permissionParam3}">会员号/</c:if>手机号"
                                   style="color:#7F7F7F;width:280px;height: 25px" value="" class="txt_more"/>
                        </td>
                        <td><input type="button" id="seatchBtn" class="buttonSmall" onfocus="this.blur();" value="查询"/>
                        </td>
                    </tr>
                </table>

                <table class="clear" id="tb_tui" style="border-collapse:collapse;font-size: 12px;width: 720px;">
                    <col width="30"/>
                    <col width="50"/>
                    <col width="160"/>
                    <c:if test="${permissionParam1 || permissionParam2}"><col width="120"/></c:if>
                    <c:if test="${permissionParam3}"><col width="135"/></c:if>
                    <col width="135"/>
                    <tr class="tab_title">
                        <td><input type="checkbox" id="checkAll"/></td>
                        <td>NO</td>
                        <td>客户/供应商</td>
                        <td>联系人</td>
                        <c:if test="${permissionParam1 || permissionParam2}"><td>车牌号</td></c:if>
                        <c:if test="${permissionParam3}"><td>会员号</td></c:if>
                        <td>联系方式</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox"/></td>
                        <td>1</td>
                        <td>机油</td>
                        <td>AA</td>
                        <td>aa</td>
                        <td>苏D22224</td>
                        <td>大众</td>
                    </tr>

                </table>
              </bcgogo:permissionParam>
            </div>
            <!--分页-->
            <div class="clear"></div>
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="remind.do?method=getCustomerAndSupplier"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
                <jsp:param name="data"
                           value="{startPageNo:1,maxRows:10,keyWords:$.trim($(\'#keyWords\').val())}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initTr"></jsp:param>
            </jsp:include>
            <!--分页结束-->
            <!--结算-->

            <div class="clear"></div>
            <div class="tableInfo4">
                <div style="display:block; float:left; margin:20px 0px 0px 0px;">
                    <input type="button" value="确认" id="confirm" onfocus="this.blur();" class="btn"/>
                    <input type="button" id="closeBtn" value="关闭" onfocus="this.blur();" class="btn"/>
                </div>
            </div>
            <div class="height"></div>
        </div>
        <div class="clear"></div>

    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>


</div>
<div id="div_brand_head" class="i_scroll" style="display:none;height:380px;width:265px">
    <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:100%;padding:0;margin:0">
    </div>
</div>
</body>
</html>