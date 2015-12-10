//
//  TGOilStationMapViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGBaseViewController.h"
#import "BMapKit.h"
#import "TGOilBubbleView.h"

@interface TGOilStationMapViewController : TGBaseViewController<BMKMapViewDelegate,TGOilBubbleViewDelegate>
{
    BMKMapView          *_mapView;
    BOOL                _isEnd;
    BOOL                _isFirstLoadData;
    
    CLLocationCoordinate2D  _currentCoordinate;
}
@end
