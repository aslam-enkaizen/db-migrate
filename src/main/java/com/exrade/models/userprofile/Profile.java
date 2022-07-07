package com.exrade.models.userprofile;

import com.exrade.controllers.userprofile.Profiles;
import com.exrade.core.FieldsAvailable;
import com.exrade.core.FieldsSerializable;
import com.exrade.models.Permission;
import com.exrade.models.Role;
import com.exrade.models.Subject;
import com.exrade.models.common.Image;
import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.userprofile.security.ProfileStatus;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class Profile extends BaseEntityUUID implements IProfile, Subject, PlanSubscriber, FieldsSerializable {

    private static Set<ProfileStatus> activeStatuses = EnumSet.of(ProfileStatus.ACTIVE, ProfileStatus.PAYMENT_PENDING);
    protected String description;
    protected String address;
    protected String postcode;
    protected String city;
    protected String phone;
    protected String country;
    protected String website;
    protected ProfileStatus profileStatus = ProfileStatus.ACTIVE;
    protected String interests;
    protected String twitter;
    protected String facebook;
    protected String linkedin;
    //@OneToMany(orphanRemoval = true)
    protected List<IPaymentMethod> paymentMethods = new ArrayList<IPaymentMethod>();
    /**
     * Set of roles allowed to this profile
     */
    //@OneToMany(orphanRemoval = true)
    protected List<Role> profileRoles = new ArrayList<>();
    protected boolean publicProfile = true;
    protected PlanSubscription planSubscription;
    private List<String> files = new ArrayList<>();
    private List<Image> images = new ArrayList<>();
    private String name; // businessName
    private String logo;
    private String vat;
    private String nace;
    private String competences;
    private boolean domainVerified;
    private boolean identityVerified;
    private String legalEmail;
    private String subdomain;
    private String agreementTemplate;
    private String video;
    private String walletAddress;
    private Map<String, Object> customFields = new HashMap<>();
    private boolean businessProfile;

    public Profile() {
    }

    @Override
    public List<? extends Permission> getPermissions() {
        if (getPlanSubscription() != null && getPlanSubscription().getPlan() != null)
            return Collections.unmodifiableList(getPlanSubscription().getPlan().getPermissions());
        return Arrays.asList();
    }

    @Override
    public List<Role> getRoles() {
        //List<Role> roles = new ArrayList<>(getPlanSubscription().getPlan().getRoles());
        //roles.addAll(getProfileRoles());
        return Collections.unmodifiableList(getProfileRoles());
    }

//	public void setUuid(String uuid) {
//		this.uuid = uuid;
//	}

    @Override
    public String getIdentifier() {
        return getUuid();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String iWebsite) {
        this.website = iWebsite;
    }

    public boolean isPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(boolean iPublicProfile) {
        this.publicProfile = iPublicProfile;
    }

    public PlanSubscription getPlanSubscription() {
        return planSubscription;
    }

    public void setPlanSubscription(PlanSubscription planSubscription) {

        this.planSubscription = planSubscription;
    }

    public String getPlanSubscriptionUUID() {
        return getPlanSubscription() != null ? getPlanSubscription().getUuid() : null;
    }

    public List<Role> getProfileRoles() {
        return profileRoles;
    }

    public void setProfileRoles(List<Role> profileRoles) {
        this.profileRoles = profileRoles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    @JsonIgnore
    public FieldsAvailable getFieldsSerializable() {
        return Profiles.PROFILE_FIELDS;
    }

    public ProfileStatus getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(ProfileStatus profileStatus) {
        this.profileStatus = profileStatus;
    }

    public boolean isActive() {
        return activeStatuses.contains(getProfileStatus());
    }

    public List<IPaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<IPaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
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

    @Override
    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    public String getCompetences() {
        return competences;
    }

    public void setCompetences(String competences) {
        this.competences = competences;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getNace() {
        return nace;
    }

    public void setNace(String nace) {
        this.nace = nace;
    }

    public boolean isDomainVerified() {
        return domainVerified;
    }

    public void setDomainVerified(boolean domainVerified) {
        this.domainVerified = domainVerified;
    }

    public boolean isIdentityVerified() {
        return identityVerified;
    }

    public void setIdentityVerified(boolean identityVerified) {
        this.identityVerified = identityVerified;
    }

    public String getLegalEmail() {
        return legalEmail;
    }

    public void setLegalEmail(String legalEmail) {
        this.legalEmail = legalEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAgreementTemplate() {
        return agreementTemplate;
    }

    public void setAgreementTemplate(String agreementTemplate) {
        this.agreementTemplate = agreementTemplate;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public boolean isBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(boolean businessProfile) {
        this.businessProfile = businessProfile;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", postcode='" + postcode + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", website='" + website + '\'' +
                ", profileStatus=" + profileStatus +
                ", interests='" + interests + '\'' +
                ", twitter='" + twitter + '\'' +
                ", facebook='" + facebook + '\'' +
                ", linkedin='" + linkedin + '\'' +
                ", files=" + files +
                ", images=" + images +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", vat='" + vat + '\'' +
                ", nace='" + nace + '\'' +
                ", competences='" + competences + '\'' +
                ", domainVerified=" + domainVerified +
                ", identityVerified=" + identityVerified +
                ", legalEmail='" + legalEmail + '\'' +
                ", subdomain='" + subdomain + '\'' +
                ", agreementTemplate='" + agreementTemplate + '\'' +
                ", video='" + video + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", customFields=" + customFields +
                ", businessProfile=" + businessProfile +
                ", paymentMethods=" + paymentMethods +
                ", profileRoles=" + profileRoles +
                ", publicProfile=" + publicProfile +
                ", planSubscription=" + planSubscription +
                ", uuid='" + uuid + '\'' +
                ", id='" + id + '\'' +
                ", version=" + version +
                '}';
    }
}
