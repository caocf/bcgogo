;
(function () {
    function activeTextInput(infoValue, node, color) {
        if (node.value === infoValue) {
            $(node).val("");
            }
        $(node).css("color", color || "#000000");
            }

    function inactiveTextInput(infoValue, node, color) {
        $(node)
            .css("color", color || "#999999")
            .val("车牌号");
        }

    function setMouseOverAttr(a_name_attrArr, div_name_attr) {
        for (var i = 0, len = a_name_attrArr.length; i < len; i++)
            if ($("#a_name" + i)[0]) $("#a_name" + i).attr("className", a_name_attrArr[i]);
        if ($("#div_name")[0]) $("#div_name").css("display", div_name_attr);
            }

    $(document).ready(function () {
        if ($("#vehicleNumber")[0]) {
            //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
            $("#vehicleNumber")
                .focus(function (event) {
                    activeTextInput("车牌号", this);
                })
                .blur(function (event) {
                    inactiveTextInput("车牌号", this);
                });
            }

        if ($("#input_search_Name")[0]) {
            $("#input_search_Name")
                .focus(function (event) {
                    activeTextInput("单位/联系人/手机号", this);
                })
                .blur(function (event) {
                    inactiveTextInput("单位/联系人/手机号", this);
                });

            $("#input_search_pName")
                .focus(function (event) {
                    activeTextInput("用品配件品名（简写缩写）", this);
                })
                .blur(function (event) {
                    inactiveTextInput("用品配件品名（简写缩写）", this);
                });
            }

        if ($("#multipleMoney")[0]) {
            $("#multipleMoney")
                .focus(function (event) {
                    activeTextInput("50的倍数", this, "#A16F3E");
                })
                .blur(function (event) {
                    inactiveTextInput("50的倍数", this, "#A16F3E");
                });
            }

        $("#txt_shopName")
            .focus(function (event) {
                activeTextInput("店铺名", this);
            })
            .blur(function (event) {
                inactiveTextInput("店铺名", this);
            });

        $("#txt_shopOwner")
            .focus(function (event) {
                activeTextInput("店主", this);
            })
            .blur(function (event) {
                inactiveTextInput("店主", this);
            });

        $("#txt_address")
            .focus(function (event) {
                activeTextInput("地址", this);
            })
            .blur(function (event) {
                inactiveTextInput("地址", this);
            });

        $("#txt_phone")
            .focus(function (event) {
                activeTextInput("手机/电话");
            })
            .blur(function (event) {
                inactiveTextInput("手机/电话");
            });

        if ($("#a_name1")[0]) {
            $("#a_name1").mouseover(function (event) {
                setMouseOverAttr(["hover", "", "", ""], "none");
            });
		}

        if ($("#a_name2")[0]) {
            $("#a_name2").mouseover(function (event) {
                setMouseOverAttr(["", "hover", "", ""], "block");
            });
		}

        if ($("#a_name3")[0]) {
            $("#a_name3").mouseover(function (event) {
                setMouseOverAttr(["", "", "hover", ""], "block");
            });
		}

        if ($("#a_name4")[0]) {
            $("#a_name4").mouseover(function (event) {
                setMouseOverAttr(["", "", "", "hover"], "block");
            });
		}

    });

})();