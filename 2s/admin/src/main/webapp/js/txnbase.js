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
        //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
        $("#vehicleNumber")
            .focus(function (event) {
                activeTextInput("车牌号", this);
            })
            .blur(function (event) {
                inactiveTextInput("车牌号", this);
            });

        $("input_search_Name")
            .focus(function (event) {
                activeTextInput("车主姓名、手机号", this);
            })
            .blur(function (event) {
                inactiveTextInput("车主姓名、手机号", this);
            });

        $("#input_search_pName")
            .focus(function (event) {
                activeTextInput("用品配件品名（简写缩写）", this);
            })
            .blur(function (event) {
                inactiveTextInput("用品配件品名（简写缩写）", this);
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