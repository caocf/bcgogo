<%@ page import="com.bcgogo.product.dto.ProductAdminDTO" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    int pageNo = 1;
    int pageSize = 10;
    try {
        pageNo = Integer.parseInt(request.getSession().getAttribute("pageNo").toString());
        pageSize = Integer.parseInt(request.getSession().getAttribute("pageSize").toString());
    } catch (Exception e) {
    }

%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——商品</title>

    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
    <%@include file="/WEB-INF/views/script-common.jsp" %>
    <script type="text/javascript" src="js/searchDefault.js"></script>
</head>
<body>
<%@include file="/WEB-INF/views/header.jsp" %>
<div class="main">
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
                </div>
                <!--代理商-->
                <div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">商品</div>
                    <div class="timeRight"></div>
                </div>
                <!--代理商结束-->
                <!--table-->
                <table cellpadding="0" cellspacing="0" id="" class="clear">
                    <col width="42">
                    <col width="100">
                    <col width="100">
                    <col width="100">
                    <col width="100">
                    <col width="110">
                    <col width="95">
                    <col width="100">
                    <col width="100">
                    <thead>
                    <tr>
                        <th>NO</th>
                        <th>店铺名</th>
                        <th>品名</th>
                        <th>品牌</th>
                        <th>规格</th>
                        <th>型号</th>
                        <th>供应商</th>
                        <th>车型</th>
                        <th style=" background-image:none;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        int no = 0;
                        for (ProductAdminDTO productAdminDTO : (List<ProductAdminDTO>) request.getAttribute("productAdminDTOList")) {
                    %>
                    <tr<%=no % 2 == 0 ? "" : " class=\"agent_bg\""%>>
                        <td>
                            <%=no%>
                        </td>
                        <td>
                            <%=productAdminDTO.getShopName()%>
                        </td>
                        <td>
                            <%=productAdminDTO.getName()%>
                        </td>
                        <td>
                            <%=productAdminDTO.getBrand()%>
                        </td>
                        <td>
                            <%=productAdminDTO.getSpec()%>
                        </td>
                        <td>
                            <%=productAdminDTO.getModel()%>
                        </td>
                        <td>
                            <%=productAdminDTO.getSupplierName()%>
                        </td>
                        <td>
                            <%=productAdminDTO.getCarModelName()%>
                        </td>
                        <td class="edit_back"><a href="#" class="font">修改</a> | <a href="#" class="font">确认</a></td>
                    </tr>
                    <%
                            no++;
                        }
                    %>
                    </tbody>
                </table>
                <!--table结束-->
                <!--分页-->
                <div class="i_leftBtn">
                    <%if (pageNo > 1) {%>
                    <div class="fist_page"></div>
                    <div class="">1</div>
                    <%}%>
                    <div class="i_leftCountHover"><%=pageNo%>
                    </div>
                    <%if (no >= pageSize) {%>
                    <div class="last_page"></div>
                    <%}%>
                </div>
            </div>
            <!--分页结束-->
            <!--内容结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
</body>
</html>