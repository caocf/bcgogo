<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 12-1-19
  Time: 下午4:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
 <%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
    <title>后台管理系统——代理商管理</title>
    <link rel="stylesheet" type="text/css" href="styles/backstage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>

    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/registerPager<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        function regedit(state, method) {
            window.location = state + "beshop.do?method=" + method;
        }
    </script>
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
    <script type="text/javascript">
        jQuery(document).ready(function(){
            jQuery(".font").live("click",function(){
               // jQuery(this).parent().parent().css('display','none');
                jQuery.ajax({
                    type:"POST",
                    url:jQuery("#basePath").val()+"beshop.do?method=deleteBlance",
                    data:{id:jQuery("#id").val()},
                    success:function(data){
                            window.location=jQuery("#basePath").val()+"beshop.do?method=getSms";
                    }
                });

            });

        });
    </script>
</head>
<body>
<input type="hidden" value="<%=basePath%>" id="basePath"/>
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
        <div class="bodyLeft">
            <ul class="leftTitle">
                <li><a href="#" class="left_register">注册</a>
                    <input type="button" class="btnNum" value="13"/>
                </li>
                <li><a href="#" class="left_shopping">商品</a><input type="button" class="btnNum" value="10"/></li>
                <li><a href="#" class="left_vehicle">车辆</a><input type="button" class="btnNum" value="8"/></li>
                <li><a href="#" class="left_recruit">招聘</a><input type="button" class="btnNum" value="5"/></li>
                <li  class="left_hover"><a href="#" class="left_recharge">充值</a><input type="button" class="btnNum" value="28"/></li>
                <li><a href="#" class="left_agaent">代理商</a><input type="button" class="btnNum" value="25"/></li>
                <li><a href="#" class="left_manage">后台管理</a><input type="button" class="btnNum" value="28"/></li>
            </ul>
        </div>
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
                    <%--<div class="rightBody">--%>
                        <%--<div class="title">立即注册</div>--%>
                        <%--<div class="title" onclick="regedit('<%=basePath%>','shoplist2')">待注册</div>--%>
                        <%--<div class="titleHover">已注册</div>--%>
                        <%--<div class="title">短信费用</div>--%>
                    <%--</div>--%>
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
                <col width="42">
                <col width="97">
                <col width="97">
                <col width="102">
                <col width="222">
                <col width="92">
                <col width="97">
                <col width="98">
                    <thead>

                    <tr>
                        <th>NO</th>
                        <th>店铺名</th>
                        <th>店主</th>
                        <th>联系方式</th>
                        <th>地址</th>
                        <th>短信余额</th>
                        <th>充值金额</th>
                        <th style=" background-image:none;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${smsAndShopDTOList}" var="sms" varStatus="status">
                        <c:choose>
                            <c:when test="${status.index%2==1}">
                                <tr class="agent_bg">
                            </c:when>
                            <c:otherwise>
                                <tr>
                            </c:otherwise>
                        </c:choose>
                        <td>${status.index+1}</td>
                        <td title="${sms.name}">${sms.name}</td>
                        <td title="${sms.legalRep}">${sms.legalRep}</td>
                        <td title="${sms.mobile}">${sms.mobile}</td>
                        <td title="${sms.address}">${sms.address}</td>
                        <td title="${sms.smsBalance}">${sms.smsBalance}</td>
                        <td title="${sms.rechargeAmount}">${sms.rechargeAmount}</td>
                        <td><a class="font" href="#">确认</a></td>
                        <%--<%=basePath%>beshop.do?method=deleteBlance&id=${sms.id}--%>
                              <input type="hidden" id="id" value="${sms.id}" />
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
                    <div class="fist_page" onclick="prePage('getSms','<%=pageNo%>')"></div>
                    <%

                        if (pageCount != null && pageNo != null) {
                            for (int m = 1; m <= pageCount; m++) {
                                if (m == pageNo) {
                    %>
                    <div class="i_leftCountHover"><%=m%>
                    </div>
                    <%} else {%>
                    <div class="" onclick="page('getSms','<%=m%>')"><%=m%>
                    </div>
                    <%
                                }
                            }
                        }
                    %>
                    <div class="last_page" onclick="nextPage('getSms','<%=pageNo%>','<%=pageCount%>')"></div>
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