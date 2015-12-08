Ext.define('Ext.view.productMaintenance.FirstProductCategorySelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.firstProductCategorySelect',
    emptyText:'所有分类',
    store:Ext.create('Ext.store.productMaintenance.FirstProductCategories'),
    displayField:'text',
    valueField:'text',
    remoteFilter:true, //ajax过滤开关
    queryMode:'remote', //远程过滤
    queryParam:'name', //过滤字
    queryDelay:500, //延迟
    minChars:1, //The minimum number of characters the user must type before autocomplete defaults to 4 if queryMode = 'remote'

    listeners:{
      beforeselect :function(combo, record, index) {

        var secondValue = Ext.getCmp("secondCategoryName").getValue();

        var firstSelect = Ext.getCmp("firstCategoryName");

        if (Ext.getCmp("formType").getValue() == "update") {
          var secondSelect = Ext.getCmp("secondCategoryName");
          secondSelect.clearValue();
          secondSelect.store.proxy.extraParams = {
            parameter : firstSelect.getValue()
          };
          secondSelect.store.load();
        }
        if (secondValue != null && secondValue != "") {
          Ext.getCmp("secondCategoryName").setValue(secondValue);
        }
      },
      select :function(combo, record, index) {
        var secondValue = Ext.getCmp("secondCategoryName").getValue();
        var firstSelect = Ext.getCmp("firstCategoryName");
        var secondSelect = Ext.getCmp("secondCategoryName");
        secondSelect.clearValue();
        secondSelect.store.proxy.extraParams = {
          parameter : firstSelect.getValue()
        };
        secondSelect.store.load();
        if (secondValue != null && secondValue != "") {
          Ext.getCmp("secondCategoryName").setValue(secondValue);
        }
      }
    },



    enableKeyEvents:true
});