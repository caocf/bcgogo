Ext.define('Ext.controller.productMaintenance.ProductMainController', {
    extend:'Ext.app.Controller',

    stores:[
        "Ext.store.productMaintenance.ProductCategoryDetails",
        "Ext.store.productMaintenance.ProductCategories"
    ],

    models:[
        "Ext.model.productMaintenance.ProductCategoryDetail",
        "Ext.model.productMaintenance.ProductCategory"
    ],

    views:[
        'Ext.view.productMaintenance.ProductCategoryMainView'
    ],

    requires:[
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.view.productMaintenance.updateProductCategory",
        "Ext.view.productMaintenance.AddCategoryView",
        "Ext.utils.Common"
    ],

    refs:[
        //树菜单
        {
            ref:'productCategoryTreeMenu',
            selector:'productCategoryTreeMenu',
            xtype:'productCategoryTreeMenu',
            autoCreate:true
        },
        {ref:'productCategoryResultView', selector:'productCategoryResultView'},
        {ref:'updateProductCategoryForm', selector:'updateProductCategoryWin productCategoryForm'},

        {ref:'addCategoryForm', selector:'addCategoryView addCategoryForm'},

        {
            ref:'addCategoryView',
            selector:'addCategoryView',
            autoCreate:true,
            xtype:'addCategoryView'
        },
        {ref:'categorySelect', selector:'addCategoryView addCategoryForm categorySelect'},

        {
            ref:'updateProductCategoryWin',
            selector:'updateProductCategoryWin',
            autoCreate:true,
            xtype:'updateProductCategoryWin'
        },

        {ref:'SecondProductCategorySelect', selector:'updateProductCategoryWin productCategoryForm secondProductCategorySelect'} ,

        {ref:'firstProductCategorySelect', selector:'updateProductCategoryWin productCategoryForm firstProductCategorySelect'} ,
        {ref:'SecondProductCategorySelect', selector:'updateProductCategoryWin productCategoryForm secondProductCategorySelect'} ,
        {ref:'productCategoryTree', selector:'productCategoryTree'}
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
           // 模块 tree
            "productCategoryMainView productCategoryTree":{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_category_search")) {
                        me.getProductCategoryTree().hide();
                    }
                },
                itemcontextmenu:me.treeMenu,
                edit:me.updateProductCategory,
                itemclick:function (view, record, item, rowIndex, e) {
                    var contextMenu = this.getProductCategoryTreeMenu();
                    if (record.data.id != -1) {
                      contextMenu.setProductCategory(record);
                      me.showProductCategoryDetails(record.get("id"),record.get("type"),null)
                    }else{
                      me.showProductCategoryDetails(record.get("id"),record.get("type"),null)
                    }
                    var listTree = this.getProductCategoryTree(),
                        selectionModel = listTree.getSelectionModel(),
                        selectedList = selectionModel.getSelection()[0],
                        parentList = selectedList.isLeaf() ? selectedList.parentNode : selectedList;
                    parentList.expand();

                }
            },

            //右击菜单新增分类
            "[id=addProductCategory]":{
                click:me.addProductCategory
            },
            //右击菜单编辑
            "[id=editProductCategory]":{
                click:me.productCategoryEdit
            },

            //每条数据编辑
            "productCategoryResultView actioncolumn":{

              beforerender:function (view, eOpts) {
                if (!me.permissionUtils.hasPermission("CRM_product_category_update")) {
                   view.hide();
                }
              },
              click:function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    var index = me.componentUtils.getActionColumnItemsIndex(e);
                        me.updateCategory(grid, row, col);
                }
            },

            //新增分类
            "productCategoryResultView button[action=addCategory]":{
              beforerender:function (view, eOpts) {
                if (!me.permissionUtils.hasPermission("CRM_product_category_add")) {
                   view.hide();
                }
              },
              click:function () {
                    me.commonUtils.mask();
                    me.getAddCategoryView().show();
              }
            },

            //新增分类页面的save按钮
            'addCategoryView addCategoryForm button[action=addCategoryForm]':{
                click:me.saveNewProductCategory
            },

            //更新分类页面的save按钮
            'updateProductCategoryWin productCategoryForm button[action=updateCategoryForm]':{
                click:me.productCategoryUpdate
            },
            'productCategoryForm firstProductCategorySelect':{
              select:function (combo, records, eOpts) {
  //                    combo.up("form").form.getRecord().data.userGroupName = records[0].data.name;
              }
            },
            'productCategoryForm secondProductCategorySelect':{
              click:function (combo, records, eOpts) {
  //                    combo.up("form").form.getRecord().data.userGroupName = records[0].data.name;
              }
            },
            //搜索
            "productCategoryResultView button[action=searchCategory]":{
              beforerender:function (view, eOpts) {
                if (!me.permissionUtils.hasPermission("CRM_product_category_search")) {
                  Ext.getCmp('productCategoryName').hide();
                  view.hide();
                }
              },
              click:function (record) {
                var me = this;
                me.showProductCategoryDetails(null, null, Ext.getCmp('productCategoryName').getValue())
              }
            },

            //刷新
            "[id=refreshProductCategoryTree]":{
                click:function () {
                    me.getProductCategoryTree().store.load();
                }
            }
        });
    },


     'updateProductCategoryWin productCategoryForm productCategoryTreePicker':{
                select:function (picker, record, eOpts) {
                }
     },


    updateCategory:function (grid, rowIndex, colIndex) {
        var me = this,
            rec = grid.getStore().getAt(rowIndex),
            win = me.getUpdateProductCategoryWin();
        me.commonUtils.mask();
        win.down("form").loadRecord(rec);

        if (rec.raw.categoryType == "FIRST_CATEGORY") {
          win.down("form").down("[name='thirdCategoryName']").hide();
          win.down("form").down("[name='secondCategoryName']").hide();
          win.down("form").down("[name='categoryType']").setValue("FIRST_CATEGORY");
        }else if(rec.raw.categoryType == "SECOND_CATEGORY"){
          win.down("form").down("[name='thirdCategoryName']").hide();
          win.down("form").down("[name='categoryType']").setValue("SECOND_CATEGORY");
        }else if(rec.raw.categoryType == "THIRD_CATEGORY"){
           win.down("form").down("[name='categoryType']").setValue("THIRD_CATEGORY");
        }
        me.commonUtils.unmask();
        win.show();

    },

    saveNewProductCategory:function (view, keyCode) {
      var me = this,
          form = me.getAddCategoryForm(),
          formEl = form.getEl(),
          thisWin = me.getAddCategoryView(),
          baseForm = form.form;
      if (baseForm.isValid()) {
        formEl.mask('正在保存 . . .');
        me.commonUtils.ajax({
          url:'productCategory.do?method=addProductCategory',
          params:baseForm.getFieldValues(),
          success:function (result) {
            if (result.success) {
              Ext.Msg.alert('返回结果', result.message, function () {
                thisWin.close();
                formEl.unmask();
              });
              me.getProductCategoryTree().store.load();
              me.getProductCategoryResultView().store.load();
            } else {
              Ext.Msg.alert('返回结果', result.message, function () {
                formEl.unmask();
              });
            }
          },
          failure:function (response) {
            formEl.unmask();
          }
        });
      } else {
        Ext.Msg.alert('警告', "输入有误", function () {
        });
      }
    },

    productCategoryUpdate:function (view, keyCode) {
        var me = this,
            form = me.getUpdateProductCategoryForm(),
            formEl = form.getEl(),
            thisWin = me.getUpdateProductCategoryWin(),
            baseForm = form.form;
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');
            me.commonUtils.ajax({
                url:'productCategory.do?method=updateCategoryForm',
                params:baseForm.getFieldValues(),
                success:function (result) {
                  if(result.success){
                    Ext.Msg.alert('返回结果', result.message, function () {
                        formEl.unmask();
                        thisWin.close();
                        me.getProductCategoryTree().store.load();
                        me.getProductCategoryResultView().store.load();
                    });
                  }else{
                    Ext.Msg.alert('返回结果', result.message, function () {
                       formEl.unmask();
                    });
                  }
                },
                failure:function (response) {
                    formEl.unmask();
                }
            });
        }else {
          Ext.Msg.alert('警告', "输入有误", function () {
          });
        }
    },

    showProductCategoryDetails:function (id,type,name) {

//      if (( id == null || id == "") && (name == null || name == "" )) {
//        return
//      }
      var me = this;
        me.getProductCategoryResultView().store.proxy.extraParams = {
            productCategoryId:id,
            productCategoryType:type,
            productCategoryName:name
        };
        me.getProductCategoryResultView().store.loadPage(1);
    },

    addProductCategory:function (component, e) {
        var record = e.record;
        var textContent;
        var type;
        if(component.ownerCt.getProductCategory().data.type =="FIRST_CATEGORY"){
          textContent ="新二级分类";
          type ="SECOND_CATEGORY";
        }else if(component.ownerCt.getProductCategory().data.type =="SECOND_CATEGORY"){
          textContent ="新三级分类";
          type ="THIRD_CATEGORY";
        }else if(component.ownerCt.getProductCategory().data.type =="TOP_CATEGORY"){
          textContent ="新一级分类";
          type ="FIRST_CATEGORY";
        }else{
          return;
        }
        var newProductCategory = Ext.create('Ext.model.productMaintenance.ProductCategory', {
            text:textContent,
            leaf:false,
            type:type,
            iconCls:'icon-hr',
            parentId:component.ownerCt.getProductCategory().getId("id"),
            loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
        });
        var listTree = this.getProductCategoryTree(),
            cellEditingPlugin = listTree.cellEditingPlugin,
            selectionModel = listTree.getSelectionModel(),
            selectedList = selectionModel.getSelection()[0],
            parentList = selectedList.isLeaf() ? selectedList.parentNode : selectedList,
            expandAndEdit = function () {
                if (parentList.isExpanded()) {
                    selectionModel.select(newProductCategory);
                    cellEditingPlugin.startEdit(newProductCategory, 0);
                } else {
                    listTree.on('afteritemexpand', function startEdit(list) {
                        if (list === parentList) {
                            selectionModel.select(newProductCategory);
                            cellEditingPlugin.startEdit(newProductCategory, 0);
                            // remove the afterexpand event listener
                            listTree.un('afteritemexpand', startEdit);
                        }
                    });
                    parentList.expand();
                }
            };

        parentList.appendChild(newProductCategory);
        if (listTree.getView().isVisible(true)) {
            expandAndEdit();
        } else {
            listTree.on('expand', function onExpand() {
                expandAndEdit();
                listTree.un('expand', onExpand);
            });
            listTree.expand();
        }
    },

    removeAllChildren:function (store) {
        store.getRootNode().removeAll();
    },


    updateProductCategory:function (editor, e, eOpts) {
      var me = this, url,
          rec = e.record, message,
          listTree = me.getProductCategoryTree();
      url = 'productCategory.do?method=updateProductCategory';
      message = "分类重复！";
      me.commonUtils.ajax({
        url:url,
        params:{
          id:rec.get("id"),
          parentId:rec.get("parentId"),
          text:rec.get("text"),
          type:rec.get("type")

        },
        success:function (result) {
          if (result.duplicate) {
            Ext.Msg.alert('警告', result.message, function () {
              rec.set("text", rec.get("text") + "-新");
                me.productCategoryEdit(Ext.getCmp("editProductCategory"), e);
            });
          } else if(result.success) {
            rec.set("id", result.node.id);
            rec.set("name", result.node.name);
            rec.set("sort", result.node.sort);
            rec.commit();
            listTree.store.sort('sort', 'ASC');
          }else{
            Ext.Msg.alert('警告', "操作失败", function () {
              rec.set("text", rec.get("text"));
                me.productCategoryEdit(Ext.getCmp("editProductCategory"), e);
            });
          }
          me.getProductCategoryTree().store.load();
        }
      });
    },

    productCategoryEdit:function (component, e) {
        var listTree = this.getProductCategoryTree(),
            cellEditingPlugin = listTree.cellEditingPlugin;
        cellEditingPlugin.startEdit(component.ownerCt.getProductCategory(), 0);
    },

    treeMenu:function (view, record, item, rowIndex, e) {
        var contextMenu = this.getProductCategoryTreeMenu(),
            addProductCategory = contextMenu.down("#addProductCategory"),
            editProductCategory = contextMenu.down("#editProductCategory");
        //如果是根节点 隐藏 添加 部门
        if (record.get("id") == -1) {
            record.set("type","TOP_CATEGORY");
            addProductCategory.show();
            editProductCategory.hide();
        } else if(record.get("type") == "SECOND_CATEGORY"){
            addProductCategory.hide();
            editProductCategory.show();
        }else{
            addProductCategory.show();
            editProductCategory.show();
        }

        if (!this.permissionUtils.hasPermission("CRM_product_category_update")) {
          editProductCategory.hide();
        }
        if (!this.permissionUtils.hasPermission("CRM_product_category_add")) {
           addProductCategory.hide();
        }

        contextMenu.setProductCategory(record);
        contextMenu.showAt(e.getX(), e.getY());
        e.preventDefault();
    }
});