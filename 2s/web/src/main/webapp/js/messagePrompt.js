var showMessage = function() {
    var inputtingTimerId;
  /**
   * 淡入淡出框 方法控制
   * @param offsetHeight 纵向位置
   * @param offsetWidth  横向位置 为空默认是中间
   * @param style(淡入)：show、fast 或者数字
   * @param time (淡出)：show、fast 或者数字
   * @param message
   */
    return {
  fadeMessage: function(offsetHeight, offsetWidth, style, time, message) {
            GLOBAL.debug("== fade ==");
            if (inputtingTimerId)clearTimeout(inputtingTimerId);
            inputtingTimerId = setTimeout(function() {
                GLOBAL.debug("== true ==");
    if (offsetWidth == "") {
      offsetWidth = (Math.round((document.body.scrollWidth - 200) / 2)).toString() + "px "; //center
    }
    $("#messageShowPrompt")
        .css({'top':offsetHeight})
        .css({'margin-left': offsetWidth});
    $("#promptContent").html(message);
    $("#messageShowPrompt").fadeIn(style).fadeOut(time);
            }, time);
    }
  }
}();
// author:zhangjuntao
function notOpen() {
    showMessage.fadeMessage("35%", "40%", "slow", 1000, "此功能稍后开放！");     // top left fadeIn fadeOut message
}
