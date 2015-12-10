//
//  KKBLEManager.h
//  KKOBD
//
//  Created by zhuyc on 13-9-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKBLEEngine.h"

@protocol KKBLEManagerDelegate;

@interface KKBLEManager : NSObject<KKBLEEngineDelegate,KKBLECoreDelegate>

@property (nonatomic ,retain)KKBLEEngine *engine;
@property (nonatomic ,assign)id<KKBLEManagerDelegate> bleManagerDelegate;

+ (KKBLEManager *)shareBleManager;

@end


@protocol KKBLEManagerDelegate <NSObject>
@optional
- (void)KKBLEManagerSupport:(BOOL)support;
- (void)KKBLEManagerScanFinishedWithResults:(NSMutableArray *)peripherals;

@end