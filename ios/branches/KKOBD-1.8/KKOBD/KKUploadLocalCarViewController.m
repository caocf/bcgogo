//
//  KKUploadLocalCarViewController.m
//  KKOBD
//
//  Created by Jiahai on 14-1-9.
//  Copyright (c) 2014年 zhuyc. All rights reserved.
//

#import "KKUploadLocalCarViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"
#import "KKModelComplex.h"
#import "KKHelper.h"
#import "KKProtocolEngine.h"
#import "KKUtils.h"
#import "KKCustomAlertView.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKPreference.h"

@interface KKUploadLocalCarViewController ()

@end

#define  VehicleChecked             [NSNumber numberWithBool:YES]
#define  VehicleUnChecked           [NSNumber numberWithBool:NO]

@implementation KKUploadLocalCarViewController

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
	// Do any additional setup after loading the view.
    
    [self setVcEdgesForExtendedLayout];
    
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"上传本地车辆";
    
    selectedVehicles = [[NSMutableArray alloc] init];
    for(int i=0;i<[self.localVehicleList count];i++)
    {
        [selectedVehicles addObject:VehicleUnChecked];
    }
    
    self.navigationItem.hidesBackButton = YES;
    
    self.navigationItem.rightBarButtonItem = [KKViewUtils createNavigationBarButtonItemWithTitle:@"跳过" bgImage:[UIImage imageNamed:@"icon_sBt_skip.png"] target:self action:@selector(skipButtonClicked)];
    
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_background.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];
    
    float originY = 32;
    
    int tableHeight = 0;
    if([self.localVehicleList count] >= 5)
        tableHeight = 45*5;
    else
        tableHeight = 45*[self.localVehicleList count];
//    int tableHeight = currentScreenHeight - [self getOrignY] - 44 - 49 - originY;
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, 320, tableHeight)];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.backgroundColor = [UIColor clearColor];
    [_tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    [self.view addSubview:_tableView];
    [_tableView release];
    
    originY += 36;
    
    UIImage *image = [UIImage imageNamed:@"bg_registerBtn.png"];
    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(14, originY+_tableView.frame.size.height, image.size.width, image.size.height)];
    [submitButton setBackgroundColor:[UIColor clearColor]];
    [submitButton setBackgroundImage:image forState:UIControlStateNormal];
    [submitButton setTitle:@"提交" forState:UIControlStateNormal];
    [submitButton addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:submitButton];
    [submitButton release];

}

-(void) skipButtonClicked
{
    [KKAppDelegateSingleton ShowRootView];
}

-(void) submitButtonClicked
{
    NSMutableArray *array = [[NSMutableArray alloc] init];
    for(int i=0;i<[selectedVehicles count];i++)
    {
        NSNumber *checked = [selectedVehicles objectAtIndex:i];
        KKModelVehicleDetailInfo *info = [self.localVehicleList objectAtIndex:i];
        
        if([checked isEqualToNumber:VehicleChecked])
        {
            info.userNo = [KKPreference sharedPreference].userInfo.userNo;
            [array addObject:info];
        }
    }
    if([array count]>0)
        [[KKProtocolEngine sharedPtlEngine] vehicleSaveList:array delegate:self];
    else
        [KKCustomAlertView showAlertViewWithMessage:@"请选择需上传的车辆！"];
    [array release];
//    while (_uploadIndex < [selectedVehicles count]) {
//        NSNumber *checked = [selectedVehicles objectAtIndex:_uploadIndex];
//        KKModelVehicleDetailInfo *info = [self.localVehicleList objectAtIndex:_uploadIndex];
//        
//        _uploadIndex++;
//        if([checked isEqualToNumber:VehicleChecked])
//        {
//            [[KKProtocolEngine sharedPtlEngine] vehicleSaveInfo:info.vehicleId vehicleVin:info.vehicleVin vehicleNo:info.vehicleNo vehicleModel:info.vehicleModel vehicleModelId:info.vehicleModelId vehicleBrand:info.vehicleBrand vehicleBrandId:info.vehicleBrandId obdSN:info.obdSN bindingShopId:info.recommendShopId userNo:info.userNo engineNo:info.engineNo registNo:info.registNo nextMaintainMileage:info.nextMaintainMileage nextInsuranceTime:[KKUtils convertStringToDate:info.nextInsuranceTime] nextExamineTime:[KKUtils convertStringToDate:info.nextExamineTime] currentMileage:info.currentMileage delegate:self];
//            _uploadSuccessCount++;
//            break;
//        }
//    }
//    
//    if(_uploadSuccessCount == 0)
//    {
//        [KKAppDelegateSingleton ShowRootView];
//        //[KKCustomAlertView showAlertViewWithMessage:@"请选择车辆！"];
//        return;
//    }
//    if(_uploadIndex == [selectedVehicles count])
//    {
//        [KKCustomAlertView showAlertViewWithMessage:@"车辆保存成功！" block:^{
//            [KKAppDelegateSingleton ShowRootView];
//        }];
//    }
}


