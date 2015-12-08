package baidu.flash4ge.list
{
	import baidu.flash4ge.item.PlusItem;
	import baidu.mod.multiimageuploader.item.AbstractItem;
	import baidu.mod.multiimageuploader.item.Item;
	import baidu.mod.multiimageuploader.list.List;
	
	public class MyList extends List
	{
		public function MyList(width:Number, height:Number, colWidth:Number, rowHeight:Number)
		{
			super(width, height, colWidth, rowHeight);
		}
		
		/**
		 * 添加Item
		 * @param	item	[AbstractItem]
		 */ 
		override public function addItem(item:AbstractItem):void{
			if(item is Item){
				_items.push(item);
				_2bPreviewedItems.push(item);
			}
			var idx:int = (item is Item) ? _items.length - 1 : _items.length;
			var centerX:Number = (idx % _colCount) * _colWidth + _colWidth / 2;
			var centerY:Number = Math.floor(idx / _colCount) * _rowHeight + _rowHeight / 2;
			
			item.x = centerX - item.width / 2;
			item.y = centerY - item.height / 2;
			addChild(item);
			
			_height = centerY + _rowHeight / 2;
			
			checkHeight();
			
			dispatchChangeSizeEvent();
		}
		
		public function removePlusItem():void{
			if(numChildren > 0){
				removeChildAt(numChildren - 1);
			}
			adjustLayout();
		}
		
		/**
		* adjustLayout根据_width _height _rowHeight _colWidth四个属性调整自身的布局和item的位置
		*/ 
		override protected function adjustLayout():void {
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
			
			if(numChildren > 0){
				var plusItem:PlusItem = getChildAt(numChildren - 1) as PlusItem;
				if(plusItem){
					centerX = (i % _colCount) * _colWidth + _colWidth / 2;
					centerY = Math.floor(i / _colCount) * _rowHeight + _rowHeight / 2;
					
					plusItem.x = centerX - plusItem.width / 2;
					plusItem.y = centerY - plusItem.height / 2;
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
	}
}