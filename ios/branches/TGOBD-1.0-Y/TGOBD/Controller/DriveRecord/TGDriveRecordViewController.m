//
//  TGDriveRecordViewController.m
//  TGOBD
//
//  Created by Jiahai on 14-3-5.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGDriveRecordViewController.h"

@interface TGDriveRecordViewController ()

@end

@implementation TGDriveRecordViewController

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
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setFrame:CGRectMake(50, 200, 80, 40)];
    [btn setTitle:@"行车日志" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(xxxx) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
}

- (void)xxxx
{
    [[TGHTTPRequestEngine sharedInstance] shopGetServiceCategoty:@"NULL" viewControllerIdentifier:self.viewControllerIdentifier success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if([self httpResponseCorrect:responseObject])
        {
            
        }
    } failure:self.faultBlock];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
