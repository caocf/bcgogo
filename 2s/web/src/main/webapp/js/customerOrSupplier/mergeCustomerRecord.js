$().ready(function() {

    $("#operator").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        var obj = this;
        droplistLite.show({
            event: event,
            id: "id",
            name: "name",
            data: "member.do?method=getSaleMans"
        });
    });

  $("#startTimeInput,#endTimeInput").bind("click", function() {
    $(this).blur();
  }).datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearSuffix": "",
        "onClose": function(dateText, inst) {
          if(!$(this).val()) {
            return;
          }
//          if($("#startTimeInput").val() >= $("#endTimeInput").val()) {
//            nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
//            $(this).val(inst.lastVal);
//          }
        },
        "onSelect": function(dateText, inst) {
          if(inst.lastVal == dateText) {
            return;
          }

          $(this).val(dateText);
        }
      });
  $("#searchMergeRecordBtn").click(function(){
    var customerName=$("#customerName").val();
    var operator=$("#operator").val();
    var startTime=$("#startTimeInput").val();
    var endTime=$("#endTimeInput").val();
    if(startTime >= endTime) {
      nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
      return;
    }
    var paramJson = {
      customerOrSupplierName:customerName,
      operator:operator,
      startTimeStr: startTime,
      endTimeStr:endTime
    };
    App.Net.asyncPost({
      url:"customer.do?method=getMergeCustomerRecords",
      data:paramJson,
      cache:false,
      dataType:"json",
      success:function(json){
        initMergeCustomerRecord(json);
        initPages(json, "dynamical1", "customer.do?method=getMergeCustomerRecords", '', "initMergeCustomerRecord", '', '', paramJson, '');
      }
    });
  });

  $("#toCustomerDataBtn").click(function(){
    window.location.href="customer.do?method=customerdata";
  });

});

function initMergeCustomerRecord(json){
  if(stringUtil.isEmpty(json)){
    return;
  }
  $(".mergeRecordTable tr:not(:first)").remove();
  var mergeRecords= json[0].results;
  for(var i = 0; i < mergeRecords.length; i++) {
    var mergeRecord=mergeRecords[i];
    var parent=mergeRecord.parent;
    var parentId=mergeRecord.parentId;
    var child= mergeRecord.child;
    var childId=mergeRecord.childId;
    var mergeTimeStr=mergeRecord.mergeTimeStr;
    var operator=G.normalize(mergeRecord.operator);

    var trStr='<tr class="dataTr">';
    trStr += '<td>' + mergeTimeStr + '</td>';
    trStr += '<td>' + operator + '</td>';
    if(mergeRecord.parentStatus=="DISABLED"){
      trStr += '<td>' + '<a class="blue_col" href ="#"  onclick="showMergedCustomers(\'' + parentId + '\',\'' + childId + '\',\'' + false + '\')">' + parent + '</a> ' + '</td>';
    }else{
      trStr += '<td>' + '<a class="blue_col" href ="#"  onclick="toUncleUser(\'' + parentId + '\')">' + parent + '</a> ' + '</td>';
    }
    trStr += '<td>' + parent +'&nbsp与&nbsp'+child+'&nbsp合并'+ '</td>';
    trStr += '<td>' + '<a class="blue_col" href ="#" onclick="showMergedCustomers(\'' + parentId + '\',\'' + childId + '\',\'' + true + '\')">' + '查看合并客户' + '</a> ' + '</td> ';
    trStr+='</tr>';
    $(".mergeRecordTable").append(trStr);
  }
  $("#totalMergeRecord").text(G.normalize(json[0].totalRows));
  tableUtil.tableStyle('.mergeRecordTable','.tab_title');

}

function toUncleUser(customerId){
  window.open('unitlink.do?method=customer&customerId='+customerId);
}

function showMergedCustomers(parentId,childId,isShowChild){
  if(stringUtil.isEmpty(parentId)||stringUtil.isEmpty(childId)){
    return;
  }
  App.Net.asyncPost({
    url:"customer.do?method=getMergeCustomerSnap",
    data:{parentIdStr:parentId,childIdStr:childId},
    cache:false,
    dataType:"json",
    success:function(jsonStr){
      var mergeRecord=jsonStr.results[0];
      if(stringUtil.isEmpty(mergeRecord)){
        return;
      }
      var mergeSnap=JSON.parse(mergeRecord.mergeSnap);
      if(jsonStr.mergeRelatedFlag){
        $("#mergeRelatedCustomerDetail").dialog({
          resizable: true,
          title: "合并客户详细信息",
          width: 1000,
          height: 520,
          modal: true,
          closeOnEscape: false
        });
        intRelatedCustomerRecordDetail(mergeSnap,isShowChild);
      }else{
        $("#mergeCustomerDetail").dialog({
          resizable: true,
          title: "合并客户详细信息",
          width: 1000,
          height: 520,
          modal: true,
          closeOnEscape: false
        });
        intCustomerRecordDetail(mergeSnap,isShowChild);
      }
      $(".mergeBefore").hide();
      $(".mergeAfter").show();
    }
  });
}

