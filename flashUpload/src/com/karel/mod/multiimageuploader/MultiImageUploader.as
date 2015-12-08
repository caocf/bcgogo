package com.karel.mod.multiimageuploader {
import com.adobe.serialization.json.JSONDecoder;
import com.adobe.serialization.json.JSONEncoder;
import com.karel.external.JSCall;
import com.karel.log.JSLog;
import com.karel.mod.multiimageuploader.config.Config;
import com.karel.mod.multiimageuploader.event.FileSelectorEvent;
import com.karel.mod.multiimageuploader.event.MultiImageUploaderEvent;
import com.karel.mod.multiimageuploader.file.FileSelector;
import com.karel.mod.multiimageuploader.model.ItemVO;
import com.karel.mod.multiimageuploader.net.post.Post;

import com.karel.mod.multiimageuploader.state.UploadState;

import flash.display.Sprite;
import flash.events.Event;
import flash.external.ExternalInterface;
import flash.net.FileFilter;
import flash.net.FileReference;
import flash.system.ApplicationDomain;


/**
 * @author zhen.pan
 * */
public class MultiImageUploader extends Sprite {
    private var _status:String = UploadState.UPLOAD_READY;

    protected var _post:Post;

    protected var _fileSelector:FileSelector;

    protected var _uploadIdx:int = 0;

    protected var jsFlashInit:String = "flashInit";
    protected var jsSetJSFuncName:String = "setJSFuncName";
    protected var jsDeleteFile:String = "deleteFile";
    protected var jsDeleteAllFile:String = "deleteAllFile";
    protected var jsUploadedFileNum:String = "uploadedFileNum";
    protected var jsGetExtParam:String = "getExtParam";


    protected var _itemList:Array; // {Array of ItemVO}
    protected var _tobeUploadItemList:Array; // {Array of ItemVO}
    protected var _currentItem:ItemVO;

    private var info:* = null;

    ////////////// SET/GET方法 //////////////

    public function get uploading():Boolean {
        return _status == UploadState.UPLOAD_ONGOING;
    }

    ////////////// SET/GET方法 //////////////

    public function MultiImageUploader() {
        // 拿到flashvars的参数
        addEventListener(Event.ADDED_TO_STAGE, initVarsParams);

        // 初始化 文件数组（现在用于图片）
        _itemList = [];
        // 将要被上传的文件
        _tobeUploadItemList = [];

        initComponents();

        initFileSelector();

        ExternalInterface.addCallback("call", execFunctionByName);
        ExternalInterface.addCallback(jsFlashInit, flashInit);
        ExternalInterface.addCallback(jsSetJSFuncName, setJSFuncName);
        ExternalInterface.addCallback(jsDeleteFile, deleteFile)
        ExternalInterface.addCallback(jsDeleteAllFile, deleteAllFile);
        ExternalInterface.addCallback(jsUploadedFileNum, uploadedFileNum);
        ExternalInterface.addCallback(jsGetExtParam, getExtParam);
    }

    public function execFunctionByName(funcName:String, args:Array = null):* {
        if (funcName == "upload") {
            ExternalInterface.call("console.log", "bugg!!");
        }
        if (args) {
            return (this[funcName] as Function).apply(this, args);
        } else {
            return (this[funcName] as Function).apply(this);
        }
    }

    /**
     * 开始上传
     */
    public function upload():void {
        JSLog.debug("Call function upload");
        JSLog.debug("status is :  " + this._status);
        JSLog.debug("uploading : " + this.uploading);

        if (uploading) {
            return;
        }

        serialUpload();
    }

    /**
     *
     */
    public function flashInit():Boolean {
        return true;
    }

    public function setJSFuncName(params:Array):Boolean {
        if (!params || params.length < 7) {
            return false;
        }

        try {
            // 设置选择文件后的回调函数名
            Config.SELECT_FILE_CALLBACK = params[0];
            // 设置文件大小超出时的回调函数名
            Config.EXCEED_FILE_CALLBACK = params[1];
            // 设置删除文件后的回调函数名
            Config.DELETE_FILE_CALLBACK = params[2];
            // 设置开始上传文件后的回调函数
            Config.START_UPLOAD_CALLBACK = params[3];
            // 设置上传文件成功的回调函数
            Config.UPLOAD_COMPLETE_CALLBACK = params[4];
            // 设置上传文件错误的回调函数
            Config.UPLOAD_ERROR_CALLLBACK = params[5];
            // 设置全部上传完成的回调函数
            Config.UPLOAD_ALL_COMPLETE_CALLBACK = params[6];
        } catch (e:Error) {
            JSLog.debug("Actionscript Error: " + e.toString());
        }
        return true;
    }

    /**
     * 构造fileselector
     * 构造list和post
     */
    protected function initComponents():void {
        // 初始化post
        _post = new Post();
        _post.addEventListener(MultiImageUploaderEvent.POST_SET_PROGRESS, changeProgressHandler);
        _post.addEventListener(MultiImageUploaderEvent.POST_UPLOAD_FAIL, uploadCompleteHandler);
        _post.addEventListener(MultiImageUploaderEvent.POST_UPLOAD_SUCCESS, uploadCompleteHandler);
    }

    /**
     * 构造fileselector
     */
    protected function initFileSelector():void {
        // 初始化fileselector
        var fileTypeObj:Object = (new JSONDecoder((Config.UPLOAD_FILE_TYPE as String ), false)).getValue();
        var type:FileFilter = new FileFilter(fileTypeObj.description, fileTypeObj.extension, fileTypeObj.extension);
        _fileSelector = new FileSelector();
        _fileSelector.addFileType(type);
        _fileSelector.addEventListener(FileSelectorEvent.SELECT_FILES, selectFilesHandler);
        _fileSelector.addEventListener(FileSelectorEvent.CANCEL_FILES, cancelFilesHandler);
    }


    /**
     * 定义ItemVO，若要使用自己的ItemVO，需要将你的ItemVO继承自ItemVO
     * 然后重写customizeYourItemVO方法
     */
    protected function customizeYourItemVO(fr:FileReference):ItemVO {
        ItemVO;
        var ItemVOClass:Class = ApplicationDomain.currentDomain.getDefinition("com.karel.mod.multiimageuploader.model.ItemVO") as Class;
        var itemVO:ItemVO = new ItemVOClass(fr);
        return itemVO;
    }

    public function selectFiles():void {
        JSLog.debug("_itemList.length : " + _itemList.length);
        JSLog.debug("_itemList array: " + _itemList.toString());
        if (_itemList.length < Config.MAX_FILE_NUM) {
            _fileSelector.browse();
        } else {
            dispatchEvent(new FileSelectorEvent(FileSelectorEvent.CANCEL_FILES));
        }
    }

    protected function cancelFilesHandler(evt:FileSelectorEvent):void {
        dispatchEvent(new FileSelectorEvent(FileSelectorEvent.CANCEL_FILES));
    }

    protected function selectFilesHandler(evt:FileSelectorEvent):void {
        this._status = UploadState.UPLOAD_READY;
        JSLog.debug("selectFilesHandler");

        // jsFileList回调js函数的参数
        var jsFileInfoList:Array = [];
        // 当前已有图片数量
        var currNum:int = _itemList.length;

        var files:Array = evt.files;

        JSLog.debug("files.length : " + files.length);

        for (var i:int = 0; i < files.length; i++) {
            var fr:FileReference = files[i];
            var itemVO:ItemVO = customizeYourItemVO(fr);
            JSLog.debug(itemVO.toString());
            JSLog.debug((fr.data == null).toString());
            _itemList.push(itemVO);
            _tobeUploadItemList.push(itemVO);

            jsFileInfoList.push({"index": currNum + i, "name": itemVO.filename, "size": itemVO.filesize});
        }

        JSLog.debug(JSON.stringify(jsFileInfoList));
        // 执行选择文件后的回调函数
        JSCall.call(Config.SELECT_FILE_CALLBACK, [jsFileInfoList]);

        // 自动上传
        upload();
    }

    public function deleteFile(index:int):Boolean {
        if (!_itemList[index]) return false;

        var realFileIndex:int = index - Config.CURRENT_ITEM_NUM;
        if(realFileIndex >= 0) {
            _fileSelector.deleteFileByIndex(realFileIndex);
        }

        if((index + 1) <= Config.CURRENT_ITEM_NUM) {
            Config.CURRENT_ITEM_NUM--;
        }

        _itemList.splice(index, 1);

        JSCall.call(Config.DELETE_FILE_CALLBACK, [index]);
        return true;
    }

    public function deleteAllFile():Boolean {
        if (!_itemList || _itemList.length <= 0) return false;

        _fileSelector.deleteFilesAll();
        _itemList = [];

        JSCall.call(Config.DELETE_FILE_CALLBACK, ["all"]);
        return true;
    }

    public function uploadedFileNum():int {
        return _itemList ? _itemList.length : 0;
    }

    public function getExtParam():String {
        return (new JSONEncoder(Config.UPLOAD_PARAMS)).getString();
    }

    /**
     * 顺序上传：
     *     一个接着一个, 这样做的好处不用说 处理简单， 但另一方面也是为了 AVM 虚拟机运行稳定，
     * 在早期版本的 FlashPlayer 中多个线程同上传很不稳定
     */
    protected function serialUpload():void {
        JSLog.debug("Call function serialUpload");
        JSLog.debug("_tobeUploadItemList.length : " + _tobeUploadItemList.length);

        if (_tobeUploadItemList.length > 0) {
            try {
                _currentItem = _tobeUploadItemList.shift() as ItemVO;
                JSLog.debug(_currentItem.toString());
            } catch (e:Error) {
                JSLog.debug("Array unshift item failed!");
            }
            if (_currentItem) {
                this._status = UploadState.UPLOAD_ONGOING;
                JSLog.debug("Start Uploading file index :  " + _itemList.indexOf(_currentItem));
                JSLog.debug(_currentItem.toString());
                _post.post(_currentItem);
                JSCall.call(Config.START_UPLOAD_CALLBACK, [
                    {"name": _currentItem.filename, "size": _currentItem.filesize}
                ]);
            }
        } else {
            JSCall.call(Config.UPLOAD_ALL_COMPLETE_CALLBACK);
            dispatchEvent(new MultiImageUploaderEvent(MultiImageUploaderEvent.UPLOAD_ALL_COMPLETE));
        }
    }


    //////////////////////////// Handler //////////////////////////
    protected function listSizeChangeHandler(evt:MultiImageUploaderEvent):void {

    }

    /**
     * 上传完成 （包括成功和失败两种情况）
     */
    protected function uploadCompleteHandler(evt:MultiImageUploaderEvent):void {
        if (!_currentItem) {
            this._status = UploadState.UPLOAD_COMPLETE;
            return;
        }

        if (evt.type == MultiImageUploaderEvent.POST_UPLOAD_FAIL) {
            JSLog.debug("MultiImageUploaderEvent.POST_UPLOAD_FAIL");
            JSCall.call(Config.UPLOAD_ERROR_CALLLBACK, [false, evt.data]);
        } else if (evt.type == MultiImageUploaderEvent.POST_UPLOAD_SUCCESS) {
            JSLog.debug("MultiImageUploaderEvent.POST_UPLOAD_SUCCESS");
            JSCall.call(Config.UPLOAD_COMPLETE_CALLBACK, [true, evt.data]);
        }
        _currentItem = null;

        _uploadIdx++;

        serialUpload();
    }

    protected function changeProgressHandler(evt:MultiImageUploaderEvent):void {
        // TODO
    }


    /**
     * 拿到vars里的参数
     */
    protected function initVarsParams(evt:Event):void {
        var params:Object = loaderInfo.parameters;
        Config.UPLOAD_URL = params["url"] ? params["url"] : Config.UPLOAD_URL;
        Config.UPLOAD_FILE_TYPE = params["fileType"] ? params["fileType"] : Config.UPLOAD_FILE_TYPE;
        Config.MAX_FILE_NUM = params["maxFileNum"] ? parseInt(params["maxFileNum"], 10) : Config.MAX_FILE_NUM;
        Config.MAX_FILE_SIZE = params["maxFileSize"] ? parseInt(params["maxFileSize"], 10) : Config.MAX_FILE_SIZE;
        Config.UPLOAD_DATAFIELD_NAME = params["dataFieldName"] ? params["dataFieldName"] : Config.UPLOAD_DATAFIELD_NAME;
        Config.PIC_DESC_FIELD_NAME = params["descFieldName"] ? params["descFieldName"] : Config.PIC_DESC_FIELD_NAME;
        Config.DUPLICATED_CHOOSE = params["duplicated"] ? parseInt(params["duplicated"], 10) : Config.DUPLICATED_CHOOSE;

        // 设置 debug 信息
        JSLog.DEBUG = params["debug"];

        // 自定义参数
        if (params["ext"]) {
            Config.UPLOAD_PARAMS = (new JSONDecoder((params["ext"] as String), false)).getValue() || Config.UPLOAD_PARAMS;
        }

        var js2asFlashReadyFnFullName:String = params["js2asFlashReadyFn"];
        JSCall.call(js2asFlashReadyFnFullName);


        if(params["currentItemNum"] && parseInt(params["currentItemNum"])) {
            Config.CURRENT_ITEM_NUM = parseInt(params["currentItemNum"]);
            _itemList = new Array(Config.CURRENT_ITEM_NUM);
            for (var i:int = 0; i < _itemList.length; i++) {
                _itemList[i] = "empty";
            }
        }
    }
}
}