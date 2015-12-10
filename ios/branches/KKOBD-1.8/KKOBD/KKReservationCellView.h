//
//  KKReservationCellView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol KKReservationCellViewDelegate;

@interface KKReservationCellView : UIControl
{
    UIImageView     *_bgImv;
    UIImageView     *_categoryImv;
    
    UILabel         *_chineseNameLb;
    UILabel         *_englishNameLb;
    
    UIImageView     *_arrowImv;
}
@property (nonatomic ,assign)id<KKReservationCellViewDelegate> delegate;
@property (nonatomic ,assign)NSInteger  index;

- (void)setContentViewWithImage:(UIImage *)image WithChinese:(NSString *)cName withEnglish:(NSString *)eName;

@end


@protocol KKReservationCellViewDelegate
@optional
- (void)KKReservationCellViewItemClicked:(NSInteger)index;

@end