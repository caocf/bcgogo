//
//  KKBLEManager.m
//  KKOBD
//
//  Created by zhuyc on 13-9-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKBLEManager.h"

static KKBLEManager *_bleManager = nil;

@implementation KKBLEManager

- (id)init
{
    self = [super init];
    if (self)
    {
        KKBLEEngine *engine = [[KKBLEEngine alloc] init];
        engine.bleEngineDelegate = self;
        engine.bleDelegate = self;
        self.engine = engine;
        [engine release];
    }
    return self;
}

+ (KKBLEManager *)shareBleManager
{
    @synchronized(self)
    {
        if (_bleManager == nil)
            _bleManager = [[[KKBLEManager alloc] init] autorelease];
    }
    return _bleManager;
}

#pragma mark -
#pragma mark KKBLECoreDelegate

- (void)didDiscoverPeripheral:(CBPeripheral *)aPeripheral RSSI:(NSNumber *)RSSI
{
    
}

- (void)mobileSupportBLE:(BOOL)aSupported
{
    if (self.bleManagerDelegate && [self.bleManagerDelegate respondsToSelector:@selector(KKBLEManagerSupport:)])
        [self.bleManagerDelegate KKBLEManagerSupport:aSupported];
}

- (void)didScanFinishWithPerioherals:(NSMutableArray *)perArr
{
    if (self.bleManagerDelegate && [self.bleManagerDelegate respondsToSelector:@selector(KKBLEManagerScanFinishedWithResults:)])
        [self.bleManagerDelegate KKBLEManagerScanFinishedWithResults:perArr];
    
}

#pragma mark -
#pragma mark KKBLEEngineDelegate

// @aRetObj: NSNumber object (0:NO 1:YES)
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
    
}

// @aRetObj: NSArray, inside are NSString
- (void)vehicleDataWithPIDResp:(id)aRetObj
{
    
}

// @aRetObj: NSString
- (void)vehicleVinResp:(id)aRetObj
{
    
}

// @aRetObj: NSNumber
- (void)vehicleSetMileageResp:(id)aRetObj
{
    
}

// @aRetObj: NSNumber object (0:NO 1:YES)
- (void)vehicleClearFetalResp:(id)aRetObj
{
    
}

// @aRetObj: NSArray, inside are NSString
- (void)vehicleDTCReport:(id)aRetObj
{
    
}

- (void)dealloc
{
    _bleManager = nil;
    
    [super dealloc];
}
@end
