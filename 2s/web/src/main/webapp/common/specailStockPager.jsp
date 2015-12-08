

<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
<script type="text/javascript">
$(function () {
  var dynamical = '<%=request.getParameter("dynamical")%>';
  var formId = '<%=request.getParameter("formId")%>';
  var buttonId = '<%=request.getParameter("buttonId")%>';
  var inputValue = "";
  /**上一页按钮事件*/
  $("#prePage" + dynamical).live('click', function () {
    var totalRows = Number($("#totalRows"+dynamical).val());
    var data = $.parseJSON($("#data" + dynamical).val());
    var pageRows = Number(data.pageRows);
    var totalPage = Math.ceil(totalRows / pageRows);
    var currentPage = data.currentPage;
    /**当前页已经是第一页就返回*/
    if (currentPage == 1) {
      return;
    }
    currentPage = currentPage - 1;
    data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};
    data.rowStart = (currentPage - 1 ) * data.pageRows;
    $("#rowStart"+dynamical).val(data.rowStart);
    var dataString = JSON.stringify(data);
    $("#data" + dynamical).val(dataString);
    pagesAjaxForSolrPost(formId, buttonId);
  });

  /**下一页按钮事件*/
  $("#nextPage" + dynamical).live('click', function () {
    var totalRows = Number($("#totalRows"+dynamical).val());
    var data = $.parseJSON($("#data" + dynamical).val());
    var pageRows = Number(data.pageRows);
    var totalPage = Math.ceil(totalRows / pageRows);
    var currentPage = data.currentPage;
    /**当前页已经是最后一页就返回*/
    if (currentPage == totalPage) {
      return;
    }
    currentPage = currentPage + 1;
    data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};
    data.rowStart = (currentPage - 1 ) * data.pageRows;
    $("#rowStart"+dynamical).val(data.rowStart);
    var dataString = JSON.stringify(data);
    $("#data" + dynamical).val(dataString);
    pagesAjaxForSolrPost(formId, buttonId);
  });

  /**首页事件*/
  $("#firstPage" + dynamical).live('click', function () {
    var totalRows = Number($("#totalRows"+dynamical).val());
    var data = $.parseJSON($("#data" + dynamical).val());
    var pageRows = Number(data.pageRows);
    var totalPage = Math.ceil(totalRows / pageRows);
    var currentPage = data.currentPage;
    /**当前页已经是第一页就返回*/
    if (currentPage == 1) {
      return;
    }
    var currentPage = 1;
    data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};
    data.rowStart = 0;
    $("#rowStart"+dynamical).val(data.rowStart);
    data.currentPage = 1;
    var dataString = JSON.stringify(data);
    $("#data" + dynamical).val(dataString);
    pagesAjaxForSolrPost(formId, buttonId);
  });

  /**尾页事件*/
  $("#lastPage" + dynamical).live('click', function () {
    var totalRows = Number($("#totalRows"+dynamical).val());
    var data = $.parseJSON($("#data" + dynamical).val());
    var pageRows = Number(data.pageRows);
    var totalPage = Math.ceil(totalRows / pageRows);
    var currentPage = data.currentPage;
    /**当前页已经是最后一页就返回*/
    if (currentPage == totalPage) {
      return;
    }
    currentPage = totalPage;
    data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};
    data.rowStart = data.pageRows * (totalPage - 1);
    $("#rowStart"+dynamical).val(data.rowStart);
    var dataString = JSON.stringify(data);
    $("#data" + dynamical).val(dataString);
    pagesAjaxForSolrPost(formId, buttonId);
  });

  /**跳转按钮*/
  $("#button" + dynamical).live('click', function () {
    var getPage = Number($("#getPage" + dynamical).val());
    var totalRows = Number($("#totalRows"+dynamical).val());
    var data = $.parseJSON($("#data" + dynamical).val());
    var pageRows = Number(data.pageRows);
    var totalPage = Math.ceil(totalRows / pageRows);
    var currentPage = data.currentPage;
    /**输入框中输入页码*/
    if (isNaN(getPage)) {
      $("#getPage" + dynamical).val(currentPage);
      return;
    }
    /**输入页码小于1把页码置1*/
    if (getPage < 1) {
      getPage = 1;
      $("#getPage" + dynamical).val(getPage);
    }
    /**输入页码大于总页码把页码置为总页码*/
    if (getPage > totalPage) {
      getPage = totalPage;
      $("#getPage" + dynamical).val(getPage);
    }
    /**输入页码等于当前页就返回*/
    if (getPage == currentPage) {
      return;
    }
    var currentPage = getPage;
    data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};
    data.currentPage = getPage;
    data.rowStart = data.pageRows * (getPage - 1);
    $("#rowStart"+dynamical).val(data.rowStart);
    var dataString = JSON.stringify(data);
    $("#data" + dynamical).val(dataString);
    pagesAjaxForSolrPost(formId, buttonId);
  });

  document.getElementById("getPage" + dynamical).onkeyup = function (evt) {
    evt = evt || event;
    var k = window.event ? evt.keyCode : evt.which
    if (this.value == '0') {
      this.value = '';
      return;
    }

    if (k == 108 || k == 13) {
      return;
    } else if ((k >= 96 && k <= 105) || (k >= 48 && k <= 57)) {
      if (this.value.substring(0, 1) == '0') {
        this.value = inputValue;
        return;
      }
      inputValue = this.value;
    } else if (k == 8) {
      inputValue = this.value;
    } else {
      this.value = inputValue;
    }
  }
  /**输入框中输入页码直接回车事件，和跳转按钮事件一样*/
  $("#getPage" + dynamical).live('keypress', function (e) {
    e = e || event;
    var k = window.event ? e.keyCode : e.which
    if (k == 13 || k == 108) {
      var getPage = Number($("#getPage" + dynamical).val());
      var totalRows = Number($("#totalRows"+dynamical).val());
      var data = $.parseJSON($("#data" + dynamical).val());
      var pageRows = Number(data.pageRows);
      var totalPage = Math.ceil(totalRows / pageRows);
      var currentPage = data.currentPage;

      if (isNaN(getPage)) {
        $("#getPage" + dynamical).val(currentPage);
        return;
      }
      if (getPage < 1) {
          getPage = 1;
        $("#getPage" + dynamical).val(getPage);
      }

      if (getPage > totalPage) {
          getPage = totalPage;
        $("#getPage" + dynamical).val(getPage);
      }

      if (getPage == currentPage)
        return;
      var currentPage = getPage;
      data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};
      data.currentPage = getPage;
      data.rowStart = data.pageRows * (getPage - 1);
      $("#rowStart"+dynamical).val(data.rowStart);
      var dataString = JSON.stringify(data);
      $("#data" + dynamical).val(dataString);
      pagesAjaxForSolrPost(formId, buttonId);
    }
    /**动态初始化组件*/

  });

});

