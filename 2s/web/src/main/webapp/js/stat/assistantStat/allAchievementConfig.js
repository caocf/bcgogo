var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


function achievementNotify() {
  var num = $("#totalConfigNum").text();
  if (num * 1 > 0) {
    nsDialog.jConfirm("还有" + num + "个项目未支配提成,是否继续配置?", null, function (getVal) {
      if (getVal) {
        $(this).dialog("close");
        $("#totalConfigNumSpan").click();
        return false;
      } else {
        window.location = "assistantStat.do?method=redirectAssistantStat";
      }
    });
  } else {
    window.location = "assistantStat.do?method=redirectAssistantStat";
  }
}


$(document).ready(function() {

  $(".ti_chen_input").live("blur",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        var prefix = $(e.target).attr("id").split(".")[0];

        var price = $("#" + prefix + "\\.achievementAmount").val()
        if (isNaN(price) || price < -0.0001) {
          $(this).val("");
          nsDialog.jAlert("请输入正确的价格!");
          return false;
        }
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        $(e.target).val(price);
        var ajaxUrl = "assistantStat.do?method=updateProductAchievement";
        var ajaxData = {
          idList: $("#" + prefix + "\\.serviceId").val(),
          achievementType: $("#" + prefix + "\\.achievementType").val(),
          achievementAmount: price
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
          if (result) {
            if (result.success) {
              $("#totalShopAchievementConfig").text(result.data);
            } else {
            }
          }
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $(".achievementTypeSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        var prefix = $(e.target).attr("id").split(".")[0];

        var price = $("#" + prefix + "\\.achievementAmount").val()
        if (isNaN(price) || price < -0.0001 || price == "") {
          nsDialog.jAlert("请输入正确的价格!");
          return false;
        }
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        $(e.target).val(price);
        var prefix = $(e.target).attr("id").split(".")[0];
        var ajaxUrl = "assistantStat.do?method=updateProductAchievement";
        var ajaxData = {
          idList: $("#" + prefix + "\\.productLocalInfoId").val(),
          achievementType: $("#" + prefix + "\\.achievementType").val(),
          achievementAmount: price
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
          if (result) {
            if (result.success) {
              $("#totalShopAchievementConfig").text(result.data);

            } else {
            }
          }
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#totalConfigNumSpan").live("click", function() {
    $("#allServiceConfigDiv").css("display", "block");
    $("#allServiceDiv").css("display", "none");
    $("#serviceConfigAchievementTable tr:not(:first)").remove();

    var data = {
      maxRows :25,
      startPageNo:1
    }
    var url = "assistantStat.do?method=getServiceAchievementByPager";

    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {

      if (jsonStr && jsonStr != "") {
        initServiceConfigAchievementTable(jsonStr, "serviceConfigAchievementTable");
        initPages(jsonStr, "dynamicalConfigService", url, '', "initServiceConfigAchievementTable", '', '', data, '');

      }
    });
  });

  $("#totalNumSpan").live("click", function() {

    $("#serviceAchievementTable tr:not(:first)").remove();

    $("#allServiceConfigDiv").css("display", "none");
    $("#allServiceDiv").css("display", "block");

    var url = 'assistantStat.do?method=searchServiceConfig',
        params = {startPageNo:1, maxRows:25};
    APP_BCGOGO.Net.asyncAjax({
      type:"POST",
      url:url,
      data:params,
      cache:false,
      dataType:"json",
      success:function (jsonStr) {
        if (jsonStr && jsonStr != "") {
          initServiceAchievementTable(jsonStr, "serviceAchievementTable");
          initPages(jsonStr, "dynamicalService", url, '', "initServiceAchievementTable", '', '', params, '');
        }
      }
    });
  });
});

function searchSearch() {


  var ajaxData = {
    serviceName : $("#serviceName").val(),
    serviceId : $("#serviceId").val(),
    categoryName : $("#categoryName").val(),
    categoryId : $("#categoryId").val()
  };

  $("#allServiceConfigDiv").css("display", "none");
  $("#allServiceDiv").css("display", "block");


  var ajaxUrl = "assistantStat.do?method=searchServiceConfig";
  bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
  bcgogoAjaxQuery.ajaxQuery(function(json) {
    initServiceAchievementTable(json, "serviceAchievementTable");
    initPages(json, "dynamicalService", ajaxUrl, '', "initServiceAchievementTable", '', '', ajaxData, '');
  });
}


function initServiceAchievementTable(json, tableId) {

  if (tableId == undefined || tableId == null) {
    tableId = "serviceAchievementTable";
  }
  $("#" + tableId + " tr:not(:first)").remove();

  if (json == null || json[0] == null) {
    return;
  }

  var tr = ' <tr class="space"><td colspan="6"></td></tr>';
  $("#" + tableId).append($(tr));

  var serviceDTOs = json[0].serviceDTOs;
  var str = '';

  for (var i = 0; i < serviceDTOs.length; i++) {
    var serviceDTO = serviceDTOs[i];

    var serviceId = serviceDTO.idStr;
    var name = serviceDTO.name;
    var categoryName = G.normalize(serviceDTOs.categoryName, "");
    var standardHours = G.normalize(serviceDTOs.standardHours, "");
    var standardUnitPrice = G.normalize(serviceDTOs.standardUnitPrice, "");
    var achievementType = G.normalize(serviceDTOs.achievementType, "");
    var achievementAmount = G.normalize(serviceDTOs.achievementAmount, "");
    str += '<tr class="titBody_Bg">';

    str += '<td style="padding-left:10px;"> ' + name + '</td>'
    str += '<input id="serviceDTOs' + i + '.serviceId" name="serviceDTOs[' + i + '].serviceId" type="hidden"  value="' + serviceId + '"/>'
        + '<td>' + categoryName + '</td>'
        + '<td>' + standardHours + '</td>'
        + '<td>' + standardUnitPrice + '</td>';
    if (achievementType == 'AMOUNT') {
      str += '<td><select id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect"><option>按金额</option><option>按比率</option>';
    } else {
      str += '<td><select id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect" class="txt selec_jin"><option>按比率</option><option>按金额</option>';
    }
    str += '<td><input type="text"  id="serviceDTOs' + i + '.achievementAmount" class="ti_chen_input" ';
    if (achievementAmount != null && achievementAmount != "") {
      str += ' value = ' + achievementAmount;
    }
    str += 'class="txt ti_chen"/></td>';

  }

  $("#" + tableId).append($(str));

}


function initServiceConfigAchievementTable(json, tableId) {

  if (tableId == undefined || tableId == null) {
    tableId = "serviceConfigAchievementTable";
  }
  $("#" + tableId + " tr:not(:first)").remove();

  if (json == null || json[0] == null) {
    return;
  }

  var tr = ' <tr class="space"><td colspan="6"></td></tr>';
  $("#" + tableId).append($(tr));

  var serviceDTOs = json[0].serviceDTOs;
  var str = '';

  for (var i = 0; i < serviceDTOs.length; i++) {
    var serviceDTO = serviceDTOs[i];

    var serviceId = serviceDTO.idStr;
    var name = serviceDTO.name;
    var categoryName = G.normalize(serviceDTOs.categoryName, "");
    var standardHours = G.normalize(serviceDTOs.standardHours, "");
    var standardUnitPrice = G.normalize(serviceDTOs.standardUnitPrice, "");
    var achievementType = G.normalize(serviceDTOs.achievementType, "");
    var achievementAmount = G.normalize(serviceDTOs.achievementAmount, "");
    str += '<tr class="titBody_Bg">';

    str += '<td style="padding-left:10px;"> ' + name + '</td>'
    str += '<input id="serviceDTOs' + i + '.serviceId" name="serviceDTOs[' + i + '].serviceId" type="hidden"  value="' + serviceId + '"/>'
        + '<td>' + categoryName + '</td>'
        + '<td>' + standardHours + '</td>'
        + '<td>' + standardUnitPrice + '</td>';
    if (achievementType == 'AMOUNT') {
      str += '<td><select  id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect"><option>按金额</option><option>按比率</option>';
    } else {
      str += '<td><select id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect"><option>按比率</option><option>按金额</option>';
    }
    str += '<td><input type="text" id="serviceDTOs' + i + '.achievementAmount" class="ti_chen_input"';
    if (achievementAmount != null && achievementAmount != "") {
      str += ' value = ' + achievementAmount;
    }
    str += 'class="txt ti_chen"/></td>';

  }

  $("#" + tableId).append($(str));

}

jQuery().ready(function() {

  jQuery("#serviceName").bind("click focus keyup", function(event) {

    var keyCode = event.keyCode || event.which;
    if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
      return;
    }

    if ($(this).val() != $(this).attr("hiddenValue")) {
      $("#" + this.id.split(".")[0] + "\\.serviceId").val("");
      $(this).removeAttr("hiddenValue");
    }

    var obj = this;

    droplistLite.show({
      event: event,
      isEditable: "true",
      isDeletable: "true",
      hiddenId: "serviceId",
      id: "id",
      name: "name",
      data: "txn.do?method=searchService",
      onsave: {
        callback: function(event, index, data, hook) {

          var _id = data.id;
          var _name = $.trim(data.label);

          var flag = false;
          var result = "";

          if (_name.length <= 0) {
            nsDialog.jAlert("施工内容不能为空");
            return false;
          }

          APP_BCGOGO.Net.syncAjax({
            url: "category.do?method=checkServiceNameRepeat",
            dataType: "json",
            data: {
              serviceName: _name,
              serviceId: _id
            },
            success: function(data) {
              if ("error" == data.resu) {
                result = data.resu;
              }
            }
          });

          if (result == 'error') {
            nsDialog.jAlert("服务名已存在");
            return false;
          }
          if (result == 'inUse') {
            nsDialog.jAlert("服务正在被进行中的单据使用，无法修改");
            return false;
          }

          //Check if this item is disabled.
          var checkResult,checkServiceId;
          APP_BCGOGO.Net.syncAjax({
            url: "category.do?method=checkServiceDisabled",
            dataType: "json",
            data: {
              serviceName: _name
            },
            success: function(data) {
              checkResult = data.resu;
              checkServiceId = data.serviceId ? data.serviceId : "";
            }
          });

          if (checkResult == "serviceDisabled") {
            nsDialog.jConfirm("此项目以前被删除过，是否恢复？", null, function(_result) {
              if (_result) {
                window.document.location = "category.do?method=updateServiceStatus&serviceId=" + checkServiceId;
              }
            });
          } else {
            //request save result & handler.
            APP_BCGOGO.Net.syncPost({
              url: "category.do?method=ajaxUpdateServiceName",
              data: {
                serviceId: _id,
                serviceName: _name,
                now: new Date()
              },
              dataType: "json",
              success: function(_result) {

                //success.
                if (_result.success) {
                  data.label = _name;
                  data.categoryName = _name;

                  //get the event
                  var $obj = $(obj);

                  //遍历item 把此id的name 和hiddenvalue都变为最新的name
                  // $obj.each(function() {
                  //     var categoryId = _hiddenValue;

                  //     if(categoryId == _id) {
                  //         $(this).val(_name);
                  //         $(this).attr("hiddenValue", _name);
                  //     }
                  // });

                  //pop message & reload the page.
                  nsDialog.jAlert(_result.msg, _result.title);
                }

                //fail.
                else if (!_result.success) {
                  nsDialog.jAlert(_result.msg, _result.title, function() {
                    data.label = data.categoryName;
                  });
                }

                //exception.
                else {
                  nsDialog.jAlert("数据异常！");
                }
              },
              //request error.
              error: function() {
                nsDialog.jAlert("保存失败！");
              }
            });
          }
        }
      },
      ondelete: {
        callback: function(event, index, data) {
          var serviceId = data.id;
          var serviceName = data.label;

          var deleteFlag = true;
          var url = "category.do?method=checkServiceUsed";
          APP_BCGOGO.Net.syncPost({
            url: url,
            data: {
              serviceId: serviceId
            },
            dataType: "json",
            success: function(data) {
              if ("error" != data.resu) {
                deleteFlag = false;
              } else {
                nsDialog.jAlert("此服务项目已被使用，不能删除！");
              }
            }
          });

          if (!deleteFlag) {

            //get the request result.
            var _result = APP_BCGOGO.Net.syncGet({
              url: "category.do?method=ajaxDeleteService",
              data: {
                serviceId: data.id,
                now: new Date()
              },
              dataType: "json"
            });

            //when failed and successed.
            if (!_result.success) {
              nsDialog.jAlert(_result.msg, _result.title);
            } else if (_result.success) {
              nsDialog.jAlert(_result.msg, _result.title);
            } else {
              nsDialog.jAlert("数据异常！");
            }
          }
        }
      }
    });
  });

  jQuery("#categoryName")._dropdownlist('businessCategary');

  $(document).click(function(e) {
    var e = e || event;
    var target = e.srcElement || e.target;
    var idStr = target.id;
    if (idStr != "div_service") {
      $("#div_service").css("display", "none");
    }
    if (idStr.indexOf("category") < 0) {
      $(".i_scroll_percentage").hide();
    }

  });
});