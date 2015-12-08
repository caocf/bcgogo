<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 14-4-2
  Time: 上午11:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="prompt_box" style="width:430px;display: none" id="sendMsgPrompt">
    <div class="prompt_title">
        <div class="turn_off J_closeSendMsgPrompt"></div>
        发送保养信息</div>
    <div class="content">
        <form action="customer.do?method=sendVehicleMsg" id="sendMsgPromptForm" method="post">
            <input type="hidden" name="type" value="7" autocomplete="off">
            <input type="hidden" name="remindEventId" value="" autocomplete="off">
            <input type="hidden" name="licenceNo" value="" autocomplete="off">
            <input type="hidden" name="year"  value="" autocomplete="off">
            <input type="hidden" name="month"  value="" autocomplete="off">
            <input type="hidden" name="day"  value="" autocomplete="off">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>发送至：</td>
                    <td>
                        <input class="prompt_input txt" id="mobile" name="mobile" maxlength="11" autocomplete="off" >
                    </td>
                    <td style="text-align:left; line-height:15px;width: 100px">
                        <div style="float:left;display: none" id="mobileWrongInfo"><a class="prompt_right"></a><span class="red_color">格式不正确！</span></div>
                        <div style="float:left;display: none" id="mobileEmptyInfo"><span style="color:#aaa">请输入手机号！</span></div>
                        <div style="float:left;display: none" id="mobileRightInfo"><a class="prompt_right"></a></div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" height="5" style="line-height:normal">&nbsp;</td>
                </tr>
                <tr>
                    <td valign="top">发送内容：</td>
                    <td>
                        <div class="prompt_textarea" style="width:220px;height: 110px;" id="vehicleMsgContent"></div>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        <div class="fl">
                            <label><input name="smsFlag" type="checkbox" value="true" disabled="disabled" checked="checked" autocomplete="off" />发送短信</label>
                        </div>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2">
                        <div class="fl">
                            <label><input name="appFlag" type="checkbox" value="true" autocomplete="off" />发送手机客户端信息</label>
                        </div>
                        <span class="gray_color">(仅对已装手机客户端的手机号有效)</span>
                    </td>
                </tr>
            </table>
            <div class="clear"></div>
            <div class="wid275">
                <div class="addressList"> <a id="sendMsgPromptBtn">发 送</a> <a class="J_closeSendMsgPrompt">取 消</a></div>
            </div>
        </form>
        <div class="clear"></div>
    </div>
</div>