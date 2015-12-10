//
//  TGOrderOnlineViewController.h
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGCustomTextFieldView.h"
#import "TGCustomDataPickerView.h"
#import "TGCustomDropDownListView.h"

@interface TGOrderOnlineViewController : TGBaseViewController <UITextFieldDelegate, UITextViewDelegate, TGCustomDataPickerDelegate, TGCustomDropDownListViewDelegate>

@property (nonatomic, strong) TGCustomTextFieldView *shopName;
@property (nonatomic, strong) TGCustomTextFieldView *vehicleNo;
@property (nonatomic, strong) TGCustomTextFieldView *serviceType;
@property (nonatomic, strong) TGCustomTextFieldView *appointTime;
@property (nonatomic, strong) TGCustomTextFieldView *contact;
@property (nonatomic, strong) TGCustomTextFieldView *mobile;
@property (nonatomic, strong) UITextView *remark;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) NSArray *faultInfoItems;
@end
