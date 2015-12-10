//
//  KKThirdViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
#import "KKProtocolEngineDelegate.h"

@interface KKThirdViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,MBProgressHUDDelegate,KKProtocolEngineDelegate>
{
    UITableView         *_mainTableView;
    UISwitch            *_switch;
    NSMutableArray      *_icons;
    NSMutableArray      *_names;
    
    MBProgressHUD       *_hud;
}
@end
