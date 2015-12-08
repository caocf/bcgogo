/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 14-2-7
 * Time: 下午12:05
 * To change this template use File | Settings | File Templates.
 */



function map() {
  var struct = function (key, value) {
    this.key = key;
    this.value = value;
  }

  var put = function (key, value) {
    for (var i = 0; i < this.arr.length; i++) {
      if (this.arr[i].key === key) {
        this.arr[i].value = value;
        return;
      }
    }
    this.arr[this.arr.length] = new struct(key, value);
  }

  var get = function (key) {
    for (var i = 0; i < this.arr.length; i++) {
      if (this.arr[i].key === key) {
        return this.arr[i].value;
      }
    }
    return null;
  }

  var remove = function (key) {
    var v;
    for (var i = 0; i < this.arr.length; i++) {
      v = this.arr.pop();
      if (v.key === key) {
        break;
      }
      this.arr.unshift(v);
    }
  }

  var size = function () {
    return this.arr.length;
  }

  var isEmpty = function () {
    return this.arr.length <= 0;
  }

  var clearMap = function () {
    this.arr = [];
  }
  this.arr = new Array();
  this.get = get;
  this.put = put;
  this.remove = remove;
  this.size = size;
  this.isEmpty = isEmpty;
  this.clearMap = clearMap;
}
var jsonStrMap = new map();

$(document).ready(function () {



  $("#lastDriveTimeStartStr,#lastDriveTimeEndStr")
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
        var vehicleLastConsumeTimeStartStr = $("#lastDriveTimeStartStr").val();
        var vehicleLastConsumeTimeEndStr = $("#lastDriveTimeEndStr").val();
        if (G.isEmpty(vehicleLastConsumeTimeStartStr)|| G.isEmpty(vehicleLastConsumeTimeEndStr)) return;
        if (APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeStartStr) && APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeEndStr)) {
          return;
        } else {
          if (vehicleLastConsumeTimeStartStr > vehicleLastConsumeTimeEndStr) {
            $("#lastDriveTimeEndStr").val(vehicleLastConsumeTimeStartStr);
            $("#lastDriveTimeStartStr").val(vehicleLastConsumeTimeEndStr);
          }
        }
      })
      .bind("click", function() {
        $(this).blur();
      })
      .change(function() {
        var vehicleLastConsumeTimeStartStr = $("#lastDriveTimeStartStr").val();
        var vehicleLastConsumeTimeEndStr = $("#lastDriveTimeEndStr").val();
        $("a[name='my_date_select']").removeClass("clicked");
        if (G.isEmpty(vehicleLastConsumeTimeStartStr)|| G.isEmpty(vehicleLastConsumeTimeEndStr)) return;
        if (APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeStartStr) && APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeEndStr)) {
          return;
        } else {
          if (vehicleLastConsumeTimeStartStr > vehicleLastConsumeTimeEndStr) {
            $("#lastDriveTimeEndStr").val(vehicleLastConsumeTimeStartStr);
            $("#lastDriveTimeStartStr").val(vehicleLastConsumeTimeEndStr);
          }
        }
      });

  $("#gsmObdImei,#gsmObdImeiMoblie")
      .bind('click', function () {
        getVehicleGsmOBdSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleGsmOBdSuggestion($(this));
        }
      });

  $("#engineNo,#chassisNumber")
      .bind('click', function () {
        getVehicleGsmOBdSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleGsmOBdSuggestion($(this));
        }
      });


  $("#licenceNo")
      .bind('click', function () {
        getVehicleGsmOBdSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleGsmOBdSuggestion($(this));
        }
      });


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
      searchFieldStrategies: "searchIncludeMemberNo",
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
          setStartPageNo();
          searchVehicleList();
        }
      });
    });

  }

  $("#vehicleLastConsumeTimeStartStr,#vehicleLastConsumeTimeEndStr")
      .datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": false,
        "changeYear": true,
        "showHour": false,
        "showMinute": false,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": ""
      })
      .blur(function () {
        var vehicleLastConsumeTimeStartStr = $("#vehicleLastConsumeTimeStartStr").val();
        var vehicleLastConsumeTimeEndStr = $("#vehicleLastConsumeTimeEndStr").val();
        if (G.isEmpty(vehicleLastConsumeTimeStartStr) || G.isEmpty(vehicleLastConsumeTimeEndStr)) return;
        if (APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeStartStr) && APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeEndStr)) {
          return;
        } else {
          if (vehicleLastConsumeTimeStartStr > vehicleLastConsumeTimeEndStr) {
            $("#vehicleLastConsumeTimeEndStr").val(vehicleLastConsumeTimeStartStr);
            $("#vehicleLastConsumeTimeStartStr").val(vehicleLastConsumeTimeEndStr);
          }
        }
      })
      .bind("click", function () {
        $(this).blur();
      })
      .change(function () {
        var vehicleLastConsumeTimeStartStr = $("#vehicleLastConsumeTimeStartStr").val();
        var vehicleLastConsumeTimeEndStr = $("#vehicleLastConsumeTimeEndStr").val();
        $("a[name='my_date_select']").removeClass("clicked");
        if (G.isEmpty(vehicleLastConsumeTimeStartStr) || G.isEmpty(vehicleLastConsumeTimeEndStr)) return;
        if (APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeStartStr) && APP_BCGOGO.Validator.stringIsZhCn(vehicleLastConsumeTimeEndStr)) {
          return;
        } else {
          if (vehicleLastConsumeTimeStartStr > vehicleLastConsumeTimeEndStr) {
            $("#vehicleLastConsumeTimeEndStr").val(vehicleLastConsumeTimeStartStr);
            $("#vehicleLastConsumeTimeStartStr").val(vehicleLastConsumeTimeEndStr);
          }
        }
      });


  $("#clearConditionBtn").bind('click', function () {
    $(".J_clear_input").val("");
    $("#startPageNo").val(1);

    $("a[name='my_date_select']").removeClass("clicked");
    $(".J_initialCss").placeHolder("reset");
  });



  $("#searchVehicleBtn").bind("click", function () {
    setStartPageNo();
    searchVehicleList();
  });


  //初始化
  $(".J_initialCss").placeHolder();
