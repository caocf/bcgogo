//
//  TGUserInfoViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"

@interface TGUserInfoViewController : TGBaseViewController <UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) UITableView *tableview;

@end

