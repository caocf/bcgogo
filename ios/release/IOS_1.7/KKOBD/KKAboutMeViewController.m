//
//  KKAboutMeViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-9-23.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKAboutMeViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKApplicationDefine.h"
#import "KKAppDelegate.h"
#import "MBProgressHUD.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKUtils.h"

@interface KKAboutMeViewController ()

@end

@implementation KKAboutMeViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self setBachGroundView];
    [self initVariables];
    [self initComponents];
}

#pragma mark -
#pragma mark Custom Methods

- (void) initVariables
{
    
}

- (void) initComponents
{
    [self setNavgationBar];
    
    UIScrollView *scrollview = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 49 - 44 - [self getOrignY])];
    scrollview.backgroundColor = [UIColor clearColor];
    
    
    CGFloat y = 30;
    
    UIImage *image = [UIImage imageNamed:@"icon_aboutus_icon.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), y, image.size.width, image.size.height)];
    iconImv.image = image;
    [scrollview addSubview:iconImv];
    [iconImv release];
    
    y += image.size.height;
    y += 14;
    
    UILabel *nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, y, 320, 15)];
    nameLabel.textAlignment = UITextAlignmentCenter;
    nameLabel.textColor = KKCOLOR_777777;
    nameLabel.backgroundColor = [UIColor clearColor];
    nameLabel.font = [UIFont systemFontOfSize:14];
    nameLabel.text = @"行车一键通";
    [scrollview addSubview:nameLabel];
    [nameLabel release];
    
    y += 15;
    y += 5;
    
    UILabel *versionLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, y, 320, 13)];
    versionLabel.textAlignment = UITextAlignmentCenter;
    versionLabel.textColor = KKCOLOR_9c9c9c;
    versionLabel.font = [UIFont systemFontOfSize:12.f];
    versionLabel.backgroundColor = [UIColor clearColor];
    versionLabel.text = KKAppDelegateSingleton.versionStr;
    [scrollview addSubview:versionLabel];
    [versionLabel release];
    
    y += 13;
    y += 19;
    
    UILabel *desLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, y, 320, 13)];
    desLabel.textAlignment = UITextAlignmentCenter;
    desLabel.textColor = KKCOLOR_9c9c9c;
    desLabel.font = [UIFont systemFontOfSize:12.f];
    desLabel.backgroundColor = [UIColor clearColor];
    desLabel.text = @"监测车况，保障安全，节省费用，行车无忧";
    [scrollview addSubview:desLabel];
    [desLabel release];
    
    y += 13;
    y += 38;
    
    image = [UIImage imageNamed:@"icon_aboutus_container.png"];
    UIImageView *containImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), y, image.size.width, image.size.height - 1)];
    containImv.userInteractionEnabled = YES;
    containImv.image = image;
    
    UIButton *button1 = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, image.size.width, 0.5*image.size.height)];
    [button1 addTarget:self action:@selector(firstButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    button1.backgroundColor = [UIColor clearColor];
    
    UILabel *firstLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(12,14,60,15)];
    firstLabel1.textAlignment = UITextAlignmentLeft;
    firstLabel1.textColor = KKCOLOR_777777;
    firstLabel1.font = [UIFont systemFontOfSize:14];
    firstLabel1.backgroundColor = [UIColor clearColor];
    firstLabel1.text = @"官网 : ";
    [button1 addSubview:firstLabel1];
    [firstLabel1 release];
    
    UILabel *firstLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(55,14,200,15)];
    firstLabel2.textAlignment = UITextAlignmentLeft;
    firstLabel2.textColor = KKCOLOR_2c59b2;
    firstLabel2.font = [UIFont systemFontOfSize:14];
    firstLabel2.backgroundColor = [UIColor clearColor];
    firstLabel2.text = @"www.bcgogo.com";
    [button1 addSubview:firstLabel2];
    [firstLabel2 release];
    
    UIImage *image2 = [UIImage imageNamed:@"icon_aboutus_rightArrow.png"];
    UIImageView *rightArrowImv = [[UIImageView alloc] initWithFrame:CGRectMake(image.size.width - image2.size.width - 12, 0.5*(0.5*image.size.height - image2.size.height), image2.size.width, image2.size.height)];
    rightArrowImv.image = image2;
    [button1 addSubview:rightArrowImv];
    [rightArrowImv release];
    
    [containImv addSubview:button1];
    [button1 release];
    
    image2 = [UIImage imageNamed:@"icon_setting_separateLine@2x.png"];
    UIImageView *lineImv = [[UIImageView alloc] initWithFrame:CGRectMake(12, 0.5*image.size.height - 0.75, image.size.width - 24, 0.75)];
    lineImv.backgroundColor = [UIColor clearColor];
    lineImv.image = [image2 stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    [containImv addSubview:lineImv];
    [lineImv release];
    
    UIButton *button2 = [[UIButton alloc] initWithFrame:CGRectMake(0, 0.5*image.size.height+1, image.size.width, 0.5*image.size.height-1)];
    [button2 addTarget:self action:@selector(secondButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    button2.backgroundColor = [UIColor clearColor];
    
    UILabel *secondLabel = [[UILabel alloc] initWithFrame:CGRectMake(12,14,image.size.width - 24,15)];
    secondLabel.textAlignment = UITextAlignmentLeft;
    secondLabel.font = [UIFont systemFontOfSize:14];
    secondLabel.backgroundColor = [UIColor clearColor];
    secondLabel.textColor = KKCOLOR_777777;
    secondLabel.text = @"客服电话 : 0512-66733331";
    [button2 addSubview:secondLabel];
    [secondLabel release];
    
    [containImv addSubview:button2];
    [button2 release];
    
    [scrollview addSubview:containImv];
    [containImv release];
    
    y += image.size.height;
    y += 60;
    
    UILabel *copyrightLabel1 = [[UILabel alloc] initWithFrame:CGRectMake(0, y, 320, 12)];
    copyrightLabel1.backgroundColor = [UIColor clearColor];
    copyrightLabel1.textAlignment = UITextAlignmentCenter;
    copyrightLabel1.font = [UIFont systemFontOfSize:11.f];
    copyrightLabel1.textColor = KKCOLOR_c4c4c3;
    copyrightLabel1.text = @"版权所有(c)2013-2015 苏州统购信息";
    [scrollview addSubview:copyrightLabel1];
    [copyrightLabel1 release];
    
    y += 12;
    y += 3;
    
    UILabel *copyrightLabel2 = [[UILabel alloc] initWithFrame:CGRectMake(0, y, 320, 12)];
    copyrightLabel2.backgroundColor = [UIColor clearColor];
    copyrightLabel2.textAlignment = UITextAlignmentCenter;
    copyrightLabel2.font = [UIFont systemFontOfSize:11.f];
    copyrightLabel2.textColor = KKCOLOR_c4c4c3;
    copyrightLabel2.text = @"科技有限公司，保留所有权利";
    [scrollview addSubview:copyrightLabel2];
    [copyrightLabel2 release];

    y += 12;
    y += 18;
    
    if (y < currentScreenHeight - 49 - 44 - [self getOrignY])
        y = currentScreenHeight - 49 - 44 - [self getOrignY];
    [scrollview setContentSize:CGSizeMake(320, y)];
    [self.view addSubview:scrollview];
    [scrollview release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"关于我们";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)firstButtonClicked
{
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://www.bcgogo.com"]];
}

- (void)secondButtonClicked
{
    UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:@"拨打客服电话"
                                                       delegate:self
                                              cancelButtonTitle:@"取消"
                                         destructiveButtonTitle:nil
                                              otherButtonTitles:@"0512-66733331", nil];
    [sheet showInView:[UIApplication sharedApplication].keyWindow];
    [sheet release];
    
}

#pragma mark -
#pragma mark UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != [actionSheet cancelButtonIndex])
    {
        NSString *string = [actionSheet  buttonTitleAtIndex:buttonIndex];
        [KKUtils makePhone:string];
    }
}
#pragma mark -
#pragma mark handle memory methods

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