function intRelatedCustomerRecordDetail(mergeSnap,isShowChild){
  var parent=mergeSnap.parent;
  var child=mergeSnap.child;
  if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
    return;
  }
  $(".mergeConfirm input").remove();
    if (!parent.relationType || parent.relationType == "UNRELATED") {
        $("#relateParentTable .name").html(G.normalize(parent.name) + "<span class='yellow_color'>(非关联)</span>");
    } else {
        $("#relateParentTable .name").html(G.normalize(parent.name) + "<span class='yellow_color'>(关联)</span>");
    }
  //$("#relateParentTable .name").text(G.normalize(parent.name));
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
  $("#relateParentTable .areaStr").text(G.normalize(parent.areaStr));
  $("#relateParentTable .customerKindStr").text(G.normalize(parent.customerKindStr));
  $("#relateParentTable .settlementTypeStr").text(G.normalize(parent.settlementTypeStr));
  $("#relateParentTable .invoiceCategoryStr").text(G.normalize(parent.invoiceCategoryStr));
  $("#relateParentTable .deposit").text(G.normalize(parent.deposit)); // add by zhuj

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
    if ($contacts) {
        $($contacts).insertAfter($("#relateParentTable tr").eq(1));
    }


  //$("#relateChildTable .name").text(G.normalize(child.name));
    if (!child.relationType || child.relationType == "UNRELATED") {
        $("#relateChildTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(非关联)</span>");
    } else {
        $("#relateChildTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(关联)</span>");
    }
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
  $("#relateChildTable .areaStr").text(G.normalize(child.areaStr));
  $("#relateChildTable .customerKindStr").text(G.normalize(child.customerKindStr));
  $("#relateChildTable .settlementTypeStr").text(G.normalize(child.settlementTypeStr));
  $("#relateChildTable .invoiceCategoryStr").text(G.normalize(child.invoiceCategoryStr));
  $("#relateChildTable .deposit").text(G.normalize(child.deposit));

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
    if ($childContacts) {
        $($childContacts).insertAfter($("#relateChildTable tr").eq(1));
    }

  var parentRecord=mergeSnap.parentRecord;
  var childRecord=mergeSnap.childRecord;
  $("#relateParentTable .totalReceivable").text(dataTransition.rounding(parentRecord.totalReceivable,2)+"\\"+G.normalize(parentRecord.totalDebt));
  $("#relateParentTable .totalAmount").text(dataTransition.rounding(parentRecord.totalAmount,2));
  $("#relateParentTable .totalReturnAmount").text(dataTransition.rounding(parentRecord.totalReturnAmount,2));
  //$("#relateParentTable .totalDebt").text(G.normalize(parentRecord.totalDebt));
  $("#relateParentTable .countCustomerReturn").text(G.normalize(parentRecord.countCustomerReturn));

  $("#relateChildTable .totalReceivable").text(G.normalize(childRecord.totalReceivable)+"\\"+G.normalize(childRecord.totalDebt));
  $("#relateChildTable .totalAmount").text(G.normalize(childRecord.totalAmount));
  $("#relateChildTable .totalReturnAmount").text(dataTransition.rounding(childRecord.totalReturnAmount,2));
  //$("#relateChildTable .totalDebt").text(G.normalize(childRecord.totalDebt));
  $("#relateChildTable .countCustomerReturn").text(G.normalize(childRecord.countCustomerReturn));

  if(isShowChild=="false"){
    $("#childBorder table").hide();
  }else{
    $("#childBorder table").show();
  }
  var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" />';
  $("#mergeRelateDiv").append(okBtns);
  doBindHandler();
}

function doBindHandler(){
  $("#sureMerge").click(function() {
    $("#mergeCustomerDetail").dialog("close");
    $("#mergeRelatedCustomerDetail").dialog("close");
  });
}


