(function(){

$().ready( function(){

	//车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
	$("#vehicleNumber")[0].onfocus = function()
	{
		if ($("#vehicleNumber")[0].value == "车牌号")
		{
			$("#vehicleNumber")[0].value = "";
			$("#vehicleNumber")[0].style.color = "#000000";
		}
		else
		{
			$("#vehicleNumber")[0].style.color = "#000000";
		}
	}
	$("#vehicleNumber")[0].onblur = function()
	{
		if (!$("#vehicleNumber")[0].value || $("#vehicleNumber")[0].value == "车牌号")
		{
			$("#vehicleNumber")[0].style.color = "#999999";
			$("#vehicleNumber")[0].value = "车牌号"
		}
	}

	$("#input_search_Name")[0].onfocus = function()
	{
		if ($("#input_search_Name")[0].value == "单位/联系人/手机号")
		{
			$("#input_search_Name")[0].value = "";
			$("#input_search_Name")[0].style.color = "#000000";
		}
		else
		{
			$("#input_search_Name")[0].style.color = "#000000";
		}
	}
	$("#input_search_Name")[0].onblur = function()
	{
		if (!$("#input_search_Name")[0].value || $("#input_search_Name")[0].value == "单位/联系人/手机号")
		{
			$("#input_search_Name")[0].style.color = "#999999";
			$("#input_search_Name")[0].value = "单位/联系人/手机号"
		}
	}

	$("#input_search_pName")[0].onfocus = function()
	{
		if ($("#input_search_pName")[0].value == "用品配件品名（简写缩写）")
		{
			$("#input_search_pName")[0].value = "";
			$("#input_search_pName")[0].style.color = "#000000";
		}
		else
		{
			$("#input_search_pName")[0].style.color = "#000000";
		}
	}
	$("#input_search_pName")[0].onblur = function()
	{
		if (!$("#input_search_pName")[0].value || $("#input_search_pName")[0].value == "用品配件品名（简写缩写）")
		{
			$("#input_search_pName")[0].style.color = "#999999";
			$("#input_search_pName")[0].value = "用品配件品名（简写缩写）"
		}
	}
	
	$("#multipleMoney")[0].onfocus = function()
	{
		if ($("#multipleMoney")[0].value == "50的倍数")
		{
			$("#multipleMoney")[0].value = "";
			$("#multipleMoney")[0].style.color = "#A16F3E";
		}
		else
		{
			$("#multipleMoney")[0].style.color = "#A16F3E";
		}
	}	
	$("#multipleMoney")[0].onblur = function()
	{
		if (!$("#multipleMoney")[0].value || $("#multipleMoney")[0].value == "50的倍数")
		{
			$("#multipleMoney")[0].style.color = "#D0B69F";
			$("#multipleMoney")[0].value = "50的倍数"
		}
	}	



	if ($("#a_name1")[0])
	{
		$("#a_name1"[0]).onmouseover = function()
		{
			if ($("#a_name1")[0]) $("#a_name1")[0].className = "hover";
			if ($("#a_name2")[0]) $("#a_name2")[0].className = "";
			if ($("#a_name3")[0]) $("#a_name3")[0].className = "";
			if ($("#a_name4")[0]) $("#a_name4")[0].className = "";
			if ($("#div_name")[0]) $("#div_name")[0].style.display = "none";
		}
	}

	if ($("#a_name2")[0])
	{
		$("#a_name2")[0].onmouseover = function()
		{
			if ($("#a_name1")[0]) $("#a_name1")[0].className = "";
			if ($("#a_name2")[0]) $("#a_name2")[0].className = "hover";
			if ($("#a_name3")[0]) $("#a_name3")[0].className = "";
			if ($("#a_name4")[0]) $("#a_name4")[0].className = "";
			if ($("#div_name")[0]) $("#div_name")[0].style.display = "block";
		}
	}

	if ($("#a_name3")[0])
	{
		$("#a_name3")[0].onmouseover = function()
		{
			if ($("#a_name1")[0]) $("#a_name1")[0].className = "";
			if ($("#a_name2")[0]) $("#a_name2")[0].className = "";
			if ($("#a_name3")[0]) $("#a_name3")[0].className = "hover";
			if ($("#a_name4")[0]) $("#a_name4")[0].className = "";
			if ($("#div_name")[0]) $("#div_name")[0].style.display = "block";
		}
	}

	if ($("#a_name4")[0])
	{
		$("#a_name4")[0].onmouseover = function()
		{
			if ($("#a_name1")[0]) $("#a_name1")[0].className = "";
			if ($("#a_name2")[0]) $("#a_name2")[0].className = "";
			if ($("#a_name3")[0]) $("#a_name3")[0].className = "";
			if ($("#a_name4")[0]) $("#a_name4")[0].className = "hover";
			if ($("#div_name")[0]) $("#div_name")[0].style.display = "block";
		}
	}
	
	$("#txt_shopName")[0].onfocus = function()
	{
		if ($("#txt_shopName")[0].value == "店铺名")
		{
			$("#txt_shopName")[0].value = "";
			$("#txt_shopName")[0].style.color = "#000000";
		}
		else
		{
			$("#txt_shopName")[0].style.color = "#000000";
		}
	}	
	$("#txt_shopName")[0].onblur = function()
	{
		if (!$("#txt_shopName")[0].value || $("#txt_shopName")[0].value == "店铺名")
		{
			$("#txt_shopName")[0].style.color = "#999999";
			$("#txt_shopName")[0].value = "店铺名"
		}
	}	
	$("#txt_shopOwner")[0].onfocus = function()
	{
		if ($("#txt_shopOwner")[0].value == "店主")
		{
			$("#txt_shopOwner")[0].value = "";
			$("#txt_shopOwner")[0].style.color = "#000000";
		}
		else
		{
			$("#txt_shopOwner")[0].style.color = "#000000";
		}
	}	
	$("#txt_shopOwner")[0].onblur = function()
	{
		if (!$("#txt_shopOwner")[0].value || $("#txt_shopOwner")[0].value == "店主")
		{
			$("#txt_shopOwner")[0].style.color = "#999999";
			$("#txt_shopOwner")[0].value = "店主"
		}
	}	
	$("#txt_address")[0].onfocus = function()
	{
		if ($("#txt_address")[0].value == "地址")
		{
			$("#txt_address")[0].value = "";
			$("#txt_address")[0].style.color = "#000000";
		}
		else
		{
			$("#txt_address")[0].style.color = "#000000";
		}
	}	
	$("#txt_address")[0].onblur = function()
	{
		if (!$("#txt_address")[0].value || $("#txt_address")[0].value == "地址")
		{
			$("#txt_address")[0].style.color = "#999999";
			$("#txt_address")[0].value = "地址"
		}
	}	
	$("#txt_phone")[0].onfocus = function()
	{
		if ($("#txt_phone")[0].value == "手机/电话")
		{
			$("#txt_phone")[0].value = "";
			$("#txt_phone")[0].style.color = "#000000";
		}
		else
		{
			$("#txt_phone")[0].style.color = "#000000";
		}
	}	
	$("#txt_phone")[0].onblur = function()
	{
		if (!$("#txt_phone")[0].value || $("#txt_phone")[0].value == "手机/电话")
		{
			$("#txt_phone")[0].style.color = "#999999";
			$("#txt_phone")[0].value = "手机/电话"
		}
	}
	
	
});

})();