package com.exrade.models.asset;

import com.exrade.models.i18n.ExLang;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.informationmodel.IInformationModel;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import javax.persistence.OneToMany;
import java.util.*;


public class AssetSchema extends BaseEntityUUIDTimeStampable implements IInformationModel {

	private String name;

	private String language = ExLang.ENGLISH.getCode();

	private List<String> supportedLanguages = new ArrayList<String>(); //other languages that translated labels are available for

	private Map<String,String> titleTranslations = new HashMap<String, String>();

	private Map<String,String> descriptionTranslations = new HashMap<String, String>();

	private Membership authorMembership;

	private String category;

	@OneToMany(orphanRemoval = true)
	private List<Attribute> items = new ArrayList<>();

	private Set<String> tags = new HashSet<String>();

	private String template;

	private boolean active = true;

	private String blockchainTx;

	public AssetSchema(){}

	public static AssetSchema newInstance(String iName,String iTitle,String iDescription){
		AssetSchema informationTemplate = new AssetSchema();
		informationTemplate.setName(iName);
		informationTemplate.setDescription(iDescription);
		informationTemplate.setLanguage(ExLang.ENGLISH.getCode()); //Default set to english
		informationTemplate.setTitle(iTitle);

		return informationTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return getDescriptionTranslations().get(getLanguage());
	}

	public void setDescription(String description) {
		getDescriptionTranslations().put(getLanguage(), description);
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
		addSupportedLanguage(language);
	}

	public String getTitle() {
		return getTitleTranslations().get(getLanguage());
	}

	public void setTitle(String title) {
		getTitleTranslations().put(getLanguage(), title);
	}

	public List<String> getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(List<String> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public void addSupportedLanguage(String langCode){
		if(!getSupportedLanguages().contains(langCode))
			getSupportedLanguages().add(langCode);
	}

	public Map<String,String> getTitleTranslations() {
		return titleTranslations;
	}

	public void setTitleTranslations(Map<String,String> nameTranslations) {
		this.titleTranslations = nameTranslations;
	}

	public Map<String,String> getDescriptionTranslations() {
		return descriptionTranslations;
	}

	public void setDescriptionTranslations(Map<String,String> descriptionTranslations) {
		this.descriptionTranslations = descriptionTranslations;
	}

	public Membership getAuthorMembership() {
		return authorMembership;
	}

	public void setAuthorMembership(Membership authorMembership) {
		this.authorMembership = authorMembership;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getAuthorMembershipUUID() {
		return getAuthorMembership() != null ? getAuthorMembership().getIdentifier() : null;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Attribute> getItems() {
		return items;
	}

	public void setItems(List<Attribute> iItems) {
		items = iItems;
	}

	public void addItem(Attribute item){
		getItems().add(item);
	}

	public void removeItem(Attribute item) {
		getItems().remove(item);
	}

	public Attribute getItem(int index) {
		return getItems().get(index);
	}

	public String getBlockchainTx() {
		return blockchainTx;
	}

	public void setBlockchainTx(String blockchainTx) {
		this.blockchainTx = blockchainTx;
	}

	@Override
	public String getModelData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setModelData(String jsonDataString) {
		// TODO Auto-generated method stub
		
	}
}
