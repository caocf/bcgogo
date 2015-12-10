//
//  KKShowOrAddNewBindCarViewController.h
//  KKOBD
//
//  Created by zhuyc on 13-8-19.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKCustomTextField.h"
#import "KKCustomDataPicker.h"
#import "KKSearchCarModelViewController.h"
#import "KKProtocolEngineDelegate.h"
#import "KKModelBaseElement.h"
#import "KKSearchCarViewController.h"
#import "KKProtocolEngineDelegate.h"
#import "KKScanViewController.h"

typedef enum
{
    KKBindCar_show,
    KKBindCar_addNew,
}KKBindCarType;

@interface KKShowOrAddNewBindCarViewController : UIViewController<KKCustomTextFieldDelegate,KKCustomDataPickerDelegate,KKSearchCarModelDelegate,KKSearchCarViewControllerDelegate,KKProtocolEngineDelegate,KKScanViewControllerDelegate>
{
    UIScrollView            *_mainScrollView;
    
    KKCustomTextField       *_textField0;       //index 10
    KKCustomTextField       *_textField1;       //index 11
    KKCustomTextField       *_textField2_1;     //index 12
    KKCustomTextField       *_textField2_2;     //index 13
    KKCustomTextField       *_textField3;       //index 18
    KKCustomTextField       *_textField4;       //index 14
    KKCustomTextField       *_textField5;       //index 15
    KKCustomTextField       *_textField6;       //index 16
    KKCustomTextField       *_textField7;       //index 17 当前里程
    KKCustomTextField       *_vehicleVinText;
    KKCustomTextField       *_engineText;       //发动机号
    KKCustomTextField       *_registText;       //行驶证号
    UIView                  *_moreMaskView;
    UIView                  *_moreInfoView;
    UIButton                *_submitBtn;
    
    NSInteger               _viewIndex;
    UIButton                *_addNewButton;
    UIView                  *_showAddButtonView;
    UIView                  *_carBrandMarkView; //品牌水印
    UIView                  *_carModelMarkView; //车型水印
}
@property (nonatomic ,assign) KKBindCarType         type;
@property (nonatomic ,retain) NSString *vehicleId;
@property (nonatomic ,retain) KKModelVehicleDetailInfo  *vehicleDetailInfo;
@property (nonatomic ,retain) NSString *vehicleVin;
@property (nonatomic ,retain) NSString *obdSN;
@property (nonatomic, assign) BOOL hiddenMoreInfo;

@property (nonatomic, assign) NSInteger popViewControllerNum;   //返回时跳过几个界面

@end
