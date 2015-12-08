<%--
  Created by IntelliJ IDEA.
  User: XinyuQiu
  Date: 14-7-18
  Time: 上午9:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript">
        function submit(){
            var r = confirm("请再次核查上传的初始化文件，只能执行一次错误之后不可能回滚");
            if (r == true) {
               document.getElementById("fileForm").submit();
            }
        }

    </script>
</head>
<body>

<form id="fileForm" action="init.do?method=initObdStorage" method="post" enctype="multipart/form-data">
    <input type="file" name="uploadFile">
</form>
<input type="button" value="提交文件" onclick="submit()">
</body>
</html>
