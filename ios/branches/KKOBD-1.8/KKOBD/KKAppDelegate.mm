//
//  KKAppDelegate.m
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKAppDelegate.h"
#import "KKRootViewController.h"
#import "KKLoginViewController.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "KKApplicationDefine.h"
#import "KKMessagePollingManager.h"
#import "KKTBDictFault.h"
#import "KKDB.h"
#import "KKTBDTCMessage.h"
#import "KKPreference.h"
#import "Wgs84ToGcj02.h"
#import "MobClick.h"
#import "KKTBVehicle.h"
#import "KKDriveRecordEngine.h"
#import "BGTaskManager.h"
#import "ASIHTTPRequest.h"

//----------------------------------------KKCity-----------------------------------------

@interface KKAppDelegate()
@property (nonatomic, retain) NSDate            *obdConnectTime;       //obd连接成功的时间
@end

@implementation KKCity

- (void)dealloc
{
    self.provinceName = nil;
    self.cityName = nil;
    self.cityName = nil;
    self.cityCode = nil;
    self.replaceCityName = nil;
    [super dealloc];
}
@end

//----------------------------------------KKAppDelegate-----------------------------------------

static BMKMapManager        *g_mapManager = nil;

@implementation KKAppDelegate

- (void)dealloc
{
    [g_mapManager release];
    g_mapManager = nil;
    
    [_dtcSoundPlay release];
    _dtcSoundPlay = nil;
    
    [_obdConnectSoundPlay release],_obdConnectSoundPlay = nil;
    [_obdDisConnectSoundPlay release],_obdDisConnectSoundPlay=nil;
    
    [_faultDict release];
    _faultDict = nil;
    
    self.window = nil;
    self.rootViewController = nil;
    self.loginNavController = nil;
    self.currentCity = nil;
    self.search = nil;
    self.waittingVc = nil;
    
    [super dealloc];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{    
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    self.window.backgroundColor = [UIColor whiteColor];
    // Override point for customization after application launch
    
    //开启网络变更通知
    [ASIHTTPRequest registerForNetworkReachabilityNotifications];
    
    //配置,是否是正式环境
    [KKProtocolEngine sharedPtlEngine].serviceEnvironment = YES;

    //-------------友盟统计
    //[MobClick startWithAppkey:@"52a0634e56240ba07d029efa"];
    //SEND_ON_EXIT-退出或进入后台时发送
    if([KKProtocolEngine sharedPtlEngine].serviceEnvironment)
    {
        self.versionStr = KK_Version;
    }
    else
    {
        self.versionStr = [NSString stringWithFormat:@"%@(%@)",KK_Version,KK_Build];
    }
    [MobClick startWithAppkey:@"52a0634e56240ba07d029efa" reportPolicy:SEND_INTERVAL   channelId:nil];
    [MobClick setAppVersion:self.versionStr];
    [MobClick setLogSendInterval:30];
    
    
//------------百度地图delegate定义
    
    g_mapManager = [[BMKMapManager alloc] init];
    
    BOOL ret = [g_mapManager start:@"27066f966039c833fdd7c9a46f3921e4"  generalDelegate:self];
    if (!ret) {
        NSLog(@"manager start failed!");
    }
    
//------------默认城市苏州
    
    KKCity *city = [[KKCity alloc] init];
//    
//    KKModelPreferenceCityInfo *cityInfo = [KKPreference sharedPreference].cityInfo;
//    if ([cityInfo.cityCode length] > 0 && [cityInfo.cityName length] > 0 && [cityInfo.cityCode length] > 0)
//    {
//        city.provinceName = cityInfo.provinceName;
//        city.cityName = cityInfo.cityName;
//        city.cityCode = cityInfo.cityCode;
//    }
//    else
//    {
//        city.provinceName = @"江苏省";
//        city.cityName = @"苏州";
//        city.cityCode = @"224";
//    }
    self.currentCity = city;
    [city release];

//    if ([cityInfo.latitude length] > 0 && [cityInfo.longitude length] > 0)
//        self.currentCoordinate2D = CLLocationCoordinate2DMake([cityInfo.latitude doubleValue], [cityInfo.longitude doubleValue]);
//    else
//        self.currentCoordinate2D = CLLocationCoordinate2DMake(31.26842725, 120.73116302);
    
    self.currentCoordinate2D = kCLLocationCoordinate2DInvalid;
    self.coordinateType = @"CURRENT";
    self.connectStatus = e_CarNotOnLine;
    _repeat = YES;
    
    [self startLocating];
    
    _dtcSoundPlay = [[KKMsgPlaySound alloc] initForPlayingSoundEffectWith:@"BEEP.WAV"];
    
    _obdConnectSoundPlay = [[KKMsgPlaySound alloc] initForPlayingSoundEffectWith:@"obd_lianjie.wav"];
    
    _obdDisConnectSoundPlay = [[KKMsgPlaySound alloc] initForPlayingSoundEffectWith:@"obd_duankai.wav"];

    self.obdList = [[[NSMutableArray alloc] init] autorelease];
    self.vehicleList = [[[NSMutableArray alloc] init] autorelease];
    self.peripheralArray = [[[NSMutableArray alloc] init] autorelease];
    self.dtcArray = [[[NSMutableArray alloc] init] autorelease];
    self.warnArray = [[[NSMutableArray alloc] init] autorelease];
    _faultDict = [[NSMutableDictionary alloc] init];
    
    
    if(KKAppDelegateSingleton.regVehicleDetailInfo == nil)
        KKAppDelegateSingleton.regVehicleDetailInfo = [[[KKModelVehicleDetailInfo alloc] init] autorelease];
    
    [KKProtocolEngine sharedPtlEngine].imageVersion = [NSString stringWithFormat:@"IV_%@",[KKHelper imageVersion]];
    
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    if ([userInfo.userNo length] > 0 && [userInfo.password length] > 0)
    {
        KKModelPreferenceGlobalValue *globalValue = [KKPreference sharedPreference].globalValues;
        if (globalValue.isNotAutoLogin)
        {
            [self registerAndLogin];
            [KKPreference sharedPreference].globalValues = nil;
        }
        else
        {
            UIViewController *Vc = [[UIViewController alloc] init];
            
            UIImageView *loginImv = [[UIImageView alloc] initWithFrame:Vc.view.bounds];
            UIImage *image = [UIImage imageNamed:@"Default.png"];
            if (currentScreenHeight > 500)
                image = [UIImage imageNamed:@"Default-568h@2x.png"];
            loginImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
            [Vc.view addSubview:loginImv];
            [loginImv release];
            self.waittingVc = Vc;
            [Vc release];
            
            self.window.rootViewController = self.waittingVc;
            
            [KKProtocolEngine sharedPtlEngine].userName = userInfo.userNo;
            [KKProtocolEngine sharedPtlEngine].password = userInfo.password;
            
            _loginRequestId = [[KKProtocolEngine sharedPtlEngine] userLoginWithUser:userInfo.userNo password:userInfo.password platform:CurrentSystemPlatform platformVersion:CurrentSystemVersion mobileModel:[KKHelper platformString] appVersion:KK_Version imageVersion:[KKHelper imageVersion] delegate:self];
        }
    }
    else
    {
        [self visitorLogin];
    }
    
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)registerAndLogin
{
    if (self.loginNavController)
        self.loginNavController = nil;
    if (self.waittingVc)
        self.waittingVc = nil;
    
    [KKProtocolEngine sharedPtlEngine].userName = nil;
    [KKProtocolEngine sharedPtlEngine].password = nil;
    
    KKLoginViewController *loginVc = [[KKLoginViewController alloc] initWithNibName:@"KKLoginViewController" bundle:nil];
    self.loginNavController = [[[UINavigationController alloc] initWithRootViewController:loginVc] autorelease];
    [loginVc release];
    
    self.window.rootViewController = self.loginNavController;
}

- (void)ShowRootView
{
//    self.rootViewController = nil;

    if(self.loginNavController)
    {
        [self.loginNavController popToRootViewControllerAnimated:NO];
        [self.loginNavController dismissModalViewControllerAnimated:YES];
        //self.loginNavController = nil;
    }
    
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Visitor:
        {
            if(!self.rootViewController)
            {
                self.rootViewController= [[[KKRootViewController alloc] init]autorelease];
                self.window.rootViewController = self.rootViewController;
            }
            [KKPreference sharedPreference].appConfig = nil;
            [KKMessagePollingManager stopPolling];
            
            [KKProtocolEngine sharedPtlEngine].userName = nil;
            [KKProtocolEngine sharedPtlEngine].password = nil;
        }
            break;
        case Authorization_Register:
        {
            self.rootViewController = nil;
            if(!self.rootViewController)
            {
                self.rootViewController= [[[KKRootViewController alloc] init]autorelease];
                self.window.rootViewController = self.rootViewController;
            }
            [KKMessagePollingManager stopPolling];
            [KKMessagePollingManager startPolling];
            
            [KKProtocolEngine sharedPtlEngine].userName = [KKPreference sharedPreference].userInfo.userNo;
            [KKProtocolEngine sharedPtlEngine].password = [KKPreference sharedPreference].userInfo.password;
            
            [self loadBLEEngine];
            
            if([KK_Version floatValue] < 1.7)
            {
                //1.7之前的版本检查数据库
                //[KKTBDictFaultVersion updateLocalDBWithVehicleModelId:<#(NSString *)#>]
            }
            
            _commonFaultDictRequestId = [[KKProtocolEngine sharedPtlEngine] vehicleFaultDict:nil dictVersion:[KKTBDictFault getNewestVersionForVehicle:nil] delegate:self];
            
            [self checkDTCWaring];
            
            [BGTaskManager startTask];
        }
            break;
    }
}

