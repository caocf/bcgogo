//
//  KKBindCarViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-16.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKBindCarViewController.h"
#import "KKApplicationDefine.h"
#import "KKViewUtils.h"
#import "UIViewController+extend.h"
#import "KKShowOrAddNewBindCarViewController.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKPreference.h"
#import "KKAppDelegate.h"
#import "KKTBVehicle.h"

@interface KKSelectedButton : UIButton
@property(nonatomic ,assign)NSInteger   index;

@end

@implementation KKSelectedButton

@end


@interface KKBindCarViewController ()
@property (nonatomic, retain) NSMutableArray      *dataArray;
@end

@implementation KKBindCarViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(vehicleListUpdated) name:Notification_UpdatedVehicleList object:nil];
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
    
    if([KKAuthorization sharedInstance].accessAuthorization.localCarManager)
    {
        self.dataArray = KKAppDelegateSingleton.vehicleList;
        [_mainTableView reloadData];
    }
    else
    {
        [self getVehicleListInfo];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    _mainTableView.editing = NO;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}
#pragma mark -
#pragma mark Custom methods

- (void) initVariables
{
//    _dataArray  = [[NSMutableArray alloc] init];
}

- (void) initComponents
{
    [self setBachGroundView];
    [self setNavgationBar];
    
    UIView *headView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 8)];
    headView.backgroundColor = [UIColor clearColor];
    
    _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, 320, currentScreenHeight - 44 - 49 - [self getOrignY]) style:UITableViewStylePlain];
    _mainTableView.delegate = self;
    _mainTableView.dataSource = self;
    _mainTableView.backgroundColor = [UIColor clearColor];
    _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _mainTableView.tableHeaderView = headView;
//    _mainTableView.tableFooterView = [self creatTableViewFootView];
    [self.view addSubview:_mainTableView];
    [headView release];
    [_mainTableView release];
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"车辆管理";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (UIImage *)getBackgroundImageWithIndexRow:(NSInteger)row
{
    UIImage *image = nil;
    
    if (row == 0)
        image = [UIImage imageNamed:@"bg_setting_bind_cell_up.png"];
    else
        image = [UIImage imageNamed:@"bg_setting_bind_cell_middle.png"];

    return [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
}

- (UIView *)creatTableViewFootView
{
    UIControl *view = [[UIControl alloc] initWithFrame:CGRectMake(0, 0, 320, 45)];
    view.backgroundColor = [UIColor clearColor];
    view.userInteractionEnabled = YES;
    [view addTarget:self action:@selector(addNewCarButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    
    UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(8.75, 0, 302.5, 45)];
    bgImageView.backgroundColor = [UIColor clearColor];
    bgImageView.userInteractionEnabled = NO;
    bgImageView.image = [UIImage imageNamed:@"bg_setting_bind_cell_down.png"];
    [view addSubview:bgImageView];
    [bgImageView release];
    
    UIImage *image = [UIImage imageNamed:@"icon_setting_bind_addCar.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(120, 0.5*(45 - image.size.height), image.size.width, image.size.height)];
    iconImv.image = image;
    [view addSubview:iconImv];
    [iconImv release];
    
    UILabel *txtLb = [[UILabel alloc] initWithFrame:CGRectMake(144, 15, 100, 15)];
    txtLb.textAlignment = UITextAlignmentLeft;
    txtLb.textColor = KKCOLOR_333333;
    txtLb.text = @"新增车辆";
    txtLb.font = [UIFont systemFontOfSize:14.f];
    [view addSubview:txtLb];
    [txtLb release];
    
    return [view autorelease];
}

- (void)getVehicleListInfo
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [[KKProtocolEngine sharedPtlEngine] vehicleListInfo:[KKPreference sharedPreference].userInfo.userNo delegate:self];
}

-(void)vehicleListUpdated
{
    self.dataArray = KKAppDelegateSingleton.vehicleList;
    [_mainTableView reloadData];
}
#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)addNewCarButtonClicked
{
    _isChanged = YES;
    
    KKShowOrAddNewBindCarViewController *Vc = [[KKShowOrAddNewBindCarViewController alloc] initWithNibName:@"KKShowOrAddNewBindCarViewController" bundle:nil];
    Vc.type = KKBindCar_addNew;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (void)updateDefaultVehicleButtonClicked:(id)sender
{
    KKSelectedButton *button = (KKSelectedButton *)sender;
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[self.dataArray objectAtIndex:button.index];
    if ([vehicleInfo.isDefault length] > 0 && [vehicleInfo.isDefault isEqualToString:@"YES"])
        return;
    else
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:[NSString stringWithFormat:@"您是否想要将车辆%@%@设置为默认车辆 ？",vehicleInfo.vehicleBrand,vehicleInfo.vehicleModel] WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            
            _isChanged = YES;
            if([KKAuthorization sharedInstance].accessAuthorization.localCarManager)
            {
                KKTBVehicle *tbVehicle = [[KKTBVehicle alloc] initWithDB:[KKDB sharedDB]];
                if([tbVehicle setDefaultVehicleWithUserNo:vehicleInfo.userNo vehicleNo:vehicleInfo.vehicleNo])
                {
                    [KKAppDelegateSingleton detachVehicleListAndObdList:[tbVehicle getVehicleWithUserNo:[KKPreference sharedPreference].userInfo.userNo]];
                    [KKCustomAlertView showAlertViewWithMessage:@"设置默认车辆成功！"];
                }
                else
                {
                    [KKCustomAlertView showErrorAlertViewWithMessage:@"设置默认车辆失败！" block:nil];
                }
                [tbVehicle release];

            }
            else
            {
                [MBProgressHUD showHUDAddedTo:self.view animated:YES];
                [[KKProtocolEngine sharedPtlEngine] updateDefaultVehicle:vehicleInfo.vehicleId delegate:self];
            }
        }];
        [alertView show];
        [alertView release];
        
    }
    
}

