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
    var supplierName=$("#supplierName").val();
    var operator=$("#operator").val();
    var startTime=$("#startTimeInput").val();
    var endTime=$("#endTimeInput").val();
    if(startTime >= endTime) {
      nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
      return;
    }
    var paramJson = {
      customerOrSupplierName:supplierName,
      operator:operator,
      startTimeStr: startTime,
      endTimeStr:endTime
    };
    App.Net.asyncPost({
      url:"supplier.do?method=getSupplierMergeRecords",
      data:paramJson,
      cache:false,
      dataType:"json",
      success:function(json){
        initMergeSupplierRecord(json);
        initPages(json, "dynamical1", "supplier.do?method=getSupplierMergeRecords", '', "initMergeSupplierRecord", '', '', paramJson, '');
      }
    });
  });

  $("#toSupplierDataBtn").click(function(){
    window.location.href="customer.do?method=searchSuppiler";
  });

});

function initMergeSupplierRecord(json){
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
    var operator=mergeRecord.operator;

    var trStr='<tr class="dataTr">';
    trStr += '<td>' + mergeTimeStr + '</td>';
    trStr += '<td>' + operator + '</td>';
    if(mergeRecord.parentStatus=="DISABLED"){
      trStr += '<td>' + '<a href ="#" style="color: #0094FF;" onclick="showMergedSuppliers(\'' + parentId + '\',\'' + childId + '\',\'' + false + '\')">' + parent + '</a> ' + '</td>';
    }else{
      trStr += '<td>' + '<a href ="#" style="color: #0094FF;" onclick="toUncleSupplier(\'' + parentId + '\')">' + parent + '</a> ' + '</td>';
    }
    trStr += '<td>' + parent +'&nbsp与&nbsp'+child+'&nbsp合并'+ '</td>';
    trStr += '<td>' + '<a href ="#" style="color: #0094FF;" onclick="showMergedSuppliers(\'' + parentId + '\',\'' + childId + '\',\'' + true + '\')">' + '查看合并供应商' + '</a> ' + '</td> ';
    trStr+='</tr>';
    $(".mergeRecordTable").append(trStr);
  }
  $("#totalMergeRecord").text(G.normalize(json[0].totalRows));
  tableUtil.tableStyle('.mergeRecordTable','.tab_title');

}

function toUncleSupplier(supplierId){
  window.open('unitlink.do?method=supplier&supplierId='+supplierId);
}

function showMergedSuppliers(parentId,childId,isShowChild){
  if(stringUtil.isEmpty(parentId)||stringUtil.isEmpty(childId)){
    return;
  }
  App.Net.asyncPost({
    url:"supplier.do?method=getMergeSupplierSnap",
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
        $("#mergeWholesalerDetail").dialog({
          resizable: true,
          title: "合并供应商详细信息",
          width: 950,
          height: 520,
          modal: true,
          closeOnEscape: false
        });
        initMergeWholesalerRecordDetail(mergeSnap,isShowChild);
      }else{
        $("#mergeSupplierDetail").dialog({
          resizable: true,
          title: "合并供应商详细信息",
          width: 950,
          height: 520,
          modal: true,
          closeOnEscape: false
        });
        initSupplierRecordDetail(mergeSnap,isShowChild);
      }
      $(".mergeBefore").hide();
      $(".mergeAfter").show();
    }
  });
}

