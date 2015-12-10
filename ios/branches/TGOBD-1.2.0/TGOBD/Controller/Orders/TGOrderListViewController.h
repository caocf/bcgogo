//
//  TGOrderListViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-7.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGOrderListTableView.h"
#import "TGCustomSegmentView.h"

@interface TGOrderListViewController : TGBaseViewController <TGOrderListTableViewDelegate, TGCustomSegmentViewDelegate>

@property (nonatomic, strong) TGCustomSegmentView *segmentView;
@property (nonatomic, strong) TGOrderListTableView *unSettleTableview;
@property (nonatomic, strong) TGOrderListTableView *settleTableView;

@end
