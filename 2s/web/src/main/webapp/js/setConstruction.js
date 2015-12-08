/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 下午10:59
 * To change this template use File | Settings | File Templates.
 */
jQuery().ready(function() {


  $(".achievementAmountInput,#setAchievementAmount,#achievementAmount,#newStandardHours,#newStandardUnitPrice").live("keyup blur", function (event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtedPriceFilter(event.target.value);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value);
      }
  });


  $(".achievementTypeStrSpanSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        var prefix = $(e.target).attr("id").split(".")[0];

        if ($(this).val() == "RATIO") {
          $("#" + prefix + "\\.achievementAmount").val("0%");
        } else {
          $("#" + prefix + "\\.achievementAmount").val("0");
        }

      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#newAchievementType").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        if ($(this).val() == "RATIO") {
          $("#achievementAmount").val("0%");
        } else {
          $("#achievementAmount").val("0");
        }
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#setAchievementType").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        if ($(this).val() == "RATIO") {
          $("#setAchievementAmount").val("0%");
        } else {
          $("#setAchievementAmount").val("0");
        }
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });



   $(".achievementAmountInput").live("blur",
      function (e) {
        var prefix = $(e.target).attr("id").split(".")[0];

        if ($("#" + prefix + "\\.achievementType").val() == "RATIO") {
            $("#" + prefix + "\\.achievementAmount").val() && $("#" + prefix + "\\.achievementAmount").val($("#" + prefix + "\\.achievementAmount").val().replace("%", "") + "%");
        } else {
          $("#" + prefix + "\\.achievementAmount").val($("#" + prefix + "\\.achievementAmount").val().replace("%", ""));
        }
      }).live("focus", function () {
        $(this).val($(this).val().replace("%", ""));
           $(this).val() == 0 && $(this).val('');
      });


  $("#setAchievementAmount").live("blur",
      function (e) {
        if ($("#setAchievementType").val() == "RATIO") {
          $("#setAchievementAmount").val($("#setAchievementAmount").val().replace("%", "") + "%");
        } else {
          $("#setAchievementAmount").val($("#setAchievementAmount").val().replace("%", ""));
        }
      }).live("focus", function () {
        $(this).val($(this).val().replace("%", ""));
      });

  $("#achievementAmount").live("blur",
      function (e) {
        if ($("#newAchievementType").val() == "RATIO") {
          $("#achievementAmount").val($("#achievementAmount").val().replace("%", "") + "%");
        } else {
          $("#achievementAmount").val($("#achievementAmount").val().replace("%", ""));
        }
      }).live("focus", function () {
        $(this).val($(this).val().replace("%", ""));
      });


  $("#newStandardHours,#newStandardUnitPrice").live("keyup blur", function(event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
      }
  });

  $(".standardHoursInput,.standardUnitPriceInput").live("keyup blur", function(event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
      }
  });


  jQuery("#serviceName").bind("click focus keyup", function(event) {

        var keyCode = event.keyCode || event.which;
        if(keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
            }

        if($(this).val() != $(this).attr("hiddenValue")) {
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

                    if(_name.length <= 0) {
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
                            if("error" == data.resu) {
                                result = data.resu;
                            }
                        }
                    });

                    if(result == 'error') {
                        nsDialog.jAlert("服务名已存在");
                        return false;
                    }
                    if(result == 'inUse'){
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

                    if(checkResult == "serviceDisabled") {
                        nsDialog.jConfirm("此项目以前被删除过，是否恢复？", null, function(_result) {
                            if(_result) {
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
                                if(_result.success) {
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
                                else if(!_result.success) {
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
                            if("error" != data.resu) {
                                deleteFlag = false;
                            } else {
                                nsDialog.jAlert("此服务项目已被使用，不能删除！");
                            }
                        }
                    });

                    if(!deleteFlag) {

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
                        if(!_result.success) {
                            nsDialog.jAlert(_result.msg, _result.title);
                        } else if(_result.success) {
                            nsDialog.jAlert(_result.msg, _result.title);
                        } else {
                            nsDialog.jAlert("数据异常！");
                        }
                    }
                }
            },
            afterSelected: function (event, index, data, hook) {
                $(obj).val(data.label);
              if ($("#pageType").val() == "setConstructions") {
                doSearch();
              }
            },
            afterKeySelected: function (event, index, data, hook) {
                $(obj).val(data.label);
              if ($("#pageType").val() == "setConstructions") {
                doSearch();
              }
            }
        });
    });

    $("#categoryName,#newCategoryName,.categoryNameInput").live("click focus keyup", function (event) {

        var keyCode = event.keyCode || event.which,
            keyName = G.keyNameFromKeyCode(keyCode);
        if (G.contains(keyName, ["up", "down", "left", "right"])) {
            return;
        }

        if ($(this).val() != $(this).attr("hiddenValue")) {
            $("#" + this.id.split(".")[0] + "\\.businessCategoryId").val("");
            $(this).removeAttr("hiddenValue");
        }

        var obj = this;

        droplistLite.show({
            event: event,
            isEditable: "true",
            isDeletable: "true",
            elementId: "businessCategoryName",
            hiddenId: "businessCategoryId",
            id: "idStr",
            name: "label",
            keyword: "keyWord",
            data: "category.do?method=getCategory",
            loadSuccess: function (result) {
                var disabledArr = ["洗车", "美容", "精品", "机修", "装潢", "音响", "油漆", "精洗", "膜", "轮胎"];
                if (result.data) {
                    for (var i = 0; i < result.data.length; i++) {
                        for (var j = 0; j < disabledArr.length; j++) {
                            if (result.data[i].label == disabledArr[j]) {
                                result.data[i].isEditable = false;
                                result.data[i].isDeletable = false;
                                break;
                            }
                        }
                    }
                }
            },
            onsave: {
                id: "categoryId",
                name: "categoryName",
                url: "category.do?method=updateCategoryName",
                errMsg: "营业分类中已经有此分类！"
            },
            ondelete: {
                id: "categoryId",
                url: "category.do?method=deleteCategory"
            }, afterSelected: function (event, index, data, hook) {
                $(obj).val(data.label);
              if ($("#pageType").val() == "setConstructions" && this.id =="categoryName") {
                doSearch();
              }
            },
            afterKeySelected: function (event, index, data, hook) {
                $(obj).val(data.label);
              if ($("#pageType").val() == "setConstructions" && this.id =="categoryName") {
                doSearch();
              }
            }
        });
    });

    $(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        var idStr = target.id;
        if(idStr != "div_service") {
            $("#div_service").css("display", "none");
        }
        if(idStr.indexOf("category") < 0) {
          $(".i_scroll_percentage").hide();
        }

    });


    jQuery("#percentageAmount,input[name$='.price'],input[name$='.percentageAmount']").live("keyup blur", function(event) {
        if(event.type == "focusout") event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
        else if(event.type == "keyup") if(event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
            event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            }
    });

    jQuery("#noCategory").bind("click", function(e) {
        window.location = "category.do?method=getServiceNoCategory";
    });
    jQuery("#noPercentage").bind("click", function(e) {
        window.location = "category.do?method=getServiceNoPercentage";
    });

    jQuery("#first_cont").bind("click", function(e) {
        window.location = "category.do?method=getServiceCategory";
    });

    jQuery("#fencount").bind("click", function(e) {
        window.location = "category.do?method=getCategoryItemSearch";
    })

    jQuery("input[id$='.categoryName']").each(function() {
      var idStr = jQuery(this).attr("id");
      var idSplit = idStr.split(".");
        jQuery(this).bind("click", function(e) {
            jQuery("#" + idSplit[0] + "\\.display").fadeIn("normal");
      });
        jQuery(this).bind("blur", function(e) {
            jQuery("#" + idSplit[0] + "\\.display").fadeOut();
      });
    });
    jQuery("input[id$='.light']").each(function() {
        if(jQuery(this).val() == "light") {
        var idStr = jQuery(this).attr("id");
        var idSplit = idStr.split(".");
        editService(this);
      }
    });
    jQuery("input[id$='.name']").each(function() {
        if(jQuery(this).val() == "洗车") {
        var idStr = jQuery(this).attr("id");
        var idSplit = idStr.split(".");
            jQuery(this).attr("readonly", true);
            jQuery("#" + idSplit[0] + "\\.categoryName").attr("readonly", true);
            jQuery("#" + idSplit[0] + "\\.categoryName").unbind();
      }
    });
    jQuery("#btnCommission").bind("click", function(e) {
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox_setServiceCategory")[0],
            'src': "category.do?method=setServiceAssistant"
    });
    });
    jQuery("#cancelPercentage").bind("click", function(e) {
        jQuery("#percentageShow").hide();
    });
    jQuery("#savePercentage").bind("click", function(e) {

        if(jQuery("#percentageAmount").val() == "") {
        jQuery("#percentageShow").hide();
        return false;
      }
        if(isNaN(jQuery("#percentageAmount").val()) || jQuery("#percentageAmount").val() < 0) {
        return false;
      }

        $(".price").each(function() {
            if(!$(this).val()) {
                $(this).val(0);
            }
        });

        $(".percentageAmount").each(function() {
            if(!$(this).val()) {
                $(this).val(0);
            }
        });

        jQuery("#categoryServiceSearchForm").attr("action", "category.do?method=updateServicePercentage" + "&percentageAmount=" + jQuery("#percentageAmount").val() + "&pageNo=" + jQuery("#pageNo").val() + "&totalRows=" + jQuery("#totalRows").val());
      jQuery("#categoryServiceSearchForm").submit();
    });
    jQuery("#setSale").bind("click", function(e) {
       setConstruction();
    });

    jQuery("#saveCategory").bind("click", function(e) {
        var $inputChecked = jQuery('input:radio[name="categoryRadio"]:checked').next(),
            val = "";

        if($inputChecked.length == 0 || !$inputChecked.val()) {
            $("#systemDialog").html("<p>请选择批量设定的类目</p>").dialog({
                    modal: true,
                    buttons: {
                        Ok: function() {
                            $(this).dialog("close");
      }
                    }
            }).find("p").css("text-align", "center");
            return;
        } else {
            val = $inputChecked.prev().val();
        }
      var flag = false;
      var categoryName = jQuery('input:radio[name="categoryRadio"]:checked').next().val();
        if(val == "") {
            jQuery(":input:radio").each(function() {
                if(jQuery(this).val() != "" && jQuery(this).next().val() == categoryName) {
            alert("新增的类目已经存在");
            flag = true;
                    return false;
          }
        });
      }
        if(flag) {
            return;
      }
      var isAllEmpty = true;
        jQuery("input[id$='.categoryName']").each(function() {
            if(jQuery(this).val() != "") {
          isAllEmpty = false;
        }
      });
        if(!isAllEmpty) {
            if(!confirm("该操作将会批量修改此页所有服务类目，是否确认修改？")) {
                return;
        }
      }
        jQuery("#categoryServiceSearchForm").attr("action", "category.do?method=updateServiceCategory" + "&categoryId=" + val + "&name=" + categoryName + "&pageNo=" + jQuery("#pageNo").val() + "&totalRows=" + jQuery("#totalRows").val());
      jQuery("#categoryServiceSearchForm").submit();
    });
    $("#categoryDisplay").bind("mouseover", function() {
            $(this).attr("mouseStatus", "mouseOver");
        }).bind("mouseout", function() {
            $(this).attr("mouseStatus", "mouseout");
        });

    $(".customCategory").live("keyup", function() {
        $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter(this.value));
    });

    $("#categoryServiceType").bind("change",function(){
        if($("#categoryServiceType").val() == "NO_CATEGORY_SERVICE"){
            $("#categoryName").val("");
        }
        doSearch();
    });

    $(".J_delete_service").live("click", function () {
        var thisTr = $(this).parent().parent();
        var serviceId = $(thisTr).find("input[id$='.id']").val();
        var serviceName = $(thisTr).find("input[id$='.name']").val();
        if ("洗车" == serviceName) {
            nsDialog.jAlert("洗车项目不能删除");
            return false;
        }
        var deleteFlag = true;
        var url = "category.do?method=checkServiceUsed";
        APP_BCGOGO.Net.syncPost({
            url: url,
            data: {
                serviceId: serviceId
            },
            dataType: "json",
            success: function (data) {
                if ("error" != data.resu) {
                    deleteFlag = false;
                } else {
                    nsDialog.jAlert("此服务项目已被使用，不能删除！");
                }
            }
        });
        if (deleteFlag) {
            return false;
        }
        nsDialog.jConfirm("确认删除该服务？", "确认删除", function (result) {
            if (result) {
                var url = "category.do?method=deleteService";
                APP_BCGOGO.Net.syncPost({
                    url: url,
                    data: {
                        serviceId: serviceId
                    },
                    dataType: "json",
                    success: function (json) {
                        if (json.success) {
                            flushThisPage("setConstructions");
                        }
                    }
                });
            }
        });
    });

    $(".J_edit_service").live("click", function () {
        var hiddenSpan = ["nameSpan", "categoryNameSpan", "standardHoursSpan", "standardUnitPriceSpan", "achievementTypeStrSpan", "achievementAmountSpan"];
        var showInput = ["name", "categoryName", "standardHours", "standardUnitPrice", "achievementType", "achievementAmount"];
        var $thisTd = $(this).parent();
        var $thisTr = $(this).parent().parent();
        var thisIdPrefix = $thisTr.find("input[id$='.id']").attr("id").split(".")[0];
        for (var i = 0; i < hiddenSpan.length; i++) {
            $thisTr.find("[id$='." + hiddenSpan[i] + "']").hide();
        }
        for (var i = 0; i < showInput.length; i++) {
            $thisTr.find("[id$='." + showInput[i] + "']").show();
        }
        var $serviceName = $thisTr.find("input[id$='.name']");
        if ("洗车" == $serviceName.val()) {
            $serviceName.attr("readOnly", true);
        }
        var html = '<a class="blue_color J_save_edit_service">保存</a>&nbsp;';
        html+= '<a class="blue_color J_cancel_edit_service">取消</a>';
        $thisTd.html(html);
    });

    $(".J_cancel_edit_service").live("click", function () {
        var showSpan = ["nameSpan", "categoryNameSpan", "standardHoursSpan", "standardUnitPriceSpan", "achievementTypeStrSpan", "achievementAmountSpan"];
        var hiddenInput = ["name", "categoryName", "standardHours", "standardUnitPrice", "achievementType", "achievementAmount"];
        var $thisTd = $(this).parent();
        var $thisTr = $(this).parent().parent();
        var thisIdPrefix = $thisTr.find("input[id$='.id']").attr("id").split(".")[0];
        for (var i = 0; i < hiddenInput.length; i++) {
            $thisTr.find("[id$='." + hiddenInput[i] + "']").hide();
            $thisTr.find("[id$='." + hiddenInput[i] + "']").val($thisTr.find("[id$='." + hiddenInput[i] + "']").attr("lastVal"));
        }
        for (var i = 0; i < showSpan.length; i++) {
            $thisTr.find("[id$='." + showSpan[i] + "']").show();
        }
        var html = '<a class="blue_color J_edit_service">编辑</a>&nbsp;<a class="blue_color J_delete_service">删除</a>';
        $thisTd.html(html);
    });

    $(".J_save_edit_service").live("click", function () {
        var thisTr = $(this).parent().parent();
        var data = {};
        data.id = $(thisTr).find("input[id$='.id']").val();
        data.name = $(thisTr).find("input[id$='.name']").val();
        data.categoryName = $(thisTr).find("input[id$='.categoryName']").val();
        data.standardHours = $(thisTr).find("input[id$='.standardHours']").val();
        data.standardUnitPrice = $(thisTr).find("input[id$='.standardUnitPrice']").val();
        data.achievementType = $(thisTr).find("[id$='.achievementType']").val();
        data.achievementAmount = $(thisTr).find("[id$='.achievementAmount']").val().replace("%", "");
        data.achievementAmount = APP_BCGOGO.StringFilter.inputtingPriceFilter(data.achievementAmount, 2);

        $(thisTr).find("[id$='.achievementAmount']").val(data.achievementAmount);

        if (G.Lang.isEmpty(data.name)) {
          nsDialog.jAlert("请输入施工内容");
          return false;
        }

        if (G.Lang.isEmpty(data.categoryName)) {
          nsDialog.jAlert("请输入营业分类");
          return false;
        }
        var result = "";

        var url = "category.do?method=checkServiceNameRepeat";
        APP_BCGOGO.Net.syncAjax({
            url: url,
            dataType: "json",
            data: {
                serviceName: data.name,
                serviceId: data.id
            },
            success: function (data) {
                result = data;
            }
        });
        if (result.resu == "error") {
            nsDialog.jAlert("服务名已存在");
            return false;
        }
        if (result.resu == "inUse") {
            var html = "<b>服务正在被进行中的单据使用，施工内容无法修改！</b><br/>";
            for (var i = 0, len = result.data.length; i < len; i++) {
                html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + result.data[i].url + "'>" + result.data[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                if (i % 2 == 1) {
                    html += "<br/>";
                }
            }
            nsDialog.jAlert(html, "修改失败");
            return false;
        }
        url = "category.do?method=checkServiceDisabled";
        var checkResult;
        var checkServiceId;
        APP_BCGOGO.Net.syncAjax({
            url: url,
            dataType: "json",
            data: {
                serviceName: data.name
            },
            success: function (data) {
                checkResult = data.resu;
                checkServiceId = data.serviceId ? data.serviceId : "";
            }
        });

        if ("serviceDisabled" == checkResult) {
            nsDialog.jConfirm("此项目以前被删除过，是否恢复？", null, function (returnVal) {
                if (returnVal) {
                    APP_BCGOGO.Net.syncPost({
                        url: "category.do?method=updateServiceStatus",
                        data: {
                            serviceId: checkServiceId
                        },
                        dataType: "json",
                        success: function (json) {
                            if (json.success) {
                                flushThisPage("setConstructions");
                            }
                        }
                    });
                } else {
                    APP_BCGOGO.Net.syncPost({
                        url: "category.do?method=updateServiceSingle",
                        data: data,
                        dataType: "json",
                        success: function (json) {
                            if (json.success) {
                                flushThisPage("setConstructions");
                            }
                        }
                    });
                }
            });
            return false;
        }
        APP_BCGOGO.Net.syncPost({
            url: "category.do?method=updateServiceSingle",
            data: data,
            dataType: "json",
            success: function (json) {
                if (json.success) {

                    $("#totalConfigNum").text(json.data)

                      flushThisPage("setConstructions");
                    }
                    }
        });
    });

    $("#addNewServiceBtn").bind("click",function(){
        $("#addNewServiceDialog").dialog({
            resizable: false,
            title: "新增施工内容",
            height: 150,
            width: 700,
            modal: true,
            closeOnEscape: false,
            close:function(){
                clearAddNewService();
            }
        });
    });

     $("#setAchievement").bind("click",function(){
        $("#setAchievementDialog").dialog({
            resizable: false,
            title: "批量设置提成",
            height: 150,
            width: 300,
            modal: true,
            closeOnEscape: false,
            close:function(){
                clearAddNewService();
            }
        });
    });


    $("#setAchievementBtn").bind("click", function() {
      if ($("#setAchievementAmount").val() == "") {
        alert("请输入提成金额");
        return false;
      }
      if ($("#setAchievementAmount").val().replace("%", "").length > APP_BCGOGO.StringFilter.inputtingPriceFilter($("#setAchievementAmount").val().replace("%", ""), 2).length) {
        nsDialog.jAlert("提成金额输入不正确");
        return false;
      }

      var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

      var data = {
        idList: $("#serviceIds").val(),
        achievementAmount: APP_BCGOGO.StringFilter.inputtingPriceFilter($("#setAchievementAmount").val().replace("%", ""), 2),
        achievementType:  $("#setAchievementType").val()
      }
      var url = "assistantStat.do?method=updateServiceAchievement";

      bcgogoAjaxQuery.setUrlData(url, data);
      bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        if (jsonStr && jsonStr.success) {
          $("#totalConfigNum").text(jsonStr.data)

          if($("#setAchievementType").val() == "RATIO"){
            $(".achievementAmountSpan").text($("#setAchievementAmount").val().replace("%", "") + "%");
            $(".achievementAmountInput").val($("#setAchievementAmount").val().replace("%", "") + "%");
            $(".achievementTypeStrSpan").text("按比率");
          }else{
            $(".achievementAmountSpan").text($("#setAchievementAmount").val());
            $(".achievementAmountInput").val($("#setAchievementAmount").val());
            $(".achievementTypeStrSpan").text("按金额");
          }
        }
        $("#setAchievementDialog").dialog("close");
      });
    });

    $("#setAchievementCancelBtn").bind("click",function(){
        $("#setAchievementDialog").dialog("close");
    });


    $("#cancelSaveNewService").bind("click",function(){
        $("#addNewServiceDialog").dialog("close");
    });
    $("#saveNewService").bind("click", function () {

        if ($("#newServiceName").val() == "") {
          nsDialog.jAlert("请输入施工内容");
          return false;
        }

        if ($("#newCategoryName").val() == "") {
          nsDialog.jAlert("请输入营业分类");
          return false;
        }

        var data = {};
        data.name = $("#newServiceName").val();
        data.standardHours = $("#newStandardHours").val();
        data.categoryName = $("#newCategoryName").val();
        data.standardUnitPrice = $("#newStandardUnitPrice").val();
        data.achievementType =  $("#newAchievementType").val();
        data.achievementAmount = APP_BCGOGO.StringFilter.inputtingPriceFilter($("#achievementAmount").val().replace("%", ""), 2);

        var result = "";
        if (G.Lang.isEmpty(data.name)) {
            nsDialog.jAlert("施工内容不能为空");
            return false;
        }

        var url = "category.do?method=checkServiceNameRepeat";
        APP_BCGOGO.Net.syncAjax({
            url: url,
            dataType: "json",
            data: {
                serviceName: data.name,
                serviceId: data.id
            },
            success: function (data) {
                result = data;
            }
        });
        if (result && result.resu == "error") {
            nsDialog.jAlert("服务名已存在");
            return false;
        }
        url = "category.do?method=checkServiceDisabled";
        var checkResult;
        var checkServiceId;
        APP_BCGOGO.Net.syncAjax({
            url: url,
            dataType: "json",
            data: {
                serviceName: data.name
            },
            success: function (data) {
                checkResult = data.resu;
                checkServiceId = data.serviceId ? data.serviceId : "";
            }
        });

        if ("serviceDisabled" == checkResult) {
            nsDialog.jConfirm("此项目以前被删除过，是否恢复？", null, function (returnVal) {
                if (returnVal) {
                    APP_BCGOGO.Net.syncPost({
                        url: "category.do?method=updateServiceStatus",
                        data: {
                            serviceId: checkServiceId
                        },
                        dataType: "json",
                        success: function (json) {
                            if (json.success) {
                                $("#addNewServiceDialog").dialog("close");
                                flushThisPage("setConstructions");
                            }
                        }
                    });
                    return false;
                } else {
                    APP_BCGOGO.Net.syncPost({
                        url: "category.do?method=addNewService",
                        data: data,
                        dataType: "json",
                        success: function (json) {
                            if (json.success) {
                                $("#addNewServiceDialog").dialog("close");
                                flushThisPage("setConstructions");
                            }
                        }
                    });
                    return false;
                }
            });
            return false;
        }
       //todo bugs by qxy
        APP_BCGOGO.Net.syncPost({
            url: "category.do?method=addNewService",
            data: data,
            dataType:  "json",
            success: function (json) {
                if (json.success) {
                    $("#addNewServiceDialog").dialog("close");
                    flushThisPage("setConstructions");
                }
            }
        });
    });

});

