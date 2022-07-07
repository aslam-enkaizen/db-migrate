package com.exrade.util;

import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;


public class ResourceUtil {

	/**
	 * Read an entity stored in JSON format. To generate new entity use:<br/>
	 * {@code createJsonResource(Object iResource, String iResourcePath)}
	 * 
	 * @param iRelativePath
	 *            path relative to {@code resource} folder
	 * @param type
	 * @return type
	 */
	public static <T> T getJsonResource(String iResourcePath, Class<? extends T> type) {

		Objects.requireNonNull(iResourcePath);
		T jsonEntity = null;

		if (!iResourcePath.startsWith("/")) {
			iResourcePath = "/" + iResourcePath;
		}

		URL resource = ResourceUtil.class.getResource(iResourcePath);

		try {
			jsonEntity = new ObjectMapper().readValue(resource, type);
		} catch (IOException e) {
			Map<String, String> replaceValues = new HashMap<String, String>();
			replaceValues.put("@@PATH@@", iResourcePath);
			throw new ExException(ErrorKeys.JSON_CANNOT_READ, e);
		}
		return jsonEntity;

	}
	
	/**
	 * Read a file from resource folder
	 * 
	 * @param iRelativePath
	 *            path relative to {@code resource} folder
	 * @return File
	 */
	public static File getFile(String iRelativePath) {
		
		Objects.requireNonNull(iRelativePath);
		
		if (!iRelativePath.startsWith("/")) {
			iRelativePath = "/" + iRelativePath;
		}
		
		URL resource = ResourceUtil.class.getResource(iRelativePath);
		
		return new File(resource.getPath());
		
	}

	/**
	 * Read an entity stored in JSON format. To generate new entity use:<br/>
	 * {@code createJsonResource(Object iResource, String iResourcePath)}
	 * 
	 * @param iPath
	 *            absolute file system path
	 * @param type
	 * @return type
	 */
	public static <T> T getJsonFile(String iPath, Class<? extends T> type) {

		Objects.requireNonNull(iPath);
		T jsonEntity = null;

		try {
			jsonEntity = new ObjectMapper().readValue(new File(iPath), type);
		} catch (IOException e) {
			Map<String, String> replaceValues = new HashMap<String, String>();
			replaceValues.put("@@PATH@@", iPath);
			throw new ExException(ErrorKeys.JSON_CANNOT_READ, e);
		}
		return jsonEntity;

	}

	/**
	 * Read the file tree starting from a given path
	 * @param iPath path of the main folder
	 * @return Tree of files path
	 */
	public static List<Path> listFiles(String iPath){

		final List<Path> filePathList = new ArrayList<>();

		Path dir = Paths.get(iPath);
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					filePathList.add(file);
					return CONTINUE;
				}

			});
		} catch (IOException e) {
			Map<String, String> replaceValues = new HashMap<String, String>();
			replaceValues.put("@@PATH@@", iPath);
			throw new ExException(ErrorKeys.DIR_CANNOT_LIST,replaceValues,e);
		}
		return filePathList;
	}

	public static InputStream getResource(String iPath){
		Objects.requireNonNull(iPath,"The resource path can not be null");
		return ResourceUtil.class.getResourceAsStream(iPath);
	}

	/**
	 * Read an InformationModelTemplate stored in JSON format under informationmodels
	 * resource folder.
	 * 
	 * @param iModelName
	 *            InformationModelTemplate name
	 * @return
	 */
	public static InformationModelTemplate getInformationModelTemplate(String iModelName) {
		return getJsonResource("/informationmodels/" + iModelName, InformationModelTemplate.class);
	}

	public static void createJsonResource(Object iResource, String iResourcePath) {

		ObjectWriter writer = JSONUtil.getExcludeMapper(new String[] { "id", "version","handler" }).writerWithDefaultPrettyPrinter();

		try {
			writer.writeValue(new File(iResourcePath), iResource);
		} catch (IOException e) {
			Map<String, String> replaceValues = new HashMap<String, String>();
			replaceValues.put("@@PATH@@", iResourcePath);
			throw new ExException(ErrorKeys.JSON_CANNOT_CREATE, e);
		}
	}

}
