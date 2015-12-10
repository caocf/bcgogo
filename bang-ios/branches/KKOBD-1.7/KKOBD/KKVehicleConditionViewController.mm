//
//  KKVehicleConditionViewController.m
//  KKOBD
//
//  Created by Jiahai on 14-1-16.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKVehicleConditionViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKServiceSegmentControl.h"
#import "BGDriveRecordConditionView.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKModelComplex.h"
#import "BGDriveRecordTableViewCell.h"
#import "KKProtocolEngine.h"
#import "KKDTCManagerViewController.h"
#import "KKCustomAlertView.h"
#import "BGDriveRecordRunTimeView.h"
#import "KKPreference.h"
#import "UIImage+ImageEffects.h"

@interface BGRouteAnnotation : BMKPointAnnotation
{
	int _type; ///<0:途经点 1:起点 2：终点
	int _degree;
}
@property (nonatomic) int type;
@property (nonatomic) int degree;
@end

@implementation BGRouteAnnotation

@end

@interface KKVehicleConditionViewController ()
@property (nonatomic, retain) BGDriveRecordDetail       *currentDriveRecordDetail;  //当前日志
@property (nonatomic, retain) BGDriveRecordDetail       *showDriveRecordDetail;    //当前显示的历史日志
@property (nonatomic, retain) NSArray                   *driveRecordArray;
@property (nonatomic, retain) NSIndexPath               *editIndexPath;          //编辑的历史日志的IndexPath
@end


@implementation KKVehicleConditionViewController

const float     height_recordCondition  = 148;
const float     height_dateSelectBtn    = 32;
const float     height_dateSelectView   = 90;
#define         panViewShowFrame        CGRectMake(0, 0, 320, height_recordCondition + height_dateSelectBtn + height_dateSelectView)
#define         panViewHiddenFrame      CGRectMake(0, -height_recordCondition, 320, height_recordCondition + height_dateSelectBtn + height_dateSelectView)
#define         panViewResetOriginY     -60

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_mapView viewWillAppear];
    _mapView.delegate = self; // 此处记得不用的时候需要置nil，否则影响内存的释放
    
    [self updateDTCBtnAnimation];
    
    [self addExistDriveRecordPoints];
}

-(void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [self removeAllPointsAndPolyLines];
    
    [_mapView viewWillDisappear];
    _mapView.delegate = nil; // 不用时，置nil
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self setVcEdgesForExtendedLayout];
    [self initTitleView];
    
    [self initVariables];
    
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:titleView.bounds];
	titleLabel.textColor = [UIColor whiteColor];
	titleLabel.font = [UIFont boldSystemFontOfSize:20.0f];
	titleLabel.backgroundColor = [UIColor clearColor];
	titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.text = @"我的车况";
    titleLabel.center = CGPointMake(titleView.bounds.size.width*0.5+18, titleView.bounds.size.height*0.5);
    [titleView addSubview:titleLabel];
    [titleLabel release];

    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    rightBarItemView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 60, 44)];
    rightBarItemView.backgroundColor = [UIColor clearColor];
    UIImageView *youjiaImgV = [[UIImageView alloc] initWithFrame:CGRectMake(24, 5, 36, 18)];
    youjiaImgV.image = [UIImage imageNamed:@"icon_driveRecord_youjia.png"];
    [rightBarItemView addSubview:youjiaImgV];
    [youjiaImgV release];
    _currentOilPriceLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 24, 60, 20)];
    _currentOilPriceLabel.backgroundColor = [UIColor clearColor];
    _currentOilPriceLabel.font = [UIFont systemFontOfSize:12];
    _currentOilPriceLabel.textColor = [UIColor whiteColor];
    _currentOilPriceLabel.textAlignment = NSTextAlignmentRight;
    _currentOilPriceLabel.text = [NSString stringWithFormat:@"%@元/L", [KKPreference sharedPreference].appUserConfig.oil_price];
    [rightBarItemView addSubview:_currentOilPriceLabel];
    [_currentOilPriceLabel release];
    
    UIButton *youjiaBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [youjiaBtn setFrame:rightBarItemView.bounds];
    [youjiaBtn addTarget:self action:@selector(showEditOilPriceViewBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [rightBarItemView addSubview:youjiaBtn];
    [youjiaBtn setExclusiveTouch:YES];
    
    UIBarButtonItem *barItem = [[UIBarButtonItem alloc] initWithCustomView:rightBarItemView];
    self.navigationItem.rightBarButtonItem = barItem;
    [rightBarItemView release];
    [barItem release];
    
    _segmentControl = [[KKServiceSegmentControl alloc] initWithFrame:CGRectMake(0, 0, 320, 35)];
    _segmentControl.delegate = self;
    _segmentControl.type = KKServiceSegmentControlType_VehicleCondition;
    [_segmentControl updateInfo];
    [self.view addSubview:_segmentControl];
    [_segmentControl release];
    
    float originY = 35;
    float subOriginY = 0;
    
    
    //当前车况View
    _runTimeView = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 320, currentScreenHeight - originY - [self getOrignY] - 44 - 49)];
    
    _refreshScrollView = [[UIScrollView alloc] initWithFrame:_runTimeView.bounds];
    _refreshScrollView.delegate = self;
    _refreshScrollView.showsHorizontalScrollIndicator = NO;
    _refreshScrollView.showsVerticalScrollIndicator = NO;
    _refreshScrollView.contentSize = CGSizeMake(320, _runTimeView.bounds.size.height + 1);
    
    _refreshHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, 0-_runTimeView.bounds.size.height, _runTimeView.bounds.size.width, _runTimeView.bounds.size.height)];
    _refreshHeaderView.delegate = self;
    [_refreshScrollView addSubview:_refreshHeaderView];
    [_refreshHeaderView release];
    
    [_runTimeView addSubview:_refreshScrollView];
    
