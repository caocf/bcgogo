using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using YiFaComm;
using System.Runtime.InteropServices;
namespace YiFaClient
{
    public partial class UpdateFFNotify : Form
    {
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);
        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();
       // ClientMain main = null;
        string updateurl;
        bool installedFF;
        public UpdateFFNotify(ClientMain mainform, string url, bool installedFF)
        {
            InitializeComponent();
          //  this.main = mainform;
            this.updateurl = url;
            this.installedFF = installedFF;
           // if (main == null)
           // {
          //      checkBox1.Visible = false;
          //  }
            
        }

        private void button1_Click(object sender, EventArgs e)
        {
           // if (main != null)
           // {
           //     main.setFFCheckFlag(!checkBox1.Checked);
          //  }

            if (!installedFF)
            {
                URLNavigator.defaultOpenURL(updateurl);
            }else
            {
                URLNavigator.ffOpenURL(updateurl);
            }

            this.Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
           // if (main != null)
           // {
          //      main.setFFCheckFlag(!checkBox1.Checked);
          //  }

            this.Close();
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            bool notify = !checkBox1.Checked;
            Utils.setFFUpdateflag(notify);
        }

        private void button3_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void panel1_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
        }

        private void button3_MouseEnter(object sender, EventArgs e)
        {
            button3.BackgroundImage = Properties.Resources.鼠标经过背景;
            button3.Image = Properties.Resources.鼠标经过关闭;
        }

        private void button3_MouseLeave(object sender, EventArgs e)
        {
            button3.BackgroundImage = Properties.Resources.默认背景;
            button3.Image = Properties.Resources.默认关闭;
        }

        private void label3_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                ReleaseCapture();
                SendMessage(this.Handle, 161, 2, 0);
            }
            base.OnMouseDown(e);
        }
    }
}
