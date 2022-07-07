package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.flatcontent.FlatContent;

public interface FlatContentAPI {

	/**
	 * Get a flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public String createFlatContent(ExRequestEnvelope request, FlatContent iFlatContent);
	
	/**
	 * Get a flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public FlatContent getFlatContentByUrl(ExRequestEnvelope request, String iUrl);
	
	/**
	 * Get a flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public FlatContent getFlatContentByUUID(ExRequestEnvelope request, String iUUID);

//	/**
//	 * Get a flat content by its name
//	 * @param iName
//	 * @return FlatContent
//	 */
//	public FlatContent getFlatContentByName(String iName);
	
	/**
	 * Get a localized flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public FlatContent getFlatContentByUrl(ExRequestEnvelope request, String iUrl,String iLanguageTag);

	public void update(ExRequestEnvelope request, FlatContent flatContent);

	public void delete(ExRequestEnvelope request, String uuid);
	
}