//    [_refreshHeaderView refreshLastUpdatedDate];

    
    _runTime_RecordConditionView = [[BGDriveRecordRunTimeView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, height_recordCondition + 1)];
    [_refreshScrollView addSubview:_runTime_RecordConditionView];
    [_runTime_RecordConditionView release];
    
    subOriginY += height_recordCondition;
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, _runTimeView.bounds.size.height - _runTime_RecordConditionView.frame.size.height)];
    _mapView.showsUserLocation = YES;
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.delegate = self;
    _mapView.isSelectedAnnotationViewFront = YES;
    _mapView.centerCoordinate = KKAppDelegateSingleton.currentCoordinate2D;
    _mapView.zoomLevel = 14;
    [_runTimeView addSubview:_mapView];
    [_mapView release];
    
    _DTCBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _DTCBtn.frame = CGRectMake(320-12-50, _runTimeView.bounds.size.height-62, 50, 50);
    [_DTCBtn setImage:[UIImage imageNamed:@"icon_dtc_warnning_nodata.png"] forState:UIControlStateNormal];
    [_DTCBtn addTarget:self action:@selector(DTCBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [_runTimeView addSubview:_DTCBtn];
    
    _DTCAnimationImgV = [[UIImageView alloc] initWithFrame:_DTCBtn.bounds];
    [_DTCBtn setImage:[UIImage imageNamed:@"icon_dtc_bg.png"] forState:UIControlStateNormal];
    _DTCAnimationImgV.animationImages = [NSArray arrayWithObjects:
                                         [UIImage imageNamed:@"icon_dtc_warnning1.png"],
                                         [UIImage imageNamed:@"icon_dtc_warnning2.png"],
                                         nil];
    
    _DTCAnimationImgV.animationDuration = 0.5;
    _DTCAnimationImgV.animationRepeatCount = NSIntegerMax;
    [_DTCBtn addSubview:_DTCAnimationImgV];
    [_DTCAnimationImgV release];
    
    [_DTCBtn bringSubviewToFront:_DTCBtn.imageView];
    
    
    [self updateDTCBtnAnimation];
    
    [self.view addSubview:_runTimeView];
    [_runTimeView release];
    
    //行车日志View
    
    subOriginY = 0;
    _recordView =[[UIView alloc] initWithFrame:CGRectMake(0, originY, 320, currentScreenHeight - originY - [self getOrignY] - 44 - 49)];
    _recordView.hidden = YES;
    
    _record_showView = [[UIView alloc] initWithFrame:_recordView.bounds];
    _record_showView.backgroundColor = [UIColor whiteColor];
    
    _record_mapSnapshotView = [[UIImageView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, height_recordCondition)];
    [_record_showView addSubview:_record_mapSnapshotView];
    [_record_mapSnapshotView release];
    
    subOriginY += height_recordCondition;
    
    
    UIImageView *imgV = [[UIImageView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, currentScreenHeight - [self getOrignY] - originY - 44 - 49 - subOriginY)];
    imgV.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [_record_showView addSubview:imgV];
    [imgV release];
    
    _driveRecordTable = [[UITableView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, currentScreenHeight - [self getOrignY] - originY - 44 - 49 - subOriginY)];
    _driveRecordTable.dataSource = self;
    _driveRecordTable.delegate = self;
    _driveRecordTable.backgroundColor = [UIColor clearColor];
    [_driveRecordTable setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    [_record_showView addSubview:_driveRecordTable];
    [_driveRecordTable release];
    
    [_recordView addSubview:_record_showView];
    [_record_showView release];
    
    _record_maskView = [[UIImageView alloc] initWithFrame:_recordView.bounds];
    [_recordView addSubview:_record_maskView];
    [_record_maskView release];
    
    subOriginY = 0;
    
    _record_panView = [[UIView alloc] initWithFrame:panViewShowFrame];
    _record_panView.backgroundColor = [UIColor clearColor];
    
    _record_RecordConditionView = [[BGDriveRecordConditionView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, height_recordCondition)];
    [_record_panView addSubview:_record_RecordConditionView];
    [_record_RecordConditionView release];
    
    subOriginY += height_recordCondition;
    
    _dateSelectBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _dateSelectBtn.frame = CGRectMake(0, subOriginY, 320, height_dateSelectBtn);
    [_dateSelectBtn setBackgroundImage:[UIImage imageNamed:@"icon_driveRecord_drag.png"] forState:UIControlStateNormal];
    [_dateSelectBtn addTarget:self action:@selector(dateBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    
    _currentDateLabel = [[UILabel alloc] init];
    _currentDateLabel.frame = CGRectMake(102, 0, 120, 16);
    _currentDateLabel.backgroundColor = [UIColor clearColor];
    _currentDateLabel.font = [UIFont systemFontOfSize:12];
    _currentDateLabel.textAlignment = NSTextAlignmentCenter;
    _currentDateLabel.textColor = [UIColor blackColor];
    _currentDateLabel.text = @"今日";
    [_dateSelectBtn addSubview:_currentDateLabel];
    [_currentDateLabel release];
    
    [_record_panView addSubview:_dateSelectBtn];
    
    UIPanGestureRecognizer *panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(moveVehicleConditionViewWithPanGesture:)];
    [_dateSelectBtn addGestureRecognizer:panGesture];
    [panGesture release];
    
    subOriginY += height_dateSelectBtn;
    
    _dateSelectView = [[BGDateSelectView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, height_dateSelectView)];
    _dateSelectView.delegate = self;
    _dateSelectView.backgroundColor = [UIColor clearColor];
    
    [_record_panView addSubview:_dateSelectView];
    [_dateSelectView release];
    
    [_recordView addSubview:_record_panView];
    [_record_panView release];
    
    [self.view addSubview:_recordView];
    [_recordView release];
    
    [self.view bringSubviewToFront:_segmentControl];
    
    //读取当天行车日志
    [self BGDateSelectItemSelected:_currentDateTimeRange];
    
    //通知中心
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateRunTimeDataVehicleCondition) name:@"updateVehicleRealTimeDataNotification" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDTCBtnAnimation) name:@"updateVehicleConditionNotification" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(driveRecordGetNewPoint) name:Notification_DriveRecord_NewPoint object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(driveRecordUnSave) name:Notification_DriveRecord_UnSave object:nil];
    
    //提示信息
    //
    if(!(KKAppDelegateSingleton.currentVehicle.obdSN && KKAppDelegateSingleton.currentVehicle.obdSN.length > 0))
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"您还没有绑定OBD！" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"我知道了" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"去绑定！" imageName:@"alert-orange-button.png" block:^{
            KKSearchCarViewController *Vc = [[KKSearchCarViewController alloc] initWithNibName:@"KKSearchCarViewController" bundle:nil];
            Vc.nextVc = NextVc_ObdAndCarListVc;
            Vc.skipToBack = YES;
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
        }];
        [alertView show];
        [alertView release];
    }
    else
    {
        if(![KKAppDelegateSingleton.bleEngine supportBLE])
        {
            [KKCustomAlertView showAlertViewWithMessage:@"您的蓝牙未打开！"];
        }
    }
    
    [self updateRunTimeDataVehicleCondition];
    
}

