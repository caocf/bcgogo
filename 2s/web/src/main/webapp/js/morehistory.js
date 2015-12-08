/**
 * Created by IntelliJ IDEA.
 * User: Rex
 * Date: 12-1-12
 * Time: 下午3:49
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function() {
//    window.parent.addHandle(document.getElementById('getmorehistory'), window);

    $("#iframe_PopupBox").load(function(){
             var height = (screen.height-$(this).attr("height"))/2;
             var width = (screen.width-$(this).attr("width"))/2;
              $(this).css('top',height+'px');
              $(this).css('left',width+'px');
//这样给以一个最小高度
            //$(this).height( height < 400 ? 400 : height );
        });


});
