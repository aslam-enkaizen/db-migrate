package com.exrade.runtime.userprofile;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.exrade.Cache;
import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.google.common.base.Strings;

import java.util.Calendar;

public class TraktiJwtManager {
	private static final TraktiJwtManager INSTANCE = new TraktiJwtManager();
	private static final String ISSUER = "trakti";
	private static Algorithm ALGORITHM;
	private static JWTVerifier VERIFIER;

	private TraktiJwtManager() {
		try {
			ALGORITHM = Algorithm.HMAC256(ExConfiguration.getStringProperty("application.secret"));
			VERIFIER = JWT.require(ALGORITHM)
			        .withIssuer(ISSUER)
			        .build(); //Reusable verifier instance
		} catch (IllegalArgumentException exception) {
			ExLogger.get().error("Failed to instantiate JWT Manager", exception);
		}
	}

	public static TraktiJwtManager getInstance() {
		return INSTANCE;
	}

	public String generateToken(String subject) {
		String token = null;
		if(!Strings.isNullOrEmpty(subject)) {
			String cacheKey = "jwt." + subject;
			token = (String) Cache.get(cacheKey);

			if(Strings.isNullOrEmpty(token)) {
				try {
					token = JWT.create()
							.withIssuer(ISSUER)
							.withIssuedAt(Calendar.getInstance().getTime())
							.withSubject(subject)
							.sign(ALGORITHM);

					Cache.set(cacheKey, token, 60 * 60 * 24); // 24 hours
				} catch (JWTCreationException exception){
					ExLogger.get().warn("Failed to generate the JWT token for the subject: " + subject, exception);
				}
			}
		}

		return token;
	}

	public String decodeSubject(String token) {
		String subject = null;
		if(!Strings.isNullOrEmpty(token)) {
			try {
				DecodedJWT jwt = VERIFIER.verify(token);
				subject = jwt.getSubject();
			} catch (JWTVerificationException exception){
				ExLogger.get().warn("Failed to decode the JWT token : " + token, exception);
			}
		}
		return subject;
	}


}
