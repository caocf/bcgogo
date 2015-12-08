package baidu.mod.multiimageuploader.item
{
	import baidu.lib.images.ImageLoader;
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.model.ItemVO;
	import baidu.mod.multiimageuploader.ui.ProgressBar;
	import baidu.ui.controls.Button;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.FocusEvent;
	import flash.events.IOErrorEvent;
	import flash.events.MouseEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.geom.Matrix;
	import flash.system.Security;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFieldType;
	import flash.text.TextFormat;

	public class Item extends AbstractItem
	{
		protected var _rotateLeftBtn:Sprite;
		protected var _rotateRightBtn:Sprite;
		protected var _deleteBtn:Sprite;
		protected var _description:TextField;
		protected var _pb:ProgressBar;
		
		protected var _imageLayer:Sprite;
		protected var _controlLayer:Sprite;
		protected var _selectedLayer:Sprite;
		protected var _controlBorder:Sprite;
		protected var _selectedBorder:Sprite;
		
		protected var _defaultText:String = "";
		
		protected var _loader:baidu.lib.images.ImageLoader;
		
		///////// SET/GET方法 /////////
		override public function set data(itemVO:ItemVO):void{
			if(itemVO){
				_data = itemVO;
			}
		}
		
		override public function set status(value:String):void{
			super.status = value;
			switch(_status){
				case Config.PREVIEW_COMPLETE:
					attachEvent();
					break;
				case Config.PREVIEW_ERROR:
					attachEvent();
					break;
				case Config.UPLOADING:
					detachEvent();
					// 移除操作框
					if(_controlBorder && _controlLayer.contains(_controlBorder)){
						_controlLayer.removeChild(_controlBorder);
					}
					if(_description){
						_description.type = TextFieldType.DYNAMIC;
						// 移除事件
						_description.removeEventListener(FocusEvent.FOCUS_IN, onDescriptionFocusIn);
						_description.removeEventListener(FocusEvent.FOCUS_OUT, onDescriptionFocusOut);
						// 如果没有设置描述，则为空
						if(_description.text == _defaultText){
							_description.text = "";
						}
						_data.description = _description.text ? _description.text : _data.filename;
					}
					if(_pb && contains(_pb)){
						removeChild(_pb);
					}
					_pb = new ProgressBar(_width - 10, 3);
					_pb.x = (_width - _pb.width) / 2;
					_pb.y = _height - 30;
					addChild(_pb);
					break;
				case Config.UPLOADED:
					if(_pb && contains(_pb)){
						removeChild(_pb);
					}
					if(_waitingMask && _imageLayer.contains(_waitingMask)){
						_imageLayer.removeChild(_waitingMask);
					}
					break;
			}
		}
		
		override public function set selected(value:Boolean):void{
			super.selected = value;
			if(_selected){	// 选中
				if(!_selectedLayer.contains(_selectedBorder)){	// 添加选择框
					_selectedLayer.addChild(_selectedBorder);
				}
			} else {
				if(_selectedBorder && _selectedLayer.contains(_selectedBorder)){	// 移除选择框
					_selectedLayer.removeChild(_selectedBorder);
				}
			}
		}
		
		override public function set waiting(value:Boolean):void{
			super.waiting = value;
			if(_waiting && _data.filesize <= Config.MAX_FILE_SIZE * 1024 * 1024 && (_status == Config.PREVIEW_COMPLETE || _status == Config.READY_FOR_UPLOAD)){
				_status = Config.READY_FOR_UPLOAD;
				if(_waitingMask && !_imageLayer.contains(_waitingMask)){
					_imageLayer.addChild(_waitingMask);
				}
				selected = false;
			} else {
				if(_waitingMask && _imageLayer.contains(_waitingMask)){
					_imageLayer.removeChild(_waitingMask);
				}
			}
		}
		///////// SET/GET方法 /////////
		
		public function Item(width:Number, height:Number)
		{
			super(width, height);
		}
		
		override protected function initUI():void{
			// 赋值默认提示信息
			_defaultText = "请输入描述";
			
			// 图片层，在最下面
			_imageLayer = new Sprite;
			addChild(_imageLayer);
			// UI控件层
			_controlLayer = new Sprite();
			addChild(_controlLayer);
			
			// 进度条层，在最上面
			_selectedLayer = new Sprite();
			addChild(_selectedLayer);
			
			initBorders();
			initControls();
			adjustControlsPosition();
		}
		
		/**
		 * 移除时释放资源
		 */ 
		override protected function dispose():void{
			if(_image && _image is Bitmap){
				(_image as Bitmap).bitmapData.dispose();
			}
			//2011-8-24
			//田驰
			if(_loader){
				_loader.removeEventListener(Event.COMPLETE, onDataLoaded);
				_loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onDataLoadError);
				_loader.removeEventListener(IOErrorEvent.IO_ERROR, onDataLoadError);
			}
			if(_data){
			_data.fr.cancel();
			}
		}
		
		/**
		 */ 
		protected function initBorders():void{
			initControlBorder();
			initSelectedBorder();
		}
		
		/**
		 * 初始化controlborder
		 */ 
		protected function initControlBorder():void{
			_controlBorder = new Sprite();
			var g:Graphics = _controlBorder.graphics;
			g.clear();
			g.lineStyle(3, 0xebebff, 1);
			g.drawRect(-3 , -3, _width + 5, _height + 5);
		}
		
		/**
		 * 初始化selectedborder
		 */
		protected function initSelectedBorder():void{
			_selectedBorder = new Sprite();
			var g:Graphics = _selectedBorder.graphics;
			g.clear();
			g.lineStyle(3, 0xccccff, 1);
			g.drawRect(-3 , -3, _width + 5, _height + 5);
		}
		
		/**
		 * 初始化UI控件
		 */ 
		protected function initControls():void{
			_rotateLeftBtn = new Button();
			_controlBorder.addChild(_rotateLeftBtn);
			_rotateLeftBtn.buttonMode = _rotateLeftBtn.useHandCursor = true;
			var rotateLeftBtnSkin:ItemRotateLeftSkin = new ItemRotateLeftSkin();
			(_rotateLeftBtn as Button).setStyle("skin", rotateLeftBtnSkin);
			(_rotateLeftBtn as Button).drawNow();
			
			_rotateRightBtn = new Button();
			_controlBorder.addChild(_rotateRightBtn);
			_rotateRightBtn.buttonMode = _rotateRightBtn.useHandCursor = true;
			var rotateRightBtnSkin:ItemRotateRightSkin = new ItemRotateRightSkin();
			(_rotateRightBtn as Button).setStyle("skin", rotateRightBtnSkin);
			(_rotateRightBtn as Button).drawNow();
			
			_deleteBtn = new Button();
			_controlBorder.addChild(_deleteBtn);
			_deleteBtn.buttonMode = _deleteBtn.useHandCursor = true;
			var deleteBtnSkin:ItemDeleteSkin = new ItemDeleteSkin();
			(_deleteBtn as Button).setStyle("skin", ItemDeleteSkin);
			(_deleteBtn as Button).drawNow();
			
			_pb = new ProgressBar(_width - 10, 3, "图像加载中");
			
			_description = new TextField();
			//_imageLayer.addChild(_description);
			_description.type = TextFieldType.INPUT;
			_description.autoSize = TextFieldAutoSize.NONE;
			_description.height = _description.textHeight + 5;
			_description.width = _width;
            _description.visible = false;
			
			//设置文字样式
			_description.htmlText = "<font color='#b4b4b4'><p align='center'>" + _defaultText + "</p></font>";
			//设置行为
//			_description.addEventListener(FocusEvent.FOCUS_IN, onDescriptionFocusIn);
//			_description.addEventListener(FocusEvent.FOCUS_OUT, onDescriptionFocusOut);
		}
		
		/**
		 * 调整UI控件坐标
		 */
		protected function adjustControlsPosition():void{
			if(_rotateLeftBtn){
				_rotateLeftBtn.x = 2;
				_rotateLeftBtn.y = 2;
			}
			if(_rotateRightBtn){
				_rotateRightBtn.x = 20;
				_rotateRightBtn.y = 2;
			}
			if(_deleteBtn){
				_deleteBtn.x = _width - _deleteBtn.width - 2;
				_deleteBtn.y = 2;
			}
			if(_description){
				_description.x = 1;
				_description.y = _height - _description.height;
				_description.width = _width - 2;
			}
			if(_pb){
				_pb.x = (_width - _pb.width) / 2;
				_pb.y = _height - 30;
			}
		}
		
		/**
		 * 显示当文件体积超出限制时的提示文字
		 */ 
		protected function showErrorSizeInfo():void{
			var txt:TextField = new TextField();
			
			var tmpsize:int = 0;
			
			if(Config.MAX_FILE_SIZE < 1){
				tmpsize = Math.floor(Config.MAX_FILE_SIZE * 1000);
				txt.text = "图片大小超过"+tmpsize+"K";
			} else {
				tmpsize = Math.floor(Config.MAX_FILE_SIZE);
				txt.text = "图片大小超过"+tmpsize+"M";
			}
			//txt.text = "图片大小超过"+tmpsize+"M";
			
			var tf:TextFormat = new TextFormat("微软雅黑", 11, 0xff0000);
			txt.setTextFormat(tf);
			txt.autoSize = "left";
			txt.width = txt.textWidth;
			txt.height = txt.textHeight;
			txt.x = (_width - txt.width) / 2;
			txt.y = _height - 22;
			txt.height = 22;
			addChild(txt);
		}
		
		/**
		 * 图片描述输入框获得焦点事件
		 * @param	evt	[FocusEvent]
		 */ 
		protected function onDescriptionFocusIn(evt:FocusEvent):void{
			var txt:String = _description.text;
			if (txt == _defaultText) {
				_description.text = "";
			} else {
				_description.text = txt;
			}
		}
		
		/**
		 * 图片描述输入框失去焦点事件
		 * @param	evt	[FocusEvent]
		 */
		protected function onDescriptionFocusOut(evt:FocusEvent):void{
			var txt:String = _description.text;
			if (txt == "") {
				_description.htmlText = "<font color='#b4b4b4'><p align='center'>" + _defaultText + "</p></font>";
			} else {
				_description.htmlText = "<font color='#000000'><p align='center'>" + txt + "</p></font>";
			}
		}
		
		/**
		 * FileReference的文件读取完成事件的响应函数
		 * @param	evt	[Event]
		 */ 
		protected function onFileDataLoaded(evt:Event):void{
			_data.fr.removeEventListener(Event.COMPLETE, onFileDataLoaded);
			_data.fr.removeEventListener(IOErrorEvent.IO_ERROR, onFileDataLoadError);
			_data.fr.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileDataLoadError);
			_data.fr.removeEventListener(ProgressEvent.PROGRESS, onFileLoadProgress);
			
			// 初始化_loader，读取图片数据
			_loader = new baidu.lib.images.ImageLoader();
			_loader.addEventListener(Event.COMPLETE, onDataLoaded);
			_loader.addEventListener(IOErrorEvent.IO_ERROR, onDataLoadError);
			_loader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onDataLoadError);
			_loader.loadBytes(_data.rawData);
		}
		
		/**
		 * FileReference的文件读取错误事件的响应函数，
		 * 该错误可能是IOError也可能是SecurityError
		 * @param	evt	[Event]
		 */
		protected function onFileDataLoadError(evt:Event):void{
			_data.fr.removeEventListener(Event.COMPLETE, onFileDataLoaded);
			_data.fr.removeEventListener(IOErrorEvent.IO_ERROR, onFileDataLoadError);
			_data.fr.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileDataLoadError);
			_data.fr.removeEventListener(ProgressEvent.PROGRESS, onFileLoadProgress);
			
			_status = Config.PREVIEW_ERROR;
			
			var type:String;
			if(evt as IOErrorEvent){
				type = MultiImageUploaderEvent.ITEM_FILE_DATA_IOERROR;
			} else if(evt as SecurityErrorEvent){
				type = MultiImageUploaderEvent.ITEM_FILE_DATA_SECURITYERROR;
			} 
			dispatchMultiUploaderItemEvent(type);
		}
		
		/**
		 * 加载图片进度
		 * @param	evt	[ProgressEvent]
		 */ 
		protected function onFileLoadProgress(evt:ProgressEvent):void{
			_pb.data = evt.bytesLoaded / evt.bytesTotal;
		}
		
		/**
		 * 图片加载完成进度
		 */ 
		protected function onDataLoaded(evt:Event):void{
			// 移除事件监听
			_loader.removeEventListener(Event.COMPLETE, onDataLoaded);
			_loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onDataLoadError);
			_loader.removeEventListener(IOErrorEvent.IO_ERROR, onDataLoadError);
			
			// 显示图片内容
			var w:Number, h:Number;
			w = _width - 2;
			if(_description){
				h = _height - _description.height;
			} else {
				h = _height;
			}
			_data.originalWidth = _loader.width;
			_data.originalHeight = _loader.height;
			
			var ratio:Number = Math.min(w / _loader.width, h / _loader.height);
			
			var bitmapdata:BitmapData = new BitmapData(_loader.width * ratio, _loader.height *ratio);
			var scaleMatrix:Matrix = new Matrix();
			scaleMatrix.scale(ratio, ratio);
			bitmapdata.draw(_loader, scaleMatrix, null, null, null, true);
			_image = new Bitmap(bitmapdata);
			_data.image = _loader;
			
			_image.x = (w - _image.width) / 2 ;
			_image.y = (h - _image.height) / 2;
			removeChild(_pb);
			_imageLayer.addChild(_image);
			
			if(_description){
				addChild(_description);
			}
			
			status = Config.PREVIEW_COMPLETE;
			
			dispatchMultiUploaderItemEvent(MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE);
		}
		
		protected function onDataLoadError(evt:Event):void{
			// 移除进度提示信息
			if(contains(_pb)){
				removeChild(_pb);
			}
			
			// 移除事件监听
			_loader.removeEventListener(Event.COMPLETE, onDataLoaded);
			_loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onDataLoadError);
			_loader.removeEventListener(IOErrorEvent.IO_ERROR, onDataLoadError);
			
			// 图片预览失败提示文字
			var txt:TextField = new TextField();
			txt.text = "预览图片失败";
			var tf:TextFormat = new TextFormat("微软雅黑", 11, 0xff0000);
			txt.setTextFormat(tf);
			txt.autoSize = "left";
			txt.width = txt.textWidth;
			txt.height = txt.textHeight;
			txt.x = (_width - txt.width) / 2;
			txt.y = _height - 22;
			addChild(txt);
			
			status = Config.PREVIEW_ERROR;
			
			// TODO list...
			dispatchMultiUploaderItemEvent(MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE);

		}
		
		protected function attachEvent():void{
			addEventListener(MouseEvent.ROLL_OVER, onItemRollOver);
			addEventListener(MouseEvent.ROLL_OUT, onItemRollOut);
			if(_selectable){
				addEventListener(MouseEvent.CLICK, onItemClick);
			}
			if(_deleteBtn){
				_deleteBtn.addEventListener(MouseEvent.CLICK, onDeleteBtnClick);
			}
			if(_rotateLeftBtn){
				_rotateLeftBtn.addEventListener(MouseEvent.CLICK, onRotateLeftBtnClick);
			}
			if(_rotateRightBtn){
				_rotateRightBtn.addEventListener(MouseEvent.CLICK, onRotateRightBtnClick);
			}
		}
		
		protected function detachEvent():void{
			removeEventListener(MouseEvent.ROLL_OVER, onItemRollOver);
			removeEventListener(MouseEvent.ROLL_OUT, onItemRollOut);
			if(_selectable){
				removeEventListener(MouseEvent.CLICK, onItemClick);
			}
			if(_deleteBtn){
				_deleteBtn.removeEventListener(MouseEvent.CLICK, onDeleteBtnClick);
			}
			if(_rotateLeftBtn){
				_rotateLeftBtn.removeEventListener(MouseEvent.CLICK, onRotateLeftBtnClick);
			}
			if(_rotateRightBtn){
				_rotateRightBtn.removeEventListener(MouseEvent.CLICK, onRotateRightBtnClick);
			}
		}
		
		/**
		 * 鼠标移上Item上的响应函数
		 * 添加操作框
		 * 
		 * @param	evt	[MouseEvent]
		 */ 
		protected function onItemRollOver(evt:MouseEvent):void{
			// 在未选中的状态下，鼠标移上时，显示外框
			if(!_controlLayer.contains(_controlBorder)){
				_controlLayer.addChild(_controlBorder);
			}
		}
		
		/**
		 * 鼠标移出Item时的响应函数
		 * 移除操作框
		 * 
		 * @param	evt	[MouseEvent]
		 */
		protected function onItemRollOut(evt:MouseEvent):void{
			if(_controlLayer.contains(_controlBorder)){
				_controlLayer.removeChild(_controlBorder);
			}
		}
		
		/**
		 * 鼠标点击Item时的响应函数
		 * 若item未选中，则选择该item，若已选中，则取消选中
		 * 
		 * @param	evt	[MouseEvent]
		 */
		protected function onItemClick(evt:MouseEvent):void{
			if(evt.currentTarget == this){
				var type:String = MultiImageUploaderEvent.ITEM_CLICK;
				if(_multiselectable){
					dispatchMultiUploaderItemEvent(type, evt.ctrlKey);
				} else {
					dispatchMultiUploaderItemEvent(type);
				}
				
			}
		}
		
		/**
		 * 点击删除按钮
		 * @param	evt	[MouseEvent]
		 */ 
		protected function onDeleteBtnClick(evt:MouseEvent):void{
			deleteItem();
			evt.stopImmediatePropagation();
			evt.stopPropagation();
		}
		
		/**
		 * 点击左转按钮
		 * @param	evt	[MouseEvent]
		 */
		protected function onRotateLeftBtnClick(evt:MouseEvent):void{
			rotate("anti-cw");
			evt.stopImmediatePropagation();
			evt.stopPropagation();
		}
		
		/**
		 * 点击右转按钮
		 * @param	evt	[MouseEvent]
		 */
		protected function onRotateRightBtnClick(evt:MouseEvent):void{
			rotate("cw");
			evt.stopImmediatePropagation();
			evt.stopPropagation();
		}
		
		/**
		 * 派发事件
		 */ 
		public function dispatchMultiUploaderItemEvent(type:String, data:* = null):void{
			var event:MultiImageUploaderEvent;
			switch(type) {
				case MultiImageUploaderEvent.ITEM_PREVIEW_ERROR_IN_FILESIZE:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR_IN_FILESIZE, true);
					break;
				case MultiImageUploaderEvent.ITEM_PREVIEW_ERROR:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR, true);
					break;
				case MultiImageUploaderEvent.ITEM_FILE_DATA_IOERROR:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_FILE_DATA_IOERROR);
					break;
				case MultiImageUploaderEvent.ITEM_FILE_DATA_SECURITYERROR:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_FILE_DATA_SECURITYERROR);
					break;
				case MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE, true);
					break;
				case MultiImageUploaderEvent.ITEM_CLICK:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_CLICK, true);
					break;
				default:
					event = new MultiImageUploaderEvent(MultiImageUploaderEvent.UNKOWN_ERROR);
			}
			if(event){
				event.data = data;
				
				dispatchEvent(event);
			}
		}
		
		/**
		 * 开始预览
		 */ 
		override public function preview():void{
			if(_data.filesize < Config.MAX_FILE_SIZE * 1024 * 1024){
				_data.fr.addEventListener(Event.COMPLETE, onFileDataLoaded);
				_data.fr.addEventListener(IOErrorEvent.IO_ERROR, onFileDataLoadError);
				_data.fr.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileDataLoadError);
				_data.fr.addEventListener(ProgressEvent.PROGRESS, onFileLoadProgress);
				_data.fr.load();
				addChild(_pb);
			} else {
				showErrorSizeInfo();
				status = Config.PREVIEW_ERROR;
				dispatchMultiUploaderItemEvent(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR_IN_FILESIZE);
			}
		}
		
		/**
		 * 旋转
		 * @param	flag	[String] flag == "cw"时，右旋转； flag == "anti-cw"时，左旋转
		 */ 
		override public function rotate(flag:String):void{
			if(_data.filesize > Config.MAX_FILE_SIZE * 1024 * 1024){
				return;
			}
			if(flag == "anti-cw"){
				_image.rotation -= 90;
			} else {
				_image.rotation += 90;
			}
			var tx:Number = 0, ty:Number = 0;
			var w:Number = _width;
			var h:Number = (_description) ? _height - _description.height : _height;
			
			// 等比调整图像的展示呀
			_image.scaleX = _image.scaleY = 1;
			var r:Number = Math.min(w / _image.width, h / _image.height);
			_image.scaleX = _image.scaleY = r;
			
			switch(_image.rotation){
				case -180:
					tx = w / 2 + _image.width / 2;
					ty = h /2 + _image.height / 2;
					newrotation = 180;
					break;
				case -90:
					tx = w / 2 - _image.width / 2;
					ty = h / 2 + _image.height / 2;
					newrotation = -90;
					break;
				case 180:
					tx = w / 2 + _image.width / 2;
					ty = h / 2 + _image.height / 2;
					newrotation = 180;
					break;
				case 90:
					tx = w / 2 + _image.width / 2;
					ty = h / 2 - _image.height / 2;
					newrotation = 90;
					break;
				default:
					tx = w / 2 - _image.width / 2;
					ty = h / 2 - _image.height / 2;
					newrotation = 0;
			}
			_image.x = tx;
			_image.y = ty;
		}
		
		/**
		 * 删除
		 */ 
		override public function deleteItem():void{
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.ITEM_DELETE);
			dispatchEvent(event);
		}
		
		/**
		 * 设置上传进度
		 * @param	ratio	[Number]
		 */ 
		override public function setUploadProgress(ratio:Number):void{
			if(_pb){
				_pb.data = ratio;
			}
			if(_waitingMask){
				_waitingMask.alpha = 0.7 - 0.7 * ratio;
			}
		}
		
		/**
		 * 上传完成后设置item状态
		 * @param	isok	[Boolean]
		 */
		override public function seal(isok:Boolean):void{
			var icon:Sprite;
			if(isok){
				icon = new UploadCompleteSkin();
			} else {
				icon = new UploadErrorSkin();
			}
			icon.x = _width - icon.width;
			icon.y = _height - 20 - icon.height;
			addChild(icon);
		}
		
	}
}