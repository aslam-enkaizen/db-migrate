package com.exrade.runtime.userprofile.providers.password;

import com.exrade.models.userprofile.ExAuthResponse;
import com.exrade.models.userprofile.TokenAction;
import com.exrade.models.userprofile.User;
import com.exrade.providers.password.UsernamePassword;
import com.exrade.runtime.userprofile.AccountManager;
import com.exrade.runtime.userprofile.IAccountManager;
import com.exrade.runtime.userprofile.TokenActionManager;
import com.exrade.util.ExForm;

import java.util.UUID;

public class ExUsernamePasswordAuthProvider {

    private static final String SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET = "loginAfterPasswordReset";
    public static final String PROVIDER_KEY = "password";
//    @Override
//    protected List<String> neededSettingKeys() {
//        final List<String> needed = new ArrayList<String>(
//                super.neededSettingKeys());
//        needed.add(SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
//        return needed;
//    }

//    public ExUsernamePasswordAuthProvider(Application app) {
//        super(app);
//    }

    public static final ExForm<SignupForm> SIGNUP_FORM = ExForm.form(SignupForm.class);
    public static final ExForm<LoginForm> LOGIN_FORM = ExForm.form(LoginForm.class);

    public static class MyIdentity {

        public MyIdentity() {
        }

        public MyIdentity(final String email) {
            this.email = email;
        }

//        @Required
//        @Email
        public String email;

//        public Map<String,List<ValidationError>> validate() {
//            Map<String,List<ValidationError>> errors = new HashMap<>();
//            if (email == null ) {
//
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("value.required", Messages
//                        .get("validation.value.required")));
//
//                errors.put("email",list);
//            }
//            return errors.isEmpty() ? null : errors;
//        }


    }