//  searchVehicleList();


  $(".J_driveLog").live("click", function () {
    $(".J_driveDetailInfo").hide();
    $(this).parent().find(".J_driveDetailInfo").eq(0).show();
    var driveLogDTO = jsonStrMap.get($(this).attr("data-id"));
    if (driveLogDTO != null) {
      showDriveLog(driveLogDTO);
    }
  });

  $("a[name='my_date_select']").not("#my_date_oneMonthBefore").removeClass("clicked");
  $("#my_date_oneMonthBefore").addClass("clicked");
  $("#lastDriveTimeStartStr").val(dateUtil.getOneMonthBefore(false,"start"));
  $("#lastDriveTimeEndStr").val(dateUtil.getToday(false, "end"));
  if (G.isNotEmpty($("#vehicleIdStr").val())) {
    setStartPageNo();
    searchVehicleList();
  } else {
    noDate();
  }


});


function noDate(){
  var $iframe = $("#map_container_iframe");
  var str = "&city=" + $("#city").val() + "&coordinate=" +$("#coordinateLon").val() + "_" + $("#coordinateLat").val();
  $iframe[0].src = "api/proxy/baidu/map/vehicleDriveLog?data=" + str;
}


function searchVehicleList() {

  $(".J_initialCss").placeHolder("clear");

  if (G.isNotEmpty($('#licenceNo').val()) && !App.Validator.stringIsLicensePlateNumber($('#licenceNo').val().replace(/\s|\-/g, ""))) {
    nsDialog.jAlert("输入的车牌号码不符合规范，请检查！");
    return false;
  }

//  $("#drive_record").html("").css("display", "none");
//  $("#map_container_iframe").hide();


  var paramForm = $("#searchVehicleListForm").serializeArray();
  var param = {};
  $.each(paramForm, function (index, val) {
    param[val.name] = val.value;
  });

  $(".J_initialCss").placeHolder("reset");
  APP_BCGOGO.Net.syncPost({
    url: "vehicleManage.do?method=queryVehicleDriveLog",
    dataType: "json",
    data: param,
    success: function (result) {
      if (result.success) {
        drawVehicleListTable(result.data);
      }else{
        nsDialog.jAlert(result.msg);

        $("#drive_record").hide();
        $("#noDateDiv").show();
        $(".addrPage").html('<img src="images/ku.png" /> <span>暂无车辆行车日志</span>');
        noDate();
      }
    },
    error: function () {
      nsDialog.jAlert("数据异常，请刷新页面！");
    }
  });
}


