<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="titBody">
  <div class="lineTop"></div>
  <div class="lineBody lineAll">
    <div class="txt_Search">
      <label>统计日期：</label>

      <label>${assistantStatSearchDTO.startYear}年</label>

      <label>${assistantStatSearchDTO.startMonth}月</label>

      <label>到</label>

      <label>${assistantStatSearchDTO.endYear}年</label>

      <label>${assistantStatSearchDTO.endMonth}月</label>

      <label style="margin-left: 30px;">统计方式：</label>

      <c:if test="${assistantStatSearchDTO.achievementCalculateWayStr =='CALCULATE_BY_ASSISTANT'}">
        按员工提成设置
      </c:if>
      <c:if test="${assistantStatSearchDTO.achievementCalculateWayStr =='CALCULATE_BY_DETAIL'}">
        按详细提成设置
      </c:if>
    </div>

    <div class="clear i_height"></div>
    <div class="txt_Search">
      <label>部门/员工：</label>
      <c:if test="${assistantStatSearchDTO.achievementStatTypeStr =='DEPARTMENT'}">
        按部门&nbsp;&nbsp;部门：
      </c:if>
      <c:if test="${assistantStatSearchDTO.achievementStatTypeStr =='ASSISTANT'}">
        按员工&nbsp;&nbsp;员工：
      </c:if>
      ${assistantStatSearchDTO.assistantOrDepartmentName}
      <label style="margin-left: 30px;">分类：</label>


      <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='ALL'}">
        全部
      </c:if>
      <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='WASH_BEAUTY'}">
        洗车美容
      </c:if>

      <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='REPAIR_SERVICE'}">
        施工
      </c:if>
      <c:if test="${assistantStatSearchDTO.achievementOrderTypeStr =='MEMBER'}">
        会员卡销售
      </c:if>

      &nbsp;&nbsp;

      <c:if test="${assistantStatSearchDTO.serviceIdStr =='NEW'}">
        购卡
      </c:if>
      <c:if test="${assistantStatSearchDTO.serviceIdStr =='RENEW'}">
        续卡
      </c:if>
      ${assistantStatSearchDTO.serviceName}


    </div>
  </div>
  <div class="lineBottom"></div>
  <div class="clear i_height"></div>
</div>
