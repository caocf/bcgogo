//
//  KKViolateViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKViolateViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKProtocolEngine.h"
#import "KKGlobal.h"
#import "KKError.h"
#import "KKCustomAlertView.h"
#import "KKModelComplex.h"
#import "MBProgressHUD.h"
#import "KKCustomTextField.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKViolateTableViewCell.h"
#import "KKProtocolParser.h"
#import "KKViolateAdditionalViewController.h"
#import "KKBindCarViewController.h"
#import "KKShowOrAddNewBindCarViewController.h"
#import "KKHelper.h"


#define     KKPopViewTag			10001
#define     PopId_VehicleNoType     1001
#define     PopId_VehicleNo         1002
#define     PopId_City              1003

@interface KKViolateViewController ()
@property(nonatomic, retain) NSMutableArray *areaList;
@property(nonatomic, retain) NSMutableArray *currentProvinces;
@property(nonatomic, retain) NSMutableArray *currentCities;
@property (nonatomic, retain) KKViolateVehicleType *currentVehicleType;
@property(nonatomic, retain) KKModelVehicleDetailInfo *currentVehicle;
@property (nonatomic, retain) KKViolateVehicleTypeRsp *vehicleTypeRsp;
@end

@implementation KKViolateViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void) viewWillAppear:(BOOL)animated
{
    if(_vehicleNoText)
    {
        BOOL exist = NO;
        for(KKModelVehicleDetailInfo *info in KKAppDelegateSingleton.vehicleList)
        {
            if([_vehicleNoText.textField.text isEqualToString:info.vehicleNo])
            {
                exist = YES;
                self.currentVehicle = info;
            }
        }
        if(exist)
        {
            _vehicleNoText.textField.text = self.currentVehicle.vehicleNo;
        }
        else
        {
            self.currentVehicle = KKAppDelegateSingleton.currentVehicle;
            _vehicleNoText.textField.text = self.currentVehicle.vehicleNo;
        }
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self setVcEdgesForExtendedLayout];
    
    self.currentProvinces=[[NSMutableArray alloc] init];
    self.currentCities = [NSKeyedUnarchiver unarchiveObjectWithData:[[NSUserDefaults standardUserDefaults] objectForKey:[NSString stringWithFormat:@"%@_violateCitys",[KKProtocolEngine sharedPtlEngine].userName]]];
    if(self.currentCities == nil)
        self.currentCities = [[NSMutableArray alloc] init];
    self.currentVehicle = KKAppDelegateSingleton.currentVehicle;
    _violateResults = [[NSMutableArray alloc] init];
    self.vehicleTypeRsp = [[[[KKViolateVehicleTypeParser alloc] init] autorelease] parse:nil];
    if(self.vehicleTypeRsp && [self.vehicleTypeRsp.result__KKViolateVehicleType count]>2)
        self.currentVehicleType = [self.vehicleTypeRsp.result__KKViolateVehicleType objectAtIndex:1];
    
    [self initComponents];
    
    [self loadCityInfo];
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBackGroundView];
    
    float originY = 27;
    
    UIImage *topBgImg = [UIImage imageNamed:@"bg_violate_top.png"];
    UIImageView *topBgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320, 188)];
    topBgView.userInteractionEnabled = YES;
    topBgView.image = topBgImg;
    
    UILabel *veNoTypeLable = [[UILabel alloc] initWithFrame:CGRectMake(26, originY, 76, 26)];
    veNoTypeLable.text = @"汽车类型:";
    veNoTypeLable.backgroundColor = [UIColor clearColor];
    veNoTypeLable.font = [UIFont systemFontOfSize:14];
    veNoTypeLable.textAlignment = UITextAlignmentRight;
    [topBgView addSubview:veNoTypeLable];
    [veNoTypeLable release];
    
    _veNoTypeText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(110, originY - 2, 160, 32) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_shopq_downArrow.png"] WithRightInsetWidth:10];
    _veNoTypeText.index = 10;
    //_vehicleNoText.textField.delegate = self;
    _veNoTypeText.textField.text = self.currentVehicleType.car;
    
    UIButton *vehicleNoTypeBtn = [[UIButton alloc] initWithFrame:_veNoTypeText.bounds];
    [vehicleNoTypeBtn addTarget:self action:@selector(setPopviewAndShow:) forControlEvents:UIControlEventTouchUpInside];
    vehicleNoTypeBtn.backgroundColor = [UIColor clearColor];
    vehicleNoTypeBtn.tag = 1;
    [_veNoTypeText addSubview:vehicleNoTypeBtn];
    [vehicleNoTypeBtn release];

    [topBgView addSubview:_veNoTypeText];
    [_veNoTypeText release];
    
    originY += 35;
    
    UILabel *vehicleNoLable = [[UILabel alloc] initWithFrame:CGRectMake(26, originY, 76, 26)];
    vehicleNoLable.text = @"车牌号:";
    vehicleNoLable.font = [UIFont systemFontOfSize:14];
    vehicleNoLable.backgroundColor = [UIColor clearColor];
    vehicleNoLable.minimumFontSize = 14;
    vehicleNoLable.textAlignment = UITextAlignmentRight;
    [topBgView addSubview:vehicleNoLable];
    [vehicleNoLable release];
    
    _vehicleNoText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(110, originY - 2, 160, 32) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_shopq_downArrow.png"] WithRightInsetWidth:10];    _vehicleNoText.index = 10;
    //_vehicleNoText.textField.delegate = self;
    _vehicleNoText.textField.text = self.currentVehicle.vehicleNo;
    
    UIButton *vehicleNoBtn = [[UIButton alloc] initWithFrame:_vehicleNoText.bounds];
    [vehicleNoBtn addTarget:self action:@selector(setPopviewAndShow:) forControlEvents:UIControlEventTouchUpInside];
    vehicleNoBtn.backgroundColor = [UIColor clearColor];
    vehicleNoBtn.tag = 2;
    [_vehicleNoText addSubview:vehicleNoBtn];
    [vehicleNoBtn release];
    
    [topBgView addSubview:_vehicleNoText];
    [_vehicleNoText release];

    originY += 35;
    
    UILabel *cityLable = [[UILabel alloc] initWithFrame:CGRectMake(26, originY, 76, 26)];
    cityLable.text = @"城市:";
    cityLable.font = [UIFont systemFontOfSize:14];
    cityLable.backgroundColor = [UIColor clearColor];
    cityLable.minimumFontSize = 14;
    cityLable.textAlignment = UITextAlignmentRight;
    [topBgView addSubview:cityLable];
    [cityLable release];
    
    _cityText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(110, originY-2, 160, 32) WithType:eTextFieldImage WithPlaceholder:nil WithImage:[UIImage imageNamed:@"icon_shopq_downArrow.png"] WithRightInsetWidth:10];
    _cityText.index = 10;
    [self setCityText];

    UIButton *cityBtn = [[UIButton alloc] initWithFrame:_cityText.bounds];
    [cityBtn addTarget:self action:@selector(setPopviewAndShow:) forControlEvents:UIControlEventTouchUpInside];
    cityBtn.backgroundColor = [UIColor clearColor];
    cityBtn.tag = 3;
    [_cityText addSubview:cityBtn];
    [cityBtn release];
    
    [topBgView addSubview:_cityText];
    [_cityText release];
    
    originY += 33;
    
    //查询按钮
    UIImage *image = [UIImage imageNamed:@"bg_registerBtn.png"];
    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(24, originY, image.size.width-20, image.size.height-3)];
    [submitButton setBackgroundColor:[UIColor clearColor]];
    [submitButton setBackgroundImage:[UIImage imageNamed:@"bg_registerBtn.png"] forState:UIControlStateNormal];
    [submitButton setTitle:@"查询" forState:UIControlStateNormal];
    [submitButton addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [topBgView addSubview:submitButton];
    [submitButton release];
    
    [self.view addSubview:topBgView];
    [topBgView release];
    
    
    //查询结果
    originY = topBgView.frame.size.height + 2;
    
    UILabel *totalLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(26, originY, 76, 26)];
    totalLabel1.text = @"累计扣分:";
    totalLabel1.backgroundColor = [UIColor clearColor];
    vehicleNoLable.font = [UIFont systemFontOfSize:14];
    totalLabel1.textAlignment = UITextAlignmentRight;
    [topBgView addSubview:totalLabel1];
    [totalLabel1 release];
    
    UIImage *imgTotalBg = [UIImage imageNamed:@"icon_violate_total.png"];
    
    UIImageView *bgImgV1 = [[UIImageView alloc] initWithFrame:CGRectMake(108, originY-2, 40, 30)];
    bgImgV1.image = imgTotalBg;
    scoreTotalLabel = [[UILabel alloc] initWithFrame:bgImgV1.bounds];
    scoreTotalLabel.textAlignment = UITextAlignmentCenter;
    scoreTotalLabel.textColor = [UIColor whiteColor];
    scoreTotalLabel.backgroundColor = [UIColor clearColor];
    [bgImgV1 addSubview:scoreTotalLabel];
    [topBgView addSubview:bgImgV1];
    [scoreTotalLabel release];
    [bgImgV1 release];
    
    UILabel *totalLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(150, originY, 76, 26)];
    totalLabel2.text = @"累计罚款:";
    totalLabel2.backgroundColor = [UIColor clearColor];
    vehicleNoLable.font = [UIFont systemFontOfSize:14];
    totalLabel2.textAlignment = UITextAlignmentRight;
    [topBgView addSubview:totalLabel2];
    [totalLabel2 release];
    
    UIImageView *bgImgV2 = [[UIImageView alloc] initWithFrame:CGRectMake(230, originY-2, 58, 30)];
    bgImgV2.image = imgTotalBg;
    moneyTotalLabel = [[UILabel alloc] initWithFrame:bgImgV2.bounds];
    moneyTotalLabel.textAlignment = UITextAlignmentCenter;
    moneyTotalLabel.textColor = [UIColor whiteColor];
    moneyTotalLabel.backgroundColor = [UIColor clearColor];
    [bgImgV2 addSubview:moneyTotalLabel];
    [topBgView addSubview:bgImgV2];
    [moneyTotalLabel release];
    [bgImgV2 release];
    
    originY += 32;
    
    //查询结果列表
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(10, originY, 300, currentScreenHeight - 44 - 49 - [self getOrignY] - originY)];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.hidden = YES;
    [self.view addSubview:_tableView];
    [_tableView release];
    
    _noResultLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, originY+20, 300, 30)];
    _noResultLabel.textAlignment = UITextAlignmentCenter;
    _noResultLabel.backgroundColor = [UIColor clearColor];
    _noResultLabel.font = [UIFont systemFontOfSize:16];
    _noResultLabel.text = @"无违章记录";
    _noResultLabel.hidden = YES;
    [self.view addSubview:_noResultLabel];
    [_noResultLabel release];
}
- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"违章查询";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItemWithTitle:@"车辆管理" bgImage:[UIImage imageNamed:@"icon_fgpwBtn.png"] target:self action:@selector(gotoBindCarView)];
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

