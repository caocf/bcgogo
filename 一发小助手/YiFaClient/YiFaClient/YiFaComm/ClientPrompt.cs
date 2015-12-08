using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace YiFaComm
{
    /*
     * 右下角弹出框消息结构
     */
    public class ClientPrompt
    {
        public string recommendScene;//推荐类型，反馈用户行为时使用
        public string recommendId;//推荐记录ID，反馈用户行为时使用
        public string title;//标题
        public string content;//内容
        public string url; //关联到shop系统连接地址
        public string feedbackUrl;                 //消息点击反馈连接地址
        public string nextRequestTime;               //下次请求时间，使用unix长整型时间格式
        public List<string> feedbackUrls;          //消息反馈连接地址，包含三个地址，依次为：用户点击后反馈连接；用户关闭/取消后反馈连接；用户未操作自动消失反馈连接
        public bool processflag = false;
        public ClientPrompt()
        {
            recommendScene = "";
            recommendId = "";
            title = "";
            content = "";
            url = "";
            feedbackUrl = "";
            nextRequestTime = "";
            feedbackUrls = new List<string>();
        }
    }
}
