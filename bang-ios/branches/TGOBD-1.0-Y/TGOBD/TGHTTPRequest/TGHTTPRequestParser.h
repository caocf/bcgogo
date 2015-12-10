//
//  XSHTTPRequestParser.h
//  General
//
//  Created by Jiahai on 14-2-27.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGComplexModel.h"
@class TGHTTPRequestAbstractParser;

@interface TGHTTPRequestParserFactory : NSObject

+ (TGHTTPRequestAbstractParser *)createParser:(NSInteger)aRequestApiID;
@end


#pragma mark -
#pragma mark abstractParser

@interface TGHTTPRequestAbstractParser : NSObject {
    
}
- (id)parse:(id)jsonObject;
- (id)parseObjectWithDict:(NSDictionary *)dict classname:(NSString *)classname;

@end

@interface TGRspHeaderParser : TGHTTPRequestAbstractParser

@end


#pragma mark - 加油站
@interface TGModelOilStationListRspParser: TGHTTPRequestAbstractParser

@end

@interface TGModelServiceCategoryRspParser : TGHTTPRequestAbstractParser

@end

