//
//  TGOrderListViewCell.m
//  TGOBD
//
//  Created by James Yu on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOrderListViewCell.h"
#import "NSDate+millisecond.h"

@implementation TGOrderListViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        CGFloat originY = 0;
        
        UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 1, 300, 40.5)];
        bgImageView.image = [UIImage imageNamed:@"bg_title.png"];
        
        _indicateView = [[UIImageView alloc] initWithFrame:CGRectMake(270, 12.5, 15, 15)];
        _indicateView.image = [UIImage imageNamed:@"icon_orderCell_indicate.png"];
        
        [bgImageView addSubview:_indicateView];
        
        _time = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 280, 30)];
        _time.backgroundColor = [UIColor clearColor];
        
        [bgImageView addSubview:_time];
        
        originY += 41.6;
        
        UIView *view1 = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 300, 40)];
        view1.layer.borderWidth = 1;
        view1.layer.borderColor = COLOR_LAYER_BORDER.CGColor;
        
        UILabel *orderType = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 80, 30)];
        orderType.textAlignment = NSTextAlignmentRight;
        orderType.text = @"账单类型:";
        
        _orderType = [[UILabel alloc] initWithFrame:CGRectMake(115, 5, 170, 30)];
        
        [view1 addSubview:orderType];
        [view1 addSubview:_orderType];
        
        originY += 42;
        
        UIView *view2 = [[UIView alloc] initWithFrame:CGRectMake(0, originY, 300, 40)];
        
        _statusOrMoneyTitle = [[UILabel alloc] initWithFrame:CGRectMake(25, 5, 80, 30)];
        _statusOrMoneyTitle.textAlignment = NSTextAlignmentRight;
        
        _statusOrMoney = [[UILabel alloc] initWithFrame:CGRectMake(115, 5, 170, 30)];
        _statusOrMoney.textColor = [UIColor redColor];
        
        [view2 addSubview:_statusOrMoneyTitle];
        [view2 addSubview:_statusOrMoney];
        
        UIView *cellBgView = [[UIView alloc] initWithFrame:CGRectMake(10, 14, 300, 126)];
        cellBgView.layer.borderWidth = 1;
        cellBgView.layer.borderColor = COLOR_LAYER_BORDER.CGColor;
        cellBgView.layer.cornerRadius = 8;
        cellBgView.layer.masksToBounds = YES;
        
        [cellBgView addSubview:bgImageView];
        [cellBgView addSubview:view1];
        [cellBgView addSubview:view2];
        
        [self.contentView addSubview:cellBgView];
        self.contentView.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)setCellContent:(TGModelOrderList *)order orderStatus:(orderStatus)orderStatus
{
    if (orderStatus == unSettlement) {
        _statusOrMoneyTitle.text = @"账单状态:";
        _statusOrMoney.text = order.status;
        _statusOrMoney.textColor = [UIColor blueColor];
        _indicateView.hidden = YES;
    }
    else
    {
        _statusOrMoneyTitle.text = @"账单金额:";
        _statusOrMoney.text = [NSString stringWithFormat:@"¥ %@", order.orderTotal];
        _statusOrMoney.textColor = [UIColor redColor];
        _indicateView.hidden = NO;
    }
    _orderType.text = order.orderType;
    _time.text = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:order.orderTime formatter:nil];
}

@end
