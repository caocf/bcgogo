/// JavaScript Document
jQuery(function() {
  jQuery(".type_card").hide();
  jQuery(".stock_txtClick").click(function() {
    jQuery(".type_card").show();
    jQuery(".type_card li").focus();
    jQuery(".stock_txtName").bind("mouseleave", function() {
      jQuery(".type_card").hide();
    });
    jQuery(".type_card li").click(function() {
      var txt = jQuery(this).html();
      jQuery(".stock_text").val(txt);
      jQuery(".type_card").hide();

      if (jQuery("#customCard")[0].checked) {
        if (txt == "计次卡") {
          jQuery("#type").val("timeCard");
        }
        else {
          jQuery("#type").val("valueCard");
        }
      }
    });
  });

  jQuery(".card_show input").bind("click", function() {
    if (this.value == "洗车卡") {
      jQuery("#type").val("timeCard");
    }
    else {
      jQuery("#type").val("valueCard");
    }
    if (jQuery("#customCard").checked) {
      if (jQuery("#cardKind").val() == "计次卡") {
        jQuery("#type").val("timeCard");
      }
      else {
        jQuery("#type").val("valueCard");
      }
    }
  });

  jQuery("#selectCardKindBtn").bind("blur", function() {
    jQuery(".type_card").hide();
  });

  jQuery("#customCard").bind("click", function() {
    jQuery("#cardName").focus();
    jQuery("#cardName").select();
    if (jQuery("#cardKind").val() == "计次卡") {
      jQuery("#type").val("timeCard");
    }
    else {
      jQuery("#type").val("valueCard");
    }
  });

  jQuery("#cardName").bind("blur", function() {
    if (jQuery("#customCard")[0].checked) {
      if (jQuery("#cardName").val() == "") {
        return;
      }

      var cardName =  (jQuery("#cardName").val().toUpperCase()).replace(/\s/g,"");

      if (cardName == "洗车卡" || cardName == "银卡"
          || cardName == "金卡" || cardName == "VIP卡") {
        alert("卡名与上述四种默认卡重复，请选择上述卡");
        jQuery("#cardName").val("");
        return;
      }

      var name = jQuery("#cardName").val();
      jQuery("#customCard").val(name);
//      jQuery.ajax({
//        type:"POST",
//        url:"member.do?method=checkCardNameByName",
//        async:true,
//        data:{
//          name:name,
//          tsLog: 10000000000 * (1 + Math.random())
//        },
//        cache:false,
//        dataType:"json",
//        success:function(data) {
//          var tmp = data.resu;
//          if (tmp == "error") {
//            var sel = confirm("此卡名称已被占用，继续操作将会覆盖此卡信息！");
//            if (sel == false) {
//              jQuery("#cardName").val("").select().focus();
//              jQuery("#customCard").val("");
//            }
//          }
//        }
//      });
    }
  });

  jQuery("#nextStep").live("click", function() {

    if (jQuery("#customCard")[0].checked) {
      if (jQuery("#cardName").val() == "") {
        alert("卡名称不能为空");
        return;
      }

      if (jQuery("#cardName").val() == "洗车卡" || jQuery("#cardName").val() == "银卡"
          || jQuery("#cardName").val() == "金卡" || jQuery("#cardName").val() == "VIP卡") {
        alert("卡名与上述四种默认卡重复，请选择上述卡");
        jQuery("#cardName").val("");
        return;
      }
        var flag = false;
        if (jQuery("#customCard")[0].checked) {
            if (jQuery("#cardName").val() == "") {
                return;
            }
      jQuery.ajax({
        type:"POST",
        url:"member.do?method=checkCardNameByName",
                async:false,
        data:{
                    name:jQuery("#cardName").val(),
          tsLog: 10000000000 * (1 + Math.random())
        },
        cache:false,
        dataType:"json",
        success:function(data) {
          var tmp = data.resu;
          if (tmp == "error") {
            var sel = confirm("此卡名称已被占用，继续操作将会覆盖此卡信息！");
            if (sel == false) {
              jQuery("#cardName").val("").select().focus();
              jQuery("#customCard").val("");
                            flag = true;
            }
          }
        }
      });
    }

        if(flag)
        {
            return false;
      }


      if (jQuery("#cardKind").val() == "计次卡") {
        jQuery("#type").val("timeCard");
      }
      else {
        jQuery("#type").val("valueCard");
      }
    }


    jQuery("#memberCardForm").submit();
  });

  trCount = jQuery(".item").size();
  var trSample = '<tr class="item">' +
      '<td>' +
      '<input type="hidden" id="memberCardServiceDTOs0.serviceId" name="memberCardServiceDTOs0.serviceId" value=""/>' +
      '<input type="text" id="memberCardServiceDTOs0.serviceName" autocomplete="off" name="memberCardServiceDTOs0.serviceName" class="txt_card serviceName textbox" value=""/>' +
      '<td><input type="text" id="memberCardServiceDTOs0.timesStr" autocomplete="off" name="memberCardServiceDTOs0.timesStr"  maxlength="8" class="txt_card useTimes textbox" value=""/></td>' +
      '<td class="qixian">' +
      '<input type="text" id="memberCardServiceDTOs0.termStr" autocomplete="off" name="memberCardServiceDTOs0.termStr" class="txt_input term textbox" value="" />' +
      '<label >月</label>' +
      '</td>' +
      '<td>' +
      '<input class="opera1" type="button" id="memberCardServiceDTOs0.opera1Btn" name="memberCardServiceDTOs0.opera1Btn">' +
      '</td>' +
      '</tr>';

  //增加行
  jQuery(".opera2").live('click', function() {
    var ischeck = checkServiceData(this);
    if (!ischeck && ischeck != null) {
      return;
    }

    //采购单检查是否相同
    if (jQuery(".item").size() >= 1)
      if (checkTheSame()) {
        alert("服务有重复内容或为空，请修改或删除。");
        return false;
      }
     var qianxianStr;
    if($(".item").find("input").size()!=0){
        qianxianStr=$(".qixian input").last().val();
    }
    var tr = jQuery(trSample).clone();
    jQuery(tr).find("input").val("");
    $(tr).find(".qixian input").val(qianxianStr)
    jQuery(tr).find("input").each(function(i) {
      //replace id
      var idStr = jQuery(this).attr("id");
      var idStrs = idStr.split(".");
      if (idStr == '') {
        return true;
      }
      var tcNum = trCount > 0 ? trCount - 1 : 0;

      while (checkThisServiceDom(tcNum, idStrs[1])) {
        tcNum = ++tcNum;
      }
      var newId = "memberCardServiceDTOs" + tcNum + "." + idStrs[1];
      jQuery(this).attr("id", newId);

      //replace name
      var nameStr = jQuery(this).attr("name");
      if (nameStr == '') {
        return true;
      }
      var nameStrs = nameStr.split(".");
      var newName = "memberCardServiceDTOs[" + tcNum + "]." + nameStrs[1];
      jQuery(this).attr("name", newName);
      jQuery(this).attr("autocomplete", "off");
      var idPrefix = this.id.split(".")[0];
      var idSuffix = this.id.split(".")[1];
      var domrows = parseInt(idPrefix.substring(21, idPrefix.length));
      jQuery(this).bind('keyup', function(e) {         //为input绑定keyup事件

        var pos = getCursorPosition(this);
        if (!checkKeyUp(this, e)) {
          return;
        }

        setCursorPosition(this, pos);
      });
      jQuery(this).bind('click', function() {
        var pos = getCursorPosition(this);

        setCursorPosition(this, pos);
      });
    });
      //init input serviceId
    jQuery(tr).appendTo("#table_productNo");
    $(".item").last().find("input").first().val("");
    trCount++;
    isShowAddButton();
  });

  // TODO settimeout 200ms to call
  setTimeout(isShowAddButton, 200);

  //删除行
  jQuery(".opera1").live('click', function() {

    var iPrefixId = jQuery(this).attr("id");
    iPrefixId = iPrefixId.substring(0, iPrefixId.indexOf("."));

    jQuery(this).closest("tr").remove();

    isShowAddButton();
  });
});

