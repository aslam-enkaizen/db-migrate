package com.exrade.util.i18n.resourcebundle;

import java.io.IOException;
import java.util.*;

public class ExResourceBundle extends ResourceBundle { 
	    
		private Properties properties = null; 
	    
	    public Properties getProperties() {
			return properties;
		}

		public ExResourceBundle(Properties props) throws IOException { 
	        this.properties = props;
	    } 
		
	    protected Object handleGetObject(String key) { 
	        return getProperties().getProperty(key); 
	    }
	    
	    public Enumeration<String> getKeys() { 
	       Set<String> handleKeys = getProperties().stringPropertyNames(); 
	       return Collections.enumeration(handleKeys); 
	    } 
	    
}
