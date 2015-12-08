
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <title>模板导入</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/importexcel<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/ajaxfileupload.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/ajaxfileupload.js"></script>
    <script type="text/javascript" src="js/importexcel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
        APP_BCGOGO.Permission.Version.MemberStoredValue=${WEB_VERSION_MEMBER_STORED_VALUE};
        </bcgogo:permissionParam>
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main">
    <jsp:include page="/admin/systemManagerNavi.jsp">
        <jsp:param name="currPage" value="dataImportNaviMenu"/>
    </jsp:include>
  <div class="i_mainRight" id="i_mainRight">
    <div class="height"></div>
    <div class="accountTitle">
            系统管理-数据导入&nbsp;&nbsp;&nbsp;<span style="color:red;font-size: 13px">(只有新店铺才能导入会员，先导客户信息(会员基本信息在客户信息中一起导入)，再导会员服务)</span>
    </div>
        
        <div class="operation-tips clearfix">
            <ul class="operation-tips-content clearfix" style="float:left;">
                <li class="left-side"></li>
                <li>
                    <span class="tips">第一步：编辑导入文件</span>
                </li>
                <li class="arrow"></li>
                <li>
                    <span class="tips">第二步：选择导入类型</span>
                </li>
                <li class="arrow"></li>
                <li>
                    <span class="tips">第三步：选择导入文件</span>
                </li>
                <li class="arrow"></li>
                <li>
                    <span class="tips">第四步：上传文件</span>
                </li>
                <li class="arrow"></li>
                <li>
                    <span class="tips">第五步：匹配字段</span>
                </li>
                <li class="arrow"></li>
                <li>
                    <span class="tips">完成</span>
                </li>
                <li class="right-side"></li>
            </ul>
        </div>
        <div class="sepcial-tips" style="padding-left:30px;width:940px;">提示：导入文件需用标准模板，若需模板请联系客服。</div>

    <div class="accountMain">
            <table class="import_table sysSet_table" cellpadding="0" cellspacing="0">
                <colgroup>
                    <col width="100"/>
                    <col width="400"/>
                </colgroup>
      <tr>
        <td>导入类型</td>
                    <td style="text-align:left;">
          <div id="input_account">
                            <form:select path="importType" id="importType"
                                         style="width:288px;height:22px;border:1px solid #CCCCCC;border-top:1px solid #666666;">
                 <form:options items="${importType}"/>
             </form:select>
          </div>
        </td>
      </tr>

                <tr>
                    <td>EXCEL文件</td>
                    <td width="500px">
                        <input type="file" name="selectfile" id="selectfile" style="float:left;display:block;margin-right:10px;"/>
                        <div style="float:left;">
                            <div class="upFileBtn" id="upFileBtn">
                                <input type="button" id="upload_button" class="localUpload" value="上传" style="display:block"/>
                            </div>
                            <img id="uploading" src="images/loadinglit.gif" alt="正在上传" title="正在上传" style="display:none">
                            <input type="hidden" id="importRecordId" value=""/>
                        </div>

                    </td>
                </tr>

      <tr>
        <td>字段匹配</td>
                    <td>

          <div class="fieldHead">
             <div id="sysFieldTitle" class="sysFieldTitle">系统字段
                                <input type="hidden" id="selectedField" value=""/>
                                <input type="hidden" id="fieldMapping" value=""/>
              </div>
            <div id="uploadTitle" class="uploadTitle">上传文件字段</div>
          </div>

          <div class="field">


            <div class="sysField">
              <div id="systemFieldList">

              </div>
            </div>


            <div id="uploadField" class="uploadField">
              <div id="uploadFieldList">

              </div>
            </div>


          </div>

          <div class="fieldBtn">
                            <input type="button" id="cancel_import" value="取消" onfocus="this.blur();"/>
                            <input type="button" id="submit_import" value="确认" onfocus="this.blur();"/>
                            <img id="importing" src="images/loadinglit.gif" alt="正在导入" title="正在导入"
                                 style="display:none"/>
          </div>
        </td>
                </tr>
      </table>
    </div>
    </div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