function initData(dynamicalID) {
  $("#rowStart"+dynamicalID).val(0);
  var data = {rowStart:0,pageRows:$("#pageRows"+dynamicalID).val(),currentPage:1};
  var dataString = JSON.stringify(data);
  $("#data" + dynamicalID).val(dataString);
}

function initData(dynamicalID,currentPage) {
  if(null==currentPage || undefined == currentPage || currentPage<1)
  {
    currentPage =1;
  }
  $("#rowStart"+dynamicalID).val(0);
  var data = {rowStart:0,pageRows:$("#pageRows"+dynamicalID).val(),currentPage:currentPage};
  var dataString = JSON.stringify(data);
  $("#data" + dynamicalID).val(dataString);
}

function initPagesForSolr(dynamicalID, formId, buttonId) {
  var totalRows = Number($("#totalRows"+dynamicalID).val());
  var data = $.parseJSON($("#data" + dynamicalID).val());
  if (null == data || totalRows < 1 || Number(data.pageRows) < 1) {
    $("#i_pageBtn" + dynamicalID).hide();
    return;
  } else {
    $("#i_pageBtn" + dynamicalID).show();
  }
  var pageRows = Number(data.pageRows);
  var totalPage = Math.ceil(totalRows / pageRows);
  var currentPage = data.currentPage;
  data = {totalRows:totalRows,pageRows:pageRows,totalPage:totalPage,currentPage:currentPage};

  $("#totalRowS" + dynamicalID + " span").html(totalRows);
  $("#totalPage" + dynamicalID).html(totalPage);
  $("#getPage" + dynamicalID + "," + "#currentPage" + dynamicalID).val(currentPage);

  if (currentPage == totalPage) {
    $("#nextPage" + dynamicalID + ","
        + "#lastPage" + dynamicalID).removeClass('wordButton');
  } else {
    $("#nextPage" + dynamicalID + ","
        + "#lastPage" + dynamicalID).addClass('wordButton');
  }

  if (currentPage == 1) {
    $("#prePage" + dynamicalID + ","
        + "#firstPage" + dynamicalID).removeClass('wordButton');
  } else {
    $("#prePage" + dynamicalID + ","
        + "#firstPage" + dynamicalID).addClass('wordButton');
  }
  var a = null;
  var jsonString = null;
  $($("#firstshenglue" + dynamicalID).nextUntil("#lastshenglue" + dynamicalID)).remove();
  for (var i = 1; i <= totalPage; i++) {
    jsonString = JSON.stringify({page:i,data:data});
    a = $('<a class="num numButton" id="menu_id' + dynamicalID + i + '" style="display:none" >' + i + '</a>');
    $(a).attr("pageInfo", JSON.stringify({page:i,data:data}));
    a.click(function() {
      var json = $.parseJSON($(this).attr("pageInfo"));
      if (json.page == json.data.currentPage) {
        return;
      }
      data.rowStart = (json.page - 1) * json.data.pageRows;
      $("#rowStart"+dynamicalID).val(data.rowStart);
      data.currentPage = json.page;
      var dataString = JSON.stringify(data);
      $("#data" + dynamicalID).val(dataString);
      pagesAjaxForSolrPost(formId, buttonId);
    });
    $("#lastshenglue" + dynamicalID).before(a);
  }

  $('#menu_id' + dynamicalID + currentPage).addClass('numButton_selected');
  if (totalPage <= 5) {
    for (var i = 1; i <= totalPage; i++) {
      $("#menu_id" + dynamicalID + i).css("display", "block");
    }
  } else {
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
}


function pagesAjaxForSolrPost(formId, buttonId) {
  if (formId && formId != "null") {
    $(formId).submit();
  } else {
    eval(buttonId)();
  }
}

</script>

<input type="hidden"  id="rowStart<%=request.getParameter("dynamical")%>" value="0">
<input type="hidden"  id="pageRows<%=request.getParameter("dynamical")%>" value="10">
<input type="hidden"  id="totalRows<%=request.getParameter("dynamical")%>" value="0">

<input type="hidden" id="currentPage<%=request.getParameter("dynamical")%>">
<input type="hidden" id="data<%=request.getParameter("dynamical")%>">

<div class="i_pageBtn" id="i_pageBtn<%=request.getParameter("dynamical")%>" style="display: none">
  <a class="first" id="firstPage<%=request.getParameter("dynamical")%>">首页</a><span class="line" style="visibility:hidden" >|</span>
  <a class="lastPage" id="prePage<%=request.getParameter("dynamical")%>">上一页</a><span class="line" style="visibility:hidden">|</span>
  <span class="ellipsis" id="firstshenglue<%=request.getParameter("dynamical")%>">...</span>
  <span class="ellipsis" id="lastshenglue<%=request.getParameter("dynamical")%>">...</span>
  <a class="nextPage" id="nextPage<%=request.getParameter("dynamical")%>">下一页</a> <span class="line" style="visibility:hidden">|</span>
  <a class="last" id="lastPage<%=request.getParameter("dynamical")%>">尾页</a><span class="line" style="visibility:hidden">|</span>

  <div class="pageNum" id="totalRowS<%=request.getParameter("dynamical")%>">
    &nbsp;&nbsp;共 <span class="recordCount">0</span> 条记录&nbsp;&nbsp;
    <%--<div class="pageNum" id="totalPage<%=request.getParameter("dynamical")%>">--%>
    共
    <span class="pageCount" id="totalPage<%=request.getParameter("dynamical")%>">0</span>
    页，到第
    <input type="text" class="selectPage"
           id="getPage<%=request.getParameter("dynamical")%>">
    页
    <input class="pageSure" type="button" onfocus="this.blur();" value="跳转"
           id="button<%=request.getParameter("dynamical")%>">
  </div>
</div>
