//
//  KKShopQueryViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKShopFilterSegmentControl.h"
#import "KKShopFilterPopView.h"
#import "KKProtocolEngineDelegate.h"
#import "MBProgressHUD.h"
#import "BMapKit.h"
#import "KKAutoTableView.h"
@class KKCity;

typedef enum
{
    eSortType_distance,
    eSortType_score
}KKShopSortType;

typedef enum
{
    eServiceType_maintenance = 0,
    eServiceType_decorating,
    eServiceType_sprayPainting,
    eServiceType_insuranceInspection,
    eServiceType_wash
}KKShopServiceType;

typedef enum
{
    eShopCategory_4S,
    eShopCategory_all
}KKShopCategoryType;


typedef enum
{
    eShopQueryVcType_home,
    eShopQueryVcType_search
}KKShopQueryVcType;


typedef enum
{
    eShopTableCellType_recommend,
    eShopTableCellType_nomal
}eShopTableCellType;

typedef enum
{
    eShopCity_province,
    eShopCity_city
}eShopCityType;


@interface KKShopQueryViewController : UIViewController<KKShopFilterSegmentControlDelegate,KKShopFilterPopViewDelegate,UITextFieldDelegate,UITableViewDataSource,UITableViewDelegate,UIScrollViewDelegate,KKProtocolEngineDelegate,BMKMapViewDelegate,KKAutoTableViewDelegate>
{
    UITableView         *_mainTableView;
    NSMutableArray      *_dataArray;
    
    KKAutoTableView     *_promptTableView;
    NSMutableArray      *_promptDataArray;
    
    UITextField         *_textField;
    KKShopFilterSegmentControl *_filterControl;
    
    NSMutableArray      *_provinceArray;
    NSMutableArray      *_cityArray;
    NSMutableDictionary *_cityDict;
    
    BOOL                _enableRefresh;
    BOOL                _isLoading;
    NSInteger           _page;
    NSInteger           _size;
    
    eShopCityType       _cityType;
    NSString            *_currentSelectedProvinceName;
    KKCity              *_currentCity;
    BOOL                _is4sShop;
    BMKMapView          *_mapView;
    
    BOOL                _isMore;
    
    BOOL                _pullRefresh;
    
    UIView              *_moreBtnView;
}
@property (nonatomic ,assign)KKShopQueryVcType  VcType;
@property (nonatomic ,assign)KKShopSortType     sortType;
@property (nonatomic ,retain)NSString           *serviceTypeKey;
@property (nonatomic ,assign)NSInteger          serviceIndex;
@property (nonatomic ,assign)KKShopCategoryType shopCategoryType;
@property (nonatomic ,retain)KKCity             *currentCity;
@property (nonatomic ,retain)NSString           *searchStr;
@property (nonatomic ,retain)NSString           *serviceIds;
@property (nonatomic ,retain)NSMutableArray     *serviceTypeArray;
@property (nonatomic ,retain)NSString           *remarkString;      //故障详情

@property (nonatomic, retain) NSArray          *dtcMsgArray;    //一键预约带出所有故障的信息；

@end
