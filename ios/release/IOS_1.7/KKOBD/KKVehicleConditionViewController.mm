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
    titleView.text = @"我的车况";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
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
    
    _runTime_RecordConditionView = [[BGDriveRecordConditionView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, height_recordCondition)];
    [_runTimeView addSubview:_runTime_RecordConditionView];
    [_runTime_RecordConditionView release];
    
    subOriginY += height_recordCondition;
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, _runTimeView.bounds.size.height - _runTime_RecordConditionView.frame.size.height)];
    _mapView.showsUserLocation = YES;
    _mapView.centerCoordinate = KKAppDelegateSingleton.currentCoordinate2D;
    _mapView.zoomLevel = 14;
    [_runTimeView addSubview:_mapView];
    [_mapView release];
    
    [self.view addSubview:_runTimeView];
    [_runTimeView release];
    
    //行车日志View
    
    subOriginY = 0;
    _recordView =[[UIView alloc] initWithFrame:CGRectMake(0, originY, 320, currentScreenHeight - originY - [self getOrignY] - 44 - 49)];
    _recordView.hidden = YES;
    
    _record_showView = [[UIView alloc] initWithFrame:_recordView.bounds];
    _record_showView.backgroundColor = [UIColor whiteColor];
    
    subOriginY += height_recordCondition;
    
    _driveRecordTable = [[UITableView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, currentScreenHeight - [self getOrignY] - originY - 44 - 49 - subOriginY)];
    _driveRecordTable.dataSource = self;
    _driveRecordTable.delegate = self;
    [_driveRecordTable setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    [_record_showView addSubview:_driveRecordTable];
    [_driveRecordTable release];
    
    [_recordView addSubview:_record_showView];
    [_record_showView release];
    
    _record_maskView = [[UIView alloc] initWithFrame:_recordView.bounds];
    _record_maskView.backgroundColor = [UIColor blackColor];
    _record_maskView.alpha = 0.8;
    [_recordView addSubview:_record_maskView];
    [_record_maskView release];
    
    subOriginY = 0;
    
    _record_panView = [[UIView alloc] initWithFrame:panViewShowFrame];
    
    _record_RecordConditionView = [[BGDriveRecordConditionView alloc] initWithFrame:CGRectMake(0, subOriginY, 320, height_recordCondition)];
    [_record_panView addSubview:_record_RecordConditionView];
    [_record_RecordConditionView release];
    
    subOriginY += height_recordCondition;
    
    _dateSelectBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _dateSelectBtn.frame = CGRectMake(130, subOriginY, 60, height_dateSelectBtn);
    [_dateSelectBtn setTitle:@"今天" forState:UIControlStateNormal];
    [_dateSelectBtn addTarget:self action:@selector(dateBtnClicked) forControlEvents:UIControlEventTouchUpInside];
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
    
    //通知中心
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateVehicleCondition) name:@"updateVehicleRealTimeDataNotification" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(driveRecordGetNewPoint) name:Notification_DriveRecord_NewPoint object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(driveRecordUnSave) name:Notification_DriveRecord_UnSave object:nil];
}

-(void) initVariables
{
    self.currentDriveRecordDetail = [KKDriveRecordEngine sharedInstance].driveRecordDetail;
    NSInteger nowDate = [[NSDate date] timeIntervalSince1970];
    _currentDateTimeRange.startTime = [KKHelper getDayStartTime:nowDate];
    _currentDateTimeRange.endTime = [KKHelper getDayEndTime:nowDate];
    self.driveRecordArray = [self queryDriveRecordWithTimeRange:_currentDateTimeRange];
    if([self.driveRecordArray count] > 0)
        self.showDriveRecordDetail = [self.driveRecordArray objectAtIndex:0];
}

