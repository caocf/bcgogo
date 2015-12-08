/**
 * @author zhen.pan
 */
package com.karel.mod.multiimageuploader.view {
import asserts.BigBtn;

import flash.display.Sprite;
import flash.events.MouseEvent;

public class AddButton extends Sprite{
    protected var _bgButton:BigBtn;

    public function AddButton() {
        _bgButton = new BigBtn();
        addChild(_bgButton);

        _bgButton.stop();

        _bgButton.addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        _bgButton.addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
        _bgButton.buttonMode = true;
    }

    private function mouseOverHandler(evt:MouseEvent):void {
        _bgButton.gotoAndStop(2);
    }

    private function mouseOutHandler(evt:MouseEvent):void {
        _bgButton.gotoAndStop(1);
    }
}
}
