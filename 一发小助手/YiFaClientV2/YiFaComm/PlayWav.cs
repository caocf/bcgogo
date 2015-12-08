using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
namespace YiFaComm
{
    public class PlayWav
    {
        [DllImport("winmm")]
        public static extern bool PlaySound(string szSound, IntPtr hMod, int flags);       
    } //End WAVSounds class
}
