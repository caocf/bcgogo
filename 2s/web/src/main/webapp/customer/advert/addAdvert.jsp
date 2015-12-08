<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">


<div id="addAdvertDiv" class="prompt_box" style="width:720px;display: none;">
  <div class="title">
    <div class="turn_off" style="margin:10px 10px 0;"></div>
    <span id="addOrUpdateAdvert">新增宣传</span>
  </div>

  <form:form commandName="advertDTO" id="addAdvertForm"
             action="advert.do?method=saveOrUpdateAdvert" target="_blank" method="post"
             name="thisform">
    <input type="hidden" name="status" id="status" value=""/>
    <input type="hidden" name="id" id="advertId" value=""/>



    <div class="content">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td align="right">标题：</td>
          <td><input name="title" maxlength="25" id="advertTitle" type="text" class="content_input_title"/></td>
        </tr>
        <tr>
          <td align="right">&nbsp;</td>
          <td><span class="gray_color">（输入不可超出<span class="red_color">25</span>个字）</span></td>
        </tr>
        <tr>
          <td colspan="2" height="5" style="line-height:normal">&nbsp;</td>
        </tr>
        <tr>
          <td valign="top" align="right">宣传内容：</td>
          <td><script id="productDescriptionEditor" name="description" type="text/plain"></script>
          </td>
        </tr>
        <tr>
          <td align="right">有效期：</td>
          <td><input name="beginDateStr" id="beginDateStr" type="text" class="content_input_title" style="width:120px;"/>
            <span style="float:left; margin:0 5px; line-height:28px;">至</span>
            <input name="endDateStr" id="endDateStr" type="text" class="content_input_title" style="width:120px;"/></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </table>


      <div class="clear"></div>
      <div class="wid275" style="width: 500px;">
        <div class="addressList">
          <div class="search_btn" id="saveDraftBtn">保存&预览</div>
          <div class="search_btn" id="publishBtn">直接发布</div>
        </div>
      </div>
      <div class="clear"></div>
    </div>

  </form:form>

</div>


