package com.tonggou.andclient.vo;

import java.util.Map;

public class ShopScore {
	float   totalScore;//总分    float
	Map<String,Float> scoreItems;//评分明细, mapkey枚举由开发时根据实际业务确定
	public float getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}
	public Map<String, Float> getScoreItems() {
		return scoreItems;
	}
	public void setScoreItems(Map<String, Float> scoreItems) {
		this.scoreItems = scoreItems;
	}

}
