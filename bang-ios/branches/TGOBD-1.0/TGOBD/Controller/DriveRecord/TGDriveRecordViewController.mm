//
//  TGDriveRecordViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDriveRecordViewController.h"
#import "TGDriveRecordTableCell.h"
#import "TGDataSingleton.h"
#import "NSDate+millisecond.h"
#import "TGDriveRecordDetailViewController.h"
#import "TGDriveRecordDBManager.h"

#define HEIGHT_DATESELECTVIEW       58
#define HEIGHT_TABLEVIEWHEADER      220

@interface TGDriveRecordViewController ()
@property(nonatomic, strong) TGModelDriveRecordListRsp  *driveRecordListRsp;
@property(nonatomic, strong) TGModelDriveRecordOilWear  *oilWearInfo;           //油耗信息
@property(nonatomic, strong) BMKPointAnnotation         *carAnnotation;         //汽车当前位置标注
@property(nonatomic, assign) NSInteger                  indexRow;
@end

@implementation TGDriveRecordViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [_mapView viewWillAppear];
    _mapView.delegate = self; // 此处记得不用的时候需要置nil，否则影响内存的释放
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateVehicleCoordinate) name:NOTIFICATION_UpdateVehicleCoordinate object:nil];
    [self updateVehicleCoordinate];
}

