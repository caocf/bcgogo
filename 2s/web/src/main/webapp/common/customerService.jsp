<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript">
  function closeService() {
    $("#customer_service").fadeOut(2000);
  }
</script>
<div class="tab_repay " style="display: none;" id="customer_service">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter">
    <div class="i_note" id="div_drag">客服</div>
    <div class="i_close" id="div_close" onclick="closeService()"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody">
    <ul class="boxContent">
      <li><span>客服电话：</span><label>0512-66733331</label><br/></li>
      <li><span>客服QQ：</span><label>1754061146&nbsp;1362756627 </label><br/>
          <label style="margin-left:60px;">2390356460</label></li>
    </ul>

  </div>
  <div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
  </div>
</div>
