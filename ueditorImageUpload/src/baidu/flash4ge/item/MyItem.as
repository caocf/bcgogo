package baidu.flash4ge.item
{
	import baidu.mod.multiimageuploader.item.Item;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	public class MyItem extends Item
	{
		public function MyItem(width:Number, height:Number)
		{
			super(width, height);
		}
		
		public function showSuccessTip(tip:String):void{
			_description.text = "";
			
			var txt:TextField = new TextField();
			
			txt.text = tip;
			
			var tf:TextFormat = new TextFormat("微软雅黑", 11, 0x0000ff);
			txt.setTextFormat(tf);
			txt.autoSize = "left";
			txt.width = txt.textWidth;
			txt.height = txt.textHeight;
			txt.x = (_width - txt.width) / 2;
			txt.y = _height - 22;
			txt.height = 22;
			addChild(txt);
		}
		
		public function showErrorTip(tip:String):void{
			_description.text = "";
			
			var txt:TextField = new TextField();
			
			txt.text = tip;
			
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
	}
}