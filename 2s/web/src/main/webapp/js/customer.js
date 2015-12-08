var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(document).ready(
    function () {
        var content = $("#smsContent").val();
        $("#contentLength").text(G.isEmpty(content)? 0: content.length);

        $('#a_name2').click(
            function () {
                $('#searchType').val('name');
                $('#a_name2').addClass('hover');
                $('#a_name3').removeClass('hover');
                $('#a_name4').removeClass('hover');
            }
        );
        $('#a_name3').click(
            function () {
                $('#searchType').val('licenecNo');
                $('#a_name3').addClass('hover');
                $('#a_name2').removeClass('hover');
                $('#a_name4').removeClass('hover');
            }
        );
        $('#a_name4').click(
            function () {
                $('#searchType').val('mobile');
                $('#a_name4').addClass('hover');
                $('#a_name2').removeClass('hover');
                $('#a_name3').removeClass('hover');
            }
        );

        $('#searchBtn').click(
            function () {
                //alert($('#searchType').val());
                var searchType = $('#searchType').val();
                var searchContent = $('#searchContent').val();
                window.location = "customer.do?method=searchcustomer&searchType=" + searchType + "&searchContent=" + encodeURI(encodeURI(searchContent));
            }
        );

        $('#searchArrearsBtn').click(
            function () {
                //alert($('#searchType').val());
                var searchType = $('#searchType').val();
                var searchContent = $('#searchContent').val();
                window.location = "customer.do?method=searcharrears&searchType=" + searchType + "&searchContent=" + encodeURI(encodeURI(searchContent));
            }
        );


//          $('#addClientBtn').click(
//              function (){   //alert('~~~');
//                  Mask.Login();
//                 $('#iframe_PopupBox').src = "customer.do?method=addClient";
//                 $('#iframe_PopupBox').style.display = "block";
//              }
//          );

        function verifyingMobiles() {
            var phoneNumbers = $('#phoneNumbers').val();
            var smsContent = $('#smsContent').val();
            if (!phoneNumbers) {
                alert("请输入接收手机！");
                return;
            }
            if (!smsContent) {
                alert("请输入内容！");
                return;
            }
            var content = $("#smsContent").val();
            if (content.length > 400) {
                alert("短信内容最多为400个汉字，请修改后发送!");
                return false;
            }
            phoneNumbers = phoneNumbers.replace(/，/g, ",").replace(/；/g, ",").replace(/;/g, ",").replace(/ /g, "");
            //去掉 最后 ","
            if (phoneNumbers.charAt(phoneNumbers.length - 1) == ",") {
                phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length - 1);
            }
            //手机号码 验证 author:zhangjuntao
            var phoneNumber = phoneNumbers.split(',');
            //如果手机号码 小于20个，不正确的号码给予提示
            // 大于20个自动剔除不正确的号码

            for (var i = 0,max = phoneNumber.length; i < max; i++) {
                var mobile = phoneNumber[i];
                phoneNumber[i] = phoneNumber[i].split("(")[0];  //处理13xxx(xx)的情况
                if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(phoneNumber[i])) {
                    if (max <= 20) {
                        alert("手机号码：“" + mobile + "”输入不正确");
                        return false;
                    } else {
                        phoneNumber.splice(i, 1);
                        max--;
                        i--;
                    }
                } else {
                    //重复剔除
                    for (var j = i + 1; j < phoneNumber.length; j++) {
                        if (phoneNumber[i] == phoneNumber[j].split("(")[0]) {
                            phoneNumber.splice(i, 1);
                            i--;
                            max--;
                            break;
                        }
                    }
                }
            }
            phoneNumbers = phoneNumber.join(",");
            $('#phoneNumbers').val(phoneNumbers);
            if (!phoneNumbers) {
                return false;
            }
            var smsType = $('#smsType').val();    //短信类型
            //余额判断
            var shopMoney = $("#smsBalance").html();
            var phoneSize = phoneNumber.length;
            var length = $("#smsContent").val().length;
            if (!shopMoney && Number(shopMoney) - Math.ceil(length / 67) * phoneSize * 0.1 < 1) {
                alert("您的短信余额不足");
                return false;
            }
            var validateSuccess = true;
            //validate Sensitive Word
            bcgogoAjaxQuery.setUrlData("sensitiveWord.do?method=validateSensitiveWord", {content: content});
            bcgogoAjaxQuery.setAsyncAndCache(false, false);
            bcgogoAjaxQuery.ajaxQuery(function (result) {
                if (!result.success) {
                    alert('存在敏感词：【' + result.msg + '】请修改后重新发送');
                    validateSuccess = false;
                }
            }, function (result) {
                console.log(result);
                validateSuccess = false;
            });
            return validateSuccess;
        }

        $('#nowSendBtn').click(function() {
            if (!verifyingMobiles()) {
                return;
            }
            $('#frm').submit();
        });
        $("#delaySendBtn").click(function() {
            if ($("#sendTime").val() == "") {
                alert("请选择延时发送时间");
                return;
            }
            if (!verifyingMobiles()) {
                return;
            }
            $('#frm').submit();
        });

        $('#input_addUser').click(function() {
//            bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':'customer.do?method=addClient&fromPage=data'});
          window.location.href = "customer.do?method=addClient"
        });

        $('#input_addUser_guid').click(
            function() {
                bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':'customer.do?method=addClient&fromPage=guid'});
            }
        );

        $("#addUserBtn").click(
            function () {
                bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':'customer.do?method=addsmsclient'});
            }
        );

        $('img.noclass').each(//只在第一页注册
            function(index) {
                $(this).bind("click", function() {
                    if ($(this).attr("name") == "off") {
                        $(this).attr("src", "/web/images/check_on.jpg");
                        $(this).attr("name", "on");
                    } else {
                        $(this).attr("src", "/web/images/check_off.jpg");
                        $(this).attr("name", "off");
                    }
                });
            }
        );

        $("#check_all").click(
            function() {
                if ($("#check_all").attr("name") == "off") {
                    $("#check_all").attr("src", "/web/images/check_on.jpg");
                    $("#check_all").attr("name", "on");
                    $('img.noclass').each(
                        function(index) {
                            $(this).attr("src", "/web/images/check_on.jpg");
                            $(this).attr("name", "on");
                        }
                    );
                } else {
                    $("#check_all").attr("src", "/web/images/check_off.jpg");
                    $("#check_all").attr("name", "off");
                    $('img.noclass').each(
                        function(index) {
                            $(this).attr("src", "/web/images/check_off.jpg");
                            $(this).attr("name", "off");
                        }
                    );
                }
            }
        );

        $("#submitBtn").click(
            function() {

                var sp = 1;                   //需要存储的页面No
                if ($("#pageNo") != null) {
                    sp = $("#pageNo").val();
                }

                var sc = "";
                var mobiles = "";

                $('img.noclass').each(
                    function(index) {
                        if ($(this).attr("name") == "on") {
                            sc = sc + index + ","
                            if ($("#phone" + index).val() != "") {
                                mobiles = mobiles + $("#phone" + index).val() + ",";
                            }
                        }
                    }
                );

                sc = sc.substr(0, sc.length - 1);
                mobiles = mobiles.substr(0, mobiles.length - 1);

                $.ajax({
                    type:"POST",
                    url:"customer.do?method=getMobiles",
                    async:true,
                    data:{
                        sp:sp,
                        sc:sc,
                        mobiles:mobiles
                    },
                    cache:false,
                    dataType:"json",
                    success:function(data) {
                        var length = data.smsLength;
                        var mobiles = data.mobiles;
                        window.parent.document.getElementById("phoneAmount").innerHTML = length;
                        window.parent.document.getElementById("phoneNumbers").value = mobiles;
                    }
                });

                // window.parent.document.getElementById("phoneAmount").innerHTML = phoneAmount;
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                //window.parent.document.getElementById("iframe_PopupBox").src = "";
                //window.parent.document.getElementById("phoneNumbers").value = phones.substring(0,phones.length-1);

            }
        );

          //test if I can change
        $("#smsContent").blur(
            function() {
                var content = $("#smsContent").val();
                var length = content.length;
                     if(length>500) {
                         alert("短信内容最多为500个汉字，请修改后发送!") ;
                  }else{

                     }
                window.parent.document.getElementById("contentLength").innerHTML = length;
            }
        );


        $("#phoneNumbers").blur(
            function() {
                var phoneAmount = 0;
                var phoneNumbers = $("#phoneNumbers").val();
								phoneNumbers = phoneNumbers.replace(/[，；;]/g, ",");
								phoneNumbers = phoneNumbers.replace(/ /g, "");
								phoneNumbers = phoneNumbers.replace(/,,*/g, ",");
	            $("#phoneNumbers").val(phoneNumbers);
                if ($.trim(phoneNumbers) != "") {
                    var length = phoneNumbers.split(",").length;
                    if (length == 0) {
                        phoneAmount = 1;
                    } else {
	                      var phoneNumberArr =  phoneNumbers.split(",");
	                      for(var i=0;i<length;i++){
		                      if(phoneNumberArr[i]){
			                        phoneAmount ++;
                    }
                }

                    }
                }
               $("#phoneAmount").text(phoneAmount);
            }
        );
	      $("#phoneNumbers").blur();

        $("#moreCustomerSpan").bind("click", function() {
            window.location = "customer.do?method=customerdata";
        });
    }


);


