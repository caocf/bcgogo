using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Win32;

namespace YiFaComm
{
    public class URLNavigator
    {
        //使用火狐打开链接
        public static bool ffOpenURL(string url)
        {
            string version;
            string ffpath;
            if (FFDetect.detect(out version, out ffpath))
            {
                string command = String.Format("\"{0}\" \"{1}\"", ffpath,url);

                try
                {
                    System.Diagnostics.Process.Start(ffpath, String.Format(" -osint -url {0}", url));
                }
                catch (Exception ex)
                {
                    string msg = ex.Message;
                }

                return true;
            }
            else
            {
                return false;
            }
        }

        public static void defaultOpenURL(string url)
        {
            try
            {
                //如果没有FF，则使用缺省浏览器
                RegistryKey key = Registry.ClassesRoot.OpenSubKey(@"http\shell\open\command\");
                string s = key.GetValue("").ToString();
                if (s.Length > 5)
                {
                    int index = s.ToLower().IndexOf(".exe");
                    if (index > 0)
                    {
                        string process = s.Substring(0, index + 4);
                        System.Diagnostics.Process.Start(process, url);
                    }
                    else
                    {
                        System.Diagnostics.Process.Start("iexplore.exe", url);
                    }
                }
                else //没有发现缺省浏览器，使用IE
                {
                    System.Diagnostics.Process.Start("iexplore.exe", url);
                }
            }
            catch (Exception ex)
            {
                System.Diagnostics.Process.Start("iexplore.exe", url);
            }
        }
    }
}
