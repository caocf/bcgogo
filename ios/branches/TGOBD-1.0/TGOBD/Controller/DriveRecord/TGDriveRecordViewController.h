//
//  TGDriveRecordViewController.h
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "BMapKit.h"
#import "EGORefreshTableHeaderView.h"

@interface TGDriveRecordViewController : TGBaseViewController <BMKMapViewDelegate,UITableViewDataSource,UITableViewDelegate,EGORefreshTableHeaderDelegate>
{
    UILabel             *_dateLabel;                //统计日期
    UIButton            *_leftDateBtn;
    UIButton            *_rightDateBtn;             //日期选择左右按钮
    
    UIScrollView        *_scrollView;
    
    UILabel             *_countDistanceLabel;       //行驶里程
    UILabel             *_countTravelTimeLabel;     //行驶时长
    UILabel             *_countOilWearLabel;        //油耗
    UILabel             *_countAverageOilWearLabel; //平均油耗
    
    BMKMapView          *_mapView;
    
    UITableView         *_tableView;
    
    UIImageView         *_noDataImageView;
    
    EGORefreshTableHeaderView   *_refreshTableHeaderView;
    
    BOOL                _zoom;
    BOOL                _locationed;
    
    NSDateFormatter     *_dateFormatter;
    
    NSInteger           _currentWeekIndex;           //距离当前周数的差值
    
    DateTimeRange       currentTimeRange;
    
    BOOL                _isloading;
}
@end
