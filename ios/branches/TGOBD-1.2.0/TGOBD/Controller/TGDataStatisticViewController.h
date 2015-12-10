//
//  TGDataStatisticViewController.h
//  TGOBD
//
//  Created by James Yu on 14-4-28.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGBaseViewController.h"
#import "TGCustomSegmentView.h"

@interface TGDataStatisticViewController : TGBaseViewController <TGCustomSegmentViewDelegate, UIScrollViewDelegate>

@property (nonatomic, strong) TGCustomSegmentView *segmentView;

@end
