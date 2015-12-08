package com.karel.log {
import flash.external.ExternalInterface;


/**
 * @author zhen.pan
 */
public class JSLog {
    public static var DEBUG:String = "on";

    public function JSLog() {
    }

    public static function debug(str:String):void {
        if (DEBUG == "off") {
            return;
        }

        try {
            ExternalInterface.call("window.console.log", str);
        } catch (e:Error) {

        }
    }


}
}