-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}
                                              
-(void)gotoBindCarView
{
    KKBindCarViewController *bindCarVc = [[KKBindCarViewController alloc] init];
    [self.navigationController pushViewController:bindCarVc animated:YES];
    [bindCarVc release];
}

-(void) submitButtonClicked
{
//    //违章查询Demo数据
//    [[KKProtocolEngine sharedPtlEngine] getViolateJuheQuery:@"粤S89U28" veNoType:@"02" city:@"SH" engineNo:@"123456" classNo:@"LFPH4ABC071A21524" registNo:nil delegate:self];
//    return;
    violateSearchCondition.needEngine = violateSearchCondition.needClassa = violateSearchCondition.needRegist = NO;
    
    if(![self.currentCities count] > 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请选择要查询的城市！" block:nil];
        return;
    }
    
    if(self.currentVehicle == nil)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请选择车辆！" block:nil];
        return;
    }
    
    for(KKViolateCityInfo *area in self.currentCities)
    {
        KKViolateSearchCondition *searchCondition = area.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition;
        if(searchCondition.engine)
            violateSearchCondition.needEngine = YES;
        if(searchCondition.regist)
            violateSearchCondition.needRegist = YES;
        if(searchCondition.classa)
            violateSearchCondition.needClassa = YES;
    }
    
    if((violateSearchCondition.needEngine && (self.currentVehicle.engineNo==nil || self.currentVehicle.engineNo.length < 1)) || (violateSearchCondition.needRegist && (self.currentVehicle.registNo==nil || self.currentVehicle.registNo.length < 1)) || (violateSearchCondition.needClassa && (self.currentVehicle.vehicleVin==nil || self.currentVehicle.vehicleVin.length < 1)))
    {
        [KKCustomAlertView showAlertViewWithMessage:@"信息不完整，请补充！" block:^{
            KKViolateAdditionalViewController *additionalVc = [[KKViolateAdditionalViewController alloc] init];
            additionalVc.violateSearchCondition = violateSearchCondition;
            additionalVc.vehicleDetailInfo = self.currentVehicle;
            [self.navigationController pushViewController:additionalVc animated:YES];
            [additionalVc release];
        }];
        
        return;
    }
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];

    [_violateResults removeAllObjects];
    for(KKViolateCityInfo *cInfo in self.currentCities)
    {
        NSString *engineNoStr = nil;
        NSString *classaNoStr = nil;
        NSString *registNoStr = nil;
        
        if(self.currentVehicle.engineNo && cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.engine)
        {
            if(cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.engineNo == 0 || self.currentVehicle.engineNo.length <= cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.engineNo)
            {
                engineNoStr = self.currentVehicle.engineNo;
            }
            else
            {
                engineNoStr = [self.currentVehicle.engineNo substringFromIndex:(self.currentVehicle.engineNo.length-cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.engineNo)];
            }
        }
        if(self.currentVehicle.vehicleVin && cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.classa)
        {
            if(cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.classNo == 0 || self.currentVehicle.vehicleVin.length <= cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.classNo)
            {
                classaNoStr = self.currentVehicle.vehicleVin;
            }
            else
            {
                classaNoStr = [self.currentVehicle.vehicleVin substringFromIndex:(self.currentVehicle.vehicleVin.length-cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.classNo)];
            }
        }
        if(self.currentVehicle.registNo && cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.regist)
        {
            if(cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.registNo == 0 || self.currentVehicle.registNo.length <= cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.registNo)
            {
                registNoStr = self.currentVehicle.registNo;
            }
            else
            {
                registNoStr = [self.currentVehicle.registNo substringFromIndex:(self.currentVehicle.registNo.length-cInfo.juheViolateRegulationCitySearchCondition__KKViolateSearchCondition.registNo)];
                
            }
        }
        
        [[KKProtocolEngine sharedPtlEngine] getViolateJuheQuery:self.currentVehicle.vehicleNo veNoType:self.currentVehicleType.id city:cInfo.juheCityCode engineNo:engineNoStr classNo:classaNoStr registNo:registNoStr delegate:self];
    }
}

