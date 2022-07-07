package com.exrade.models.userprofile;

import com.exrade.models.Permission;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.runtime.timer.TimeProvider;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Plan extends BaseEntityUUID implements IPlan {

	private String name;

	private String title;

	private String description;

	/** Id of payment provider offer for this plan */
	private String offerID;

	/** Give a qualitative measure of features and resources offered by the plan */
	private Integer grade = 12; // default plan is higher than Pro but lower than Corporate

	/** Set of permissions allowed by this plan */
	private List<Permission> permissions = new ArrayList<>();

	private boolean uponRequest = false;

	private boolean active = true;

	private boolean defaultPlan = false;

	private Date expirationDate;

	private String couponCode;

	private Integer trialPeriodDays = 15;

	private BigDecimal amount;

	private String currency = "EUR";

	private Long paymentInterval;

	private IntervalUnit paymentIntervalUnit;

	private Integer maxAllowedSubscriptions = -1;

	private Date creationDate = TimeProvider.now();

	private Date updateDate;

	private Set<String> tags = new HashSet<String>();

	public static Plan createPlan(String iName, String iTitle, String iOfferID, boolean iUponRequest, Integer iGrade){
		Plan plan = new Plan();
		plan.name = iName;
		plan.grade = iGrade;
		plan.uponRequest = iUponRequest;
		return plan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/*public List<? extends Role> getRoles() {
		return roles;
	}

	public void addRole(Role role) {
		roles.add(role);
	}

	public void removeRole(Role role) {
		roles.remove(role);
	}*/

	public List<? extends Permission> getPermissions() {
		return permissions;
	}

	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

	public void removePermission(Permission permission) {
		permissions.remove(permission);
	}

	public Permission getPermission(String iValue){
		for (Permission permission : getPermissions()) {
			if (permission.getValue().equals(iValue)){
				return permission;
			}
		}
		return null;
	}

	public boolean isBusiness(){
		return true;
	}

	public boolean isFree(){
		return  !isUponRequest() && (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) == 0 || Strings.isNullOrEmpty(getOfferID()));
	}

	public boolean isUponRequest() {
		return uponRequest;
	}

	public void setUponRequest(boolean iUponRequest) {
		this.uponRequest = iUponRequest;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOfferID() {
		return offerID;
	}

	public void setOfferID(String offerID) {
		this.offerID = offerID;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Integer getTrialPeriodDays() {
		return trialPeriodDays;
	}

	public void setTrialPeriodDays(Integer trialPeriodDays) {
		this.trialPeriodDays = trialPeriodDays;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		amount.setScale(2, RoundingMode.HALF_UP);
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getPaymentInterval() {
		return paymentInterval;
	}

	public void setPaymentInterval(Long paymentInterval) {
		this.paymentInterval = paymentInterval;
	}

	public IntervalUnit getPaymentIntervalUnit() {
		return paymentIntervalUnit;
	}

	public void setPaymentIntervalUnit(IntervalUnit paymentIntervalUnit) {
		this.paymentIntervalUnit = paymentIntervalUnit;
	}

	public Integer getMaxAllowedSubscriptions() {
		return maxAllowedSubscriptions;
	}

	public void setMaxAllowedSubscriptions(Integer maxAllowedSubscriptions) {
		this.maxAllowedSubscriptions = maxAllowedSubscriptions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isDefaultPlan() {
		return defaultPlan;
	}

	public void setDefaultPlan(boolean defaultPlan) {
		this.defaultPlan = defaultPlan;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

}
