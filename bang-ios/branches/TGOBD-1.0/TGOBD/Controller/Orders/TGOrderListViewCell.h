//
//  TGOrderListViewCell.h
//  TGOBD
//
//  Created by James Yu on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TGBasicModel.h"

typedef enum {
    unSettlement = 1000,        //未结算
    settlement,                 //已经结算
}orderStatus;

@interface TGOrderListViewCell : UITableViewCell

@property (nonatomic, strong) UILabel *time;
@property (nonatomic, strong) UILabel *orderType;
@property (nonatomic, strong) UILabel *statusOrMoneyTitle;
@property (nonatomic, strong) UILabel *statusOrMoney;
@property (nonatomic, strong) UIImageView *indicateView;

- (void)setCellContent:(TGModelOrderList *)order orderStatus:(orderStatus)orderStatus;

@end