function initMergeWholesalerRecordDetail(mergeSnap,isShowChild){
  var parent=mergeSnap.parent;
  var child=mergeSnap.child;
  if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
    return;
  }
  $(".mergeConfirm input").remove();
    if(!parent.relationType || parent.relationType == "UNRELATED"){
        $("#wholeSaleParentTable .name").html(G.normalize(parent.name) + "<span class='yellow_color'>(非关联)</span>");
    }else{
        $("#wholeSaleParentTable .name").html(G.normalize(parent.name)+ "<span class='yellow_color'>(关联)</span>");
    }
  //$("#wholeSaleParentTable .relationType").text(G.normalize(parent.relationTypeStr));
  //$("#wholeSaleParentTable .name").text(G.normalize(parent.name));
  $("#wholeSaleParentTable .contact").text(G.normalize(parent.contact));
  $("#wholeSaleParentTable .abbr").text(G.normalize(parent.abbr));
  $("#wholeSaleParentTable .categoryStr").text(G.normalize(parent.categoryStr));
  $("#wholeSaleParentTable .areaStr").text(G.normalize(parent.areaStr));
  $("#wholeSaleParentTable .birthday").text(G.normalize(parent.birthdayString));
  $("#wholeSaleParentTable .address").text(G.normalize(parent.address));
  $("#wholeSaleParentTable .landLine").text(G.normalize(parent.landLine));
  $("#wholeSaleParentTable .mobile").text(G.normalize(parent.mobile));
  $("#wholeSaleParentTable .bank").text(G.normalize(parent.bank));
  $("#wholeSaleParentTable .accountName").text(G.normalize(parent.accountName));
  $("#wholeSaleParentTable .account").text(G.normalize(parent.account));
  $("#wholeSaleParentTable .qq").text(G.normalize(parent.qq));
  $("#wholeSaleParentTable .abbr").text(G.normalize(parent.abbr));
  $("#wholeSaleParentTable .email").text(G.normalize(parent.email));
  $("#wholeSaleParentTable .fax").text(G.normalize(parent.fax));
  $("#wholeSaleParentTable .settlementType").text(G.normalize(parent.settlementType));
  $("#wholeSaleParentTable .invoiceCategory").text(G.normalize(parent.invoiceCategory));
  $("#wholeSaleParentTable .categoryStr").text(G.normalize(parent.categoryStr));
  $("#wholeSaleParentTable .settlementType").text(G.normalize(parent.settlementType));
  $("#wholeSaleParentTable .categoryStr").text(G.normalize(parent.categoryStr));
  $("#wholeSaleParentTable .businessScope").text(G.normalize(parent.businessScope));
  $("#wholeSaleParentTable .account").text(G.normalize(parent.account));
  $("#wholeSaleParentTable .deposit").text(dataTransition.rounding(parent.deposit,2));
  $("#wholeSaleParentTable .totalPayable").text(dataTransition.rounding(parent.totalPayable,2));
  $("#wholeSaleParentTable .totalTradeAmount").text(dataTransition.rounding(parent.totalTradeAmount,2));
  $("#wholeSaleParentTable .totalReturnAmount").text(dataTransition.rounding(parent.totalReturnAmount,2));
  $("#wholeSaleParentTable .countSupplierReturn").text(G.normalize(parent.countSupplierReturn));
  $("#wholeSaleParentTable .totalReceivable").text(dataTransition.rounding(parent.totalReceivable,2));

    var parentContacts = parent.contacts;
    var $contacts = "";
    if (!G.isEmpty(parentContacts) && !G.isEmpty(parentContacts.length)) {
        for (var i = 0; i < parentContacts.length; i++) {
            if (isValidContact(parentContacts[i])) { // 引入contact.js
                $contacts += "<tr class='single_contact'>" +
                    "<td class='tab_title'>联系人</td><td id='name'>" + G.normalize(parentContacts[i].name) + "</td>" +
                    "<td class='tab_title'>手机</td><td id='mobile'>" +G.normalize(parentContacts[i].mobile) + "</td>" +
                    "<td class='tab_title'>QQ</td><td id='qq'>" + G.normalize(parentContacts[i].qq) + "</td>" +
                    "<td class='tab_title'>Email</td><td id='email'>" + G.normalize(parentContacts[i].email) + "</td>" +
                    "</tr>"
            }
        }
    }
    if($contacts){
        $($contacts).insertAfter($("#wholeSaleParentTable tr").eq(1));
    }


  if (!child.relationType || child.relationType == "UNRELATED") {
      $("#wholeChildTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(非关联)</span>");
  }else{
      $("#wholeChildTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(关联)</span>");
  }
  //$("#wholeChildTable .name").text(G.normalize(child.name));
  $("#wholeChildTable .contact").text(G.normalize(child.contact));
  $("#wholeChildTable .abbr").text(G.normalize(child.abbr));
  $("#wholeChildTable .categoryStr").text(G.normalize(child.categoryStr));
  $("#wholeChildTable .areaStr").text(G.normalize(child.areaStr));
  $("#wholeChildTable .birthday").text(G.normalize(child.birthdayString));
  $("#wholeChildTable .address").text(G.normalize(child.address));
  $("#wholeChildTable .landLine").text(G.normalize(child.landLine));
  $("#wholeChildTable .mobile").text(G.normalize(child.mobile));
  $("#wholeChildTable .bank").text(G.normalize(child.bank));
  $("#wholeChildTable .accountName").text(G.normalize(child.accountName));
  $("#wholeChildTable .account").text(G.normalize(child.account));
  $("#wholeChildTable .qq").text(G.normalize(child.qq));
  $("#wholeChildTable .abbr").text(G.normalize(child.abbr));
  $("#wholeChildTable .email").text(G.normalize(child.email));
  $("#wholeChildTable .fax").text(G.normalize(child.fax));
  $("#wholeChildTable .settlementType").text(G.normalize(child.settlementType));
  $("#wholeChildTable .invoiceCategory").text(G.normalize(child.invoiceCategory));
  $("#wholeChildTable .settlementType").text(G.normalize(child.settlementType));
  $("#wholeChildTable .categoryStr").text(G.normalize(child.categoryStr));
  $("#wholeChildTable .businessScope").text(G.normalize(child.businessScope));
  $("#wholeChildTable .account").text(G.normalize(child.account));
  $("#wholeChildTable .deposit").text(dataTransition.rounding(child.deposit,2));
  $("#wholeChildTable .totalPayable").text(dataTransition.rounding(child.totalPayable,2));
  $("#wholeChildTable .totalTradeAmount").text(dataTransition.rounding(child.totalTradeAmount,2));
  $("#wholeChildTable .totalReturnAmount").text(dataTransition.rounding(child.totalReturnAmount,2));
  $("#wholeChildTable .countSupplierReturn").text(G.normalize(child.countSupplierReturn));
  $("#wholeChildTable .totalReceivable").text(dataTransition.rounding(child.totalReceivable,2));
  $("#wholeChildTable .relationType").text(G.normalize(child.relationTypeStr));

    $("#wholeChildTable .single_contact").remove();
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
        $($childContacts).insertAfter($("#wholeChildTable tr").eq(1));
    }

  if(isShowChild=="false"){
    $("#childBorder table").hide();
  }else{
    $("#childBorder table").show();
  }
  var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" />';
  $("#mergeWholesalerDiv").append(okBtns);
  doBindHandler();
}

