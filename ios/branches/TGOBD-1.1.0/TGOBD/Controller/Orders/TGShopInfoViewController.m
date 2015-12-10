//
//  TGShopInfoViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGShopInfoViewController.h"
#import "TGDataSingleton.h"
#import "UIImageView+AFNetworking.h"

@interface TGShopInfoViewController ()
@property (nonatomic, strong) UILabel *phoneNum;
@end

@implementation TGShopInfoViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self setNavigationTitle:@"店铺详情"];
    [self initComponents];
}

- (void)viewWillAppear:(BOOL)animated
{
    [_mapView viewWillAppear];
    _mapView.delegate = self;
}

- (void)viewDidAppear:(BOOL)animated
{
    TGDataSingleton *singleton = [TGDataSingleton sharedInstance];
    
    BMKPointAnnotation *annotation = [[BMKPointAnnotation alloc] init];
    CLLocationCoordinate2D coor;
    
    NSArray *coordinate = [singleton.shopInfo.coordinate componentsSeparatedByString:@","];
    
    if ([coordinate count] == 2) {
        coor.latitude = [[coordinate lastObject] doubleValue];
        coor.longitude = [[coordinate firstObject] doubleValue];;
    }
    annotation.coordinate = coor;
    annotation.title = singleton.shopInfo.name;
    _mapView.centerCoordinate = coor;
    _mapView.zoomLevel = 14;
    [_mapView addAnnotation:annotation];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [_mapView selectAnnotation:annotation animated:YES];
    });
}

- (void)viewWillDisappear:(BOOL)animated
{
    [_mapView viewWillDisappear];
    _mapView.delegate = nil;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Methods

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar] + 5;
    CGFloat originX = 5;
    
    TGDataSingleton *singleton = [TGDataSingleton sharedInstance];
    
    UIImageView *shopLogo = [[UIImageView alloc] initWithFrame:CGRectMake(originX, originY, 103, 95)];
    [shopLogo setImageWithURL:[NSURL URLWithString:singleton.shopInfo.smallImageUrl] placeholderImage:[UIImage imageNamed:@"shop_noPhoto.png"]];
    
    
    UILabel *shopName = [[UILabel alloc] initWithFrame:CGRectMake(115 + 3, originY - 5, 190, 40)];
    shopName.text = singleton.shopInfo.name;
    shopName.minimumFontSize = 7;
    shopName.backgroundColor = [UIColor clearColor];
    
    originY += 35;
    
    UIImageView *location = [[UIImageView alloc] initWithFrame:CGRectMake(115, originY + 2, 16, 16)];
    location.image = [UIImage imageNamed:@"icon_location.png"];

    UILabel *address = [[UILabel alloc] initWithFrame:CGRectMake(135, originY, 180, 800)];
    address.text = singleton.shopInfo.address;
    address.numberOfLines = 0;
    address.lineBreakMode = NSLineBreakByWordWrapping;
    [address sizeToFit];
    address.backgroundColor = [UIColor clearColor];
    
    originY += address.frame.size.height;
    
    UIImageView *phone = [[UIImageView alloc] initWithFrame:CGRectMake(115, originY + 12, 16, 16)];
    phone.image = [UIImage imageNamed:@"icon_telephone.png"];
    
    _phoneNum = [[UILabel alloc] initWithFrame:CGRectMake(135, originY, 210, 40)];
    _phoneNum.text = singleton.shopInfo.landLine == nil ? (singleton.shopInfo.mobile == nil ? (singleton.shopInfo.accidentMobile == nil ? @"暂无联系电话": singleton.shopInfo.accidentMobile) :singleton.shopInfo.mobile) : singleton.shopInfo.landLine;
    _phoneNum.backgroundColor = [UIColor clearColor];
    _phoneNum.userInteractionEnabled = YES;
    
    UIButton *phoneBtn = [[UIButton alloc] initWithFrame:_phoneNum.bounds];
    phoneBtn.backgroundColor = [UIColor clearColor];
    [phoneBtn addTarget:self action:@selector(callShop) forControlEvents:UIControlEventTouchUpInside];
    
    [_phoneNum addSubview:phoneBtn];
    
    originY += 55;
    
    _mapView = [[BMKMapView alloc] initWithFrame:CGRectMake(5, originY, 310, [self getViewHeightWithNavigationBar] - originY - 30)];
    
    [self.view addSubview:shopLogo];
    [self.view addSubview:shopName];
    [self.view addSubview:location];
    [self.view addSubview:address];
    [self.view addSubview:phone];
    [self.view addSubview:_phoneNum];
    [self.view addSubview:_mapView];
}

- (void)callShop
{
    if (![_phoneNum.text isEqualToString:@"暂无联系电话"]) {
        [TGHelper makePhone:_phoneNum.text];
    }
}

#pragma mark - baidu map delegate

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id<BMKAnnotation>)annotation
{
    if ([annotation isKindOfClass:[BMKPointAnnotation class]]) {
        BMKPinAnnotationView *newAnnotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"identify"];
        newAnnotationView.pinColor = BMKPinAnnotationColorRed;
        newAnnotationView.animatesDrop = YES;
        return newAnnotationView;
    }
    return nil;
}

@end
