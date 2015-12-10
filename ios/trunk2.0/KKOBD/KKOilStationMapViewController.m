//
//  KKOilStationMapViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-6.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKOilStationMapViewController.h"
#import <objc/runtime.h>
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKApplicationDefine.h"
#import "KKModelComplex.h"
#import "KKAppDelegate.h"
#import "KKProtocolEngine.h"
#import "KKGlobal.h"
#import "KKError.h"
#import "KKCarRouteViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "BMKActionPaopaoView.h"
#import "KKOilStationViewController.h"
#import "KKCustomAlertView.h"

const NSString *KKBMKPointAnnotationStationKey = @"BMKPointAnnotationView_Station";
@implementation BMKPointAnnotation(KKAdditional)

- (void)setOilStation:(KKModelOilStation *)aStation
{
    objc_setAssociatedObject(self, KKBMKPointAnnotationStationKey,  aStation, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (KKModelOilStation *)oilStation
{
    return  objc_getAssociatedObject(self, KKBMKPointAnnotationStationKey);
}

@end


@interface KKOilStationMapViewController ()
//@property (nonatomic,retain) UIView *mapScrollView;
@property (nonatomic, assign) BOOL viewIsDisplaying;
@property (nonatomic,assign) int annotationLoadIndex;
@end

@implementation KKOilStationMapViewController

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
    [self addAnnotations];
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
    
    _isFirstLoadData = YES;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setShowPaoPaoView:) name:@"OilStationListClicked" object:nil];
    
    [self setVcEdgesForExtendedLayout];
    [self initComponents];

//    self.mapScrollView = [[[[_mapView subviews] objectAtIndex:0] subviews] objectAtIndex:2];
    
    _currentGcj02Coordinate = KKAppDelegateSingleton.currentCoordinate2D_Gcj02;
    
    [self loadAllData];
    
    [self addAnnotations:self.oilStationListRsp.result.data__KKModelOilStation];
}

- (void) initComponents
{
    [self setNavgationBar];
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - [self getOrignY] - 49)];
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.delegate = self;
    _mapView.isSelectedAnnotationViewFront = YES;
    _mapView.showsUserLocation = YES;
    [_mapView setCenterCoordinate:KKAppDelegateSingleton.currentCoordinate2D];
    _mapView.zoomLevel = 14;
    [self.view addSubview:_mapView];
    [_mapView release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"加油站地图";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_shopList.png"] bgImage:nil target:self action:@selector(oilListBtnClicked)];
}

-(void) addAnnotations
{
    [self addAnnotations:self.oilStationListRsp.result.data__KKModelOilStation];
}

-(void) addAnnotations:(NSArray *) stations
{
    if(self.viewIsDisplaying)
    {
        for(int i=0;i<[stations count]; i++)
        {
            KKModelOilStation *station = [stations objectAtIndex:i];
            if(!station.isLoadToMap)
            {
                BMKPointAnnotation *annotation = [[BMKPointAnnotation alloc] init];
                annotation.oilStation = station;
                NSDictionary *dic = BMKBaiduCoorForGcj(CLLocationCoordinate2DMake([station.lat doubleValue], [station.lon doubleValue]));
                CLLocationCoordinate2D coo = BMKCoorDictionaryDecode(dic);
                if(self.currentStation && [self.currentStation.id isEqualToString:station.id])
                {
                    [self setCurrentStationAndMoveToCenter:annotation];
                }
                annotation.coordinate = coo;
                annotation.title = station.name;
                annotation.subtitle = station.address;
                station.isLoadToMap = self.viewIsDisplaying;
                [_mapView addAnnotation:annotation];
                [annotation release];
            }
        }
    }
}

-(void) removeAllAnnotations
{
    [_mapView removeAnnotations:_mapView.annotations];
}

-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) oilListBtnClicked
{
    KKOilStationViewController *listView = [[KKOilStationViewController alloc] init];
    listView.oilStationListRsp = self.oilStationListRsp;
    [self.navigationController pushViewController:listView animated:YES];
    [listView release];
}

#pragma mark NSNotificationCenter

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

#pragma mark -
#pragma mark HTTPRequest
-(void) loadAllData
{
    if(self.oilStationListRsp!= nil && ![self.oilStationListRsp.resultcode isEqualToString:@"200"])
    {
        _isEnd = YES;
       // [NSNotificationCenter defaultCenter]
    }
    
    
    if(!_isEnd)
    {
        if(self.oilStationListRsp == nil)
        {
            [[KKProtocolEngine sharedPtlEngine] getOilStationList:_currentGcj02Coordinate Radius:10000 Page:[self.oilStationListRsp.result.pageinfo.current intValue]+1 delegate:self];
        }
        else
        {
            [[KKProtocolEngine sharedPtlEngine] getOilStationList:_currentGcj02Coordinate Radius:10000 Page:[self.oilStationListRsp.result.pageinfo.current intValue]+1 delegate:self];
        }
    }
    if(_isFirstLoadData && !_isEnd)
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
}

