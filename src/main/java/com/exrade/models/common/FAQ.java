package com.exrade.models.common;

import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.Objects;

/**
 * Frequently Asked Question container
 *
 * @author Carlo Polisini
 */
public class FAQ extends BaseEntityUUID {

	private String question;

	private String answer;

	public FAQ(){
	}

	public FAQ(String iQuestion, String iAnswer){
		Objects.requireNonNull(iQuestion,"Question cant be null");
		Objects.requireNonNull(iAnswer,"Answer cant be null");

		setQuestion(iQuestion);
		setAnswer(iAnswer);
	}


	public String getQuestion() {
		return question;
	}

	public void setQuestion(String iQuestion) {
		question = iQuestion;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String iAnswer) {
		answer = iAnswer;
	}

}
