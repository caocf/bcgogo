
$(function(){
  $("#editConstruction_project").combobox({
    url:'cameraRecord.do?method=setCategoryPage',
    method:'post',
    valueField:'name',
    textField:'name',
    mode:'remote',
    multiple:true

  });
});









