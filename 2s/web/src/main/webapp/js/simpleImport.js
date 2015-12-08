$(function(){
    $(".tabSlip tr").not(".titleBg").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tabSlip tr:nth-child(odd)").not(".titleBg").css("background","#eaeaea");

    $(".tabSlip tr").not(".titleBg").hover(
        function () {
            $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px"});

            $(this).css("cursor","pointer");
        },
        function () {
            $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px"});
            $(".tabSlip tr:nth-child(odd)").not(".titleBg" ).find("td").css("background","#eaeaea");
        }
    );

    $("#tab_slip tr:gt(1)").css("display","none");
    $("#tab_Remark tr:gt(1)").css("display","none");

    $(".up").toggle(
        function(){
            $(this).parent().parent().parent().find("tr:gt(1)").css("display","");
            $(this).html("收拢");
            $(this).removeClass().addClass("down");
        },
        function(){
            $(this).parent().parent().parent().find("tr:gt(1)").css("display","none");
            $(this).html("更多");
            $(this).removeClass().addClass("up");
        }
    );

    $(".tempBody").hide();
    $(".tempTop").toggle(
        function(){
            $(".tempBody").show();
            $(".info").html("收拢");
            $(".info").css({"background-image":"url(images/rightTop.png)"});
        },
        function(){
            $(".tempBody").hide();
            $(".info").html("详细");
            $(".info").css({"background-image":"url(images/rightArrow.png)"});
        }
    );

    $("input[type='radio']").bind("click",function(){
        initRadioCss();
    });

    initRadioCss();

    var ieNoticeProxy = function() {
        if($.browser.msie) {
            var noticeInfo = "" +
                "<div style='line-height:26px;'>" +
                "    请使用 火狐浏览器 进行文件上传。 <br>如果您电脑未安装火狐浏览器，请联系客服。<br>" +
                "    客服电话：" +
                "    <p style='padding-left:30px;'>0512-66733331</p>" +
                "    客服QQ：" +
                "    <p style='padding-left:30px;'>1754061146 1362756627</p>" +
                "    <p style='padding-left:30px;'>2390356460</p>" +
                "</div>";

            nsDialog.jAlert(noticeInfo);
            return true;
        }
        return false;
    };

    $("#selUp").bind("click",function(){
        if(ieNoticeProxy()) {
            return false;
        }

        var fileName = $("#selectfile").val();
        if(fileName == undefined || fileName == "" || (fileName.search(/(.*xls$)|(.*xlsx$)/g) === -1)){
            nsDialog.jAlert("请选择EXCEL文件！");
            return ;
        }
        ajaxFileUpload();
    });

    $("#input1,#selectfileBtn").bind("click",function(){
        if(ieNoticeProxy()) {
            return false;
        }
        $("#selectfile").click();
    });
});

/**
 * 使用ajax上传文件
 */
function ajaxFileUpload()
{
    var importType = $("input:radio:checked").val();
    var uploadUrl = "import.do?method=simpleImportExcelData";

    if(!importType)
    {
        nsDialog.jAlert("请选择导入类型！");
        return ;
    }
    $("#importing").show();
    $("#selUp").hide();

    $("#ajaxForm").ajaxSubmit({
            dataType: "json",
            type: "POST",
            success: function (data) {
                if(data && data!= undefined && data.result == "error")
                {
                    nsDialog.jAlert(data.errorMsg);
                }
                else if(data && data!= undefined)
                {
                    var alertMessage = "";
                    var resultJson = data;
                    if(resultJson.success == true){
                        alertMessage += "导入成功！";
                        alertMessage += " 总数：" + resultJson.totalCount;
                        alertMessage += " 成功：" + resultJson.successCount;
                        alertMessage += " 失败：" + resultJson.failCount;
                        $("#importToDefault").val('');
                        nsDialog.jAlert(alertMessage);
                    }else if("storeHouseEmpty" == resultJson.message) {
                        nsDialog.jConfirm("您有商品未填写仓库，若未填写则该商品将导入到默认仓库！是否继续上传？",null,function(resultValue){
                             if(resultValue) {
                                 $("#importToDefault").val('true');
                                 $("#selUp").click();
                             }
                        });
                    } else {
                        alertMessage += "导入失败！<br>";
                        alertMessage +=  resultJson.message;
                        alertMessage += "<br>请修改导入文件重新导入";
                        $("#importToDefault").val('');
                        nsDialog.jAlert(alertMessage);
                    }

                }
                $("#importing").hide();
                $("#selUp").show();
            },
            error: function (data) {
                var alertMessage = "后台解析文件出现异常：" + data;
                nsDialog.jAlert(alertMessage);
                $("#importToDefault").val('');
                $("#importing").hide();
                $("#selUp").show();
            }
        }
    );
}

function initRadioCss(){
    $("input[type='radio']").each(function(i){
        if($(this)[0].checked)
        {
            $(this).next("label").attr("class","selected");
        }
        else
        {
            $(this).next("label").removeAttr("class");
        }
    });
}