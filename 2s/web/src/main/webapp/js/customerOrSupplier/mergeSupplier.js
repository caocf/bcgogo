$(document).ready(function() {

    $("#mergeSupplierBtn").click(function() {
        var selectedSize = 0;
        var supplierIds = "";
        var wholesalerSize=0;
        $("#selectedIdArray input").each(function() {
            if($(this).val()) {
                selectedSize++;
                supplierIds += $(this).val() + ",";
            }
            if(!stringUtil.isEmpty($(this).attr("supplierShopId"))) {
                wholesalerSize++;
            }
        });
        supplierIds = supplierIds.substr(0, supplierIds.length - 1);
        if(selectedSize != 2) {
            nsDialog.jAlert("请选择两个要合并的供应商！");
            return;
        }
        if(wholesalerSize==2){
            nsDialog.jAlert("两个关联供应商无法合并！");
            return;
        }

        $("<div id='mergeConfirmDiv' >友情提示：供应商合并后，将只保留一个供应商的信息，其他不保留供应商的定金信息、应付款信息将转移到保留供应商上。合并操作无法撤消，请确认</div>").dialog({
            resizable: true,
            title:"合并确认",
            height:150,
            width:500,
            modal: true,
            closeOnEscape: false,
            buttons:{
                "确定":function(){
                    $(this).dialog("close");
                    getMergedSupplier(supplierIds);
                },
                "取消":function(){
                    $(this).dialog("close");
                }
            }
        });
    });

    $("div[id^='selectedSupplier']").click(function(event) {
        $("div[id^='selectedSupplier']").removeClass("radCheck").addClass("radNormal");
        $("div[id^='selectedSupplier']").attr("isParent", false);
        $('.abMerge').removeClass('tabMerge_selected');
        var targetDiv= $(event.target).closest('.border');
        $(targetDiv).find('table').addClass('tabMerge_selected');
        $(this).removeClass().addClass("radCheck");
        $(this).attr("isParent", true);
    });

});

function getMergedSupplier(supplierIds){
    var data = {
        supplierIds: supplierIds
    };
    var url = "supplier.do?method=getMergedSuppliers";
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
                $("#mergeWholesalerDetail").dialog({
                    resizable: true,
                    title: "合并供应商",
                    width: 950,
                    height: 520,
                    modal: true,
                    closeOnEscape: false
                });
                initMergeWholesalerInfo(json);
            }else{
                $("#mergeSupplierDetail").dialog({
                    resizable: true,
                    title: "合并供应商",
                    width: 950,
                    height: 520,
                    modal: true,
                    closeOnEscape: false
                });
                initMergeSupplierInfo(json);
            }
            $(".mergeBefore").show();
            $(".mergeAfter").hide();
        },
        error: function(json) {
            nsDialog.jAlert("获取供应商信息异常！");
        }
    });
}

