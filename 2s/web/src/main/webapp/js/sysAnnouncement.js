function initAnnouncements(jsonStr){
  if(stringUtil.isEmpty(jsonStr)){
    return;
  }
  var titleList=jsonStr[0].titleList;
  if(stringUtil.isEmpty(titleList)){
    return;
  }
  $(".sysList ").attr("announcementId","");
  $(".sysList ").attr("releaseDate","");
  $(".sysList").text("");
  var count=1;
  for(var key in titleList){
    $(".sysList"+count).attr("announcementId",key);
    $(".sysList"+count).text(titleList[key][0]);
    $(".sysList"+count).attr("releaseDate",titleList[key][1]);
    $(".sysList"+count).append('<span class="sysTime">'+'('+titleList[key][1]+')'+'</span>');
    count++;
  }

  $(".sysList").click(function(){
    var announcementId=$(this).attr("announcementId");
    if(stringUtil.isEmpty(announcementId)){
      return;
    }
    var data={
      announcementId:announcementId
    }
    $.ajax({
      url:'sysReminder.do?method=getAnnouncementById',
      data:data,
      type: "POST",
      dataType:"json",
      cache:false,
      success:function(announcement){
        if(stringUtil.isEmpty(announcement)) return;
        $("#nTitle").text("");
        $("#nReleaseDate").text("");
        $("#nContent").text("");
        $("#nReleaseDate").text("");
        $("#nTitle").append(announcement.title+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
        $("#nContent").append(announcement.content);
        $("#nReleaseDate").append('('+announcement.releaseDate+')');
      }
    });

  });
}