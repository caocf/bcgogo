//
//  TGUserInfoViewController.m
//  TGOBD
//
//  Created by James Yu on 14-3-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGUserInfoViewController.h"
#import "TGChangePasswordViewController.h"
#import "TGHTTPRequestEngine.h"

@interface TGUserInfoViewController ()

@property (nonatomic, strong) NSMutableArray *dateSource;

@end

@implementation TGUserInfoViewController

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
	
    [self setNavigationTitle:@"个人资料"];
    [self initComponents];
    [self initValiables];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom Methods

- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    _tableview = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, 130) style:UITableViewStylePlain];
    _tableview.backgroundColor = [UIColor clearColor];
    _tableview.scrollEnabled = NO;
    _tableview.dataSource = self;
    _tableview.delegate = self;
    _tableview.backgroundView = nil;
    
    originY += 170;
    
    UIButton *logout = [[UIButton alloc] initWithFrame:CGRectMake(20, originY, 280, 45)];
    [logout setTitle:@"退出登录" forState:UIControlStateNormal];
    logout.backgroundColor = [UIColor redColor];
    [logout addTarget:self action:@selector(logout) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:_tableview];
    [self.view addSubview:logout];
}

- (void)logout
{
    //TODO
}

- (void)initValiables
{
    _dateSource = [[NSMutableArray alloc] init];
    [_dateSource addObject:@"15262760323"];
    [_dateSource addObject:@"15262760323"];
    [_dateSource addObject:@""];
    
    [_tableview reloadData];
}


#pragma mark - UITableView delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dateSource count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identify = @"cellIdentify";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identify];
    }
    
    if (indexPath.row == 0) {
        cell.textLabel.text = @"用户名";
        cell.detailTextLabel.text = [_dateSource objectAtIndex:indexPath.row];
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    if (indexPath.row == 1) {
        cell.textLabel.text = @"手机号";
        cell.detailTextLabel.text = [_dateSource objectAtIndex:indexPath.row];
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    if (indexPath.row == 2) {
        cell.textLabel.text = @"修改密码";
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    TGChangePasswordViewController *vc = [[TGChangePasswordViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
