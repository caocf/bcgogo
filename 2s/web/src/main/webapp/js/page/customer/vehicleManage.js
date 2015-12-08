/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 14-2-7
 * Time: 下午12:05
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function () {
    $("#maintainIntervalsDay,#maintainIntervalsMileage").bind("keyup", function () {
        if(G.isNotEmpty($(this).val()))
            $(this).val(APP_BCGOGO.StringFilter.inputtingNumberFilter($(this).val(), 1)*1);
    });

    $("#maintainIntervalsDay").bind("click",function(){
        var dropList = App.Module.droplist;
        dropList.setUUID(G.generateUUID());
        var $domObject = $(this);
        var result = {
            uuid: dropList.getUUID(),
            data: [{label: "3"},{label: "7"},{label: "10"},{label: "15"},{label: "30"}]
        }

        dropList.show({
            "height":120,
            "isIgnoreMinWidth":true,
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.label);
                dropList.hide();
                searchVehicleList();
            }
        });
    });
    $(".J_vehicleBrandSuggestion")
        .bind('click', function () {
            getVehicleBrandSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getVehicleBrandSuggestion($(this),eventKeyCode);
            }
        });


    function getVehicleBrandSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var currentSearchField =  $domObject.attr("searchField");
        var dropList = App.Module.droplist;
        dropList.setUUID(G.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField:currentSearchField,
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "product.do?method=searchVehicleSuggestionForGoodsBuy";
        App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    dropList.hide();
                    searchVehicleList();
                }
            });
        });
    }


    $("#licenceNo")
        .bind('click', function () {
            getVehicleLicenceNoSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getVehicleLicenceNoSuggestion($(this));
            }
        });

    $("#engineNo,#chassisNumber")
        .bind('click', function () {
          getVehicleEngineNoClassNoSuggestion($(this));
        })
        .bind('keyup', function (event) {
          var eventKeyCode = event.which || event.keyCode;
          if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            getVehicleEngineNoClassNoSuggestion($(this));
          }
        });

      function getVehicleEngineNoClassNoSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var currentSearchField = $domObject.attr("searchField");

        var droplist = APP_BCGOGO.Module.droplist;
        droplist.setUUID(GLOBAL.Util.generateUUID());
        var ajaxUrl = "searchInventoryIndex.do?method=getVehicleEngineNoClassNoSuggestion";
        var ajaxData = {
          searchWord: searchWord,
          searchField: currentSearchField,
          uuid: droplist.getUUID()
        };
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
          droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
              $domObject.val(data.label);
              $domObject.css({"color": "#000000"});
              droplist.hide();
              searchVehicleList();
            }
          });
        });
      }



    function getVehicleLicenceNoSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
