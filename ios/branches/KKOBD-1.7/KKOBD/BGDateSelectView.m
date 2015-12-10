//
//  BGDateSelectView.m
//  KKOBD
//
//  Created by Jiahai on 14-1-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGDateSelectView.h"
#import "SDSegmentedControl.h"
#import "KKDriveRecordEngine.h"

@interface BGDateSelectView ()
@property (nonatomic, assign) long long             dateEndTime;            //日历的结束时间，默认为当前时间
@property (nonatomic, assign) long long             currentDateTime;
@property (nonatomic, retain) NSCalendar            *calendar;
@end

#define CalendarUnit_Month      NSMonthCalendarUnit|NSYearCalendarUnit
#define CalendarUnit_Date       NSDayCalendarUnit|NSMonthCalendarUnit|NSYearCalendarUnit
#define CalendarUnit_DateTime   NSDayCalendarUnit|NSMonthCalendarUnit|NSYearCalendarUnit|NSHourCalendarUnit|NSMinuteCalendarUnit|NSSecondCalendarUnit

#define OneDayMilliSeconds           86400000           //一天有多少毫秒，等于 24*60*60*1000


@implementation BGDateSelectView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        self.calendar = [NSCalendar currentCalendar];
        self.calendar.firstWeekday = 2;
        
        self.dateStartingTime = [self getDayStartTime:[KKDriveRecordEngine sharedInstance].firstRecordTime];
        //self.currentDateTime = [self getDayStartTime:[[NSDate dateWithTimeIntervalSinceNow:-2*24*60*60] timeIntervalSince1970WithMillisecond]];
        
        self.dateEndTime = [self getDayEndTime:[[NSDate date] timeIntervalSince1970WithMillisecond]];
        
        self.dateSelectType = DateSelectType_Day;
        
        SDSegmentedControl* segmentedControl = [[SDSegmentedControl alloc] initWithItems:@[@"日", @"周", @"月"]];
        segmentedControl.frame = CGRectMake(0, 0, 320, 30);
        [segmentedControl addTarget:self action:@selector(changeDateSelectType:) forControlEvents:UIControlEventValueChanged];
        [self addSubview:segmentedControl];
        [segmentedControl release];
        
        
        _dateSelectCarousel=[[iCarousel alloc] initWithFrame:CGRectMake(0, 30, 320, 60)];
        _dateSelectCarousel.dataSource = self;
        _dateSelectCarousel.delegate = self;
        [self addSubview:_dateSelectCarousel];
        [_dateSelectCarousel release];
        
        _dateSelectCarousel.currentItemIndex = _dateSelectCarousel.numberOfItems - 1;
    }
    return self;
}

#pragma mark Event
-(void) changeDateSelectType:(id)sender
{
    SDSegmentedControl* segmentedControl = sender;
    self.dateSelectType = segmentedControl.selectedSegmentIndex + 1;
    
    [_dateSelectCarousel reloadData];
    
    _dateSelectCarousel.currentItemIndex = _dateSelectCarousel.numberOfItems - 1;
    
//    switch (self.dateSelectType) {
//        case DateSelectType_Day:
//        {
//            _dateSelectCarousel.currentItemIndex = (self.currentDateTime - self.dateStartingTime)/OneDayMilliSeconds + 1;
//        }
//            break;
//            
//        default:
//            break;
//    }
    
}

#pragma mark DateTimeOperation

-(long long) getDayStartTime:(long long)aTime
{
    //根据给定的时间，获取当天的 yyyy-MM-dd 00:00:00 时间
    NSDate *date = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTime];
    NSDateComponents *comps = [self.calendar components:CalendarUnit_Date fromDate:date];
    NSDate *date1 = [self.calendar dateFromComponents:comps];
    return [date1 timeIntervalSince1970WithMillisecond];
}

-(long long) getDayEndTime:(long long)aTime
{
    //根据给定的时间，获取当天的 yyyy-MM-dd 23:59:59 时间
    NSDate *date = [NSDate dateWithTimeIntervalSince1970WithMillisecond:aTime];
    NSDateComponents *comps = [self.calendar components:CalendarUnit_DateTime fromDate:date];
    [comps setHour:23];
    [comps setMinute:59];
    [comps setSecond:59];
    return [[self.calendar dateFromComponents:comps] timeIntervalSince1970WithMillisecond];
}

