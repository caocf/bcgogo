//
//  KKRegisterAccount3ViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKRegisterAccount3ViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKCustomAlertView.h"
#import "KKCustomTextField.h"
#import "KKModelComplex.h"
#import "KKHelper.h"
#import "KKScanViewController.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKUtils.h"
#import "KKError.h"
#import "KKGlobal.h"

@interface KKRegisterAccount3ViewController ()
@property(nonatomic,retain) KKModelCarInfo      *carInfo;
@end

@implementation KKRegisterAccount3ViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void) viewWillDisappear:(BOOL)animated
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self setVcEdgesForExtendedLayout];
    [self initComponents];
    
    
}
- (void) initComponents
{
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    switch (self.regEntrance) {
        case RegEntrance_RegisterView:
        {
            [self setNavigationBarTitle:@"车辆信息"];
        }
            break;
        case RegEntrance_OBDSearchView:
        {
            [self setNavigationBarTitle:@"车辆信息"];
        }
            break;
        case RegEntrance_ShopSearchView:
        {
            [self setNavigationBarTitle:@"车辆信息"];
        }
            break;
            
        default:
            break;
    }    
//    if(!self.isRequired)
//    {
//        self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItemWithTitle:@"跳过" bgImage:[UIImage imageNamed:@"icon_fgpwBtn.png"] target:self action:@selector(skipButtonClicked)];
//    }
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    float orignY1 = 21 ,orignY2 = 10;
    
    if(KKAppDelegateSingleton.regVehicleDetailInfo.obdSN&&KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin)
    {
    //--------------------OBD------------------------------------------------------------------------------------------
    
        UILabel *label0 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
        label0.backgroundColor = [UIColor clearColor];
        label0.textColor = [UIColor blackColor];
        label0.font = [UIFont systemFontOfSize:15.0f];
        label0.textAlignment = UITextAlignmentRight;
        label0.text = @"绑定OBD :";
        [self.view addSubview:label0];
        [label0 release];
        
        _obdText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
        _obdText.index = 10;
        _obdText.textField.enabled = NO;
        _obdText.textField.delegate = self;
        _obdText.textField.text = KKAppDelegateSingleton.regVehicleDetailInfo.obdSN;
        [self.view addSubview:_obdText];
        [_obdText release];
        
        orignY1 += 45;
        orignY2 += 45;
    }
    
    //--------------------车牌号码------------------------------------------------------------------------------------------
    
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label1.backgroundColor = [UIColor clearColor];
    label1.textColor = [UIColor blackColor];
    label1.font = [UIFont systemFontOfSize:15.0f];
    label1.textAlignment = UITextAlignmentRight;
    label1.text = @"车牌号码 :";
    [self.view addSubview:label1];
    [label1 release];
    
    _vehicleNoText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _vehicleNoText.index = 10;
    _vehicleNoText.textField.delegate = self;
    _vehicleNoText.textField.text = KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo;
    [self.view addSubview:_vehicleNoText];
    [_vehicleNoText release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    //--------------------品牌车型------------------------------------------------------------------------------------------
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
    label2.backgroundColor = [UIColor clearColor];
    label2.textColor = [UIColor blackColor];
    label2.font = [UIFont systemFontOfSize:15.0f];
    label2.textAlignment = UITextAlignmentRight;
    label2.text = @"品牌车型 :";
    [self.view addSubview:label2];
    [label2 release];
    
    _vehicleBrandText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _vehicleBrandText.index = 11;
    _vehicleBrandText.textField.text = KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrand;
    _vehicleBrandText.addtionalInfo = KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrandId;
    _carBrandMarkView = [KKHelper creatCarBrandMarkView:_vehicleBrandText.bounds withTitle:@"品牌"];
    if ([_vehicleBrandText.textField.text length] > 0)
        _carBrandMarkView.hidden = YES;
    [_vehicleBrandText addSubview:_carBrandMarkView];
    [self.view addSubview:_vehicleBrandText];
    [_vehicleBrandText release];

    UIButton *carBrandBtn = [[UIButton alloc] initWithFrame:_vehicleBrandText.bounds];
    [carBrandBtn addTarget:self action:@selector(carModelChooseButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    carBrandBtn.backgroundColor = [UIColor clearColor];
    carBrandBtn.tag = 21;
    [_vehicleBrandText addSubview:carBrandBtn];
    [carBrandBtn release];
    
    _vehicleModelText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(202, orignY2, 106, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _vehicleModelText.index = 11;
    _vehicleModelText.textField.text = KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModel;
    _vehicleModelText.addtionalInfo = KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModelId;

    _carModelMarkView = [KKHelper creatCarBrandMarkView:_vehicleModelText.bounds withTitle:@"车型"];
    if ([_vehicleModelText.textField.text length] > 0)
        _carModelMarkView.hidden = YES;
    [_vehicleModelText addSubview:_carModelMarkView];
    [self.view addSubview:_vehicleModelText];
    [_vehicleModelText release];
    
    UIButton *carModelBtn = [[UIButton alloc] initWithFrame:_vehicleModelText.bounds];
    [carModelBtn addTarget:self action:@selector(carModelChooseButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    carModelBtn.backgroundColor = [UIColor clearColor];
    carModelBtn.tag = 22;
    [_vehicleModelText addSubview:carModelBtn];
    [carModelBtn release];
    
    orignY1 += 45;
    orignY2 += 45;
    
    if(KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId && KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopName)
    {
    //--------------------店铺信息------------------------------------------------------------------------------------------
    
        UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY1, 75, 15)];
        label3.backgroundColor = [UIColor clearColor];
        label3.textColor = [UIColor blackColor];
        label3.font = [UIFont systemFontOfSize:15.0f];
        label3.textAlignment = UITextAlignmentRight;
        label3.text = @"绑定店铺 :";
        [self.view addSubview:label3];
        [label3 release];
        
        _shopText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, orignY2, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
        _shopText.index = 10;
        _shopText.textField.enabled = NO;
        _shopText.textField.delegate = self;
        _shopText.textField.text = KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopName;
        [self.view addSubview:_shopText];
        [_shopText release];
        
        orignY1 += 45;
        orignY2 += 45;
    }
    
    UIImage *image = [UIImage imageNamed:@"bg_registerBtn.png"];
    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(14, orignY2+12, image.size.width, image.size.height)];
    [submitButton setBackgroundColor:[UIColor clearColor]];
    [submitButton setBackgroundImage:image forState:UIControlStateNormal];
    [submitButton setTitle:self.regEntrance == RegEntrance_OBDSearchView ? @"下一步" : @"完成" forState:UIControlStateNormal];
    [submitButton addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:submitButton];
    [submitButton release];

}

-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void) skipButtonClicked
{
    KKScanViewController *scanVc = [[KKScanViewController alloc] init];
    scanVc.isFromRegister = YES;
    scanVc.showsZBarControls = NO;
    [self.navigationController pushViewController:scanVc animated:YES];
    [scanVc release];
}

-(void) carModelChooseButtonClicked:(id)sender
{
//    KKSearchCarModelViewController *modelVc = [[KKSearchCarModelViewController alloc] init];
//    modelVc.delegate = self;
//    [self.navigationController pushViewController:modelVc animated:YES];
//    [modelVc release];
    [self resignVcFirstResponder];
    
    NSInteger tag = [sender tag];
    
    KKSearchCarModelViewController *Vc = [[KKSearchCarModelViewController alloc] initWithNibName:@"KKSearchCarModelViewController" bundle:nil];
    Vc.delegate = self;
    Vc.haveTabbar = NO;
    if (tag == 21)
        Vc.isBrand = YES;
    else
    {
        Vc.brandID = (NSString *)_vehicleBrandText.addtionalInfo;
        Vc.isBrand = NO;
    }
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

-(void) submitButtonClicked
{
    [_vehicleNoText.textField resignFirstResponder];
    
    BOOL isVehicleNoCanNil = YES;           //车牌号是否能为空?
    
    if(KKAppDelegateSingleton.regVehicleDetailInfo.obdSN && KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin)
    {
        isVehicleNoCanNil = NO;
    }
    
    if(!isVehicleNoCanNil && _vehicleNoText.textField.text.length<1)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入车牌号！"];
        return;
    }
    
    if(_vehicleNoText.textField.text.length > 0 && ![KKHelper KKHElpRegexMatchForVehicleNo:_vehicleNoText.textField.text])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"车牌号输入不正确！"];
        return;
    }
    
    if(_vehicleNoText.textField.text.length > 0)
    {
        if(_vehicleBrandText.textField.text.length <= 0)
        {
            [KKCustomAlertView showAlertViewWithMessage:@"请选择品牌！"];
            return;
        }
        if(_vehicleModelText.textField.text.length <= 0)
        {
            [KKCustomAlertView showAlertViewWithMessage:@"请选择车型！"];
            return;
        }
    }
    else
        self.carInfo = nil;
    
    KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo = _vehicleNoText.textField.text;

    if(self.carInfo)
    {
        KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrand = self.carInfo.brandName;
        KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrandId = self.carInfo.brandId;
        KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModel = self.carInfo.modelName;
        KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModelId = self.carInfo.modelId;
    }
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    if(KKAppDelegateSingleton.regVehicleDetailInfo.obdSN && KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin)
    {
        [self obdBindAPI];
    }
    else
    {
        if(KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo && KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo.length > 0)
        {
            [self saveVehicleInfoAPI];
        }
        else
        {
            if(KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId && KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopName)
            {
                [self shopBindAPI];
            }
            else
                [self.navigationController popToRootViewControllerAnimated:YES];
        }
    }
}

