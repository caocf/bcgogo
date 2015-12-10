//
//  TGNetworkOperation.h
//  TGYIFA
//
//  Created by James Yu on 14-5-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "MKNetworkOperation.h"

@interface TGNetworkOperation : MKNetworkOperation

//主要用来之后进行数据返回解析
@property (nonatomic, assign) NSInteger apiId;
//标志是哪个viewController发出的请求
@property (nonatomic, strong) Class viewControllerClass;

@end
