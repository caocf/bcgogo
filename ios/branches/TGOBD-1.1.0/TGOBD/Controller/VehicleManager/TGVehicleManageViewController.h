//
//  TGVehicleManageViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-12.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGCustomTextFieldView.h"
#import "TGCustomDataPickerView.h"
#import "TGCustomPickerView.h"

typedef enum {
    insuranceTime,
    examTime
} selectTimeType;

typedef enum {
    registerUser,
    updateVehicle
} vehicleManageType;

@interface TGVehicleManageViewController : TGBaseViewController <UITextFieldDelegate, TGCustomDataPickerDelegate, TGCustomPickerViewDelegate>

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) TGCustomTextFieldView *vehicleNo;
@property (nonatomic, strong) TGCustomTextFieldView *modelName;
@property (nonatomic, strong) TGCustomTextFieldView *oilPrice;
@property (nonatomic, strong) TGCustomTextFieldView *currentMileage;
@property (nonatomic, strong) TGCustomTextFieldView *maintainCycle;
@property (nonatomic, strong) TGCustomTextFieldView *lastMaintainMileage;
@property (nonatomic, strong) TGCustomTextFieldView *nextInsuranceTime;
@property (nonatomic, strong) TGCustomTextFieldView *nextExamineTime;
@property (nonatomic, assign) vehicleManageType manageType;

@property (nonatomic, strong) TGCustomTextFieldView *queryCity;
@property (nonatomic, strong) TGCustomTextFieldView *engineNo;
@property (nonatomic, strong) TGCustomTextFieldView *vehicleVin;
@property (nonatomic, strong) TGCustomTextFieldView *registerNo;

@end
