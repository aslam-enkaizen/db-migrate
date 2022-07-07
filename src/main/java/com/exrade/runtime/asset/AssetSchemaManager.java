package com.exrade.runtime.asset;

import com.exrade.core.ExLogger;
import com.exrade.models.asset.AssetSchema;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.asset.persistence.AssetSchemaQuery;

import java.util.List;


public class AssetSchemaManager {

	private final PersistentManager persistenceManager;

	public AssetSchemaManager() {
		this(new PersistentManager());
	}

	public AssetSchemaManager(PersistentManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	public AssetSchema createAssetSchema(AssetSchema assetSchema) {
		assetSchema = persistenceManager.create(assetSchema);
		ExLogger.get().info("Created AssetSchema: {} - {}", assetSchema.getUuid(), assetSchema.getName());
		return assetSchema;
	}

	public AssetSchema getAssetSchema(String iAssetSchemaUUID) {
		return persistenceManager.readObjectByUUID(AssetSchema.class, iAssetSchemaUUID);
	}

	public List<AssetSchema> listAssetSchemas(QueryFilters iFilters){
		return persistenceManager.listObjects(new AssetSchemaQuery(), iFilters);
	}

	public AssetSchema updateAssetSchema(AssetSchema iAssetSchema) {
		AssetSchema updatedAssetSchema = persistenceManager.update(iAssetSchema);
		ExLogger.get().info("Updated AssetSchema: {} - {}", updatedAssetSchema.getUuid(), updatedAssetSchema.getName());
		return updatedAssetSchema;
	}

	public void deleteAssetSchema(String iAssetSchemaUUID) {
		persistenceManager.delete(getAssetSchema(iAssetSchemaUUID));
		ExLogger.get().info("Deleted AssetSchema: {}", iAssetSchemaUUID);
	}
}
