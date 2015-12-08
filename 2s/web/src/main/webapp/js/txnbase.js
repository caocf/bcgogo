;(function () {

    $().ready(function () {

        //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    //    $("#vehicleNumber").focus( function () {
     //       if ($("#vehicleNumber").val() == "车牌号") {
    //            $("#vehicleNumber").val("");
     //           $("#vehicleNumber").css("color", "#000000");
     //       }
     //       else {
     //           $("#vehicleNumber").css("color", "#000000");
    //        }
    //    });
    //    $("#vehicleNumber").blur( function () {
    //        if (!$("#vehicleNumber").val() || $("#vehicleNumber").val() == "车牌号") {
     //           $("#vehicleNumber").css("color", "#999999");
    //            $("#vehicleNumber").val("车牌号");
    //        }
    //    });

    //    $("#input_search_Name").focus( function () {
    //        if ($("#input_search_Name").val() == "车主姓名、手机号") {
    //            $("#input_search_Name").val("");
    //            $("#input_search_Name").css("color", "#000000");
     //       }
     //       else {
    //            $("#input_search_Name").css("color", "#000000");
    //        }
     //   });
    //    $("#input_search_Name").blur( function () {
    //        if (!$("#input_search_Name").val() || $("#input_search_Name").val() == "车主姓名、手机号") {
     //           $("#input_search_Name").css("color", "#999999");
     //           $("#input_search_Name").val("车主姓名、手机号");
     //       }
    //    });

        $("#input_search_pName").focus( function () {
            if ($("#input_search_pName").val() == "用品配件品名（简写缩写）") {
                $("#input_search_pName").val("");
                $("#input_search_pName").css("color", "#000000");
            }
            else {
                $("#input_search_pName").css("color", "#000000");
            }
        });
        $("#input_search_pName").blur( function () {
            if (!$("#input_search_pName").val() || $("#input_search_pName").val() == "用品配件品名（简写缩写）") {
                $("#input_search_pName").css("color", "#999999");
                $("#input_search_pName").val("用品配件品名（简写缩写）")
            }
        });

        if ($("#a_name1")[0]) {
            $("#a_name1").bind("mouseenter", function () {
                if ($("#a_name1")[0]) $("#a_name1").attr("class", "hover");
                if ($("#a_name2")[0]) $("#a_name2").attr("class", "");
                if ($("#a_name3")[0]) $("#a_name3").attr("class", "");
                if ($("#a_name4")[0]) $("#a_name4").attr("class", "");
                if ($("#div_name")[0]) $("#div_name").hide();
            });
        }

        if ($("#a_name2")[0]) {
            $("#a_name2").bind("mouseenter", function () {
                if ($("#a_name1")[0]) $("#a_name1").attr("class", "");
                if ($("#a_name2")[0]) $("#a_name2").attr("class", "hover");
                if ($("#a_name3")[0]) $("#a_name3").attr("class", "");
                if ($("#a_name4")[0]) $("#a_name4").attr("class", "");
                if ($("#div_name")[0]) $("#div_name").show();
            });
        }

        if ($("#a_name3")[0]) {
            $("#a_name3").bind("mouseenter", function () {
                if ($("#a_name1")[0]) $("#a_name1").attr("class", "");
                if ($("#a_name2")[0]) $("#a_name2").attr("class", "");
                if ($("#a_name3")[0]) $("#a_name3").attr("class", "hover");
                if ($("#a_name4")[0]) $("#a_name4").attr("class", "");
                if ($("#div_name")[0]) $("#div_name").show();
            });
        }

        if ($("#a_name4")[0]) {
            $("#a_name4").bind( "mouseenter", function () {
                if ($("#a_name1")[0]) $("#a_name1").attr("class", "");
                if ($("#a_name2")[0]) $("#a_name2").attr("class", "");
                if ($("#a_name3")[0]) $("#a_name3").attr("class", "");
                if ($("#a_name4")[0]) $("#a_name4").attr("class", "hover");
                if ($("#div_name")[0]) $("#div_name").show();
            });
        }
    });

})();