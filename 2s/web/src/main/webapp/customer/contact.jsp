<%--
  Created by IntelliJ IDEA.
  User: zhuj
  Date: 13-6-4
  Time: 下午5:57
  联系人信息相关
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="right_customer">
    	<label class="lblTitle">联系人信息<span class="icon_connacter">为主联系人</span></label>
        <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact">
        <col width="50">
        <col width="80">
        <col>
        <col width="90">
        <col width="32">
        <tr>
            <td>姓名</td>
            <td>手机</td>
            <td>Email</td>
            <td>QQ</td>
            <td></td>
        </tr>
        <tr>
            <td><input type="text" class="txt" /></td>
            <td><input type="text" class="txt" style="width:73px;" /></td>
            <td><input type="text" class="txt" style="width:120px;" /></td>
            <td><input type="text" class="txt" /></td>
            <td><a class="icon_connacter"></a></td>
        </tr>
        <tr>
            <td><input type="text" class="txt" /></td>
            <td><input type="text" class="txt" style="width:73px;" /></td>
            <td><input type="text" class="txt" style="width:120px;" /></td>
            <td><input type="text" class="txt" /></td>
            <td>
                <a class="icon_grayconnacter hover"></a>
                <div class="alert">
                    <span class="arrowTop"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">点击设为主联系人</div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td><input type="text" class="txt" /></td>
            <td><input type="text" class="txt" style="width:73px;" /></td>
            <td><input type="text" class="txt" style="width:120px;" /></td>
            <td><input type="text" class="txt" /></td>
            <td><a class="icon_grayconnacter"></a><a class="close"></a></td>
        </tr>
        </table>
    </div>