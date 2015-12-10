//
//  KKShopDetailViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShopDetailViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKApplicationDefine.h"
#import "KKSmallRatingView.h"
#import "KKOrderOnlineViewController.h"
#import "KKCarRouteViewController.h"
#import "MBProgressHUD.h"
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
#import "KKUtils.h"


//---------KKShopServiceItemsView---------------------------------------

@interface KKShopServiceItemsView : UIView
@property (nonatomic ,retain)UILabel    *firstLabel;
@property (nonatomic ,retain)UILabel    *secondLabel;
@property (nonatomic ,retain)UILabel    *thirdLabel;

- (id)initWithFrame:(CGRect)frame WithViewOffset:(CGFloat)offset;

@end


@implementation KKShopServiceItemsView

- (id)initWithFrame:(CGRect)frame WithViewOffset:(CGFloat)offset
{
    self = [super initWithFrame:frame];
    if (self) {
        float width = (frame.size.width - 2*offset)/3.0;
        
        UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(offset, 0, width, frame.size.height)];
        label1.backgroundColor = [UIColor clearColor];
        label1.textAlignment = UITextAlignmentLeft;
        label1.textColor = [UIColor blackColor];
        label1.font = [UIFont systemFontOfSize:13.f];
        self.firstLabel = label1;
        [self addSubview:label1];
        [label1 release];
        
        UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(offset+width+20, 0, width-40, frame.size.height)];
        label2.backgroundColor = [UIColor clearColor];
        label2.textAlignment = UITextAlignmentCenter;
        label2.textColor = [UIColor blackColor];
        label2.font = [UIFont systemFontOfSize:13.f];
        self.secondLabel = label2;
        [self addSubview:label2];
        [label2 release];
        
        UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(offset+2*width - 40, 0, width+40, frame.size.height)];
        label3.backgroundColor = [UIColor clearColor];
        label3.textAlignment = UITextAlignmentRight;
        label3.textColor = [UIColor blackColor];
        label3.font = [UIFont systemFontOfSize:13.f];
        self.thirdLabel = label3;
        [self addSubview:label3];
        [label3 release];
        
    }
    return self;
}

- (void)dealloc
{
    self.firstLabel = nil;
    self.secondLabel = nil;
    self.thirdLabel = nil;
    [super dealloc];
}
@end


//---------KKShopDetailViewController---------------------------------------
@interface KKShopDetailViewController ()

@end

@implementation KKShopDetailViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    [self getShopDetailInfo];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [_mapView viewWillAppear];
    _mapView.delegate = self;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_mapView viewWillDisappear];
    _mapView.delegate = nil;
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBackGroundView];
    
    _mapView.userTrackingMode = BMKUserTrackingModeNone;
    _mapView.delegate = self;
    _mapView.hidden = YES;
    
    [_mainScrollView setFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY])];
    _mainScrollView.backgroundColor = [UIColor clearColor];
    [self.view bringSubviewToFront:_mainScrollView];
    [_mainScrollView bringSubviewToFront:_mapView];
    
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"店铺详情";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_shopq_map.png"] bgImage:nil target:self action:@selector(mapButtonClicked)];
}

