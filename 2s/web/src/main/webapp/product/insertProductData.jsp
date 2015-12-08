<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-19
  Time: 下午3:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head><title>导入产品数据</title></head>
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    var labelValue;
    $(function () {
        $("#radio_td>input:eq(0)").attr("checked", true);
        labelValue = $("#radio_td>input:eq(0)").next().html();
        $("#radio_td>input").live("click", function () {
            labelValue = $(this).next().html();
        });
    });
    function thisformsubmit() {
        if (thisform.productFile.value == null || thisform.productFile.value == '') {
            nsDialog.jAlert("请选择需要上传的文件!");
        } else {
            var filesuffix = thisform.productFile.value.split(".")[1];
            if (filesuffix != 'csv') {
                nsDialog.jAlert("请选择CSV文件!");
            }
            else {
                nsDialog.jConfirm("您选择的是:" + labelValue + ",确认上传?", null, function(returnVal) {
                    if (returnVal) {
                        thisform.submit();
                    }
                });
            }
        }
    }
</script>
<body>
<form:form id="" action="product.do?method=insertproductdata" method="post" name="thisform" commandName="command"
           enctype="multipart/form-data">
    <table>
        <tr>
            <td id="radio_td">
                <input type="radio" name="productFileType" value="0"/><label>车型数据</label>
                <input type="radio" name="productFileType" value="1"/> <label>产品数据</label>
                <input type="radio" name="productFileType" value="2"/> <label>车型与产品规格对应表数据</label>
                <input type="radio" name="productFileType" value="3"/> <label>车牌信息</label>
                <input type="radio" name="productFileType" value="4"/> <label>地区信息</label>
            </td>
            <td>
                <input type="file" name="productFile"/>
            </td>
            <td><input type="button" value="上传" onclick="thisformsubmit()"/></td>

        </tr>
    </table>

</form:form>

</body>
</html>