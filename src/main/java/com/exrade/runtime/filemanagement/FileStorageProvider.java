package com.exrade.runtime.filemanagement;

/**
 * Provides configured IFileStorageController
 * 
 * @author john
 *
 */
public class FileStorageProvider {
	public static IFileStorageController getFileStorageController(){
		return new DBFileStorageController();
	}
}
