// JavaScript Document
var xx,dd,aa,ss,nn,ee,ww;
	 $(function() {
		 xx=window.document.getElementById("checkAll");
		 dd=window.document.getElementById("no1");
		 ss=window.document.getElementById("no2");
		 aa=window.document.getElementById("no3");
		 ww=window.document.getElementById("no4");
		 ee=window.document.getElementById("no5");
		 nn=window.document.getElementById("chk_show").getElementsByTagName("label").length;
	});
	var j=0;
	var bol=false;
	function show1(ss)
	{

		if(ss.style.backgroundImage == "url(../web/images/check_off.jpg)")
		{             alert("xxxxxx")
		ss.style.backgroundImage = "url(../web/images/check_on.jpg)";
		j=j+1;
		}
		else{
		ss.style.backgroundImage = "url(../web/images/check_off.jpg)";
		xx.style.backgroundImage = "url(../web/images/check_off.jpg)";
		j=j-1;
		}
		qq();
	}
	
	
	
	
	function show()
	{		
		if(xx.style.backgroundImage == "url(../web/images/check_off.jpg)")
		{
			
		xx.style.backgroundImage = "url(../web/images/check_on.jpg)";
		bol=true;
		}
		else{
		xx.style.backgroundImage = "url(../web/images/check_off.jpg)";
		bol=false;
		
		}
		if(bol==true)
		{
			for(var i=0;i<nn;i++)
			{
				ss.style.backgroundImage = "url(../web/images/check_on.jpg)";
				dd.style.backgroundImage = "url(../web/images/check_on.jpg)";
				aa.style.backgroundImage = "url(../web/images/check_on.jpg)";
				ee.style.backgroundImage = "url(../web/images/check_on.jpg)";
				ww.style.backgroundImage = "url(../web/images/check_on.jpg)";
			
			}
		}
		else
		{
			for(var i=0;i<nn;i++)
			{
				
				ss.style.backgroundImage = "url(../web/images/check_off.jpg)";
				dd.style.backgroundImage = "url(../web/images/check_off.jpg)";
				aa.style.backgroundImage = "url(../web/images/check_off.jpg)";
				ee.style.backgroundImage = "url(../web/images/check_off.jpg)";
				ww.style.backgroundImage = "url(../web/images/check_off.jpg)";
				
			}
		}
	}
   function qq()
   {
	  
	   if(j==nn)
	   {
		   xx.style.backgroundImage = "url(../web/images/check_on.jpg)";
		}
	}