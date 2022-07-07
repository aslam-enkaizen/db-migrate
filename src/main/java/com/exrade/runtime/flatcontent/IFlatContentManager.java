package com.exrade.runtime.flatcontent;

import com.exrade.models.flatcontent.FlatContent;

public interface IFlatContentManager {

	/**
	 * Get a flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public String createFlatContent(FlatContent iFlatContent);
	
	/**
	 * Get a flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public FlatContent getFlatContentByUrl(String iUrl);
	
	/**
	 * Get a flat content by the corresponding url
	 * @param iUrl
	 * @return FlatContent
	 */
	public FlatContent getFlatContentByUUID(String iUUID);

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
	public FlatContent getFlatContentByUrl(String iUrl,String iLanguageTag);

	public void update(FlatContent flatContent);

	public void delete(String uuid);
	
}
