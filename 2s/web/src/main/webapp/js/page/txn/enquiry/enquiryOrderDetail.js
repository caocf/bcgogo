;
$(function () {
    $(".price-img img").imageChange({});
    $("#quote").bind("click", function () {
        var $this = $(this);
        var $responseMsg = $("#responseMsg");
        var responseMsg = $responseMsg.val();
        responseMsg = $.trim(responseMsg);
        if (G.Lang.isEmpty(responseMsg)) {
            nsDialog.jAlert("请填写报价信息！", null, function () {
                $responseMsg.focus();
            });
            return;
        }
        var enquiryId = $("#enquiryId").val();
        if (G.Lang.isEmpty(enquiryId)) {
            nsDialog.jAlert("报价异常，当前询价单不存在，请重新选择询价单进行报价！", null, function () {
                window.location.href = "enquiry.do?method=showEnquiryOrderList";
            });
            return;
        }
        if ($this.attr("lock")) {
            return;
        }
        $this.attr("lock", true);
        App.Net.syncAjax({
            dataType: "json",
            url: "enquiry.do?method=shopEnquiryResponse",
            data: {enquiryId: enquiryId, responseMsg: responseMsg},
            success: function (result) {
                if (result && result.success) {
                    var $enquiryResponseInfo = $("#enquiryResponseInfo");
                    $enquiryResponseInfo.find(".J_NO_Quotation").remove();
                    var currentIndex = 0;
                    $enquiryResponseInfo.find(".J_response_content").each(function () {
                        var thisIdIndex = $(this).attr("id").substring("enquiryShopResponses".length, "enquiryShopResponses".length + 1);
                        thisIdIndex = parseInt(thisIdIndex);
                        if (thisIdIndex >= currentIndex) {
                            currentIndex = thisIdIndex + 1;
                        }
                    });
                    var enquiryResponseDTO = result["data"];
                    var enquiryResponseId = enquiryResponseDTO["idStr"];
                    var responseTimeStr = enquiryResponseDTO["responseTimeStr"];
                    var msg = enquiryResponseDTO["responseMsg"];
                    var html = '';
                    html += '<div id="enquiryShopResponses' + currentIndex + '" class="J_response_content">';
                    html += '<input type="hidden" id="enquiryShopResponses' + currentIndex + '.id" value="' + enquiryResponseId + '">';
                    html += '<div class="gray-line">' + responseTimeStr + '</div>';
                    html += '<div class="white-line">' + msg + '</div>';
                    html += '</div>';
                    $(html).appendTo($enquiryResponseInfo);

                    $("#responseStatusStr").html("已报价");
                    $responseMsg.val("");
                }
                $this.removeAttr("lock");
            },
            error: function () {
                nsDialog.jAlert("网络异常，请联系管理员!");
                $this.removeAttr("lock");
            }
        });
    });

    $("#returnBtn").bind("click",function(){
        window.location.href = "enquiry.do?method=showEnquiryOrderList";
    });

    $("#printBtn").bind("click",function(){
        var enquiryId = $("#enquiryId").val();
        window.showModalDialog("enquiry.do?method=printEnquiryOrderDetail&enquiryId=" + enquiryId  +
            "&now=" + new Date(), '询价单', "dialogWidth=1024px;dialogHeight=768px,status=no;help=no");
    });


});