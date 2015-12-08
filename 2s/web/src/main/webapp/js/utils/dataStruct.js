/**
 * 类似java的map，key-value的形式
 */
function Map() {

    var struct = function(key, value) {
        this.key = key;
        this.value = value;
    }

    var _put = function(key, value) {
        key=String(key);
        for(var i = 0; i < this.arr.length; i++) {
            if(this.arr[i].key === key) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var _get = function(key) {
        key=String(key);
        for(var i = 0; i < this.arr.length; i++) {
            if(this.arr[i].key === key) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var _remove = function(key) {
        var v;
        for(var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
            if(v.key === key) {
                break;
            }
            this.arr.unshift(v);
        }
    }

    var _removeAll = function() {
        this.arr= new Array();
    }

    var size = function() {
        return this.arr.length;
    }

    var isEmpty = function() {
        return this.arr.length <= 0;
    }

    var clearMap = function() {
        this.arr = [];
    }

    var keySet= function(){
        var keySet=new Array();
        for(var i=0;i<this.arr.length;i++){
            keySet.push(this.arr[i].key);
        }
        return keySet;
    }

    this.arr = new Array();
    this.get = _get;
    this.put = _put;
    this.remove = _remove;
    this.removeAll = _removeAll;
    this.size = size;
    this.isEmpty = isEmpty;
    this.clearMap = clearMap;
    this.keySet = keySet;
}
//javaMap-->jsMap
function toMap(javaMap){
    if(G.isEmpty(javaMap)){
        return;
    }
    var jsMap=new Map();
    for(var key in javaMap){
        jsMap.put(key,javaMap[key]);
    }
    return jsMap;
}