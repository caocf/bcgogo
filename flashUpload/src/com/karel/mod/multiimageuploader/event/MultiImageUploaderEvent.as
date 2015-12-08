package com.karel.mod.multiimageuploader.event {
import flash.events.Event;

/**
 * @author zhen.pan
 * */
public class MultiImageUploaderEvent extends Event {
    // POST 事件
    public static const POST_SET_PROGRESS:String = "PostSetProgress";
    public static const POST_UPLOAD_FAIL:String = "PostUploadFail";
    public static const POST_UPLOAD_SUCCESS:String = "PostUploadSuccess";

    // 对外的事件
    public static const DELETE_ALL:String = "DeleteAll";

    // Error 事件
    public static const UNKOWN_ERROR:String = "UnkownError";

    public static const UPLOAD_ALL_COMPLETE:String = "UploadAllComplete";

    public var data:*;

    public function MultiImageUploaderEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false) {
        super(type, bubbles, cancelable);
    }
}

}