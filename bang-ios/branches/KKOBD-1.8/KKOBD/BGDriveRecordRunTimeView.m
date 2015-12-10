//
//  BGDriveRecordRunTimeView.m
//  KKOBD
//
//  Created by Jiahai on 14-2-10.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGDriveRecordRunTimeView.h"
#import "KKModelBaseElement.h"
#import "KKBLEEngine.h"

#define NAStr  @"--"

@implementation BGDriveRecordRunTimeView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        UIImageView *bgImgV = [[UIImageView alloc] initWithFrame:self.bounds];
        bgImgV.image = [UIImage imageNamed:@"bg_vehiclecondition.png"];
        [self addSubview:bgImgV];
        [bgImgV release];
        
        UIScrollView *_scrollView = [[UIScrollView alloc] initWithFrame:self.bounds];
        [_scrollView setContentSize:CGSizeMake(self.bounds.size.width, self.bounds.size.height)];
        _scrollView.pagingEnabled = YES;
        _scrollView.showsVerticalScrollIndicator = NO;
        _scrollView.showsHorizontalScrollIndicator = NO;
        [self addSubview:_scrollView];
        [_scrollView release];
        
        float imageOriginX = 10,imageOriginXDelta = 64;
        float imageOriginY = 16;
        float imageWidth = 42,imageHeight = 42;
        float labelOriginX = 2,labelOriginXDelta = 64;
        float labelOriginY1 = 72,labelOriginY2 = 110;
        float labelWidth = 62, labelHeight1=36, labelHeight2=21;
        
        //电瓶电压
        UIImageView *image5 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image5.image = [UIImage imageNamed:@"icon_driveRecord_dy.png"];
        [_scrollView addSubview:image5];
        [image5 release];
        _oilOverplusLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _oilOverplusLabel.backgroundColor = [UIColor clearColor];
        _oilOverplusLabel.textAlignment = UITextAlignmentCenter;
        _oilOverplusLabel.textColor = [UIColor blackColor];
        _oilOverplusLabel.font = [UIFont systemFontOfSize:13.0];
        _oilOverplusLabel.numberOfLines = 2;
        [_scrollView addSubview:_oilOverplusLabel];
        [_oilOverplusLabel release];
        UILabel *label5 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label5.backgroundColor = [UIColor clearColor];
        label5.textAlignment = UITextAlignmentCenter;
        label5.textColor = [UIColor darkGrayColor];
        label5.font = [UIFont systemFontOfSize:14.0];
        label5.text = @"电瓶电压";
        [_scrollView addSubview:label5];
        [label5 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        //水箱温度
        UIImageView *image6 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image6.image = [UIImage imageNamed:@"icon_driveRecord_wd.png"];
        [_scrollView addSubview:image6];
        [image6 release];
        _waterTemperatureLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _waterTemperatureLabel.backgroundColor = [UIColor clearColor];
        _waterTemperatureLabel.textAlignment = UITextAlignmentCenter;
        _waterTemperatureLabel.textColor = [UIColor blackColor];
        _waterTemperatureLabel.font = [UIFont systemFontOfSize:13.0];
        _waterTemperatureLabel.numberOfLines = 2;
        [_scrollView addSubview:_waterTemperatureLabel];
        [_waterTemperatureLabel release];
        UILabel *label6 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label6.backgroundColor = [UIColor clearColor];
        label6.textAlignment = UITextAlignmentCenter;
        label6.textColor = [UIColor darkGrayColor];
        label6.font = [UIFont systemFontOfSize:14.0];
        label6.text = @"水箱温度";
        [_scrollView addSubview:label6];
        [label6 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        //平均油耗
        UIImageView *image2 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image2.image = [UIImage imageNamed:@"icon_driveRecord_oil.png"];
        [_scrollView addSubview:image2];
        [image2 release];
        _oilWearLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _oilWearLabel.backgroundColor = [UIColor clearColor];
        _oilWearLabel.textAlignment = UITextAlignmentCenter;
        _oilWearLabel.textColor = [UIColor blackColor];
        _oilWearLabel.font = [UIFont systemFontOfSize:13.0];
        _oilWearLabel.numberOfLines = 2;
        [_scrollView addSubview:_oilWearLabel];
        [_oilWearLabel release];
        UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label2.backgroundColor = [UIColor clearColor];
        label2.textAlignment = UITextAlignmentCenter;
        label2.textColor = [UIColor darkGrayColor];
        label2.font = [UIFont systemFontOfSize:14.0];
        label2.text = @"平均油耗";
        [_scrollView addSubview:label2];
        [label2 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        //行驶里程
        UIImageView *image1 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image1.image = [UIImage imageNamed:@"icon_driveRecord_xc.png"];
        [_scrollView addSubview:image1];
        [image1 release];
        _drivingMileageLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _drivingMileageLabel.backgroundColor = [UIColor clearColor];
        _drivingMileageLabel.textAlignment = UITextAlignmentCenter;
        _drivingMileageLabel.textColor = [UIColor blackColor];
        _drivingMileageLabel.font = [UIFont systemFontOfSize:13.0];
        _drivingMileageLabel.numberOfLines = 2;
        [_scrollView addSubview:_drivingMileageLabel];
        [_drivingMileageLabel release];
        UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label1.backgroundColor = [UIColor clearColor];
        label1.textAlignment = UITextAlignmentCenter;
        label1.textColor = [UIColor darkGrayColor];
        label1.font = [UIFont systemFontOfSize:14.0];
        label1.text = @"行驶里程";
        [_scrollView addSubview:label1];
        [label1 release];
        
        imageOriginX += imageOriginXDelta;
        labelOriginX += labelOriginXDelta;
        
        
//        //油费
//        UIImageView *image3 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
//        image3.image = [UIImage imageNamed:@"icon_driverecord_yf.png"];
//        [_scrollView addSubview:image3];
//        [image3 release];
//        _oilCostsLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
//        _oilCostsLabel.backgroundColor = [UIColor clearColor];
//        _oilCostsLabel.textAlignment = UITextAlignmentCenter;
//        _oilCostsLabel.textColor = [UIColor blackColor];
//        _oilCostsLabel.font = [UIFont systemFontOfSize:13.0];
//        _oilCostsLabel.numberOfLines = 2;
//        [_scrollView addSubview:_oilCostsLabel];
//        [_oilCostsLabel release];
//        UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
//        label3.backgroundColor = [UIColor clearColor];
//        label3.textAlignment = UITextAlignmentCenter;
//        label3.textColor = [UIColor darkGrayColor];
//        label3.font = [UIFont systemFontOfSize:14.0];
//        label3.text = @"油费";
//        [_scrollView addSubview:label3];
//        [label3 release];
//        
//        imageOriginX += imageOriginXDelta;
//        labelOriginX += labelOriginXDelta;
        
        //行车时长
        UIImageView *image4 = [[UIImageView alloc] initWithFrame:CGRectMake(imageOriginX, imageOriginY, imageWidth, imageHeight)];
        image4.image = [UIImage imageNamed:@"icon_driveRecord_time.png"];
        [_scrollView addSubview:image4];
        [image4 release];
        _drivingTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY1, labelWidth, labelHeight1)];
        _drivingTimeLabel.backgroundColor = [UIColor clearColor];
        _drivingTimeLabel.textAlignment = UITextAlignmentCenter;
        _drivingTimeLabel.textColor = [UIColor blackColor];
        _drivingTimeLabel.font = [UIFont systemFontOfSize:13.0];
        _drivingTimeLabel.numberOfLines = 2;
        [_scrollView addSubview:_drivingTimeLabel];
        [_drivingTimeLabel release];
        UILabel *label4 = [[UILabel alloc] initWithFrame:CGRectMake(labelOriginX, labelOriginY2, labelWidth, labelHeight2)];
        label4.backgroundColor = [UIColor clearColor];
        label4.textAlignment = UITextAlignmentCenter;
        label4.textColor = [UIColor darkGrayColor];
        label4.font = [UIFont systemFontOfSize:14.0];
        label4.text = @"行车时长";
        [_scrollView addSubview:label4];
        [label4 release];
    }
    return self;
}


