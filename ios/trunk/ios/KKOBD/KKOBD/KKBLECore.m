//
//  KKBLECore.m
//  KKOBD
//
//  Created by codeshu on 9/17/13.
//  Copyright (c) 2013 sgq. All rights reserved.
//

#import <objc/runtime.h>
#import "KKBLECore.h"

// ===================================================================================================
const NSString *KKSystemIdKey = @"KKCBSystemId";
@implementation CBPeripheral(KKAdditional)

- (void)setSystemId:(NSString *)aSystemId
{
    objc_setAssociatedObject(self, KKSystemIdKey,  aSystemId, OBJC_ASSOCIATION_COPY);
    
}

- (NSString *)systemId
{
   return  objc_getAssociatedObject(self, KKSystemIdKey);
}

@end

// ===================================================================================================
@interface KKBLECore(_private)
- (void)scanTimeout:(NSTimer *)aTimer;
// because in iOS5 iOS6 is not supported peripheral.identifer
- (NSString *)peripheralUUID:(CBPeripheral *)aPeripheral;

@end

@implementation KKBLECore

NSString *KKOBDDeviceInfoServiceUUID = @"180a";
NSString *KKOBDSystemIdCharacteristicUUID = @"2a23";


- (id)init
{
    self = [super init];
    if (nil == self)
        return self;
    _manager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
    _scanPeripherals = [[NSMutableArray alloc] initWithCapacity:10];
    return self;
}

- (BOOL)supportBLE
{
    return _supportBLE;
}

- (void)scanPeripherals:(NSTimeInterval)aTimeInterval
{
    [_scanPeripherals removeAllObjects];
    
// Scans for any peripheral
//    CBUUID *uid = [CBUUID UUIDWithString:self.writeServiceUUID];
//    NSLog(@"uid=%@", uid);
//    CBUUID *uid1 = [CBUUID UUIDWithString:self.readServiceUUID];
//    NSArray *serviceUUID = @[uid, uid1];
    NSDictionary *options = @{CBCentralManagerScanOptionAllowDuplicatesKey:@NO};
    [_manager scanForPeripheralsWithServices:nil
                                         options:options];
    if (_scanTimer && [_scanTimer isValid])
        [_scanTimer invalidate];
    _scanTimer = [NSTimer scheduledTimerWithTimeInterval:aTimeInterval target:self selector:@selector(scanTimeout:) userInfo:nil repeats:NO];
}

- (void)scanTimeout:(NSTimer *)aTimer;
{
    [self stopScan];
    
    if (self.bleDelegate && [self.bleDelegate respondsToSelector:@selector(didScanFinishWithResult:)])
    {
        [self.bleDelegate didScanFinishWithResult:([_scanPeripherals count]> 0 ? YES : NO)];
    }
    
    if (self.bleDelegate && [self.bleDelegate respondsToSelector:@selector(didScanFinishWithPeripherals:)])
        [self.bleDelegate didScanFinishWithPeripherals:_scanPeripherals];
    
}

- (void)stopScan
{
    if ([_scanTimer isValid])
        [_scanTimer invalidate];
    _scanTimer = nil;
    
    [_manager stopScan];
}

- (void)connectPeripheral:(CBPeripheral *)aPeripheral
{
    [_manager connectPeripheral:aPeripheral options:nil];
}

- (void)disConnectPeripheral:(CBPeripheral *)aPeripheral
{
    [_manager cancelPeripheralConnection:aPeripheral];
}

- (void)disConnectActivePeripheral
{
    if (_activePeripheral)
        [self disConnectPeripheral:_activePeripheral];
}

- (void)writeData:(NSData *)aData
{
    if ([aData length] == 0)
        return;
    
    if (NO == [_activePeripheral isConnected]) {
        // auto scan and auto connect
        if ([_defaultPeripheralUUID length] > 0)
            [self scanPeripherals:10];
        return;
    }
    
    [_activePeripheral writeValue:aData forCharacteristic:_writeCharacteristic type:CBCharacteristicWriteWithResponse];
}

- (void)writeString:(NSString *)aData
{
    NSData *valData = [aData dataUsingEncoding:NSUTF8StringEncoding];
    [self writeData:valData];
}

