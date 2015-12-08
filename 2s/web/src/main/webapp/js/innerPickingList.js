$(document).ready(function () {
    $(function () {
        tableUtil.tableStyle(".tabPick",null,"odd");
    })
    $("#searchBtn").click(function () {
        $("#thisform").attr("action", "pick.do?method=showInnerPickingListPage");
        $("#thisform").submit();
    });

    $("#startTimeStr,#endTimeStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-5, c",
        "yearSuffix": "",
        "showButtonPanel": true,
        "maxDate": 0
    });

    $("#addNewInnerPicking").click(function(){
        window.location.href = "pick.do?method=createInnerPicking";
    });
    $(".showInnerPicking").bind("click", function () {
        window.location.href = $(this).attr("url");
    });
})

