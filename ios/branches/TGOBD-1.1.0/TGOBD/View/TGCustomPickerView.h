//
//  TGCustomPickerView.h
//  TGOBD
//
//  Created by James Yu on 14-4-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TGCustomPickerViewDelegate;

@interface TGCustomPickerView : UIView <UIPickerViewDataSource, UIPickerViewDelegate>

@property (nonatomic, strong) UIPickerView *picker;
@property (nonatomic, strong) NSMutableArray *city;
@property (nonatomic, strong) NSMutableArray *childrenCity;
@property (nonatomic, assign) id <TGCustomPickerViewDelegate> delegate;

- (void)show;

@end

@protocol TGCustomPickerViewDelegate <NSObject>

- (void)selectVlues:(NSString *)cityName juheCode:(NSString *)juheCode;

@end