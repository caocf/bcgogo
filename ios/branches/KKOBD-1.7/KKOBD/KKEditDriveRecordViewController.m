//
//  KKEditDriveRecordViewController.m
//  KKOBD
//
//  Created by Jiahai on 14-2-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "KKEditDriveRecordViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKModelBaseElement.h"
#import "KKDriveRecordEngine.h"
#import "KKHelper.h"
#import "KKCustomAlertView.h"
#import "KKApplicationDefine.h"

@interface KKEditDriveRecordViewController ()

@end

@implementation KKEditDriveRecordViewController

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
    
    [self setVcEdgesForExtendedLayout];
    
    [self initComponents];
}

-(void) initVariables
{
    
}

-(void) initComponents
{
    [self initTitleView];
    
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"校准行程";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItemWithTitle:@"保存" bgImage:[UIImage imageNamed:@"icon_fgpwBtn.png"] target:self action:@selector(submitButtonClicked)];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    float originY = 27;
    
    
    UILabel *oilPriceLable = [[UILabel alloc] initWithFrame:CGRectMake(18, originY, 76, 26)];
    oilPriceLable.text = @"油价:";
    oilPriceLable.font = [UIFont systemFontOfSize:16];
    oilPriceLable.backgroundColor = [UIColor clearColor];
    oilPriceLable.minimumFontSize = 16;
    oilPriceLable.textAlignment = UITextAlignmentRight;
    [self.view addSubview:oilPriceLable];
    [oilPriceLable release];
    
    _oilPriceText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(102, originY - 4, 160, 32) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:10];
    _oilPriceText.index = 1;
    _oilPriceText.textField.tag = 1;
    _oilPriceText.transEditNoti = YES;
    _oilPriceText.textField.delegate = self;
    _oilPriceText.textField.keyboardType = UIKeyboardTypeDecimalPad;
    _oilPriceText.textField.text = [NSString stringWithFormat:@"%.2f",self.driveRecordDetail.oilPrice];
    
    UILabel *oilPriceDWLabel = [[UILabel alloc] initWithFrame:CGRectMake(266, originY, 56, 26)];
    oilPriceDWLabel.text = @"元/L";
    oilPriceDWLabel.font = [UIFont systemFontOfSize:16];
    oilPriceDWLabel.backgroundColor = [UIColor clearColor];
    oilPriceDWLabel.minimumFontSize = 16;
    oilPriceDWLabel.textAlignment = UITextAlignmentLeft;
    [self.view addSubview:oilPriceDWLabel];
    [oilPriceDWLabel release];
    
    [self.view addSubview:_oilPriceText];
    [_oilPriceText release];
    
    originY += 45;
    
