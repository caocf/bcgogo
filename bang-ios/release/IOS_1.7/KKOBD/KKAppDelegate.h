//
//  KKAppDelegate.h
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <CoreLocation/CoreLocation.h>
#import "BMapKit.h"
#import "AKLocationManager.h"
#import "KKProtocolEngineDelegate.h"
#import "KKBLEEngine.h"
#import "KKSearchCarViewController.h"
#import "KKCarStatusView.h"
#import "KKMsgPlaySound.h"
#import "KKCarWarningView.h"

@class KKRootViewController;
@class KKModelLoginRsp;
@class KKModelVehicleDetailInfo;

//----------------------------------------KKCity-----------------------------------------

@interface KKCity : NSObject
@property (nonatomic ,copy)NSString *provinceName;
@property (nonatomic ,copy)NSString *cityName;
@property (nonatomic ,copy)NSString *cityCode;
@property (nonatomic ,copy)NSString *cityID;
@property (nonatomic ,copy)NSString *replaceProvinceName;
@property (nonatomic ,copy)NSString *replaceCityName;

@end

typedef enum
{
    DriveRecordType_Unknow = 0,
    DriveRecordType_Start = 1,
    DriveRecordType_Recording,
    DriveRecordType_Stop
}DriveRecordType;

@interface KKAppDelegate : UIResponder <UIApplicationDelegate,BMKGeneralDelegate,BMKSearchDelegate,KKProtocolEngineDelegate,KKBLECoreDelegate,KKBLEEngineDelegate,KKCarWarningViewDelegate>
{
    NSMutableDictionary     *_faultDict;
    KKMsgPlaySound          *_dtcSoundPlay;
    NSInteger               _commonFaultDictRequestId;
    NSInteger               _loginRequestId;
    NSInteger               _connectIndex;
    BOOL                    _isInQureySysIdLoop;
    NSTimer                 *_timer;                //经纬度反向编码timer
    BOOL                    _repeat;
    BOOL                    _geocodeCitySuccess;    //反解地址成功
    
}
@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) KKRootViewController *rootViewController;
@property (strong, nonatomic) UINavigationController *loginNavController;
@property (nonatomic, retain) UIViewController       *waittingVc;
@property (nonatomic, assign) CLLocationCoordinate2D  currentCoordinate2D;
@property (nonatomic, assign) CLLocationCoordinate2D  currentCoordinate2D_Gcj02;
@property (nonatomic, retain) KKCity            *currentCity;
@property (nonatomic, retain) BMKSearch         *search;
@property (nonatomic, assign) NSInteger         unReadNum;
@property (nonatomic, assign) CBPeripheral      *currentConnectedPeripheral;
@property (nonatomic, retain) KKModelLoginRsp   *loginRsp;
@property (nonatomic, retain) KKModelVehicleDetailInfo *currentVehicle;
@property (nonatomic, retain) KKModelVehicleDetailInfo *regVehicleDetailInfo;
@property (nonatomic, retain) KKModelVehicleRealtimeData *firstVehicleRealtimeData;//连上OBD第一次读到的实时数据
@property (nonatomic, retain) KKModelVehicleRealtimeData *vehicleRealtimeData;
@property (nonatomic, retain) KKBLEEngine       *bleEngine;
@property (nonatomic, retain) NSMutableArray    *vehicleList;
@property (nonatomic, retain) NSMutableArray    *obdList;
@property (nonatomic, retain) NSMutableArray    *peripheralArray;
@property (nonatomic, retain) NSTimer           *querySysIdConnTimer;
@property (nonatomic, retain) NSTimer           *connectTimer;
@property (nonatomic, assign) KKCarStatusType   connectStatus;
@property (nonatomic, retain) NSMutableArray    *dtcArray;          //新增车辆报警信息(总数)
@property (nonatomic, retain) NSMutableArray    *warnArray;         //报警数组 （inside is faultCode）
@property (nonatomic, assign) BOOL              isConnect;          //当前obd连接状态
@property (nonatomic, retain) NSString          *coordinateType;    //定位定位的状态:CURRENT,LAST （当前、上次）
@property (nonatomic, assign) BOOL              bindOBDRemind;      //从注册界面过来，提醒绑定OBD
@property (nonatomic, copy) NSString            *versionStr;
@property (nonatomic, assign) BOOL              isLoginOverdue;     //登录过期弹出框是否已显示

- (void)registerAndLogin;
- (void)ShowRootView;
- (void)loadBLEEngine;
- (void)logOff;
- (void)startLocating;
- (void)stopLocating;
- (void)setNewMsgBadgeValue:(NSInteger)aUnreads;
- (void)detachVehicleListAndObdList:(NSArray *)aVehicleList;
- (void)reLogin;
- (void)getVehicleRealData:(BOOL)on;
- (void)updateVehicleCondition:(KKCarStatusType)type;
- (void)scanPeripheral;
- (void)getCommonFaultDict;
- (void)querySysIdLoopConnectStart;
- (void)querySysIdLoopConnectFinished;
- (void)loopConnect;
- (void)setCurrentVehicleInfo:(KKModelVehicleDetailInfo *)currentVehicle;
- (BOOL)isHaveFaultCodeWhenLaunch:(NSString *)faultCode;
- (void)stopAutoConnectTimer;
- (void)setDefaultObdSN:(NSString *)obdsn;
- (void)loginOverdue;

-(void) visitorLogin;
-(void) jumpToLoginVc;

@end
