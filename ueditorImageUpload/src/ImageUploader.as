package
{
	import baidu.flash4ge.GEMultiImageUploaderMode0;
	import baidu.flash4ge.GEMultiImageUploaderMode1;
	import baidu.mod.multiimageuploader.MultiImageUploader;
	import baidu.mod.multiimageuploader.config.Config;
	
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.external.ExternalInterface;
	import flash.system.Security;
	import flash.system.System;
	
	/**
	 * 针对GE组封装的通用多图上传组件。
	 * <p>
	 * 对外接口：
	 * <ul>
	 * 	<li>开始上传</li>
	 * 	<li>暂停上传</li>
	 * 	<li>选择文件后回调</li>
	 * 	<li>文件大小超出限制回调</li>
	 * 	<li>删除文件回到</li>
	 * 	<li>开始上传回调</li>
	 * 	<li>上传失败回调</li>
	 * 	<li>上传完成回调</li>
	 * </ul>
	 * <p>
	 * Flash初始化参数：
	 * <ul>
	 * 	<li>Flash宽度，必须设置</li>
	 * 	<li>Flash高度，必须设置</li>
	 * 	<li>单张图片占用的宽度，必须设置</li>
	 * 	<li>单张图片占用的高度，必须设置</li>
	 * 	<li>单张图片的宽度，必须设置</li>
	 * 	<li>单张图片的高度，必须设置</li>
	 * 	<li>上传POST请求中，图片数据的key值，必须设置。</li>
	 * 	<li>上传POST请求中，图片描述的key值，必须设置。</li>
	 * 	<li>允许选择的最大图片体积，默认为5M。若设该值为N，则选择图片后会对图片大小判断，如果图片大小超过N，则不预览图片，执行文件大小超出限制回调。单位为M。</li>
	 * 	<li>允许上传的最大图片体积，默认为3M。若设该值为N，则上传图片时会对图片大小判断，如果图片大小超过N，则对图片进行压缩，压缩后若还是大于N，则不上传。单位为M。</li>
	 * 	<li>允许选择的最大图片数，默认为32张</li>
	 * 	<li>允许的图片最大边长，默认为1200。若设该值为N，则上传图片时会对图片寸尺判断，如果图片尺寸为A*B，且(A < N && B < N)，则不压缩尺寸，否则会等比的压缩A、B的值。单位像素。</li>
	 *  <li>是使用下拉框，还是拉伸Flash。默认为使用下拉框</li>
	 * </ul>
	 * </p>
	 */ 
	public class ImageUploader extends Sprite
	{
		private var _multiImageUploader:MultiImageUploader;
		
		public function ImageUploader()
		{
			super();
			
			flash.system.Security.allowDomain("*");
			//flash.system.System.useCodePage = true;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			stage.align = StageAlign.TOP_LEFT;
			addEventListener(Event.ENTER_FRAME, checkReady);
			checkReady(null);
		}
		
		private function init():void{
//			ExternalInterface.call("console.log", "###");
//			flash.system.Security.loadPolicyFile("http://172.21.45.37/crossdomain.xml");
			// 拿到vars里的参数
			var params:Object = loaderInfo.parameters;
			var flashWidth:Number = params.width;
			var flashHeight:Number = params.height;
			var gridWidth:Number = params.gridWidth;
			var gridHeight:Number = params.gridHeight;
			var mode:int = params.mode;
			
			Config.BG_URL = params.backgroundUrl ? params.backgroundUrl : Config.BG_URL;
			Config.LIST_BG_URL = params.listBackgroundUrl ? params.listBackgroundUrl : Config.LIST_BG_URL;
			Config.BUTTTON_URL = params.buttonUrl ? params.buttonUrl : Config.BUTTTON_URL;
			Config.UPLOAD_FILE_TYPE = params.fileType ? params.fileType : Config.UPLOAD_FILE_TYPE;
			Config.COMPRESS_SIDE = params["compressSide"] ? parseInt(params["compressSide"], 10) : Config.COMPRESS_SIDE;
			
			// 多图上传的边框
			var border:Sprite = new Sprite();
			border.graphics.lineStyle(1, 0xbccbd2, 1);//0xbccbd2
			border.graphics.drawRect(0, 0, flashWidth, flashHeight);
			
			// 多图上传的背景
			var background:Sprite = new Sprite();
			background.graphics.beginFill(0xeeeeee, 1);
			background.graphics.drawRect(0, 0, flashWidth, flashHeight);
			background.graphics.endFill();
			
			// 初始化多图上传对象
			_multiImageUploader = mode ? new GEMultiImageUploaderMode1(flashWidth, flashHeight, gridWidth, gridHeight) : new GEMultiImageUploaderMode0(flashWidth, flashHeight, gridWidth, gridHeight);
			//_multiImageUploader = new GEMultiImageUploaderMode1(flashWidth, flashHeight, gridWidth, gridHeight);
			_multiImageUploader.background = background;
			_multiImageUploader.border = border;
			addChild(_multiImageUploader);


		}
		
		private function checkReady(evt:Event):void{
			var w : Number = stage.stageWidth;
			var h : Number = stage.stageHeight;
			if (w > 0 && h > 0) {
				removeEventListener(Event.ENTER_FRAME, checkReady);
				
				init();
			}
		}
	}
}