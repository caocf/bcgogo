//
//  TGCustomDataPickerView.h
//  TGOBD
//
//  Created by James Yu on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TGCustomDataPickerDelegate;

@interface TGCustomDataPickerView : UIView

@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIDatePicker *dataPicker;
@property (nonatomic, strong) UINavigationBar *navigationBar;
@property (nonatomic, assign) id <TGCustomDataPickerDelegate> delegate;

- (void)show;

@end

@protocol TGCustomDataPickerDelegate <NSObject>

- (void)TGDataPickerSelected:(NSDate *)timeDate;

@end
