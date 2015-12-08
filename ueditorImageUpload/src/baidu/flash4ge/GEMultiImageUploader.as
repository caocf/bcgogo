package baidu.flash4ge
{
	import baidu.flash4ge.event.PlusItemEvent;
	import baidu.flash4ge.item.MyItem;
	import baidu.flash4ge.item.PlusItem;
	import baidu.flash4ge.list.MyList;
	import baidu.lib.serialization.JSON;
	import baidu.mod.multiimageuploader.MultiImageUploader;
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.FileSelectorEvent;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.item.AbstractItem;
	import baidu.mod.multiimageuploader.item.Item;
	import baidu.mod.multiimageuploader.list.List;
	import baidu.mod.multiimageuploader.model.ItemVO;
	import baidu.mod.multiimageuploader.post.Post;
	import baidu.ui.controls.Button;
	
	import flash.display.Graphics;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.display.MovieClip;
	import flash.display.Shape;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.MouseEvent;
	import flash.external.ExternalInterface;
	import flash.net.FileReference;
	import flash.net.URLRequest;
	import flash.system.ApplicationDomain;
	import flash.system.Security;
	
	public class GEMultiImageUploader extends MultiImageUploader
	{
		protected var _addBtn:Button;
		protected var _gridWidth:Number;
		protected var _gridHeight:Number;
		
		protected var _allComplete:Boolean = false;
		
		public function GEMultiImageUploader(width:Number, height:Number, gridWidth:Number, gridHeight:Number)
		{
			_gridWidth = gridWidth;
			_gridHeight = gridHeight;
			
			super(width, height);
			
			if(Config.BG_URL != ""){
				getBgIcon();
			} else {
				getBtnIcon();
			}
			
			addEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_ALLCOMPLETE, function(evt:MultiImageUploaderEvent):void{
				if(_list.getAllItems().length == Config.MAX_FILE_NUM){
					return;
				}
				addPlusItem();
			});
			addEventListener(FileSelectorEvent.CANCEL_FILES, function(evt:FileSelectorEvent):void{
				_list.getAllItems().length ? addPlusItem() : addChild(_addBtn);
			});
			addEventListener(MultiImageUploaderEvent.DELETE_ALL, function(evt:MultiImageUploaderEvent):void{
				(_list as MyList).removePlusItem();
				addChild(_addBtn);
			});
		}
		
		/**
		 * 根据函数名执行函数。
		 * js通过call("upload")和call("pause")来执行开始上传和暂停上传功能。
		 */ 
		override public function execFunctionByName(funcName:String, args:Array = null):*{
			if(args){
				return (this[funcName] as Function).apply(this, args);
			} else {
				return (this[funcName] as Function).apply(this);
			}
		}
		
		/**
		 * 设置回调函数名。
		 * @param Array	回调的js函数名数组。
		 * <ul>
		 * 	<li>params[0]选择文件的回调函数名</li>
		 * 	<li>params[1]文件大小超出的回调函数名</li>
		 * 	<li>params[2]删除文件的回调函数名</li>
		 * 	<li>params[3]开始上传文件的回调函数名</li>
		 * 	<li>params[4]上传文件完成的回调函数名</li>
		 * 	<li>params[5]上传文件出错的回调函数名</li>
		 * 	<li>params[6]全部上传完成的回调函数</li>
		 * 	<li>params[7]Flash高度改变后的回调函数，可以不设置</li>
		 * </ul>
		 * @return	是否设置成功
		 */ 
		override public function setJSFuncName(params:Array):Boolean{
			if(!params || params.length < 6){
				return false;
			}
			// 设置选择文件后的回调函数名
			Config.SELECT_FILE_CALLBACK = params[0];
			// 设置文件大小超出时的回调函数名
			Config.EXCEED_FILE_CALLBACK = params[1];
			// 设置删除文件后的回调函数名
			Config.DELETE_FILE_CALLBACK = params[2];
			// 设置开始上传文件后的回调函数
			Config.START_UPLOAD_CALLBACK = params[3];
			// 设置上传文件成功的回调函数
			Config.UPLOAD_COMPLETE_CALLBACK = params[4];
			// 设置上传文件错误的回调函数
			Config.UPLOAD_ERROR_CALLLBACK = params[5];
			// 设置全部上传完成的回调函数
			Config.UPLOAD_ALL_COMPLETE_CALLBACK = params[6];
			// 设置Flash高度改变的回调函数
			Config.HEIGHT_CHANGED_CALLBACK = params[7] ? params[7] : Config.HEIGHT_CHANGED_CALLBACK;
			return true;
		}
		
		/**
		 * 开始上传
		 */ 
		override public function upload():void{
			if(_allComplete || _list.getAllItems().length == 0){
				return;
			}
			super.upload();
			if(_list.items.length != Config.MAX_FILE_NUM){
				(_list as MyList).removePlusItem();
			}
		}
		
		/**
		 * 构造fileselector
		 * 构造list和post
		 */ 
		override protected function initComponents():void{
			// 初始化list
			_list = new MyList(_width, _height - 30, _gridWidth, _gridHeight);
			addChild(_list);
			_list.x = 0;
			_list.y = 30;
			var listBg:Shape = new Shape();
			var g:Graphics = listBg.graphics;
			g.beginFill(0xffffff, 1);
			g.drawRect(0, 0, _list.width, _list.height);
			g.endFill();
			_list.background = listBg;
			_list.addEventListener(MultiImageUploaderEvent.LIST_ITEM_SELECTED, listSelected);
			_list.addEventListener(MultiImageUploaderEvent.LIST_ITEM_REVIEW_COMPLETE, serialPreview);
			_list.addEventListener(MultiImageUploaderEvent.LIST_SIZE_CHANGE, listSizeChange);
			
			// 初始化post
			_post = new Post();
			_post.addEventListener(MultiImageUploaderEvent.POST_SET_PROGRESS, changeProgress);
			_post.addEventListener(MultiImageUploaderEvent.POST_UPLOAD_FAIL, onUploadComplete);
			_post.addEventListener(MultiImageUploaderEvent.POST_UPLOAD_SUCCESS, onUploadComplete);
		}
		
		/**
		 * 初始化界面
		 */ 
		override protected function initUI():void{
			// 提示信息文字
			createInfoText();
			setInfoTextStyle();
			// 全部删除按钮
			createAllDeleteBtn();
			// 删除按钮
			createDeleteBtn();
			// 右旋转按钮
			createRotateRightBtn();
			// 左旋转按钮
			createRotateLeftBtn();
		}
		
		/**
		 * 定义Item，若要使用自己的Item，需要将你的Item继承自AbstractItem或者Item
		 * 然后重写customizeYourItem方法
		 */ 
		override protected function customizeYourItem(itemVO:ItemVO):AbstractItem{
			MyItem;
			var ItemClass:Class = ApplicationDomain.currentDomain.getDefinition("baidu.flash4ge.item.MyItem") as Class;
			var itemWidth:Number = loaderInfo.parameters.picWidth;
			var itemHeight:Number = loaderInfo.parameters.picHeight;
			
			var item:AbstractItem = new ItemClass(itemWidth, itemHeight);
			item.data = itemVO;
			// 绘制背景
			var bg:Shape = new Shape();
			var g:Graphics = bg.graphics;
			g.beginFill(0xffffff, 1);
			g.drawRect(0, 0, itemWidth, itemHeight);
			g.endFill();
			// 绘制边框
			var border:Shape = new Shape();
			g = border.graphics;
			g.lineStyle(1, 0xa7a6a4, 1);
			g.drawRect(0, 0, itemWidth, itemHeight);
			g.moveTo(0, itemHeight - 19);
			g.lineTo(itemWidth, itemHeight - 19);
			// 绘制遮罩
			var waitingMask:Shape = new Shape();
			g = waitingMask.graphics;
			g.beginFill(0, 0.7);
			g.drawRect(0, 0, itemWidth, itemHeight);
			g.endFill();
			item.waitingMask = waitingMask;
			item.background = bg;
			item.border = border;
			return item;
		}
		
		override protected function onSelectFiles(evt:FileSelectorEvent):void{
			// jsFileList回调js函数的参数
			var jsFileList:Array = [];
			// 当前已有图片数量
			var currNum:int = _list.getAllItems().length;
			
			var files:Array = evt.files;
			for(var i:int = 0, iLen:int = files.length; i < iLen; i++){
				var fr:FileReference = files[i];
				// 初始化item，item加入到preview中
				var itemVO:ItemVO = customizeYourItemVO(fr);
				var item:AbstractItem = customizeYourItem(itemVO);
				item.addEventListener(MultiImageUploaderEvent.ITEM_DELETE, delelteItem);
				_list.addItem(item);
				// 把选择的文件塞到jsFileList里
				jsFileList.push({"index":currNum + i, "name":itemVO.filename, "size":itemVO.filesize});
			}
			// 执行选择文件后的回调函数
			if(Config.SELECT_FILE_CALLBACK && ExternalInterface.available){
				ExternalInterface.call(Config.SELECT_FILE_CALLBACK, jsFileList);
			}
			
			if(_fileSelector.getAllFiles().length == 0){
				if(_uploadInfo){
					_uploadInfo.text = "";
				}
				if(_allDeleteBtn && contains(_allDeleteBtn)){
					removeChild(_allDeleteBtn);
				}
			} else {
				if(_allDeleteBtn && !contains(_allDeleteBtn)){
					addChild(_allDeleteBtn);
				}
				if(_uploadInfo){
					_uploadInfo.text = _fileSelector.getAllFiles().length + "张图片等待上传";
					_uploadInfo.width = _uploadInfo.textWidth;
					_uploadInfo.height = _uploadInfo.textHeight;
				}
				if(_allDeleteBtn && _uploadInfo){
					_allDeleteBtn.x = _uploadInfo.x + _uploadInfo.width + 5;
				}
			}
			
			serialPreview(null);
		}
		
		/**
		 * 删除item
		 */ 
		override protected function delelteItem(evt:MultiImageUploaderEvent):void{
			super.delelteItem(evt);
			
			if(_list.getAllItems().length == 0){
				(_list as MyList).removePlusItem();
				addChild(_addBtn);
			} else if( !(_list.getChildAt(_list.numChildren - 1) is PlusItem)){
				addPlusItem();
			}
			
			if(_list.getSelectedItems.length == 0){
				if(_rotateLeftBtn && _rotateLeftBtn as Button) {
					(_rotateLeftBtn as Button).enabled = false;
				}
				if(_rotateRightBtn && _rotateRightBtn as Button) {
					(_rotateRightBtn as Button).enabled = false;
				}
				if(_deleteBtn && _deleteBtn as Button) {
					(_deleteBtn as Button).enabled = false;
				}
			}
		}
		
		/**
		 * 拿到Vars里的参数
		 */ 
		override protected function initVarsParams(evt:Event):void{
			var params:Object = loaderInfo.parameters;
			Config.UPLOAD_DATAFIELD_NAME = params.uploadDataFieldName ? params.uploadDataFieldName : Config.UPLOAD_DATAFIELD_NAME;
			Config.PIC_DESC_FIELD_NAME = params.picDescFieldName ? params.picDescFieldName : Config.PIC_DESC_FIELD_NAME;
			Config.MAX_FILE_NUM = params.maxNum ? params.maxNum : Config.MAX_FILE_NUM;
			Config.COMPRESS_FILE_SIZE = params.compressSize ? params.compressSize : Config.COMPRESS_FILE_SIZE;
			Config.MAX_FILE_SIZE = params.maxSize ? params.maxSize : Config.MAX_FILE_SIZE;
			Config.COMPRESS_MAX_LENGTH = params.compressLength ? params.compressLength : Config.COMPRESS_MAX_LENGTH;
			Config.UPLOAD_URL = params.url ? params.url : Config.UPLOAD_URL;
			
			if(params["ext"]){
				Config.UPLOAD_PARAMS = baidu.lib.serialization.JSON.decode(params["ext"]);
			}
		}
		
		/**
		 * 顺序上传
		 */ 
		override protected function serialUpload():void{
			if(_list.tobeUploadedItems.length){
				if(!_isPaused){
					var item:AbstractItem = _list.tobeUploadedItems.shift() as AbstractItem;
					_currentItem = item;
					if(item && item.data){
						item.status = Config.UPLOADING;
						_post.post(item.data);
						_uploading = true;
						if(Config.START_UPLOAD_CALLBACK && ExternalInterface.available){
							ExternalInterface.call(Config.START_UPLOAD_CALLBACK, {"name":item.data.filename, "size":item.data.filesize});
						}
					}
				}
			} else {
				if(Config.UPLOAD_ALL_COMPLETE_CALLBACK && ExternalInterface.available){
					ExternalInterface.call(Config.UPLOAD_ALL_COMPLETE_CALLBACK);
					_allComplete = true;
				}
			}
		}
		
		/**
		 * 上传完成 （包括成功和失败两种情况）
		 */
		override protected function onUploadComplete(evt:MultiImageUploaderEvent):void{
			if(!_currentItem){
				return;
			}
			_currentItem.status = Config.UPLOADED;
			_uploading = false;
			
			if(evt.type == MultiImageUploaderEvent.POST_UPLOAD_FAIL){
				_currentItem.seal(false);
				if(Config.UPLOAD_ERROR_CALLLBACK && ExternalInterface.available){
					ExternalInterface.call(Config.UPLOAD_ERROR_CALLLBACK, evt.data);
				}
			} else if(evt.type == MultiImageUploaderEvent.POST_UPLOAD_SUCCESS){
				var info:String = evt.data.info as String;
				
				var tmp_arr:Array = info.split("'");
				info = tmp_arr.join("\"");
				
				var infoObj:Object = baidu.lib.serialization.JSON.decode(info);

                var conditionIsOk:Boolean = ((infoObj.state as String) && (infoObj.state as String).toUpperCase() == "SUCCESS")
                        || ((infoObj.code as int) && (infoObj.code as int).toString() == "200");

				if(conditionIsOk){
					_currentItem.seal(true);
					if(Config.UPLOAD_COMPLETE_CALLBACK && ExternalInterface.available){
						ExternalInterface.call(Config.UPLOAD_COMPLETE_CALLBACK, evt.data);
					}
				} else {
					_currentItem.seal(false);
					if(Config.UPLOAD_ERROR_CALLLBACK && ExternalInterface.available){
						ExternalInterface.call(Config.UPLOAD_ERROR_CALLLBACK, evt.data);
					}
					if((infoObj.state as String) || (infoObj.code as int)){
						var errorMsg:String = (infoObj.state as String) || (infoObj.code as int).toString();
						(_currentItem as MyItem).showErrorTip(errorMsg);
					}
				}
			}
			_currentItem = null;
			
			_uploadIdx++;
			if(_uploadInfo){
				_uploadInfo.text = "共" + _list.items.length + "张图片,上传完成" + _uploadIdx + "张";
			}
			if(_pb){
				_pb.data = _uploadIdx / _list.items.length;
			}
			
			serialUpload();
		}
		
		protected function getBgIcon():void{
			var loader:Loader = new Loader();
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onBgIconComplete);
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onBgIconError);
			loader.load(new URLRequest(Config.BG_URL));
		}
		
		protected function onBgIconComplete(evt:Event):void{
			(evt.target as LoaderInfo).removeEventListener(Event.COMPLETE, onBgIconComplete);
			(evt.target as LoaderInfo).removeEventListener(IOErrorEvent.IO_ERROR, onBgIconError);
			background = (evt.target as LoaderInfo).loader;
			getBtnIcon();
		}
		
		protected function onBgIconError(evt:IOErrorEvent):void{
			(evt.target as LoaderInfo).removeEventListener(Event.COMPLETE, onBgIconComplete);
			(evt.target as LoaderInfo).removeEventListener(IOErrorEvent.IO_ERROR, onBgIconError);
			getBtnIcon();
		}
		
		protected function getBtnIcon():void{
			if(Config.BUTTTON_URL) {
				var loader:Loader = new Loader();
				loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onButtonIconComplete);
				loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onButtonIconError);
				loader.load(new URLRequest(Config.BUTTTON_URL));
			} else {
				_addBtn = new Button();
				var addBtnSkin:BigBtn = new BigBtn();
				_addBtn.setStyle("skin", addBtnSkin);
				_addBtn.buttonMode = _addBtn.useHandCursor = true;
				_addBtn.addEventListener(MouseEvent.CLICK, onAddBtnClick);
				addChild(_addBtn);
				
				_addBtn.drawNow();
				_addBtn.x = (_width - _addBtn.width) >> 1;
				_addBtn.y = (_height - _addBtn.height) >> 1;
			}
		}
		
		protected function onButtonIconComplete(evt:Event):void{
			(evt.target as LoaderInfo).removeEventListener(Event.COMPLETE, onButtonIconComplete);
			(evt.target as LoaderInfo).removeEventListener(IOErrorEvent.IO_ERROR, onButtonIconError);
			
			_addBtn = new Button();
			var loader:Loader = (evt.target as LoaderInfo).loader;
			var addBtnSkin:MovieClip = new MovieClip();
			addBtnSkin.addChild(loader);
			_addBtn.setStyle("skin", addBtnSkin);
			_addBtn.buttonMode = _addBtn.useHandCursor = true;
			_addBtn.addEventListener(MouseEvent.CLICK, onAddBtnClick);
			addChild(_addBtn);
			
			_addBtn.drawNow();
			_addBtn.x = (_width - _addBtn.width) >> 1;
			_addBtn.y = (_height - _addBtn.height) >> 1;
		}
		
		protected function onButtonIconError(evt:IOErrorEvent):void{
			(evt.target as LoaderInfo).removeEventListener(Event.COMPLETE, onButtonIconComplete);
			(evt.target as LoaderInfo).removeEventListener(IOErrorEvent.IO_ERROR, onButtonIconError);
			
			_addBtn = new Button();
			var addBtnSkin:BigBtn = new BigBtn();
			_addBtn.setStyle("skin", addBtnSkin);
			_addBtn.buttonMode = _addBtn.useHandCursor = true;
			_addBtn.addEventListener(MouseEvent.CLICK, onAddBtnClick);
			addChild(_addBtn);
			
			_addBtn.drawNow();
			_addBtn.x = (_width - _addBtn.width) >> 1;
			_addBtn.y = (_height - _addBtn.height) >> 1;
		}
		
		/**
		 * "添加照片"按钮单击的响应函数
		 */ 
		protected function onAddBtnClick(evt:MouseEvent):void{
			removeChild(_addBtn);
			selectFiles();
		}
		
		protected function addPlusItem():void{
			var plusItem:PlusItem = new PlusItem(loaderInfo.parameters.picWidth, loaderInfo.parameters.picHeight);
			plusItem.addEventListener(PlusItemEvent.ADD_NEW_PICS, onAddNewPics);
			_list.addItem(plusItem);	
		}
		
		protected function onAddNewPics(evt:PlusItemEvent):void{
			//_list.removeItemAt(_list.items.length - 1);
			(_list as MyList).removePlusItem();
			selectFiles();
		}
	}
}