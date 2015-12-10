//
//  TGOilStationTableViewCell.h
//  TGOBD
//
//  Created by Jiahai on 14-3-6.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
@class TGModelOilStation;
@interface TGOilStationTableViewCell : UITableViewCell
{
    UILabel *titleLabel;
    UILabel *distanceLabel;
    UILabel *addressLabel;
    UILabel *priceLabel;
}

@property (nonatomic, strong) TGModelOilStation *oilStation;

- (void)setDataAndRefresh:(TGModelOilStation *)aOilStation;
@end
