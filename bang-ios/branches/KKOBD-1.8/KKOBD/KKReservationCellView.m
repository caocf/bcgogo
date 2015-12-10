//
//  KKReservationCellView.m
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKReservationCellView.h"
#import "KKApplicationDefine.h"

@implementation KKReservationCellView
@synthesize delegate;
@synthesize index;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self initial];
    }
    self.userInteractionEnabled = YES;
    self.backgroundColor = [UIColor clearColor];
    return self;
}

- (void)initial
{

    UIImage *image = [UIImage imageNamed:@"bg_rService_up.png"];
    CGSize size = image.size;
    
    _bgImv  = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, size.width,size.height)];;
    _bgImv.backgroundColor = [UIColor clearColor];
    _bgImv.userInteractionEnabled = YES;
    _bgImv.image = image;

    image = [UIImage imageNamed:@"icon_rService_0.png"];
    
    _categoryImv = [[UIImageView alloc] initWithFrame:CGRectMake(0.5*(size.height - image.size.height), 0.5*(size.height - image.size.height), image.size.width, image.size.height)];
    _categoryImv.contentMode = UIViewContentModeScaleAspectFill;
    _categoryImv.backgroundColor = [UIColor clearColor];
    _categoryImv.userInteractionEnabled = YES;
    _categoryImv.clipsToBounds = YES;
    [_bgImv addSubview:_categoryImv];
    [_categoryImv release];
    
    _chineseNameLb = [[UILabel alloc] initWithFrame:CGRectMake(109, 28, 125, 20)];
    _chineseNameLb.backgroundColor = [UIColor clearColor];
    _chineseNameLb.textColor = KKCOLOR_717171;
    _chineseNameLb.font = [UIFont boldSystemFontOfSize:20.0f];
    _chineseNameLb.userInteractionEnabled = YES;
    _chineseNameLb.textAlignment = UITextAlignmentLeft;
    [_bgImv addSubview:_chineseNameLb];
    [_chineseNameLb release];
    
    
    _englishNameLb = [[UILabel alloc] initWithFrame:CGRectMake(109, 50, 125, 11)];
    _englishNameLb.backgroundColor = [UIColor clearColor];
    _englishNameLb.textColor = KKCOLOR_717171;
    _englishNameLb.font = [UIFont systemFontOfSize:11.0f];
    _englishNameLb.userInteractionEnabled = YES;
    _englishNameLb.textAlignment = UITextAlignmentLeft;
    [_bgImv addSubview:_englishNameLb];
    [_englishNameLb release];
    
    image = [UIImage imageNamed:@"icon_rService__arrow_gray.png"];
    
    _arrowImv = [[UIImageView alloc] initWithFrame:CGRectMake(265, 0.5*(size.height - image.size.height), image.size.width, image.size.height)];
    _arrowImv.userInteractionEnabled = YES;
    _arrowImv.image = image;
    [_bgImv addSubview:_arrowImv];
    [_arrowImv release];
    
    [self addSubview:_bgImv];
    [_bgImv release];
    
}

- (void)setContentViewWithImage:(UIImage *)image WithChinese:(NSString *)cName withEnglish:(NSString *)eName
{
    _categoryImv.image = image;
    _chineseNameLb.text = cName;
    _englishNameLb.text = eName;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [UIView animateWithDuration:0.3 animations:^{
        _chineseNameLb.textColor = [UIColor whiteColor];
        _englishNameLb.textColor = [UIColor whiteColor];
        _bgImv.image = [UIImage imageNamed:@"bg_rService_down.png"];
        _arrowImv.image = [UIImage imageNamed:@"icon_rService__arrow_white.png"];
    }];

}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [UIView animateWithDuration:0.3 animations:^{
        _chineseNameLb.textColor = KKCOLOR_717171;
        _englishNameLb.textColor = KKCOLOR_717171;
        _bgImv.image = [UIImage imageNamed:@"bg_rService_up.png"];
        _arrowImv.image = [UIImage imageNamed:@"icon_rService__arrow_gray.png"];
    } completion:^(BOOL finished) {
        if (self.delegate && [(NSObject *)self.delegate respondsToSelector:@selector(KKReservationCellViewItemClicked:)])
            [self.delegate KKReservationCellViewItemClicked:self.index];
    }];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [UIView animateWithDuration:0.3 animations:^{
        _chineseNameLb.textColor = KKCOLOR_717171;
        _englishNameLb.textColor = KKCOLOR_717171;
        _bgImv.image = [UIImage imageNamed:@"bg_rService_up.png"];
        _arrowImv.image = [UIImage imageNamed:@"icon_rService__arrow_gray.png"];
    }];
}

- (void)dealloc
{
    _bgImv = nil;
    _categoryImv = nil;
    _chineseNameLb = nil;
    _englishNameLb = nil;
    _arrowImv = nil;
    
    [super dealloc];
}
@end
