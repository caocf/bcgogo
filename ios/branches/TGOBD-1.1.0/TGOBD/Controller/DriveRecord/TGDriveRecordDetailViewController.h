//
//  TGDriveRecordDetailViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "BMapKit.h"
@class TGDriveRecordDetailCountView;

@interface TGDriveRecordDetailViewController : TGBaseViewController <BMKMapViewDelegate>
{
    
    BMKMapView          *_mapView;
    
    TGDriveRecordDetailCountView              *_countView;
    UIImageView         *_mapImageView;
    
    UIScrollView        *_scrollView;
    
    CGFloat             viewHeight;
    
    BOOL                _playing;
    NSInteger           _playIndex;
}

@property(nonatomic, assign) CGFloat    worstOilWear;
@property(nonatomic, assign) CGFloat    bestOilWear;
@property(nonatomic, assign) CGFloat    totalOilWear;
@property(nonatomic, strong) TGModelDriveRecordDetail *detail;
@end
