//
//  KKServiceDetailView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-21.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKServiceDetailView.h"
#import "KKApplicationDefine.h"
#import "KKSmallRatingView.h"
#import "KKUtils.h"

//----------------------------KKRepairDetailView---------------------------
@interface KKRepairDetailView : UIView
{
    UILabel     *_contentLb;
    UILabel     *_repairTypeLb;
    UILabel     *_costLb;
}
- (void)setContent:(id)obj;

@end

@implementation KKRepairDetailView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        _contentLb = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 145, 13)];
        _contentLb.backgroundColor = [UIColor clearColor];
        _contentLb.textColor = [UIColor blackColor];
        _contentLb.textAlignment = UITextAlignmentLeft;
        _contentLb.font = [UIFont systemFontOfSize:13.f];
        [self addSubview:_contentLb];
        [_contentLb release];
        
        _repairTypeLb = [[UILabel alloc] initWithFrame:CGRectMake(160, 0, 40, 13)];
        _repairTypeLb.backgroundColor = [UIColor clearColor];
        _repairTypeLb.textColor = [UIColor blackColor];
        _repairTypeLb.textAlignment = UITextAlignmentCenter;
        _repairTypeLb.font = [UIFont systemFontOfSize:13.f];
        [self addSubview:_repairTypeLb];
        [_repairTypeLb release];
        
        _costLb = [[UILabel alloc] initWithFrame:CGRectMake(206, 0, 60, 13)];
        _costLb.backgroundColor = [UIColor clearColor];
        _costLb.textColor = [UIColor blackColor];
        _costLb.textAlignment = UITextAlignmentRight;
        _costLb.font = [UIFont systemFontOfSize:13.f];
        [self addSubview:_costLb];
        [_costLb release];
    }
    return self;
}

- (void)setContent:(KKModelOrderItem *)obj
{
    _contentLb.text = obj.content;
    _repairTypeLb.text = obj.type;
    _costLb.text = [NSString stringWithFormat:@"%d",obj.amount];
}


- (void)dealloc
{
    _contentLb = nil;
    _repairTypeLb = nil;
    _costLb = nil;
    
    [super dealloc];
}
@end


//----------------------------KKServiceDetailView---------------------------

@implementation KKServiceDetailView

