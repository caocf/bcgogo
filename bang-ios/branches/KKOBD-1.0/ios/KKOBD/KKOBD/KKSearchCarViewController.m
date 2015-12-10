//
//  KKSearchCarViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKSearchCarViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKSearchCarTableViewCell.h"
#import "KKWaittingView.h"
#import "KKCustomAlertView.h"
#import "KKAppDelegate.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "KKUtils.h"
#import "KKRegisterAccount3ViewController.h"
#import "KKScanViewController.h"
#import "KKObdAndCarListViewController.h"
#import "KKShowOrAddNewBindCarViewController.h"

@interface KKSearchCarViewController ()
//@property(nonatomic,copy) KKModelVehicleDetailInfo *regVehicleDetailInfo;    //注册时储存车辆的详细信息
@property (nonatomic, copy) NSString *vehicleVin;
@end

@implementation KKSearchCarViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    
    if (self.isFromRegister)
    {
        [KKAppDelegateSingleton loadBLEEngine];
        //[[KKProtocolEngine sharedPtlEngine] vehicleListInfo:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [KKAppDelegateSingleton stopAutoConnectTimer];
    
    if (KKAppDelegateSingleton.bleEngine && [KKAppDelegateSingleton.bleEngine supportBLE])
    {
        [KKAppDelegateSingleton getVehicleRealData:NO];
        [KKAppDelegateSingleton.bleEngine disConnectActivePeripheral];
    }
    
    [self refreshButtonClicked];
    
    KKAppDelegateSingleton.bleEngine.bleDelegate = self;
    KKAppDelegateSingleton.bleEngine.bleEngineDelegate = self;
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    if (KKAppDelegateSingleton.bleEngine &&[KKAppDelegateSingleton.bleEngine supportBLE])
    {
        [KKAppDelegateSingleton.bleEngine stopScan];
        [KKAppDelegateSingleton.bleEngine disConnectActivePeripheral];
    }
    
    if (_isAnimating)
    {
        _isAnimating = NO;
        [MBProgressHUD hideAllHUDsForView:self.view
                                 animated:YES];
    }
    
    [self stopGetVinTimer];
    
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    KKAppDelegateSingleton.bleEngine.bleDelegate = KKAppDelegateSingleton;
    KKAppDelegateSingleton.bleEngine.bleEngineDelegate = KKAppDelegateSingleton;
    [KKAppDelegateSingleton scanPeripheral];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    _dataArray = [[NSMutableArray alloc] init];
    _isFirstGetVin = YES;
}

- (void) initComponents
{
    [self setBachGroundView];
    [self setNavgationBar];
    
    float var = self.isFromRegister ? 0 : 49;
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(24, 8, 272, currentScreenHeight - 44 - [self getOrignY] - 43 - var)];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [[UIImage imageNamed:@"bg_sBt_tableview.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    UIView *headView = [self creatTableViewHeadView];
    [headView setFrame:CGRectMake(24, 8, 272, 53)];
    [self.view addSubview:headView];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(24, 8 + 53, 272, currentScreenHeight - 44 - [self getOrignY] - 43 - 53 - var) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;   
    [self.view addSubview:_mainTableView];
    [_mainTableView release];
    
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"搜索车辆";
    
    UIImage *image = [UIImage imageNamed:@"icon_sBt_skip.png"];
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, image.size.width, image.size.height)];
    [button setTitle:@"跳过" forState:UIControlStateNormal];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    [button.titleLabel setFont:[UIFont systemFontOfSize:15.0f]];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(skipButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithCustomView:button] autorelease];
    [button release];
    
    [self.navigationItem setHidesBackButton:YES];
    //self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)startSearchCar
{
    if (KKAppDelegateSingleton.currentConnectedPeripheral != nil)
        [KKAppDelegateSingleton.bleEngine disConnectActivePeripheral];
    else
    {
        [MBProgressHUD hideAllHUDsForView:self.view
                                 animated:YES];
        
//        MBProgressHUD *hub = [[MBProgressHUD alloc] initWithView:self.view];
//        hub.labelText = @"正在搜索车辆，请耐心等待...";
//        hub.minSize = CGSizeMake(245, 145);
//        [self.view addSubview:hub];
//        [hub show:YES];
//        [hub release];
        
        [_dataArray removeAllObjects];
        [_mainTableView reloadData];
        [self startAnimating];
        [KKAppDelegateSingleton.bleEngine scanPeripherals:6];
    }
}