-(void) initVariables
{
    _dateFormatter = [[NSDateFormatter alloc] init];
    
    self.currentDriveRecordDetail = [KKDriveRecordEngine sharedInstance].driveRecordDetail;
    long long nowDate = [[NSDate date] timeIntervalSince1970WithMillisecond];
    _currentDateTimeRange.startTime = [KKHelper getDayStartTime:nowDate];
    _currentDateTimeRange.endTime = [KKHelper getDayEndTime:nowDate];
    self.driveRecordArray = [self queryDriveRecordWithTimeRange:_currentDateTimeRange];
    if([self.driveRecordArray count] > 0)
        self.showDriveRecordDetail = [self.driveRecordArray objectAtIndex:0];
}

-(void)backButtonClicked
{
    _dateSelectView.hidden = YES;
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) showEditOilPriceViewBtnClicked
{
    UIView *view = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 160, 34)];
    view.backgroundColor = [UIColor clearColor];
    UILabel *lable1 = [[UILabel alloc] initWithFrame:CGRectMake(0, 2, 62, 26)];
    lable1.text = @"当前油价:";
    lable1.font = [UIFont systemFontOfSize:14];
    lable1.backgroundColor = [UIColor clearColor];
    lable1.minimumFontSize = 14;
    lable1.textAlignment = NSTextAlignmentRight;
    [view addSubview:lable1];
    [lable1 release];
    
    KKCustomTextField *_oilPriceText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(64, 2 - 4, 68, 32) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:10];
    _oilPriceText.index = 1;
    _oilPriceText.textField.delegate = self;
    _oilPriceText.textField.keyboardType = UIKeyboardTypeDecimalPad;
    _oilPriceText.textField.text = [KKPreference sharedPreference].appUserConfig.oil_price;
    [view addSubview:_oilPriceText];
    [_oilPriceText release];
    
    UILabel *oilPriceDWLabel = [[UILabel alloc] initWithFrame:CGRectMake(132, 2, 28, 26)];
    oilPriceDWLabel.text = @"元/L";
    oilPriceDWLabel.font = [UIFont systemFontOfSize:14];
    oilPriceDWLabel.backgroundColor = [UIColor clearColor];
    oilPriceDWLabel.minimumFontSize = 14;
    oilPriceDWLabel.textAlignment = NSTextAlignmentLeft;
    [view addSubview:oilPriceDWLabel];
    [oilPriceDWLabel release];
    
    [PopoverView showPopoverAtPoint:CGPointMake(290, 0) inView:self.view withContentView:view delegate:self];
    [_oilPriceText.textField becomeFirstResponder];
    [view release];
    
}

#pragma mark - PopoverViewDelegate
- (void)popoverViewDidDismiss:(PopoverView *)popoverView
{
    
}