-(void) saveVehicleInfoAPI
{
    [[KKProtocolEngine sharedPtlEngine] vehicleSaveInfo:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleId
                                             vehicleVin:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin
                                              vehicleNo:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo
                                           vehicleModel:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModel
                                         vehicleModelId:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModelId
                                           vehicleBrand:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrand
                                         vehicleBrandId:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrandId
                                                  obdSN:KKAppDelegateSingleton.regVehicleDetailInfo.obdSN
                                          bindingShopId:KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId
                                                 userNo:[KKProtocolEngine sharedPtlEngine].userName
                                               engineNo:KKAppDelegateSingleton.regVehicleDetailInfo.engineNo
                                               registNo:KKAppDelegateSingleton.regVehicleDetailInfo.registNo
                                    nextMaintainMileage:KKAppDelegateSingleton.regVehicleDetailInfo.nextMaintainMileage
                                      nextInsuranceTime:[KKAppDelegateSingleton.regVehicleDetailInfo.
                                                         nextInsuranceTime length] > 0 ? [KKUtils convertStringToDate:KKAppDelegateSingleton.regVehicleDetailInfo.nextInsuranceTime]:nil
                                        nextExamineTime:[KKAppDelegateSingleton.regVehicleDetailInfo.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:KKAppDelegateSingleton.regVehicleDetailInfo.nextExamineTime]:nil
                                         currentMileage:KKAppDelegateSingleton.regVehicleDetailInfo.currentMileage
                                               delegate:self];
}