- (void)logOff
{
    if (self.bleEngine &&[self.bleEngine supportBLE])
    {
        [self getVehicleRealData:NO];
        [self.bleEngine disConnectActivePeripheral];
        [self.bleEngine stopScan];
        //self.bleEngine = nil;
    }
    
    [KKAuthorization sharedInstance].authorizationType = Authorization_Visitor;
    
    [BGTaskManager stopTask];
    
    self.rootViewController = nil;
    [self.vehicleList removeAllObjects];
    [self.obdList removeAllObjects];
    [self.warnArray removeAllObjects];
    [self.dtcArray removeAllObjects];
    [_faultDict removeAllObjects];
    
    [KKPreference sharedPreference].voiceSwitch = nil;
    
    self.regVehicleDetailInfo = [[[KKModelVehicleDetailInfo alloc] init] autorelease];
    
//    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
//    userInfo.mobile = nil;
//    userInfo.username = nil;
//    [KKPreference sharedPreference].userInfo = userInfo;
    
    [KKPreference sharedPreference].userInfo = nil;
    [KKProtocolEngine sharedPtlEngine].userName = nil;
    [KKProtocolEngine sharedPtlEngine].password = nil;
    
    KKModelPreferenceGlobalValue *globalValues = [[KKModelPreferenceGlobalValue alloc] init];
    globalValues.currentVehicleMile = @"-1";
    globalValues.isNotAutoLogin = YES;
    [KKPreference sharedPreference].globalValues = globalValues;
    [globalValues release];
    
    self.connectStatus = e_CarNotOnLine;
    self.unReadNum = 0;
    self.currentConnectedPeripheral = nil;
    self.currentVehicle = nil;
    self.vehicleRealtimeData = nil;
    self.firstVehicleRealtimeData = nil;
    [self stopAutoConnectTimer];
    self.isConnect = NO;
    
    [self registerAndLogin];
}

//-(void) setAppUserConfig:(KKModelAppUserConfig *)aAppUserConfig
//{
//    KKModelAppUserConfig *appuserConfig = [KKPreference sharedPreference].appUserConfig;
//    
//    if(!appuserConfig.oil_price || (appuserConfig.oil_price && [appuserConfig.oil_price isEqualToString:@"0"]))
//    {
//        appuserConfig.oil_kind = aOilKind;
//        appuserConfig.oil_price = aOilPrice;
//        [KKPreference sharedPreference].appUserConfig = appuserConfig;
//    }
//}
//
-(void) setDefaultOilPrice:(NSString *)aOilPrice oilKind:(NSString *)aOilKind
{
    KKModelAppUserConfig *appuserConfig = [KKPreference sharedPreference].appUserConfig;
    
    appuserConfig.oil_kind = aOilKind;
    appuserConfig.oil_price = aOilPrice;
    [KKPreference sharedPreference].appUserConfig = appuserConfig;
}

#pragma mark -
#pragma mark 游客

-(void) visitorLogin
{
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    
    //获取车辆列表
    KKTBVehicle *tbVehicle = [[KKTBVehicle alloc] initWithDB:[KKDB sharedDB]];
    [self detachVehicleListAndObdList:[tbVehicle getVehicleWithUserNo:userInfo.userNo]];
    [tbVehicle release];
    
    [self ShowRootView];
}

-(void) jumpToLoginVc
{
    if(!self.loginNavController)
    {
        KKLoginViewController *loginVc = [[KKLoginViewController alloc] initWithNibName:@"KKLoginViewController" bundle:nil];
        self.loginNavController = [[[UINavigationController alloc] initWithRootViewController:loginVc] autorelease];
        [loginVc release];
    }
    
    [self.rootViewController presentViewController:self.loginNavController animated:YES completion:nil];
}

////////////////////////////////////////////////////////////////