- (id)initWithFrame:(CGRect)frame WithContent:(KKModelserviceDetail *)content
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        _content = [content retain];
        _maxSize = frame.size;
        _height = 8;
        [self initial];
    }
    
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)initial
{
    UIImage *image = [UIImage imageNamed:@"bg_serviceDetail_Body.png"];
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, image.size.width, 300)];
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.userInteractionEnabled = YES;
    
    //----------------head--------------------------------------
    
    image = [UIImage imageNamed:@"bg_serviceDetail_head.png"];
    
    UIImageView *headImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(_maxSize.width - image.size.width), _height, image.size.width, image.size.height)];
    headImv.backgroundColor = [UIColor clearColor];
    headImv.userInteractionEnabled = YES;
    
    float height0 = 23;
    CGSize size = CGSizeZero;
    
    UILabel *label0 = [[UILabel alloc] initWithFrame:CGRectMake(0, height0, 61, 15)];
    label0.backgroundColor = [UIColor clearColor];
    label0.textAlignment = UITextAlignmentRight;
    label0.textColor = [UIColor blackColor];
    label0.font = [UIFont systemFontOfSize:15.f];
    label0.text = @"单据号 :";
    [headImv addSubview:label0];
    [label0 release];
    
    UILabel *label00 = [[UILabel alloc] initWithFrame:CGRectMake(70, height0, 200, 15)];
    label00.backgroundColor = [UIColor clearColor];
    label00.textAlignment = UITextAlignmentLeft;
    label00.textColor = [UIColor blackColor];
    label00.font = [UIFont systemFontOfSize:15.f];
    label00.numberOfLines = 0;
    label00.text = _content.receiptNo;
    size = [label00.text sizeWithFont:[UIFont systemFontOfSize:15.f] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    [label00 setFrame:CGRectMake(70, height0, 200, size.height)];
    [headImv addSubview:label00];
    [label00 release];
    
    height0 += size.height;
    height0 += 10;
    
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, height0, 61, 15)];
    label1.backgroundColor = [UIColor clearColor];
    label1.textAlignment = UITextAlignmentRight;
    label1.textColor = [UIColor blackColor];
    label1.font = [UIFont systemFontOfSize:15.f];
    label1.text = @"类型 :";
    [headImv addSubview:label1];
    [label1 release];
    
    UILabel *label11 = [[UILabel alloc] initWithFrame:CGRectMake(70, height0, 200, 15)];
    label11.backgroundColor = [UIColor clearColor];
    label11.textAlignment = UITextAlignmentLeft;
    label11.textColor = [UIColor blackColor];
    label11.font = [UIFont systemFontOfSize:15.f];
    label11.numberOfLines = 0;
    label11.text = [_content.serviceType length] > 0 ? _content.serviceType : _content.orderType;
    size = [label11.text sizeWithFont:[UIFont systemFontOfSize:15.f] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    [label11 setFrame:CGRectMake(70, height0, 200, MAX(size.height, 15))];
    [headImv addSubview:label11];
    [label11 release];
    
    height0 += size.height;
    height0 += 10;
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, height0, 61, 15)];
    label2.backgroundColor = [UIColor clearColor];
    label2.textAlignment = UITextAlignmentRight;
    label2.textColor = [UIColor blackColor];
    label2.font = [UIFont systemFontOfSize:15.f];
    label2.text = @"时间 :";
    [headImv addSubview:label2];
    [label2 release];
    
    UILabel *label22 = [[UILabel alloc] initWithFrame:CGRectMake(70, height0, 200, 15)];
    label22.backgroundColor = [UIColor clearColor];
    label22.textAlignment = UITextAlignmentLeft;
    label22.textColor = [UIColor blackColor];
    label22.font = [UIFont systemFontOfSize:15.f];
    label22.numberOfLines = 0;
    label22.text = [KKUtils convertDateTOString2:[NSDate dateWithTimeIntervalSince1970:_content.orderTime/1000]];
    size = [label22.text sizeWithFont:[UIFont systemFontOfSize:15.f] constrainedToSize:CGSizeMake(200, MAXFLOAT)];
    [label22 setFrame:CGRectMake(70, height0, 200, size.height)];
    [headImv addSubview:label22];
    [label22 release];
    
    height0 += size.height;
    height0 += 10;
    
    [headImv setFrame:CGRectMake(0.5*(_maxSize.width - image.size.width), _height, image.size.width, height0)];
    headImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [bgImv addSubview:headImv];
    [headImv release];
    
    _height += height0;
    
    //----------------shop--------------------------------------
    _height += 10;
    
    UILabel *shopNameLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height , 140, 15)];
    shopNameLb.backgroundColor = [UIColor clearColor];
    shopNameLb.textColor = KKCOLOR_3359ac;
    shopNameLb.textAlignment = UITextAlignmentLeft;
    shopNameLb.font = [UIFont boldSystemFontOfSize:15.f];
    shopNameLb.numberOfLines = 0;
    shopNameLb.text = _content.shopName;
    size = [shopNameLb.text sizeWithFont:[UIFont boldSystemFontOfSize:15.f] constrainedToSize:CGSizeMake(140, MAXFLOAT)];
    [shopNameLb setFrame:CGRectMake(20, _height, 140, size.height)];
    [bgImv addSubview:shopNameLb];
    [shopNameLb release];
    
    UIButton *shopNameButton = [[UIButton alloc] initWithFrame:shopNameLb.frame];
    shopNameLb.backgroundColor = [UIColor clearColor];
    [shopNameButton addTarget:self action:@selector(shopNameButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [bgImv addSubview:shopNameButton];
    [shopNameButton release];
    
    UILabel *statusLb = [[UILabel alloc] initWithFrame:CGRectMake(185, _height + 5 , 50, 10)];
    statusLb.backgroundColor = [UIColor clearColor];
    statusLb.textColor = [UIColor blackColor];
    statusLb.textAlignment = UITextAlignmentLeft;
    statusLb.font = [UIFont systemFontOfSize:10.f];
    statusLb.numberOfLines = 0;
    statusLb.text = _content.status;
    [bgImv addSubview:statusLb];
    [statusLb release];

    if ([_content.status isEqualToString:@"已接受"] || [_content.status isEqualToString:@"待确认"])
    {
        image = [UIImage imageNamed:@"btn_serviceDetail_cancel.png"];
        UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(240, _height, image.size.width, image.size.height)];
        [cancelBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:10.f]];
        [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
        [cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [cancelBtn setBackgroundImage:image forState:UIControlStateNormal];
        [cancelBtn addTarget:self action:@selector(cancelButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        [bgImv addSubview:cancelBtn];
        [cancelBtn release];
    }
    else if ([_content.status isEqualToString:@"已结算"])
    {
        image = [UIImage imageNamed:@"btn_serviceDetail_cancel.png"];
        UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(240, _height, image.size.width, image.size.height)];
        [cancelBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:10.f]];
        [cancelBtn setTitle:@"评价" forState:UIControlStateNormal];
        [cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [cancelBtn setBackgroundImage:image forState:UIControlStateNormal];
        [cancelBtn addTarget:self action:@selector(evaluatButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        [bgImv addSubview:cancelBtn];
        [cancelBtn release];
    }
    
    _height += size.height;
    _height += 7;
    
    image = [UIImage imageNamed:@"icon_serviceDetail_line.png"];
    UIImageView *lineImv1 = [[UIImageView alloc] initWithFrame:CGRectMake(20, _height, image.size.width, image.size.height)];
    lineImv1.image = image;
    [bgImv addSubview:lineImv1];
    [lineImv1 release];
    
    _height += image.size.height;
    _height += 7;
    
    if (_content.settleAccounts != nil)
        self.isFinished = YES;
    
    float orignX = 46;
    
    if (self.isFinished)
        orignX = 20;
    UILabel *customerNameLb = [[UILabel alloc] initWithFrame:CGRectMake(orignX, _height, 268, 13)];
    customerNameLb.backgroundColor = [UIColor clearColor];
    customerNameLb.textColor = [UIColor blackColor];
    customerNameLb.textAlignment = UITextAlignmentLeft;
    customerNameLb.font = [UIFont systemFontOfSize:13.f];
    customerNameLb.text = [_content.customerName length] > 0 ? [NSString stringWithFormat:@"客户 : %@",_content.customerName] : @"客户 : ";
    [bgImv addSubview:customerNameLb];
    [customerNameLb release];
    
    _height += 13;
    _height += 10;
    
    UILabel *carNumLb = [[UILabel alloc] initWithFrame:CGRectMake(orignX, _height, 268, 13)];
    carNumLb.backgroundColor = [UIColor clearColor];
    carNumLb.textColor = [UIColor blackColor];
    carNumLb.textAlignment = UITextAlignmentLeft;
    carNumLb.font = [UIFont systemFontOfSize:13.f];
    carNumLb.text = [_content.vehicleNo length] > 0 ? [NSString stringWithFormat:@"车牌 : %@",_content.vehicleNo] : @"车牌 : ";
    [bgImv addSubview:carNumLb];
    [carNumLb release];
    
    _height += 13;
    _height += 10;
    
    UILabel *carModelLb = [[UILabel alloc] initWithFrame:CGRectMake(orignX, _height, 268, 13)];
    carModelLb.backgroundColor = [UIColor clearColor];
    carModelLb.textColor = [UIColor blackColor];
    carModelLb.textAlignment = UITextAlignmentLeft;
    carModelLb.font = [UIFont systemFontOfSize:13.f];
    carModelLb.text = [_content.vehicleBrandModelStr length] > 0 ? [NSString stringWithFormat:@"车型 : %@",_content.vehicleBrandModelStr] : @"车型 : ";
    [bgImv addSubview:carModelLb];
    [carModelLb release];
    
    _height += 13;
    _height += 10;
    
    if (!self.isFinished)
    {
        UILabel *appointTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 268, 13)];
        appointTimeLabel.backgroundColor = [UIColor clearColor];
        appointTimeLabel.textColor = [UIColor blackColor];
        appointTimeLabel.textAlignment = UITextAlignmentLeft;
        appointTimeLabel.font = [UIFont systemFontOfSize:13.f];
        appointTimeLabel.text = [NSString stringWithFormat:@"预约时间 : %@",[KKUtils convertDateTOString2:[NSDate dateWithTimeIntervalSince1970:_content.orderTime/1000]]];
        [bgImv addSubview:appointTimeLabel];
        [appointTimeLabel release];
        
        _height += 13;
        _height += 10;
        
        UILabel *connectLabel = [[UILabel alloc] initWithFrame:CGRectMake(33, _height, 268, 13)];
        connectLabel.backgroundColor = [UIColor clearColor];
        connectLabel.textColor = [UIColor blackColor];
        connectLabel.textAlignment = UITextAlignmentLeft;
        connectLabel.font = [UIFont systemFontOfSize:13.f];
        connectLabel.text = [_content.vehicleContact length] > 0 ?[NSString stringWithFormat:@"联系人 : %@",_content.vehicleContact]: @"联系人 : ";
        [bgImv addSubview:connectLabel] ;
        [connectLabel release];
        
        _height += 13;
        _height += 10;
        
        UILabel *connectPhone = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 268, 13)];
        connectPhone.backgroundColor = [UIColor clearColor];
        connectPhone.textColor = [UIColor blackColor];
        connectPhone.textAlignment = UITextAlignmentLeft;
        connectPhone.font = [UIFont systemFontOfSize:13.f];
        connectPhone.text = [_content.vehicleMobile length] > 0 ? [NSString stringWithFormat:@"联系电话 : %@",_content.vehicleMobile] : @"联系电话 :";
        [bgImv addSubview:connectPhone];
        [connectPhone release];
        
        _height += 13;
        _height += 10;
        
        UILabel *remarkLabel = [[UILabel alloc] initWithFrame:CGRectMake(46, _height, 268, 13)];
        remarkLabel.backgroundColor = [UIColor clearColor];
        remarkLabel.textColor = [UIColor blackColor];
        remarkLabel.textAlignment = UITextAlignmentLeft;
        remarkLabel.font = [UIFont systemFontOfSize:13.f];
        remarkLabel.text = [NSString stringWithFormat:@"备注 : "];
        [bgImv addSubview:remarkLabel];
        [remarkLabel release];
                
        if ([_content.remark length] > 0)
        {
            
            NSString *remarkString = _content.remark;
            
            CGSize size = [remarkString sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(210, MAXFLOAT)];
            
            UILabel *remarkLabel = [[UILabel alloc] initWithFrame:CGRectMake(83, _height, 210, size.height)];
            remarkLabel.backgroundColor = [UIColor clearColor];
            remarkLabel.textColor = [UIColor blackColor];
            remarkLabel.textAlignment = UITextAlignmentLeft;
            remarkLabel.font = [UIFont systemFontOfSize:13.f];
            remarkLabel.numberOfLines = 0;
            remarkLabel.text = remarkString;
            [bgImv addSubview:remarkLabel];
            [remarkLabel release];
            
            _height += size.height;
        }
        else
            _height += 13;
        
        _height += 10;
    }

    if ([_content.orderItems__KKModelOrderItem count] > 0)
    {
        for (int i = 0; i < [_content.orderItems__KKModelOrderItem count]; i++) {
            KKRepairDetailView *detailView = [[KKRepairDetailView alloc] initWithFrame:CGRectMake(20, _height, _maxSize.width - 35, 13)];
            [detailView setContent:_content.orderItems__KKModelOrderItem[i]];
            [bgImv addSubview:detailView];
            [detailView release];
            
            _height += (13 + 3);
        }
        
        _height += 15;
    }

    if (_content.settleAccounts != nil)
    {
        UILabel *priceLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 100, 15)];
        priceLb.backgroundColor = [UIColor clearColor];
        priceLb.textColor = [UIColor blackColor];
        priceLb.font = [UIFont systemFontOfSize:15.f];
        priceLb.textAlignment = UITextAlignmentLeft;
        priceLb.text =@"价格";
        [bgImv addSubview:priceLb];
        [priceLb release];
        
        _height += 15;
        _height += 7;
        
        image = [UIImage imageNamed:@"icon_serviceDetail_line.png"];
        UIImageView *lineImv2 = [[UIImageView alloc] initWithFrame:CGRectMake(20, _height, image.size.width, image.size.height)];
        lineImv2.image = image;
        [bgImv addSubview:lineImv2];
        [lineImv2 release];
        
        _height += image.size.height;
        _height += 10;
        
        UILabel *receivableLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 200, 13)];
        receivableLb.backgroundColor = [UIColor clearColor];
        receivableLb.textColor = [UIColor blackColor];
        receivableLb.textAlignment = UITextAlignmentLeft;
        receivableLb.font = [UIFont systemFontOfSize:13.f];
        receivableLb.text = [NSString stringWithFormat:@"应收 : %.2f",_content.settleAccounts.totalAmount];
        [bgImv addSubview:receivableLb];
        [receivableLb release];
        
        _height += 13;
        _height += 10;
        
        UILabel *PaidLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 200, 13)];
        PaidLb.backgroundColor = [UIColor clearColor];
        PaidLb.textColor = [UIColor blackColor];
        PaidLb.textAlignment = UITextAlignmentLeft;
        PaidLb.font = [UIFont systemFontOfSize:13.f];
        PaidLb.text = [NSString stringWithFormat:@"实收 : %.2f￥",_content.settleAccounts.settledAmount];
        [bgImv addSubview:PaidLb];
        [PaidLb release];
        
        _height += 13;
        _height += 10;
        
        UILabel *PreferentialLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 200, 13)];
        PreferentialLb.backgroundColor = [UIColor clearColor];
        PreferentialLb.textColor = [UIColor blackColor];
        PreferentialLb.textAlignment = UITextAlignmentLeft;
        PreferentialLb.font = [UIFont systemFontOfSize:13.f];
        PreferentialLb.text = [NSString stringWithFormat:@"优惠 : %.2f￥",_content.settleAccounts.discount];
        [bgImv addSubview:PreferentialLb];
        [PreferentialLb release];
        
        _height += 13;
        _height += 10;
        
        UILabel *OnLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 200, 13)];
        OnLb.backgroundColor = [UIColor clearColor];
        OnLb.textColor = [UIColor blackColor];
        OnLb.textAlignment = UITextAlignmentLeft;
        OnLb.font = [UIFont systemFontOfSize:13.f];
        OnLb.text = [NSString stringWithFormat:@"挂账 : %.2f￥",_content.settleAccounts.debt];
        [bgImv addSubview:OnLb];
        [OnLb release];
        
        _height += 13;
        _height += 20;

    }
    
    
    if (_content.comment != nil)
    {
        UILabel *serviceRateLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 100, 15)];
        serviceRateLb.backgroundColor = [UIColor clearColor];
        serviceRateLb.textColor = [UIColor blackColor];
        serviceRateLb.font = [UIFont systemFontOfSize:15.f];
        serviceRateLb.textAlignment = UITextAlignmentLeft;
        serviceRateLb.text =@"服务评价";
        [bgImv addSubview:serviceRateLb];
        [serviceRateLb release];
        
        _height += 15;
        _height += 7;
        
        
        image = [UIImage imageNamed:@"icon_serviceDetail_line.png"];
        UIImageView *lineImv3 = [[UIImageView alloc] initWithFrame:CGRectMake(20, _height, image.size.width, image.size.height)];
        lineImv3.image = image;
        [bgImv addSubview:lineImv3];
        [lineImv3 release];
        
        _height += image.size.height;
        _height += 7;
        
        KKSmallRatingView *rateView = [[KKSmallRatingView alloc] initWithRank:_content.comment.commentScore];
        [rateView setFrame:CGRectMake(20, _height, 90, 15)];
        [bgImv addSubview:rateView];
        [rateView release];
        
        _height += 15;
        _height += 7;
        
        UILabel *rateDetailLb = [[UILabel alloc] initWithFrame:CGRectMake(20, _height, 265, 13)];
        rateDetailLb.backgroundColor = [UIColor clearColor];
        rateDetailLb.textColor = [UIColor blackColor];
        rateDetailLb.textAlignment = UITextAlignmentLeft;
        rateDetailLb.font = [UIFont systemFontOfSize:13.f];
        rateDetailLb.numberOfLines = 0;
        rateDetailLb.text = _content.comment.commentContent;
        size = [rateDetailLb.text sizeWithFont:[UIFont systemFontOfSize:13.f] constrainedToSize:CGSizeMake(265, MAXFLOAT)];
        [rateDetailLb setFrame:CGRectMake(20, _height, 265, size.height)];
        [bgImv addSubview:rateDetailLb];
        [rateDetailLb release];
        
        _height += size.height;
        _height += 5;

    }
    
    [bgImv setFrame:CGRectMake(0, 0, 300, _height)];
    bgImv.image = [[UIImage imageNamed:@"bg_serviceDetail_Body.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [self addSubview:bgImv];
    [bgImv release];
    
    image = [UIImage imageNamed:@"bg_serviceDetail_foot@2x"];
    UIImageView *footImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, _height, 300, image.size.height)];
    footImv.image = image;
    [self addSubview:footImv];
    [footImv release];
    
    _height += image.size.height;
    _height += 10;
    
    [self setFrame:CGRectMake(self.frame.origin.x, 0, _maxSize.width, _maxSize.height)];
    [self setContentSize:CGSizeMake(_maxSize.width, _height)];
    
}


- (void)cancelButtonClicked
{
    if (self.actionDelegate && [(NSObject *)self.actionDelegate respondsToSelector:@selector(KKServiceDetailViewCancelButtonClicked)])
    {
        [self.actionDelegate KKServiceDetailViewCancelButtonClicked];
    }
}

- (void)evaluatButtonClicked
{
    if (self.actionDelegate && [(NSObject *)self.actionDelegate  respondsToSelector:@selector(KKServiceDetailViewEvaluatButtonClicked)])
    {
        [self.actionDelegate KKServiceDetailViewEvaluatButtonClicked];
    }
}

- (void)shopNameButtonClicked
{
    if (self.actionDelegate && [(NSObject *)self.actionDelegate respondsToSelector:@selector(KKServiceDetailViewShopNameButtonClicked)])
    {
        [self.actionDelegate KKServiceDetailViewShopNameButtonClicked];
    }
}

- (void)dealloc
{
    if (_content)
        [_content release];
    _content = nil;
    
    [super dealloc];
}
@end
