package com.exrade.runtime.asset;

import com.exrade.core.ExLogger;
import com.exrade.models.asset.Asset;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.asset.persistence.AssetQuery;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.filemanagement.IFileManager;

import java.util.List;

public class AssetManager {

	private PersistentManager persistenceManager = new PersistentManager();

	private IFileManager fileManager = new FileManager();

	public Asset createAsset(Asset iAsset) {
		iAsset = persistenceManager.create(iAsset);
		fileManager.updateFileMetadata(iAsset);
		ExLogger.get().info("Created asset: {} - {}", iAsset.getUuid(), iAsset.getName());

		return iAsset;
	}

	public Asset getAsset(String iAssetUUID) {
		return persistenceManager.readObjectByUUID(Asset.class, iAssetUUID);
	}

	public List<Asset> listAssets(QueryFilters iFilters){
		return persistenceManager.listObjects(new AssetQuery(), iFilters);
	}

	public Asset updateAsset(Asset iAsset) {
		iAsset = persistenceManager.update(iAsset);
		fileManager.updateFileMetadata(iAsset);
		ExLogger.get().info("Updated asset: {} - {}", iAsset.getUuid(), iAsset.getName());

		return iAsset;
	}

	public void deleteAsset(String iAssetUUID) {
		persistenceManager.delete(getAsset(iAssetUUID));
		ExLogger.get().info("Deleted asset: {}", iAssetUUID);
	}

}
