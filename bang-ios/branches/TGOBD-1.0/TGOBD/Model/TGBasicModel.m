//
//  TGBasicModel.m
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBasicModel.h"
#import "TGHelper.h"

@implementation TGModelObject

@end

@implementation TGRspHeader

@end

@implementation TGModelRouteData

@end

#pragma mark - 通用

@implementation TGModelPagerInfo

@end


#pragma mark - 加油站相关

@implementation TGModelOilPageInfo

@end

@implementation TGModelOilPrice

@end

@implementation TGModelOilStation

@end

@implementation TGModelOilStationList

@end

#pragma mark - 违章相关

@implementation TGModelViolateSearchCondition

@end

@implementation TGModelViolateCityInfo

@end

@implementation TGModelViolateDetailInfo

@end

@implementation TGModelViolateResultInfo

@end

@implementation TGModelViolateVehicleType

@end

#pragma mark - 行车日志相关

@implementation TGModelDriveRecordOilWear

@end

@implementation TGModelDriveRecordDetail

@end

@implementation TGModelDriveRecordPoint

@end


#pragma mark - 故障码相关

@implementation TGModelDTCInfo

@end

#pragma mark - 消息

@implementation TGModelMessage

@end

#pragma mark - 账单相关

@implementation TGModelOrderList

@end

@implementation TGModelOrderItem

@end

@implementation TGModelSettleAccounts

@end

@implementation TGModelShopInfo

@end

@implementation TGModelOrderDetail

@end

#pragma mark - 汽车

@implementation TGModelVehicleInfo

@end

#pragma mark - 用户信息

@implementation TGModelUserInfo

@end

#pragma mark - 登录信息

@implementation TGModelLoginInfo

- (id)init
{
    if (self = [super init]) {
        _platform = @"IOS";
        _platformVersion = [[UIDevice currentDevice] systemVersion];
        _mobileModel = [TGHelper getPlatformModel];
        _appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
        _imageVersion = [TGHelper getImageVersion];
    }
    return self;
}

@end
