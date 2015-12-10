//
//  KKCustomDataPicker.h
//  KKOBD
//
//  Created by zhuyc on 13-8-14.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol KKCustomDataPickerDelegate;

@interface KKCustomDataPicker : UIView
{
    UIView          *_contentView;
    UIDatePicker    *_dataPicker;
}
@property (nonatomic ,assign) id<KKCustomDataPickerDelegate> delegate;

- (void)show;


@end


@protocol KKCustomDataPickerDelegate
@optional
- (void)KKCustomDataPickerDataSelected:(NSDate *)timeData;

@end