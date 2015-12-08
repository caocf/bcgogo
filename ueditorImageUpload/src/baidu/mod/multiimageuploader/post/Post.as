package baidu.mod.multiimageuploader.post
{
	import baidu.lib.utils.UploadPostHelper;
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.MultiImageUploaderEvent;
	import baidu.mod.multiimageuploader.extra.JPEGAsyncCompleteEvent;
	import baidu.mod.multiimageuploader.extra.JPEGAsyncEncoder;
	import baidu.mod.multiimageuploader.model.ItemVO;
	
	import flash.display.BitmapData;
	import flash.events.DataEvent;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.events.TimerEvent;
	import flash.external.ExternalInterface;
	import flash.geom.Matrix;
	import flash.net.FileReference;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.net.URLRequestHeader;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	import flash.utils.ByteArray;
	import flash.utils.Timer;

	[Event(name="post_set_progress", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	
	[Event(name="post_upload_fail", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	
	[Event(name="post_upload_success", type="baidu.mod.multiimageuploader.event.MultiImageUploaderEvent")]
	
	public class Post extends EventDispatcher
	{
		protected var _data:ItemVO;
		protected var _simulationTimer:Timer;
		protected var _percent:Number = 0;
		protected var _simulationStep:Number = 0;
		
		public function set data(value:ItemVO):void{
			_data = value;
		}
		
		public function Post()
		{
			
		}
		
		/**
		 * 上传
		 */ 
		public function post(data:ItemVO):void{
			if(data) {
				_data = data;
			}
			if(_data){
				postData();
			} else {
				trace("无数据!");
			}
		}
		
		protected function reset():void{
			_percent = 0.5;
			_simulationStep = 0.05;
		}
		
		/**
		 * 真正的上传
		 * 上传的逻辑是：
		 * 如果没有旋转
		 * 		如果文件体积小于最大体积
		 * 			那么直接上传
		 * 		如果文件体积大于最大体积
		 * 			那么先压缩再上传
		 * 如果旋转了
		 * 		一定压缩后再上传
		*/ 
		protected function postData():void{
			reset();
			
			if(_data.filesize <= Config.COMPRESS_FILE_SIZE * 1024 *1024 && _data.rotation == 0 && _data.originalWidth <= Config.COMPRESS_MAX_LENGTH && _data.originalHeight <= Config.COMPRESS_MAX_LENGTH){
				// 初始化variables
				var variables:URLVariables = new URLVariables();
				// 把Config.UPLOAD_PARAMS里的内容放到variables中
				for(var pro:String in Config.UPLOAD_PARAMS){
					variables[pro] = Config.UPLOAD_PARAMS[pro];
				}
				variables[Config.PIC_DESC_FIELD_NAME] = _data.description;
				// 初始化request
				var request:URLRequest = new URLRequest(Config.UPLOAD_URL);
				if(Config.SUPPORT_GIF && _data.filetype.toLowerCase().indexOf("gif")){
					var fr:FileReference = _data.fr;
					fr.addEventListener(DataEvent.UPLOAD_COMPLETE_DATA, onGifFileUploaded);
					fr.addEventListener(ProgressEvent.PROGRESS, onGifFileUploadProgress);
					fr.addEventListener(IOErrorEvent.IO_ERROR, onGifFileUploadError);
					fr.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onGifFileUploadError);
					fr.upload(request, Config.UPLOAD_DATAFIELD_NAME);
				} else {
					request.method = URLRequestMethod.POST;
					
					request.data = UploadPostHelper.getPostDataX(_data.filename, _data.rawData, Config.UPLOAD_DATAFIELD_NAME, variables);
					request.requestHeaders.push(new URLRequestHeader('Cache-Control', 'no-cache'));
					request.requestHeaders.push(new URLRequestHeader('Content-Type', 'multipart/form-data; boundary=' + UploadPostHelper.getBoundary()));
					var loader:URLLoader = new URLLoader();
					//loader.dataFormat = URLLoaderDataFormat.BINARY;
					loader.addEventListener(Event.COMPLETE, onFileUploaded);
					loader.addEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
					loader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
					loader.load(request);
					
					// 模拟进度
					_simulationTimer = new Timer(50);
					_simulationTimer.addEventListener(TimerEvent.TIMER, simulateProgress);
					_simulationTimer.start();
				}
			} else {
				startAsyncEncode();
			}
		}
		
		protected function startAsyncEncode():void{
			_data.image.rotation = _data.rotation;
			var w:Number = _data.image.width;
			var h:Number = _data.image.height;
			
			//var s:Number = Math.min(Config.COMPRESS_MAX_LENGTH/w, Config.COMPRESS_MAX_LENGTH/h, 1);
			var s:Number = 0;
			if(Config.COMPRESS_SIDE == 1){
				s = Math.min(Config.COMPRESS_MAX_LENGTH/w, 1);
			} else if(Config.COMPRESS_SIDE == 2){
				s = Math.min(Config.COMPRESS_MAX_LENGTH/h, 1);
			} else {
				s = Math.min(Config.COMPRESS_MAX_LENGTH/w, Config.COMPRESS_MAX_LENGTH/h, 1);
			}
			var matrix:Matrix = new Matrix();
			var tx:Number = 0;
			var ty:Number = 0;
			var rotation:Number = 0;
			switch(_data.rotation){
				case 90:
					tx = w * s;
					ty = 0;
					rotation = Math.PI/2;
					break;
				case -90:
					tx = 0;
					ty = h * s;
					rotation = -Math.PI/2;
					break;
				case 180:
					tx = w*s;
					ty = h * s;
					rotation = Math.PI;
					break
				case -180:
					tx = w*s;
					ty = h * s;
					rotation = Math.PI;
					break;
				case 0:
					tx = 0;
					ty = 0;
					rotation = 0;
					break;
				default:
					tx = 0;
					ty = 0;
					rotation = 0;
			}
			matrix.createBox(s, s, rotation, tx, ty);
			var asyncEncoder:JPEGAsyncEncoder = new JPEGAsyncEncoder(80);
			asyncEncoder.PixelsPerIteration = 256;
			asyncEncoder.addEventListener(ProgressEvent.PROGRESS, updateProgress);
			asyncEncoder.addEventListener(JPEGAsyncCompleteEvent.JPEGASYNC_COMPLETE, asyncComplete);
			var bmd:BitmapData = new BitmapData(w*s, h*s, true, 0xffffff);
			bmd.draw(_data.image, matrix, null, null, null, true);
			asyncEncoder.encode(bmd);
		}
		
		protected function updateProgress(evt:ProgressEvent):void{
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_SET_PROGRESS);
			event.data = 0.5 * (evt.bytesLoaded / evt.bytesTotal);
			dispatchEvent(event);
		}
		
		protected function asyncComplete(evt:JPEGAsyncCompleteEvent):void{
			// 移除监听
			evt.target.removeEventListener(ProgressEvent.PROGRESS, updateProgress);
			evt.target.removeEventListener(JPEGAsyncCompleteEvent.JPEGASYNC_COMPLETE, asyncComplete);
			// 压缩后的图片数据
			var byteArray:ByteArray = evt.ImageData;
			// 初始化variables
			var variables:URLVariables = new URLVariables();
			// 把Config.UPLOAD_PARAMS里的内容放到variables中
			for(var pro:String in Config.UPLOAD_PARAMS){
				variables[pro] = Config.UPLOAD_PARAMS[pro];
			}
			variables[Config.PIC_DESC_FIELD_NAME] = _data.description;
			
			var request:URLRequest = new URLRequest(Config.UPLOAD_URL);
			request.method = URLRequestMethod.POST;
			
			// 据说png、gif压缩后，体积还会增大...
			// 先判断一下，是否超过了上传的大小限制，如果超过了，就不上传了
			if(byteArray.length > Config.MAX_FILE_SIZE * 1024 * 1024){
				var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_FAIL);
				event.data = {"filename":_data.filename, "filesize":_data.filesize, "filetype":_data.filetype, "info":Config.ERROR_SIZE};
				dispatchEvent(event);
				return;
			} else if(byteArray.length > _data.filesize && _data.rotation == 0){//压缩完了判断是不是大于原始未压缩的大小.....
				// 如果压缩后，体积变得 更大，并且未旋转，那么上传压缩前的数据
				request.data = UploadPostHelper.getPostDataX(_data.filename, _data.rawData, Config.UPLOAD_DATAFIELD_NAME, variables);
			} else {
				// 1 压缩后体积减少
				// 2压缩后体积变大，但是有旋转
				// 这两种情况都是上传压缩后的数据
				request.data = UploadPostHelper.getPostDataX(_data.filename, byteArray, Config.UPLOAD_DATAFIELD_NAME, variables);
			}
			
			request.requestHeaders.push(new URLRequestHeader('Cache-Control', 'no-cache'));
			request.requestHeaders.push(new URLRequestHeader('Content-Type', 'multipart/form-data; boundary=' + UploadPostHelper.getBoundary()));
			var loader:URLLoader = new URLLoader();
			//loader.dataFormat = URLLoaderDataFormat.BINARY;
			loader.addEventListener(Event.COMPLETE, onFileUploaded);
			loader.addEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
			loader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
			loader.load(request);
			
			// 模拟进度
			_simulationTimer = new Timer(50);
			_simulationTimer.addEventListener(TimerEvent.TIMER, simulateProgress);
			_simulationTimer.start();
		}
		
		/**
		 * 通用文件上传成功
		 * @param	evt	[Event]
		 */
		protected function onFileUploaded(evt:Event):void{
			var loader:URLLoader = evt.target as URLLoader;
			loader.removeEventListener(Event.COMPLETE, onFileUploaded);
			loader.removeEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
			loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
			
			if(_simulationTimer){
				if(_simulationTimer.running){
					_simulationTimer.stop()
				}
				_simulationTimer.removeEventListener(TimerEvent.TIMER, simulateProgress);
				_simulationTimer = null;
			}
			
			//获取结果
			//var resultString:String = loader.data;
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_SUCCESS);
			event.data = {"filename":_data.filename, "filesize":_data.filesize, "filetype":_data.filetype, "info":loader.data};
			dispatchEvent(event);
		}
		
		protected function simulateProgress(evt:TimerEvent):void{
			if (_percent < 0.9) {
				_percent += _simulationStep;
			} else {
				_simulationTimer.stop()
				_simulationTimer.removeEventListener(TimerEvent.TIMER, simulateProgress);
				_simulationTimer = null;
			}
			
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_SET_PROGRESS);
			event.data = _percent;
			dispatchEvent(event);
		}
		
		/**
		 * 通用文件上传失败
		 * @param	evt	[Event]
		 */ 
		protected function onFileUploadError(evt:Event):void{
			var loader:URLLoader = evt.target as URLLoader;
			loader.removeEventListener(Event.COMPLETE, onFileUploaded);
			loader.removeEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
			loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
			
			if(_simulationTimer){
				if(_simulationTimer.running){
					_simulationTimer.stop()
				}
				_simulationTimer.removeEventListener(TimerEvent.TIMER, simulateProgress);
				_simulationTimer = null;
			}
			
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_FAIL);
			event.data = {"filename":_data.filename, "filesize":_data.filesize, "filetype":_data.filetype, "info":loader.data};
			dispatchEvent(event);
		}
		
		/**
		 * gif文件上传成功
		 * @param 	evt	[DataEvent]
		 */ 
		protected function onGifFileUploaded(evt:DataEvent):void{
			var fr:FileReference = evt.target as FileReference;
			fr.removeEventListener(DataEvent.UPLOAD_COMPLETE_DATA, onGifFileUploaded);
			fr.removeEventListener(ProgressEvent.PROGRESS, onGifFileUploadProgress);
			fr.removeEventListener(IOErrorEvent.IO_ERROR, onGifFileUploadError);
			fr.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onGifFileUploadError);
			
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_SUCCESS);
			//event.data = _data;
			event.data = {"filename":_data.filename, "filesize":_data.filesize, "filetype":_data.filetype, "info":evt.data};
			dispatchEvent(event);
		}
		
		/**
		 * gif文件上传进度
		 * @param	evt	[ProgressEvent]
		 */ 
		protected function onGifFileUploadProgress(evt:ProgressEvent):void{
			var ratio:Number = evt.bytesLoaded / evt.bytesTotal;
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_SET_PROGRESS);
			event.data = ratio;
			dispatchEvent(event);
		}
		
		/**
		 * gif文件上传失败
		 * @param	evt	[Event]
		 */ 
		protected function onGifFileUploadError(evt:Event):void{
			var fr:FileReference = evt.target as FileReference;
			fr.removeEventListener(DataEvent.UPLOAD_COMPLETE_DATA, onGifFileUploaded);
			fr.removeEventListener(ProgressEvent.PROGRESS, onGifFileUploadProgress);
			fr.removeEventListener(IOErrorEvent.IO_ERROR, onGifFileUploadError);
			fr.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onGifFileUploadError);
			
			var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_FAIL);
			event.data = {"filename":_data.filename, "filesize":_data.filesize, "filetype":_data.filetype, "info":evt.toString()};
			event.data = _data;
			dispatchEvent(event);
		}
	}
}