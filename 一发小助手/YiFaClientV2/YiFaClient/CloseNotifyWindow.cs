using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using YiFaComm;
namespace YiFaClient
{
    public partial class CloseNotifyWindow : Form
    {
        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);
        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();
        public CloseNotifyWindow()
        {
            InitializeComponent();
            radioButton1.Checked = true;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Utils.setCloseMainDirec(radioButton2.Checked);
            if (radioButton2.Checked)
            {            
                DialogResult = DialogResult.Yes;
            }
            else//隐藏到任务栏
            {
                DialogResult = DialogResult.Cancel;
            }

            if (checkBox1.Checked)//不再提示
            {
                bool bAutostart;
                string lastUserId = "";
                int closeoption = 1;
                bool nevernotifyclose = false;
                string preversion;
                FileUtils.getUsers(out bAutostart, out lastUserId, out closeoption, out nevernotifyclose,out preversion);
                FileUtils.setAutoStart(bAutostart, radioButton2.Checked ? 2 : 1, true);
            }

            this.Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Utils.setCloseMainDirec(radioButton2.Checked);
            DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            Utils.showNotify(checkBox1.Checked);
           
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

        private void label1_MouseDown(object sender, MouseEventArgs e)
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
