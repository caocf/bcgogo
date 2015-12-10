//
//  KKOilStationTableViewCell.h
//  KKOBD
//
//  Created by Jiahai on 13-12-6.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <UIKit/UIKit.h>
@class KKModelOilStation;

@interface KKOilStationTableViewCell : UITableViewCell
{
    UILabel *titleLabel;
    UILabel *distanceLabel;
    UILabel *addressLabel;
    UILabel *priceLabel;
}
@property (nonatomic,retain) KKModelOilStation *oilStation;
-(void) setDataAndRefresh:(KKModelOilStation *)aOilStation;
@end
