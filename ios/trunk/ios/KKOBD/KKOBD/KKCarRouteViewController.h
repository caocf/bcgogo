//
//  KKCarRouteViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-28.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BMapKit.h"
@class KKModelShopDetail;

@interface KKCarRouteViewController : UIViewController<BMKMapViewDelegate,BMKSearchDelegate>
{
    BMKMapView                  *_mapView;
    BMKSearch                   *_search;
}
@property (nonatomic ,retain)KKModelShopDetail *shopInfo;

@end
