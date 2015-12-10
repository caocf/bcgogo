//
//  KKServiceDetailViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
#import "KKServiceDetailView.h"
@class KKModelserviceDetail;

@interface KKServiceDetailViewController : UIViewController<KKProtocolEngineDelegate,KKServiceDetailViewDelegate>
@property (nonatomic ,retain)NSString *orderId;
@property (nonatomic ,retain)KKModelserviceDetail   *detail;

@end
