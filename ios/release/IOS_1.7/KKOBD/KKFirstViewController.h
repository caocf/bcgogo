//
//  KKFirstViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GNWheelView.h"
#import "KKCarStatusView.h"
#import "KKMsgPlaySound.h"
#import "KKProtocolEngine.h"
#import "KKProtocolEngineDelegate.h"

@interface KKFirstViewController : UIViewController<KKCarStatusViewDelegate,KKProtocolEngineDelegate>
{
    KKCarStatusView     *_carStatusView;
    KKMsgPlaySound      *_soundPlayer;
}
@end
