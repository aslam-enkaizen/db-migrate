package com.exrade.runtime.kyc.qii;

import com.exrade.core.ExLogger;
import com.exrade.models.kyc.qii.*;
import com.exrade.platform.exception.ExException;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.JSONUtil;
import com.exrade.util.RESTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class QiiApiClient {
	private static final Logger logger = ExLogger.get();

	private static String AUTH_TOKEN;

	private static Date AUTH_TOKEN_EXPIRY_DATE;

	private static String AUTH_URL;

	private static String BASE_URL;

	private static String CLIENT_ID;

	private static String CLIENT_SECRET;

	private static boolean TEST_FLOW;

	private static final QiiApiClient INSTANCE = new QiiApiClient();

	public static abstract class Constants {
		private static final String ACCESS_TOKEN 		= "access_token";
		private static final String AUTH_URL 			= "qii.api.authurl";
		private static final String AUTHORIZATION 		= "Authorization";
		private static final String CLIENT_CREDENTIALS 	= "client_credentials";
		private static final String CLIENT_ID 			= "qii.api.id";
		private static final String CLIENT_SECRET 		= "qii.api.key";
		private static final String EXPIRES_IN			= "expires_in";
		private static final String GRANT_TYPE 			= "grant_type";
		private static final String BASE_URL 			= "qii.api.baseurl";
		private static final String TEST_FLOW 			= "qii.api.testflow";
	}

	public static abstract class EndPoints {
		private static final String HOUSE_HOLDS			= "Households";
		private static final String REQUEST_MANAGEMENTS = "RequestManagements";
		private static final String MEMBERS = "Members";
		private static final String MEMBER_INCOME_EMPLOYERS = "MemberIncomeEmployers";
		private static final String MEMBER_INCOME_EMPLOYER_MONTHS = "MemberIncomeEmployerMonths";
		private static final String MEMBER_INCOME_YEARS = "MemberIncomeYears";
		private static final String MEMBER_DOCUMENTS = "MemberDocuments";
		private static final String MEMBER_VERIFIED_IDENTIFICATION_DOCUMENT = "MemberVerifiedIdentificationDocument";
		private static final String MEMBER_ADDRESS_HISTORY = "MemberAddressHistory";
		private static final String MEMBER_VERIFIED_ASSET = "MemberVerifiedAsset";
		private static final String MEMBER_PROFILES = "MemberProfiles";
		private static final String MEMBER_OWN_STATEMENTS = "MemberOwnStatements";
	}

	private QiiApiClient() {
		AUTH_URL = ExConfiguration.getStringProperty(Constants.AUTH_URL);
		BASE_URL = ExConfiguration.getStringProperty(Constants.BASE_URL);
		CLIENT_ID = ExConfiguration.getStringProperty(Constants.CLIENT_ID);
		CLIENT_SECRET = ExConfiguration.getStringProperty(Constants.CLIENT_SECRET);
		TEST_FLOW = ExConfiguration.getPropertyAsBoolean(Constants.TEST_FLOW);
	}

	public static QiiApiClient getInstance() {
		return INSTANCE;
	}

	public String getAccessToken() {
		if(!existValidToken())
			authenticate();

		return AUTH_TOKEN;
	}

	public void createRequestManagement(String displayName, String linkName, String redirectUrl) {
		Map<String, Object> bodyParameters = new HashMap<>();
		bodyParameters.put("name", displayName);
		bodyParameters.put("linkName", linkName);
		bodyParameters.put("activateLink", true);
		bodyParameters.put("redirect", redirectUrl);
		bodyParameters.put("testflow", TEST_FLOW);


		RESTUtil.doRestPOST(
				String.format("%s/%s", BASE_URL, EndPoints.REQUEST_MANAGEMENTS),
				getAuthorizationHeader(),
				JSONUtil.toJsonNode(bodyParameters));
	}

	public String getRequestManagementIdFromLinkName(String linkName) {
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("$filter", String.format("linkName eq '%s'", linkName));
		JsonNode response = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.REQUEST_MANAGEMENTS),
				getAuthorizationHeader(),
				queryParameters);

		String requestManagementId = null;

		if(response.isArray() && response.size() > 0) {
			requestManagementId = String.format("%d", response.get(0).path("id").asInt());
		}

		return requestManagementId;
	}

	public String getRequestManagementIdFromHouseholdId(String householdId) {
		JsonNode response = RESTUtil.doRestGET(
				String.format("%s/%s/%s", BASE_URL, EndPoints.HOUSE_HOLDS, householdId),
				getAuthorizationHeader(),
				null);

		String requestManagementId = String.format("%d", response.path("requestManagementId").asInt());

		return requestManagementId;
	}

	public QiiRequest getRequestManagement(String requestManagementId) {
		JsonNode response = RESTUtil.doRestGET(
				String.format("%s/%s/%s", BASE_URL, EndPoints.REQUEST_MANAGEMENTS, requestManagementId),
				getAuthorizationHeader(),
				null);

		return JSONUtil.deserialize(response.toString(), QiiRequest.class);
	}

	public List<QiiMember> getMembersOfHousehold(String householdId) {
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("$filter", String.format("householdId eq %s", householdId));
		JsonNode membersResponseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBERS),
				getAuthorizationHeader(),
				queryParameters);

		List<QiiMember> members = new ArrayList<QiiMember>();
		if(membersResponseJson.isArray()) {
			for(JsonNode memberJson : membersResponseJson) {
				QiiMember member = new QiiMember();
				int memberId = memberJson.path("id").asInt();
				member.setMemberId(memberId);

				member.setAddress(buildAddress(memberJson));
				member.setDateOfBirth(memberJson.path("dateOfBirth").asText(null));

				member.setFirstName(memberJson.path("firstName").asText(null));

				if(memberJson.path("gender").isInt())
					member.setGender(QiiGender.fromValue(memberJson.path("gender").asInt()));
				else if(memberJson.path("genderEnum").isInt())
					member.setGender(QiiGender.fromValue(memberJson.path("genderEnum").asInt()));
				else
					member.setGender(QiiGender.Unknown);

				member.setIncome(memberJson.path("income").asDouble());

				member.setInitials(memberJson.path("initials").asText(null));
				member.setLastName(memberJson.path("lastName").asText(null));
				member.setLinks(JSONUtil.deserializeMap(memberJson.path("links").toString(), String.class, String.class));
				member.setMaritalStatus(QiiMaritialStatus.fromValue(memberJson.path("maritalStatus").asInt()));
				member.setNumberOfVerifiedChildren(memberJson.path("numberOfVerifiedChildren").asInt());
				member.setPhoneCode(memberJson.path("phoneCode").asText(null));
				member.setPhoneNumber(memberJson.path("phoneNumber").asText(null));
				member.setPlaceOfBirth(memberJson.path("placeOfBirth").asText(null));
				member.setType(QiiMemberType.fromValue(memberJson.path("type").asInt()));

				JsonNode memberProfileResponseJson = getMemberProfile(memberId);
				if(memberProfileResponseJson != null) {
					member.setEmail(memberProfileResponseJson.path("email").asText(null));
					member.setEquity(memberProfileResponseJson.path("equity").asInt());
					member.setIncomeType(QiiIncomeType.fromValue(memberProfileResponseJson.path("incomeType").asInt()));
				}

				queryParameters = buildQueryParams(memberId, householdId);
				member.setVerifiedIdentityDocuments(getVerifiedIdentityDocuments(queryParameters));
				member.setAddresses(getAddressHisoty(queryParameters));
				member.setStatements(getOwnStatements(queryParameters));
				member.setIncomeHistory(getIncomeHistory(queryParameters));

				members.add(member);
			}
		}

		return members;
	}

	public JsonNode getMemberProfile(int memberId) {
		try {
			JsonNode memberProfileResponseJson = RESTUtil.doRestGET(String.format("%s/%s/%d", BASE_URL, EndPoints.MEMBER_PROFILES, memberId),
					getAuthorizationHeader(),
					null);
			return memberProfileResponseJson;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	public List<QiiVerifiedIdentityDocument> getVerifiedIdentityDocuments(Map<String, String> queryParameters){
		List<QiiVerifiedIdentityDocument> verifiedIdentityDocuments = new ArrayList<QiiVerifiedIdentityDocument>();

		JsonNode memberIdentityDocumentsResponseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBER_VERIFIED_IDENTIFICATION_DOCUMENT),
				getAuthorizationHeader(),
				queryParameters);

		if(memberIdentityDocumentsResponseJson.isArray()) {
			for(JsonNode memberIdentityDocumentJson : memberIdentityDocumentsResponseJson) {
				QiiVerifiedIdentityDocument document = new QiiVerifiedIdentityDocument();
				document.setDocumentNumber(memberIdentityDocumentJson.path("documentNumber").asText(null));
				document.setExpirationDate(memberIdentityDocumentJson.path("expirationDate").asText(null));
				document.setType(QiiDocumentType.fromValue(memberIdentityDocumentJson.path("type").asInt()));
				verifiedIdentityDocuments.add(document);
			}
		}

		return verifiedIdentityDocuments;
	}

	public List<QiiAddress> getAddressHisoty(Map<String, String> queryParameters){
		List<QiiAddress> addressHistory = new ArrayList<QiiAddress>();

		try {
			JsonNode responseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBER_ADDRESS_HISTORY),
					getAuthorizationHeader(),
					queryParameters);

			if(responseJson.isArray()) {
				for(JsonNode addressJson : responseJson) {
					QiiAddress address = buildAddress(addressJson);
					if(address != null)
						addressHistory.add(address);
				}
			}
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return addressHistory;
	}

	public List<QiiStatement> getOwnStatements(Map<String, String> queryParameters){
		List<QiiStatement> ownStatements = new ArrayList<QiiStatement>();

		try {
			JsonNode responseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBER_OWN_STATEMENTS),
					getAuthorizationHeader(),
					queryParameters);

			if(responseJson.isArray()) {
				for(JsonNode statementJson : responseJson) {
					QiiStatement statement = new QiiStatement();
					statement.setAnswer(statementJson.path("answer").asText(null));
					statement.setQuestion(statementJson.path("question").asText(null));
					ownStatements.add(statement);
				}
			}
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return ownStatements;
	}

	public QiiIncome getIncomeHistory(Map<String, String> queryParameters) {
		try {
			QiiIncome income = new QiiIncome();

			income.setIncomeEmployerList(getIncomeEmployers(queryParameters));
			income.setIncomeEmployerMonthList(getIncomeEmployerMonths(queryParameters));
			income.setIncomeYearList(getIncomeYears(queryParameters));

			return income;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	public List<QiiEmployerHistoryYear> getEmployerHistoryYears(JsonNode responseJson) {
		List<QiiEmployerHistoryYear> employerHistoryYearList = new ArrayList<>();
		try {
			if(responseJson != null && responseJson.isArray()) {
				for(JsonNode employerHistoryYearJson : responseJson) {
					QiiEmployerHistoryYear employerHistoryYear = buildEmployerHistoryYear(employerHistoryYearJson);
					if(employerHistoryYear != null)
						employerHistoryYearList.add(employerHistoryYear);
				}
			}

		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return employerHistoryYearList;
	}

	public List<QiiIncomeEmployer> getIncomeEmployers(Map<String, String> queryParameters) {
		JsonNode responseJson = null;

		try {
			responseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBER_INCOME_EMPLOYERS),
					getAuthorizationHeader(),
					queryParameters);
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return getIncomeEmployers(responseJson);
	}

	public List<QiiIncomeEmployer> getIncomeEmployers(JsonNode responseJson) {
		List<QiiIncomeEmployer> incomeEmployerList = new ArrayList<>();

		try {
			if(responseJson != null && responseJson.isArray()) {
				for(JsonNode incomeEmployerJson : responseJson) {
					QiiIncomeEmployer incomeEmployer = buildIncomeEmployer(incomeEmployerJson);
					if(incomeEmployer != null)
						incomeEmployerList.add(incomeEmployer);
				}
			}

		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return incomeEmployerList;
	}

	public List<QiiIncomeEmployerMonth> getIncomeEmployerMonths(Map<String, String> queryParameters) {
		JsonNode responseJson = null;

		try {
			responseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBER_INCOME_EMPLOYER_MONTHS),
					getAuthorizationHeader(),
					queryParameters);
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return getIncomeEmployerMonths(responseJson);
	}

	public List<QiiIncomeEmployerMonth> getIncomeEmployerMonths(JsonNode responseJson) {
		List<QiiIncomeEmployerMonth> incomeEmployerMonthList = new ArrayList<>();

		try {
			if(responseJson != null && responseJson.isArray()) {
				for(JsonNode incomeEmployerMonthJson : responseJson) {
					QiiIncomeEmployerMonth incomeEmployerMonth = buildIncomeEmployerMonth(incomeEmployerMonthJson);
					if(incomeEmployerMonth != null)
						incomeEmployerMonthList.add(incomeEmployerMonth);
				}
			}

		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return incomeEmployerMonthList;
	}

	public List<QiiIncomeYear> getIncomeYears(Map<String, String> queryParameters) {
		JsonNode responseJson = null;

		try {
			responseJson = RESTUtil.doRestGET(String.format("%s/%s", BASE_URL, EndPoints.MEMBER_INCOME_YEARS),
					getAuthorizationHeader(),
					queryParameters);
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return getIncomeYears(responseJson);
	}

	public List<QiiIncomeYear> getIncomeYears(JsonNode responseJson) {
		List<QiiIncomeYear> incomeYearList = new ArrayList<>();

		try {
			if(responseJson != null && responseJson.isArray()) {
				for(JsonNode incomeYearJson : responseJson) {
					QiiIncomeYear incomeYear = buildIncomeYear(incomeYearJson);
					if(incomeYear != null)
						incomeYearList.add(incomeYear);
				}
			}
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
		}

		return incomeYearList;
	}

	private void authenticate() {
		logger.debug("Authenticating with Qii");

		String base64EncodedCredential;

		try {
			base64EncodedCredential = getBase64EncodedCredential();
		} catch (UnsupportedEncodingException e) {
			throw new ExException("Invalid Qii client credentials");
		}
		//todo commented

//		final WSRequestHolder requestHolder = WS.url(AUTH_URL).setContentType("application/x-www-form-urlencoded");
//		requestHolder.setHeader(Constants.AUTHORIZATION, String.format("Basic %s", base64EncodedCredential));
//		final Promise<WSResponse> responsePromise = requestHolder.post(
//														String.format("%s=%s", Constants.GRANT_TYPE, Constants.CLIENT_CREDENTIALS));
//
//		Promise<JsonNode> jsonPromise = responsePromise.map(
//		    new Function<WSResponse, JsonNode>() {
//		        public JsonNode apply(WSResponse response) {
//		        	if(response.getStatus() == 200) {
//		        		JsonNode json = response.asJson();
//		        		return json;
//		        	}
//		        	else {
//		        		throw new ExAuthenticationException(String.format("%d: %s", response.getStatus(), response.getStatusText()));
//		        	}
//		        }
//		    }
//		);
//
//		final JsonNode authResponse = jsonPromise.get(PlayAuthenticate.TIMEOUT);
//		logger.debug(JSONUtil.toJson(authResponse));
//
//		AUTH_TOKEN = authResponse.path(Constants.ACCESS_TOKEN).asText();
//		AUTH_TOKEN_EXPIRY_DATE = DateUtil.addWithCurrentDate(Calendar.SECOND, authResponse.path(Constants.EXPIRES_IN).asInt(), true);

		logger.debug("Authenticated with Qii");
	}

	private String getBase64EncodedCredential() throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(String.format("%s:%s", CLIENT_ID, CLIENT_SECRET).getBytes("UTF-8"));
	}

	private boolean existValidToken() {
		if(Strings.isNullOrEmpty(AUTH_TOKEN) || AUTH_TOKEN_EXPIRY_DATE == null || AUTH_TOKEN_EXPIRY_DATE.before(TimeProvider.now()))
			return false;

		return true;
	}

	private Map<String, String> getAuthorizationHeader(){
		Map<String,String> headerParameters = new HashMap<>();
		headerParameters.put(Constants.AUTHORIZATION, String.format("Bearer %s", QiiApiClient.getInstance().getAccessToken()));

		return headerParameters;
	}

	private QiiAddress buildAddress(JsonNode jsonNode) {
		try {
			QiiAddress address = new QiiAddress();

			address.setActualityType(QiiActualityType.fromValue(jsonNode.path("actualityType").asInt()));
			address.setCity(jsonNode.path("city").asText(null));
			address.setCountry(jsonNode.path("country").asText("Netherlands"));

			if(!jsonNode.path("function2").isMissingNode())
				address.setFunction(jsonNode.path("function2").asText(null));
			else
				address.setFunction(jsonNode.path("function").asText(null));

			address.setHouseNumber(jsonNode.path("houseNumber").asText(null));
			address.setMunicipality(jsonNode.path("municipality").asText(null));
			address.setPostalCode(jsonNode.path("postalCode").asText(null));
			address.setStartDate(jsonNode.path("startDate").asText(null));
			address.setStreet(jsonNode.path("street").asText(null));
			address.setSuffix(jsonNode.path("suffix").asText(null));

			return address;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	private QiiEmployerHistoryYear buildEmployerHistoryYear(JsonNode jsonNode) {
		try {
			QiiEmployerHistoryYear employerHistoryYear = new QiiEmployerHistoryYear();

			employerHistoryYear.setEmployer(jsonNode.path("employer").asText(null));
			employerHistoryYear.setEmployerHistoryYearId(jsonNode.path("id").asInt());
			employerHistoryYear.setHours(jsonNode.path("hours").asInt());
			employerHistoryYear.setYear(jsonNode.path("year").asInt());

			return employerHistoryYear;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	private QiiIncomeEmployer buildIncomeEmployer(JsonNode jsonNode) {
		try {
			QiiIncomeEmployer incomeEmployer = new QiiIncomeEmployer();

			incomeEmployer.setEmployer(jsonNode.path("employer").asText(null));
			incomeEmployer.setEndDate(jsonNode.path("endDate").asText(null));
			incomeEmployer.setIncomeEmployerId(jsonNode.path("id").asInt());
			incomeEmployer.setPartTimePercentage(jsonNode.path("partTimePercentage").asInt());
			incomeEmployer.setPayPeriod(QiiPayPeriod.fromValue(jsonNode.path("payPeriod").asInt()));
			incomeEmployer.setStartDate(jsonNode.path("startDate").asText(null));
			incomeEmployer.setType(QiiIncomeType.fromValue(jsonNode.path("type").asInt()));

			return incomeEmployer;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	private QiiIncomeEmployerMonth buildIncomeEmployerMonth(JsonNode jsonNode) {
		try {
			QiiIncomeEmployerMonth incomeEmployerMonth = new QiiIncomeEmployerMonth();

			incomeEmployerMonth.setEndDate(jsonNode.path("endDate").asText(null));
			incomeEmployerMonth.setHours(jsonNode.path("hours").asInt());
			incomeEmployerMonth.setIncome(jsonNode.path("income").asDouble());
			incomeEmployerMonth.setIncome2(jsonNode.path("income2").asDouble());
			incomeEmployerMonth.setIncomeEmployerMonthId(jsonNode.path("id").asInt());
			incomeEmployerMonth.setStartDate(jsonNode.path("startDate").asText(null));

			return incomeEmployerMonth;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	private QiiIncomeYear buildIncomeYear(JsonNode jsonNode) {
		try {
			QiiIncomeYear incomeYear = new QiiIncomeYear();

			incomeYear.setIncomeYearId(jsonNode.path("id").asInt());
			incomeYear.setValue(jsonNode.path("value").asDouble());
			incomeYear.setYear(jsonNode.path("year").asInt());

			return incomeYear;
		}
		catch(Exception ex) {
			ExLogger.get().warn("", ex);
			return null;
		}
	}

	private Map<String, String> buildQueryParams(int memberId, String householdId) {
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("$filter", String.format("memberId eq %d and householdId eq %s", memberId, householdId));

		return queryParameters;
	}
}
