
(function(){
    $().ready( function(){
		var myDate = new Date();

		var year = myDate.getFullYear();
		var month = myDate.getMonth();
		var date = myDate.getDate();
		var day = myDate.getDay();
		var hour = myDate.getHours();
		var minute = myDate.getMinutes();		

		//今天
		$("#a_today")[0].onclick = function(){
			$("#endDateStr")[0].value = year + "-" + ((month + 1) > 9 ? (month + 1) : ("0" + (month + 1))) + "-" + (date > 9 ? date : ("0" + date)) + " 00:00";
			$("#endDateStr2")[0].value = year + "-" + ((month + 1) > 9 ? (month + 1) : ("0" + (month + 1))) + "-" + (date > 9 ? date : ("0" + date)) + " 23:59";
		}

		//本周
		$("#a_week")[0].onclick = function(){

			var datestartstr;
			var dateendstr;
			var myDate2;
			var myDate3;

			if (day == 0)
			{
				myDate2 = new Date((myDate/1000 + 86400)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 7 *86400)*1000);
			    dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + myDate3.getDate() + " 00:00";
			}
			else if (day == 1)
			{
				myDate2 = new Date((myDate/1000)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 6 *86400)*1000);
				dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + (myDate3.getDate() > 9 ? myDate3.getDate() : ("0" + myDate3.getDate())) + " 23:59";
			}
			else if (day == 2)
			{
				myDate2 = new Date((myDate/1000 - 86400)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 5 *86400)*1000);
				dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + (myDate3.getDate() > 9 ? myDate3.getDate() : ("0" + myDate3.getDate())) + " 23:59";
			}
			else if (day == 3)
			{
				myDate2 = new Date((myDate/1000 - 2*86400)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 4 *86400)*1000);
				dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + (myDate3.getDate() > 9 ? myDate3.getDate() : ("0" + myDate3.getDate())) + " 23:59";
			}
			else if (day == 4)
			{
				myDate2 = new Date((myDate/1000 - 3*86400)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 3 *86400)*1000);
				dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + (myDate3.getDate() > 9 ? myDate3.getDate() : ("0" + myDate3.getDate())) + " 23:59";
			}
			else if (day == 5)
			{
				myDate2 = new Date((myDate/1000 - 4*86400)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 2 *86400)*1000);
				dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + (myDate3.getDate() > 9 ? myDate3.getDate() : ("0" + myDate3.getDate())) + " 23:59";
			}
			else if (day == 6)
			{
				myDate2 = new Date((myDate/1000 - 5*86400)*1000);
				datestartstr = myDate2.getFullYear() + "-" + ((myDate2.getMonth() + 1) > 9 ? (myDate2.getMonth() + 1) : ("0" + (myDate2.getMonth() + 1))) + "-" + (myDate2.getDate() > 9 ? myDate2.getDate() : ("0" + myDate2.getDate())) + " 00:00";
				myDate3 = new Date((myDate/1000 + 1 *86400)*1000);
				dateendstr = myDate3.getFullYear() + "-" + ((myDate3.getMonth() + 1) > 9 ? (myDate3.getMonth() + 1) : ("0" + (myDate3.getMonth() + 1))) + "-" + (myDate3.getDate() > 9 ? myDate3.getDate() : ("0" + myDate3.getDate())) + " 23:59";
			}
			$("#endDateStr")[0].value = datestartstr;
			$("#endDateStr2")[0].value = dateendstr;
			
		}

		//本月
		$("#a_month")[0].onclick = function(){
			var data2 = new Date(year, (month + 1), 0).getDate();

			$("#endDateStr")[0].value = year + "-" + ((month + 1) > 9 ? (month + 1) : ("0" + (month + 1))) + "-01 00:00";
			$("#endDateStr2")[0].value = year + "-" + ((month + 1) > 9 ? (month + 1) : ("0" + (month + 1))) + "-" + data2 + " 23:59";

		}

		//今年
		$("#a_year")[0].onclick = function(){
			$("#endDateStr")[0].value = year + "-01-01 00:00";
			$("#endDateStr2")[0].value = year + "-12-31 23:59";
		
		}
		
	});

})();