function doBindHandler(){
  $("#sureMerge").click(function() {
    $("#mergeSupplierDetail").dialog("close");
    $("#mergeWholesalerDetail").dialog("close");
  });
}


function initSupplierRecordDetail(mergeSnap,isShowChild){
  var parent=mergeSnap.parent;
  var child=mergeSnap.child;
  if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
    return;
  }
  $(".mergeConfirm input").remove();
  $("#parentTable .relationType").text(G.normalize(parent.relationTypeStr));
  $("#selectedSupplier1").attr("supplierId",G.normalize(parent.idStr));
  $("#selectedSupplier1").attr("supplierShopId",G.normalize(parent.supplierShopId));
  $("#selectedSupplier2").attr("supplierId",child.idStr);
  $("#selectedSupplier2").attr("supplierShopId",child.supplierShopId);

    if(!parent.relationType || parent.relationType == "UNRELATED"){
        $("#parentTable .name").html(G.normalize(parent.name) + "<span class='yellow_color'>(非关联)</span>");
    }else{
        $("#parentTable .name").html(G.normalize(parent.name)+ "<span class='yellow_color'>(关联)</span>");
    }

  //$("#parentTable .name").text(G.normalize(parent.name));
  $("#parentTable .contact").text(G.normalize(parent.contact));
  $("#parentTable .abbr").text(G.normalize(parent.abbr));
  $("#parentTable .categoryStr").text(G.normalize(parent.categoryStr));
  $("#parentTable .areaStr").text(G.normalize(parent.areaStr));
  $("#parentTable .birthday").text(G.normalize(parent.birthdayString));
  $("#parentTable .address").text(G.normalize(parent.address));
  $("#parentTable .landLine").text(G.normalize(parent.landLine));
  $("#parentTable .mobile").text(G.normalize(parent.mobile));
  $("#parentTable .bank").text(G.normalize(parent.bank));
  $("#parentTable .accountName").text(G.normalize(parent.accountName));
  $("#parentTable .account").text(G.normalize(parent.account));
  $("#parentTable .qq").text(G.normalize(parent.qq));
  $("#parentTable .abbr").text(G.normalize(parent.abbr));
  $("#parentTable .email").text(G.normalize(parent.email));
  $("#parentTable .fax").text(G.normalize(parent.fax));
  $("#parentTable .settlementType").text(G.normalize(parent.settlementType));
  $("#parentTable .invoiceCategory").text(G.normalize(parent.invoiceCategory));
  $("#parentTable .categoryStr").text(G.normalize(parent.categoryStr));
  $("#parentTable .settlementType").text(G.normalize(parent.settlementType));
  $("#parentTable .categoryStr").text(G.normalize(parent.categoryStr));
  $("#parentTable .businessScope").text(G.normalize(parent.businessScope));
  $("#parentTable .account").text(G.normalize(parent.account));
  $("#parentTable .deposit").text(dataTransition.rounding(parent.deposit,2));
  $("#parentTable .totalPayable").text(dataTransition.rounding(parent.totalPayable,2));
  $("#parentTable .totalTradeAmount").text(dataTransition.rounding(parent.totalTradeAmount,2));
  $("#parentTable .totalReturnAmount").text(dataTransition.rounding(parent.totalReturnAmount,2));
  $("#parentTable .countSupplierReturn").text(G.normalize(parent.countSupplierReturn));
  $("#parentTable .totalReceivable").text(dataTransition.rounding(parent.totalReceivable,2));

    $("#parentTable .single_contact").remove();
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
        $($contacts).insertAfter($("#parentTable tr").eq(1));
    }


    if (!child.relationType || child.relationType == "UNRELATED") {
        $("#childTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(非关联)</span>");
    }else{
        $("#childTable .name").html(G.normalize(child.name) + "<span class='yellow_color'>(关联)</span>");
    }


  //$("#childTable .name").text(G.normalize(child.name));
  $("#childTable .contact").text(G.normalize(child.contact));
  $("#childTable .abbr").text(G.normalize(child.abbr));
  $("#childTable .categoryStr").text(G.normalize(child.categoryStr));
  $("#childTable .areaStr").text(G.normalize(child.areaStr));
  $("#childTable .birthday").text(G.normalize(child.birthdayString));
  $("#childTable .address").text(G.normalize(child.address));
  $("#childTable .landLine").text(G.normalize(child.landLine));
  $("#childTable .mobile").text(G.normalize(child.mobile));
  $("#childTable .bank").text(G.normalize(child.bank));
  $("#childTable .accountName").text(G.normalize(child.accountName));
  $("#childTable .account").text(G.normalize(child.account));
  $("#childTable .qq").text(G.normalize(child.qq));
  $("#childTable .abbr").text(G.normalize(child.abbr));
  $("#childTable .email").text(G.normalize(child.email));
  $("#childTable .fax").text(G.normalize(child.fax));
  $("#childTable .settlementType").text(G.normalize(child.settlementType));
  $("#childTable .invoiceCategory").text(G.normalize(child.invoiceCategory));
  $("#childTable .settlementType").text(G.normalize(child.settlementType));
  $("#childTable .categoryStr").text(G.normalize(child.categoryStr));
  $("#childTable .businessScope").text(G.normalize(child.businessScope));
  $("#childTable .account").text(G.normalize(child.account));
  $("#childTable .deposit").text(dataTransition.rounding(child.deposit,2));
  $("#childTable .totalPayable").text(dataTransition.rounding(child.totalPayable,2));
  $("#childTable .totalTradeAmount").text(dataTransition.rounding(child.totalTradeAmount,2));
  $("#childTable .totalReturnAmount").text(dataTransition.rounding(child.totalReturnAmount,2));
  $("#childTable .countSupplierReturn").text(G.normalize(child.countSupplierReturn));
  $("#childTable .totalReceivable").text(dataTransition.rounding(child.totalReceivable,2));
  $("#childTable .relationType").text(G.normalize(child.relationTypeStr));

    $("#childTable .single_contact").remove();
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
        $($childContacts).insertAfter($("#childTable tr").eq(1));
    }

  if(isShowChild=="false"){
    $("#childBorder table").hide();
  }else{
    $("#childBorder table").show();
  }
  var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" />';
  $("#mergeSupplierDiv").append(okBtns);
  doBindHandler();
}