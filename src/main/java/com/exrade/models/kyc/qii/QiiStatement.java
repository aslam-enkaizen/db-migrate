package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

public class QiiStatement extends BaseEntityUUIDTimeStampable {
	private String question;
	private String answer;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
