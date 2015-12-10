//
//  KKAuthorization.h
//  KKOBD
//
//  Created by Jiahai on 13-12-27.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    Authorization_Visitor = 1,          //游客
    Authorization_Register              //注册用户
}AuthorizationType;


@interface KKAccessAuthorization : NSObject

@property(nonatomic, assign) BOOL           localCarManager;    //YES-本地管理车辆信息，NO-服务器管理车辆信息
@property(nonatomic, assign) BOOL           shopQuery;          //店铺查询界面
@property(nonatomic, assign) BOOL           shopQuery_MoreBtn;  //店铺查询界面中的“更多”按钮使用
@property(nonatomic, assign) BOOL           vehicleCondition;   //车况查询界面
@property(nonatomic, assign) BOOL           orderOnline;        //在线预约
@property(nonatomic, assign) BOOL           serviceSeeking;     //服务查询
@property(nonatomic, assign) BOOL           personalInfo;       //个人资料
@property(nonatomic, assign) BOOL           searchCar;          //绑定OBD
@property(nonatomic, assign) BOOL           scanShop;           //扫描店铺


+(KKAccessAuthorization *) createWithAuthorizationType:(AuthorizationType)aType;
@end


@interface KKAuthorization : NSObject
{
    
}
@property(nonatomic, readonly) AuthorizationType        authorizationType;
@property(nonatomic, retain) KKAccessAuthorization      *accessAuthorization;

+(KKAuthorization *)sharedInstance;

-(void) setAuthorizationType:(AuthorizationType)authorizationType;

@end