-(void) loadCityInfo
{
    _loadingCity = YES;
    [[KKProtocolEngine sharedPtlEngine] getViolateJuheAreaList:self];
}

- (KKShopFilterPopView*) popMenuView
{
	for (UIView *subView in [self.view subviews]) {
		if (subView.tag == KKPopViewTag && [subView isKindOfClass:[KKShopFilterPopView class]])
			return (KKShopFilterPopView*)subView;
	}
	return nil;
}

-(void) setPopviewAndShow:(UIButton *)btn
{
    KKShopFilterPopView *popView = [self popMenuView];
    
    if (popView)
		[popView removeFromSuperview];
    
    int originY = btn.superview.frame.origin.y + btn.superview.frame.size.height*0.7;
    KKShopFilterPopView *FilterPopView = [[KKShopFilterPopView alloc] initWithFrame:CGRectMake(0, originY, 320, currentScreenHeight - originY- 44 - 49 - [self getOrignY]) WithArrowOrignX:236 WithRowHeight:33];
    FilterPopView.popViewDelegate = self;
    FilterPopView.tag = KKPopViewTag;
    [self.view addSubview:FilterPopView];
    [FilterPopView release];
    
    switch (btn.tag) {
        case 1:
        {
            //选择车辆类型
            FilterPopView.popId = PopId_VehicleNoType;
            
            NSMutableArray *parr = [NSMutableArray arrayWithCapacity:10];
            NSInteger pid = 0;
            
            for (KKViolateVehicleType *type in self.vehicleTypeRsp.result__KKViolateVehicleType) {
                
                KKPopMenuItem *item = [[KKPopMenuItem alloc] initWithId:pid parentId:-1 title:type.car others:nil];
                if([type.car isEqualToString:self.currentVehicleType.car])
                    FilterPopView.selectedInFirstList = pid;
                [parr addObject:item];
                [item release];
                pid++;
            }
            [FilterPopView setLeftDataArray:parr RightDataArray:nil];
        }
            break;
        case 2:
        {
            //选择车牌号
            FilterPopView.popId = PopId_VehicleNo;
            
            NSMutableArray *parr = [NSMutableArray arrayWithCapacity:10];
            NSInteger pid = 0;
            
            if([KKAppDelegateSingleton.vehicleList count]<1)
            {
                [FilterPopView removeFromSuperview];
                KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"没有车辆信息，是否添加车辆？" WithType:KKCustomAlertView_default];
                [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
                [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
                    KKShowOrAddNewBindCarViewController *Vc = [[KKShowOrAddNewBindCarViewController alloc] init];
                    Vc.type = KKBindCar_addNew;
                    [self.navigationController pushViewController:Vc animated:YES];
                    [Vc release];
                }];
                [alertView show];
                [alertView release];
                return;
            }
            
            for (KKModelVehicleDetailInfo *info in KKAppDelegateSingleton.vehicleList) {
                
                KKPopMenuItem *item = [[KKPopMenuItem alloc] initWithId:pid parentId:-1 title:info.vehicleNo others:nil];
                if([info.vehicleNo isEqualToString:self.currentVehicle.vehicleNo])
                    FilterPopView.selectedInFirstList = pid;
                [parr addObject:item];
                [item release];
                pid++;
            }
            [FilterPopView setLeftDataArray:parr RightDataArray:nil];
        }
            break;
        case 3:
        {
            //选择城市
            FilterPopView.popId = PopId_City;
            [self setCityDataAndShow];
        }
            break;
        default:
            break;
    }
}

