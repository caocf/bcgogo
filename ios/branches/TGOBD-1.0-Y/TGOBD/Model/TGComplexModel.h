//
//  TGComplexModel.h
//  TGOBD
//
//  Created by Jiahai on 14-3-4.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGBasicModel.h"

@interface TGComplexObject : TGModelObject
@property (nonatomic, strong)       TGRspHeader     *header;
@end


@interface TGModelServiceCategoryRsp : TGComplexObject
{
    
}
@end


#pragma mark - 加油站相关

@interface TGModelOilStationListRsp : TGComplexObject
@property (nonatomic, copy) NSString *resultcode;
@property (nonatomic, copy) NSString *reason;
@property (nonatomic, assign) BOOL isEnd;
@property (nonatomic, strong) TGModelOilStationList *result;
@end
