<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="div_show" style="height: 200px;">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="config_title" id="div_drag">短信编辑重新发送</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody" style="height: 200px;">
        <form name="sendFailedMessage" id="sendFailedMessage" action="sms.do?method=sendSmsJob" method="post">
            <table cellpadding="0" id="editSms_table" cellspacing="0" >
                <col width="100">
                <col width="100"/>
                <tr>
                    <td class="label">手机号码</td>
                    <td>
                        <input type="hidden" id="id" name="id" value=""/>
                        <input type="text" id="receiveMobile" name="receiveMobile" value="" class="mobile"/></td>
                </tr>
                <tr>
                    <td class="label">短信内容</td>
                    <td><textarea id="smsContent" name="smsContent" value="" class="text_area" wrap="physical" cols="30" rows="6"></textarea></td>
                </tr>
            </table>
            <div class="more_his" style="text-align: center;">
                <input type="button" value="发送" onfocus="this.blur();" class="btn" id="confirmBtn"/>
                <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
            </div>
        </form>
    </div>
</div>

