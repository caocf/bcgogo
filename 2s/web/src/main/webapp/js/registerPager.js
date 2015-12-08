/**
 * Created by IntelliJ IDEA.
 * User: JYW
 * Date: 12-1-29
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */

function page(method,pageNo){
     window.location="beshop.do?method="+method+"&pageNo="+pageNo;
}

function prePage(method,pageNo){
    if(pageNo==1){
        alert("已是第一页");
        return;
    }
    pageNo--;
    window.location="beshop.do?method="+method+"&pageNo="+pageNo;
}

function nextPage(method,pageNo,pageCount){
    if(pageNo==pageCount){
        alert("已是最后一页");
        return;
    }
    pageNo++;
     window.location="beshop.do?method="+method+"&pageNo="+pageNo;
}