#pragma mark - UITextFieldDelegate
-(void) textFieldDidEndEditing:(UITextField *)textField
{
    NSString *str = [textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    if(str.length == 0)
        str = @"0";
    
    if(![KKHelper KKHElpRegexMatchForFloatValue:str])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"油价输入格式不正确！"];
        //[self showEditOilPriceViewBtnClicked];
        return;
    }
    else
    {
        KKModelAppUserConfig *appUserConfig = [KKPreference sharedPreference].appUserConfig;
        appUserConfig.oil_price = str;
        [KKPreference sharedPreference].appUserConfig = nil;
        [KKPreference sharedPreference].appUserConfig = appUserConfig;
        
        [KKDriveRecordEngine sharedInstance].driveRecordDetail.oilPrice = [str floatValue];
        [[KKDriveRecordEngine sharedInstance] countTotalOilMoney];
        _currentOilPriceLabel.text = [NSString stringWithFormat:@"%@元/L",str];
        
        //更新用户配置-油价
        [[KKProtocolEngine sharedPtlEngine] updateAppUserConfig:str delegate:self];
        
        [self updateRunTimeDataVehicleCondition];
    }
}


#pragma mark - Event
-(void) setBlurImage
{
    UIImage *mapSnapshot = [_mapView takeSnapshot];
    
    _record_mapSnapshotView.image = mapSnapshot;
    [_record_RecordConditionView setbgImage:[mapSnapshot applyBlurWithRadius:10 tintColor:nil saturationDeltaFactor:1.f maskImage:nil]];
    
    UIGraphicsBeginImageContext(_record_showView.bounds.size);
    [_record_showView.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *snapshot = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    snapshot = [snapshot applyBlurWithRadius:10
                                   tintColor:nil
                       saturationDeltaFactor:1.f
                                   maskImage:nil];
    
    _record_maskView.image = snapshot;

}

-(void) dateSelectViewHiddenAnimateion:(BOOL) aHidden
{
    if(aHidden)
    {
        _dateSelectView.hidden = aHidden;
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
        [UIView setAnimationRepeatAutoreverses:NO];
        [UIView animateWithDuration:0.8f animations:^{
            _record_panView.frame = panViewHiddenFrame;
            _record_maskView.hidden = YES;
        } completion:^(BOOL finished) {
            _record_maskView.hidden = aHidden;
            CGRect rect = _record_panView.frame;
            rect.size.height -= height_dateSelectView;
            _record_panView.frame = rect;
        }];
        [UIView commitAnimations];
    }
    else
    {
        [self setBlurImage];
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
        [UIView setAnimationRepeatAutoreverses:NO];
        [UIView animateWithDuration:0.8f animations:^{
            _record_panView.frame = panViewShowFrame;
            _record_maskView.hidden = NO;
        } completion:^(BOOL finished) {
            _dateSelectView.hidden = aHidden;
            _record_maskView.hidden = aHidden;
            CGRect rect = _record_panView.frame;
            rect.size.height += height_dateSelectView;
            _record_panView.frame = rect;
        }];
        [UIView commitAnimations];
    }
}

-(void) dateBtnClicked
{
    [self dateSelectViewHiddenAnimateion:!_dateSelectView.hidden];
}

-(void) changeDriveRecordWithIndex:(NSInteger) index
{
    if(self.driveRecordArray && [self.driveRecordArray count] > index)
    {
        self.showDriveRecordDetail = [self.driveRecordArray objectAtIndex:index];
    }
    else
    {
        self.showDriveRecordDetail = nil;
    }
    
    [self removeAllPointsAndPolyLines:YES];
    [self addExistDriveRecordPoints];
    [_driveRecordTable reloadData];
}

-(void) DTCBtnClicked
{
    KKDTCManagerViewController *Vc = [[KKDTCManagerViewController alloc] init];
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

#pragma mark - BGDateSelectViewDelegate



-(NSString *) getTimeRangeString:(DateTimeRange)aTimeRange
{
    NSDate *startDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTimeRange.startTime];
    NSDate *endDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTimeRange.endTime];
    NSString *str=nil,*str1=nil;
    
    switch (_dateSelectView.dateSelectType) {
        case DateSelectType_Day:
        {
            [_dateFormatter setDateFormat:@"M月d日"];
            str = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"%@",str];
        }
            break;
        case DateSelectType_Week:
        {
            [_dateFormatter setDateFormat:@"MM/dd"];
            str = [_dateFormatter stringFromDate:startDate];
            str1 = [_dateFormatter stringFromDate:endDate];
            return [NSString stringWithFormat:@"%@-%@",str,str1];
            
        }
            break;
        case DateSelectType_Month:
        {
            [_dateFormatter setDateFormat:@"M月"];
            str = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"%@",str];
        }
            break;
    }
}