-(void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [_mapView viewWillDisappear];
    _mapView.delegate = nil; // 不用时，置nil
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)dealloc
{
    NSLog(@"TGDriveRecordViewController dealloc!");
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    self.indexRow = 0;
    
    _dateFormatter = [[NSDateFormatter alloc] init];
    
    self.oilWearInfo = [[TGModelDriveRecordOilWear alloc] init];
    
    CGFloat height = [self getViewHeightWithNavigationBar];
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];

    /**
     *  时间选择
     */
    UIImage *img = [UIImage imageNamed:@"bg_driveRecord_top.png"];
    UIImageView *countBgImgView = [[UIImageView alloc] initWithImage:img];
    countBgImgView.frame = CGRectMake(0, originY, screenWidth, 48);
    countBgImgView.userInteractionEnabled = YES;
    [self.view addSubview:countBgImgView];
    
    _dateLabel = [[UILabel alloc] initWithFrame:CGRectMake(90, 14, 140, 22)];
    _dateLabel.backgroundColor = [UIColor clearColor];
    _dateLabel.textAlignment = NSTextAlignmentCenter;
    _dateLabel.font = [UIFont systemFontOfSize:18];
    _dateLabel.textColor = [UIColor whiteColor];
    [countBgImgView addSubview:_dateLabel];
    
    _leftDateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_leftDateBtn setFrame:CGRectMake(50, 6, 40, 38)];
    _leftDateBtn.tag = 1;
    [_leftDateBtn setImage:[UIImage imageNamed:@"icon_arrow_left.png"] forState:UIControlStateNormal];
    [_leftDateBtn addTarget:self action:@selector(dateSelectBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_leftDateBtn setExclusiveTouch:YES];
    [countBgImgView addSubview:_leftDateBtn];
    
    _rightDateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [_rightDateBtn setFrame:CGRectMake(230, 6, 40, 38)];
    _rightDateBtn.tag = 2;
    [_rightDateBtn setImage:[UIImage imageNamed:@"icon_arrow_right.png"] forState:UIControlStateNormal];
    [_rightDateBtn addTarget:self action:@selector(dateSelectBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_rightDateBtn setExclusiveTouch:YES];
    [countBgImgView addSubview:_rightDateBtn];
    _rightDateBtn.hidden = YES;
    
    originY += 48;
    
    /**
     *  UIScrollView
     */
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, screenWidth, HEIGHT_TABLEVIEWHEADER)];
    _scrollView.backgroundColor = [UIColor clearColor];
    _scrollView.bounces = NO;
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.showsHorizontalScrollIndicator = NO;
//    [self.view addSubview:_scrollView];
    
    UIImageView *topBgImgView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_driveRecord_top.png"]];
    
    [_scrollView addSubview:topBgImgView];
    
    float sOriginY = 0;     //scrollView里面布局的Y坐标值
    
    float labelOriginX = 10,labelOriginXDelta = 75;
    float labelOriginY1 = 8;
    float labelWidth = 70, labelHeight1=16, labelHeight2=20;
    
    float labelOriginY2 = labelOriginY1 + labelHeight1 + 8;
    float labelOriginY3 = labelOriginY2 + labelHeight2;
    
    //行驶里程
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
    label1.backgroundColor = [UIColor clearColor];
    label1.textAlignment = NSTextAlignmentCenter;
    label1.textColor = [UIColor whiteColor];
    label1.font = [UIFont boldSystemFontOfSize:16];
    label1.text = @"行驶里程";
    [topBgImgView addSubview:label1];
    
    _countDistanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
    _countDistanceLabel.backgroundColor = [UIColor clearColor];
    _countDistanceLabel.textAlignment = NSTextAlignmentCenter;
    _countDistanceLabel.textColor = [UIColor whiteColor];
    _countDistanceLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:20];
    _countDistanceLabel.text = @"20";
    [topBgImgView addSubview:_countDistanceLabel];
    
    
    UILabel *label1_1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY3, labelWidth, labelHeight1)];
    label1_1.backgroundColor = [UIColor clearColor];
    label1_1.textAlignment = NSTextAlignmentCenter;
    label1_1.textColor = [UIColor whiteColor];
    label1_1.font = [UIFont systemFontOfSize:13.0];
    label1_1.text = @"KM";
    [topBgImgView addSubview:label1_1];
    
    labelOriginX += labelOriginXDelta;
    
    //行驶时长
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
    label2.backgroundColor = [UIColor clearColor];
    label2.textAlignment = NSTextAlignmentCenter;
    label2.textColor = [UIColor whiteColor];
    label2.font = [UIFont boldSystemFontOfSize:16];
    label2.text = @"行驶时长";
    [topBgImgView addSubview:label2];
    
    _countTravelTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
    _countTravelTimeLabel.backgroundColor = [UIColor clearColor];
    _countTravelTimeLabel.textAlignment = NSTextAlignmentCenter;
    _countTravelTimeLabel.textColor = [UIColor whiteColor];
    _countTravelTimeLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:20];
    _countTravelTimeLabel.text = @"2600";
    [topBgImgView addSubview:_countTravelTimeLabel];
    
    
    UILabel *label2_1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY3, labelWidth, labelHeight1)];
    label2_1.backgroundColor = [UIColor clearColor];
    label2_1.textAlignment = NSTextAlignmentCenter;
    label2_1.textColor = [UIColor whiteColor];
    label2_1.font = [UIFont systemFontOfSize:13.0];
    label2_1.text = @"H";
    [topBgImgView addSubview:label2_1];
    
    labelOriginX += labelOriginXDelta;
    
    //总油耗
    UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
    label3.backgroundColor = [UIColor clearColor];
    label3.textAlignment = NSTextAlignmentCenter;
    label3.textColor = [UIColor whiteColor];
    label3.font = [UIFont boldSystemFontOfSize:16];
    label3.text = @"总油耗";
    [topBgImgView addSubview:label3];
    
    _countOilWearLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
    _countOilWearLabel.backgroundColor = [UIColor clearColor];
    _countOilWearLabel.textAlignment = NSTextAlignmentCenter;
    _countOilWearLabel.textColor = [UIColor whiteColor];
    _countOilWearLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:20];
    _countOilWearLabel.text = @"160";
    [topBgImgView addSubview:_countOilWearLabel];
    
    
    UILabel *label3_1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY3, labelWidth, labelHeight1)];
    label3_1.backgroundColor = [UIColor clearColor];
    label3_1.textAlignment = NSTextAlignmentCenter;
    label3_1.textColor = [UIColor whiteColor];
    label3_1.font = [UIFont systemFontOfSize:13.0];
    label3_1.text = @"L";
    [topBgImgView addSubview:label3_1];
    
    labelOriginX += labelOriginXDelta;
    
    //平均油耗
    UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
    label4.backgroundColor = [UIColor clearColor];
    label4.textAlignment = NSTextAlignmentCenter;
    label4.textColor = [UIColor whiteColor];
    label4.font = [UIFont boldSystemFontOfSize:16];
    label4.text = @"平均油耗";
    [topBgImgView addSubview:label4];
    
    _countAverageOilWearLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
    _countAverageOilWearLabel.backgroundColor = [UIColor clearColor];
    _countAverageOilWearLabel.textAlignment = NSTextAlignmentCenter;
    _countAverageOilWearLabel.textColor = [UIColor whiteColor];
    _countAverageOilWearLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:20];
    _countAverageOilWearLabel.text = @"10";
    [topBgImgView addSubview:_countAverageOilWearLabel];
    
    
    UILabel *label4_1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY3, labelWidth, labelHeight1)];
    label4_1.backgroundColor = [UIColor clearColor];
    label4_1.textAlignment = NSTextAlignmentCenter;
    label4_1.textColor = [UIColor whiteColor];
    label4_1.font = [UIFont systemFontOfSize:13.0];
    label4_1.text = @"L/100KM";
    [topBgImgView addSubview:label4_1];
    
    sOriginY += labelOriginY3 + labelHeight2 + 8;
    
    topBgImgView.frame = CGRectMake(0, 0, _scrollView.bounds.size.width, sOriginY);
    
    /**
     *  地图UI
     */
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, sOriginY, screenWidth, 140)];
    _mapView.delegate = self;
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.showsUserLocation = NO;
    _mapView.isSelectedAnnotationViewFront = YES;
    [_mapView setZoomLevel:6];
    [_scrollView addSubview:_mapView];
    
    sOriginY += 140;
    
    [_scrollView setContentSize:CGSizeMake(screenWidth, sOriginY)];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height - originY) style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
