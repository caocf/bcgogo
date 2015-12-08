using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace YiFaComm
{
    /**
     * 主窗口消息项结构
     * 
     * */
    public class ClientAssortedMessageItem //消息项
    {
        public int clicktime;
        public string content;                     //消息内容
        public string url;                         //消息连接地址
        public List<string> feedbackUrls;          //消息反馈连接地址，包含三个地址，依次为：用户点击后反馈连接；用户关闭/取消后反馈连接；用户未操作自动消失反馈连接
        public ClientAssortedMessageItem()
        {
            clicktime = 0;
            content = "";
            url = "";
            feedbackUrls = new List<string>();
        }

        public void click()
        {
            clicktime++;
        }

        public int clickTimes()
        {
            return clicktime;
        }
    }
    /*
     * 主窗口消息结构，包含最多3个子消息项
     * 
     */
    public class ClientAssortedMessage
    {      
        public string recommendScene;              //业务信息场景分类
        public string title;                       //标题
        public string relatedTitle;                //关联到shop系统连接标题
        public string relatedUrl;                  //关联到shop系统连接地址
        public List<ClientAssortedMessageItem> items;    //消息项列表
        public string nextRequestTime;               //下次请求时间，使用unix长整型时间格式
        public string feekbackUrl;                 //用户点击反馈连接
        public string msgNumber; //消息总数
        public ClientAssortedMessage()
        {
            items = new List<ClientAssortedMessageItem>();
        }
    }
}