- (UIView *)creatTableViewHeadView
{
    UIView  *headView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 272, 53)];
    headView.backgroundColor = [UIColor clearColor];
    
    UIImage *image = [UIImage imageNamed:@"bg_sBt_headView.png"];
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 272, image.size.height)];
    bgImv.userInteractionEnabled = YES;
    bgImv.image = image;
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(20, 16.5, 100, 15)];
    label.font = [UIFont systemFontOfSize:15.0f];
    label.backgroundColor = [UIColor clearColor];
    label.textColor = [UIColor whiteColor];
    label.textAlignment = UITextAlignmentLeft;
    label.text = @"设备蓝牙";
    [bgImv addSubview:label];
    [label release];
    
    image = [UIImage imageNamed:@"icon_sBt_refresh.png"];
    _refreshBtn = [[UIButton alloc] initWithFrame:CGRectMake(225, 0.5*(53 - 44)-5, 44, 44)];
    [_refreshBtn setImage:image forState:UIControlStateNormal];
    [_refreshBtn addTarget:self action:@selector(refreshButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [bgImv addSubview:_refreshBtn];
    [_refreshBtn release];
    
    [headView addSubview:bgImv];
    [bgImv release];
    
    return [headView autorelease];
}

- (void)spin
{
    CABasicAnimation *spinAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    spinAnimation.byValue = [NSNumber numberWithFloat:2*M_PI];
    spinAnimation.duration = 1.0f;
    spinAnimation.delegate = self;
    [_refreshBtn.layer addAnimation:spinAnimation forKey:@"spinAnimation"];
}


- (void)startAnimating
{
    _isAnimating = YES;
    _refreshBtn.userInteractionEnabled = NO;
    [self spin];
}

- (void)stopAnimation
{
    _isAnimating = NO;
    _refreshBtn.userInteractionEnabled = YES;
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag
{
    if (flag && _isAnimating)
    {
        [self spin];
    }
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)skipButtonClicked
{
    [self skipButtonClickedWithObdRead:NO];
}

-(void) skipButtonClickedWithObdRead:(BOOL) isRead
{
    if(self.skipToBack)
    {
        [self.navigationController popViewControllerAnimated:YES];
        return;
    }
    
    if (self.isFromRegister)
    {
        KKScanViewController *scanVc = [[KKScanViewController alloc] init];
        scanVc.isFromRegister = YES;
        scanVc.showsZBarControls = NO;
        [self.navigationController pushViewController:scanVc animated:YES];
        [scanVc release];
    }
    else
    {
        switch (self.nextVc) {
            case NextVc_ObdAndCarListVc:
            {
                if([KKAppDelegateSingleton.vehicleList count]>0)
                {
                    KKObdAndCarListViewController *Vc = [[KKObdAndCarListViewController alloc] init];
                    Vc.obdSN = KKAppDelegateSingleton.currentConnectedPeripheral.systemId;
                    Vc.vehicleVin = self.vehicleVin;
                    [self.navigationController pushViewController:Vc animated:YES];
                    [Vc release];
                }
                else
                {
                    KKShowOrAddNewBindCarViewController *Vc = [[KKShowOrAddNewBindCarViewController alloc] initWithNibName:@"KKShowOrAddNewBindCarViewController" bundle:nil];
                    Vc.type = KKBindCar_addNew;
                    Vc.obdSN = KKAppDelegateSingleton.currentConnectedPeripheral.systemId;
                    Vc.vehicleVin = self.vehicleVin;
                    Vc.popViewControllerNum = 1;
                    [self.navigationController pushViewController:Vc animated:YES];
                    [Vc release];
                }
            }
                break;
                
            default:
            {
                [self.navigationController popViewControllerAnimated:YES];
            }
                break;
        }
        
    }
}

- (void)refreshButtonClicked
{
    if ([KKAppDelegateSingleton.bleEngine supportBLE])
    {
        if (_isAnimating)
            return;
        [self startSearchCar];
    }
    else
    {
         [KKCustomAlertView showAlertViewWithMessage:@"您的手机蓝牙没打开或者你的手机不支持蓝牙4.0!!!"];
    }
}

- (void)getVehicleVin
{
    [self stopGetVinTimer];
    
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    _getVinHud = [[MBProgressHUD alloc] initWithView:self.view];
    _getVinHud.labelText = @"正在读取车辆信息";
    [self.view addSubview:_getVinHud];
    [_getVinHud release];
    [_getVinHud show:YES];
    
    [self reGetVehicleVin];
    
    if(self.getVinTimer)
    {
        [self.getVinTimer invalidate];
        self.getVinTimer = nil;
    }
    
    self.getVinTimer = [NSTimer scheduledTimerWithTimeInterval:10
                                                    target:self
                                                  selector:@selector(showGetViewInfoFailed) userInfo:nil repeats:NO];
    
    if (_isFirstGetVin)
    {
        self.reGetVinTimer = [NSTimer scheduledTimerWithTimeInterval:5
                                                              target:self
                                                            selector:@selector(reGetVehicleVin) userInfo:nil repeats:NO];
        _isFirstGetVin = NO;
    }
}

- (void)reGetVehicleVin
{
    [KKAppDelegateSingleton.bleEngine  getVehicleVin];
}

- (void)stopGetVinTimer
{
    [self.getVinTimer invalidate];
    self.getVinTimer = nil;
    
    [self.reGetVinTimer invalidate];
    self.reGetVinTimer = nil;
    
    [self stopServiceTimer];
}

- (void)stopServiceTimer
{
    [self.getServiceTimer invalidate];
    self.getServiceTimer = nil;
}

- (void)showGetServiceError
{
    [KKCustomAlertView showAlertViewWithMessage:@"获取设备服务失败，请重试！"];
}

- (void)showGetViewInfoFailed
{
    if (_getVinHud)
    {
        [_getVinHud hide:YES];
        _getVinHud = nil;
    }
    [KKCustomAlertView showAlertViewWithMessage:@"获取车辆信息失败!"];
}
#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_searchCar";
    KKSearchCarTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[KKSearchCarTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    CBPeripheral *periperal = [_dataArray objectAtIndex:indexPath.row];

    [cell setDeviceLinked:periperal.isConnected];
    [cell setDeviceName:[periperal.name length] > 0 ? periperal.name : @"未知设备"];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 44;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _isReadBackByVin = YES;
    [self stopServiceTimer];
    
    //[KKAppDelegateSingleton.bleEngine stopScan];
    
    CBPeripheral *periperal = [_dataArray objectAtIndex:indexPath.row];
    if (periperal.isConnected)
    {
        [self getVehicleVin];
    }
    else
    {
        self.getServiceTimer = [NSTimer scheduledTimerWithTimeInterval:7.0
                                                                target:self
                                                              selector:@selector(showGetServiceError)
                                                              userInfo:nil repeats:NO];
        
        KKSearchCarTableViewCell *cell = (KKSearchCarTableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
        [cell startAnimating];
        _currentIndex = indexPath.row;
        [KKAppDelegateSingleton.bleEngine connectPeripheral:periperal];
    }
}

#pragma mark -
#pragma mark KKBLECoreDelegate

- (void)didDiscoverPeripheral:(CBPeripheral *)aPeripheral RSSI:(NSNumber *)RSSI
{
    [_dataArray addObject:aPeripheral];
    [_mainTableView reloadData];
}

- (void)mobileSupportBLE:(BOOL)aSupported
{
    if (aSupported)
    {
        [self startSearchCar];
    }
    else
        [KKCustomAlertView showAlertViewWithMessage:@"您的手机蓝牙没打开或者你的手机不支持蓝牙4.0!!!"];
}

- (void)didScanFinishWithResult:(BOOL)success
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    [self stopAnimation];
    
    _isFirstGetVin = YES;
    
    if (!success)
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"未搜索到车辆，是否重试？" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"重试" imageName:@"alert-orange-button.png" block:^{
            [self startSearchCar];
        }];
        [alertView addButtonWithTitle:@"跳过" imageName:@"alert-blue2-button.png" block:^{
            [self skipButtonClicked];
        }];
        [alertView show];
        [alertView release];
    }
}