#pragma mark -
#pragma mark UITableViewDataSource,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* reuseID = @"ReservationService";
    UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:reuseID];
    
    if (nil == cell)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseID] autorelease];
        
        UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(8.75, 0, 302.5, 45)];
        bgImageView.tag = 100;
        bgImageView.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:bgImageView];
        [bgImageView release];
        
        UILabel *brandLb = [[UILabel alloc] initWithFrame:CGRectMake(45, 10, 100, 9)];
        brandLb.textAlignment = UITextAlignmentLeft;
        brandLb.textColor = KKCOLOR_333333;
        brandLb.text = @"品牌 : ";
        brandLb.font = [UIFont systemFontOfSize:9.f];
        brandLb.minimumFontSize = 7;
        brandLb.tag = 101;
        [cell.contentView addSubview:brandLb];
        [brandLb release];
        
        UILabel *carNumLb = [[UILabel alloc] initWithFrame:CGRectMake(45, 27, 100, 9)];
        carNumLb.textAlignment = UITextAlignmentLeft;
        carNumLb.textColor = KKCOLOR_333333;
        carNumLb.text = @"车牌号 : ";
        carNumLb.font = [UIFont systemFontOfSize:9.f];
        carNumLb.minimumFontSize = 7;
        carNumLb.tag = 102;
        [cell.contentView addSubview:carNumLb];
        [carNumLb release];
        
        UILabel *carModelLb = [[UILabel alloc] initWithFrame:CGRectMake(150, 10, 100, 9)];
        carModelLb.textAlignment = UITextAlignmentLeft;
        carModelLb.textColor = KKCOLOR_333333;
        carModelLb.text = @"车型 : ";
        carModelLb.font = [UIFont systemFontOfSize:9.f];
        carModelLb.minimumFontSize = 7;
        carModelLb.tag = 103;
        [cell.contentView addSubview:carModelLb];
        [carModelLb release];
        
        UILabel *dNumLb = [[UILabel alloc] initWithFrame:CGRectMake(150, 27, 100, 9)];
        dNumLb.textAlignment = UITextAlignmentLeft;
        dNumLb.textColor = KKCOLOR_333333;
        dNumLb.text = @"设备号 : ";
        dNumLb.font = [UIFont systemFontOfSize:9.f];
        dNumLb.minimumFontSize = 7;
        dNumLb.tag = 104;
        [cell.contentView addSubview:dNumLb];
        [dNumLb release];
        
        UIImage *image = [UIImage imageNamed:@"icon_setting_bind_line.png"];
        UIImageView *line = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(320 - image.size.width), 45-image.size.height, image.size.width, image.size.height)];
        line.image = image;
        line.tag = 105;
        [cell.contentView addSubview:line];
        [line release];
        
        image = [UIImage imageNamed:@"icon_arrow.png"];
        
        UIImageView *accImv = [[UIImageView alloc] initWithFrame:CGRectMake(285, 0.5*(45-image.size.height), image.size.width, image.size.height)];
        accImv.image = image;
        accImv.backgroundColor = [UIColor clearColor];
        accImv.tag = 106;
        [cell.contentView addSubview:accImv];
        [accImv release];
        
        KKSelectedButton *selectBtn = [[KKSelectedButton alloc] initWithFrame:CGRectMake(2.5, 2.5, 40, 40)];
        selectBtn.backgroundColor = [UIColor clearColor];
        selectBtn.tag = 107;
        [selectBtn addTarget:self action:@selector(updateDefaultVehicleButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:selectBtn];
        [selectBtn release];
        
    }
    UIImageView *bgimv = (UIImageView *)[cell.contentView viewWithTag:100];
    UILabel *brandLabel = (UILabel *)[cell.contentView viewWithTag:101];
    UILabel *carNumLabel = (UILabel *)[cell.contentView viewWithTag:102];
    UILabel *carModelLabel = (UILabel *)[cell.contentView viewWithTag:103];
    UILabel *deviceNumLabel = (UILabel *)[cell.contentView viewWithTag:104];
    KKSelectedButton *selectedbutton = (KKSelectedButton *)[cell.contentView viewWithTag:107];
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[self.dataArray objectAtIndex:indexPath.row];
    brandLabel.text = [NSString stringWithFormat:@"品牌 :%@",nilToDefaultString(vehicleInfo.vehicleBrand,@"未知")];
    carNumLabel.text = [NSString stringWithFormat:@"车牌号 : %@",nilToDefaultString(vehicleInfo.vehicleNo,@"未知")];
    carModelLabel.text = [NSString stringWithFormat:@"车型 : %@",nilToDefaultString(vehicleInfo.vehicleModel,@"未知")];
    deviceNumLabel.text = [NSString stringWithFormat:@"设备号 : %@",nilToDefaultString(vehicleInfo.obdSN,@"未知")];
    
    selectedbutton.index = indexPath.row;
    [selectedbutton setImage:[UIImage imageNamed:@"icon_unselected.png"] forState:UIControlStateNormal];
    if ([vehicleInfo.isDefault length] > 0 && [vehicleInfo.isDefault isEqualToString:@"YES"])
    {
        [selectedbutton setImage:[UIImage imageNamed:@"icon_selected.png"] forState:UIControlStateNormal];
    }
    
    [bgimv setFrame:CGRectMake(8.75, 0, 302.5, 45)];
    bgimv.image = [self getBackgroundImageWithIndexRow:indexPath.row];
    
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 45;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 45;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIControl *view = [[UIControl alloc] initWithFrame:CGRectMake(0, 0, 320, 45)];
    view.backgroundColor = [UIColor clearColor];
    view.userInteractionEnabled = YES;
    [view addTarget:self action:@selector(addNewCarButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    
    UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(8.75, 0, 302.5, 45)];
    bgImageView.backgroundColor = [UIColor clearColor];
    bgImageView.userInteractionEnabled = NO;
    bgImageView.image = [self.dataArray count] > 0 ? [UIImage imageNamed:@"bg_setting_bind_cell_down.png"] : [UIImage imageNamed:@"bg_setting_bind_cell_full.png"];
    [view addSubview:bgImageView];
    [bgImageView release];
    
    UIImage *image = [UIImage imageNamed:@"icon_setting_bind_addCar.png"];
    UIImageView *iconImv = [[UIImageView alloc] initWithFrame:CGRectMake(120, 0.5*(45 - image.size.height), image.size.width, image.size.height)];
    iconImv.image = image;
    [view addSubview:iconImv];
    [iconImv release];
    
    UILabel *txtLb = [[UILabel alloc] initWithFrame:CGRectMake(144, 15, 100, 15)];
    txtLb.textAlignment = UITextAlignmentLeft;
    txtLb.textColor = KKCOLOR_333333;
    txtLb.text = @"新增车辆";
    txtLb.font = [UIFont systemFontOfSize:14.f];
    [view addSubview:txtLb];
    [txtLb release];
    
    return [view autorelease];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    _isChanged = YES;
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[self.dataArray objectAtIndex:indexPath.row];
    
    KKShowOrAddNewBindCarViewController *Vc = [[KKShowOrAddNewBindCarViewController alloc] initWithNibName:@"KKShowOrAddNewBindCarViewController" bundle:nil];
    Vc.type = KKBindCar_show;
    Vc.vehicleId = vehicleInfo.vehicleId;
    Vc.vehicleDetailInfo = vehicleInfo;
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
 
    _isChanged = YES;
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[self.dataArray objectAtIndex:indexPath.row];
    
    if([KKAuthorization sharedInstance].accessAuthorization.localCarManager)
    {
        KKTBVehicle *tbVehicle = [[KKTBVehicle alloc] initWithDB:[KKDB sharedDB]];
        if([tbVehicle deleteVehicle:vehicleInfo])
        {
            [KKAppDelegateSingleton detachVehicleListAndObdList:[tbVehicle getVehicleWithUserNo:[KKPreference sharedPreference].userInfo.userNo]];
            [KKCustomAlertView showAlertViewWithMessage:@"车辆删除成功！"];
        }
        else
        {
            [KKCustomAlertView showErrorAlertViewWithMessage:@"车辆删除失败！" block:nil];
        }
        [tbVehicle release];
    }
    else
    {
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        [[KKProtocolEngine sharedPtlEngine]vehicleDeleteWithId:vehicleInfo.vehicleId delegate:self];
        
        _mainTableView.editing = NO;
    }
}

