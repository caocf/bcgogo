/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 14-1-28
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */

/**
 * 比较粗糙  希望牛人能帮忙整理下
 *
 * sUrl :提交到appIframe的页面地址
 * appIframe:提交到appIframe对象
 * PostParams:提交到appIframe对象的Post参数对象*
 * @type {IframePost}
 */
IframePost = function () {
    var setFrame = function (oFrm) {
            if (!oFrm.name || oFrm.name == "")
                oFrm.name = oFrm.id;
        },
        createForm = function (obj) {
            var oForm = document.createElement("form");
            oForm.id = "forPost";
            oForm.method = "post";
            oForm.action = obj.Url;
            oForm.target = obj.Target.name;
            var oIpt, arrParams;
            arrParams = obj.PostParams;
            for (var tmpName in arrParams) {
                oIpt = document.createElement("input");
                oIpt.type = "hidden";
                oIpt.name = tmpName;
                oIpt.value = arrParams[tmpName];
                oForm.appendChild(oIpt);
            }
            return oForm;
        },
        deleteForm = function () {
            var oOldFrm = document.getElementById("forPost");
            if (oOldFrm) {
                document.body.removeChild(oOldFrm);
            }
        }

    return {
        //功能：给嵌套的Iframe界面Post值
        //参数：obj - 传递对象
        //     {Url - 页面地址, Target - Iframe对象, PostParams - Post参数,{ 参数名1 : 值1, 参数名2 : 值2 } }
        //例：{ Url: "ProjPlan_DcRate_Dept_Main.aspx?YearMonth=2012-01", Target: appIframe, PostParams: { DeptGUID: document.all["txt_DeptGUID"].value} }
        doPost: function (obj) {
            setFrame(obj.Target);
            deleteForm();
            var oForm = createForm(obj);
            document.body.appendChild(oForm);
            oForm.submit();
            deleteForm();
        }
    }
}();