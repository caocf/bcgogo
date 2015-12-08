<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>后台管理系统——代理商管理</title>

    <%-- styles --%>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <style type="text/css">
         .clear{
             table-layout:fixed;
             /*width:848px;*/
         }
        .clear td {
            white-space:nowrap;
            overflow: hidden;
            text-overflow: ellipsis;

        }
    </style>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/registerPager.js"></script>
   <script type="text/javascript" src="js/searchDefault.js"></script>
    <script type="text/javascript">
        function regedit(state, method) {
            window.location = state + "beshop.do?method=" + method;
        }
    </script>
</head>
<body>
<div class="main">
    <!--头部-->
    <div class="top">
        <div class="top_left">
            <div class="top_name">统购后台管理系统</div>
            <div class="top_image"></div>
            你好，<span>张三</span>|<a href="#">退出</a></div>
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
                        <div class="title" onclick="regedit('<%=basePath%>','shoplist')">立即注册</div>
                        <div class="title" onclick="regedit('<%=basePath%>','shoplist2')">待注册</div>
                        <div class="title" onclick="regedit('<%=basePath%>','shoplist1')">已注册</div>
        <div class="title" onclick="regedit('<%=basePath%>','smsRecharge')">短信费用</div>
                    </div>
                </div>
                <!--代理商-->
                <div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">店铺</div>
                    <div class="timeRight"></div>
                </div>
                <!--代理商结束-->
                <!--table-->
                <table cellpadding="0" cellspacing="0" id="" class="clear" width="848px">
                    <col width="44">
                    <col width="79">
                    <col width="59">
                    <col width="89">
                    <col width="94">
                    <col width="79">
                    <col width="70">
                    <col width="90">
                    <col width="74">
                    <col width="90">
                    <col width="80">
                    <thead>

                    <tr>
                        <th>NO</th>
                        <th>店铺名</th>
                        <th>店主</th>
                        <th>联系方式</th>
                        <th>地址</th>
                        <th>成立时间</th>
                        <th>员工数</th>
                        <th>业务ID号</th>
                        <th>业务员</th>
                        <th>软件价格</th>
                        <th style=" background-image:none;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        List shoplist = (List) request.getAttribute("shoplist");
                    %>
                    <c:forEach items="${shoplist}" var="shop" varStatus="status">
                        <c:choose>
                            <c:when test="${status.index%2==1}">
                                <tr class="agent_bg">
                            </c:when>
                            <c:otherwise>
                                <tr>
                            </c:otherwise>
                        </c:choose>
                        <td>${status.index+1}</td>
                        <td title="${shop.name}">${shop.name}</td>
                        <td title="${shop.legalRep}">${shop.legalRep}</td>
                        <td title="${shop.mobile}">${shop.mobile}</td>
                        <td title="${shop.address}">${shop.address}</td>
                        <td title="${shop.established}">${shop.established}</td>
                        <td title="${shop.personnel}">${shop.personnel}</td>
                        <td title="${shop.agentId}">${shop.agentId}</td>
                        <td title="${shop.agent}">${shop.agent}</td>
                        <td title="${shop.softPrice}">${shop.softPrice}</td>
                        <td><a href="shopInfoHistory.do?method=shopInfo&&shopId=${shop.id}" class="font">点击详情</a></td>

                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <!--table结束-->
                <!--分页-->
                <div class="i_leftBtn">
                    <%--  <div class="fist_page"></div>
                <div class="">1</div>
                <div class="i_leftCountHover">2</div>
                <div class="">3</div>
                <div class="">4</div>
                <div class="">5</div>
                <div class="last_page"></div>   --%>
                    <%
                        Integer pageCount = (Integer) request.getAttribute("pageCount");
                        Integer pageNo = (Integer) request.getAttribute("pageNo");
                    %>
                    <div class="fist_page" onclick="prePage('shoplist1','<%=pageNo%>')"></div>
                    <%

                        if (pageCount != null && pageNo != null) {
                            for (int m = 1; m <= pageCount; m++) {
                                if (m == pageNo) {
                    %>
                    <div class="i_leftCountHover"><%=m%>
                    </div>
                    <%} else {%>
                    <div class="" onclick="page('shoplist1','<%=m%>')"><%=m%>
                    </div>
                    <%
                                }
                            }
                        }
                    %>
                    <div class="last_page" onclick="nextPage('shoplist1','<%=pageNo%>','<%=pageCount%>')"></div>
                </div>
            </div>
            <!--分页结束-->
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
</body>

</html>