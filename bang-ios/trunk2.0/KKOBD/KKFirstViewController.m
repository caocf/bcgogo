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
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKBindCarViewController.h"
#import "KKHelper.h"
#import "KKViolateViewController.h"
#import "KKOilStationMapViewController.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKCustomAlertView.h"
#import "KKSearchCarViewController.h"
#import "KKGlobal.h"
#import "KKError.h"
#import "KKScanViewController.h"
#import "KKAuthorization.h"

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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateVehicleList) name:@"updateVehicleList" object:nil];//获取/更新车辆列表

    [self.navigationController.navigationBar addBgImageView];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
    
    [self initVariables];
    [self initComponents];
    
    [self addStatusBarBackgroundView];

    [self updateVehicleList];
    
    [NSTimer scheduledTimerWithTimeInterval:0.8 target:self selector:@selector(showOBDBindAlertView) userInfo:Nil repeats:NO];
}

-(void) showOBDBindAlertView
{
    if(KKAppDelegateSingleton.bindOBDRemind)
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"是否现在绑定OBD ？" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:^{
            KKAppDelegateSingleton.bindOBDRemind = NO;
            
            KKScanViewController *scanVc = [[KKScanViewController alloc] init];
            scanVc.isFromRegister = YES;
            scanVc.showsZBarControls = NO;
            [self.navigationController pushViewController:scanVc animated:YES];
            [scanVc release];
        }];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            KKAppDelegateSingleton.bindOBDRemind = NO;
            KKSearchCarViewController *searchCarVc = [[KKSearchCarViewController alloc] init];
            searchCarVc.isFromRegister = YES;
            [self.navigationController pushViewController:searchCarVc animated:YES];
            [searchCarVc release];
            
        }];
        [alertView show];
        [alertView release];
    }
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
    
    orignY += 27 + 10;
    
    int height = currentScreenHeight - 49 - orignY -10;
    NSLog(@"%d",height);//480
    int heightBuf = 10;
    int iconHeight = (height - heightBuf*3)/4 ;
    
    
    UIImage *iconBgImg = [UIImage imageNamed:@"icon_bg_Service.png"];
    
    UIImage *iconImg = [UIImage imageNamed:@"icon_xcfw.png"];
    int txtHeight = iconHeight-iconImg.size.height-12;
    UIEdgeInsets imgInsets = UIEdgeInsetsMake(0, (145-iconImg.size.width)*0.5, txtHeight+6, (145-iconImg.size.width)*0.5);
    UIEdgeInsets txtInsets = UIEdgeInsetsMake(iconHeight - txtHeight, -iconImg.size.width, 10, 0);
    
    UIButton *btn1 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn1.frame = CGRectMake(10, orignY, 145, iconHeight);
    btn1.tag = 1;
    //[btn1 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn1.backgroundColor = KKCOLOR_Blue;
    [btn1 setImage:iconImg forState:UIControlStateNormal];
    btn1.imageEdgeInsets = imgInsets;
    [btn1 setTitle:@"洗车服务" forState:UIControlStateNormal];
    btn1.titleEdgeInsets = txtInsets;
    [btn1 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    btn1.opaque = 0.5;
    [self.view addSubview:btn1];
    
    iconImg =[UIImage imageNamed:@"icon_wzcx.png"];
    UIButton *btn2 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn2.frame = CGRectMake(165, orignY, 145, iconHeight);
    btn2.tag = 2;
    //[btn2 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn2.backgroundColor = KKCOLOR_Blue;
    [btn2 setImage:iconImg forState:UIControlStateNormal];
    [btn2 setTitle:@"违章查询" forState:UIControlStateNormal];
    btn2.imageEdgeInsets = imgInsets;
    btn2.titleEdgeInsets = txtInsets;
    [btn2 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    
    orignY += heightBuf+iconHeight;
    
    iconImg =[UIImage imageNamed:@"icon_yyfw.png"];
    UIButton *btn3 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn3.frame = CGRectMake(10, orignY, 145, iconHeight);
    btn3.tag = 3;
    //[btn3 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn3.backgroundColor = KKCOLOR_Blue;
    [btn3 setImage:iconImg forState:UIControlStateNormal];
    [btn3 setTitle:@"预约服务" forState:UIControlStateNormal];
    btn3.imageEdgeInsets = imgInsets;
    btn3.titleEdgeInsets = txtInsets;
    [btn3 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn3];
    
    iconImg =[UIImage imageNamed:@"icon_ckcx.png"];
    UIButton *btn4 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn4.frame = CGRectMake(165, orignY, 145, iconHeight*2+heightBuf);
    btn4.tag = 4;
    //[btn4 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn4.backgroundColor = KKCOLOR_Blue;
    [btn4 setImage:iconImg forState:UIControlStateNormal];
    [btn4 setTitle:@"车况查询" forState:UIControlStateNormal];
    btn4.imageEdgeInsets = imgInsets;
    btn4.titleEdgeInsets = txtInsets;
    [btn4 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn4];
    
    orignY += heightBuf+iconHeight;
    
    iconImg =[UIImage imageNamed:@"icon_dmcx.png"];
    UIButton *btn5 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn5.frame = CGRectMake(10, orignY, 145, iconHeight);
    btn5.tag = 5;
    //[btn5 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn5.backgroundColor = KKCOLOR_Blue;
    [btn5 setImage:iconImg forState:UIControlStateNormal];
    [btn5 setTitle:@"店面查询" forState:UIControlStateNormal];
    btn5.imageEdgeInsets = imgInsets;
    btn5.titleEdgeInsets = txtInsets;
    [btn5 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn5];
    
    orignY += heightBuf+iconHeight;
    
    iconImg =[UIImage imageNamed:@"icon_jyz.png"];
    UIButton *btn6 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn6.frame = CGRectMake(10, orignY, 145, iconHeight);
    btn6.tag = 6;
    //[btn6 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn6.backgroundColor = KKCOLOR_Blue;
    [btn6 setImage:iconImg forState:UIControlStateNormal];
    [btn6 setTitle:@"加油站" forState:UIControlStateNormal];
    btn6.imageEdgeInsets = imgInsets;
    btn6.titleEdgeInsets = txtInsets;
    [btn6 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn6];
    
    iconImg = [UIImage imageNamed:@"icon_fwcx.png"];
    UIButton *btn7 = [UIButton buttonWithType:UIButtonTypeCustom];
    btn7.frame = CGRectMake(165, orignY, 145, iconHeight);
    btn7.tag = 7;
    //[btn7 setBackgroundImage:iconBgImg forState:UIControlStateNormal];
    btn7.backgroundColor = KKCOLOR_Blue;
    [btn7 setImage:iconImg forState:UIControlStateNormal];
    [btn7 setTitle:@"服务查询" forState:UIControlStateNormal];
    btn7.imageEdgeInsets = imgInsets;
    btn7.titleEdgeInsets = txtInsets;
    [btn7 addTarget:self action:@selector(iconBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn7];
    
//    _wheelView = [[GNWheelView alloc] initWithFrame:CGRectMake(0, orignY, 320, currentScreenHeight - 49 - orignY)];
//    _wheelView.delegate = self;
//    [self.view addSubview:_wheelView];
//    [_wheelView reloadData];
//    [_wheelView release];
    
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

-(void) updateVehicleList
{
    [[KKProtocolEngine sharedPtlEngine] vehicleListInfo:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
}

-(void) iconBtnClicked:(UIButton *)sender
{
    [self pageChange:sender.tag];
}

-(void) pageChange:(int) tag
{
    switch (tag) {
        case 1:
        {
            KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
            Vc.serviceTypeKey = @"洗车服务";
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
        }
            break;
        case 2:
        {
            KKViolateViewController *violate = [[KKViolateViewController alloc] init];
            [self.navigationController pushViewController:violate animated:YES];
            [violate release];
        }
            break;
        case 3:
        {
            KKReservationServiceViewController *Vc = [[KKReservationServiceViewController alloc] init];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
        }
            break;
        case 4:
        {
            if([KKAuthorization sharedInstance].accessAuthorization.vehicleCondition)
            {
                KKVehicleConditionQueryViewController *Vc = [[KKVehicleConditionQueryViewController alloc] initWithNibName:@"KKVehicleConditionQueryViewController" bundle:nil];
                [self.navigationController pushViewController:Vc animated:YES];
                [Vc release];
            }
            else
            {
                [KKAppDelegateSingleton jumpToLoginVc];
            }
        }
            break;
        case 5:
        {
            KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
            Vc.serviceTypeKey = @"服务范围";
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
        }
            break;
        case 6:
        {
            KKOilStationMapViewController *oilVc = [[KKOilStationMapViewController alloc] init];
            [self.navigationController pushViewController:oilVc animated:YES];
            [oilVc release];
        }
            break;
        case 7:
        {
            if([KKAuthorization sharedInstance].accessAuthorization.serviceSeeking)
            {
                KKServiceSeekingViewController *Vc = [[KKServiceSeekingViewController alloc] initWithNibName:@"KKServiceSeekingViewController" bundle:nil];
                [self.navigationController pushViewController:Vc animated:YES];
                [Vc release];
            }
            else
            {
                [KKAppDelegateSingleton jumpToLoginVc];
            }
        }
            break;
        default:
            break;
    }
}

#pragma mark -
#pragma mark GNWheelViewDelegate

//- (unsigned int)numberOfRowsOfWheelView:(GNWheelView *)wheelView
//{
//    return 7;
//}
//- (UIView *)wheelView:(GNWheelView *)wheelView viewForRowAtIndex:(unsigned int)index
//{
//    NSInteger tag = (NSInteger)index;
//    NSString *string = [NSString stringWithFormat:@"Service-%d",tag];
//    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 317, 162)];
//    view.backgroundColor = [UIColor clearColor];
//    view.tag = tag;
//    
//    UIImageView *v = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 317, 162)];
//    v.backgroundColor = [UIColor clearColor];
//    v.userInteractionEnabled = YES;
//    v.image = [UIImage imageNamed:string];
//    v.contentMode = UIViewContentModeCenter;
//    [view addSubview:v];
//    [v release];
//    
//    UIView *maskView = [[UIView alloc] initWithFrame:CGRectMake(12, 0, 298, 145)];
//    maskView.backgroundColor = [UIColor blackColor];
//    maskView.tag = 100;
//    [view addSubview:maskView];
//    [maskView release];
//
//    return [view autorelease];
//}
//- (float)rowWidthInWheelView:(GNWheelView *)wheelView
//{
//    return 317;
//}
//- (float)rowHeightInWheelView:(GNWheelView *)wheelView
//{
//    return 162;
//}
//
//- (void)wheelView:(GNWheelView *)wheelView didSelectedRowAtIndex:(unsigned int)index
//{
//    switch (index) {
//        case 0:
//        {            
//            KKReservationServiceViewController *Vc = [[KKReservationServiceViewController alloc] init];
//            [self.navigationController pushViewController:Vc animated:YES];
//            [Vc release];
//        }
//            break;
//        case 1:
//        {
//            KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
//            Vc.serviceTypeKey = @"洗车服务";
//            [self.navigationController pushViewController:Vc animated:YES];
//            [Vc release];
//        }
//            break;
//        case 2:
//        {
//            KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
//            Vc.serviceTypeKey = @"服务范围";
//            [self.navigationController pushViewController:Vc animated:YES];
//            [Vc release];
//        }
//            break;
//        case 3:
//        {
//            KKVehicleConditionQueryViewController *Vc = [[KKVehicleConditionQueryViewController alloc] initWithNibName:@"KKVehicleConditionQueryViewController" bundle:nil];
//            [self.navigationController pushViewController:Vc animated:YES];
//            [Vc release];
//        }
//            break;
//        case 4:
//        {
//            KKServiceSeekingViewController *Vc = [[KKServiceSeekingViewController alloc] initWithNibName:@"KKServiceSeekingViewController" bundle:nil];
//            [self.navigationController pushViewController:Vc animated:YES];
//            [Vc release];
//        }
//            break;
//        case 5:
//        {
//            KKOilStationMapViewController *oilVc = [[KKOilStationMapViewController alloc] init];
//            [self.navigationController pushViewController:oilVc animated:YES];
//            [oilVc release];
//        }
//            break;
//        case 6:
//        {
//            KKViolateViewController *violate = [[KKViolateViewController alloc] init];
//            [self.navigationController pushViewController:violate animated:YES];
//            [violate release];
//        }
//            break;
//        default:
//            break;
//    }
//}
//
//- (void)wheelViewDidScrolledWithVoice
//{
//    [_soundPlayer play];
////    [[UIDevice currentDevice] playInputClick];
//}
#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)vehicleListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKVehicleListRsp *listRsp = (KKVehicleListRsp *)rsp;
    
    [KKAppDelegateSingleton detachVehicleListAndObdList:listRsp.KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo)];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark KKCarStatusViewDelegate

- (void)KKCarStatusViewStatusButtonClicked:(KKCarStatusType)carStatus
{
    [self pageChange:4];
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
}

- (void)dealloc
{
    [_soundPlayer release];
    [super dealloc];
}
@end
