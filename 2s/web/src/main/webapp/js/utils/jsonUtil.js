/**
 * @description 数字处理
 * @author ndong
 * @date create 2012-11-30
 */

var jsonUtil={};

/**
 * 变量
 */
jsonUtil. rangeData={

};

/**
 * 函数
 */

/**
 * 判空
 * @param json
 */
jsonUtil.isEmpty = function(json){
  if(json==null||json==""||json.length==0){
    return true;
  }
  return false;
};