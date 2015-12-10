//
//  TGOilStationMapViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOilStationMapViewController.h"
#import "TGDataSingleton.h"
#import <objc/runtime.h>
#import "TGRouteViewController.h"
#import "TGOilStationViewController.h"

NSString * const TGBMKPointAnnotationStationKey = @"BMKPointAnnotationView_Station";

@implementation BMKPointAnnotation(TGAdditional)

- (void)setOilStation:(TGModelOilStation *)aStation
{
    objc_setAssociatedObject(self, (__bridge void *)TGBMKPointAnnotationStationKey,  aStation, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (TGModelOilStation *)oilStation
{
    return  objc_getAssociatedObject(self, (__bridge void *)TGBMKPointAnnotationStationKey);
}

@end


@interface TGOilStationMapViewController ()

@property (nonatomic, assign) BOOL viewIsDisplaying;
@property (nonatomic,retain) TGModelOilStation *currentStation;
@property (nonatomic,retain) TGModelOilStationListRsp *oilStationListRsp;
@end

@implementation TGOilStationMapViewController

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
    
    self.viewIsDisplaying = YES;
}

-(void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [_mapView viewWillDisappear];
    _mapView.delegate = nil; // 不用时，置nil
    
    self.viewIsDisplaying = NO;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setShowPaoPaoView:) name:NOTIFICATION_OilStationListClicked object:nil];
    
    _isFirstLoadData = YES;
    
    CGFloat originY = [self getViewLayoutStartOriginY];
    CGFloat height = [self getViewHeight];
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height)];
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.delegate = self;
    _mapView.isSelectedAnnotationViewFront = YES;
    _mapView.showsUserLocation = YES;
    [_mapView setCenterCoordinate:[TGDataSingleton sharedInstance].currentCoordinate2D];
    _mapView.zoomLevel = 14;
    [self.view addSubview:_mapView];
    
    [self loadAllData];
}

#pragma mark - NSNotificationCenter

-(void) setShowPaoPaoView:(NSNotification *)notification
{
    NSString *stationId = [notification.userInfo objectForKey:@"oilStationID"];
    NSArray *annotations = [_mapView annotations];
    for(BMKPointAnnotation *annotation in annotations)
    {
        if([stationId isEqualToString:annotation.oilStation.id])
        {
            [_mapView selectAnnotation:annotation animated:YES];
            [self setCurrentStationAndMoveToCenter:annotation];
            break;
        }
    }
}


-(void) loadAllData
{
    if(self.oilStationListRsp!= nil && ![self.oilStationListRsp.resultcode isEqualToString:@"200"])
    {
        _isEnd = YES;
    }
    
    if(!_isEnd)
    {
        [[TGHTTPRequestEngine sharedInstance] oilStationGetList:[TGDataSingleton sharedInstance].currentCoordinate2D radius:10000 pageNo:[self.oilStationListRsp.result.pageinfo.current intValue]+1 viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
            if([self httpResponseCorrect:responseObject])
            {
                _isFirstLoadData = NO;
                
                if([((TGModelOilStationListRsp *)responseObject).resultcode isEqualToString:@"200"])
                {
                    if(self.oilStationListRsp)
                    {
                        [self.oilStationListRsp.result.TGArrayFieldName(data, TGModelOilStation) addObjectsFromArray:((TGModelOilStationListRsp *)responseObject).result.TGArrayFieldName(data, TGModelOilStation)];
                        self.oilStationListRsp.result.pageinfo =((TGModelOilStationListRsp *)responseObject).result.pageinfo;
                    }
                    else
                    {
                        self.oilStationListRsp = (TGModelOilStationListRsp *)responseObject;
                    }
                    
                    if([((TGModelOilStationListRsp *)responseObject).result.TGArrayFieldName(data, TGModelOilStation) count] < ((TGModelOilStationListRsp *)responseObject).result.pageinfo.pnums)
                    {
                        _isEnd = YES;
                        self.oilStationListRsp.resultcode = @"205"; //请求完成
                    }
                    
                    [self addAnnotations:((TGModelOilStationListRsp *)responseObject).result.TGArrayFieldName(data, TGModelOilStation)];
                }
                else
                {
                    self.oilStationListRsp.resultcode = ((TGModelOilStationListRsp *)responseObject).resultcode;
                    _isEnd = YES;
                }
                
                [self loadAllData];
            }
        } failure:self.faultBlock];
    }
    if(_isFirstLoadData && !_isEnd)
        [TGProgressHUD show];
}


