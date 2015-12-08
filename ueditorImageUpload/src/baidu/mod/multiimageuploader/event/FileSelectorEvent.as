
package baidu.mod.multiimageuploader.event
{
	import flash.events.Event;

	public class FileSelectorEvent extends Event
	{
		public static const SELECT_FILES:String = "select_files";
		public static const CANCEL_FILES:String = "cancel_files";
		
		public var files:Array;
		
		public function FileSelectorEvent(type:String)
		{
			super(type);
		}
	}
}