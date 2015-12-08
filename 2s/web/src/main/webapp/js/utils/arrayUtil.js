/**
 * @description js数组处理
 * @author ndong
 * @date create 2013-4-7
 */

var arrayUtil={};

/**
 * 变量
 */
arrayUtil. rangeData={

};

/**
 * 函数
 */

/**
 *
 * @param str
 */
arrayUtil.isEmpty = function(array){
    if(array==null||array=="null"||array==undefined||array=="undefined"||array==""||array.length==0){
        return true;
    }
    return false;
};
arrayUtil.contains = function(templateArray,str){
    if(arrayUtil.isEmpty(templateArray)||G.isEmpty(str)){
        return false;
    }
    for(var i=0;i<templateArray.length;i++){
        if(str==templateArray[i]){
            return true;
        }
    }
    return false;
};
