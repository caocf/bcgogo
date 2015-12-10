//
//  XSHTTPRequest.h
//  
//
//  Created by Jiahai on 14-2-26.
//
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"
#import <CoreLocation/CLLocation.h>

@class TGHTTPRequestOperationManager;
//typedef AFHTTPRequestOperationManager TGHTTPRequestManager;

typedef enum{
    apiID_oilStation_getList    =   0,
    apiID_Shop_serviceCategoty,
}TGHTTPRequest_ApiID;

typedef enum {
    TGRequestType_Get   = 1,
    TGRequestType_Post,
    TGRequestType_Put
}TGRequestType;

typedef struct {
    TGHTTPRequest_ApiID                 apiID;
    __unsafe_unretained NSString        *urlStr;
}TGApiIDDictItem;


@interface TGHTTPRequestEngine : NSObject
{
    TGHTTPRequestOperationManager   *_requestManager;
}

+(TGHTTPRequestEngine *)sharedInstance;

- (void)cancleRequestWithViewControllerIdentifier:(id)aIdentifier;

#pragma mark - 加油站相关
/**
 *  获取加油站列表
 *
 *  @param coordinate 当前位置坐标
 *  @param radius     获取周边加油站的半径
 *  @param pageNO     当前页数
 */
- (void)oilStationGetList:(CLLocationCoordinate2D)coordinate radius:(NSInteger)radius pageNo:(NSInteger)pageNo viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;

#pragma mark - 店铺相关
- (void)shopGetServiceCategoty:(NSString *)aServiceScope viewControllerIdentifier:(id)aIdentifier success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))aSuccess failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))aFailure;


@end
