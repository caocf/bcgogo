//
//  TGTrafficViolationViewController.h
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "EGORefreshTableHeaderView.h"

@interface TGTrafficViolationViewController : TGBaseViewController <UITableViewDataSource, UITableViewDelegate, EGORefreshTableHeaderDelegate>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) EGORefreshTableHeaderView *refreshHeaderView;

@end
