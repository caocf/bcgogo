(function() {
    var bcgogo = {version:1.0};

    bcgogo["get"] = function(url) {
        try {
            return eval(APP_BCGOGO.Net.syncGet({url:url}));
        } catch(e) {
            return null;
        }
    }

    bcgogo.post = function(url, data) {
        try {
            return eval(APP_BCGOGO.Net.syncPost({url:url, data:data}));
        } catch(e) {
            return null;
        }
    }

    $().ready(function() {

        var p = $.url.param(window.location.search);

        if ("rechangeamount" in p && p.rechangeamount) {
            var radios = document.getElementsByName("radio");
            for (var i = 0, l = radios.length; i < l; i++) {
                if (p.rechangeamount == radios[i].value) {
                    radios[i].checked = true;
                    break;
                }
            }
        }

        $("#multipleMoney")[0].onfocus = function() {

            $("#input_radiother")[0].checked = true;

            if ($("#multipleMoney")[0].value == $("#multipleMoney")[0].defaultValue) {
                $("#multipleMoney")[0].value = "";
            }

        }

        $("#input_confirm")[0].onclick = function() {
            var amount;//金额

            if ($("#input_radiother")[0].checked) {
                if (isNaN($("#multipleMoney")[0].value)) {
                    alert("请输入充值金额！");
                    $("#multipleMoney")[0].value = $("#multipleMoney")[0].defaultValue;
                    return;
                }

                if (parseInt($("#multipleMoney")[0].value) < 50) {
                    alert("最低充值金额50！");
                    $("#multipleMoney")[0].focus();
                    return;
                }
                amount = parseInt($("#multipleMoney")[0].value);
            }
            else {
                var radios = document.getElementsByName("radio");
                for (var i = 0, l = radios.length; i < l; i++) {
                    if (radios[i].checked && radios[i].value) {
                        amount = radios[i].value;
                        break;
                    }
                }
            }

            if (!amount) {
                alert("请选择或输入充值金额！");
                return;
            }
            var r = APP_BCGOGO.Net.asyncGet({url:"smsrecharge.do?method=postdata",
                data:{"amount":amount, desc:"短信充值"},dataType:"json"});
            if (r.length == 0) return;

            if (typeof r != "object") {
                alert("暂时无法充值！");
            }
            else {
                if ("formStr" in r && "rechargeNumber" in r) {
                    var div = document.body.appendChild($("<div>")[0]);
                    div.innerHTML = r["formStr"];

                    if ($("#form_chinapay")[0]) {
                        $("#form_chinapay")[0].target = "_blank";
                        $("#form_chinapay")[0].submit();

                        Mask.Login();

                        $("#iframe_PopupBox")[0].style.display = "";
                        $("#iframe_PopupBox")[0].src = "smsrecharge.do?method=smsrechargein&rechargeNumber=" + r["rechargeNumber"];

                        var countserInterval = setInterval(function() {
                            getSmsrechargeNumber(r["rechargeNumber"], countserInterval)
                        }, 1000);
                    }
                    else {
                        alert("暂时无法充值！");
                    }
                    document.body.removeChild(div);
                }
                else {
                    alert("暂时无法充值！");
                }
            }
        }
    });

    function getSmsrechargeNumber(rechargeNumber, intervalObj) {
        var r = APP_BCGOGO.Net.asyncGet({url:"smsrecharge.do?method=getsmsrechargestate",
            data:{"rechargeNumber":rechargeNumber},dataType:"json"});
        if (r.length == 0) return;
        if (!("state" in r) || r["state"] == null) {
            alert("充值失败，请稍候重试！");
            $("#iframe_PopupBox")[0].style.display = "none";
            $("#mask")[0].style.display = "none";
            clearInterval(intervalObj);
        }
        else if (r["state"] == 1 && !("paytime" in r)) {
            if ($("#iframe_PopupBox")[0].contentWindow.document.getElementById("rechangeIn_state")) {
                $("#iframe_PopupBox")[0].contentWindow.document.getElementById("rechangeIn_state").value = "请付款";
            }
        }
        else if (r["state"] == 2 || (r["state"] == 1 && "paytime" in r)) {
            window.location.assign("smsrecharge.do?method=smsrechargejump&rechargeNumber=" + rechargeNumber);
        }
    }

})();