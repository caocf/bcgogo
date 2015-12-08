$(document).ready(function() {

    $("#mergeCustomerBtn").click(function() {
        var selectedSize = 0;
        var customerIds = "";
        var relatedCustomerSize=0;
        var obdCustomerSize = 0;
        $("#selectedIdArray input").each(function() {
            if($(this).val()) {
                selectedSize++;
                customerIds += $(this).val() + ",";
            }
            if(!stringUtil.isEmpty($(this).attr("customerShopId"))) {
                relatedCustomerSize++;
            }
            if($(this).attr("isObdCustomer") == 'true') {
                obdCustomerSize++;
            }
        });
        customerIds = customerIds.substr(0, customerIds.length - 1);
        if(selectedSize != 2) {
            nsDialog.jAlert("请选择两个要合并的客户！");
            return;
        }
        if(relatedCustomerSize==2){
            nsDialog.jAlert("两个关联客户无法合并！");
            return;
        }
        if(relatedCustomerSize==2){
            nsDialog.jAlert("两个关联客户无法合并！");
            return;
        }
        if(obdCustomerSize == 2) {
            nsDialog.jAlert("两个OBD客户无法合并！");
            return;
        }
        $("<div id='mergeConfirmDiv' >友情提示：客户合并后，将只保留选中客户的信息，其他未选中客户的消费信息、欠款信息、会员服务,OBD信息和车辆信息将转移到选中客户上。合并操作无法撤消，请确认</div>").dialog({
            resizable: true,
            title:"合并确认",
            height:150,
            width:500,
            modal: true,
            closeOnEscape: false,
            buttons:{
                "确定":function(){
                    $(this).dialog("close");
                    getMergedCustomer(customerIds);
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });

    $("div[id^='selectedCustomer']").click(function(event) {
        $("div[id^='selectedCustomer']").removeClass("radCheck").addClass("radNormal");
        $("div[id^='selectedCustomer']").attr("isParent", false);
        $('.tabMerge').removeClass('tabMerge_selected');
        var targetDiv= $(event.target).closest('.border')
        $(targetDiv).find('table').addClass('tabMerge_selected');
        $(targetDiv.find('table').get(0)).css("border-bottom","none");
        $(targetDiv.find('table').get(1)).css("border-top","none");
        $(this).removeClass().addClass("radCheck");
        $(this).attr("isParent", true);
    });
});

function getMergedCustomer(customerIds){
    var data = {
        customerIds: customerIds
    };
    var url = "customer.do?method=getMergedCustomers";
    APP_BCGOGO.Net.asyncPost({
        url: url,
        dataType: "json",
        data: data,
        success: function(json) {
            if(!json.success) {
                nsDialog.jAlert(json.msg);
                return;
            }

            if(json.mergeRelatedFlag){
                $("#mergeRelatedCustomerDetail").dialog({
                    resizable: true,
                    title: "合并客户",
                    width: 1050,
                    height: 620,
                    modal: true,
                    closeOnEscape: false
                });
                initMergedRelatedCustomerInfo(json);
            }else{
                $("#mergeCustomerDetail").dialog({
                    resizable: true,
                    title: "合并客户",
                    width: 1050,
                    height: 620,
                    modal: true,
                    closeOnEscape: false
                });
                initMergedCustomerInfo(json);
            }
            $(".mergeBefore").show();
            $(".mergeAfter").hide();

        },
        error: function(json) {
            nsDialog.jAlert("获取客户信息异常！");
        }
    });
}

function initMergedRelatedCustomerInfo(json){
    var parent=json.parent;
    var child=json.child;
    if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
        return;
    }
    $(".unRelateMergedCustomer").remove();
    $(".mergeConfirm input").remove();
    $("[name='selectedCustomer1']").attr("customerId",G.normalize(parent.idStr));
    $("[name='selectedCustomer1']").attr("customerShopId",G.normalize(parent.customerShopId));
    $("[name='selectedCustomer2']").attr("customerId",child.idStr);
    $("[name='selectedCustomer2']").attr("customerShopId",child.customerShopId);
    if(!parent.relationType || parent.relationType == "UNRELATED"){
        $("#relateParentTable .name").html(G.normalize(parent.name) + "<span class='yellow_color'>(非关联)</span>");
    }else{
        $("#relateParentTable .name").html(G.normalize(parent.name)+ "<span class='yellow_color'>(关联)</span>");
    }
    $("#relateParentTable .contact").text(G.normalize(parent.contact));
    $("#relateParentTable .address").text(G.normalize(parent.address));
    $("#relateParentTable .landLine").text(G.normalize(parent.landLine));
    $("#relateParentTable .mobile").text(G.normalize(parent.mobile));
    $("#relateParentTable .bank").text(G.normalize(parent.bank));
    $("#relateParentTable .bankAccountName").text(G.normalize(parent.bankAccountName));   //todo
    $("#relateParentTable .account").text(G.normalize(parent.account));
    $("#relateParentTable .qq").text(G.normalize(parent.qq));
    $("#relateParentTable .shortName").text(G.normalize(parent.shortName));
    $("#relateParentTable .email").text(G.normalize(parent.email));
    $("#relateParentTable .fax").text(G.normalize(parent.fax));
    $("#relateParentTable .deposit").text(G.normalize(parent.deposit));
    $("#relateParentTable .areaStr").text(G.normalize(parent.areaStr));
    $("#relateParentTable .customerKindStr").text(G.normalize(parent.customerKindStr));
    $("#relateParentTable .settlementTypeStr").text(G.normalize(parent.settlementTypeStr));
    $("#relateParentTable .invoiceCategoryStr").text(G.normalize(parent.invoiceCategoryStr));
    /*if (!parent.relationType || parent.relationType == "UNRELATED") {
     $("#relateParentTable .relationType").text("非关联");
     }else{
     $("#relateParentTable .relationType").text("已关联");
     }*/
    $("#relateParentTable .single_contact").remove();
    var parentContacts = parent.contacts;
    var $contacts = "";
    if (!G.isEmpty(parentContacts) && !G.isEmpty(parentContacts.length)) {
        for (var i = 0; i < parentContacts.length; i++) {
            if (isValidContact(parentContacts[i])) { // 引入contact.js
                $contacts += "<tr class='single_contact'>" +
                    "<td class='tab_title'>联系人</td><td id='name'>" + G.normalize(parentContacts[i].name) + "</td>" +
                    "<td class='tab_title'>手机</td><td id='mobile'>" + G.normalize(parentContacts[i].mobile) + "</td>" +
                    "<td class='tab_title'>QQ</td><td id='qq'>" + G.normalize(parentContacts[i].qq) + "</td>" +
                    "<td class='tab_title'>Email</td><td id='email'>" + G.normalize(parentContacts[i].email) + "</td>" +
                    "</tr>"
            }
        }
    }
    if($contacts){
        $($contacts).insertAfter($("#relateParentTable tr").eq(1));
    }

    if (!child.relationType || child.relationType == "UNRELATED") {
        $("#relateChildTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(非关联)</span>");
    }else{
        $("#relateChildTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(关联)</span>");
    }
    //$("#relateChildTable .name").text(G.normalize(child.name));
    $("#relateChildTable .contact").text(G.normalize(child.contact));
    $("#relateChildTable .address").text(G.normalize(child.address));
    $("#relateChildTable .landLine").text(G.normalize(child.landLine));
    $("#relateChildTable .mobile").text(G.normalize(child.mobile));
    $("#relateChildTable .bank").text(G.normalize(child.bank));
    $("#relateChildTable .bankAccountName").text(G.normalize(child.bankAccountName));   //todo
    $("#relateChildTable .account").text(G.normalize(child.account));
    $("#relateChildTable .qq").text(G.normalize(child.qq));
    $("#relateChildTable .shortName").text(G.normalize(child.shortName));
    $("#relateChildTable .email").text(G.normalize(child.email));
    $("#relateChildTable .fax").text(G.normalize(child.fax));
    $("#relateChildTable .deposit").text(G.normalize(child.deposit));
    $("#relateChildTable .areaStr").text(G.normalize(child.areaStr));
    $("#relateChildTable .customerKindStr").text(G.normalize(child.customerKindStr));
    $("#relateChildTable .settlementTypeStr").text(G.normalize(child.settlementTypeStr));
    $("#relateChildTable .invoiceCategoryStr").text(G.normalize(child.invoiceCategoryStr));
    /*if (!child.relationType || child.relationType == "UNRELATED") {
     $("#relateChildTable .relationType").text("非关联");
     }else{
     $("#relateChildTable .relationType").text("已关联");
     }*/

    $("#relateChildTable .single_contact").remove();
    var childContacts = child.contacts;
    var $childContacts = "";
    if (!G.isEmpty(childContacts) && !G.isEmpty(childContacts.length)) {
        for (var i = 0; i < childContacts.length; i++) {
            if (isValidContact(childContacts[i])) { // 引入contact.js
                $childContacts += "<tr class='single_contact'>" +
                    "<td class='tab_title'>联系人</td><td id='name'>" + G.normalize(childContacts[i].name) + "</td>" +
                    "<td class='tab_title'>手机</td><td id='mobile'>" + G.normalize(childContacts[i].mobile) + "</td>" +
                    "<td class='tab_title'>QQ</td><td id='qq'>" + G.normalize(childContacts[i].qq) + "</td>" +
                    "<td class='tab_title'>Email</td><td id='email'>" + G.normalize(childContacts[i].email) + "</td>" +
                    "</tr>"
            }
        }
    }
    if($childContacts){
        $($childContacts).insertAfter($("#relateChildTable tr").eq(1));
    }

    var parentRecord=parent.customerRecordDTO;
    var childRecord=child.customerRecordDTO;
    $("#relateParentTable .totalReceivable").text(dataTransition.rounding(parentRecord.totalReceivable, 2) + "/" + G.normalize(parentRecord.totalPayable));
    $("#relateParentTable .totalAmount").text(dataTransition.rounding(parentRecord.totalAmount,2));
    $("#relateParentTable .totalReturnAmount").text(dataTransition.rounding(parentRecord.totalReturnAmount,2));
    $("#relateParentTable .totalPayable").text(G.normalize(parentRecord.totalPayable));
    $("#relateParentTable .countCustomerReturn").text(G.normalize(parentRecord.countCustomerReturn));

    $("#relateChildTable .totalReceivable").text(G.normalize(childRecord.totalReceivable) + "/" + G.normalize(childRecord.totalPayable));
    $("#relateChildTable .totalAmount").text(G.normalize(childRecord.totalAmount));
    $("#relateChildTable .totalReturnAmount").text(dataTransition.rounding(childRecord.totalReturnAmount,2));
    $("#relateChildTable .totalPayable").text(G.normalize(childRecord.totalPayable));
    $("#relateChildTable .countCustomerReturn").text(G.normalize(childRecord.countCustomerReturn));

    var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" /><input type="button" id="cancelMerge" value="取&nbsp;消" onfocus="this.blur();"  />';
    $("#mergeRelateDiv").append(okBtns);
    //$("div[id^='selectedCustomer']").hide();
    doBindHandler();

}

function initMergedCustomerInfo(json){
    var parent=json.parent;
    var child=json.child;
    if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
        return;
    }
    $(".relatedMergedCustomer").remove();
    $(".mergeConfirm input").remove();
    $("#selectedCustomer1").attr("customerId",G.normalize(parent.idStr));
    $("#selectedCustomer1").attr("customerShopId",G.normalize(parent.customerShopId));
    $("#selectedCustomer2").attr("customerId",child.idStr);
    $("#selectedCustomer2").attr("customerShopId",child.customerShopId);
    $("#parentTable .name").text(G.normalize(parent.name));
    if(parent.isObd) {
        $("#parentTable .name").append($('<span class="customer-obd" title="已安装故障检测仪" style="margin-top:-1px;margin-left:2px;"></span>'));
    }
    $("#parentTable .customerKindStr").text(G.normalize(parent.customerKindStr));
    $("#parentTable .areaStr").text(G.normalize(parent.areaStr));
    $("#parentTable .birthday").text(G.normalize(parent.birthdayString));
    $("#parentTable .contact").text(G.normalize(parent.contact));
    $("#parentTable .address").text(G.normalize(parent.address));
    $("#parentTable .landLine").text(G.normalize(parent.landLine));
    $("#parentTable .mobile").text(G.normalize(parent.mobile));
    $("#parentTable .bank").text(G.normalize(parent.bank));
    $("#parentTable .bankAccountName").text(G.normalize(parent.bankAccountName));
    $("#parentTable .account").text(G.normalize(parent.account));
    $("#parentTable .qq").text(G.normalize(parent.qq));
    $("#parentTable .shortName").text(G.normalize(parent.shortName));
    $("#parentTable .email").text(G.normalize(parent.email));
    $("#parentTable .fax").text(G.normalize(parent.fax));
    $("#parentTable .deposit").text(G.normalize(parent.deposit));
    $("#parentTable .settlementTypeStr").text(G.normalize(parent.settlementTypeStr));
    $("#parentTable .invoiceCategoryStr").text(G.normalize(parent.invoiceCategoryStr));
    if (!parent.relationType || parent.relationType == "UNRELATED") {
        $("#parentTable .relationType").text("非关联");
    }else{
        $("#parentTable .relationType").text("已关联");
    }

    $("#childTable .name").text(G.normalize(child.name));
    if(child.isObd) {
        $("#childTable .name").append($('<span class="customer-obd" title="已安装故障检测仪" style="margin-top:-1px;margin-left:2px;"></span>'));
    }
    $("#childTable .customerKind").text(G.normalize(child.customerKind));
    $("#childTable .birthday").text(G.normalize(child.birthdayString));
    $("#childTable .contact").text(G.normalize(child.contact));
    $("#childTable .address").text(G.normalize(child.address));
    $("#childTable .landLine").text(G.normalize(child.landLine));
    $("#childTable .mobile").text(G.normalize(child.mobile));
    $("#childTable .bank").text(G.normalize(child.bank));
    $("#childTable .bankAccountName").text(G.normalize(child.bankAccountName));   //todo
    $("#childTable .account").text(G.normalize(child.account));
    $("#childTable .qq").text(G.normalize(child.qq));
    $("#childTable .shortName").text(G.normalize(child.shortName));
    $("#childTable .email").text(G.normalize(child.email));
    $("#childTable .fax").text(G.normalize(child.fax));
    $("#childTable .deposit").text(G.normalize(child.deposit));
    $("#childTable .settlementTypeStr").text(G.normalize(child.settlementTypeStr));
    $("#childTable .invoiceCategoryStr").text(G.normalize(child.invoiceCategoryStr));
    if (!child.relationType || child.relationType == "UNRELATED") {
        $("#childTable .relationType").text("非关联");
    }else{
        $("#childTable .relationType").text("已关联");
    }
    var parentRecord=parent.customerRecordDTO;
    var childRecord=child.customerRecordDTO;
    $("#parentTable .totalReceivable").text(dataTransition.rounding(parentRecord.totalReceivable,2));
    $("#parentTable .totalAmount").text(dataTransition.rounding(parentRecord.totalAmount,2));
    $("#parentTable .totalReturnAmount").text(dataTransition.rounding(parentRecord.totalReturnAmount,2));
    $("#parentTable .totalPayable").text(G.normalize(parentRecord.totalPayable));
    $("#parentTable .countCustomerReturn").text(G.normalize(parentRecord.countCustomerReturn));

    $("#childTable .totalReceivable").text(G.normalize(childRecord.totalReceivable));
    $("#childTable .totalAmount").text(G.normalize(childRecord.totalAmount));
    $("#childTable .totalReturnAmount").text(dataTransition.rounding(childRecord.totalReturnAmount,2));
    $("#childTable .totalPayable").text(G.normalize(childRecord.totalPayable));
    $("#childTable .countCustomerReturn").text(G.normalize(childRecord.countCustomerReturn));

    var parentMember=parent.memberDTO;
    var childMember=child.memberDTO;
    var parentMemberServices="";
    var childMemberServices="";
    if(!stringUtil.isEmpty(parentMember)){
        parentMemberServices=parentMember.memberServiceDTOs;
        $("#parentTable .memberNo").text(G.normalize(parentMember.memberNo));
        $("#parentTable .balance").text(dataTransition.rounding(parentMember.balance,2));
        $("#parentTable .joinDateStr").text(G.normalize(parentMember.joinDateStr));
        $("#parentTable .serviceDeadLineStr").text(G.normalize(parentMember.serviceDeadLineStr));
        $("#parentTable .dateKeep").text(G.normalize(parentMember.dateKeep));
        $("#parentTable .memberStatus").text(G.normalize(parentMember.statusStr));
        $("#parentTable .memberConsumeTotal").text(dataTransition.rounding(parentMember.memberConsumeTotal,2));
        $("#parentTable .type").text(G.normalize(parentMember.type));
        if(!stringUtil.isEmpty(parentMemberServices)){
            for(var i=0;i<parentMemberServices.length;i++){
                var $serviceName="#parentTable .serviceName"+String(i);
                var $serviceTime="#parentTable .serviceTime"+String(i);
                var $deadLine="#parentTable .deadLine"+String(i);
                $($serviceName).text(G.normalize(parentMemberServices[i].serviceName));
                $($serviceTime).text(G.normalize(parentMemberServices[i].timesStr));
                $($deadLine).text(G.normalize(parentMemberServices[i].deadlineStr));
            }
        }
    }
    if(!stringUtil.isEmpty(childMember)){
        childMemberServices=childMember.memberServiceDTOs;
        $("#childTable .memberNo").text(G.normalize(childMember.memberNo));
        $("#childTable .balance").text(dataTransition.rounding(childMember.balance,2));
        $("#childTable .joinDateStr").text(G.normalize(childMember.joinDateStr));
        $("#childTable .serviceDeadLineStr").text(G.normalize(childMember.serviceDeadLineStr));
        $("#childTable .dateKeep").text(G.normalize(childMember.dateKeep));
        $("#childTable .memberStatus").text(G.normalize(childMember.statusStr));
        $("#childTable .memberConsumeTotal").text(dataTransition.rounding(childMember.memberConsumeTotal,2));
        $("#childTable .type").text(G.normalize(childMember.type));
        if(!stringUtil.isEmpty(childMemberServices)){
            for(var i=0;i<childMemberServices.length;i++){
                var $serviceName="#childTable .serviceName"+String(i);
                var $serviceTime="#childTable .serviceTime"+String(i);
                var $deadLine="#childTable .deadLine"+String(i);
                $($serviceName).text(G.normalize(childMemberServices[i].serviceName));
                $($serviceTime).text(G.normalize(childMemberServices[i].timesStr));
                $($deadLine).text(G.normalize(childMemberServices[i].deadlineStr));
            }
        }
    }

    var parentVehicles=parent.customerVehicleResponses;
    var childVehicles=child.customerVehicleResponses;
    $("#parentVehicleTable tr").not(".tab_title").remove();
    $("#childVehicleTable tr").not(".tab_title").remove();
    if(!stringUtil.isEmpty(parentVehicles)){
        $("#parentVehicleNum").text(parentVehicles.length);
        for(var i=0;i<parentVehicles.length;i++){
            var parentVehicle=parentVehicles[i];
            var maintainTime=G.normalize(parentVehicle.maintainTimeStr);
            var insureTime=G.normalize(parentVehicle.maintainTimeStr);
            var examineTime=G.normalize(parentVehicle.maintainTimeStr);
            var service="保养"+maintainTime+";"+"保险"+insureTime+";"+"验车"+examineTime+";";
            var brand = G.normalize(parentVehicle.brand);
            var licenceNo = G.normalize(parentVehicle.licenceNo);
            var model = G.normalize(parentVehicle.model);
            var chassisNumber = G.normalize(parentVehicle.vin);
            var year = G.normalize(parentVehicle.year);
            var engine = G.normalize(parentVehicle.engine);
            var carDateStr = G.normalize(parentVehicle.carDateStr);
            var engineNo = G.normalize(parentVehicle.engineNo);
            var startMileage = G.normalize(parentVehicle.startMileage);
            var color = G.normalize(parentVehicle.color);
            var vin = G.normalize(parentVehicle.vin);
            var pTr="<tr>";
            pTr+='<td>'+(i+1)+'</td>'
            pTr+='<td>'+licenceNo+'</td>';
            pTr+='<td>'+brand+'</td>';
            pTr+='<td>'+model+'</td>';
            pTr+='<td>'+year+'</td>';
            pTr+='<td>'+engine+'</td>';
            pTr+='<td>'+chassisNumber+'</td>';
            pTr+='<td>'+engineNo+'</td>';
            pTr+='<td>'+color+'</td>';
            pTr+='<td>'+carDateStr+'</td>';
            pTr+='<td>'+startMileage+'</td>';
            pTr+='<td>'+service+'</td>';
            pTr+='</tr>';
            $("#parentVehicleTable").append(pTr);
        }
    }

    if(!stringUtil.isEmpty(childVehicles)){
        $("#childVehicleNum").text(childVehicles.length);
        for(var i=0;i<childVehicles.length;i++){
            var childVehicle=childVehicles[i];
            var maintainTime=G.normalize(childVehicle.maintainTimeStr);
            var insureTime=G.normalize(childVehicle.maintainTimeStr);
            var examineTime=G.normalize(childVehicle.maintainTimeStr);
            var service="保养"+maintainTime+";"+"保险"+insureTime+";"+"验车"+examineTime+";";
            var brand = G.normalize(childVehicle.brand);
            var licenceNo = G.normalize(childVehicle.licenceNo);
            var model = G.normalize(childVehicle.model);
            var chassisNumber = G.normalize(childVehicle.vin);
            var year = G.normalize(childVehicle.year);
            var engine = G.normalize(childVehicle.engine);
            var carDateStr = G.normalize(childVehicle.carDateStr);
            var engineNo = G.normalize(childVehicle.engineNo);
            var startMileage = G.normalize(childVehicle.startMileage);
            var color = G.normalize(childVehicle.color);
            var vin = G.normalize(childVehicle.vin);
            var pTr="<tr>";
            pTr+='<td>'+(i+1)+'</td>'
            pTr+='<td>'+licenceNo+'</td>';
            pTr+='<td>'+brand+'</td>';
            pTr+='<td>'+model+'</td>';
            pTr+='<td>'+year+'</td>';
            pTr+='<td>'+engine+'</td>';
            pTr+='<td>'+chassisNumber+'</td>';
            pTr+='<td>'+engineNo+'</td>';
            pTr+='<td>'+color+'</td>';
            pTr+='<td>'+carDateStr+'</td>';
            pTr+='<td>'+startMileage+'</td>';
            pTr+='<td>'+service+'</td>';
            pTr+='</tr>';
            $("#childVehicleTable").append(pTr);
        }
    }
    var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" /><input type="button" id="cancelMerge" value="取&nbsp;消" onfocus="this.blur();"  />';
    $("#mergeCustomerDiv").append(okBtns);
    $("div[id^='selectedCustomer']").show();
    doBindHandler();
}

//Deprecated
function mergeRelatedCustomerInfo(json){
    if(json == null) {
        return;
    }
    var customers = json.results;
    $("#mergedCustomerInfo table,#mergedCustomerInfo div").remove();
    for(var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        if(customer == null) {
            continue;
        }
        var idStr = customer.idStr;
        var name = customer.name;
        var contact = customer.contact == null ? "" : customer.contact;
        var mobile = customer.mobile == null ? "" : customer.mobile;
        var address = customer.address == null ? "" : customer.address;
        var landLine = customer.landLine == null ? "" : customer.landLine;
        var customerShopId=customer.customerShopId;
        var memberDTO = customer.memberDTO;
        var customerRecordDTO = customer.customerRecordDTO;
        var memberDTO = customer.memberDTO;
        var vehicleResponses = customer.customerVehicleResponses;

        var cardType = "";
        var memberCardNo = "";
        var serviceDeadLineStr = "";
        var balanceStr = 0;
        var totalReceivable = 0;
        var totalAmount = 0;
        var vehicleSize = 0;
        var isVIP = "";
        var memberServiceDTOs = null;
        if(customerRecordDTO != null && customerRecordDTO) {
            totalReceivable = customerRecordDTO.totalReceivable;
            totalAmount = customerRecordDTO.totalAmount;
        }
        if(memberDTO != null && memberDTO) {
            isVIP = memberDTO.isVIP;
            memberCardNo = memberDTO.memberNo == null ? "" : memberDTO.memberNo;
            balanceStr = memberDTO.balanceStr == null ? "" : memberDTO.balanceStr;
            cardType = memberDTO.type;
            serviceDeadLineStr = memberDTO.serviceDeadLineStr == null ? "" : memberDTO.serviceDeadLineStr;
            memberServiceDTOs = memberDTO.memberServiceDTOs;
        }
        if(vehicleResponses != null && vehicleResponses) {
            vehicleSize = vehicleResponses.length;
        }

        var table='';
        table += '<table cellpadding="0" cellspacing="0" class="fr tabMerge tabMerge' + i + '">';

        table += '<colgroup>';
        table += '<col width="75">';
        table += '<col width="60">';
        table += '<col width="70">';
        table += '<col width="70">';
        table += '<col width="65">';
        table += '<col width="60">';
        table += '<col width="70">';
        table += '<col width="60">';
        table += '<col width="110">';
        table += '</colgroup>';

        table += '<tr class="tab_title">';
        table += '<td colspan="9" class="nameTitle" style="font: solid;">' + name + '</td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title">联系人</td>';
        table += '<td class="customerName">' + contact + '</td>';
        table += '<td class="tab_title">手&nbsp;机</td>';
        table += '<td id="mobile">' + mobile + '</td>';
        table += '<td class="tab_title">会员级别</td>';
        table += '<td class="memberType">' + cardType + '</td>';
        table += '<td><span>项目</span></td><td><span>剩余次数</span></td>';
        table += '<td><span>失效日期</span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title">地&nbsp;址</td>';
        table += '<td class="address">' + address + '</td>';
        table += '<td class="tab_title">座&nbsp;机</td>';
        table += '<td id="landline">' + landLine + '</td>';
        table += '<td class="tab_title">卡&nbsp;号</td>';
        table += '<td class="cardNo">' + memberCardNo + '</td>';
        table += '<td><span class="serviceName0"></span></td>';
        table += '<td><span class="cardTime0"></span></td>';
        table += '<td><span class="deadline0"></span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title"><div class="radNormal"  isParent=false customerShopId="'+customerShopId+'" customerId="' + idStr + '" id="selectedCustomer' + i + '"></div>累计消费</td>';
        table += '<td id="totalAmount">' + totalAmount + '</td>';
        table += '<td class="tab_title">当前挂账</td>';
        table += '<td id="debt">' + totalReceivable + '</td>';
        table += '<td class="tab_title">到&nbsp;期</td>';
        table += '<td>' + serviceDeadLineStr + '</td>';
        table += '<td><span class="serviceName1"></span></td>';
        table += '<td><span class="cardTime1"></span></td>';
        table += '<td><span class="deadline1"></span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td colspan="4" class="tab_cars">拥有车辆数：<span class="qian_red">' + vehicleSize + '</span>&nbsp;辆</td>';
        table += '<td class="tab_title">储值余额</td><td>' + balanceStr + '</td>';
        table += '<td><span class="serviceName2"></span></td>';
        table += '<td><span class="cardTime2"></span></td>';
        table += '<td><span class="deadline2"></span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title">车&nbsp;牌</td>';
        table += '<td class="tab_title" colspan="2">车辆品牌</td>';
        table += '<td class="tab_title">车&nbsp;型</td>';
        table += '<td class="tab_title" colspan="2">车牌号</td>';
        table += '<td class="tab_title" colspan="3">预约服务</td>';
        table += '</tr>';

        if(vehicleResponses != null && vehicleResponses) {
            for(var count = 0; count < vehicleResponses.length; count++) {
                var vehicle = vehicleResponses[count];
                var brand = vehicle.brand == null ? "" : vehicle.brand;
                var licenceNo = vehicle.licenceNo == null ? "" : vehicle.licenceNo;
                var model = vehicle.model == null ? "" : vehicle.model;
                var maintainTimeStr = vehicle.maintainTimeStr;
                var insureTimeStr = vehicle.insureTimeStr;
                var examineTimeStr = vehicle.examineTimeStr;
                if(model && model.length > 10) {
                    modelStr = model.substr(0, 9) + "...";
                } else {
                    modelStr = model;
                }
                table += ' <tr><td>' + licenceNo + '</td><td colspan="2">' + brand + '</td><td title=' + model + '>' + modelStr + '</td><td colspan="2">' + licenceNo + '</td><td>保养：' + maintainTimeStr + '&nbsp;</td><td>|&nbsp;保险：' + insureTimeStr + '&nbsp;</td><td>|&nbsp;验车：' + examineTimeStr + '</td></tr>';
            }
        }
        table += '</table>';
        table += '<div class="height"></div>';
        $("#mergedCustomerInfo").append($(table));
        $("#selectedCustomer0").removeClass().addClass("radCheck");
        $("#selectedCustomer0").closest('.tabMerge').addClass('tabMerge_selected');
        $("#selectedCustomer0").attr("isParent", true);
        showMemberService(i, memberServiceDTOs);

    }
    var okBtns = '<div class="btnClick"><input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" /><input type="button" id="cancelMerge" value="取&nbsp;消" onfocus="this.blur();"  /></div>';
    $("#mergedCustomerInfo").append(okBtns);
    //对生成按钮等绑定
    doBindHandler();
}

//Deprecated
function mergedCustomerInfo(json) {
    if(json == null) {
        return;
    }
    var customers = json.results;
    $("#mergedCustomerInfo table,#mergedCustomerInfo div").remove();
    for(var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        if(customer == null) {
            continue;
        }
        var temp = "";
        var idStr = customer.idStr;
        var name = customer.name;
        var contact = customer.contact == null ? "" : customer.contact;
        var mobile = customer.mobile == null ? "" : customer.mobile;
        var address = customer.address == null ? "" : customer.address;
        var landLine = customer.landLine == null ? "" : customer.landLine;
        var customerShopId=customer.customerShopId;
        var memberDTO = customer.memberDTO;
        var customerRecordDTO = customer.customerRecordDTO;
        var memberDTO = customer.memberDTO;
        var vehicleResponses = customer.customerVehicleResponses;

        var cardType = "";
        var memberCardNo = "";
        var serviceDeadLineStr = "";
        var balanceStr = 0;
        var totalReceivable = 0;
        var totalAmount = 0;
        var vehicleSize = 0;
        var isVIP = "";
        var memberServiceDTOs = null;
        if(customerRecordDTO != null && customerRecordDTO) {
            totalReceivable = customerRecordDTO.totalReceivable;
            totalAmount = customerRecordDTO.totalAmount;
        }
        if(memberDTO != null && memberDTO) {
            isVIP = memberDTO.isVIP;
            memberCardNo = memberDTO.memberNo == null ? "" : memberDTO.memberNo;
            balanceStr = memberDTO.balanceStr == null ? "" : memberDTO.balanceStr;
            cardType = memberDTO.type;
            serviceDeadLineStr = memberDTO.serviceDeadLineStr == null ? "" : memberDTO.serviceDeadLineStr;
            memberServiceDTOs = memberDTO.memberServiceDTOs;
        }
        if(vehicleResponses != null && vehicleResponses) {
            vehicleSize = vehicleResponses.length;
        }

        var table='';
        //    var table= '<input type="hidden" id="mergedCustomerId' + i + '" value="' + idStr + '" name="mergedCustomerId"/></td>';
        table += '<table cellpadding="0" cellspacing="0" class="fr tabMerge tabMerge' + i + '">';

        table += '<colgroup>';
        table += '<col width="75">';
        table += '<col width="60">';
        table += '<col width="70">';
        table += '<col width="70">';
        table += '<col width="65">';
        table += '<col width="60">';
        table += '<col width="70">';
        table += '<col width="60">';
        table += '<col width="110">';
        table += '</colgroup>';

        table += '<tr class="tab_title">';
        table += '<td colspan="9" class="nameTitle" style="font: solid;">' + name + '</td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title">联系人</td>';
        table += '<td class="customerName">' + contact + '</td>';
        table += '<td class="tab_title">手&nbsp;机</td>';
        table += '<td id="mobile">' + mobile + '</td>';
        table += '<td class="tab_title">会员级别</td>';
        table += '<td class="memberType">' + cardType + '</td>';
        table += '<td><span>项目</span></td><td><span>剩余次数</span></td>';
        table += '<td><span>失效日期</span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title">地&nbsp;址</td>';
        table += '<td class="address">' + address + '</td>';
        table += '<td class="tab_title">座&nbsp;机</td>';
        table += '<td id="landline">' + landLine + '</td>';
        table += '<td class="tab_title">卡&nbsp;号</td>';
        table += '<td class="cardNo">' + memberCardNo + '</td>';
        table += '<td><span class="serviceName0"></span></td>';
        table += '<td><span class="cardTime0"></span></td>';
        table += '<td><span class="deadline0"></span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td class="tab_title"><div class="radNormal"  isParent=false customerShopId="'+customerShopId+'" customerId="' + idStr + '" id="selectedCustomer' + i + '"></div>累计消费</td>';
        table += '<td id="totalAmount">' + totalAmount + '</td>';
        table += '<td class="tab_title">当前挂账</td>';
        table += '<td id="debt">' + totalReceivable + '</td>';
        table += '<td class="tab_title">到&nbsp;期</td>';
        table += '<td>' + serviceDeadLineStr + '</td>';
        table += '<td><span class="serviceName1"></span></td>';
        table += '<td><span class="cardTime1"></span></td>';
        table += '<td><span class="deadline1"></span></td>';
        table += '</tr>';

        table += '<tr>';
        table += '<td colspan="4" class="tab_cars">拥有车辆数：<span class="qian_red">' + vehicleSize + '</span>&nbsp;辆</td>';
        table += '<td class="tab_title">储值余额</td><td>' + balanceStr + '</td>';
        table += '<td><span class="serviceName2"></span></td>';
        table += '<td><span class="cardTime2"></span></td>';
        table += '<td><span class="deadline2"></span></td>';
        table += '</tr>';
        // table+='</table>';
        // table+=' <table cellpadding="0" cellspacing="0" class="tabMerge tabMereCus">';
        // table+='  <col width="110"><col width="110"><col width="110"><col width="120"><col width="110"><col width="110"><col width="110">';
        table += '<tr>';
        table += '<td class="tab_title">车&nbsp;牌</td>';
        table += '<td class="tab_title" colspan="2">车辆品牌</td>';
        table += '<td class="tab_title">车&nbsp;型</td>';
        table += '<td class="tab_title" colspan="2">车牌号</td>';
        table += '<td class="tab_title" colspan="3">预约服务</td>';
        table += '</tr>';
        //    <td style="border-left:1px solid #d5d5d5">洗车 剩余次数30次 至 2013-11-12 到期 5</td></tr>
        //    table+='<td><span class="serviceName3"></span></td><td><span class="cardTime3"></span></td><td><span class="deadline3"></span> </td></tr>';
        if(vehicleResponses != null && vehicleResponses) {
            for(var count = 0; count < vehicleResponses.length; count++) {
                var vehicle = vehicleResponses[count];
                var brand = vehicle.brand == null ? "" : vehicle.brand;
                var licenceNo = vehicle.licenceNo == null ? "" : vehicle.licenceNo;
                var model = vehicle.model == null ? "" : vehicle.model;
                var maintainTimeStr = vehicle.maintainTimeStr;
                var insureTimeStr = vehicle.insureTimeStr;
                var examineTimeStr = vehicle.examineTimeStr;
                if(model && model.length > 10) {
                    modelStr = model.substr(0, 9) + "...";
                } else {
                    modelStr = model;
                }
                table += ' <tr><td>' + licenceNo + '</td><td colspan="2">' + brand + '</td><td title=' + model + '>' + modelStr + '</td><td colspan="2">' + licenceNo + '</td><td>保养：' + maintainTimeStr + '&nbsp;</td><td>|&nbsp;保险：' + insureTimeStr + '&nbsp;</td><td>|&nbsp;验车：' + examineTimeStr + '</td></tr>';
                //        table+='<td><span class="serviceName'+(5+count)+'"></span> <span class="cardTime'+(5+count)+'"></span><span class="deadline'+(5+count)+'"></span> </td></tr>';
            }
        }
        table += '</table>';

        table += '<div class="height"></div>';

        $("#mergedCustomerInfo").append($(table));
        $("#selectedCustomer0").removeClass().addClass("radCheck");
        $("#selectedCustomer0").closest('.tabMerge').addClass('tabMerge_selected');
        $("#selectedCustomer0").attr("isParent", true);
        showMemberService(i, memberServiceDTOs);

    }
    var okBtns = '<div class="btnClick"><input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" /><input type="button" id="cancelMerge" value="取&nbsp;消" onfocus="this.blur();"  /></div>';
    $("#mergedCustomerInfo").append(okBtns);
    //对生成按钮等绑定
    doBindHandler();


}

function showMemberService(tableCount, memberServiceDTOs) {
    if(memberServiceDTOs == null) {
        return;
    }
    var serviceLen = memberServiceDTOs.length;
    for(var i = 0; i < serviceLen; i++) {
        var serviceName = memberServiceDTOs[i].serviceName;
        if(serviceName != null && serviceName) {

        }
        var timesStr = memberServiceDTOs[i].timesStr;
        if(timesStr != null && timesStr) {
            if(timesStr != "不限次") timesStr = "剩余" + timesStr + "次  ";
        }
        var deadlineStr = memberServiceDTOs[i].deadlineStr;
        if(deadlineStr != null && deadlineStr) {
            if(deadlineStr != "无限期") {
                deadlineStr = "至 " + deadlineStr + " 到期  ";
            }
        }
        var serviceNameID = ".tabMerge" + tableCount + " .serviceName" + i;
        var cardTimeID = ".tabMerge" + tableCount + " .cardTime" + i;
        var deadlineID = ".tabMerge" + tableCount + " .deadline" + i;
        if($(serviceNameID).length == 1) {
            $(serviceNameID).text(serviceName);
        }
        if($(cardTimeID).length == 1) {
            $(cardTimeID).text(timesStr);
        }
        if($(deadlineID).length == 1) {
            $(deadlineID).text(deadlineStr);
        }
    }

}

function doBindHandler() {

    $("#sureMerge").click(function() {
        $(this).attr("disabled", "disabled");
        var parentIdStr = "";
        var chilIdStrs = "";
        $("div[id^='selectedCustomer']").each(function() {
            if($(this).attr("isParent") == "true") {
                parentIdStr = $(this).attr("customerId");
            } else {
                chilIdStrs += $(this).attr("customerId") + ",";
            }
        });
        chilIdStrs = chilIdStrs.substr(0, chilIdStrs.length - 1);
        if(!parentIdStr || !chilIdStrs) {
            alert("合并客户id不存在");
            return;
        }
        var data = {
            parentIdStr: parentIdStr,
            chilIdStrs: chilIdStrs
        };
        var url = "customer.do?method=mergeCustomerHandler";
        APP_BCGOGO.Net.asyncPost({
            url: url,
            dataType: "json",
            data: data,
            success: function(json) {
                $("#mergeCustomerDetail").dialog("close");
                $("#mergeRelatedCustomerDetail").dialog("close");
                if(json != null && json.success == false) {
                    alert(json.msg);
                } else {
                    nsDialog.jAlert("合并成功，合并客户信息十分钟后生效！");
                }
            },
            error: function(json) {
                nsDialog.jAlert("合并出现异常，合并失败！");
            }
        });
    });

    $("#cancelMerge").click(function() {
        $("#mergeCustomerDetail").dialog("close");
        $("#mergeRelatedCustomerDetail").dialog("close");
    });

    //merge related customer
//  $("div[id^='selectedCustomer']").each(function(){
//    if(!stringUtil.isEmpty($(this).attr("customerShopId"))){
//      $("div[id^='selectedCustomer']").hide();
//      $(this).click();
//    }
//  });

}