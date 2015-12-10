//
//  KKRegisterAccount3ViewController.h
//  KKOBD
//
//  Created by Jiahai on 13-12-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKSearchCarModelViewController.h"
#import "KKProtocolEngineDelegate.h"
@class KKModelVehicleDetailInfo;
@class KKCustomTextField;

typedef enum
{
    RegEntrance_RegisterView = 1,
    RegEntrance_OBDSearchView,
    RegEntrance_ShopSearchView
}RegEntrance;

@interface KKRegisterAccount3ViewController : UIViewController<KKSearchCarModelDelegate,UITextFieldDelegate,KKProtocolEngineDelegate>
{
    KKCustomTextField   *_obdText;
    KKCustomTextField   *_shopText;
    KKCustomTextField   *_vehicleNoText;
    KKCustomTextField   *_vehicleBrandText;
    KKCustomTextField   *_vehicleModelText;
    UIView              *_carModelMarkView;
    UIView              *_carBrandMarkView;
}

//@property (nonatomic,retain) KKModelVehicleDetailInfo *regVehicleDetailInfo;
@property (nonatomic,assign) RegEntrance regEntrance;
//@property (nonatomic,assign) BOOL isRequired;       //此页面是否为必填
@end
