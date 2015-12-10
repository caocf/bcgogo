//
//  TGScanViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <AVFoundation/AVCaptureDevice.h>
#import "TGScanViewController.h"
#import "TGMacro.h"
#import "TGScanMaskView.h"
#import "UIViewController+extend.h"
#import "TGViewUtils.h"

@interface TGScanViewController ()

@end

@implementation TGScanViewController

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
	// Do any additional setup after loading the view.
    
    // ADD: present a barcode reader that scans from the camera feed
    
    self.wantsFullScreenLayout = NO;
    
    [self setNavigationBackGroundImage:@"bg_navigation.png"];
    
    [self setNavigationTitle:@"扫描IMEI号"];
    
    self.navigationItem.leftBarButtonItem = [TGViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_navibar_backbtn.png"] bgImage:nil target:self action:@selector(backButtonClicked:)];
    
    CGRect rect = CGRectMake(0, [self getViewLayoutStartOriginYWithNavigationBar], screenWidth, [self getViewHeightWithNavigationBar]);
    
    self.readerDelegate = self;
    self.supportedOrientationsMask = ZBarOrientationMaskAll;
    self.showsZBarControls = NO;
    self.readerView.torchMode = AVCaptureTorchModeOff;
    self.readerView.frame = rect;
    // TODO: (optional) additional reader configuration here
    
    // EXAMPLE: disable rarely used I2/5 to improve performance
    [self.scanner setSymbology: ZBAR_NONE | ZBAR_PARTIAL | ZBAR_EAN2 | ZBAR_EAN5 | ZBAR_EAN8 | ZBAR_UPCE | ZBAR_ISBN10 | ZBAR_UPCA | ZBAR_EAN13 | ZBAR_ISBN13 | ZBAR_COMPOSITE | ZBAR_I25 | ZBAR_DATABAR | ZBAR_DATABAR_EXP | ZBAR_CODE39 | ZBAR_PDF417 | ZBAR_QRCODE | ZBAR_CODE93 | ZBAR_CODE128
                   config: ZBAR_CFG_ENABLE
                       to: 0];
    
    TGScanMaskView *maskView = [[TGScanMaskView alloc] initWithFrame:rect WithHoleImage:[UIImage imageNamed:@"icon_scan_field.png"]];
    [self.view addSubview:maskView];
}

- (void) imagePickerController: (UIImagePickerController*) reader didFinishPickingMediaWithInfo: (NSDictionary*) info
{
    // ADD: get the decode results
    id<NSFastEnumeration> results =
    [info objectForKey: ZBarReaderControllerResults];
    ZBarSymbol *symbol = nil;
    for(symbol in results)
        // EXAMPLE: just grab the first barcode
        break;
    
    // EXAMPLE: do something useful with the barcode data
    TGLog(@"%@",symbol.data);
    
    if(self.delegate && [self.delegate respondsToSelector:@selector(scanSuccess:)])
    {
        [self.delegate scanSuccess:symbol.data];
    }
    
    // ADD: dismiss the controller (NB dismiss from the *reader*!)
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)backButtonClicked:(UIButton *)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
