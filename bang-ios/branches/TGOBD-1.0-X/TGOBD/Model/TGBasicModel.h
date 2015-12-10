//
//  TGBasicModel.h
//  TGOBD
//
//  Created by Jiahai on 14-2-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CLLocation.h>

// Macro define
// 以下定义主要是为了json解析方便，请遵循
#define TGReservedKeyExt(key) key##__               // 成员名字与保留关键字冲突时
#define TGArrayFieldName(name, objectClassname)   name##__##objectClassname      // 定义array

typedef NS_ENUM(NSInteger, TGRespStatusCode) {
    rspStatus_Succeed = 1,
    rspStatus_Failed,
    rspStatus_Unknown = 10
};

@interface TGModelObject : NSObject

@end

@interface TGRspHeader : TGModelObject

@property(nonatomic, assign) NSInteger  status;
@property(nonatomic, copy) NSString     *message;
@property(nonatomic, assign) NSInteger  msgCode;
@end

/**
 *  导航前需配置的参数
 */
@interface TGModelRouteData : TGModelObject
@property (nonatomic, assign)   CLLocationCoordinate2D endCoordinate2D;
@property (nonatomic, copy)     NSString        *endTitle;
@property (nonatomic, copy)     NSString        *endAddress;
@end

#pragma mark - 加油站相关

@interface TGModelOilPageInfo : TGModelObject
@property (nonatomic, assign) NSInteger pnums;
@property (nonatomic, copy) NSString *current;
@end

@interface TGModelOilPrice : TGModelObject

@property (nonatomic, copy) NSString *E90;
@property (nonatomic, copy) NSString *E93;
@property (nonatomic, copy) NSString *E97;
@property (nonatomic, copy) NSString *E0;

@end

@interface TGModelOilStation : TGModelObject
@property (nonatomic, copy) NSString *id;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *type;
@property (nonatomic, copy) NSString *discount;
@property (nonatomic, copy) NSString *lon;
@property (nonatomic, copy) NSString *lat;
@property (nonatomic, strong) TGModelOilPrice *price;
@property (nonatomic, strong) NSMutableDictionary *gastprice;
@property (nonatomic, assign) NSInteger distance;
@property (nonatomic, assign) BOOL      isLoadToMap;            //是否已经在地图上显示
@end

@interface TGModelOilStationList : TGModelObject
@property (nonatomic, strong) NSMutableArray *TGArrayFieldName(data, TGModelOilStation);
@property (nonatomic, strong) TGModelOilPageInfo *pageinfo;
@end



