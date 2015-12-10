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
        
        _monthLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.bounds.size.width, self.bounds.size.height * 0.3)];
        _monthLabel.backgroundColor = [UIColor clearColor];
        _monthLabel.textAlignment = UITextAlignmentCenter;
        _monthLabel.font = [_monthLabel.font fontWithSize:14];
        [self addSubview:_monthLabel];
        [_monthLabel release];
        
        _dateLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, self.bounds.size.height * 0.3, self.bounds.size.width, self.bounds.size.height * 0.7)];
        _dateLabel.backgroundColor = [UIColor clearColor];
        _dateLabel.textAlignment = UITextAlignmentCenter;
        _dateLabel.font = [_dateLabel.font fontWithSize:18];
        _dateLabel.tag = 1;
//        _dateLabel.lineBreakMode = UILineBreakModeWordWrap;
//        _dateLabel.numberOfLines = 0;
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
    NSArray *array = [[self dateStringOfCalendar:aCalendar dateSelectType:dsType] componentsSeparatedByString:@"|"];
    if([array count] > 1)
    {
        _monthLabel.text = [array objectAtIndex:0];
        _dateLabel.text = [array objectAtIndex:1];
    }
}

-(NSString *) dateStringOfCalendar:(NSCalendar *)aCalendar dateSelectType:(DateSelectType)dsType
{
    
    NSDate *startDate = [NSDate dateWithTimeIntervalSince1970WithMillisecond:self.timeRange.startTime];
    NSString *str1=nil,*str2=nil;
    
    switch (dsType) {
        case DateSelectType_Day:
        {
            [_dateFormatter setDateFormat:@"M月"];
            str1 = [_dateFormatter stringFromDate:startDate];
            [_dateFormatter setDateFormat:@"dd"];
            str2 = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"%@|%@",str1,str2];
            //return [NSString stringWithFormat:@"%d-%d-%d",startComps.year,startComps.month,startComps.day];
        }
            break;
        case DateSelectType_Week:
        {
            
            [_dateFormatter setDateFormat:@"M月"];
            str1 = [_dateFormatter stringFromDate:startDate];
            [_dateFormatter setDateFormat:@"dd"];
            str2 = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"%@|%@",str1,str2];
            
        }
            break;
        case DateSelectType_Month:
        {
            
            [_dateFormatter setDateFormat:@"M月"];
            str1 = [_dateFormatter stringFromDate:startDate];
            return [NSString stringWithFormat:@"|%@",str1];
//            [_dateFormatter setDateFormat:@"yy年M月"];
//            return [_dateFormatter stringFromDate:startDate];
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
