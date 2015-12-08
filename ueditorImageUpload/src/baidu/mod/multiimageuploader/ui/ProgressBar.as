package baidu.mod.multiimageuploader.ui
{
	import flash.display.DisplayObject;
	import flash.display.Graphics;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	public class ProgressBar extends Sprite
	{
		// ProgressBar的宽、高
		protected var _width:Number;
		protected var _height:Number;
		// 
		protected var _bottom:DisplayObject;
		protected var _top:DisplayObject;
		protected var _info:String;
		
		/////////// SET/GET方法 ///////////
		public function set bottom(value:DisplayObject):void{
			if(_bottom && contains(_bottom)){
				removeChild(_bottom);
				_bottom = null;
			}
			_bottom = value;
			_bottom.width = _width;
			_bottom.height = _height;
			addChildAt(_bottom, 0);
		}
		
		public function get top():DisplayObject{
			return _top;
		}
		
		public function set top(value:DisplayObject):void{
			var ratio:Number = 0;
			if(_top && contains(_top)){
				ratio = _top.scaleX;
				removeChild(_top);
				_top = null;
			}
			_top = value;
//			_top.width = _width;
//			_top.height = _height;
			_top.x = (_width - _top.width) / 2;
			_top.y = (_height - _top.height) / 2;
			_top.scaleX = ratio;
			addChild(_top);
		}
		
		public function set data(value:Number):void{
			_top.scaleX = (value > 1) ? 1 : value;
		}
		/////////// SET/GET方法 ///////////
		
		public function ProgressBar(width:Number, height:Number, info:String = "")
		{
			super();
			_width = width;
			_height = height;
			_info = info;
			
			initUI();
		}
		
		protected function initUI():void{
			_top = new Shape();
			_bottom = new Shape();
			
			var g:Graphics = (_bottom as Shape).graphics;
			g.beginFill(0xcccccc, 1);
			g.drawRect(0, 0, _width, _height);
			g.endFill();
			addChild(_bottom);
			
			g = (_top as Shape).graphics;
			g.beginFill(0x3344cc, 1);
			g.drawRect(0, 0, _width, _height);
			g.endFill();
			addChild(_top);
			_top.scaleX = 0;
			
			if(_info != ""){
				var txt:TextField = new TextField();
				txt.text = _info;
				var tf:TextFormat = new TextFormat("微软雅黑", 11, 0x3344cc);
				txt.setTextFormat(tf);
				txt.autoSize = "left";
				txt.width = txt.textWidth;
				txt.height = txt.textHeight;
				txt.x = (_width - txt.width) / 2;
				txt.y = -1 * txt.height;
				addChild(txt);
			}
		}
	}
}