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
 *  <#Description#>
 */

#define systemVersion   [[UIDevice currentDevice].systemVersion floatValue]
#define systemVersionAboveiOS7  (systemVersion >= 7.0)
#define screenWidth     320
#define screenHeight    (systemVersionAboveiOS7 ? ([UIScreen mainScreen].bounds.size.height) : ([UIScreen mainScreen].bounds.size.height - 20))


#pragma mark - 通知用到的宏

#define NOTIFICATION_OilStationListClicked          @"NOTIFICATION_OilStationListClicked"



#endif
