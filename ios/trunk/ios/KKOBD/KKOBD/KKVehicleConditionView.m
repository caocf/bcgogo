//
//  KKVehicleConditionView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKVehicleConditionView.h"
#import "KKPreference.h"
#import "KKHelper.h"

@implementation KKVehicleConditionItem

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        UILabel *detailLb = [[UILabel alloc] initWithFrame:CGRectMake(0, 8, frame.size.width, 13)];
        detailLb.backgroundColor = [UIColor clearColor];
        detailLb.textAlignment = UITextAlignmentCenter;
        detailLb.textColor = [UIColor whiteColor];
        [detailLb setFont:[UIFont boldSystemFontOfSize:13.f]];
        detailLb.minimumFontSize = 4;
        self.detailLabel = detailLb;
        [self addSubview:detailLb];
        [detailLb release];
        
        UILabel *titleLb = [[UILabel alloc] initWithFrame:CGRectMake(0, 30, frame.size.width, 13)];
        titleLb.backgroundColor = [UIColor clearColor];
        titleLb.textAlignment = UITextAlignmentCenter;
        titleLb.textColor = [UIColor grayColor];
        [titleLb setFont:[UIFont boldSystemFontOfSize:13.f]];
        self.titleLabel = titleLb;
        [self addSubview:titleLb];
        [titleLb release];
    }
    return self;
}

- (void)dealloc
{
    self.titleLabel = nil;
    self.detailLabel = nil;
    [super dealloc];
}

@end

@implementation KKVehicleConditionView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        UIImage *image = [UIImage imageNamed:@"seg_background.png"];
        UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        bgImv.userInteractionEnabled = YES;
        bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        
        float width = frame.size.width/4;
        
        _item1 = [[KKVehicleConditionItem alloc] initWithFrame:CGRectMake(0, 0, width-10, frame.size.height)];
        _item1.titleLabel.text = @"瞬时油耗";
        [bgImv addSubview:_item1];
        [_item1 release];
        
        _item2 = [[KKVehicleConditionItem alloc] initWithFrame:CGRectMake(width-10, 0, width+20, frame.size.height)];
        _item2.titleLabel.text = @"百公里油耗";
        [bgImv addSubview:_item2];
        [_item2 release];
        
        _item3 = [[KKVehicleConditionItem alloc] initWithFrame:CGRectMake(2*width+10, 0, width-5, frame.size.height)];
        _item3.titleLabel.text = @"剩余油量";
        [bgImv addSubview:_item3];
        [_item3 release];
        
        _item4 = [[KKVehicleConditionItem alloc] initWithFrame:CGRectMake(3*width+5, 0, width-5, frame.size.height)];
        _item4.titleLabel.text = @"水箱温度";
        [bgImv addSubview:_item4];
        [_item4 release];
        
        image = [UIImage imageNamed:@"seg_separateLine.png"];
        
        for (int i = 1; i <= 3; i ++) {
            UIImageView *imv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, image.size.width, 35)];
            imv.backgroundColor = [UIColor clearColor];
            imv.userInteractionEnabled = YES;
            imv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
            if (i == 1)
                imv.center = CGPointMake(i * width -10, 0.5*frame.size.height);
            else if (i == 2)
                imv.center = CGPointMake(i * width + 10, 0.5*frame.size.height);
            else
                imv.center = CGPointMake(i * width + 5, 0.5*frame.size.height);
            [bgImv addSubview:imv];
            [imv release];
        }
        
        [self addSubview:bgImv];
        [bgImv release];
        
    }
    return self;
}

- (void)setContent:(KKModelVehicleRealtimeData *)content
{
    if (content)
    {
        NSString *oilRange = [KKPreference sharedPreference].appConfig.remainOilMassWarn;
        NSArray *rangArray = [KKHelper getArray:oilRange BySeparateString:@"_"];
        if ([rangArray count] == 0)
            rangArray = [NSArray arrayWithObjects:@"15",@"25",nil];
    
        _item1.detailLabel.text = (content.oilWearOfInstant != KKOBDDataNA) ? [NSString stringWithFormat:@"%.2fl/h",content.oilWearOfInstant*3.6] : @"--l/h";
        
        if (content.speed != KKOBDDataNA)
        {
            if (content.oilWearPer100 != KKOBDDataNA)
            {
                if (content.speed < 10)
                    _item2.detailLabel.text = [NSString stringWithFormat:@"%.2fl/h",content.oilWearPer100];
                else
                {
                    _item2.detailLabel.text = [NSString stringWithFormat:@"%.2fl/100Km",content.oilWearPer100];
                }
            }
            else
                _item2.detailLabel.text = @"--l/h";
            
        }
        else
        {
            _item2.detailLabel.text = (content.oilWear != KKOBDDataNA) ? [NSString stringWithFormat:@"%.2fl/h",content.oilWear] : @"--l/h";
        }
        [_item2.detailLabel sizeToFit];
        _item2.detailLabel.center = CGPointMake(0.5*_item2.frame.size.width, 0.25*_item2.frame.size.height);
            
        if (content.oilMass != KKOBDDataNA)
        {
            _item3.detailLabel.text = [NSString stringWithFormat:@"%.2f%%",content.oilMass];
            
            if (content.oilMass <= [[rangArray objectAtIndex:1] intValue])
            {
                _item3.detailLabel.textColor = [UIColor orangeColor];
            }
            else
                _item3.detailLabel.textColor = [UIColor whiteColor];
        }
        else
        {
            _item3.detailLabel.textColor = [UIColor whiteColor];
            _item3.detailLabel.text = @"--%";
        }
        _item4.detailLabel.text = (content.engineTempture != KKOBDDataNA) ? [NSString stringWithFormat:@"%.2f℃",content.engineTempture] : @"--℃";
    }
    else
    {
        _item1.detailLabel.text = @"--l/h";
        _item2.detailLabel.textColor = [UIColor whiteColor];
        _item2.detailLabel.text = @"--l/h";
        _item3.detailLabel.text = @"--%";
        _item4.detailLabel.text = @"--℃";
    }

}

- (void)dealloc
{
    _item1 = nil;
    _item2 = nil;
    _item3 = nil;
    _item4 = nil;
    [super dealloc];
}
@end
