using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace YiFaComm
{
    public class FileUtils
    {
        
        private static string getFilename()
        {
            String filename = System.IO.Path.GetTempPath();
            filename += "\\xwwEx3rEt7501e";
            return filename;
        }

        private static string getFilename1()
        {
            String filename = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
            filename += "\\YiFaClient";
            if (!Directory.Exists(filename))
            {
                Directory.CreateDirectory(filename);
            }
            filename += "\\xwwEx3rEt7501e";
            return filename;
        }

        public static List<UserInfo> getUsers(out bool autoStart, out string latestUserId, out int closeoption , out bool nevernotifyclose, out string preVersion)
        {
            autoStart = true;
            latestUserId = "";
            closeoption = 1;
            nevernotifyclose = false;
            preVersion = "";
            //String filename = exePath.Substring(0, exePath.LastIndexOf('\\') + 1);
            String filename = getFilename();
            string filename1 = getFilename1();

            bool newfilenameExit = true;
            FileStream fs = null;
            try
            {
                fs = new FileStream(filename1, FileMode.OpenOrCreate, FileAccess.Read);
            }
            catch (Exception ex)
            {
                newfilenameExit = false;
                File.Create(filename1);
                if (fs != null)
                {
                    fs.Close();
                }
               
                return null;
            }


            if (!newfilenameExit)
            {
                try
                {
                    fs = new FileStream(filename, FileMode.OpenOrCreate, FileAccess.Read);
                }
                catch (Exception ex)
                {
                 
                    File.Create(filename);
                    if (fs != null)
                    {
                        fs.Close();
                    }

                    return null;
                }
            }


            StreamReader sr = new StreamReader(fs, Encoding.UTF8);
            List<UserInfo> results = new List<UserInfo>();
            String curLine;
            bool firstline = true;
            while ((curLine = sr.ReadLine()) != null)
            {
                if (firstline)
                {
                    string[] firstKeys = curLine.Split(',');
                    if (firstKeys.Length == 2)
                    {
                        autoStart = firstKeys[0].Equals("true");
                        latestUserId = firstKeys[1];
                    }
                    else if (firstKeys.Length == 4)
                    {
                        autoStart = firstKeys[0].Equals("true");
                        latestUserId = firstKeys[1];
                        closeoption = firstKeys[2] == "2" ? 2 : 1;
                        nevernotifyclose = firstKeys[3] == "1" ? true : false;
                    }
                    else if (firstKeys.Length == 5)
                    {
                        autoStart = firstKeys[0].Equals("true");
                        latestUserId = firstKeys[1];
                        closeoption = firstKeys[2] == "2" ? 2 : 1;
                        nevernotifyclose = firstKeys[3] == "1" ? true : false;
                        preVersion = firstKeys[4];
                    }
                    firstline = false;
                    continue;
                }

                if (curLine.Length < 150) continue;
            
                string key1 = curLine.Substring(0, 50);
                key1 = unformatKey(key1);
                string key2 = curLine.Substring(50, 50);
                key2 = unformatKey(key2);
                key2 = new EncryPt(key1).Decrypto(key2);
                string key3 = curLine.Substring(100, 50);
                key3 = unformatKey(key3);
                string key4 = curLine.Substring(150);
                key4 = unformatKey(key4);

                bool bExist = false;
                foreach (UserInfo ui in results)
                {
                    if (ui.userNo == key1)
                    {
                        bExist = true;
                        break;
                    }
                }

                if (!bExist && key1 != "用户名")
                {
                    UserInfo u = new UserInfo(key1, key2, key3, key4);
                    results.Add(u);
                }
                
            }
            sr.Close();

            fs.Close();

            return results;
        }
       
        public static void saveUsers(bool autoStartsetting, int incloseoption, bool innvernotifyclose,  string userNo, string password, string rememberPass, string autologin)
        {
            bool bAutostart;
            string lastUserId;
             int closeoption = 1;
            bool nevernotifyclose = false;
            string preversion;
            List<UserInfo> users = getUsers(out bAutostart, out lastUserId, out closeoption , out nevernotifyclose, out preversion);
            bool found = false;

            if (users != null)
            {
                foreach (UserInfo u in users)
                {
                    if (u.userNo.Equals(userNo) && u.userPass.Equals(password) && u.savePass.Equals(rememberPass) && u.autoLogin.Equals(autologin))
                    {
                        found = true;
                        break;
                    }
                    else if (u.userNo.Equals(userNo))
                    {
                        u.userPass = password;
                        u.savePass = rememberPass;
                        u.autoLogin = autologin;

                        found = true;
                        break;
                    }
                }
            }
            else
            {
                users = new List<UserInfo>();
            }

            if (!found)
            {
                UserInfo u = new UserInfo();
                u.userNo = userNo;
                u.userPass = rememberPass.Equals("true") ? password : "xxx";
                u.savePass = rememberPass;
                u.autoLogin = autologin;
                users.Add(u);
            }                  

            //String filename = exePath.Substring(0, exePath.LastIndexOf('\\') + 1);
            String filename = getFilename1();

            FileStream fs = new FileStream(filename, FileMode.Create, FileAccess.ReadWrite);
            String strAutostart = autoStartsetting ? "true" : "false" ;
            strAutostart += ",";
            strAutostart += userNo;
            strAutostart += ",";
            strAutostart += incloseoption == 2 ? "2" : "1";
            strAutostart += ",";
            strAutostart += innvernotifyclose ? "1": "0";
            strAutostart += ",";
            strAutostart += Constants.currentVersion; 
            strAutostart += "\r\n";
            byte[] bytestowrite = Encoding.UTF8.GetBytes(strAutostart);
            fs.Write(bytestowrite, 0, bytestowrite.Length);
            foreach (UserInfo u in users)
            {
                string encryuserpass = new EncryPt(u.userNo).Encrypto(u.userPass);
                String usernameandpassword = formatKey(u.userNo) + formatKey(encryuserpass) + formatKey(u.savePass) + formatKey(u.autoLogin) + "\r\n";
                bytestowrite = Encoding.UTF8.GetBytes(usernameandpassword);
                fs.Write(bytestowrite, 0, bytestowrite.Length);
            }
            fs.Close();
        }

        public static void removeUser(string userno)
        {
            bool bAutostart;
            string lastUserId = "";
            int closeoption = 1;
            bool nevernotifyclose = false;
            string preversion;
            List<UserInfo> users = getUsers(out bAutostart, out lastUserId, out closeoption,out nevernotifyclose,out preversion);
            foreach (UserInfo u in users)
            {
                if (u.userNo.Trim().Equals( userno.Trim()))
                {
                    users.Remove(u);
                    break;
                }
            }

            //String filename = exePath.Substring(0, exePath.LastIndexOf('\\') + 1);
            String filename = getFilename1();

            FileStream fs = new FileStream(filename, FileMode.Create, FileAccess.ReadWrite);
            String strAutostart = bAutostart ? "true" : "false";
            strAutostart += ",";
            strAutostart += lastUserId;
            strAutostart += ",";
            strAutostart += closeoption == 2 ? "2" : "1";
            strAutostart += ",";
            strAutostart += nevernotifyclose ? "1" : "0";
            strAutostart += ",";
            strAutostart += Constants.currentVersion; 
            strAutostart += "\r\n";
            byte[] bytestowrite = Encoding.UTF8.GetBytes(strAutostart);
            fs.Write(bytestowrite, 0, bytestowrite.Length);
            foreach (UserInfo u in users)
            {
                string encryuserpass = new EncryPt(u.userNo).Encrypto(u.userPass);
                String usernameandpassword = formatKey(u.userNo) + formatKey(encryuserpass) + formatKey(u.savePass) + formatKey(u.autoLogin) + "\r\n";
                bytestowrite = Encoding.UTF8.GetBytes(usernameandpassword);
                fs.Write(bytestowrite, 0, bytestowrite.Length);
            }
            fs.Close();
        }

        public static void setAutoStart(bool autostart, int incloseoption,bool innevernotifyclose)
        {
            bool bAutostart;
            string lastUserId = "";
            int closeoption = 1;
            bool nevernotifyclose = false;
            string preversion;
            List<UserInfo> users = getUsers(out bAutostart, out lastUserId,out closeoption, out nevernotifyclose,out preversion);
             
           // String filename = exePath.Substring(0, exePath.LastIndexOf('\\') + 1);
            String filename = getFilename1();
            try
            {
                FileStream fs = new FileStream(filename, FileMode.Create, FileAccess.ReadWrite);
                String strAutostart = autostart ? "true" : "false";
                strAutostart += ",";
                strAutostart += lastUserId;
                strAutostart += ",";
                strAutostart += incloseoption == 2 ? "2" : "1";
                strAutostart += ",";
                strAutostart += innevernotifyclose ? "1" : "0";
                strAutostart += ",";
                strAutostart += Constants.currentVersion; 
                strAutostart += "\r\n";
                byte[] bytestowrite = Encoding.UTF8.GetBytes(strAutostart);
                fs.Write(bytestowrite, 0, bytestowrite.Length);
                if (users != null)
                    foreach (UserInfo u in users)
                    {
                        string encryuserpass = new EncryPt(u.userNo).Encrypto(u.userPass);
                        String usernameandpassword = formatKey(u.userNo) + formatKey(encryuserpass) + formatKey(u.savePass) + formatKey(u.autoLogin) + "\r\n";
                        bytestowrite = Encoding.UTF8.GetBytes(usernameandpassword);
                        fs.Write(bytestowrite, 0, bytestowrite.Length);
                    }
                fs.Close();
            }
            catch (Exception ex)
            {
            }
        }

        private static string formatKey(string key)
        {
            string formattedKey = key;
            int length = key.Length;
            string padding = Convert.ToString(length);
            if (length < 10)
            {
                padding = "0" + padding;
            }

            formattedKey = padding + formattedKey;

            if (formattedKey.Length < 50)
            {
                int nLen = 50-formattedKey.Length;
                for (int i = 0; i < nLen; i++)
                {
                    formattedKey += " ";
                }
            }

            return formattedKey;
        }

        private static string unformatKey(string key)
        {
            string formatKey = key;
            int length = Convert.ToInt16(formatKey.Substring(0, 2));
            return formatKey.Substring(2, length);
        }
    }
}
