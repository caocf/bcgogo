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
@property (nonatomic, retain) KKCity            *currentCity;
@property (nonatomic, retain) BMKSearch         *search;
@property (nonatomic, assign) NSInteger         unReadNum;
@property (nonatomic, assign) CBPeripheral      *currentConnectedPeripheral;
@property (nonatomic, retain) KKModelLoginRsp   *loginRsp;
@property (nonatomic, retain) KKModelVehicleDetailInfo *currentVehicle;
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

- (void)registerAndLogin;
- (void)ShowRootView;
- (void)loadBLEEngine;
- (void)logOff;
- (void)startLocating;
- (void)stopLocating;
- (void)setNewMsgBadgeValue:(NSInteger)aUnreads;
- (void)detachVehicleListAndObdList;
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

@end
