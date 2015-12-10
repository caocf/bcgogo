//
//  KKThirdViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-5.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKThirdViewController.h"
#import "UIViewController+extend.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKViewUtils.h"
#import "KKPersonalInfoViewController.h"
#import "KKUserFeedbackViewController.h"
#import "KKAboutMeViewController.h"
#import "KKBindCarViewController.h"
#import "KKPreference.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKMessagePollingManager.h"
#import "KKShowOrAddNewBindCarViewController.h"

@interface KKThirdViewController ()

@end

@implementation KKThirdViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
}

#pragma mark -
#pragma mark Custom Meyhods

- (void) initVariables
{
    _icons = [[NSMutableArray alloc] initWithObjects:[UIImage imageNamed:@"icon_setting_1.png"],
                                                      [UIImage imageNamed:@"icon_setting_2.png"],
                                                      [UIImage imageNamed:@"icon_setting_3.png"],
                                                      [UIImage imageNamed:@"icon_setting_4.png"],
                                                      [UIImage imageNamed:@"icon_setting_5.png"],
                                                     [UIImage imageNamed:@"icon_setting_6.png"],
                                                     [UIImage imageNamed:@"icon_setting_7.png"],
                                                     [UIImage imageNamed:@"icon_setting_8.png"],nil];
    NSString *str = @"个人资料";
    if(![KKAuthorization sharedInstance].accessAuthorization.personalInfo)
        str = @"用户注册";
    _names = [[NSMutableArray alloc] initWithObjects:str,@"消息提示音",@"车辆管理",@"绑定OBD",@"绑定店铺",@"用户反馈",@"版本检测",@"关于我们",nil];
    
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBachGroundView];
    UIImage *image = [UIImage imageNamed:@"icon_setting_logOff.png"];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_setting.png"]];
    bgImv.frame = CGRectMake(0, 6, 320, currentScreenHeight - 42 - image.size.height - [self getOrignY] - 44 - 49);
    bgImv.userInteractionEnabled = YES;
    
    //_mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,0, image.size.width,image.size.height) style:UITableViewStylePlain];
    _mainTableView = [[UITableView alloc] initWithFrame:bgImv.bounds style:UITableViewStylePlain];
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.backgroundView = nil;
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _mainTableView.scrollEnabled = YES;
    [bgImv addSubview:_mainTableView];
    [_mainTableView release];
    
    [self.view addSubview:bgImv];
    [bgImv release];
    
    if([KKAuthorization sharedInstance].accessAuthorization.personalInfo)
    {
        //UIButton *logoffBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 10 +bgImv.frame.origin.y + bgImv.frame.size.height, image.size.width, image.size.height)];
        UIButton *logoffBtn = [[UIButton alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 16 +_mainTableView.frame.origin.y + _mainTableView.frame.size.height, image.size.width, image.size.height)];
        [logoffBtn setBackgroundImage:image forState:UIControlStateNormal];
        [logoffBtn.titleLabel setFont:[UIFont boldSystemFontOfSize:17.0f]];
        [logoffBtn setTitle:@"退出登录" forState:UIControlStateNormal];
        [logoffBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [logoffBtn addTarget:self action:@selector(logOffButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:logoffBtn];
        [logoffBtn release];
    }
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"设置";
}

- (void)setBachGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];
}

#pragma mark -
#pragma mark Events

- (void)switchButtonClicked:(id)sender
{
    UISwitch *switchBtn = (UISwitch *)sender;
    
    KKModelPreferencePromptVoiceSwitch *voice = [[KKModelPreferencePromptVoiceSwitch alloc] init];
    voice.isOn = switchBtn.isOn;
    [KKPreference sharedPreference].voiceSwitch = voice;
    [voice release];
    
    [_mainTableView reloadData];
}

- (void)logOffButtonClicked
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] userLogout:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
}

