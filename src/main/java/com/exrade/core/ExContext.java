package com.exrade.core;

import com.exrade.models.userprofile.Membership;
import com.exrade.util.ObjectsUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ExContext {

	public static ThreadLocal<ExContext> current = new ThreadLocal<ExContext>();

	private final String id = ObjectsUtil.generateUniqueID();
	
	private final Membership membership;

	private String lang = null;
	
	 /**
     * Free space to store your request specific data
     */
    public Map<String, Object> args;

	public ExContext(Membership membership,String lang) {
		this.membership = membership;
		this.lang = lang; 
	}
	
	public ExContext(Membership membership,String lang,HashMap<String,Object> args) {
		this.membership = membership;
		this.lang = lang; 
		this.args = new HashMap<String,Object>(args);
	}
	
	/**
	 * Retrieves the current Exrade context, for the current thread.
	 */
	public static ExContext current() {
		ExContext c = current.get();
		if(c == null) {
			throw new RuntimeException("There is no Exrade Context available from here.");
		}
		return c;
	}

	public String lang(){
		if (lang != null){
			return lang;
		}
		return Locale.getDefault().getLanguage();
	}

	 /**
     * Change durably the lang for the current user.
     * @param code New lang code to use (e.g. "fr", "en_US", etc.)
     * @return true if the requested lang was supported by the application, otherwise false.
     */
    public boolean changeLang(String code) {
        Locale locale = Locale.forLanguageTag(code);
        if (locale != null && locale.getLanguage() != null) {
            this.lang = locale.getLanguage();
            return true;
        } else {
            return false;
        }
    }
	
	public String id() {
		return id;
	}

	public Membership getMembership() {
		return membership;
	}

}
