$().ready(function(){

    $(".otherIncomeKindName").live("blur",function(){
      $(this).val($.trim($(this).val()));
      var object = $(this).attr("id");
      var idStr = object.split(".")[0];
      var inputName = "otherIncomeItemDTOList" + "[" + idStr.split("otherIncomeItemDTOList")[1] + "]";
      if ($(this).val() == "材料管理费" && $("#" + idStr + "\\.otherIncomePriceByRate").length == 0) {
        var tdHtml = '<input type="radio" class="otherIncomePriceByRate" name="' + inputName + '.priceCheckBox" id="' + idStr + '.otherIncomePriceByRate" style="float:left; margin-right:4px;"/>' +
            '<label></label>按材料费比率计算<input type="text" id="' + idStr + '.otherIncomePriceRate" class="txt otherIncomePriceRate txt_color"' +
            ' value="请输入比率" ' +
            'style="width:70px;color:#9a9a9a;" />' +
            '&nbsp;%&nbsp;&nbsp;<span id="' + idStr + '.otherIncomePriceSpan">0</span>元' +
            '<div class="clear i_height"></div><input type="radio" class="otherIncomePriceByAmount" checked="checked" name="' + inputName + '.priceCheckBox" id="' + idStr + '.otherIncomePriceByAmount" style="float:left; margin-right:4px;"/>' +
            '<label></label>按固定金额计算&nbsp;&nbsp;<input type="text" id="' + idStr + '.otherIncomePriceText" value="请输入金额" class="txt otherIncomePriceText txt_color" ' +
            'style="width:100px;color:#9a9a9a;" />元';

            if ($("#orderType").val() == "repairOrder") {
              tdHtml += '<input id="' + idStr + '.price" class="table_input otherIncomePrice checkStringEmpty" type="hidden" name="' + inputName + '.price">' + '';
            }
            if ($("#orderType").val() == "goodsSaleOrder") {
              tdHtml += '<input id="' + idStr + '.price" class="table_input itemTotal otherIncomePrice checkStringEmpty" type="hidden" name="' + inputName + '.price">' + '';
            }

            '<input id="' + idStr + '.otherIncomeCalculateWay" type="hidden" name="' + inputName + '.otherIncomeCalculateWay">' +
            '<input id="' + idStr + '.otherIncomeRate" type="hidden" name="' + inputName + '.otherIncomeRate">';


        $(this).parent().next().html(tdHtml);
      } else if ($(this).val() != "材料管理费" && $("#" + idStr + "\\.otherIncomePriceByRate").length > 0) {
        if($("#orderType").val()=="repairOrder"){
          var tdHtml = '<input id="' + idStr + '.id" type="hidden" name="' + inputName + '.id">' +
              '<input  id="' + idStr + '.templateIdStr" class="table_input" type="hidden" name="' + inputName + '.templateIdStr">' +
               '<input id="' + idStr + '.templateId" class="table_input" type="hidden" name="' + inputName + '.templateId">' +
               '<input id="' + idStr + '.price" class="table_input otherIncomePrice checkStringEmpty" ' +
               'type="text" name="' + inputName + '.price" autocomplete="off">';
          $(this).parent().next().html(tdHtml);
        }
        if ($("#orderType").val() == "goodsSaleOrder") {
          var thHtml = '<input id="' + idStr + '.price" class="table_input itemTotal otherIncomePrice checkStringEmpty" ' +
              'type="text" style="width:80%;" value="" maxlength="10" name="' + inputName + '.price" autocomplete="off">';
          $(this).parent().next().html(thHtml);
        }
      }
    });

    $(".otherIncomeKindName").live("click focus keyup",function(event){

        event = event || event.which;

        var keyCode = event.keyCode;

        if(keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40)
        {
            return;
        }
        $(this).val(APP_BCGOGO.StringFilter.inputtingOtherCostNameFilter($(this).val()));
        var obj = this;
        askForAssistDroplist(event,obj);
    });

    $(".otherIncomePriceText").live("click", function (event) {
      $(this).css("color", "#000");
      if ("请输入金额" == $(this).val()) {
        $(this).val("");
        $("#" + $(this).attr("id").split(".")[0] +"\\.price").val("");
        return;
      }else{
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByAmount").attr("checked","checked");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceRate").val("请输入比率");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceSpan").text("0");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceRate").css("color", "#9a9a9a");
      }
      setTotal();
    });

    $(".otherIncomePriceText").live("blur", function (event) {
      if (!$(this).val()) {
        $(this).val("请输入金额");
        $(this).css("color", "#9a9a9a");
        return;
      } else {
        $(this).css("color", "#000");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByAmount").attr("checked","checked");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceRate").val("请输入比率");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceSpan").text("0");
        $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceRate").css("color", "#9a9a9a");
      }
      setTotal();
    });

    $(".otherIncomePriceText").live("focus keyup", function (event) {
      $(this).val(App.StringFilter.inputtingPriceFilter($(this).val(), 2));
      var idPrefix =$(this).attr("id").split(".")[0];
      $("#" +idPrefix +"\\.price").val($(this).val());
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByAmount").attr("checked","checked");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceRate").val("请输入比率");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceSpan").text("0");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceRate").css("color", "#9a9a9a");
      setTotal();
    });


  $(".otherIncomePriceRate").live("click", function (event) {
    $(this).css("color", "#000");
    if ("请输入比率" == $(this).val()) {
      $(this).val("");
      return;
    }else{
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByRate").attr("checked","checked");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").val("请输入金额");

    }
    setTotal();
  });

  $(".otherIncomePriceRate").live("blur", function (event) {
    if (!$(this).val()) {
      $(this).val("请输入比率");
      $(this).css("color", "#9a9a9a");
      return;
    } else {
      $(this).css("color", "#000");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByRate").attr("checked","checked");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").val("请输入金额");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").css("color", "#9a9a9a");
    }
    setTotal();
  });

  $(".otherIncomePriceRate").live("focus keyup", function (event) {
    $(this).val(App.StringFilter.inputtingPriceFilter($(this).val(), 2));
//    var idPrefix = $(this).attr("id").split(".")[0];
//    $("#" + idPrefix + "\\.otherIncomePriceSpan").text(App.StringFilter.inputtingPriceFilter($(this).val() * $("#salesTotalSpan").text()/100, 2));
//    $("#" + idPrefix + "\\.price").val($("#" + idPrefix + "\\.otherIncomePriceSpan").text());
    $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByRate").attr("checked","checked");
    $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").val("请输入金额");
    $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").css("color", "#9a9a9a");
    setTotal();
  });

  $(".otherIncomePriceByRate").live("click", function (event) {

    var idPrefix = $(this).attr("id").split(".")[0];
    $("#" + idPrefix + "\\.otherIncomePriceText").val("请输入金额");
    $("#" + idPrefix + "\\.otherIncomePriceText").css("color", "#9a9a9a");
    setTotal();
  });

  $(".otherIncomePriceByAmount").live("click", function (event) {

    var idPrefix = $(this).attr("id").split(".")[0];
    $("#" + idPrefix + "\\.otherIncomePriceRate").val("请输入比率");
    $("#" + idPrefix + "\\.otherIncomePriceRate").css("color", "#9a9a9a");
    $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceSpan").text("0");

    setTotal();
  });

  $(".otherIncomePriceRate").live("blur", function (event) {
    if (!$(this).val()) {
      $(this).val("请输入比率");
      $(this).css("color", "#9a9a9a");
      return;
    } else {
      $(this).css("color", "#000");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceByRate").attr("checked","checked");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").val("请输入金额");
      $("#" + $(this).attr("id").split(".")[0] +"\\.otherIncomePriceText").css("color", "#9a9a9a");

    }
    setTotal();
  });

  function askForAssistDroplist(event,obj) {
        var keycode= event.which || event.keyCode;
        var droplist = APP_BCGOGO.Module.droplist;
        clearTimeout(droplist.delayTimerId || 1);
        droplist.delayTimerId = setTimeout(function () {
            var droplist = APP_BCGOGO.Module.droplist;
            // 我们dummy 一个数据集， 这个数据集即符合 droplist 的需要。
           var otherIncomeKindName=$(obj).val();
           var otherIncomeKindId = $("#"+obj.id.split(".")[0]+"\\.otherIncomeKindId").val();
            var uuid = GLOBAL.Util.generateUUID();
            droplist.setUUID(uuid);
            APP_BCGOGO.Net.asyncGet({
                url:"txn.do?method=getOtherIncomeKind",
                data:{
                    "uuid":uuid,
                    "keyWord":otherIncomeKindName,
                    "now":new Date()
                },
                dataType:"json",
                success:function(result) {
                    if(null == result || null == result.data) return;
                    if(!G.isEmpty(result.data[0])){
                        G.completer({
                                'domObject':obj,
                                'keycode':keycode,
                                'title':result.data[0].label}
                        );
                    }
                    droplist.show({
                        "selector":$(event.currentTarget),
                        "isEditable":true,
                        "originalValue":{label:otherIncomeKindName,idStr:otherIncomeKindId},
                        "data":result,
                        "isDeletable":true,
                        "onSelect":function (event, index, data,hook) {
                            var id = data.idStr;
                            var name=data.kindName;

                            $(hook).val(name);
                            droplist.hide();

                            if(name =="材料管理费"){
                              var object = $(hook).attr("id");
                              var idStr = object.split(".")[0];
                              var inputName = "otherIncomeItemDTOList" +"[" + idStr.split("otherIncomeItemDTOList")[1] +"]";
                              var tdHtml = '<input type="radio" class="otherIncomePriceByRate" name="'+inputName +'.priceCheckBox" id="'+ idStr +'.otherIncomePriceByRate" style="float:left; margin-right:4px;"/>' +
                                  '<label></label>按材料费比率计算<input type="text" id="'+ idStr +'.otherIncomePriceRate" class="txt otherIncomePriceRate txt_color"' +
                                  ' value="请输入比率" ' +
                                  'style="width:70px;color:#9a9a9a;" data-filter-zero="true"/>' +
                                  '&nbsp;%&nbsp;&nbsp;<span id="'+ idStr +'.otherIncomePriceSpan">0</span>元' +
                                  '<div class="clear i_height"></div><input type="radio" class="otherIncomePriceByAmount" checked="checked" name="'+inputName +'.priceCheckBox" id="'+ idStr +'.otherIncomePriceByAmount" style="float:left; margin-right:4px;"/>' +
                                  '<label></label>按固定金额计算&nbsp;&nbsp;<input type="text" id="'+ idStr +'.otherIncomePriceText" value="请输入金额" class="txt otherIncomePriceText txt_color" ' +
                                  'style="width:100px;color:#9a9a9a;" data-filter-zero="true"/>元';

                              if ($("#orderType").val() == "repairOrder") {
                                tdHtml += '<input id="' + idStr + '.price" class="table_input otherIncomePrice checkStringEmpty" type="hidden" name="' + inputName + '.price">' + '';
                              }
                              if ($("#orderType").val() == "goodsSaleOrder") {
                                tdHtml += '<input id="' + idStr + '.price" class="table_input itemTotal otherIncomePrice checkStringEmpty" type="hidden" name="' + inputName + '.price">' + '';
                              }
                                tdHtml +=   '<input id="' + idStr + '.otherIncomeCalculateWay" type="hidden" name="' + inputName + '.otherIncomeCalculateWay">'+
                                  '<input id="' + idStr + '.otherIncomeRate" type="hidden" name="' + inputName + '.otherIncomeRate">';


                              $(hook).parent().next().html(tdHtml);
                            }
                        },

                        "onEdit":function (event, index, data,hook) {
//                            nsDialog.jAlert("修改此营业分类，其他使用此分类的服务或者商品也会随之修改！", null, function() {});
                        },

                        "onSave":function (event, index, data,hook) {
                            var id = data.idStr;
                            var name=$.trim(data.label);
                            var shopId = data.shopId;
                            if (shopId == -1) {
                              nsDialog.jAlert("该数据是标准数据,不能修改");
                              return;
                            }

                            if(name.search(/[^\u4e00-\u9fa5\w\(\)（）×\*/\.\-_\-‘’“”、,，\$% ]+/g) !== -1 ) {
                                droplist.hide();
                                nsDialog.jAlert("只能输入中英文,数字,$,%,/,*,（）,×,-,‘’,“”,.空格等");
                                return;
                            }
                            if(name.length>50) {
                                droplist.hide();
                                nsDialog.jAlert("输入的长度不能超过50");
                                return;
                            }
//                            // TODO 保存数据到服务器端 AJAX 请求
                            APP_BCGOGO.Net.syncPost({
                                url:"txn.do?method=updateOtherIncomeKind",
                                data:{
                                    "otherIncomeKindId":id,
                                    "otherIncomeKind":name,
                                    "now":new Date()
                                },
                                dataType:"json",
                                success:function(jsonObject) {
                                    if(jsonObject.resu == "success")
                                    {
                                        data.label = name;
                                        data.kindName = name;

                                    }
                                    else
                                    {
                                        alert("其他费用下拉列表中已经有此项！");
                                        data.label = data.kindName;
                                    }
                                }
                            });
                        },

                        "onDelete":function(event, index, data) {

                            var shopId = data.shopId;
                            if (shopId == -1) {
                              nsDialog.jAlert("该数据是标准数据,不能修改");
                              return;
                            }

                            var r = APP_BCGOGO.Net.syncGet({
                                url: "txn.do?method=deleteOtherIncomeKind",
                                data: {
                                    otherIncomeKindId: data.idStr,
                                    now:new Date()
                                },
                                dataType: "json"
                            });
                            if(r == null || r.resu == "error") {
                                alert("删除失败！");
                            } else if(r.resu == "success") {
                                alert("删除成功！");
                            }
                        },
                        "onKeyboardSelect":function(event, index, data, hook) {
                            // TODO;
                            $(hook).val(data.label);
                        }
                    });

                }
            });
        }, 200);
    }

  $(".otherIncomeCostPrice").live("click focus keyup", function (event) {
    $(this).val(App.StringFilter.inputtingPriceFilter($(this).val(), 2));
  });

  $(".otherIncomeCostPriceCheckbox").live("click", function (event) {
      var idPrefix = $(this).attr("id").split(".")[0];

     if($(this).attr("checked")){
       $("#" + idPrefix +"\\.otherIncomeSpan").css("display","inline");
       $("#" + idPrefix +"\\.otherIncomeCostPrice").val($("#" + idPrefix +"\\.price").val());
     }else{
       $("#" + idPrefix +"\\.otherIncomeCostPrice").val("");
       $("#" + idPrefix +"\\.otherIncomeSpan").css("display","none");
     }
    });
});