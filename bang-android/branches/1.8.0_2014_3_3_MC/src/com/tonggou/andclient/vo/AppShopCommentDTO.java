package com.tonggou.andclient.vo;

/**
 * @author fbl
 *
 */
public class AppShopCommentDTO {
	
	private float commentScore;  //评分
	private String commentContent;  // 评价内容
	private String commentTimeStr;  //评价时间
	private String commentTime;     //
	private String commentatorName;  //评价的用户名
	
	
	
	@Override
	public String toString() {
		return "AppShopCommentDTO [commentScore=" + commentScore
				+ ", commentContent=" + commentContent + ", commentTimeStr="
				+ commentTimeStr + ", commentTime=" + commentTime
				+ ", commentatorName=" + commentatorName + "]";
	}
	public float getCommentScore() {
		return commentScore;
	}
	public void setCommentScore(float commentScore) {
		this.commentScore = commentScore;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public String getCommentTimeStr() {
		return commentTimeStr;
	}
	public void setCommentTimeStr(String commentTimeStr) {
		this.commentTimeStr = commentTimeStr;
	}
	public String getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}
	public String getCommentatorName() {
		return commentatorName;
	}
	public void setCommentatorName(String commentatorName) {
		this.commentatorName = commentatorName;
	}
	
	
	
	

}
