package com.exrade.models.userprofile;

import com.exrade.platform.persistence.BaseEntity;
import com.exrade.runtime.timer.TimeProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class TokenAction extends BaseEntity {

	public enum Type {
		EMAIL_VERIFICATION,

		PASSWORD_RESET
	}

	/**
	 * Verification time frame (until the user clicks on the link in the email)
	 * in seconds
	 * Defaults to one week
	 */
	private final static long VERIFICATION_TIME = 7 * 24 * 3600;

	@Column(unique = true)
	private String token;

	@ManyToOne
	private User targetUser;

	private Type type;

//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date expiryDate;

	public static TokenAction create(final Type type, final String token,
			final User targetUser) {
		final TokenAction ta = new TokenAction();
		ta.targetUser = targetUser;
		ta.token = token;
		ta.type = type;
		final Date created = TimeProvider.now();
		ta.expiryDate = new Date(created.getTime() + VERIFICATION_TIME * 1000);
		return ta;
	}
	
	
	public String getToken() {
		return token;
	}


	public User getTargetUser() {
		return targetUser;
	}


	public Type getType() {
		return type;
	}


	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public boolean isValid() {
		return this.getExpiryDate().after(TimeProvider.now());
	}

}
