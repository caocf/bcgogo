package baidu.mod.multiimageuploader.model
{
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.net.FileReference;
	import flash.utils.ByteArray;

	public class ItemVO
	{
		protected var _fr:FileReference;
		
		protected var _filesize:Number;
		protected var _filename:String;
		protected var _filetype:String;
		protected var _rotation:Number = 0;
		protected var _image:DisplayObject;
		protected var _description:String;
		// 图片本身的长宽
		protected var _originalWidth:Number = 0;
		protected var _originalHeight:Number = 0;
		
		////////// GET/SET方法 //////////
		public function get rawData():ByteArray{
			return _fr.data;
		}
		
		public function get filesize():Number{
			return _filesize;
		}
		
		public function get filename():String{
			return _filename;
		}
		
		public function get filetype():String{
			return _filetype;
		}
		
		public function get fr():FileReference{
			return _fr;
		}
		
		public function get rotation():Number{
			return _rotation;
		}
		public function set rotation(value:Number):void{
			_rotation = value;
		}
		
		public function get description():String{
			return _description;
		}
		public function set description(value:String):void{
			_description = value;
		}
		
		public function get image():DisplayObject{
			return _image;
		}
		public function set image(data:DisplayObject):void{
			_image = data
		}
		
		public function get originalWidth():Number{
			return _originalWidth;
		}
		public function set originalWidth(value:Number):void{
			_originalWidth = value;
		}
		
		public function get originalHeight():Number{
			return _originalHeight;
		}
		public function set originalHeight(value:Number):void{
			_originalHeight = value;
		}
		////////// GET/SET方法 //////////
		
		public function ItemVO(fr:FileReference)
		{
			_fr = fr;
			
			_filesize = _fr.size;
			_filename = _fr.name;
			_filetype = _fr.type;
			_description = _fr.name;
		}
	}
}