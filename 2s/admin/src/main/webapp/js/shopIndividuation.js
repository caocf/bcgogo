$(document).ready(function() {
  var shopName = $("#shopName")[0];
  $("#shopName").live("keyup", function(e) {
    if (!checkKeyUp(this, e)) {
      return;
    }

    if (shopName.value == '' || shopName.value == null) {
      $("#div_shopName").css({'display':'none'});
    }
    else {
      shopName.value = shopName.value.replace(/[\ |\\]/g, "");
      $.ajax({
            type:"POST",
            url:"print.do?method=getShopNameByName",
            async:true,
            data:{
              name:shopName.value,
              now:new Date()
            },
            cache:false,
            dataType:"json",
            error:function(XMLHttpRequest, error, errorThrown) {
              $("#div_shopName").css({'display':'none'});
            },
            success:function(jsonStr) {
              ajaxStyleShopName(shopName, jsonStr);
            }
          }
      );
    }
  });

  $("#shopName").bind("blur", function() {
    if (isout) {
      $("#div_shopName").css({'display':'none'});
    }
  });

  jQuery(".configSwitch").live("click",function(){

    var obj = this;
    var scene = jQuery(this).attr("scene");
    var status = jQuery(this).attr("status");
    var shopId = jQuery(this).attr("shopId");
    //如果按钮是蓝色的，继续点击则返回
    if(("ON"==status && -1 != jQuery(this).attr("class").indexOf("manualHover")) ||
        ("OFF" == status && -1 != jQuery(this).attr("class").indexOf("closedHover")))
    {
      return;
    }

    var url = "shopConfig.do?method=changeConfigSwitch"

    jQuery.ajax({
      type:"POST",
      url:url,
      data:{
        scene:scene,
        status:status,
        shopId:shopId
      },
      cache:false,
      dataType:"json",
      success:function(jsonObject) {
        if("success" == jsonObject.resu)
        {
          if("ON"==status)
          {
            jQuery(obj).attr("class","manualHover configSwitch");
            jQuery(obj).next().attr("class","closed configSwitch");
          }
          else
          {
            jQuery(obj).prev().attr("class","manual configSwitch");
            jQuery(obj).attr("class","closedHover configSwitch");
          }
        }
        else
        {
          "更改失败！";
        }
      }
    });
  });

  $("#configSearchBtn").live("click",function(){

    var flag = true;

    if(!$('#shopId').val() && $('#shopName').val())
    {
      $.ajax({
            type:"POST",
            url:"shopConfig.do?method = checkShop",
            async:false,
            data:{
              shopName:$("#shopName").val()
            },
            cache:false,
            dataType:"json",
            success:function(jsonObj) {
              if("error" == jsonObj.resu)
              {
                alert("此店铺没注册！");
                $("#shopName").val("");
                flag = false;
              }
            }
          }
      );
    }

    if(!flag)
    {
      return;
    }

    var data = {startPageNo:1,maxRows:10,shopId:$('#shopId').val(),shopName:$('#shopName').val(),scene:$('#type').next().val()};

    var url = "shopConfig.do?method=getShopConfigBySceneAndShop";

    $.ajax({
          type:"POST",
          url:url,
          async:true,
          data:data,
          cache:false,
          dataType:"json",
          success:function(jsonObj) {
            init(jsonObj);
            $("#shopId").val("");
            initfenye(jsonObj, "dynamical1", url, '', "init", '', '',
                data, '');
          }
        }
    );
  });

  $("#addShopConfig").live("click",function(){
    bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"shopConfig.do?method=addShopConfig"});
  });

  $("#hiddenSearchBtnClick").live("click",function(){
    var form = $('#type').next()[0];
    var scene = $("#hiddenScene").val();
    for(var i=0;i<form.options.length;i++)
    {
      if(scene == form.options[i].value)
      {
        form.options[i].selected;
        break;
      }
    }
    $("#configSearchBtn").click();
  });

});

var isout = true;
var lastvalue;
function checkKeyUp(domObj, domEvent) {
  var e = domEvent || event;
  var eventKeyCode = e.which || e.keyCode;
  if (eventKeyCode == 38 || eventKeyCode == 40) {
    return false;
  } else {
    var domvalue = domObj.value;
    if (domvalue != lastvalue) {
      lastvalue = domvalue;
      return true;
    } else {
      return false;
    }
  }
}
function getX(elem) {
  var x = 0;
  while (elem) {
    x = x + elem.offsetLeft;
    elem = elem.offsetParent;
  }
  return x;
}
function getY(elem) {
  var y = 0;
  while (elem) {
    y = y + elem.offsetTop;
    elem = elem.offsetParent;
  }
  return y;
}

function ajaxStyleShopName(domObject, jsonStr) {
  var offset = $(domObject).offset();
  var offsetHeight = $(domObject).height();
  var offsetWidth = $(domObject).width();
  domTitle = domObject.name;
  var x = getX(domObject);
  var y = getY(domObject);
  selectmore = jsonStr.length;
  if (selectmore <= 0) {
    $("#div_shopName").css({'display':'none'});
  }
  else {
    $("#div_shopName").css({
      'display':'block','position':'absolute',
      'left':x + 'px',
      'top':y + offsetHeight + 5+ 'px',
      'width':'150px',
      'overflowY':'scroll',
      'overflowX':'hidden'
    });
    $("#Scroller-Container_shopName").html("");

    for (var i = 0; i < (jsonStr.length>10?10:jsonStr.length); i++) {
      var id = jsonStr[i].id;
      var a = $("<a id="+id+"></a>");
      a.html(jsonStr[i].name+ "<br>");
      $(a).bind("mouseover", function() {
        isout = false;
        $("#Scroller-Container_shopName > a").removeAttr("class");
        $(this).attr("class", "hover");
        selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name;// $(this).html();
        selectItemNum = parseInt(this.id.substring(10));
      });

      $(a).bind("mouseout", function(event) {
        isout = true;
        selectValue = "";
      });

      $(a).click(function() {
        var sty = this.id;
        $(domObject).val(selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name); //取的第一字符串
        $("#shopId").val(sty);
        selectItemNum = -1;
        $("#div_shopName").css({'display':'none'});
      });

      $("#Scroller-Container_shopName").append(a);
    }
  }
}

function init(jsonObj)
{
  $("#table_config tr:not(:first)").remove();
  if (jsonObj.length > 1) {
    for (var i = 0; i < jsonObj.length - 1; i++) {

      var shopId = jsonObj[i].shopIdStr;
      var shopName = jsonObj[i].shopName;
      var scene = jsonObj[i].scene;
      var sceneDescription = jsonObj[i].sceneDescription;
      var status = jsonObj[i].status;

      var tr = '<tr>';
      tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
      tr += '<td id="shopId">'+shopName+'</td>';
      tr += '<td>'+scene+'</td>';
      tr += '<td>'+sceneDescription+'</td>';
      if("ON" == status)
      {
        tr += '<td><a class="manualHover configSwitch" status="ON" scene="'+scene+'" shopId="'+shopId+'">打开</a>' +
            '<a class="closed configSwitch" status="OFF" scene="'+scene+'" shopId="'+shopId+'">关闭</a></td>';
      }
      else
      {
        tr += '<td><a class="manual configSwitch" status="ON" scene="'+scene+'" shopId="'+shopId+'">打开</a>' +
            '<a class="closedHover configSwitch" status="OFF" scene="'+scene+'" shopId="'+shopId+'">关闭</a></td>';
      }
      tr += '</tr >';
      $("#table_config").append($(tr));
    }
  }
}

function changeShopConfigScene()
{
  $("#configSearchBtn").click();
}
