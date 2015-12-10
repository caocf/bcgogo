//
//  KKShopMapListViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShopMapListViewController.h"
#import "UIViewController+extend.h"
#import "KKApplicationDefine.h"
#import "KKViewUtils.h"
#import "KKAppDelegate.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "KKAppDelegate.h"
#import "UIImageView+WebCache.h"
#import "KKShopDetailViewController.h"
#import "MBProgressHUD.h"
#import "KKShopQueryViewController.h"

@interface KKPointAnnotation : BMKPointAnnotation
@property (nonatomic, assign)NSInteger index;

@end

@implementation KKPointAnnotation
@synthesize index;

@end


@interface KKShopMapListViewController ()

@end

@implementation KKShopMapListViewController

static BOOL Center = NO;

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    if (self.VcType == eShopMapListVcType_home)
        [self addAnnotations];
    else
        [self getShopInfo];
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

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self resignKeyboardNotification];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self removeKeyboardNotification];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    if (self.shopArray == nil)
    {
        NSMutableArray *arr = [[NSMutableArray alloc] init];
        self.shopArray = arr;
        [arr release];
    }
    _page = 1;
    _size = 10;
    _promptDataArray = [[NSMutableArray alloc] init];
    
}

- (void) initComponents
{
    [self setNavgationBar];
    [self createSearchBarWithFrame:CGRectMake(0, 0, 320, 44)];
    [_textField addTarget:self action:@selector(textFieldValueChanged:) forControlEvents:UIControlEventEditingChanged];
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(0, 44, 320, currentScreenHeight - 44 - [self getOrignY] - 44 - 49)];
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.delegate = self;
    _mapView.showsUserLocation = YES;
    [self.view addSubview:_mapView];
    [_mapView release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"店铺地图";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    if (self.VcType == eShopMapListVcType_home)
        self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_shopList.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)createSearchBarWithFrame:(CGRect)rect
{
    UIImage *image = [UIImage imageNamed:@"bg_shopq_searchBar.png"];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:rect];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    image = [UIImage imageNamed:@"bg_shopq_searchBar_field.png"];
    CGSize size = image.size;
    
    UIImageView *bgInputImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 0.5*(44 - image.size.height), 240, image.size.height)];
    bgInputImv.userInteractionEnabled = YES;
    bgInputImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    image = [UIImage imageNamed:@"icon_shopq_searchBar_magnifier.png"];
    UIImageView *marImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 0.5*(size.height - image.size.height), image.size.width, image.size.height)];
    marImv.image = image;
    [bgInputImv addSubview:marImv];
    [marImv release];
    
    _textField = [[UITextField alloc] initWithFrame:CGRectMake(30, 0, 210, size.height)];
    _textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _textField.delegate = self;
    _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    _textField.font = [UIFont systemFontOfSize:15.f];
    _textField.backgroundColor = [UIColor clearColor];
    _textField.textColor = [UIColor blackColor];
    _textField.placeholder = @"店铺名称/地址";
    _textField.text = self.searchStr;
    [bgInputImv addSubview:_textField];
    [_textField release];
    
    image = [UIImage imageNamed:@"icon_shopq_searchBar_btn.png"];
    UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(255, 0.5*(44 - image.size.height), image.size.width, image.size.height)];
    [cancelBtn setBackgroundImage:image forState:UIControlStateNormal];
    [cancelBtn.titleLabel setFont:[UIFont systemFontOfSize:15.f]];
    [cancelBtn.titleLabel setTextColor:[UIColor whiteColor]];
    [cancelBtn setTitle:@"确定" forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [bgImv addSubview:cancelBtn];
    [cancelBtn release];
    
    [bgImv addSubview:bgInputImv];
    [bgInputImv release];
    
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)addAnnotations {
    
    for (int t = 0 ;t < [self.shopArray count] ; t ++)
    {
        KKModelShopInfo *shopInfo = [self.shopArray objectAtIndex:t];
        
        KKPointAnnotation *pointAnnotation = [[KKPointAnnotation alloc]init];
        pointAnnotation.index = (100 + t);
        NSArray *arr = [KKHelper getArray:shopInfo.coordinate BySeparateString:@","];
        CLLocationCoordinate2D coo  = CLLocationCoordinate2DMake([arr[1] doubleValue], [arr[0] doubleValue]);
        pointAnnotation.coordinate = coo;
        pointAnnotation.title = shopInfo.name;
        pointAnnotation.subtitle = shopInfo.address;
        [_mapView addAnnotation:pointAnnotation];
        [pointAnnotation release];
    }
    
    if (_mapView && [self.shopArray count] > 0)
    {
        KKModelShopInfo *shopInfo = [self.shopArray objectAtIndex:0];
        NSArray *arr = [KKHelper getArray:shopInfo.coordinate BySeparateString:@","];
        if ([arr count] == 2)
        {
            BMKCoordinateRegion region;
            region.center.latitude  = [arr[1] doubleValue];
            region.center.longitude = [arr[0] doubleValue];
            region.span.latitudeDelta  = 0.1;
            region.span.longitudeDelta = 0.1;
            _mapView.region   = region;
        }
    }
}


