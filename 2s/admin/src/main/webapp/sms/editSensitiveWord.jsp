<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="div_show" style="height: 200px;">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="config_title" id="div_drag">敏感词修改</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody" style="height: 200px;">
        <form id="editSensitiveWord" action="sms.do?method=updateSensitiveWord" method="post">
            <table cellpadding="0" id="sensitiveWords_table" cellspacing="0"  style="z-index: 12;">
                <col width="100">
                <col width="100"/>
                <tr>
                    <td class="label">原敏感</td>
                    <td><input type="text" name="oldWord" id="oldWord" onfocus="this.blur()" style="color: #666666" readonly="true" class="mobile"/></td>
                </tr>
                <tr>
                    <td class="label">新敏感词</td>
                    <td><input type="text" name="newWord" id="newWord" value="" class="text_area"/></td>
                </tr>
            </table>
            <div class="more_his" style="text-align: center;">
                <input type="button" value="修改" onfocus="this.blur();" class="btn" id="confirmBtn"/>
                <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
            </div>
        </form>
    </div>
</div>

