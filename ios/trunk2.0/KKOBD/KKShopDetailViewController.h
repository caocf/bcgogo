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

@interface KKShopDetailViewController : UIViewController<KKProtocolEngineDelegate,BMKMapViewDelegate,UIActionSheetDelegate>
{
    IBOutlet UIScrollView            *_mainScrollView;
    IBOutlet BMKMapView              *_mapView;
}
@property (nonatomic ,retain)NSString           *shopId;
@property (nonatomic ,retain)KKModelShopDetail  *detailInfo;
@property (nonatomic ,retain)NSString           *serviceName;
@property (nonatomic ,retain)NSString           *remarkString;

@end
