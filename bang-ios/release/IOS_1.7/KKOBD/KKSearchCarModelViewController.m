//
//  KKSearchCarModelViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-28.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKSearchCarModelViewController.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "TTTAttributedLabel.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "MBProgressHUD.h"

@interface KKSearchCarModelViewController ()

@end

@implementation KKSearchCarModelViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textFieldChanged:) name:UITextFieldTextDidChangeNotification object:nil];
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    [self getInfo];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self resignKeyboardNotification];
    [_textField becomeFirstResponder];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self removeKeyboardNotification];
}

#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
    if (_dataArray == nil)
        _dataArray = [[NSMutableArray alloc] init];
}

- (void) initComponents
{
    [self setNavgationBar];
    [self setBackGroundView];
    [self createSearchBarWithFrame:CGRectMake(0, 0, 320, 44)];
    
    float tabbarHeight = self.haveTabbar ? 49 : 0;
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, 320, currentScreenHeight - 44 - tabbarHeight - 44 - [self getOrignY]) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_mainTableView];
    [_mainTableView release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = self.isBrand ? @"品牌搜索" : @"车型搜索";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)setBackGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:self.view.bounds];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor clearColor];
    bgImv.image = [UIImage imageNamed:@"bg_serviceSeeking.png"];
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)createSearchBarWithFrame:(CGRect)rect
{
    UIImage *image = [UIImage imageNamed:@"bg_shopq_searchBar.png"];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:rect];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    bgImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    image = [UIImage imageNamed:@"bg_shopq_searchBar_field.png"];
    CGSize size = image.size;
    
    UIImageView *bgInputImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 0.5*(44 - image.size.height), 240, image.size.height)];
    bgInputImv.userInteractionEnabled = YES;
    bgInputImv.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    
    image = [UIImage imageNamed:@"icon_shopq_searchBar_magnifier.png"];
    UIImageView *marImv = [[UIImageView alloc] initWithFrame:CGRectMake(10, 0.5*(size.height - image.size.height), image.size.width, image.size.height)];
    marImv.image = image;
    [bgInputImv addSubview:marImv];
    [marImv release];
    
    _textField = [[UITextField alloc] initWithFrame:CGRectMake(30, 0, 210, size.height)];
    _textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _textField.delegate = self;
    _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    _textField.font = [UIFont systemFontOfSize:15.f];
    _textField.textColor = [UIColor blackColor];
    _textField.text = self.brandName;
    [bgInputImv addSubview:_textField];
