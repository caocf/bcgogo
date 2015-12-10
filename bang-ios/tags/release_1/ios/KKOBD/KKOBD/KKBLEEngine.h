//
//  KKBLEEngine.h
//  KKOBD
//
//  Created by codeshu on 9/18/13.
//  Copyright (c) 2013 sgq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKBLECore.h"

@protocol KKBLEEngineDelegate<NSObject>

@optional

// @aRetObj: NSNumber object (0:NO 1:YES)
- (void)setOBDProtocolResp:(id)aRetObj;

// @aRetObj: NSNumber object (0:NO 1:YES)
- (void)setOBDTimeIntervalResp:(id)aRetObj;

// @aRetObj: KKModelVehicleRunData
- (void)vehicleAllDataResp:(id)aRetObj;

// @aRetObj: KKModelVehicleRealtimeData
- (void)vehicleRealtimeDataResp:(id)aRetObj;

// @aRetObj: NSArray, inside are NSString
- (void)vehicleDataWithPIDResp:(id)aRetObj;

// @aRetObj: NSString
- (void)vehicleVinResp:(id)aRetObj;

// @aRetObj: NSNumber
- (void)vehicleSetMileageResp:(id)aRetObj;

// @aRetObj: NSNumber object (0:NO 1:YES)
- (void)vehicleClearFetalResp:(id)aRetObj;

// @aRetObj: NSArray, inside are NSString
- (void)vehicleDTCReport:(id)aRetObj;

@end

#define KKOBDDataNA  -10000.0f 
// =========================================================================================================
@interface KKModelVehicleRunData : NSObject

// 以下属性当值 > KKOBDDataNA 时认为是 N/A
@property (nonatomic, assign) CGFloat           engineBurden;                   // 发动机负荷（%），
@property (nonatomic, assign) CGFloat           engineTempture;                 // 发动机水温（℃），
@property (nonatomic, assign) CGFloat           engineFuelShortFix;             // 短时燃油修正（%），
@property (nonatomic, assign) CGFloat           engineFuelLongFix;              // 长时燃油修正（%），
@property (nonatomic, assign) CGFloat           intakePressure;                 // 进气歧管绝对压力（kPa）,
@property (nonatomic, assign) CGFloat           engineRotation;                 // 发动机转速（r/min）
@property (nonatomic, assign) CGFloat           speed;                          // 速度（km/h）
@property (nonatomic, assign) CGFloat           angle;                          // 1 号汽缸点火提前角（°）
@property (nonatomic, assign) CGFloat           incomeAirTempture;              // 进气温度（℃），
@property (nonatomic, assign) CGFloat           airFlux;                        // 空气流量（g/s），
@property (nonatomic, assign) CGFloat           throttlePosition;               // 节气门绝对位置（%），
@property (nonatomic, assign) CGFloat           distanceAfterMIL;               // MIL（故障灯） 亮起后的行驶距离（km）
@property (nonatomic, assign) CGFloat           distanceAfterRepaire;           // 清除故障码后的行驶距离（km），
@property (nonatomic, assign) CGFloat           kiloMileage;                    // 里程（km），
@property (nonatomic, assign) CGFloat           flueInput;                      // 燃油量输入（%）
@property (nonatomic, assign) CGFloat           atmosphere;                     // 大气压力（kPa），
@property (nonatomic, assign) CGFloat           voltageOfControl;               // 控制模块电压（V）
@property (nonatomic, assign) CGFloat           oilWearOfInstant;               // 瞬时油耗（ml/s），
@property (nonatomic, assign) CGFloat           oilWearPer100;                  // 瞬时百公里油耗（l/100km），,
@property (nonatomic, assign) CGFloat           oilWear;                        // 油耗(l/h),
@property (nonatomic, assign) CGFloat           oilMass;                        // 油量（%）,
@property (nonatomic, assign) CGFloat           voltageOfBattery;               // 电瓶电压（V)

@end

// =========================================================================================================
@interface KKModelVehicleRealtimeData : NSObject

@property (nonatomic, assign) CGFloat           oilWearOfInstant;               // 瞬时油耗（ml/s），
@property (nonatomic, assign) CGFloat           oilWearPer100;                  // 瞬时百公里油耗（l/100km），,
@property (nonatomic, assign) CGFloat           oilWear;                        // 油耗(l/h),
@property (nonatomic, assign) CGFloat           oilMass;                        // 油量（%）,
@property (nonatomic, assign) CGFloat           kiloMileage;                    // 里程（km），
@property (nonatomic, assign) CGFloat           engineTempture;                 // 发动机水温（℃），
@property (nonatomic, assign) CGFloat           voltageOfBattery;               // 电瓶电压（V)
@property (nonatomic, assign) CGFloat           speed;                          // 速度 （km/h）

