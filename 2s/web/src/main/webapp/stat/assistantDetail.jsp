<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bcgogo.enums.OrderTypes" %>
<%@ page import="com.bcgogo.search.dto.OrderIndexDTO" %>
<%--
  Created by IntelliJ IDEA.
  User: liuwei
  Date: 11-12-15
  Time: 下午3:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compativle" content="IE-EmulateIE7"/>
    <title>员工业绩明细表</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/uncleUser<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        function prePage(nowPage, pageCount, assistantName, memberIncome, washing, carRepair, sales, total) {
            if (nowPage != null) {
                if (nowPage * 1 == 1) {
                    alert("已经是第一页!");
                    return;
                }
                var year = document.getElementById("queryYear").value;
                var startMonth = document.getElementById("startMonth").value;
                var endMonth = document.getElementById("endMonth").value;
                //window.location = "customer.do?method=customerdata&pageNo=" + (nowPage * 1 - 1);
                window.location = 'bizstat.do?method=getAssistantDetail&pageNo=' + (nowPage * 1 - 1) + '&memberIncome=' + memberIncome + '&washing=' + washing + '&carRepair=' + carRepair + '&sales=' + sales + '&total=' + total
                        + '&assistantName=' + assistantName + '&year=' + year + '&startMonth=' + startMonth + '&endMonth=' + endMonth;
                    }
        }
        function nextPage(nowPage, pageCount, assistantName, memberIncome, washing, carRepair, sales, total) {
            if (nowPage * 1 == pageCount * 1) {
                        alert("已经是最后一页!");
                return;
                    }
            var year = document.getElementById("queryYear").value;
            var startMonth = document.getElementById("startMonth").value;
            var endMonth = document.getElementById("endMonth").value;
            window.location = 'bizstat.do?method=getAssistantDetail&pageNo=' + (nowPage * 1 + 1) + '&memberIncome=' + memberIncome + '&washing=' + washing + '&carRepair=' + carRepair + '&sales=' + sales + '&total=' + total
                    + '&assistantName=' + assistantName + '&year=' + year + '&startMonth=' + startMonth + '&endMonth=' + endMonth;
                }
        function detail(url) {
            window.open(url);
        }
        $(function() {
            tableUtil.tableStyle('#history', '.title_his');
        });
    </script>
</head>
<body class="bodyMain">

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" value="${queryYear}" name="queryYear" id="queryYear"/>
<input type="hidden" value="${startMonth}" name="startMonth" id="startMonth"/>
<input type="hidden" value="${endMonth}" name="endMonth" id="endMonth"/>
<input type="hidden" value="${isSearch}" name="isSearch" id="isSearch"/>

