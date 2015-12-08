/**
 *  车辆施工和洗车美容中车辆信息校验
 **/

$(function(){
    $('.chassisNumber').live('input',function(){
        var node  =$(this);
        var val = node.val();
        val && val!='' && node.val(val.toUpperCase());
    });

    //车辆颜色实时校验
    var vehicleColorValidator = function(){
        var result = /([\u4e00-\u9fa5]|\w)*/.exec($(this).val());
        result && result.length && $(this).val(result[0]);
    }

    //车架号实时校验
    var vehicleChassisNoValidator = function(){
        var result = /[a-z0-9A-Z]*/.exec($(this).val());
        result && result.length && $(this).val(result[0]);
    }

    //车辆发动机号实时校验
    var vehicleEngineNoValidator = function () {
        var result = /[a-z0-9A-Z]*/.exec($(this).val());
        result && result.length && $(this).val(result[0]);
    }

    $('#vehicleColor').bind('input', vehicleColorValidator);
    $('#vehicleChassisNo').bind('input', vehicleChassisNoValidator);
    $('#vehicleEngineNo').bind('input', vehicleEngineNoValidator);

    $('input[id^=vin]').live('input',vehicleChassisNoValidator);
    $('input[id^=engineNo]').live('input',vehicleEngineNoValidator);
    $('input[id^=color]').live('input',vehicleColorValidator);

    $('input[id$=chassisNumber]').live('input', vehicleChassisNoValidator);
    $('input[id$=vehicleEngineNo]').live('input', vehicleEngineNoValidator);
    $('input[id$=vehicleColor]').live('input', vehicleColorValidator);
})