function initMergeWholesalerInfo(json){
    var parent=json.parent;
    var child=json.child;
    if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
        return;
    }
    $(".mergeConfirm input").remove();
    $(".mSupplierDiv").remove();
    $("#selectedSupplier1").attr("supplierId",G.normalize(parent.idStr));
    $("#selectedSupplier1").attr("supplierShopId",G.normalize(parent.supplierShopId));
    $("#selectedSupplier2").attr("supplierId",child.idStr);
    $("#selectedSupplier2").attr("supplierShopId",G.normalize(child.supplierShopId));

   //$("#parentTable .name").text(G.normalize(parent.name));
    if(!parent.relationType || parent.relationType == "UNRELATED"){
        $("#wholeSaleParentTable .name").html(G.normalize(parent.name) + "<span class='yellow_color'>(非关联)</span>");
    }else{
        $("#wholeSaleParentTable .name").html(G.normalize(parent.name)+ "<span class='yellow_color'>(关联)</span>");
    }
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
    $("#wholeSaleParentTable .settlementType").text(G.normalize(parent.settlementType));
    $("#wholeSaleParentTable .categoryStr").text(G.normalize(parent.categoryStr));
    $("#wholeSaleParentTable .businessScope").text(G.normalize(parent.businessScope));
    $("#wholeSaleParentTable .account").text(G.normalize(parent.account));
    $("#wholeSaleParentTable .deposit").text(dataTransition.rounding(parent.deposit,2));
    $("#wholeSaleParentTable .totalPayable").text(dataTransition.rounding(parent.totalPayable,2));
    $("#wholeSaleParentTable .totalReceivable").text(dataTransition.rounding(parent.totalReceivable,2));
    $("#wholeSaleParentTable .totalTradeAmount").text(dataTransition.rounding(parent.totalTradeAmount,2));
    $("#wholeSaleParentTable .totalReturnAmount").text(dataTransition.rounding(parent.totalReturnAmount,2));
    $("#wholeSaleParentTable .countSupplierReturn").text(G.normalize(parent.countSupplierReturn));
    $("#wholeSaleParentTable .single_contact").remove();
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
    $("#wholeChildTable .totalReceivable").text(dataTransition.rounding(child.totalReceivable,2));
    $("#wholeChildTable .totalTradeAmount").text(dataTransition.rounding(child.totalTradeAmount,2));
    $("#wholeChildTable .totalReturnAmount").text(dataTransition.rounding(child.totalReturnAmount,2));
    $("#wholeChildTable .countSupplierReturn").text(G.normalize(child.countSupplierReturn));

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

//
//    $("#selectedSupplier0").removeClass().addClass("radCheck");
//    $("#selectedSupplier0").closest('.tabMerge').addClass('tabMerge_selected');
//    $("#selectedSupplier0").attr("isParent", true);
    var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" /><input type="button" id="cancelMerge" value="取&nbsp;消" onfocus="this.blur();"  />';
    $("#mergeWholesalerDiv").append(okBtns);
    $("div[id^='selectedSupplier']").show();
    doBindHandler();
}

