package com.exrade.runtime.userprofile;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

public class JwtManager {
	public static DecodedJWT decode(String token, URL jwksUrl) throws JwkException {
		DecodedJWT decodedJwt = JWT.decode(token);

		if(jwksUrl != null) {
			JwkProvider provider = new UrlJwkProvider(jwksUrl);
			if(!Strings.isNullOrEmpty(decodedJwt.getKeyId())) {
				Jwk jwk = provider.get(decodedJwt.getKeyId());
				Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
				algorithm.verify(decodedJwt);
			}
			else {
				List<Jwk> allJkws = ((UrlJwkProvider)provider).getAll();
				boolean verified = false;
				for(Jwk jwk : allJkws) {
					try {
						Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
						algorithm.verify(decodedJwt);
						verified = true;
						break;
					}
					catch(SignatureVerificationException ex) {

					}
				}

				if(!verified)
					throw new JwkException("Signature verification failed!");
			}

		}

		return decodedJwt;
	}

	public static DecodedJWT decode(String token, String jwksUrl) throws MalformedURLException, JwkException{
		if(Strings.isNullOrEmpty(jwksUrl))
			return JWT.decode(token);
		else
			return decode(token, new URL(jwksUrl));
	}

	public static String getClaim(String token, String jwksUrl, String claimName) throws MalformedURLException, JwkException {
		DecodedJWT decodedJwt = decode(token, jwksUrl);
		Claim claim = decodedJwt.getClaim(claimName);

		return claim.asString();
	}
}