function drawVehicleListTable(json) {

  if (json == null || json[0].length == 0) {
    $("#drive_record").hide();
    $("#noDateDiv").show();
    $(".addrPage").html('<img src="images/ku.png" /> <span>暂无车辆行车日志</span>');
    noDate();
    return;
  }
//  $("#map_container_iframe").show();


  var driveLogHtml = "";
  $.each(json[0], function (index, driveLogDTO) {
    var startPlace = driveLogDTO.startPlace;
    var endPlace = driveLogDTO.endPlace;
    var startTimeStr = driveLogDTO.startTimeStr;
    var endTimeStr = driveLogDTO.endTimeStr;
    var distance = driveLogDTO.distance;
    var travelTimeStr = driveLogDTO.travelTimeStr;
    var oilCost = driveLogDTO.oilCost;
    var oilWear = driveLogDTO.oilWear;
    var detailStartPlace = driveLogDTO.detailStartPlace;
    var detailEndPlace = driveLogDTO.detailEndPlace;
    var idStr = driveLogDTO.idStr;


    jsonStrMap.put(idStr, driveLogDTO);

    driveLogHtml += '<div class="driving_route">' +
        '<div data-id="' + idStr + '" class="J_driveLog way">' +
        '<div class="div_01">' +
        '<strong>' + startPlace + '</strong>' + startTimeStr +
        '</div>' +
        '<div class="div_02"><strong>→</strong></div>' +
        '<div class="div_01">' +
        '<strong>' + endPlace + '</strong>' + endTimeStr +
        '</div>' +
        '<div class="clear"></div>' +
        '</div>';


    if (index == 0) {
      showDriveLog(driveLogDTO);
      driveLogHtml += '<div class="route_0 J_driveDetailInfo">';
    } else {
      driveLogHtml += '<div class="route_0 J_driveDetailInfo" style="display: none;">';
    }
    driveLogHtml += '<h3 class="no1">' + detailStartPlace + '</h3>  ' +
        '<ul>      ' +
        '       <li>里程' + distance + 'km</li> ' +
        '      <li>全程' + travelTimeStr + '</li>  ' +
        '      <li>油耗' + oilCost + 'L</li>        ' +
        '      <li>均耗' + oilWear + 'L/100Km</li>      ' +
        '  </ul>                  ' +
        '  <div class="clear"></div>    ' +
        '<h3 class="no2">' + detailEndPlace + '</h3>   ' +
        ' <div class="clear"></div> ' +
        '  </div>   ' +

        '</div>';
  });

  var pager = json[1];

  driveLogHtml += '<div class="i_pageBtn"> <a'+ (pager.isFirstPage ? "" : " onclick='goToPrevPage(this)'") +' class="lastPage ' + (pager.isFirstPage ? "" : " wordButton ") + '">上一页</a>' +
      ' <a  '+ (pager.isLastPage ? "" : " onclick='goToNextPage(this)'") +'class="nextPage '+ (pager.isLastPage ? "" : " wordButton ") +'" max-pageNo ="' + pager.totalPage + '">下一页</a></div>';

  $("#drive_record").html(driveLogHtml);
  $("#drive_record").show();
  $("#noDateDiv").hide();

}

function showDriveLog(driveLogDTO) {
  var $iframe = $("#map_container_iframe");
  var str = "&city=" + $("#city").val() + "&coordinate=" +$("#coordinateLon").val() + "_" + $("#coordinateLat").val();
  $iframe[0].src = "api/proxy/baidu/map/vehicleDriveLog?data="+driveLogDTO.idStr + str;
      //+ JSON.stringify(driveLogDTO);
//  $iframe[0].style.display = "block";
}

function goToNextPage(object) {

  if ( parseInt($(object).attr("max-pageNo")) <= parseInt($("#startPageNo").val())) {
    nsDialog.jAlert("已经是最后一页了");
    return;
  }

  $("#startPageNo").val(parseInt($("#startPageNo").val())+ 1);
  searchVehicleList();

}

function goToPrevPage(object) {

  if ($("#startPageNo").val() <= 1) {
    $("#startPageNo").val(1);
    nsDialog.jAlert("已经是第一页了");
    return;
  }
  $("#startPageNo").val(parseInt($("#startPageNo").val()) - 1);
  searchVehicleList();
}

function getVehicleGsmOBdSuggestion($domObject) {
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
        setStartPageNo();
        searchVehicleList();
      }
    });
  });
}

function setStartPageNo(){
  $("#startPageNo").val(1);
}



