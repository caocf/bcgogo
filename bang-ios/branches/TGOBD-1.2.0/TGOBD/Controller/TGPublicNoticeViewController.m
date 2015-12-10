//
//  TGPublicNoticeViewController.m
//  TGOBD
//
//  Created by James Yu on 14-4-8.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGPublicNoticeViewController.h"
#import "TGPublicNoticeTableViewCell.h"
#import "TGHTTPRequestEngine.h"
#import "TGPublicNoticeDetailViewController.h"
#import "TGAppDelegate.h"
#import "TGDataSingleton.h"

#define PUBLIC_NOTICE_IDENTIFY [NSString stringWithFormat:@"publiNoticeCache-%@", [[[TGDataSingleton sharedInstance] userInfo] userNo]]

@interface TGPublicNoticeViewController ()

@property (nonatomic, assign) BOOL isLoading;
@property (nonatomic, strong) TGModelPagerInfo *pager;
@property (nonatomic, strong) NSMutableArray *dataSource;

@end

@implementation TGPublicNoticeViewController

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
    [self initVariable];
    [self httpRequestHandler];
    [self setNavigationTitle:@"4S店公告"];
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
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.scrollEnabled = YES;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    _refreshHeaderView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_tableView.bounds.size.height, _tableView.bounds.size.width, _tableView.bounds.size.height)];
    _refreshHeaderView.delegate = self;
    _refreshHeaderView.refreshTableName = @"publicNotice";
    [_refreshHeaderView refreshLastUpdatedDate];
    
    [_tableView addSubview:_refreshHeaderView];
    
    [self.view addSubview:_tableView];
}

- (void)initVariable
{
    _dataSource = [[NSMutableArray alloc] init];
    
    _pager = [[TGModelPagerInfo alloc] init];
    _pager.currentPage = 1;
    _pager.nextPage = 1;
    _pager.pageSize = 10;
    _pager.isLastPage = false;
    
    [self loadFromCache];
}

- (void)loadFromCache
{
    //从本地读取缓存
    NSData *cacheData = [[NSUserDefaults standardUserDefaults] dataForKey:PUBLIC_NOTICE_IDENTIFY];
    NSMutableArray *cacheArray = [NSKeyedUnarchiver unarchiveObjectWithData:cacheData];
    for (int i = 0 ; i < [cacheArray count]; i ++) {
        [_dataSource insertObject:[cacheArray objectAtIndex:i] atIndex:0];
    }
    
    [_tableView reloadData];
    
    if ([_dataSource count] > 0) {
        [_tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForItem:[_dataSource count] - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:NO];
    }
}

- (void)httpRequestHandler
{
    [TGProgressHUD show];
    
    [[TGHTTPRequestEngine sharedInstance] getAdvertList:_pager.nextPage pageSize:_pager.pageSize viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([self httpResponseCorrect:responseObject]) {
            TGMOdelPublicNoticeListRsp *rsp = (TGMOdelPublicNoticeListRsp *)responseObject;
            _pager = rsp.pager;
            
            if (_pager.currentPage == 1 && [rsp.advertDTOList__TGModelPublicNoticeInfo count] > 0) {
                //最近一次进行缓存保存
                NSData *archiverData = [NSKeyedArchiver archivedDataWithRootObject:rsp.advertDTOList__TGModelPublicNoticeInfo];
                [[NSUserDefaults standardUserDefaults] setObject:archiverData forKey:PUBLIC_NOTICE_IDENTIFY];
                [[NSUserDefaults standardUserDefaults] synchronize];
                
                [_dataSource removeAllObjects];
            }
            
            NSMutableArray *indexPaths = [[NSMutableArray alloc] init];
            
            for (int i = 0; i < [rsp.advertDTOList__TGModelPublicNoticeInfo count]; i++) {
                NSIndexPath *indexPath = [NSIndexPath indexPathForItem:i inSection:0];
                [_dataSource insertObject:[rsp.advertDTOList__TGModelPublicNoticeInfo objectAtIndex:i] atIndex:0];
                [indexPaths addObject:indexPath];
            }

            if ([_dataSource count] > 0) {
                [_tableView reloadData];
                [_tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForItem:[rsp.advertDTOList__TGModelPublicNoticeInfo count] - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:NO];
            }
            
        }
        [self stopLoading];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self httpRequestSystemError:error];
        [self stopLoading];
    }];
}

- (void)stopLoading
{
    _isLoading = NO;
    [_refreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:_tableView];
}

#pragma mark - EGORefreshHeaderView delegate

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView *)view
{
    _isLoading = YES;
    if (_pager.isLastPage) {
        [TGProgressHUD showErrorWithStatus:@"没有更多历史公告了！"];
        [self performSelector:@selector(stopLoading) withObject:nil afterDelay:0.4];
        return;
    }
    [self httpRequestHandler];
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

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_dataSource count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 302;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identify = @"publicNoticeCell";
   
    TGPublicNoticeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:NSStringFromClass([TGPublicNoticeTableViewCell class]) owner:nil options:nil];
        cell = [nib objectAtIndex:0];
    }
    
    [cell setCellContent:[_dataSource objectAtIndex:indexPath.row]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    TGPublicNoticeDetailViewController *vc = [[TGPublicNoticeDetailViewController alloc] init];
    TGModelPublicNoticeInfo *info = (TGModelPublicNoticeInfo *)[_dataSource objectAtIndex:indexPath.row];
    vc.advertId = info.id;
    [TGAppDelegateSingleton.rootViewController pushViewController:vc animated:YES];
}

#pragma mark - UIScrollView delegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [_refreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [_refreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
}

@end
