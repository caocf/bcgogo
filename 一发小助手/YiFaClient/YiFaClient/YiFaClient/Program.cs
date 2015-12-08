using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Threading;
using System.Runtime.InteropServices;
using YiFaComm;
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
            
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(Utils.getMainInstance());

        }
    }
}
