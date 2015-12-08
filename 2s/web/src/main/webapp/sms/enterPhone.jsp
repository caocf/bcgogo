<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: zhangjuntao
  Date: 12-5-3
  Time: 下午3:52
  To change this template use File | Settings | File Templates.
--%>
<style>
 .tab_repay .i_upBody .sure{
	 float:right;
}
 .tab_repay .i_upBody .sure input{
	background:url(images/washBtn1.jpg) no-repeat;
	width:75px;
	height:27px;
	text-align:center;
	line-height:27px;
	border:none;
	margin-top:10px;
	color:#6699cc;
	cursor:pointer;
}

.tab_repay .i_upBody .sure input:hover {
  background:url(images/washBtn2.jpg) no-repeat;
  color:#663A02;
}
</style>
<link rel="stylesheet" type="text/css" href="styles/cleanCar<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" src="js/enterPhone<%=ConfigController.getBuildVersion()%>.js"></script>
<div class="clear tab_repay" id="enterPhoneSetLocation"
     style="position:fixed; top:35%; margin-left:40%;display:none;z-index:10008;">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">请输入手机号</div>
        <div class="i_close" id="divEnterPhoneClose"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="tab_repayTime number_xiche" style="color:#000000;">
            请输入手机号：
            <input type="text" id="enterPhoneMobile" maxlength="11"/>
        </div>
        <div class="sure">
            <input type="hidden" id="enterPhoneCustomerId"/>
            <input type="hidden" id="enterPhoneScene"/>
            <input type="hidden" id="enterPhoneSupplierId"/>
            <input type="button" value="确认" id="submitEnterPhoneBtn" onclick="submitEnterPhoneBtn();"
                   onfocus="this.blur();"/>
            <input type="button" value="取消" id="cancelEnterPhoneBtn" onfocus="this.blur();"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