- (void)setBackGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)addShopDetailView
{
    CGPoint startPoint = CGPointMake(16, 13);
    CGSize size = CGSizeZero;
    float width = 0;
    
    NSArray *arr = [KKHelper getArray:self.detailInfo.coordinate BySeparateString:@","];
    CLLocationCoordinate2D coo = {0,0};
    if(!([arr count]<2))
    {
        coo = CLLocationCoordinate2DMake([arr[1] doubleValue], [arr[0] doubleValue]);
    }
    
    double distance = 0;
    if (CLLocationCoordinate2DIsValid(KKAppDelegateSingleton.currentCoordinate2D))
        distance = [KKHelper distanceBetweenOrderBy:KKAppDelegateSingleton.currentCoordinate2D.latitude :coo.latitude :KKAppDelegateSingleton.currentCoordinate2D.longitude :coo.longitude];
    
    NSString *distanceStr = [NSString stringWithFormat:@"距离：%.2fkm",distance/1000];
    size = [distanceStr sizeWithFont:[UIFont systemFontOfSize:10.f] constrainedToSize:CGSizeMake(320, 10)];
    width = 320 - 2*startPoint.x - 5 - size.width;
    
    UILabel *distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(320 - size.width - startPoint.x, startPoint.y + 4, size.width, 10)];
    distanceLabel.backgroundColor = [UIColor clearColor];
    distanceLabel.textColor = [UIColor grayColor];
    distanceLabel.textAlignment = UITextAlignmentLeft;
    distanceLabel.font = [UIFont systemFontOfSize:10.f];
    distanceLabel.text = distanceStr;
    [_mainScrollView addSubview:distanceLabel];
    [distanceLabel release];
    
    UILabel *nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x, startPoint.y , 120, 15)];
    nameLabel.backgroundColor = [UIColor clearColor];
    nameLabel.textColor = KKCOLOR_3359ac;
    nameLabel.textAlignment = UITextAlignmentLeft;
    nameLabel.font = [UIFont boldSystemFontOfSize:15.f];
    nameLabel.numberOfLines = 0;
    nameLabel.text = self.detailInfo.name;
    size = [nameLabel.text sizeWithFont:[UIFont boldSystemFontOfSize:15.f] constrainedToSize:CGSizeMake(width, MAXFLOAT)];
    [nameLabel setFrame:CGRectMake(startPoint.x, startPoint.y, width, size.height)];
    [_mainScrollView addSubview:nameLabel];
    [nameLabel release];
    
    
    startPoint.y += size.height;
    startPoint.y += 10;
    
    UILabel *addressLabel = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x, startPoint.y, 320 - 2*startPoint.x, 13)];
    addressLabel.backgroundColor = [UIColor clearColor];
    addressLabel.textAlignment = UITextAlignmentLeft;
    addressLabel.textColor = [UIColor blackColor];
    addressLabel.numberOfLines = 0;
    addressLabel.font = [UIFont systemFontOfSize:13.f];
    addressLabel.text = [NSString stringWithFormat:@"地址：%@",self.detailInfo.address];
    size = [addressLabel.text sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(320 - 2*startPoint.x, MAXFLOAT)];
    [addressLabel setFrame:CGRectMake(startPoint.x, startPoint.y, 320 - 2*startPoint.x, size.height)];
    [_mainScrollView addSubview:addressLabel];
    [addressLabel release];
    
    startPoint.y += size.height;
    startPoint.y += 5;
    
    UILabel *servicesLabel = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x, startPoint.y, 320 - 2*startPoint.x, 13)];
    servicesLabel.backgroundColor = [UIColor clearColor];
    servicesLabel.textAlignment = UITextAlignmentLeft;
    servicesLabel.textColor = [UIColor blackColor];
    servicesLabel.numberOfLines = 0;
    servicesLabel.font = [UIFont systemFontOfSize:13.f];
    servicesLabel.text = [NSString stringWithFormat:@"服务：%@",self.detailInfo.serviceScope];
    size = [servicesLabel.text sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(320 - 2*startPoint.x, MAXFLOAT)];
    [servicesLabel setFrame:CGRectMake(startPoint.x, startPoint.y, 320 - 2*startPoint.x, size.height)];
    [_mainScrollView addSubview:servicesLabel];
    [servicesLabel release];
    
    startPoint.y += size.height;
    startPoint.y += 10;
    
    [_mapView setCenterCoordinate:coo animated:YES];
    BMKCoordinateRegion viewRegion = BMKCoordinateRegionMake(coo, BMKCoordinateSpanMake(0.02,0.02));
    _mapView.region = viewRegion;
    
    BMKPointAnnotation *pointAnnotation = [[BMKPointAnnotation alloc]init];
    pointAnnotation.coordinate = coo;
    pointAnnotation.title = self.detailInfo.name;
    [_mapView addAnnotation:pointAnnotation];
    [pointAnnotation release];
    
    [_mapView setFrame:CGRectMake(startPoint.x, startPoint.y, 320 - 2*startPoint.x, 98)];
    _mapView.hidden = NO;

    startPoint.y += 98;
    startPoint.y += 10;
    
    UIImage *image = nil;
    
    if (self.detailInfo.memberInfo != nil)
    {
        KKModelMemberInfo *memberInfo = self.detailInfo.memberInfo;
        
        UILabel *serviceRateLb = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x, startPoint.y, 100, 15)];
        serviceRateLb.backgroundColor = [UIColor clearColor];
        serviceRateLb.textColor = [UIColor blackColor];
        serviceRateLb.font = [UIFont systemFontOfSize:15.f];
        serviceRateLb.textAlignment = UITextAlignmentLeft;
        serviceRateLb.text =@"店面会员";
        [_mainScrollView addSubview:serviceRateLb];
        [serviceRateLb release];
        
        startPoint.y += 20;
        
        UILabel *cardLb = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x, startPoint.y+1, 60, 15)];
        cardLb.backgroundColor = [UIColor clearColor];
        cardLb.textColor = [UIColor blackColor];
        cardLb.font = [UIFont systemFontOfSize:13.f];
        cardLb.textAlignment = UITextAlignmentLeft;
        cardLb.text = @"会员卡：";
        [_mainScrollView addSubview:cardLb];
        [cardLb release];
        
        UILabel *cardNoLb = [[UILabel alloc] initWithFrame:CGRectMake(startPoint.x+60, startPoint.y+1, 140, 15)];
        cardNoLb.backgroundColor = [UIColor clearColor];
        cardNoLb.textColor = [UIColor redColor];
        cardNoLb.font = [UIFont systemFontOfSize:13.f];
        cardNoLb.textAlignment = UITextAlignmentLeft;
        cardNoLb.text = memberInfo.memberNo;
        [_mainScrollView addSubview:cardNoLb];
        [cardNoLb release];
        
        UILabel *moneyLb = [[UILabel alloc] initWithFrame:CGRectMake(214, startPoint.y+1, 40, 15)];
        moneyLb.backgroundColor = [UIColor clearColor];
        moneyLb.textColor = [UIColor blackColor];
        moneyLb.font = [UIFont systemFontOfSize:13.f];
        moneyLb.textAlignment = UITextAlignmentRight;
        moneyLb.text = @"余额：";
        [_mainScrollView addSubview:moneyLb];
        [moneyLb release];
        
        UILabel *balanceLb = [[UILabel alloc] initWithFrame:CGRectMake(254, startPoint.y+1, 50, 15)];
        balanceLb.backgroundColor = [UIColor clearColor];
        balanceLb.textColor = [UIColor redColor];
        balanceLb.font = [UIFont systemFontOfSize:13.f];
        balanceLb.textAlignment = UITextAlignmentCenter;
        balanceLb.text =[NSString stringWithFormat:@"%.1f",memberInfo.balance];
        [_mainScrollView addSubview:balanceLb];
        [balanceLb release];
        
        startPoint.y += 15;
        startPoint.y += 8;
        
        image = [UIImage imageNamed:@"icon_serviceDetail_line.png"];
        UIImageView *lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(20, startPoint.y, image.size.width, image.size.height)];
        lineImv.image = image;
        [_mainScrollView addSubview:lineImv];
        [lineImv release];
        
        startPoint.y += 12;
        
        KKShopServiceItemsView *titleItem = [[KKShopServiceItemsView alloc] initWithFrame:CGRectMake(0, startPoint.y, 320, 15) WithViewOffset:startPoint.x];
        titleItem.firstLabel.text = @"服务项目";
        titleItem.secondLabel.text= @"剩余次数";
        titleItem.thirdLabel.text = @"失效时间";
        [_mainScrollView addSubview:titleItem];
        [titleItem release];
        
        startPoint.y += 22;
        
        for (KKModelMemberService *service in memberInfo.KKArrayFieldName(memberServiceList, KKModelMemberService))
        {
            KKShopServiceItemsView *service1Item = [[KKShopServiceItemsView alloc] initWithFrame:CGRectMake(0, startPoint.y, 320, 15) WithViewOffset:startPoint.x];
            service1Item.firstLabel.text = service.serviceName;
            service1Item.secondLabel.text= service.timesStr;
            service1Item.thirdLabel.text = service.deadlineStr;
            [_mainScrollView addSubview:service1Item];
            [service1Item release];
            
            startPoint.y += 22;
        }
        
    }
