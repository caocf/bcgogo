<%--
  Created by IntelliJ IDEA.
  User: xzhu
  Date: 13-9-13
  Time: 下午1:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript">
        function test(){
            window.print();
            window.close();
        }
        window.onload=test;
    </script>
</head>
<body>
<img src="${imageURL}"/>
</body>
</html>