function checkThisServiceDom(tn, idstr) {
  if (document.getElementById("memberCardServiceDTOs" + tn + "." + idstr)) {
    return true;
  }
  return false;
}

//判断是否显示+按钮
function isShowAddButton() {
  //如果初始化的话就默认加一行
  if (jQuery(".item").size() <= 0) {
    jQuery(".opera2").trigger("click");
  }
  jQuery(".item .opera2").remove();
  var opera1Id = jQuery(".item:last").find("td:last>input[class='opera1']").attr("id");
  if (opera1Id == null || opera1Id == "") {
    return;
  }

  jQuery(".item:last").find("td:last>input[class='opera1']").after(' <input class="opera2" ' +
      ' id="memberCardServiceDTOs' + (opera1Id.split(".")[0].substring(21)) + '.plusbutton" type="button"/>');
}


function setCursorPosition(ctrl, pos) {//设置光标位置函数
  if (ctrl.type != "text") {
    return;
  }
  if (ctrl.setSelectionRange) {
    ctrl.focus();
    ctrl.setSelectionRange(pos, pos);
  }
  else if (ctrl.createTextRange) {
    var range = ctrl.createTextRange();
    range.collapse(true);
    range.moveEnd('character', pos);
    range.moveStart('character', pos);
    range.select();
  }
}

