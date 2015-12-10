//
//  KKPersonalInfoViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
@class KKModelUserInfo;

@interface KKPersonalInfoViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UITextFieldDelegate,KKProtocolEngineDelegate,UITextFieldDelegate>
{
    UITableView         *_mainTableView;
    NSMutableArray      *_titles;
}
@property (nonatomic ,retain)KKModelUserInfo *userInfo;

@end
