package com.exrade.util;

import com.exrade.runtime.filemanagement.FileMetadata;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileUtil {
	public static final List<String> IMAGE_FILE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff", "raw", "eps", "svg");

	public static boolean isImage(Map<String, Object> metadata) {
		if(metadata != null && metadata.containsKey(FileMetadata.FILE_EXTENSION)){
			String fileExtension = (String)metadata.get(FileMetadata.FILE_EXTENSION);
			if(!Strings.isNullOrEmpty(fileExtension) && IMAGE_FILE_EXTENSIONS.contains(fileExtension.toLowerCase()))
				return true;
		}
		
		return false;
	}
	
}
