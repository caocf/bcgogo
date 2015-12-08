package baidu.mod.multiimageuploader
{
	import baidu.lib.serialization.JSON;
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.FileSelectorEvent;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.file.FileSelector;
	import baidu.mod.multiimageuploader.item.AbstractItem;
	import baidu.mod.multiimageuploader.item.Item;
	import baidu.mod.multiimageuploader.list.List;
	import baidu.mod.multiimageuploader.model.ItemVO;
	import baidu.mod.multiimageuploader.post.Post;
	import baidu.mod.multiimageuploader.ui.ProgressBar;
	import baidu.ui.controls.Button;
	
	import flash.display.DisplayObject;
	import flash.display.GradientType;
	import flash.display.Graphics;
	import flash.display.Shader;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.external.ExternalInterface;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.system.ApplicationDomain;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	public class MultiImageUploader extends Sprite
	{
		protected var _list:List;
		protected var _post:Post;
		protected var _pb:ProgressBar;
		
		protected var _uploadInfo:TextField;
		protected var _rotateLeftBtn:Sprite;
		protected var _rotateRightBtn:Sprite;
		protected var _deleteBtn:Sprite;
		protected var _allDeleteBtn:Sprite;
		
		protected var _background:DisplayObject;
		protected var _border:DisplayObject;
		
		protected var _width:Number;
		protected var _height:Number;
		
		protected var _fileSelector:FileSelector;
		
		protected var _uploadIdx:int = 0;
		
		// 当前正在处理(预览/上传)的item
		protected var _currentItem:AbstractItem;
		
		// 是否开始上传
		protected var _hasStartedUpload:Boolean = false;
		protected var _isPaused:Boolean = false;
		protected var _uploading:Boolean = false;

		protected var jsFlashInit:String = "flashInit";
		protected var jsSetJSFuncName:String = "setJSFuncName";
		
		private var info:* = null;
		
		////////////// SET/GET方法 //////////////
		public function set border(value:DisplayObject):void{
			if(value){
				if(_border && contains(_border)){
					removeChild(_border);
					_border = null;
				}
				
				_border = value;
				_border.width = _width;
				_border.height = _height;
				addChild(_border);
			}
		}
		
		public function set background(value:DisplayObject):void{
			if(value){
				if(_background && contains(_background)){
					removeChild(_background);
					_background = null;
				}
				
				_background = value;
				_background.width = _width;
				_background.height = _height;
				addChildAt(_background, 0);
			}
		}
		
		public function get uploading():Boolean{
			return _uploading;
		}
		////////////// SET/GET方法 //////////////
		
		public function MultiImageUploader(width:Number, height:Number)
		{
			// 拿到flashvars的参数
			addEventListener(Event.ADDED_TO_STAGE, initVarsParams);
			
			_width = width;
			_height = height;
			
			initUI();
			
			attachUIEvent();
			
			initComponents();
			
			initFileSelector();
			
			ExternalInterface.addCallback("call", execFunctionByName);
			ExternalInterface.addCallback(jsFlashInit, flashInit);
			ExternalInterface.addCallback(jsSetJSFuncName, setJSFuncName);
		}
		
		public function execFunctionByName(funcName:String, args:Array = null):*{
			if(funcName == "upload"){
				ExternalInterface.call("console.log", "bugg!!");
			}
			if(args){
				return (this[funcName] as Function).apply(this, args);
			} else {
				return (this[funcName] as Function).apply(this);
			}
		}
		
		/**
		 * 开始上传
		 */ 
		public function upload():void{
			if(_isPaused){
				_isPaused = false;
				if(_uploading){
					return;
				} 
			}
			
			_hasStartedUpload = true;
			_list.cancelAllSelectedItems();
			
			var items:Array = _list.tobeUploadedItems;
			for(var i:int = 0, iLen:int = items.length; i < iLen; i++) {
				var item:AbstractItem = items[i];
				item.waiting = true;
			}
			
			if(_uploadInfo){
				_uploadInfo.text = "共" + _list.items.length + "张图片,上传完成" + _uploadIdx + "张";
			}
			if(_pb){
				var top:Shape = new Shape();
				var g:Graphics = top.graphics;
				g.beginGradientFill(GradientType.LINEAR, [0x2b72b0, 0x2b72ee], [1, 1], [0, 255]);
				g.drawRect(0, 0, 133, 10);
				g.endFill();
				
				var bottom:Shape = new Shape();
				g = bottom.graphics;
				g.beginFill(0xd3d3d3, 1);
				g.drawRect(0, 0, 137, 14);
				g.endFill();
				
				_pb.top = top;
				_pb.bottom = bottom;
				_pb.x = _uploadInfo.x + _uploadInfo.width + 5;
				_pb.y = _uploadInfo.y;
				addChild(_pb);
			}
			
			if(_allDeleteBtn && contains(_allDeleteBtn)){
				removeChild(_allDeleteBtn);
			}
			
			serialUpload();
		}
		
		/**
		 * 暂停上传
		 */ 
		public function pause():void{
			_isPaused = true;
		}
		
		/**
		 * 
		 */
		public function flashInit():Boolean{
			return true;
		}
		
		public function setJSFuncName(params:Array):Boolean{
			if(!params || params.length < 7){
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
			return true;
		}
		
		/**
		 * 初始化界面
		 */ 
		protected function initUI():void{
			_pb = new ProgressBar(137, 14);
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
		 * 构造文本提示信息
		 */ 
		protected function createInfoText():void{
			_uploadInfo = new TextField();
			addChild(_uploadInfo);
			_uploadInfo.text = " ";
		}
		
		/**
		 * 设置文本提示信息样式和位置
		 */
		protected function setInfoTextStyle():void{
			var tf:TextFormat = new TextFormat("宋体", 12, 0, true);
			if(_uploadInfo){
				_uploadInfo.setTextFormat(tf);
				_uploadInfo.defaultTextFormat = tf;
			}
			_uploadInfo.autoSize = "left";
			_uploadInfo.width = _uploadInfo.textWidth;
			_uploadInfo.height = _uploadInfo.textHeight;
			_uploadInfo.selectable = false;
			_uploadInfo.x = 10;
			_uploadInfo.y = 6;
		}
		
		/**
		 * 构造“全部删除”按钮，并设置“全部删除”按钮的位置
		 */ 
		protected function createAllDeleteBtn():void{
			_allDeleteBtn = new Button();
			_allDeleteBtn.buttonMode = _allDeleteBtn.useHandCursor = true;
			var allDeleteBtnSkin:AllDeleteSkin = new AllDeleteSkin();
			(_allDeleteBtn as Button).setStyle("skin", allDeleteBtnSkin);
			if(_uploadInfo){
				_allDeleteBtn.x = _uploadInfo.x + _uploadInfo.width + 5;
				_allDeleteBtn.y = _uploadInfo.y + 2;
			} else {
				_allDeleteBtn.x = 80;
				_allDeleteBtn.y = 10;
			}
			_allDeleteBtn.addEventListener(MouseEvent.CLICK, deleteAllItems);
		}
		
		/**
		 * 构造删除按钮
		 */ 
		protected function createDeleteBtn():void{
			_deleteBtn = new Button();
			addChild(_deleteBtn);
			_deleteBtn.buttonMode = _deleteBtn.useHandCursor = true;
			var deleteBtnSkin:GlobalDeleteSkin = new GlobalDeleteSkin();
			(_deleteBtn as Button).enabled = false;
			(_deleteBtn as Button).setStyle("skin", deleteBtnSkin);
			(_deleteBtn as Button).drawNow();
			_deleteBtn.x = _width - _deleteBtn.width - 30;
			_deleteBtn.y = 7;
		}
		
		/**
		 * 构造右旋转按钮
		 */ 
		protected function createRotateRightBtn():void{
			_rotateRightBtn = new Button();
			addChild(_rotateRightBtn);
			_rotateRightBtn.buttonMode = _rotateRightBtn.useHandCursor = true;
			var rotateRightBtnSkin:GlobalRotateRightSkin = new GlobalRotateRightSkin();
			(_rotateRightBtn as Button).enabled = false;
			(_rotateRightBtn as Button).setStyle("skin", rotateRightBtnSkin);
			(_rotateRightBtn as Button).drawNow();
			if(_deleteBtn){
				_rotateRightBtn.x = _deleteBtn.x - _rotateRightBtn.width - 5;
			} 
			_rotateRightBtn.y = 7;
		}
		
		/**
		 * 构造左旋转按钮
		 */ 
		protected function createRotateLeftBtn():void{
			_rotateLeftBtn = new Button();
			addChild(_rotateLeftBtn);
			_rotateLeftBtn.buttonMode = _rotateLeftBtn.useHandCursor = true;
			var rotateLeftBtnSkin:GlobalRotateLeftSkin = new GlobalRotateLeftSkin();
			(_rotateLeftBtn as Button).enabled = false;
			(_rotateLeftBtn as Button).setStyle("skin", rotateLeftBtnSkin);
			(_rotateLeftBtn as Button).drawNow();
			if(_rotateRightBtn){
				_rotateLeftBtn.x = _rotateRightBtn.x - _rotateLeftBtn.width - 5;
			} 
			_rotateLeftBtn.y = 7;
		}
		
		/**
		 * 添加UI事件
		 */ 
		protected function attachUIEvent():void{
			if(_rotateLeftBtn){
				_rotateLeftBtn.addEventListener(MouseEvent.CLICK, rotateSelectedItemsLeft);
			}
			if(_rotateRightBtn){
				_rotateRightBtn.addEventListener(MouseEvent.CLICK, rotateSelectedItemsRight);
			}
			if(_deleteBtn){
				_deleteBtn.addEventListener(MouseEvent.CLICK, deleteSelectedItems);
			}
		}
		
		/**
		 * 构造fileselector
		 * 构造list和post
		 */ 
		protected function initComponents():void{
			// 初始化list
			_list = new List(_width, _height - 30, 120, 135);
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
		 * 构造fileselector
		 */
		protected function initFileSelector():void{
			// 初始化fileselector
			var fileTypeObj:Object = baidu.lib.serialization.JSON.decode(Config.UPLOAD_FILE_TYPE);
			var type:FileFilter = new FileFilter(fileTypeObj.description, fileTypeObj.extension, fileTypeObj.extension);
			_fileSelector = new FileSelector();
			_fileSelector.addFileType(type);
			_fileSelector.addEventListener(FileSelectorEvent.SELECT_FILES, onSelectFiles);
			_fileSelector.addEventListener(FileSelectorEvent.CANCEL_FILES, onCancelFiles);
		}
		
		/**
		 * 定义Item，若要使用自己的Item，需要将你的Item继承自AbstractItem或者Item
		 * 然后重写customizeYourItem方法
		 */ 
		protected function customizeYourItem(itemVO:ItemVO):AbstractItem{
			Item;
			var ItemClass:Class = ApplicationDomain.currentDomain.getDefinition("baidu.mod.multiimageuploader.item.Item") as Class;
			var itemWidth:Number = 88;
			var itemHeight:Number = 108;
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
		
		/**
		 * 定义ItemVO，若要使用自己的ItemVO，需要将你的ItemVO继承自ItemVO
		 * 然后重写customizeYourItemVO方法
		 */ 
		protected function customizeYourItemVO(fr:FileReference):ItemVO{
			ItemVO;
			var ItemVOClass:Class = ApplicationDomain.currentDomain.getDefinition("baidu.mod.multiimageuploader.model.ItemVO") as Class;
			var itemVO:ItemVO = new ItemVOClass(fr);
			return itemVO;
		}
		
		public function selectFiles():void{
			_fileSelector.browse();
		}
		
		protected function onCancelFiles(evt:FileSelectorEvent):void{
			dispatchEvent(new FileSelectorEvent(FileSelectorEvent.CANCEL_FILES));
		}
		
		protected function onSelectFiles(evt:FileSelectorEvent):void{
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
		 * 当list中的item被选中或者取消选中时
		 */ 
		protected function listSelected(evt:MultiImageUploaderEvent):void{
			if(evt.data > 0) {
				if(_rotateLeftBtn && _rotateLeftBtn as Button) {
					(_rotateLeftBtn as Button).enabled = true;
				}
				if(_rotateRightBtn && _rotateRightBtn as Button) {
					(_rotateRightBtn as Button).enabled = true;
				}
				if(_deleteBtn && _deleteBtn as Button) {
					(_deleteBtn as Button).enabled = true;
				}
			} else {
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
		 * 开始依次预览
		 */ 
		protected function serialPreview(evt:MultiImageUploaderEvent):void{
			if(_list.tobePreviewedItems.length){
				var item:AbstractItem = _list.tobePreviewedItems.shift() as AbstractItem;
				
				if(item){
					item.preview();
				} else {
					trace("null abstractItem @serialPreview！");
				}
			} else {
				dispatchEvent(new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_PREVIEW_ALLCOMPLETE));
			}
		}
		
		protected function listSizeChange(evt:MultiImageUploaderEvent):void{
			
		}
		
		/**
		 * 删除item
		 */ 
		protected function delelteItem(evt:MultiImageUploaderEvent):void{
			var item:AbstractItem = evt.target as AbstractItem;
			if(item){
				var index:int = _list.removeItem(item);
				_fileSelector.deleteFileByIndex(index);
				// 执行删除文件后的回调函数
				if(Config.DELETE_FILE_CALLBACK && ExternalInterface.available){
					ExternalInterface.call(Config.DELETE_FILE_CALLBACK, [{"index":index, "name":item.data.filename, "size":item.data.filesize}]);
				}
			}
			if(_fileSelector.getAllFiles().length == 0){
				if(_uploadInfo){
					_uploadInfo.text = "";
				}
				if(_allDeleteBtn && contains(_allDeleteBtn)){
					removeChild(_allDeleteBtn);
				}
			} else {
				if(_hasStartedUpload) {
					if(_uploadInfo){
						_uploadInfo.text = "共" + _list.items.length + "张图片,上传完成" + _uploadIdx + "张";
						_uploadInfo.width = _uploadInfo.textWidth;
						_uploadInfo.height = _uploadInfo.textHeight;
					}
				} else {
					if(_allDeleteBtn){
						if(!contains(_allDeleteBtn)){
							addChild(_allDeleteBtn);
						}
						_allDeleteBtn.x = _uploadInfo.x + _uploadInfo.width + 5;
					}
					if(_uploadInfo){
						_uploadInfo.text = _fileSelector.getAllFiles().length + "张图片等待上传";
						_uploadInfo.width = _uploadInfo.textWidth;
						_uploadInfo.height = _uploadInfo.textHeight;
					}
				}
			}
		}
		
		/**
		 * 删除所有预览的图片
		 * @param	evt	[MouseEvent]
		 */ 
		protected function deleteAllItems(evt:MouseEvent):void{
			var jsFileList:Array = [];
			
			for(var i:int = 0, iLen:int = _list.getAllItems().length; i < iLen; i ++){
				jsFileList.push({"index":i, "name":_list.getItemAt(i).data.filename, "size":_list.getItemAt(i).data.filesize});
			}
			
			_list.removeAllItems();
			_fileSelector.deleteFilesAll();
			_uploadInfo.text = "";
			if(contains(_allDeleteBtn)){
				removeChild(_allDeleteBtn);
			}
			if(_rotateLeftBtn && _rotateLeftBtn as Button) {
				(_rotateLeftBtn as Button).enabled = false;
			}
			if(_rotateRightBtn && _rotateRightBtn as Button) {
				(_rotateRightBtn as Button).enabled = false;
			}
			if(_deleteBtn && _deleteBtn as Button) {
				(_deleteBtn as Button).enabled = false;
			}
			// 执行删除文件后的回调函数
			if(Config.DELETE_FILE_CALLBACK && ExternalInterface.available){
				ExternalInterface.call(Config.DELETE_FILE_CALLBACK, jsFileList);
			}
			dispatchEvent(new MultiImageUploaderEvent(MultiImageUploaderEvent.DELETE_ALL));
		}
		
		/**
		 * 左转已选择的item
		 * @param	evt	[MouseEvent]
		 */ 
		protected function rotateSelectedItemsLeft(evt:MouseEvent):void{
			var items:Array = _list.getSelectedItems();
			for(var i:int = 0, iLen:int = items.length; i < iLen; i++){
				(items[i] as AbstractItem).rotate("anti-cw")
			}
		}
		
		/**
		 * 右转已选择的item
		 * @param	evt	[MouseEvent]
		 */ 
		protected function rotateSelectedItemsRight(evt:MouseEvent):void{
			var items:Array = _list.getSelectedItems();
			for(var i:int = 0, iLen:int = items.length; i < iLen; i++){
				(items[i] as AbstractItem).rotate("cw")
			}
		}
		
		/**
		 * 删除已选择的item
		 * @param	evt	[MouseEvent]
		 */ 
		protected function deleteSelectedItems(evt:MouseEvent):void{
			var items:Array = _list.getSelectedItems();
			for(var i:int = 0, iLen:int = items.length; i < iLen; i++){
				(items[i] as AbstractItem).deleteItem();
			}
		}
		
		/**
		 * 顺序上传
		 */ 
		protected function serialUpload():void{
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
				}
			}
		}
		
		/**
		 * 改变正上传的item的上传进度条
		 * @param 	evt	[MultiImageUploaderEvent]
		 */ 
		protected function changeProgress(evt:MultiImageUploaderEvent):void{
			if(!_currentItem) {
				return;
			}
			_currentItem.setUploadProgress(evt.data);
		}
		
		/**
		 * 上传完成 （包括成功和失败两种情况）
		 */
		protected function onUploadComplete(evt:MultiImageUploaderEvent):void{
			if(!_currentItem){
				return;
			}
			_currentItem.status = Config.UPLOADED;
			_uploading = false;
			
			if(evt.type == MultiImageUploaderEvent.POST_UPLOAD_FAIL){
				_currentItem.seal(false);
				if(Config.UPLOAD_ERROR_CALLLBACK && ExternalInterface.available){
					ExternalInterface.call(Config.UPLOAD_ERROR_CALLLBACK, false, evt.data);
				}
			} else if(evt.type == MultiImageUploaderEvent.POST_UPLOAD_SUCCESS){
				_currentItem.seal(true);
				if(Config.UPLOAD_COMPLETE_CALLBACK && ExternalInterface.available){
					ExternalInterface.call(Config.UPLOAD_COMPLETE_CALLBACK, true, evt.data);
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
		
		/**
		 * 拿到vars里的参数
		 */ 
		protected function initVarsParams(evt:Event):void{
			var params:Object = loaderInfo.parameters;
			Config.UPLOAD_URL = params["url"] ? params["url"] : Config.UPLOAD_URL;
			Config.UPLOAD_FILE_TYPE = params["fileType"] ? params["fileType"] : Config.UPLOAD_FILE_TYPE;
			Config.MAX_FILE_NUM = params["maxNum"] ? parseInt(params["maxNum"], 10) : Config.MAX_FILE_NUM;
			Config.MAX_FILE_SIZE = params["maxSize"] ? parseInt(params["maxSize"], 10) : Config.MAX_FILE_SIZE;
			Config.COMPRESS_FILE_SIZE = params["compressSize"] ? parseInt(params["compressSize"], 10) : Config.COMPRESS_FILE_SIZE;
			Config.COMPRESS_MAX_LENGTH = params["compressLength"] ? parseInt(params["compressLength"], 10) :Config.COMPRESS_MAX_LENGTH;
			Config.UPLOAD_DATAFIELD_NAME = params["uploadDataFieldName"] ? params["uploadDataFieldName"] : Config.UPLOAD_DATAFIELD_NAME;
			Config.PIC_DESC_FIELD_NAME = params["picDescFieldName"] ? params["picDescFieldName"] : Config.PIC_DESC_FIELD_NAME;
			Config.SUPPORT_GIF = params["supportGif"] ? parseInt(params["supportGif"], 10) : Config.SUPPORT_GIF;
			Config.DUPLICATED_CHOOSE = params["duplicated"] ? parseInt(params["duplicated"], 10) : Config.DUPLICATED_CHOOSE;
			
			// 自定义参数
			if(params["ext"]){
				Config.UPLOAD_PARAMS = baidu.lib.serialization.JSON.decode(params["ext"]);
			}
		}
	}
}