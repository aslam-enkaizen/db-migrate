package com.exrade.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UrlUtil {
	public static final String UUID_REGEX = "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}";

	public static String getQueryParamValue(String url, String paramName) {
		try {
			List<NameValuePair> params = URLEncodedUtils.parse(url, Charset.forName("UTF-8"));

			for (NameValuePair param : params) {
				if(param.getName().equalsIgnoreCase(paramName))
					return param.getValue();
			}
		}
		catch(Exception ex) {

		}

		return null;
	}

	public static String extractNegotiationInvitationUUID(String url) {
		try {
			Pattern pairRegex = Pattern.compile(UUID_REGEX);
			Matcher matcher = pairRegex.matcher(url.split("description")[1]);
			if (matcher.find()) {
				return matcher.group(0);
			}
		}
		catch(Exception ex) {

		}
	    return null;
	}
}
