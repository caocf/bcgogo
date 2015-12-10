//
//  KKBLEEngine.m
//  KKOBD
//
//  Created by codeshu on 9/18/13.
//  Copyright (c) 2013 sgq. All rights reserved.
//

#import <objc/runtime.h>

#import "KKBLEEngine.h"


// =========================================================================================================
@implementation KKModelVehicleRunData

@end

@implementation KKModelVehicleRealtimeData

@end

@implementation KKModelLastKnowRealtimeData

- (void)dealloc
{
    self.realtimeData = nil;
    self.date = nil;
    
    [super dealloc];
}

@end

// =========================================================================================================
@interface KKBLEEngine (_private)

- (BOOL)handlePackage:(NSString *)aPackage;

- (id)handleDAT:(NSString *)aData classname:(NSString *)aClassname;

-(void)autoQueryTimeout:(NSTimer *)timer;


@end


@implementation KKBLEEngine


const NSString *KKOBDResponseHeader = @"##";
const NSString *KKOBDResponseEnd = @"\r\n";
const NSString *KKOBDDataDelimate = @",";
const NSString *KKOBDDataUnvalide = @"N/A";

const NSString *KKOBDCmdOBD = @"##OBD";
const NSString *KKOBDCmdTIM = @"##TIM";
const NSString *KKOBDCmdDATA = @"##DAT";
const NSString *KKOBDCmdPID = @"##PID";
const NSString *KKOBDCmdRTD = @"##RTD";
const NSString *KKOBDCmdVIN = @"##VIN";
const NSString *KKOBDCmdMIL = @"##MIL";
const NSString *KKOBDCmdCLR = @"##CLR";
const NSString *KKOBDCmdDTC = @"##DTC";

- (id)init
{
    self = [super init];
    if (nil == self)
        return self;
    
    _dtcReplaceFromArray = [[NSArray alloc] initWithObjects:@":",@";",@"<",@"=",@">",@"?", nil];
    _dtcReplaceToArray =   [[NSArray alloc] initWithObjects:@"A",@"B",@"C",@"D",@"E",@"F", nil];
    
    return self;
}

// 手机端发送OBD标定请求：##OBD:X\r\n
// OBD端回复：##OBD OK\r\n
// 其中X是OBD标定的协议 有1 2 3 4 5 6 7 8 9 A B C D E F
- (void)setOBDProtocol:(NSString *)aProtocol
{
    NSString *cmd = [NSString stringWithFormat:@"%@:%@%@", KKOBDCmdOBD, aProtocol, KKOBDResponseEnd];
    [self writeString:cmd];
}

// 手机端设定OBD数据上传时间间隔：##TIM:X\r\n
// OBD端回复：##TIM OK\r\n
// 其中X为时间间隔（单位为秒），如果不设定，默认值为30秒
- (void)setOBDUploadTimeInterval:(NSTimeInterval)aInterval
{
    NSString *cmd = [NSString stringWithFormat:@"%@:%.f%@", KKOBDCmdTIM, aInterval,KKOBDResponseEnd];
    [self writeString:cmd];
}

// 手机端请求获取所有数据：##DAT\r\n
// OBD端回复：##DAT:发动机负荷（%），发动机水温（℃），短时燃油修正（%），长时燃油修正（%）, 进气歧管绝对压力（kPa）, 发动机转速（r/min），速度（km/h），1 号汽缸点火提前角（°）, 进气温度（℃），空气流量（g/s），节气门绝对位置（%），MIL（故障灯） 亮起后的行驶距离（km），清除故障码后的行驶距离（km），里程（km），燃油量输入（%），大气压力（kPa），控制模块电压（V），瞬时油耗（ml/s），瞬时百公里油耗（l/100km），,油耗(l/h), 油量（%）,电瓶电压（V）\r\n
// 如果里面有某些数据车辆不支持的话，回复N/A;
- (void)getVehicleAllData
{ 
    NSString *cmd = [NSString stringWithFormat:@"%@%@", KKOBDCmdDATA, KKOBDResponseEnd];
    [self writeString:cmd];
}

