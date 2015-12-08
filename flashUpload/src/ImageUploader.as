package {
import com.karel.log.JSLog;
import com.karel.mod.multiimageuploader.MultiImageUploader;
import com.karel.mod.multiimageuploader.event.FileSelectorEvent;
import com.karel.mod.multiimageuploader.event.MultiImageUploaderEvent;
import com.karel.mod.multiimageuploader.view.AddButton;
import com.karel.mod.multiimageuploader.view.CustomizedAddButton;

import flash.display.Sprite;
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.external.ExternalInterface;
import flash.system.Security;
import flash.ui.ContextMenu;
import flash.ui.ContextMenuItem;

[SWF(frameRate="30")]
/**
 * 图片加载 flash 自定制组件
 * @author zhen.pan
 *
 */
public class ImageUploader extends Sprite {
    private var _multiImageUploader:MultiImageUploader;
//    private var _addButton:Sprite;
//    private var _addButton:AddButton;
    private var _addButton:Sprite;

    public function ImageUploader() {
        super();

        // 跨域权限
        flash.system.Security.allowDomain("*");
        flash.system.Security.allowInsecureDomain("*");

        // 调整布局
        stage.scaleMode = StageScaleMode.NO_SCALE;
        stage.align = StageAlign.TOP_LEFT;

        // 去除不需要的右键菜单
        var contextMenu:ContextMenu = new ContextMenu();
        var contextMenuItem:ContextMenuItem = new ContextMenuItem("Powered by zhen.pan", true);
        contextMenu.customItems.push(contextMenuItem);
        contextMenu.hideBuiltInItems();
        this.contextMenu = contextMenu;

        _multiImageUploader = new MultiImageUploader();
        _multiImageUploader.addEventListener(MultiImageUploaderEvent.UPLOAD_ALL_COMPLETE, uploadAllCompleteHandler);
        _multiImageUploader.addEventListener(FileSelectorEvent.CANCEL_FILES, cancelFileHandler);

        addEventListener(Event.ENTER_FRAME, checkReadyHandler);
        // 构造函数中 主动调用一次的目的是：
        //     假设在  绑定事件之后flash 就已经初始化好了 stageWidth 和 stageHeight ，那么我们立即执行初始化。
        //     flash 的运行机制是一个弹性跑道机制，具体可以参看博文
        // @link http://www.craftymind.com/2008/04/18/updated-elastic-racetrack-for-flash-9-and-avm2/
        // @link http://bbs.9ria.com/thread-45497-1-1.html
        checkReadyHandler(null);
    }

    private function init():void {
        // log
        JSLog.debug("Swf ImageUploader init start");

        var info:Object = loaderInfo.parameters;

        stage.stageWidth  = parseFloat(info.width);
        stage.stageHeight = parseFloat(info.height);

        if(info["buttonBgUrl"]
            && info["buttonBgUrl"] != ""
            && info["buttonOverBgUrl"]
            && info["buttonOverBgUrl"] != "") {
            createAddButton( (info["transparent"] == "true"), info["buttonBgUrl"], info["buttonOverBgUrl"], parseInt(info.width), parseInt(info.height));
        } else {
            createAddButton( (info["transparent"] == "true") );
        }
        stage.addChild(_multiImageUploader);

        ExternalInterface.addCallback("resetAddButton", resetAddButton);
    }

    public function resetAddButton(isTransparent:Boolean = false, normalUrl:String = null, overUrl:String = null, width:int = 0, height:int = 0):void {
        _addButton.removeEventListener(MouseEvent.CLICK, addButtonClickHandler);
        _addButton = null;

        createAddButton(isTransparent, normalUrl, overUrl, width, height);
    }

    private function createAddButton(isTransparent:Boolean = false, normalUrl:String = null, overUrl:String = null, width:int = 0, height:int = 0):void {
        // addButton
        if(normalUrl && overUrl) {
            _addButton = new CustomizedAddButton(normalUrl, overUrl, width, height);
        } else {
            _addButton = new AddButton();
        }

        JSLog.debug(_addButton.toString());
        JSLog.debug("buttonBgUrl : " + normalUrl);
        JSLog.debug("buttonOverBgUrl : " + overUrl);

        this.addChild(_addButton);
        JSLog.debug("addButton added to display object list!");
        _addButton.addEventListener(MouseEvent.CLICK, addButtonClickHandler);

        _addButton.alpha    = isTransparent ? 0 : 1;
        _addButton.x        = 0;
        _addButton.y        = 0;
        _addButton.buttonMode = true;
    }

    private function addButtonClickHandler(evt:MouseEvent):void {
        _addButton.buttonMode = false;
        _addButton.removeEventListener(MouseEvent.CLICK, addButtonClickHandler);
        _multiImageUploader.selectFiles();
    }

    private function checkReadyHandler(evt:Event):void {
        var w:Number = stage.stageWidth,
            h:Number = stage.stageHeight;

        JSLog.debug(w.toString() + "    " + h.toString());

        if (w > 0 && h > 0) {
            removeEventListener(Event.ENTER_FRAME, checkReadyHandler);
            init();
        }
    }

    private function uploadAllCompleteHandler(evt:MultiImageUploaderEvent):void {
        enableAddButton();
    }

    private function cancelFileHandler(evt:FileSelectorEvent):void {
        enableAddButton();
    }

    private function enableAddButton():void {
        _addButton.buttonMode = true;
        _addButton.addEventListener(MouseEvent.CLICK, addButtonClickHandler);
    }
}
/*end Class ImageUploader.as*/


}