<div class="title">
</div>
<div class="i_main clear">

    <div class="mainTitles clear">
        <div class="titleWords">
            超级管理
        </div>
        <!--导航-->
        <div class="i_mainTitle stock_search">
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BASE">
                <a id="carWash" href="businessStat.do?method=getBusinessStat">营业统计</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE">
                <a id="businessAccount" href="businessAccount.do?method=initBusinessAccountSearch">营业外记账</a>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.STAT.RECEIVABLE_PAYABLE_STAT.BASE">
                <a id="recOrPayStat" href="arrears.do?method=toPayableStat">应付应收统计</a>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.BASE">
                <a id="costStat" href="costStat.do?method=getCostStat">采购分析</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.AGENT_ACHIEVEMENTS.BASE">
                <a id="carWash2" href="bizstat.do?method=agentAchievements&month=thisMonth" class="title_hover">员工业绩统计</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.GOOD_BAD_STAT.BASE">
                <a id="goodBadSaleStat" href="salesStat.do?method=getGoodSaleCost" class="returnLast">畅销/滞销品统计</a>
            </bcgogo:hasPermission>
        </div>
    </div>
    <div class="mainTitle">员工业绩明细</div>


    <div style="float:left; width:100%; color:#000000;">
        <div style="float:left; width:80px;">
            共有:<span><%=(request.getAttribute("orderIndexListSize") == null ? "0" : request.getAttribute("orderIndexListSize"))%></span>条
        </div>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
            <div style="float:left; width:120px;">
                会员共计:<span><%=(request.getAttribute("memberIncome") == null ? "0" : request.getAttribute(
                    "memberIncome"))%></span>元
            </div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <div style="float:left; width:160px;">
                洗车共计:<span><%=(request.getAttribute("washing") == null ? "0" : request.getAttribute("washing"))%></span>元
            </div>
            <div style="float:left; width:150px;">
                服务共计:<span><%=(request.getAttribute("carRepair") == null ? "0" : request.getAttribute("carRepair"))%></span>元
            </div>
        </bcgogo:hasPermission>
        <div style="float:left; width:160px;">
            销售:<span><%=(request.getAttribute("sales") == null ? "0" : request.getAttribute("sales"))%></span>元
        </div>
        <div style="float:left; width:160px;">
            共计:<span><%=(request.getAttribute("total") == null ? "0" : request.getAttribute("total"))%></span>元
        </div>
        <div style="float:right;">查询时间:<span>${queryYear}年${startMonth}月至${endMonth}月</span></div>
    </div>

        <table cellpadding="0" cellspacing="0" class="table2" id="history">
          <col width="27px"/>
          <col width="50px"/>
          <col width="55px"/>
          <col width="100px"/>
          <col width="100px"/>
          <col width="100px"/>
          <col width="42px"/>
          <col width="42px"/>
          <col width="42px"/>
          <col width="60px"/>
          <col width="40px"/>
          <tr class="title_his">
            <td>NO</td>
            <td id="timeSort">
              <div style="float:left">店员</div>
            </td>
            <td id="brandSort">
              <div style="float:left">单据类型</div>
            </td>
              <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                  <td>洗车</td>
                  <td>服务</td>
              </bcgogo:hasPermission>
            <td>销售</td>
            <td>单据总额</td>
            <td>单据状态</td>

            <td>欠款</td>
            <td>预计还款时间</td>
            <td style="border-right:none;color:black;">操作</td>
          </tr>

          <tr id="highLightRow" style="display:none;">
          </tr>
          <%
            int pageStart = 0;
            Integer p = (Integer) request.getAttribute("pageNo");
            if (p != null) {
              pageStart = (p - 1) * 15;
            }
          %>
          <%
            List<OrderIndexDTO> orderIndexDTOList = (List<OrderIndexDTO>) request.getAttribute("orderIndexDTOList");
            if (orderIndexDTOList == null || orderIndexDTOList.size() <= 0) {
          %>
        </table>
        <%
        } else {
          OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
          for (int i = 0; i < orderIndexDTOList.size(); i++) {
            orderIndexDTO = orderIndexDTOList.get(i);
        %>
    <tr <%=(i == 0 ? "id='firstRow'" : "")%> class="table-row-original">
        <td style="border-left:none;"><%=(i + 1)%>
          </td>
        <td title="<%=orderIndexDTO.getServiceWorker()%>"><%=orderIndexDTO.getServiceWorkerStr()%>
          </td>
        <td><%=(orderIndexDTO.getOrderType() == null ? "" : orderIndexDTO.getOrderType().getName())%>
          </td>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
        <td title="<%=orderIndexDTO.getWashingStr()%>"><%=orderIndexDTO.getWashing()%>
          </td>
        <td title="<%=orderIndexDTO.getServiceStr()%>"><%=orderIndexDTO.getService()%>
          </td>
        </bcgogo:hasPermission>
        <td title="<%=orderIndexDTO.getSalesStr()%>"><%=orderIndexDTO.getSales()%>
          </td>
        <td><%=(orderIndexDTO.getOrderTotalAmount() == null ? "" : orderIndexDTO.getOrderTotalAmount())%>
          </td>
        <td><%=(orderIndexDTO.getArrears() == null || orderIndexDTO.getArrears() == 0 ? "已结算" : "欠款结算")%>
          </td>
        <td><%=(orderIndexDTO.getArrears() == null ? "" : orderIndexDTO.getArrears())%>
          </td>
        <td><%=(orderIndexDTO.getPaymentTimeStr() == null ? "" : orderIndexDTO.getPaymentTimeStr())%>
            <%
              if (orderIndexDTO.getOrderType() != null) {
                if (orderIndexDTO.getOrderType() != OrderTypes.WASH && orderIndexDTO.getOrderType() != OrderTypes.MEMBER_BUY_CARD) {
            %>
          </td>
          <td style="border-right:none;"><a href="#" onclick="detail('<%=orderIndexDTO.getUrl()%>')">点击详情</a>
          </td>
          <%
        }
        else {
          %>
          <td style="border-right:none;">
          </td>
          <%
              }
            }
          %>
        </tr>
        <%
            }
          }
        %>
        </table>



    <div class="i_leftBtn">
        <%
            Integer pageCount = (Integer) request.getAttribute("pageCount");
            Integer pageNo = (Integer) request.getAttribute("pageNo");
            if (pageNo == null) {
                pageNo = 1;
            }
            if (pageNo > 1) {
        %>
        <div class="lastPage"
             onclick="prePage('<%=(request.getAttribute("pageNo"))%>','<%=(request.getAttribute("pageCount"))%>','<%=(request.getAttribute("assistantName"))%>','<%=(request.getAttribute("memberIncome"))%>','<%=(request.getAttribute("washing"))%>','<%=(request.getAttribute("carRepair"))%>','<%=(request.getAttribute("sales"))%>','<%=(request.getAttribute("total"))%>')">
            上一页
        </div>
        <%
            }
            if (pageCount != null && pageNo != null) {
                for (int m = 1; m <= pageCount; m++) {
                    if (m == pageNo) {
        %>
        <div class="i_leftCountHover"><%=(m)%>
        </div>
        <%
                    }
                }
            }
            if (pageNo < pageCount) {
        %>
        <div class="nextPage"
             onclick="nextPage('<%=(request.getAttribute("pageNo"))%>','<%=(request.getAttribute("pageCount"))%>','<%=(request.getAttribute("assistantName"))%>','<%=(request.getAttribute("memberIncome"))%>','<%=(request.getAttribute("washing"))%>','<%=(request.getAttribute("carRepair"))%>','<%=(request.getAttribute("sales"))%>','<%=(request.getAttribute("total"))%>')">
            下一页
        </div>
        <%}%>
    </div>
    <div class="clear"></div>
    <div class="his_bottom">
        <div class="clear"></div>
    </div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
