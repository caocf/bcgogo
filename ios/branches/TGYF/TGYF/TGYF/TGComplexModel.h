//
//  TGComplexModel.h
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGBaseModel.h"

//=======================================================================================
//网络请求返回数据Model的基类
//=======================================================================================
@interface TGComplexModel : TGBaseModel
@property (nonatomic, strong) TGModelRspHeader *header;
@end

#pragma mark - 登录
@interface TGModelLoginRsp : TGComplexModel
@property (nonatomic, strong) TGModelPrvilegeMap *privilegeMap;
@end

#pragma mark - 获取短信内容
@interface TGModelGetSMSRsp : TGComplexModel
@property (nonatomic, copy) NSString *msgContent;
@end

#pragma mark - 发送短信
@interface TGModelSendMsgRsp : TGComplexModel

@end

#pragma mark - 预约或者保养更改为已处理
@interface TGModelRemindHandleRsp : TGComplexModel

@end

#pragma mark - 接受预约单
@interface TGModelAcceptAppointRsp : TGComplexModel

@end

#pragma mark - 更改预约单服务时间
@interface TGModelChangeAppointTimeRsp : TGComplexModel

@end

#pragma mark - 获取故障列表
//此处是jsonModel解析数组的时候必须定义对应的protocol
@protocol TGModelFaultInfo
@end

@interface TGModelGetVehicleFaultInfoList : TGComplexModel
@property (nonatomic, strong) TGModelPageInfo *pager;
@property (nonatomic, strong) NSArray<TGModelFaultInfo> *faultInfoToShopDTOList;
@end

#pragma mark - 获取保养列表
@protocol TGModelCustomerServiceJobDTO
@end

@interface TGModelGetCustomerRemindList : TGComplexModel
@property (nonatomic, strong) TGModelPageInfo *pager;
@property (nonatomic, strong) NSArray<TGModelCustomerServiceJobDTO> *serviceJobDTOList;
@end

