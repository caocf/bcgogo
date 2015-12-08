/**
 * @description 字符串处理
 * @author ndong
 * @date create 2012-12-3
 */

var stringUtil={};

/**
 * 变量
 */
stringUtil. rangeData={

};

/**
 * 函数
 */

/**
 * 判空
 * @param str
 */
stringUtil.isEmpty = function(str){
  if(str==null||str=="null"||str==undefined||str=="undefined"||str==""){
    return true;
  }
  return false;
};

stringUtil.isNotEmpty = function(str){
  return !stringUtil.isEmpty(str);
};

stringUtil.generateKey=function(str1,str2){
    str1=G.isEmpty(str1)?"":str1;
    str2=G.isEmpty(str2)?"":str2;
    return (str1+str2);
}