//    _tableView.bounces = NO;
    _tableView.showsVerticalScrollIndicator = NO;
    _tableView.showsHorizontalScrollIndicator = NO;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.tableHeaderView = _scrollView;
    [self.view addSubview:_tableView];
    
    _refreshTableHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_tableView.bounds.size.height, _tableView.bounds.size.width, _tableView.bounds.size.height)];
    _refreshTableHeaderView.delegate = self;
    _refreshTableHeaderView.refreshTableName = @"driveRecordTableView";
    [_tableView addSubview:_refreshTableHeaderView];
    [_refreshTableHeaderView refreshLastUpdatedDate];
    
    [self dateSelectBtnClicked:nil];
    
    //首次进入页面下拉刷新
    [_tableView setContentOffset:CGPointMake(0, -80) animated:YES];
    [_refreshTableHeaderView egoRefreshScrollViewDidEndDragging:_tableView];
    
    //初始化地图缩放比例
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 0.8 * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        CLLocationCoordinate2D coor = [self getCarCoordination];
        TGModelDriveRecordPoint *point = [[TGModelDriveRecordPoint alloc] init];
        point.lat = coor.latitude;
        point.lon = coor.longitude;
        [self setDriveRecordMapViewRegion:[NSMutableArray arrayWithObject:point] useLocalPoint:YES animated:NO];
        [_mapView setCenterCoordinate:coor];
    });
}

#pragma mark - Event

- (CLLocationCoordinate2D)getCarCoordination
{
    CLLocationCoordinate2D coor = [TGHelper getBaiDuCoordinate2DWithStringLat:[TGDataSingleton sharedInstance].vehicleInfo.coordinateLat stringLon:[TGDataSingleton sharedInstance].vehicleInfo.coordinateLon];

    if(!CLLocationCoordinate2DIsValid(coor))
    {
        coor = [TGDataSingleton sharedInstance].currentCoordinate2D;
    }
    return coor;
}

- (void)updateVehicleCoordinate
{
    CLLocationCoordinate2D coor = [self getCarCoordination];
    if(!self.carAnnotation)
    {
        self.carAnnotation = [[BMKPointAnnotation alloc] init];
    }
    self.carAnnotation.coordinate = coor;
    
    [_mapView setCenterCoordinate:coor animated:YES];
    
    [_mapView removeAnnotation:self.carAnnotation];
    [_mapView addAnnotation:self.carAnnotation];
}

-(NSString *) getTimeRangeString:(DateTimeRange)aTimeRange
{
    NSDate *startDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTimeRange.startTime];
    NSDate *endDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTimeRange.endTime];
    NSString *str=nil,*str1=nil;
    
    switch (2) {
        case 1:
        {
            [_dateFormatter setDateFormat:@"M月d日"];
            str = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"%@",str];
        }
            break;
        case 2:
        {
            [_dateFormatter setDateFormat:@"MM/dd"];
            str = [_dateFormatter stringFromDate:startDate];
            str1 = [_dateFormatter stringFromDate:endDate];
            return [NSString stringWithFormat:@"%@ - %@",str,str1];
            
        }
            break;
        case 3:
        {
            [_dateFormatter setDateFormat:@"M月"];
            str = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"%@",str];
        }
            break;
    }
}

