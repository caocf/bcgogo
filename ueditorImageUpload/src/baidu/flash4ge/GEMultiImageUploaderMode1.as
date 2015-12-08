package baidu.flash4ge
{
	import baidu.flash4ge.list.MyList;
	import baidu.mod.multiimageuploader.MultiImageUploader;
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.post.Post;
	
	import flash.display.Graphics;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.display.Shape;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.external.ExternalInterface;
	import flash.net.URLRequest;
	
	public class GEMultiImageUploaderMode1 extends GEMultiImageUploader
	{
		public function GEMultiImageUploaderMode1(width:Number, height:Number, gridWidth:Number, gridHeight:Number)
		{
			super(width, height, gridWidth, gridHeight);
			trace("GEMultiImageUploaderMode1");
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
			g.beginFill(0xffffff, 0);
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
			
			if(Config.LIST_BG_URL == ""){
				return;
			}
			var loader:Loader = new Loader();
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onListBgComplete);
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onListBgError);
			loader.load(new URLRequest(Config.LIST_BG_URL));
		}
		
		protected function onListBgComplete(evt:Event):void{
			(evt.target as LoaderInfo).removeEventListener(Event.COMPLETE, onListBgComplete);
			(evt.target as LoaderInfo).removeEventListener(IOErrorEvent.IO_ERROR, onListBgError);
			_list.background = (evt.target as LoaderInfo).loader;
		}
		
		protected function onListBgError(evt:IOErrorEvent):void{
			(evt.target as LoaderInfo).removeEventListener(Event.COMPLETE, onListBgComplete);
			(evt.target as LoaderInfo).removeEventListener(IOErrorEvent.IO_ERROR, onListBgError);
		}
		
		/**
		 * 重写自己的listSizeChange响应函数
		 */ 
		override protected function listSizeChange(evt:MultiImageUploaderEvent):void{
			// 这里的是+30是和下面这个行代码的-30一致
			//_list = new List(_width, _height - 30, 120, 135);
			_background.height = evt.data.height + 30;
			_border.height = evt.data.height + 30;
			_list.background.height = evt.data.height + 30;
			
			// 在jsFunChangeHeight这个函数里将flash的高度改成evt.data.height的值
			ExternalInterface.call(Config.HEIGHT_CHANGED_CALLBACK, evt.data.height + 30);
		}
	}
}