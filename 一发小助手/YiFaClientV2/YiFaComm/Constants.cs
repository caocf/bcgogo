using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace YiFaComm
{
    public class Constants
    {
        public static string registerurl = "https://shop.bcgogo.com/web/login.jsp";
        //public static string registerurl = "https://phone.bcgogo.cn/web/login.jsp";
        //进入一发系统页面
        public static string yifahomepage = "https://shop.bcgogo.com/web/user.client?method=createmain";
        //public static string yifahomepage = "https://phone.bcgogo.cn/web/user.client?method=createmain";

        //忘记密码
      
        public static string forgetpassword = "";
        public static string currentVersion = "1.0.0.16";

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
