;
$(function() {
    var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
    $("#invoicingItem").click(function(e) {
        $(this).select();
        invoicingSuggestion(this);
    });
    function invoicingSuggestion(domObj, eventKeyCode) {
        if (!$(domObj).val()) {
            $("#div_brand").css({'display':'none'});
        }
        if (eventKeyCode && eventKeyCode == 13) {
            //触发 模糊库存查询
            stockSearchBoxsAdjust(domObj);
        } else {
            //判断事件类型
            var ajaxUrl = "txn.do?method=searchService";
            bcgogoAjaxQuery.setUrlData(ajaxUrl, {service:$(domObj).val()});
            bcgogoAjaxQuery.ajaxQuery(function(jsonStr) {
                ajaxStyle(domObj, jsonStr);
            })
            //回车 按键触发事件
        }
    }

    //计次收费项目
    $("#payPerProject")
        .click(function(e) {
            var ajaxUrl = "txn.do?method=searchService";
            var domObj = this;
            bcgogoAjaxQuery.setUrlData(ajaxUrl, {name:$(domObj).val()});
            bcgogoAjaxQuery.ajaxQuery(function(jsonStr) {
                ajaxStyleService(domObj, jsonStr);
            });
        })
        .keyup(function(event) {
            if (GLOBAL.Interactive.keyNameFromEvent(event).search(/left|up|right|down/g) == -1) {
                var keycode= event.which || event.keyCode;
                var ajaxUrl = "txn.do?method=searchService";
                var domObj = this;
                bcgogoAjaxQuery.setUrlData(ajaxUrl, {name:$(domObj).val()});
                bcgogoAjaxQuery.ajaxQuery(function(jsonStr) {
                    if(!G.isEmpty(jsonStr[0])){
                        G.completer({
                                'domObject':domObj,
                                'keycode':keycode,
                                'title':jsonStr[0].name}
                        );
                    }
                    ajaxStyleService(domObj, jsonStr);
                });
            }
        });

    //弹出施工人下拉框
    $("#serviceWorker,#saler,#operator").bind("click keyup", function(e) {
            if(e.type == 'click'){
                $(this).select();
            }
            var keyword = $(this).val();
            var domObject = this;
            var keycode= e.which || e.keyCode;
            $.ajax({
                    type:"POST",
                    url:"txn.do?method=searchWorks",
                    data:{keyWord:keyword},
                    async:false,
                    cache:false,
                    dataType:"json",
                    success:function(jsonStr) {
                        G.completer({
                                'domObject':domObject,
                                'keycode':keycode,
                                'title':jsonStr[0].name}
                        );

                        ajaxStyleWorkers(domObject, jsonStr);
                    },
                    error:function(XMLHttpRequest, error, errorThrown) {

                    }
                }
            );
    })
        .blur(function(event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == '') {
                    event.target.value = initialValue;
                    $(this).css({"color":"#ADADAD"});
                } else {
                    $(this).css({"color":"#000000"});
                }
            }
            $(this).removeClass("J-active");
        })
        .focus(function(event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == initialValue) {
                    event.target.value = "";
                }
                $(this).css({"color":"#000000"});
            }
        });

    function ajaxStyleService(domObject, jsonStr) {
        var offsetHeight = $(domObject).height();
        suggestionPosition(domObject, 0, offsetHeight + 3);
        $("#Scroller-Container_id").html("");
        $("#div_brand").css({
            'overflow-x':"hidden",
            'overflow-y':"auto",
            'padding-left':0 + 'px'
        });
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(stringMethod.substring(jsonStr[i].name, 10)).attr('title', jsonStr[i].name);
            a.attr("title", jsonStr[i].name);
            a.data("serviceId", jsonStr[i].id);
            a.data("price", jsonStr[i].price);
            a.mouseover(function() {
                $("#Scroller-Container_id > a").removeAttr("class");
                $(this).attr("class", "hover");
            });
            a.click(function() {
                $(domObject).val($(this).html()).css("color","#000000");
            });
            $("#Scroller-Container_id").append(a);
        }
    }

    $("#invoicingDepartment").click(function(e) {
        $(this).select();
    });

    function ajaxStyle(domObject, jsonStr) {
        var id = $(domObject).attr("id");
        var firstCustomer = null;
        var offset = $(domObject).offset();
        var offsetHeight = $(domObject).height();
        if (jsonStr.length <= 0) {
            $("#div_brand").hide();
        } else {
            $("#div_brand").css({
                'display':'block','position':'absolute',
                'left':offset.left + 'px',
                'top':offset.top + offsetHeight + 3 + 'px',
                'color':"#000000" ,
                'height': 320 + 'px'
            });
            $("#Scroller-Container_id").html("");
            for (var i = 0; i < jsonStr.length; i++) {
                var a = $("<a id='selectItem" + i + "'></a>");
                a.html(stringMethod.substring(jsonStr[i].name, 10)).attr('title', jsonStr[i].name);
                a.val(jsonStr[i].idStr + "," + jsonStr[i].price);
                a.attr("title", jsonStr[i].name);
                a.data("serviceId", jsonStr[i].id);
                a.mouseover(function() {
                    $("#Scroller-Container_id > a").removeAttr("class");
                    $(this).attr("class", "hover");
                });
                a.click(function() {
                    $("#invoicingItem").val($(this).attr("title"));
                });
                $("#Scroller-Container_id").append(a);
            }
        }

    }

    function ajaxStyleWorkers(domObject, jsonStr) {
        var offset = $(domObject).offset();
        var offsetHeight = $(domObject).height();
        var offsetWidth = $(domObject).width();
        if (jsonStr.length <= 0) {
            $("#div_brand").hide();
        } else {
            $("#div_brand").css({
                'display':'block','position':'absolute',
                'left':offset.left + 'px',
                'top':offset.top + offsetHeight + 3 + 'px',
                'overflow-x':"hidden",
                'overflow-y':"auto"
            });
            $("#Scroller-Container_id").html("");
            for (var i = 0; i < jsonStr.length; i++) {
                var a = $("<a id='selectItem" + i + "'></a>");
                a.attr("workerInfo", JSON.stringify(jsonStr[i]));
                a.attr("inputtype", $(domObject).attr("id"));
                var text = jsonStr[i].name;
                if(jsonStr[i].department){
                    text += ' ' + jsonStr[i].department;
                }
                a.html(stringMethod.substring(text, 10)).attr('title', jsonStr[i].name);
                a.val(jsonStr[i].idStr);
                a.attr("title", jsonStr[i].name);
                a.mouseover(function() {
                    $("#Scroller-Container_id > a").removeAttr("class");
                    $(this).attr("class", "hover");
                });
                a.click(function() {
                    var workerInfo = $.parseJSON($(this).attr("workerInfo"));
                    var inputType = a.attr("inputtype");
                    if (inputType == "serviceWorker") {
                        $("#serviceWorker").val(workerInfo.name).css("color","#000000");
                        $("#invoicingDepartment").val(workerInfo.department);
                    } else if (inputType == "saler") {
                        $("#saler").val(workerInfo.name).css("color","#000000");
                    } else if (inputType == "operator") {
                        $("#operator").val(workerInfo.name).css("color","#000000");
                        APP_BCGOGO.Net.asyncPost({
                            url: "user.do?method=getUserIdBySalesManId",
                            data: {
                                salesManId: workerInfo.idStr
                            },
                            cache: false,
                            dataType: "json",
                            success: function(userDTO) {
                               var userId=userDTO.idStr;
                              if(userId != null) {
                                  if($("#operatorId")[0]) {
                                      $("#operatorId").val(userId);
                                  }
                              } else {
                                  if($("#operatorId")[0]) {
                                      $("#operatorId").val('');
                                  }
                              }
                            }
                        });

                    }
                    $(domObject).blur();
                });
                $("#Scroller-Container_id").append(a);
            }
        }

    }
});

