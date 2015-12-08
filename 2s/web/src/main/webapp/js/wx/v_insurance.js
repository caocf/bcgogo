;
$(function () {

    $(".item_chk").click(function () {
        if ($(this).hasClass("item_unselected")) {
            $(this).removeClass("item_unselected");
            $(this).addClass("item_selected");
        } else {
            $(this).addClass("item_unselected");
            $(this).removeClass("item_selected");
        }
        calcAll();
    });

    $("#txtMoney").keyup(function () {
        calcAll();
    });

    $(".insurance_opt").change(function () {
        calcAll();
    });



    $("#closeBtn").click(function () {
        $(".help-swing").animate({"right": -$(".d-close-btn").width()}, 200, "swing");
        $("#helpMask").hide();
    });

    $(".t-help").click(function () {
        $("#helpMask").show();
        $(".help-swing .txt").hide();
        $(".txt_"+$(this).attr("d-type")).show();
        $(".help-swing").animate({"right": 0}, 200, "swing");
    });
});

function calcAll() {
    calcCompulsory();
    calcTPL();
    calcCarDamage();
    calcCarTheft();
    calcBreakageOfGlass();
    calcCarDamageDW();
    calcSelfignite();
    calcLimitOfDriver();
    calcLimitOfPassenger();
    calcAbatement();
    calcCommonTotal();
}

function calcCommonTotal() {
    if ($("#txtMoney").val() == 0) {
        $("#i_tpl .insurance_price").text(0);
        return;
    }
    var insurance_price = 0;
    var total = Number($("#i_compulsory .insurance_price").text());
    if ($("#i_tpl .item_chk").hasClass("item_selected")) {
        total += Number($("#i_tpl .insurance_price").text());
    }
    if ($("#i_car_damage .item_chk").hasClass("item_selected")) {
        total += Number($("#i_car_damage .insurance_price").text());
    }
    if ($("#i_abatement .item_chk").hasClass("item_selected")) {
        total += Number($("#i_abatement .insurance_price").text());
    }
    if ($("#i_carTheft .item_chk").hasClass("item_selected")) {
        total += Number($("#i_carTheft .insurance_price").text());
    }
    if ($("#i_limitOfDriver .item_chk").hasClass("item_selected")) {
        total += Number($("#i_limitOfDriver .insurance_price").text());
    }
    if ($("#i_limitOfPassenger .item_chk").hasClass("item_selected")) {
        total += Number($("#i_limitOfPassenger .insurance_price").text());
    }
    if ($("#i_breakageOfGlass .item_chk").hasClass("item_selected")) {
        total += Number($("#i_breakageOfGlass .insurance_price").text());
    }
    if ($("#i_carDamageDW .item_chk").hasClass("item_selected")) {
        total += Number($("#i_carDamageDW .insurance_price").text());
    }
    if ($("#i_selfignite .item_chk").hasClass("item_selected")) {
        total += Number($("#i_selfignite .insurance_price").text());
    }
    $("#total").text(Math.round(total));
}

/**
 * 交强险
 */
function calcCompulsory() {
    if ($("#txtMoney").val() == 0) {
        $("#i_compulsory .insurance_price").text("0");
        return;
    }
    var insurance_price = $("#i_compulsory .insurance_opt").val();
    $("#i_compulsory .insurance_price").text(insurance_price);
}

/**
 * 第三者责任险
 */
function calcTPL() {
    if ($("#txtMoney").val() == 0 || $("#i_tpl .item_chk").hasClass("item_unselected")) {
        $("#i_tpl .insurance_price").text(0);
        return;
    }
    var compulsory_opt = $("#i_compulsory .insurance_opt option:selected").attr("name");
    var $selected = $("#i_tpl .insurance_opt option:selected");
    var insurance_price = compulsory_opt == "6sit" ? $selected.attr("aValue") : $selected.attr("bValue");
    $("#i_tpl .insurance_price").text(insurance_price);
}

/**
 * 车辆损失险
 */
function calcCarDamage() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_car_damage .item_chk").hasClass("item_unselected")) {
        $("#i_car_damage .insurance_price").text(0);
        return;
    }
    $("#i_car_damage .insurance_price").text(Math.round($("#txtMoney").val() * 0.012));
}