-(void) obdBindAPI
{
    [[KKProtocolEngine sharedPtlEngine] obdBinding:[KKProtocolEngine sharedPtlEngine].userName
                                             obdSN:KKAppDelegateSingleton.regVehicleDetailInfo.obdSN
                                        vehicleVin:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin
                                         vehicleId:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleId
                                         vehicleNo:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo
                                      vehicleModel:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModel
                                    vehicleModelId:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleModelId
                                      vehicleBrand:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrand
                                    vehicleBrandId:KKAppDelegateSingleton.regVehicleDetailInfo.vehicleBrandId
                                        sellShopId:KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId
                                          engineNo:nil
                                          registNo:nil                               nextMaintainMileage:KKAppDelegateSingleton.regVehicleDetailInfo.nextMaintainMileage
                                 nextInsuranceTime:[KKAppDelegateSingleton.regVehicleDetailInfo.nextInsuranceTime length] > 0 ? [KKUtils convertStringToDate:KKAppDelegateSingleton.regVehicleDetailInfo.nextInsuranceTime]:nil
                                   nextExamineTime:[KKAppDelegateSingleton.regVehicleDetailInfo.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:KKAppDelegateSingleton.regVehicleDetailInfo.nextExamineTime]:nil
                                    currentMileage:KKAppDelegateSingleton.regVehicleDetailInfo.currentMileage
                                          delegate:self];
}

