//
//  TGOrderDetailView.m
//  TGOBD
//
//  Created by James Yu on 14-3-19.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOrderDetailView.h"
#import "TGBasicModel.h"
#import "NSDate+millisecond.h"

#define ROWVIE_WHEIGHT 40
#define BORDER_COLOR [UIColor colorWithRed:190/255.0 green:190/255.0 blue:190/255.0 alpha:1].CGColor
//#define BORDER_COLOR [UIColor colorWithRed:255/255.0 green:190/255.0 blue:255/255.0 alpha:1].CGColor
typedef enum
{
    orderHeader = 10000,
    orderItem,
}orderDetailViewCategory;

@implementation TGOrderDetailView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
    }
    return self;
}

- (TGOrderDetailView *)initViewHeaderWithFrame:(CGRect)frame headInfo:(TGModelOrderDetail *)headInfo
{
    if (self = [super initWithFrame:frame]) {
        
        _viewHeight = 0;
        
        UIImageView *imgView = [self createHeaderImage];
        UILabel *leftLbl = [self createLeftTitle:@"单据号:"];
        leftLbl.textAlignment = NSTextAlignmentLeft;
        [imgView addSubview:leftLbl];
        
        UILabel *orderId = [[UILabel alloc] initWithFrame:CGRectMake(100, 5, 190, 30)];
        orderId.backgroundColor = [UIColor clearColor];
        [imgView addSubview:orderId];
        orderId.text = headInfo.receiptNo;
        [self addSubview:imgView];
        
        _viewHeight += 39;
        [self createHeaderRowWithTitle:@"账单类型:" rightTitle:headInfo.orderType rightTitleColor:nil];
        
        _viewHeight += 39;
        [self createHeaderRowWithTitle:@"账单金额:" rightTitle:[NSString stringWithFormat:@"¥ %.2f", headInfo.settleAccounts.totalAmount] rightTitleColor:[UIColor redColor]];
        
        _viewHeight += 39;
        [self createHeaderRowWithTitle:@"车牌:" rightTitle:headInfo.vehicleNo rightTitleColor:nil];
        
        _viewHeight += 39;
        [self createHeaderRowWithTitle:@"车型:" rightTitle:headInfo.vehicleBrandModelStr rightTitleColor:nil];
        
        _viewHeight += 39;
        NSString *time = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:headInfo.orderTime formatter:nil];
        [self createHeaderRowWithTitle:@"时间:" rightTitle:time rightTitleColor:nil];
    
        _viewHeight += 39;
        [self createHeaderRowWithTitle:@"联系人:" rightTitle:headInfo.customerName rightTitleColor:nil];
        
        _viewHeight += 39;
        [self createHeaderRowWithTitle:@"联系电话:" rightTitle:headInfo.vehicleMobile rightTitleColor:nil];
       
        _viewHeight += 39;
        
        [self setFrame:CGRectMake(frame.origin.x, frame.origin.y, 300, _viewHeight)];
        self.layer.borderColor = BORDER_COLOR;
        self.layer.borderWidth = 1;
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 8;
}
    ;
    return self;
}


- (TGOrderDetailView *)initViewDetailWithFrame:(CGRect)frame detailInfo:(TGModelOrderDetail *)detailInfo
{
    if (self = [super initWithFrame:frame]) {
        _viewHeight = 0;
        
        UIImageView *imgView = [self createHeaderImage];
        UILabel *leftLbl = [self createLeftTitle:@"消费明细"];
        leftLbl.textAlignment = NSTextAlignmentLeft;
        [imgView addSubview:leftLbl];
        [self addSubview:imgView];
        
        _viewHeight += 39;
        [self createRowViewWithTitle:@"服务项目" detail:@"金额" detailColor:nil];
        
        if ([detailInfo.orderItems__TGModelOrderItem count] > 0) {
            for (int i = 0; i < [detailInfo.orderItems__TGModelOrderItem count]; i++) {
                _viewHeight += 39;
                
                TGModelOrderItem *tmp = [detailInfo.orderItems__TGModelOrderItem objectAtIndex:i];
                
                [self createRowViewWithTitle:tmp.content detail:[NSString stringWithFormat:@"¥ %.2f", tmp.amount] detailColor:[UIColor redColor]];
            }
        }
        _viewHeight += 39;
        
        [self setFrame:CGRectMake(frame.origin.x, frame.origin.y, 300, _viewHeight)];
        self.layer.borderColor = BORDER_COLOR;
        self.layer.borderWidth = 1;
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 8;
    }
    
    return self;
}

- (void)createHeaderRowWithTitle:(NSString *)title rightTitle:(NSString *)rightTitle rightTitleColor:(UIColor *)rightTitleColor
{
    UIView *view = [self createRowView];
    
    UILabel *leftLbl = [self createLeftTitle:title];
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(100, 5, 200, 30)];
    label.text = rightTitle;
    label.backgroundColor = [UIColor clearColor];
    
    if (rightTitleColor) {
        label.textColor = rightTitleColor;
    }

    [view addSubview:label];
    [view addSubview:leftLbl];
    [self addSubview:view];
    
}

- (UILabel *)createLeftTitle:(NSString *)title
{
    UILabel *lbl = [[UILabel alloc] initWithFrame:CGRectMake(10, 5, 80, 30)];
    lbl.text = title;
    lbl.textAlignment = NSTextAlignmentRight;
    lbl.backgroundColor = [UIColor clearColor];
    return lbl;
}

//创建行背景
- (UIView *)createRowView
{
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, _viewHeight, 300, ROWVIE_WHEIGHT)];
//    view.layer.borderWidth = 1;
//    view.layer.borderColor = BORDER_COLOR;
    UIImageView *line = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0 , 300, 1)];
    line.image = [UIImage imageNamed:@"left_line.png"];
    [view addSubview:line];
    return view;
}

//创建消费明细内容
- (void)createRowViewWithTitle:(NSString *)title detail:(NSString *)detail detailColor:(UIColor *)color
{
    UIView *view = [self createRowView];
    
    UILabel *leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 5, 180, 30)];
    leftLabel.text = title;
    leftLabel.backgroundColor = [UIColor clearColor];
    
    UILabel *rightLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 5, 100, 30)];
    rightLabel.backgroundColor = [UIColor clearColor];
    rightLabel.text = detail;
    if (color) {
        rightLabel.textColor = color;
    }
    
    [view addSubview:leftLabel];
    [view addSubview:rightLabel];
    [self addSubview:view];
}

- (UIImageView *)createHeaderImage
{
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, _viewHeight, 300, ROWVIE_WHEIGHT)];
    
    imageView.image = [UIImage imageNamed:@"bg_title.png"];
    
    return imageView;
}

@end