/**
 * 不计免赔特约险
 * (车辆损失险+第三者责任险)×20%
 */
function calcAbatement() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_abatement .item_chk").hasClass("item_unselected")) {
        $("#i_abatement .insurance_price").text(0);
        return;
    }
    //车辆损失险+第三者责任险
    var total = parseInt($("#i_tpl .insurance_price").text()) + parseInt($("#i_car_damage .insurance_price").text());
    $("#i_abatement .insurance_price").text(Math.round(total * 0.2));

}

/**
 * 全车盗抢险
 */
function calcCarTheft() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_carTheft .item_chk").hasClass("item_unselected")) {
        $("#i_carTheft .insurance_price").text(0);
        return;
    }
    $("#i_carTheft .insurance_price").text(Math.round($("#txtMoney").val() * 0.01));
}

/**
 * 玻璃单独破碎险
 */
function calcBreakageOfGlass() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_breakageOfGlass .item_chk").hasClass("item_unselected")) {
        $("#i_breakageOfGlass .insurance_price").text(0);
        return;
    }
    var insurance_price = 0;
    var opt = $("#i_breakageOfGlass .insurance_opt option:selected").val();
    insurance_price = Math.round(txtMoney * (opt == "import" ? 0.0025 : 0.0015));
    $("#i_breakageOfGlass .insurance_price").text(insurance_price);
}

/**
 * 车身划痕险
 */
function calcCarDamageDW() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_carDamageDW .item_chk").hasClass("item_unselected")) {
        $("#i_carDamageDW .insurance_price").text(0);
        return;
    }
    var insurance_price = 0;
    if (txtMoney < 3E5) {
        insurance_price = Number($("#i_carDamageDW .insurance_opt option:selected").attr("aValue"));
    } else if (txtMoney > 5E5) {
        insurance_price = Number($("#i_carDamageDW .insurance_opt option:selected").attr("bValue"));
    } else {
        insurance_price = Number($("#i_carDamageDW .insurance_opt option:selected").attr("cValue"));
    }
    $("#i_carDamageDW .insurance_price").text(insurance_price);
}

/**
 * 自燃损失险
 */
function calcSelfignite() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_selfignite .item_chk").hasClass("item_unselected")) {
        $("#i_selfignite .insurance_price").text(0);
        return;
    }
    var insurance_price = Math.round(txtMoney * 0.0015);
    $("#i_selfignite .insurance_price").text(insurance_price);
}

/**
 * 乘客责任险（所选金额*费率*（座位数-1）。如果没有座位数，则*4）
 */
function calcLimitOfPassenger() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_limitOfPassenger .item_chk").hasClass("item_unselected")) {
        $("#i_limitOfPassenger .insurance_price").text(0);
        return;
    }
    var insurance_price = 0;
    var compulsory_opt = $("#i_compulsory .insurance_opt option:selected").attr("name");
    var seatNum = compulsory_opt == "6sit" ? 4 : 7;
    var calCount = seatNum - 1;
    var opt = Number($("#i_limitOfPassenger .insurance_opt option:selected").val());
    if (compulsory_opt == "6sit") { //6座以下
        insurance_price = Math.round(opt * 0.0027 * calCount);
    } else {
        insurance_price = Math.round(opt * 0026 * calCount);
    }
    $("#i_limitOfPassenger .insurance_price").text(insurance_price);
}

/**
 * 司机责任险
 */
function calcLimitOfDriver() {
    var txtMoney = Number($("#txtMoney").val());
    if (txtMoney == 0 || $("#i_limitOfDriver .item_chk").hasClass("item_unselected")) {
        $("#i_limitOfDriver .insurance_price").text(0);
        return;
    }
    var compulsory_opt = $("#i_compulsory .insurance_opt option:selected").attr("name");
    var insurance_price = 0;
    var opt = Number($("#i_limitOfDriver .insurance_opt option:selected").val());
    if (compulsory_opt == "6sit") { //6座以下
        insurance_price = Math.round(opt * 0.0042);
    } else {
        insurance_price = Math.round(opt * 004);
    }
    $("#i_limitOfDriver .insurance_price").text(insurance_price);
}



