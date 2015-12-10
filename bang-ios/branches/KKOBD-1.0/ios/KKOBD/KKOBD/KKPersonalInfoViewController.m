//
//  KKPersonalInfoViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKPersonalInfoViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKModifyPasswordViewController.h"
#import "KKPreference.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "MBProgressHUD.h"

@interface KKPersonalInfoViewController ()

@end

@implementation KKPersonalInfoViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    [self getUserInfo];
}

#pragma mark -
#pragma mark Custom Methods
- (void) initVariables
{
    _titles = [[NSMutableArray alloc] initWithObjects:@"用户名 :",@"修改密码",@"姓   名 :",@"手机号 :", nil];
}

- (void)initComponents
{
    [self setNavgationBar];
    [self setBachGroundView];
    
    UIImage *image = [UIImage imageNamed:@"bg_setting.png"];
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 10, image.size.width, 165)];
    bgImv.userInteractionEnabled = YES;
    bgImv.image = image;
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,0, image.size.width,160) style:UITableViewStylePlain];
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.backgroundView = nil;
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _mainTableView.scrollEnabled = NO;
    [bgImv addSubview:_mainTableView];
    [_mainTableView release];
    
    [self.view addSubview:bgImv];
    [bgImv release];
    
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"客户资料";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    UIImage *image = [UIImage imageNamed:@"icon_register.png"];
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, image.size.width, image.size.height)];
    [button setTitle:@"确定" forState:UIControlStateNormal];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    [button.titleLabel setFont:[UIFont systemFontOfSize:15.0f]];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(sureButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithCustomView:button] autorelease];
    [button release];
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

- (void)getUserInfo
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] userInformation:[KKProtocolEngine sharedPtlEngine].userName delegate:self];
}

- (NSString *)getTextFieldTextForTableViewCell:(NSInteger)index
{
    UITableViewCell *cell = [_mainTableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:index inSection:0]];
    UITextField *textfield = (UITextField *)[cell.contentView viewWithTag:103];
    return textfield.text;
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)sureButtonClicked
{
    [self resignVcFirstResponder];
    
    NSString *userNo = [self getTextFieldTextForTableViewCell:0];
    if ([userNo length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入用户名！"];
        return;
    }
    
    NSString *userName = [self getTextFieldTextForTableViewCell:2];
    if ([userName length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入客户名！"];
        return;
    }
    
    NSString *mobileNum = [self getTextFieldTextForTableViewCell:3];
    if ([mobileNum length] == 0)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入手机号码！"];
        return;
    }
    else if (![KKHelper KKHElpRegexMatchForTelephone:mobileNum])
    {
        [KKCustomAlertView showAlertViewWithMessage:@"请输入正确的手机号码!"];
        return;
    }
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] userInformationModify:userNo mobile:mobileNum name:userName delegate:self];

}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_titles count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_PersonalInfo";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        
        UILabel *textLb = [[UILabel alloc] initWithFrame:CGRectMake(30, 12.5, 100, 15)];
        textLb.tag = 100;
        textLb.textColor = [UIColor blackColor];
        textLb.font = [UIFont systemFontOfSize:15.0f];
        textLb.backgroundColor = [UIColor clearColor];
        textLb.textAlignment = UITextAlignmentLeft;
        [cell.contentView addSubview:textLb];
        [textLb release];
        
        UITextField *textfield = [[UITextField alloc] initWithFrame:CGRectMake(95, 10, 200, 20)];
        textfield.backgroundColor = [UIColor clearColor];
        textfield.delegate = self;
        textfield.tag = 103;
        textfield.font = [UIFont systemFontOfSize:15.f];
        [cell.contentView addSubview:textfield];
        [textfield release];
        
        UIImageView *arrImv = [[UIImageView alloc] initWithFrame:CGRectZero];
        arrImv.backgroundColor = [UIColor clearColor];
        arrImv.tag = 101;
        [cell.contentView addSubview:arrImv];
        [arrImv release];
        
        UIImageView *separateLine = [[UIImageView alloc] initWithFrame:CGRectZero];
        separateLine.backgroundColor = [UIColor clearColor];
        separateLine.tag = 102;
        [cell.contentView addSubview:separateLine];
        [separateLine release];
        
    }
    UILabel *titleLb = (UILabel *)[cell.contentView viewWithTag:100];
    UIImageView *arrow = (UIImageView *)[cell.contentView viewWithTag:101];
    UIImageView *line = (UIImageView *)[cell.contentView viewWithTag:102];
    UITextField *textfield = (UITextField *)[cell.contentView viewWithTag:103];
    textfield.hidden = NO;
    
    titleLb.text = [_titles objectAtIndex:indexPath.row];
    [titleLb sizeToFit];
    
    UIImage *image = [UIImage imageNamed:@"icon_setting_separateLine.png"];
    line.image = image;
    [line setFrame:CGRectMake(8, 40 - image.size.height, 304, image.size.height)];
    if (indexPath.row == 3)
        [line setHidden:YES];
    else
        [line setHidden:NO];
    
    if (indexPath.row == 0)
    {
        textfield.userInteractionEnabled = NO;
        textfield.text = self.userInfo.userNo;
    }
    else if (indexPath.row == 1)
        textfield.hidden = YES;
    else if (indexPath.row == 2)
        textfield.text = self.userInfo.name;
    else
        textfield.text= self.userInfo.mobile;
    
    image = [UIImage imageNamed:@"icon_arrow.png"];
    arrow.image = image;
    [arrow setFrame:CGRectMake(270, 0.5*(45 - image.size.height), image.size.width, image.size.height)];
    if (indexPath.row == 1)
        [arrow setHidden:NO];
    else
        [arrow setHidden:YES];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return  40;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger index = indexPath.row;
    switch (index) {
        case 0:
            break;
        case 1:
        {
            KKModifyPasswordViewController *Vc = [[KKModifyPasswordViewController alloc] initWithNibName:@"KKModifyPasswordViewController" bundle:nil];
            [self.navigationController pushViewController:Vc animated:YES];
            [Vc release];
        }
        default:
            break;
    }
}

#pragma mark -
#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)userInformationResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        return KKNumberResultEnd;
    }
    
    KKModelUserInfomationRsp *userInfoRsp = (KKModelUserInfomationRsp *)rsp;
    self.userInfo = userInfoRsp.userInfo;
    
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    userInfo.username = userInfoRsp.userInfo.name;
    userInfo.mobile = userInfoRsp.userInfo.mobile;
    [KKPreference sharedPreference].userInfo = userInfo;
    
    [_mainTableView reloadData];
    
    return KKNumberResultEnd;
}

- (NSNumber *)userInformationModifyResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        return KKNumberResultEnd;
    }
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc];
    
    KKModelPreferenceUserInfo *userInfo = [KKPreference sharedPreference].userInfo;
    userInfo.userNo = [self getTextFieldTextForTableViewCell:0];
    userInfo.username = [self getTextFieldTextForTableViewCell:2];
    userInfo.mobile = [self getTextFieldTextForTableViewCell:3];
    [KKPreference sharedPreference].userInfo = userInfo;
    
    [KKProtocolEngine sharedPtlEngine].userName = [self getTextFieldTextForTableViewCell:0];
    
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