- (void)dateSelectBtnClicked:(UIButton *)sender
{
    if(sender)
    {
        switch (sender.tag) {
            case 1:
            {
                _currentWeekIndex -= 1;
            }
                break;
            case 2:
            {
                _currentWeekIndex += 1;
            }
                break;
            default:
                break;
        }
    }
    
    currentTimeRange = [TGHelper getWeekTimeRangeWithIndex:_currentWeekIndex];
    _dateLabel.text = _currentWeekIndex == 0 ? @"本周统计" : [self getTimeRangeString:currentTimeRange];
    
    _rightDateBtn.hidden = _currentWeekIndex == 0;
    
    self.indexRow = 0;
    
    [self loadDataFromDB];
    
    [self reloadDriveRecordData];
    
    [self refreshView];
}

- (void)loadDataFromDB
{
    self.driveRecordListRsp = nil;
    
    self.driveRecordListRsp = [[TGModelDriveRecordListRsp alloc] init];
    
    NSArray *array = [[TGDriveRecordDBManager sharedInstance] getDriveRecordList:[TGDataSingleton sharedInstance].userInfo.userNo startTime:currentTimeRange.startTime endTime:currentTimeRange.endTime];
    self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail = (NSMutableArray *)array;
    
    NSInteger   subtotalTravelTime = 0;
    CGFloat     subtotalDisance = 0;
    CGFloat     subtotalOilMoney = 0;
    CGFloat     subtotalOilWear = 0;
    CGFloat     subtotalOilCost = 0;
    for(TGModelDriveRecordDetail *detail in array)
    {
        subtotalTravelTime  += detail.travelTime;
        subtotalDisance     += detail.distance;
        subtotalOilMoney    += detail.totalOilMoney;
        subtotalOilCost     += detail.oilCost;
    }
    if(subtotalDisance != 0)
        subtotalOilWear = (subtotalOilCost/subtotalDisance)*100;
    
    self.driveRecordListRsp.subtotalTravelTime  = subtotalTravelTime;
    self.driveRecordListRsp.subtotalDistance    = subtotalDisance;
    self.driveRecordListRsp.subtotalOilMoney    = subtotalOilMoney;
    self.driveRecordListRsp.subtotalOilWear     = subtotalOilWear;
    self.driveRecordListRsp.subtotalOilCost     = subtotalOilCost;
}

- (void)reloadDriveRecordData
{
    _isloading = YES;
    
    if(!(self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail && [self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail count] > 0))
    {
        [TGProgressHUD show];
    }
    
    [[TGHTTPRequestEngine sharedInstance] driveRecordDownLoadList:currentTimeRange.startTime endTime:currentTimeRange.endTime viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            self.driveRecordListRsp = (TGModelDriveRecordListRsp *)responseObject;
            
            self.oilWearInfo.worstOilWear = self.driveRecordListRsp.worstOilWear;
            self.oilWearInfo.bestOilWear = self.driveRecordListRsp.bestOilWear;
            self.oilWearInfo.totalOilWear = self.driveRecordListRsp.totalOilWear;
            
            [[TGDriveRecordDBManager sharedInstance] updateDriveRecordWithArray:self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail];
            
            [self downLoadFinishedTableViewData];
            [self refreshView];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self httpRequestSystemError:error];
        
        [self downLoadFinishedTableViewData];
    }];
}

- (void)downLoadFinishedTableViewData
{
    _isloading = NO;
    
    [_refreshTableHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_tableView];
}

- (void)refreshView
{
    _countDistanceLabel.text = [NSString stringWithFormat:@"%.1f",self.driveRecordListRsp.subtotalDistance];
    _countTravelTimeLabel.text = [NSDate dateIntervalHourStringWithSeconds:self.driveRecordListRsp.subtotalTravelTime];
    _countOilWearLabel.text = [NSString stringWithFormat:@"%.1f",self.driveRecordListRsp.subtotalOilCost];
    _countAverageOilWearLabel.text = [NSString stringWithFormat:@"%.1f",self.driveRecordListRsp.subtotalOilWear];
    
    NSLog(@"%@",[NSString stringWithFormat:@"%.1f",self.driveRecordListRsp.subtotalOilWear]);
    
    if(self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail && [self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail count] > 0)
    {
        //行车日志列表有数据
        if(_noDataImageView)
        {
            [_noDataImageView removeFromSuperview];
            _noDataImageView = nil;
        }
    }
    else
    {
        //无数据
        if(_noDataImageView==nil)
        {
            UIImage *img = [UIImage imageNamed:@"no_data.png"];
            _noDataImageView = [[UIImageView alloc] initWithImage:img];
            _noDataImageView.frame = CGRectMake((320-img.size.width)*0.5, 232, img.size.width, img.size.height);
        }
        [_tableView addSubview:_noDataImageView];
    }
    
    [_tableView reloadData];
    [_tableView setContentOffset:CGPointZero animated:NO];
}

