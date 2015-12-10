//
//  TGBaseModel.m
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseModel.h"

@implementation TGBaseModel

+ (BOOL)propertyIsOptional:(NSString *)propertyName
{
    return YES;
}

@end

@implementation TGModelRspHeader

@end

#pragma mark - 权限
@implementation TGModelPrvilegeMap

@end

#pragma mark - 分页信息
@implementation TGModelPageInfo

@end

#pragma mark - 故障信息
@implementation TGModelFaultInfo

@end

#pragma mark - 保养信息
@implementation TGModelCustomerServiceJobDTO

@end