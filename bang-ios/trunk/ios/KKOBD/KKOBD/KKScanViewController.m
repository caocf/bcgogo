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
    KKScanMaskView *maskView = [[KKScanMaskView alloc] initWithFrame:CGRectMake(0, 44, 320, rect.size.height - 44) WithHoleImage:[UIImage imageNamed:@"icon_scan_field.png"]];
    [self.view addSubview:maskView];
    [maskView release];
}

- (void)addNavgationBar
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
    [self dismissModalViewControllerAnimated:YES];
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
            if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKScanViewControllerSuccessWithResult:)])
            {
                [self.delegate KKScanViewControllerSuccessWithResult:array];
            }
            
            [self backButtonClicked];
        }
    }

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