- (void)startLocating
{
    [AKLocationManager startLocatingWithUpdateBlock:^(CLLocation *location) {
        NSDictionary *dictionary = BMKBaiduCoorForWgs84(location.coordinate);
        CLLocationCoordinate2D coo = BMKCoorDictionaryDecode(dictionary);
        self.currentCoordinate2D = coo;
        self.currentCoordinate2D_Gcj02 = coo;
//        if (![WGS84TOGCJ02 isLocationOutOfChina:location.coordinate]) {
//            //转换后的coord
//            self.currentCoordinate2D_Gcj02 = [WGS84TOGCJ02 transformFromWGSToGCJ:[location coordinate]];
//        }
        [[KKDriveRecordEngine sharedInstance] recordDrivePoint:coo];
        
        self.coordinateType = @"CURRENT";
        _repeat = YES;
        if (!_geocodeCitySuccess)
            [self reverseGeocode];
    } failedBlock:^(NSError *error) {
        _repeat = NO;
        _geocodeCitySuccess = NO;
        self.coordinateType = @"LAST";
        if (error.code == AKLocationManagerErrorCannotLocate)
        {
            [AKLocationManager stopLocating];
        }
    }];
}

- (void)stopLocating
{
    [AKLocationManager stopLocating];
}

- (void)reverseGeocode
{
    if (_repeat)
    {
        if (self.search == nil)
        {
            BMKSearch *aSearch = [[BMKSearch alloc] init];
            aSearch.delegate = self;
            self.search = aSearch;
            [aSearch release];
        }
        
        if (_timer == nil)
            _timer = [NSTimer scheduledTimerWithTimeInterval:15.f target:self selector:@selector(reverseGeocode) userInfo:nil repeats:YES];
        
        [self.search reverseGeocode:self.currentCoordinate2D];
    }
    else
    {
        [_timer invalidate];
        _timer = nil;
    }

}

- (void) setNewMsgBadgeValue:(NSInteger)aUnreads
{
    self.unReadNum = aUnreads;
	KKCustomTabbarContentView *tabBar = [self.rootViewController tabbarContentView];
    if (tabBar)
        [tabBar setBadgeValue:aUnreads andIndex:1];
}

- (void)reLogin
{
   [[KKProtocolEngine sharedPtlEngine] userLoginWithUser:[KKProtocolEngine sharedPtlEngine].userName password:[KKProtocolEngine sharedPtlEngine].password platform:CurrentSystemPlatform platformVersion:CurrentSystemVersion mobileModel:[KKHelper platformString] appVersion:KK_Version imageVersion:[KKHelper imageVersion] delegate:self];
}

- (void)getVehicleRealData:(BOOL)on
{
    NSInteger interval = [KKPreference sharedPreference].appConfig.obdReadInterval/1000;
    if (interval == 0)
        interval = 60;
    
    if (on)
        [self.bleEngine getVehicleRealtimeData];
    [self.bleEngine setAutoQueryRealtimeData:on timeInterval:interval];
}

- (void)updateVehicleCondition:(KKCarStatusType)type
{
//    KKMsgPlaySound *msgPlayer = [[KKMsgPlaySound alloc] initForPlayingVibrate];
//    [msgPlayer play];
//    [msgPlayer release];
    
    self.connectStatus = type;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"updateVehicleConditionNotification" object:nil];
}

-(BOOL) checkCurrentVehicleEqualOBDSN
{
    [self stopAutoConnectTimer];
    if(self.currentVehicle.obdSN == nil && self.bleEngine && self.bleEngine.supportBLE)
    {
        [self.bleEngine stopScan];
        if(self.currentConnectedPeripheral.isConnected)
           [self.bleEngine disConnectActivePeripheral];
        return NO;
    }
    
    
    return YES;
}

- (void)scanPeripheral
{
    [self stopAutoConnectTimer];
    
    if(![self checkCurrentVehicleEqualOBDSN])
        return;
    
    if (self.bleEngine)
    {
        [self.bleEngine disConnectActivePeripheral];
        if ([self.obdList count] > 0 && [self.bleEngine supportBLE])
        {
            [self.bleEngine stopScan];
            [self.bleEngine scanPeripherals:8];
        }
    }
    else
        [self loadBLEEngine];
}

- (void)getCommonFaultDict
{
    if (self.currentVehicle == nil)
        return;
    
    NSInteger requestId = [[KKProtocolEngine sharedPtlEngine] vehicleFaultDict:self.currentVehicle.vehicleModelId dictVersion:[KKTBDictFault getNewestVersionForVehicle:self.currentVehicle.vehicleModelId] delegate:self];
    
    if ([self.currentVehicle.vehicleModelId length] > 0)
    [_faultDict setObject:self.currentVehicle.vehicleModelId forKey:[NSString stringWithFormat:@"%d",requestId]];
    
}

- (void)querySysId
{
    if (_connectIndex < [self.peripheralArray count])
    {
        [self.bleEngine disConnectActivePeripheral];
        [self.bleEngine connectPeripheral:[self.peripheralArray objectAtIndex:_connectIndex]];
        
        [self stopQuerySystemIdTimer];
                
        self.querySysIdConnTimer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(querySysIdTimeout:) userInfo:nil repeats:NO];
    }
    else
        [self querySysIdLoopConnectFinished];
}

- (void)querySysIdLoopConnectFinished
{
    // to sort and filter self.peripheralArray
    KKModelObdDetailInfo *defaultObd = nil;
    for (KKModelObdDetailInfo *obd in self.obdList)
    {
        if (obd.isDefault && [obd.obdSN length] > 0)
            defaultObd = obd;
    }
    
    NSMutableArray *tempArr = [NSMutableArray arrayWithCapacity:10];
    for (CBPeripheral *peripheral in self.peripheralArray)
    {
        NSString *uuidStr1 = peripheral.systemId;
        for (KKModelObdDetailInfo *obd in self.obdList)
        {
            if ([obd.obdSN length] > 0 && [uuidStr1 length] > 0 && [obd.obdSN isEqualToString:uuidStr1])
            {
                if (defaultObd && [obd.obdSN isEqualToString:defaultObd.obdSN])
                    [tempArr insertObject:peripheral atIndex:0];
                else
                    [tempArr addObject:peripheral];
            }
        }
    }
    self.peripheralArray = tempArr;
    
    [self stopQuerySystemIdTimer];
    
    _connectIndex = 0;
    _isInQureySysIdLoop = NO;
    
    // start loopConnect
    [self loopConnect];
}

- (void)querySysIdTimeout:(NSTimer *)aTimer
{
    NSLog(@"query sysId timer time out !!!!!!!!!!!");
    
    _connectIndex++;
    
    if (_connectIndex >= [self.peripheralArray count])    // has finished query system id loop
        [self querySysIdLoopConnectFinished];
    else
        [self querySysId];
}

- (void)querySysIdLoopConnectStart
{
    _isInQureySysIdLoop = YES;
    _connectIndex = 0;
    
    [self querySysId];
}

- (void)loopConnect
{
    if(![self checkCurrentVehicleEqualOBDSN])
        return;
    
    if ([self.peripheralArray count] == 0)
        [self scanPeripheral];
    
    if (_connectIndex < [self.peripheralArray count])
    {
        [self.bleEngine disConnectActivePeripheral];
        [self.bleEngine connectPeripheral:[self.peripheralArray objectAtIndex:_connectIndex]];
        
        [self.connectTimer invalidate];
        self.connectTimer = nil;
        
        if (self.connectTimer == nil)
        {
            self.connectTimer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(judgeConnect) userInfo:nil repeats:NO];
        }
    }
    else
    {
        _connectIndex = 0;
        [self scanPeripheral];
    }
}

