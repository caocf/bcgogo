Ext.define('Ext.view.productMaintenance.ProductCategoryTreeMenu', {
  extend:'Ext.menu.Menu',
  xtype:'productCategoryTreeMenu',
  items:[
    {
      iconCls:'tasks-new-list',
      id:'addProductCategory',
      text:'增加子分类'
    },
    {
      id:'editProductCategory',
      text:'编辑'
    },
    {
      text:'刷新',
      id:'refreshProductCategoryTree'
    }
  ],

  setProductCategory:function (productCategory) {
    this.productCategory = productCategory;
  },

  getProductCategory:function () {
    return this.productCategory;
  }
});
