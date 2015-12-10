//
//  KKScanViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-14.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKScanViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKScanMaskView.h"
#import "ZBarReaderView.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKRegisterAccount3ViewController.h"
#import "KKModelBaseElement.h"
#import "KKBindShopViewController.h"
#import "KKCustomAlertView.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKModelComplex.h"
#import "KKHelper.h"

@interface KKScanViewController ()

@end

@implementation KKScanViewController
@synthesize delegate;

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self hiddenStatusBar:YES];
    [self.readerView start];
    
    Class class = NSClassFromString(@"AVCaptureDevice");
    if (class) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        NSError *error = nil;
        
        if ([device isTorchModeSupported:AVCaptureTorchModeOn])
            [device lockForConfiguration:&error];
        {
            @try {
                [device setTorchMode:AVCaptureTorchModeOff];
            }
            @catch (NSException *exception) {
                NSLog(@"main: Caught %@: %@:%@", [exception name], [exception reason],error);
            }
            [device unlockForConfiguration];
        }
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [super viewWillDisappear:YES];
    [self hiddenStatusBar:NO];
    [self.readerView stop];
}

#pragma mark -
#pragma mark custom methods
- (void) initVariables
{
    self.readerDelegate = self;
}

- (void) initComponents
{
    [self addNavgationBar];
    
    CGRect rect = [UIApplication sharedApplication].keyWindow.bounds;
    int barHeight = self.isFromRegister||self.isInNavigationController ? 0 : 44;
    KKScanMaskView *maskView = [[KKScanMaskView alloc] initWithFrame:CGRectMake(0, barHeight, 320, rect.size.height-barHeight) WithHoleImage:[UIImage imageNamed:@"icon_scan_field.png"]];
    [self.view addSubview:maskView];
    [maskView release];
    
}

- (void)addNavgationBar
{
    if(!self.isFromRegister)
    {
        if(!self.isInNavigationController)
        {
            UINavigationBar *navBar = [self createCustomNaviBar];
            [navBar setFrame:CGRectMake(0, 0, 320, 44)];
            UILabel *label = (UILabel *)navBar.topItem.titleView;
            label.text = @"店铺扫描";
            UIImage *image = [UIImage imageNamed:@"icon_back.png"];
            UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0,0,60, 44)];
            [button addTarget:self action:@selector(backButtonClicked) forControlEvents:UIControlEventTouchUpInside];
            [button setImage:image forState:UIControlStateNormal];
            [navBar addSubview:button];
            [button release];
            [self.view addSubview:navBar];
        }
        else
        {
            [self.navigationItem setHidesBackButton:YES];
            //self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
            
            [self.navigationController.navigationBar addBgImageView];
            [self initTitleView];
            [self setNavigationBarTitle:@"店铺扫描"];
            
            self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItemWithTitle:@"跳过" bgImage:[UIImage imageNamed:@"icon_fgpwBtn.png"] target:self action:@selector(skipButtonClicked)];
        }
    }
    else
    {
        [self.navigationItem setHidesBackButton:YES];
        //self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
        
        [self.navigationController.navigationBar addBgImageView];
        [self initTitleView];
        [self setNavigationBarTitle:@"店铺扫描"];
        
        self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItemWithTitle:@"跳过" bgImage:[UIImage imageNamed:@"icon_fgpwBtn.png"] target:self action:@selector(skipButtonClicked)];
    }
}

