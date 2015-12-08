/**
 * 供应商评价常用js function
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-19
 * Time: 上午9:52
 * To change this template use File | Settings | File Templates.
 */

function redirectShopCommentDetail(paramShopId, toTab){
//    window.open("supplier.do?method=redirectSupplierComment&paramShopId=" + paramShopId);
    if(G.isEmpty(toTab)){
        window.open("shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=" + paramShopId);
    }else{
        window.open("shopMsgDetail.do?method=renderShopMsgDetail&shopMsgTabFlag=" + toTab + "&paramShopId=" + paramShopId);
    }
}


function scorePanelHide() {
  $(".bcgogo-scorePanel").remove();
}


function showSupplierCommentScore(object,totalAverageScore,commentRecordCount,qualityAverageScore,performanceAverageScore,speedAverageScore,attitudeAverageScore) {

  var idStr = $(object).attr("id");
  var scorePanel = new App.Module.ScorePanel();
  var panelWidth = 300;
  if(totalAverageScore == 0){
    panelWidth = 70;
  }

  scorePanel.show({
    selector:"#" + idStr,
    config:{
      avgLabelWidth:100,
      subLabelWidth:100,
      width:panelWidth,
      height:120,
      'z-index':10
    },
    avgScore:{
      value:totalAverageScore,
      htmlLabel:"总分：",
      amount:commentRecordCount,
      noScoreHtmlText:"暂无评分"
    },
    subScore:[
      {
        value:qualityAverageScore,
        htmlLabel:"货品质量："
      },
      {
        value:performanceAverageScore,
        htmlLabel:"货品性价比："
      },
      {
        value:speedAverageScore,
        htmlLabel:"发货速度："
      },
      {
        value:attitudeAverageScore,
        htmlLabel:"服务态度："
      }
    ]
  });

  scorePanel.getJqDom().css("position", "absolute");

}
