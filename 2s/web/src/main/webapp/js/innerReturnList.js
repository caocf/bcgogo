$(document).ready(function () {
    $(function () {
        tableUtil.tableStyle(".tabPick",null,"odd");
    })
    $("#searchBtn").click(function () {
        $("#thisform").attr("action", "pick.do?method=showInnerReturnListPage");
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

    $("#addNewInnerReturn").click(function(){
        window.location.href = "pick.do?method=createInnerReturn";
    });
    $(".showInnerReturn").bind("click", function () {
        window.location.href = $(this).attr("url");
    });

})