- (NSString *)peripheralUUID:(CBPeripheral *)aPeripheral
{
    NSString *ret = nil;
    @try {
        CFUUIDRef uuid = aPeripheral.UUID;
        if (uuid == nil)
            return ret;
        CFStringRef uuidStrRef = CFUUIDCreateString(kCFAllocatorSystemDefault, uuid);
        ret = [NSString stringWithFormat:@"%@", (NSString*)uuidStrRef];
        CFRelease(uuidStrRef);
    }
    @catch (NSException *exception) {
    }

    return ret;
}

#pragma mark -
#pragma mark CBCentralManagerDelegate
- (void)centralManagerDidUpdateState:(CBCentralManager *)central
{
    _supportBLE = NO;
    switch (central.state) {
        case CBCentralManagerStatePoweredOn: {
            _supportBLE = YES;
            break;
        }
        case CBCentralManagerStateUnsupported:
            _supportBLE = NO;
            break;
            
        default:
            NSLog(@"Central Manager did change state");
            break;
    }
    if ([self.bleDelegate respondsToSelector:@selector(mobileSupportBLE:)])
        [self.bleDelegate mobileSupportBLE:_supportBLE];
}

- (void)centralManager:(CBCentralManager *)central
 didDiscoverPeripheral:(CBPeripheral *)peripheral
     advertisementData:(NSDictionary *)advertisementData
                  RSSI:(NSNumber *)RSSI
{
    //[self stopScan];
    NSLog(@"a peripheral has discoverd: %@", peripheral);
    NSInteger __block pos = 0;
    NSString *puuid = [self peripheralUUID:peripheral];
    [_scanPeripherals enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        CBPeripheral *device = (CBPeripheral *)obj;
        NSString *duuid = [self peripheralUUID:device];
        if (puuid == nil) {
            pos = -1;
            return;
        }
        if ([duuid isEqual:puuid]) {
            if (device == peripheral) { // same device and same instance, will abandon
                pos = -1;
                return;
            }
            // not same instance, will replace
            [_scanPeripherals removeObject:obj];
            pos = idx;
            return;
        }
    } ];
    
//    if (puuid == nil) {
//        return;
//    }
    if (pos < 0)        // abandon same instance
        return;
    
    [_scanPeripherals insertObject:peripheral atIndex:pos];
    if ([self.bleDelegate respondsToSelector:@selector(didDiscoverPeripheral:RSSI:)])
        [self.bleDelegate didDiscoverPeripheral:peripheral RSSI:RSSI];
    // auto connect
    if ([_defaultPeripheralUUID length] > 0
        && [[self peripheralUUID:peripheral] isEqualToString:_defaultPeripheralUUID]
        && NO == [_activePeripheral isConnected]) {
        [self connectPeripheral:peripheral];
    }
}


- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral
{
    NSLog(@"connect peripheral %@ succeed", peripheral);
    
    [_activePeripheral release];
    _activePeripheral = [peripheral retain];
    // Sets the peripheral delegate
    [_activePeripheral setDelegate:self];
    
    if ([self.bleDelegate respondsToSelector:@selector(didConnectPeripheral:state:error:)])
        [self.bleDelegate didConnectPeripheral:peripheral state:KKBLE_STATUS_CONNECTED error:nil];

    // Asks the peripheral to discover the service
    [_activePeripheral discoverServices:@[ [CBUUID UUIDWithString:KKOBDDeviceInfoServiceUUID], [CBUUID UUIDWithString:self.readServiceUUID],  [CBUUID UUIDWithString:self.writeServiceUUID] ]];
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    if (error) {
        NSLog(@"disconnect peripheral failed %@, error is %@", peripheral, error);
        if (YES == [peripheral isConnected])
            return;
    }
    
    NSLog(@"disconnect peripheral %@ succed", peripheral);
    [_activePeripheral release];
    _activePeripheral = nil;
    
    if ([self.bleDelegate respondsToSelector:@selector(didConnectPeripheral:state:error:)])
        [self.bleDelegate didConnectPeripheral:peripheral state:KKBLE_STATUS_DISCONNECTED error:error];
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    NSLog(@"connect peripheral %@ failed, error is %@", peripheral, error);
    
    if ([self.bleDelegate respondsToSelector:@selector(didConnectPeripheral:state:error:)])
        [self.bleDelegate didConnectPeripheral:peripheral state:KKBLE_STATUS_FAIL_TO_CONNECT error:error];
}

