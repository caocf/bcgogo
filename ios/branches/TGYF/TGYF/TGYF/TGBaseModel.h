//
//  TGBaseModel.h
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JSONModel.h"

//=======================================================================================
//model的基类,继承第三方jsonModel解析
//=======================================================================================
@interface TGBaseModel : JSONModel

@end

//=======================================================================================
// http 请求数据返回信息
//=======================================================================================
@interface TGModelRspHeader : TGBaseModel
@property (nonatomic, copy) NSString *status;
@property (nonatomic, copy) NSString *message;
@property (nonatomic, assign) NSInteger msgCode;
@end

#pragma mark - 权限
@interface TGModelPrvilegeMap : TGBaseModel
@property (nonatomic, assign) BOOL faultInfo;                   //是否有故障权限
@property (nonatomic, assign) BOOL appoint;                     //是否有预约权限
@property (nonatomic, assign) BOOL maintain;                    //是否有保养权限
@end

#pragma mark - 分页信息
@interface TGModelPageInfo : TGBaseModel
@property (nonatomic, assign) NSInteger pageSize;
@property (nonatomic, assign) NSInteger currentPage;
@property (nonatomic, assign) NSInteger nextPage;
@property (nonatomic, assign) BOOL isLastPage;
@property (nonatomic, assign) BOOL hasNextPage;
@end

#pragma mark - 故障信息
@interface TGModelFaultInfo : TGBaseModel
@property (nonatomic, assign) long long id;                             //故障id
@property (nonatomic, copy)    NSString *faultCode;                     //故障码
@property (nonatomic, copy)    NSString *vehicleNo;                     //车牌号
@property (nonatomic, copy)    NSString *faultCodeReportTimeStr;        //故障报告时间
@property (nonatomic, copy)    NSString *customerName;                  //车主
@property (nonatomic, copy)    NSString *faultCodeDescription;          //故障描述
@property (nonatomic, copy)    NSString *faultAlertTypeValue;           //故障类型
@end

#pragma mark - 保养信息
@interface TGModelCustomerServiceJobDTO : TGBaseModel
@property (nonatomic, assign)  long long id;                            //保养id
@property (nonatomic, assign)  long remindMileage;                      //保养里程
@property (nonatomic, assign)  long currentMileage;                     //当前里程
@property (nonatomic, copy)     NSString *licenceNo;                    //车牌号
@property (nonatomic, copy)     NSString *customerName;                 //车主
@end