- (void)getShopInfo
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] shopSearchList:[KKHelper getShopListCoordinate2DString:KKAppDelegateSingleton.currentCoordinate2D]
                                       serviceScopeIds:self.serviceIds
                                        coordinateType:KKAppDelegateSingleton.coordinateType
                                              sortType:@"DISTANCE"
                                                areaId:self.currentCity.cityID
                                              cityCode:self.currentCity.cityCode
                                              shopType:@"ALL"
                                              keywords:self.searchStr
                                                isMore:NO
                                                pageNo:_page pageSize:_size delegate:self];

}

- (void)getSuggestInfo
{    
    [[KKProtocolEngine sharedPtlEngine] shopSuggestionsByKey:self.searchStr
                                                    cityCode:self.currentCity.cityCode
                                                      areaId:self.currentCity.cityID
                                                    delegate:self];
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)disclosureTapped:(id)sender
{
    NSInteger tag = [sender tag] - 100;
    
    _mapView.showsUserLocation = NO;
    
    KKModelShopInfo *shopInfo = [self.shopArray objectAtIndex:tag];
    KKShopDetailViewController *Vc= [[KKShopDetailViewController alloc] initWithNibName:@"KKShopDetailViewController" bundle:nil];
    Vc.shopId = shopInfo.id;
    Vc.serviceName = self.serviceName;
    Vc.remarkString = self.remarkString;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
    
}

- (void)cancelButtonClicked
{    
    [_textField resignFirstResponder];
    
//    if ([_textField.text length] > 0)
//    {
//        if (self.VcType == eShopMapListVcType_home)
//        {
//            KKShopMapListViewController *mapVc = [[KKShopMapListViewController alloc] initWithNibName:@"KKShopMapListViewController" bundle:nil];
//            mapVc.VcType = eShopMapListVcType_search;
//            mapVc.searchStr = _textField.text;
//            mapVc.currentCity = self.currentCity;
//            mapVc.serviceIds = self.serviceIds;
//            mapVc.serviceName = self.serviceName;
//            [self.navigationController pushViewController:mapVc animated:YES];
//            [mapVc release];
//        }
//        else
//        {
//            _page = 1;
//            self.searchStr = nilOrString(_textField.text);
//            [self getShopInfo];
//            
//        }
//    }
    
    [_textField resignFirstResponder];
    
    if ([_textField.text length] > 0)
    {
        _page = 1;
        self.searchStr = nilOrString(_textField.text);
        [self getShopInfo];
    }
    
}

- (void) didKeyboardNotification:(NSNotification*)notification
{
    NSString* nName = notification.name;
    NSDictionary* nUserInfo = notification.userInfo;
    if ([nName isEqualToString:UIKeyboardDidShowNotification])
    {
        NSString* sysStr = [[UIDevice currentDevice] systemVersion];
        sysStr = [sysStr substringToIndex:1];
        NSInteger ver = [sysStr intValue];
        if (ver >= 5)
        {
            NSValue* value = [nUserInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
            CGRect rect = CGRectZero;
            [value getValue:&rect];
            float keyboardHeight = rect.size.height;
            float height = 44;
            
            if (_promptTableView != nil)
            {
                float h = currentScreenHeight - [self getOrignY] - height - 44 - keyboardHeight;
                [_promptTableView setFrame:CGRectMake(0, height, 320, h)];
                [_promptTableView setClipsFrame:CGRectMake(0, 0, 320, h)];
            }
        }
    }
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        if (_promptTableView != nil)
        {
            [_promptTableView removeFromSuperview];
            _promptTableView = nil;
        }
    }
}

#pragma mark -
#pragma mark BMKMapViewDelegate

