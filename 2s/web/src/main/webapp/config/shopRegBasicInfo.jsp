<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-17
  Time: 下午3:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>基础信息</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>

<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
<script language="javascript" type="text/javascript">
(function () {

    function focusAndSelect(idName) {
        $("#" + idName).focus().select();
    }

    $().ready(function () {

//        var r = bcgogo.get("shop.do?method=getname");
//        $("span_userName").innerHTML = r.userName;
        var regC = /[^\u4E00-\u9FA5]/g;//过滤掉非汉字字符
        var regC2 = /[\u4E00-\u9FA5]/g;//过滤掉汉字字符
        var regNum = /[0-9]/g;//过滤掉数字字符
        var regNum2 = /[^0-9]/g;//过滤掉非数字字符

        var regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

        var regSy = new RegExp("[`~!@#$^&()=|\\\\{\\}%_\\+\"':;',\\[\\]<>/?~！@#￥……&（）——|{}【】‘；：”“'。，、？·～《》]", "g");

        //非电子邮件符号的验证
        var regSy2 = new RegExp("[`~!#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\]<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");

        //过滤掉所有特殊符号
        var regS = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");

        $("#input_name")[0].onblur = function () {
            $("#input_name").val($.trim($("#input_name").val()));
            $.ajax({
                       type:"POST",
                       url:"shop.do?method=checkshopname",
                       async:true,
                       data:{name:$("#input_name").val()},
                       cache:false,
                       dataType:"json",
                       success:function(jsonStr) {
                           if (jsonStr === null) {
                               return;
                           }
                           else if (typeof jsonStr == "string") {
                               if (jsonStr == "false") {
                                   nsDialog.jAlert("本单位名称已经注册，请在店名后增加地区、路名等便于区别，谢谢！", null, function() {
                                       focusAndSelect("input_name");
                                   });
                               }
                           }
                       }
                   }
            );
        }
        $("#input_storeManagerMobile")[0].onblur = function () {
            $.ajax({
               type:"POST",
               url:"shop.do?method=checkStoreManagerMobile",
               async:true,
               data:{mobile:$("#input_storeManagerMobile").val()},
               cache:false,
               dataType:"json",
               success:function(jsonStr) {
                   if (jsonStr === null) {
                       return;
                   }
                   else if (typeof jsonStr == "string") {
                       if (jsonStr == "true") {
                           nsDialog.jConfirm("该手机号码已经注册其他店面，是否继续使用此号码注册该店面？", null, function(returnVal) {
                               if (!returnVal) {
                                   $("#input_storeManagerMobile").val("");
                               }
                               else {
                                   return;
                               }
                           });
                       }
                   }
               }
           });
        };

        //绑定下拉列表的值        select_province    select_city    select_township
        provinceBind();
        $("#select_province")[0].onchange = function () {
            cityBind(this);
        };
        $("#select_city")[0].onchange = function () {
            licensePlate(this);
            townshipBind(this);
        };
        $("#select_township")[0].onchange = function () {

            if (this.selectedIndex != 0) {

                $("#input_areaId")[0].value = this.value;

                $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                        + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML
                        + $("#select_township")[0].options[$("#select_township")[0].selectedIndex].innerHTML;

            }
            else {
                $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                        + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;
            }

        };

        $("#input_address")[0].value = "";

        $("#input_owner")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regNum, "");
                s = s.replace(regS, '');
                if (s != this.value) {
                    this.value = s;
                }
            }
        }

        $("#input_mobile")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regNum2, "");
                if (s != this.value) {
                    this.value = s;
                }
            }
        }

        $("#input_landLine")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regSy, "");
                s.replace(regC2, '');
                if (s != this.value) {
                    this.value = s;
                }
            }
        }

        $("#input_storeManager")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regC, "");
                if (s != this.value) {
                    this.value = s;
                }
            }
        }

        $("#input_storeManagerMobile")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regNum2, "");
                if (s != this.value) {
                    this.value = s;
                }
            }
        };

        $("#input_qq")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regNum2, "");
                if (s != this.value) {
                    this.value = s;
                }
            }
        };

        $("#input_email")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regC2, "");
                s.replace(regSy2, '');
                if (s != this.value) {
                    this.value = s;
                }
            }
        };

        $("#input_email")[0].onblur = function () {
            if ($("#input_email")[0].value != "" && !regEmail.test($("#input_email")[0].value)) {
                nsDialog.jAlert("非法电子邮箱格式");
            }
        };

        $("#input_softPrice")[0][document.all ? "onpropertychange" : "oninput"] = function () {
            if ((!document.all || window.event.propertyName == "value") && this.value != this.defaultValue) {
                var s = this.value.replace(regNum2, "");
                if (s != this.value) {
                    this.value = s;
                }
            }
        };

        //点击下一步按钮的时候进行验证并提交表单
        $("#register_next")[0].onclick = function () {
            var patrn = /^[1][358][0-9]{9}$/;
            if (!$("#input_name")[0].value || !$.trim($("#input_name").val())) {
                nsDialog.jAlert("请输入单位名称");
            }
            else {
                if ($("#licencePlate").val() == '' || $("#licencePlate").val() == null) {
                    nsDialog.jAlert("车牌前缀不能为空，请补全地区信息或者手动填写车牌前缀");
                }
                else {
                    if (!$("#input_name")[0].value || !$.trim($("#input_name").val())) return;

                    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=checkshopname&name=" + $.trim($("#input_name").val()),"dataType":"text"});
                    if (r === null) {
                        return;
                    }
                    else if (typeof r == "string") {
                        if (r == "false") {
                            nsDialog.jAlert("本单位名称已经注册，请在店名后增加地区、路名等便于区别，谢谢！");
                            return false;
                        }
                    }

                    if (!$("#input_areaId")[0].value) {
                        nsDialog.jAlert("请选择地区");
                        return false;
                    }

                    if (!$("#input_owner")[0].value
                            || !$.trim($("#input_owner").val())
                            ) {
                        nsDialog.jAlert("请输入负责人/店主");
                        return false;
                    }
                    if (!$("#input_mobile")[0].value
                            || !$.trim($("#input_mobile").val())
                            ) {
                        nsDialog.jAlert("请输入联系手机");
                        return false;
                    }
                    else if (!patrn.exec($("#input_mobile")[0].value)) {
                        nsDialog.jAlert("店主/负责人的联系手机号码输入有误！请重新输入！");
                        return false;
                    }
                    if (!$("#input_storeManager")[0].value
                            || !$.trim($("#input_storeManager").val())
                            ) {
                        nsDialog.jAlert("请输入店面管理员");
                        return false;
                    }
                    if (!$("#input_storeManagerMobile")[0].value
                            || !$.trim($("#input_storeManagerMobile").val())
                            ) {
                        nsDialog.jAlert("请输入店面管理员联系手机");
                        return false;
                    } else if (!patrn.exec($("#input_storeManagerMobile")[0].value)) {
                        nsDialog.jAlert("店面管理员的联系手机号码输入有误！请重新输入！");
                        return false;
                    }

                    if (!$("#input_count")[0]) {
                        $.ajax({
                                   type:"POST",
                                   url:"shop.do?method=checkStoreManagerMobile",
                                   async:true,
                                   data:{mobile:$("#input_storeManagerMobile").val()},
                                   cache:false,
                                   dataType:"json",
                                   success:function(jsonStr) {
                                       if (jsonStr === null) {
                                           return;
                                       }
                                       else if (typeof jsonStr == "string") {
                                           if (jsonStr == "true") {
                                               nsDialog.jConfirm("您用此手机已经注册了其他店面，现在新增注册管理" + $("#input_name")[0].value + "店",
                                                                 null, function(returnVal) {
                                                           if (!returnVal) {
                                                               var input = $("<input>")[0];
                                                               input.type = "hidden";
                                                               input.id = "input_count";
                                                               input.value = r.count;
                                                               document.body.appendChild(input);
                                                           }
                                                       });
                                           }
                                       }
                                   }
                               });
                    }

                    if ($("#input_email")[0].value != "" && !regEmail.test($("#input_email")[0].value)) {
                        nsDialog.jAlert("非法电子邮箱格式");
                        return false;
                    }

                    if (!$("#input_agent")[0].value
                            || !$.trim($("#input_agent").val())
                            ) {
                        nsDialog.jAlert("请输入业务员");
                        return false;
                    }

                    if (!$("#input_agentMobile")[0].value
                            || !$.trim($("#input_agentMobile").val())
                            ) {
                        nsDialog.jAlert("请输入业务员手机号");
                        return false;
                    }

//                    if (!$("#input_agentId")[0].value
//                            || !$.trim($("#input_agentId").val())
//                            || isNaN($("#input_agentId")[0].value)
//                            ) {
//                        nsDialog.jAlert("请输入业务员ID");
//                        return false;
//                    }

                    if (!$("#input_softPrice")[0].value
                            || !$.trim($("#input_softPrice").val())
                            || isNaN($("#input_softPrice").val())
                            ) {
                        nsDialog.jAlert("请输入软件销售价");
                        return false;
                    }

                    $("#form_reg")[0].submit();
                    //window.location.assign("shop.do?method=shopbusinessinfo");
                }
            }
        };

        //    单位名称
        $("#input_name").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("shortname");
            }
        });
        //简称
        $("#shortname").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("licencePlate");
            }
        });
        $("#shortname").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_name");
            }
        });
        //所在地车牌前缀：
        $("#licencePlate").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_address");
            }
        });
        $("#licencePlate").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("shortname");
            }
        });
        //地址
        $("#input_address").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_owner");
            }
        });
        $("#input_address").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("licencePlate");
            }
        });

        //负责人/店主
        $("#input_owner").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_mobile");
            }
        });
        $("#input_owner").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_address");
            }
        });

        //联系手机
        $("#input_mobile").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_landLine");
            }
        });
        $("#input_mobile").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_owner");
            }
        });

        //固定电话
        $("#input_landLine").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_storeManager");
            }
        });
        $("#input_landLine").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_mobile");
            }
        });

        //     店面管理员
        $("#input_storeManager").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_storeManagerMobile");
            }
        });
        $("#input_storeManager").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_landLine");
            }
        });
        //联系手机
        $("#input_storeManagerMobile").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_qq");
            }
        });
        $("#input_storeManagerMobile").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_storeManager");
            }
        });

        //QQ
        $("#input_qq").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_email");
            }
        });
        $("#input_qq").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_storeManagerMobile");
            }
        });
        //email
        $("#input_email").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_agent");
            }
        });
        $("#input_email").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_qq");
            }
        });
        //业务员
        $("#input_agent").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_agentId");
            }
        });
        $("#input_agent").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_email");
            }
        });
        //业务员  ID
        $("#input_agentId").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_agentMobile");
            }
        });
        $("#input_agentId").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_agent");
            }
        });
        //业务员手机号
        $("#input_agentMobile").live("keydown", function (event) {
            //向下，右箭头
            if (event.keyCode == 39 || event.keyCode == 40) {
                focusAndSelect("input_softPrice");
            }
        });
        $("#input_agentMobile").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_agentId");
            }
        });
        //销售价格
        //        $("#input_agentMobile").live("keydown",  function(event)
        //                    {
        //向下，右箭头
        //                      if (event.keyCode == 39||event.keyCode == 40) {
        //                          $("#input_softPrice").focus();
        //                          $("#input_softPrice").select();
        //                      }
        //                    });
        $("#input_softPrice").live("keydown", function (event) {
            if (event.keyCode == 37 || event.keyCode == 38) {
                focusAndSelect("input_agentMobile");
            }
        });
    });


    //第一级菜单 select_province
    function provinceBind() {
        var r = APP_BCGOGO.Net.syncGet({url:"shop.do?method=selectarea",
                                           data:{parentNo:1},dataType:"json"});
        if (!r||r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_province")[0].appendChild(option);
            }
        }
    }

    //第二级菜单 select_city
    function cityBind(select) {
        if (select.selectedIndex == 0) {
            $("#select_city")[0].style.display = "none";
            $("#select_township")[0].style.display = "none";
            $("#input_areaId")[0].value = "";
            $("#input_address")[0].value = "";

            return;
        }

        $("#select_township")[0].style.display = "none";

        while ($("#select_city")[0].options.length > 1) {
            $("#select_city")[0].remove(1);
        }

        $("#input_areaId")[0].value = select.value;
        $("#select_city")[0].style.display = "block";

        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;

        var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
        if (r === null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city")[0].appendChild(option);
            }
        }
    }

    function licensePlate(select) {
        if (select.selectedIndex == 0) {
            $("#licencePlate")[0].value = "";
            return;
        }
        else {

        }
        var r = APP_BCGOGO.Net.syncGet({"url":"product.do?method=searchlicenseNo&localArea=" + select.value, "dataType":"json"});
        if (r === null) {
            return;
        }
        else {
            $("#licencePlate")[0].value = r[0].platecarno;
        }
    }

    //第三级菜单 select_township
    function townshipBind(select) {
        if (select.selectedIndex == 0) {
            $("#select_township")[0].style.display = "none";
            $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;
            return;
        }
        else {

        }
        $("#input_areaId")[0].value = select.value;
        $("#select_township")[0].style.display = "block";

        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;

        var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
        if (r === null || typeof(r) == "undefined") {
            return;
        }
        else {
            while ($("#select_township")[0].options.length > 1) {
                $("#select_township")[0].remove(1);
            }
            if (typeof(r) != "undefined" && r.length > 0) {
                for (var i = 0, l = r.length; i < l; i++) {
                    var option = $("<option>")[0];
                    option.value = r[i].no;
                    option.innerHTML = r[i].name;
                    $("#select_township")[0].appendChild(option);
                }
            }
        }
    }
})();


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
        <div class="l_topTitle">欢迎使用一发软件</div>
        <div class="l_topBorder"></div>
        <div style="float:left; width:70px; text-align:center; line-height:27px; cursor:pointer;"><a
                href="http://www.bcgogo.com/industrynews.htm" style=" color:#BEBEBE;" target="_blank">公告中心</a></div>
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
        <div class="register_personalHover"><a>基础信息</a></div>
        <div class="register_company"><a>经营信息</a></div>
    </div>
