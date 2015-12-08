<%--
  Created by IntelliJ IDEA.
  User: king
  Date: 14-2-18
  Time: 上午11:08
  To change this template use File | Settings | File Templates.
--%>
<!-- 操作日志弹出框 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="i_searchBrand" id="showOrderOperationLog_div" title="查看单据操作记录" style="display:none; width: 500px;">
    <table id="orderOperationLog_tab" border="0" width="480">
        <tr style="background-color:#E9E9E9; height:16px; ">
            <td class="tab_first">NO</td>
            <td>操作时间</td>
            <td>操作者</td>
            <td>操作内容</td>
        </tr>
    </table>
</div>
<script type="text/javascript">
    function showOrderOperationLog(orderId,orderType) {
        APP_BCGOGO.Net.asyncPost({
            url: "txn.do?method=showOrderOperationLog",
            data: {
                orderId: orderId,
                orderType: orderType
            },
            cache: false,
            dataType: "json",
            success: function(result) {
                if(result && result.length>0){
                    $("#orderOperationLog_tab tr").not(":first").remove();
                    for(var i = 0; i < result.length; i++) {
                        var creationDateStr = result[i].creationDateStr == null ? "" : result[i].creationDateStr;
                        var userName = result[i].userName == null ? "" : result[i].userName;
                        var content = result[i].content == null ? "" : result[i].content;
                        var tr = '<tr>';
                        tr += '<td style="border-left:none;padding-left:10px;"><span>' + (i + 1) + '&nbsp;</span></td>';
                        tr += '<td>' + creationDateStr + '</td>';
                        tr += '<td>' + userName + '</td>';
                        tr += '<td>' + content + '</td>';
                        $("#orderOperationLog_tab").append($(tr));
                    }
                    $("#showOrderOperationLog_div").dialog({
                        resizable: false,
                        width:500,
                        height: "auto",
                        modal: true,
                        closeOnEscape: false,
                        buttons:{
                            "关闭":function(){
                                $(this).dialog("close");
                            }
                        }
                    });
                }
            }
        });

    }
</script>