-(void) BGDateSelectItemSelected:(DateTimeRange)aTimeRange
{
    [self dateSelectViewHiddenAnimateion:YES];
    
    _currentDateTimeRange = aTimeRange;
    
    _currentDateLabel.text = [self getTimeRangeString:aTimeRange];
    
    self.driveRecordArray = [self queryDriveRecordWithTimeRange:aTimeRange];
    
    BGDriveRecordDetail *detail = [[BGDriveRecordDetail alloc] init];
    for(BGDriveRecordDetail *de in self.driveRecordArray)
    {
        detail.distance += de.distance;
        detail.oilWear += de.distance * de.oilWear * 0.01;     //总油耗
        detail.totalOilMoney += de.totalOilMoney;
        detail.travelTime += de.travelTime;
    }
    [_record_RecordConditionView setContentWithRealTimeData:detail];
    [detail release];
    
    if([self.driveRecordArray count] == 0)
    {
        if(_noDataImageView == nil)
        {
            _noDataImageView = [[UILabel alloc] initWithFrame:CGRectMake(0, 20, 320, 32)];
            _noDataImageView.backgroundColor = [UIColor clearColor];
            _noDataImageView.font = [UIFont systemFontOfSize:14];
            _noDataImageView.textAlignment = NSTextAlignmentCenter;
            _noDataImageView.text = @"没有行车记录";
            //_noDataImageView.image = [UIImage imageNamed:@"icon_dmcx.png"];
            [_driveRecordTable addSubview:_noDataImageView];
            [_noDataImageView release];
        }
    }
    else
    {
        if(_noDataImageView)
        {
            [_noDataImageView removeFromSuperview];
            _noDataImageView = nil;
        }
    }
    
    [_driveRecordTable setContentOffset:CGPointZero];
    [self changeDriveRecordWithIndex:0];
}

-(NSArray *) queryDriveRecordWithTimeRange:(DateTimeRange)aTimeRange
{
    return [[KKDriveRecordEngine sharedInstance] queryDriveRecordWithTimeRange:aTimeRange appUserNo:[KKProtocolEngine sharedPtlEngine].userName vehicleNo:KKAppDelegateSingleton.currentVehicle.vehicleNo];
}

#pragma mark UIGestureRecognizerDelegate
//-(BOOL) gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
//{
//    _lastGesturePoint = CGPointZero;
//    return YES;
//}
-(void) moveVehicleConditionViewWithPanGesture:(UIPanGestureRecognizer*)recognizer
{
    CGPoint point = [recognizer translationInView:self.view];
    switch (recognizer.state) {
        case UIGestureRecognizerStateBegan:
        {
            _lastGesturePoint = point;
            if(_record_maskView.hidden && _record_mapSnapshotView.image == nil)
                [self setBlurImage];
            return;
        }
            break;
        case UIGestureRecognizerStateChanged:
        {
            CGRect rect = _record_panView.frame;
            if(abs(point.y) > 8 && rect.origin.y >= panViewHiddenFrame.origin.y && rect.origin.y <= panViewShowFrame.origin.y)
            {
                CGPoint delta = CGPointMake(point.x - _lastGesturePoint.x, point.y - _lastGesturePoint.y);
                _lastGesturePoint = point;
                rect.origin.y += delta.y;
                _record_panView.frame = rect;
                NSLog(@"point:%@，state:%d",NSStringFromCGPoint(delta),recognizer.state);
            }
        }
            break;
        case UIGestureRecognizerStateEnded:
        {
            _lastGesturePoint = CGPointZero;
            if(_record_panView.frame.origin.y >= panViewResetOriginY)
            {
                //上拉小于XX时，动画显示统计信息
                //_record_panView.frame = panViewShowFrame;
                [self dateSelectViewHiddenAnimateion:NO];
            }
            else
            {
                //上拉大于XX时，动画隐藏统计信息
                //_record_panView.frame = panViewHiddenFrame;
                [self dateSelectViewHiddenAnimateion:YES];
            }
        }
            break;
        default:
            break;
    }
}

#pragma mark KKServiceSegmentControlDelegate
- (void)KKServiceSegmentControlSegmentChanged:(NSInteger)index;
{
    switch (index) {
        case 0:
        {
            _runTimeView.hidden = NO;
            _recordView.hidden = YES;
            
            rightBarItemView.hidden = NO;
            
            [_mapView retain];
            [_mapView removeFromSuperview];
            _mapView.frame = CGRectMake(0, height_recordCondition, 320, _runTimeView.bounds.size.height - _runTime_RecordConditionView.frame.size.height);
            _mapView.showsUserLocation = YES;
            [_runTimeView addSubview:_mapView];
            [_mapView release];
            
            [_runTimeView bringSubviewToFront:_DTCBtn];
            
            [self removeAllPointsAndPolyLines];
            [self addExistDriveRecordPoints];
        }
            break;
        case 1:
        {
            _runTimeView.hidden = YES;
            _recordView.hidden = NO;
            
            rightBarItemView.hidden = YES;
            
            [_mapView retain];
            [_mapView removeFromSuperview];
            _mapView.frame = CGRectMake(0, 0, 320, height_recordCondition);
            _mapView.showsUserLocation = NO;
            [_record_showView addSubview:_mapView];
            [_mapView release];
            
//            [_record_showView sendSubviewToBack:_mapView];
            
            [self removeAllPointsAndPolyLines:YES];
            [self addExistDriveRecordPoints];
        }
            break;
        default:
            break;
    }
}

