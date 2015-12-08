using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Win32;

namespace YiFaComm
{
    public class AutoStartUtil
    {
        /**
         * 根据用户选择，写注册表，设置系统是否开机自启动
         * 返回：设置成功则返回true,设置失败则返回false
         **/
        public static bool SetAutoStart(bool bAuto, string apppath)
        {
            try
            {
                RegistryKey rk = Registry.CurrentUser;
                RegistryKey lm = Registry.LocalMachine;
                //SOFTWARE/Microsoft/Windows/CurrentVersion/App Paths/firefox.exe

                RegistryKey runKey = rk.CreateSubKey("SOFTWARE").CreateSubKey("Microsoft").CreateSubKey("Windows").CreateSubKey("CurrentVersion").CreateSubKey("Run");
                
                if (bAuto)
                {
                    object yifaclientpath = runKey.GetValue("YIFACLIENT");
                    if (yifaclientpath != null)
                    {
                        if (yifaclientpath.ToString().ToLower().Equals(apppath.ToLower()))
                        {
                            return true;
                        }
                        else
                        {
                            runKey.SetValue("YIFACLIENT", apppath);
                        }
                    }
                    else
                    {
                        runKey.SetValue("YIFACLIENT", apppath);
                    }

                     
                }
                else
                {
                    runKey.DeleteValue("YIFACLIENT");
                }
                runKey.Close();

                try
                {
                    RegistryKey runKeylm = lm.CreateSubKey("SOFTWARE").CreateSubKey("Microsoft").CreateSubKey("Windows").CreateSubKey("CurrentVersion").CreateSubKey("Run");
                    runKeylm.DeleteValue("YIFACLIENT");
                }
                catch (Exception ex)
                {
                    string s = ex.Message;
                }

                return true;
            }
            catch (Exception ex)
            {
                string s = ex.Message;
            }

            return false;
        }
    }
}
