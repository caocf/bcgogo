<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: zyj
  Date: 12-4-12
  Time: 上午11:25
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>商品分类批量修改</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/setSale<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet"
          href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var droplist = APP_BCGOGO.Module.droplist;

            $("#edit_productKind").live("click focus",function (event) {
                askForAssistDroplist(event, null);
            }).live("keyup", function (event) {
                        askForAssistDroplist(event, "enter");
                    });

            function askForAssistDroplist(event, action) {
                var uuid = GLOBAL.Util.generateUUID();
                droplist.setUUID(uuid);
                //ajax获得最近15次使用的商品分类
                var jsonStr = null;
                if (action == null) {
                    jsonStr = APP_BCGOGO.Net.syncGet({url: "stockSearch.do?method=getProductKindsRecentlyUsed", data: {uuid: uuid}, dataType: "json"});
                } else if (action == "enter") {
                    jsonStr = APP_BCGOGO.Net.syncGet({url: "stockSearch.do?method=getProductKindsWithFuzzyQuery", data: {uuid: uuid, keyword: $.trim(event.target.value)}, dataType: "json"});
                }
                var inputId = event.target, result;

                result = {
                    uuid: uuid,
                    data: (jsonStr && jsonStr.data) ? jsonStr.data : ""
                };
                if (jsonStr && jsonStr.uuid && uuid == jsonStr.uuid) {
                    droplist.show({
                        "selector": $(event.currentTarget),
                        "isEditable": true,
                        "data": result,
                        "onSelect": function (event, index, data) {
                            inputId.value = data.label;
                            droplist.hide();
                        },
                        "onEdit": function (event, index, data) {
                            //记下修改前的分类名称
                            $("#oldKindName").val(data.label);
                        },
                        "onSave": function (event, index, data) {
                            var newKindName = $.trim(data.label);
                            if ($.trim(newKindName) == "") {
                                droplist.hide();
                                nsDialog.jAlert("空字符串不能保存！");
                            } else if (newKindName != $("#oldKindName").val()) {
                                var r = APP_BCGOGO.Net.syncGet({url: "stockSearch.do?method=saveOrUpdateProductKind",
                                    data: {oldKindName: $("#oldKindName").val(), newKindName: newKindName}, dataType: "json"});
                                if (r == null || r.flag == undefined) {
                                    droplist.hide();
                                    nsDialog.jAlert("保存失败！");
                                } else if (r.flag == "false") {
                                    nsDialog.jAlert("分类名“" + newKindName + "”已经存在！");
                                } else if (r.flag == "true") {
                                    nsDialog.jAlert("修改成功！", null, function () {
                                        window.parent.document.location.reload();
                                    });
                                }
                                //保存后清空，避免影响下次保存
                                $("#oldKindName").val("");
                            }
                        },
                        "onDelete": function (event, index, data) {
                            var r = APP_BCGOGO.Net.syncGet({url: "stockSearch.do?method=deleteProductKind",
                                data: {kindName: data.label}, dataType: "json"});
                            if (r == null || r.flag == undefined) {
                                nsDialog.jAlert("删除失败！");
                            } else if (r.flag == "true") {
                                nsDialog.jAlert("删除成功！", null, function () {
                                    window.parent.document.location.reload();
                                });
                            }
                        }
                    });
                }
            }

            document.getElementById("div_close").onclick = function () {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_Kind").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_Kind").src = "";
                if (jQuery("#btnSaleja", parent.document)) {
                    jQuery("#btnSaleja", parent.document).focus().select();
                }
            };

            document.getElementById("submit").onclick = function () {
                window.parent.setProducKind($("#edit_productKind").val());
                document.getElementById("div_close").click();
            };

            document.getElementById("cancel").onclick = function () {
                document.getElementById("div_close").click();
            };
        });
    </script>
</head>
<body>
<input type="hidden" id="oldKindName"/>

<div class="tabSale" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">设定商品分类</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <label>商品分类</label>
        <input id="edit_productKind" maxlength="20"/>

        <div class="addInput">
            <input id="submit" type="button" value="确定" class="cancel" onfocus="this.blur();"/>
            <input id="cancel" type="button" value="取消" class="cancel" onfocus="this.blur();"/>
        </div>
    </div>
</div>
</body>
</html>