//
//  TGMessageViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"

@interface TGMessageViewController : TGBaseViewController <UITableViewDelegate, UITableViewDataSource, UIScrollViewDelegate>

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSMutableArray *dateSource;

@end
