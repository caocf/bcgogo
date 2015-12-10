//
//  KKObdAndCarListViewController.m
//  KKOBD
//
//  Created by Jiahai on 13-12-18.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKObdAndCarListViewController.h"
#import "KKCustomTextField.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "UIViewController+extend.h"
#import "KKCustomAlertView.h"
#import "KKHelper.h"
#import "KKProtocolEngine.h"
#import "KKUtils.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKModelComplex.h"
#import "KKViewUtils.h"


@interface KKCheckedButton : UIButton
@property(nonatomic ,assign)NSInteger   index;

@end

@implementation KKCheckedButton

@end

@interface KKObdAndCarListViewController ()

@end

@implementation KKObdAndCarListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void) viewWillAppear:(BOOL)animated
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updatedVehicleList) name:Notification_UpdatedVehicleList object:nil];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:Notification_UpdatedVehicleList object:nil];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    [self setVcEdgesForExtendedLayout];
    
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"绑定OBD";
    
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
    
    float originY = 10;
    
    UILabel *label0 = [[UILabel alloc] initWithFrame:CGRectMake(0, originY +11, 75, 15)];
    label0.backgroundColor = [UIColor clearColor];
    label0.textColor = [UIColor blackColor];
    label0.font = [UIFont systemFontOfSize:15.0f];
    label0.textAlignment = UITextAlignmentRight;
    label0.text = @"OBD号码 :";
    [self.view addSubview:label0];
    [label0 release];
    
    _obdText = [[KKCustomTextField alloc] initWithFrame:CGRectMake(85, 10, 223, 38) WithType:eTextFieldNone WithPlaceholder:nil WithImage:nil WithRightInsetWidth:0];
    _obdText.index = 10;
    _obdText.textField.enabled = NO;
    _obdText.textField.text = self.obdSN;
    [self.view addSubview:_obdText];
    [_obdText release];
    
    originY += 68;
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, 320, currentScreenHeight - originY - 49 - 44 - [self getOrignY])];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    [self.view addSubview:_tableView];
    [_tableView release];
}

-(void) backButtonClicked
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)obdBindVehicleButtonClicked:(id)sender
{
    KKCheckedButton *button = (KKCheckedButton *)sender;
    
    if(!self.obdSN && !self.vehicleVin)
    {
        [KKCustomAlertView showAlertViewWithMessage:@"OBD号码/车架号不能为空！"];
        return;
    }
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[KKAppDelegateSingleton.vehicleList objectAtIndex:button.index];
    if (vehicleInfo.obdSN && [vehicleInfo.obdSN isEqualToString:self.obdSN])
        return;
    else
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:[NSString stringWithFormat:@"您是否想要将OBD绑定到车辆%@%@ ？",vehicleInfo.vehicleBrand,vehicleInfo.vehicleModel] WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"取消" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"确定" imageName:@"alert-blue2-button.png" block:^{
            
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            [[KKProtocolEngine sharedPtlEngine] obdBinding:[KKProtocolEngine sharedPtlEngine].userName
                                                     obdSN:self.obdSN
                                                vehicleVin:self.vehicleVin
                                                 vehicleId:vehicleInfo.vehicleId
                                                 vehicleNo:vehicleInfo.vehicleNo
                                              vehicleModel:vehicleInfo.vehicleModel
                                            vehicleModelId:vehicleInfo.vehicleModelId
                                              vehicleBrand:vehicleInfo.vehicleBrand
                                            vehicleBrandId:vehicleInfo.vehicleBrandId
                                                sellShopId:vehicleInfo.recommendShopId
                                                  engineNo:vehicleInfo.engineNo
                                                  registNo:vehicleInfo.registNo
                                       nextMaintainMileage:vehicleInfo.nextMaintainMileage
                                         nextInsuranceTime:[vehicleInfo.nextInsuranceTime length] > 0 ? [KKUtils convertStringToDate:vehicleInfo.nextInsuranceTime]:nil
                                           nextExamineTime:[vehicleInfo.nextExamineTime length] > 0 ? [KKUtils convertStringToDate:vehicleInfo.nextExamineTime]:nil
                                            currentMileage:vehicleInfo.currentMileage
                                                  delegate:self];

        }];
        [alertView show];
        [alertView release];
        
    }
    
}

