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

@interface KKFirstViewController : UIViewController<GNWheelViewDelegate,KKCarStatusViewDelegate>
{
    GNWheelView         *_wheelView;
    KKCarStatusView     *_carStatusView;
    KKMsgPlaySound      *_soundPlayer;
}
@end
