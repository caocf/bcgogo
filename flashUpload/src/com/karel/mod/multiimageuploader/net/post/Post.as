package com.karel.mod.multiimageuploader.net.post {
import baidu.lib.utils.UploadPostHelper;

import com.karel.log.JSLog;

import com.karel.mod.multiimageuploader.config.Config;
import com.karel.mod.multiimageuploader.event.MultiImageUploaderEvent;
import com.karel.mod.multiimageuploader.model.ItemVO;

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.events.TimerEvent;
import flash.net.URLLoader;
import flash.net.URLRequest;
import flash.net.URLRequestHeader;
import flash.net.URLRequestMethod;
import flash.net.URLVariables;
import flash.utils.Timer;

[Event(name="post_set_progress", type="com.karel.mod.multiimageuploader.event.MultiImageUploaderEvent")]

[Event(name="post_upload_fail", type="com.karel.mod.multiimageuploader.event.MultiImageUploaderEvent")]

[Event(name="post_upload_success", type="com.karel.mod.multiimageuploader.event.MultiImageUploaderEvent")]

/**
 * 在这里 我们使用到了 baidu 的 开源 lib， 说开源，只是开放使用却没有源代码， 百度啊。。。
 * @author zhen.pan
 * */
public class Post extends EventDispatcher {
    protected var _data:ItemVO;
    protected var _simulationTimer:Timer;
    protected var _percent:Number = 0;
    protected var _simulationStep:Number = 0;

    public function set data(value:ItemVO):void {
        _data = value;
    }

    public function Post() {

    }

    /**
     * 上传
     */
    public function post(data:ItemVO):void {
        _data = data ? data : _data;
        if (_data) {
            postData();
        } else {
            trace("无数据!");
        }
    }

    protected function reset():void {
        _percent = 0.5;
        _simulationStep = 0.05;
    }

    /**
     * 真正的上传
     * 上传的逻辑是：
     * 如果没有旋转
     *        如果文件体积小于最大体积
     *            那么直接上传
     *        如果文件体积大于最大体积
     *            那么先压缩再上传
     * 如果旋转了
     *        一定压缩后再上传
     */
    protected function postData():void {
        JSLog.debug("start post");
        JSLog.debug(_data.toString());
        reset();

        try {
            // 初始化variables
            var variables:URLVariables = new URLVariables();
            // 把AbstractConfig.UPLOAD_PARAMS里的内容放到variables中
            for (var pro:String in Config.UPLOAD_PARAMS) {
                variables[pro] = Config.UPLOAD_PARAMS[pro];
            }

            variables[Config.PIC_DESC_FIELD_NAME] = _data.description;

            JSLog.debug(Config.UPLOAD_PARAMS.toString());
            JSLog.debug(variables.toString());
            JSLog.debug(Config.UPLOAD_URL);

            // 初始化request
            var request:URLRequest = new URLRequest(Config.UPLOAD_URL);
            request.method = URLRequestMethod.POST;

            JSLog.debug("set data to urlRequest!");
            try {
                request.data = UploadPostHelper.getPostDataX(_data.filename, _data.rawData, Config.UPLOAD_DATAFIELD_NAME, variables);
            } catch (e:Error) {
                JSLog.debug(e.toString());
                JSLog.debug("GetPostDataX error!");
            }
            request.requestHeaders.push(new URLRequestHeader('Cache-Control', 'no-cache'));
            request.requestHeaders.push(new URLRequestHeader('Content-Type', 'multipart/form-data; boundary=' + UploadPostHelper.getBoundary()));
            var loader:URLLoader = new URLLoader();
            //loader.dataFormat = URLLoaderDataFormat.BINARY;
            loader.addEventListener(Event.COMPLETE, onFileUploaded);
            loader.addEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
            loader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
        } catch (e:Error) {
            JSLog.debug("Post Exception parameters errors !! ");
        }

        loader.load(request);

        // 模拟进度 , 这个原因是使用 URLLoader 进行上传时，嫉妒
        _simulationTimer = new Timer(50);
        _simulationTimer.addEventListener(TimerEvent.TIMER, simulateProgress);
        _simulationTimer.start();
    }

    /**
     * 通用文件上传成功
     * @param    evt    [Event]
     */
    protected function onFileUploaded(evt:Event):void {
        var loader:URLLoader = evt.target as URLLoader;
        loader.removeEventListener(Event.COMPLETE, onFileUploaded);
        loader.removeEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
        loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
        if (_simulationTimer) {
            if (_simulationTimer.running) {
                _simulationTimer.stop()
            }
            _simulationTimer.removeEventListener(TimerEvent.TIMER, simulateProgress);
            _simulationTimer = null;
        }
        //获取结果
        //var resultString:String = loader.data;
        var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_SUCCESS);
        event.data = {"filename": _data.filename, "filesize": _data.filesize, "filetype": _data.filetype, "info": loader.data};
        dispatchEvent(event);
    }

    protected function simulateProgress(evt:TimerEvent):void {
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
     * @param    evt    [Event]
     */
    protected function onFileUploadError(evt:Event):void {
        var loader:URLLoader = evt.target as URLLoader;
        loader.removeEventListener(Event.COMPLETE, onFileUploaded);
        loader.removeEventListener(IOErrorEvent.IO_ERROR, onFileUploadError);
        loader.removeEventListener(SecurityErrorEvent.SECURITY_ERROR, onFileUploadError);
        if (_simulationTimer) {
            if (_simulationTimer.running) {
                _simulationTimer.stop();
            }
            _simulationTimer.removeEventListener(TimerEvent.TIMER, simulateProgress);
            _simulationTimer = null;
        }
        var event:MultiImageUploaderEvent = new MultiImageUploaderEvent(MultiImageUploaderEvent.POST_UPLOAD_FAIL);
        event.data = {"filename": _data.filename, "filesize": _data.filesize, "filetype": _data.filetype, "info": loader.data};
        dispatchEvent(event);
    }


}
}