    public static class LoginForm extends MyIdentity
            implements
            UsernamePassword {

//        @Required
//        @MinLength(8)
        public String password;

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getPassword() {
            return password;
        }

//        public Map<String,List<ValidationError>> validate() {
//
//            Map<String,List<ValidationError>> errors = new HashMap<>();
//            if (password == null ) {
//
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("value.required", Messages
//                        .get("validation.value.required")));
//
//                errors.put("password",list);
//            }
//
//            return errors.isEmpty() ? null : errors;
//        }

    }

    public static class SignupForm extends LoginForm {

//        @Required
//        @MinLength(8)
        public String repeatPassword;

//        @Required
        public String firstName;

//        @Required
        public String lastName;

//        @Required
        public String timezone;

        public String planName;

        public String language;

        public String phone;

//        @Required
        public boolean termsOfServiceAgreement;

        public String profileUUID;

        public String businessName;

        public String vat;

        public String address;

        public String city;

        public String postcode;

        public String country;

        public String redirectUrl;

//        public Map<String,List<ValidationError>> validate() {
//            Map<String,List<ValidationError>> errors = new HashMap<>();
//            if (password == null || !password.equals(repeatPassword)) {
//
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("passwords_not_same", Messages
//                        .get("playauthenticate.password.signup.error.passwords_not_same")));
//
//                errors.put("password",list);
//            }
//            if (password.length() < 8){
//                List<ValidationError> list = new ArrayList<>();
//                if (errors.get("password") != null) {
//                    list = errors.get("password");
//                }
//                list.add(new ValidationError("passwords_too_short", Messages
//                        .get("playauthenticate.password.too_short")));
//                errors.put("password",list);
//            }
//
//            if (firstName == null){
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("value.required", Messages
//                        .get("validation.value.required")));
//
//                errors.put("firstName",list);
//            }
//            if (lastName == null){
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("value.required", Messages
//                        .get("validation.value.required")));
//
//                errors.put("firstName",list);
//            }
//            if (timezone == null){
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("value.required", Messages
//                        .get("validation.value.required")));
//
//                errors.put("timezone",list);
//            }
//
//            if (!termsOfServiceAgreement) {
//                List<ValidationError> list = new ArrayList<>();
//                list.add(new ValidationError("terms_not_agreed", Messages
//                        .get("playauthenticate.password.signup.error.terms_not_agreed")));
//                errors.put("termsOfServiceAgreement",list);
//            }
//            return errors.isEmpty() ? null : errors;
//        }
    }

//    public boolean isLoginAfterPasswordReset() {
//        return getConfiguration().getBoolean(
//                SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
//    }

    public static ExUsernamePasswordAuthProvider getProvider() {
//        return (ExUsernamePasswordAuthProvider) PlayAuthenticate.getProvider(PROVIDER_KEY);
        return null;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

//    @Override
    protected String generateVerificationRecord(
            final ExUsernamePasswordAuthUser authUser) {
        IAccountManager accountManager = new AccountManager();
        User user = accountManager.findByUsernamePasswordIdentity(authUser);
        String verificationRecord = generateVerificationRecord(user);
        return verificationRecord;
    }

    public static String generatePasswordResetRecord(final User user) {
        final String token = generateToken();
        TokenActionManager tam = new TokenActionManager();
        tam.create(TokenAction.Type.PASSWORD_RESET, token, user);
        return token;
    }

    public static String generateVerificationRecord(final User user) {
        final String token = generateToken();
        TokenActionManager tam = new TokenActionManager();
        tam.create(TokenAction.Type.EMAIL_VERIFICATION, token, user);
        return token;
    }

//  protected String getEmailTemplate(final String template,
//          final String langCode, final String url, final String token,
//          final String name, final String email) {
//      Class<?> cls = null;
//      String ret = null;
//      try {
//          cls = Class.forName(template + "_" + langCode);
//      } catch (ClassNotFoundException e) {
//          Logger.warn("Template: '"
//                  + template
//                  + "_"
//                  + langCode
//                  + "' was not found! Trying to use English fallback template instead.");
//      }
//      if (cls == null) {
//          try {
//              cls = Class.forName(template + "_"
//                      + EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
//          } catch (ClassNotFoundException e) {
//              Logger.error("Fallback template: '" + template + "_"
//                      + EMAIL_TEMPLATE_FALLBACK_LANGUAGE
//                      + "' was not found either!");
//          }
//      }
//      if (cls != null) {
//          Method htmlRender = null;
//          try {
//              htmlRender = cls.getMethod("render", String.class,
//                      String.class, String.class, String.class);
//              ret = htmlRender.invoke(null, url, token, name, email)
//                      .toString();
//
//          } catch (NoSuchMethodException e) {
//              ExLogger.get().error("Error email html rendering",e);
//          } catch (IllegalAccessException e) {
//              ExLogger.get().error("Error email html rendering",e);
//          } catch (InvocationTargetException e) {
//              ExLogger.get().error("Error email html rendering,e);
//          }
//      }
//      return ret;
//  }

    /*private enum Case {
        SIGNUP, LOGIN
    }*/

    /**
     *  This dirty override is needed to avoid verification email sending
     *  since we want to return a REST response instead
     */
    /*@Override
    public Object authenticate(Context context, Object payload)
            throws AuthException {

        if (payload == Case.SIGNUP) {

            final SignupForm signup = getSignup(context);
            final ExUsernamePasswordAuthUser authUser = buildSignupAuthUser(
                    signup, context);
            final SignupResult r = signupUser(authUser);

            switch (r) {
            case USER_EXISTS:
                // The user exists already
                return r;
            case USER_EXISTS_UNVERIFIED:
            case USER_CREATED_UNVERIFIED:
                // User got created as unverified
                // Send validation email
                //final String record = generateVerificationRecord(authUser);
                return authUser;
            // case not handled
//          case USER_CREATED:
//              // continue to login...
//              return transformAuthUser(authUser, context);
            default:
                throw new AuthException("Something in signup went wrong");
            }
        }

        else if (payload == Case.LOGIN) {
            final LoginForm login = getLogin(context);
            final ExLoginUsernamePasswordAuthUser authUser = buildLoginAuthUser(login, context);
            final LoginResult r = loginUser(authUser);
            switch (r) {
            case USER_UNVERIFIED:
                // The email of the user is not verified, yet - we won't allow
                // him to log in
                return LoginResult.USER_UNVERIFIED;
            case USER_LOGGED_IN:
                // The user exists and the given password was correct
                return authUser;
            case WRONG_PASSWORD:
                // don't expose this - it might harm users privacy if anyone
                // knows they signed up for our service
            case NOT_FOUND:
                return LoginResult.NOT_FOUND;
            default:
                throw new AuthException("Something in login went wrong");
            }
        }
        return super.authenticate(context, payload);
    }*/

//    @Override
//    public Object authenticate(Context context, Object payload)
//            throws AuthException {
//
//        if (payload instanceof ExUsernamePasswordAuthUser) {
//
//            //final SignupForm signup = getSignup(context);
//            //final ExUsernamePasswordAuthUser authUser = buildSignupAuthUser(
//            //      signup, context);
//            final ExUsernamePasswordAuthUser authUser = (ExUsernamePasswordAuthUser) payload;
//            final SignupResult r = signupUser(authUser);
//
//            switch (r) {
//            case USER_EXISTS:
//                // The user exists already
//                return r;
//            case USER_EXISTS_UNVERIFIED:
//            case USER_CREATED_UNVERIFIED:
//                // User got created as unverified
//                // Send validation email
//                //final String record = generateVerificationRecord(authUser);
//                return authUser;
//            // case not handled
////          case USER_CREATED:
////              // continue to login...
////              return transformAuthUser(authUser, context);
//            default:
//                throw new AuthException("Something in signup went wrong");
//            }
//        }
//
//        else if (payload instanceof ExLoginUsernamePasswordAuthUser) {
//            //final LoginForm login = getLogin(context);
//            //final ExLoginUsernamePasswordAuthUser authUser = buildLoginAuthUser(login, context);
//            final ExLoginUsernamePasswordAuthUser authUser = (ExLoginUsernamePasswordAuthUser) payload;
//            final LoginResult r = loginUser(authUser);
//            switch (r) {
//            case USER_UNVERIFIED:
//                // The email of the user is not verified, yet - we won't allow
//                // him to log in
//                return LoginResult.USER_UNVERIFIED;
//            case USER_LOGGED_IN:
//                // The user exists and the given password was correct
//                return authUser;
//            case WRONG_PASSWORD:
//                // don't expose this - it might harm users privacy if anyone
//                // knows they signed up for our service
//            case NOT_FOUND:
//                return LoginResult.NOT_FOUND;
//            default:
//                throw new AuthException("Something in login went wrong");
//            }
//        }
//        return super.authenticate(context, payload);
//    }

    public static ExAuthResponse login(ExLoginUsernamePasswordAuthUser loginAuthUser){
        return null;
//        final AuthProvider ap = PlayAuthenticate.getProvider(PROVIDER_KEY);
//        if (ap == null) {
//            // Provider wasn't found and/or user was fooling with our stuff -
//            // tell him off:
//            /*return Controller.notFound(Messages.get(
//                    "playauthenticate.core.exception.provider_not_found",
//                    PROVIDER_KEY));*/
//            throw new ExNotFoundException(Messages.get(
//                    "playauthenticate.core.exception.provider_not_found",
//                    PROVIDER_KEY));
//        }
//        try {
//            final Object authResult = ap.authenticate(Http.Context.current(), loginAuthUser);
//
//            if (authResult instanceof LoginResult) {
//                final LoginResult loginResult = (LoginResult) authResult;
//
//                if (LoginResult.USER_UNVERIFIED == loginResult) {
//                    //return Controller.badRequest(toJsonErrorResponse(ErrorKeys.AUTHENTICATOR_USER_UNVERIFIED));
//                    throw new ExParamException(ErrorKeys.AUTHENTICATOR_USER_UNVERIFIED); //TODO
//                }
//
//                if (LoginResult.NOT_FOUND == loginResult) {
//                    /*return Controller.badRequest(
//                            toJsonErrorResponse(ErrorKeys.AUTHENTICATOR_WRONG_USER_OR_PASSWORD));*/
//                    throw new ExParamException(ErrorKeys.AUTHENTICATOR_WRONG_USER_OR_PASSWORD); //TODO
//                }
//
//            }
//            else if (authResult instanceof AuthUser){
//                // Assume the user is loaded in the context
//                Membership membership = (Membership) ContextHelper.getMembership();
//                ExAuthResponse authResponse = new ExAuthResponse();
//                authResponse.setMembership(membership);
//                authResponse.setAccessToken(TraktiJwtManager.getInstance().generateToken(membership.getUuid()));
//                return authResponse;
//                //JsonNode json = JSONUtil.toJsonFieldsFiltered(membership, MembershipFields.DEFAULT_FIELDS,Membership.class);
//                //return Controller.ok(json);
//            }
//
//            /*return Controller.internalServerError(Messages
//                    .get("playauthenticate.core.exception.general"));*/
//            throw new ExException(Messages
//                    .get("playauthenticate.core.exception.general"));
//
//        } catch (final AuthException e) {
//            //return ControllerUtil.httpErrorResponse(e);
//            throw new ExException(e);
//        }

    }

    public static String signup(ExUsernamePasswordAuthUser authUser){
        return null;
//        final AuthProvider ap = PlayAuthenticate.getProvider(PROVIDER_KEY);
//        if (ap == null) {
//            // Provider wasn't found and/or user was fooling with our stuff -
//            // tell him off:
//            /*return Controller.notFound(Messages.get(
//                    "playauthenticate.core.exception.provider_not_found",
//                    PROVIDER_KEY));*/
//            throw new ExNotFoundException(Messages.get(
//                    "playauthenticate.core.exception.provider_not_found",
//                    PROVIDER_KEY));
//        }
//        try {
//            final Object authResult = ap.authenticate(Http.Context.current(), authUser);
//
//            if (authResult instanceof SignupResult) {
//                final SignupResult signupResult = (SignupResult) authResult;
//                if (SignupResult.USER_EXISTS == signupResult) {
//                    /*return Controller.badRequest(ControllerUtil.toJsonErrorResponseInvalidParam(
//                            "email",ErrorKeys.USER_NAME_EXISTS.name(),Messages.get(ErrorKeys.USER_NAME_EXISTS.getKey())));*/
//                    throw new ExParamException(ErrorKeys.USER_NAME_EXISTS, "email");
//                }
//            }
//            else if (authResult  instanceof ExUsernamePasswordAuthUser) {
//
//                com.feth.play.module.pa.controllers.Authenticate.noCache(Controller.response());
//                String verificationToken = ((ExUsernamePasswordAuthProvider)ap).generateVerificationRecord(authUser);
//                //ObjectNode result = Json.newObject();
//                //result.put(TokenActionFields.TOKEN, verificationToken);
//                //return Controller.created(result);
//                return verificationToken;
//            }
//
//
//            /*return Controller.internalServerError(Messages
//                    .get("playauthenticate.core.exception.general"));*/
//            throw new ExException(Messages
//                    .get("playauthenticate.core.exception.general"));
//
//        } catch (final AuthException e) {
//            //return Controller.internalServerError(e.getMessage());
//            throw new ExException(e);
//        }

    }



}
