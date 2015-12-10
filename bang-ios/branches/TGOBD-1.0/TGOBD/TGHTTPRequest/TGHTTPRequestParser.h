//
//  XSHTTPRequestParser.h
//  General
//
//  Created by Jiahai on 14-2-27.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGComplexModel.h"

@interface TGHTTPRequestParser : NSObject {
    
}
+ (id)parse:(id)jsonObject apiID:(NSInteger)apiID;
+ (id)parseObjectWithDict:(NSDictionary *)dict classname:(NSString *)classname;

@end

@interface TGRspHeaderParser : NSObject

- (id)parse:(id)jsonObject;
@end
