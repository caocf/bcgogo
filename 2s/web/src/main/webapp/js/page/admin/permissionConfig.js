$(function () {
    var userGroupId = $("#userGroupId").val();
    if (!userGroupId) {
        $("#pageTitle").html("新增职位");
    } else {
        $("#pageTitle").html("修改职位");
        $("#userGroupNoShow").html($("#userGroupNo").val());
    }
    //init memo
    if (!$("#userGroupMemo").val()) {
        $("#userGroupMemo").val($("#userGroupMemo").attr("initialValue"))
    }
    if ($("#copyPermission").val()) {
        userGroupId = $("#copyUserGroupId").val();
    }

    APP_BCGOGO.Net.asyncAjax({
        type:"POST",
        url:"permissionManager.do?method=getTreeModuleRolesForUserConfig",
        cache:false,
        data:{
            userGroupId:userGroupId
        },
        dataType:"json",
        success:function (permission) {
            buildModuleRoleTrees(permission);
            //init page document
            if ($("#defaultSysUserGroup").val() == "true") {
                $("input[type=checkbox]").attr("disabled", "disabled");
                $("#userGroupMemo").attr("disabled", "disabled");
                $("input[type=text]").attr("disabled", "disabled");
            }
            if (!$("#copyPermission").val()) {
                userGroupId = "";
            }
        }
    });

    function buildModuleRoleTrees(permission) {
        var $container, $title, child;
        for (var i = 0, max = permission.children.length; i < max; i++) {
            child = permission.children[i];
            var $area = $('#chks' + (i % 12 + 1));
            $title = $('<h3><label class="lblChk"><input type="checkbox" class="moduleCheckbox"/><span  style="font-size:14px; font-weight:bold; color:#000000">' + child.value + '</span></label></h3>');
            buildCheckbox($($title[0]).find('input'), child);
            $area.append($title);
            buildTree(permission.children[i], $area);
            //intTreeExpand($area);
        }
    }

    function buildTree(permission, $container) {
        //role module 分离
        var roles = [], modules = [];
        if ($.isEmptyObject(permission))return;
        for (var i = 0, roleIndex = 0, moduleIndex = 0, max = permission.children.length; i < max; i++) {
            if (permission.children[i].type == "ROLE") {
                roles[roleIndex++] = permission.children[i];
            } else {
                modules[moduleIndex++] = permission.children[i];
            }
        }
        if (roles.length != 0) {
            buildRoles(roles, $container);
        }
        if (modules.length != 0) {
            buildModules(modules, $container);
        }
        $(".box>span label").removeClass('lblChk').addClass('boxTit');
        $(".box h3 label input[type=checkbox]").click(function(){
          var $box = $(this).parent().parent().parent();
          if($(this).attr("checked")) {
            $box.find('input[type=checkbox]').not(":first").attr("checked",true);
          } else {
            $box.find('input[type=checkbox]').not(":first").attr("checked",false);
          }

        });
    }

    function buildCheckbox($dom, data) {
        $dom.attr("id", data.idStr).attr("permissionType", data.type).attr("hasThisNode", data.hasThisNode ? data.hasThisNode : false).attr("checked", data.hasThisNode ? data.hasThisNode : false);
    }

    function buildRoles(roles, $container) {
        var $roleContainer = $('<span></span>'), $li;
        for (var i = 0, max = roles.length; i < max; i++) {
            $li = $('<label class="lblChk"><input type="checkbox"/>' + roles[i].value + '</label>');
            buildCheckbox($($li.find("input[type=checkbox]")), roles[i]);

            $roleContainer.append($li);

        }

        $container.append($roleContainer);
    }

    function buildModules(modules, $container) {
        var $moduleContainer, $title;
        for (var i = 0, max = modules.length; i < max; i++) {
            $moduleContainer = $('<div class="boxTit"></div>');
            $title = $('<label class="lblChk" style="font-size:14px;"><input type="checkbox" class="moduleCheckbox"/><span>' + modules[i].value + '</span></label>');
            buildCheckbox($($($title[0]).children()[0]), modules[i]);
            $moduleContainer.append($title);
            buildTree(modules[i], $moduleContainer);
            $container.append($moduleContainer);
        }
    }

//    $(".downs").live("click", function (e) {
//        collapse(e.target);
//    });
//    $(".ups").live("click", function (e) {
//        expand(e.target);
//    });

    //选中自选 module
    $(".moduleCheckbox").live("click", function (e) {
        var $parent = $(e.target).parent().parent();
        //修改children
        if ($(e.target).is(":checked")) {
            $parent.find("input[type=checkbox]").not('.moduleCheckbox').attr("checked", 'true');
        } else {
            $parent.find("input[type=checkbox]").not('.moduleCheckbox').removeAttr("checked")
        }
        var isAllModuleChecked = true;
        if($(this).parent().parent().parent().find("input[class=moduleCheckbox]").not(":first").length > 0) {
            for (var i = 0, children = $(this).parent().parent().parent().find("input[class=moduleCheckbox]").not(":first"), max = children.length; i < max; i++) {
                if (!$(children[i]).attr("checked")) {
                    isAllModuleChecked = false;
                    break;
                }
            }
            $(this).parent().parent().parent().find('h3 label input[type=checkbox]').attr("checked",isAllModuleChecked);
        }

    });

    //role
    $("input[type=checkbox]").live("click", function (e) {
        if(this.id == 'copy') {
          return;
        }
        var $parent;
        //修改parent
        if ($(e.target).attr("permissionType") == "MODULE") {
            $parent = $(e.target).parent().parent();
        } else {
            $parent = $(e.target).parent().parent().parent();
            if($parent.hasClass("box")) {
                $parent = $(e.target).parent().parent();
            }
        }
        recursionCheckedUp($parent);
    });

    //递归 改变父类的checkbox
    function recursionCheckedUp($dom) {
        if ($dom.hasClass("box"))return;
        //递归 改变父类的checkbox
        var isAllChildrenChecked = true;
        var isAllModuleChecked = true;
        for (var i = 0, children = $dom.find("input[type=checkbox]").not(".moduleCheckbox"), max = children.length; i < max; i++) {
            if (!$(children[i]).attr("checked")) {
                isAllChildrenChecked = false;
                break;
            }
        }
        var $title = $dom.find('.moduleCheckbox');

        $title.attr("checked", isAllChildrenChecked);
        for (var i = 0, children = $dom.parent().find("input[class=moduleCheckbox]").not(":first"), max = children.length; i < max; i++) {
            if (!$(children[i]).attr("checked")) {
                isAllModuleChecked = false;
                break;
            }
        }
        if($dom.get(0).tagName == 'SPAN' && !$dom.find('input[type=checkbox]').attr("checked")) {
            isAllModuleChecked = false;
        }
        if($dom.parent().hasClass("box")) {
         $dom.parent().find('h3 label input[type=checkbox]').attr("checked",isAllModuleChecked);
         recursionCheckedUp($dom.parent());
        } else {
          recursionCheckedUp($dom.parent().parent());
        }


    }


    function expand(dom) {
        $(dom).parent().children().show();
//        $(dom).removeClass("ups").addClass("downs");
        $(dom).css("color","#000000");
    }

    function collapse(dom) {
        $(dom).parent().children().not($(dom)).not($(dom).prev()).hide();
//        $(dom).removeClass("downs").addClass("ups");
        $(dom).css("color","#000000");
    }

    //初始化展开项
    function intTreeExpand($dom) {
        if ($.isEmptyObject($dom))return;
        var children = $dom.children("div");
        for (var i = 0, max = children.length; i < max; i++) {
            var child = children[i];
            collapse($(child).children("a"));
            if ($(child).children())intTreeExpand($(child));
        }
    }

    $("#cancel").click(function () {
        openOrAssign('userGroupsManage.do?method=showUserGroupsManage');
    });

    $("#reset").click(function () {
        clearPermissionConfig();
    });

    function clearPermissionConfig() {
        $(".mainBody").find("input[type=checkbox]").removeAttr("checked");
        $("#userGroupName").val("");
        $("#userGroupMemo").val($("#userGroupMemo").attr("initialValue")).css({"color":"#666666"});
    }

    var isUserGroupNameDuplicate = false;
    $("#userGroupName")
        .blur(function (e) {
            var url, userGroupName = $(e.target).val().trim(), data = {userGroupName:userGroupName};
            if (!userGroupName) return;
            APP_BCGOGO.Net.asyncAjax({
                type:"POST",
                url:"permissionManager.do?method=checkUserGroupName",
                data:data,
                cache:false,
                dataType:"json",
                success:function (result) {
                    if (result && result.isDuplicated) {
                        isUserGroupNameDuplicate = true;
                        nsDialog.jAlert("职位名称已存在！", "", function () {
                            $("#userGroupName").val(userGroupName + "-新");
                            isUserGroupNameDuplicate = false;
                            $("#userGroupName").focus();
                            $("#userGroupName").select();
                        });
                    }
                }
            });
        })
        .keyup(function () {
            isUserGroupNameDuplicate = false;
        });

    $("#userGroupMemo")
        .blur(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == '') {
                    event.target.value = initialValue;
                    $(event.target).css({"color":"#666666"});
                } else {
                    $(event.target).css({"color":"#000000"});
                }
            }
            $(this).removeClass("J-active");
        })
        .focus(function (event) {
            var initialValue = $(event.target).attr("initialValue");
            if (initialValue != null && initialValue != "") {
                if (event.target.value == initialValue) {
                    event.target.value = "";
                }
                $(event.target).css({"color":"#000000"});
            }
        });

    $("#saveUserGroup").click(function () {
        if (!$("#userGroupName").val().trim()) {
            nsDialog.jAlert("职位名称不能为空！");
            $("#userGroupName").val('');
            return;
        }
        var userGroupId = $("#userGroupId").val()
        var checkboxes = $(".mainBody").find("input[type=checkbox]"), isAllRolesUnchecked = true, roleIds = "", checks = "";
        for (var i = 0, max = checkboxes.length; i < max; i++) {
            if ($(checkboxes[i]).attr("permissionType") != 'ROLE')continue;
            if ($(checkboxes[i]).is(":checked")) {
                isAllRolesUnchecked = false;
                if (!userGroupId) {
                    roleIds += checkboxes[i].id + ",";
                    checks += $(checkboxes[i]).attr("checked") + ",";
                }
            }
            if (userGroupId) {
                var node = $(checkboxes[i]).attr("hasThisNode"), originalValue = (node ? node : false);
                if (originalValue != $(checkboxes[i]).attr("checked")) {
                    roleIds += checkboxes[i].id + ",";
                    checks += $(checkboxes[i]).attr("checked") + ",";
                }
            }
        }
        if (isAllRolesUnchecked) {
            nsDialog.jAlert("权限配置不能为空！");
            return;
        }
        if (isUserGroupNameDuplicate) {
            nsDialog.jAlert("职位名称已存在！");
            $("#userGroupName").focus();
            return;
        }
        var userGroupMemo = $("#userGroupMemo").val();
        APP_BCGOGO.Net.asyncAjax({
            url:"permissionManager.do?method=saveOrUpdatePermissionConfig",
            data:{
                roleIds:roleIds,
                checks:checks,
                id:$("#userGroupId").val(),
                name:$("#" +
                    "userGroupName").val().trim(),
                memo:userGroupMemo == $("#userGroupMemo").attr("initialValue") ? "" : userGroupMemo
            },
            cache:false,
            dataType:"json",
            success:function (success) {
                if (success) {
                    if (!$("#userGroupId").val()) {

                                clearPermissionConfig();
                                $("#iframe_PopupBox").css("display","block");
	                              Mask.Login();
	                              $("#iframe_PopupBox").attr("src","admin/permissionManager/helpfulHints.jsp");
                    } else {
                        nsDialog.jAlert("修改职位成功！", "", function () {
                            openOrAssign('userGroupsManage.do?method=showUserGroupsManage');
                        });
                    }
                }
            }
        });
    });

    $("#copy").click(function(event){
       if($(event.target).attr("checked")) {
         $("#userGroup5").removeAttr("disabled");
       } else {
         $("#userGroup5").attr("disabled","true");
       }
    });
    $("#userGroup5").change(function(){
       $("#copyUserGroupId").val($("#userGroup5").find('option[value="' + $("#userGroup5").val() + '"]').attr("userGroupId"));
//       openOrAssign(('permissionManager.do?method=showPermissionConfig&userGroupId=' + $("#copyUserGroupId").val() + "&copyPermission=true"));
       APP_BCGOGO.Net.asyncAjax({
        type:"POST",
        url:"permissionManager.do?method=getTreeModuleRolesForUserConfig",
        cache:false,
        data:{
            userGroupId:$("#copyUserGroupId").val()
        },
        dataType:"json",
        success:function (permission) {
            $(".box").html('');
            buildModuleRoleTrees(permission);
            //init page document
            if ($("#defaultSysUserGroup").val() == "true") {
                $("input[type=checkbox]").attr("disabled", "disabled");
                $("#userGroupMemo").attr("disabled", "disabled");
                $("input[type=text]").attr("disabled", "disabled");
            }
            if (!$("#copyPermission").val()) {
                userGroupId = "";
            }
        }
    });
    });
    $(".quanHelp").click(function(){
       window.location.href = 'help.do?method=toHelper&title=staffConfigHelper';
    });
});