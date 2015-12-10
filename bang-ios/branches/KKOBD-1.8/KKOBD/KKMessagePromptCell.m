//
//  KKMessagePromptCell.m
//  KKOBD
//
//  Created by zhuyc on 13-8-15.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKMessagePromptCell.h"
#import "KKApplicationDefine.h"
#import "KKTBMessage.h"

@implementation KKMessagePromptCell
@synthesize delegate;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _titlelabel = [[UILabel alloc] initWithFrame:CGRectMake(18.5, 16, 100, 15)];
        _titlelabel.textColor = KKCOLOR_409beb;
        _titlelabel.textAlignment = UITextAlignmentLeft;
        _titlelabel.font = [UIFont systemFontOfSize:15.0];
        [self addSubview:_titlelabel];
        [_titlelabel release];
        
        _timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(153, 20, 100, 10)];
        _timeLabel.textColor = KKCOLOR_999999;
        _timeLabel.textAlignment = UITextAlignmentLeft;
        _titlelabel.font = [UIFont systemFontOfSize:15.0];
        [self addSubview:_timeLabel];
        [_timeLabel release];
        
        _messageLabel = [[UILabel alloc] initWithFrame:CGRectMake(18.5, 42, 208, 10)];
        _messageLabel.textColor = KKCOLOR_333333;
        _messageLabel.numberOfLines = 0;
        _messageLabel.textAlignment = UITextAlignmentLeft;
        _messageLabel.font = [UIFont systemFontOfSize:15.0];
        [self addSubview:_messageLabel];
        [_messageLabel release];
        
        UIImage *image = [UIImage imageNamed:@"bg_msg_service.png"];
        _actionButton = [[UIButton alloc] initWithFrame:CGRectMake(240, 42, image.size.width, image.size.height)];
        [_actionButton setBackgroundImage:image forState:UIControlStateNormal];
        [_actionButton.titleLabel setFont:[UIFont boldSystemFontOfSize:11.0f]];
        [_actionButton addTarget:self action:@selector(buttonClicked) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_actionButton];
        [_actionButton release];
        
        _lineView = [[UIView alloc] initWithFrame:CGRectMake(0, 40, 320, 1)];
        _lineView.backgroundColor = KKCOLOR_d4d4d4;
        [self addSubview:_lineView];
        [_lineView release];

    }
    return self;
}

- (void)setContent:(KKPollMessage *)sender
{
    self.message = sender;
    
    CGPoint startPoint = CGPointMake(18.5, 16);
    CGFloat height = startPoint.y;
    
    _titlelabel.text = [self.message.title length] > 0 ? self.message.title :@"title";
    
    CGSize size = [self.message.type sizeWithFont:[UIFont systemFontOfSize:15.0] constrainedToSize:CGSizeMake(320 - 2*18.5, 15)];
    [_titlelabel setFrame:CGRectMake(startPoint.x,startPoint.y,size.width, size.height)];
    
    height += size.height;
    height += 10;
    
    BOOL hideActionButton = YES;
    if ([self.message.actionType isEqualToString:@"SEARCH_SHOP"] || [self.message.actionType isEqualToString:@"SERVICE_DETAIL"] || [self.message.actionType isEqualToString:@"CANCEL_ORDER"] || [self.message.actionType isEqualToString:@"ORDER_DETAIL"] || [self.message.actionType isEqualToString:@"COMMENT_SHOP"])
        hideActionButton = NO;
    
    NSString *title = nil;
    if ([self.message.actionType isEqualToString:@"SEARCH_SHOP"])
        title = @"预约服务";
    else if ([self.message.actionType isEqualToString:@"SERVICE_DETAIL"])
        title = @"查看单据";
    else if ([self.message.actionType isEqualToString:@"CANCEL_ORDER"])
        title = @"取消服务";
    else if ([self.message.actionType isEqualToString:@"COMMENT_SHOP"])
        title = @"评价";
    else if ([self.message.actionType isEqualToString:@"ORDER_DETAIL"])
        title = @"单据详情";
    
    float width = 215;
    _actionButton.hidden = NO;
    [_actionButton setTitle:title forState:UIControlStateNormal];
    
    if (hideActionButton)
    {
        _actionButton.hidden = YES;
        width = 320 - 2*18.5;
    }
    
    _messageLabel.text = self.message.content;
    size = [self.message.content sizeWithFont:[UIFont systemFontOfSize:15.0f] constrainedToSize:CGSizeMake(width, MAXFLOAT)];
    [_messageLabel setFrame:CGRectMake(startPoint.x, height, width, size.height)];
    
    height += size.height;
    height += 22;

    if (height < 80)
        height = 80;
    
    [_lineView setFrame:CGRectMake(0, height - 1, 320, 1)];
}

- (void)buttonClicked
{
    if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKMessagePromptCellButtonClicked:)])
        [self.delegate KKMessagePromptCellButtonClicked:self.message];
}

+ (float)calculateCellHeightWith:(KKPollMessage *)sender
{
    CGPoint startPoint = CGPointMake(18.5, 16);
    CGFloat height = startPoint.y;
    
    CGSize size = [sender.type sizeWithFont:[UIFont systemFontOfSize:15.0] constrainedToSize:CGSizeMake(320 - 2*18.5, 15)];
    
    height += size.height;
    height += 10;
    
    BOOL hideActionButton = YES;
    if ([sender.actionType isEqualToString:@"SEARCH_SHOP"] || [sender.actionType isEqualToString:@"SERVICE_DETAIL"] || [sender.actionType isEqualToString:@"CANCEL_ORDER"] || [sender.actionType isEqualToString:@"ORDER_DETAIL"] || [sender.actionType isEqualToString:@"COMMENT_SHOP"])
        hideActionButton = NO;
    
    float width = 215;
    if (hideActionButton)
    {
        width = 320 - 2*18.5;
    }
    size = [sender.content sizeWithFont:[UIFont systemFontOfSize:15.0f] constrainedToSize:CGSizeMake(width, MAXFLOAT)];
    
    height += size.height;
    height += 22;
    
    if (height < 80)
        height = 80;

    return height;
}

- (void)dealloc
{
    self.message = nil;
    _titlelabel = nil;
    _timeLabel = nil;
    _messageLabel = nil;
    _actionButton = nil;
    [super dealloc];
}
@end