//    UILabel *oilKindLable = [[UILabel alloc] initWithFrame:CGRectMake(18, originY, 76, 26)];
//    oilKindLable.text = @"燃油类型:";
//    oilKindLable.backgroundColor = [UIColor clearColor];
//    oilKindLable.font = [UIFont systemFontOfSize:16];
//    oilKindLable.textAlignment = UITextAlignmentRight;
//    [self.view addSubview:oilKindLable];
//    [oilKindLable release];
//    
//    _oilKindText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(102, originY - 4, 160, 32) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_shopq_downArrow.png"] WithRightInsetWidth:10];
//    //_vehicleNoText.textField.delegate = self;
//    _oilKindText.textField.text = self.driveRecordDetail.oilKind;
//    
//    UIButton *oilKindTextBtn = [[UIButton alloc] initWithFrame:_oilKindText.bounds];
//    [oilKindTextBtn addTarget:self action:@selector(setPopviewAndShow:) forControlEvents:UIControlEventTouchUpInside];
//    oilKindTextBtn.backgroundColor = [UIColor clearColor];
//    oilKindTextBtn.tag = 1;
//    [_oilKindText addSubview:oilKindTextBtn];
//    [oilKindTextBtn release];
//    
//    [self.view addSubview:_oilKindText];
//    [_oilKindText release];
//    
//    originY += 45;

    UILabel *distanceLable = [[UILabel alloc] initWithFrame:CGRectMake(18, originY, 76, 26)];
    distanceLable.text = @"本次行驶:";
    distanceLable.font = [UIFont systemFontOfSize:16];
    distanceLable.backgroundColor = [UIColor clearColor];
    distanceLable.minimumFontSize = 16;
    distanceLable.textAlignment = UITextAlignmentRight;
    [self.view addSubview:distanceLable];
    [distanceLable release];
    
    _distanceText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(102, originY - 4, 160, 32) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:10];
    _distanceText.index = 2;
    _distanceText.textField.tag = 2;
    _distanceText.transEditNoti = YES;
    _distanceText.textField.delegate = self;
    _distanceText.textField.keyboardType = UIKeyboardTypeDecimalPad;
    _distanceText.textField.text = [NSString stringWithFormat:@"%.1f",self.driveRecordDetail.distance];
    
    [self.view addSubview:_distanceText];
    [_distanceText release];
    
    UILabel *distanceDWLable = [[UILabel alloc] initWithFrame:CGRectMake(266, originY, 56, 26)];
    distanceDWLable.text = @"公里";
    distanceDWLable.font = [UIFont systemFontOfSize:16];
    distanceDWLable.backgroundColor = [UIColor clearColor];
    distanceDWLable.minimumFontSize = 16;
    distanceDWLable.textAlignment = UITextAlignmentLeft;
    [self.view addSubview:distanceDWLable];
    [distanceDWLable release];
    
    originY += 45;
    
    UILabel *oilWearLable = [[UILabel alloc] initWithFrame:CGRectMake(18, originY, 76, 26)];
    oilWearLable.text = @"本次油耗:";
    oilWearLable.font = [UIFont systemFontOfSize:16];
    oilWearLable.backgroundColor = [UIColor clearColor];
    oilWearLable.minimumFontSize = 16;
    oilWearLable.textAlignment = UITextAlignmentRight;
    [self.view addSubview:oilWearLable];
    [oilWearLable release];
    
    _oilWearText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(102, originY - 4, 160, 32) backgroundImage:1 WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:10];
    _oilWearText.textField.enabled = NO;
    _oilWearText.textField.delegate = self;
    _oilWearText.textField.keyboardType = UIKeyboardTypeDecimalPad;
    _oilWearText.textField.text = [self countOilWearWithDistance:self.driveRecordDetail.distance];
    
    [self.view addSubview:_oilWearText];
    [_oilWearText release];
    
    UILabel *oilWearDWLable = [[UILabel alloc] initWithFrame:CGRectMake(266, originY, 58, 26)];
    oilWearDWLable.text = @"L";
    oilWearDWLable.font = [UIFont systemFontOfSize:16];
    oilWearDWLable.backgroundColor = [UIColor clearColor];
    oilWearDWLable.textAlignment = UITextAlignmentLeft;
    [self.view addSubview:oilWearDWLable];
    [oilWearDWLable release];
    
    originY += 45;
    
    UILabel *totalOilMoneyLable = [[UILabel alloc] initWithFrame:CGRectMake(18, originY, 76, 26)];
    totalOilMoneyLable.text = @"本次油费:";
    totalOilMoneyLable.font = [UIFont systemFontOfSize:16];
    totalOilMoneyLable.backgroundColor = [UIColor clearColor];
    totalOilMoneyLable.minimumFontSize = 16;
    totalOilMoneyLable.textAlignment = UITextAlignmentRight;
    [self.view addSubview:totalOilMoneyLable];
    [totalOilMoneyLable release];
    
    _totalOilMoneyText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(102, originY - 4, 160, 32) backgroundImage:1 WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:10];
    _totalOilMoneyText.textField.enabled = NO;
    _totalOilMoneyText.textField.delegate = self;
    _totalOilMoneyText.textField.keyboardType = UIKeyboardTypeDecimalPad;
    _totalOilMoneyText.textField.text = [NSString stringWithFormat:@"%.1f",self.driveRecordDetail.totalOilMoney];
    
    [self.view addSubview:_totalOilMoneyText];
    [_totalOilMoneyText release];
    
    UILabel *totalOilMoneyDWLable = [[UILabel alloc] initWithFrame:CGRectMake(266, originY, 56, 26)];
    totalOilMoneyDWLable.text = @"元";
    totalOilMoneyDWLable.font = [UIFont systemFontOfSize:16];
    totalOilMoneyDWLable.backgroundColor = [UIColor clearColor];
    totalOilMoneyDWLable.minimumFontSize = 16;
    totalOilMoneyDWLable.textAlignment = UITextAlignmentLeft;
    [self.view addSubview:totalOilMoneyDWLable];
    [totalOilMoneyDWLable release];
    
    originY += 56;

//    //修改按钮
//    UIImage *image = [UIImage imageNamed:@"bg_registerBtn.png"];
//    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(24, originY, image.size.width-20, image.size.height-3)];
//    [submitButton setBackgroundColor:[UIColor clearColor]];
//    [submitButton setBackgroundImage:image forState:UIControlStateNormal];
//    [submitButton setTitle:@"保存" forState:UIControlStateNormal];
//    [submitButton addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
//    [self.view addSubview:submitButton];
//    [submitButton release];
    
}

#pragma mark -
#pragma mark - Event
-(NSString *) countOilWearWithDistance:(CGFloat)aDistance
{
    //计算油耗
    return [NSString stringWithFormat:@"%.1f",(self.driveRecordDetail.oilWear / 100) * aDistance];
}

