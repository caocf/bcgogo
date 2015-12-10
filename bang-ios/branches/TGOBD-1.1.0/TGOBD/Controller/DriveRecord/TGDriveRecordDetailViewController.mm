//
//  TGDriveRecordDetailViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDriveRecordDetailViewController.h"
#import "TGDriveRecordDetailCountView.h"
#import "TGDataSingleton.h"
#import "TGDriveRecordDBManager.h"

@interface TGDriveRecordDetailViewController ()
@property(nonatomic, strong) NSMutableArray *pointArray;
@property(nonatomic, strong) NSMutableArray *mapOverlays;
@end

@implementation TGDriveRecordDetailViewController

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
    
}

-(void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [_mapView viewWillDisappear];
    _mapView.delegate = nil; // 不用时，置nil
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    _playIndex = 1;
    
    self.mapOverlays = [[NSMutableArray alloc] init];
    
    self.pointArray = (NSMutableArray *)[[TGDriveRecordDBManager sharedInstance] getPlaceNotesArray:self.detail.id];
    
    if(self.pointArray == nil || [self.detail.status isEqualToString:@"DRIVING"])
    {
        [self downLoadPlaceNotes];
    }
    
    viewHeight = [self getViewHeightWithNavigationBar];
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    [self setNavigationTitle:@"轨迹详情"];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, viewHeight)];
    _scrollView.backgroundColor = [UIColor clearColor];
    _scrollView.showsVerticalScrollIndicator = NO;
    [_scrollView setContentSize:CGSizeMake(screenWidth, 312+192)];
    [self.view addSubview:_scrollView];
    
    _countView = [[[NSBundle mainBundle] loadNibNamed:@"TGDriveRecordDetailCountView"
                                                owner:self options:nil] objectAtIndex:0];
    [_countView setValueWithDistance:self.detail.distance
                          travelTime:self.detail.travelTime
                          totalMoney:self.detail.totalOilMoney
                             oilWear:self.detail.oilWear
                             oilCost:self.detail.oilCost
                        totalOilWear:self.totalOilWear
                          chartItems:@[
                                       [[TGChartItem alloc] initWithTitle:@"最差" value:self.worstOilWear type:TGChartItemTypeWorst],
                                       [[TGChartItem alloc] initWithTitle:@"平均" value:self.totalOilWear type:TGChartItemTypeAverage],
                                       [[TGChartItem alloc] initWithTitle:@"本次" value:self.detail.oilWear type:TGChartItemTypeCurrent],
                                       [[TGChartItem alloc] initWithTitle:@"最好" value:self.bestOilWear type:TGChartItemTypeBest]
                                       ]];
    _countView.frame = CGRectMake(0, 0, screenWidth, 312);
    [_scrollView addSubview:_countView];
    
    _mapImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 312, screenWidth, 192)];
    _mapImageView.userInteractionEnabled = YES;
    
    UIImageView *playBackTitleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_title.png"]];
    playBackTitleView.frame = CGRectMake(0, 0, screenWidth, 32);
    UILabel *pbLabel = [[UILabel alloc] initWithFrame:CGRectMake(11, 6, 93, 20)];
    pbLabel.backgroundColor = [UIColor clearColor];
    pbLabel.font = [UIFont systemFontOfSize:17];
    pbLabel.textColor = [UIColor blackColor];
    pbLabel.text = @"轨迹回放";
    [playBackTitleView addSubview:pbLabel];
    [_mapImageView addSubview:playBackTitleView];
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, 32, screenWidth, 160)];
    _mapView.delegate = self;
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.isSelectedAnnotationViewFront = YES;
    _mapView.showsUserLocation = NO;
    [_mapView setZoomLevel:14];
    [_mapImageView addSubview:_mapView];
    [_scrollView addSubview:_mapImageView];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showTGMapView)];
    [_mapImageView addGestureRecognizer:tapGesture];
    
    [self closePlayBack];
}

#pragma mark - Event

- (void)showTGMapView
{
    self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"回放" bgImage:nil target:self action:@selector(rePlay)];
    
    [_mapView removeFromSuperview];
    _mapView.frame = CGRectMake(0, 0, screenWidth, viewHeight);
    
    self.navigationItem.leftBarButtonItem = [TGViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_close.png"] bgImage:nil target:self action:@selector(closePlayBack)];
    
    [self.view addSubview:_mapView];
    
    [self setDriveRecordMapViewRegion:self.pointArray useLocalPoint:NO];
    
    [self startPlayBack];
}

