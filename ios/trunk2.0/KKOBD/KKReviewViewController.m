//
//  KKReviewViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKReviewViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "MBProgressHUD.h"
#import "KKTBMessage.h"
#import "KKDB.h"

@interface KKReviewViewController ()

@end

@implementation KKReviewViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    
}

- (void) initComponents
{
    [self setBachGroundView];
    [self setNavgationBar];
    
    float orignY = 22;
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, orignY, 320, 15)];
    label.backgroundColor = [UIColor clearColor];
    label.text = @"请打分";
    label.textAlignment = UITextAlignmentCenter;
    label.font = [UIFont systemFontOfSize:15.0f];
    label.textColor = KKCOLOR_A7a6a6;
    [self.view addSubview:label];
    [label release];
    
    orignY += 33;
    
    if (_rankView == nil)
    {
        _rankView = [[KKRatingView alloc] initWithRank:2];
        [_rankView setFrame:CGRectMake(0, orignY, 320, 40)];
        [self.view addSubview:_rankView];
        [_rankView release];
    }
    
    orignY += 60;
    
    UIImage *image = [UIImage imageNamed:@"bg_rank_textView.png"];
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    bgImv.userInteractionEnabled = YES;
    bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    _textView = [[GCPlaceholderTextView alloc] initWithFrame:CGRectMake(10, 10, image.size.width - 20, image.size.height - 20)];
    _textView.delegate = self;
    _textView.returnKeyType = UIReturnKeyDone;
    _textView.font = [UIFont systemFontOfSize:15.0f];
    _textView.placeholder = @"输入评论";
    _textView.textColor = KKCOLOR_A7a6a6;
    _textView.backgroundColor = [UIColor clearColor];
    [bgImv addSubview:_textView];
    [_textView release];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    orignY += image.size.height;
    orignY += 15;
    
    image = [UIImage imageNamed:@"bg_setting_uf_send.png"];
    
    UIButton *sendBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), orignY, image.size.width, image.size.height)];
    [sendBtn setBackgroundImage:image forState:UIControlStateNormal];
    [sendBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:17.0f]];
    [sendBtn setTitle:@"发送" forState:UIControlStateNormal];
    [sendBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [sendBtn addTarget:self action:@selector(sendButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:sendBtn];
    [sendBtn release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"评论";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)sendButtonClicked
{
    [self resignVcFirstResponder];
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    [[KKProtocolEngine sharedPtlEngine] shopScore:self.orderId
                                     commentScore:_rankView.rank
                                   commentContent:_textView.text  delegate:self];
}

#pragma mark -
#pragma mark UITextViewDelegate
-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if ([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)shopScoreResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    if ([self.messageId length] > 0)
    {
        KKTBMessage *tbMessage = [[KKTBMessage alloc] initWithDB:[KKDB sharedDB]];
        [tbMessage setMessageActionTypeToNull:self.messageId];
        [tbMessage release];
    }
    else if ([self.orderId length] > 0)
    {
        KKTBMessage *tbMessage = [[KKTBMessage alloc] initWithDB:[KKDB sharedDB]];
        NSMutableArray *arr = [tbMessage getPollMessagesWithUserNo:[KKProtocolEngine sharedPtlEngine].userName];
        BOOL have = NO;
        KKPollMessage *temMessage = nil;
        for (KKPollMessage *message in arr)
        {
            NSRange range = [message.params rangeOfString:self.orderId];
            if (range.length > 0)
            {
                have = YES;
                temMessage = message;
                break;
            }
        }
        if (have)
        {
            KKTBMessage *tbMessage = [[KKTBMessage alloc] initWithDB:[KKDB sharedDB]];
            [tbMessage setMessageActionTypeToNull:temMessage.id];
            [tbMessage release];
        }
    }
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc block:^{
        [self backButtonClicked];
    }];
    return KKNumberResultEnd;
}


#pragma mark -
#pragma mark Handle memory

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}
- (void)dealloc
{
    
    [super dealloc];
}
@end