;    [_textField release];
    
    image = [UIImage imageNamed:@"icon_shopq_searchBar_btn.png"];
    UIButton *sureBtn = [[UIButton alloc] initWithFrame:CGRectMake(255, 0.5*(44 - image.size.height), image.size.width, image.size.height)];
    [sureBtn setBackgroundImage:image forState:UIControlStateNormal];
    [sureBtn.titleLabel setFont:[UIFont systemFontOfSize:15.f]];
    [sureBtn.titleLabel setTextColor:[UIColor whiteColor]];
    [sureBtn setTitle:@"确定" forState:UIControlStateNormal];
    [sureBtn addTarget:self action:@selector(sureButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [bgImv addSubview:sureBtn];
    [sureBtn release];
    
    [bgImv addSubview:bgInputImv];
    [bgInputImv release];
    
    [self.view addSubview:bgImv];
    [bgImv release];
}

- (void)getInfo
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] vehicleBrandModel: nilOrString(_textField.text)  type:self.isBrand ? @"brand" : @"model" brandId:self.brandID delegate:self];

}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void) didKeyboardNotification:(NSNotification*)notification
{
    NSString* nName = notification.name;
    NSDictionary* nUserInfo = notification.userInfo;
    if ([nName isEqualToString:UIKeyboardDidShowNotification])
    {
        NSString* sysStr = [[UIDevice currentDevice] systemVersion];
        sysStr = [sysStr substringToIndex:1];
        NSInteger ver = [sysStr intValue];
        if (ver >= 5)
        {
            NSValue* value = [nUserInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
            CGRect rect = CGRectZero;
            [value getValue:&rect];
            float keyboardHeight = rect.size.height;
            [_mainTableView setFrame:CGRectMake(0, 44, 320, currentScreenHeight - 44  - 44 - [self getOrignY] - keyboardHeight)];
        }
        
    }
    if ([nName isEqualToString:UIKeyboardWillHideNotification])
    {
        [_mainTableView setFrame:CGRectMake(0, 44, 320, currentScreenHeight - 44 - 49 - 44 - [self getOrignY])];
    }
}

- (void)sureButtonClicked
{
    [self getInfo];
    [_textField resignFirstResponder];
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cell_mutiInputCell_isSearching";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        
        UIView *grayView = [[UIView alloc] initWithFrame:CGRectZero];
        grayView.backgroundColor = [UIColor clearColor];
        grayView.tag = 104;
        [cell.contentView addSubview:grayView];
        [grayView release];
        
        UIView *leftLine = [[UIView alloc] initWithFrame:CGRectMake(17, 0, 1, 39)];
        leftLine.backgroundColor = KKCOLOR_d4d4d4;
        leftLine.tag = 100;
        [cell.contentView addSubview:leftLine];
        [leftLine release];
        
        UIView *rightLine = [[UIView alloc] initWithFrame:CGRectMake(303, 0, 1, 39)];
        rightLine.backgroundColor = KKCOLOR_d4d4d4;
        rightLine.tag = 101;
        [cell.contentView addSubview:rightLine];
        [rightLine release];
        
        UIView *downline = [[UIView alloc] initWithFrame:CGRectMake(17, 38, 286, 1)];
        downline.backgroundColor = KKCOLOR_d4d4d4;
        downline.tag = 102;
        [cell.contentView addSubview:downline];
        [downline release];
        
        TTTAttributedLabel *textLabel = [[TTTAttributedLabel alloc] initWithFrame:CGRectMake(50, 11.5, 220, 16)];
        textLabel.backgroundColor = [UIColor clearColor];
        textLabel.font = [UIFont systemFontOfSize:14.0];
        textLabel.numberOfLines = 0;
        textLabel.textColor = [UIColor blackColor];
        textLabel.tag = 103;
        [cell.contentView addSubview:textLabel];
        [textLabel release];
    }
    UIView *leftView = (UIView *)[cell.contentView viewWithTag:100];
    UIView *rightView = (UIView *)[cell.contentView viewWithTag:101];
    UIView *downView = (UIView *)[cell.contentView viewWithTag:102];
    TTTAttributedLabel *textLabel = (TTTAttributedLabel *)[cell.contentView viewWithTag:103];
    UIView *graView = (UIView *)[cell.contentView viewWithTag:104];
    graView.backgroundColor = [UIColor clearColor];
    
    KKModelCarInfo *carInfo = (KKModelCarInfo *)[_dataArray objectAtIndex:indexPath.row];
    textLabel.text = self.isBrand ? carInfo.brandName : carInfo.modelName;
    
    CGFloat height = 39.0;
    
    if (indexPath.row == 0)
    {
        [leftView setFrame:CGRectMake(17, -150, 1, height + 150)];
        [rightView setFrame:CGRectMake(303, -150, 1, height + 150)];
    }
    else
    {
        [leftView setFrame:CGRectMake(17, -1, 1, height)];
        [rightView setFrame:CGRectMake(303, -1, 1, height)];
    }
    [downView setFrame:CGRectMake(17, height - 1, 286, 1)];
    [graView setFrame:CGRectMake(17, 0, 286, height)];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 39;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    UIView *bgView = [cell viewWithTag:104];
    if (bgView)
    {
        KKModelBrandModel *obj = (KKModelBrandModel *)[_dataArray objectAtIndex:indexPath.row];
        
        [UIView animateWithDuration:0.3 animations:^{
            bgView.backgroundColor = KKCOLOR_dedede;
  
        } completion:^(BOOL finished) {
            bgView.backgroundColor = [UIColor clearColor];
            if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKSearchCarModelViewDidSelected:isBrand:)])
                [self.delegate KKSearchCarModelViewDidSelected:obj isBrand:self.isBrand];
            
            [UIView animateWithDuration:0.3 animations:^{
                [self backButtonClicked];
            }];
        }];
    }

}
#pragma mark -
#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self getInfo];
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldChanged:(id)sender
{
    if ([_textField.text length] > 0)
        [self getInfo];
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)vehicleGetBrandModelResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        
		return KKNumberResultEnd;
	}
    [_dataArray removeAllObjects];
    KKModelGetBrandModelRsp *infoRsp = (KKModelGetBrandModelRsp *)rsp;
    [_dataArray addObjectsFromArray:infoRsp.KKArrayFieldName(result, KKModelCarInfo)];
    [_mainTableView reloadData];
    
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
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:nil];
    
    if (_dataArray != nil)
        [_dataArray release];
    _dataArray = nil;
    
    _mainTableView = nil;
    _textField = nil;
    
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:nil];
    
    if (_dataArray != nil)
        [_dataArray release];
    _dataArray = nil;
    
    _mainTableView = nil;
    _textField = nil;
    
    [super dealloc];
}

@end
