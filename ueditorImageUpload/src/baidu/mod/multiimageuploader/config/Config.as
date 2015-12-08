package baidu.mod.multiimageuploader.config
{
	public class Config
	{
		//////////// VARS PARAMS ////////////
		// 上传的url地址
		public static var UPLOAD_URL:String = "http://test.baidu.com";
		// 上传的图片文件类型
		public static var UPLOAD_FILE_TYPE:String = "{\"description\":\"图片\", \"extension\":\"*.gif;*.jpeg;*.png;*.jpg;*.bmp\"}";
		// 上传的最大图片数
		public static var MAX_FILE_NUM:int = 32;
		// 上传的图片体积大小
		public static var MAX_FILE_SIZE:Number = 5;
		// 上传的图片大小超过多少时进行压缩
		public static var COMPRESS_FILE_SIZE:Number = 3;
		// 上传的图片大小的最大边长
		public static var COMPRESS_MAX_LENGTH:Number = 1280;
		// COMPRESS_MAX_LENGTH是针对最长边还是宽还是高；COMPRESS_SIDE=0时，针对最长边，COMPRESS_SIZE=1时，针对宽，COMPRESS_SIZE=2时，针对高
		public static var COMPRESS_SIDE:int = 0;
		// 上传的post请求中，图片数据对应的name
		public static var UPLOAD_DATAFIELD_NAME:String = "picData";
		// 上传的post请求中，图片描述对应的name
		public static var PIC_DESC_FIELD_NAME:String = "picDesc";
		// 各个应用上传特有的参数
		public static var UPLOAD_PARAMS:Object;
//		// 超出大小、限制类型、未知错误和上传成功 的提示文字
//		public static const UNDEFINED:String = "undefined";
//		public static var TIPS:Array = [UNDEFINED, UNDEFINED, UNDEFINED, UNDEFINED];
		// 是否支持GIF
		public static var SUPPORT_GIF:int = 0;
		// BG_URL
		public static var BG_URL:String = "";//"assets/background.png";
		// LIST_BG_URL
		public static var LIST_BG_URL:String = "";//"assets/list_background.png";
		// BTN_URL
		public static var BUTTTON_URL:String = "";//"assets/button.png";
		// 是否可以通过复制地址重复选择
		public static var DUPLICATED_CHOOSE:int = 0;
		//////////// VARS PARAMS ////////////
		
		//////////// CALLBACK PARAMS ////////////
		// 选择文件后回调JS的函数名
		public static var SELECT_FILE_CALLBACK:String = "";
		// 文件超出大小限制后回调JS的函数名
		public static var EXCEED_FILE_CALLBACK:String = "";
		// 删除文件后回调JS的函数名
		public static var DELETE_FILE_CALLBACK:String = "";
		// 开始上传文件后回调JS的函数名
		public static var START_UPLOAD_CALLBACK:String = "";
		// 文件上传完成后回调JS的函数名
		public static var UPLOAD_COMPLETE_CALLBACK:String = "";
		// 文件上传出错后回调JS的函数名
		public static var UPLOAD_ERROR_CALLLBACK:String = "";
		// 全部上传完成后回调JS的函数名
		public static var UPLOAD_ALL_COMPLETE_CALLBACK:String = "";
		// 多图上传组件高度改变时回调JS的函数名
		public static var HEIGHT_CHANGED_CALLBACK:String = "";
		//////////// CALLBACK PARAMS ////////////
		
		//////////// STATUS ////////////
		public static const PREVIEW_ERROR:String = "PreviewError";
		public static const PREVIEW_COMPLETE:String = "PreviewComplete";
		public static const READY_FOR_UPLOAD:String = "ReadyForUpload";
		public static const UPLOADING:String = "Uploading";
		public static const UPLOADED:String = "Uploaded";
		//////////// STATUS ////////////
	    
		//////////// 提示信息 ///////////
		public static const ERROR_SIZE:String = "error_size";
		//////////// 提示信息 ///////////
		
		public function Config()
		{
		}
	}
}