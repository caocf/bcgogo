<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-17
  Time: 下午3:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>经营信息</title>
    <link rel="stylesheet" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/uploadPreview<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(function () {
            var regNum2 = /[^0-9]/g;//过滤掉非数字字符
//            var r = bcgogo.get("shop.do?method=getname");
//            $("#span_userName").innerHTML = r.userName;

            function filterNonNumeric() {
                if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                    var s = this.value.replace(regNum2, "");
                    if (s != this.value) {
                        this.value = s;
                    }
                }
            }

            $("#input_personnel")[0][document.all ? "onpropertychange" : "oninput"] = filterNonNumeric;
            $("#input_area")[0][document.all ? "onpropertychange" : "oninput"] = filterNonNumeric;

            var input_operate = $("#input_operate")[0];
            var input_business = $("#input_business")[0];
            var input_busScope = $("#input_busScope")[0];
            var input_relatedbus = $("#input_relatedbus")[0];
            var input_storeCharacter = $("#input_storeCharacter")[0];
            var iframe_PopupBox = $("#iframe_PopupBox")[0];

            input_operate.onclick = function () {
                bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':'shop.do?method=shopoperatemode'});
            }
            input_business.onclick = function () {
                bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':'shop.do?method=shopbustime'});
            }
            input_busScope.onclick = function () {
                bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':'shop.do?method=shopbuscontent'});
            }
            input_relatedbus.onclick = function () {
                bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':'shop.do?method=shoprelatedbus'});
            }
            input_storeCharacter.onclick = function () {
                bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':'shop.do?method=shopstorecharacter'});
            }
        });
        var submitTimes = 0;
        $(document).ready(function () {
            $("#imgfile").uploadPreview({width:50, height:30, imgDiv:"#imgDiv", imgType:["bmp", "gif", "png", "jpg"], maxwidth:800, maxheight:600});
            $('#register').click(function () {

                if (!$("#input_established")[0].value) {
                    nsDialog.jAlert("请选择成立时间");
                }
                else {
                    jQuery("#uploading").css("display", "block");
                    submitTimes = submitTimes + 1;
                    if (submitTimes >= 2) {
                        nsDialog.jAlert("请勿重复注册！");
                    }
                    else {
                        $('#form_save').ajaxSubmit(function (data) {
                            if (-1 != data.indexOf("pre") || -1 != data.indexOf("PRE")) {
                                data = data.substring(5, data.length - 6);
                            }
                            var jsonObj = JSON.parse(data);
                            if (jsonObj.resu == "maxOver") {
                                nsDialog.jAlert("上传照片大于2M，无法上传！");
                            }
                            else if (jsonObj.resu == "success") {
                                nsDialog.jAlert("注册成功！请耐心等待后台人员审核！", null, function () {
                                    jQuery("#uploading").css("display", "none");
                                    <bcgogo:permission>
                                    <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                                        window.location = "user.do?method=createmain";
                                    </bcgogo:if>
                                    <bcgogo:else>
                                        window.location.assign("j_spring_security_logout");
                                    </bcgogo:else>
                                    </bcgogo:permission>

                                });
                            }
                            else {
                                nsDialog.jAlert("注册失败！请重新注册！");
                            }
                        });
                        return false;
                    }
                }
            });
        });
        $().ready(function() {
            $("#input_established").datepicker({
                "numberOfMonths" : 1,
                "showButtonPanel": true,
                "changeYear":true,
                "yearRange":"c-30:c+30",
                "changeMonth":true,
                "yearSuffix":""
            });
        });
    </script>
</head>

<body class="bodyMain">

<div class="m_topMain">
    <div class="l_top">
        <div class="l_topBorder"></div>
        <a target="_blank" href="http://www.bcgogo.com">
            <div class="home"></div>
        </a>

        <div class="l_topBorder"></div>
        <div class="l_topTitle">感谢使用一发EasyPower软件</div>
        <div class="l_topBorder"></div>
        <div style="float:left; width:70px; text-align:center; line-height:27px; cursor:pointer;"><a href="http://www.bcgogo.com/industrynews.htm" style=" color:#BEBEBE;" target="_blank">系统公告</a></div>
        <div class="l_topBorder"></div>
        <div class="l_topRight">
            <div class="l_topBorder"></div>
            <div style="float:left; width:100px; text-align:center; line-height:27px;">欢迎您，<span
                    id="span_userName">${userName}</span>！
            </div>
            <div class="l_topBorder"></div>
            <div class="exist"><a href="j_spring_security_logout">退出</a></div>
            <div class="l_topBorder"></div>
        </div>
    </div>
</div>

<div class="register_titleBg">
    <div class="register_titleBtn">
        <div class="register_personal"><a>基础信息</a></div>
        <div class="register_companyHover"><a>经营信息</a></div>
    </div>
