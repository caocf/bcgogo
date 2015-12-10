//
//  TGTrafficViolationViewController.m
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGTrafficViolationViewController.h"
#import "TGHTTPRequestEngine.h"
#import "TGTrafficViolationTableViewCell.h"
#import "TGSetVilolationQueryConditionViewController.h"
#import "TGAppDelegate.h"

@interface TGTrafficViolationViewController ()

@property (nonatomic, strong) NSMutableArray *dataSource;
@property (nonatomic, assign) BOOL isLoading;
@property (nonatomic, strong) UIImageView *noDateImgView;
@end

@implementation TGTrafficViolationViewController

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
    
    [self initComponents];
    
    self.navigationItem.rightBarButtonItem = [TGViewUtils createNavigationBarButtonItemWithTitle:@"设置" bgImage:nil target:self action:@selector(setQueryAdition)];
    
    _dataSource = [[NSMutableArray alloc] init];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Custom method
- (void)initComponents
{
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    CGFloat height = [self getViewHeightWithNavigationBar];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height) style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundView = nil;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.scrollEnabled = YES;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    _refreshHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_tableView.bounds.size.height, _tableView.bounds.size.width, _tableView.bounds.size.height)];
    _refreshHeaderView.delegate = self;
    _refreshHeaderView.refreshTableName = @"trafficeViolation";
    [_refreshHeaderView refreshLastUpdatedDate];
    
    [_tableView addSubview:_refreshHeaderView];
    
    [self.view addSubview:_tableView];
    //首次进入 下拉刷新
    [_tableView setContentOffset:CGPointMake(0, -80) animated:YES];
    [_refreshHeaderView egoRefreshScrollViewDidEndDragging:_tableView];
    
}

- (void)getDataSource
{
    _isLoading = YES;
    
    __weak TGTrafficViolationViewController *weakSelf = self;
    
    TGModelVehicleInfo *vehicle = [[TGDataSingleton sharedInstance] vehicleInfo];
    [[TGHTTPRequestEngine sharedInstance] violateJuheQuery:vehicle.juheCityCode hphm:vehicle.vehicleNo hpzl:@"02" engineno:vehicle.engineNo classno:vehicle.vehicleVin registerno:@"NULL" viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([weakSelf httpResponseCorrect:responseObject]) {
            TGModelViolationInfoListRsp *rsp = (TGModelViolationInfoListRsp *)responseObject;
            [weakSelf.dataSource removeAllObjects];
            [weakSelf.dataSource addObjectsFromArray:rsp.queryResponse.result.lists__TGModelViolationInfo];
            [weakSelf.tableView reloadData];
        }
        [weakSelf stopLoading];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [weakSelf stopLoading];
        [weakSelf httpRequestSystemError:error];
    }];
}

- (void)stopLoading
{
    _isLoading = NO;
    [_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_tableView];
}

- (void)setQueryAdition
{
    [TGAppDelegateSingleton.rootViewController pushViewController:[[TGSetVilolationQueryConditionViewController alloc] init] animated:YES];
}

#pragma mark - EGORefreshHeaderView delegate

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView *)view
{
    [self getDataSource];
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView *)view
{
    return _isLoading;
}

- (NSDate *)egoRefreshTableHeaderDataSourceLastUpdated:(EGORefreshTableHeaderView *)view
{
    return [NSDate date];
}

#pragma mark - UITableView delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if ([_dataSource count] == 0) {
        return nil;
    }
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 40)];
    headerView.backgroundColor = [UIColor yellowColor];
    
    UIImageView *car = [[UIImageView alloc] initWithFrame:CGRectMake(30, 7, 34, 26)];
    car.image = [UIImage imageNamed:@"icon_car.png"];
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(90, 7, 90, 26)];
    label.backgroundColor = [UIColor clearColor];
    label.text = [[[TGDataSingleton sharedInstance] vehicleInfo] vehicleNo];
    
    [headerView addSubview:car];
    [headerView addSubview:label];
    
    return headerView;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    if ([_dataSource count] == 0) {
        return nil;
    }
    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 30)];
    footView.backgroundColor = TGRGBA(204, 204, 204, 1);
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 320, 30)];
    label.text = [NSString stringWithFormat:@"共有%d条违章记录", [_dataSource count]];
    label.textColor = [UIColor redColor];
    label.textAlignment = NSTextAlignmentCenter;
    
    [footView addSubview:label];
    
    return footView;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return [_dataSource count] == 0 ? 0 : 40;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return [_dataSource count] == 0 ? 0 : 30;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_dataSource count] == 0) {
        if (_noDateImgView == nil) {
            UIImage *img = [UIImage imageNamed:@"no_violation.png"];
            _noDateImgView = [[UIImageView alloc] initWithImage:img];
            _noDateImgView.frame = CGRectMake((320-img.size.width)*0.5, 60, img.size.width, img.size.height);
            [_tableView addSubview:_noDateImgView];
        }
        _noDateImgView.hidden = NO;
    }
    else
    {
        _noDateImgView.hidden = YES;
    }
    
    return [_dataSource count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [TGTrafficViolationTableViewCell getCellHeight:[_dataSource objectAtIndex:indexPath.row]];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identify = @"trafficViolation";
    TGTrafficViolationTableViewCell *cell = (TGTrafficViolationTableViewCell *)[tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:NSStringFromClass([TGTrafficViolationTableViewCell class]) owner:nil options:nil];
        cell = [nib objectAtIndex:0];
    }
    
    [cell setCellContent:[_dataSource objectAtIndex:indexPath.row]];
    
    return cell;
}


#pragma mark - uiscrollview delegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
}

- (void)dealloc
{
    [[TGHTTPRequestEngine sharedInstance] cancleRequestWithViewControllerIdentifier:self.viewControllerIdentifier];
}

@end
