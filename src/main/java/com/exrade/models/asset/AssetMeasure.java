package com.exrade.models.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AssetMeasure {
	@JsonIgnore
	private Asset asset;
	
	private Measure measure;

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Measure getMeasure() {
		return measure;
	}

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}
	
	public String getAssetUuid() {
		if(getAsset() != null)
			getAsset().getUuid();
		
		return null;
	}
}
