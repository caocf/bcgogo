
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <title>系统管理——数据导入——自定义导入</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/importexcel<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/ajaxfileupload.css"/>
    <link rel="stylesheet" type="text/css" href="styles/customImort<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/ajaxfileupload.js"></script>
    <script type="text/javascript" src="js/importexcel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
        APP_BCGOGO.Permission.Version.MemberStoredValue=${WEB_VERSION_MEMBER_STORED_VALUE};
        </bcgogo:permissionParam>
        defaultStorage.setItem(storageKey.MenuUid,"WEB.SYSTEM_SETTINGS.CUSTOM_IMPORT");
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="importToDefault" name="importToDefault"/>
<div class="i_main clear">
    <jsp:include page="/admin/systemManagerNavi.jsp">
        <jsp:param name="currPage" value="dataImportNaviMenu"/>
    </jsp:include>
<%--    <div class="i_mainRight" id="i_mainRight">
        <div class="height"></div>
    </div>--%>
<%--    <div class="mainTitles">
        <div class="titleWords">自定义导入</div>
    </div>--%>
    <bcgogo:permissionParam permissions="WEB.SYSTEM_SETTINGS.IMPORT_TEMPLATE,WEB.SYSTEM_SETTINGS.CUSTOM_IMPORT" permissionKey="importMenu">
        <c:if test="${importMenuPermissionCounts>1}">
            <div class="titleList">
                <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.IMPORT_TEMPLATE">
                    <a class=""  action-type="menu-click" menu-name="WEB.SYSTEM_SETTINGS.IMPORT_TEMPLATE"  href="import.do?method=openSimpleImportPage">模板导入</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.CUSTOM_IMPORT">
                    <a class="click"  action-type="menu-click"
                       menu-name="WEB.SYSTEM_SETTINGS.CUSTOM_IMPORT"  href="import.do?method=openImportPage">自定义导入</a>
                </bcgogo:hasPermission>
            </div>
        </c:if>
    </bcgogo:permissionParam>
    </div>
    <div class="titBody">
        <div class="leftStep">
            <div class="step">
                <div class="stepLeft"></div>
                <div class="stepBody">
                    <span>第一步：编辑导入文件</span>
                    <a class="stepImg"></a>
                    <span>第二步：选择数据导入类型</span>
                    <a class="stepImg"></a>
                    <span>第三步：上传文件</span>
                    <a class="stepImg"></a>
                    <span>第四步：匹配文件表头内容</span>
                </div>

                <div class="stepRight"></div>
            </div>
            <div class="clear i_height"></div>
            <div class="stepType">
                <div class="bgTop"></div>
                <div class="bgBody">
                    <div style="float:left; width:100px; text-align:right;">导入数据类型：</div>
                    <label class="position"><input type="radio" name="importType" style="margin-right: 5px;vertical-align: middle; margin-top: -3px;"value="CUSTOMER" checked="checked"><label>客户数据</label> </label>
                    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE" resourceType="menu">
                    <label class="position"><input type="radio" style="margin-left: 10px;margin-right: 5px;vertical-align: middle; margin-top: -3px;" name="importType" value="SUPPLIER"><label>供应商数据</label> </label>
                    </bcgogo:hasPermission>
                    <label class="position"><input type="radio" style="margin-left: 10px;margin-right: 5px;vertical-align: middle; margin-top: -3px;" name="importType" value="INVENTORY"><label>库存数据</label> </label>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                        <label class="position"><input type="radio" style="margin-left: 10px;margin-right: 5px;vertical-align: middle; margin-top: -3px;" name="importType" value="MEMBER_SERVICE"><label>会员服务数据</label></label>
                    </bcgogo:hasPermission>
                    <label class="position"><input type="radio" style="margin-left: 10px;margin-right: 5px;vertical-align: middle; margin-top: -3px;" name="importType" value="ORDER"><label>单据数据</label></label>
                    <div class="height"></div>
                    <div style="float:left; width:100px; line-height:22px; text-align:right;">上传Excel文件：</div>
                    <input type="text" id="input1" style="width:250px;" readonly="true" onclick="//selectfile.click();">
                    <a class="selBtn" id="selectfileBtn" onclick="//selectfile.click();">选择文件</a>
                    <input type="file" id="selectfile" name="selectfile" onchange="input1.value=this.value" style="display:none">
                    <%--<input type="file" id="selectfile" name="selectfile" >--%>
                    <div class="i_height"></div>

                    <img id="uploading" src="images/loadinglit.gif" alt="正在上传" title="正在上传" style="display:none;margin-left: 100px;">
                    <input type="hidden" id="importRecordId" value=""/>
                    <a class="selUp" id="selUp">上&nbsp;传</a>
                    <div class="height"></div>
                    <div style="float:left; width:100px; text-align:right;">内容匹配：</div>
                    <label style="float:left; width:80%;">（请点击需要匹配的文件内容）</label>
                    <div class="i_height clear"></div>
                    <input type="hidden" id="selectedField" value=""/>
                    <input type="hidden" id="fieldMapping" value=""/>

                    <div class="listBox" style="margin-left:100px;">
                        <h4>系统标准文件内容</h4>

                        <div id="systemFieldList" style="height:200px;width:220px;overflow-y:scroll;overflow-x: hidden;">

                        </div>
                    </div>
                    <div class="listBox" style="margin-left:50px;">
                        <h4>待匹配文件内容</h4>

                        <div id="uploadFieldList" style="height:200px;width:220px;overflow-y:scroll;overflow-x: hidden;">

                        </div>
                    </div>
                    <div class="height"></div>
                    <div class="button"><a style="margin-left:100px;" id="submit_import">确&nbsp;认</a><a id="cancel_import">取&nbsp;消</a>
                        <img id="importing" src="images/loadinglit.gif" alt="正在导入" title="正在导入"
                             style="display:none;margin-left:100px"/>
                    </div>
                </div>
                <div class="bgBottom"></div>
            </div>
        </div>
        <div class="tips">
            <div class="tips_top"></div>
            <div class="tips_body">
                <h4 class="yellow_color" style="float:left;">友情提示</h4>
                <a class="blue_color help" onclick="toImportSelfDefineExcel('importDataHelper')">导入数据帮助</a>

                <div class="clear i_height"></div>
                <ol style="list-style: decimal inside none;">
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <li class="lines" style="color:red">只有新店铺才能导入会员，先导客户信息(会员基本信息在客户信息中一起导入)，再导会员服务</li>
                </bcgogo:hasPermission>
                <li class="lines">自定义导入无需使用标准模板，自有格式的文件上传后需将表头内容与系统相应内容进行匹配</li>
                <li class="lines">文件中多个手机号请用“/”分隔</li>
                <li class="lines">文件内容需为文本格式</li>
                <li class="lines">如遇错误，请尝试关闭Excel程序（如有打开），刷新页面，重新选择文件上传。</li>
                </ol>
            </div>

            <div class="tips_bottom"></div>
        </div>

    </div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