- (void)hiddenStatusBar:(BOOL)hidden
{
    if ([self respondsToSelector:@selector(setNeedsStatusBarAppearanceUpdate)]) {
        // iOS 7
        _hiddenStatusBar = hidden;
        [self performSelector:@selector(setNeedsStatusBarAppearanceUpdate)];
    } else {
        // iOS 6
        [[UIApplication sharedApplication] setStatusBarHidden:hidden withAnimation:UIStatusBarAnimationSlide];
    }
}
- (BOOL)prefersStatusBarHidden
{
    return _hiddenStatusBar;
}
#pragma mark -
#pragma mark Events
- (void)backButtonClicked
{
    if(self.isFromRegister)
    {
        [self.navigationController popViewControllerAnimated:YES];
    }
    else
    {
        [self dismissModalViewControllerAnimated:YES];
    }
}
-(void) skipButtonClicked
{
    if(self.isFromRegister)
    {
        if(KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId == nil && KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopName == nil)
        {
            if(KKAppDelegateSingleton.regVehicleDetailInfo.obdSN != nil && KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin != nil)
            {
                KKRegisterAccount3ViewController *reg3Vc = [[KKRegisterAccount3ViewController alloc] init];
                reg3Vc.regEntrance = RegEntrance_ShopSearchView;
                [self.navigationController pushViewController:reg3Vc animated:YES];
                [reg3Vc release];
            }
            else
                [self.navigationController popToRootViewControllerAnimated:YES];
        }
        else
        {
            KKRegisterAccount3ViewController *reg3Vc = [[KKRegisterAccount3ViewController alloc] init];
            reg3Vc.regEntrance = RegEntrance_ShopSearchView;
            [self.navigationController pushViewController:reg3Vc animated:YES];
            [reg3Vc release];
        }
    }
    else
    {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}
#pragma mark -
#pragma mark zBarDelegate

- (void) imagePickerController: (UIImagePickerController*) reader
 didFinishPickingMediaWithInfo: (NSDictionary*) info {
    
    id<NSFastEnumeration> results = [info objectForKey:ZBarReaderControllerResults];
    ZBarSymbol *symbol = nil;
    for(symbol in results) break;
    
    NSLog(@"***********scan isbn view");
    NSLog(@"symbol data : %@ , type:%@",symbol.data,[NSString stringWithFormat:@"%u",symbol.type]);
    
    if ([symbol.data length] > 0)
    {
        NSString *data = symbol.data;
        if ([data canBeConvertedToEncoding:NSShiftJISStringEncoding])
            data = [NSString stringWithCString:[data cStringUsingEncoding:NSShiftJISStringEncoding] encoding:NSUTF8StringEncoding];
        NSLog(@"symbol data after convert : %@", data);
        
        NSArray *array = [data componentsSeparatedByString:@","];
        if ([array count] != 2)
            array = [symbol.data componentsSeparatedByString:@"，"];
        
        if ([array count] == 2)
        {
            if(![KKHelper KKHElpRegexMatchForNum:[array objectAtIndex:0]])
            {
                [self ReadZBarCodeError];
            }
            
            if(self.isFromRegister)
            {
                KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopName = [array objectAtIndex:1];
                KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId = [array objectAtIndex:0];
               
                [self skipButtonClicked];
            }
            else
            {
                switch (self.nextVc) {
                    case NextVc_BindShopVc:
                    {
                        if([KKAppDelegateSingleton.vehicleList count] > 0)
                        {
                            
//                            KKRegisterAccount3ViewController *reg3Vc = [[KKRegisterAccount3ViewController alloc] init];
//                            reg3Vc.regEntrance = RegEntrance_ShopSearchView;
//                            [self.navigationController pushViewController:reg3Vc animated:YES];
//                            [reg3Vc release];
                            KKBindShopViewController *bindShopVc = [[KKBindShopViewController alloc] init];
                            bindShopVc.shopId = [array objectAtIndex:0];
                            bindShopVc.shopName = [array objectAtIndex:1];
                            [self.navigationController pushViewController:bindShopVc animated:YES];
                            [bindShopVc release];
                        }
                        else
                        {
                            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
                            [[KKProtocolEngine sharedPtlEngine] registerShopBind:[array objectAtIndex:0] vehicleId:nil delegate:self];
                        }
                    }
                        break;
                        
                    default:
                    {
                        if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKScanViewControllerSuccessWithResult:)])
                        {
                            [self.delegate KKScanViewControllerSuccessWithResult:array];
                        }
                        
                        [self backButtonClicked];
                    }
                        break;
                }
            }
        }
        else
        {
            [self ReadZBarCodeError];
        }
    }

}

-(void) ReadZBarCodeError
{
    if(self.isFromRegister)
        [KKCustomAlertView showAlertViewWithMessage:@"二维码格式不正确！"];
    else
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"二维码扫描失败！" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            [self skipButtonClicked];
        }];
        [alertView show];
        [alertView release];
    }
    return;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
-(NSNumber *) registerShopBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    [KKCustomAlertView showAlertViewWithMessage:((KKModelProtocolRsp *)rsp).header.desc block:^{
        [self.navigationController popViewControllerAnimated:YES];
    }];
    
    return KKNumberResultEnd;
}
#pragma mark -
#pragma mark Handle memory 

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    NSLog(@"didReceiveMemory warning in KKScanViewController");
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}
- (void)dealloc
{
    self.readerDelegate = nil;
    [super dealloc];
}
@end
