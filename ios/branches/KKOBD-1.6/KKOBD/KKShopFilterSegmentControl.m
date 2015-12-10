//
//  KKShopFilterSegmentControl.m
//  KKOBD
//
//  Created by zhuyc on 13-8-22.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKShopFilterSegmentControl.h"
static NSString *KKSegmentStrings[] = {@"排序",@"服务",@"地点",@"4S店",nil};

//----------------------KKShopFilterItem-------------------------------------------

@implementation KKShopFilterItem
@synthesize textLabel = _textLabel;

- (id)initWithFrame:(CGRect)frame hideArrowImageView:(BOOL)hidden
{
    self = [super initWithFrame:frame];
    if (self) {
        
        UIImage *image = [UIImage imageNamed:@"icon_shopq_downArrow.png"];
        float reduceWidth = (15 + image.size.width);
        
        if (hidden)
        {
            reduceWidth = 0;
        }
        else
        {
            _arrowImv = [[UIImageView alloc] initWithFrame:CGRectMake(frame.size.width - image.size.width - 10, 0.5*(frame.size.height - image.size.height), image.size.width, image.size.height)];
            _arrowImv.backgroundColor = [UIColor clearColor];
            _arrowImv.image = image;
            [self addSubview:_arrowImv];
            [_arrowImv release];
        }
        
        UILabel *textLb = [[UILabel alloc] initWithFrame:CGRectMake(0, 0.5*(frame.size.height - 15), frame.size.width - reduceWidth, 15)];
        textLb.backgroundColor = [UIColor clearColor];
        textLb.textColor = [UIColor grayColor];
        textLb.font = [UIFont systemFontOfSize:13.f];
        textLb.minimumFontSize = 8;
        textLb.textAlignment = UITextAlignmentCenter;
        self.textLabel = textLb;
        [self addSubview:textLb];
        [textLb release];
        
    }
    return self;
}

- (void)dealloc
{
    _arrowImv = nil;
    self.textLabel = nil;
    
    [super dealloc];
}
@end


//----------------------KKShopFilterSegmentControl-------------------------------------------

@implementation KKShopFilterSegmentControl
@synthesize delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        UIImage *image = [UIImage imageNamed:@"seg_background.png"];
        UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        bgImv.userInteractionEnabled = YES;
        bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        [self addSubview:bgImv];
        [bgImv release];
        
        float width = frame.size.width/4;
        image = [UIImage imageNamed:@"seg_separateLine.png"];
        
        BOOL hidden = NO;
        
        for (int i = 0 ;i < 4 ; i++)
        {
            if (i == 3)
                hidden = YES;
            
            KKShopFilterItem *item = [[KKShopFilterItem alloc] initWithFrame:CGRectMake(i*width, 0, width, frame.size.height) hideArrowImageView:hidden];
            item.itemId = i + 1000;
            item.textLabel.text = KKSegmentStrings[i];
            [item addTarget:self action:@selector(itemClicked:) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:item];
            [item release];
        }
        
        for (int i = 1; i <= 3; i ++) {
            UIImageView *imv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0.5*(frame.size.height - 23), image.size.width, 23)];
            imv.backgroundColor = [UIColor clearColor];
            imv.userInteractionEnabled = YES;
            imv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
            imv.center = CGPointMake(i * width, 0.5*frame.size.height);
            [self addSubview:imv];
            [imv release];
        }
        
    }
    return self;
}

- (void)setShopFilterItemTitle:(NSString *)title WithIndex:(NSInteger)index
{
    for (UIView *subview in self.subviews)
    {
        if ([subview isKindOfClass:[KKShopFilterItem class]])
        {
            KKShopFilterItem *filterItem = (KKShopFilterItem *)subview;
            if (filterItem.itemId == index)
            {
                filterItem.textLabel.text = title;
//                [filterItem.textLabel sizeThatFits:filterItem.textLabel.frame.size];
            }
        }
    }
}

- (void)setShopFilterItemBackGroundImage:(UIImage *)image WithIndex:(NSInteger)index
{
    for (UIView *subview in self.subviews)
    {
        if ([subview isKindOfClass:[KKShopFilterItem class]])
        {
            KKShopFilterItem *filterItem = (KKShopFilterItem *)subview;
            if (filterItem.itemId == index)
                [filterItem setBackgroundImage:image forState:UIControlStateNormal];
        }
    }
}

- (void)itemClicked:(id)sender
{
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(shopFilterSegmentControlClicked:)])
        [self.delegate shopFilterSegmentControlClicked:sender];
    
}

@end
