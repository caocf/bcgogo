//
//  TGDataSingleton.h
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "TGBasicModel.h"
/**
 *  数据单例，用到的全局变量放在这里统一访问，如:当前定位信息、车辆等
 */
@interface TGDataSingleton : NSObject
{
    
}

@property (nonatomic, assign)   CLLocationCoordinate2D  currentCoordinate2D;    //当前位置-百度坐标
@property (nonatomic, strong)   TGModelUserInfo *userInfo;
@property (nonatomic, strong)   TGModelVehicleInfo *vehicleInfo;
@property (nonatomic, strong)   TGModelShopInfo *shopInfo;

+ (TGDataSingleton *)sharedInstance;
@end
