var year = 2012;
var month = 1;
var nextPageNo = 1;
var isTheLastPage = false;
$(document).ready(function() {
    $.ajax({
        type:"POST",
        url:"agents.do?method=getSalesMan",
        data:{startPageNo:nextPageNo,maxRows:2,agentIdStr:$("#agentId").val(),month:month,year:year},
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            initTr(jsonStr);
            if (isTheLastPage == true && nextPageNo == 1) {
                $("#pageNo_id>div:eq(1)").css('display', 'none');
            }
            if (isTheLastPage == true) {
                $("#pageNo_id>div:last").css('display', 'none');

            }
            if (nextPageNo == 1) {
                $("#pageNo_id>div:first").css('display', 'none');
            }
        }


    });
    $("#monthRight").click(function() {
        month = month + 1;
        if (month > 12) {
            month = month - 12;
            year = year + 1;
        }
        $("#year").html(year + "年");
        $("#year1").val(year);
        $("#year2").html(year + "年");
        $("#month").html(month + "月");
        $.ajax({
            type:"POST",
            url:"agents.do?method=getSalesMan",
            data:{startPageNo:nextPageNo,maxRows:2,agentIdStr:$("#agentId").val(),month:month,year:year},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initTr(jsonStr);
            }
        });
    });
    $("#monthLeft").click(function() {
        month = month - 1;
        if (month < 1) {
            month = month + 12;
            year = year - 1;
        }
        $("#year").html(year + "年");
        $("#year1").val(year);
        $("#year2").html(year + "年");
        $("#month").html(month + "月");
        $.ajax({
            type:"POST",
            url:"agents.do?method=getSalesMan",
            data:{startPageNo:nextPageNo,maxRows:2,agentIdStr:$("#agentId").val(),month:month,year:year},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initTr(jsonStr);
            }
        });
    });

    $("#yearLeft").click(function() {
        year = year - 1;
        $("#year").html(year + "年");
        $.ajax({
            type:"POST",
            url:"agents.do?method=getSalesMan",
            data:{startPageNo:nextPageNo,maxRows:2,agentIdStr:$("#agentId").val(),month:month,year:year},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initTr(jsonStr);
            }
        });
    });

    $("#yearRight").click(function() {
        year = year + 1;
        $("#year").html(year + "年");
        $.ajax({
            type:"POST",
            url:"agents.do?method=getSalesMan",
            data:{startPageNo:nextPageNo,maxRows:2,agentIdStr:$("#agentId").val(),month:month,year:year},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initTr(jsonStr);
            }
        });
    });

    $("#pageNo_id>div").bind("click", function() {
        $("#salesManTable tr:not(:first)").remove();
        var selectItem = $(this).html();
        if (selectItem == "上一页") {
            if (nextPageNo > 1) {
                nextPageNo = nextPageNo - 1;
                $("#thisPageNo").html(nextPageNo);
                $("#pageNo_id>div:last").css('display', 'block');
                if (nextPageNo == 1) {
                    $(this).css('display', 'none');
                }
            }
        } else if (selectItem == "下一页") {
            if (!isTheLastPage) {
                nextPageNo = nextPageNo + 1;
                $("#thisPageNo").html(nextPageNo);
                $("#pageNo_id>div:first").css('display', 'block');
            }
        }
        $.ajax({
            type:"POST",
            url:"agents.do?method=getSalesMan",
            data:{startPageNo:nextPageNo,maxRows:2,agentIdStr:$("#agentId").val(),month:month,year:year},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initTr(jsonStr);
                if (jsonStr[jsonStr.length - 1].isTheLastPage == "true") {
                    $("#pageNo_id>div:last").css('display', 'none');
                }
            }
        });
    });
});

function initTr(jsonStr) {
    $("#salesManTable tr:not(:first)").remove();
    if (jsonStr.length > 1) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            if (jsonStr[jsonStr.length - 1].isTheLastPage == "true") {
                isTheLastPage = true;
            } else {
                isTheLastPage = false;
            }
            var SalesManCode = jsonStr[i].SalesManCode;
            var name = jsonStr[i].name;
            var mobile = jsonStr[i].mobile;
            var monthTarget = jsonStr[i].monthTarget;
            var monthActual = jsonStr[i].monthActual;
            var yearTarget = jsonStr[i].yearTarget;
            var yearActual = jsonStr[i].yearActual;
            var state = jsonStr[i].state;
            var stateStr = "在职";
            if (state == 0) {
                stateStr = "离职";
            }

            if (i % 2 == 1) {
                var tr = '<tr class="agent_bg">';
            } else {
                var tr = '<tr>';
            }
            tr += '<td>' + SalesManCode + '</td>';
            tr += '<td>' + name + '</td>';
            tr += '<td>' + mobile + '</td>';
            tr += '<td>' + monthTarget+ '</td>';
            tr += '<td>' + yearTarget +  '</td>';
            tr += '<td>' + stateStr + '</td>';
            tr += '</tr >';
            $("#salesManTable").append($(tr));
        }
    } else {
        isTheLastPage = true;
    }

}

//修改目标AJAX
$(document).ready(function() {
    $("#btnInput").click(function() {
        var dataString = "";
        var monthTargets = document.getElementsByName("monthTarget");
        var state = document.getElementById("state").value;
        var yearTarget = document.getElementById("yearTarget").value;

        for (var i = 0; i < monthTargets.length; i++) {
            if (dataString == "") {

                dataString = dataString + monthTargets[i].value;
            } else {
                dataString = dataString + "," + monthTargets[i].value;
            }

        }
        $.ajax({
            type:"POST",
            url:"agents.do?method=updateAgent",
            data:{startPageNo:nextPageNo,maxRows:2,agentId:$("#agentId").val(),monthTargets:dataString,year:$("#year1").val(),stateStr:state,yearTarget:yearTarget},
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                alert("shaolei");
            }
        });
    });
});

$(document).ready(function() {
    $.ajax({
        type:"POST",
        url:"agents.do?method=getAgentTarget",
        data:{agentIdStr:$("#agentId").val(),year:year},
        cache:false,
        async:false,
        dataType:"json",
        success:function(jsonStr) {
            initTd(jsonStr);
        }


    });

    $("#yearRight1").click(function() {
        year = year + 1;
        $("#year2").html(year + "年");
        $("#year1").val(year);
        $.ajax({
            type:"POST",
            url:"agents.do?method=getAgentTarget",
            data:{agentIdStr:$("#agentId").val(),year:year},
            cache:false,
            async:false,
            dataType:"json",
            success:function(jsonStr) {
                initTd(jsonStr);
            }


        });
    });

    $("#yearLeft1").click(function() {
        year = year - 1;
        $("#year2").html(year + "年");
        $("#year1").val(year);
        $.ajax({
            type:"POST",
            url:"agents.do?method=getAgentTarget",
            data:{agentIdStr:$("#agentId").val(),year:year},
            cache:false,
            async:false,
            dataType:"json",
            success:function(jsonStr) {
                initTd(jsonStr);
            }


        });
    });


});
function initTd(jsonStr) {
    $("#monthTable tr :eq(2)").remove();
    for (var i = 0; i < jsonStr.length; i++) {
        var id = "#month" + (i + 1);
        $(id).val(jsonStr[i].monthTarget);

    }

}
