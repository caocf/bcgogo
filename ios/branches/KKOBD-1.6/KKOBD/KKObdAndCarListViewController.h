//
//  KKObdAndCarListViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-18.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
@class KKCustomTextField;

@interface KKObdAndCarListViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,KKProtocolEngineDelegate>
{
    KKCustomTextField       *_obdText;
    UITableView             *_tableView;
}

@property (nonatomic, copy) NSString *obdSN;
@property (nonatomic, copy) NSString *vehicleVin;
@end
