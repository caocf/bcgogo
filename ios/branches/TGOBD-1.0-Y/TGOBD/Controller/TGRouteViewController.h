//
//  TGRouteViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BMapKit.h"
#import "TGBaseViewController.h"
@class TGModelRouteData;

@interface TGRouteViewController : TGBaseViewController<BMKMapViewDelegate,BMKSearchDelegate>
{
    BMKMapView          *_mapView;
    BMKSearch           *_search;
}

@property (nonatomic, strong)   TGModelRouteData *routeData;
@end
