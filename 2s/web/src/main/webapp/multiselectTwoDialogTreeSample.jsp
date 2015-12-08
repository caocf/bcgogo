<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <link rel="stylesheet" href="js/components/themes/bcgogo-multiselectTwoDialog.css" />

    <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-multiselectTwoDialogTree.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            App.Net.asyncPost({
                url:"test/dummyData/multiselectTwoDialogTreeDummyData.json",
                dataType:"json",
                success:function(data){
                    var multiSelectTwoDialogTree = new App.Module.MultiSelectTwoDialogTree();
                    window.multiSelectTwoDialogTree = multiSelectTwoDialogTree;
                    multiSelectTwoDialogTree.init({
                        "startLevel":2,
                        "data":data,
                        "selector":"#testId",
                        "onSearch":function(searchWord, event) {
                            console.log(searchWord);
                            return App.Net.syncGet({
                                url:"test/dummyData/multiselectTwoDialogTreeDummyData.json",
                                dataType:"json"
                            });
                        },
                        "onDelete":function(itemData, event) {
                            console.log(itemData);
                        },
                        "onSelect":function(itemData, event) {
                            console.log(itemData);
                        },
                        "onClear":function(event) {
                            console.log("onClear");
                        }

                    });
                },
                error:function() {
                    console.log("error");
                }
            });


        });
    </script>

</head>
<body>

<div id="testId"></div>

</body>
</html>