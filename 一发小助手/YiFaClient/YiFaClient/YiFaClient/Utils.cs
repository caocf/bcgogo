using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using YiFaComm;

namespace YiFaClient
{
    public class Utils
    {
        private static bool closeWindowntifyShowflag = true;
        private static bool closeMainDirect = false;
        private static ClientMain mainInstance = null;
        private static bool ffUpdateNotify = true;
        public static void FFUpdate()
        {
            if (!ffUpdateNotify) return;

            string version = "";
            string ffpath = "";
            bool bFFResult = FFDetect.detect(out version, out ffpath);

            FFCheckResult ffcheckresult = new FFCheckResult();
            DataProcessUtil.ffUpdateCheck(version, out ffcheckresult);         
         
            if (ffcheckresult != null && ffcheckresult.needUpdate)
            {
                //最小化主窗口
                getMainInstance().HideMain();
                //没有安装火狐，提示安装
                UpdateFFNotify notifywindow = new UpdateFFNotify(null, ffcheckresult.updateUrl, bFFResult);
                notifywindow.TopLevel = true;
                notifywindow.ShowDialog(); //显示为模式对话框
            }                  
        }

        public static void showNotify(bool bshow)
        {
            if (bshow)//不再提示
            {
                closeWindowntifyShowflag = false;
            }
            else
            {
                closeWindowntifyShowflag = true;
            }
        }

        public static bool getCloseWindownotifyflag()
        {
            return closeWindowntifyShowflag;
        }

        public static ClientMain getMainInstance()
        {
            if (mainInstance == null)
            {
                mainInstance = new ClientMain();
            }

            return mainInstance;
        }

        public static void setCloseMainDirec(bool bDirectExit)
        {
            closeMainDirect = bDirectExit;
        }

        public static bool DirectExit()
        {
            return closeMainDirect;
        }

        public static void setFFUpdateflag(bool notify)
        {
            ffUpdateNotify = notify;
        }
    }
}
