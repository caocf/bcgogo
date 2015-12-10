//
//  KKReservationServiceViewController.m
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKReservationServiceViewController.h"
#import "UIViewController+extend.h"
#import "KKViewUtils.h"
#import "UIView+Additon.h"
#import "KKApplicationDefine.h"
#import "KKShopQueryViewController.h"

@interface KKReservationServiceViewController ()

@end

@implementation KKReservationServiceViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    [self setVcEdgesForExtendedLayout];
    [self initVariables];
    [self initComponents];
}

#pragma mark -
#pragma mark custom methods

- (void) initVariables
{
    _cNameArr = [[NSMutableArray alloc] initWithObjects:@"机修保养",@"美容装潢",@"钣金喷漆",@"保险验车",nil];
    _eNameArr = [[NSMutableArray alloc] initWithObjects:@"Car's Maintenance", @"Car's Decorating",@"Car Spray Painting",@"Insurance Inspection",nil];
}

- (void) initComponents
{
    [self setBackGroundView];
    [self setNavgationBar];
    [self creatTableView];
}

- (void)creatTableView
{
    if (_mainTableView == nil)
    {
        _mainTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, currentScreenHeight - 44 - 49 - [self getOrignY]) style:UITableViewStylePlain];
        _mainTableView.backgroundColor = [UIColor clearColor];
        _mainTableView.delegate = self;
        _mainTableView.dataSource = self;
        _mainTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [self.view addSubview:_mainTableView];
        [_mainTableView release];
    }
}

- (void)setNavgationBar
{
    self.view.backgroundColor = [UIColor whiteColor];
    [self.navigationController.navigationBar addBgImageView];
    [self initTitleView];
    UILabel *titleView = (UILabel *)self.navigationItem.titleView;
    titleView.text = @"预约服务";
    self.navigationItem.leftBarButtonItem = [KKViewUtils createNavigationBarButtonItem:[UIImage imageNamed:@"icon_back.png"] bgImage:nil target:self action:@selector(backButtonClicked)];
}

- (void)setBackGroundView
{
    UIImageView *bgImv = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 320,  self.view.bounds.size.height)];
    bgImv.image = [[UIImage imageNamed:@"bg_Home.png"] stretchableImageWithLeftCapWidth:0 topCapHeight:0];
    bgImv.userInteractionEnabled = YES;
    bgImv.backgroundColor = [UIColor redColor];
    [self.view addSubview:bgImv];
    [bgImv release];
}

#pragma mark -
#pragma mark Events

- (void)backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark -
#pragma mark UITableViewDataSource, UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_cNameArr count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* reuseID = @"ReservationService";
    UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:reuseID];
    
    if (nil == cell)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseID] autorelease];
        
        KKReservationCellView *reservationView = [[KKReservationCellView alloc] initWithFrame:CGRectMake(0.5*(cell.bounds.size.width - 283), 18, 283, 82)];
        reservationView.tag = 100;
        reservationView.delegate = self;
        [cell.contentView addSubview:reservationView];
        [reservationView release];
    }
    
    KKReservationCellView *contentView = (KKReservationCellView *)[cell.contentView viewWithTag:100];
    contentView.index = indexPath.row;
    [contentView setContentViewWithImage:[UIImage imageNamed:[NSString stringWithFormat:@"icon_rService_%d.png",indexPath.row]] WithChinese:[_cNameArr objectAtIndex:indexPath.row] withEnglish:[_eNameArr objectAtIndex:indexPath.row]];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor clearColor];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    float height = 100;
    if (indexPath.row == [_cNameArr count] - 1)
        height += 20;
    return height;
}

#pragma mark -
#pragma mark KKReservationCellViewDelegate

- (void)KKReservationCellViewItemClicked:(NSInteger)index
{
    KKShopQueryViewController *Vc = [[KKShopQueryViewController alloc] initWithNibName:@"KKShopQueryViewController" bundle:nil];
    switch (index) {
        case 0:
            Vc.serviceTypeKey = @"机修保养";
            break;
        case 1:
            Vc.serviceTypeKey = @"美容装潢";
            break;
        case 2:
            Vc.serviceTypeKey = @"钣金喷漆";
            break;
        case 3:
            Vc.serviceTypeKey = @"保险验车";
            break;
            
        default:
            break;
    }
    [self.navigationController pushViewController:Vc animated:YES];
    [Vc release];
}

#pragma mark -
#pragma mark memory handle

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _mainTableView = nil;
}

- (void)dealloc
{
    _mainTableView = nil;
    
    [_cNameArr release];
    _cNameArr = nil;
    [_eNameArr release];
    _eNameArr = nil;
    
    [super dealloc];
}

@end