-(void) setCityDataAndShow
{
    KKShopFilterPopView *popView = [self popMenuView];
    
    if (popView)
    {
        [popView removeFromSuperview];
    }
    
    if(self.areaList)
    {
        NSInteger pid = 0;
        
        NSMutableArray *leftArr = [[NSMutableArray alloc] init];
        NSMutableArray *rightArr = [[NSMutableArray alloc] init];
        NSMutableArray *selectedIds = [[NSMutableArray alloc] init];
        
        NSInteger topSelected = 0;			// first(top) menu selected index
        NSInteger secSelected = 0;
        
        for (int i = 0 ;i < [self.areaList count] ; i++)
        {
            KKViolateCityInfo *areaInfo = [self.areaList objectAtIndex:i];
            if ([self.currentProvinces containsObject:areaInfo.name])
                topSelected = i;
            
            KKPopMenuItem *leftItem = [[KKPopMenuItem alloc] initWithId:areaInfo.id parentId:-1 title:areaInfo.name others:areaInfo];
            
            pid = areaInfo.id;
            
            [leftArr addObject:leftItem];
            [leftItem release];
            
            NSArray *childArr = areaInfo.children__KKViolateCityInfo;
            if([childArr count]==0)
            {
                KKPopMenuItem *rightItem = [[KKPopMenuItem alloc] initWithId:areaInfo.id parentId:pid title:areaInfo.name others:areaInfo];
                [rightArr addObject:rightItem];
                [rightItem release];
            }
            else
            {
                for (int j = 0; j < [childArr count]; j ++) {
                    KKViolateCityInfo *childAreaInfo = [childArr objectAtIndex:j];
                    if ([self.currentCities containsObject:childAreaInfo])
                        secSelected = j;
                    
                    KKPopMenuItem *rightItem = [[KKPopMenuItem alloc] initWithId:childAreaInfo.id parentId:pid title:childAreaInfo.name others:childAreaInfo];
                    [rightArr addObject:rightItem];
                    [rightItem release];
                }
            }
            
        }
        
        
        KKShopFilterPopView *FilterPopView = [[KKShopFilterPopView alloc] initWithFrame:CGRectMake(0, 113, 320, currentScreenHeight - 113- 44 - 49 - [self getOrignY]) WithArrowOrignX:236 WithRowHeight:33];
        FilterPopView.popViewDelegate = self;
        FilterPopView.tag = KKPopViewTag;
        FilterPopView.popId = PopId_City;
        FilterPopView.isTwoStep = YES;
        FilterPopView.isInit = YES;
        
        for(KKViolateCityInfo *info in self.currentCities)
        {
            [selectedIds addObject:[NSString stringWithFormat:@"%d",info.id]];
        }
        FilterPopView.selectedItemsId = selectedIds;
        
        [FilterPopView setLeftDataArray:leftArr RightDataArray:rightArr];
        [self.view addSubview:FilterPopView];
        [FilterPopView release];
        
        [leftArr release];
        [rightArr release];
        [selectedIds release];
    }
    else
    {
        if(_loadingCity)
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        else
        {
            [self loadCityInfo];
        }
        _loadedCityShow = YES;
    }
}