- (void)closePlayBack
{
    self.navigationItem.rightBarButtonItem = nil;
    
    [_mapView removeFromSuperview];
    _mapView.frame = CGRectMake(0, 32, screenWidth, 160);
    [_mapImageView addSubview:_mapView];
    
    self.navigationItem.leftBarButtonItem = [TGViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_navibar_backbtn.png"] bgImage:nil target:self action:@selector(backButtonClicked:)];
    
    [self stopPlayBack];
    
    [self addDriveRecordPolyLine:self.pointArray];
    
    [self setDriveRecordMapViewRegion:self.pointArray useLocalPoint:NO];
}

- (void)playBack
{
    if(_playing)
    {
        if(_playIndex <= [self.pointArray count])
        {
            double delayInSeconds = 0.1;
            
            dispatch_time_t ckTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
            dispatch_after(ckTime, dispatch_get_main_queue(), ^(void){
                [_mapView removeOverlays:self.mapOverlays];
                [self.mapOverlays removeAllObjects];
                [self addDriveRecordPolyLine:[self.pointArray objectsAtIndexes:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, _playIndex)]]];
                _playIndex++;
                [self playBack];
            });
        }
        else
        {
            _playIndex = 1;
            _playing = NO;
        }
    }
    else
    {
        if([self.mapOverlays count] > 0)
        {
            [_mapView removeOverlays:self.mapOverlays];
            [self.mapOverlays removeAllObjects];
        }
        [self addDriveRecordPolyLine:self.pointArray];
    }
}

- (void)startPlayBack
{
    [self stopPlayBack];
    
    _playing = YES;
    _playIndex = 1;
    [self playBack];
}

- (void)stopPlayBack
{
    _playing = NO;
    _playIndex = 1;
    if([self.mapOverlays count] > 0)
    {
        [_mapView removeOverlays:self.mapOverlays];
        [self.mapOverlays removeAllObjects];
    }
}

- (void)rePlay
{
    if(!_playing)
    {
        [self startPlayBack];
    }
}

- (void)downLoadPlaceNotes
{
    [TGProgressHUD showWithStatus:@"正在加载详情..."];
    [[TGHTTPRequestEngine sharedInstance] driveRecordDownLoadDetail:self.detail.id viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            if([((TGModelDriveRecordDetailRsp *)responseObject).detailDriveLogs__TGModelDriveRecordDetail count] > 0)
            {
                TGModelDriveRecordDetail *detail = [((TGModelDriveRecordDetailRsp *)responseObject).detailDriveLogs__TGModelDriveRecordDetail objectAtIndex:0];
                
                [[TGDriveRecordDBManager sharedInstance] updateDriveRecordPlaceNotes:detail.placeNotes id:detail.id];
                self.pointArray = (NSMutableArray *)[[TGDriveRecordDBManager sharedInstance] getPlaceNotesArray:detail.id];
                if([detail.status isEqualToString:@"DRIVING"])
                {
                    [[TGDriveRecordDBManager sharedInstance] updateDriveRecordPlaceNotes:nil id:detail.id];
                }
                [self closePlayBack];
            }
        }
    } failure:self.faultBlock];
}

#pragma mark - BMKMapView
- (void)setDriveRecordMapViewRegion:(NSMutableArray *)pointArray useLocalPoint:(BOOL)useLocalPoint
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
    [_mapView setRegion:region animated:YES];
}

- (void)addDriveRecordPolyLineWithStartPoint:(TGModelDriveRecordPoint *)startPoint endPoint:(TGModelDriveRecordPoint *)endPoint
{
    CLLocationCoordinate2D *points = new CLLocationCoordinate2D[2];
    points[0] = CLLocationCoordinate2DMake(startPoint.lat, startPoint.lon);
    points[1] = CLLocationCoordinate2DMake(endPoint.lat, endPoint.lon);
    BMKPolyline *polyline = [BMKPolyline polylineWithCoordinates:points count:2];
    [self.mapOverlays addObject:polyline];
    [_mapView addOverlay:polyline];
    delete []points;
}

- (void)addDriveRecordPolyLine:(NSArray *)points
{
    int n = [points count];
    if(n > 1)
    {
        CLLocationCoordinate2D *polyPoints = new CLLocationCoordinate2D[n];
        for(int i=0; i<n; i++)
        {
            TGModelDriveRecordPoint *point = [points objectAtIndex:i];
            polyPoints[i] = CLLocationCoordinate2DMake(point.lat, point.lon);
        }
        BMKPolyline *polylines = [BMKPolyline polylineWithCoordinates:polyPoints count:n];
        [self.mapOverlays addObject:polylines];
        [_mapView addOverlay:polylines];
        delete []polyPoints;
    }
}

- (BMKOverlayView *)mapView:(BMKMapView *)mapView viewForOverlay:(id <BMKOverlay>)overlay
{
    if ([overlay isKindOfClass:[BMKPolyline class]]) {
        BMKPolylineView* polylineView = [[BMKPolylineView alloc] initWithOverlay:overlay];
        polylineView.fillColor = [[UIColor cyanColor] colorWithAlphaComponent:1];
        polylineView.strokeColor = [[UIColor blueColor] colorWithAlphaComponent:0.7];
        polylineView.lineWidth = 3.0;
        return polylineView;
    }
	return nil;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc
{
    TGLog(@"");
}

@end
