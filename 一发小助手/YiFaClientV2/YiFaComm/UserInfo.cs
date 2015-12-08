using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace YiFaComm
{
    public class UserInfo
    {
        public UserInfo() { }
        public UserInfo(string userNo, string userPass, string savePass, string autoLogin)
        {
            this.userNo = userNo;
            this.userPass = userPass;
            this.savePass = savePass;
            this.autoLogin = autoLogin;
        }
        public string userNo{get;set;}
        public string userPass { get; set; }
        public string savePass { get; set; }
        public string autoLogin { get; set; }
    }
}