-(NSString *) getTimeString:(NSInteger)aTravelTime
{
    NSMutableString *str = [[[NSMutableString alloc] init] autorelease];
    
    int value,x,y;
    
    value = (NSInteger)(aTravelTime / 1000);
    
    x = value / 60;
    y = value % 60;
    
    if(x != 0)
    {
        if(y != 0)
        {
            //[str appendString:[NSString stringWithFormat:@"%d秒",y]];
        }
        
        value = x;
        x = value / 60;
        y = value % 60;
        if(x != 0)
        {
            if(y != 0)
            {
                [str insertString:[NSString stringWithFormat:@"%d分",y] atIndex:0];
            }
            
            value = x;
            x = value / 60;
            y = value % 60;
            
            if(x != 0)
            {
                if(y != 0)
                    [str insertString:[NSString stringWithFormat:@"%d时",y] atIndex:0];
                
//                value = x;
//                x = value / 24;
//                y = value % 24;
//                
//                if(x != 0)
//                {
//                    if(y != 0)
//                        [str insertString:[NSString stringWithFormat:@"%d天",y] atIndex:0];
//                }
//                else
//                {
//                    [str insertString:[NSString stringWithFormat:@"%d天",y] atIndex:0];
//                }
            }
            else
            {
                [str insertString:[NSString stringWithFormat:@"%d时",y] atIndex:0];
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

-(void) setContentWithRealTimeData:(KKModelVehicleRealtimeData *)aRunTimeData recordDetail:(BGDriveRecordDetail *)aRecordDetail
{
    if(aRunTimeData)
    {
        _oilOverplusLabel.text = [NSString stringWithFormat:@"%.1fV",aRunTimeData.voltageOfBattery];
        _waterTemperatureLabel.text = [NSString stringWithFormat:@"%.1f℃",aRunTimeData.engineTempture];
        _drivingMileageLabel.text = [NSString stringWithFormat:@"%.fKM",aRecordDetail.distance];
        _oilWearLabel.text = [NSString stringWithFormat:@"%.1f\r\nL/100KM",aRunTimeData.oilWearPer100];
    //    _oilCostsLabel.text = [NSString stringWithFormat:@"%.0f元",aRecordDetail.totalOilMoney];
        _drivingTimeLabel.text = [self getTimeString:aRecordDetail.travelTime];
    }
    else
    {
        _oilOverplusLabel.text = NAStr;
        _waterTemperatureLabel.text = NAStr;
        _drivingMileageLabel.text = NAStr;
        _oilWearLabel.text = NAStr;
        //    _oilCostsLabel.text = NAStr;
        _drivingTimeLabel.text = NAStr;
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
    [super dealloc];
}

@end