- (void)judgeConnect
{
    if (self.isConnect)
    {
        [self stopAutoConnectTimer];
        _connectIndex = 0;
    }
    else
    {
        _connectIndex ++;
        [self loopConnect];
    }
}


- (void)loadBLEEngine
{
    if (self.bleEngine == nil)
    {
        KKBLEEngine *engine = [[KKBLEEngine alloc] init];
        engine.bleDelegate = self;
        engine.bleEngineDelegate = self;
        self.bleEngine = engine;
        [engine release];
    }        
}

- (void)detachVehicleListAndObdList:(NSArray *)aVehicleList
{
    [self.vehicleList removeAllObjects];
    [self.obdList removeAllObjects];
    
    for (KKModelVehicleDetailInfo *vehicleInfo in aVehicleList)
    {
        if ([vehicleInfo.isDefault isEqualToString:@"YES"] || [vehicleInfo.isDefault isEqualToString:@"1"])
            [self setCurrentVehicleInfo:vehicleInfo];
        
        if (vehicleInfo != nil)
        {
            [self.vehicleList addObject:vehicleInfo];
        }
        if ([vehicleInfo.obdSN length] != 0)
        {
            KKModelObdDetailInfo *obdInfo = [[KKModelObdDetailInfo alloc] init];
            obdInfo.obdSN = vehicleInfo.obdSN;
            obdInfo.obdId = nil;
            obdInfo.isDefault = [vehicleInfo.isDefault integerValue] == 1 ? YES : NO;
            [self.obdList addObject:obdInfo];
            [obdInfo release];
        }
        if(self.currentVehicle && [self.currentVehicle.vehicleId isEqualToString:vehicleInfo.vehicleId])
        {
            self.currentVehicle = vehicleInfo;
        }
    }
    
    if([self.vehicleList count] == 0)
    {
        self.currentVehicle = nil;
    }
    
    if (self.currentVehicle == nil && [self.vehicleList count] > 0)
        [self setCurrentVehicleInfo:[self.vehicleList objectAtIndex:0]];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:Notification_UpdatedVehicleList object:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"updateVehicleConditionNotification" object:nil];
    
    if(!self.isConnect)
    {
        [self loopConnect];
    }
    else
    {
        if(self.currentVehicle.obdSN == nil && (![self.currentConnectedPeripheral.systemId isEqualToString:self.currentVehicle.obdSN]))
        {
            [self loopConnect];
        }
    }
}


//- (void)detachVehicleListAndObdList
//{
//    [self.vehicleList removeAllObjects];
//    [self.obdList removeAllObjects];
//    
//    for (KKMOdelLoginVehicleInfo *vehicleInfo in KKAppDelegateSingleton.loginRsp.KKArrayFieldName(obdList,KKMOdelLoginVehicleInfo))
//    {
//        if (NO == [vehicleInfo.vehicleInfo isKindOfClass:[KKModelVehicleDetailInfo class]])
//            continue;
//        
//        if ([vehicleInfo.vehicleInfo.isDefault isEqualToString:@"YES"] || [vehicleInfo.vehicleInfo.isDefault isEqualToString:@"1"])
//            [self setCurrentVehicleInfo:vehicleInfo.vehicleInfo];
//        
//        if (vehicleInfo.vehicleInfo != nil)
//        {
//            vehicleInfo.vehicleInfo.obdSN = vehicleInfo.obdSN;
//            [self.vehicleList addObject:vehicleInfo.vehicleInfo];
//        }
//        if ([vehicleInfo.obdSN length] != 0 && [vehicleInfo.obdId length] != 0)
//        {
//            KKModelObdDetailInfo *obdInfo = [[KKModelObdDetailInfo alloc] init];
//            obdInfo.obdSN = vehicleInfo.obdSN;
//            obdInfo.obdId = vehicleInfo.obdId;
//            obdInfo.isDefault = [vehicleInfo.isDefault integerValue] == 1 ? YES : NO;
//            [self.obdList addObject:obdInfo];
//            [obdInfo release];
//        }
//    }
//    
//    if (self.currentVehicle == nil && [self.vehicleList count] > 0)
//        [self setCurrentVehicleInfo:[self.vehicleList objectAtIndex:0]];
//}

- (void)setCurrentVehicleInfo:(KKModelVehicleDetailInfo *)currentVehicle
{
    self.currentVehicle = currentVehicle;
    switch ([KKAuthorization sharedInstance].authorizationType) {
        case Authorization_Register:
            [self getCommonFaultDict];
            break;
            
        default:
            break;
    }
}

- (BOOL)isHaveFaultCodeWhenLaunch:(NSString *)faultCode
{
    BOOL have = NO;
    for (NSString *string in self.warnArray)
    {
        if ([string isEqualToString:faultCode])
        {
            have = YES;
            break;
        }
    }
    if (have == NO)
        [self.warnArray addObject:faultCode];
    
    return have;
}

- (void)stopAutoConnectTimer
{
    if (self.bleEngine && [self.bleEngine supportBLE])
        [self.bleEngine stopScan];
    
    [self.connectTimer invalidate];
    self.connectTimer = nil;
    
    [self stopQuerySystemIdTimer];
}

- (void)stopQuerySystemIdTimer
{
    [self.querySysIdConnTimer invalidate];
    self.querySysIdConnTimer = nil;
}

- (void)setDefaultObdSN:(NSString *)obdSN
{
    if ([obdSN length] == 0)
        return;
    
    BOOL have = NO;
    
    for (KKModelObdDetailInfo *obdInfo in KKAppDelegateSingleton.obdList)
    {
        obdInfo.isDefault = NO;
        if ([obdSN isEqualToString:obdInfo.obdSN])
        {
            have = YES;
            break;
        }
    }
    
    if (!have && [obdSN length] > 0)
    {
        KKModelObdDetailInfo *obdInfo = [[KKModelObdDetailInfo alloc] init];
        obdInfo.obdSN = obdSN;
        obdInfo.obdId = nil;
        obdInfo.isDefault = YES;
        [KKAppDelegateSingleton.obdList addObject:obdInfo];
        [obdInfo release];
    }
}

- (void)loginOverdue
{
    [KKMessagePollingManager stopPolling];
    [KKProtocolEngine sharedPtlEngine].userName = nil;
    [KKProtocolEngine sharedPtlEngine].password = nil;
    
    if(!self.isLoginOverdue)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"登录过期，请重新登录！" block:^{
            self.isLoginOverdue = NO;
            switch ([KKAuthorization sharedInstance].authorizationType) {
                case Authorization_Visitor:
                {
                    [self jumpToLoginVc];
                }
                    break;
                case Authorization_Register:
                {
                    [self logOff];
                }
                    break;
            }
        }];
        
        self.isLoginOverdue = YES;
    }
}