// Function:    手机端请求获取部分数据：##PID：M,X1,…,Xn\r\n
// Note:        OBD端回复：##PID:Y1,…,Yn\r\n
// Params:      以nil结尾
//              其中 M表示包含多少个PID; 其中X是OBD标定PID，例如：0109; Y是执行OBD指令后返回的直接结果（请求的PID车辆不支持的话回复N/A）。注：PID定义
//              http://en.wikipedia.org/wiki/OBD-II_PIDs
//              Yi是Xi相应的数据
//              例如想获取车速和转速: ##DATA:2,010C,010D\r\n返回##PID:768，11\r\n;
//              如果车速这个PID车辆不支持: ##PID:2,010C,010D\r\n返回##DATA:768，N/A\r\n;
- (void)getVehicleDataWithPID:(NSString *)aPid1, ...
{
    if ([aPid1 length] == 0)
        return;
    
    NSMutableString *cmd = [[NSMutableString alloc] initWithCapacity:1024];
    va_list args;
    va_start(args, aPid1);
    [cmd appendString:aPid1];
    NSInteger params = 1;
    while (1) {
        NSString *pid = va_arg(args, NSString*);
        if ([pid length] == 0)
            break;
        [cmd appendString:(NSString *)KKOBDDataDelimate];
        [cmd appendString:pid];
        params++;
    }
    va_end(args);
    
    NSString *command = [NSString stringWithFormat:@"%@:%d,%@%@", KKOBDCmdPID, params, cmd, KKOBDResponseEnd];
    [cmd release];
    
    [self writeString:command];
}

// Function:    手机端请求实时显示数据（）: ##RTD\r\n
// Note:        OBD端回复：##瞬时油耗（ml/s），百公里油耗（l/100km）,油耗(l/h), 油量（%）,里程（km），发动机水温（℃），电瓶电压（V）\r\n
//              ,油耗(l/h)，油量（%）有些车辆也不支持，则回复N/A;
- (void)getVehicleRealtimeData
{
    NSString *cmd = [NSString stringWithFormat:@"%@%@", KKOBDCmdRTD, KKOBDResponseEnd];
    [self writeString:cmd];
}

// Function:    手机端请求VIN码：##VIN\r\n
// Note:        OBD端回复：##VIN: LFPH4ABC071A21524\r\n
- (void)getVehicleVin
{
    NSString *cmd = [NSString stringWithFormat:@"%@%@", KKOBDCmdVIN, KKOBDResponseEnd];
    NSLog(@"getVehicleVin: -------%@----Data:%@", KKOBDCmdVIN,[KKOBDCmdVIN dataUsingEncoding:NSUTF8StringEncoding]);
    [self writeString:cmd];
}

-(void) getVehicleDTC
{
    NSString *cmd = [NSString stringWithFormat:@"%@%@", KKOBDCmdDTC, KKOBDResponseEnd];
    NSLog(@"getDTC:---%@",cmd);
    [self writeString:cmd];
}

// Function:    里程校正指令：##MIL:XXX\r\n
// Note:        回复：##MIL OK\r\n
//              (需要绑定时把车辆的当前里程告诉OBD)
- (void)setMileage:(CGFloat)aCurrentMile
{
    NSString *cmd = [NSString stringWithFormat:@"%@:%.f%@", KKOBDCmdMIL, aCurrentMile, KKOBDResponseEnd];
    [self writeString:cmd];
}

// Function:    清除故障码：##CLR\r\n
// Note:        回复：##CLR OK\r\n
- (void)clearFetal
{
    NSString *cmd = [NSString stringWithFormat:@"%@%@", KKOBDCmdCLR, KKOBDResponseEnd];
    [self writeString:cmd];
}


// Function:    自动查询实时数据
// Note:
- (void)setAutoQueryRealtimeData:(BOOL)on timeInterval:(NSTimeInterval)interval
{
    if (on) {
        if (_autoQueryTimer)
            [_autoQueryTimer invalidate];
        _autoQueryTimer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(autoQueryTimeout:) userInfo:nil repeats:YES];
    }
    else {
        [_autoQueryTimer invalidate];
        _autoQueryTimer = nil;
    }
    _autoQueryTimeInterval = interval;
}

-(void)autoQueryTimeout:(NSTimer *)timer
{
    [self getVehicleRealtimeData];
}