- (void)updateAppVersion:(NSString *)url
{
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_icons count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_settingIdentifier";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        
        UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        iconImv.userInteractionEnabled = YES;
        iconImv.contentMode = UIViewContentModeCenter;
        iconImv.tag = 100;
        [cell.contentView addSubview:iconImv];
        [iconImv release];
        
        UILabel *textLb = [[UILabel alloc] initWithFrame:CGRectMake(60, 15, 100, 15)];
        textLb.tag = 101;
        textLb.textColor = [UIColor blackColor];
        textLb.font = [UIFont systemFontOfSize:15.0f];
        textLb.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:textLb];
        [textLb release];
        
        UIImageView *arrImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        arrImv.backgroundColor = [UIColor clearColor];
        arrImv.tag = 102;
        [cell.contentView addSubview:arrImv];
        [arrImv release];
        
        UIImageView *separateLine = [[UIImageView alloc] initWithFrame:CGRectZero];
        separateLine.backgroundColor = [UIColor clearColor];
        separateLine.tag = 103;
        [cell.contentView addSubview:separateLine];
        [separateLine release];
        
        UISwitch *switchBtn = [[UISwitch alloc] initWithFrame:CGRectZero];
        [switchBtn addTarget:self action:@selector(switchButtonClicked:) forControlEvents:UIControlEventValueChanged];
        switchBtn.tag = 104;
        [cell.contentView addSubview:switchBtn];
        [switchBtn release];
        
    }
    UIImageView *icon = (UIImageView *)[cell.contentView viewWithTag:100];
    UILabel *megLb = (UILabel *)[cell.contentView viewWithTag:101];
    UIImageView *arrow = (UIImageView *)[cell.contentView viewWithTag:102];
    UIImageView *line = (UIImageView *)[cell.contentView viewWithTag:103];
    UISwitch *switchBtn = (UISwitch *)[cell.contentView viewWithTag:104];
    [switchBtn setOn:NO];
    
    UIImage *image = [_icons objectAtIndex:indexPath.row];
    icon.image = image;
    [icon setFrame:CGRectMake(15, 0, 45, 45)];
    
    megLb.text = [_names objectAtIndex:indexPath.row];
    
    image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
    line.image = image;
    [line setFrame:CGRectMake(0.5*(320 - image.size.width), 45-image.size.height, image.size.width, image.size.height)];
    if (indexPath.row == 7)
        [line setHidden:YES];
    else
        [line setHidden:NO];
    
    image = [UIImage imageNamed:@"icon_arrow.png"];
    arrow.image = image;
    [arrow setFrame:CGRectMake(280, 0.5*(45 - image.size.height), image.size.width, image.size.height)];
    if (indexPath.row == 1)
    {
        KKModelPreferencePromptVoiceSwitch *voice = [KKPreference sharedPreference].voiceSwitch;
        [arrow setHidden:YES];
        [switchBtn setHidden:NO];
        [switchBtn setOn:voice.isOn];
        
        CGRect rect = switchBtn.frame;
        [switchBtn setFrame:CGRectMake(320 - rect.size.width - 32, 0.5*(45 - rect.size.height), rect.size.width, rect.size.height)];
    }
    else
    {
        [switchBtn setHidden:YES];
        [arrow setHidden:NO];        
    }
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return  45;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger index = indexPath.row;
    
    switch (index) {
        case 0:
        {
            if([KKAuthorization sharedInstance].accessAuthorization.personalInfo)
            {
                KKPersonalInfoViewController *Vc = [[KKPersonalInfoViewController alloc] initWithNibName:@"KKPersonalInfoViewController" bundle:nil];
                [self.navigationController pushViewController:Vc animated:YES];
                [Vc release];
            }
            else
            {
                [KKAppDelegateSingleton jumpToLoginVc];
            }
        }
            break;
        case 1:
            return;
            break;
        case 2:
        {
            KKBindCarViewController *Vc = [[KKBindCarViewController alloc] initWithNibName:@"KKBindCarViewController" bundle:nil];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
        }
            break;
        case 3:
        {
            if([KKAuthorization sharedInstance].accessAuthorization.searchCar)
            {
                KKSearchCarViewController *Vc = [[KKSearchCarViewController alloc] initWithNibName:@"KKSearchCarViewController" bundle:nil];
                Vc.nextVc = NextVc_ObdAndCarListVc;
                Vc.skipToBack = YES;
                [self.navigationController pushViewController:Vc animated:YES];
                [Vc release];
            }
            else
            {
                [KKAppDelegateSingleton jumpToLoginVc];
            }
        }
            break;
        case 4:
        {
            if([KKAuthorization sharedInstance].accessAuthorization.scanShop)
            {
                KKScanViewController *Vc = [[KKScanViewController alloc] init];
                Vc.nextVc = NextVc_BindShopVc;
                Vc.isInNavigationController = YES;
                Vc.showsZBarControls = NO;
                [self.navigationController pushViewController:Vc animated:YES];
                [Vc release];
            }
            else
            {
                [KKAppDelegateSingleton jumpToLoginVc];
            }
        }
            break;
        case 5:
        {
            KKUserFeedbackViewController *Vc = [[KKUserFeedbackViewController alloc] initWithNibName:@"KKUserFeedbackViewController" bundle:nil];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            break;
        }
        case 6:
        {
            _hud = [[MBProgressHUD alloc] initWithWindow:[UIApplication sharedApplication].keyWindow];
            _hud.delegate = self;
            _hud.labelText = @"正在检测";
            _hud.minSize = CGSizeMake(135.f, 135.f);
            [_hud show:YES];
            [[UIApplication sharedApplication].keyWindow addSubview:_hud];
            [_hud release];
            
            [[KKProtocolEngine sharedPtlEngine] newVersion:CurrentSystemPlatform appVersion:KKAppDelegateSingleton.versionStr platformVersion:CurrentSystemVersion mobileModel:[KKHelper platformString] delegate:self];
            
            break;
        }
        case 7:
        {
            KKAboutMeViewController *Vc = [[KKAboutMeViewController alloc] initWithNibName:@"KKAboutMeViewController" bundle:nil];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
            
            break;
        }
        default:
            break;
    }
}