//    startPoint.y += 20;
    
    KKSmallRatingView *rateView = [[KKSmallRatingView alloc] initWithRank:self.detailInfo.totalScore];
    [rateView setFrame:CGRectMake(startPoint.x, startPoint.y, 90, 15)];
    [_mainScrollView addSubview:rateView];
    [rateView release];
    
    startPoint.y += 22;
    
    
    if ([self.detailInfo.mobile length] > 0 || [self.detailInfo.landLine length] > 0)
    {
        image = [UIImage imageNamed:@"icon_shopDetail_btn_phone.png"];
        
        NSString *telStr = self.detailInfo.mobile;
        if ([telStr length] == 0)
            telStr = self.detailInfo.landLine;
        
        UIButton *phoneBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 -image.size.width), startPoint.y, image.size.width, image.size.height)];
        [phoneBtn addTarget:self action:@selector(phoneButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [phoneBtn setImage:image forState:UIControlStateNormal];
        
        UILabel *phoneNumLabel = [[UILabel alloc] initWithFrame:CGRectMake(108, 27, image.size.width - 108, 12)];
        phoneNumLabel.backgroundColor = [UIColor clearColor];
        phoneNumLabel.textColor = [UIColor whiteColor];
        phoneNumLabel.textAlignment = UITextAlignmentLeft;
        phoneNumLabel.tag = 100;
        phoneNumLabel.text = telStr;
        phoneNumLabel.font = [UIFont systemFontOfSize:12.f];
        [phoneBtn addSubview:phoneNumLabel];
        [phoneNumLabel release];
        [_mainScrollView addSubview:phoneBtn];
        [phoneBtn release];
        
        startPoint.y += image.size.height;
        startPoint.y += 6;
    }
    
    image = [UIImage imageNamed:@"icon_shopDetail_btn_onLine.png"];
    
    UIButton *onLineBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), startPoint.y, image.size.width, image.size.height)];
    [onLineBtn addTarget:self action:@selector(onLineButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [onLineBtn setImage:image forState:UIControlStateNormal];
    [_mainScrollView addSubview:onLineBtn];
    [onLineBtn release];
    
    
    startPoint.y += image.size.height;
    startPoint.y += 10;
    
    [_mainScrollView setFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY])];
    [_mainScrollView setContentSize:CGSizeMake(320, MAX(startPoint.y, currentScreenHeight - 44 - 49 - [self getOrignY]))];
    
}

