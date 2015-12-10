//
//  TGMacroDefine.h
//  TGYIFA
//
//  Created by James Yu on 14-5-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#ifndef TGYIFA_TGMacroDefine_h
#define TGYIFA_TGMacroDefine_h

#ifdef DEBUG
#define TGLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
#define TGLog(fmt, ...)
#endif

#pragma mark - 系统常见属性

#define NavigationBar_HEIGHT                    44
#define SCREEN_WIDTH                            [UIScreen mainScreen].bounds.size.width
#define SCREEN_HEIGHT                           [UIScreen mainScreen].bounds.size.height
#define CURRENT_SYSTEM_VERSION                  ([[UIDevice currentDevice].systemVersion floatValue])
#define isIOS7                                  (CURRENT_SYSTEM_VERSION >= 7.0)
#define CURRENT_APP_VERSION                     [[NSBundle mainBundle] infoDictionary][@"CFBundleVersion"]

#pragma mark - 系统常用单例

#define SharedUserDefault                       [NSUserDefaults standardUserDefaults]
#define SharedNotificationCenter                [NSNotificationCenter defaultCenter]
#define SharedFileManager                       [NSFileManager defaultManager]
#define SharedApplicationDelegate               [[UIApplication sharedApplication] delegate]

#pragma mark - 文件目录
#define PathForDocument                         NSSearchPathForDirectoriesInDomains (NSDocumentDirectory, NSUserDomainMask, YES)[0]
#define PathForLibrary                          NSSearchPathForDirectoriesInDomains (NSLibraryDirectory, NSUserDomainMask, YES)[0]
#define PathForCaches                           NSSearchPathForDirectoriesInDomains (NSCachesDirectory, NSUserDomainMask, YES)[0]

#pragma mark - 加载图片

#define IMAGE(name)                             [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:name ofType:nil]]
#define ImageNamed(name)                        [UIImage imageNamed:name]

#pragma mark - 颜色分类
// rgb颜色转换（16进制->10进制）
#define UIColorFromRGB(rgbValue)                [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

#define RGBA(r,g,b,a)                           [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:a]
#define RGB(r,g,b)                              RGBA(r,g,b,1.0f)

#pragma mark - 重用颜色
#define COLOR_BORDER_LAYER                      RGBA(190,190,190,1.0)
#define COLOR_NAVIGATION_BAR                    RGBA(47, 168, 197, 1.0)
#define COLOR_CELL_TITLE                        RGBA(231, 250, 255, 1.0)
#define COLOR_CELL_BUTTON                       RGBA(49, 168, 200, 1.0)
#define COLOR_CELL_LABEL                        RGBA(128, 204, 220, 1.0)
#define COLOR_ALERTVIEW_BACKROUND               RGBA(160, 219, 237, 1.0)

#pragma mark - 字体

#define FONT(F)                                 [[UIFont fontWithName:@"FZHTJW--GB1-0" size:F]]

#pragma mark - 通知字符串


#endif
