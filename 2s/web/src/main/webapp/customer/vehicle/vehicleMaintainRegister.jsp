<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 14-5-6
  Time: 下午3:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="prompt_box" id="maintainRegisterDiv" style="width:500px;display: none;">
  <%--<div class="title">--%>
    <%--<div class="turn_off"></div>--%>
    <%--保养登记--%>
  <%--</div>--%>
  <div class="content_t">

    <form action="vehicleManage.do?method=vehicleMaintainRegister" id="maintainRegisterForm" method="post">

      <input type="hidden" name="vehicleId" value="${customerVehicleResponse.vehicleId}"/>
      <input type="hidden" name="customerId" value="${customerVehicleResponse.customerId}"/>

      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="order-table">
        <tr>
          <td>本次保养里程 ：
            <input id="lastMaintainMileage" style="width:70px;" name="lastMaintainMileage" type="text" class="txt_shorter"/>
            公里
          </td>
          <td>保养里程周期 ：
            <input id="registerMaintainMileagePeriod" name="maintainMileagePeriod" type="text" class="txt_shorter"/>
            公里
          </td>
        </tr>
        <tr>
          <td>本次保养日期 ：
            <input id="registerMaintainTime" value="${today}" name="lastMaintainTimeStr" onclick="showDatePicker(this);" readonly="readonly" type="text" class="txt_short"/></td>
          <td>保养时间周期 ：
            <input id="registerMaintainTimePeriod" name="maintainTimePeriodStr" type="text" class="txt_shorter"/>月

          </td>
        </tr>
      </table>
      <div class="clear"></div>
      <div class="wid275" style="width:175px;">
        <div class="addressList">
          <div class="search_btn" data-index="" id="saveMaintainRegister">确 定</div>
          <div class="empty_btn" data-index="" id="cancelMaintainRegister">取 消</div>
        </div>
      </div>
      <div class="clear"></div>
    </form>
  </div>
</div>