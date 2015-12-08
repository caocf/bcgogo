package baidu.flash4ge.item
{
	import baidu.flash4ge.event.PlusItemEvent;
	
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import baidu.mod.multiimageuploader.item.AbstractItem;
	
	public class PlusItem extends AbstractItem
	{
		public function PlusItem(width:Number, height:Number)
		{
			super(width, height);
		}
		
		// initUI是用来初始化背景 边框
		override protected function initUI():void{
			graphics.lineStyle(1, 0xe4e4e4, 1);
			graphics.beginFill(0xffffff, 1);
			graphics.drawRect(0, 0, _width, _height);
			graphics.endFill();
			
			var background:Sprite = new Sprite();
			background.graphics.beginFill(0xefefef, 1);
			background.graphics.drawRect(2, 2, _width - 4, _height - 4);
			background.graphics.endFill();
			addChild(background);
			addEventListener(MouseEvent.CLICK, addNewPics);
			
			var icon:Plus = new Plus();
			icon.x = (_width - icon.width) >> 1;
			icon.y = (_height - icon.height) >> 1;
			addChild(icon);
		}
		
		private function addNewPics(evt:MouseEvent):void{
			dispatchEvent(new PlusItemEvent(PlusItemEvent.ADD_NEW_PICS));
		}
	}
}