- (void)didScanFinishWithPeripherals:(NSMutableArray *)perArr
{
    [_dataArray removeAllObjects];
    [_dataArray addObjectsFromArray:perArr];
    
    [_mainTableView reloadData];
}

- (void)didConnectPeripheral:(CBPeripheral *)aPeripheral state:(KKBLE_STATUS)aState error:(NSError *)aError
{
    [self stopGetVinTimer];
    
    switch (aState) {
        case KKBLE_STATUS_DISCONNECTED:
        {
            [KKAppDelegateSingleton stopAutoConnectTimer];
            KKAppDelegateSingleton.currentConnectedPeripheral = nil;
            [KKAppDelegateSingleton updateVehicleCondition:e_CarNotOnLine];
            [self startSearchCar];
            
            break;
        }
        case KKBLE_STATUS_FAIL_TO_CONNECT:
        {
            if (_currentIndex < [_dataArray count])
            {
                KKSearchCarTableViewCell *cell = (KKSearchCarTableViewCell *)[_mainTableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:_currentIndex]];
                [cell stopAnimation];
            }
            break;
        }
        case KKBLE_STATUS_CONNECTED:
        {
            _CBWriteReady = NO;                 // will wait for discover service of write BT
            KKAppDelegateSingleton.currentConnectedPeripheral = aPeripheral;
            [KKAppDelegateSingleton updateVehicleCondition:e_CarWell];
            
            if (_currentIndex < [_dataArray count])
            {
                KKSearchCarTableViewCell *cell = (KKSearchCarTableViewCell *)[_mainTableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:_currentIndex]];
                [cell stopAnimation];
            }
            break;
        }
        default:
            break;
    }
    [_mainTableView reloadData];
    
}

