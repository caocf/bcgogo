//
//  KKRegisterAccount2ViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-12.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTextField.h"
#import "KKCustomDataPicker.h"
#import "KKScanViewController.h"
#import "KKProtocolEngineDelegate.h"
#import "KKSearchCarModelViewController.h"
#import "KKRegisterAccountViewController.h"
#import "KKSearchCarViewController.h"

@interface KKRegisterAccount2ViewController : UIViewController<KKCustomTextFieldDelegate,KKCustomDataPickerDelegate,KKScanViewControllerDelegate,KKProtocolEngineDelegate,KKSearchCarModelDelegate,KKSearchCarViewControllerDelegate>
{
    UIScrollView        *_mainScrollView;
    UIView              *_detailInfoView;
    KKCustomTextField   *_textField1;       //车牌号码      index:||    10
    KKCustomTextField   *_textField2_1;     //车辆的|品牌               11
    KKCustomTextField   *_textField2_2;     //     |车型               12
    KKCustomTextField   *_textField3;       //扫描店铺                  13
    KKCustomTextField   *_obdTextField;     //扫描OBD
    KKCustomTextField   *_textField4;       //下次保养里程               15
    KKCustomTextField   *_textField5;       //下次保险时间               16
    KKCustomTextField   *_textField6;       //下次验车时间               17
    KKCustomTextField   *_textField7;       //代办人                    18
    KKCustomTextField   *_textField8;       //当前里程                   14
    UIButton            *_submitBtn;
    NSString            *_vehicleVin;       //OBD读出的车辆Vin信息
    
    NSInteger            _viewIndex;
    
    UIView              *_carBrandMarkView; //品牌水印
    UIView              *_carModelMarkView; //车型水印
    
}
@property (nonatomic ,retain)NSString *aAccountName;
@property (nonatomic ,retain)NSString *aPasswordNum;
@property (nonatomic ,retain)NSString *aUserName;
@property (nonatomic ,retain)NSString *aPhoneNum;
@property (nonatomic ,retain)KKRegisterAccountViewController *superVc;
@property (nonatomic, retain) KKModelVehicleDetailInfo *regVehicleDetailInfo;

@end
