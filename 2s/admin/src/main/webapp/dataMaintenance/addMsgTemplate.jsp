<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>新增短信模板</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp" %>
</head>
<script type="text/javascript">
    jQuery(function () {

        jQuery("#div_close,#cancleBtn").click(function () {
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            window.parent.document.getElementById("mask").style.display = "none";
        });
        jQuery("#confirmBtn").click(function () {
            if (checkFormData() == false) {
                return;
            }
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            jQuery('#addMsgTemplate_table').ajaxSubmit(function (data) {
                if (data == "succ") {
//                    window.parent.nextPageNo=1;
                    window.parent.searchMsgTemplate();
                }
        });
        });
        jQuery("#msgTemplate_type").blur(function () {
            var msg_type = jQuery("#msgTemplate_type").val();
            jQuery.ajax({
                type:"POST",
                url:"dataMaintenance.do?method=searchMessageTemplate",
                data:{type:msg_type, pageNo:1, shopId:-1},
                cache:false,
                dataType:"json",
                success:function (jsonStr) {
                    if (jsonStr[0].length == 1) {
                        jQuery("#msgTemplate_type").val("");
                        alert("模板类型重复，请重新输入！");
                    }
                }
            });
        });
    });
    function checkFormData() {

        if (isEmpty(jQuery("#msgTemplate_type").val())) {
            alert("模板类型不为空，请重新输入！");
            return false;
        }
    }
    function isEmpty(text) {
        if (text == "" || new RegExp("^\\s+|\\s+$", "g").test(text)) {
            return true;
        }
        return false;
    }
</script>

<body>
<form:form commandName="msgTemplateDTO" id="addMsgTemplate_table" action="dataMaintenance.do?method=saveMsgTemplate"
           method="post" onsubmit="return false;">
    <form:hidden path="shopId" value="${msgTemplateDTO.shopId}"/>

    <div>
  <div id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
      <div class="config_title" id="div_drag">新增短信模板</div>
      <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
      <table cellpadding="0" id="msgTemplateTable" cellspacing="0" class="msgTemplateTable">
        <col width="150">
        <col width="200">
        <%--<col width="100"/>--%>
        <%--<col width="100">--%>
        <%--<col width="100">--%>
        <tr>
          <td class="label">模板名</td>
                        <td><form:input path="name" id="msgTemplate_name" value="${msgTemplateDTO.name}" class="txt"/>
                        </td>
        </tr>
        <tr>
          <td class="label">模板类型</td>
                        <td><form:input path="type" id="msgTemplate_type" value="${msgTemplateDTO.type}" class="txt"/>
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
                            <input type="radio" id="repairCbox" name="necessary" value="NECESSARY"
                                   checked="true"/><label>必要</label>
                            <input type="radio" id="inverntoryCbox" name="necessary"
                                   value="UNNECESSARY"/><label>不必要</label>
          </td>
        </tr>
        <tr>
          <td class="label">内容</td>
        </tr>
        <tr>
          <td class="label"></td>
                        <td><form:textarea path="content" id="msgTemplateTable_content"
                                           value="${msgTemplateDTO.content}"
                             rows="6" class="txt"/></td>
        </tr>
      </table>
      <div class="more_his">
        <input type="submit" value="确认" onfocus="this.blur();" class="btn" id="confirmBtn"/>
        <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
      </div>

    </div>
  </div>
</form:form>


</body>
</html>