-(NSNumber *) oilStationListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        if(_isFirstLoadData)
        {
            [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
                [self loadAllData];
            }];
        }
        else
        {
            _isEnd = YES;
        }
		return KKNumberResultEnd;
	}
    
    _isFirstLoadData = NO;
    
    if([((KKModelOilStationListRsp *)rsp).resultcode isEqualToString:@"200"])
    {
        if(self.oilStationListRsp)
        {
            [self.oilStationListRsp.result.KKArrayFieldName(data, KKModelOilStation) addObjectsFromArray:((KKModelOilStationListRsp *)rsp).result.KKArrayFieldName(data, KKModelOilStation)];
            self.oilStationListRsp.result.pageinfo =((KKModelOilStationListRsp *)rsp).result.pageinfo;
        }
        else
        {
            self.oilStationListRsp = (KKModelOilStationListRsp *)rsp;
        }
        
        if([((KKModelOilStationListRsp *)rsp).result.KKArrayFieldName(data, KKModelOilStation) count] < ((KKModelOilStationListRsp *)rsp).result.pageinfo.pnums)
        {
            _isEnd = YES;
            self.oilStationListRsp.resultcode = @"205"; //请求完成
        }
        
        [self addAnnotations:((KKModelOilStationListRsp *)rsp).result.KKArrayFieldName(data, KKModelOilStation)];
    }
    else
    {
        self.oilStationListRsp.resultcode = ((KKModelOilStationListRsp *)rsp).resultcode;
        _isEnd = YES;
    }
    
    [self loadAllData];
    return KKNumberResultEnd;
}


#pragma mark -
#pragma mark BMKMapViewDelegate

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    BMKAnnotationView *annotationView = nil;
    
    if ([annotation isKindOfClass:[BMKPointAnnotation class]])
    {
        NSString *reuseIdentifier = @"oilStation";
        
        //annotationView = (BMKPinAnnotationView*)[mapView viewForAnnotation:annotation];
        annotationView = [mapView dequeueReusableAnnotationViewWithIdentifier:reuseIdentifier];
        
        BMKPointAnnotation *pointAnnotation = (BMKPointAnnotation *)annotation;
        if (annotationView == nil)
        {
            annotationView = [[[BMKPinAnnotationView alloc] initWithAnnotation:pointAnnotation reuseIdentifier:reuseIdentifier] autorelease];
            ((BMKPinAnnotationView*) annotationView).pinColor = BMKPinAnnotationColorRed;
            ((BMKPinAnnotationView*) annotationView).animatesDrop = YES;
            
            KKOilBubbleView *bubbleView = [[KKOilBubbleView alloc] init];
            bubbleView.oilStation = pointAnnotation.oilStation;
            bubbleView.tag = 88;
            bubbleView.delegate = self;
            [bubbleView setUIFit];
            BMKActionPaopaoView *paopaoView = [[BMKActionPaopaoView alloc] initWithCustomView:bubbleView];
            
            ((BMKPinAnnotationView*) annotationView).paopaoView = paopaoView;
            [bubbleView release];
            [paopaoView release];
        }
        
        
        if(annotationView.paopaoView)
        {
            KKOilBubbleView *bView = (KKOilBubbleView *)[annotationView.paopaoView viewWithTag:88];
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
    if([view.annotation class] == [BMKPointAnnotation class])
    {
        self.currentStation = ((BMKPointAnnotation *)view.annotation).oilStation;
    }
    else
    {
        [self setLabelViewTextColor:view.paopaoView];
    }
}

#pragma mark 导航
- (void)rightBtnClicked
{
    CLLocationCoordinate2D coo = BMKCoorDictionaryDecode(BMKBaiduCoorForGcj(CLLocationCoordinate2DMake([_currentStation.lat doubleValue], [_currentStation.lon doubleValue])));
    //    NSDictionary *dictionary = BMKBaiduCoorForWgs84(KKAppDelegateSingleton.currentCoordinate2D);
    //    CLLocationCoordinate2D currentCoo = BMKCoorDictionaryDecode(dictionary);
    CLLocationCoordinate2D currentCoo = KKAppDelegateSingleton.currentCoordinate2D;
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"baidumap://map/direction?origin=%lf,%lf&destination=%lf,%lf&mode=driving",currentCoo.latitude,currentCoo.longitude,coo.latitude,coo.longitude]];
    
    if ([[UIApplication sharedApplication] canOpenURL:url]) {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"是否打开导航 ？" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            [[UIApplication sharedApplication] openURL:url];
        }];
        [alertView show];
        [alertView release];
    }
    else
    {
        //        [KKCustomAlertView showAlertViewWithMessage:@"本机没有安装百度地图"];
        
        KKModelShopDetail *shopinfo = [[KKModelShopDetail alloc] init];
        shopinfo.name = _currentStation.name;
        shopinfo.address = _currentStation.address;
        shopinfo.coordinate = [NSString stringWithFormat:@"%f,%f",coo.longitude,coo.latitude];
        
        KKCarRouteViewController *Vc = [[KKCarRouteViewController alloc] initWithNibName:@"KKCarRouteViewController" bundle:nil];
        Vc.shopInfo = shopinfo;
        [self.navigationController pushViewController:Vc animated:YES];
        [shopinfo release];
        [Vc release];
    }
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"OilStationListClicked" object:nil];
    
//    self.mapScrollView = nil;
    self.currentStation = nil;
    self.oilStationListRsp = nil;
    [super dealloc];
}

@end
