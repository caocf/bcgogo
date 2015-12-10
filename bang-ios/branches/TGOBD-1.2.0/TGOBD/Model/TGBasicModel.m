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

@implementation TGModelViolateCityInfo

@end

@implementation TGModelViolationInfo

@end

@implementation TGModelViolationInfoResult

@end

@implementation TGModelValidateQueryResponse

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
        _deviceToken = [[NSUserDefaults standardUserDefaults] objectForKey:@"deviceToken"];
    }
    return self;
}

@end

#pragma mark - 公告相关
@implementation TGModelPublicNoticeInfo

- (void)encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeInt64:_id forKey:@"id"];
    [aCoder encodeInt64:_publishDate forKey:@"publishDate"];
    [aCoder encodeObject:_title forKey:@"title"];
    [aCoder encodeObject:_imageUrl forKey:@"imageUrl"];
    [aCoder encodeObject:_desc forKey:@"description"];
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init]) {
        self.id = [aDecoder decodeInt64ForKey:@"id"];
        self.publishDate = [aDecoder decodeInt64ForKey:@"publishDate"];
        self.desc = [aDecoder decodeObjectForKey:@"description"];
        self.title = [aDecoder decodeObjectForKey:@"title"];
        self.imageUrl = [aDecoder decodeObjectForKey:@"imageUrl"];
    }
    return self;
}

@end

#pragma mark - 统计信息
@implementation TGModelDriveStatisticInfo
@end