#pragma mark -
#pragma mark CBPeripheralDelegate
- (void)peripheral:(CBPeripheral *)aPeripheral didDiscoverServices:(NSError *)error
{
    if (error) {
        NSLog(@"Error discovering service:%@", [error localizedDescription]);
        if ([self.bleDelegate respondsToSelector:@selector(didDiscoverServiceForPeripheral:error:)])
            [self.bleDelegate didDiscoverServiceForPeripheral:aPeripheral error:error];
        return;
    }
    
    for (CBService *service in aPeripheral.services) {
        NSLog(@"Service found with UUID: %@",service.UUID);
        // Discovers the characteristics for a given service
        if ([service.UUID isEqual:[CBUUID UUIDWithString:self.writeServiceUUID]] ||
            [service.UUID isEqual:[CBUUID UUIDWithString:self.readServiceUUID]]) {
            [_activePeripheral discoverCharacteristics:@[[CBUUID UUIDWithString:self.writeCharacteristicUUID], [CBUUID UUIDWithString:self.readCharacteristicUUID]] forService:service];
        }
        if ([service.UUID isEqual:[CBUUID UUIDWithString:KKOBDDeviceInfoServiceUUID]]) {  // device information
            [_activePeripheral discoverCharacteristics:@[[CBUUID UUIDWithString:KKOBDSystemIdCharacteristicUUID]] forService:service];
        }
    }
    if ([self.bleDelegate respondsToSelector:@selector(didDiscoverServiceForPeripheral:error:)])
        [self.bleDelegate didDiscoverServiceForPeripheral:aPeripheral error:error];
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error
{
    if (error) {
        NSLog(@"Error discovering characteristic:%@", [error localizedDescription]);
        return;
    }
    
    NSLog(@"service characteristics:%@", service.characteristics);
    if ([service.UUID isEqual:[CBUUID UUIDWithString:self.writeServiceUUID]] ||
        [service.UUID isEqual:[CBUUID UUIDWithString:self.readServiceUUID]]) {
        for (CBCharacteristic *characteristic in service.characteristics) {
            if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:self.writeCharacteristicUUID]]) {
                NSLog(@"write service uuid:%@, characteristic uuid:%@", characteristic.service.UUID, characteristic.UUID);
                [_writeCharacteristic release];
                _writeCharacteristic= [characteristic retain];
                if ([self.bleDelegate respondsToSelector:@selector(didDiscoverCharacteristicsForReadWrite:error:)])
                    [self.bleDelegate didDiscoverCharacteristicsForReadWrite:1 error:error];
                continue;
            }
            if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:self.readCharacteristicUUID]]) {
                NSLog(@"read service uuid:%@, characteristic uuid:%@", characteristic.service.UUID, characteristic.UUID);
                [_readCharacteristic release];
                _readCharacteristic = [characteristic retain];
                [peripheral setNotifyValue:YES forCharacteristic:characteristic];
                if ([self.bleDelegate respondsToSelector:@selector(didDiscoverCharacteristicsForReadWrite:error:)])
                    [self.bleDelegate didDiscoverCharacteristicsForReadWrite:0 error:error];
                continue;
            }
        }
    }
    if ([service.UUID isEqual:[CBUUID UUIDWithString:KKOBDDeviceInfoServiceUUID]]) {
        for (CBCharacteristic *characteristic in service.characteristics) {
            if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:KKOBDSystemIdCharacteristicUUID]]) {  // system id
                [peripheral readValueForCharacteristic:characteristic];
                break;
            }
        }
    }
}

