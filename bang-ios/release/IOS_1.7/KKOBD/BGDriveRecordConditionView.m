//
//  BGDriveRecordConditionView.m
//  KKOBD
//
//  Created by Jiahai on 14-1-14.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGDriveRecordConditionView.h"
#import "KKModelBaseElement.h"

@implementation BGDriveRecordConditionView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        UIImageView *bgImgV = [[UIImageView alloc] initWithFrame:self.bounds];
        bgImgV.image = [UIImage imageNamed:@"bg_vehiclecondition.png"];
        [self addSubview:bgImgV];
        [bgImgV release];
        
        float imageOriginX = 28,imageOriginXDelta = 70;
        float imageOriginY = 16;
        float imageWidth = 46,imageHeight = 46;
        float labelOriginX = 20,labelOriginXDelta = 70;
        float labelOriginY1 = 80,labelOriginY2 = 116;
        float labelWidth = 62, labelHeight1=36, labelHeight2=21;
        
        //行驶里程
        UIImageView *image1 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image1.image = [UIImage imageNamed:@"icon_driveRecord_xc.png"];
        [self addSubview:image1];
        [image1 release];
        _drivingMileageLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _drivingMileageLabel.backgroundColor = [UIColor clearColor];
        _drivingMileageLabel.textAlignment = UITextAlignmentCenter;
        _drivingMileageLabel.textColor = [UIColor blackColor];
        _drivingMileageLabel.font = [UIFont systemFontOfSize:13.0];
        _drivingMileageLabel.numberOfLines = 2;
        [self addSubview:_drivingMileageLabel];
        [_drivingMileageLabel release];
        UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label1.backgroundColor = [UIColor clearColor];
        label1.textAlignment = UITextAlignmentCenter;
        label1.textColor = [UIColor darkGrayColor];
        label1.font = [UIFont systemFontOfSize:14.0];
        label1.text = @"行驶里程";
        [self addSubview:label1];
        [label1 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        //耗油量
        UIImageView *image2 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image2.image = [UIImage imageNamed:@"icon_driveRecord_oil.png"];
        [self addSubview:image2];
        [image2 release];
        _oilConsumptionLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _oilConsumptionLabel.backgroundColor = [UIColor clearColor];
        _oilConsumptionLabel.textAlignment = UITextAlignmentCenter;
        _oilConsumptionLabel.textColor = [UIColor blackColor];
        _oilConsumptionLabel.font = [UIFont systemFontOfSize:13.0];
        _oilConsumptionLabel.numberOfLines = 2;
        [self addSubview:_oilConsumptionLabel];
        [_oilConsumptionLabel release];
        UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label2.backgroundColor = [UIColor clearColor];
        label2.textAlignment = UITextAlignmentCenter;
        label2.textColor = [UIColor darkGrayColor];
        label2.font = [UIFont systemFontOfSize:14.0];
        label2.text = @"耗油量";
        [self addSubview:label2];
        [label2 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        //油费
        UIImageView *image3 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image3.image = [UIImage imageNamed:@"icon_driverecord_3.png"];
        [self addSubview:image3];
        [image3 release];
        _oilCostsLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _oilCostsLabel.backgroundColor = [UIColor clearColor];
        _oilCostsLabel.textAlignment = UITextAlignmentCenter;
        _oilCostsLabel.textColor = [UIColor blackColor];
        _oilCostsLabel.font = [UIFont systemFontOfSize:13.0];
        _oilCostsLabel.numberOfLines = 2;
        [self addSubview:_oilCostsLabel];
        [_oilCostsLabel release];
        UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label3.backgroundColor = [UIColor clearColor];
        label3.textAlignment = UITextAlignmentCenter;
        label3.textColor = [UIColor darkGrayColor];
        label3.font = [UIFont systemFontOfSize:14.0];
        label3.text = @"油费";
        [self addSubview:label3];
        [label3 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        //行车时长
        UIImageView *image4 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image4.image = [UIImage imageNamed:@"icon_driveRecord_time.png"];
        [self addSubview:image4];
        [image4 release];
        _drivingTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _drivingTimeLabel.backgroundColor = [UIColor clearColor];
        _drivingTimeLabel.textAlignment = UITextAlignmentCenter;
        _drivingTimeLabel.textColor = [UIColor blackColor];
        _drivingTimeLabel.font = [UIFont systemFontOfSize:13.0];
        _drivingTimeLabel.numberOfLines = 2;
        [self addSubview:_drivingTimeLabel];
        [_drivingTimeLabel release];
        UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label4.backgroundColor = [UIColor clearColor];
        label4.textAlignment = UITextAlignmentCenter;
        label4.textColor = [UIColor darkGrayColor];
        label4.font = [UIFont systemFontOfSize:14.0];
        label4.text = @"行车时长";
        [self addSubview:label4];
        [label4 release];
    }
    return self;
}


-(NSString *) getTimeString:(NSInteger)aTravelTime
{
    NSMutableString *str = [[[NSMutableString alloc] init] autorelease];
    
    int value,x,y;
    
    value = aTravelTime;
    
    x = value / 60;
    y = value % 60;
    
    if(x != 0)
    {
        if(y != 0)
            [str appendString:[NSString stringWithFormat:@"%d秒",y]];
        
        value = x;
        x = value / 60;
        y = value % 60;
        if(x != 0)
        {
            if(y != 0)
                [str insertString:[NSString stringWithFormat:@"%d分",y] atIndex:0];
            
            value = x;
            x = value / 60;
            y = value % 60;
            
            if(x != 0)
            {
                if(y != 0)
                    [str insertString:[NSString stringWithFormat:@"%d小时",y] atIndex:0];
                
                value = x;
                x = value / 60;
                y = value % 60;
                
                if(x != 0)
                {
                    if(y != 0)
                        [str insertString:[NSString stringWithFormat:@"%d天",y] atIndex:0];
                }
                else
                {
                    [str insertString:[NSString stringWithFormat:@"%d天",y] atIndex:0];
                }
            }
            else
            {
                [str insertString:[NSString stringWithFormat:@"%d小时",y] atIndex:0];
            }
        }
        else
        {
            [str insertString:[NSString stringWithFormat:@"%d分",y] atIndex:0];
        }
    }
    else
    {
        [str appendString:[NSString stringWithFormat:@"%d秒",y]];
    }
    return str;
}

-(void) setContentWithRealTimeData:(BGDriveRecordDetail *)aRecordDetail
{
    _drivingMileageLabel.text = [NSString stringWithFormat:@"%.1f公里",aRecordDetail.distance];

    _drivingTimeLabel.text = [self getTimeString:aRecordDetail.travelTime];
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
    [super dealloc];
}

@end
