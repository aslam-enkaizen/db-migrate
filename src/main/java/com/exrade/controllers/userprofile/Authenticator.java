package com.exrade.controllers.userprofile;

public class Authenticator {

//	private static AuthenticationAPI authenticationManager = new AuthenticationManager();
//
//	private static AccountAPI accountManager = new AccountManagerAdapter();
//
//	public static Result doAuthenticate(String iProvider) {
//		try{
//			AuthProvider authProvider = AuthProvider.valueOf(iProvider.toUpperCase());
//			if (authProvider == AuthProvider.PASSWORD){
//				return doPasswordAuth();
//			}
//			else {
//				return doOauth2Auth(authProvider);
//			}
//		}
//		catch(IllegalArgumentException ex) {
//			throw new ExParamException(ErrorKeys.PARAM_INVALID, "provider");
//		}
//
//	}
//
//	private static Result doOauth2Auth(AuthProvider authProvider) {
//		ExAuthRequest authRequest = new ExAuthRequest();
//		JsonNode jsonobject = request().body().asJson();
//		authRequest.setTimeZone(jsonobject.path("timezone").asText(null));
//		authRequest.setAccessToken(jsonobject.get(Constants.ACCESS_TOKEN).asText());
//		authRequest.setExpiresIn(jsonobject.get(Constants.EXPIRES_IN).asLong());
//		authRequest.setAuthProvider(authProvider);
//		ExAuthResponse authResponse = authenticationManager.doAuthenticate(authRequest);
//		JsonNode json = buildAuthResponse(authResponse);
//		return Controller.ok(json);
//	}
//
//	private static Result doPasswordAuth(){
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		final ExForm<LoginForm> loginForm = (ExForm<LoginForm>) ExUsernamePasswordAuthProvider.LOGIN_FORM
//				.bindFromRequest();
//
//		if (loginForm.hasErrors()) {
//			// User did not fill everything properly
//			return ControllerUtil.httpErrorResponse(loginForm.formErrors());
//		} else {
//			//return ExUsernamePasswordAuthProvider.login(ctx());
//			ExAuthRequest authRequest = new ExAuthRequest();
//			authRequest.setUserIdentifier(loginForm.get().email);
//			authRequest.setPassword(loginForm.get().getPassword());
//			authRequest.setAuthProvider(AuthProvider.PASSWORD);
//			ExAuthResponse authResponse = authenticationManager.doAuthenticate(authRequest);
//			JsonNode json = buildAuthResponse(authResponse);
//			return Controller.ok(json);
//		}
//	}
//
//	/**
//	 * Returns a token object if valid, null if not
//	 *
//	 * @param token
//	 * @param type
//	 * @return
//	 */
//	public static Result verify(final String token) {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		authenticationManager.verify(token);
//
//		return ok();
//	}
//
//	/**
//	 * Returns a token object if valid, null if not
//	 *
//	 * @param token
//	 * @param type
//	 * @return
//	 */
//	public static Result getTokenAction(final String token) {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		TokenAction tokenAction = null;
//		tokenAction = authenticationManager.getTokenAction(token,getQueryString());
//
//		if (tokenAction == null){
//			throw new ExNotFoundException(token);
//		}
//
//		ObjectNode result =  new ObjectMapper().createObjectNode();
//		result.put(TokenActionFields.TOKEN,tokenAction.getToken());
//		result.put(TokenActionFields.TYPE,tokenAction.getType().name());
//		result.put(UserFields.ACCOUNT_STATUS,tokenAction.getTargetUser().getAccountStatus().name());
//		result.put(UserFields.EMAIL ,tokenAction.getTargetUser().getEmail());
//		result.put(UserFields.FIRST_NAME,tokenAction.getTargetUser().getFirstName());
//		result.put(UserFields.LAST_NAME,tokenAction.getTargetUser().getLastName());
//		return ok(result.toString());
//	}
//
//	public static Result doSignup() {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		//JsonNode jsonobject = request().body().asJson();
//
//		final ExForm<SignupForm> signupForm = (ExForm<SignupForm>) ExUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();
//
//		//final Form<SignupForm> signupForm = ExUsernamePasswordAuthProvider.SIGNUP_FORM.bind(jsonobject);
//
//
//		if (signupForm.hasErrors()) {
//			// User did not fill everything properly
//			return ControllerUtil.httpErrorResponse(signupForm.formErrors());
//		} else {
//			//return ExUsernamePasswordAuthProvider.signup(ctx());
//			ExUsernamePasswordAuthUser authUser = ExUsernamePasswordAuthUser.create(signupForm.get());
//			String verificationToken = authenticationManager.doSignup(authUser);
//			ObjectNode result = Json.newObject();
//			result.put(TokenActionFields.TOKEN, verificationToken);
//			return Controller.created(result);
//		}
//	}
//
//	public static Result doSignupNoVerify() {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		final ExForm<SignupForm> signupForm = (ExForm<SignupForm>) ExUsernamePasswordAuthProvider.SIGNUP_FORM
//				.bindFromRequest();
//
//		if (signupForm.hasErrors()) {
//			// User did not fill everything properly
//			return ControllerUtil.httpErrorResponse(signupForm.formErrors());
//		} else {
//			ExUsernamePasswordAuthUser authUser = ExUsernamePasswordAuthUser.create(signupForm.get());
//			ExAuthResponse authResponse = authenticationManager.doSignupNoVerify(authUser);
//			JsonNode json = buildAuthResponse(authResponse);
//			return Controller.ok(json);
//		}
//	}
//
//	public static Result unverified(String iVerificationToken) {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		ObjectNode result = Json.newObject();
//		result.put(TokenActionFields.TOKEN, iVerificationToken);
//		return created(result);
//	}
//
//	private static final ExForm<MyIdentity> FORGOT_PASSWORD_FORM = ExForm.form(MyIdentity.class);
//
//	public static Result doForgotPassword() {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		final ExForm<MyIdentity> filledForm = (ExForm<MyIdentity>) FORGOT_PASSWORD_FORM.bindFromRequest();
//
//		if (filledForm.hasErrors()) {
//			// User did not fill in his/her email
//			return ControllerUtil.httpErrorResponse(filledForm.formErrors());
//		}
//
//		TokenAction tokenAction = accountManager.changePasswordWithToken(filledForm.get().email.toLowerCase());
//		ObjectNode result =  new ObjectMapper().createObjectNode();
//		result.put(TokenActionFields.TOKEN,tokenAction.getToken());
//		result.put(UserFields.EMAIL ,tokenAction.getTargetUser().getEmail());
//		result.put(TokenActionFields.TYPE,tokenAction.getType().name());
//		result.put(UserFields.ACCOUNT_STATUS,tokenAction.getTargetUser().getAccountStatus().name());
//		result.put(UserFields.FIRST_NAME,tokenAction.getTargetUser().getFirstName());
//		result.put(UserFields.LAST_NAME,tokenAction.getTargetUser().getLastName());
//		return ok(result.toString());
//	}
//
//	public static class PasswordForm {
//
//		@MinLength(8)
//		@Required
//		public String password;
//
//		@MinLength(8)
//		@Required
//		public String repeatPassword;
//
//		public Map<String,List<ExValidationError>> validate() {
//
//			Map<String,List<ExValidationError>> errors = new HashMap<>();
//			if (password == null || !password.equals(repeatPassword)) {
//
//				List<ExValidationError> list = new ArrayList<>();
//				list.add(new ExValidationError("passwords_not_same", Messages
//						.get("playauthenticate.password.signup.error.passwords_not_same")));
//
//				errors.put("password",list);
//			}
//
//			if (password.length() < 8){
//				List<ExValidationError> list = new ArrayList<>();
//				if (errors.get("password") != null) {
//					list = errors.get("password");
//				}
//				list.add(new ExValidationError("passwords_too_short", Messages
//						.get("playauthenticate.password.too_short")));
//				errors.put("password",list);
//			}
//
//			return null;
//		}
//	}
//
//	public static class PasswordChange extends PasswordForm {
//		@Required
//		public String oldPassword;
//	}
//
//	private static final ExForm<PasswordChange> PASSWORD_CHANGE_FORM = ExForm.form(PasswordChange.class);
//
//	public static Result doChangePassword() {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		final ExForm<PasswordChange> filledForm = (ExForm<PasswordChange>) PASSWORD_CHANGE_FORM.bindFromRequest();
//		if (filledForm.hasErrors()) {
//			return ControllerUtil.httpErrorResponse(filledForm.formErrors());
//		} else {
//			final String oldPassword = filledForm.get().oldPassword;
//			final String newPassword = filledForm.get().password;
//
//			accountManager.changePassword(getRequestEnvelope(), newPassword, oldPassword);
//
//			return ok();
//		}
//	}
//
//	public static class PasswordReset extends PasswordForm {
//
//		@Required
//		public String token;
//
//		public Map<String,List<ExValidationError>> validate() {
//
//			Map<String,List<ExValidationError>> errors = new HashMap<>();
//			ControllerUtil.toRequiredValidationError(token,"token",errors);
//
//			return errors.isEmpty() ? null : errors;
//		}
//
//
//
//	}
//
//	private static final ExForm<PasswordReset> PASSWORD_RESET_FORM = ExForm.form(PasswordReset.class);
//
//	public static Result doResetPassword() {
//		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
//		final ExForm<PasswordReset> filledForm = (ExForm<PasswordReset>) PASSWORD_RESET_FORM
//				.bindFromRequest();
//		if (filledForm.hasErrors()) {
//			return ControllerUtil.httpErrorResponse(filledForm.formErrors());
//		}
//
//		final String token = filledForm.get().token;
//		final String newPassword = filledForm.get().password;
//
//		accountManager.resetPassword(getRequestEnvelope(), token, newPassword);
//
//		return ok();
//	}
//
//	public static Result userunverified() {
//		return badRequest(ControllerUtil.toJsonErrorResponse(ErrorKeys.AUTHENTICATOR_USER_UNVERIFIED));
//	}
//
//	public static Result currentMembership(){
//
//		JsonNode jsonobject = request().body().asJson();
//		String membershipUUID = jsonobject.get(ContextHelper.MEMBERSHIP_UUID).asText();
//
//		MembershipAPI membershipManager = new MembershipManagerAdapter();
//		Membership membership = membershipManager.changeCurrentMembership(getRequestEnvelope(), membershipUUID);
//		JsonNode json = JSONUtil.toJsonFieldsFiltered(membership, MembershipFields.DEFAULT_FIELDS,Membership.class);
//		return Controller.ok(json);
//
//	}
//
//	private static JsonNode buildAuthResponse(ExAuthResponse authResponse) {
//		JsonNode membershipJson = JSONUtil.toJsonFieldsFiltered(authResponse.getMembership(), MembershipFields.DEFAULT_FIELDS,Membership.class);
//		ObjectNode result =  new ObjectMapper().createObjectNode();
//		result.set("membership", membershipJson);
//		result.put("accessToken", authResponse.getAccessToken());
//		result.put("expiresIn", authResponse.getExpiresIn());
//		return result;
//	}
}
