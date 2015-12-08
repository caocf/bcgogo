/**动态初始化组件*/
function initPage(jsonStr, dynamicalID, url, urlChange, functionName, hide, jstogetChecked, data, dataChange) {
    url = $("#url" + dynamicalID).val();
    $("#urlChange" + dynamicalID).val(urlChange);
    /***/
    var pager = jsonStr ? jsonStr.pager : null;

    if(!pager) {
        G.error("pager is null, from bcgogo-paging.js");
        return;
    }

    $("#functionName"+dynamicalID).val(functionName);

    $("#totalRowS" + dynamicalID + " span").html(pager.totalRows);
    $("#totalPage" + dynamicalID).html(pager.totalPage);
    $("#getPage" + dynamicalID + ","
            + "#currentPage" + dynamicalID).val( pager.currentPage );

    var totalPage = pager.totalPage * 1;
    var currentPage = pager.currentPage * 1;
    var prefixArr = [];

    if (currentPage == totalPage) {
        $("#nextPage" + dynamicalID + ","
                + "#lastPage" + dynamicalID).removeClass('wordButton');
    }else {
        $("#nextPage" + dynamicalID + ","
                + "#lastPage" + dynamicalID).addClass('wordButton');
    }
    if (currentPage == 1) {
        $("#prePage" + dynamicalID + ","
                + "#firstPage" + dynamicalID).removeClass('wordButton');
    }else {
        $("#prePage" + dynamicalID + ","
                + "#firstPage" + dynamicalID).addClass('wordButton');
    }

    var tr = ' ';
    $($("#firstshenglue" + dynamicalID).nextUntil("#lastshenglue" + dynamicalID)).remove();

    var dataString = JsonToStr(data);
    $("#data" + dynamicalID).val(dataString);
    var dataChangeString = JsonToStr(dataChange);
    $("#dataChange" + dynamicalID).val(dataChangeString);

    for (var i = 1; i <= totalPage; i++) {
        tr = tr + '<a class="num numButton" id="menu_id' + dynamicalID + i + '" style="display:none" onclick = "toPage(' + i + ',' + currentPage + ',\'' + dynamicalID + '\',\'' + url + '\',\'' + urlChange + '\',\'' + functionName + '\',\'' + hide + '\',\'' + jstogetChecked + '\',' + dataString + ',' + dataChangeString + ')">' + i + '</a>';
    }

    $("#firstshenglue" + dynamicalID).after(tr);
    $('#menu_id' + dynamicalID + currentPage).addClass('numButton_selected');
    if (totalPage <= 5) {
        for (var i = 1; i <= totalPage; i++) {
            $("#menu_id" + dynamicalID + i).css("display", "block");
        }
    }else {
        if (currentPage - 2 <= 1) {
            for (var i = 1; i <= 5; i++) {
                $("#menu_id" + dynamicalID + i).css("display", "block");
            }
            $("#lastshenglue" + dynamicalID).css('display', 'block');
        }

        if ((currentPage + 2) >= totalPage) {
            for (var i = totalPage - 4; i <= totalPage; i++) {
                $("#menu_id" + dynamicalID + i).css("display", "block");
            }
            $("#firstshenglue" + dynamicalID).css('display', 'block');
        }

        if (currentPage > 3 && currentPage + 2 < totalPage) {
            //$(".i_pageBtn >a:gt("+(currentPage-3)+"):lt("+(currentPage+1)+")").css('display','block');
            for (var i = currentPage - 2; i <= currentPage + 2; i++) {
                $("#menu_id" + dynamicalID + i).css("display", "block");
            }

            $("#firstshenglue" + dynamicalID + ","
                    + "#lastshenglue" + dynamicalID).css('display', 'block');
        }
    }

    if ($('#menu_id' + dynamicalID + totalPage).css('display') == 'block')
        $("#lastshenglue" + dynamicalID).css('display', 'none')

    if ($('#menu_id' + dynamicalID + "1").css('display') == 'block')
        $("#firstshenglue" + dynamicalID).css('display', 'none')

    if(null == jsonStr ||"[]"==JSON.stringify(jsonStr) || pager.totalRows*1 <1) {
        $("#i_pageBtn"+dynamicalID).hide();
        return;
    } else {
        $("#i_pageBtn" + dynamicalID).show();
    }
}

function toPage(num, currentPage, dynamicalID, url, urlChange, functionName, hide, jstogetChecked, data1, dataChange) {
    url = $("#url" + dynamicalID).val();

    var data = eval(data1);
    if (num == currentPage) {
        return;
    }
    data.startPageNo = num;

  if (null != urlChange && 'null' != urlChange && '' != urlChange && undefined != urlChange && "undefined" != urlChange) {
    url = urlChange;
  }
  if (null != dataChange && 'null' != dataChange && '' != dataChange && undefined != dataChange && "undefined" != dataChange) {
    data = dataChange;
    data.startPageNo = num;
  }

    pagingAjaxPost({
        "dynamical":dynamicalID, "url":url,
        "urlChange":urlChange, "functionName":functionName,
        "hide":hide, "jstogetChecked":jstogetChecked,
        "data":data, "dataChange": dataChange
    });
}

// TODO not necessary function, delete it,  use the JSON2 only
function JsonToStr(obj) {
    var dataString = JSON.stringify(obj);
    String.prototype.replaceAll = function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    }
    dataString = dataString.replaceAll("\"", "\'");
    return dataString;
}

// TODO not necessary function, delete it,  use the JSON2 only, evel() that I give the garentine is not safe
function strToJson(str) {
    return eval('(' + str + ')');
}

function pagingAjaxPost( params ) {
    $.ajax({
        type:"POST",
        url:params["url"],
        data:params["data"],
        cache:false,
        dataType:"json",
        success:function( jsonData ){
            if( !jsonData )
                return false;

            eval(params["functionName"])(jsonData);
            if("setCheckedByPreOrNextPage" == params["jstogetChecked"]) {
                eval(params["jstogetChecked"] + "()");
            }
            initPage( jsonData, params["dynamical"], params["url"], params["urlChange"]
                    , params["functionName"], params["hide"], params["jstogetChecked"], params["data"]
                    , params["dataChange"] );
        }
    });
}