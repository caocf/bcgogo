//
//  KKOilStationTableViewCell.m
//  KKOBD
//
//  Created by Jiahai on 13-12-6.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKOilStationTableViewCell.h"
#import "KKApplicationDefine.h"
#import "KKModelBaseElement.h"
#import "KKHelper.h"

@implementation KKOilStationTableViewCell
@synthesize oilStation;

#define origin_x     36

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        float height = 8;
        
        titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 280, 20)];
        titleLabel.textColor = KKCOLOR_3359ac;
        titleLabel.font = [UIFont boldSystemFontOfSize:15];
        titleLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:titleLabel];
        [titleLabel release];
        
        height += 20;
        
        distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 100, 16)];
        distanceLabel.textColor = KKCOLOR_717171;
        distanceLabel.font = [UIFont boldSystemFontOfSize:12];
        distanceLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:distanceLabel];
        [distanceLabel release];
        
        height += 20;
        
        addressLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 260, 16)];
        addressLabel.textColor = KKCOLOR_717171;
        addressLabel.font = [UIFont boldSystemFontOfSize:12];
        addressLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:addressLabel];
        [addressLabel release];
        
        height += 20;
        
        priceLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 270, 16)];
        priceLabel.textColor = KKCOLOR_717171;
        priceLabel.font = [UIFont boldSystemFontOfSize:12];
        priceLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:priceLabel];
        [priceLabel release];
        
        UIImage *image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
        UIImageView *_lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 95-image.size.height, 300, image.size.height)];
        _lineImv.backgroundColor = [UIColor clearColor];
        _lineImv.userInteractionEnabled = YES;
        _lineImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        [self addSubview:_lineImv];
        [_lineImv release];

        
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
    }
    return self;
}

-(void) setDataAndRefresh:(KKModelOilStation *)aOilStation
{
    self.oilStation = aOilStation;
    
    titleLabel.text = self.oilStation.name;
    distanceLabel.text = [NSString stringWithFormat:@"距离：%@",[KKHelper meterToKiloFromInt:self.oilStation.distance]];
    addressLabel.text = self.oilStation.address;
    NSMutableString *strBuf = [[NSMutableString alloc] initWithString:@"今日油价:"];
    NSArray *priceKeys = [self.oilStation.gastprice allKeys];
    for(int i=0;i<[priceKeys count];i++)
    {
        [strBuf appendString:[NSString stringWithFormat:@"%@:%@, ",[priceKeys objectAtIndex:i] ,[self.oilStation.gastprice objectForKey:[priceKeys objectAtIndex:i]]]];
    }
    NSString *str = strBuf;
    if(strBuf.length > 2)
    {
        str = [strBuf substringWithRange:NSMakeRange(0, strBuf.length-2)];
    }
    priceLabel.text = str;
    [strBuf release];
    //priceLabel.text = [NSString stringWithFormat:@"燃油价格：0#:%@  93#:%@  97#:%@",self.oilStation.price.E0,self.oilStation.price.E93,self.oilStation.price.E97];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void) dealloc
{
    self.oilStation = nil;
    [super dealloc];
}

@end
