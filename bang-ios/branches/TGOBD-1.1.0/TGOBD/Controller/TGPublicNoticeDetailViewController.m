//
//  TGPublicNoticeDetailViewController.m
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGPublicNoticeDetailViewController.h"
#import "NSDate+millisecond.h"
#import "TGScanMaskView.h"

@interface TGPublicNoticeDetailViewController ()

@end

@implementation TGPublicNoticeDetailViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self initCompoents];
    
    [self setNavigationTitle:@"公告详情"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Method

- (void)initCompoents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    _webView = [[UIWebView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, [self getViewHeightWithNavigationBar])];
    _webView.scalesPageToFit = NO;
    _webView.delegate = self;
    _webView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:_webView];
    
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"shop_notice" ofType:@"html"];
    NSURL *url = [NSURL fileURLWithPath:filePath];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    [_webView loadRequest:request];
    
}

- (void)httpHandler
{
    [TGProgressHUD show];
    [[TGHTTPRequestEngine sharedInstance] getAdvertDetail:_advertId viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            TGModelPUBlicNoticeDetailRsp *rsp = (TGModelPUBlicNoticeDetailRsp *)responseObject;
            if ([rsp.advertDTOList__TGModelPublicNoticeInfo count] > 0) {
                NSString *title = [[rsp.advertDTOList__TGModelPublicNoticeInfo objectAtIndex:0] title];
                NSString *content = [[rsp.advertDTOList__TGModelPublicNoticeInfo objectAtIndex:0] description];
                NSString *time = [NSDate dateStringWithTimeIntervalSince1970WithMillisecond:[[rsp.advertDTOList__TGModelPublicNoticeInfo objectAtIndex:0] editDate] formatter:nil];
                [self setWebViewContent:title time:time content:content];
            }
        }
    } failure:self.faultBlock];
}

- (void)setWebViewContent:(NSString *)title time:(NSString *)time content:(NSString *)content
{
    NSString *tmp = [NSString stringWithFormat:@"setContent(\'%@\',\'%@\',\'%@\');", title, time, content];
    [_webView stringByEvaluatingJavaScriptFromString:tmp];
}

#pragma mark - UIWebViewDelegate

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    [self httpHandler];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    if (error) {
        [TGAlertView showAlertViewWithTitle:nil message:[error localizedDescription]];
    }
}
@end