@end

// =========================================================================================================
@interface KKModelLastKnowRealtimeData : NSObject

@property (nonatomic, retain) KKModelVehicleRealtimeData *realtimeData;
@property (nonatomic, retain) NSDate *date;

@end

// =========================================================================================================
@interface KKBLEEngine : KKBLECore
{
    NSMutableString         *_buffer;
    NSTimeInterval          _autoQueryTimeInterval;
    BOOL                    _autoQuery;         // if set defaultPeripheralUUID, when disconnect, it will auto scan and connect
    NSTimer                 *_autoQueryTimer;
}
@property (nonatomic, retain) id<KKBLEEngineDelegate>  bleEngineDelegate;
@property (nonatomic, retain) KKModelLastKnowRealtimeData *lastRealtimeData;        // 最后一次产生的实数据


// Function:    手机端发送OBD标定请求：##OBD:X\r\n
// Note:        OBD端回复：##OBD OK\r\n
// Params:      其中X是OBD标定的协议 有1 2 3 4 5 6 7 8 9 A B C D E F
- (void)setOBDProtocol:(NSString *)aProtocol;

// Function:    手机端设定OBD数据上传时间间隔：##TIM:X\r\n
// Note:        OBD端回复：##TIM OK\r\n
// Params:      其中X为时间间隔（单位为秒），如果不设定，默认值为30秒
- (void)setOBDUploadTimeInterval:(NSTimeInterval)aInterval;

// Function:    手机端请求获取所有数据：##DAT\r\n
// Note:        OBD端回复：##DAT:发动机负荷（%），发动机水温（℃），短时燃油修正（%），长时燃油修正（%）, 进气歧管绝对压力（kPa）, 发动机转速（r/min），速度（km/h），1 号汽缸点火提前角（°）, 进气温度（℃），空气流量（g/s），节气门绝对位置（%），MIL（故障灯） 亮起后的行驶距离（km），清除故障码后的行驶距离（km），里程（km），燃油量输入（%），大气压力（kPa），控制模块电压（V），瞬时油耗（ml/s），瞬时百公里油耗（l/100km），,油耗(l/h), 油量（%）,电瓶电压（V）\r\n
// 如果里面有某些数据车辆不支持的话，回复N/A;
- (void)getVehicleAllData;

// Function:    手机端请求获取部分数据：##PID：M,X1,…,Xn\r\n
// Note:        OBD端回复：##PID:Y1,…,Yn\r\n
// Params:      以nil结尾
//              其中 M表示包含多少个PID; 其中X是OBD标定PID，例如：0109; Y是执行OBD指令后返回的直接结果（请求的PID车辆不支持的话回复N/A）。注：PID定义
//              http://en.wikipedia.org/wiki/OBD-II_PIDs
//              Yi是Xi相应的数据
//              例如想获取车速和转速: ##DATA:2,010C,010D\r\n返回##PID:768，11\r\n;
//              如果车速这个PID车辆不支持: ##PID:2,010C,010D\r\n返回##DATA:768，N/A\r\n;
- (void)getVehicleDataWithPID:(NSString *)aPid1, ...;

// Function:    手机端请求实时显示数据（）: ##RTD\r\n
// Note:        OBD端回复：##瞬时油耗（ml/s），百公里油耗（l/100km）,油耗(l/h), 油量（%）,里程（km），发动机水温（℃），电瓶电压（V）\r\n
//              ,油耗(l/h)，油量（%）有些车辆也不支持，则回复N/A;
- (void)getVehicleRealtimeData;

// Function:    手机端请求VIN码：##VIN\r\n
// Note:        OBD端回复：##VIN: LFPH4ABC071A21524\r\n
- (void)getVehicleVin;

// Function:    里程校正指令：##MIL:XXX\r\n
// Note:        回复：##MIL OK\r\n
//              (需要绑定时把车辆的当前里程告诉OBD)
- (void)setMileage:(CGFloat)aCurrentMile;

// Function:    清除故障码：##CLR\r\n
// Note:        回复：##CLR OK\r\n
- (void)clearFetal;

// Function:    自动查询实时数据
// Note:
- (void)setAutoQueryRealtimeData:(BOOL)on timeInterval:(NSTimeInterval)interval;

@end
