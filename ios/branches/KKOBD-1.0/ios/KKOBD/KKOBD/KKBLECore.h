//
//  KKBLECore.h
//  KKOBD
//
//  Created by codeshu on 9/17/13.
//  Copyright (c) 2013 sgq. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <CoreBluetooth/CoreBluetooth.h>

typedef enum {
    OBDManufacturer_Unknow = 0,
    OBDManufacturer_Old,
    OBDManufacturer_QiQu
}OBDManufacturer;

typedef NS_ENUM(NSInteger, KKBLE_STATUS)
{
    KKBLE_STATUS_DISCONNECTED = 0,
    KKBLE_STATUS_FAIL_TO_CONNECT,
    KKBLE_STATUS_CONNECTED
};

@interface CBPeripheral(KKAdditional)
@property (nonatomic, copy) NSString *systemId;
@end

// =========================================================================================================
@protocol KKBLECoreDelegate <NSObject>
@required
// revoke caused by scanPeripheral
- (void)didDiscoverPeripheral:(CBPeripheral *)aPeripheral RSSI:(NSNumber *)RSSI;

@optional
// after power on the mobile, will revoke it.
// NOTE: all the operation must be implemented after this callback
- (void)mobileSupportBLE:(BOOL)aSupported;

// after received data, will revoke it
- (void)didReadData:(NSData*)aData error:(NSError *)aError;

// after write data to peripheral
- (void)didWriteData:(NSData *)aData error:(NSError *)aError;

// revoke cause by connectPeripheral
- (void)didConnectPeripheral:(CBPeripheral *)aPeripheral state:(KKBLE_STATUS)aState error:(NSError *)aError;

// when the service assigned by writeServiceUUID+readServiceUUID is found or not, will be revoked
- (void)didDiscoverServiceForPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)aError;

// when the characteristics assigned by writeServiceUUID+readServiceUUID is found or not, will be revoked
// @aReadOrWrite:  0:read 1:write
- (void)didDiscoverCharacteristicsForReadWrite:(NSInteger)aReadOrWrite error:(NSError *)error;

// after scan peripheral finished
- (void)didScanFinishWithResult:(BOOL)success;

// result of scan
- (void)didScanFinishWithPeripherals:(NSMutableArray *)perArr;

// will be revoked after the system Id has read, system Id will transmit to mac address format
- (void)didGetSystemId:(NSString *)aSystemId forPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error;


@end

// =========================================================================================================
@interface KKBLECore : NSObject <CBCentralManagerDelegate, CBPeripheralDelegate>
{
    CBCentralManager    *_manager;
    CBPeripheral        *_activePeripheral;
    CBCharacteristic    *_writeCharacteristic;
    CBCharacteristic    *_readCharacteristic;
    NSMutableArray      *_scanPeripherals;

    BOOL                _supportBLE;
    
    NSTimer             *_scanTimer;
    
}

@property(nonatomic, retain) id<KKBLECoreDelegate>  bleDelegate;
@property(nonatomic, copy) NSString                 *defaultPeripheralUUID;
@property(nonatomic, copy) NSString                 *readServiceUUID;
@property(nonatomic, copy) NSString                 *readCharacteristicUUID;
@property(nonatomic, copy) NSString                 *writeServiceUUID;
@property(nonatomic, copy) NSString                 *writeCharacteristicUUID;
@property (nonatomic, assign) OBDManufacturer       obdManufacturer;


- (BOOL)supportBLE;
- (void)scanPeripherals:(NSTimeInterval)aTimeInterval;
- (void)stopScan;
- (void)connectPeripheral:(CBPeripheral *)aPeripheral;
- (void)disConnectPeripheral:(CBPeripheral *)aPeripheral;
- (void)disConnectActivePeripheral;
- (void)writeData:(NSData *)aData;
- (void)writeString:(NSString *)aData;




@end


