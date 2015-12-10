//
//  TGOilBubbleView.h
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelOilStation;

@protocol TGOilBubbleViewDelegate <NSObject>

-(void) rightBtnClicked;

@end

@interface TGOilBubbleView : UIView
{
    UILabel         *titleLabel;
    UILabel         *detailLabel;
    UILabel         *distanceLabel;
    UIButton        *rightButton;
    NSUInteger      index;
}

@property (nonatomic, weak) id<TGOilBubbleViewDelegate> delegate;
@property (nonatomic, strong) TGModelOilStation *oilStation;

- (void)setUIFit;
@end