#pragma mark - DriveRecord
-(void) startDriveRecord
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    [[KKDriveRecordEngine sharedInstance] startDriveRecord];
    
    [_obdConnectSoundPlay play];
    
    [self stopLocating];
    [self startLocating];
}

-(void) stopDriveRecord
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
    
    if([KKDriveRecordEngine sharedInstance].recording)
        [_obdDisConnectSoundPlay play];
    
//    if([[NSDate date] timeIntervalSinceDate:self.obdConnectTime] > 20)
//    {
//        //OBD连接时间大于20秒
        switch ([UIApplication sharedApplication].applicationState) {
            case UIApplicationStateBackground:
            {
                [[KKDriveRecordEngine sharedInstance] stopDriveRecordImmediately:YES];
            }
                break;
                
            default:
            {
                [[KKDriveRecordEngine sharedInstance] stopDriveRecordImmediately:NO];
                [self stopLocating];
                [self startLocating];
            }
                break;
        }
//    }
//    else
//    {
//        [[KKDriveRecordEngine sharedInstance] stopDriveRecordWithOutSave];
//    }
}

#pragma mark -
#pragma mark KKBLECoreDelegate

-(void) obdDisconnectDataClear
{
    self.isConnect = NO;
    self.vehicleRealtimeData = nil;
    self.firstVehicleRealtimeData = nil;
    self.currentConnectedPeripheral = nil;
    [self updateVehicleCondition:e_CarNotOnLine];
    [self getVehicleRealData:NO];
    
}

- (void)didDiscoverPeripheral:(CBPeripheral *)aPeripheral RSSI:(NSNumber *)RSSI
{
    
}

-(void) centralManagerDidUpdateState:(CBCentralManagerState)state
{
    switch (state) {
        case CBCentralManagerStateUnsupported:
            [KKCustomAlertView showAlertViewWithMessage:@"您的手机不支持蓝牙4.0！"];
            break;
        case CBCentralManagerStatePoweredOff:
        {
            [KKCustomAlertView showAlertViewWithMessage:@"您的手机蓝牙没打开！"];
            
            [self stopDriveRecord];
            
            [self obdDisconnectDataClear];
//            [[NSNotificationCenter defaultCenter]postNotificationName:@"updateVehicleRealTimeDataNotification" object:nil];
        }
            break;
            
        default:
            break;
    }
}

- (void)mobileSupportBLE:(BOOL)aSupported
{
    if (aSupported)
    {
        if ([self.obdList count] > 0)
        {
            self.isConnect = NO;
            [self scanPeripheral];
        }
    }
    else
    {
        self.vehicleRealtimeData = nil;
        self.firstVehicleRealtimeData = nil;
        [self updateVehicleCondition:e_CarNotOnLine];
        [self getVehicleRealData:NO];
        self.isConnect = NO;
    }
}

- (void)didScanFinishWithPeripherals:(NSMutableArray *)perArr
{
    [self.peripheralArray removeAllObjects];
    self.isConnect = NO;
    
    [self stopAutoConnectTimer];
    
    [self.peripheralArray addObjectsFromArray:perArr];
    
    if ([self.peripheralArray count] == 0)
        [self scanPeripheral];
    else
        [self querySysIdLoopConnectStart];
}

- (void)didConnectPeripheral:(CBPeripheral *)aPeripheral state:(KKBLE_STATUS)aState error:(NSError *)aError
{
    if (aState == KKBLE_STATUS_CONNECTED)
    {
        self.obdConnectTime = [NSDate date];
        self.currentConnectedPeripheral = aPeripheral;
        
    }
    else
    {
        [self obdDisconnectDataClear];
        
        if(aState == KKBLE_STATUS_DISCONNECTED)
        {
            [self stopDriveRecord];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"disConnectedPeripheralNotication" object:nil];
//            [[NSNotificationCenter defaultCenter]postNotificationName:@"updateVehicleRealTimeDataNotification" object:nil];
        }
        
        if (_isInQureySysIdLoop)
        {
            if (aError)
            {
                BOOL b = [self.querySysIdConnTimer isValid];
                NSLog(@"queryTimer valid:%@", b==YES?@"TRUE":@"FALSE");
                if (!b)
                {
                    [self querySysIdTimeout:nil];
                }
            }
            //不能立马查询下一个,可能蓝牙会自动断开
//            _connectIndex ++;
//            [self querySysIdTimeout:nil];

        }
        else
        {
            if (self.isConnect)
            {
                self.isConnect = NO;
                [self scanPeripheral];
            }
            else
                [self judgeConnect];
        }
    }
}


- (void)didDiscoverCharacteristicsForReadWrite:(NSInteger)aReadOrWrite error:(NSError *)error
{
   if (_isInQureySysIdLoop)
       return;
    
    if (aReadOrWrite == 1)
        [self.bleEngine getVehicleVin];
}

- (void)didGetSystemId:(NSString *)aSystemId forPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error
{
    if (_isInQureySysIdLoop) {
        [self stopQuerySystemIdTimer];
        
        if([aSystemId isEqualToString:self.currentVehicle.obdSN])
        {
            _connectIndex = 0;
            _isInQureySysIdLoop = NO;
            
            [self stopAutoConnectTimer];
            self.isConnect = YES;
            
            [self checkDTCWaring];
            
            [self getVehicleRealData:YES];
            
            [self startDriveRecord];
        }
        else
        {
            _connectIndex++;
            if (_connectIndex >= [self.peripheralArray count])
                [self querySysIdLoopConnectFinished];
            else {
                CBPeripheral *peripheral = [self.peripheralArray objectAtIndex:_connectIndex];
                peripheral.systemId = aSystemId;
                [self querySysId];
            }
        }
    }
    
}

#pragma mark -
#pragma mark KKBLEEngineDelegate

- (void)setOBDProtocolResp:(id)aRetObj
{
    
}

// @aRetObj: NSNumber object (0:NO 1:YES)
- (void)setOBDTimeIntervalResp:(id)aRetObj
{
    
}

// @aRetObj: KKModelVehicleRunData
- (void)vehicleAllDataResp:(id)aRetObj
{
    
}

