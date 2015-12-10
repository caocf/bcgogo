//
//  TGComplexModel.h
//  TGOBD
//
//  Created by Jiahai on 14-3-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGBasicModel.h"

@interface TGComplexObject : TGModelObject
@property (nonatomic, strong)       TGRspHeader     *header;
@end


@interface TGModelServiceCategoryRsp : TGComplexObject
{
    
}
@end

#pragma mark - 登录/注册

@interface TGModelValidateIMEIRsp : TGComplexObject

@property(nonatomic, strong)        TGModelShopInfo *shopDTO;
@property(nonatomic, strong)        TGModelVehicleInfo *appVehicleDTO;

@end

@interface TGModelLoginRsp : TGComplexObject

@property (nonatomic, strong) TGModelShopInfo *appShopDTO;
@property (nonatomic, strong) TGModelUserInfo *appUserDTO;
@property (nonatomic, strong) TGModelVehicleInfo *appVehicleDTO;

@end

@interface TGModelRegisterRsp : TGComplexObject

@property (nonatomic, strong) TGModelShopInfo *appShopDTO;
@property (nonatomic, strong) TGModelUserInfo *appUserDTO;
@property (nonatomic, strong) TGModelVehicleInfo *appVehicleDTO;

@end

#pragma mark - 行车日志

@interface TGModelDriveRecordListRsp : TGComplexObject
@property(nonatomic, assign) CGFloat    worstOilWear;
@property(nonatomic, assign) CGFloat    bestOilWear;
@property(nonatomic, assign) CGFloat    totalOilWear;               //总平均油耗
@property(nonatomic, assign) NSInteger  subtotalTravelTime;         //小计行程时间 （秒为单位）
@property(nonatomic, assign) CGFloat    subtotalDistance;
@property(nonatomic, assign) CGFloat    subtotalOilMoney;
@property(nonatomic, assign) CGFloat    subtotalOilWear;            //小计平均油耗
@property(nonatomic, assign) CGFloat    subtotalOilCost;            //小计耗油量

@property(nonatomic, strong) NSMutableArray *TGArrayFieldName(driveLogDTOs,TGModelDriveRecordDetail);
@end

@interface TGModelDriveRecordDetailRsp : TGComplexObject
@property(nonatomic, strong) NSMutableArray *TGArrayFieldName(detailDriveLogs,TGModelDriveRecordDetail);
@end

@interface TGModelDriveRecordGetVehicleRsp : TGComplexObject
@property (nonatomic, strong) TGModelVehicleInfo *vehicleInfo;
@end

#pragma mark - 加油站相关

@interface TGModelOilStationListRsp : TGComplexObject
@property (nonatomic, copy) NSString *resultcode;
@property (nonatomic, copy) NSString *reason;
@property (nonatomic, assign) BOOL isEnd;
@property (nonatomic, strong) TGModelOilStationList *result;
@end

#pragma mark - 违章查询

@interface TGModelViolateCityInfoListRsp : TGComplexObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(areaList, TGModelViolateCityInfo);
@end

@interface TGModelViolationInfoListRsp : TGComplexObject
@property (nonatomic, strong) TGModelValidateQueryResponse *queryResponse;
@end

#pragma mark - 故障码相关

@interface TGModelDTCListRsp : TGComplexObject
@property(nonatomic, strong)    NSMutableArray *TGArrayFieldName(result, TGModelDTCInfo);
@property(nonatomic, strong)    TGModelPagerInfo *pager;
@end

#pragma mark - 个人资料

@interface TGModelChangePasswordRsp : TGComplexObject

@end

@interface TGModelLogoutRsp : TGComplexObject

@end

#pragma mark - 消息
@interface TGModelGetNewMessageRsp : TGComplexObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(messageList, TGModelMessage);
@end

#pragma mark - 账单相关
@interface TGModelOrderLineRsp : TGComplexObject
@end

@interface TGModelOrderListRsp : TGComplexObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(unFinishedServiceList, TGModelOrderList);
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(finishedServiceList, TGModelOrderList);
@property (nonatomic, assign) double finishedServiceTotal;
@end

@interface TGModelOrderDetailRsp : TGComplexObject
@property (nonatomic, strong) TGModelOrderDetail *serviceDetail;
@end

#pragma mark - 更新汽车

@interface TGModelUpdateVehicleRsp : TGComplexObject

@end

#pragma mark - 版本检测

@interface TGModelCheckNewVersionRsp : TGComplexObject
@property (nonatomic, copy) NSString *url;              // 电子市场下载地址
@property (nonatomic, copy) NSString *action;           // 交互类型（force：强制|alert：提醒|normal：正常）
@property (nonatomic, copy) NSString *description;
@end

#pragma mark - 4S店公告
@interface TGMOdelPublicNoticeListRsp: TGComplexObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(advertDTOList, TGModelPublicNoticeInfo);
@property (nonatomic, strong) TGModelPagerInfo *pager;
@end

@interface TGModelPUBlicNoticeDetailRsp : TGComplexObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(advertDTOList, TGModelPublicNoticeInfo);
@property (nonatomic, strong) TGModelPagerInfo *pager;
@end
