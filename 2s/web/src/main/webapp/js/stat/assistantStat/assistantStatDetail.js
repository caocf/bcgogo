var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


$(document).ready(function () {

  $(".detailTitle").live("click", function () {
    $("#assistantStatDetailDiv").find("a").removeClass("hover_title");
    $(this).addClass("hover_title");
    var id = $(this).attr("id");

    $("#category").text($(this).text());
    $("#assistantServiceRecordDiv,#assistantWashBeautyRecordDiv,#assistantMemberRecordDiv,#assistantProductRecordDiv,#assistantBusinessAccountRecordDiv").css("display","none");

    var size = 0;

    if (id == "repair") {
      $("#recordType").val("assistantServiceRecord");
      $("#assistantServiceRecordDiv").css("display", "block");
      size = $("#assistantServiceRecord").find("tr").size();

    } else if (id == "washBeauty") {
      $("#recordType").val("assistantWashRecord");
      $("#assistantWashBeautyRecordDiv").css("display", "block");
      size = $("#assistantWashBeautyRecord").find("tr").size();


    } else if (id == "sales") {
      $("#recordType").val("assistantProductRecord");
      $("#assistantProductRecordDiv").css("display", "block");
      size = $("#assistantProductRecord").find("tr").size();
    } else if (id == "member") {
      $("#recordType").val("assistantMemberRecord");
      $("#assistantMemberRecordDiv").css("display", "block");
      size = $("#assistantMemberRecord").find("tr").size();

    } else if (id == "businessAccount") {
      $("#recordType").val("assistantBusinessAccountRecord");
      $("#assistantBusinessAccountRecordDiv").css("display", "block");

      size = $("#assistantBusinessAccountRecord").find("tr").size();
    }
//    initData();

//    if(size <= 2){
//      $("#printButton,#exportButton").css("display","none");
//    }else{
//      $("#printButton,#exportButton").css("display","block");
//    }

  });

  if ($("#assistantProductRecordDiv").length > 0 && $("#sales").length > 0) {

    initData("assistantProductRecord");
  }

  if ($("#assistantServiceRecordDiv").length > 0 && $("#repair").length > 0) {
    initData("assistantServiceRecord");
  }

  if ($("#assistantWashBeautyRecordDiv").length > 0 && $("#washBeauty").length > 0) {
    initData("assistantWashRecord");
  }

  if ($("#assistantMemberRecordDiv").length > 0 && $("#member").length > 0) {
    initData("assistantMemberRecord");
  }

  if ($("#assistantBusinessAccountRecord").length > 0 && $("#businessAccount").length > 0) {
    initData("assistantBusinessAccountRecord");
  }

  if ($("#achievementOrderTypeStrHidden").val() == "SALES") {
    $("#sales").click();
  } else if ($("#achievementOrderTypeStrHidden").val() == "REPAIR_SERVICE") {
    $("#repair").click();
  } else if ($("#achievementOrderTypeStrHidden").val() == "WASH_BEAUTY") {
    $("#washBeauty").click();
  } else if ($("#achievementOrderTypeStrHidden").val() == "BUSINESS_ACCOUNT") {
    $("#businessAccount").click();
  } else if ($("#achievementOrderTypeStrHidden").val() == "MEMBER") {
    $("#member").click();
  }

  if ($("#repair").length > 0) {
    $("#repair").click();
  } else if ($("#sales").length > 0) {
    $("#sales").click();
  } else if ($("#businessAccount").length > 0) {
    $("#businessAccount").click();
  } else if ($("#washBeauty").length > 0) {
    $("#washBeauty").click();
  } else if ($("#member").length > 0) {
    $("#member").click();
  }



  $("#printButton").live("click", function () {

    var orderType = "";
    var url ="";
    var size = 0;
    if ($("#recordType").val() == "assistantServiceRecord") {
      orderType = "ASSISTENT_SERVICE_STAT";
      url ="assistantStat.do?method=printAssistantService";
      size = $("#assistantServiceRecord").find("tr").size();
    } else if ($("#recordType").val() == "assistantWashRecord") {
      orderType = "ASSISTENT_WASH_STAT";
      url ="assistantStat.do?method=printAssistantService";
      size = $("#assistantWashBeautyRecord").find("tr").size();
    } else if ($("#recordType").val() == "assistantProductRecord") {
      orderType = "ASSISTENT_PRODUCT_STAT";
      url ="assistantStat.do?method=printAssistantProduct";
      size = $("#assistantProductRecord").find("tr").size();
    } else if ($("#recordType").val() == "assistantMemberRecord") {
      orderType = "ASSISTENT_MEMBER_CARD_STAT";
      url ="assistantStat.do?method=printAssistantMember";
      size = $("#assistantMemberRecord").find("tr").size();
    } else if ($("#recordType").val() == "assistantBusinessAccountRecord") {
      orderType = "ASSISTANT_BUSINESS_ACCOUNT_STAT";
      url ="assistantStat.do?method=printAssistantBusinessAccount";
      size = $("#assistantBusinessAccountRecord").find("tr").size();
    }

    if (size <= 2) {
      alert("没有记录,不能打印");
      return;
    }

    var url;
    APP_BCGOGO.Net.syncGet({
      url: "print.do?method=getTemplates",
      data: {
        "orderType": orderType,
        "now": new Date()
      },
      dataType: "json",
      success: function (result) {
        if (result && result.length > 1) {
          var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
              "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
          for (var i = 0; i < result.length; i++) {
            var radioId = "selectTemplate" + i;
            selects += "<input type='radio' id='" + radioId + "' name='selectTemplate' value='" + result[i].idStr + "'";
            if (i == 0) {
              selects += " checked='checked'";
            }
            selects += " />" + "<label for='" + radioId + "'>" + result[i].displayName + "</label><br/>";
          }
          selects += "</div>";
          nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
            if (returnVal) {
              printPageContent($("input:radio[name='selectTemplate']:checked").val(),url);
            }
          });
        }else{
          printPageContent("",url);
        }
      }
    });
  });

    $("#exportButton").click(function(){
        $(this).attr("disabled",true);
        $("#exporting").css("display","");
        var $totalNum = $("#" + $("#recordType").val() + 'Num');

        if($totalNum.val() * 1 == 0) {
            nsDialog.jAlert("对不起，暂无数据，无法导出！");
            $(this).removeAttr("disabled");
            $("#exporting").css("display","none");
            return;
        }
        var orderType = "";
        var pageType = $("#recordType").val();
        if (pageType == "assistantServiceRecord") {
            orderType = "repair";
        } else if (pageType == "assistantWashRecord") {
            orderType = "washBeauty"
        } else if (pageType == "assistantBusinessAccountRecord") {
            orderType = "businessAccount"
        }
        var data = {
            assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),
            achievementStatTypeStr:$('#achievementStatTypeStr').val(),
            startTimeStr:$('#startTime').val(),
            endTimeStr:$('#endTime').val(),
            orderType:orderType,
            achievementOrderTypeStr:$("#achievementOrderTypeStrHidden").val(),
            achievementCalculateWayStr:$("#achievementCalculateWayHidden").val(),
            serviceIdStr:$("#serviceIdStrHidden").val(),
            pageType:$("#recordType").val(),
            totalNum:$totalNum.val()
        }
        var url = "export.do?method=exportAssistantStatDetail";
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#export").removeAttr("disabled");
            $("#exporting").css("display","none");
            if(json && json.exportFileDTOList) {
                if(json.exportFileDTOList.length > 1) {
                    showDownLoadUI(json);
                } else {
                    if("REPAIR_ASSISTANT_STAT" == json.exportScene) {
                        window.open("download.do?method=downloadExportFile&exportFileName=员工业绩统计-车辆施工.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                    } else if("WASH_ASSISTANT_STAT" == json.exportScene) {
                        window.open("download.do?method=downloadExportFile&exportFileName=员工业绩统计-洗车美容.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                    } else if("SALES_ASSISTANT_STAT" == json.exportScene) {
                        window.open("download.do?method=downloadExportFile&exportFileName=员工业绩统计-商品销售.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                    } else if("MEMBER_ASSISTANT_STAT" == json.exportScene) {
                        window.open("download.do?method=downloadExportFile&exportFileName=员工业绩统计-会员卡销售.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                    } else if("BUSINESS_ACCOUNT_ASSISTANT_STAT" == json.exportScene) {
                        window.open("download.do?method=downloadExportFile&exportFileName=员工业绩统计-营业外统计.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                    }
                }
            }

        });
    });
});


function printPageContent(templateId,url) {
  var data = "";
  data += "&startTimeStr=" + $("#startTime").val();
  data += "&endTimeStr=" + $("#endTime").val();
  data += "&achievementStatTypeStr=" + $("#achievementStatTypeStr").val();
  data += "&assistantOrDepartmentIdStr=" + $("#assistantOrDepartmentId").val();
  data += "&startPageNo=" + $("#currentPage" + "dynamical" + $("#recordType").val()).val();
  data += "&maxRows=15";
  data += "&achievementOrderTypeStr=" + $("#achievementOrderTypeStrHidden").val();
  data += "&achievementCalculateWayStr=" + $("#achievementCalculateWayHidden").val();
  data += "&serviceIdStr=" + $("#serviceIdStrHidden").val();

  if ($("#recordType").val() == "assistantWashRecord") {
    data += "&orderTypeStr=washBeauty";
  } else if ($("#recordType").val() == "assistantServiceRecord") {
    data += "&orderTypeStr=repair";
  }

  window.showModalDialog(url+"&" + data + "&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
  return;
}