-(void)mapView:(BMKMapView *)mapView didUpdateUserLocation:(BMKUserLocation *)userLocation
{
//    if (!Center)
//    {
//        //给view中心定位
//        BMKCoordinateRegion region;
//        region.center.latitude  = userLocation.location.coordinate.latitude;
//        region.center.longitude = userLocation.location.coordinate.longitude;
//        region.span.latitudeDelta  = 0.1;
//        region.span.longitudeDelta = 0.1;
//        _mapView.region   = region;
//        
//        Center = YES;
//    }
        
}

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    BMKAnnotationView *annotationView = nil;
    
    if ([annotation isKindOfClass:[KKPointAnnotation class]])
    {
        NSString *reuseIdentifier = @"shopDetail";
        
        annotationView = (BMKPinAnnotationView*)[mapView viewForAnnotation:annotation];
        
        if (annotationView == nil)
        {
            KKPointAnnotation *pointAnnotation = (KKPointAnnotation *)annotation;
            
            annotationView = [[[BMKPinAnnotationView alloc] initWithAnnotation:pointAnnotation reuseIdentifier:reuseIdentifier] autorelease];
            ((BMKPinAnnotationView*) annotationView).pinColor = BMKPinAnnotationColorRed;
            ((BMKPinAnnotationView*) annotationView).animatesDrop = YES;
            
            UIImage *image = [UIImage imageNamed:@"btn_Disclosure.png"];
            UIButton *disclosure = [UIButton buttonWithType:UIButtonTypeCustom];
            [disclosure setImage:image forState:UIControlStateNormal];
            [disclosure setFrame:CGRectMake(0, 0, image.size.width, image.size.height)];
            [disclosure addTarget:self action:@selector(disclosureTapped:) forControlEvents:UIControlEventTouchUpInside];
            disclosure.tag = pointAnnotation.index;
            annotationView.rightCalloutAccessoryView = disclosure;
            
        }
    }
    
    return annotationView;    
}

- (void)mapView:(BMKMapView *)mapView regionWillChangeAnimated:(BOOL)animated
{
    
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

- (void)mapView:(BMKMapView *)mapView didDeselectAnnotationView:(BMKAnnotationView *)view
{
    
}

#pragma mark -
#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldValueChanged:(UITextField *)textField
{
    self.searchStr = textField.text;
    if ([self.searchStr length] > 0 && _promptTableView)
        [self getSuggestInfo];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if (_promptTableView != nil)
    {
        [_promptTableView removeFromSuperview];
        _promptTableView = nil;
    }
    
    float height = 44;
    
    _promptTableView = [[KKAutoTableView alloc] initWithFrame:CGRectMake(0, height, 320, currentScreenHeight - height - [self getOrignY] - 44 - 49)];
    _promptTableView.autoDelegate = self;
    [self.view addSubview:_promptTableView];
    [_promptTableView release];
    
}

#pragma mark -
#pragma mark KKAutoTableViewDelegate

- (NSInteger)KKAutoTableViewnumberOfRows
{
    return [_promptDataArray count];
}

- (NSString *)KKAutoTableViewCellShowString:(NSInteger)index
{
    KKModelShopInfo *shopInfo = [_promptDataArray objectAtIndex:index];
    return shopInfo.name;
}

- (void)KKAutoTableViewCellClicked:(NSInteger)index
{
    [_textField resignFirstResponder];
    
    KKModelShopInfo *shopInfo = [_promptDataArray objectAtIndex:index];
    _textField.text  = shopInfo.name;
    
    [self cancelButtonClicked];
}

- (void)KKAutoTableViewRemoveButtonClicked
{
    [_promptTableView removeFromSuperview];
    _promptTableView = nil;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)shopSearchListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelShopSearchListRsp *listRsp = (KKModelShopSearchListRsp *)rsp;
    [self.shopArray removeAllObjects];
    [self.shopArray addObjectsFromArray:listRsp.KKArrayFieldName(shopList, KKModelShopInfo)];
    
    if ([listRsp.KKArrayFieldName(shopList, KKModelShopInfo) count] == 0 && _page == 1)
        [KKCustomAlertView showAlertViewWithMessage:@"该地区暂无相关店铺信息"];

    NSArray* array = [NSArray arrayWithArray:_mapView.annotations];
	[_mapView removeAnnotations:array];
	array = [NSArray arrayWithArray:_mapView.overlays];
	[_mapView removeOverlays:array];
    
    [self addAnnotations];
    
    return KKNumberResultEnd;
}

- (NSNumber *)shopSuggestionsResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"suggest error is %@",error.description);
//        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelShopSuggestionsRsp *suggestRsp = (KKModelShopSuggestionsRsp *)rsp;
    [_promptDataArray removeAllObjects];
    [_promptDataArray addObjectsFromArray:suggestRsp.KKArrayFieldName(shopSuggestionList, KKModelShopInfo)];
    [_promptTableView reloadAllData];
    
    return KKNumberResultEnd;
}


#pragma mark -
#pragma mark Handle memory

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _mapView = nil;
    Center = NO;
}

- (void)dealloc {
    _mapView = nil;
    Center = NO;
    [super dealloc];
}

@end
