//
//  TGSetVilolationQueryConditionViewController.h
//  TGOBD
//
//  Created by James Yu on 14-4-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGCustomTextFieldView.h"
#import "TGCustomPickerView.h"

@interface TGSetVilolationQueryConditionViewController : TGBaseViewController <UITextFieldDelegate, TGCustomPickerViewDelegate>

@property (nonatomic, strong) TGCustomTextFieldView *queryCity;
@property (nonatomic, strong) TGCustomTextFieldView *registerNo;
@property (nonatomic, strong) TGCustomTextFieldView *engineNo;
@property (nonatomic, strong) TGCustomTextFieldView *vehicleVin;

@end