#pragma mark -
#pragma mark NSNotificationCenter
-(void) updateRunTimeDataVehicleCondition
{
    [_runTime_RecordConditionView setContentWithRealTimeData:KKAppDelegateSingleton.vehicleRealtimeData recordDetail:self.currentDriveRecordDetail];
}
-(void) updateDTCBtnAnimation
{
    if(_DTCBtn)
    {
        if(KKAppDelegateSingleton.dtcWarnning)
        {
            [_DTCBtn setImage:nil forState:UIControlStateNormal];
            [_DTCBtn setImage:[UIImage imageNamed:@"icon_dtc_warnning_mask.png"] forState:UIControlStateHighlighted];
            _DTCAnimationImgV.hidden = NO;
            [_DTCAnimationImgV startAnimating];
        }
        else
        {
            _DTCAnimationImgV.hidden = YES;
            [_DTCAnimationImgV stopAnimating];
    //        _DTCBtn.imageView.animationImages = nil;
            [_DTCBtn setImage:[UIImage imageNamed:@"icon_dtc_warnning_nodata.png"] forState:UIControlStateNormal];
            [_DTCBtn setImage:nil forState:UIControlStateHighlighted];
        }
    }
}

-(void) driveRecordGetNewPoint
{
    self.currentDriveRecordDetail = [KKDriveRecordEngine sharedInstance].driveRecordDetail;
    if(_segmentControl.selectedIndex == 0)
    {
        if(self.currentDriveRecordDetail == nil)
        {
            //结束
            [self removeAllPointsAndPolyLines:YES];
            if([KKDriveRecordEngine sharedInstance].carPoint)
                [self setDriveRecordMapViewRegion:[NSMutableArray arrayWithObject:[KKDriveRecordEngine sharedInstance].carPoint] useLocalPoint:YES];
        }
        else
        {
            BGDriveRecordPoint *point = [self.currentDriveRecordDetail.pointArray lastObject];
            [self addDriveRecordPointAnnotation:point];
            if(point.type == DriveRecordPointType_Common)
            {
                int count = [self.currentDriveRecordDetail.pointArray count];
                if(count > 1)
                    [self addDriveRecordPolyLineWithStartPoint:[self.currentDriveRecordDetail.pointArray objectAtIndex:count-2] endPoint:point];
            }
        }
    }
}

-(void) driveRecordUnSave
{
    self.currentDriveRecordDetail = nil;
    [self removeAllPointsAndPolyLines];
}

#pragma mark - EGORefreshTableHeaderDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    
    if(_segmentControl.selectedIndex == 0)
    {
        [_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
        CGRect rect = _mapView.frame;
        rect.origin.y = height_recordCondition - scrollView.contentOffset.y;
        _mapView.frame = rect;
    }
    
}
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    
    if(_segmentControl.selectedIndex == 0)
        [_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
}

-(void) doneLoadingTableViewData
{
	_reloading = NO;
	[_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_refreshScrollView];
}


- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView*)view
{
    if (KKAppDelegateSingleton.bleEngine && [KKAppDelegateSingleton.bleEngine supportBLE] && KKAppDelegateSingleton.currentConnectedPeripheral != nil)
    {
     //   [self performSelector:@selector(doneLoadingTableViewData) withObject:nil afterDelay:3.0];
        
        double delayInSeconds = 2;
        //刷新车况
        dispatch_time_t ckTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(ckTime, dispatch_get_main_queue(), ^(void){
            [KKAppDelegateSingleton getVehicleRealData:YES];
            [self doneLoadingTableViewData];
        });

//        delayInSeconds = 3.5;
//        //读取故障码
//        dispatch_time_t gzTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
//        dispatch_after(gzTime, dispatch_get_main_queue(), ^(void){
//            [self doneLoadingTableViewData];
//            [KKAppDelegateSingleton.bleEngine getVehicleDTC];
//        });
    }
    else
    {
        double delayInSeconds = 1;
        dispatch_time_t ckTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(ckTime, dispatch_get_main_queue(), ^(void){
            [self doneLoadingTableViewData];
        });

        [KKCustomAlertView showAlertViewWithMessage:@"您的手机未连接OBD，不能更新车况信息"];
    }

}
- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView*)view
{
    return NO;
}

#pragma mark - KKProtocolEngineDelegate
-(NSNumber *) updateAppUserConfigResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSLog(@"更新油价成功!");
    return KKNumberResultEnd;
}
#pragma mark - UITableViewDelegate
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.driveRecordArray count];
}

-(CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 118;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"driveRecordCell";
    BGDriveRecordTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if(cell == nil)
    {
        cell = [[[BGDriveRecordTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier] autorelease];
        cell.delegate = self;
    }
    
    BGDriveRecordDetail *detail = [self.driveRecordArray objectAtIndex:indexPath.row];
    
    [cell refreshUIWithDriveRecordDetail:detail selected:[detail.appDriveLogId isEqualToString:self.showDriveRecordDetail.appDriveLogId] indexPath:indexPath];
    
    return cell;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self changeDriveRecordWithIndex:indexPath.row];
}

#pragma mark - BGDriveRecordTableViewCellDelegate
-(void) driveRecordEditBtnClicked:(NSIndexPath *)aIndexPath
{
    self.editIndexPath = aIndexPath;
    if([self.driveRecordArray count] > self.editIndexPath.row)
    {
        KKEditDriveRecordViewController *Vc = [[KKEditDriveRecordViewController alloc] init];
        Vc.driveRecordDetail = [self.driveRecordArray objectAtIndex:self.editIndexPath.row];
        Vc.delegate = self;
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
    }
    else
    {
        [KKCustomAlertView showAlertViewWithMessage:@"此条行车日志记录不存在！" block:^{
            [self BGDateSelectItemSelected:_currentDateTimeRange];
        }];
    }
}

