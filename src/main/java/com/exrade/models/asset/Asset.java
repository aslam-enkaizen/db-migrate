package com.exrade.models.asset;

import com.exrade.models.common.Image;
import com.exrade.models.userprofile.IProfile;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.util.ExCollections;

import java.util.*;

public class Asset extends BaseEntityUUIDTimeStampable {

	private String name;

	private String category;

	private String type;

	private String status;

	private String description;

	private String serialNumber;

	private String gpsPosition;

	private String externalId;

	private String dataSource;

	private Measure measure;

	private String assetSchemaUUID;

	private List<String> files = new ArrayList<>();

	private List<Image> images = new ArrayList<>();

	private List<String> videos = new ArrayList<>();

	private Negotiator owner;

	private IProfile ownerProfile;

	private Negotiator assignedTo;

	private IProfile assignedToProfile;

	private List<AssetMeasure> fromAssetMeasures = new ArrayList<AssetMeasure>();

	private List<Asset> subAssets = new ArrayList<Asset>();

	private Map<String, Object> fields = new HashMap<>();

	private Map<String, Object> customFields = new HashMap<>();

	private Set<String> tags = new HashSet<String>();

	public Asset() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getGpsPosition() {
		return gpsPosition;
	}

	public void setGpsPosition(String gpsPosition) {
		this.gpsPosition = gpsPosition;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<String> getVideos() {
		return videos;
	}

	public void setVideos(List<String> videos) {
		this.videos = videos;
	}

	public Negotiator getOwner() {
		return owner;
	}

	public void setOwner(Negotiator owner) {
		this.owner = owner;
		if(owner != null)
			this.setOwnerProfile(owner.getProfile());
	}

	public IProfile getOwnerProfile() {
		return ownerProfile;
	}

	public void setOwnerProfile(IProfile ownerProfile) {
		this.ownerProfile = ownerProfile;
	}

	public Negotiator getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Negotiator assignedTo) {
		this.assignedTo = assignedTo;
		if(assignedTo != null)
			this.setAssignedToProfile(assignedTo.getProfile());
	}

	public IProfile getAssignedToProfile() {
		return assignedToProfile;
	}

	public void setAssignedToProfile(IProfile assignedToProfile) {
		this.assignedToProfile = assignedToProfile;
	}

	public List<AssetMeasure> getFromAssetMeasures() {
		return fromAssetMeasures;
	}

	public void setFromAssetMeasures(List<AssetMeasure> fromAssetMeasures) {
		this.fromAssetMeasures = fromAssetMeasures;
	}

	public List<Asset> getSubAssets() {
		return subAssets;
	}

	public void setSubAssets(List<Asset> subAssets) {
		this.subAssets = subAssets;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public List<String> getSubAssetUUIDs() {
		List<String> subAssetUUIDs = new ArrayList<String>();

		if(ExCollections.isNotEmpty(getSubAssets())) {
			for(Asset asset : getSubAssets()) {
				if(asset != null)
					subAssetUUIDs.add(asset.getUuid());
			}
		}

		return subAssetUUIDs;
	}

	public String getAssetSchemaUUID() {
		return assetSchemaUUID;
	}

	public void setAssetSchemaUUID(String assetSchemaUUID) {
		this.assetSchemaUUID = assetSchemaUUID;
	}
}