-(void) refreshUI
{
    fenTotal = moneyTotal = 0;
    
    int count = 0;
    
    for (int i=0; i<[_violateResults count]; i++) {
        NSArray *array = ((KKViolateResultRsp *)[_violateResults objectAtIndex:i]).result.lists__KKViolateDetailInfo;
        
        for(KKViolateDetailInfo *info in array)
        {
            fenTotal += [info.fen intValue];
            moneyTotal += [info.money intValue];
            count++;
        }
    }
    
    scoreTotalLabel.text = [NSString stringWithFormat:@"%d",fenTotal];
    moneyTotalLabel.text = [NSString stringWithFormat:@"%d",moneyTotal];

    if(count>0)
    {
        _tableView.hidden = NO;
        _noResultLabel.hidden = YES;
        [_tableView reloadData];
    }
    else
    {
        _tableView.hidden = YES;
        _noResultLabel.hidden = NO;
    }
}

-(void) setCityText
{
    NSMutableString *cityStr = [[NSMutableString alloc] init];
    NSString *cStr = @"";
    for(KKViolateCityInfo *info in self.currentCities)
    {
        [cityStr appendString:[NSString stringWithFormat:@"%@,",info.name]];
    }
    if(cityStr.length > 0)
        cStr = [cityStr substringToIndex:cityStr.length-1];
    _cityText.textField.text = cStr;
    [cityStr release];
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
-(NSNumber *) localCacheGetSuccess:(NSNumber *)aReqId withObject:(id)cacheObj
{
    switch ([aReqId integerValue]) {
        case ePtlApi_violate_juheAreaList:
        {
            if([cacheObj isEqual:[NSNull null]])
            {
                _loadingCity = NO;
            }
        }
            break;
        case ePtlApi_violate_query:
        {
            if(!loadedCache)
            {
                loadedCache = YES;
            }
            else
            {
                return [NSNumber numberWithBool:NO];
            }
        }
        default:
            break;
    }
    return [NSNumber numberWithBool:YES];
}
-(NSNumber *)getViolateAreaListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
            _loadingCity = NO;
        }];
		return KKNumberResultEnd;
	}
    
    self.areaList = ((KKViolateAreaListRsp *)rsp).result__KKViolateCityInfo;
    
    if(_loadedCityShow)
    {
        [self setCityDataAndShow];
    }
    _loadingCity = NO;
    _loadedCityShow = NO;
    
    return KKNumberResultEnd;
}

