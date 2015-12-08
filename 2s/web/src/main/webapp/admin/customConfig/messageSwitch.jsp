<%@ page import="com.bcgogo.enums.MessageSwitchStatus" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>功能配置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/staffManage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/remindSet<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.SYSTEM_SETTINGS.FUNCTIONAL_CONFIG");
        jQuery(document).ready(function () {
            jQuery(".messageSwitch").bind("click", function () {
                var obj = this;
                var scene = jQuery(this).attr("scene");
                var status = jQuery(this).attr("status");

                //如果按钮是蓝色的，继续点击则返回
                if (("ON" == status && -1 != jQuery(this).attr("class").indexOf("manualHover")) ||
                        ("OFF" == status && -1 != jQuery(this).attr("class").indexOf("closedHover"))) {
                    return;
                }

                var url = "admin.do?method=changeMessageSwitch"

                jQuery.ajax({
                    type:"POST",
                    url:url,
                    data:{
                        scene:scene,
                        status:status
                    },
                    cache:false,
                    dataType:"json",
                    success:function (jsonObject) {
                        if ("success" == jsonObject.resu) {
                            if ("ON" == status) {
                                jQuery(obj).attr("class", "manualHover messageSwitch");
                                jQuery(obj).next().attr("class", "closed messageSwitch");
                            }
                            else {
                                jQuery(obj).prev().attr("class", "manual messageSwitch");
                                jQuery(obj).attr("class", "closedHover messageSwitch");
                            }
                        }
                        else {
                            "更改失败！";
                        }
                    }
                });
            });

            jQuery(".userSwitch").bind("click", function () {
                var obj = this;
                var scene = jQuery(this).attr("scene");
                var status = jQuery(this).attr("status");
                //如果按钮是蓝色的，继续点击则返回
                if (("ON" == status && -1 != jQuery(this).attr("class").indexOf("manualHover")) ||
                        ("OFF" == status && -1 != jQuery(this).attr("class").indexOf("closedHover"))) {
                    return;
                }
                var url = "admin.do?method=changeUserSwitch";
                jQuery.ajax({
                    type:"POST",
                    url:url,
                    data:{
                        scene:scene,
                        status:status
                    },
                    cache:false,
                    dataType:"json",
                    success:function (result) {
                        if (result.success) {
                            if ("ON" == status) {
                                jQuery(obj).attr("class", "manualHover userSwitch");
                                jQuery(obj).next().attr("class", "closed userSwitch");
                                if($("a[scene='SCANNING_BARCODE']").eq(1).hasClass("closed") && $("a[scene='SCANNING_CARD']").eq(1).hasClass("closed")) {
                                    App.Module.scanningPanel.updateStatus();
                                }
                            }
                            else {
                                jQuery(obj).prev().attr("class", "manual userSwitch");
                                jQuery(obj).attr("class", "closedHover userSwitch");
                                App.Module.scanningPanel.updateStatus();
                            }
                        } else {
                           nsDialog.jAlert(result.msg);
                        }
                    }
                });
            });

        });

    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_main clear">
    <div class="mainTitles">
        <jsp:include page="customConfigNav.jsp">
            <jsp:param name="currPage" value="messageSwitch"/>
        </jsp:include>
    </div>

    <div class="titBody">
        <h3 class="h3_title"></h3>
        <div class="height"></div>
        <div class="accountMain">
            <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.SMS_CONFIG">
                <h3 class="h3_title">短信提醒配置</h3>
                <div class="height"></div>
                <table class="tab_remind" cellpadding="0" cellspacing="0">
                    <col width="200"/>
                    <col width="200"/>
                    <col/>
                    <tr>
                        <td>
                            <div class="remindTitle">
                                <div class="remindLeft"></div>
                                <div class="remindCenter">提醒名称</div>
                                <div class="remindRight"></div>
                            </div>
                        </td>
                        <td>
                            <div class="remindTitle">
                                <div class="remindLeft"></div>
                                <div class="remindCenter">提醒方式设置</div>
                                <div class="remindRight"></div>
                            </div>
                        </td>
                    </tr>

                    <c:forEach items="${messageTemplateDTOs}" var="messageTemplateDTO">
                        <tr>
                            <td>${messageTemplateDTO.name}</td>
                            <td>
                                <div class="remindBtn">
                                    <c:choose>
                                        <c:when test="${messageTemplateDTO.status == 'ON'}">
                                            <a class="manualHover messageSwitch" scene="${messageTemplateDTO.scene}"
                                               status="ON">打开</a>
                                            <a class="closed messageSwitch" scene="${messageTemplateDTO.scene}"
                                               status="OFF">关闭</a>
                                        </c:when>

                                        <c:otherwise>
                                            <a class="manual messageSwitch" scene="${messageTemplateDTO.scene}"
                                               status="ON">打开</a>
                                            <a class="closedHover messageSwitch" scene="${messageTemplateDTO.scene}"
                                               status="OFF">关闭</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.OTHER_CONFIG">
                <div class="height"></div>
                <h3 class="h3_title">其他功能配置</h3>
                <div class="height"></div>
                <table class="tab_remind" cellpadding="0" cellspacing="0">
                    <col width="200"/>
                    <col width="200"/>
                    <col/>
                    <tr>
                        <td>
                            <div class="remindTitle">
                                <div class="remindLeft"></div>
                                <div class="remindCenter">配置名称</div>
                                <div class="remindRight"></div>
                            </div>
                        </td>
                        <td>
                            <div class="remindTitle">
                                <div class="remindLeft"></div>
                                <div class="remindCenter">配置开关</div>
                                <div class="remindRight"></div>
                            </div>
                        </td>
                    </tr>
                    <c:forEach items="${userSwitchDTOList}" var="userSwitchDTO">
                        <tr>
                            <td>${userSwitchDTO.name}</td>
                            <td>
                                <div class="remindBtn">
                                    <c:choose>
                                        <c:when test="${userSwitchDTO.status == 'ON'}">
                                            <a class="manualHover userSwitch" scene="${userSwitchDTO.scene}"
                                               status="ON">打开</a>
                                            <a class="closed userSwitch" scene="${userSwitchDTO.scene}"
                                               status="OFF">关闭</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="manual userSwitch" scene="${userSwitchDTO.scene}"
                                               status="ON">打开</a>
                                            <a class="closedHover userSwitch" scene="${userSwitchDTO.scene}"
                                               status="OFF">关闭</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </bcgogo:hasPermission>
        </div>
    </div>
</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>