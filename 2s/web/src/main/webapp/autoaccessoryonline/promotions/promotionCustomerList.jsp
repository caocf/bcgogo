<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-6-27
  Time: 下午10:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
    $().ready(function(){
        $("#allChk").click(function(){
            if($(this).attr("checked")){
                $("input[name='subCustomerCheckBox']").attr("checked",true);
            }else{
                $("input[name='subCustomerCheckBox']").attr("checked",false);
            }
        });

        $(document).bind("click",function(event){
          if($(event.target).hasClass("close_icon")){
              $(event.target).closest(".customerli").remove();
          }
        });
        $("#searchCustomerBtn").bind("click", function () {
            var paramJson={startPageNo:1,maxRows:10};
            if($("#customerInfoText").val() == $("#customerInfoText").attr("initialValue")){
                paramJson["searchWord"]="";
            }else{
                paramJson["searchWord"]=$("#customerInfoText").val();
            }

            APP_BCGOGO.Net.syncPost({
                url:"message.do?method=selectCustomer",
                data:paramJson,
                dataType:"json",
                success:function (data) {
                    drawCustomerTable(data);
                    initPages(data, "_customerList", "message.do?method=selectCustomer", '', "drawCustomerTable", '', '', paramJson, '');
                },
                error:function () {
                    nsDialog.jAlert("数据异常!");
                }
            });
        });
    });
    function callAddCustomer(){

    }

    function contain(arr,elem){
        if(G.isEmpty(arr)){
            return false;
        }
        for(var i=0;i<arr.length;i++){
            if(arr[i]==elem){
                return true;
            }
        }
        return false;
    }

    function selectCustomer(){
        var sCustomerIdArray=new Array();
        $("#receiverBox .customerli").each(function(){
            sCustomerIdArray.push($(this).attr("customerId"));
        });

        var customerIdArray=new Array();
        $("input[name='subCustomerCheckBox']").each(function(){
            var customerList="";
            if($(this).attr("checked")){
                var customerId=$(this).val();
                if(contain(sCustomerIdArray,customerId)){
                    return;
                }
                var customerName=$(this).attr("data-name");
                customerList+='<li class="customerli bcgogo-customerSmsInput-option" customerId="'+customerId+'"><p>'+customerName+'</p><span class="close_icon">×</span></li>';
                if(!contain(customerIdArray,customerId)){
                    customerIdArray.push(customerId);
                }
            }
            if(!G.isEmpty(customerList)){
                $("#receiverBox").append(customerList);
            }
        });
        if(!G.isEmpty(customerIdArray)){
            $("#messageReceivers").val(customerIdArray.toString())
        }
        $('#customerList').dialog('close');
    }

    function drawCustomerTable(data) {
        $(".j_clear_span").text("0");
        $("#customerTable tbody").empty();
        $("#checkAllCustomerCheckBox").attr("checked",false);
        if (data == null || data[0] == null || data[0].customerSuppliers == null || data[0].customerSuppliers == 0) {
            return;
        }

        $.each(data[0].customerSuppliers, function(index, customer) {
            var customerId = (!customer.idStr ? "" : customer.idStr);
            var customerName = (!customer.name ? "--" : customer.name);
            var contact = (!customer.contact ? "--" : customer.contact);
            var mobile = (!customer.mobile ? "--" : customer.mobile);
            var areaInfo = (!customer.areaInfo ? "--" :customer.areaInfo);

            var tr = '<tr class="table-row-original">';
            tr += '<td><input type="checkbox" style="margin-left: 10px;" name="subCustomerCheckBox" data-name="'+customer.name+'" data-mobile="'+customer.mobile+'" value="'+customerId+'"/></td>';
            tr += '<td title="' + customerName + '">' + customerName + '</td>';
            tr += '<td title="' + contact + '">' + contact + '</td>';
            tr += '<td title="' + mobile + '">' + mobile + '</td>';
            tr += '<td title="' + areaInfo + '">' + areaInfo + '</td>';
            tr += '</tr>';
            $("#customerTable tbody").append($(tr));
        });
        tableUtil.tableStyle('#customerTable','.tab_title');
        $("#hasMobileCustomerNum").text(data[0].hasMobileNumFound==null?0:data[0].hasMobileNumFound);
        $("#relatedCustomerNum").text(data[1].totalRows);
        $("#allCustomerNum").text(data[0].numFound==null?0:data[0].numFound);
    }
</script>
<div id="customerList" class="alertMain" style="display: none">

    <div class="height"></div>
    <div class="customerInfo">
        客户信息：
        <input type="text" id="customerInfoText" style="width: 250px" class="txt J-bcgogo-droplist-on" pagetype="relatedcustomerdata"
               initialvalue="客户名/联系人/手机" value="客户名/联系人/手机" />
        <input type="button" id="searchCustomerBtn" value="查&nbsp;询" onfocus="this.blur();" class="buttonSmall">
    </div>
    <div class="height"></div>
    <div class="blue_color">关联客户（<span id="relatedCustomerNum">0</span>）</div>
    <div class="height"></div>
    <table id="customerTable" cellpadding="0" cellspacing="0" class="tabRecord tabSupplier">
        <colgroup>
        <col width="40">
        <col width="180">
        <col width="70">
        <col width="100">
        <col>
        </colgroup>
        <thead>
        <tr class="tabTitle">
            <td style="padding-left: 10px"><input id="allChk" type="checkbox" /></td>
            <td>客户名</td>
            <td>主联系人</td>
            <td>手机</td>
            <td>所在区域</td>
        </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
    <div class="height"></div>
    <div class="i_pageBtn">
        <%--<bcgogo:ajaxPaging--%>
                <%--url="message.do?method=selectCustomer"--%>
                <%--postFn="drawCustomerTable"--%>
                <%--dynamical="_customerList"--%>
                <%--display="none"--%>
                <%--/>--%>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="message.do?method=selectCustomer"></jsp:param>
            <jsp:param name="dynamical" value="_customerList"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,maxRows:10}"></jsp:param>
            <jsp:param name="jsHandleJson" value="drawCustomerTable"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>
    <div class="height"></div>
    <div class="button">
        <a onclick="selectCustomer()" class="btnSure">确&nbsp;定</a>
        <a onclick="{$('#customerList').dialog('close')}" class="btnSure">取&nbsp;消</a>
    </div>
</div>

