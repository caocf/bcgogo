$(function () {
    var testTemp = 0;
    //回车事件根据具体场景而定

    $("#customerInfoText,#supplierInfoText,#customer_supplierInfoText,#customerMemberNo")
        .bind("keypress", function (event) {
            if (GLOBAL.Interactive.isKeyName(event, "enter")) {
                var pageType = $(event.target).attr("pagetype");    //relatedcustomerdata
                if (G.contains(pageType, ["customerdata", "relatedcustomerdata"])) {
                    //客户页面
                    $("#customerSearchBtn").click();
                } else if ("supplierdata".search(pageType) != -1) {
                    //供应商页面
                    $("#supplierSearchBtn").click();
                } else if ("stockSearch".search(pageType) != -1) {
                    //库存查询
                    $("#searchInventoryBtn").click();
                }else if("onlinePurchaseOrder".search(pageType)!=-1){
                    $("#searchOnlinePurchaseOrderBtn").click();
                }
                else {
                    GLOBAL.error("input attr pagetype is null.");
                }
                return;
            }
        });

    $("#customerInfoText,#supplierInfoText,#customer_supplierInfoText,#customerMemberNo")
        .bind("click focus keyup", function (event) {

            //BCSHOP-6047 隐藏还未替换新组件的原下拉提示.
            //TODO 全部替换成新组件后,该段代码可删除.
            if ($("#div_brand_head").length > 0) {
                $("#div_brand_head").hide();
            }

            $(this).removeAttr("customerOrSupplierId");
            var selectValue = $(event.target).attr("selectvalue");
            if (!selectValue || selectValue != $(event.target).val()) {
                $(event.target)
                    .attr("supplierInfo", null)
                    .attr("customerInfo", null);
            }

            if (G.contains(G.keyNameFromEvent(event), ["up", "down", "left", "right"])) {
                return;
            }

            if ($(event.target).attr("initialValue")) {
                if ($.trim($(event.target).val()) != $(event.target).attr("initialValue")) {
                    $(event.target).css({
                        "color": "#000000"
                    });
                } else {
                    $(event.target).val('');
                }
            }

            var domObject = event.target;
            var _data = "customer.do?method=getCustomerOrSupplierSuggestion",
                _pageType = $(domObject).attr("pagetype"),
                _searchField = $(domObject).attr("searchfield");
            //供应商/客户
            if (domObject.id.indexOf("customer") < 0 || domObject.id.indexOf("supplier") < 0) {
                if (domObject.id.indexOf("customer") != -1) {
                    _data += "&" + "customerOrSupplier=" + "customer";
                } else if (domObject.id.indexOf("supplier") != -1) {
                    _data += "&" + "customerOrSupplier=" + "supplier";
                }
            }
            var filterType = $(domObject).attr("filtertype");
            if(!G.Lang.isEmpty(filterType)){
                _data += "&" + "filterType="+filterType;
            }
            if(_pageType === "customerdata"){
                _data += "&" + "searchFieldStrategies=searchIncludeLicenseNo,searchIncludeMemberNo";
            }
            if (_pageType === "appointOrder") {
                _data += "&" + "searchFieldStrategies=searchIncludeLicenseNo";
            }
            if (_pageType === "relatedcustomerdata" || _pageType === "onlinePurchaseOrder") {
                _data += "&" + "searchStrategies=" + "customerOrSupplierShopIdNotEmpty";
            }
            if(_pageType==="appCustomerData"){
                _data += "&" + "searchIncludeLicenseNoAndMemberNo=true";
            }

            if (_searchField) {
                _data += "&" + "searchField=" + _searchField;
            }

            //会员卡
            if (domObject.id == "customerMemberNo") {
                _data += "&" + "searchField=" + "member_no";
            }

            var _selectHandler = function (event, index, data, hook) {
                var _getValue = data.name || data.member_no;
                if (_getValue) {
                    $(domObject)
                        .val(_getValue)
                        .css({"color": "#000000"})
                        // TODO 下面这句话， 有危险， 因为 input[type="hidden"] 选择符得出的非常可能是list，
                        // TODO     若为list 那么此操作无疑是错误的， 故需修改， 添加class 来标示原 input节点， 并修改 selector
                        .parent().find('input[type="hidden"]').val(data.id);
                }


                if (_pageType == 'inquiryCenter'||_pageType=='memberstatistics') {
                    if (data.contact) {
                        $("#contact").val(data.contact).css("color", "#000000");
                    } else {
                        $("#contact").val('联系人').css("color", "#ADADAD");
                    }
                    if (data.mobile) {
                        $("#mobile").val(data.mobile).css("color", "#000000");
                    } else {
                        if ($('#mobile').attr("pagetype")) {
                            $('#mobile').val('');
                        } else {
                            $('#mobile').val('手机');
                        }
                        $("#mobile").css("color", "#ADADAD");
                    }
                    if(_pageType=='memberstatistics'){
                        $("#statistics").click();
                    }
                } else if (_pageType == 'onlinePurchaseOrder') {
                    if (data.mobile) {
                        $("#mobile").val(data.mobile).css("color", "#000000");
                    } else {
                        if ($('#mobile').attr("pagetype")) {
                            $('#mobile').val('');
                        } else {
                            $('#mobile').val('手机');
                        }
                        $("#mobile").css("color", "#ADADAD");
                    }
                } else if (_pageType == 'customerdata') {
                  $("#customerSearchBtn").click();
                } else if (_pageType == 'supplierdata') {
                  $("#supplierSearchBtn").click();
                }else if(_pageType=="appCustomerData"){
                    $("#searchCommentData").click();
                }else if(_pageType=="appointOrder"){
                    customerSelectHandler(data);
                }
            };

            //使用下拉组件
            droplistLite.show({
                event: event,
                id: "id",
                keyword: "searchWord",
                name: function (_obj) {
                    var label;

                    if (_pageType == 'inquiryCenter') {
                        //不同类型的label
                        if (_obj.customerOrSupplier == "customer") {
                            label = "客户：" + "{name} {contact} {mobile}";
                        } else if (_obj.customerOrSupplier == "supplier") {
                            label = "供应商：" + "{name} {contact} {mobile}";
                        } else if (_obj.member_no) {
                            label = "卡号：" + "{member_no}";
                        }
                    }else if(_pageType == 'onlinePurchaseOrder'){
                        label = "供应商：" + "{name} {mobile}";
                    }else if(_pageType == 'appCustomerData'){
                        label = "{name} {contact} {licenseNo}";
                    }else if(_pageType === "customerdata"){
                        label = "客户："+ "{name} {contact} {mobile} {licenseNo} {member_no}";
                    }else if(_pageType === "appointOrder"){
                        label = "{name} {licenseNo} {mobile}";
                    }else {
                        //不同类型的label
                        if (_obj.customerOrSupplier == "customer") {
                            label = "客户："+ "{name} {contact} {mobile}";
                        } else if (_obj.customerOrSupplier == "supplier") {
                            label = "供应商：" + "{name} {contact} {mobile}";
                        } else if (_obj.member_no) {
                            label = "卡号：" + "{member_no}";
                        }
                    }


                    return label;
                },
                data: _data,
                afterSelected: function (event, index, data, hook) {
                    _selectHandler(event, index, data, hook);
                },
                afterKeySelected: function (event, index, data, hook) {
                    _selectHandler(event, index, data, hook);
                }
            });
        })
        .blur(function (event) {
            if ($.trim($(event.target).val()).length <= 0) {
                $(event.target).val($(event.target).attr("initialValue"));
                $(event.target).css({
                    "color": "#ADADAD"
                });
            }
        });
});