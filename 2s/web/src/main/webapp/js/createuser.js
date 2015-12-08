function checkUserNoIllegalRegExp(data) {
  var userNo = data.value;
  data.value = dataFilter.replace(userNo, regExpPattern.loginPattern, "");
}

function checkDigitalIllegalRegExp(data) {
  var mobile = data.value;
  data.value = dataFilter.replace(mobile, regExpPattern.notDigital, "");
}

function checkEmailIllegalRegExp(data) {
  var email = data.value;
  data.value = dataFilter.replace(email, regExpPattern.emailPattern, "");
}

function checkPassword(dom) {
  var password = jQuery("#password").val();
  var password2 = jQuery("#password2").val();
  if(dom.id == "password") {

    if(password.search(/\W/) >= 0 || password.length > 12 || password.length < 6) {
      jQuery("#password_note").html("密码要求是6至12位的字母或数字！").css({
        'color': 'red'
      });
  } else {
    jQuery("#password_note").html("");
  }

  } else {

    if(password2.search(/\W/) >= 0 || password2.length > 12 || password2.length < 6) {
      jQuery("#confirmpassword_note").html("密码要求是6至12位的字母或数字！").css({
        'color': 'red'
      });
    } else {
      jQuery("#confirmpassword_note").html("");

      if(password2 != '' && password != password2) {
        jQuery("#confirmpassword_note").html("两次输入的密码不相同！").css({
          'color': 'red'
        });
        jQuery("#password2").focus();
      } else {
        jQuery("#confirmpassword_note").html("");

      }
    }

  }
}

function checkUserNo(dom) {
  var data = "";
  var userNo = dom.value;
  jQuery.ajax({
    type: "POST",
    url: "admin.do?method=checkUserNo",
    async: false,
    cache: false,
    data: {
      userNo: userNo
    },
    dataType: "text",
    success: function(str) {
      data = str;
    }
  });
  return data;
}

function userGroupItemClick(name, id, memo) {
  jQuery("#div_account").css({
    'display': 'none'
  });
  jQuery("#note").html(memo);
  jQuery("#span_account").html(name);
  jQuery("#userGroupId").val(id);
}

function userGroupItemMouseOver(dom) {
  jQuery("#div_account > div").css({
    'background-color': '#FFFFFF',
    'color': '#000000'
  });
  jQuery(dom).css({
    "background-color": "#397DF3",
    "color": "#FFFFFF",
    "cursor": "pointer"
  });
}

function userGroupClick() {
  var offset = jQuery("#span_account").parent().offset();
  var offsetHeight = jQuery("#span_account").height();
  jQuery("#div_account").css({
    'display': 'block',
    'position': 'absolute',
    'left': offset.left + 'px',
    'top': offset.top + offsetHeight + 3 + 'px',
    'overflow-x': "hidden",
    'overflow-y': "hidden",
    'color': '#000000',
    'padding-left': 0 + 'px'
  });
}

function showResponse(str) {
  if(str == "") {
    alert("用户添加失败！")
  } else {
    showMessage.fadeMessage("25%", "", "slow", 3000, str);
  }
  jQuery("#span_account").html("--请选择--");
  jQuery("#password2").val("");
  jQuery("#password").val("");
  jQuery("#userGroupId").val("");
  jQuery("#note").html("");
}

function checkRequest() {
  var form = jQuery("#form_createuser")[0];
  var userGroupId = form.userGroupId.value;
  var name = form.name.value;
  var userNo = form.userNo.value;
  var mobile = form.mobile.value;
  var pwd = form.password.value;
  var pwd2 = form.password2.value;
  var qq = form.qq.value;
  var flag = jQuery("#userNo").attr("isTooSimple");
  if(userNo.length == 0) {
    alert("请填写用户名！");
    form.userNo.focus();
    return false;
  }
  if(pwd.length == 0) {
    alert("请填写密码！");
    form.password.focus();
    return false;
  }
  if(!dataValidate.test(mobile, regExpPattern.mobile)) {
    alert("请输入准确的手机号码，否则用户无法通过短信接收信息！");
    form.mobile.focus();
    return false;
  }
  if(userGroupId == "") {
    alert("请选择工作类型！");
    jQuery("#span_account").click();
    return false;
  }
  if(name.length == 0) {
    alert("请填写姓名！");
    form.name.focus();
    return false;
  }
  if(pwd.search(/\W/) >= 0 || pwd.length > 12 || pwd.length < 6) {
    alert("密码要求是6至12位的字母或数字！");
    form.password.focus();
    return false;
  }
  if(pwd2.search(/\W/) >= 0 || pwd2.length > 12 || pwd2.length < 6) {
    alert("密码要求是6至12位的字母或数字！");
    form.password2.focus();
    return false;
  }
  if(!dataValidate.test(userNo, regExpPattern.userNo)) {
    alert("建议使用字母，汉字，数字等其组合！");
    form.userNo.focus();
    return false;
  }
  if(jQuery("#password2").val() != pwd) {
    alert("两次输入的密码不相同！");
    jQuery("#password2").focus();
    return false;
  }
  var email = jQuery.trim(form.email.value);    //去掉空格
  if(email.length > 0) {
    if(!dataValidate.test(email, regExpPattern.email)) {
      alert("输入的Email有误！");
      return false;
    }
  }
  if(flag != "continue") {
    if(userNo.length <= 4) {
      if(!confirm("您的用户名过于简单，确定继续？")) {
        form.userNo.focus();
        return false;
      } else {
        jQuery("#userNo").attr("isTooSimple", "continue");
      }
    }
  }
  var data = checkUserNo(form.userNo);
  if(data != "") {
    showMessage.fadeMessage("25%", "", "slow", 3000, data);
    form.userNo.focus();
    return false;
  }
  return true;
}
jQuery(document).ready(function() {
  jQuery(document).click(function(e) {
    var e = e || event;
    var target = e.srcElement || e.target;
    if(target.id != "div_account" && target.id != "span_account" && target.id != "input_account_button") {
      jQuery("#div_account").hide();
    }
  });
  var userGroupId = jQuery("#userGroupId").val();
  if(userGroupId != "") {
    jQuery("#note").html(jQuery("#" + userGroupId).attr("title"));
    jQuery("#span_account").html(jQuery("#" + userGroupId)[0].innerHTML);
  }
  jQuery('#form_createuser').submit(function() {
    if(checkRequest()) {
    var options = {
        target: '#form_createuser',
        success: showResponse,
        type: 'post',
        dataType: "text",
        clearForm: true,
        resetForm: true
    };
    jQuery(this).ajaxSubmit(options);
      }
    return false;
  });
});