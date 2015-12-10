//
//  KKCustomTabbarContentView.m
//  KaiKai
//
//  Created by mazhiwei on 11-9-27.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "KKCustomTabbarContentView.h"

@implementation KKCustomTabbarItem
@synthesize title;
@synthesize selectedColor;
@synthesize selectedImage;
@synthesize noselectedColor;
@synthesize noselectedImage;
@synthesize selectedBgImage;
@synthesize delegate = _delegate;

- (id)initWithFrame:(CGRect)frame {
    
    self = [super initWithFrame:frame];
    if (self) {
        _itemImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _itemImageView.userInteractionEnabled = YES;
        _itemImageView.center = CGPointMake(0.5*frame.size.width, 18);
        _itemImageView.backgroundColor = [UIColor clearColor];
        [self addSubview:_itemImageView];
        [_itemImageView release];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _titleLabel.userInteractionEnabled = YES;
        _titleLabel.center = CGPointMake(0.5*frame.size.width, 38);
        _titleLabel.font = [UIFont systemFontOfSize:12.0f];
        _titleLabel.backgroundColor = [UIColor clearColor];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.textAlignment = UITextAlignmentCenter;
        [self addSubview:_titleLabel];
        [_titleLabel release];
        
        _badgeValue = 0;
		
		_badgeImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _badgeImageView.userInteractionEnabled = YES;
		_badgeImageView.backgroundColor = [UIColor clearColor];
		[self addSubview:_badgeImageView];
		[_badgeImageView release];
		
		_badgeValueLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		_badgeValueLabel.textColor = [UIColor whiteColor];
		_badgeValueLabel.font = [UIFont boldSystemFontOfSize:12.0f];
		_badgeValueLabel.backgroundColor = [UIColor clearColor];
		_badgeValueLabel.textAlignment = UITextAlignmentCenter;
		[self addSubview:_badgeValueLabel];
		[_badgeValueLabel release];
        
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code.
}
*/

- (void)setContentViews
{
    CGPoint center = _itemImageView.center;
    CGSize size = self.noselectedImage.size;
    [_itemImageView setFrame:CGRectMake(0, 0, size.width, size.height)];
    _itemImageView.image = self.noselectedImage;
    _itemImageView.center = center;
    
    center = _titleLabel.center;
    size = [self.title sizeWithFont:[UIFont systemFontOfSize:12.0]];
    [_titleLabel setFrame:CGRectMake(0, 0, size.width, size.height)];
    _titleLabel.text = self.title;
    _titleLabel.center = center;
}


- (void)setSelected:(BOOL)selected
{
    if (selected)
    {
        if (_itemImageView && self.selectedImage)
            _itemImageView.image = self.selectedImage;
        if (_titleLabel && self.selectedColor)
            _titleLabel.textColor = self.selectedColor;
    }
    else 
    {
        if (_itemImageView && self.noselectedImage)
            _itemImageView.image = self.noselectedImage;
        if (_titleLabel && self.noselectedColor)
            _titleLabel.textColor = self.noselectedColor;
    }
}

- (void)setBadgeValue:(NSInteger)value
{
	if (value == 0) {
		_badgeImageView.frame = CGRectZero;
		_badgeValueLabel.frame = CGRectZero;
	}
	else {
		UIImage *badgeImage = nil;
		CGFloat align = 0;
		if (value < 10) {
			badgeImage  = [UIImage imageNamed:@"badge_1.png"];
			align = 3;
		}
		else if (value < 100) {
			badgeImage  = [UIImage imageNamed:@"badge_2.png"];
			align = 5;
		}
		else  { // (value >= 100) 
			badgeImage  = [UIImage imageNamed:@"badge_3.png"];
			align = 5;
		}
		CGSize imageSize = badgeImage.size;
		
		_badgeImageView.frame = CGRectMake(0.5*self.bounds.size.width+ 10 , _itemImageView.frame.origin.y - 7, imageSize.width, imageSize.height);
		_badgeImageView.image = badgeImage;

		_badgeValueLabel.frame = CGRectMake(_badgeImageView.frame.origin.x+align, 
											_badgeImageView.frame.origin.y+3, 
											_badgeImageView.frame.size.width-2*align, 
											_badgeImageView.frame.size.height-2*3);
		_badgeValueLabel.text = [NSString stringWithFormat:@"%d", value];
		[self bringSubviewToFront:_badgeImageView];
		[self bringSubviewToFront:_badgeValueLabel];
	}
	
	_badgeValue = value;
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
	// UITouch *touch = [[touches allObjects]objectAtIndex:0];
	// CGPoint pt = [touch locationInView:self];
	if (_delegate) {
		[_delegate didSelectedItem:self];
	}
	
}


- (void)dealloc {
    
    _itemImageView = nil;
    _titleLabel = nil;
    _badgeImageView = nil;
    _badgeValueLabel = nil;
    
    self.delegate = nil;
    self.title = nil;
    
    self.selectedColor = nil;
    self.selectedImage = nil;
    self.noselectedColor = nil;
    self.noselectedImage = nil;
    self.selectedBgImage = nil;
	
    [super dealloc];
}
@end

#pragma mark -
#pragma mark KKCustomTabbarContentView
@implementation KKCustomTabbarContentView
- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame])
    {
        items = [[NSMutableArray alloc] initWithCapacity:3];
        selectedItemIndex = -1;
        
        _bgImageView = [[UIImageView alloc] initWithFrame:frame];
        _bgImageView.backgroundColor = [UIColor clearColor];
        _bgImageView.userInteractionEnabled = YES;
        [self addSubview:_bgImageView];
        [_bgImageView release];
        
        _selectedBgImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _selectedBgImageView.backgroundColor = [UIColor clearColor];
        _selectedBgImageView.userInteractionEnabled = YES;
        [self addSubview:_selectedBgImageView];
        [_selectedBgImageView release];
    }
    return self;
}

- (void)addItem:(KKCustomTabbarItem*)item
{
    if (item)
    {
        [items addObject:item];
        [self addSubview:item];
    }
}

- (NSArray*)getItems 
{
	return items;
}

- (void)setItemSelected:(NSInteger)index
{
    if (index == selectedItemIndex)
    {
        return;
    }
    
    if (index >= 0 && index < items.count) 
    {
        CGFloat width = self.frame.size.width/[items count];
        CGPoint center = _selectedBgImageView.center;
        center.x = 0.5*width + index*width;
        [UIView animateWithDuration:0.3 animations:^{
            _selectedBgImageView.center = center;
        }];
        
        [(KKCustomTabbarItem*)[items objectAtIndex:index] setSelected:YES];
    }
    if (selectedItemIndex >= 0 && selectedItemIndex < items.count)
    {
        [(KKCustomTabbarItem*)[items objectAtIndex:selectedItemIndex] setSelected:NO];
    }
    selectedItemIndex = index;
}

- (void)setBadgeValue:(NSInteger)count andIndex:(NSInteger)index
{
    KKCustomTabbarItem *item = (KKCustomTabbarItem *)[items objectAtIndex:index];
    [item setBadgeValue:count];
}

- (void)setbgImageView:(UIImage *)image
{
    _bgImageView.image = image;
}

- (void)setSelectedBgImageViewWithImage:(UIImage *)image andWidth:(float)width
{
    [_selectedBgImageView setFrame:CGRectMake(0, 0, width, 49)];
    _selectedBgImageView.image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
}

- (void)dealloc
{
    [items release];
    items = nil;
    
    _bgImageView = nil;
    _selectedBgImageView = nil;
    
    [super dealloc];
}
@end