-(NSNumber *) getViolateJuheQueryResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}

    KKViolateResultRsp *vRsp = (KKViolateResultRsp *) rsp;
    
    //违章列表排序
    [vRsp.result.lists__KKViolateDetailInfo sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        KKViolateDetailInfo *vdInfo1 = (KKViolateDetailInfo *)obj1;
        KKViolateDetailInfo *vdInfo2 = (KKViolateDetailInfo *)obj2;
        NSDate *date1 = [KKHelper dateFromString:vdInfo1.date];
        NSDate *date2 = [KKHelper dateFromString:vdInfo2.date];
        return [date2 compare:date1];
    }];
    
    if(![vRsp.resultcode isEqualToString:@"200"])
    {
        [KKCustomAlertView showErrorAlertViewWithMessage:vRsp.reason block:nil];
		return KKNumberResultEnd;
    }
    
    [_violateResults removeAllObjects];
    [_violateResults addObject:vRsp];
        //多城市违章查询
//    if([_violateResults count] == 0)
//    {
//        [_violateResults addObject:vRsp];
//    }
//    else
//    {
//        KKViolateResultRsp *buf = nil;
//        for(KKViolateResultRsp *r in _violateResults)
//        {
//            if([r.result.province isEqualToString:vRsp.result.province] && [r.result.city isEqualToString:vRsp.result.city])
//            {
//                buf = r;
//                break;
//            }
//            else
//            {
//                [_violateResults addObject:vRsp];
//            }
//        }
//        if(buf)
//        {
//            [_violateResults removeObject:buf];
//            [_violateResults addObject:vRsp];
//        }
//    }
    
    [self refreshUI];
    
    return KKNumberResultEnd;
}