- (UIImage *)getBackgroundImageWithIndexRow:(NSInteger)row
{
    UIImage *image = nil;
    
    if (row == 0)
    {
        if([KKAppDelegateSingleton.vehicleList count] == 1)
        {
            image = [UIImage imageNamed:@"bg_setting_bind_cell_full.png"];
        }
        else
        {
            image = [UIImage imageNamed:@"bg_setting_bind_cell_up.png"];
        }
    }
    else if(row == [KKAppDelegateSingleton.vehicleList count]-1)
        image = [UIImage imageNamed:@"bg_setting_bind_cell_down.png"];
    else
        image = [UIImage imageNamed:@"bg_setting_bind_cell_middle.png"];
    
    return [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
}

-(void) updatedVehicleList
{
    [_tableView reloadData];
}

#pragma mark -
#pragma mark UITableViewDelegate
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [KKAppDelegateSingleton.vehicleList count];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* reuseID = @"ObdAndListCell";
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
        
//        image = [UIImage imageNamed:@"icon_arrow.png"];
//        
//        UIImageView *accImv = [[UIImageView alloc] initWithFrame:CGRectMake(285, 0.5*(45-image.size.height), image.size.width, image.size.height)];
//        accImv.image = image;
//        accImv.backgroundColor = [UIColor clearColor];
//        accImv.tag = 106;
//        [cell.contentView addSubview:accImv];
//        [accImv release];
        
        KKCheckedButton *selectBtn = [[KKCheckedButton alloc] initWithFrame:CGRectMake(2.5, 2.5, 40, 40)];
        selectBtn.backgroundColor = [UIColor clearColor];
        selectBtn.tag = 107;
        [selectBtn addTarget:self action:@selector(obdBindVehicleButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:selectBtn];
        [selectBtn release];
        
    }
    UIImageView *bgimv = (UIImageView *)[cell.contentView viewWithTag:100];
    UILabel *brandLabel = (UILabel *)[cell.contentView viewWithTag:101];
    UILabel *carNumLabel = (UILabel *)[cell.contentView viewWithTag:102];
    UILabel *carModelLabel = (UILabel *)[cell.contentView viewWithTag:103];
    UILabel *deviceNumLabel = (UILabel *)[cell.contentView viewWithTag:104];
    KKCheckedButton *selectedbutton = (KKCheckedButton *)[cell.contentView viewWithTag:107];
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[KKAppDelegateSingleton.vehicleList objectAtIndex:indexPath.row];
    brandLabel.text = [NSString stringWithFormat:@"品牌 :%@",nilToDefaultString(vehicleInfo.vehicleBrand,@"未知")];
    carNumLabel.text = [NSString stringWithFormat:@"车牌号 : %@",nilToDefaultString(vehicleInfo.vehicleNo,@"未知")];
    carModelLabel.text = [NSString stringWithFormat:@"车型 : %@",nilToDefaultString(vehicleInfo.vehicleModel,@"未知")];
    deviceNumLabel.text = [NSString stringWithFormat:@"设备号 : %@",nilToDefaultString(vehicleInfo.obdSN,@"未知")];
    
    selectedbutton.index = indexPath.row;
    [selectedbutton setImage:[UIImage imageNamed:@"icon_unselected.png"] forState:UIControlStateNormal];
    if (vehicleInfo.obdSN && [vehicleInfo.obdSN isEqualToString:self.obdSN])
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

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    KKCheckedButton *btn = (KKCheckedButton *)[cell viewWithTag:107];
    [self obdBindVehicleButtonClicked:btn];
}


#pragma mark -
#pragma mark KKProtocolEngineDelegate
-(NSNumber *) obdBindResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
		return KKNumberResultEnd;
	}
    
    [KKCustomAlertView showAlertViewWithMessage:((KKModelProtocolRsp *)rsp).header.desc block:nil];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:Notification_UpdateVehicleList object:nil];
    
    return KKNumberResultEnd;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