</div>
<div class="register_main">
    <form id="form_reg" action="shop.do?method=shopbusinessinfo" method="post">
        <table cellpadding="0" cellspacing="0" class="register_table">
            <tr>
                <td>单位名称</td>
                <td><input type="text" name="name" id="input_name"/></td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>简称</td>
                <td>
                    <input type="text" id="shortname" name="shortname" style="width:100px; float:left;"/>

                    <div style="float:left; padding:0px 3px 0px 20px; line-height:25px;">所在地车牌前缀：</div>
                    <input type="text" id="licencePlate" name="licencePlate" maxlength="2"
                           style="width:80px; float:left;"/>
                </td>
                <td><img src="images/star.jpg"/></td>
                <td></td>
            </tr>
            <tr>
                <td>地区<input type="hidden" name="areaId" id="input_areaId" value="1"/></td>
                <td>
                    <select class="register_position" id="select_province" name="province">
                        <option selected="selected">请选择</option>
                    </select>
                    <select class="register_position" id="select_city" name="city" style="display:none;">
                        <option selected="selected">请选择</option>
                    </select>
                    <select class="register_position" id="select_township" name="region" style="display:none;">
                        <option selected="selected">请选择</option>
                    </select>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>地址</td>
                <td><input id="input_address" name="address" type="text" class="register_border"/></td>
                <td></td>
            </tr>
            <tr>
                <td>负责人/店主</td>
                <td><input type="text" name="owner" id="input_owner" value="${customerDTO.name}"/>
                    <input type="hidden" id="customerId" name="customerId" value="${customerDTO.id}"/></td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>联系手机</td>
                <td><input type="text" name="mobile" id="input_mobile" maxlength="11" value="${customerDTO.mobile}"/>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>固定电话</td>
                <td><input type="text" name="landLine" id="input_landLine" value="${customerDTO.landLine}"/></td>
                <td></td>
            </tr>
            <tr>
                <td>店面管理员</td>
                <td><input type="text" name="storeManager" id="input_storeManager" value="${customerDTO.name}"/></td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>联系手机</td>
                <td><input type="text" name="storeManagerMobile" id="input_storeManagerMobile" maxlength="11"
                           value="${customerDTO.mobile}"/></td>
                <td><img src="images/star.jpg" id="img_ajaxload"/></td>
            </tr>
            <tr>
                <td>QQ</td>
                <td><input type="text" name="qq" id="input_qq" value="${customerDTO.qq}"/></td>
                <td></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type="text" name="email" id="input_email" value="${customerDTO.email}"/></td>
                <td></td>
            </tr>
            <tr>
                <td>业务员</td>
                <td>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                            <input type="text" name="agent" id="input_agent" readonly="true" value="${userDTO.name}" /></td>
                        </bcgogo:if>
                        <bcgogo:else>
                            <input type="text" name="agent" id="input_agent" value="${userDTO.name}"/></td>
                        </bcgogo:else>
                    </bcgogo:permission>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                    <tr style="display:none;">
                        <td>业务员ID</td>
                        <td><input type="text" name="agentId" id="input_agentId" maxlength="20" value="0" />
                        </td>
                        <td><img src="images/star.jpg"/></td>
                    </tr>
                </bcgogo:if>
                <bcgogo:else>
                    <tr>
                        <td>业务员ID</td>
                        <td><input type="text" name="agentId" id="input_agentId" maxlength="20" readonly="true" value="${userDTO.userNo}"/>
                        </td>
                        <td><img src="images/star.jpg"/></td>
                    </tr>
                </bcgogo:else>
            </bcgogo:permission>

            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                <tr class="notice">
                    <td colspan="2">（新注册店铺以此业务员作为批发商的联系人）</td>
                    <td></td>
                </tr>
            </bcgogo:hasPermission>
            <tr>
                <td>业务员手机号</td>
                <td>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                            <input type="text" name="agentMobile" id="input_agentMobile"/>
                        </bcgogo:if>
                        <bcgogo:else>
                            <input type="text" name="agentMobile" id="input_agentMobile" readonly="true" value="${userDTO.mobile}"/>
                        </bcgogo:else>
                    </bcgogo:permission>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
                <tr class="notice">
                    <td colspan="2">（新注册店铺以此号码作为批发商的联系方式）</td>
                    <td></td>
                </tr>
            </bcgogo:hasPermission>
            <tr>
                <td>软件版本</td>
                <td>
                    <select name="shopVersionId" class="register_position" style="width:300px;height:30px;">
                        <c:forEach var='shopVersionDTO' items='${shopVersionDTOList}'>
                            <option value="${shopVersionDTO.id}">${shopVersionDTO.value}</option>
                        </c:forEach>
                    </select>
                </td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td>软件销售价</td>
                <td><input type="text" name="softPrice" id="input_softPrice"/></td>
                <td><img src="images/star.jpg"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input id="register_next" type="button" class="register_next" value="下一步" onfocus="this.blur();"/></td>
                <td></td>
            </tr>
        </table>
    </form>
    <div class="register_sign"></div>
    <div class="register_Title">商机管家一触即发</div>
    <div class="register_num">第<span>${shopCount}</span>位</div>
    <div class="register_serial">Easy Power软件用户序列</div>

    <div id="div_areaList" style="background-color:#FFFFFF;color:#000000;position:absolute;z-index:10;top:134px;"></div>
</div>
<div id="div_brandvehicle" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller-1">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>