-(void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark Event
-(void) dateSelectViewHiddenAnimateion:(BOOL) aHidden
{
    if(aHidden)
    {
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
        [UIView setAnimationRepeatAutoreverses:NO];
        [UIView animateWithDuration:0.8f animations:^{
            _record_panView.frame = panViewHiddenFrame;
            _record_maskView.alpha = 0;
        } completion:^(BOOL finished) {
            _dateSelectView.hidden = aHidden;
            _record_maskView.hidden = aHidden;
            CGRect rect = _record_panView.frame;
            rect.size.height -= height_dateSelectView;
            _record_panView.frame = rect;
        }];
        [UIView commitAnimations];
    }
    else
    {
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
        [UIView setAnimationRepeatAutoreverses:NO];
        [UIView animateWithDuration:0.8f animations:^{
            _record_panView.frame = panViewShowFrame;
            _record_maskView.alpha = 0.8;
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
        self.showDriveRecordDetail = [self.driveRecordArray objectAtIndex:index];
    else
        self.showDriveRecordDetail = nil;
    
    [self removeAllPointsAndPolyLines:YES];
    [self addExistDriveRecordPoints];
    [_driveRecordTable reloadData];
}

#pragma mark - BGDateSelectViewDelegate

-(void) BGDateSelectItemSelected:(DateTimeRange)aTimeRange
{
    [self dateSelectViewHiddenAnimateion:YES];
    _currentDateTimeRange = aTimeRange;
    self.driveRecordArray = [self queryDriveRecordWithTimeRange:aTimeRange];
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
            
            [_mapView retain];
            [_mapView removeFromSuperview];
            _mapView.frame = CGRectMake(0, height_recordCondition, 320, _runTimeView.bounds.size.height - _runTime_RecordConditionView.frame.size.height);
            _mapView.showsUserLocation = YES;
            [_runTimeView addSubview:_mapView];
            [_mapView release];
            
            [self removeAllPointsAndPolyLines];
            [self addExistDriveRecordPoints];
        }
            break;
        case 1:
        {
            _runTimeView.hidden = YES;
            _recordView.hidden = NO;
            
            [_mapView retain];
            [_mapView removeFromSuperview];
            _mapView.frame = CGRectMake(0, 0, 320, height_recordCondition);
            _mapView.showsUserLocation = NO;
            [_record_showView addSubview:_mapView];
            [_mapView release];
            
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
-(void) updateVehicleCondition
{
    [_runTime_RecordConditionView setContentWithRealTimeData:self.currentDriveRecordDetail];
    
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

#pragma mark -
#pragma mark - UITableViewDelegate
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.driveRecordArray count];
}

-(CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 136;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"driveRecordCell";
    BGDriveRecordTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if(cell == nil)
    {
        cell = [[[BGDriveRecordTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier] autorelease];
    }
    
    BGDriveRecordDetail *detail = [self.driveRecordArray objectAtIndex:indexPath.row];
    
    [cell refreshUIWithDriveRecordDetail:detail selected:[detail.appDriveLogId isEqualToString:self.showDriveRecordDetail.appDriveLogId]];
    
    return cell;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self changeDriveRecordWithIndex:indexPath.row];
}

#pragma mark -
#pragma mark - BMKMapViewDelegate
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
        [_mapView setCenterCoordinate:KKAppDelegateSingleton.currentCoordinate2D animated:YES];
    }
    else if(_segmentControl.selectedIndex == 1)
    {
        detail = self.showDriveRecordDetail;
        
        if([detail.pointArray count] > 0)
        {
            BMKCoordinateRegion region;
            CLLocationCoordinate2D center= {0,0},coor={0,0};
            CLLocationCoordinate2D leftCoor = {0,0},rightCoor = {0,0},topCoor = {0,0},bottomCoor = {0,0};
            CGFloat latLDelta=0,latRDelta=0,lonTDelta=0,lonBDelta = 0;
            CGFloat latDelta = 0,lonDelta = 0;
            for(BGDriveRecordPoint *point in detail.pointArray)
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
            [_mapView removeOverlays:_mapView.overlays];
            [array release];
        }
    }
}

-(void) addDriveRecordPointAnnotation:(BGDriveRecordPoint *)point
{
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
    self.driveRecordArray = nil;
    self.currentDriveRecordDetail = nil;
    self.showDriveRecordDetail = nil;
    [super dealloc];
}

@end


