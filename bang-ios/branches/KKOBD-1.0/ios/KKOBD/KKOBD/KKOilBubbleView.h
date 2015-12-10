//
//  KKOilBubbleView.h
//  KKOBD
//
//  Created by Jiahai on 13-12-11.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKModelOilStation;

@protocol KKOilBubbleViewDelegate <NSObject>

-(void) rightBtnClicked;

@end


@interface KKOilBubbleView : UIView
{
    UILabel         *titleLabel;
    UILabel         *detailLabel;
    UILabel         *distanceLabel;
    UIButton        *rightButton;
    NSUInteger      index;
    //UILabel         *priceLabel;
}
@property (nonatomic, assign) id<KKOilBubbleViewDelegate> delegate;
@property (nonatomic, retain) KKModelOilStation *oilStation;


-(void) setUIFit;

@end