#pragma mark - KKEditDriveRecordViewControllDelegate
-(void) driveRecordEdited
{
    [_driveRecordTable reloadRowsAtIndexPaths:[NSArray arrayWithObject:self.editIndexPath] withRowAnimation:UITableViewRowAnimationRight];
}

#pragma mark - BMKMapViewDelegate

- (void)setDriveRecordMapViewRegion:(NSMutableArray *)pointArray useLocalPoint:(BOOL)useLocalPoint
{
    BMKCoordinateRegion region;
    CLLocationCoordinate2D center= {0,0},coor={0,0};
    CLLocationCoordinate2D leftCoor = {0,0},rightCoor = {0,0},topCoor = {0,0},bottomCoor = {0,0};
    
    CGFloat latLDelta=0,latRDelta=0,lonTDelta=0,lonBDelta = 0;
    CGFloat latDelta = 0,lonDelta = 0;
    
    if(useLocalPoint)
        center = coor = leftCoor = rightCoor = topCoor = bottomCoor = KKAppDelegateSingleton.currentCoordinate2D;
    
    for(BGDriveRecordPoint *point in pointArray)
    {
        [self addDriveRecordPointAnnotation:point];
        
        CLLocationCoordinate2D coorBuf = CLLocationCoordinate2DMake(point.lat, point.lon);
        CGFloat latDeltaBuf,lonDeltaBuf;
        if(coor.latitude == 0 && coor.longitude == 0)
        {
            center = coor = coorBuf;
            leftCoor = rightCoor = topCoor = bottomCoor = coorBuf;
            continue;
        }
        
        latDeltaBuf = coorBuf.latitude-coor.latitude;
        lonDeltaBuf = coorBuf.longitude-coor.longitude;
        
        if(latDeltaBuf > 0 && latDeltaBuf > latRDelta)
        {
            //最右端的点
            rightCoor = coorBuf;
            latRDelta = latDeltaBuf;
        }
        
        if(latDeltaBuf < 0 && latDeltaBuf < latLDelta)
        {
            //最左端的点
            leftCoor = coorBuf;
            latLDelta = latDeltaBuf;
        }
        
        if(lonDeltaBuf > 0 && lonDeltaBuf > lonTDelta)
        {
            //最上端的点
            topCoor = coorBuf;
            lonTDelta = lonDeltaBuf;
        }
        
        if(lonDeltaBuf < 0 && lonDeltaBuf < lonTDelta)
        {
            //最下端的点
            bottomCoor = coorBuf;
            lonBDelta = lonDeltaBuf;
        }
    }
    
    latDelta = fabs(latLDelta-latRDelta);
    lonDelta = fabs(lonTDelta-lonBDelta);
    
    center = CLLocationCoordinate2DMake(leftCoor.latitude+latDelta *0.5, bottomCoor.longitude+lonDelta*0.5);
    
    //region = BMKCoordinateRegionMake(center, BMKCoordinateSpanMake(latDelta, lonDelta));
    region = [_mapView regionThatFits:BMKCoordinateRegionMake(center, BMKCoordinateSpanMake(latDelta, lonDelta))];
    [_mapView setRegion:region animated:YES];
}

//添加已存在的点和画线
-(void) addExistDriveRecordPoints
{
    BGDriveRecordDetail *detail = nil;
    if(_segmentControl.selectedIndex == 0)
    {
        detail = self.currentDriveRecordDetail;
        for(BGDriveRecordPoint *point in detail.pointArray)
        {
            [self addDriveRecordPointAnnotation:point];
        }
      
        NSMutableArray *array = nil;
        if([KKDriveRecordEngine sharedInstance].recording)
        {
            array = [NSMutableArray arrayWithArray:detail.pointArray];
        }
        else
        {
            if([KKDriveRecordEngine sharedInstance].carPoint)
            {
                array = [NSMutableArray arrayWithObject:[KKDriveRecordEngine sharedInstance].carPoint];
            }
        }
        if([array count] > 0)
            [self setDriveRecordMapViewRegion:array useLocalPoint:YES];
        else
            [_mapView setCenterCoordinate:KKAppDelegateSingleton.currentCoordinate2D animated:YES];
    }
    else if(_segmentControl.selectedIndex == 1)
    {
        detail = self.showDriveRecordDetail;
        
        if([detail.pointArray count] > 0)
        {
            [self setDriveRecordMapViewRegion:detail.pointArray useLocalPoint:NO];
        }
        else
        {
            [_mapView setCenterCoordinate:KKAppDelegateSingleton.currentCoordinate2D animated:YES];
        }
    }
    [self addDriveRecordPolyLine:detail.pointArray];
}

//
-(void) removeAllPointsAndPolyLines
{
    [self removeAllPointsAndPolyLines:NO];
}
-(void) removeAllPointsAndPolyLines:(BOOL) removeInIndex2
{
    if(_segmentControl.selectedIndex == 0 || (_segmentControl.selectedIndex == 1 && removeInIndex2))
    {
        if(_mapView.annotations)
        {
            NSArray *array = [_mapView.annotations copy];
            [_mapView removeAnnotations:array];
            [array release];
        }
        if(_mapView.overlays)
        {
            NSArray *array = [_mapView.overlays copy];
            [_mapView removeOverlays:array];
            [array release];
        }
    }
}

