package baidu.flash4ge
{
	import baidu.flash4ge.list.MyList;
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.FileSelectorEvent;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.item.AbstractItem;
	import baidu.mod.multiimageuploader.item.Item;
	import baidu.mod.multiimageuploader.list.List;
	import baidu.mod.multiimageuploader.model.ItemVO;
	import baidu.mod.multiimageuploader.post.Post;
	import baidu.ui.containers.ScrollPane;
	import baidu.ui.controls.Button;
	
	import flash.display.Graphics;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.display.Shape;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.MouseEvent;
	import flash.net.URLRequest;
	import flash.system.ApplicationDomain;
	
	public class GEMultiImageUploaderMode0 extends GEMultiImageUploader
	{
		private var _scrollPane:ScrollPane;
		
		public function GEMultiImageUploaderMode0(width:Number, height:Number, gridWidth:Number, gridHeight:Number)
		{
			super(width, height, gridWidth, gridHeight);
			trace("GEMultiImageUploaderMode0");
		}
		
		/**
		 * 构造fileselector
		 * 构造list和post
		 */ 
		override protected function initComponents():void{
			// 创建scrollpane
			ScrollPane_Skin;
			ScrollBar_Skin;
			_scrollPane = new ScrollPane();
			_scrollPane.setPosition(0, 30);
			_scrollPane.setSize(_width, _height - 30);
			addChild(_scrollPane);
			_scrollPane.hScrollPolicy = "none";
			_scrollPane.vScrollPolicy = "auto";
			
			// 初始化list
			_list = new MyList(_width, _height - 30, _gridWidth, _gridHeight);
//			addChild(_list);
//			_list.x = 0;
//			_list.y = 30;
			var listBg:Shape = new Shape();
			var g:Graphics = listBg.graphics;
			g.beginFill(0xffffff, 0);
			g.drawRect(0, 0, _list.width, _list.height);
			g.endFill();
			_list.background = listBg;
			_list.addEventListener(MultiImageUploaderEvent.LIST_ITEM_SELECTED, listSelected);
			_list.addEventListener(MultiImageUploaderEvent.LIST_ITEM_REVIEW_COMPLETE, serialPreview);
			_list.addEventListener(MultiImageUploaderEvent.LIST_SIZE_CHANGE, listSizeChange);
			_scrollPane.content = _list;
			_scrollPane.update();
			
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
			_scrollPane.update();
		}
	}
}