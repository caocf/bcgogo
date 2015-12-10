//
//  TGOrderListTableview.h
//  TGOBD
//
//  Created by James Yu on 14-3-18.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGOrderListViewCell.h"
#import "EGORefreshTableHeaderView.h"

@protocol TGOrderListTableViewDelegate;

@interface TGOrderListTableView : UIView <UITableViewDataSource, UITableViewDelegate, EGORefreshTableHeaderDelegate>

@property (nonatomic, assign) orderStatus orderStatus;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSMutableArray *dataSource;
@property (nonatomic, assign) double totalMoney;
@property (nonatomic, strong) EGORefreshTableHeaderView *refreshTableHeaderView;
@property (nonatomic, assign) BOOL isLoading;
@property (nonatomic, strong) TGModelPagerInfo *pager;
@property (nonatomic, strong) UIImageView *noDateImageView;

@property (nonatomic, assign) id <TGOrderListTableViewDelegate> delegate;

- (void)stopLoading;

@end

@protocol TGOrderListTableViewDelegate <NSObject>

- (void)TGOrderListTableViewPullRefresh:(orderStatus)status pageInfo:(TGModelPagerInfo *)pager;

- (void)TGOrderListTableViewLoadeMore:(orderStatus)status pageInfo:(TGModelPagerInfo *)pager;

- (void)TGOrderListTableViewdidSelectRowAtIndexPath:(NSIndexPath *)indexPath orderStatus:(orderStatus)orderStatus;

@end