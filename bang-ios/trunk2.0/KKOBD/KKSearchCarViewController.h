//
//  KKSearchCarViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
#import "KKBLECore.h"
#import "KKBLEEngine.h"
#import "MBProgressHUD.h"
#import "KKApplicationDefine.h"

@class KKModelVehicleDetailInfo;
@protocol KKSearchCarViewControllerDelegate;

@interface KKSearchCarViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,KKProtocolEngineDelegate,KKBLECoreDelegate,KKBLEEngineDelegate,MBProgressHUDDelegate>
{
    UITableView             *_mainTableView;
    NSMutableArray          *_dataArray;
    UIButton                *_refreshBtn;
    BOOL                     _isAnimating;
    NSInteger               _currentIndex;
    BOOL                    _isConnect;
    BOOL                    _CBWriteReady;              // core bluetooth has ready for write
    NSTimer                 *_getVinTimer;
//    MBProgressHUD           *_getVinHud;
    BOOL                     _isFirstGetVin;
    BOOL                     _isReadBackByVin;
}
@property (nonatomic ,assign)id <KKSearchCarViewControllerDelegate> delegate;
@property (nonatomic ,retain)NSTimer                 *getVinTimer;
@property (nonatomic ,retain)NSTimer                 *reGetVinTimer;
@property (nonatomic ,retain)NSTimer                 *getServiceTimer;
@property (nonatomic ,assign)BOOL   isFromRegister;
@property (nonatomic, assign) BOOL skipToBack;          //点击跳过时，返回
@property (nonatomic ,assign)NextViewControllerEnum     nextVc;

@end


@protocol KKSearchCarViewControllerDelegate
@optional
- (void)KKSearchCarViewControllerTransferInfoByOperation:(NSString *)info;

@end