-(void) addAnnotations:(NSArray *) stations
{
    if(self.viewIsDisplaying)
    {
        for(int i=0;i<[stations count]; i++)
        {
            TGModelOilStation *station = [stations objectAtIndex:i];
            if(!station.isLoadToMap)
            {
                BMKPointAnnotation *annotation = [[BMKPointAnnotation alloc] init];
                annotation.oilStation = station;
                if(self.currentStation && [self.currentStation.id isEqualToString:station.id])
                {
                    [self setCurrentStationAndMoveToCenter:annotation];
                }
                
                annotation.coordinate = CLLocationCoordinate2DMake([station.lat doubleValue], [station.lon doubleValue]);
                annotation.title = station.name;
                annotation.subtitle = station.address;
                station.isLoadToMap = self.viewIsDisplaying;
                [_mapView addAnnotation:annotation];
            }
        }
    }
}

-(void) removeAllAnnotations
{
    [_mapView removeAnnotations:_mapView.annotations];
}

#pragma mark BMKMapViewDelegate

//-(void) mapView:(BMKMapView *)mapView didUpdateUserLocation:(BMKUserLocation *)userLocation
//{
//    NSLog(@"BMKMapView:%f,%f",userLocation.coordinate.latitude,userLocation.coordinate.longitude);
//}

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    BMKAnnotationView *annotationView = nil;
    
    if ([annotation isKindOfClass:[BMKPointAnnotation class]])
    {
        NSString * const reuseIdentifier = @"oilStation";
        
        annotationView = [mapView dequeueReusableAnnotationViewWithIdentifier:reuseIdentifier];
        
        BMKPointAnnotation *pointAnnotation = (BMKPointAnnotation *)annotation;
        if (annotationView == nil)
        {
            annotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:pointAnnotation reuseIdentifier:reuseIdentifier];
            ((BMKPinAnnotationView*) annotationView).pinColor = BMKPinAnnotationColorRed;
            ((BMKPinAnnotationView*) annotationView).animatesDrop = YES;
            
            TGOilBubbleView *bubbleView = [[TGOilBubbleView alloc] init];
            bubbleView.oilStation = pointAnnotation.oilStation;
            bubbleView.tag = 88;
            bubbleView.delegate = self;
            [bubbleView setUIFit];
            
            BMKActionPaopaoView *paopaoView = [[BMKActionPaopaoView alloc] initWithCustomView:bubbleView];
            
            ((BMKPinAnnotationView*) annotationView).paopaoView = paopaoView;
        }
        
        
        if(annotationView.paopaoView)
        {
            TGOilBubbleView *bView = (TGOilBubbleView *)[annotationView.paopaoView viewWithTag:88];
            bView.oilStation = pointAnnotation.oilStation;
            [bView setUIFit];
        }
        
        if([pointAnnotation.oilStation.id isEqualToString:self.currentStation.id])
        {
            [annotationView setSelected:YES animated:YES];
            
        }
    }
    
    return annotationView;
}

-(void) setCurrentStationAndMoveToCenter:(BMKPointAnnotation *)annotation
{
    self.currentStation = annotation.oilStation;
    [_mapView setCenterCoordinate:CLLocationCoordinate2DMake([_currentStation.lat doubleValue], [_currentStation.lon doubleValue]) animated:YES];
}

- (void)setLabelViewTextColor:(UIView *)view
{
    for (UIView *subview in view.subviews) {
        if ([subview isKindOfClass:[UILabel class]])
        {
            UILabel *label = (UILabel *)subview;
            label.textColor = [UIColor blackColor];
        }
        else
        {
            [self setLabelViewTextColor:subview];
        }
    }
}

- (void)mapView:(BMKMapView *)mapView didSelectAnnotationView:(BMKAnnotationView *)view
{
    if([view.annotation class] == [BMKPointAnnotation class])
    {
        self.currentStation = ((BMKPointAnnotation *)view.annotation).oilStation;
    }
    else
    {
        [self setLabelViewTextColor:view.paopaoView];
    }
}

#pragma mark - TGOilBubbleViewDelegate
- (void)rightBtnClicked
{
//    CLLocationCoordinate2D coo = CLLocationCoordinate2DMake([_currentStation.lat doubleValue], [_currentStation.lon doubleValue]);
//    TGModelRouteData *routeData = [[TGModelRouteData alloc] init];
//    routeData.endTitle = self.currentStation.name;
//    routeData.endAddress = self.currentStation.address;
//    routeData.endCoordinate2D = coo;
//    
//    TGRouteViewController *routeVc = [[TGRouteViewController alloc] init];
//    routeVc.routeData = routeData;
//    [self.navigationController pushViewController:routeVc animated:YES];
    TGOilStationViewController *oilVc = [[TGOilStationViewController alloc] init];
    oilVc.oilStationListRsp = self.oilStationListRsp;
    [self.navigationController pushViewController:oilVc animated:YES];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    NSLog(@"TGOilStationMapViewController dealloc!");
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
