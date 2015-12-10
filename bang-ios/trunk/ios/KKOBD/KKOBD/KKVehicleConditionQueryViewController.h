//
//  KKVehicleConditionQueryViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKVehicleConditionView.h"
#import "KKProtocolEngineDelegate.h"
#import "EGORefreshTableHeaderView.h"

@interface KKVehicleConditionQueryViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,KKProtocolEngineDelegate,EGORefreshTableHeaderDelegate>
{
    NSMutableArray                  *_dataArray;
    UITableView                     *_mainTableView;
    KKVehicleConditionView          *_carStatusView;
    UIView                          *_linkWellPromptView;
    UIView                          *_unLinkPromptView;
    
    EGORefreshTableHeaderView       *_refreshHeaderView;
    BOOL                            _reloading;
}

- (void)reloadTableViewDataSource;
- (void)doneLoadingTableViewData;

@end