#pragma mark -
#pragma mark MBProgressHUDDelegate

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    [_hud removeFromSuperview];
    _hud = nil;
}

- (NSNumber *)newVersionResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
    [_hud hide:YES];
    
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelNewVersionRsp *versionRsp = (KKModelNewVersionRsp *)rsp;
    
    if ([versionRsp.action isEqualToString:@"force"])
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:versionRsp.header.desc WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            [self updateAppVersion:versionRsp.url];
        }];
        [alertView show];
        [alertView release];
    }
    else if ([versionRsp.action isEqualToString:@"alert"])
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:versionRsp.header.desc WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            [self updateAppVersion:versionRsp.url];
        }];
        [alertView show];
        [alertView release];
    }
    else if ([versionRsp.action isEqualToString:@"normal"])
        [KKCustomAlertView showAlertViewWithMessage:versionRsp.header.desc];
    
    
    return KKNumberResultEnd;
}

- (NSNumber *)userLogoutResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:^{
            [KKProtocolEngine sharedPtlEngine].userName = nil;
            [KKProtocolEngine sharedPtlEngine].password = nil;
            [KKMessagePollingManager stopPolling];
            [KKAppDelegateSingleton logOff];
        }];
		return KKNumberResultEnd;
	}
    
    [KKProtocolEngine sharedPtlEngine].userName = nil;
    [KKProtocolEngine sharedPtlEngine].password = nil;
    [KKMessagePollingManager stopPolling];
    [KKAppDelegateSingleton logOff];
    
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
    _hud = nil;
}

- (void)dealloc
{
    _hud = nil;
    [super dealloc];
}

@end
