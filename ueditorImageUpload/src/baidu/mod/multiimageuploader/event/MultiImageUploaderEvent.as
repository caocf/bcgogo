package baidu.mod.multiimageuploader.event
{
	import flash.events.Event;
	
	public class MultiImageUploaderEvent extends Event
	{
		// LIST事件
		public static const LIST_SIZE_CHANGE:String = "ListSizeChange";
		public static const LIST_ITEM_SELECTED:String = "ListItemSelected";
		public static const LIST_ITEM_REVIEW_COMPLETE:String = "ListItemReviewComplete";
		
		// ITEM事件
		public static const ITEM_PREVIEW_ERROR_IN_FILESIZE:String = "ItemPreviewErrorInFileSize";
		public static const ITEM_FILE_DATA_IOERROR:String = "ItemFileDataIOError";
		public static const ITEM_FILE_DATA_SECURITYERROR:String = "ItemFileDataSecurityError";
		public static const ITEM_PREVIEW_COMPLETE:String = "ItemPreviewComplete";
		public static const ITEM_PREVIEW_ERROR:String = "ItemPreviewError";
		public static const ITEM_CLICK:String = "ItemClick";
		public static const ITEM_DELETE:String = "ItemDelete";
		public static const ITEM_PREVIEW_ALLCOMPLETE:String = "ItemPreviewAllComplete";
		
		// POST事件
		public static const POST_SET_PROGRESS:String = "PostSetProgress";
		public static const POST_UPLOAD_FAIL:String = "PostUploadFail";
		public static const POST_UPLOAD_SUCCESS:String = "PostUploadSuccess";
		
		// 对外的事件
		public static const DELETE_ALL:String = "DeleteAll";
		
		//
		public static const UNKOWN_ERROR:String = "UnkownError";
		
		public var data:*;
		
		public function MultiImageUploaderEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}