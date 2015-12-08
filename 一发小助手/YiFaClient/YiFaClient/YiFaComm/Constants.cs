using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace YiFaComm
{
    public class Constants
    {
        //注册页面
        public static string registerurl = "http://reg.bcgogo.com";
        //进入一发系统页面
        private static string yifahomepage = "https://mail.bcgogo.com/web/user.client?method=createmain";
        //public static string yifahomepage = "https://shop.bcgogo.com/web/user.client?method=createmain";

        //忘记密码
      
        public static string forgetpassword = "";
        public static string currentVersion = "1.0.0.8";

        public static string getYiFaHomepage(string userno)
        {
            if (DataProcessUtil.isLogin())
                return yifahomepage + "&clientUserNo=" + userno;
            else
            {
                return yifahomepage;
            }
        }
    }
}
