//
//  KKShopDetailViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
#import "BMapKit.h"
@class KKModelShopInfo;
@class KKModelShopDetail;

@interface KKShopDetailViewController : UIViewController<KKProtocolEngineDelegate,BMKMapViewDelegate,UIActionSheetDelegate,UITableViewDataSource,UITableViewDelegate,UIScrollViewDelegate>
{
    IBOutlet UIScrollView            *_mainScrollView;
    IBOutlet BMKMapView              *_mapView;
    
    UITableView                     *_rateTableView;
    
    NSInteger                       _page;
    NSInteger                       _pageSize;
    BOOL                            _enableRefresh;
    BOOL                            _isLoading;
}
@property (nonatomic ,retain)NSString           *shopId;
@property (nonatomic ,retain)KKModelShopDetail  *detailInfo;
@property (nonatomic ,retain)NSString           *serviceName;
@property (nonatomic ,retain)NSString           *remarkString;

@property (nonatomic, retain) NSArray          *dtcMsgArray;    //一键预约带出所有故障的信息；
@end
