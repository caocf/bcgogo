
$(document).ready(function(){
    $("<iframe id='iframe_ad_small' name='iframe_ad_small' width='45' height='300' frameBorder=0 style='position: fixed; right:0px; top:50%;margin-top: -150px; z-index: 1000; display:block; ' allowTransparency='true'></iframe>").appendTo('body');
    $("#iframe_ad_small").attr("src", "js/advertisement/small.html");
    $("<iframe id='iframe_ad' name='iframe_ad' width='100%' height='100%' frameBorder=0 style='position: fixed; right:0px;top:0px; z-index: 1000; display:block; 'allowTransparency='true'></iframe>").appendTo('body');
    $("#iframe_ad").attr("src", "js/advertisement/demo.html");
    var c_name=getCookie("loginRecently");
    if(c_name !== null && c_name !== undefined && c_name !== ''){
        //alert();
        $("#iframe_ad").css("z-index",'-1');
        $("#iframe_ad").css("filter","alpha(opacity=0)");
        $("#iframe_ad").css("-moz-opacity","0");
        $("#iframe_ad").css("opacity","0");
        var music=document.getElementById('iframe_ad').contentWindow.document.getElementById('music');
        //document.getElementById('iframe_ad').contentWindow.document.getElementById('music').pause();
    }
    else{
        $("#iframe_ad_small").hide();
        setCookie("loginRecently","recently",24);
        //document.getElementById('iframe_ad').contentWindow.document.getElementById('music').play();
    }
;
});

function getCookie(cname)
{
var name = cname + "=";
var ca = document.cookie.split(';');
for(var i=0; i<ca.length; i++)
  {
  var c = ca[i].trim();
  if (c.indexOf(name)==0)
      return c.substring(name.length,c.length);
  }
return "";
}

function setCookie(cname,cvalue,exhours)
{
var d = new Date();
d.setTime(d.getTime()+(exhours*60*60*1000));
var expires = "expires="+d.toGMTString();
document.cookie = cname + "=" + cvalue + "; " + expires;
}