- (void)getShopDetailInfo
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] shopDetailWithId:self.shopId delegate:self];
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)mapButtonClicked
{
    NSArray *arr = [KKHelper getArray:self.detailInfo.coordinate BySeparateString:@","];
    CLLocationCoordinate2D coo = CLLocationCoordinate2DMake([arr[1] doubleValue], [arr[0] doubleValue]);
//    NSDictionary *dictionary = BMKBaiduCoorForWgs84(KKAppDelegateSingleton.currentCoordinate2D);
//    CLLocationCoordinate2D currentCoo = BMKCoorDictionaryDecode(dictionary);
    CLLocationCoordinate2D currentCoo = KKAppDelegateSingleton.currentCoordinate2D;
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"baidumap://map/direction?origin=%lf,%lf&destination=%lf,%lf&mode=driving",currentCoo.latitude,currentCoo.longitude,coo.latitude,coo.longitude]];
    
    if ([[UIApplication sharedApplication] canOpenURL:url]) {
        [[UIApplication sharedApplication] openURL:url];
    }
    else
    {
//        [KKCustomAlertView showAlertViewWithMessage:@"本机没有安装百度地图"];
        
        KKCarRouteViewController *Vc = [[KKCarRouteViewController alloc] initWithNibName:@"KKCarRouteViewController" bundle:nil];
        Vc.shopInfo = self.detailInfo;
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
    }
}

- (void)phoneButtonClicked:(id)sender
{
    NSMutableArray *telArr = [[NSMutableArray alloc] init];
    if ([self.detailInfo.mobile length] > 0)
    {
        [telArr addObject:self.detailInfo.mobile];
    }
    if ([self.detailInfo.landLine length] > 0)
        [telArr addObject:self.detailInfo.landLine];
    
    if ([telArr count] == 0)
    {
        [telArr release];
        return;
    }
    else if ([telArr count] == 1)
        [KKUtils makePhone:[telArr objectAtIndex:0]];
    else if ([telArr count] == 2)
    {
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:nil
                                                delegate:self
                                       cancelButtonTitle:@"取消"
                                  destructiveButtonTitle:nil
                                       otherButtonTitles:telArr[0],telArr[1], nil];
        [sheet showInView:[UIApplication sharedApplication].keyWindow];
        [sheet release];
    }
    [telArr release];

}

- (void)onLineButtonClicked
{
    if([KKAuthorization sharedInstance].accessAuthorization.orderOnline)
    {
        KKOrderOnlineViewController *Vc = [[KKOrderOnlineViewController alloc] initWithNibName:@"KKOrderOnlineViewController" bundle:nil];
        Vc.detailShopInfo = self.detailInfo;
        Vc.serviceName = self.serviceName;
        Vc.remarkString = self.remarkString;
        [self.navigationController pushViewController:Vc animated:YES];
        [Vc release];
    }
    else
    {
        [KKAppDelegateSingleton jumpToLoginVc];
    }
}


#pragma mark -
#pragma mark BMKMapViewDelegate

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation
{
    BMKAnnotationView *annotationView = nil;
    
    if ([annotation isKindOfClass:[BMKPointAnnotation class]])
    {
        NSString *reuseIdentifier = @"shopDetail";
        
        annotationView = (BMKPinAnnotationView*)[mapView viewForAnnotation:annotation];
        
        if (annotationView == nil)
        {
            annotationView = [[[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:reuseIdentifier] autorelease];
            ((BMKPinAnnotationView*) annotationView).pinColor = BMKPinAnnotationColorGreen;
        }
    }
    
    return annotationView;
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

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != [actionSheet cancelButtonIndex])
    {
        [KKUtils makePhone:[actionSheet buttonTitleAtIndex:buttonIndex]];
    }
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)shopDetailResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelShopDetailRsp *detailShopInfo = (KKModelShopDetailRsp *)rsp;
    self.detailInfo = detailShopInfo.shop;
    [self addShopDetailView];
    
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
    _mainScrollView = nil;
    
}

- (void)dealloc
{
    _mapView = nil;
    _mainScrollView = nil;
    self.detailInfo = nil;
    
    [super dealloc];
}
@end