function addConstruction() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox")[0],
        'src': "category.do?method=createNewService"
    });
}

function setConstruction() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_setCategory")[0],
        'src': "category.do?method=setAllCategory"
    });
}

function doSearch() {
    var data = {
        serviceName : $.trim($("#serviceName").val()),
        categoryName: $.trim($("#categoryName").val()),
        categoryServiceType:$("#categoryServiceType").val(),
        startPageNo:1
    }
    var url = "category.do?method=getCategoryItemSearch";
    $("#tb_construction tr:gt(1)").remove();
    var html = '';
    html += '<tr class="titBody_Bg ">';
    html += '<td colspan="7">数据加载中...</td>';
    html += '</tr>';
    html += '<tr class="titBottom_Bg">';
    html += '<td colspan="7"></td>';
    html += '</tr>';
    $("#tb_construction ").append(html);
    APP_BCGOGO.Net.asyncPost({
        url: url,
        dataType: "json",
        data:data,
        success: function(json) {
            initServiceItem(json);
            initPage(json,"setConstructions",url,null,"initServiceItem",null,null,data,null);
        }
    });
}

function doServiceUpdate(dom) {
    var idStr = jQuery(dom).attr("id");
    var idSplit = idStr.split(".");
    var result = "";
    if(jQuery("#" + idSplit[0] + "\\.name").val() == "") {
        alert("施工内容不能为空");
      return false;
    }
    var url = "category.do?method=checkServiceNameRepeat";
    var serviceName = $("#" + $(dom)[0].id.split(".")[0] + "\\.name").val();
    var serviceId = $("#" + $(dom)[0].id.split(".")[0] + "\\.id").val();
    APP_BCGOGO.Net.syncAjax({
        url: url,
        dataType: "json",
        data: {
            serviceName: serviceName,
            serviceId: serviceId
        },
        success: function(data) {
            result = data;
        }
    });
    if(result.resu=="error") {
        nsDialog.jAlert("服务名已存在");
        return false;
    }
    if(result.resu=="inUse"){
        var html = "<b>服务正在被进行中的单据使用，施工内容无法修改！</b><br/>";
        for (var i = 0, len = result.data.length; i < len; i++) {
            html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + result.data[i].url + "'>" + result.data[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
            if (i % 2 == 1) {
                html += "<br/>";
            }
        }
        nsDialog.jAlert(html, "修改失败");
        return false;
    }
    if(isNaN(jQuery("#" + idSplit[0] + "\\.price").val()) || jQuery("#" + idSplit[0] + "\\.price").val() < 0) {
      return false;
    }
    if(isNaN(jQuery("#" + idSplit[0] + "\\.percentageAmount").val()) || jQuery("#" + idSplit[0] + "\\.percentageAmount").val() < 0) {
      return false;
    }

    url = "category.do?method=checkServiceDisabled";
    var checkResult;
    var checkServiceId;
    APP_BCGOGO.Net.syncAjax({
        url: url,
        dataType: "json",
        data: {
            serviceName: serviceName
        },
        success: function(data) {
        checkResult = data.resu;
            checkServiceId = data.serviceId ? data.serviceId : "";
        }
    });

    if("serviceDisabled" == checkResult) {
        if(confirm("此项目以前被删除过，是否恢复？")) {
            window.document.location = "category.do?method=updateServiceStatus&serviceId=" + checkServiceId;
        }

        return false;
    }

    //    jQuery(dom).css('display', 'none');
    jQuery("#" + idSplit[0] + "\\.light").val("");
    jQuery("#categoryServiceSearchForm").attr("action", "category.do?method=updateServiceSingle" + "&serviceId=" + jQuery("#" + idSplit[0] + "\\.id").val() + "&sName=" + encodeURIComponent(jQuery("#" + idSplit[0] + "\\.name").val()) + "&cName=" + encodeURIComponent(jQuery("#" + idSplit[0] + "\\.categoryName").val()) + "&price=" + jQuery("#" + idSplit[0] + "\\.price").val() + "&percentageAmount=" + jQuery("#" + idSplit[0] + "\\.percentageAmount").val() + "&pageNo=" + jQuery("#pageNo").val() + "&totalRows=" + jQuery("#totalRows").val());
    jQuery("#categoryServiceSearchForm").submit();
}

function doValueSet(dom, flag) {
    var value = jQuery(dom).html();
    jQuery(dom).parent().prev().val(jQuery.trim(value));
    if(flag) {
      changeInfo(jQuery(dom).parent().prev());
    }
    jQuery(dom).parent().fadeOut("normal");

}

function doCategoryValueSet(dom, categoryId) {
  var value = jQuery(dom).html();
  $("#categoryName").val(jQuery.trim(value));
  $("#categoryId").val(jQuery.trim(categoryId));
  jQuery(dom).parent().fadeOut("normal");
}

function changeInfo(dom) {
  var idStr = jQuery(dom).attr("id");
  var idSplit = idStr.split(".");
    jQuery("#" + idSplit[0] + "\\.btnUpdate").css("display", "block");
  jQuery("#" + idSplit[0] + "\\.light").val("light");
}

function divMouseOver(dom) {
    jQuery(dom).css('background-color', '#c8f0ff');
}

function divMouseOut(dom) {
    jQuery(dom).css('background-color', 'white');
}

function setCategory(dom) {
    if(jQuery(dom).val() == "洗车") {
    jQuery("#categoryName").val("洗车");
        jQuery("#categoryName").attr("readonly", true);
    jQuery("#categoryName").unbind();
    } else {
        jQuery("#categoryName").attr("readonly", false);
        jQuery("#categoryName").bind("click", function(e) {
        jQuery("#categoryDisplay").fadeIn("normal");
    });
        jQuery("#categoryName").bind("blur", function(e) {
            if($("#categoryDisplay").attr("mouseStatus") === "mouseOut") {
        jQuery("#categoryDisplay").fadeOut();
            }
    });
  }
}

function showCategoryShow(i) {
    $(".i_scroll_percentage").hide();
    $(".radioCategoryShow" + i).next().val("");
    $("#categoryShow" + i).show();
}

function cancleBtn(i) {
    $("#categoryShow" + i).hide();
}

function changeCategory(i) {
    var OldCategory = $("#categoryShow" + i).prev().val();

    var newCategory = "";
    var theLastChecked = false;
    $(".radioCategoryShow" + i).each(function(i) {
        if($(this)[0].checked) {
            if($(this).next().attr("type") == "hidden") {
                newCategory = $(this).attr("categoryName");
            } else {
                newCategory = $(this).next().val();
                theLastChecked = true
            }

            return false;
        }
    });

    var length = $(".radioCategoryShow" + i).length - 1;
    var flag = true;
    $(".radioCategoryShow" + i + ":lt(" + length + ")").each(function(i) {
        if(!theLastChecked) {
            return false;
        }
        if($.trim(newCategory).replace(/(\s+)/g, "").toUpperCase() == $.trim($(this).next().next().text()).replace(/(\s+)/g, "").toUpperCase()) {
            alert("类目有重复请重新选择！");
            flag = false;
            return;
        }
    });

    if(!flag) {
        return;
    }

    if($.trim(OldCategory).toUpperCase() == $.trim(newCategory).toUpperCase()) {
        $("#categoryShow" + i).hide();
        return;
    }

    $("#categoryShow" + i).prev().val(newCategory);

    changeInfo($("#categoryShow" + i).prev());

    $("#categoryShow" + i).hide();

}

function saveService(domObject) {
    doServiceUpdate(domObject);
}

function initServiceItem(json) {
    var result = json.result;
    if (!result.success) {
        nsDialog.jAlert("网络异常");
        return;
    }
    $("#tb_construction tr:gt(1)").remove();

    var serviceDTOs ="";
    if(json == null || json.categoryServiceSearchDTO == undefined){
        serviceDTOs = null;
    }else{
         serviceDTOs = json.categoryServiceSearchDTO.serviceDTOs;
    }


    var html = '';
    var serviceIds = "";
    if (serviceDTOs && serviceDTOs.length > 0) {
        for (var i = 0, len = serviceDTOs.length; i < len; i++) {
            var serviceDTO = serviceDTOs[i];
            var name = G.Lang.normalize(serviceDTO.name);
            var id = G.Lang.normalize(serviceDTO.idStr);
            var categoryName = G.Lang.normalize(serviceDTO.categoryName);
            var standardHours = dataTransition.simpleRounding(serviceDTO.standardHours,1);   //标准工时
            var standardUnitPrice = dataTransition.simpleRounding(serviceDTO.standardUnitPrice,2); //工时单价
            var achievementType = G.Lang.normalize(serviceDTO.achievementType); //提成类型
            var achievementTypeStr = G.Lang.normalize(serviceDTO.achievementTypeStr); //提成类型中文
            var achievementAmount = dataTransition.simpleRounding(serviceDTO.achievementAmount,1); //提成数额
            html += '<tr class="titBody_Bg ">';
            html += '<td style="padding-left:10px;">';
            html += '<span id="item'+i+'.nameSpan">'+name+'</span>';
            html += '<input class="txt" type="text" style="display: none" id="item'+i+'.name" value = "'+name+'"/>';
            html += '<input type="text" style="display: none" id="item'+i+'.id" value = "'+id+'" lastVal = "' + id + '"/>';
            html += '</td>';
            html += '<td>';
            html += '<span class="categoryNameSpan" id="item'+i+'.categoryNameSpan">'+categoryName+'</span>';
            html += '<input class="txt categoryNameInput" type="text" style="display: none" id="item'+i+'.categoryName" value = "'+categoryName+'" lastVal = "' + categoryName + '"/>';
            html += '</td>';
            html += '<td>';
            html += '<span id="item'+i+'.standardHoursSpan">'+standardHours+'</span>';
            html += '<input class="txt standardHoursInput " type="text" style="display: none" id="item'+i+'.standardHours" value = "'+standardHours+'" lastVal = "' + standardHours + '" data-filter-zero="true"/>';
            html += '</td>';
            html += '<td>';
            html += '<span id="item'+i+'.standardUnitPriceSpan">'+standardUnitPrice+'</span>';
            html += '<input class="txt standardUnitPriceInput" type="text" style="display: none" id="item'+i+'.standardUnitPrice" value = "'+standardUnitPrice+'" lastVal = "' + standardUnitPrice + '"data-filter-zero="true"/>';
            html += '</td>';
            html += '<td>';
            html += '<span class="achievementTypeStrSpan" id="item'+i+'.achievementTypeStrSpan">'+achievementTypeStr+'</span>';
            html += '<select class="txt txt_color achievementTypeStrSpanSelect" style="display: none" id="item'+i+'.achievementType">';

            if (achievementType == "RATIO") {
              html += '<option value="RATIO">按比率</option>';
              html += '<option value="AMOUNT">按金额</option>';
              achievementAmount = achievementAmount + "%";
            } else {
              html += '<option value="AMOUNT">按金额</option>';
              html += '<option value="RATIO">按比率</option>';
            }

            html += '</select>';
            html += '</td>';
            html += '<td>';
            html += '<span class="achievementAmountSpan" id="item'+i+'.achievementAmountSpan">'+achievementAmount+'</span>'
            html += '<input class="txt achievementAmountInput" type="text" style="display: none" id="item'+i+'.achievementAmount" value = "'+achievementAmount+'" lastVal = "' + achievementAmount + '" data-filter-zero="true"/>';
//            if (achievementType && achievementType == 'RATIO') {
//                html += '<span style="display: block" id="item' + i + '.RATIO">%</span>';
//            } else {
//                html += '<span style="display: none" id="item' + i + '.RATIO">%</span>';
//            }
            html += '</td>';
            html += '<td>';
            html += '<a class="blue_color J_edit_service">编辑</a>&nbsp;<a class="blue_color J_delete_service">删除</a>';
            html += '</td>';
            html += '</tr>';
            html += '<tr class="titBottom_Bg ">';
            html += '<td colspan="7"></td>';
            html += '</tr>';
            serviceIds += id+ ",";
        }
    } else {
        html += '<tr class="titBody_Bg ">';
        html += '<td colspan="7">暂无数据！</td>';
        html += '</tr>';
        html += '<tr class="titBottom_Bg">';
        html += '<td colspan="7"></td>';
        html += '</tr>';
    }
    $("#tb_construction").append(html);

  $("#serviceIds").val(serviceIds);
}

function flushThisPage(dynamical) {
    var url = $("#url" + dynamical).val();
    var data = strToJson($("#data" + dynamical).val());
    var initFunction = $("#functionName" + dynamical).val();
    APP_BCGOGO.Net.asyncPost({
        url: url,
        dataType: "json",
        data:data,
        success: function(json) {
            eval(initFunction)(json);
            initPage(json,dynamical,url,null,initFunction,null,null,data,null);
        }
    });
}

function clearAddNewService(){
    $("#newServiceName").val("");
    $("#newCategoryName").val("");
    $("#newStandardHours").val("0");
    $("#newStandardUnitPrice").val("0");
    $("#achievementAmount").val("0");
}