#pragma mark - mapViewDelegate
- (void)setDriveRecordMapViewRegion:(NSMutableArray *)pointArray useLocalPoint:(BOOL)useLocalPoint animated:(BOOL)animated
{
    BMKCoordinateRegion region;
    CLLocationCoordinate2D center= {0,0},coor={0,0};
    CLLocationCoordinate2D leftCoor = {0,0},rightCoor = {0,0},topCoor = {0,0},bottomCoor = {0,0};
    
    CGFloat latLDelta=0,latRDelta=0,lonTDelta=0,lonBDelta = 0;
    CGFloat latDelta = 0,lonDelta = 0;
    
    if(useLocalPoint)
        center = coor = leftCoor = rightCoor = topCoor = bottomCoor = [TGDataSingleton sharedInstance].currentCoordinate2D;
    
    for(TGModelDriveRecordPoint *point in pointArray)
    {
        //        [self addDriveRecordPointAnnotation:point];
        
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
    [_mapView setRegion:region animated:animated];
}

//- (void)mapView:(BMKMapView *)mapView didUpdateUserLocation:(BMKUserLocation *)userLocation
//{
//    if(!_locationed)
//    {
//        [mapView setCenterCoordinate:userLocation.location.coordinate];
//        _locationed = YES;
//    }
//}

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    static NSString *identifier = @"driveRecordCarParkingAnnotation";
    BMKPinAnnotationView *view = (BMKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:identifier];
    
    if(view == nil)
    {
        view = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
    }
    view.image = [UIImage imageNamed:@"icon_navi_carparking.png"];
    view.canShowCallout = NO;
    return view;
}

- (void)mapView:(BMKMapView *)mapView onClickedMapBlank:(CLLocationCoordinate2D)coordinate
{
    UIView *view = nil;
    CGRect rect;
    if(!_zoom)
    {
        rect = CGRectMake(0, 0, 320, [self getViewHeightWithNavigationBar]);
        view = self.view;
        [_mapView removeFromSuperview];
        
        _mapView.showsUserLocation = YES;
        
        TGModelDriveRecordPoint *point = [[TGModelDriveRecordPoint alloc] init];
        point.lat = self.carAnnotation.coordinate.latitude;
        point.lon = self.carAnnotation.coordinate.longitude;
        [self setDriveRecordMapViewRegion:[NSMutableArray arrayWithObject:point] useLocalPoint:YES animated:YES];
    }
    else
    {
        rect = CGRectMake(0, 80, 320, 140);
        [_mapView removeFromSuperview];
        view = _scrollView;
        
        _mapView.showsUserLocation = NO;
        [_mapView setCenterCoordinate:self.carAnnotation.coordinate];
    }
    _zoom = !_zoom;
    [view addSubview:_mapView];
    
    [UIView beginAnimations:@"xx" context:nil];
    [UIView animateWithDuration:1.8 delay:0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        _mapView.frame = rect;
    } completion:nil];
    [UIView commitAnimations];
}

#pragma mark - UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 118;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"driverecordTableViewCell";
    TGDriveRecordTableCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[TGDriveRecordTableCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    
    cell.indexRow = indexPath.row;
    [cell refreshUIWithDriveRecordDetail:[self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail objectAtIndex:indexPath.row] selected:self.indexRow==indexPath.row];
    
    return cell;
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    self.indexRow = indexPath.row;
    
    TGDriveRecordDetailViewController *detailVc = [[TGDriveRecordDetailViewController alloc] init];
    detailVc.worstOilWear = self.oilWearInfo.worstOilWear;
    detailVc.bestOilWear = self.oilWearInfo.bestOilWear;
    detailVc.totalOilWear = self.oilWearInfo.totalOilWear;
    detailVc.detail = [self.driveRecordListRsp.driveLogDTOs__TGModelDriveRecordDetail objectAtIndex:indexPath.row];
    [self.navigationController pushViewController:detailVc animated:YES];
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [_refreshTableHeaderView egoRefreshScrollViewDidScroll:_tableView];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    
    [_refreshTableHeaderView egoRefreshScrollViewDidEndDragging:_tableView];
    
}

#pragma mark - EGORefreshTableHeaderDelegate

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView*)view
{
    [self reloadDriveRecordData];
}
- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView*)view
{
    
    return _isloading;
}

- (NSDate*)egoRefreshTableHeaderDataSourceLastUpdated:(EGORefreshTableHeaderView*)view
{
    return [NSDate date];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
