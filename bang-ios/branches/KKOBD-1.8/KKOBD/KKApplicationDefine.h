//
//  KKFontAndColorDefine.h
//  KKShowBooks
//
//  Created by zhuyc on 12-10-9.
//  Copyright (c) 2012年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>

#define KKAppDelegateSingleton ((KKAppDelegate*)[[UIApplication sharedApplication] delegate])
#define currentScreenHeight ((currentSystemVersion >= 7.0) ? ([UIScreen mainScreen].bounds.size.height) : ([UIScreen mainScreen].bounds.size.height - 20))
#define currentSystemVersion [[UIDevice currentDevice].systemVersion floatValue]
#define KKImageByName(name) [UIImage imageNamed:name]
#define KKRGBA(r,g,b,a) [UIColor colorWithRed:(float)r/255.0f green:(float)g/255.0f blue:(float)b/255.0f alpha:a]
#define DLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#define LOADIMAGE(file,ext) [UIImage imageWithContentsOfFile:[[NSBundle mainBundle]pathForResource:file ofType:ext]]


#define KKServiceScopeFirstCategoryDict     [NSDictionary dictionaryWithObjects:@[@"OVERHAUL_AND_MAINTENANCE",@"DECORATION_BEAUTY",@"PAINTING",@"INSURANCE",@"WASH",@"NULL"] forKeys:@[@"机修保养",@"美容装潢",@"钣金喷漆",@"保险验车",@"洗车服务",@"服务范围"]]

#define iPhone5 ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(640, 1136), [[UIScreen mainScreen] currentMode].size) : NO)
#define isRetina ([[UIScreen mainScreen] scale]== 2 ? YES: NO)
#define CurrentSystemPlatform @"IOS"
#define CurrentSystemVersion ([[UIDevice currentDevice] systemVersion])


#define KKCOLOR_CCCCCC             KKRGBA(204,204,204,1)
#define KKCOLOR_999999             KKRGBA(0x99,0x99,0x99,1)
#define KKCOLOR_409beb             KKRGBA(0x40,0x9b,0xeb,1)
#define KKCOLOR_a7a6a6             KKRGBA(0xa7,0xa6,0xa6,1)
#define KKCOLOR_333333             KKRGBA(0x33,0x33,0x33,1)
#define KKCOLOR_7b7b7b             KKRGBA(0x7b,0x7b,0x7b,1)
#define KKCOLOR_1c1c1c             KKRGBA(0x1c,0x1c,0x1c,1)
#define KKCOLOR_A7a6a6             KKRGBA(0xA7,0xa6,0xa6,1)
#define KKCOLOR_fe7701             KKRGBA(0xfe,0x77,0x01,1)
#define KKCOLOR_fe7701             KKRGBA(0xfe,0x77,0x01,1)
#define KKCOLOR_00a2cd             KKRGBA(0x00,0xa2,0xcd,1)
#define KKCOLOR_a7a8a6             KKRGBA(0xa7,0xa8,0xa6,1)
#define KKCOLOR_717171             KKRGBA(0x71,0x71,0x71,1)
#define KKCOLOR_3359ac             KKRGBA(0x33,0x59,0xac,1)
#define KKCOLOR_c0c0c0             KKRGBA(0xc0,0xc0,0xc0,1)
#define KKCOLOR_777777             KKRGBA(0x77,0x77,0x77,1)
#define KKCOLOR_d4d4d4             KKRGBA(0xd4,0xd4,0xd4,1)
#define KKCOLOR_dedede             KKRGBA(0xde,0xde,0xde,1)
#define KKCOLOR_9c9c9c             KKRGBA(0x9c,0x9c,0x9c,1)
#define KKCOLOR_2c59b2             KKRGBA(0x2c,0x59,0xb2,1)
#define KKCOLOR_c4c4c3             KKRGBA(0xc4,0xc3,0xc3,1)
#define KKCOLOR_Blue               KKRGBA(0x00,0x7E,0xE9,(float)0xBB/0xFF)

#define KKNavgationBarbgImage      @"bg_navigation.png"

#define Notification_UpdateVehicleList @"updateVehicleList"
#define Notification_UpdatedVehicleList @"updatedVehicleList"
#define Notification_DriveRecord_NewPoint   @"DriveRecord_NewPoint"
#define Notification_DriveRecord_UnSave     @"DriveRecord_UnSave"           //时间太短无需记录




typedef enum {
    NextVc_ObdAndCarListVc = 1,
    NextVc_BindShopVc
}NextViewControllerEnum;

