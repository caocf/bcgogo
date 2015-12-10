//
//  KKOilStationMapViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-6.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BMapKit.h"
#import "KKProtocolEngineDelegate.h"
#import "KKOilBubbleView.h"
@class KKModelOilStationListRsp,KKModelOilStation;

@interface KKOilStationMapViewController : UIViewController<KKOilBubbleViewDelegate,BMKMapViewDelegate,KKProtocolEngineDelegate>
{
    BMKMapView *_mapView;
    BOOL        _isEnd;
    CLLocationCoordinate2D _initCenterCoordinate;
    CLLocationCoordinate2D _currentGcj02Coordinate;
    
    BOOL                _isFirstLoadData;
}
@property (nonatomic,retain) KKModelOilStation *currentStation;

@property (nonatomic,retain) KKModelOilStationListRsp *oilStationListRsp;
@end
