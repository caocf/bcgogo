package baidu.mod.multiimageuploader.list
{
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.item.AbstractItem;
	import baidu.mod.multiimageuploader.item.Item;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.external.ExternalInterface;
	
	[Event(name="list_size_change", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	[Event(name="list_item_selected", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	[Event(name="list_item_preview_complete", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	public class List extends Sprite
	{
		// item是否可以选择
		protected var _selectable:Boolean;
		// item是否可以多选
		protected var _multiselectable:Boolean;
		// item的宽度
		protected var _colWidth:Number;
		// item的高度
		protected var _rowHeight:Number;
		// 列的个数，由_rowHeight和_height计算得到
		protected var _rowCount:int;
		// 行的个数，由_colWidth和_width计算得到
		protected var _colCount:int;
		// list自身的宽度
		protected var _width:Number;
		// list自身的高度
		protected var _height:Number;
		// 通过构造函数传进来的高度值，_height不可以小于这个值
		protected var _minHeight:Number;
		
		// Item实例的数组
		protected var _items:Array = [];
		// 已选的Item实例的数组
		protected var _selectedItems:Array = [];
		// 待预览的item实例的数组
		protected var _2bPreviewedItems:Array = [];
		// 待上传的item实例的数组
		protected var _2bUploadedItems:Array = [];
		
		// List的背景
		protected var _background:DisplayObject;
		
		// 是否要自动调整高度让整个list的所有元素可见。
		// 因为list对于item的默认布局为先从左到右、再从上到下,若不自动调整高度，可能会在y方向上有item被挡住或出现空白。
		// 如果为true，当修改了width或者height或者rowHeight或者colWidth后，会自动调整_height
		// 如果为false，当修改了width或者height或者rowHeight或者colWidth后，不会修改_height
		protected var autoHeight:Boolean = false;
		
		/////////// GET/SET方法 ///////////
		public function get items():Array{
			return _items;
		}
		
		public function get tobePreviewedItems():Array{
			return _2bPreviewedItems;
		}
		
		public function get tobeUploadedItems():Array{
			return _2bUploadedItems;
		}
		
		override public function get width():Number{
			return _width;
		}
		override public function set width(value:Number):void{
			if(_width == value){
				return;
			}
			setSize(value, _height);
		}
		
		override public function get height():Number{
			return _height;
		}
		override public function set height(value:Number):void{
			if(_height == value){
				return;
			}
			setSize(_width, value);
		}
		
		public function set rowHeight(value:Number):void{
			if(_rowHeight == value){
				return;
			}
			_rowHeight = value;
			adjustLayout();
		}
		public function get rowHeight():Number{
			return _rowHeight;
		}
		
		public function set colWidth(value:Number):void{
			if(_colWidth == value){
				return;
			}
			_colWidth = value;
			adjustLayout();
		}
		public function get colWidth():Number{
			return _colWidth;
		}
		
		public function set minHeight(value:Number):void{
			_minHeight = value;
		}
		public function get minHeight():Number{
			return _minHeight;
		}
		
		public function get background():DisplayObject{
			return _background;
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
		/////////// GET/SET方法 ///////////
		
		/**
		 * 构造函数
		 * @param	width	[Number] List的宽度
		 * @param	height	[Number] List的高度
		 */ 
		public function List(width:Number, height:Number, colWidth:Number, rowHeight:Number)
		{
			_width = width;
			_height = height;
			_minHeight = height;
			
			_colWidth = colWidth;
			_rowHeight = rowHeight;
			
			_colCount = Math.floor(_width / _colWidth);
			_rowCount = Math.floor(_height / _rowHeight);
			_colCount = (_colCount == 0) ? 1 : _colCount;
			_rowCount = (_rowCount == 0) ? 1 : _rowCount;
			
			attachEvent();
		}
		
		/**
		 * 添加Item
		 * @param	item	[AbstractItem]
		 */ 
		public function addItem(item:AbstractItem):void{
			_items.push(item);
			_2bPreviewedItems.push(item);
			
			var idx:int = _items.length - 1;
			var centerX:Number = (idx % _colCount) * _colWidth + _colWidth / 2;
			var centerY:Number = Math.floor(idx / _colCount) * _rowHeight + _rowHeight / 2;
			
			item.x = centerX - item.width / 2;
			item.y = centerY - item.height / 2;
			addChild(item);
			
			_height = centerY + _rowHeight / 2;
			
			checkHeight();
			
			dispatchChangeSizeEvent();
		}
		
		/**
		 * 删除Item
		 * @param	item	[AbstractItem]
		 * @return 返回被删除的item的index
		 */
		public function removeItem(item:AbstractItem):int{
			var idx:int = -1;
			for(var i:int = 0, iLen:int = _items.length; i < iLen; i++){
				if(_items[i] == item){
					if(contains(_items[i])) {
						removeChild(_items[i]);
					}
					idx = i;
				}
			}
			_items = _items.filter(function(obj:*, index:int, array:Array):Boolean{
				if(obj != item){
					return true;
				}
				return false;
			});
			_selectedItems = _selectedItems.filter(function(obj:*, index:int, array:Array):Boolean{
				if(obj != item){
					return true;
				}
				return false;
			});
			_2bPreviewedItems = _2bPreviewedItems.filter(function(obj:*, index:int, array:Array):Boolean{
				if(obj != item){
					return true;
				}
				return false;
			});
			_2bUploadedItems = _2bUploadedItems.filter(function(obj:*, index:int, array:Array):Boolean{
				if(obj != item){
					return true;
				}
				return false;
			});
			
			adjustLayout();
			
			return idx;
		}
		
		/**
		 * 删除指定序号的元素
		 * @param	index	[int]
		 */ 
		public function removeItemAt(index:int):void{
			if(index < _items.length){
				removeItem(_items[index]);
			} else {
				trace("Invalid param:'index' @function:'removeItemAt' in List");
			}
		}
		
		/**
		 * 删除所有的item
		 */ 
		public function removeAllItems():void{
			var tmp:AbstractItem;
			while(_items.length){
				tmp = _items.shift() as AbstractItem;
				if(tmp && contains(tmp)){
					removeChild(tmp);
				}
			}
			
			_items = [];
			_selectedItems = [];
			_2bPreviewedItems = [];
			_2bUploadedItems = [];
			
			adjustLayout();
		}
		
		/**
		 * 删除所有的item
		 * @return	[Array]
		 */
		public function getAllItems():Array{
			return _items;
		}
		
		/**
		 * 返回所有已选的item
		 * @return	[Array]
		 */ 
		public function getSelectedItems():Array{
			return _selectedItems;
		}
		
		/**
		 * 返回item
		 * @return [AbstractItem]
		 */ 
		public function getItemAt(index:int):AbstractItem{
			return _items[index];
		}
		
		public function attachEvent():void{
			addEventListener(MouseEvent.CLICK, cancelAllSelectedItemsHandler);
			addEventListener(MultiImageUploaderEvent.ITEM_CLICK, onItemClick);
			addEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE, onItemPreview);
			addEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR, onItemPreview);
			addEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR_IN_FILESIZE, onItemPreview);
		}
		
		public function detachEvent():void{
			removeEventListener(MouseEvent.CLICK, cancelAllSelectedItemsHandler);
			removeEventListener(MultiImageUploaderEvent.ITEM_CLICK, onItemClick);
			removeEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE, onItemPreview);
			removeEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR, onItemPreview);
			removeEventListener(MultiImageUploaderEvent.ITEM_PREVIEW_ERROR_IN_FILESIZE, onItemPreview);
		}
		
		/**
		 * 调整宽、高
		 * @param	width	[Number]
		 * @param	height 	[Number]
		 */ 
		protected function setSize(width:Number, height:Number):void{
			if (_width == width && _height == height) {
				return;
			}
			_width = width;
			_height = height;
			
			adjustLayout();
		}
		
		/**
		 * adjustLayout根据_width _height _rowHeight _colWidth四个属性调整自身的布局和item的位置
		 */ 
		protected function adjustLayout():void {
			_colCount = Math.floor(_width / _colWidth);
			_rowCount = Math.floor(_height / _rowHeight);
			_colCount = (_colCount == 0) ? 1 : _colCount;
			_rowCount = (_rowCount == 0) ? 1 : _rowCount;
			
			// 调整背景的宽、高
			if(_background){
				_background.width = _width;
				_background.height = _height;
			}
			
			var item:AbstractItem;
			var centerX:Number, centerY:Number;
			var maxY:Number = 0;
			for(var i:int = 0, iLen:int = _items.length; i < iLen; i++){
				item = _items[i];
				if(item){
					centerX = (i % _colCount) * _colWidth + _colWidth / 2;
					centerY = Math.floor(i / _colCount) * _rowHeight + _rowHeight / 2;
					
					item.x = centerX - item.width / 2;
					item.y = centerY - item.height / 2;
					if((centerY + _rowHeight / 2) > maxY){
						maxY = centerY + _rowHeight / 2;
					}
				}
			}
			
			if(maxY > _height || maxY < _height){
				_height = maxY
			} 
			checkHeight();
			
			dispatchChangeSizeEvent();
		}
		
		/**
		 * 取消所有已选的item
		 * 
		 * @param	evt	[MouseEvent]
		 */ 
		protected function cancelAllSelectedItemsHandler(evt:MouseEvent):void{
			if(evt.target == this){
				cancelAllSelectedItems();
			}
		}
		
		public function cancelAllSelectedItems():void{
			while(_selectedItems.length > 0){
				(_selectedItems.shift() as Item).selected = false;
			}
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.LIST_ITEM_SELECTED);
			event.data = 0;
			dispatchEvent(event);
		}
		
		/**
		 * 单击图片的响应函数
		 * 
		 * @param	evt	[MultiImageUploadEvent]
		 */
		protected function onItemClick(evt:MultiImageUploaderEvent):void{
			var item:AbstractItem = evt.target as AbstractItem;
			if (evt.data) {//增加选择
				if(item.selected){
					item.selected = false;
					deleteItemInSelectedItems(item);
				} else {
					item.selected = true;
					_selectedItems.push(item);
				}
			} else {
				while(_selectedItems.length > 0){
					(_selectedItems.shift() as Item).selected = false;
				}
				item.selected = true;
				_selectedItems.push(item);
			}
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.LIST_ITEM_SELECTED);
			event.data = _selectedItems.length;
			dispatchEvent(event);
		}
		
		/**
		 * 图片预览完成或者失败
		 */ 
		protected function onItemPreview(evt:MultiImageUploaderEvent):void {
			if(evt.type == MultiImageUploaderEvent.ITEM_PREVIEW_COMPLETE){
				var item:AbstractItem = evt.target as AbstractItem;
				if(item){
					_2bUploadedItems.push(item);
				}
			} else if(evt.type == MultiImageUploaderEvent.ITEM_PREVIEW_ERROR_IN_FILESIZE){
				var i:int = 0, iLen:int = getAllItems().length;
				while(i < iLen){
					if(evt.target == getItemAt(i)){
						break;
					}
					i++;
				}
				if(Config.EXCEED_FILE_CALLBACK && ExternalInterface.available){
					ExternalInterface.call(Config.EXCEED_FILE_CALLBACK, {"index":i, "name":(evt.target as AbstractItem).data.filename, "size":(evt.target as AbstractItem).data.filesize});
				}
			}
			dispatchEvent(new MultiImageUploaderEvent(MultiImageUploaderEvent.LIST_ITEM_REVIEW_COMPLETE));
		}
		
		protected function deleteItemInSelectedItems(item:AbstractItem):void{
			_selectedItems = _selectedItems.filter(function(obj:*, index:int, array:Array):Boolean{
				if(obj != item){
					return true;
				}
				return false;
			});
		}
		
		protected function dispatchChangeSizeEvent():void{
			// 派发事件
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.LIST_SIZE_CHANGE);
			event.data = {"width":_width, "height":_height};
			dispatchEvent(event);
		}
		
		protected function checkHeight():void{
			_height = (_height < _minHeight) ? _minHeight : _height;
		}
	}
}