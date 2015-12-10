package com.tonggou.gsm.andclient.ui;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

import android.os.Bundle;
import android.widget.TextView;

/**
 * 4S 店铺公告详情
 * @author lwz
 *
 */
public class ShopNoticeDetailActivity extends BackableTitleBarActivity {

    TextView mNoticeTitle;
    TextView mNoticeContent;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_4s_notice_detail);
        
        mNoticeTitle = (TextView) findViewById(R.id.notice_title);
        mNoticeContent = (TextView) findViewById(R.id.notice_content);
        
        mNoticeTitle.setText("Micro：第一款真正面向大众的3D打印机");
        mNoticeContent.setText("3D 打印技术公司 M3D，今天在众筹平台KickStarter上发布了其最新款 3D 打印机 Micro。值得注意的是，这款打印机一经发布就宣告筹资完成，速度可谓惊人。在短短的几个小时内，筹资额已达到 77 万美元，远远超过其筹资目标 5 万美元。\nM3D 公司认为，之所以 3D 打印机无法彻底被大众市场接受，是因为其笨重的外观及不便理解的应用界面。而 M3D 则推出了交互更有趣，用户界面更友好的配套应用，以解决这个问题。在使用 Micro 时，用户还能从网上自己搜索模型打印，选定模型后拖拽入打印机中，按下打印键就能开始打印。软件支持 Windows、Mac，以及所有基于 Linux 的操作系统。");
    }
    
    @Override
    protected void onTitleBarCreated(SimpleTitleBar titleBar) {
        super.onTitleBarCreated(titleBar);
        titleBar.setTitle(R.string.title_4s_shop_detail);
    }
}
