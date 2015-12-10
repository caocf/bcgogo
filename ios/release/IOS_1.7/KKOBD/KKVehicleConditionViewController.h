//
//  KKVehicleConditionViewController.h
//  KKOBD
//
//  Created by Jiahai on 14-1-16.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKServiceSegmentControl.h"
#import "BMKMapView.h"
#import "iCarousel.h"
#import "KKDriveRecordEngine.h"
#import "BGDateSelectView.h"
@class BGDriveRecordConditionView,BGDateSelectView;

@interface KKVehicleConditionViewController : UIViewController<KKServiceSegmentControlDelegate,BMKMapViewDelegate,UIGestureRecognizerDelegate,BGDateSelectViewDelegate,UITableViewDataSource,UITableViewDelegate>
{
    KKServiceSegmentControl         *_segmentControl;
    
    UIView                          *_runTimeView;
    BGDriveRecordConditionView      *_runTime_RecordConditionView;
    
    UIView                          *_recordView;
    UIView                          *_record_showView;              //地图+列表图层
    UITableView                     *_driveRecordTable;
    UIView                          *_record_maskView;              //遮罩图层
    UIView                          *_record_panView;               //统计信息及日期选择图层
    BGDriveRecordConditionView      *_record_RecordConditionView;
    UIButton                        *_dateSelectBtn;
    BGDateSelectView                *_dateSelectView;
    
    BMKMapView                      *_mapView;
    
    CGPoint                         _lastGesturePoint;
    
    DateTimeRange                   _currentDateTimeRange;
}

@end