// @aRetObj: KKModelVehicleRealtimeData
- (void)vehicleRealtimeDataResp:(id)aRetObj
{
    KKModelVehicleRealtimeData *realData = (KKModelVehicleRealtimeData *)aRetObj;
    self.vehicleRealtimeData = realData;
    if(self.firstVehicleRealtimeData == nil)
        self.firstVehicleRealtimeData = realData;
    
    [KKDriveRecordEngine sharedInstance].driveRecordDetail.distance = self.vehicleRealtimeData.kiloMileage - self.firstVehicleRealtimeData.kiloMileage;
    [KKDriveRecordEngine sharedInstance].driveRecordDetail.oilWear = self.vehicleRealtimeData.oilWearPer100;
    [KKDriveRecordEngine sharedInstance].driveRecordDetail.oilPrice = [[KKPreference sharedPreference].appUserConfig.oil_price floatValue];
    [KKDriveRecordEngine sharedInstance].driveRecordDetail.oilKind = [KKPreference sharedPreference].appUserConfig.oil_kind;
    [[KKDriveRecordEngine sharedInstance] countTotalOilMoney];
    
    [KKDriveRecordEngine sharedInstance].driveRecordDetail.travelTime = [[NSDate date] timeIntervalSince1970WithMillisecond] - [self.obdConnectTime timeIntervalSince1970WithMillisecond];
    
    NSString *oilRange = [KKPreference sharedPreference].appConfig.remainOilMassWarn;
    NSArray *rangArray = [KKHelper getArray:oilRange BySeparateString:@"_"];
    if ([rangArray count] == 0)
        rangArray = [NSArray arrayWithObjects:@"15",@"25",nil];
    if (realData.oilMass != KKOBDDataNA)
    {
        KKModelPreferencePromptVoiceSwitch *alertSwitch = [KKPreference sharedPreference].voiceSwitch;
        if (realData.oilMass >= 0 && realData.oilMass <= [[rangArray objectAtIndex:0] integerValue])
        {
            if (alertSwitch.lowLevelWarn == NO)
            {
                alertSwitch.lowLevelWarn = YES;
                KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:[NSString stringWithFormat:@"您当前油量为%.f%@,请及时加油！",realData.oilMass,@"%"] WithType:KKCustomAlertView_default];
                [alertView addButtonWithTitle:@"我知道了" imageName:@"alert-blue2-button.png" block:nil];
                [alertView addButtonWithTitle:@"去加油！" imageName:@"alert-orange-button.png" block:^{
                    [[NSNotificationCenter defaultCenter] postNotificationName:@"jumpToOilMapViewController" object:nil];
                }];
                [alertView show];
                [alertView release];
            }
        }
        else if (realData.oilMass >= [[rangArray objectAtIndex:0] integerValue] && realData.oilMass <= [[rangArray objectAtIndex:1] integerValue])
        {
            if (alertSwitch.highLevelWarn == NO)
            {
                alertSwitch.highLevelWarn = YES;
                KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:[NSString stringWithFormat:@"您当前油量为%2f,请及时加油！",realData.oilMass] WithType:KKCustomAlertView_default];
                [alertView addButtonWithTitle:@"我知道了" imageName:@"alert-blue2-button.png" block:nil];
                [alertView addButtonWithTitle:@"去加油！" imageName:@"alert-orange-button.png" block:^{
                    [[NSNotificationCenter defaultCenter] postNotificationName:@"jumpToOilMapViewController" object:nil];
                }];
                [alertView show];
                [alertView release];
            }
        }
        else
        {
            alertSwitch.highLevelWarn = NO;
            alertSwitch.lowLevelWarn = NO;
        }
        [KKPreference sharedPreference].voiceSwitch = alertSwitch;
    }
    
    [[KKProtocolEngine sharedPtlEngine] vehicleCondition:self.currentVehicle.vehicleVin
                                                   obdSN:self.currentConnectedPeripheral.systemId
                                                 oilWear:realData.oilWear
                                          currentMileage:realData.kiloMileage
                                          instantOilWear:realData.oilWearOfInstant
                                       oilWearPerHundred:realData.oilWearPer100
                                                 oilMass:realData.oilMass
                                engineCoolantTemperature:realData.engineTempture
                                          batteryVoltage:realData.voltageOfBattery
                                              reportTime:[NSDate date]
                                               vehicleId:self.currentVehicle.vehicleId
                                                delegate:self];
    
    [[NSNotificationCenter defaultCenter]postNotificationName:@"updateVehicleRealTimeDataNotification" object:nil];
}

// @aRetObj: NSArray, inside are NSString
- (void)vehicleDataWithPIDResp:(id)aRetObj
{
    
}

// @aRetObj: NSString
- (void)vehicleVinResp:(id)aRetObj
{
//    NSString *vin = (NSString *)aRetObj;
//    
//    if ([vin length] == 0)
//    {
//        BOOL haveFound = NO;
//        for (KKModelVehicleDetailInfo *vehicleInfo in self.vehicleList)
//        {
//            if ([self.currentConnectedPeripheral.systemId isEqualToString:vehicleInfo.obdSN])
//            {
//                [self stopAutoConnectTimer];
//                
//                KKModelPreferenceGlobalValue *values = [KKPreference sharedPreference].globalValues;
//                if ([values.currentVehicleMile length] > 0 && [values.currentVehicleMile intValue] > 0)
//                    [self.bleEngine setMileage:[values.currentVehicleMile intValue]];
//                
//                self.isConnect = YES;
//                [self setCurrentVehicleInfo:vehicleInfo];
//                [self updateVehicleCondition:e_CarWell];
//                [self getVehicleRealData:YES];
//                
//                haveFound = YES;
//                
//                [self startDriveRecord];
//                break;
//            }
//        }
//        if (!haveFound)
//        {
//            [self.bleEngine disConnectActivePeripheral];
//            [self updateVehicleCondition:e_CarNotOnLine];
//            [self judgeConnect];
//        }
//    }
//    else
//    {
//        [self stopAutoConnectTimer];
//        self.isConnect = YES;
//
//        if(![self.currentConnectedPeripheral.systemId isEqualToString:self.currentVehicle.obdSN])
//        {
//            [self.bleEngine disConnectActivePeripheral];
//        }
//        else
//        {
//            [self updateVehicleCondition:e_CarWell];
//            [self getVehicleRealData:YES];
//            
//            [self startDriveRecord];
//        }
//    }
//    
//    
//    //接口已废弃
////    else
////    {
////        [[KKProtocolEngine sharedPtlEngine] getVehicleInfoWithVehicleVin:vin
////                                                              withUserNo:[KKProtocolEngine sharedPtlEngine].userName                                                                delegate:self];
////    }
}

// @aRetObj: NSNumber
- (void)vehicleSetMileageResp:(id)aRetObj
{
    NSNumber *result = (NSNumber *)aRetObj;
    if ([result intValue] == 1)
    {
        [KKPreference sharedPreference].globalValues = nil;
    }
}

// @aRetObj: NSNumber object (0:NO 1:YES)
- (void)vehicleClearFetalResp:(id)aRetObj
{
    
}

-(void) vehicleDTCAllReport:(NSString *)aRetObj
{
    [[KKProtocolEngine sharedPtlEngine] vehicleFault:aRetObj
                                              userNo:[KKProtocolEngine sharedPtlEngine].userName
                                          vehicleVin:self.currentVehicle.vehicleVin
                                               obdSN:self.currentVehicle.obdSN
                                          reportTime:[NSDate date]
                                           vehicleId:self.currentVehicle.vehicleId delegate:self];
}

