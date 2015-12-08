package baidu.mod.multiimageuploader.item
{
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.model.ItemVO;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.Event;

	[Event(name="item_review_complete", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	[Event(name="item_click", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	public class AbstractItem extends Sprite
	{
		// 是否可选择
		protected var _selectable:Boolean = true;
		// 是否可以多选
		protected var _multiselectable:Boolean = true;
		// item的宽度
		protected var _width:Number;
		// item的高度
		protected var _height:Number;
		// 数据模型
		protected var _data:ItemVO;
		// item的背景
		protected var _background:DisplayObject;
		// item的边框
		protected var _border:DisplayObject;
		// item的图片区
		protected var _image:DisplayObject;
		// item的遮罩
		protected var _waitingMask:DisplayObject;
		// 标识item是否被选中
		protected var _selected:Boolean = false;
		// 标识item是否再等待上传
		protected var _waiting:Boolean = false;
		// 状态
		protected var _status:String = "";
		// 旋转的角度
		protected var _rotation:Number = 0;
		
		///////// SET/GET方法 /////////
		public function set newrotation(value:Number):void{
			_rotation = value;
			_data.rotation = value;
		}
		
		public function set data(itemVO:ItemVO):void{
			
		}
		public function get data():ItemVO{
			return _data;
		}
		
		public function set status(value:String):void{
			_status = value;
		}
		public function get status():String{
			return _status;
		}
		
		/**
		 * get方法，反回item是否被选中
		 * 
		 * @return selected	[Boolean]
		 */ 
		public function get selected():Boolean{
			return _selected;
		}
		/**
		 * set方法，设置item是否被选中
		 * 
		 * @param	value	[Boolean]
		 */ 
		public function set selected(value:Boolean):void{
			if(_selected == value){
				return;
			}
			_selected = value;
		}
		
		public function get waiting():Boolean{
			return _waiting;
		}
		public function set waiting(value:Boolean):void{
			if(_waiting == value){
				return;
			}
			_waiting = value;
		}
		
		public function set waitingMask(value:DisplayObject):void{
			_waitingMask = value;
			_waitingMask.width = _width;
			_waitingMask.height = _height;
		}
		
		public function set background(value:DisplayObject):void{
			if(_background && contains(_background)){
				removeChild(_background);
				_background = null;
			}
			_background = value;
			var ratio:Number = Math.min(_width/_background.width, _height/_background.height);
			_background.scaleX = _background.scaleY = ratio;
			_background.x = (_width - _background.width) / 2;
			_background.y = (_height - _background.height) / 2;
			addChildAt(_background, 0);
		}
		
		public function set border(value:DisplayObject):void{
			if(_border && contains(_border)){
				removeChild(_border);
				_border = null;
			}
			_border = value;
			var ratio:Number = Math.min(_width/_border.width, _height/_border.height);
			_border.scaleX = _border.scaleY = ratio;
			_border.x = (_width - _border.width) / 2;
			_border.y = (_height - _border.height) / 2;
			addChild(_border);
		}
		
		public function set image(value:DisplayObject):void{
			
		}
		///////// SET/GET方法 /////////
		
		public function AbstractItem(width:Number, height:Number)
		{
			_width = width;
			_height = height;
			
			initUI();
			addEventListener(Event.REMOVED_FROM_STAGE, onRemove);
		}
		
		// initUI是用来初始化背景 边框
		protected function initUI():void{
		}
		
		/**
		 * 移除时释放资源
		 */ 
		protected function dispose():void{
			
		}
		
		public function preview():void{
			
		}
		
		public function rotate(flag:String):void{
			
		}
		
		public function deleteItem():void{
			
		}
		
		public function setUploadProgress(ratio:Number):void{
			
		}
		
		public function seal(isok:Boolean):void{
			
		}
		
		protected function onRemove(evt:Event):void{
			dispose();
		}
	}
}