using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Threading;
using System.Runtime.InteropServices;
using YiFaComm;
using System.Deployment.Application;
using System.Net.NetworkInformation;
using System.Net;
using System.Net.Sockets;
namespace YiFaClient
{
    static class Program
    {
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {       
            bool bRestart = false;
            string newversion = Constants.currentVersion;
            InstallUpdateSyncWithInfo(out bRestart, out newversion);

            System.Diagnostics.Process currentProcess = System.Diagnostics.Process.GetCurrentProcess();
            int currentProcessId = currentProcess.Id;

            System.Diagnostics.Process[] processes = System.Diagnostics.Process.GetProcesses();
            if (processes != null)
            {
                foreach (System.Diagnostics.Process p in processes)
                {
                    if (p.Id != currentProcessId && p.ProcessName.ToUpper().IndexOf("YiFaClient".ToUpper()) >= 0)
                    {
                        p.Kill();
                    }
                }
            }

            if (bRestart || !newversion.Equals(Constants.currentVersion))
            {
                Application.Restart();
                return;
            }

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(Utils.getMainInstance());

        }
     
      
        private static void InstallUpdateSyncWithInfo(out bool bRestart, out string newversion)
        {
            UpdateCheckInfo info = null;
            newversion = Constants.currentVersion;
            bRestart = false;
            ApplicationDeployment ad = null;

            try
            {
                ad = ApplicationDeployment.CurrentDeployment;

                info = ad.CheckForDetailedUpdate();
                newversion = info.AvailableVersion.ToString();
            }
            catch (DeploymentDownloadException dde)
            {
                return;
            }
            catch (InvalidDeploymentException ide)
            {
                return;
            }
            catch (InvalidOperationException ioe)
            {
                return;
            }


            if (info.UpdateAvailable)
            {
                Boolean doUpdate = true;
                if (doUpdate)
                {
                    try
                    {
                        ad.Update();
                        bRestart = true;
                    }
                    catch (DeploymentDownloadException dde)
                    {
                        return;
                    }
                }
            }

        }
    }
}