// @aRetObj: NSArray, inside are NSString
- (void)vehicleDTCReport:(id)aRetObj
{
    if (!self.isConnect)
        return;
    
    NSArray *array = (NSArray *)aRetObj;
    
    [self.dtcArray removeAllObjects];
    
    for (NSString *string in array)
    {
        if ([string length] > 0)
        {
            KKModelDTCMessage *message = [[KKModelDTCMessage alloc] init];
            message.faultCode = string;
            message.userNo = [KKProtocolEngine sharedPtlEngine].userName;
            message.timeStamp = [NSString stringWithFormat:@"%lld",[[NSDate date] timeIntervalSince1970WithMillisecond]];
            message.vehicleModelId = self.currentVehicle.vehicleModelId;
            message.warnTimeStamp = message.timeStamp;
            [self.dtcArray addObject:message];
            [message release];
        }
    }
    
    BOOL voice = NO;
    if ([self.dtcArray count] > 0)
    {
        self.dtcWarnning = YES;
        KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];

        for (KKModelDTCMessage *message in self.dtcArray)
        {
//            BOOL existFaultCode = [self isHaveFaultCodeWhenLaunch:message.faultCode];
//            if(needWarn == NO)
//                needWarn = !existFaultCode;
//            if (existFaultCode)
//            {
                NSArray *queryArr = [dtcTb queryDTCMessageByUserNo:message.userNo vehicleModelId:message.vehicleModelId faultCode:message.faultCode];
                if ([queryArr count] > 0)
                {
                    KKModelDTCMessage *temMessage = (KKModelDTCMessage *)[queryArr objectAtIndex:0];
                    NSInteger timeStamp = [KKPreference sharedPreference].appConfig.appVehicleErrorCodeWarnIntervals;
                    if (timeStamp == 0)
                        timeStamp = 24*60*60;
                    else
                        timeStamp = timeStamp * 3600;
                    
//                    NSLog(@"new :%f , old warn timeStamp :%f ,difference is %f, interval :%d ",[message.warnTimeStamp doubleValue],[temMessage.warnTimeStamp doubleValue],[message.warnTimeStamp doubleValue] - [temMessage.warnTimeStamp doubleValue],timeStamp);
                    
                    if ([message.warnTimeStamp doubleValue] - [temMessage.warnTimeStamp doubleValue] > timeStamp)
                    {
                        [dtcTb insertDTCMessage:message];
//                        voice = YES;
                    }
                }
                else
                {
                    [dtcTb insertDTCMessage:message];
                    voice = YES;
                }
//            }
//            else
//            {
////                [self showDTCAlertInfo:message];
//                [dtcTb insertDTCMessage:message];
//                voice = YES;
//            }
        }
        [dtcTb release];
        
        if(voice)
        {
            [self showAllDTCAlerInfo];
        }
    
        [[NSNotificationCenter defaultCenter] postNotificationName:@"updateDtcArrayNotication" object:nil];
        [self updateVehicleCondition:e_CarAlarm];
        if ([UIApplication sharedApplication].applicationState ==  UIApplicationStateActive && [KKPreference sharedPreference].voiceSwitch && voice)
        {
            [_dtcSoundPlay play];
        }
    }
}

-(void)showAllDTCAlerInfo
{
    UIApplication *application = [UIApplication sharedApplication];
    if (application.applicationState !=  UIApplicationStateActive)
    {
        application.applicationIconBadgeNumber = application.applicationIconBadgeNumber ++;
        
        NSString *dtcMsg = nil;
        dtcMsg = [NSString stringWithFormat: @"收到新故障，您有 %d 条故障未处理！",[self.dtcArray count]];
        
        UILocalNotification *notification=[[UILocalNotification alloc] init];
        if (notification!=nil) {
            NSLog(@">> support local notification");
            NSDate *now=[NSDate date];
            notification.fireDate=[now dateByAddingTimeInterval:3];
            notification.timeZone=[NSTimeZone defaultTimeZone];
            notification.soundName = @"BEEP.WAV";
            notification.alertBody= dtcMsg;
            [[UIApplication sharedApplication] scheduleLocalNotification:notification];
        }
        [notification release];
    }
    else
    {
        KKCarWarningView *warningView = [[KKCarWarningView alloc] initWithTitle:@"故障提醒"
                                                                    WithMessage:[NSString stringWithFormat: @"收到新故障，您有 %d 条故障未处理！",[self.dtcArray count]]
                                                                WithWarningType:KKCarWarningType_0];
        warningView.delegate = self;
        [warningView show];
        [warningView release];
    }
}

- (void)showDTCAlertInfo:(KKModelDTCMessage *)message
{
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    
    NSArray *array = [faultDict getFaultInfoWithCode:message.faultCode vehicleModelId:message.vehicleModelId];
    
    NSString *dtcCode = nil;
    NSString *dtcMsg = nil;
    if ([array count] > 0)
    {
        dtcMsg = [NSString string];
        
        for (int t = 0 ; t < [array count] ; t ++)
        {
            KKModelFaultCodeInfo *faultInfo  = [array objectAtIndex:t];
            dtcCode = faultInfo.faultCode;
            dtcMsg = [dtcMsg stringByAppendingString:[NSString stringWithFormat:@",%@",faultInfo.description]];
        }

        if ([dtcMsg length] > 0)
            dtcMsg = [dtcMsg stringByReplacingCharactersInRange:NSMakeRange(0, 1) withString:@""];
    }
    else
    {
        dtcCode = message.faultCode;
        dtcMsg = @"未知故障";
    }
    
    UIApplication *application = [UIApplication sharedApplication];
    if (application.applicationState !=  UIApplicationStateActive)
    {
        application.applicationIconBadgeNumber = application.applicationIconBadgeNumber ++;

        dtcMsg = [NSString stringWithFormat:@"%@ :%@",dtcCode,dtcMsg];

        UILocalNotification *notification=[[UILocalNotification alloc] init];
        if (notification!=nil) {
            NSLog(@">> support local notification");
            NSDate *now=[NSDate date];
            notification.fireDate=[now dateByAddingTimeInterval:3];
            notification.timeZone=[NSTimeZone defaultTimeZone];
            notification.soundName = @"BEEP.WAV";
            notification.alertBody= dtcMsg;
            [[UIApplication sharedApplication] scheduleLocalNotification:notification];
        }
        [notification release];
    }
    else
    {
        KKCarWarningView *warningView = [[KKCarWarningView alloc] initWithTitle:dtcCode
                                                                    WithMessage:dtcMsg
                                                                WithWarningType:KKCarWarningType_0];
        warningView.delegate = self;
        [warningView show];
        [warningView release];
    }
    
    [faultDict release];
}


- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification NS_AVAILABLE_IOS(4_0)
{
    
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
//    [self stopLocating];
    _geocodeCitySuccess = NO;
    
//    KKModelPreferenceCityInfo *cityInfo = [[KKModelPreferenceCityInfo alloc] init];
//    cityInfo.provinceName = self.currentCity.provinceName;
//    cityInfo.cityName = self.currentCity.cityName;
//    cityInfo.cityCode = self.currentCity.cityCode;
//    cityInfo.latitude = [NSString stringWithFormat:@"%f",self.currentCoordinate2D.latitude];
//    cityInfo.longitude = [NSString stringWithFormat:@"%f",self.currentCoordinate2D.longitude];
//    [KKPreference sharedPreference].cityInfo = cityInfo;
//    [cityInfo release];
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
//    [self startLocating];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    application.applicationIconBadgeNumber = 0;
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    
    [[KKDriveRecordEngine sharedInstance] stopDriveRecordImmediately:YES];
}

- (void)onGetNetworkState:(int)iError
{
    NSLog(@"onGetNetworkState %d",iError);
}

- (void)onGetPermissionState:(int)iError
{
    NSLog(@"onGetPermissionState %d",iError);
}

-(void) checkDTCWaring
{
    KKTBDTCMessage *dtcTb = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
    NSArray *queryArr = [dtcTb getDTCMessageByUserNo:[KKProtocolEngine sharedPtlEngine].userName vehicleModelId:self.currentVehicle.vehicleModelId];
    [dtcTb release];
    if([queryArr count] > 0)
    {
        self.dtcWarnning = YES;
        if(self.isConnect)
            [self updateVehicleCondition:e_CarAlarm];
        else
            [self updateVehicleCondition:e_CarNotOnLine];
    }
    else
    {
        self.dtcWarnning = NO;
    
        if(self.isConnect)
        {
            [self updateVehicleCondition:e_CarWell];
        }
        else
        {
            [self updateVehicleCondition:e_CarNotOnLine];
        }
    }
}

#pragma mark -
#pragma mark KKCarWarningViewDelegate

- (void)KKCarWarningViewButtonClicked:(NSInteger)index andFaultCode:(NSString *)faultCode
{
    if ([self.window.rootViewController isKindOfClass:[KKRootViewController class]] && index == 101)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"jumpToDTCManagerViewController" object:nil userInfo:nil];
//        [self.rootViewController popToRootViewWithIndex:0];
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"handleDTCMessageNotification" object:nil userInfo:[NSDictionary dictionaryWithObject:faultCode forKey:@"faultCode"]];
        [self updateVehicleCondition:e_CarWell];
    }
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)userLoginResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;    
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"login error is %@",error.description);
        if (_loginRequestId == [aReqId intValue])
        {
            [self registerAndLogin];
            [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        }
		return KKNumberResultEnd;
	}
    
    [[KKAuthorization sharedInstance] setAuthorizationType:Authorization_Register];
    
    KKModelLoginRsp *loginRsp = (KKModelLoginRsp *)rsp;
    [KKPreference sharedPreference].appConfig = loginRsp.appConfig;
    [KKPreference sharedPreference].appUserConfig = loginRsp.appUserConfig;
    
    [KKDriveRecordEngine sharedInstance].firstRecordTime = [loginRsp.appUserConfig.first_drive_log_time longLongValue];
    
    NSMutableArray *array = [[NSMutableArray alloc] init];
    for(KKMOdelLoginVehicleInfo *vInfo in loginRsp.obdList__KKMOdelLoginVehicleInfo)
    {
        if(vInfo.vehicleInfo != nil)
        {
            vInfo.vehicleInfo.obdSN = vInfo.obdSN;
            [array addObject:vInfo.vehicleInfo];
        }
    }
    [KKAppDelegateSingleton detachVehicleListAndObdList:array];
    [array release];
    
    if (_loginRequestId == [aReqId intValue] || ePtlApi_LoadCache == [aReqId integerValue])
        [self ShowRootView];
    
    return KKNumberResultEnd;
}

- (NSNumber *)getVehicleInfoByVehicleVin:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
//        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        NSLog(@"get Vehicle Info is %@",error.description);
        [self updateVehicleCondition:e_CarNotOnLine];
        [self.bleEngine disConnectActivePeripheral];
        [self judgeConnect];
		return KKNumberResultEnd;
	}
    KKModelVehicleGetInfoRsp *getRsp = (KKModelVehicleGetInfoRsp *)rsp;
    if ([getRsp.vehicleInfo.vehicleId length] > 0 && getRsp.vehicleInfo != nil)
    {
        KKModelPreferenceGlobalValue *values = [KKPreference sharedPreference].globalValues;
        if ([values.currentVehicleMile length] > 0 && [values.currentVehicleMile floatValue] > 0)
            [self.bleEngine setMileage:[values.currentVehicleMile floatValue]];
        
        [self stopAutoConnectTimer];
        self.isConnect = YES;
        [self setCurrentVehicleInfo:getRsp.vehicleInfo];
        [self updateVehicleCondition:e_CarWell];
        [self getVehicleRealData:YES];
    }
    else
    {
        [self.bleEngine disConnectActivePeripheral];
        [self updateVehicleCondition:e_CarNotOnLine];
        [self judgeConnect];
    }
    
    return KKNumberResultEnd;
}

- (NSNumber *)vehicleFaultDictResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"vehicle fault dict error is %@",error.description);
		return KKNumberResultEnd;
	}
    KKModelVehicleFaultDictRsp *faultDictRsp = (KKModelVehicleFaultDictRsp *)rsp;
    KKTBDictFault *faultDict = [[KKTBDictFault alloc] initWithDB:[KKDB sharedDB]];
    if ([aReqId intValue] == _commonFaultDictRequestId)
        [faultDict createTableWithVehicleModel:nil dictDetail:faultDictRsp];
    else
    {
        NSString *vehicleModelId = [_faultDict objectForKey:[NSString stringWithFormat:@"%d",[aReqId intValue]]];
        if ([vehicleModelId length] > 0)
            [faultDict createTableWithVehicleModel:self.currentVehicle.vehicleModelId dictDetail:faultDictRsp];
    }
    
    [faultDict release];
    
    return KKNumberResultEnd;
}

- (NSNumber *)vehicleFaultResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"vehicleFault error is %@",error.description);
//        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    return KKNumberResultEnd;
}

- (NSNumber *)vehicleConditionResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        NSLog(@"vehicle Condition error is %@",error.description);
		return KKNumberResultEnd;
	}
    return KKNumberResultEnd;
}
#pragma mark -
#pragma mark BMKSearchDelegate
- (void)onGetAddrResult:(BMKAddrInfo*)result errorCode:(int)error
{
   if (error == BMKErrorOk)
   {
        if ([result.addressComponent.city length] > 0)
        {
            if (![self.currentCity.cityName isEqualToString:result.addressComponent.city])
            {
                NSLog(@"current City Name is %@",result.addressComponent.city);
                
                _geocodeCitySuccess = YES;
                
                self.currentCity.replaceCityName = result.addressComponent.city;
                self.currentCity.replaceProvinceName = result.addressComponent.province;
                
                _repeat = NO;
            }
        }
    }
    else
    {
        NSLog(@"reverseGeocode error code is %d",error);
    }
}

@end
