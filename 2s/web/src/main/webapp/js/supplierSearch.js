//TODO  采购单、入库单 供应商搜索

var selectItemNum = -1;
var selectmore = -1;
var domTitle;
var selectValue = '';
$(document).ready(function () {
    var elementCarNo = $("#supplier")[0];
    $("#supplier").live("blur",function() {
        var $thisDom = $(this);
        setTimeout(function () {
            if($thisDom.attr("blurLock")){
                return;
            }
            if ($.trim($thisDom.val()) == "") {
                $("#hiddenMobile").html("");
                $("#supplierId,#contact,#mobile,#address,"
                    + "#bank,#account,#accountName,#businessScope"
                    + ",#category,#abbr,#settlementType,#landline"
                    + ",#fax,#qq,#invoiceCategory,#email").val("");

                $("#receivable").html("0");
                $("#payable").html("0");
                initDuiZhanInfo();
            } else {
                if (!$thisDom.attr("lastValue")) {
                    $thisDom.attr("lastValue", "");
                }
                var lastValue = $thisDom.attr("lastValue");
                if (lastValue != null && lastValue == $thisDom.val()) {
                    return;
                }
                //todo 后续添加，修改代码注意！代码需要放在此标注后面，这里modify的意图是执行chang操作，如果需要执行bulr，放在之前。by qxy
                getSupplierInfo();
                contactDeal(!G.isEmpty($("#contactId").val()));
                selectItemNum = -1;
            }
        }, 200);
    }).live("focus", function () {
            $(this).attr("lastValue", $(this).val());
            $(this).removeAttr("blurLock");
        });


    $("#supplier").live("keyup", function (e) {   //TODO 供应商搜索
        //webChangelicenceNo(this);
    });

    function webChangelicenceNo(thisObj) {
        if (elementCarNo.value == '' || elementCarNo.value == null) {
            $("#div_brandvehiclelicenceNo").css({'display':'none'});
        }
        else {
            elementCarNo.value = elementCarNo.value.replace(/[\ |\\]/g, "");
            searchSuggestionlicenceNo(thisObj, elementCarNo.value, "notclick");
        }
    }

    //TODO 供应商下拉建议上下键快速选取内容
    $("#supplier").keydown(function (e) {
        var e = G.getEvent(e);
        var keycode = G.keyCodeFromEvent(e);
        if ($("#div_brandvehiclelicenceNo").css("display") == "block") {
            if (keycode == 38 && (selectItemNum - 1 >= 0 || selectItemNum == 0 || selectItemNum == -1)) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore + 1) : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == 0 || selectItemNum == -1 ? (selectmore) : selectItemNum;
                }
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $("#selectItem" + (selectItemNum - 1)).mouseover();
            } else if (keycode == 40) {
                if (domTitle == 'brand' || domTitle == 'model' || domTitle == 'product_name') {
                    selectItemNum = selectItemNum == selectmore ? -1 : selectItemNum;
                } else {
                    selectItemNum = selectItemNum == selectmore - 1 ? -1 : selectItemNum;
                }
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $("#selectItem" + (selectItemNum + 1)).mouseover();
            } else if (selectItemNum != -1 && keycode == 13) {
                $("#selectItem" + (selectItemNum)).click();
                $(this).blur();
            }
        }
    });
});

