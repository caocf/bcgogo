Ext.define('Ext.view.productMaintenance.CategorySelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.categorySelect',
    emptyText:'',
    editable:false,
    store:Ext.create('Ext.store.productMaintenance.CategoryTypes'),
    queryMode:'local',
    displayField:'label',
    valueField:'value',

    listeners:{
      select:function(combo, record, index) {
        if(combo.getValue() =="FIRST_CATEGORY"){
         Ext.getCmp("firstCategoryName").show();
         Ext.getCmp("thirdCategoryName").hide();
         Ext.getCmp("secondCategoryName").hide();
         Ext.getCmp("categoryType").setValue("FIRST_CATEGORY");
        }else if(combo.getValue() =="SECOND_CATEGORY"){
         Ext.getCmp("firstCategoryName").show();
         Ext.getCmp("secondCategoryName").show();
         Ext.getCmp("thirdCategoryName").hide();
         Ext.getCmp("categoryType").setValue("SECOND_CATEGORY");

        }else if(combo.getValue() =="THIRD_CATEGORY"){
         Ext.getCmp("firstCategoryName").show();
         Ext.getCmp("secondCategoryName").show();
         Ext.getCmp("thirdCategoryName").show();
         Ext.getCmp("categoryType").setValue("THIRD_CATEGORY");
        }
      }
    }


//    getDisplayName:function (value) {
//        if (value == "FIRST_CATEGORY") return "一级分类(系统类别)";
//        else if (value == "SECOND_CATEGORY") return "二级分类(种类)";
//        else if (value == "THIRD_CATEGORY") return "三级分类(品名)";
//    }
});