-(void) addDriveRecordPointAnnotation:(BGDriveRecordPoint *)point
{
    if(point == nil)
        return;
    
    BGDriveRecordDetail *detail = nil;
    
    if(_segmentControl.selectedIndex == 0)
    {
        detail = self.currentDriveRecordDetail;
    }
    else if(_segmentControl.selectedIndex == 1)
    {
        detail = self.showDriveRecordDetail;
    }
    
    if(point.type == DriveRecordPointType_Common)
        return;
    
    BGRouteAnnotation *annotation = [[BGRouteAnnotation alloc] init]; //[_mapView dequeueReusableAnnotationViewWithIdentifier:@"driveRecordAnnotation"];
    annotation.coordinate = CLLocationCoordinate2DMake(point.lat,point.lon);
    annotation.type = point.type;
    switch (point.type) {
        case DriveRecordPointType_Start:
        {
            annotation.title = @"我的起点";
            annotation.subtitle = detail.startPlace;
        }
            break;
        case DriveRecordPointType_End:
        {
            annotation.title = @"我的终点";
            annotation.subtitle = detail.endPlace;
        }
            break;
        case DriveRecordPointType_CarPark:
        {
            annotation.title = @"我的车辆";
        }
            break;
        default:
        {
            annotation.title = nil;
            annotation.subtitle = nil;
        }
            break;
    }
    [_mapView addAnnotation:annotation];
    [annotation release];
}

-(void) addDriveRecordPolyLineWithStartPoint:(BGDriveRecordPoint *)startPoint endPoint:(BGDriveRecordPoint *)endPoint
{
    CLLocationCoordinate2D *points = new CLLocationCoordinate2D[2];
    points[0] = CLLocationCoordinate2DMake(startPoint.lat, startPoint.lon);
    points[1] = CLLocationCoordinate2DMake(endPoint.lat, endPoint.lon);
    BMKPolyline *polyline = [BMKPolyline polylineWithCoordinates:points count:2];
    [_mapView addOverlay:polyline];
    delete []points;
}

-(void) addDriveRecordPolyLine:(NSArray *)points
{
    int n = [points count];
    if(n > 1)
    {
        CLLocationCoordinate2D *polyPoints = new CLLocationCoordinate2D[n];
        for(int i=0; i<n; i++)
        {
            BGDriveRecordPoint *point = [points objectAtIndex:i];
            polyPoints[i] = CLLocationCoordinate2DMake(point.lat, point.lon);
        }
        BMKPolyline *polylines = [BMKPolyline polylineWithCoordinates:polyPoints count:n];
        [_mapView addOverlay:polylines];
        delete []polyPoints;
    }
}

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    BMKAnnotationView *view = nil;//[mapView dequeueReusableAnnotationViewWithIdentifier:@"driveRecordAnnotation"];
    if(view == nil)
    {
        view = [[[BMKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"driveRecordAnnotation"] autorelease];
    }
    BGRouteAnnotation *routeAnnotation = (BGRouteAnnotation *)annotation;
    switch (routeAnnotation.type) {
        case DriveRecordPointType_Start:
        {
            view.image = [UIImage imageNamed:@"mapapi.bundle/images/icon_nav_start.png"];
            view.canShowCallout = YES;
        }
            break;
        case DriveRecordPointType_Common:
        {
            view.image = [UIImage imageNamed:@"mapapi.bundle/images/icon_direction.png"];
            view.canShowCallout = NO;
        }
            break;
        case DriveRecordPointType_End:
        {
            view.image = [UIImage imageNamed:@"mapapi.bundle/images/icon_nav_end.png"];
            view.canShowCallout = YES;
        }
            break;
        case DriveRecordPointType_CarPark:
        {
            view.image = [UIImage imageNamed:@"icon_nav_carparking.png"];
            view.canShowCallout = YES;
        }
            break;
        default:
            break;
    }
    view.annotation = routeAnnotation;
    return view;
}

- (BMKOverlayView *)mapView:(BMKMapView *)mapView viewForOverlay:(id <BMKOverlay>)overlay
{
    if ([overlay isKindOfClass:[BMKPolyline class]]) {
        BMKPolylineView* polylineView = [[[BMKPolylineView alloc] initWithOverlay:overlay] autorelease];
        polylineView.fillColor = [[UIColor cyanColor] colorWithAlphaComponent:1];
        polylineView.strokeColor = [[UIColor blueColor] colorWithAlphaComponent:0.7];
        polylineView.lineWidth = 3.0;
        return polylineView;
    }
	return nil;
}

- (void)setLabelViewTextColor:(UIView *)view
{
    for (UIView *subview in view.subviews) {
        if ([subview isKindOfClass:[UILabel class]])
        {
            UILabel *label = (UILabel *)subview;
            label.textColor = [UIColor whiteColor];
        }
        else
        {
            [self setLabelViewTextColor:subview];
        }
    }
}

- (void)mapView:(BMKMapView *)mapView didSelectAnnotationView:(BMKAnnotationView *)view
{
    [self setLabelViewTextColor:view.paopaoView];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) viewDidUnload
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void) dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [_dateFormatter release],_dateFormatter = nil;
    self.driveRecordArray = nil;
    self.currentDriveRecordDetail = nil;
    self.showDriveRecordDetail = nil;
    self.editIndexPath = nil;
    [super dealloc];
}

@end


