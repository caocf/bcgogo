package baidu.mod.multiimageuploader.file
{
	import baidu.mod.multiimageuploader.config.Config;
	import baidu.mod.multiimageuploader.event.FileSelectorEvent;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.net.FileReferenceList;

	public class FileSelector extends EventDispatcher
	{
		// 上传需要的参数
		protected var _fileReferenceList:FileReferenceList;
		protected var _fileTypes:Array;
		// 选择的文件
		protected var _fileList:Array;
		// 所选的文件数量，这个数量与_fileList的长度强制同步
		protected var _numFiles:int = 0;
		
		public function FileSelector()
		{
		}
		
		/**
		 * 增加文件选择类型
		 */ 
		public function addFileType(filter:FileFilter):void{
			if(filter){
				if(!_fileTypes){
					_fileTypes = [];
				}
				_fileTypes.push(filter);
			}
		}
		
		/**
		 * 选择文件
		 */ 
		public function browse():void{
			if(!_fileReferenceList) {
				_fileReferenceList = new FileReferenceList();
				_fileReferenceList.addEventListener(Event.SELECT, selectFiles);
				_fileReferenceList.addEventListener(Event.CANCEL, cancelSelect);
			}
			_fileReferenceList.browse(_fileTypes);
		}
		
		/**
		 * 得到所有文件
		 */ 
		public function getAllFiles():Array{
			return _fileList;
		}
		
		/**
		 * 删除文件
		 * 
		 * @param	index [int]
		 */ 
		public function deleteFileByIndex(index:int):void{
			if(index >= 0 && index < _fileList.length) {
				_fileList.splice(index, 1);
			}
			//更新文件数量
			_numFiles = _fileList.length;
		}
		
		/**
		 * 删除所有文件
		 */ 
		public function deleteFilesAll():void{
			//重置变量
			_fileReferenceList = null;
			_fileList = [];
			//更新文件数量
			_numFiles = _fileList.length;
		}
		
		/**
		 * 删除所有文件
		 */ 
		public function clear():void{
			deleteFilesAll();
		}
		
		/**
		 * 选择文件后的响应函数
		 */ 
		protected function selectFiles(evt:Event):void{
			// 现存文件列表
			if(!_fileList){
				_fileList = [];
			}
			var selectEvent:FileSelectorEvent = new FileSelectorEvent(FileSelectorEvent.SELECT_FILES);
			selectEvent.files = [];
			
			// 使用了FileReferenceList选择了一个或多个文件
			var tmpFileList:Array = _fileReferenceList.fileList;
			
			// 对选择的文件去重
			if(Config.DUPLICATED_CHOOSE == 0){
				tmpFileList = deleteSameFiles(tmpFileList, _fileList);
			}
			
			// 把文件放到_fileList中
			for(var i:int = 0, iLen:int = tmpFileList.length; i < iLen; i++) {
				var file:FileReference = tmpFileList[i] as FileReference;
				_fileList.push(file);
				selectEvent.files.push(file);
			}
			// 对选择的文件提出数量限制
			if(Config.MAX_FILE_NUM > 0 && _fileList && Config.MAX_FILE_NUM < _fileList.length) {
				_fileList.splice(Config.MAX_FILE_NUM);
				
				selectEvent.files.splice(Config.MAX_FILE_NUM - _numFiles);
			}
			//更新文件数量
			_numFiles = _fileList.length;
			
			//派发选择事件
			dispatchEvent(selectEvent);
		}
		
		/**
		 * 取消文件选择的响应函数
		 */ 
		protected function cancelSelect(evt:Event):void{
			var selectEvent:FileSelectorEvent = new FileSelectorEvent(FileSelectorEvent.CANCEL_FILES);
			//派发选择事件
			dispatchEvent(selectEvent);
		}
		
		protected function deleteSameFiles(newFileList:Array, srcFileList:Array):Array {
			var uniqueFileList:Array = [];
			
			if(!newFileList || newFileList.length == 0) {
				uniqueFileList = srcFileList;
			}else if (!srcFileList || srcFileList.length == 0) {
				uniqueFileList = newFileList;
			}else {
				for(var i:int = 0, iLen:int = newFileList.length; i < iLen; i++){
					var newFileReference:FileReference = newFileList[i] as FileReference;
					var unique:Boolean = true;
					for (var j:int = 0, jLen:int = srcFileList.length; j < jLen; j++) {
						var oldFileReference:FileReference = srcFileList[j] as FileReference;
						if (newFileReference.name == oldFileReference.name
							&& newFileReference.type == oldFileReference.type
							&& newFileReference.size == oldFileReference.size
							&& (newFileReference.modificationDate).time == (oldFileReference.modificationDate).time
							&& (newFileReference.creationDate).time == (oldFileReference.creationDate).time) {
							unique = false;
							break;
						}
					}
					//这个地方特别注意：FileReference多选的时候，居然可以同时多次选择同一份文件，复现办法为直接拷贝文件名...
					if(unique){
						for (j = 0, jLen = uniqueFileList.length; j < jLen; j++ ) {
							oldFileReference = uniqueFileList[j] as FileReference;
							if (newFileReference.name == oldFileReference.name
								&& newFileReference.type == oldFileReference.type
								&& newFileReference.size == oldFileReference.size
								&& (newFileReference.modificationDate).time == (oldFileReference.modificationDate).time
								&& (newFileReference.creationDate).time == (oldFileReference.creationDate).time) {
								unique = false;
								break;
							}
						}
					}
					if (unique) {
						uniqueFileList.push(newFileReference);
					}
				}
			}
			return uniqueFileList;
		}
	}
}