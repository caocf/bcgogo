var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(document).ready(function () {
    $(function () {
        tableUtil.tableStyle(".tabPick",null,"odd");
    })
    $("#searchBtn").click(function () {
        /*$("#thisform").attr("action", "insurance.do?method=searchInsuranceOrderData");
        $("#thisform").submit();*/
        searchInsuranceDataAction();
    });

    $("#startTimeStr,#endTimeStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-5, c",
        "yearSuffix": "",
        "showButtonPanel": true,
        "maxDate": 0
    });

    $(".showInsurance,.showRepair").bind("click", function () {
        window.location.href = $(this).attr("url");
    });

    $("#cleanCondition").bind("click",function(){
        $("#policyNo").val("");
        $("#licenceNo").val("");
        $("#customer").val("");
        $("#startTimeStr").val("");
        $("#endTimeStr").val("");
        $("#insuranceCompanyId").val("");
        $("#status").val("");

    });

    $("#insuranceIdSort").bind("click",function(){
        clickOnCondition("insuranceIdSort","policy_no");
       searchInsuranceDataAction();

    });

    $("#insuranceAccidentDateSort").bind("click",function(){
        clickOnCondition("insuranceAccidentDateSort","accident_date");
        searchInsuranceDataAction();
    });

    $("#insuranceCompanySort").bind("click",function(){
        clickOnCondition("insuranceCompanySort","insurance_company");
        searchInsuranceDataAction();
    });
    $("#sortStatus").val("policy_no asc,accident_date desc,insurance_company desc")
    searchInsuranceDataAction();



})

function clickOnCondition(conditionId,sqlColumn){
    if($("a[name='J_sortStyle']").not(this).hasClass("hover")){
        $("a[name='J_sortStyle']").removeClass("hover");
    }
    $("#"+conditionId).addClass("hover");
    var sortStr = "";
    if ($("#"+conditionId+"Span").hasClass("arrowDown")) {
        $("#"+conditionId+"Span").addClass("arrowUp").removeClass("arrowDown");
        sortStr =" "+sqlColumn+" asc ";
    } else {
        $("#"+conditionId+"Span").addClass("arrowDown").removeClass("arrowUp");
        sortStr =" "+sqlColumn+" desc ";
    }
    if($("#sortStatus")){
        $("#sortStatus").val(sortStr);
    }

}

function searchInsuranceDataAction(){
    var ajaxData=beforeSearchInsurance();
    var ajaxUrl = "insurance.do?method=searchInsuranceOrderData";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        initInsuranceTable(json);
        initPage(json, "insuranceList", ajaxUrl, '', "initInsuranceTable", '', '', ajaxData, '');
    });
}
function initInsuranceTable(json){
    $("#insuranceDataTable tr:not(:first)").remove();

        $("#insuranceTableTitleDiv").remove();


    if($("#showNoRecordMessage").length>0){
        $("#showNoRecordMessage").remove();
        $("#brStr").remove();
    }


    var htmlStr='';
    var tableHtml="";

        var insuranceTableData=[];
        insuranceTableData =json["results"];

     tableHtml+='<div id="insuranceTableTitleDiv"><span>共有：<b class="yellow_color">'+json["total"]+'</b>&nbsp;条</span>&nbsp;&nbsp;<span>金额<b class="yellow_color">'+json["data"]+'</b>元</span>' +
                '<a class="addNewSup blue_color" class="addNew" target="_blank" href="insurance.do?method=createInsuranceOrder" id="addNewInsurance">新增保险理赔</a><div>';

        if(insuranceTableData.length==0){
            htmlStr+='<br id="brStr"><div style="color: #000000" id="showNoRecordMessage">无保险单记录！</div>';
        }else{
            for(var i=0;i<insuranceTableData.length;i++){
                htmlStr+='<tr class="titBody_Bg"><td style="padding-left:10px;">'+(i+1)+'</td>';
                htmlStr+='<td><a class="blue_color showInsurance"';
                htmlStr+='href="insurance.do?method=showInsuranceOrder&insuranceOrderId='+insuranceTableData[i].idStr+'">'+insuranceTableData[i].policyNo+'</a></td>';
                htmlStr+='<td>'+insuranceTableData[i].insuranceCompany+'</td>';
                htmlStr+='<td>'+insuranceTableData[i].customer+'</td>';
                htmlStr+='<td>'+insuranceTableData[i].licenceNo+'</td>';
                htmlStr+='<td>'+insuranceTableData[i].accidentDateStr+'</td>';
                htmlStr+='<td>'+insuranceTableData[i].statusStr+'</td>';
                if(insuranceTableData[i].repairOrderId!=""){
                    htmlStr+='<td><a class="blue_color showRepair" style="cursor: pointer;"';
                    htmlStr+='href=txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId='+insuranceTableData[i].repairOrderIdStr+'>';
                    htmlStr+=insuranceTableData[i].repairOrderReceiptNo+'</a>';

                }

                htmlStr+='</td></tr><tr class="titBottom_Bg"><td colspan="8"></td></tr>';


            }

        }

        $("#insuranceDataTable").append($(htmlStr));

    $("#insuranceTableTitle").append($(tableHtml));


}
function beforeSearchInsurance(){
    var ajaxInsuranceData=null,
        policyNo=$("#policyNo").val()!=""? $("#policyNo").val():"",
        licenceNo=$("#licenceNo").val()!=""? $("#licenceNo").val():"",
        customer=$("#customer").val()!=""? $("#customer").val():"",
        startTimeStr=$("#startTimeStr").val()!=""? $("#startTimeStr").val():"",
        endTimeStr=$("#endTimeStr").val()!=""? $("#endTimeStr").val():"",
        insuranceCompanyId=$("#insuranceCompanyId").val()!=""? $("#insuranceCompanyId").val():"",
        status=$("#status").val()!=""? $("#status").val():"",
        sortStr =$("#sortStatus").val();
    ajaxInsuranceData={
        policyNo:policyNo,
        licenceNo:licenceNo,
        customer:customer,
        startTimeStr:startTimeStr,
        endTimeStr:endTimeStr,
        insuranceCompanyId:insuranceCompanyId,
        status:status,
        sortStr:sortStr,
        maxRows:5
    }
    return ajaxInsuranceData;


}






