</div>
<div class="register_main">
    <form:form id="form_save" action="shop.do?method=saveshop" method="post" enctype="multipart/form-data">
        <input type="hidden" id="customerId" name="customerId" value="${customerId}"/>
        <table cellpadding="0" cellspacing="0" class="register_table">
            <col width="70">
            <col>
            <col width="10">
            <tr>
                <td>经营方式</td>
                <td>
                    <div class="register_operate" id="input_operate">
                        <div class="register_icon"><input type="button" onfocus="this.blur();"/></div>
                        <!--<span id="span_operate" name="operationMode">连锁加盟</span>-->
                        <input type="text" id="span_operate" name="operationMode" value="加盟连锁" readonly="true"
                               style="border-style: none solid;border: 0 0 0 0;width:100px;border-color:#FFF;"/>
                    </div>
                    <div class="register_operate" id="input_business">
                        <div class="register_icon"><input type="button" onfocus="this.blur();"/></div>
                        <!--<span id="span_busTime" name="businessHours">24小时营业</span>-->
                        <input type="text" id="span_busTime" name="businessHours" value="24小时营业" readonly="true"
                               style="border-style: none solid;border: 0 0 0 0;width:100px;border-color:#FFF;"/>
                    </div>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>
                    <div class="register_info">
                        <div style="width:55px; float:left;">成立时间</div>
                        <input id="input_established" type="text" style="width:80px;" name="established"
                               readonly="true"/></div>
                    <div class="register_info">
                        <div style="width:55px; float:left;">资　　质</div>
                        <input type="text" style="width:80px;" name="qualification" id="input_qualification"/></div>
                    <div class="register_info">
                        <div style="width:55px; float:left;">人　　员</div>
                        <input type="text" style="width:80px;" name="personnel" id="input_personnel"/></div>
                    <div class="register_info">
                        <div style="width:55px; float:left;">面　　积</div>
                        <div style="float:left;"><input type="text" style="width:80px;" name="area" id="input_area"/>
                        </div>
                        <div class="area">㎡</div>
                    </div>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>经营产品</td>
                <td>
                    <div class="register_busScope" id="input_busScope">
                        <div class="register_icon"><input type="button" onfocus="this.blur();"/></div>
                        <!--<span id="span_busScope" name="businessScope">汽车保养</span>-->
                        <input type="text" id="span_busScope" name="businessScope" value="汽车保养" readonly="true"
                               style="border-style: none solid;border: 0 0 0 0;width:250px;border-color:#FFF;"/>
                        <input type="hidden" id="hidden_busScope" name="busScope"/>
                    </div>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>相关业务</td>
                <td>
                    <div class="register_busScope" id="input_relatedbus">
                        <div class="register_icon"><input type="button" onfocus="this.blur();"/></div>
                        <!--<span id="span_relatedbus" name="relatedBusiness">救援</span>-->
                        <input type="text" id="span_relatedbus" name="relatedBusiness" value="救援" readonly="true"
                               style="border-style: none solid;border: 0 0 0 0;width:250px;border-color:#FFF;"/>
                    </div>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>店面特色</td>
                <td>
                    <div class="register_busScope" id="input_storeCharacter">
                        <div class="register_icon"><input type="button" onfocus="this.blur();"/></div>
                        <!--<span id="span_storeCharacter" name="feature">免费车接送</span>-->
                        <input type="text" id="span_storeCharacter" name="feature" value="免费车接送" readonly="true"
                               style="border-style: none solid;border: 0 0 0 0;width:250px;border-color:#FFF;"/>
                    </div>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>店面照片</td>
                <td colspan="2">
                    <!--<input type="text" style="width:260px;float:left;"/><input type="button" class="upload"
                                                                                           value="上传"
                                                                                           onfocus="this.blur();"/>-->


                    <input type="file" id="imgfile" name="input_fileLoad" target="iframe_fileLoad"/>

                    <div style="width: 50px; height: 30px; overflow: hidden; " id="parentImgDiv">
                        <div style="width: 50px; height: 30px; " id="imgDiv">
                        </div>
                    </div>

                </td>
            </tr>
            <tr>
                <td>备注</td>
                <td>
                    <textarea name="memo" class="register_txtarea"></textarea>
                </td>
            </tr>
            <tr>
                <td colspan="2"><input id="register" type="button" class="register_next" value="同意用户协议并注册"
                                       onfocus="this.blur();"/>
                    <img style="display:none" title="正在注册...." alt="正在注册...." src="images/loadinglit.gif"
                         id="uploading">
                </td>
                <td></td>
            </tr>
            <tr class="agreement">
                <td colspan="2" class="provision"><a href="shop.do?method=shopagreement"
                                                     target="_blank">阅读统购信息软件服务条款</a></td>
                <td></td>
            </tr>
        </table>
    </form:form>
    <div class="register_sign"></div>
    <div class="register_Title">商机管家一触即发</div>
    <div class="register_num">第<span>${shopCount}</span>位</div>
    <div class="register_serial">Easy Power软件用户序列</div>
</div>


<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" scrolling="no"
        allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
