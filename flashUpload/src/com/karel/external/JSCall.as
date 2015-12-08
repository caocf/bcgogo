package com.karel.external {
import com.karel.external.JSCall;

import flash.external.ExternalInterface;

/**
 * @author zhen.pan
 */
public class JSCall {
    public function JSCall() {
    }

    public static function call(fnName:String, args:Array = null):void {
        if (fnName == "upload") {
            ExternalInterface.call("console.log", "bugg!!");
        }

        if (fnName && ExternalInterface.available) {
            if (args == null) {
                ExternalInterface.call(fnName)
            } else {
                switch (args.length) {
                    case 1:
                        ExternalInterface.call(fnName, args[0]);
                        break;
                    case 2:
                        ExternalInterface.call(fnName, args[0], args[1]);
                        break;
                    case 3:
                        ExternalInterface.call(fnName, args[0], args[1], args[2]);
                        break;
                }
            }
        }
    }

}
}
