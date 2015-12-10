//
//  KKViolateViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKProtocolEngineDelegate.h"
#import "KKShopFilterPopView.h"
@class KKCustomTextField,KKModelVehicleDetailInfo;

typedef struct {
    BOOL needEngine;
    BOOL needRegist;
    BOOL needClassa;
}ViolateSearchCondition;

@interface KKViolateViewController : UIViewController<KKProtocolEngineDelegate,KKShopFilterPopViewDelegate,UITableViewDataSource,UITableViewDelegate>
{
    KKCustomTextField   *_veNoTypeText;
    KKCustomTextField   *_vehicleNoText;
    KKCustomTextField   *_cityText;
    UILabel *moneyTotalLabel;
    UILabel *scoreTotalLabel;
    UILabel *_noResultLabel;
    
    UITableView         *_tableView;
    NSMutableArray *_violateResults;
    
    BOOL _loadingCity,_loadedCityShow;
    int fenTotal,moneyTotal;
    
    BOOL        loadedCache;
    
    ViolateSearchCondition violateSearchCondition;
}

@end