-(void) checkButtonClicked:(KKCheckedButton *)btn
{
    if([[selectedVehicles objectAtIndex:btn.index] isEqualToNumber:VehicleChecked])
    {
        [selectedVehicles replaceObjectAtIndex:btn.index withObject:VehicleUnChecked];
        [btn setImage:[UIImage imageNamed:@"icon_unselected.png"] forState:UIControlStateNormal];
    }
    else
    {
        [selectedVehicles replaceObjectAtIndex:btn.index withObject:VehicleChecked];
        [btn setImage:[UIImage imageNamed:@"icon_selected.png"] forState:UIControlStateNormal];
    }
}

#pragma mark KKProtocolEngineDelegate
- (NSNumber *) vehicleSaveInfoResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
        [KKCustomAlertView showAlertViewWithMessage:error.description];
		return KKNumberResultEnd;
	}

    KKModelProtocolRsp *rltRsp = (KKModelProtocolRsp *)aRspObj;
    if(rltRsp.header.code == eRsp_succeed)
    {
        [KKCustomAlertView showAlertViewWithMessage:rltRsp.header.desc block:^{
            [KKAppDelegateSingleton ShowRootView];
        }];
    }
    else
    {
        [KKCustomAlertView showAlertViewWithMessage:rltRsp.header.desc];
    }
        
    
//    KKModelProtocolRsp *rltRsp = (KKModelProtocolRsp *)aRspObj;
//    if(_uploadIndex < [selectedVehicles count])
//    {
//        [self submitButtonClicked];
//    }
    
    return KKNumberResultEnd;
}

#pragma mark UITableViewDelegate
- (UIImage *)getBackgroundImageWithIndexRow:(NSInteger)row
{
    UIImage *image = nil;
    
    if (row == 0)
    {
        if([self.localVehicleList count] == 1)
        {
            image = [UIImage imageNamed:@"bg_setting_bind_cell_full.png"];
        }
        else
        {
            image = [UIImage imageNamed:@"bg_setting_bind_cell_up.png"];
        }
    }
    else if(row == [self.localVehicleList count]-1)
        image = [UIImage imageNamed:@"bg_setting_bind_cell_down.png"];
    else
        image = [UIImage imageNamed:@"bg_setting_bind_cell_middle.png"];
    
    return [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
}

#pragma mark -
#pragma mark UITableViewDelegate
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.localVehicleList count];
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
        [selectBtn addTarget:self action:@selector(checkButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:selectBtn];
        [selectBtn release];
        
    }
    UIImageView *bgimv = (UIImageView *)[cell.contentView viewWithTag:100];
    UILabel *brandLabel = (UILabel *)[cell.contentView viewWithTag:101];
    UILabel *carNumLabel = (UILabel *)[cell.contentView viewWithTag:102];
    UILabel *carModelLabel = (UILabel *)[cell.contentView viewWithTag:103];
    UILabel *deviceNumLabel = (UILabel *)[cell.contentView viewWithTag:104];
    
    KKCheckedButton *selectedbutton = (KKCheckedButton *)[cell.contentView viewWithTag:107];
    
    KKModelVehicleDetailInfo *vehicleInfo = (KKModelVehicleDetailInfo *)[self.localVehicleList objectAtIndex:indexPath.row];
    brandLabel.text = [NSString stringWithFormat:@"品牌 :%@",nilToDefaultString(vehicleInfo.vehicleBrand,@"未知")];
    carNumLabel.text = [NSString stringWithFormat:@"车牌号 : %@",nilToDefaultString(vehicleInfo.vehicleNo,@"未知")];
    carModelLabel.text = [NSString stringWithFormat:@"车型 : %@",nilToDefaultString(vehicleInfo.vehicleModel,@"未知")];
    deviceNumLabel.text = [NSString stringWithFormat:@"设备号 : %@",nilToDefaultString(vehicleInfo.obdSN,@"未知")];
    
    selectedbutton.index = indexPath.row;
    
    if([[selectedVehicles objectAtIndex:selectedbutton.index] isEqualToNumber:VehicleChecked])
    {
        [selectedbutton setImage:[UIImage imageNamed:@"icon_selected.png"] forState:UIControlStateNormal];
    }
    else
    {
        [selectedbutton setImage:[UIImage imageNamed:@"icon_unselected.png"] forState:UIControlStateNormal];
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

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    KKCheckedButton *btn = (KKCheckedButton *)[cell viewWithTag:107];
    [self checkButtonClicked:btn];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) dealloc
{
    [selectedVehicles release],selectedVehicles=nil;
    self.localVehicleList = nil;
    [super dealloc];
}
@end