#pragma mark -
#pragma mark KKShopFilterPopViewDelegate
- (void)KKShopFilterPopView:(KKShopFilterPopView *)popView WithItem:(KKPopMenuItem *)item AndParentItem:(KKPopMenuItem *)pItem
{
    switch (popView.popId) {
        case PopId_VehicleNoType:
        {
            self.currentVehicleType = [self.vehicleTypeRsp.result__KKViolateVehicleType objectAtIndex:item.itemId];
            _veNoTypeText.textField.text = self.currentVehicleType.car;
        }
            break;
        case PopId_VehicleNo:
        {
            self.currentVehicle = [KKAppDelegateSingleton.vehicleList objectAtIndex:item.itemId];
            _vehicleNoText.textField.text = self.currentVehicle.vehicleNo;
        }
            break;
        case PopId_City:
        {
            //单城市
            KKViolateCityInfo *buf = (KKViolateCityInfo *)item.additional;
            if(buf != nil)
            {
                [self.currentCities removeAllObjects];
                [self.currentCities addObject:buf];
            }
            
//            //多城市
//            KKViolateCityInfo *buf = nil;
//            for(KKViolateCityInfo *info in self.currentCities)
//            {
//                if(info.id == ((KKViolateCityInfo *)item.additional).id)
//                {
//                    buf = info;
//                    break;
//                }
//            }
//            if(buf!=nil)
//            {
//                [self.currentCities removeObject:buf];
//            }
//            else
//            {
//                [self.currentCities addObject:(KKViolateCityInfo *)item.additional];
//            }
            
            NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
            [userDefault setObject:[NSKeyedArchiver archivedDataWithRootObject:self.currentCities] forKey:[NSString stringWithFormat:@"%@_violateCitys",[KKProtocolEngine sharedPtlEngine].userName]];
            [userDefault synchronize];
            
            [self setCityText];
        }
            break;
        default:
            break;
    }
    
}

#pragma mark -
#pragma mark UITableViewDelegate
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    int n = 0;
    for(KKViolateResultRsp *rsp in _violateResults)
    {
        n += [rsp.result.lists__KKViolateDetailInfo count];
    }
    return n;
}

-(float) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 140;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"violateCell";
    KKViolateTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[KKViolateTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    
    KKViolateDetailInfo *info = nil;
    int count = -1;
    for (int i=0; i<[_violateResults count]; i++) {
        int n=[((KKViolateResultRsp *)[_violateResults objectAtIndex:i]).result.lists__KKViolateDetailInfo count];
        if(indexPath.row <= count+n)
        {
            info = (KKViolateDetailInfo *)[((KKViolateResultRsp *)[_violateResults objectAtIndex:i]).result.lists__KKViolateDetailInfo objectAtIndex:(indexPath.row-count-1)];
            break;
        }
        else
        {
            count += n;
        }
    }
    
    [cell setUIFit:info];
    return cell;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    [_violateResults release];
    self.currentVehicle = nil;
    [self.currentProvinces release];
    [self.currentCities release];
    [super dealloc];
}

@end