-(NSString *) countTotalMoneyWithPrice:(CGFloat)aPrice distance:(CGFloat)aDistance
{
    //计算总油钱
    return [NSString stringWithFormat:@"%.1f",(self.driveRecordDetail.oilWear / 100) * aPrice * aDistance];
}
-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) submitButtonClicked
{
    [self resignFirstResponder_Text];
    if(![KKHelper KKHElpRegexMatchForFloatValue:_oilPriceText.textField.text])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"油价输入不正确！"];
        return;
    }
    
    if(![KKHelper KKHElpRegexMatchForFloatValue:_oilWearText.textField.text])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"本次油耗输入不正确！"];
        return;
    }
    
    if(![KKHelper KKHElpRegexMatchForFloatValue:_totalOilMoneyText.textField.text])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"本次油费输入不正确！"];
        return;
    }
    
    if(![KKHelper KKHElpRegexMatchForFloatValue:_distanceText.textField.text])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"本次行驶距离输入不正确！"];
        return;
    }
    
    self.driveRecordDetail.oilPrice = [_oilPriceText.textField.text floatValue];
    self.driveRecordDetail.totalOilMoney = [_totalOilMoneyText.textField.text floatValue];
    self.driveRecordDetail.distance = [_distanceText.textField.text floatValue];
    
    self.driveRecordDetail.state = DriveRecordState_UnUploaded;
    
    if([[KKDriveRecordEngine sharedInstance] updateLocalDriveRecord:self.driveRecordDetail])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"车行程校准成功！" block:^(void){
            //发送通知，让列表刷新该条记录
            if(self.delegate && [self.delegate respondsToSelector:@selector(driveRecordEdited)])
            {
                [self.delegate driveRecordEdited];
            }
            [self backButtonClicked];
        }];
    }
}

#pragma mark - KKCustomTextFieldDelegate
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
            [self.view setFrame:CGRectMake(0, keyboardHeight, self.view.bounds.size.width,self.view.bounds.size.height)];//currentScreenHeight - keyboardHeight - 44 - [self getOrignY])];
        }
    }
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        [self.view setFrame:CGRectMake(0, 0, self.view.bounds.size.width,self.view.bounds.size.height)];
    }
}

-(void) resignFirstResponder_Text
{
    [_oilPriceText.textField resignFirstResponder];
    [_oilWearText.textField resignFirstResponder];
    [_totalOilMoneyText.textField resignFirstResponder];
    [_distanceText.textField resignFirstResponder];
}

-(BOOL) textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *str = [textField.text stringByReplacingCharactersInRange:range withString:string];
    
    if([str isEqualToString:@""])
    {
        switch (textField.tag) {
            case 1:
            {
                _oilWearText.textField.text = [self countOilWearWithDistance:[_distanceText.textField.text floatValue]];
                _totalOilMoneyText.textField.text = [self countTotalMoneyWithPrice:0 distance:[_distanceText.textField.text floatValue]];
            }
                break;
            case 2:
            {
                _oilWearText.textField.text = [self countOilWearWithDistance:0];
                _totalOilMoneyText.textField.text = [self countTotalMoneyWithPrice:[_oilPriceText.textField.text floatValue] distance:0];
            }
                break;
            default:
                break;
        }
    }
    else
    {
        if([KKHelper KKHElpRegexMatchForFloatValue:str])
        {
            _oilWearText.textField.text = [self countOilWearWithDistance:[_distanceText.textField.text floatValue]];
            _totalOilMoneyText.textField.text = [self countTotalMoneyWithPrice:[_oilPriceText.textField.text floatValue] distance:[_distanceText.textField.text floatValue]];
        }
    }
    
    return [KKHelper KKHElpRegexMatchForFloatValue:str] || [str isEqualToString:@""];
}

-(void) textFieldDidEndEditing:(UITextField *)textField
{
    if([textField.text isEqualToString:@""])
    {
        textField.text = 0;
    }
}

//- (void)KKCustomTextFieldDidEndEditing:(KKCustomTextField *)sender
//{
//    if(_hideKeyboardRun)
//    {
//        NSString *str = [sender.textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
//        
//        if(sender == _totalOilMoneyText)
//        {
//            if(![KKHelper KKHElpRegexMatchForFloatValue:str])
//            {
//                [KKCustomAlertView showAlertViewWithMessage:@"本次油费输入不正确！" block:^(void){
//                    _hideKeyboardRun = YES;
//                }];
//
//                [self resignFirstResponder_Text];
//                return;
//            }
//            else
//            {
//                _totalMoney = [str floatValue];
//                sender.textField.text = str;
//            }
//        }
//        else
//        {
//            if(![KKHelper KKHElpRegexMatchForFloatValue:str])
//            {
//                [KKCustomAlertView showAlertViewWithMessage:@"输入格式不正确！" block:^(void){
//                    _hideKeyboardRun = YES;
//                }];
//                [self resignFirstResponder_Text];
//                return;
//            }
//            else
//            {
//                if(_totalMoney == 0)
//                {
//                    float total = [_oilPriceText.textField.text floatValue] * [_oilWearText.textField.text floatValue];
//                    
//                    _totalOilMoneyText.textField.text = [NSString stringWithFormat:@"%.1f",total];
//                }
//                sender.textField.text = str;
//            }
//        }
//    }
//}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    self.driveRecordDetail = nil;
    [super dealloc];
}

@end
