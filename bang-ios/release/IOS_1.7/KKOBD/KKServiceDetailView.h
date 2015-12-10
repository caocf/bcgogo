//
//  KKServiceDetailView.h
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKModelComplex.h"

@protocol KKServiceDetailViewDelegate;

@interface KKServiceDetailView : UIScrollView
{
    KKModelserviceDetail        *_content;
    float                       _height;
    CGSize                      _maxSize;
    
}
@property (nonatomic ,assign)id<KKServiceDetailViewDelegate> actionDelegate;
@property (nonatomic ,assign)BOOL isFinished;

- (id)initWithFrame:(CGRect)frame WithContent:(KKModelserviceDetail *)content;

@end


@protocol KKServiceDetailViewDelegate
@optional
- (void)KKServiceDetailViewCancelButtonClicked;
- (void)KKServiceDetailViewEvaluatButtonClicked;
- (void)KKServiceDetailViewShopNameButtonClicked;

@end