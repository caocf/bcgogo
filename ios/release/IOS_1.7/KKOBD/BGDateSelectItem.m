//
//  BGDateSelectItem.m
//  KKOBD
//
//  Created by Jiahai on 14-1-20.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGDateSelectItem.h"
#import "KKHelper.h"

#define CalendarUnit_Date       NSDayCalendarUnit|NSMonthCalendarUnit|NSYearCalendarUnit
#define CalendarUnit_DateTime   NSDayCalendarUnit|NSMonthCalendarUnit|NSYearCalendarUnit|NSHourCalendarUnit|NSMinuteCalendarUnit|NSSecondCalendarUnit

#define OneDaySeconds           86400           //一天有多少秒，等于 24*60*60

@implementation BGDateSelectItem

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        _dateFormatter = [[NSDateFormatter alloc] init];
        
        _dateLabel = [[UILabel alloc] initWithFrame:self.bounds];
        _dateLabel.backgroundColor = [UIColor clearColor];
        _dateLabel.textAlignment = UITextAlignmentCenter;
        _dateLabel.font = [_dateLabel.font fontWithSize:12];
        _dateLabel.tag = 1;
        _dateLabel.lineBreakMode = UILineBreakModeWordWrap;
        _dateLabel.numberOfLines = 0;
        [self addSubview:_dateLabel];
        [_dateLabel release];
    }
    return self;
}

-(void) setTimeRange:(long long)aStartTime endTime:(long long)aEndTime
{
    _timeRange.startTime = aStartTime;
    _timeRange.endTime = aEndTime;
}

-(void) refreshUIWithCalendar:(NSCalendar *)aCalendar dateSelectType:(DateSelectType)dsType
{
    _dateLabel.text = [self dateStringOfCalendar:aCalendar dateSelectType:dsType];
}

-(NSString *) dateStringOfCalendar:(NSCalendar *)aCalendar dateSelectType:(DateSelectType)dsType
{
    
    NSDate *startDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:self.timeRange.startTime];
    NSDate *endDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:self.timeRange.endTime];
//    NSDateComponents *startComps = [aCalendar components:CalendarUnit_Date fromDate:startDate];
//    NSDateComponents *endComps = [aCalendar components:CalendarUnit_Date fromDate:endDate];
    
    switch (dsType) {
        case DateSelectType_Day:
        {
            [_dateFormatter setDateFormat:@"yyyy-MM-dd"];
            return [_dateFormatter stringFromDate:startDate];
            //return [NSString stringWithFormat:@"%d-%d-%d",startComps.year,startComps.month,startComps.day];
        }
            break;
        case DateSelectType_Week:
        {
            
            [_dateFormatter setDateFormat:@"yyyy-MM-dd"];
            return [NSString stringWithFormat:@"%@\n|\n%@",[_dateFormatter stringFromDate:startDate],[_dateFormatter stringFromDate:endDate]];
            //return [NSString stringWithFormat:@"%d-%d-%d\n|\n%d-%d-%d",startComps.year,startComps.month,startComps.day,endComps.year,endComps.month,endComps.day];
        }
            break;
        case DateSelectType_Month:
        {
            [_dateFormatter setDateFormat:@"yy年M月"];
            return [_dateFormatter stringFromDate:startDate];
            //return [NSString stringWithFormat:@"%d-%d | %d-%d",startComps.year,startComps.month,endComps.year,endComps.month];
        }
            break;
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

-(void) dealloc
{
    [_dateFormatter release],_dateFormatter = nil;
    [super dealloc];
}

@end