//        if (GLOBAL.Lang.isEmpty(searchWord)) return;
        var droplist = APP_BCGOGO.Module.droplist;
        droplist.setUUID(GLOBAL.Util.generateUUID());
        var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
        var ajaxData = {
            searchWord: searchWord,
            uuid: droplist.getUUID()
        };
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            droplist.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    droplist.hide();
                    searchVehicleList();
                }
            });
        });
    }

    $("#customerInfo")
        .bind('click', function () {
            getCustomerSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerSuggestion($(this));
            }
        });

    function getCustomerSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = App.Module.droplist;
        dropList.setUUID(G.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField: "info",
            searchFieldStrategies:"searchIncludeMemberNo",
            customerOrSupplier: "customer",
            titles: "name,contact,mobile,memberNo",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector": $domObject,
                "autoSet": false,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.details.name);
                    $domObject.css({"color": "#000000"});
                    dropList.hide();
                    searchVehicleList();
                }
            });
        });

    }

    $("#vehicleLastConsumeTimeStartStr,#vehicleLastConsumeTimeEndStr")
        .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":false,
            "changeYear":true,
            "showHour":false,
            "showMinute":false,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":""
        })
        .blur(function() {
            var vehicleLastConsumeTimeStartStr = $("#vehicleLastConsumeTimeStartStr").val();
            var vehicleLastConsumeTimeEndStr = $("#vehicleLastConsumeTimeEndStr").val();
            if (G.isEmpty(vehicleLastConsumeTimeStartStr)|| G.isEmpty(vehicleLastConsumeTimeEndStr)) return;
            if (APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeStartStr) && APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeEndStr)) {
                return;
            } else {
                if (vehicleLastConsumeTimeStartStr > vehicleLastConsumeTimeEndStr) {
                    $("#vehicleLastConsumeTimeEndStr").val(vehicleLastConsumeTimeStartStr);
                    $("#vehicleLastConsumeTimeStartStr").val(vehicleLastConsumeTimeEndStr);
                }
            }
        })
        .bind("click", function() {
            $(this).blur();
        })
        .change(function() {
            var vehicleLastConsumeTimeStartStr = $("#vehicleLastConsumeTimeStartStr").val();
            var vehicleLastConsumeTimeEndStr = $("#vehicleLastConsumeTimeEndStr").val();
            $("a[name='my_date_select']").removeClass("clicked");
            if (G.isEmpty(vehicleLastConsumeTimeStartStr)|| G.isEmpty(vehicleLastConsumeTimeEndStr)) return;
            if (APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeStartStr) && APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeEndStr)) {
                return;
            } else {
                if (vehicleLastConsumeTimeStartStr > vehicleLastConsumeTimeEndStr) {
                    $("#vehicleLastConsumeTimeEndStr").val(vehicleLastConsumeTimeStartStr);
                    $("#vehicleLastConsumeTimeStartStr").val(vehicleLastConsumeTimeEndStr);
                }
            }
        });

    $(".J_sendMsg").bind("click",function(e){
        e.preventDefault();
        if($(this).attr("data-type")=="mobile" && $("#totalHasMobileNumber").text()*1>0){
            $("#searchStrategies").val("SEARCH_STRATEGY_HAS_MOBILE");
        }else if($(this).attr("data-type")=="obd" && $("#totalHasOBDNumber").text()*1>0){
            $("#searchStrategies").val("SEARCH_STRATEGY_OBD");
        }else{
            return;
        }
        $(".J_initialCss").placeHolder("clear");
        $("#searchVehicleListForm").attr("target","_blank");
        var oldAction = $("#searchVehicleListForm").attr("action");
        $("#searchVehicleListForm").attr("action","customer.do?method=sendMsgByVehicleSearchConditionDTO");
        $("#searchVehicleListForm").submit();
        $("#searchVehicleListForm").attr("action",oldAction);
        $("#searchStrategies").val("");
        $("#searchVehicleListForm").removeAttr("target");
        $(".J_initialCss").placeHolder("reset");
    });

    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $("a[name='my_date_select']").removeClass("clicked");
        $(".J_initialCss").placeHolder("reset");
    });

    function searchVehicleList(){
        $(".J_span_sort_vehicleManageList").each(function () {
            $(this).removeClass("hover");
            $(this).find(".J_sort_span_image").removeClass("arrowUp").addClass("arrowDown");
            $(this).attr("currentSortStatus", "Desc");
            $(this).find(".J_sort_div_info_val").html($(this).attr("ascContact"));
        });
        $(".J_initialCss").placeHolder("clear");

        var paramForm = $("#searchVehicleListForm").serializeArray();
        var param = {};
        $.each(paramForm, function (index, val) {
            param[val.name] = val.value;
        });

        var isEmpty = true;
        $(".J_clear_input").each(function () {
            if(G.isNotEmpty($(this).val())){
                isEmpty = false;
                return false;
            }
        });

        if(isEmpty){
            var $domObject = $("[sortFiled='vehicleLastConsumeTime']");
            $domObject.addClass("hover");
            $domObject.find(".J_sort_span_image").removeClass("arrowUp").addClass("arrowDown");
            $domObject.attr("currentSortStatus", "Desc");
            $domObject.find(".J_sort_div_info_val").html($domObject.attr("ascContact"));
            param["sortStatus"] = $domObject.attr("sortFiled") + $domObject.attr("currentSortStatus");
        }

        $(".J_initialCss").placeHolder("reset");
        APP_BCGOGO.Net.syncPost({
            url: "customer.do?method=getVehicleList",
            dataType: "json",
            data:param,
            success: function (result) {
                drawVehicleListTable(result);
                initPages(result, "vehicleManageList", "customer.do?method=getVehicleList", '', "drawVehicleListTable", '', '', param, '');
            },
            error: function () {
                $("#vehicleListTable").find(".J_ItemBody").remove();
                $("#vehicleListTable").append($("<tr class='titBody_Bg J_ItemBody'><td colspan='11' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>数据异常，请刷新页面！</span></td></tr>"));
                $("#totalNumber").text("0");
                $("#totalHasMobileNumber").text("0");
                $("#totalConsumeAmount").text("0");
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });
    }
    $("#searchVehicleBtn").bind("click",function(){
        searchVehicleList();
    });

    $(".J_vehicleConsumeHistory").live("click",function(e){
        e.preventDefault();
        var licenceNo = $(this).closest("tr").attr("data-licenceno");
        if(G.isNotEmpty(licenceNo)){
            var url = "inquiryCenter.do?method=inquiryCenterIndex&pageType=vehicleManageList&startDateStr=&vehicleNumber="+licenceNo;
            window.open(encodeURI(url), "_blank");
        }
    });
    $(".J_vehicleLastConsumeOrder").live("click",function(e){
        e.preventDefault();
        var lastConsumeOrderIdStr = $(this).attr("data-lastconsumeorderidstr");
        var lastConsumeOrderType = $(this).attr("data-lastconsumeordertype");
        if(G.isNotEmpty(lastConsumeOrderIdStr)){
            if(lastConsumeOrderType=="SALE"){
                window.open("sale.do?method=getSalesOrder&salesOrderId="+lastConsumeOrderIdStr, "_blank");
            }else if(lastConsumeOrderType=="REPAIR"){
                window.open("txn.do?method=getRepairOrder&repairOrderId="+lastConsumeOrderIdStr, "_blank");
            }else if(lastConsumeOrderType=="WASH_BEAUTY"){
                window.open("washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId="+lastConsumeOrderIdStr, "_blank");
            }

        }
    });

    $("#sendMsgPromptForm").find("input[name='smsFlag'],input[name='appFlag']").bind("click", function (e) {
        if($("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").length<2){
            $("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").attr("disabled","disabled");
        }else{
            $("#sendMsgPromptForm").find("input[name='smsFlag']:checked,input[name='appFlag']:checked").removeAttr("disabled");
        }
    });

    $("#sendMsgPromptForm").find("input[name='mobile']").bind("blur",function(e){
        checkSendMsgPromptMobile();
    });

    function checkSendMsgPromptMobile(){
        var mobile = $("#sendMsgPromptForm").find("input[name='mobile']").val();
        if (G.isEmpty(mobile)) {
            $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").hide();
            $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").show();
            $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").hide();
            return false;
        }
        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").show();
            $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").hide();
            $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").hide();
            return false;
        }
        $("#sendMsgPromptForm").find("div[id='mobileWrongInfo']").hide();
        $("#sendMsgPromptForm").find("div[id='mobileEmptyInfo']").hide();
        $("#sendMsgPromptForm").find("div[id='mobileRightInfo']").show();
        return true;
    }
    $(".J_sendMaintainMsg").live("click",function(e){
        e.preventDefault();
        var sendMobile = $(this).closest("tr").attr("data-send-mobile");
        var licenceNo = $(this).closest("tr").attr("data-licenceno");
        var result = APP_BCGOGO.Net.syncGet({"url": "sms.do?method=getMobileMsgContent", data: {"licenceNo": licenceNo,"type":7}, dataType: "json"});
        if(G.isNotEmpty(result) && G.isNotEmpty(result.content)){
            $("#sendMsgPrompt").dialog({
                width: 430,
                modal: true,
                resizable:false,
                position:'center',
                open: function() {
                    $("#sendMsgPromptForm").find("div[id='vehicleMsgContent']").html(result.content);
                    $("#sendMsgPromptForm").find("input[name='licenceNo']").val(licenceNo);
                    $(".ui-dialog-titlebar", $(this).parent()).hide();
                    $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
                    if(G.isNotEmpty(sendMobile)){
                        $("#sendMsgPromptForm").find("input[name='mobile']").val(sendMobile);
                        $("#sendMsgPromptForm").find("input[name='mobile']").attr("disabled","disabled");
                    }
                    checkSendMsgPromptMobile();
                },
                close:function(){
                    $("#sendMsgPromptForm").find("input[name='smsFlag']").attr("disabled","disabled");
                    $("#sendMsgPromptForm").find("input[name='appFlag']").removeAttr("disabled");
                    $("#sendMsgPromptForm").find("input[name='mobile']").removeAttr("disabled");
                    $("#sendMsgPromptForm")[0].reset();
                }
            });
        }else{
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
    $("#sendMsgPromptBtn").bind("click",function(e){
        e.preventDefault();
        if (checkSendMsgPromptMobile()) {
            $("#sendMsgPromptForm").find("input[name='smsFlag']").removeAttr("disabled");
            $("#sendMsgPromptForm").find("input[name='appFlag']").removeAttr("disabled");
            $("#sendMsgPromptForm").find("input[name='mobile']").removeAttr("disabled");

            var paramForm = $("#sendMsgPromptForm").serializeArray();
            var param = {};
            $.each(paramForm, function (index, val) {
                param[val.name] = val.value;
            });
            $("#sendMsgPrompt").dialog("close");
            APP_BCGOGO.Net.asyncPost({
                url: "customer.do?method=sendVehicleMsg",
                dataType: "json",
                data:param,
                success: function (json) {
                    if(G.isNotEmpty(json)){
                        if(json.success){
                            nsDialog.jAlert("短信发送成功！");
                        }else{
                            nsDialog.jAlert(json.msg);
                        }
                    }else{
                        nsDialog.jAlert("网络异常，请联系客服");
                    }
                },
                error: function () {
                    nsDialog.jAlert("网络异常，请联系客服");
                }
            });
        }
    });

    $(".J_closeSendMsgPrompt").bind("click",function(e){
        e.preventDefault();
        $("#sendMsgPrompt").dialog("close");
    });

  $("#vehicle_bind_cancelBtn").click(function(){
    $("#obd_bind_div").dialog("close");
  });

   $("#vehicle_bind_okBtn").click(function(){
    if($(this).attr("lock")){
      return;
    }
    var imei=$("#imei_input").val();
    var mobile=$("#sim_no_input").val();
    var vehicleId=$("#bind_vehicle_id").val();
    if(G.isEmpty(imei)){
      nsDialog.jAlert("请输入IMEI号！");
      $(this).removeAttr("lock");
      return;
    }
    if(G.isEmpty(mobile)){
      nsDialog.jAlert("请输入SIM卡号！");
      $(this).removeAttr("lock");
      return;
    }
    if(G.isEmpty(vehicleId)){
      nsDialog.jAlert("车辆信息异常！");
      $(this).removeAttr("lock");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsCharacter(imei)) {
      nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
      $(this).removeAttr("lock");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
      nsDialog.jAlert("SIM卡号格式错误，请确认后重新输入！");
      $(this).removeAttr("lock");
      return;
    }
    $(this).attr("lock","lock");
    var _$me=$(this);
    APP_BCGOGO.Net.asyncGet({
      url:"OBDManager.do?method=gsmOBDBind",
      data:{
        imei:imei,
        mobile:mobile,
        vehicleId:vehicleId,
        now:new Date()
      },
      dataType:"json",
      success:function(result) {
        _$me.removeAttr("lock");
        if(G.isEmpty(result)){
          nsDialog.jAlert("保存异常！");
          return;
        }
        if(!result.success){
          nsDialog.jAlert(result.msg);
          return;
        }
        $("#searchVehicleBtn").click();
        $("#obd_bind_div").dialog("close");

      },
      error:function(){
        $(this).removeAttr("lock");
      }
    });

  });

  $(".vehicle_install_btn").live("click",function(){
    var vehicleId=$(this).closest("tr").find(".vehicle_id").val();
    $("#bind_vehicle_id").val(vehicleId);
    $("#obd_bind_div").dialog({
      width: 380,
      height:170,
      modal: true,
      draggable:false,
      resizable: true,
      title: "绑定OBD",
      close:function(){
        $("#imei_input").val("");
        $("#sim_no_input").val("");
        $("#bind_vehicle_id").val("");
      }

    });
  });


$("#imei_input,#sim_no_input").bind('focus',function () {
    searchOBDInfo(this, $(this).val());
  }).bind("input",function(){
      searchOBDInfo(this, $(this).val());
    });


    //初始化
    $(".J_initialCss").placeHolder();
    searchVehicleList();
});

function searchOBDInfo(dom, searchWord){
  var target_id=$(dom).attr("id");
  var droplist = APP_BCGOGO.Module.droplist;
  searchWord = searchWord.replace(/\s/g, '');
  var uuid = GLOBAL.Util.generateUUID();
  droplist.setUUID(uuid);
  if(G.isEmpty(searchWord)){
    return;
  }
  var data={now: new Date().getTime()};
  if(target_id=="imei_input"){
    data['imei']=searchWord;
  }else{
    data['mobile']=searchWord;
  }
  $.post('OBDManager.do?method=getShopOBDSuggestion', data,function (list) {
    droplist.show({
      "selector": $(dom),
      "data": {
        uuid: uuid,
        data:$.map(list, function (n) {
          if(target_id=="imei_input"){
            n.label = n.imei;
          }else{
            n.label = n.mobile;
          }
          return n;
        })
      },
      "onSelect": function (event, index, data, hook) {
        if(target_id=="imei_input"){
          $(hook).val(data.imei);
          $("#sim_no_input").val(data.mobile);
        }else{
          $(hook).val(data.mobile);
          $("#imei_input").val(data.imei);
        }

        droplist.hide();
      }
    });
  }, 'json');
}

function drawVehicleListTable(json){
    $("#vehicleListTable").find(".J_ItemBody").remove();
    if(json==null || json[0].vehicleDTOList==null || json[0].vehicleDTOList.length == 0 ){
        $("#vehicleListTable").append('<tr class="titBody_Bg J_ItemBody"><td colspan="11" style="text-align: left;padding-left: 10px;">暂无车辆数据!</td></tr>');
        $("#vehicleListTable").append('<tr class="titBottom_Bg J_ItemBody"><td colspan="11"></td></tr>');
        $("#totalNumber").text("0");
        $("#totalHasMobileNumber").text("0");
        $("#totalConsumeAmount").text("0");
        return;
    }
    $("#totalNumber").text(json[0].numFound);
    if(G.isNotEmpty(json[0].statNotNullCounts)){
        $("#totalHasMobileNumber").text(G.normalize(json[0].statNotNullCounts["IS_MOBILE_VEHICLE"],"0"));
    }else{
        $("#totalHasMobileNumber").text("0");
    }
    if(G.isNotEmpty(json[0].statAmounts)){
        $("#totalConsumeAmount").text(G.normalize(json[0].statAmounts["VEHICLE_TOTAL_CONSUME_AMOUNT"],"0"));
    }else{
        $("#totalConsumeAmount").text("0");
    }

    $.each(json[0].vehicleDTOList, function (index, vehicleDTO) {
        var sendMobile = G.normalize(vehicleDTO.mobile);
        if(G.isEmpty(sendMobile)&&!G.isEmpty(vehicleDTO.customerDTO)){
            sendMobile = G.normalize(vehicleDTO.customerDTO.mobile);
        }
        var tr = '<tr class="titBody_Bg J_ItemBody" data-licenceno="'+vehicleDTO.licenceNo+'" data-send-mobile="'+sendMobile+'">';
        tr += '     <td style="padding-left:10px;">';
         tr+='<input type="hidden" class="vehicle_id" value="'+vehicleDTO.idStr+'">';
        if (APP_BCGOGO.Permission.CustomerManager.VehicleDetail) {
          tr += '    <div class="line"><a class="blue_color" href="vehicleManage.do?method=toVehicleDetail&fromPage=vehicleList&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">' + vehicleDTO.licenceNo + '</a></div>';
        } else if (APP_BCGOGO.Permission.CustomerManager.CustomerModify) {
          tr += '    <div class="line"><a class="blue_color" href="unitlink.do?method=customer&customerId=' + vehicleDTO.customerDTO.idStr + '&vehicleId=' + vehicleDTO.idStr + '">' + vehicleDTO.licenceNo + '</a></div>';
        } else {
          tr += '  <div class="line">' + vehicleDTO.licenceNo + '</div>';
        }
        var temp ='';
//        if(G.isNotEmpty(vehicleDTO.obdId)){
//            temp += '<div class="customer-obd" title="已安装故障检测仪"></div>';
//        }
        if(G.isNotEmpty(vehicleDTO.customerDTO.memberDTO)){
            temp += '<div id="vip_'+vehicleDTO.idStr+'" class="customer-vip"></div>';
        }
        if(vehicleDTO.isObd){
            temp += '<span class="customer-obd" title="已安装故障检测仪"></span>';
        }else if(vehicleDTO.isApp){
            temp += '<span class="customer-app" title="手机APP用户"></span>';

        }
        if(G.isNotEmpty(temp)){
            tr += '<div>'+temp+'</div>';
        }
        tr += '</td>';

        temp ='';
        if(G.isNotEmpty(vehicleDTO.contact)){
            temp += '<div class="line">'+ vehicleDTO.contact+'</div>';
        }
        if(G.isNotEmpty(vehicleDTO.mobile)){
            temp += '<div class="line">'+ vehicleDTO.mobile+'</div>';
        }
        if(G.isNotEmpty(temp)){
            tr += '<td>'+temp+'</td>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }

        tr += '<td><div class="line">'+ G.normalize(vehicleDTO.customerDTO.name)+'</div>';
        if(G.isNotEmpty(vehicleDTO.customerDTO.mobile)){
            tr += '<div class="line">'+ vehicleDTO.customerDTO.mobile+'</div>';
        }
        tr += '</td>';
        temp ='';
        if(G.isNotEmpty(vehicleDTO.brand) || G.isNotEmpty(vehicleDTO.model)){
            temp += '<div class="line">'+ G.normalize(vehicleDTO.brand)+'&nbsp;'+G.normalize(vehicleDTO.model)+'</div>';
        }
        if(G.isNotEmpty(vehicleDTO.color)){
            temp += '<div class="line">'+ vehicleDTO.color+'</div>';
        }
        if(G.isNotEmpty(temp)){
            tr += '<td>'+temp+'</td>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }
        if(vehicleDTO.vehicleTotalConsumeCount*1>0){
            tr += '<td><div class="line"><a class="blue_color J_vehicleConsumeHistory">'+G.normalize(vehicleDTO.vehicleTotalConsumeCount,"0")+'次 <br />累计<span class="arialFont">&yen;</span>'+G.normalize(vehicleDTO.vehicleTotalConsumeAmount,"0")+'</a></div></td>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }
        if(G.isNotEmpty(vehicleDTO.vehicleLastConsumeTimeStr)){
            tr += '<td><a class="blue_color J_vehicleLastConsumeOrder" data-lastconsumeorderidstr="'+vehicleDTO.lastConsumeOrderIdStr+'" data-lastconsumeordertype="'+vehicleDTO.lastConsumeOrderType+'">'+vehicleDTO.vehicleLastConsumeTimeStr+'</a></td>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }

        if(G.isNotEmpty(vehicleDTO.obdMileage)){
            tr += '<td><div class="line">'+vehicleDTO.obdMileage+'km</td></div>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }

        temp ='';
        if(G.isNotEmpty(vehicleDTO.maintainMileage)){
            temp += '<div class="line">'+ vehicleDTO.maintainMileage+'km</div>';
        }
        if(G.isNotEmpty(vehicleDTO.maintainTimeStr)){
            temp += '<div class="line">'+ vehicleDTO.maintainTimeStr+'</div>';
        }

        if(G.isNotEmpty(temp)){
            tr += '<td>'+temp+'</td>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }
        temp ='';
        if(G.isNotEmpty(vehicleDTO.maintainIntervalsDays) && G.isNotEmpty(vehicleDTO.maintainIntervalsMileage)){
            temp += '<div style="float: left">';
            temp += '<div class="line">'+(vehicleDTO.maintainIntervalsMileage*1<0?'<span style="color: red">超出</span>':'<span style="color: #008000">还有</span>')+Math.abs(vehicleDTO.maintainIntervalsMileage)+'km</div>';
            temp += '<div class="line">'+(vehicleDTO.maintainIntervalsDays*1<0?'<span style="color: red">超出</span>':'<span style="color: #008000">还有</span>')+Math.abs(vehicleDTO.maintainIntervalsDays)+'天</div>';
            temp += '</div>';
        }else if(G.isNotEmpty(vehicleDTO.maintainIntervalsDays) && G.isEmpty(vehicleDTO.maintainIntervalsMileage)){
            temp += '<div class="line" style="float: left" >'+(vehicleDTO.maintainIntervalsDays*1<0?'<span style="color: red">超出</span>':'<span style="color: #008000">还有</span>')+Math.abs(vehicleDTO.maintainIntervalsDays)+'天</div>';
        }else if(G.isEmpty(vehicleDTO.maintainIntervalsDays) && G.isNotEmpty(vehicleDTO.maintainIntervalsMileage)){
            temp += '<div class="line" style="float: left">'+(vehicleDTO.maintainIntervalsMileage*1<0?'<span style="color: red">超出</span>':'<span style="color: #008000">还有</span>')+Math.abs(vehicleDTO.maintainIntervalsMileage)+'km</div>';
        }

        if(G.isNotEmpty(temp)){
            tr += '<td class="lineConnect">'+temp+'<div style="float: right;width: 20px;margin-left: 5px"><a class="phone J_sendMaintainMsg" style="margin: -2px 4px -4px -20px"></a></div></td>';
        }else{
            tr += '<td class="lineConnect" style="color: #ADADAD"><div class="line" style="float: left;">--</div><div style="float: right;width: 20px;margin-left: 5px"><a class="phone J_sendMaintainMsg" style="margin: -2px 4px -4px -20px"></a></div></td>';
        }
        if(G.isNotEmpty(vehicleDTO.insureTimeStr)){
            tr += '<td><div class="line">'+vehicleDTO.insureTimeStr+'</div></td>';
        }else{
            tr += '<td style="color: #ADADAD"><div class="line">--</div></td>';
        }
        tr+='<td><div class="line">';
      if(APP_BCGOGO.Permission.Version.FourSShopVersion){
        if(G.isEmpty(vehicleDTO.gsmObdImei)&&G.isEmpty(vehicleDTO.gsmObdImeiMoblie)){
          tr+='<a class="blue_color vehicle_install_btn">安装OBD</a><br/>';
        }
      }
      if (vehicleDTO.isObd && APP_BCGOGO.Permission.CustomerManager.VehiclePosition && APP_BCGOGO.Permission.CustomerManager.VehicleDriveLog) {
          tr += '<a class="blue_color" href="vehicleManage.do?method=toVehiclePosition&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">智能定位</a>'
              + '<br/>'
              + '<a class="blue_color" href="vehicleManage.do?method=toVehicleDriveLog&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">行车日志</a>';
          if (APP_BCGOGO.Permission.CustomerManager.CustomerModify || APP_BCGOGO.Permission.CustomerManager.VehicleDetail) {
            tr += '<br/><a class="blue_color" href="vehicleManage.do?method=toVehicleDetail&fromPage=vehicleList&edit=true&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">' + '编辑</a>';
          }

          tr += '</div></td>';
        } else if (vehicleDTO.isObd && APP_BCGOGO.Permission.CustomerManager.VehiclePosition) {
          tr += '<a class="blue_color" href="vehicleManage.do?method=toVehiclePosition&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">智能定位</a>';
          if (APP_BCGOGO.Permission.CustomerManager.CustomerModify || APP_BCGOGO.Permission.CustomerManager.VehicleDetail) {
            tr += '<br/><a class="blue_color" href="vehicleManage.do?method=toVehicleDetail&fromPage=vehicleList&edit=true&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">' + '编辑</a>';
          }
          tr += '</div></td>';
        } else if (vehicleDTO.isObd && APP_BCGOGO.Permission.CustomerManager.VehicleDriveLog) {
          tr += '<a class="blue_color" href="vehicleManage.do?method=toVehicleDriveLog&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">行车日志</a>';
          if (APP_BCGOGO.Permission.CustomerManager.CustomerModify || APP_BCGOGO.Permission.CustomerManager.VehicleDetail) {
            tr += '<br/><a class="blue_color" href="vehicleManage.do?method=toVehicleDetail&fromPage=vehicleList&edit=true&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">' + '编辑</a>';
          }
          tr += '</div></td>';
        } else if (APP_BCGOGO.Permission.CustomerManager.VehicleDetail) {
          tr += '<a class="blue_color" href="vehicleManage.do?method=toVehicleDetail&fromPage=vehicleList&edit=true&customerIdStr=' + vehicleDTO.customerDTO.idStr + '&vehicleIdStr=' + vehicleDTO.idStr + '">' + '编辑</a></div></td>';
        } else if (APP_BCGOGO.Permission.CustomerManager.CustomerModify) {
          tr += '<a class="blue_color" href="unitlink.do?method=customer&vehicleEdit=true&customerId=' + vehicleDTO.customerDTO.idStr + '&vehicleId=' + vehicleDTO.idStr + '">编辑</a></div></td>';
        } else {
          tr += '</div></td>';
        }
        tr += '</tr>';
        $("#vehicleListTable").append($(tr));
        $("#vehicleListTable").append('<tr class="titBottom_Bg J_ItemBody"><td colspan="11"></td></tr>');
        tooltip("vip_"+vehicleDTO.idStr, vehicleDTO.customerDTO);
    });

    $("#exportVehicle").click(function(){
        var $this = $(this);
        var $exportCover = $("#exportExportVehicleCover");
        if($this.attr("lock")){
            return;
        }else{
            try{
                $this.attr("lock",true);
                $exportCover.show();
                $this.hide();
                $(".J_span_sort_vehicleManageList").each(function () {
                    $(this).removeClass("hover");
                    $(this).find(".J_sort_span_image").removeClass("arrowUp").addClass("arrowDown");
                    $(this).attr("currentSortStatus", "Desc");
                    $(this).find(".J_sort_div_info_val").html($(this).attr("ascContact"));
                });
                $(".J_initialCss").placeHolder("clear");

                var paramForm = $("#searchVehicleListForm").serializeArray();
                var param = {};
                $.each(paramForm, function (index, val) {
                    param[val.name] = val.value;
                });

                var isEmpty = true;
                $(".J_clear_input").each(function () {
                    if(G.isNotEmpty($(this).val())){
                        isEmpty = false;
                        return false;
                    }
                });

                if(isEmpty){
                    var $domObject = $("[sortFiled='vehicleLastConsumeTime']");
                    $domObject.addClass("hover");
                    $domObject.find(".J_sort_span_image").removeClass("arrowUp").addClass("arrowDown");
                    $domObject.attr("currentSortStatus", "Desc");
                    $domObject.find(".J_sort_div_info_val").html($domObject.attr("ascContact"));
                    param["sortStatus"] = $domObject.attr("sortFiled") + $domObject.attr("currentSortStatus");
                }

                $(".J_initialCss").placeHolder("reset");
                APP_BCGOGO.Net.asyncPost({
                    url: "export.do?method=exportVehicleList",
                    data: param,
                    dataType: "json",
                    cache: false,
                    success: function (json) {
                        $this.removeAttr("lock");
                        $exportCover.hide();
                        $this.show();
                        if(json && json.exportFileDTOList) {
                            if(json.exportFileDTOList.length > 1) {
                                showDownLoadUI(json);
                            } else {
                                window.open("download.do?method=downloadExportFile&exportFileName=车辆管理导出.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                            }
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("数据异常!");
                    }
                });
            }catch (e){
                $this.removeAttr("lock");
                $exportCover.hide();
                $this.show();
            }
        }
    });
}
function tooltip(id, customer){
    if(G.isNotEmpty($('#'+id))){
        var memberInfo = customer.memberDTO;
        var node = setTooltipHtml(id, customer.idStr);
        setTooltipContent(node,memberInfo);
    }
}

function setTooltipHtml(id , customerId){
    var root = $('#'+id);
    var body = $('<div class="tooltipBody"></div>').append('<a class="icon_close"></a>') .append('<div class="title"><div style="float: left;width: 60px;"><strong>会员信息</strong></div><div style="float: right;width: 72px;"><a href="javascript:void(0)">查看更多资料</a></div></div>').append('<div class="prompt-left"></div>').append('<div class="prompt-right"></div>')
    var node = $('<div class="tooltip" style="margin:0px 0px 0px -12px;display: none;"></div>').append('<div class="tooltipTop"></div>').append(body).append('<div class="tooltipBottom"></div>')
    root.append(node).bind('mouseover',function(e){
        node.show();
    }).bind('mouseout',function(){
            node.hide();
        });
    $('.icon_close',node).click(function(){
        node.hide();
    });
    $('a', node).last().click(function(){
        window.open('unitlink.do?method=customer&customerId=' + customerId);
    });
    return node;
}

function setTooltipContent(node,memberInfo){
    var leftList = [{name:'卡号：',val:'memberNo'}, {name:'卡类型：',val:'type'}, {name:'入会日期：',val:'joinDateStr'}, {name:'过期日期：',val:'serviceDeadLineStr'}, {name:'会员储值：',val:'balanceStr'}];
    var left = $('.prompt-left',node);
    var right = $('.prompt-right',node);
    $.each(leftList,function(i,n){
        var val = memberInfo[n.val];
        if(val && val.length>11){
            val = '<span title="' + val + '">' + val.substr(0,8) + '...</span>';
        } else {
            val = G.normalize(val);
        }
        left.append('<div class="clear"><div class="left">' + n.name + '</div><div class="tipRight">' + val + '</div></div>');
    });
    var memberServices = memberInfo.memberServiceDTOs;
    if (memberServices) {
        right.append('<div>服务项目(共' + memberServices.length + '项)</div>');
        $.each(memberServices, function (i, memberService) {
            var name = memberService.serviceName && memberService.serviceName.length > 11 ? '<span title="' + memberService.serviceName + '">' + memberService.serviceName.substr(0, 8) + '...</span>' : memberService.serviceName;
            right.append('<div style="overflow: hidden;"><div class="div left">'+ name +'</div><div class="div tipRight">' + memberService.timesStr + '</div></div>');
        });
    }else{
        right.append('<div class="gray_color">暂无服务项目）</div>');
    }
}
