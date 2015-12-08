<%@ page language="java" pageEncoding="UTF-8" %>
<label>刷新本地缓存，选择命令：</label>
<select id="freshResources">
    <optgroup label="刷新资源">
        <option value="freshAllResources">刷新All资源</option>
        <option value="freshUserGroupResources">刷新用户组资源</option>
        <option value="freshShopResources">刷新店面版本资源</option>
    </optgroup>
</select>
<input type="button" onclick="permissionFreshResources($('#freshResources').val())" value="确认" id="freshButton" style="margin-left: 10%"/>
<br>
<br>
<h3 style="color: red">操作完权限 请刷新资源</h3>
<br>
<br>
<input type="button" value="shop版本操作" onclick="{location.href='permission.do?method=showAllShopVersion'}"/>

<input type="button" value="用户组操作" onclick="{location.href='permission.do?method=showAllUserGroup'}"/>

<input type="button" value="角色操作" onclick="{location.href='permission.do?method=showRole'}"/>

<input type="button" value="资源操作" onclick="{location.href='permission.do?method=showResource'}"/>