function getCursorPosition(ctrl) {//获取光标位置函数
  var CaretPos = 0;
  // IE Support
  if (ctrl.type != "text") {
    return;
  }
  if (document.selection) {
    ctrl.focus();
    var Sel = document.selection.createRange();
    Sel.moveStart('character', -ctrl.value.length);
    CaretPos = Sel.text.length;
  }
  // Firefox support
  else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0')
    CaretPos = ctrl.selectionStart;
  return (CaretPos);
}

//检查服务是否相同
function checkTheSame() {
  var trs = jQuery(".item");
  if (!trs)
    return false;
  if (trs.length < 2)
    return false;
  var s = '';

  //先获取最后一个
  var cur = '';//当前最后添加的一条记录
  for (var i = trs.length - 1; i >= 0; i --) {
    var inputs = trs[i].getElementsByTagName("input");
    if (!inputs)
      continue;
    var index = inputs[0].name.split(".")[0].substring(inputs[0].name.indexOf('[') + 1, inputs[0].name.indexOf(']'));

    if (i == trs.length - 1) {
//            最后添加的一个
      cur += document.getElementById("memberCardServiceDTOs" + index + ".serviceName").value + ",";

    } else {
      var older = '';
      older += document.getElementById("memberCardServiceDTOs" + index + ".serviceName").value + ",";
      if (cur == older) {
        return true;
      }
    }

  }
  return false;
}


function checkServiceData(domObj) {
  var reg = /^\-{0,1}[0-9]{1,}$/;
  var idPrefix = domObj.id.split(".")[0];
  if (idPrefix == "" || idPrefix == null) {
    return null;
  }
  var serviceName = document.getElementById(idPrefix + ".serviceName").value;
  var times = document.getElementById(idPrefix + ".timesStr").value;
  var term = document.getElementById(idPrefix + ".termStr").value;

  if (serviceName == null || serviceName == "") {
    alert("请选择服务！");
    return false;
  }

  if (times == null || times == "") {
    alert("请输入次数");
    return false;
  }

  if (term == null || term == "") {
    alert("请输入日期");
    return false;
  }

  //  正则表达式判定整数
    if(times!="不限次"){
  var foo = APP_BCGOGO.Validator;
  if (!(foo.stringIsIntGreaterThanNegativeOne(times))) {
    alert("请输入大于等于-1的整数");
    return false;
  }
    }
    if("无期限" != $.trim(term) && "无限期"!=$.trim(term))
    {
        var foo = APP_BCGOGO.Validator;
        if (!(foo.stringIsInt(term))) {
            alert("期限填写不正确！");
            return false;
        }
    }

}

function checkServiceDataNoMessage(domObj) {
  var reg = /^[1-9]\d*|0$/;
  var idPrefix = domObj.id.split(".")[0];
  if (idPrefix == "" || idPrefix == null) {
    return null;
  }
  var serviceName = document.getElementById(idPrefix + ".serviceName").value;
  var times = document.getElementById(idPrefix + ".timesStr").value.toString();
  var term = document.getElementById(idPrefix + ".termStr").value.toString();

  if ((serviceName == null || serviceName == "") && (times == null || times == "") && (term == null || term == "")) {
    return true;
  }

  if (serviceName == null || serviceName == "") {
    return false;
  }

  if (times == null || times == "") {
    return false;
  }

  if (term == null || term == "") {
    return false;
  }

//  正则表达式判定整数
  if(times!="不限次"){
  var foo = APP_BCGOGO.Validator;
  if (!(foo.stringIsIntGreaterThanNegativeOne(times))) {
    return false;
  }
  }
}

