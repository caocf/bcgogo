//
//  TGOilStationViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGOilStationViewController.h"
#import "TGOilStationTableViewCell.h"

@interface TGOilStationViewController ()

@end

@implementation TGOilStationViewController

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
    CGFloat height = [self getViewHeightWithNavigationBar];
    CGFloat originY = [self getViewLayoutStartOriginYWithNavigationBar];
    
    [self setNavigationTitle:@"加油站列表"];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, originY, screenWidth, height) style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    [self.view addSubview:_tableView];
}

-(void) backButtonClicked
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - UITableViewDataSource
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 96;
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.oilStationListRsp.result.TGArrayFieldName(data, TGModelOilStation) count];
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"oilStationCell";
    TGOilStationTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[TGOilStationTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    [cell setDataAndRefresh:[self.oilStationListRsp.result.TGArrayFieldName(data, TGModelOilStation) objectAtIndex:indexPath.row]];
    
    return cell;
}

#pragma mark -
-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_OilStationListClicked object:nil userInfo:[NSDictionary dictionaryWithObjectsAndKeys:((TGModelOilStation *)[self.oilStationListRsp.result.data__TGModelOilStation objectAtIndex:indexPath.row]).id,@"oilStationID", nil]];
    
    [self backButtonClicked];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
