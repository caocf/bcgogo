$(document).ready(function(){
    $("#searchButton").click(function(){
          window.location="agents.do?method=getAgents&searchName="+$.trim($("#txt_shopOwner").val().toString())+"&searchAgentCode="+$.trim($("#txt_shopName").val().toString());
    });
});