-(void) shopBindAPI
{
    [[KKProtocolEngine sharedPtlEngine] registerShopBind:KKAppDelegateSingleton.regVehicleDetailInfo.recommendShopId vehicleId:nil delegate:self];
}

-(BOOL) textFieldShouldEndEditing:(UITextField *)textField
{
    _vehicleNoText.textField.text = [_vehicleNoText.textField.text uppercaseString];
    return YES;
}

#pragma mark -
#pragma mark KKSearchCarModelDelegate
- (void)KKSearchCarModelViewDidSelected:(id)obj isBrand:(BOOL)isbrand
{
    KKModelCarInfo *carInfo = (KKModelCarInfo *)obj;
    if (isbrand)
    {
        _vehicleBrandText.textField.text = carInfo.brandName;
        _vehicleBrandText.addtionalInfo = carInfo.brandId;
        
        _vehicleModelText.textField.text = nil;
        _vehicleModelText.addtionalInfo = nil;
        
        _carBrandMarkView.hidden = YES;
    }
    else
    {
        _vehicleModelText.textField.text = carInfo.modelName;
        _vehicleModelText.addtionalInfo = carInfo.modelId;
        
        _carModelMarkView.hidden = YES;
        
        self.carInfo = carInfo;
    }
}
-(void) KKSearchCarModelViewDidSelected:(KKModelCarInfo *)aCarInfo
{
    self.carInfo = aCarInfo;
    
    _vehicleModelText.textField.text = [NSString stringWithFormat:@"%@ / %@", aCarInfo.brandName,aCarInfo.modelName];
    _carModelMarkView.hidden = YES;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
-(NSNumber *) obdBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    return [self requestResult:aRspObj];
}

-(NSNumber *) vehicleSaveInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    return [self requestResult:aRspObj];
}

-(NSNumber *) registerShopBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    return [self requestResult:aRspObj];
}

-(NSNumber *) requestResult:(id) aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
            
        }];
		return KKNumberResultEnd;
	}
    
    if(KKAppDelegateSingleton.regVehicleDetailInfo.vehicleNo)
    {
        if([rsp class] == [KKModelSaveVehicleInfoRsp class])
        {
            KKAppDelegateSingleton.regVehicleDetailInfo = ((KKModelSaveVehicleInfoRsp *)rsp).vehicleInfo;
        }
        NSLog(@"%@",KKAppDelegateSingleton.regVehicleDetailInfo.vehicleId);
        KKAppDelegateSingleton.currentVehicle = KKAppDelegateSingleton.regVehicleDetailInfo;
        [KKAppDelegateSingleton.vehicleList addObject:KKAppDelegateSingleton.currentVehicle];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"updateVehicleConditionNotification" object:nil];
    }
    
    [KKCustomAlertView showAlertViewWithMessage:((KKModelProtocolRsp *)rsp).header.desc block:^{
        switch (self.regEntrance) {
            case RegEntrance_RegisterView:
                [KKAppDelegateSingleton ShowRootView];
                break;
            case RegEntrance_ShopSearchView:
                [self.navigationController popToRootViewControllerAnimated:YES];
                break;
            default:
                break;
        }
    }];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"updateVehicleList" object:nil];
    
    return KKNumberResultEnd;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
-(void) dealloc
{
    [super dealloc];
}

@end