- (NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return @"删除";
}

#pragma mark -
#pragma mark MBProgressHUDDelegate

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    [hud removeFromSuperview];
    hud = nil;
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate
- (NSNumber *)vehicleListResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKVehicleListRsp *listRsp = (KKVehicleListRsp *)rsp;
    
    [KKAppDelegateSingleton detachVehicleListAndObdList:listRsp.KKArrayFieldName(vehicleList,KKModelVehicleDetailInfo)];
    
    self.dataArray = KKAppDelegateSingleton.vehicleList;
    [_mainTableView reloadData];
    
    if (_isChanged)
    {
        //重新获取车辆和obd列表
//        [KKAppDelegateSingleton reLogin];
        
        [KKAppDelegateSingleton updateVehicleCondition:e_CarNotOnLine];
        [KKAppDelegateSingleton scanPeripheral];
        _isChanged = NO;
    }
    
    return KKNumberResultEnd;
}

- (NSNumber *)vehicleDeleteResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    KKModelProtocolRsp *proRsp = (KKModelProtocolRsp *)rsp;
    [KKCustomAlertView showAlertViewWithMessage:proRsp.header.desc];
    [self getVehicleListInfo];
    
    return KKNumberResultEnd;
}

- (NSNumber *)updateDefaultVehicle:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    [self getVehicleListInfo];
    
    return KKNumberResultEnd;
}


#pragma mark -
#pragma mark HandleMemory

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
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.dataArray = nil;
    [super dealloc];
}
@end
