<%--
  Created by IntelliJ IDEA.
  User: king
  Date: 13-12-18
  Time: 下午3:03
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ taglib prefix="jUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
    <title>短信充值</title>
    <!--<script type="text/javascript" src="script/invoicing.js"></script>-->
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"  href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javaScript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.SMS_RECHARGE");

            if(${info != null}) {
                alert("${info}");
            }
            $(function(){
                changeStyle();
                $(":radio[name=rechargeamount]").live("click",function(){
                    if($(this).attr("presentAmount")) {
                        if($(this).attr("presentAmount") == 0) {
                            $("#preferentialSpan").html('您所充值的金额无法享受优惠');
                            $("#actualAmount").text($(this).val());
                            $("#presentAmountHidden").val(0);
                        } else {
                            $("#preferentialSpan").html('充值享优惠 充' + $(this).val() + '送' + $(this).attr("presentAmount"));
                            $("#actualAmount").text($(this).val()*1 + $(this).attr("presentAmount")*1);
                            $("#presentAmountHidden").val($(this).attr("presentAmount"));
                        }
                    }
                });

                if(${rechargeAmount != null}) {
                    init(${rechargeAmount});
                }
                //初始化充值金额
                function init(recharge_amount) {
                    var radios = document.getElementsByName("rechargeamount");
                    for (var i = 0, l = radios.length; i < l; i++) {
                        if (recharge_amount == radios[i].value) {
                            $(radios[i]).click();
                            return;
                        }
                    }
                    setOtherAmount(recharge_amount);
                }

                //设置其他充值金额
                function setOtherAmount(amount) {
                    if (isNaN(amount)) return;

                    amount = parseInt(amount);
                    if (amount < 50) return;
                    $("#input_otheramount")[0].value = amount;
                    $("#radio_otheramount")[0].value = amount;
                    $("#radio_otheramount")[0].checked = true;
                    $("#actualAmount").text(parseInt($("#actualAmount").text()));
                }

                function changeStyle() {
                    if($(".hot_orange")[0]) {
                        $(".hot_orange").css("height",$("#preferentialDiv").height() + 3);
                    }
                }

                $("#input_otheramount").keyup(function(){
                    $(this).val(APP_BCGOGO.StringFilter.inputtingFloatFilter($(this).val()));
                    if(!G.isEmpty($(this).val())) {
                        $("#radio_otheramount").attr("checked",true);
                    }
                })
                .blur(function(){
                            if(G.isEmpty($.trim($(this).val()))) {
                                if($("#radio_otheramount").attr("checked")) {
                                    $("#actualAmount").text(0);
                                }
                                return;
                            }
                            var radios = document.getElementsByName("rechargeamount");
                            var preferentialPolicies = [];
                            for (var i = 0, l = radios.length; i < l; i++) {
                               if($(radios[i]).attr("presentAmount") && $(radios[i]).attr("presentAmount") > 0) {
                                   var preferentialPolicy = {};
                                   preferentialPolicy["rechargeAmount"] = $(radios[i]).val();
                                   preferentialPolicy["presentAmount"] = $(radios[i]).attr("presentAmount");
                                   preferentialPolicies.push(preferentialPolicy);
                               }
                            }
                            var allChecked = true;
                            if(preferentialPolicies.length > 0) {
                               for(var i = 0; i < preferentialPolicies.length; i++) {
                                   if(preferentialPolicies[i].rechargeAmount * 1 > $(this).val() * 1) {
                                      if(i == 0) {
                                          $("#preferentialSpan").html('您所充值的金额无法享受优惠');
                                          $("#actualAmount").text($(this).val());
                                          $("#presentAmountHidden").val(0);
                                      } else {
                                          $("#preferentialSpan").html('充值享优惠 冲' + preferentialPolicies[i - 1].rechargeAmount + '送' + preferentialPolicies[i - 1].presentAmount);
                                          $("#actualAmount").text($(this).val()*1 + preferentialPolicies[i - 1].presentAmount*1);
                                          $("#presentAmountHidden").val(preferentialPolicies[i - 1].presentAmount);
                                      }
                                      allChecked = false;
                                      break;
                                   }
                               }
                               if(allChecked) {
                                   $("#preferentialSpan").html('充值享优惠 冲' + preferentialPolicies[preferentialPolicies.length - 1].rechargeAmount + '送' + preferentialPolicies[preferentialPolicies.length - 1].presentAmount);
                                   $("#actualAmount").text($(this).val()*1 + preferentialPolicies[preferentialPolicies.length - 1].presentAmount*1);
                                   $("#presentAmountHidden").val(preferentialPolicies[preferentialPolicies.length - 1].presentAmount);
                               }
                            } else {
                                $("#actualAmount").text($(this).val());
                            }
                });

                $("#button_recharge").click(function(){
                    if ($("#radio_otheramount")[0].checked) {
                        var amount = $("#input_otheramount")[0].value;
                        if(G.isEmpty(amount)) {
                            alert("请输入充值金额！");
                           return;
                        }
                        if (isNaN(amount)) {
                            alert("请输入正确的充值金额！");
                            return;
                        }

                        amount = parseInt(amount);
                        if (amount < 50) {
                            alert("充值金额须大于等于50！");
                            return;
                        }

                        setOtherAmount(amount);
                    }
                    $("#form_smsrecharge")[0].submit();
                });

                $("#smsAccount").click(function(){
                    window.location.href = 'smsrecharge.do?method=shopSmsAccount';
                });
            });
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">短信管理</div>
    </div>
    <div class="messageContent">
        <jsp:include page="/sms/smsNavi.jsp">
            <jsp:param name="currPage" value="smsrecharge" />
        </jsp:include>
        <div class="messageRight">
            <div class="advertise_r">
                <c:if test="${not empty preferentialPolicyImageURL}">
                    <img src="${preferentialPolicyImageURL}" />
                </c:if>
            </div>
            <div class="step-01">
                <ul>
                    <li><span>1、选择充值金额</span></li>
                    <li>2、银联卡支付</li>
                    <li>3、完成</li>
                </ul>
            </div>
            <div class="messageRight_radius">
                <table width="100%" border="0" cellspacing="0" class="equal2">
                    <colgroup>
                        <col width="150">
                        <col>
                    </colgroup>
                    <tr>
                        <td class="equal2_title">当前余额</td>
                        <td><div class="message_coin"><strong>${smsBalance == null ? 0 : smsBalance}</strong> 元</div> <a class="blue_color" onclick="toHelper('transferHelper')">短信充值帮助</a></td>
                    </tr>
                    <tr>
                        <td class="equal2_title">充值金额</td>
                        <td>
                            <form name="form_smsrecharge" id="form_smsrecharge" target="_blank"
                                  action="smsrecharge.do" method="get" style="display:inline;">
                            <input type="hidden" name="method" value="smsrecharging"/>
                            <c:forEach items="${preferentialPolicyList}" var="preferentialPolicy" >
                                <label class="rad"><input type="radio" name="rechargeamount"  value="${jUtil:roundInt(preferentialPolicy.rechargeAmount)}" presentAmount="${jUtil:roundInt(preferentialPolicy.presentAmount)}"/>${jUtil:roundInt(preferentialPolicy.rechargeAmount)}元 </label>
                            </c:forEach>
                            <input type="hidden" id="presentAmountHidden" name="presentAmount" />
                            <label class="rad"><input type="radio" name="rechargeamount"  id="radio_otheramount" />其他</label>
                            </form>
                           <input  type="text"  class="amount_input" id="input_otheramount"
                                  style="width: 45px;border: 1px solid #CDCDCD;height: 19px;margin: 0 3px 3px 0;vertical-align: middle;" autocomplete="off" />元
                        </td>

                    </tr>
                    <tr>
                        <td class="equal2_title">可享优惠</td>
                        <td><div class="discount" id="preferentialSpan">充值享优惠 充1000送100</div>
                            <c:if test="${hasPreferentialPolicy}">
                                <div class="hot_orange">
                                    <strong>热推：</strong>
                                    <p id="preferentialDiv">
                                        <c:forEach items="${preferentialPolicyList}" var="preferentialPolicy" >
                                            <c:if test="${preferentialPolicy.presentAmount > 0}">
                                                * 充${jUtil:roundInt(preferentialPolicy.rechargeAmount)} 送${jUtil:roundInt(preferentialPolicy.presentAmount)}元 享${jUtil:roundInt(preferentialPolicy.rechargeAmount/(preferentialPolicy.rechargeAmount + preferentialPolicy.presentAmount)*10)}分折后价；<br />
                                            </c:if>
                                        </c:forEach>
                                    </p>
                                </div>
                            </c:if>

                        </td>
                    </tr>
                    <tr>
                        <td class="equal2_title">实际到账</td>
                        <td><div class="message_coin"><strong class="orange_color" id="actualAmount">0</strong> 元</div></td>
                    </tr>
                    <tr>
                        <td class="equal2_title">支付方式</td>
                        <td><img src="images/message/yl_r2_c2.jpg" width="122" height="29" /></td>
                    </tr>
                </table>
            </div>
            <div class="clear i_height"></div>
            <div class="promptTxt_right">提示：初次使用银联支付时请先安装安全控件，安装完成后刷新页面再支付。
            </div>
            <div class="btnTit">
                <input name="" type="button"  class="blueBtn_76" value="马上充值" id="button_recharge"/>
                <input name="" type="button"  class="blueBtn_76" value="查看账单" id="smsAccount"/>
            </div>
        </div>

    </div>
</div>
<div id="mask"  style="display:block;position: absolute;"> </div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
