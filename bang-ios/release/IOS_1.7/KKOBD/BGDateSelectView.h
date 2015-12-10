//
//  BGDateSelectView.h
//  KKOBD
//
//  Created by Jiahai on 14-1-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iCarousel.h"
#import "BGDateSelectItem.h"

@protocol BGDateSelectViewDelegate;

@interface BGDateSelectView : UIView<iCarouselDataSource,iCarouselDelegate>
{
    iCarousel           *_dateSelectCarousel;
    
//    NSInteger           _nextStartTime;
    
}
@property (nonatomic, assign) id<BGDateSelectViewDelegate> delegate;
@property (nonatomic, assign) long long         dateStartingTime;       //日历的起始时间
@end

@protocol BGDateSelectViewDelegate <NSObject>

@required
-(void) BGDateSelectItemSelected:(DateTimeRange) aTimeRange;

@end