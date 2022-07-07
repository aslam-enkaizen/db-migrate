package com.exrade.util;

import org.mindrot.jbcrypt.BCrypt;

public class ExHelpers {

	public static String hash(final String clearString) {
		return BCrypt.hashpw(clearString, BCrypt.gensalt());
	}
	
	/**
	 * Compare the hash of a string with the clear candidate string provided
	 * @param hashed (hashed string)
	 * @param candidate (clear string)
	 * @return true if hashes match, false if one of the args is null
	 */
	public static boolean checkHash(final String hashed, final String candidate) {
		if(hashed == null || candidate == null) {
			return false;
		}
		return BCrypt.checkpw(candidate, hashed);
	}
	
}