- (void)peripheral:(CBPeripheral *)peripheral
didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic
             error:(NSError *)error {
    if (error) {
        NSLog(@"didUpdateNotificationStateForCharacteristic error state:%@, character uuid=%@", error.localizedDescription, characteristic.UUID);
        return;
    }
    
    // Exits if it's not the transfer characteristic
    if (NO == [characteristic.UUID isEqual:[CBUUID UUIDWithString:_readCharacteristicUUID]] &&
        NO == [characteristic.UUID isEqual:[CBUUID UUIDWithString:_writeCharacteristicUUID]]&&
        NO == [characteristic.UUID isEqual:[CBUUID UUIDWithString:KKOBDSystemIdCharacteristicUUID]]) {
        return;
    }
    
    // Notification has started
    if (characteristic.isNotifying) {
        NSLog(@"didUpdateNotificationStateForCharacteristic  isNotifying characteristic uuid: %@",  characteristic.UUID);
        //[peripheral readValueForCharacteristic:characteristic];
        
    } else { // Notification has stopped
        // so disconnect from the peripheral
        NSLog(@"Notification stopped on %@. Disconnecting", characteristic);
        [self disConnectPeripheral:_activePeripheral];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    if (error) {
        NSLog(@"didUpdateValueForCharacteristic error is %@", error);
        if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:KKOBDSystemIdCharacteristicUUID]]) {
            if ([self.bleDelegate respondsToSelector:@selector(didGetSystemId:forPeripheral:error:)])
                [self.bleDelegate didGetSystemId:nil forPeripheral:peripheral error:error];
        }
        else if ([self.bleDelegate respondsToSelector:@selector(didReadData:error:)])
            [self.bleDelegate didReadData:characteristic.value error:error];
        return;
    }
    
    NSString *value = [[NSString alloc] initWithData:characteristic.value encoding:NSUTF8StringEncoding];
    NSLog(@"service uuid is %@, characteristic uuid is %@, read value:%@, read origin data:%@", characteristic.service.UUID, characteristic.UUID, value, characteristic.value);
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:KKOBDSystemIdCharacteristicUUID]]) {
        NSLog(@"system Id value data is %@", characteristic.value);         
        //system id: mac address 3Bytes(LSB)+"0000"(2bytes) + mac address3Bytes(MSB)
        if ([characteristic.value length] < 8) {
            NSLog(@"system Id formate is illeage");
            if ([self.bleDelegate respondsToSelector:@selector(didGetSystemId:forPeripheral:error:)]) {
                NSError *err = [NSError errorWithDomain:@"bluetooth" code:-1 userInfo:nil];
                [self.bleDelegate didGetSystemId:nil forPeripheral:peripheral error:err];
            }
            [value release];
            return;
        }
        
        unsigned char *sysData = (unsigned char *)malloc([characteristic.value length]);
        [characteristic.value getBytes:sysData length:[characteristic.value length]];
        NSString *systemId = [NSString stringWithFormat:@"%02X:%02X:%02X:%02X:%02X:%02X", sysData[0], sysData[1], sysData[2], sysData[5], sysData[6], sysData[7]];
        free(sysData);
        NSLog(@"system Id string:%@", systemId);
        peripheral.systemId = systemId;
        if ([self.bleDelegate respondsToSelector:@selector(didGetSystemId:forPeripheral:error:)])
            [self.bleDelegate didGetSystemId:systemId forPeripheral:peripheral error:nil];
    }
    
    if ([self.bleDelegate respondsToSelector:@selector(didReadData:error:)])
        [self.bleDelegate didReadData:characteristic.value error:error];
    [value release];
}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    if (error) {
        NSLog(@"didWriteValueForCharacteristic error is %@", error);
        if ([self.bleDelegate respondsToSelector:@selector(didWriteData:error:)])
            [self.bleDelegate didWriteData:characteristic.value error:error];
        return;
    }
    
    NSString *value = [[NSString alloc] initWithData:characteristic.value encoding:NSUTF8StringEncoding];
    NSLog(@"service uuid is %@, characteristic uuid is %@, write value:%@", characteristic.service.UUID, characteristic.UUID, value);
    if ([self.bleDelegate respondsToSelector:@selector(didWriteData:error:)])
        [self.bleDelegate didWriteData:characteristic.value error:error];
    [value release];
}



- (void)dealloc
{
    [_manager release];
    _manager = nil;
    
    [_activePeripheral release];
    _activePeripheral = nil;
    
    [_writeCharacteristic release];
    _writeCharacteristic = nil;
    
    [_readCharacteristic release];
    _readCharacteristic = nil;
    
    [_scanPeripherals release];
    _scanPeripherals = nil;
    
    if ([_scanTimer isValid])
        [_scanTimer invalidate];
    _scanTimer = nil;

    self.bleDelegate = nil;
    self.defaultPeripheralUUID = nil;
    self.readCharacteristicUUID = nil;
    self.writeCharacteristicUUID = nil;
    self.writeServiceUUID = nil;
    self.readServiceUUID = nil;    

    [super dealloc];
}
@end
