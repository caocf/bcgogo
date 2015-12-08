<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>新增配置</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

<%-- styles --%>
<link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
<link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
<link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
<link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
<link rel="stylesheet" type="text/css" href="styles/style.css"/>
<link rel="stylesheet" type="text/css" href="styles/shopIndividuation.css"/>
<%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

<%-- scripts --%>
<%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<%@include file="/WEB-INF/views/script-common.jsp" %>

<script type="text/javascript">
    $(document).ready(function () {

      var shopName = $("#shopName")[0];
        $("#shopName").live("keyup", function (e) {
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
                        error:function (XMLHttpRequest, error, errorThrown) {
                  $("#div_shopName").css({'display':'none'});
                },
                        success:function (jsonStr) {
                  ajaxStyleShopName(shopName, jsonStr);
                }
              }
          );
        }
      });

        $("#shopName").bind("blur", function () {
        if (isout) {
          $("#div_shopName").css({'display':'none'});
        }
      });

        $("#div_close,#cancleBtn").live("click", function () {
        closeWindow();
      });

        $("#confirmBtn").live("click", function () {
        var shopName = $("#shopName").val();
        var shopId = $("#shopId").val();
        var scene = $("#type").next().children().val();

        var flag = true;

        if (!$('#shopName').val()) {
          return;
        }


        $.ajax({
              type:"POST",
              url:"shopConfig.do?method=checkShopExistAndShopConfigExist",
              async:false,
              data:{
                shopName:$("#shopName").val(),
                scene:scene
              },
              cache:false,
              dataType:"json",
                    success:function (jsonObj) {
                        if ("noShop" == jsonObj.resu) {
                  alert("此店铺没注册！");
                  $("#shopName").val("");
                  flag = false;
                }
                        else if ("hasShopConfig" == jsonObj.resu) {
                  alert("此店铺已有此项，请去父页面搜索并修改！");
                  flag = false;
                }
                        else {
                  $("#shopId").val(jsonObj.resu);
                }
              }
            }
        );


        if (!flag) {
          return;
        }

            $("#addShopConfig_table").ajaxSubmit(function (data) {
          var jsonObj = JSON.parse(data);
            if ("error" == jsonObj.resu) {
            alert("新增失败！");
            return;
          }
          $("#shopId", parent.document).val($("#shopId").val());
          $("#shopName", parent.document).val($("#shopName").val());
          $("#hiddenScene", parent.document).val(scene);
          closeWindow();
          jQuery("#hiddenSearchBtnClick", parent.document).click();
        });

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
                'display':'block', 'position':'absolute',
          'left':x + 'px',
            'top':y + offsetHeight + 5 + 'px',
          'width':'150px',
          'overflowY':'scroll',
          'overflowX':'hidden'
        });
        $("#Scroller-Container_shopName").html("");

        for (var i = 0; i < (jsonStr.length > 10 ? 10 : jsonStr.length); i++) {
          var id = jsonStr[i].id;
            var a = $("<a id=" + id + "></a>");
            a.html(jsonStr[i].name + "<br>");
                $(a).bind("mouseover", function () {
            isout = false;
            $("#Scroller-Container_shopName > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name;// $(this).html();
            selectItemNum = parseInt(this.id.substring(10));
          });

                $(a).bind("mouseout", function (event) {
            isout = true;
            selectValue = "";
          });

                $(a).click(function () {
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

    function closeWindow() {
      $(window.parent.document).find("#mask").hide();
      $(window.parent.document).find("#iframe_PopupBox").hide();
    }
</script>
</head>

<body>

<div>
 <div  id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
         <div class="config_title" id="div_drag">新增店铺配置</div>
         <div class="i_close" id="div_close"></div>
    </div>
        <form:form commandName="shopConfigDTO" id="addShopConfig_table" action="shopConfig.do?method=saveShopConfig"
                   method="post">

   <div class="i_upRight"></div>
   <div class="i_upBody">
            <table cellpadding="0" id="configTable" cellspacing="0" class="configTable">
                <col width="100">
                <col  width="100"/>

                <tr>
                    <td class="label">店铺名称:</td>
                    <td><input type="text" id="shopName" name="shopName" style="height:20px;"/>
                        <input type="hidden" id="shopId" name="shopId"/>
                    </td>
               </tr>
              <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td class="label" id="type">类型:</td>
                    <td><form:select path="scene" cssStyle="width:150px;height:22px;line-height: 22px;">
                          <form:options items="${scene}"/>
                        </form:select>
                    </td>
               </tr>
               <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td class="label" id="configSwitch">开关:</td>
                    <td><input type="radio" name="status" value="ON" checked="checked"/>开启
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="radio" name="status" value="OFF"/>关闭
                    </td>
                </tr>

             </table>
        <div class="height"></div>
        <div class="more_his">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                &nbsp;&nbsp;&nbsp;
                <input type="button" value="确认" onfocus="this.blur();" class="rightSearch" id="confirmBtn"/>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="button" value="取消" onfocus="this.blur();" class="rightSearch" id="cancleBtn"/>
        </div>
     </div>
   </div>
  </form:form>

  <div id="div_shopName" class="i_scroll" style="display:none;width:250px;">
    <div class="Scroller-ContainerShopName" id="Scroller-Container_shopName">
    </div>
  </div>
</body>
</html>