- (void)didDiscoverCharacteristicsForReadWrite:(NSInteger)aReadOrWrite error:(NSError *)error
{
    if (aReadOrWrite == 1)
        _CBWriteReady = YES;
    CBPeripheral *peripheral = KKAppDelegateSingleton.currentConnectedPeripheral;
    if ([peripheral.systemId length] > 0) {
        [self getVehicleVin];
    }
}

- (void)didGetSystemId:(NSString *)aSystemId forPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error
{
    if (error != nil)
        return;
        
    if (_CBWriteReady == YES)
        [self getVehicleVin];
        
}

// @aRetObj: NSString
- (void)vehicleVinResp:(id)aRetObj
{
    if (!_isReadBackByVin)
        return;
    
    [self stopGetVinTimer];
    
    if (_getVinHud)
    {
        [_getVinHud hide:YES];
        _getVinHud = nil;
    }
    
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
    NSString *obdSN = KKAppDelegateSingleton.currentConnectedPeripheral.systemId;
    self.vehicleVin = (NSString *)aRetObj;
    
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKSearchCarViewControllerTransferInfoByOperation:)])
    {
        [self.delegate KKSearchCarViewControllerTransferInfoByOperation:(NSString *)aRetObj];
    }
    
    self.skipToBack = NO;
    
    if (self.isFromRegister)
    {
        if ([KKAppDelegateSingleton.currentVehicle.currentMileage length] > 0)
        {
            KKModelPreferenceGlobalValue *globalValue  = [KKPreference sharedPreference].globalValues;
            globalValue.currentVehicleMile = KKAppDelegateSingleton.currentVehicle.currentMileage;
            [KKPreference sharedPreference].globalValues = globalValue;
        }
        
        
        KKAppDelegateSingleton.regVehicleDetailInfo.vehicleVin = (NSString *)aRetObj;
        KKAppDelegateSingleton.regVehicleDetailInfo.obdSN = obdSN;
        
        [self skipButtonClickedWithObdRead:YES];
        
        
//        [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
//        
//            
//        [[KKProtocolEngine sharedPtlEngine] obdBinding:[KKProtocolEngine sharedPtlEngine].userName
//                                                 obdSN:obdSN
//                                            vehicleVin:(NSString *)aRetObj
//                                             vehicleId:KKAppDelegateSingleton.currentVehicle.vehicleId
//                                             vehicleNo:KKAppDelegateSingleton.currentVehicle.vehicleNo
//                                          vehicleModel:KKAppDelegateSingleton.currentVehicle.vehicleModel
//                                        vehicleModelId:KKAppDelegateSingleton.currentVehicle.vehicleModelId
//                                          vehicleBrand:KKAppDelegateSingleton.currentVehicle.vehicleBrand
//                                        vehicleBrandId:KKAppDelegateSingleton.currentVehicle.vehicleBrandId
//                                            sellShopId:KKAppDelegateSingleton.currentVehicle.recommendShopId
//                                   nextMaintainMileage:KKAppDelegateSingleton.currentVehicle.nextMaintainMileage
//                                     nextInsuranceTime:[KKAppDelegateSingleton.currentVehicle.nextInsuranceTime length] > 0 ? [KKUtils convertStringToDate:KKAppDelegateSingleton.currentVehicle.nextInsuranceTime]:nil
//                                       nextExamineTime:[KKAppDelegateSingleton.currentVehicle.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:KKAppDelegateSingleton.currentVehicle.nextExamineTime]:nil
//                                        currentMileage:KKAppDelegateSingleton.currentVehicle.currentMileage
//                                              delegate:self];
//        
//        MBProgressHUD *hud = [[MBProgressHUD alloc] init];
//        hud.labelText = @"正在绑定车辆...";
//        [hud show:YES];
//        [self.view addSubview:hud];
//        [hud release];
        
        
        /*
         [self.vehicleDetailInfo.nextInsuranceTime length] > 0 ? [KKUtils ConvertDataToString:[KKUtils convertStringToDate:self.vehicleDetailInfo.nextInsuranceTime]] : nil;
         */
    }
    else
        [self skipButtonClicked];
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)obdBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
//            [KKAppDelegateSingleton reLogin];
            [self skipButtonClicked];
        }];
		return KKNumberResultEnd;
	}
    
    [KKAppDelegateSingleton setDefaultObdSN:KKAppDelegateSingleton.currentConnectedPeripheral.systemId];
    KKAppDelegateSingleton.connectStatus = e_CarNotOnLine;
    
    if (KKAppDelegateSingleton.bleEngine &&[KKAppDelegateSingleton.bleEngine supportBLE])
        [KKAppDelegateSingleton.bleEngine disConnectActivePeripheral];
    
