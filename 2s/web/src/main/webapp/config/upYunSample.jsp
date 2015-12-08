<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Simple jsp page</title>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <%@ include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.config<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.all<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.parse<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $(document).ready(function(){
            var editor =UE.getEditor('myEditor');
            $("#saveUEditorInfo").bind("click",function(){
                //alert(editor.getTagNumInContents("img"));
                if(editor.hasContents()){
                    editor.sync();
//                    $("#uEditorInfoForm").submit();
                }else{
                    alert("不能为空！");
                }

            })
            uParse('#showContent');

            $("#submitImage").bind("click",function(){
                $("#imageForm").ajaxSubmit({
                    dataType: "json",
                    type: "POST",
                    success: function(data) {
                        alert(JSON.stringify(data));
                    },
                    error: function() {
                        alert("查询异常！");
                    }
                })
            });
        });

    </script>

</head>
<body>
<form action="http://v0.api.upyun.com/${upYunFileDTO.bucket}/" method="post" id="imageForm" enctype="multipart/form-data">
    <input type="hidden" id="policy" name="policy" value="${upYunFileDTO.policy}">
    <input type="hidden" id="signature" name="signature"  value="${upYunFileDTO.signature}">
    <input type="file" name="file">
    <input id="submitImage" type="button" value="保存">
</form>
<%--<c:forEach items="${upYunFileDTOList}" var="upYunFileDTO" varStatus="status">--%>
    <%--<input id="policy${status.index}" value="${upYunFileDTO.policy}">--%>
    <%--<input id="signature${status.index}" value="${upYunFileDTO.signature}">--%>
<%--</c:forEach>--%>
<form id="uEditorInfoForm" action="upYun.do?method=saveUEditorInfo" method="post">
    <script id="myEditor" name="uEditorInfo" type="text/plain">这里可以书写，编辑器的初始内容</script>
    <input type="button" id="saveUEditorInfo" value="保存">
</form>
<div id="showContent" style="width: 720px">
    ${uEditorInfo}
</div>
</body>
</html>