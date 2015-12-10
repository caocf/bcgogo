//
//  TGOilStationTableViewCell.m
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOilStationTableViewCell.h"
#import "TGBasicModel.h"
#import "TGHelper.h"

@implementation TGOilStationTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        float height = 8;
        CGFloat origin_x = 36;
        
        titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 280, 20)];
        titleLabel.textColor = [UIColor blackColor];
        titleLabel.font = [UIFont boldSystemFontOfSize:15];
        titleLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:titleLabel];
        
        height += 20;
        
        distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 100, 16)];
        distanceLabel.textColor = [UIColor grayColor];
        distanceLabel.font = [UIFont boldSystemFontOfSize:12];
        distanceLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:distanceLabel];
        
        height += 20;
        
        addressLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 260, 16)];
        addressLabel.textColor = [UIColor grayColor];
        addressLabel.font = [UIFont boldSystemFontOfSize:12];
        addressLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:addressLabel];
        
        height += 20;
        
        priceLabel = [[UILabel alloc] initWithFrame:CGRectMake(origin_x, height, 270, 16)];
        priceLabel.textColor = [UIColor grayColor];
        priceLabel.font = [UIFont boldSystemFontOfSize:12];
        priceLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:priceLabel];
        
        UIImage *image = [UIImage imageNamed:@"icon_tableviewcell_separateLine.png"];
        UIImageView *_lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 95-image.size.height, 300, image.size.height)];
        _lineImv.backgroundColor = [UIColor clearColor];
        _lineImv.userInteractionEnabled = YES;
        _lineImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        [self addSubview:_lineImv];
        
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    return self;
}

- (void)setDataAndRefresh:(TGModelOilStation *)aOilStation
{
    self.oilStation = aOilStation;
    
    titleLabel.text = self.oilStation.name;
    distanceLabel.text = [NSString stringWithFormat:@"距离：%@",[TGHelper meterToKiloFromInt:self.oilStation.distance]];
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
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
