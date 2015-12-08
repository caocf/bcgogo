package com.karel.mod.multiimageuploader.config
{
    /**
     *
     * @author zhen.pan
     *
     * */
	public class Config
	{
        // 上传的url地址
        public static var UPLOAD_URL:String = "http://v0.api.upyun.com/bcgogo-develop/";
        // 上传的图片文件类型
        public static var UPLOAD_FILE_TYPE:String = "{\"description\":\"Image Files\", \"extension\":\"*.jpeg;*.png;*.jpg;\"}";
        // 上传的最大图片数
        public static var MAX_FILE_NUM:int = 4;
        // 上传的图片体积大小
        public static var MAX_FILE_SIZE:Number = 3;
        // curent item num
        public static var CURRENT_ITEM_NUM:int = 0;
        // 上传的post请求中，图片数据对应的name
        public static var UPLOAD_DATAFIELD_NAME:String = "file";
        // 上传的post请求中，图片描述对应的name
        public static var PIC_DESC_FIELD_NAME:String = "picDesc";
        // 各个应用上传特有的参数
        public static var UPLOAD_PARAMS:Object = {
            "policy":"eyJzYXZlLWtleSI6Ii97eWVhcn0ve21vbn0ve2RheX0vMTAwMDAwMTAwMDY0OTA1NDkve2hvdXJ9e21pbn17c2VjfS17ZmlsZW1kNX17LnN1ZmZpeH0iLCJleHBpcmF0aW9uIjoxMzc4MTE5MTIyMTg0LCJhbGxvdy1maWxlLXR5cGUiOiJqcGcsanBlZyxwbmciLCJidWNrZXQiOiJiY2dvZ28tZGV2ZWxvcCIsIngtZ21rZXJsLXRodW1ibmFpbCI6InZlcnNpb24uMSIsImNvbnRlbnQtbGVuZ3RoLXJhbmdlIjoiMCw1MTIwMDAwIn0=",
            "signature":"1532e7c398f35306c2f992f420059dae"
        };
        // 是否可以通过复制地址重复选择
        public static var DUPLICATED_CHOOSE:int = 0;


		//////////// CALLBACK PARAMS ////////////
		// 选择文件后回调JS的函数名
		public static var SELECT_FILE_CALLBACK:String           = "";
		// 文件超出大小限制后回调JS的函数名
		public static var EXCEED_FILE_CALLBACK:String           = "";
		// 删除文件后回调JS的函数名
		public static var DELETE_FILE_CALLBACK:String           = "";
		// 开始上传文件后回调JS的函数名
		public static var START_UPLOAD_CALLBACK:String          = "";
		// 文件上传完成后回调JS的函数名
		public static var UPLOAD_COMPLETE_CALLBACK:String       = "";
		// 文件上传出错后回调JS的函数名
		public static var UPLOAD_ERROR_CALLLBACK:String         = "";
		// 全部上传完成后回调JS的函数名
		public static var UPLOAD_ALL_COMPLETE_CALLBACK:String   = "";
		// 多图上传组件高度改变时回调JS的函数名
		public static var HEIGHT_CHANGED_CALLBACK:String        = "";





		
		//////////// 提示信息 ///////////
		public static const ERROR_SIZE:String                   = "error_size";
		//////////// 提示信息 ///////////


		public function Config()
		{
			
		}
	}
}