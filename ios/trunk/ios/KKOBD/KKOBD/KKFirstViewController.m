//
//  KKFirstViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKFirstViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKReservationServiceViewController.h"
#import "KKViewUtils.h"
#import "KKCarWarningView.h"
#import "KKServiceSeekingViewController.h"
#import "KKVehicleConditionQueryViewController.h"
#import "KKShopQueryViewController.h"
#import "KKShopQueryViewController.h"
#import "KKAppDelegate.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKBindCarViewController.h"
#import "KKHelper.h"

@interface KKFirstViewController ()

@end

@implementation KKFirstViewController

#pragma mark -
#pragma mark - view methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateVehicleCondition) name:@"updateVehicleConditionNotification" object:nil];//更新车辆状态
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleDtcMessage:) name:@"handleDTCMessageNotification" object:nil];//处理车况信息
    
    [self.navigationController.navigationBar addBgImageView];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
    
    [self initVariables];
    [self initComponents];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (!self.navigationController.navigationBarHidden)
        [self.navigationController setNavigationBarHidden:YES animated:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}

#pragma mark -
#pragma mark custom methods

- (void) initVariables
{
    _soundPlayer = [[KKMsgPlaySound alloc] initForPlayingSoundEffectWith:@"Tink.caf"];
}

- (void) initComponents
{    
    UIImageView *bg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 49)];
    bg.userInteractionEnabled = YES;
    bg.backgroundColor = [UIColor clearColor];
    bg.image = [[UIImage imageNamed:@"bg_Home.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [self.view addSubview:bg];
    [bg release];
    
    float orignY = [self getOrignY];
    
    _carStatusView = [[KKCarStatusView alloc] initWithFrame:CGRectMake(0, orignY, 320, 27)];
    _carStatusView.delegate = self;
    [self.view addSubview:_carStatusView];
    [_carStatusView release];
    
    [self updateVehicleCondition];
    
    orignY += 27;
    
    _wheelView = [[GNWheelView alloc] initWithFrame:CGRectMake(0, orignY, 320, currentScreenHeight - 49 - orignY)];
    _wheelView.delegate = self;
    [self.view addSubview:_wheelView];
    [_wheelView reloadData];
    [_wheelView release];
    
}

#pragma mark -
#pragma mark Event

- (void)updateVehicleCondition
{
    _carStatusView.carStatus = KKAppDelegateSingleton.connectStatus;
    KKModelVehicleDetailInfo *vehicleDetailInfo = KKAppDelegateSingleton.currentVehicle;
    NSString *textStr = @"暂无车辆";
    if (vehicleDetailInfo != nil)
        textStr = [NSString stringWithFormat:@"%@%@",vehicleDetailInfo.vehicleBrand,vehicleDetailInfo.vehicleModel];
    _carStatusView.carModelLb.text = textStr;
}

- (void)handleDtcMessage:(id)sender
{
    NSNotification *notification = (NSNotification *)sender;
    NSString *faultCode = [notification.userInfo objectForKey:@"faultCode"];
    
    KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
    Vc.serviceTypeKey = @"服务范围";
    Vc.remarkString = [KKHelper getVehicleFaultDesWithFaultCode:faultCode andVehicleModelId:KKAppDelegateSingleton.currentVehicle.vehicleModelId];
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

#pragma mark -
#pragma mark GNWheelViewDelegate

- (unsigned int)numberOfRowsOfWheelView:(GNWheelView *)wheelView
{
    return 5;
}
- (UIView *)wheelView:(GNWheelView *)wheelView viewForRowAtIndex:(unsigned int)index
{
    NSInteger tag = (NSInteger)index;
    NSString *string = [NSString stringWithFormat:@"Service-%d",tag];
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 317, 162)];
    view.backgroundColor = [UIColor clearColor];
    view.tag = tag;
    
    UIImageView *v = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 317, 162)];
    v.backgroundColor = [UIColor clearColor];
    v.userInteractionEnabled = YES;
    v.image = [UIImage imageNamed:string];
    v.contentMode = UIViewContentModeCenter;
    [view addSubview:v];
    [v release];
    
    UIView *maskView = [[UIView alloc] initWithFrame:CGRectMake(12, 0, 298, 145)];
    maskView.backgroundColor = [UIColor blackColor];
    maskView.tag = 100;
    [view addSubview:maskView];
    [maskView release];

    return [view autorelease];
}
- (float)rowWidthInWheelView:(GNWheelView *)wheelView
{
    return 317;
}
- (float)rowHeightInWheelView:(GNWheelView *)wheelView
{
    return 162;
}

- (void)wheelView:(GNWheelView *)wheelView didSelectedRowAtIndex:(unsigned int)index
{
    switch (index) {
        case 0:
        {            
            KKReservationServiceViewController *Vc = [[KKReservationServiceViewController alloc] init];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            
            break;
        }
        case 1:
        {
            KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
            Vc.serviceTypeKey = @"洗车服务";
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            
            break;
        }
        case 2:
        {
            KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
            Vc.serviceTypeKey = @"服务范围";
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            
            break;
        }
        case 3:
        {
            KKVehicleConditionQueryViewController *Vc = [[KKVehicleConditionQueryViewController alloc] initWithNibName:@"KKVehicleConditionQueryViewController" bundle:nil];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            
            break;
        }
        case 4:
        {
            KKServiceSeekingViewController *Vc = [[KKServiceSeekingViewController alloc] initWithNibName:@"KKServiceSeekingViewController" bundle:nil];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            
            break;
        }
        default:
            break;
    }
    
    
}

- (void)wheelViewDidScrolledWithVoice
{
    [_soundPlayer play];
//    [[UIDevice currentDevice] playInputClick];
}

#pragma mark -
#pragma mark KKCarStatusViewDelegate

- (void)KKCarStatusViewStatusButtonClicked:(KKCarStatusType)carStatus
{
    KKVehicleConditionQueryViewController *Vc = [[KKVehicleConditionQueryViewController alloc] initWithNibName:@"KKVehicleConditionQueryViewController" bundle:nil];
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)KKCarStatusViewShopButtonClicked
{
    KKBindCarViewController *bindVc= [[KKBindCarViewController alloc] initWithNibName:@"KKBindCarViewController" bundle:nil];
    [self.navigationController pushViewController:bindVc animated:YES];
    [bindVc release];
}

#pragma mark -
#pragma mark memory handle

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _wheelView = nil;
}

- (void)dealloc
{
    [_soundPlayer release];
    _wheelView = nil;
    [super dealloc];
}
@end
