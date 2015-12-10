//
//  TGOrderDetailView.h
//  TGOBD
//
//  Created by James Yu on 14-3-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelOrderDetail;

@interface TGOrderDetailView : UIView

@property (nonatomic, assign) CGFloat viewHeight;

- (TGOrderDetailView *)initViewHeaderWithFrame:(CGRect)frame headInfo:(TGModelOrderDetail *)headInfo;

- (TGOrderDetailView *)initViewDetailWithFrame:(CGRect)frame detailInfo:(TGModelOrderDetail *)detailInfo;

@end
