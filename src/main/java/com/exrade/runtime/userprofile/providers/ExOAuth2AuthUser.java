package com.exrade.runtime.userprofile.providers;

import com.exrade.providers.oauth2.BasicOAuth2AuthUser;
import com.exrade.providers.oauth2.OAuth2AuthInfo;
import com.exrade.user.BasicIdentity;
import com.exrade.user.FirstLastNameIdentity;
import com.exrade.user.PicturedIdentity;

public abstract class ExOAuth2AuthUser extends BasicOAuth2AuthUser implements
        BasicIdentity, FirstLastNameIdentity, PicturedIdentity, ExLocalIdentity,ExSocialIdentity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected String picture;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String timezone;
    protected String language;
    protected String phone;
    protected String accessToken;
    
    protected String description;
    protected String address;
    protected String city;
    protected String interests;
    protected String country;
    protected String website;
    protected String profileLink;
    
    protected String planName;
    protected String profileUUID;
    
    protected String businessName;
    protected String vat;
    protected String postcode;
    
    
    public ExOAuth2AuthUser(String id, OAuth2AuthInfo info, String state) {
        super(id, info, state);
    }
    
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getPicture() {
        return picture;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }
    
    public String getLanguage(){
        return language;    
    }
    
    @Override
    public String getPhone(){
        return phone;
    }

    public String getAddress() {
        return address;
    }
    
    public String getCountry() {
        return country;
    }
    
    public String getAccessToken() {
        return accessToken;
    }

    public String getDescription() {
        return description;
    }

    public String getInterests() {
        return interests;
    }

    public String getWebsite() {
        return website;
    }
    
    public String getCity() {
        return city;
    }

    @Override
    public String getProfileLink() {
        return profileLink;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public String getProfileUUID() {
        return profileUUID;
    }
    
    @Override
	public String getBusinessName() {
		return businessName;
	}

	@Override
	public String getVat() {
		return vat;
	}
	
	@Override
	public String getPostcode() {
		return postcode;
	}
}
