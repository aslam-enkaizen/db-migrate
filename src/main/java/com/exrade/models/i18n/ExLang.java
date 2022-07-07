package com.exrade.models.i18n;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/***
 * Languages supported in exrade-core
 * @author Md Mahfuzul Islam
 *
 */
public enum ExLang {

	ENGLISH("en"),

	ITALIAN("it"),

	SPANISH("es"),

	GERMAN("de"),

	DUTCH("nl");

	private final static Map<String, ExLang> codeToExLang = new HashMap<>();

	static {
		for (ExLang lang : ExLang.values()) {
			codeToExLang.put(lang.getCode(), lang);
		}
	}

	private final String code;

	private ExLang(String iCode) {
		code = iCode;
	}

	public String getCode() {
		return code;
	}


	public static ExLang fromCode(String iCode) {
		return codeToExLang.get(iCode);
	}

	public static EnumSet<ExLang> all = EnumSet.of(ENGLISH, ITALIAN, SPANISH, GERMAN, DUTCH);

}