#pragma mark - iCarouselDelegate,iCarouselDataSource
-(NSUInteger) numberOfItemsInCarousel:(iCarousel *)carousel
{
    int num = 0;
    switch (self.dateSelectType) {
        case DateSelectType_Day:
        {
            num = (self.dateEndTime - self.dateStartingTime)/OneDayMilliSeconds + 1;
        }
            break;
        case DateSelectType_Week:
        {
//            int startIndexInWeek = [self.calendar ordinalityOfUnit:NSDayCalendarUnit inUnit:NSWeekCalendarUnit forDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateStartingTime]];
//            int endIndexInWeek = [self.calendar ordinalityOfUnit:NSDayCalendarUnit inUnit:NSWeekCalendarUnit forDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateEndTime]];
//            
//            num = ((self.dateEndTime - self.dateStartingTime) - ((8 - startIndexInWeek)*OneDayMilliSeconds + endIndexInWeek * OneDayMilliSeconds))/(7*OneDayMilliSeconds) + 3;
            
            NSDateComponents *comps =[self.calendar components:(NSWeekCalendarUnit | NSWeekdayCalendarUnit |NSWeekdayOrdinalCalendarUnit)
                               fromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateStartingTime]];
            NSInteger startWeek = [comps week]; // 今年的第几周
            comps =[self.calendar components:(NSWeekCalendarUnit | NSWeekdayCalendarUnit |NSWeekdayOrdinalCalendarUnit)
                                    fromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateEndTime]];
            NSInteger endWeek = [comps week];
            
            num = endWeek - startWeek + 1;

        }
            break;
        case DateSelectType_Month:
        {
//            NSDate *sDate = [NSDate dateWithTimeIntervalSince1970:self.dateStartingTime];
//            NSDate *eDate = [NSDate dateWithTimeIntervalSince1970:self.dateEndTime];
//            NSDateComponents *deltaComps = [self.calendar components:CalendarUnit_Month fromDate:sDate toDate:eDate options:0];
            NSDateComponents *sComps = [self.calendar components:CalendarUnit_Month fromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateStartingTime]];
            NSDateComponents *eComps = [self.calendar components:CalendarUnit_Month fromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateEndTime]];
            int deltaYear = eComps.year - sComps.year;
            if(deltaYear == 0)
            {
                num = eComps.month - sComps.month + 1;
            }
            else
            {
                num = (deltaYear - 1)*12 + (13-sComps.month) + eComps.month;
            }
        }
            break;
        default:
            break;
    }
    return num;
}

-(UIView *) carousel:(iCarousel *)carousel viewForItemAtIndex:(NSUInteger)index reusingView:(UIView *)view
{
    BGDateSelectItem *itemView = nil;
    //create new view if no view is available for recycling
    if (view == nil)
    {
        itemView = [[[BGDateSelectItem alloc] initWithFrame:CGRectMake(0, 0, 72, 60)] autorelease];
    }
    else
    {
        //get a reference to the label in the recycled view
        itemView = (BGDateSelectItem *)view;
    }
    
    //set item label
    //remember to always set any properties of your carousel item
    //views outside of the `if (view == nil) {...}` check otherwise
    //you'll get weird issues with carousel item content appearing
    //in the wrong place in the carousel
    long long startTime = 0,endTime = 0;
    
    switch (self.dateSelectType) {
        case DateSelectType_Day:
        {
            startTime = self.dateStartingTime + index * OneDayMilliSeconds;
            endTime = startTime + OneDayMilliSeconds-1;
        }
            break;
        case DateSelectType_Week:
        {
            int startIndexInWeek = [self.calendar ordinalityOfUnit:NSDayCalendarUnit inUnit:NSWeekCalendarUnit forDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateStartingTime]];
//            int endIndexInWeek = [self.calendar ordinalityOfUnit:NSDayCalendarUnit inUnit:NSWeekCalendarUnit forDate:[NSDate dateWithTimeIntervalSince1970:self.dateEndTime]];

            if(index == 0)
            {
                startTime = self.dateStartingTime;
                endTime = self.dateStartingTime + (8 - startIndexInWeek)*OneDayMilliSeconds -1;
            }
            else
            {
                startTime = self.dateStartingTime + (8 - startIndexInWeek + (index-1)*7)*OneDayMilliSeconds;
                endTime = self.dateStartingTime + (8 - startIndexInWeek + index*7)*OneDayMilliSeconds-1;
            }

            if(endTime > self.dateEndTime)
                endTime = self.dateEndTime;
        }
            break;
        case DateSelectType_Month:
        {
            NSDateComponents *comps = [[NSDateComponents alloc] init];
            [comps setMonth:index];
            NSDateComponents *sComps = [self.calendar components:CalendarUnit_Month fromDate:[NSDate dateWithTimeIntervalSince1970WithMillisecond:self.dateStartingTime]];
            [sComps setDay:1];
            startTime = [[self.calendar dateByAddingComponents:comps toDate:[self.calendar dateFromComponents:sComps] options:0] timeIntervalSince1970WithMillisecond];
            [comps setMonth:index+1];
            endTime = [[self.calendar dateByAddingComponents:comps toDate:[self.calendar dateFromComponents:sComps] options:0] timeIntervalSince1970WithMillisecond] - 1;
            [comps release];
        }
            break;
        default:
            break;
    }
    
    [itemView setTimeRange:startTime endTime:endTime];
    [itemView refreshUIWithCalendar:self.calendar dateSelectType:self.dateSelectType];
    
    return itemView;
    
}

-(void) carousel:(iCarousel *)carousel didSelectItemAtIndex:(NSInteger)index
{
    BGDateSelectItem *itemView = (BGDateSelectItem *)[carousel itemViewAtIndex:index];
    if(self.delegate && [self.delegate respondsToSelector:@selector(BGDateSelectItemSelected:)])
    {
        [self.delegate BGDateSelectItemSelected:itemView.timeRange];
    }
}

- (CGFloat)carousel:(iCarousel *)carousel valueForOption:(iCarouselOption)option withDefault:(CGFloat)value
{
    //customize carousel display
    switch (option)
    {
        case iCarouselOptionWrap:
        {
            //normally you would hard-code this to YES or NO
            return NO;
        }
        case iCarouselOptionSpacing:
        {
            //add a bit of spacing between the item views
            return value * 1.05f;
        }
        case iCarouselOptionFadeMax:
        {
            if (carousel.type == iCarouselTypeCustom)
            {
                //set opacity based on distance from camera
                return 0.0f;
            }
            return value;
        }
        default:
        {
            return value;
        }
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
    self.calendar = nil;
    [super dealloc];
}

@end
