$().ready(function(){
//    var customerSmsInput = APP_BCGOGO.Module.customerSmsInput;
//
//
//    $("#addContent").live("click",function(){
//        customerSmsInput.addData(JSON.parse($("#addContent").val()));
//    });



//    $("#allUser,#allCustomer,#allSupplier,#allMember,#allHasMobile,.cust_time").live("click", function() {
//
//        var name = "";
//        var mobile = "";
//        var id = this.id
//        if (id == "cust_time1") {
//            $("#allUser").click();
//            return;
//        } else if (id == "cust_time2") {
//            $("#allCustomer").click();
//            return;
//        } else if (id == "cust_time3") {
//            $("#allSupplier").click();
//            return;
//        } else if (id == "cust_time4") {
//            $("#allHasMobile").click();
//            return;
//        } else if (id == "cust_time5") {
//            $("#allMember").click();
//            return;
//        }
//
//        if (id == "allUser") {
//            name = "所有联系人";
//        } else if (id == "allCustomer") {
//            name = "所有客户";
//        } else if (id == "allSupplier") {
//            name = "所有供应商";
//        } else if (id == "allMember") {
//            name = "会员"
//        } else if (id == "allHasMobile") {
//            name = "所有手机联系人";
//        }
//        mobile = name;
//        var obj = [
//            {
//                name: name,
//                mobile: mobile,
//                userId: id
//            }
//        ];
//        var jsonArrStr = JSON.stringify(obj);
//        $("#addContent").val(jsonArrStr);
//        $("#addContent").click();
//
//    });

    $(".itemChk").live("click",function(){
        $(".select_all").attr("checked",$(".itemChk").length==$(".itemChk:checked").length)
    });

    $(".select_all").click(function(){
        $(".itemChk").attr("checked",$(this).attr("checked"));
        $(".select_all").attr("checked",$(this).attr("checked"));
    });


});




//TODO writer in smsWriter.js
$(function(){

    $(".group_item_btn").live("click",function(){
        if(G.isEmpty($(this).attr("groupType"))){
            return;
        }
        var $itemGroupDiv=$(this).closest(".group-item");
        $itemGroupDiv.find(".item_group_contact").remove();
        APP_BCGOGO.Net.asyncAjax({
            url: "contact.do?method=queryContact",
            type: "POST",
            cache: false,
            data:{
                startPageNo:1,
                contactGroupType:$(this).attr("groupType")
            },
            dataType: "json",
            success: function (result) {
                var contactDTOList=result.results;
                if(G.isEmpty(contactDTOList)) return;
                var contactStr="";
                for(var i=0;i<contactDTOList.length;i++){
                    var contact=contactDTOList[i];
                    var contactId=contact.specialIdStr;
                    var mobile=contact.mobile;
                    var name=G.normalize(contact.name);
                    var label=G.isEmpty(name)?"未命名":contact.name;
                    label+='<'+mobile+'>';
                    contactStr+='<div contactId="'+contactId+'" name="'+name+'" mobile="'+mobile+'" ' +
                        'class="item_group_contact" style="margin-left: 15px;height:18px;cursor:pointer;">'+label+'</div>'

                }
                $itemGroupDiv.append(contactStr);
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });
    });






//    $("#customerSupplierFlag").click(function(){
//        var checked=$(this).attr("checked");
//        $(this).attr("checked",true);
//    });


});

function doAddContactAlert(name,mobile){
    $("#addContactAlert").dialog({
        resizable: false,
        draggable:true,
        title: "编辑联系人",
        height: 220,
        width: 380,
        closeText: "hide",
        modal: true,
        closeOnEscape: false,
        open:function(){
            name=name=="未命名"?"":name;
            $("#addContactAlert .contact_name").val(name);
            $("#addContactAlert .contact_mobile").val(mobile);
        },
        beforeClose:function() {
            $("#addContactAlert .contact_name").val("");
            $("#addContactAlert .contact_mobile").val("");
            $("#customerSupplierFlag").attr("checked", false)
        }
    });
}

function addNewContactToList(contact){
    if(G.isEmpty(contact)) return;
    var name=contact.name;
    var mobile=contact.mobile;
    var tLabel=(G.isEmpty(name)?"未命名":name)+"<"+mobile+">";
    var con_item_str='<li contactid="'+contact.idStr+'" mobile="'+mobile+'" name="'+name+'" style="cursor:pointer;display:none " ' +
        'class="item_group_contact" search-content="' + name + ' ' + mobile + '">'+tLabel+'</li>';
    $("#smsContactUI").append(con_item_str);
    $("#searchContactInfoResultDiv ul").append(con_item_str);
}

function toSmsWrite(smsId,contactIds){
    window.location.href="sms.do?method=smswrite&smsId="+smsId+"&contactIds="+contactIds;
}






