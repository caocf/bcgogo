//
//  KKShopMapListViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-26.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BMapKit.h"
#import "KKProtocolEngineDelegate.h"
#import "KKAutoTableView.h"
@class KKCity;

typedef enum
{
    eShopMapListVcType_home,
    eShopMapListVcType_search
}KKShopMapListVcType;

@interface KKShopMapListViewController : UIViewController<UITextFieldDelegate,BMKMapViewDelegate,KKAutoTableViewDelegate,KKProtocolEngineDelegate>
{
    BMKMapView          *_mapView;
    UITextField         *_textField;
    
    KKAutoTableView     *_promptTableView;
    NSMutableArray      *_promptDataArray;
    
    NSInteger           _page;
    NSInteger           _size;
    
}
@property (nonatomic ,retain)NSMutableArray *shopArray;
@property (nonatomic ,assign)KKShopMapListVcType VcType;
@property (nonatomic ,retain)KKCity     *currentCity;
@property (nonatomic ,retain)NSString   *searchStr;
@property (nonatomic ,retain)NSString   *serviceIds;                //服务ID
@property (nonatomic ,retain)NSString   *serviceName;               //服务名
@property (nonatomic ,retain)NSString   *remarkString;

@end