function pageSmsCustomer(pageNo) {

    var sp = 1;                   //需要存储的页面No
    if ($("#pageNo") != null) {
        sp = $("#pageNo").val();
    }

    var sc = "";
    var mobiles = "";

    $('img.noclass').each(
        function(index) {
            if ($(this).attr("name") == "on") {
                sc = sc + index + ","
                if ($("#phone" + index).val() != "") {
                    mobiles = mobiles + $("#phone" + index).val() + ",";
                }
            }
        }
    );

    sc = sc.substr(0, sc.length - 1);
    mobiles = mobiles.substr(0, mobiles.length - 1);

    $.ajax({
        type:"POST",
        url:"customer.do?method=pageSmsClient",
        async:true,
        data:{
            pageNo:pageNo ,
            sp:sp,
            sc:sc,
            mobiles:mobiles
        },
        cache:false,
        success:function(data) {
            $("#chk_show").html(data);
            if ($("#checkAllCustomer").length > 0) {
                $("#check_all").attr("src", "/web/images/check_on.jpg");
                $("#check_all").attr("name", "on");
            } else {
                $("#check_all").attr("src", "/web/images/check_off.jpg");
                $("#check_all").attr("name", "off");
            }
        }
    });

}

function beChoose(obj) {
    if (obj.name == "off") {
        obj.src = "/web/images/check_on.jpg";
        obj.name = "on";
    } else if (obj.name == "on") {
        obj.src = "/web/images/check_off.jpg";
        obj.name = "off";
    }
}


