package com.exrade.runtime.template;

import com.exrade.core.ExLogger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TemplateUtil {

	public static final String TAMPLATE_BASE_DIR = "/templates/";
	public static final String PDF_TAMPLATE_BASE_DIR_V2 = "/templates/pdf-v2/";

	public static byte[] cleanHtml(byte[] htmlData) throws IOException{
		byte[] cleandHtmlData = null;

		ByteArrayOutputStream cleanHtmlOutputStream = new ByteArrayOutputStream();
		CleanerProperties props = new CleanerProperties();
        HtmlCleaner cleaner = new HtmlCleaner(props);
        TagNode cleanedHtmlNode = cleaner.clean(new String(htmlData).replace("&lt;", "&amp;lt;").replace("&gt;", "&amp;gt;").
        		replace("&amp;", "&amp;amp;").replace("&quot;", "&amp;quot;").replaceAll("&(?![a-z0-9#]*;)", "&amp;amp;"));
        SimpleHtmlSerializer htmlSerializer = new SimpleHtmlSerializer(props);

        try{
        	htmlSerializer.writeToStream(cleanedHtmlNode, cleanHtmlOutputStream);
        	cleandHtmlData = cleanHtmlOutputStream.toByteArray();
        }
        catch(Exception e){
        	ExLogger.get().error("Error in creation of html",e);
        }
        finally{
			try {
				cleanHtmlOutputStream.close();
			} catch (IOException e) {
				ExLogger.get().error("Error cleaning html output stream",e);
			}
        }

        return cleandHtmlData;
	}

}

