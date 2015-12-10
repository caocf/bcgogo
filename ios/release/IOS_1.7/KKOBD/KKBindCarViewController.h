//
//  KKBindCarViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
#import "KKProtocolEngineDelegate.h"

@interface KKBindCarViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,MBProgressHUDDelegate,KKProtocolEngineDelegate>
{
    UITableView         *_mainTableView;
    
    BOOL                _isChanged;
}

- (void)updateDefaultVehicleButtonClicked:(id)sender;

@end
