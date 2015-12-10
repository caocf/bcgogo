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
#import "BGDriveRecordTableViewCell.h"
#import "KKEditDriveRecordViewController.h"
#import "PopoverView.h"
#import "KKProtocolEngineDelegate.h"
#import "EGORefreshTableHeaderView.h"

@class BGDriveRecordRunTimeView,BGDriveRecordConditionView,BGDateSelectView;

@interface KKVehicleConditionViewController : UIViewController<KKServiceSegmentControlDelegate,BMKMapViewDelegate,UIGestureRecognizerDelegate,BGDateSelectViewDelegate,UITableViewDataSource,UITableViewDelegate,BGDriveRecordTableViewCellDelegate,KKEditDriveRecordViewControllDelegate,UITextFieldDelegate,PopoverViewDelegate,KKProtocolEngineDelegate,EGORefreshTableHeaderDelegate,UIScrollViewDelegate>
{
    UIView                          *rightBarItemView;
    
    UIScrollView                    *_refreshScrollView;
    
    UILabel                         *_currentOilPriceLabel;
    
    KKServiceSegmentControl         *_segmentControl;
    
    UIView                          *_runTimeView;
    BGDriveRecordRunTimeView        *_runTime_RecordConditionView;
    UIButton                        *_DTCBtn;
    UIImageView                     *_DTCAnimationImgV;
    
    EGORefreshTableHeaderView       *_refreshHeaderView;
    
    UIView                          *_recordView;
    UIView                          *_record_showView;              //地图+列表图层
    UITableView                     *_driveRecordTable;
    UIImageView                     *_record_maskView;              //遮罩图层
    UIImageView                     *_record_mapSnapshotView;       //record界面百度地图的截图
    UIView                          *_record_panView;               //统计信息及日期选择图层
    BGDriveRecordConditionView      *_record_RecordConditionView;
    UIButton                        *_dateSelectBtn;
    UILabel                         *_currentDateLabel;             //显示当前选择日期
    BGDateSelectView                *_dateSelectView;
    
    UILabel                         *_noDataImageView;
    
    BMKMapView                      *_mapView;
    
    CGPoint                         _lastGesturePoint;
    
    DateTimeRange                   _currentDateTimeRange;
    
    BOOL                            _reloading;                     //下拉刷新
    
    NSDateFormatter                 *_dateFormatter;
}

@end
