<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>修改配置</title>
    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
</head>
<script type="text/javascript">
    jQuery(function() {
        jQuery("#div_close,#cancleBtn").click(function() {
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            window.parent.document.getElementById("mask").style.display = "none";
        });

        jQuery("#confirmBtn").click(function() {
            if (checkFormData() == false) {
                return;
            }
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            jQuery('#modifyMsgTemplate_table').ajaxSubmit(function(data) {
                if (data == "succ") {
//                    window.parent.nextPageNo=1;
                    window.parent.searchMsgTemplate();
                }
        });
    });
    });
    function checkFormData() {
    }
</script>

<body>
<form:form commandName="msgTemplateDTO" id="modifyMsgTemplate_table"
           action="dataMaintenance.do?method=updateMsgTemplate" method="post">
<form:hidden path="shopId" value="${msgTemplateDTO.shopId}"/>
<div  id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="config_title" id="div_drag">修改短信模板</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
      <table cellpadding="0" id="msgTemplateTable" cellspacing="0" class="msgTemplateTable">
        <col width="150">
        <col width="200"/>
        <tr>
          <td class="label">模板名</td>
          <td><form:input path="name" id="msgTemplate_name" value="${msgTemplateDTO.name}" class="txt"/></td>
        </tr>
        <tr>
          <td class="label">模板类型</td>
          <td>
            <span>${msgTemplateDTO.type}</span>
            <form:hidden path="type" id="msgTemplate_type" value="${msgTemplateDTO.type}" />
          </td>
        </tr>
        <tr>
          <td class="label">场景</td>
          <td><form:select path="scene" id="scene" value="${msgTemplateDTO.messageSceneList}" class="txt">
            <c:forEach items="${msgTemplateDTO.messageSceneList}" var="messageScene" varStatus="status">
              <form:option value="${messageScene}">${messageScene}</form:option>
            </c:forEach>
          </form:select>
          </td>
        </tr>
        <tr>
          <td class="label">必要性</td>
          <td>
            <input type="radio" id="repairCbox" name="necessary" value="NECESSARY" checked="true"/><label>必要</label>
            <input type="radio" id="inverntoryCbox" name="necessary" value="UNNECESSARY" /><label>不必要</label>
          </td>
        </tr>

            </tr>
            <tr>
                <td class="label">内容</td>
            </tr>
            <tr>
                <td class="label"></td>
                <td><form:textarea path="content" value="${msgTemplateDTO.content}" rows="6" id="msgTemplate_content"
                                   class="txt"/></td>
            </tr>

      </table>
        <div class="more_his">
            <input type="button" value="确认" onfocus="this.blur();" class="btn" id="confirmBtn"/>
            <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
        </div>
    </div>
    </form:form>


</body>
</html>