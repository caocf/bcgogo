using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Win32;

namespace YiFaComm
{
    public class FFDetect
    {
        [System.Runtime.InteropServices.DllImport("kernel32")]

        private static extern int GetPrivateProfileString(string section, string key, string def, System.Text.StringBuilder retVal, int size, string filePath);
        public static bool detect(out string Version, out string ffpath)
        {
            RegistryKey rk = Registry.LocalMachine;

            Version = "";
            ffpath = "";

            if (rk == null) return false;
            //SOFTWARE/Microsoft/Windows/CurrentVersion/App Paths/firefox.exe
            bool bResult = false;
            string path = "";
            try
            {
                RegistryKey appPaths = rk.OpenSubKey("SOFTWARE").OpenSubKey("Microsoft").OpenSubKey("Windows").OpenSubKey("CurrentVersion").OpenSubKey("App Paths");

                foreach (string keyname in appPaths.GetSubKeyNames())
                {
                    if (keyname.ToLower().Equals("firefox.exe"))
                    {
                        bResult = true;
                        RegistryKey ffkey = appPaths.OpenSubKey(keyname);
                        object obj = ffkey.GetValue("Path");
                        path = obj.ToString();
                        break;
                    }
                }

                ffpath = path + "\\firefox.exe";
                path += "//application.ini";
                System.Text.StringBuilder temp = new System.Text.StringBuilder(255);
                GetPrivateProfileString("App", "Version", "", temp, 255, path);

                Version = temp.ToString();
            }
            catch (Exception ex)
            {
            }

            return bResult;
        }
    }
}