function initMergeSupplierInfo(json){
    var parent=json.parent;
    var child=json.child;
    if(stringUtil.isEmpty(parent)||stringUtil.isEmpty(child)){
        return;
    }
    $(".mWholesalesDiv").remove();
    $(".mergeConfirm input").remove();
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
    $("#parentTable .businessScope").text(G.normalize(parent.businessScope));
    $("#parentTable .account").text(G.normalize(parent.account));
    $("#parentTable .deposit").text(dataTransition.rounding(parent.deposit,2));
    $("#parentTable .totalPayable").text(dataTransition.rounding(parent.totalPayable,2));
    $("#parentTable .totalReceivable").text(dataTransition.rounding(parent.totalReceivable,2));
    $("#parentTable .totalTradeAmount").text(dataTransition.rounding(parent.totalTradeAmount,2));
    $("#parentTable .totalReturnAmount").text(dataTransition.rounding(parent.totalReturnAmount,2));
    $("#parentTable .countSupplierReturn").text(G.normalize(parent.countSupplierReturn));

    $("#parentTable .single_contact").remove();
    var parentContacts = parent.contacts;
    var $contacts = "";
    if (!G.isEmpty(parentContacts) && !G.isEmpty(parentContacts.length)) {
        for (var i = 0; i < parentContacts.length; i++) {
            if (isValidContact(parentContacts[i])) { // 引入contact.js
                $contacts += "<tr class='single_contact'>" +
                    "<td class='tab_title'>联系人</td><td id='name'>" + parentContacts[i].name + "</td>" +
                    "<td class='tab_title'>手机</td><td id='mobile'>" + parentContacts[i].mobile + "</td>" +
                    "<td class='tab_title'>QQ</td><td id='qq'>" + parentContacts[i].qq + "</td>" +
                    "<td class='tab_title'>Email</td><td id='email'>" + parentContacts[i].email + "</td>" +
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
    $("#childTable .totalReceivable").text(dataTransition.rounding(child.totalReceivable,2));
    $("#childTable .totalTradeAmount").text(dataTransition.rounding(child.totalTradeAmount,2));
    $("#childTable .totalReturnAmount").text(dataTransition.rounding(child.totalReturnAmount,2));
    $("#childTable .countSupplierReturn").text(G.normalize(child.countSupplierReturn));

    $("#childTable .single_contact").remove();
    var childContacts = child.contacts;
    var $childContacts = "";
    if (!G.isEmpty(childContacts) && !G.isEmpty(childContacts.length)) {
        for (var i = 0; i < childContacts.length; i++) {
            if (isValidContact(childContacts[i])) { // 引入contact.js
                $childContacts += "<tr class='single_contact'>" +
                    "<td class='tab_title'>联系人</td><td id='name'>" + childContacts[i].name + "</td>" +
                    "<td class='tab_title'>手机</td><td id='mobile'>" + childContacts[i].mobile + "</td>" +
                    "<td class='tab_title'>QQ</td><td id='qq'>" + childContacts[i].qq + "</td>" +
                    "<td class='tab_title'>Email</td><td id='email'>" + childContacts[i].email + "</td>" +
                    "</tr>"
            }
        }
    }
    if($childContacts){
        $($childContacts).insertAfter($("#childTable tr").eq(1));
    }

    var okBtns = '<input type="button" id="sureMerge" value="确&nbsp;定" onfocus="this.blur();" /><input type="button" id="cancelMerge" value="取&nbsp;消" onfocus="this.blur();"  />';
    $("#mergeSupplierDiv").append(okBtns);
    $("div[id^='selectedSupplier']").show();
    doBindHandler();
}

function doBindHandler() {
    $("div[id^='selectedSupplier']").click(function(event) {
        $("div[id^='selectedSupplier']").removeClass("radCheck").addClass("radNormal");
        $("div[id^='selectedSupplier']").attr("isParent", false);

        $('.tabMerge').removeClass('tabMerge_selected');
        $(event.target).closest('.tabMerge').addClass('tabMerge_selected');

        $(this).removeClass().addClass("radCheck");
        $(this).attr("isParent", true);
    });

    $("#sureMerge").click(function() {
        $(this).attr("disabled", "disabled");
        var parentIdStr = "";
        var chilIdStrs = "";
        $("div[id^='selectedSupplier']").each(function() {
            if($(this).attr("isParent") == "true") {
                parentIdStr = $(this).attr("supplierId");
            } else {
                chilIdStrs += $(this).attr("supplierId") + ",";
            }
        });
        chilIdStrs = chilIdStrs.substr(0, chilIdStrs.length - 1);
        if(!parentIdStr || !chilIdStrs) {
            alert("合并供应商id不存在");
            return;
        }
        var data = {
            parentIdStr: parentIdStr,
            chilIdStrs: chilIdStrs
        };
        var url = "supplier.do?method=mergeSupplierHandler";
        APP_BCGOGO.Net.asyncPost({
            url: url,
            dataType: "json",
            data: data,
            success: function(json) {
                $("#mergeSupplierDetail").dialog("close");
                $("#mergeWholesalerDetail").dialog("close");
                if(json != null && json.success == false) {
                    alert(json.msg);
                } else {
                    nsDialog.jAlert("合并成功，合并供应商信息十分钟后生效！");
                }
            },
            error: function(json) {
                nsDialog.jAlert("合并出现异常，合并失败！");
            }
        });
    });

    $("#cancelMerge").click(function() {
        $("#mergeSupplierDetail").dialog("close");
        $("#mergeWholesalerDetail").dialog("close");
    });


}



function isContainSelectedId(selectedId){
    var flag=false;
    $("#selectedIdArray input").each(function(){
        if($(this).val()==selectedId){
            flag=true;
            return;
        }
    });
    return flag;
}