// overide
- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    [super peripheral:peripheral didUpdateValueForCharacteristic:characteristic error:error];
    
    NSString *value = [[[NSString alloc] initWithData:characteristic.value encoding:NSUTF8StringEncoding] autorelease];
    if (_buffer == nil)
        _buffer = [[NSMutableString alloc] initWithCapacity:1024];
    if ([value length] > 0)
    {
        NSRange range = [value rangeOfString:(NSString *)KKOBDResponseHeader];
        if(range.length > 0 && range.location == 0)
        {
            [_buffer release];
            _buffer = nil;
            _buffer = [[NSMutableString alloc] init];
        }
            
        [_buffer appendString:value];
    }
    else {
        NSData *valueData = characteristic.value;
        if ([valueData length] > [KKOBDCmdVIN length]+1) {      // maybe can't get Vin
            NSRange cmdRange = {0, [KKOBDCmdVIN length]+1};
            NSData *cmdData = [valueData subdataWithRange:cmdRange];
            value = [[NSString alloc] initWithData:cmdData encoding:NSUTF8StringEncoding];
            cmdRange = [value rangeOfString:(NSString *)KKOBDCmdVIN];
            if (cmdRange.length == 0)   // not found ##VIN, here only specially handle VIN, others will be ignored
            {
                value = nil;
            }
            else {
                value = [NSString stringWithFormat:@"%@%@", value, KKOBDResponseEnd];       // simulate a package
                [_buffer appendString:value];
            }
        }
    }
    NSRange range = [_buffer rangeOfString:(NSString *)KKOBDResponseEnd];
    if (range.length > 0) {
        NSString *package = [_buffer substringToIndex:range.location];
        [self handlePackage:package];
        NSString *remain = [_buffer substringFromIndex:range.location+range.length];
        if ([remain length] > 0) {
            NSString *swap = [[NSString alloc] initWithString:remain];
            [_buffer release];
            _buffer = [[NSMutableString alloc] initWithCapacity:1024];
            [_buffer appendString:swap];
            [swap release];
        }
        else {
            [_buffer release];
            _buffer = nil;
        }
    }
//    [value release];
}

#define __Macro_GetPureData__   do { \
    NSInteger start = range.location+range.length+1;  \
    NSRange dataRange = {start, aPackage.length-start}; \
    data = [aPackage substringWithRange:dataRange]; \
} while(0)

