<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.dto.OperationLogDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bcgogo.utils.CollectionUtil" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>查看操作记录</title>
<link rel="stylesheet" type="text/css" href="styles/up2<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
<script type="text/javascript">
$().ready(function(){
    $('#div_close').bind('click', function () {
        $('#mask', parent.document).css('display', 'none');
        $('#iframe_PopupBox', parent.document).css('display', 'none');
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });
});
</script>
</head>
<%
    List<OperationLogDTO> operationLogDTOList = (List<OperationLogDTO>)request.getAttribute("operationLogDTOList");
%>
<body style='overflow: hidden;'>
<div class="i_supplierInfo more_supplier" id="div_show">
    <div class="i_arrow"></div>

    <div class="i_upLeft"></div>
    <div class="i_upCenter i_two">
        <div class="i_note more_title" id="div_drag">查看操作记录</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
            <table class="table3 supplierTable" style="border-collapse:collapse;">
                <col width="40"/>
                <col width="150"/>
                <col width="150"/>
                <col width="150"/>
                <tr class="tab_title">
                    <td class="tab_first">NO</td>
                    <td>操作时间</td>
                    <td>操作者</td>
                    <td>操作内容</td>
                </tr>
<%
    if(CollectionUtil.isNotEmpty(operationLogDTOList)){
        for(int i=0;i<operationLogDTOList.size();i++){

%>
                <tr>
                    <td class="tab_first"><%=i+1%></td>
                    <td><%=operationLogDTOList.get(i).getCreationDateStr()%></td>
                    <td><%=operationLogDTOList.get(i).getUserName()%></td>
                    <td><%=operationLogDTOList.get(i).getContent()%></td>
                </tr>
<%
        }
    }
%>
            </table>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>

</body>
</html>
