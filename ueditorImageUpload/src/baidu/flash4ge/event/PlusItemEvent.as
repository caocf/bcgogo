package baidu.flash4ge.event
{
	import flash.events.Event;
	
	public class PlusItemEvent extends Event
	{
		public static const ADD_NEW_PICS:String = "addNewPics";
		
		public function PlusItemEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}