//TODO 将包含供应商的JSON，组装成下拉建议
function ajaxStylelicenceNo(node, json) {
    var offsetHeight = $(node).height();
    domTitle = node.name;
    var x = G.getX(node);
    var y = G.getY(node);
    selectmore = json.length;
    if (selectmore <= 0) {
        $("#div_brandvehiclelicenceNo").css({'display':'none'});
    } else {
        $("#div_brandvehiclelicenceNo").css({
            'display':'block', 'position':'absolute',
            'left':x + 'px', 'top':y + offsetHeight + 8 + 'px'
        });
        $("#Scroller-Container_idlicenceNo").html("");

        for (var i = 0; i < json.length; i++) {
            var $a = $("<a id='selectItem" + i + "'></a>");
            //todo 供应商的下拉建议包含供应商名-联系人-联系方式
            $a
                .html((json[i].name))
                .attr("title", json[i].name)
                .css({"overflow":"hidden", "width":"290px"});

            if (typeof(json[i].contract) != "undefined" && json[i].contract != "" && json[i].contract != null) {
                $a.append("+" + json[i].contract);
            }
            if (typeof(json[i].mobile) != "undefined" && json[i].mobile != "" && json[i].contract != null) {
                $a.append("+" + json[i].mobile);
            }
//            a.html(jsonData[i].name);

            $a
                .mouseover(function (event) {
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = json[$("#Scroller-Container_idlicenceNo > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            })
                .mouseout(function (event) {
                    selectValue = "";
                })
                .click(function (event) {
                    //TODO 为相关域赋值  BEGIN-->
                    var foo = $("#Scroller-Container_idlicenceNo > a");
                    $(node).val(selectValue = json[foo.index(this)].name);     //取的第一字符串
                    $("#supplierId").val(selectValue = json[foo.index(this)].idStr);
                    $("#contact").val(selectValue = json[foo.index(this)].contract);
                    $("#mobile").val(selectValue = json[foo.index(this)].mobile);
                    $("#hiddenMobile").val(selectValue = json[foo.index(this)].mobile);
                    $("#address").val(selectValue = json[foo.index(this)].address);
                    $("#bank").val(json[foo.index(this)].bank);
                    $("#account").val(json[foo.index(this)].account);
                    $("#accountName").val(json[foo.index(this)].accountName);
                    if (json[foo.index(this)].businessScope == null) {
                        $("#businessScope").val("");
                    } else {
                        $("#businessScope").val(json[foo.index(this)].businessScope.replaceAll('<br/>', '\r\n'));
                    }
                    $("#category").val(json[foo.index(this)].category);
                    $("#abbr").val(json[foo.index(this)].abbr);
                    $("#settlementType").val(json[foo.index(this)].settlementType);
                    $("#landline").val(json[foo.index(this)].landLine);
                    $("#fax").val(json[foo.index(this)].fax);
                    $("#qq").val(json[foo.index(this)].qq);
                    $("#invoiceCategory").val(json[foo.index(this)].invoiceCategory);
                    $("#email").val(json[foo.index(this)].email);
                    $(node).blur();
                    //todo <--END
                    //TODO 通过供应商ID查询供应商信息
                    $.ajax({
                            type:"POST",
                            url:"RFSupplier.do?method=ajaxSearchSupplierById",
                            async:true,
                            data:{
                                shopId:$("#shopId").val(),
                                supplierId:selectValue = json[foo.index(this)].idStr
                            },
                            cache:false,
                            dataType:"json",
                            error:function (XMLHttpRequest, error, errorThrown) {
                                $("#supplierId").val('');
                            },
                            success:function (jsonObjs) {
                                if (!jsonObjs || !jsonObjs[0])
                                    return;
                                var jsonData = jsonObjs[0];
                                $("#supplierId").val(jsonData.idString);
                                $("#supplier").val(jsonData.name);
                                if (jsonData.contact) {
                                    $("#contact").val(jsonData.contact);
                                }
                                $("#mobile").val(jsonData.mobile);
                                $("#hiddenMobile").html(jsonData.mobile);
                                $("#address").val(jsonData.address);
                                $("#bank").val(jsonData.bank);
                                $("#account").val(jsonData.account);
                                $("#accountName").val(jsonData.accountName);
                                if (!jsonData.businessScope) {
                                    $("#businessScope").val("");
                                } else {
                                    $("#businessScope").val(jsonData.businessScope);
                                }
                                $("#category").val(jsonData.category);
                                $("#abbr").val(jsonData.abbr);
                                $("#settlementType").val(jsonData.settlementTypeId);
                                $("#landline").val(jsonData.landLine);
                                $("#fax").val(jsonData.fax);
                                $("#qq").val(jsonData.qq);
                                $("#invoiceCategory").val(jsonData.invoiceCategoryId);
                                $("#email").val(jsonData.email);
                            }
                        }
                    );
                    selectItemNum = -1;
                    $("#div_brandvehiclelicenceNo").css({'display':'none'});
                });
            $("#Scroller-Container_idlicenceNo").append($a);
        }
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && json.length == 9) {
        var a = $("<a id='selectItem" + (selectmore) + "'></a>");
        a
            .html("更多")
            .mouseover(function () {
                $("#Scroller-Container_idlicenceNo > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectItemNum = parseInt(this.id.substring(10));
            })
            .click(function () {
                $("#div_brandvehiclelicenceNo").css({'display':'none'});
                $("#iframe_PopupBox")
                    .attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle=" + domTitle + "&brandvalue=" + $("#brand").val()))
                    .css({'display':'block'});
                Mask.Login();
            });
        $("#Scroller-Container_idheader").append(a);
    }
}


//TODO 查询供应商信息，命名有误
function searchSuggestionlicenceNo(domObject, elementCarNo, eventStr) { //车辆信息查询
    var searchWord;
    if (eventStr == "click") {
        searchWord = "";
    } else {
        searchWord = domObject.value;
    }

    $.ajax({
            type:"POST",
            url:"RFSupplier.do?method=ajaxSearchSupplierName",
            async:true,
            data:{name:searchWord},
            cache:false,
            dataType:"json",
            error:function (XMLHttpRequest, error, errorThrown) {
                $("#div_brandvehiclelicenceNo").css({'display':'none'});
            },
            success:function (jsonData) {
                ajaxStylelicenceNo(domObject, jsonData);
            }
        }
    );
}

//TODO 将相关信息（联系人、联系方法、联系地址） 设置为只读, 这个是需求吗
function isReadOnly() {
    var infoNameList = ["contact", "mobile", "address"];
    for (var i = 0, len = infoNameList.length; i < len; i++)
        $("#" + infoNameList[i]).attr("readonly", $.trim($("#" + infoNameList[i]).val()) != "");
}

function clearSupplierRelateInfo(clearSupplierInput){
    if(typeof clearSupplierInput =='undefined' || clearSupplierInput == null || clearSupplierInput){
        $("#supplier").val("");
    }
    $("#hiddenMobile").html("");
    $("#supplierId,#contactId,#contact,#mobile,#address,"
        + "#bank,#account,#accountName,#businessScope,"
        + "#category,#abbr,#settlementType,#landline,"
        + "#fax,#qq,#invoiceCategory,#email,#supplierShopId,#customerOrSupplierId").val("");
    $("#contact, #mobile,#email,#qq").removeAttr("readonly");
}

function getSupplierInfo() {
    $.ajax({
        type:"POST",
        url:"RFSupplier.do?method=getSupplierByNameAndShopId",
        async:true,
        data:{name:$("#supplier").val()},
        cache:false,
        dataType:"json",
        error:function (XMLHttpRequest, error, errorThrown) {
            clearSupplierRelateInfo(false);
        },
        success:function (jsonData) {
            if (jsonData!=null && jsonData.supplierDTO != undefined && jsonData.supplierDTO != null) {
                nsDialog.jConfirm("存在重名的供应商，是否带出原有供应商信息?", "友情提示", function(returnVal){
                    if(!returnVal){
                        clearSupplierRelateInfo();
                        return;
                    }
                    var foo = jsonData.supplierDTO;
                    $("#supplierId").val(foo.idString);
                    $("#customerOrSupplierId").val(foo.idString);
                    $("#supplier").val(foo.name);
                    if($("#supplierShopId")) $("#supplierShopId").val(foo.supplierShopIdString);

                    $("#contact").val(foo.contact);
                    $("#contactId").val(foo.contactIdStr);
                    $("#mobile").val(foo.mobile);
                    if(!G.isEmpty(foo.contactIdStr)){
                        if(!G.isEmpty(foo.contact)){
                            $("#contact").attr("readonly", true);
                        }
                        if(!G.isEmpty(foo.mobile)){
                            $("#mobile").attr("readonly", true);
                        }
                    }
                    $("#address").val(foo.address);
                    $("#bank").val(foo.bank);
                    $("#account").val(foo.account);
                    $("#accountName").val(foo.accountName);
                    $("#businessScope").val(foo.businessScope);
                    $("#category").val(foo.category);
                    $("#abbr").val(foo.abbr);
                    $("#settlementType").val(foo.settlementTypeId);
                    $("#landline").val(foo.landLine);
                    $("#fax").val(foo.fax);
                    $("#qq").val(foo.qq);
                    $("#invoiceCategory").val(foo.invoiceCategoryId);
                    $("#email").val(foo.email);

                    if(getOrderType() == "BORROW_ORDER"){
                        $("#customerOrSupplierId").val(foo.idString);
                        $("#name").val(foo.name);
                        var contacts = foo.contacts;
                        if(contacts){
                            for(var i= 0,len = contacts.length;i<len;i++){
                              if(contacts[i] && contacts[i].idStr && contacts[i].idStr ==$("#contactId").val()){
                                  $("#email").val(G.Lang.normalize(contacts[i].email));
                                  $("#qq").val(G.Lang.normalize(contacts[i].qq));
                              }
                            }
                        }
                    }

                    if(jsonData.totalDebt){
                        $("#receivable").html(dataTransition.rounding(jsonData.totalDebt,2));
                    }else{
                        $("#receivable").html("0");
                    }
                    if(jsonData.totalPayable){
                        $("#payable").html(dataTransition.rounding(jsonData.totalPayable,2));
                    }else{
                        $("#payable").html("0");
                    }
                    initDuiZhanInfo();

                });

            } else {
                clearSupplierRelateInfo(false);

                $("#receivable").html("0");
                $("#payable").html("0");
                initDuiZhanInfo();
            }
            if(verifyProductThroughOrderVersion(getOrderType())&&!G.isEmpty($("#storehouseId").val())){
                if(getOrderType()=="RETURN"){
                    $(".j_checkStoreHouse").change();
                }
            }
        }
    });
}


