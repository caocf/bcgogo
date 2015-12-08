<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 14-12-4
  Time: 下午4:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="addWXAccountDiv" class="rightMain add-wx-account-div" style="display: none;">
    <div>
        <div class="t-t-title">公共号信息</div>
        <table cellspacing="0" cellpadding="0" id="addWXAccountTable" style="width: 500px;">
            <input type="hidden" id="accountId">
            <colgroup>
                <col width="150">
                <col width="200">
            </colgroup>
            <tbody>
            <tr>
                <td class="label">公共号</td>
                <td><input id="public_name" type="text" value="" class="txt" name="name"  autocomplete="off">
                </td>
            </tr>
            <tr>
                <td class="label">PUBLIC_NO</td>
                <td><input id="public_no" type="text" value="" class="txt" autocomplete="off">
                </td>
            </tr>
            <tr>
                <td class="label">APP_ID</td>
                <td><input id="app_id" type="text" value="" class="txt" autocomplete="off">
                </td>
            </tr>
            <tr>
                <td class="label">SECRET</td>
                <td><input id="secret" type="text" value="" class="txt" autocomplete="off">
                </td>
            </tr>
            <tr>
                <td class="label">TOKEN</td>
                <td><input id="token" type="text" value="" class="txt" autocomplete="off">
                </td>
            </tr>
            <tr>
                <td class="label">ENCODING_KEY</td>
                <td><input id="encodingKey" type="text" value="" class="txt" autocomplete="off">
                </td>
            </tr>
            <tr>
                <td class="label">备注</td>
                <td><input id="remark" type="text" value="" class="txt" autocomplete="off">
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div style="margin-top: 20px;" class="add-shop-info">
        <div class="t-t-title"><span>关联店铺信息</span></div>
        <div><span id="addShopBtn" class="add-btn">增加</span></div>
        <table cellspacing="0" cellpadding="0" id="addWXShopAccountTable" style="width: 500px;">
            <colgroup>
                <col width="150">
                <col width="100">
            </colgroup>
            <tbody>
            <tr class="dm_table_title">
                <td class="label">店铺名</td>
                <td>操作</td>
            </tr>
            </tbody>
        </table>
    </div>

</div>

<div id="div_shopName" class="i_scroll" style="display:none;width:250px;z-index: 1003;">
    <div class="Scroller-ContainerShopName" id="Scroller-Container_shopName">
    </div>
</div>