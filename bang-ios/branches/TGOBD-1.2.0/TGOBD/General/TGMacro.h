//
//  TGMacro.h
//  TGOBD
//
//  Created by Jiahai on 14-3-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

/**
 *  此文件用来定义宏
 */

#ifndef TGOBD_TGMacro_h
#define TGOBD_TGMacro_h

/**
 *  系统配置
 */
#pragma mark - 系统配置

#define TGAppDelegateSingleton ((TGAppDelegate *)[[UIApplication sharedApplication] delegate])

#ifdef DEBUG
    #define TGLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
    #define TGLog(fmt, ...) 
#endif

#define TGSystemVersion   [[UIDevice currentDevice].systemVersion floatValue]
#define systemVersionAboveiOS7  (TGSystemVersion >= 7.0)
#define screenWidth     320
#define screenHeight    (systemVersionAboveiOS7 ? ([UIScreen mainScreen].bounds.size.height) : ([UIScreen mainScreen].bounds.size.height - 20))
/**
 *  默认相关时间设置,单位为秒
 */
#pragma mark - 时间配置

#define TIME_MESSAGE_ROLLING 60
#define TIME_GET_VEHICLE_INFO 30
#define TIME_HTTP_TIMEOUT 20

#pragma mark - 通知用到的宏

#define NOTIFICATION_LoginOut                       @"NOTIFICATION_LoginOut"
#define NOTIFICATION_OilStationListClicked          @"NOTIFICATION_OilStationListClicked"
#define NOTIFICATION_GetNewMessage                  @"NOTIFICATION_GetNewMessage"
#define NOTIFICATION_SetUnreadMessageNum            @"NOTIFICATION_SetUnreadMessageNum"
#define NOTIFICATION_UpdateVehicleCoordinate        @"NOTIFICATION_UpdateVehicleCoordinate"
#define NOTIFICATION_UpdateTrafficViolation         @"NOTIFICATION_UpdateTrafficViolation"
#define NOTIFICATION_FINISHED_SCAN                  @"NOTIFICATION_FINISHED_SCAN"

///首页左右两个barItem 有更新或新消息故障小红点提示
#define NOTIFICATION_updateLeftBarItemTip           @"NOTIFICATION_updateLeftBarItemTip"
#define NOTIFICATION_updateRightBarItemTip          @"NOTIFICATION_updateRightBarItemTip"
#define NOTIFICATION_updateVersionCheckTip          @"NOTIFICATION_updateVersionCheckTip"

#pragma mark - 颜色配置
#define TGRGBA(r,g,b,a) [UIColor colorWithRed:(float)r/255.0f green:(float)g/255.0f blue:(float)b/255.0f alpha:a]

#define COLOR_BLUE_0099CC        TGRGBA(0x00,0x99,0xCC,1)
#define COLOR_ORANGE_FF8800      TGRGBA(0xFF,0x88,0x00,1)
#define COLOR_3359ac             TGRGBA(0x33,0x59,0xac,1)
#define COLOR_777777             TGRGBA(0x77,0x77,0x77,1)
#define COLOR_TEXTLEFT_6C6C6C    TGRGBA(0x6c,0x6c,0x6c,1)
#define COLOR_TEXT_000000        TGRGBA(0x00,0x00,0x00,1)
#define COLOR_DRIVERECORD_DISTANCE          TGRGBA(3,127,222,1)
#define COLOR_DRIVERECORD_TRAVELTIME        TGRGBA(15,177,144,1)
#define COLOR_DRIVERECORD_AVERAGEOILWEAR    TGRGBA(233,59,37,1)
#define COLOR_DRIVERECORD_OILWEAR           TGRGBA(255,144,0,1)
#define COLOR_LAYER_BORDER                  TGRGBA(190,190,190,1)

//自定义button 字典key
#define SHOW_TIPIMG  @"SHOW_TIPIMG"
#define TIPIMG_NAME  @"TIPIMG_NAME"

//消息类型
#pragma mark - 消息类型
#define SHOP_CHANGE_APPOINT             @"SHOP_CHANGE_APPOINT"              //店铺预约修改消息   跳列表
#define SHOP_FINISH_APPOINT             @"SHOP_FINISH_APPOINT"              //店铺预约结束消息   跳详情
#define SHOP_ACCEPT_APPOINT             @"SHOP_ACCEPT_APPOINT"              //店铺接受预约单     跳列表
#define SHOP_REJECT_APPOINT             @"SHOP_REJECT_APPOINT"              //店铺预约拒绝消息   不跳
#define SHOP_CANCEL_APPOINT             @"SHOP_CANCEL_APPOINT"              //店铺预约取消消息   不跳
#define OVERDUE_APPOINT_TO_APP          @"OVERDUE_APPOINT_TO_APP"           //APP过期预约单     在线预约
#define APP_VEHICLE_MAINTAIN_MILEAGE    @"APP_VEHICLE_MAINTAIN_MILEAGE"     //保养里程  在线预约
#define APP_VEHICLE_MAINTAIN_TIME       @"APP_VEHICLE_MAINTAIN_TIME"        //保养时间  在线预约
#define APP_VEHICLE_INSURANCE_TIME      @"APP_VEHICLE_INSURANCE_TIME"       //保险时间  不跳
#define APP_VEHICLE_EXAMINE_TIME        @"APP_VEHICLE_EXAMINE_TIME"         //验车时间  不跳
#define VEHICLE_FAULT_2_APP             @"VEHICLE_FAULT_2_APP"              //故障消息  故障列表
#define CUSTOM_MESSAGE_2_APP            @"CUSTOM_MESSAGE_2_APP"             //自定义消息 不跳
#define SHOP_ADVERT_TO_APP              @"SHOP_ADVERT_TO_APP"               //店铺公告 跳到公告详情
#define VIOLATE_REGULATION_RECORD_2_APP @"VIOLATE_REGULATION_RECORD_2_APP"         //违章消息，跳违章查询页面

//用户信息配置
#pragma mark - 用户信息配置

#define USER_MOBILE     @"user_mobile"
#define USER_PASSWORD   @"user_password"
#define USER_REMBER_PASSWORD    @"user_rember_password"

#pragma mark - 其他字段
#define TGCOOKIE    @"TGCOOKIE"

#endif
