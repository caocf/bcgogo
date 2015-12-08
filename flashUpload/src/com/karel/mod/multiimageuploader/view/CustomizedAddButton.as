package com.karel.mod.multiimageuploader.view {
import com.karel.log.JSLog;

import flash.display.Bitmap;

import flash.display.Loader;
import flash.display.LoaderInfo;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.MouseEvent;
import flash.events.SecurityErrorEvent;
import flash.net.URLRequest;

public class CustomizedAddButton extends Sprite {
    protected var _bgNormalLoader:Loader;
    protected var _bgOverLoader:Loader;
    protected var _width:int;
    protected var _height:int;


    public function CustomizedAddButton(bgNormalUrl:String, bgOverUrl:String, width:int, height:int):void {
        init(bgNormalUrl, bgOverUrl, width, height);
    }

    private function init(bgNormalUrl:String, bgOverUrl:String, width:int, height:int):void {
        _width = width;
        _height = height;

        _bgNormalLoader = new Loader();
        _bgNormalLoader.contentLoaderInfo.addEventListener(Event.COMPLETE, bgNormalCompleteHandler);
        _bgNormalLoader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, bgNormalIOErrorHandler);
        _bgNormalLoader.contentLoaderInfo.addEventListener(SecurityErrorEvent.SECURITY_ERROR, bgNormalSecurityErrorHandler);

        _bgOverLoader = new Loader();
        _bgOverLoader.contentLoaderInfo.addEventListener(Event.COMPLETE, bgOverCompleteHandler);
        _bgOverLoader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, bgOverIOErrorHandler);
        _bgOverLoader.contentLoaderInfo.addEventListener(SecurityErrorEvent.SECURITY_ERROR, bgOverSecurityErrorHandler);

        _bgNormalLoader.load(new URLRequest(bgNormalUrl));
        _bgOverLoader.load(new URLRequest(bgOverUrl));

        this.addEventListener(MouseEvent.MOUSE_OVER, function(event:MouseEvent):void {
            _bgNormalLoader.visible = false;
        });

        this.addEventListener(MouseEvent.MOUSE_OUT, function(event:MouseEvent):void {
            _bgNormalLoader.visible = true;
        });

        addChild(_bgOverLoader);
        addChild(_bgNormalLoader);
    }


    // bg normal events handler
    protected function bgNormalCompleteHandler(event:Event):void {
        var loaderInfo:LoaderInfo = (event.target as LoaderInfo);

        JSLog.debug("success normal bg: " + loaderInfo.url);
        JSLog.debug("" + loaderInfo.contentType);
        JSLog.debug("" + loaderInfo.content.toString());
        JSLog.debug("" + loaderInfo.content.width);
        JSLog.debug("" + loaderInfo.content.height);

		loaderInfo.content.width = _width;
		loaderInfo.content.height = _height;
    }

    protected function bgNormalIOErrorHandler(event:IOErrorEvent):void {JSLog.debug("bgNormalIOErrorHandler");}
    protected function bgNormalSecurityErrorHandler(event:SecurityErrorEvent):void {JSLog.debug("bgNormalSecurityErrorHandler");}


    // bg over events handler
    protected function bgOverCompleteHandler(event:Event):void {
        var loaderInfo:LoaderInfo = (event.target as LoaderInfo);

        JSLog.debug("success over bg: " + loaderInfo.url);
        JSLog.debug("" + loaderInfo.contentType);
        JSLog.debug("" + loaderInfo.content.toString());
        JSLog.debug("" + loaderInfo.content.width);
        JSLog.debug("" + loaderInfo.content.height);

		loaderInfo.content.width = _width;
		loaderInfo.content.height = _height;
    }

    protected function bgOverIOErrorHandler(event:IOErrorEvent):void {JSLog.debug("bgOverIOErrorHandler");}
    protected function bgOverSecurityErrorHandler(event:SecurityErrorEvent):void {JSLog.debug("bgOverSecurityErrorHandler");}


}
}
