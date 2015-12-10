//
//  BGDateSelectItem.h
//  KKOBD
//
//  Created by Jiahai on 14-1-20.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KKDriveRecordEngine.h"
#import "KKHelper.h"

typedef enum {
    DateSelectType_Day = 1,
    DateSelectType_Week,
    DateSelectType_Month
}DateSelectType;

@interface BGDateSelectItem : UIView
{
    UILabel             *_dateLabel;
    NSDateFormatter     *_dateFormatter;
    
    DateTimeRange       _timeRange;
}
@property (nonatomic, readonly) DateTimeRange     timeRange;

-(void) setTimeRange:(long long)aStartTime endTime:(long long)aEndTime;
-(void) refreshUIWithCalendar:(NSCalendar *)aCalendar dateSelectType:(DateSelectType)dsType;
@end
