//
//  TGOrderDetailViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGOrderDetailView.h"

@interface TGOrderDetailViewController : TGBaseViewController

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) TGOrderDetailView *orderHeadView;
@property (nonatomic, strong) TGOrderDetailView *orderDetailView;
@property (nonatomic, assign) long long orderId;
@property (nonatomic, copy) NSString *serviceScope;

@end