//    [KKAppDelegateSingleton reLogin];
    [self skipButtonClicked];
    
    return KKNumberResultEnd;
}

- (NSNumber *)vehicleListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"vehicle list error is %@",error.description);
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
//            [KKAppDelegateSingleton reLogin];
            //[[KKProtocolEngine sharedPtlEngine] vehicleListInfo:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
            KKAppDelegateSingleton.regVehicleDetailInfo = [[[KKModelVehicleDetailInfo alloc] init] autorelease];
        }];
		return KKNumberResultEnd;
	}
    KKVehicleListRsp *listRsp = (KKVehicleListRsp *)rsp;
    if ([listRsp.KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo) count] > 0)
    {
        //[KKAppDelegateSingleton.vehicleList addObjectsFromArray:listRsp.KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo)];
        KKAppDelegateSingleton.vehicleList = listRsp.vehicleList__KKModelVehicleDetailInfo;
        [KKAppDelegateSingleton setCurrentVehicleInfo:[listRsp.KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo) objectAtIndex:0]];
        
        KKAppDelegateSingleton.regVehicleDetailInfo = KKAppDelegateSingleton.currentVehicle;
        KKAppDelegateSingleton.regVehicleDetailInfo.obdSN = nil;
        KKAppDelegateSingleton.regVehicleDetailInfo.vehicleId = nil;
    }
    else
    {
        KKAppDelegateSingleton.regVehicleDetailInfo = [[[KKModelVehicleDetailInfo alloc] init] autorelease];
    }
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
    _mainTableView = nil;
    _refreshBtn = nil;
}

- (void)dealloc
{
    _mainTableView = nil;
    [_dataArray release];
    _dataArray = nil;
    _refreshBtn = nil;
    [self.getVinTimer invalidate];
    self.getVinTimer = nil;
    
    [self.reGetVinTimer invalidate];
    self.reGetVinTimer = nil;
    
    [self.getServiceTimer invalidate];
    self.getServiceTimer = nil;
    
    [super dealloc];
}
@end
