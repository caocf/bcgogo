package
{
	import com.karel.mod.multiimageuploader.view.CustomizedAddButton;
	
	import flash.display.Loader;
	import flash.display.Sprite;
	import flash.net.URLRequest;

	[SWF(width="300",height="300",frameRate="30")]
	public class Test extends Sprite
	{
		private var url:String = "http://www.sinaimg.cn/dy/slidenews/4_img/2013_37/704_1093387_539397.jpg";
		private var button:CustomizedAddButton;
		
		public function Test()
		{
			button = new CustomizedAddButton(url, url, 200, 200 );
			addChild(button);
			
//			var group:Sprite = new Sprite();
//			
//			var _bgNormalLoader:Loader = new Loader();
//			_bgNormalLoader.load(new URLRequest(url));
//			group.addChild(_bgNormalLoader);
//			
//			addChild(group);
		}
	}
}