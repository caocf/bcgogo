/*
$().ready(function(){

    $("#printButton").live("click",function(){
        var url;
        var data;
        if(getPageContent()=="assistantStat"){
            url="print.do?method=printAssistantStat"
        }
        APP_BCGOGO.Net.syncGet({
            url:url,
            data:data,
            dataType:"json",
            success:function(result) {
                if(result && result.length >1){
                    var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
                        "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
                    for(var i = 0; i<result.length; i++){
                        var radioId = "selectTemplate" + i;
                        selects += "<input type='radio' id='"+radioId+"' name='selectTemplate' value='"+result[i].idStr+"'";
                        if(i==0){
                            selects += " checked='checked'";
                        }
                        selects += " />" +"<label for='"+radioId+"'>"+result[i].displayName +"</label><br/>";
                    }
                    selects += "</div>";
                    nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
                        if (returnVal) {
                            printSalesOrder($("input:radio[name='selectTemplate']:checked").val());
                        }
                    });
                }else{
                    printSalesOrder();
                }
            }
        });
    });
});

function printPageContent(url){
    function printSalesOrder(templateId){
    if ($("#id").val()) {
        window.showModalDialog("sale.do?method=getSalesOrderToPrint&salesOrderId=" + $("#id").val() +"&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
        return;
    }
    if ($("#draftOrderIdStr").val()) {
        window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=SALE&now=" + "&templateId="+templateId + new Date());
    }
}
}


function getPageContent(){
    return  $("#pageContent").val();
}*/
