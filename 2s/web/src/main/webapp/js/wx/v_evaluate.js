/**
 * @description 车价评估
 * @author ndong
 * @date  2014-11-14
 */
;
$(function(){






    $("#cityNo").bind("change", function() {
        scroll(0,document.body.scrollWidth);
    });

    $("#provinceNo").bind("change", function() {
        $("#cityNo option").not(".default").remove();
        $(this).css({"color": "#000000"});
        $.ajax({
            type: "POST",
            url: "/web/evaluate/getAreaDTOByNo",
            data: {"parentNo": $("#provinceNo").val()},
            dataType: "json",
            async:false,
            success: function(result){
                if (!result || result.length == 0) return;
                for (var i = 0, l = result.length; i < l; i++) {
                    var option = $("<option>")[0];
                    option.value = result[i].no;
                    option.innerHTML = result[i].name;
                    option.style.color = "#000000";
                    $("#cityNo")[0].appendChild(option);
                }
            }
        });
    });

    if($("#page_type").val()=="EDIT"&&$("#province").val()){
        $("#provinceNo").val($("#province").val());
        $("#provinceNo").change();
        $("#cityNo").val($("#city").val());
    }

    $("#submitBtn").click(function(){
        var mask=APP_BCGOGO.Module.waitMask;
        mask.login({dev:"wx"});
        var brand=$("#s_brand").val();
        if(G.isEmpty(brand)){
            $("#errorMsg").text("请选择评估车辆的品牌。");
            mask.open();
            $(this).removeAttr("lock");
            return;
        }
        var series=$("#s_series").val();
        if(G.isEmpty(series)){
            $("#errorMsg").text("请选择评估车辆的车系。");
            mask.open();
            return;
        }
        var model=$("#s_model").val();
        if(G.isEmpty(model)){
            $("#errorMsg").text("请选择评估车辆的车型。");
            mask.open();
            return;
        }
        var cityNo=$("#cityNo").val();
        if(G.isEmpty(cityNo)){
            $("#errorMsg").text("请选择评估车辆所在地区。");
            mask.open();
            return;
        }
        var regDate=$("#regDate").val()
        if(G.isEmpty(regDate)){
            $("#errorMsg").text("请选择评估车辆上牌时间。");
            mask.open();
            return;
        }
        var mile=$("#mile").val();
        if(G.isEmpty(mile)||mile<=0){
            $("#errorMsg").text("请填写评估车辆行驶里程。");
            mask.open();
            return;
        }
        $.ajax({
            type: "POST",
            url:"/web/evaluate",
            data: {
                vehicleNo:$("#vehicleNo").val(),
                modelId:model,
                mile:mile,
                regDate:regDate,
                zone:cityNo,
                openId:$("#openId").val()
            },
            dataType: "json",
            success: function(result){
                mask.open();
                if(!result.success){
                    $("#errorMsg").text(result.msg);
                    return;
                }
                $("#errorMsg").text("");
                $("#evalPrice").text(result.evalPrice+"万");
                $("#lowPrice").text(result.lowPrice+"万");
                $("#goodPrice").text(result.goodPrice+"万");
                $("#highPrice").text(result.highPrice+"万");
                $("#price").text(result.price+"万");
                $(".eval-result-d").show();
                scroll(0,document.body.scrollHeight);
            },
            error:function(){
                mask.open();
            }
        });

    });


    $("#s_brand").change(function(){
        var $option=$(this).find("option:selected");
        var brand=$(this).find("option:selected").text();
        if(G.isEmpty($option.val())){
            brand="品牌";
        }
        $("#brand").text(brand);
        $("#s_series").val("");
        $("#series").text("车系");
        $("#s_model").val("");
        $("#model").text("车型");
        $.ajax({
            type: "POST",
            url: "/web/evaluate/getVehicleSeriesDTOs",
            data: {"brandId": $("#s_brand").val()},
            dataType: "json",
            async:false,
            success: function(seriesDTOs){
                if (G.isEmpty(seriesDTOs)) return;
                $("#s_series option").remove()
                var option = $("<option>")[0];
                option.value ="";
                option.innerHTML ="选择车系";
                $("#s_series")[0].appendChild(option);
                for (var i = 0;i < seriesDTOs.length;i++) {
                    var seriesDTO=seriesDTOs[i];
                    var option = $("<option>")[0];
                    option.value = seriesDTO.id;
                    option.innerHTML = seriesDTO.name;
                    option.style.color = "#000000";
                    $("#s_series")[0].appendChild(option);
                }
            }
        });

    });

    $("#s_series").change(function(){
        var $option=$(this).find("option:selected");
        var series=$(this).find("option:selected").text();
        if(G.isEmpty($option.val())){
            series="车系";
        }
        $("#series").text(series);
        $("#s_model").val("");
        $("#model").text("车型");
        $.ajax({
            type: "POST",
            url: "/web/evaluate/getVehicleModelDTOs",
            data: {"seriesId": $("#s_series").val()},
            dataType: "json",
            async:false,
            success: function(modelDTOs){
                if (G.isEmpty(modelDTOs)) return;
                $("#s_model option").remove();
                var option = $("<option>")[0];
                option.value ="";
                option.innerHTML ="选择车型";
                $("#s_model")[0].appendChild(option);
                for (var i = 0;i < modelDTOs.length;i++) {
                    var modelDTO=modelDTOs[i];
                    var option = $("<option>")[0];
                    option.value = modelDTO.id;
                    option.innerHTML = modelDTO.name;
                    option.style.color = "#000000";
                    $("#s_model")[0].appendChild(option);
                }
            }
        });
    });


    $("#v-brand-select").click(function(){
        $("#v-brand-select").hide();
        $("#d-brand-select").show();
    });

//    initLastEvaluateRecord();
});


function initLastEvaluateRecord(){
    var vehicleNo= $("#vehicleNo").val();
    if(!G.isEmpty(vehicleNo)){
        $.ajax({
            type: "POST",
            url: "/web/evaluate/getLastEvaluateRecord",
            data: {"vehicleNo":vehicleNo},
            dataType: "json",
            async:false,
            success: function(recordDTO){
                if(G.isEmpty(recordDTO)) return;
                var vehicleNo=G.normalize(recordDTO.vehicleNo);
                var mile=G.normalize(recordDTO.mile);
                $("#vehicleNo").val(vehicleNo);
                $("#mile").val(mile);
            }
        });
    }

}