function intCustomerRecordDetail(mergeSnap,isShowChild){
  var parent=mergeSnap.parent;;
  var child=mergeSnap.child;
  if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
    return;
  }
  $(".mergeConfirm input").remove();
  $("#parentTable .name").text(G.normalize(parent.name));
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
  $("#parentTable .settlementTypeStr").text(G.normalize(parent.settlementTypeStr));
  $("#parentTable .invoiceCategoryStr").text(G.normalize(parent.invoiceCategoryStr));

  $("#childTable .name").text(G.normalize(child.name));
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
  $("#childTable .settlementTypeStr").text(G.normalize(child.settlementTypeStr));
  $("#childTable .invoiceCategoryStr").text(G.normalize(child.invoiceCategoryStr));

  var parentRecord=mergeSnap.parentRecord;
  var childRecord=mergeSnap.childRecord;
  $("#parentTable .totalReceivable").text(dataTransition.rounding(parentRecord.totalReceivable,2));
  $("#parentTable .totalAmount").text(dataTransition.rounding(parentRecord.totalAmount,2));
  $("#parentTable .totalReturnAmount").text(dataTransition.rounding(parentRecord.totalReturnAmount,2));
  $("#parentTable .totalDebt").text(G.normalize(parentRecord.totalDebt));
  $("#parentTable .countCustomerReturn").text(G.normalize(parentRecord.countCustomerReturn));

  $("#childTable .totalReceivable").text(G.normalize(childRecord.totalReceivable));
  $("#childTable .totalAmount").text(G.normalize(childRecord.totalAmount));
  $("#childTable .totalReturnAmount").text(dataTransition.rounding(childRecord.totalReturnAmount,2));
  $("#childTable .totalDebt").text(G.normalize(childRecord.totalDebt));
  $("#childTable .countCustomerReturn").text(G.normalize(childRecord.countCustomerReturn));

  var parentMember=mergeSnap.parentMember;
  var childMember=mergeSnap.childMember;
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





  var parentVehicles=mergeSnap.parentVehicles;
  var childVehicles=mergeSnap.childVehicles;
  $("#parentVehicleTable tr:gt(1)").remove();
  $("#childVehicleTable tr:gt(1)").remove();
  if(!stringUtil.isEmpty(parentVehicles)){
    $("#parentVehicleNum").text(parentVehicles.length);
    for(var i=0;i<parentVehicles.length;i++){
      var maintainTime=G.normalize(parentVehicles[i].maintainTimeStr);
      var insureTime=G.normalize(parentVehicles[i].maintainTimeStr);
      var examineTime=G.normalize(parentVehicles[i].maintainTimeStr);
      var service="保养"+maintainTime+";"+"保险"+insureTime+";"+"验车"+examineTime+";";
      var vehicle=parentVehicles[i].vehicleDTO;

      var brand = G.normalize(vehicle.brand);
      var licenceNo = G.normalize(vehicle.licenceNo);
      var model = G.normalize(vehicle.model);
      var chassisNumber = G.normalize(vehicle.chassisNumber);
      var year = G.normalize(vehicle.year);
      var engine = G.normalize(vehicle.engine);
      var carDateStr = G.normalize(vehicle.carDateStr);
      var engineNo = G.normalize(vehicle.engineNo);
      var color = G.normalize(vehicle.color);
      var startMileage = G.normalize(vehicle.startMileage);
      var vin = G.normalize(vehicle.vin);
      var brand = G.normalize(vehicle.brand);
      var brand = G.normalize(vehicle.brand);
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
  }else{
      $("#parentVehicleNum").text("0");
  }

  if(!stringUtil.isEmpty(childVehicles)){
    $("#childVehicleNum").text(childVehicles.length);
    for(var i=0;i<childVehicles.length;i++){
      var maintainTime=G.normalize(childVehicles[i].maintainTimeStr);
      var insureTime=G.normalize(childVehicles[i].maintainTimeStr);
      var examineTime=G.normalize(childVehicles[i].maintainTimeStr);
      var service="保养"+maintainTime+";"+"保险"+insureTime+";"+"验车"+examineTime+";";
      var vehicle=childVehicles[i].vehicleDTO;

      var brand = G.normalize(vehicle.brand);
      var licenceNo = G.normalize(vehicle.licenceNo);
      var model = G.normalize(vehicle.model);
      var chassisNumber = G.normalize(vehicle.chassisNumber);
      var year = G.normalize(vehicle.year);
      var engine = G.normalize(vehicle.engine);
      var carDateStr = G.normalize(vehicle.carDateStr);
      var engineNo = G.normalize(vehicle.engineNo);
      var startMileage = G.normalize(vehicle.startMileage);
      var vin = G.normalize(vehicle.vin);
      var brand = G.normalize(vehicle.brand);
      var brand = G.normalize(vehicle.brand);
      var color = G.normalize(vehicle.color);
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
  }else{
      $("#childVehicleNum").text("0");
  }
  if(isShowChild=="false"){
    $("#childBorder table").hide();
  }else{
    $("#childBorder table").show();
  }
  var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" />';
  $("#mergeCustomerDiv").append(okBtns);
  doBindHandler();
}