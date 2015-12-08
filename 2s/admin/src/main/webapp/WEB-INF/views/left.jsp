<%--
  .left_hover {
    background: url("../images/left_hover.jpg") repeat-x scroll 0 0 transparent;
    height: 39px;
}
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="bodyLeft">
    <ul class="leftTitle">
        <li><a href="beshop.do?method=shoplist" class="left_register">注册</a><input type="button" class="btnNum" value="${shopCounts}"/></li>
        <li><a href="shopInfoHistory.do?method=shopInfo" class="left_shopping">商品</a><input type="button" class="btnNum" value="10"/>
        </li>
        <li><a href="#" class="left_vehicle">车辆</a><input type="button" class="btnNum" value="8"/></li>
        <li><a href="#" class="left_recruit">招聘</a><input type="button" class="btnNum" value="5"/></li>
        <li><a href="beshop.do?method=getSms" class="left_recharge">充值</a><input type="button" class="btnNum" value="28"/></li>
        <li><a href="#" class="left_agaent">代理商</a><input type="button" class="btnNum" value="25"/></li>
        <li><a href="#" class="left_manage">后台管理</a><input type="button" class="btnNum" value="28"/></li>
        <li><a href="dataMaintenance.do?method=createDM" class="left_datamaintain">数据维护</a><input type="button" class="btnNum" value="5"/></li>
        <li><a href="shopConfig.do?method=shopIndividuation" class="left_shopConfig">店铺设置</a><input type="button" class="btnNum" value="0"/></li>
        <li><a href="print.do?method=toLeadPage" class="left_print">打印模板</a><input type="button" class="btnNum" value="0"/></li>
        <li><a href="sms.do?method=initFailedSmsPage" class="left_datamaintain">短信管理</a><input type="button" class="btnNum" value="5"/></li>
        <li><a href="weChat.do?method=toAdultList" class="left_datamaintain">微信管理</a><input type="button" class="btnNum" value="5"/></li>
         <li><a href="order.do?method=toOrder" class="left_datamaintain">订单管理</a><input type="button" class="btnNum" value="8"/></li>
         <li><a href="mirrorTask.do?method=toAddTask" class="left_datamaintain">任务管理</a></li>        <li><a href="fourSVehicle.do?method=initFourSVehiclePage" class="left_datamaintain">内部车辆管理</a></li>
        <li><a href="camera.do?method=initCameraList" class="left_datamaintain">摄像头管理</a></li>
    </ul>
</div>