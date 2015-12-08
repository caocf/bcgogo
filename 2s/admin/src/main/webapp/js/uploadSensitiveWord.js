/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-8-14
 * Time: 下午1:58
 * To change this template use File | Settings | File Templates.
 */
function thisFormSubmit() {
            if ($("#sensitiveWords").val() == "") {
                alert("请选择需要上传的文件!");
            } else {
                var fileSuffix = $("#sensitiveWords").val().split(".")[1];
                if (fileSuffix != 'txt') {
                    alert("请选择txt文件!");
        } else {
                   $("#uploadSensitiveWords").submit();
                }
            }
}