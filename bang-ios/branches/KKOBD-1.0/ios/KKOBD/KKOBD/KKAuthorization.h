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

@interface KKAuthorization : NSObject

@property(nonatomic, readonly) BOOL     shopQuery;
//@property(nonatomic, readonly) BOOL

+(KKAuthorization *)sharedInstance;
@end
