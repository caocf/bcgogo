/**
 * 所有控制dom元素的都采用class命名约束
 * 来料待修:incoming_materials
 * 缺料待修:lack_materials
 */
var privilegeVerifier = {
  //来料待修
  repairOrderPermission:function(repairOrderPermission) {
    if (repairOrderPermission == "false") {
      removeAttr(".incoming_materials");
    }
  },
  //lack_materials
  goodsStoragePermission:function(goodsStoragePermission) {
    if (goodsStoragePermission == "false") {
      //缺料待修
      removeAttr(".lack_materials");
      jQuery(".lack_materials").css({'cursor':'pointer'});
      jQuery(".lack_materials").click(function() {
        showMessage.fadeMessage("35%", "", "slow", 3000, "请通知仓管入库！");
      });
    }
  }
}
function removeAttr(id) {
  jQuery(id).removeAttr("href");
}