- (BOOL)handlePackage:(NSString *)aPackage
{
    if ([aPackage length] == 0)
        return NO;
    
    // handle DATA
    NSString *data = nil;
    NSRange range = [aPackage rangeOfString:@"##DTA"];  //[aPackage rangeOfString:(NSString *)KKOBDCmdDATA];
    if (range.length > 0) {
        __Macro_GetPureData__;
        KKModelVehicleRunData *runData = [self handleDAT:data classname:@"KKModelVehicleRunData"];
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleAllDataResp:)])
            [self.bleEngineDelegate vehicleAllDataResp:runData];
        return YES;
    }
    
    // handle OBD protocol set
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdOBD];
    if (range.length > 0) {
        __Macro_GetPureData__;
        BOOL suc = NO;
        if ([data isEqualToString:@"OK"])
            suc = YES;
        if ([self.bleEngineDelegate respondsToSelector:@selector(setOBDProtocolResp:)]) {
            NSNumber *retObj = [NSNumber numberWithBool:suc];
            [self.bleEngineDelegate setOBDProtocolResp:retObj];
        }
        return YES;
    }
 
    // handle TIM set
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdTIM];
    if (range.length > 0) {
        __Macro_GetPureData__;
        BOOL suc = NO;
        if ([data isEqualToString:@"OK"])
            suc = YES;
        if ([self.bleEngineDelegate respondsToSelector:@selector(setOBDTimeIntervalResp:)]) {
            NSNumber *retObj = [NSNumber numberWithBool:suc];
            [self.bleEngineDelegate setOBDTimeIntervalResp:retObj];
        }
        return YES;
    }
    
    // handle PID
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdPID];  //[aPackage rangeOfString:(NSString *)KKOBDCmdDATA];
    if (range.length > 0) {
        __Macro_GetPureData__;
        NSArray *arr = [data componentsSeparatedByString:(NSString *)KKOBDDataDelimate];
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleDataWithPIDResp:)])
            [self.bleEngineDelegate vehicleDataWithPIDResp:arr];
        return YES;
    }

    // handle RTD
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdRTD];  //[aPackage rangeOfString:(NSString *)KKOBDCmdDATA];
    if (range.length > 0) {
        __Macro_GetPureData__;
        KKModelVehicleRealtimeData *runData = [self handleDAT:data classname:@"KKModelVehicleRealtimeData"];
        if (runData) {
            self.lastRealtimeData = nil;
            self.lastRealtimeData = [[[KKModelLastKnowRealtimeData alloc] init] autorelease];
            self.lastRealtimeData.realtimeData = runData;
            self.lastRealtimeData.date = [NSDate date];
        }
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleRealtimeDataResp:)])
            [self.bleEngineDelegate vehicleRealtimeDataResp:runData];
        return YES;
    }
 
    // handle VIN
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdVIN];
    if (range.length > 0) {
        __Macro_GetPureData__;
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleVinResp:)]) {
            [self.bleEngineDelegate vehicleVinResp:data];
        }
        return YES;
    }

    // handle MIL set
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdMIL];
    if (range.length > 0) {
        __Macro_GetPureData__;
        BOOL suc = NO;
//        if ([data isEqualToString:@"OK"])
//            suc = YES;
        NSRange sucRange = [data rangeOfString:@"OK"];
        if (sucRange.location != NSNotFound)
            suc = YES;
        
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleSetMileageResp:)]) {
            NSNumber *retObj = [NSNumber numberWithBool:suc];
            [self.bleEngineDelegate vehicleSetMileageResp:retObj];
        }
        return YES;
    }
   
    // handle CLR
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdCLR];
    if (range.length > 0) {
        __Macro_GetPureData__;
        BOOL suc = NO;
        if ([data isEqualToString:@"OK"])
            suc = YES;
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleClearFetalResp:)]) {
            NSNumber *retObj = [NSNumber numberWithBool:suc];
            [self.bleEngineDelegate vehicleClearFetalResp:retObj];
        }
        return YES;
    }

    // handle DTC
    range = [aPackage rangeOfString:(NSString *)KKOBDCmdDTC];
    if (range.length > 0) {
        __Macro_GetPureData__;
        
        for(int i=0;i<[_dtcReplaceFromArray count];i++)
        {
            data = [data stringByReplacingOccurrencesOfString:[_dtcReplaceFromArray objectAtIndex:i] withString:[_dtcReplaceToArray objectAtIndex:i]];
        }
        
        NSMutableArray *arr = [NSMutableArray arrayWithArray:[data componentsSeparatedByString:(NSString *)KKOBDDataDelimate]];
        [arr removeObject:KKOBDDataUnvalide];
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleDTCAllReport:)]) {
            [self.bleEngineDelegate vehicleDTCAllReport:data];
        }
        if ([self.bleEngineDelegate respondsToSelector:@selector(vehicleDTCReport:)]) {
            [self.bleEngineDelegate vehicleDTCReport:arr];
        }
        return YES;
    }

    return NO;
}

- (id)handleDAT:(NSString *)aData classname:(NSString *)aClassname;
{
    if ([aData length] == 0 || [aClassname length] == 0)
        return nil;
    
    Class cls = NSClassFromString(aClassname);
    NSArray *arr = [aData componentsSeparatedByString:(NSString *)KKOBDDataDelimate];
    unsigned int outCount = 0;
    objc_property_t *properties = class_copyPropertyList(cls, &outCount);
    if (outCount > [arr count])
        outCount = [arr count];
        
    id runData = [[cls alloc] init];
    for (NSInteger i=0; i<outCount; i++) {
        objc_property_t property = properties[i];
        const char *cszName = property_getName(property);
        NSString *keyName = [NSString stringWithCString:cszName encoding:NSUTF8StringEncoding];
        
        NSString *valueStr = [arr objectAtIndex:i];
        if (YES == [valueStr isEqualToString:(NSString *)KKOBDDataUnvalide]) {
            NSNumber *value = [NSNumber numberWithInt:KKOBDDataNA];
            [runData setValue:value forKey:keyName];
        }
        else {
            NSNumberFormatter *numberFormatter = [[NSNumberFormatter alloc] init];
            [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
            NSNumber *value = [numberFormatter numberFromString:(NSString *)valueStr];
            [numberFormatter release];
            if(value)
                [runData setValue:value forKey:keyName];
        }
    }
    free(properties);
    return [runData autorelease];
}

- (void)dealloc
{
    self.bleEngineDelegate = nil;
    self.lastRealtimeData = nil;
   
    [_autoQueryTimer invalidate];
    _autoQueryTimer = nil;
    
    [_buffer release];
    _buffer = nil;
    
    [_dtcReplaceFromArray release],_dtcReplaceFromArray=nil;
    [_dtcReplaceToArray release],_dtcReplaceToArray=nil;
    
    [super dealloc];
}

@end
