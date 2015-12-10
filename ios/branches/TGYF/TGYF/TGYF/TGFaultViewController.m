//
//  TGFaultViewController.m
//  TGYF
//
//  Created by James Yu on 14-5-19.
//  Copyright (c) 2014年 james. All rights reserved.
//

#import "TGFaultViewController.h"
#import "EGORefreshTableHeaderView.h"
#import "TGFaultTableViewCell.h"

@interface TGFaultViewController () <UITableViewDataSource, UITableViewDelegate, EGORefreshTableHeaderDelegate>

@property (nonatomic, strong) NSMutableArray *dataSource;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, assign) BOOL isLoading;
@property (nonatomic, strong) EGORefreshTableHeaderView *refreshView;

@end

@implementation TGFaultViewController

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
    [self hideNavigationBar];
    [self initComponents];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Custom Method

- (void)initComponents
{
    CGRect rect = self.parentViewController.view.bounds;
    _tableView = [[UITableView alloc] initWithFrame:rect];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundView = nil;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.scrollEnabled = YES;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    _refreshView = [[EGORefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, -_tableView.bounds.size.height, _tableView.bounds.size.width, _tableView.bounds.size.height)];
    _refreshView.delegate = self;
    _refreshView.refreshTableName = @"TGFaultViewController";
    [_refreshView refreshLastUpdatedDate];
    
    [_tableView addSubview:_refreshView];
    
    [self.view addSubview:_tableView];
    
    _dataSource = [[NSMutableArray alloc] init];
}

- (void)stopLoading
{
    _isLoading = NO;
    [_refreshView egoRefreshScrollViewDataSourceDidFinishedLoading:_tableView];
}

#pragma mark - UITableView Delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 142;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identify = @"faultCell";
    
    TGFaultTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"TGFaultTableViewCell" owner:nil options:nil];
        cell = [nib objectAtIndex:0];
    }
    
    return cell;
}

#pragma mark - EGORefreshTableHeaderDelegate

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView *)view
{
    _isLoading = YES;
//    if (_pager.isLastPage) {
//        [TGProgressHUD showErrorWithStatus:@"没有更多历史公告了！"];
//        [self performSelector:@selector(stopLoading) withObject:nil afterDelay:0.4];
//        return;
//    }
//    [self httpRequestHandler];
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView *)view
{
    return _isLoading;
}

- (NSDate *)egoRefreshTableHeaderDataSourceLastUpdated:(EGORefreshTableHeaderView *)view
{
    return [NSDate date];
}

#pragma mark - UIScrollView delegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [_refreshView egoRefreshScrollViewDidScroll:scrollView];
    
    CGPoint offset = scrollView.contentOffset;
    CGSize size = scrollView.frame.size;
    CGSize contentSize = scrollView.contentSize;
    
    float yMargin = offset.y + size.height - contentSize.height;
    
    if (yMargin > -1 && !_isLoading /*&& !_pager.isLastPage*/) {
        //上拉加载更多
        _isLoading = YES;
        
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [_refreshView egoRefreshScrollViewDidEndDragging:scrollView];
}
@end
