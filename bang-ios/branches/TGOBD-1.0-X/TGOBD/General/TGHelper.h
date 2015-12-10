//
//  TGHelper.h
//  TGOBD
//
//  Created by Jiahai on 14-3-3.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>//

@interface TGHelper : NSObject


/**
 *  生成UUID
 *
 *  @return NSString
 */
+ (NSString *)createUUIDString;

/**
 *  获取Document下的完整文件路径
 *
 *  @param aPath document下的路径
 *
 *  @return NSString
 */
+ (NSString *)getPathWithinDocumentDir:(NSString *)aPath;

/**
 *  Int型 米转换成千米
 *
 *  @param aMeter 米
 *
 *  @return NSString
 */
+(NSString *) meterToKiloFromInt:(int) aMeter;

/**
 *  String型 米转换成千米
 *
 *  @param aMeter 米
 *
 *  @return NSString
 */
+(NSString *) meterToKiloFromString:(NSString *)aMeter;

@end
