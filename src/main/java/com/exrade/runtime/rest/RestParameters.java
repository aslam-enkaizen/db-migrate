package com.exrade.runtime.rest;

import com.exrade.util.ExCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestParameters {

    // Generic parameters
    public static final String UUID = "uuid";

    /**
     * Generic UUID field
     */
    public static final int MAX_RETRY = 5;

    /**
     * Common fields
     */
    public static final String CREATION_DATE = "creationDate";
    public static final String UPDATE_DATE = "updateDate";

    /**
     * Generic query parameter
     */
    // public static final String QUERY = "q";
    public static final String FIELDS = "fields";
    public static final String EXPAND = "expand";
    public static final String LANGUAGE = "lang";
    public static final String KEYWORDS = "keywords";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";

    /**
     * Tag field
     */
    public static final String TAG = "tag";

    /**
     * UUID of the session authenticated user
     */
    // public static final String USER_UUID = "userid";
    public static final String TARGET_USER = "targetUser"; // used when admin suspends a user account

    public static final String OBJECT_ID = "objectID";

    public static final String PARTICIPANT_ID = "participantID";
    public static final String OFFER_ID = "offerID";
    public static final String ERROR = "error";
    public static final String VALIDATION_ERRORS = "validationErrors";

    public static final String NEGOTIATION_MESSAGE = "negotiationMessage";

    public static final String FILE_NAME = "fileName";
    public static final String SIGNATURE = "signature";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String SECRET_SIGN_KEY = "secret_sign_key";
    public static final String SEARCH_RESULT_SUMMARY = "searchResultSummary";
    public static final String NOTIFICATION_EVENT = "NOTIFICATION_EVENT";
    public static final String IMPORT_EVENT = "IMPORT_EVENT";
    public static final String EXPORT_EVENT = "EXPORT_EVENT";

    public static final String DATA = "data";

    public static final class Pagination {
        public static final String PAGE = "page";
        public static final String PER_PAGE = "per_page";
    }

    public static final class ResourceCollectionMetaFields {
        public static final String PAGE = Pagination.PAGE;
        public static final String PER_PAGE = Pagination.PER_PAGE;
        public static final String TOTAL_PAGES = "total_pages";
        public static final String TOTAL_RESULTS = "total_results";
        public static final String PAGE_PREV = "prev";
        public static final String PAGE_NEXT = "next";
        public static final String PAGE_FIRST = "first";
        public static final String PAGE_LAST = "last";
    }

    public static final class Error {
        public static final String STATUS = "status";
        public static final String USER_MESSAGE = "userMessage";
        public static final String FIELD = "field";
        public static final String CODE = "code";
        public static final String URL = "url";
        public static final String REQUEST = "request";
        public static final String STACKTRACE = "stacktrace";

    }

    public static final class Resources {
        // TODO to be improved because there are still hardcoded parameters
        public static final String FIELDS = "fields";

        public static final String USERS = "users";
        public static final String NEGOTIATIONS = "negotiations";
        public static final String DRAFTS = "drafts";
        public static final String INFOMODELS = "infomodels";
        public static final String STATUS = "status";
        public static final String MESSAGES = "messages";
        public static final String ADMISSIONS = "admissions";
        public static final String LOGS = "logs";
        public static final String COMMENTS = "comments";
        public static final String FAQ = "faq";
        public static final String TAGS = "tags";
        public static final String DOCUMENT = "document";
        public static final String DRAFT = "draft";
        public static final String FILES = "files";
        public static final String BUNDLES = "bundles";
        public static final String PROCESSMODELS = "processmodels";
        public static final String EVENTS = "events";
        public static final String METADATA = "metadata";
        public static final String INFO_MESSAGES = "infomessages";
        public static final String REMINDERS = "reminders";
        public static final String ATTRIBUTES = "attributes";
        public static final String INVITATIONS = "invitations";
        public static final String COPY = "copy";
        public static final String PROFILES = "profiles";
        public static final String MEMBERSHIPS = "memberships";
        public static final String PLANS = "plans";
        public static final String SUBSCRIPTIONS = "subscriptions";
        public static final String SIGNATURES = "signatures";
        public static final String CONTACTS = "contacts";
        public static final String TEMPLATES = "templates";
        public static final String WORKGROUPS = "workgroups";
        public static final String NOTIFICATION_SETTINGS = "notificationsettings";
        public static final String USER_SETTINGS = "usersettings";
        public static final String NOTIFICATION_FEEDS = "notificationfeeds";
        public static final String ACTIVITY_FEEDS = "activityfeeds";
        public static final String POSTS = "posts";
        public static final String AUTHORISATIONS = "authorisations";
        public static final String REVIEW_REQUESTS = "reviewrequests";
        public static final String REVIEWS = "reviews";
        public static final String BLOCKCHAIN_TRANSACTIONS = "blockchaintransactions";
        public static final String BLOCKCHAIN_WALLETS = "blockchainwallets";
        public static final String KYCS = "kycs";
        public static final String INTEGRATION_SETTINGS = "integrationsettings";
        public static final String CONTRACTS = "contracts";
        public static final String MEMBERS = "own-members";
        public static final String ATM = "atm";
        public static final String ATM_ASSET_SCHEMAS = "assetschemas";
        public static final String ATM_ASSETS = "assets";
        public static final String CLAUSE = "clauses";
        public static final String ATM_ACTIVITIES = "activities";
        public static final String ATM_COURSES = "courses";
        public static final String QII = "qii";
        public static final String INVOICES = "invoices";
        public static final String TEAMS = "teams";
        public static final String TRAKS = "traks";
        public static final String TRAK_RESPONSES = "trakresponses";
        public static final String TRAK_APPROVALS = "trakapprovals";
        public static final String WEBHOOKS = "webhooks";
        public static final String USER_NOTIFICATION_SETTING = "usernotificationsettings";
        public static final String UPCOMING_LIFECYCLE_EVENTS = "upcominglifecycleevents";
        public static final String EXPORTS = "exports";
        public static final String IMPORTS = "imports";
        public static final String CALLBACKS = "callbacks";

    }

    public static final class Controllers {
        public static final String SEARCH = "search";
    }

    public static final class AdmissionRequestFilters {
        public static final String ADMISSION_UUID = "admissionUUID";
        public static final String DECISION = "decision";
        public static final String INVITE = "invite";
        public static final String AGREEMENT_SIGNERS = "agreementSigners";
    }

    public static final class MessageFields {
        public static final String MESSAGE_TYPE = "messageType";
        public static final String RECEIVER = "receiver";
        public static final String SENDER = "sender";
        public static final String ITEMS = "items";
        public static final String TEMPLATE = "template";
        public static final String PARTICIPANTS = "participants";
        public static final String REQUEST_FOR_PROPOSAL = "requestForProposal";
        public static final String MESSAGE_CREATION_TIME = "messageCreationTime";
        public static final String NOTE = "note";
        public static final String PUBLISH_STATUS = "publishStatus";
    }

    public static final class MessageFilters {
        public static final String MESSAGE_UUID = "messageUUID";
        public static final String STATUS = "q"; // temporary is q but should be changed to status
        public static final String ACCEPT = "accept";
        public static final String MESSAGE = "message";
        public static final String REJECT = "reject";
        public static final String EXCLUSIVE = "exclusive";
        public static final String ACCEPTED = "accepted";
        public static final String SCORE = "score";
    }

    public static final class NegotiationStatusFilters {
        public static final String OPTION = "userOption";
    }

    public static final class NegotiationStatusFields {
        public static final String STATE = "state";
        public static final String PROCESSMODEL_NAME = "processModelName";
        public static final String ACTION = "action";
        public static final String USER_OPTIONS = "userOptions";
        public static final String PENDING_ADMISSION_REQUESTS = "pendingAdmissionRequests";
        public static final String PARTICIPANT_IDS = "participantIDs";

    }

    public static final class NegotiationSummaryFields {

        public static final String ADMISSION_OPEN = "admissionOpen";
        public static final String AGREEMENT_SIGNATURE_TYPE = "agreementSignatureType";
        public static final String AGREEMENT_SIGNERS = "agreementSigners";
        public static final String AGREEMENT_SIGNING_ENABLED = "agreementSigningEnabled";
        public static final String ALLOWED_OPERATIONS = "allowedOperations";
        public static final String ARCHIVED = "archived";
        public static final String BLOCKCHAIN_ENABLED = "blockchainEnabled";
        public static final String CLOSED = "closed";
        public static final String CREATION_TIME = "creationTime";
        public static final String CURRENCY_CODE = "currencyCode";
        public static final String CUSTOM_FIELDS = "customFields";
        public static final String DESCRIPTION = "description";
        public static final String EDITING_CONTRACT_BY_PARTICIPANT_ALLOWED = "editingContractByParticipantAllowed";
        public static final String END_TIME = "endTime";
        public static final String EXPECTED_AMOUNT = "expectedAmount";
        public static final String FILES = "files";
        public static final String HEADED_PAPER_TEMPLATE_UUID = "headedPaperTemplateUUID";
        public static final String IMAGES = "images";
        public static final String INFORMATION_DOCUMENT = "informationModelDocument";
        public static final String JOIN_ALLOWED_FOR_REQUESTOR = "joinAllowedForRequestor";
        public static final String LANGUAGE = "language";
        public static final String NEGOTIATION_MODEL_NAME = "negotiationModelName";
        public static final String NEGOTIATION_MODEL_TITLE = "negotiationModelTitle";
        public static final String NEGOTIATION_MODEL_UUID = "negotiationModelUUID";
        public static final String NEGOTIATION_ROLE = "negotiationRole";
        public static final String NEGOTIATION_TEMPLATE_UUID = "negotiationTemplateUUID";
        public static final String NEGOTIATION_TYPE = "negotiationType";
        public static final String OWNER = "owner";
        public static final String OWNER_TIME_EVENTS = "ownerTimeEvents";
        public static final String OWNER_UUID = "ownerUuid";
        public static final String PARENT_CONTRACT_UUID = "parentContractUUID";
        public static final String PARTICIPANT_TIME_EVENTS = "participantTimeEvents";
        public static final String PARTICIPANTS = "participants";
        public static final String PAYMENT = "payment";
        public static final String POLICIES = "policies";
        public static final String PRIVACY_LEVEL = "privacyLevel";
        public static final String PROCESS_ATTRIBUTES = "processAttributes";
        public static final String PUBLICATION_TIME = "publicationTime";
        public static final String PUBLISH_STATUS = "publishStatus";
        public static final String REVIEWERS = "reviewers";
        public static final String SMART_CONTRACT_ADDRESS = "smartContractAddress";
        public static final String SMART_CONTRACT_TRANSACTION_ID = "smartContractTransactionId";
        public static final String START_TIME = "startTime";
        public static final String TEMPLATE = "template";
        public static final String TITLE = "title";
        public static final String USER_ADMISSION_STATUS = "userAdmissionStatus";
        public static final String USER_NEGOTIATION_STATUS_NAME = "userNegotiationStatusName";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {

            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, TITLE, NEGOTIATION_MODEL_NAME,
                    NEGOTIATION_MODEL_UUID, NEGOTIATION_MODEL_TITLE, DESCRIPTION, CREATION_TIME, START_TIME, END_TIME,
                    PUBLICATION_TIME, PRIVACY_LEVEL, OWNER_UUID, ADMISSION_OPEN, USER_ADMISSION_STATUS,
                    NEGOTIATION_ROLE, JOIN_ALLOWED_FOR_REQUESTOR, OWNER_TIME_EVENTS, PARTICIPANT_TIME_EVENTS,
                    PROCESS_ATTRIBUTES, FILES, AGREEMENT_SIGNING_ENABLED, AGREEMENT_SIGNATURE_TYPE, EXPECTED_AMOUNT,
                    USER_NEGOTIATION_STATUS_NAME, LANGUAGE, CURRENCY_CODE, IMAGES, PUBLISH_STATUS, PAYMENT,
                    ALLOWED_OPERATIONS, BLOCKCHAIN_ENABLED, SMART_CONTRACT_ADDRESS, SMART_CONTRACT_TRANSACTION_ID,
                    CLOSED, EDITING_CONTRACT_BY_PARTICIPANT_ALLOWED, ARCHIVED, HEADED_PAPER_TEMPLATE_UUID, TEMPLATE,
                    NEGOTIATION_TEMPLATE_UUID, NEGOTIATION_TYPE, PARENT_CONTRACT_UUID));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(INFORMATION_DOCUMENT, POLICIES, OWNER,
                    PARTICIPANTS, AGREEMENT_SIGNERS, REVIEWERS, CUSTOM_FIELDS));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }

    }

    public static final class BundleFilters {
        public static final String BUNDLE_NAME = "bundleName";
        public static final String TAGS = "tags";
    }

    public static final class BundleFields {
        public static final String NAME = "name";
        public static final String PROCESSMODEL = "processModel";
        public static final String INFORMATIONMODEL_TEMPLATE = "informationModelTemplate";
        public static final String DESCRIPTION = "description";
        public static final String POLICIES = "policies";
        public static final String TAGS = "tags";
        public static final String PRIVACY_LEVEL = "privacyLevel";
        public static final String TITLE = "title";

        public static final List<String> VALID_FIELDS;

        static {
            VALID_FIELDS = Arrays.asList(UUID, NAME, PROCESSMODEL, INFORMATIONMODEL_TEMPLATE, DESCRIPTION,
                    POLICIES, TAGS, TITLE, PRIVACY_LEVEL);
        }

    }

    public static final class ProfileFilters {
        public static final String COMMENT = "comment";
        public final static String PROFILE_TYPE = "profileType";
        public final static String USER_UUID = "user.uuid";
    }

    public static final class ProfileFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("active", "address", "businessProfile", "city", "competences", "country",
                    "description", "facebook", "files", "identityVerified", "images", "interests", "legalEmail",
                    "linkedin", "logo", "nace", "name", "phone", "postcode", "profileStatus", "publicProfile",
                    "subdomain", "twitter", "uuid", "vat", "video", "walletAddress", "website");
            EXPAND_FIELDS = Arrays.asList("planSubscription", "profileRoles", "permissions", "paymentMethods",
                    "domainVerified", "customFields");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String DESCRIPTION = "description";
        public static final String ADDRESS = "address";
        public static final String POSTCODE = "postcode";
        public static final String CITY = "city";
        public static final String PHONE = "phone";
        public static final String COUNTRY = "country";
        public static final String WEBSITE = "website";
        public static final String PUBLIC_PROFILE = "publicProfile";
        public static final String ACTIVE = "active";
        public static final String PLAN_SUBSCRIPTION = "planSubscription";
        public static final String INTERESTS = "interests";
        public static final String PROFILE_STATUS = "profileStatus";
        public static final String PAYMENT_METHODS = "paymentMethods";
        public static final String TWITTER = "twitter";
        public static final String FACEBOOK = "facebook";
        public static final String LINKEDIN = "linkedin";
        public static final String IMAGES = "images";
        public static final String FILES = "files";
        public static final String CUSTOM_FIELDS = "customFields";
        public static final String NAME = "name";
        public static final String LOGO = "logo";
        public static final String VAT = "vat";
        public static final String NACE = "nace";
        public static final String LEGAL_EMAIL = "legalEmail";
        public static final String COMPETENCES = "competences";
        public static final String DOMAIN_VERIFIED = "domainVerified";
        public static final String IDENTITY_VERIFIED = "identityVerified";
        public static final String VIDEO = "video";
        public static final String SUBDOMAIN = "subdomain";
        public static final String WALLET_ADDRESS = "walletAddress";
        public static final String BUSINESS_PROFILE = "businessProfile";
    }

    public static final class ReminderFilters {
        public static final String FROM = "from";
        public static final String TO = "to";
    }

    public static final class NegotiationFilters {
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String PRIVACY_LEVEL = "privacyLevel";
        public static final String OWNED = "owned";
        public static final String PARTICIPATED = "participated";
        public static final String INVOLVED = "involved";
        public static final String REQUESTED = "requested";
        public static final String INVITED = "invited";
        public static final String EVENT_STARTS = "eventStarts";
        public static final String EVENT_ENDS = "eventEnds";
        public static final String KEYWORDS = "keywords";
        public static final String CANCEL_REASON = "cancelReason";
        public static final String USER_ADMISSION_STATUS = "admissionStatus";
        public static final String BUNDLE_UUID = "bundle_uuid";
        public static final String PROCESSMODEL_UUID = "processmodel_uuid";
        public static final String INFORMATIONMODEL_UUID = "informationmodel_uuid";
        public static final String PROCESSMODELS = "processModels";
        public static final String OWNER = "owner";
        public static final String NOT_OWNER = "not.owner";
        public static final String LANGUAGE = "language";
        public static final String NEGOTIATION_STAGE = "negotiationStage";
        public static final String SUPPORT_PAYMENT = "supportPayment";
        public static final String SYSTEM_TAGS = "systemTags";
        public static final String COMPANION_MEMBER = "companionMember";
        public static final String COMPANION_PROFILE = "companionProfile";
        public static final String PROFILE_OWNER = "ownerProfileUUID";
        public static final String NOT_COMPANION_MEMBER = "notCompanionMember";
        public static final String MEMBERSHIP_STATUS = "membershipStatus";
        public static final String EXCLUDE_OWNED_COMPANY_DEALS = "excludeOwnedCompanyDeals";
        public static final String ADMISSION_OPEN = "admissionOpen";
        public static final String INCLUDE_ARCHIVED = "includeArchived";
        public static final String NEGOTIATION_TEMPLATE_UUID = "negotiationTemplateUUID";
        public static final String CREATED_AFTER_INCLUSIVE = "createdAfterInclusive";
        public static final String PAYMENT_STATUS = "payment.paymentStatus";
        public static final String PAYMENT_UUID = "paymentUUID";

    }

    public static final class NegotiationFields {

        public static final String AGREEMENT_SIGNATURE_TYPE = "agreementSignatureType";
        public static final String AGREEMENT_SIGNERS = "agreementSigners";
        public static final String AGREEMENT_SIGNING_ENABLED = "agreementSigningEnabled";
        public static final String ALLOWED_PAYMENT_METHOD_UUIDS = "allowedPaymentMethodUUIDs";
        public static final String BLOCKCHAIN_ENABLED = "blockchainEnabled";
        public static final String BUNDLE_UUID = "bundleUUID";
        public static final String CATEGORY = "category";
        public static final String CURRENCYCODE = "currencyCode";
        public static final String CUSTOM_FIELDS = "customFields";
        public static final String DESCRIPTION = "description";
        public static final String EDITING_CONTRACT_BY_PARTICIPANT_ALLOWED = "editingContractByParticipantAllowed";
        public static final String END_DATE = "endDate";
        public static final String EXPECTED_AMOUNT = "expectedAmount";
        public static final String FAQS = "faqs";
        public static final String FILES = "files";
        public static final String FINAL_AMOUNT_ATTRIBUTE = "finalAmountAttribute";
        public static final String HEADED_PAPER_TEMPLATE_UUID = "headedPaperTemplateUUID";
        public static final String IMAGES = "images";
        public static final String INFORMATIONMODELDOCUMENT = "informationModelDocument";
        public static final String LANGUAGE = "language";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String MODEL_TAGS = "modelTags";
        public static final String NEGOTIATION_SCHEMA = "negotiationSchema";
        public static final String NEGOTIATION_TYPE = "negotiationType";
        public static final String NEGOTIATORS = "negotiators";
        public static final String OWNER = "owner";
        public static final String OWNER_TIME_EVENTS = "ownerTimeEvents";
        public static final String PARENT_CONTRACT_UUID = "parentContractUUID";
        public static final String PARTICIPANT_TIME_EVENTS = "participantTimeEvents";
        public static final String PARTICIPANTS = "participants";
        public static final String PAYMENT = "payment";
        public static final String POLICIES = "policies";
        public static final String PRIVACY_LEVEL = "privacyLevel";
        public static final String PROCESS_ATTRIBUTES = "processAttributes";
        public static final String PROCESSMODEL_UUID = "processModelUUID";
        public static final String PUBLICATION_DATE = "publicationDate";
        public static final String PUBLISH_STATUS = "publishStatus";
        public static final String RANGE_KM = "rangeKM";
        public static final String REVIEWERS = "reviewers";
        public static final String SMART_CONTRACT_ADDRESS = "smartContractAddress";
        public static final String SMART_CONTRACT_TRANSATION_ID = "smartContractTransactionId";
        public static final String START_DATE = "startDate";
        public static final String STATEMACHINES = "stateMachines";
        public static final String TAGS = "tags";
        public static final String TITLE = "title";

        public static final class CustomFields {
            public static final String WORKGROUP_UUID = "workgroupUUID";
            public static final String CONTRACT_UUID = "contractUUID";
            public static final String REFERENCE_ID = "referenceId";
            public static final String PARENT_CONTRACT_UUID = "parentContractUUID";
            public static final String PARENT_NEGOTIATION_UUID = "parentNegotiationUUID";
        }

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {

            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(TITLE, DESCRIPTION, PUBLICATION_DATE,
                    START_DATE, END_DATE, LATITUDE, AGREEMENT_SIGNING_ENABLED, AGREEMENT_SIGNATURE_TYPE, LONGITUDE,
                    RANGE_KM, PRIVACY_LEVEL, PUBLISH_STATUS, BUNDLE_UUID, INFORMATIONMODELDOCUMENT, FILES, IMAGES,
                    OWNER, PARTICIPANTS, PROCESSMODEL_UUID, FAQS, PARTICIPANT_TIME_EVENTS, OWNER_TIME_EVENTS,
                    PROCESS_ATTRIBUTES, POLICIES, CURRENCYCODE, CATEGORY, UUID, PAYMENT, LANGUAGE, NEGOTIATION_SCHEMA,
                    TAGS, MODEL_TAGS, AGREEMENT_SIGNERS, REVIEWERS, BLOCKCHAIN_ENABLED, SMART_CONTRACT_ADDRESS,
                    SMART_CONTRACT_TRANSATION_ID, CUSTOM_FIELDS, EDITING_CONTRACT_BY_PARTICIPANT_ALLOWED,
                    HEADED_PAPER_TEMPLATE_UUID, NEGOTIATION_TYPE, PARENT_CONTRACT_UUID));
            EXPAND_FIELDS = Collections.unmodifiableList(new ArrayList<>());
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }

    }

    public static final class NegotiationCommentFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "message", "files", "creator");
            EXPAND_FIELDS = new ArrayList<>();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String CREATOR = "creator";
        public static final String MESSAGE = "message";
        public static final String DESCRIPTION = "description";
        public static final String CREATE_DATE = "created";
        public static final String UPDATE_DATE = "updated";
        public static final String FILES = "files";
        public static final String NEGOTIATION = "negotiation";
    }

    public static class ImageFields {
        public static final String FILE_UUID = "fileUUID";
        public static final String ORDER = "order";
    }

    public static class InvitationFilters {
        public static final String INVITATION_UUID = "invitationUUID";
        public static final String DECISION = "decision";
        public static final String ACCEPT = "accept";
        public static final String IGNORE = "ignore";
        public static final String REJECT = "reject";
        public static final String BLOCK = "block";
    }

    public static class InvitationFields {
        public static final String INVITED_EMAIL = "invitedEmail";
        public static final String INVITATION_STATUS = "invitationStatus";
    }

    public static class NegotiationInvitationFields extends InvitationFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "invitedEmail", "invitationStatus",
                    "invitedMembershipUUID", "invitedNegotiationUUID", CREATION_DATE, UPDATE_DATE, "expiryDate");
            EXPAND_FIELDS = Arrays.asList("invitedNegotiation", "invitedMembership");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String INVITED_MEMBERSHIP = "invitedMembership";
        public static final String INVITED_NEGOTIATION = "invitedNegotiation";
        // public static final String ROLENAME = "roleName";
    }

    public static class NegotiationInvitationFilters {
        public static final String INVITED_NEGOTIATION_UUID = "invitedNegotiation.uuid";
        public static final String INVITED_MEMBERSHIP_UUID = "invitedMembership.uuid";
        public static final String INVITATION_INBOX = "invitationInbox";
    }

    public static class MemberInvitationFields extends InvitationFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "invitedEmail", "invitationStatus", "invitedUserUUID",
                    "invitedUserGuest", "roleName", "invitedProfileUUID", CREATION_DATE, "invitedBy", UPDATE_DATE,
                    "expiryDate", "membershipExpirationDate", "agreementSigner", "maxNegotiationAmount",
                    "authorizationDocuments", "title");
//			EXPAND_FIELDS = java.util.Arrays.asList("invitedUser", "invitedProfile");
            EXPAND_FIELDS = Arrays.asList("supervisor");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String INVITED_USER = "invitedUser";
        public static final String INVITED_USER_UUID2 = "invitedUserUUID";
        public static final String INVITED_PROFILE = "invitedProfile";
        public static final String INVITED_PROFILE_UUID2 = "invitedProfileUUID";
        public static final String ROLENAME = "roleName";
        public static final String TITLE = "title";
        public static final String MEMBERSHIP_EXPIRATION_DATE = "membershipExpirationDate";
        public static final String AUTHORIZATION_DOCUMENTS = "authorizationDocuments";
        public static final String MAX_NEGOTIATION_AMOUNT = "maxNegotiationAmount";
        public static final String AGREEMENT_SIGNER = "agreementSigner";
        public static final String SUPERVISOR = "supervisor";
    }

    public static class MemberInvitationFilters {
        public static final String INVITATION_INBOX = "invitationInbox";
        public static final String INVITED_USER_UUID = "invitedUser.uuid";
        public static final String INVITED_MEMBERSHIP_UUID = "invitedMembership.uuid";
        public static final String INVITED_PROFILE_UUID = "invitedProfile.uuid";
    }

    public static class ActorSessionFields {
        public static final String ACTOR_UUID = "actorUUID";
        public static final String EXPIRATION_DATE = "expirationDate";
        public static final String TOKEN = "token";
    }

    public static final class CommentFields {
        public static final String CONTENT = "content";
    }

    public static final class InformationTemplateFilters {
        public static final String NAME = "informationTemplateName";
        public static final String PUBLISH_STATUS = "publishStatus";
        public static final String INCLUDE_ARCHIVED = "includeArchived";
    }

    public static final class InformationTemplateFields {
        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String MODEL_VERSION = "modelVersion";
        public static final String AUTHOR = "author";
        public static final String AUTHOR_MEMBERSHIP = "authorMembership";
        public static final String AUTHOR_MEMBERSHIP_UUID = "authorMembershipUUID";
        public static final String PULICATION_DATE = "publicationDate";
        public static final String LANGUAGE = "language";
        public static final String TAGS = "tags";
        public static final String CATEGORY = "category";

        public static final String ITEMS = "items";
        public static final String SECTIONS = "sections";
        public static final String PUBLISH_STATUS = "publishStatus";
        public static final String TEMPLATE = "template";

    }

    public static final class InformationDocumentFields {
        public static final String INFORMATIONTEMPLATEUUID = "informationTemplateUUID";
        public static final String CATEGORY = "category";
        public static final String TITLE = "title";
        public static final String LANGUAGE = "language";
    }

    public static final class UserProfileFields {
        public static final String USER = "user";
    }

    public static final class MembershipFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {

            DEFAULT_FIELDS = Arrays.asList("uuid", "identifier", "email", "phone", "firstName", "lastName",
                    "fullName", "userAvatar", "businessLogo", "status", "active", "profileActive", "businessProfile",
                    "language", "timezone", "profileUUID", "userUUID", "platformRole", "role", "name", "defaultProfile",
                    "providerNames", "agreementSigner", "maxNegotiationAmount", "expirationDate", "title",
                    "supervisorUUID", "blockchainAddress", "blockchainKey", "guest", "publicProfile", CREATION_DATE,
                    UPDATE_DATE, "membershipExpired", "profileWalletAddress", "roleName");
            EXPAND_FIELDS = Arrays.asList("user", "profile", "authorizationDocuments", "updatedBy",
                    "supervisor", "permissions", "plan");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String IDENTIFIER = "identifier";
        public static final String USER = "user";
        public static final String PROFILE = "profile";
        public static final String ROLE = "role";
        public static final String IS_DEFAULT_PROFILE = "defaultProfile";

        public static final String EMAIL = "email";
        public static final String USER_AVATAR = "userAvatar";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String BUSINESS_LOGO = "businessLogo";
        public static final String IS_BUSINESS_PROFILE = "businessProfile";
        public static final String LANGUAGE = "language";
        public static final String TIME_ZONE = "timezone";
        public static final String PROFILE_UUID = "profileUUID";
        public static final String USER_UUID = "userUUID";
        public static final String ROLE_NAME = "roleName";
        public static final String IS_SUPERADMIN = "superAdmin";
        public static final String PLATFORM_ROLE = "platformRole";
        public static final String STATUS = "status";
        public static final String AGREEMENT_SIGNER = "agreementSigner";
        public static final String MAX_NEGOTIATION_AMOUNT = "maxNegotiationAmount";
        public static final String EXPIRATION_DATE = "expirationDate";
        public static final String TITLE = "title";
        public static final String AUTHORIZATION_DOCUMENTS = "authorizationDocuments";
        public static final String SUPERVISOR = "supervisor";
        public static final String GUEST = "guest";
        public static final String PROFILE_WALLET_ADDRESS = "profileWalletAddress";

    }

    public static final class MembershipFilters {
        public static final String EMAIL = "user.email";
        public static final String PHONE = "user.phone";
        public static final String USER_NAME = "user.userName";
        public static final String USER = "user.uuid";
        public static final String PROFILE = "profile.uuid";
        public static final String ROLENAME = "role.name";
        public static final String STATUS = "status";
        public static final String NOT_IN_USE = "notInUse";
        public static final String NOT_IN_CONTACT = "notInContact";
        public static final String AGREEMENT_SIGNER = "agreementSigner";
        public static final String IS_ACTIVE = "isActive";
        public static final String GUEST_ONLY = "guestOnly";
        public static final String INCLUDE_GUEST = "includeGuest";
    }

    public static final class UserFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {

            DEFAULT_FIELDS = Arrays.asList("uuid", "email", "avatar", "firstName", "lastName", "fullName", "lastLogin",
                    "accountStatus", "language", "timezone", "dateJoined", "guest", "phone");
            EXPAND_FIELDS = Arrays.asList("linkedAccounts");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String USER_NAME = "userName";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String AVATAR = "avatar";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String LAST_LOGIN = "lastLogin";
        public static final String DATE_JOINED = "dateJoined";
        public static final String LANGUAGE = "language";
        public static final String TIME_ZONE = "timezone";
        public static final String LINKED_ACCOUNTS = "linkedAccounts";
        public static final String SUPERADMIN = "isSuperAdmin";
        public static final String DEFAULT_MEMBERSHIP = "defaultMembership";
        public static final String ACCOUNT_STATUS = "accountStatus";
//		public static final String MEMBERSHIPS="memberships";
    }

    public static final class UserFilters {
        public static final String NOT_ACCOUNT_STATUS = "notAccountStatus";
    }

    public static final class LinkedAccountFields {
        public static final String PROVIDER_KEY = "providerKey";
        public static final String PROVIDER_USER_ID = "providerUserId";
        public static final String USER = "user";

    }

    public static final class AuthRestFields {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String TOKEN = "token";
    }

    public static final class UserProfileFilters {
        public static final String ACTOR_UUID = "actor.uuid";
    }

    public static final class ProcessModelFilters {
        public static final String PROCESSMODEL_NAME = "processModelName";
        public static final String KEYWORDS = "keywords";
    }

    public static final class ProcessModelFields {
        public static final String PARTICIPANT_TIME_EVENTS = "participantTimeEvents";
        public static final String OWNER_TIME_EVENTS = "ownerTimeEvents";
        public static final String PROCESS_ATTRIBUTES = "processAttributes";
        public static final String TAGS = "tags";
        public static final String PRIVACY_LEVEL = "privacyLevel";
    }

    public static final class ProcessAttributeFilters {
        public static final String VISIBILITY = "visibility";
        public static final String CONFIGURABLE = "configurable";

    }

    public static final class WorkGroupFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "name", "category", "description", "created", "updated", "logo",
                    "tags", "owner", "members", "cover");
            EXPAND_FIELDS = Arrays.asList();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String OWNER = "owner";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String DESCRIPTION = "description";
        public static final String CREATE_DATE = "created";
        public static final String UPDATE_DATE = "updated";
        public static final String TAGS = "tags";
        public static final String LOGO = "logo";
        public static final String MEMBERS = "members";
        public static final String MEMBERSHIP_UUID = "membershipUUID";
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String NEGOTIATIONS = "negotiations";
    }

    public static final class PostFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "title", "description", "created", "updated", "files", "creator");
            EXPAND_FIELDS = Arrays.asList("workGroup");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String CREATOR = "creator";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CREATE_DATE = "created";
        public static final String UPDATE_DATE = "updated";
        public static final String FILES = "files";
        public static final String WORKGROUP = "workGroup";
    }

    public static final class WorkGroupCommentFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "message", "files", "creator", "created", "updated");
            EXPAND_FIELDS = new ArrayList<>();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String CREATOR = "creator";
        public static final String MESSAGE = "message";
        public static final String DESCRIPTION = "description";
        public static final String CREATE_DATE = "created";
        public static final String UPDATE_DATE = "updated";
        public static final String FILES = "files";
        public static final String POST = "post";
    }

    public static final class NotificationSettingFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "channelType", "notificationType", "allowedFrequencies", "active");
            EXPAND_FIELDS = new ArrayList<>();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String CHANNEL_TYPE = "channelType";
        public static final String NOTIFICATION_TYPE = "notificationType";
        public static final String ALLOWED_FREQUENCIES = "allowedFrequencies";
        public static final String ACTIVE = "active";
    }

    public static final class UserSettingFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "userNotificationSettings", CREATION_DATE, UPDATE_DATE);
            EXPAND_FIELDS = Arrays.asList("membership");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String MEMBERSHIP = "membership";
        public static final String USER_NOTIFICATION_SETTINGS = "userNotificationSettings";
    }

    public static final class UserSettingFilters {
        public static final String CHANNEL_TYPE = "channelType";
        public static final String NOTIFICATION_TYPE = "notificationType";
        public static final String NOTIFICATION_SETTING = "notificationSetting";
    }

    public static final class NotificationFeedFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "activity", "seen", "read", "visible", CREATION_DATE, UPDATE_DATE);
            EXPAND_FIELDS = Arrays.asList("membership");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String MEMBERSHIP = "membership";
        public static final String ACTIVITY = "activity";
        public static final String SEEN = "seen";
        public static final String READ = "read";
        public static final String VISIBLE = "visible";
    }

    public static final class ActivityFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "actor", "object", "target", "extraContext", "verb", "published",
                    "url", "message");
            EXPAND_FIELDS = Arrays.asList();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String ACTOR = "actor";
        public static final String OBJECT = "object";
        public static final String TARGET = "target";
        public static final String EXTRA_CONTEXT = "extraContext";
        public static final String VERB = "verb";
        public static final String PUBLISHED = "published";
        public static final String URL = "url";
    }

    public static final class ProjectFields {

        public static final List<String> DEFAULT_FIELDS = Arrays.asList("uuid", "name", "priority");
        public static final List<String> EXPAND_FIELDS = Arrays.asList("owner", "negotiations");

        public static final List<String> VALID_FIELDS = Arrays.asList("uuid", "name", "priority", "owner",
                "negotiations");

        public static final String OWNER = "owner";
        public static final String NAME = "name";
        public static final String PRIORITY = "priority";
        public static final String NEGOTIATIONS = "negotiations";
    }

    public static final class CategoryFilters {
        public static final String PATH = "path";
    }

    public static final class FlatContentFields {
        public static final String URL = "url";
    }

    public static final class FAQFields {
        public static final String QUESTION = "question";
        public static final String ANSWER = "answer";
    }

    public static final class InformationMessageFilters {
        public static final String SENT = "sent";
        public static final String RECEIVED = "received";
        public static final String PARTNER_UUID = "partnerUUID";
    }

    public static final class InformationMessageFields {
        public static final String RECEIVER_UUID = "receiverUUID";
        public static final String SEND_TO_ALL = "sendToAll";
        public static final String CONTENT = "content";
        public static final String FILES = "files";
    }

    public static final class AttributeFields {
        public static final String NAME = "name";
        public static final String LABEL = "label";
        public static final String VALUE = "value";
        public static final String META = "meta";
        public static final String NEGOTIABLE = "negotiable";
        public static final String LABEL_TRANSLATIONS = "labelTranslations";
        public static final String COMMENT_TRANSLATIONS = "commentTranslations";
        public static final String DATA_TYPE = "dataType";
        public static final String TAG_TYPE = "tagType";
    }

    public static final class MetaFields {
        public static final String ALLOWED_VALUES = "allowedValues";
        public static final String TYPE = "type";
        public static final String REQUIRED = "required";
        public static final String DEFAULT_VALUE = "defaultValue";
        public static final String MINIMUM = "minimum";
        public static final String MAXIMUM = "maximum";
    }

    public static class TokenActionFields {
        public final static String TOKEN = "token";
        public final static String TYPE = "type";
        public final static String TARGET_USER = "targetUser";
    }

    public static class PlanFields {
        public final static String NAME = "name";
        public final static String TITLE = "title";
        public final static String DESCRIPTION = "description";
        public final static String GRADE = "grade";
        public final static String PRICE = "price";
        public final static String PERMISSIONS = "permissions";
        public final static String PERMISSION_VALUE = "permission.value";
        public final static String PERMISSION_NAME = "permission.name";
        public final static String UPON_REQUEST = "uponRequest";
        public final static String BUSINESS = "business";
        public final static String FREE = "free";
        public final static String ACTIVE = "active";
        public final static String DEFAULT_PLAN = "defaultPlan";
        public final static String OFFER_ID = "offerID";
        public final static String COUPON_CODE = "couponCode";
        public final static String EXPIRATION_DATE = "expirationDate";
        public final static String TRIAL_PERIOD_DAYS = "trialPeriodDays";
        public final static String AMOUNT = "amount";
        public final static String CURRENCY = "currency";
        public final static String PAYMENT_INTERVAL = "paymentInterval";
        public final static String PAYMENT_INTERVAL_UNIT = "paymentIntervalUnit";
        public final static String MAX_ALLOWED_SUBSCRIPTIONS = "maxAllowedSubscriptions";
        public final static String TAGS = "tags";
    }

    public static class PermissionsFields {
        public final static String VALUE = "value";
        public final static String LIMIT = "limit";
    }

    public static class PlanSubscriptionFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {

            DEFAULT_FIELDS = Arrays.asList("uuid", CREATION_DATE, "cancelDate", "externalClientID",
                    "externalSubscriptionID", "externalPaymentConfirmationToken", "externalPaymentConfirmationType", "status", "gift", "profileUUID",
                    "planUUID");
            EXPAND_FIELDS = Arrays.asList(UPDATE_DATE, "profile", "plan", "billingAddress");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public final static String BILLING_ADDRESS = "billingAddress";
        public final static String CANCEL_DATE = "cancelDate";
        public final static String EXTERNAL_CLIENTID = "externalClientID";
        public final static String EXTERNAL_PAYMENT_CONFIRMATION_TOKEN = "externalPaymentConfirmationToken";
        public final static String EXTERNAL_SUBSCRIPTIONID = "externalSubscriptionID";
        public final static String PLAN = "plan";
        public final static String PLAN_UUID = "plan.uuid";
        public final static String BUSINESS_PROFILE_NAME = "businessProfileName";
        // only needed in payment phase, not part of the entity
        public static final String PAYMENT_TOKEN = "paymentToken";
        public static final String PROMOTION_CODE = "promotionCode";
        public static final String OFFER_ID = "offerID";
        public final static String PROFILE = "profile";
        public final static String PROFILE_UUID = "profile.uuid";
        public final static String STATUS = "status";

    }

    public static class EventFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "user", "content", "time", "logEventType", "objectUUID", "note");
            EXPAND_FIELDS = new ArrayList<>();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String USER = "user";
        public static final String CONTENT = "content";
        public static final String TIME = "time";
        public static final String LOG_EVENT_TYPE = "logEventType";
        public static final String OBJECT_UUID = "objectUUID";
        public static final String NOTE = "note";
    }

    public final static class EventFilters {
        public final static String USER_UUID = "user.uuid";
    }

    public static class NegotiationEventFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;
        public static final String OTHER_USER = "otherUser";
        public static final String NEGOTIATION_MESSAGE = "negotiationMessage";
        public static final String MESSAGE_UUID = "messageUUID";
        public static final String MESSAGE_TYPE = "messageType";

        static {
            DEFAULT_FIELDS = Arrays.asList(UUID, EventFields.USER, EventFields.CONTENT, EventFields.TIME,
                    EventFields.LOG_EVENT_TYPE, EventFields.OBJECT_UUID, OTHER_USER, EventFields.NOTE, MESSAGE_UUID,
                    MESSAGE_TYPE);
            EXPAND_FIELDS = new ArrayList<>(Arrays.asList(NEGOTIATION_MESSAGE));
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }
    }

    public static final class ContactFilters {
        public static final String COMMENT = "comment";
        public final static String PROFILE_TYPE = "profileType";
        public final static String USER_UUID = "user.uuid";
    }

    public static final class ContactFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "created", "updated", "name", "organization", "email", "phone",
                    "address", "city", "country", "note", "linkedMembershipIdentifier", "tags", "externalId");
            EXPAND_FIELDS = Arrays.asList("linkedMembership", "owner");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String ADDRESS = "address";
        public static final String PHONE = "phone";
        public static final String NOTE = "note";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String TAGS = "tags";
        public static final String LINKED_MEMBERSHIP = "linkedMembership";
        public static final String LINKED_MEMBERSHIP_IDENTIFIER = "linkedMembershipIdentifier";
        public static final String OWNER = "owner";
        public static final String OWNER_PROFILE = "ownerProfile";
        public final static String EXTERNAL_ID = "externalId";
    }

    public static final class TemplateFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        public static final String NAME = "name";
        public static final String CONTENT = "content";
        public static final String HEADER = "header";
        public static final String FOOTER = "footer";
        public static final String TEMPLATE_TYPE = "templateType";
        public static final String OWNER_MEMBERSHIP = "ownerMembership";
        static {
            DEFAULT_FIELDS = Arrays.asList(UUID, NAME, TEMPLATE_TYPE, CONTENT, HEADER, FOOTER, CREATION_DATE, UPDATE_DATE);
            EXPAND_FIELDS = Collections.singletonList(OWNER_MEMBERSHIP);
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

    }

    public static final class AuthorisationFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "sender", "receivers", "responses", CREATION_DATE, UPDATE_DATE,
                    "note", "objectID", "objectType", "status", "acceptanceRule", "priority", "responseDeadline",
                    "extraContext", "title");
            EXPAND_FIELDS = Arrays.asList("canAuthorise");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String SENDER = "sender";
        public static final String RECEIVERS = "receivers";
        public static final String RESPONSES = "responses";
        public static final String NOTE = "note";
        public static final String OBJECTID = "objectID";
        public static final String OBJECTTYPE = "objectType";
        public static final String STATUS = "status";
        public static final String ACCEPTANCERULE = "acceptanceRule";
        public static final String PRIORITY = "priority";
        public static final String RESPONSE_DEADLINE = "responseDeadline";
        public static final String EXTRA_CONTEXT = "extraContext";

        public static final String EXTRA_CONTEXT_NEGOTIAION_UUID = "negotiationUUID";
        public static final String EXTRA_CONTEXT_ACTION = "action";
        public static final String EXTRA_CONTEXT_NOTE = "note";
    }

    public static class AuthorisationFilters {
        public static final String SENT = "sent";
        public static final String RECEIVED = "received";
        public static final String REQUEST_TYPE = "requestType";
        public static final String SENDER_UUID = "senderUUID";
        public static final String CREATED_AFTER_INCLUSIVE = "createdAfterInclusive";
    }

    public static final class ReviewRequestFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "creator", "negotiationUUID", "offerUUIDs", CREATION_DATE,
                    UPDATE_DATE, "status", "responseDeadline", "reviewStatusPerReviewer", "note", "reviewTemplateUUID");
            EXPAND_FIELDS = Arrays.asList("offers", "reviewers", "reviews", "negotiation");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String CREATOR = "creator";
        public static final String REVIEWERS = "reviewers";
        public static final String REVIEWS = "reviews";
        public static final String STATUS = "status";
        public static final String RESPONSE_DEADLINE = "responseDeadline";
        public static final String OFFERS = "offers";
        public static final String OFFER_UUIDS = "offerUUIDs";
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String REVIEWER_UUIDS = "reviewerUUIDs";
        public static final String NOTE = "note";
        public static final String REVIEW_TEMPLATE_UUID = "reviewTemplateUUID";
    }

    public static class ReviewRequestFilters {
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String REVIEWER_UUID = "reviewerUUID";
        public static final String CREATOR_UUID = "creatorUUID";
        public static final String CREATOR_PROFILE_UUID = "creatorProfileUUID";
        public static final String STATUS = "status";
        public static final String OFFER_UUID = "offerUUID";
        public static final String CREATED_AFTER_INCLUSIVE = "createdAfterInclusive";
        public static final String REVIEW_TEMPLATE_UUID = "reviewTemplateUUID";
    }

    public static final class ReviewFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "reviewer", "negotiationUUID", "offerUUID", "reviewRequestUUID",
                    CREATION_DATE, UPDATE_DATE, "comment", "score", "status", "files", "reviewTemplateUUID");
            EXPAND_FIELDS = Arrays.asList("offer", "negotiation", "reviewRequest");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String REVIEWER = "reviewer";
        public static final String COMMENT = "comment";
        public static final String SCORE = "score";
        public static final String STATUS = "status";
        public static final String REVIEW_REQUEST_UUID = "reviewRequestUUID";
        public static final String OFFER_UUID = "offerUUID";
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String FILES = "files";
    }

    public static class ReviewFilters {
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String REVIEWER_UUID = "reviewerUUID";
        public static final String OFFER_UUID = "offerUUID";
        public static final String REVIEW_REQUEST_UUID = "reviewRequestUUID";
        public static final String STATUS = "status";
        public static final String OWN_REVIEW_ONLY = "ownReviewOnly";
        public static final String REQUESTOR_PROFILE_UUID = "requestorProfileUUID";
    }

    public static final class BlockchainTransactionFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "objectType", "objectId", "transactionId", "contractAddress",
                    CREATION_DATE, UPDATE_DATE, "customFields", "status");
            EXPAND_FIELDS = Arrays.asList();
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String OBJECT_TYPE = "objectType";
        public static final String OBJECT_ID = "objectId";
        public static final String TRANSACTION_ID = "transactionId";
        public static final String CONTRACT_ADDRESS = "contractAddress";
        public static final String CUSTOM_FIELDS = "customFields";
        public static final String STATUS = "status";
    }

    public static final class BlockchainWalletFields {

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", "address", CREATION_DATE, UPDATE_DATE, "blockchainType");
            EXPAND_FIELDS = Arrays.asList("owner");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String ADDRESS = "address";
        public static final String OWNER = "owner";
        public static final String BLOCKCHAIN_TYPE = "blockchainType";
        public static final String PRIVATE_KEY = "privateKey";
        public static final String PASSWORD = "password";
    }

    public static final class AssetFields {

        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String DESCRIPTION = "description";
        public static final String OWNER = "owner";
        public static final String OWNER_PROFILE = "ownerProfile";
        public static final String TYPE = "type";
        public static final String STATUS = "status";
        public static final String SERIAL_NUMBER = "serialNumber";
        public static final String ASSIGNED_TO = "assignedTo";
        public static final String ASSIGNED_TO_PROFILE = "assignedToProfile";
        public static final String FILES = "files";
        public static final String IMAGES = "images";
        public static final String VIDEOS = "videos";
        public static final String MEASURE = "measure";
        public static final String FROM_ASSET_MEASURES = "fromAssetMeasures";
        public static final String EXTERNAL_ID = "externalId";
        public static final String DATA_SOURCE = "dataSource";
        public static final String TAGS = "tags";
        public static final String GPS_POSITION = "gpsPosition";
        public static final String SUB_ASSETS = "subAssets";
        public static final String SUB_ASSET_UUIDS = "subAssetUUIDs";
        public static final String CUSTOM_FIELDS = "customFields";
        public static final String ASSET_SCHEMA_UUID = "assetSchemaUUID";
        public static final String FIELDS = "fields";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, NAME, CREATION_DATE, UPDATE_DATE, TYPE,
                    STATUS, DESCRIPTION, SERIAL_NUMBER, GPS_POSITION, FILES, IMAGES, VIDEOS, MEASURE, CATEGORY,
                    EXTERNAL_ID, DATA_SOURCE, TAGS, SUB_ASSET_UUIDS, ASSET_SCHEMA_UUID, FIELDS));
            EXPAND_FIELDS = Collections.unmodifiableList(
                    Arrays.asList(OWNER, ASSIGNED_TO, SUB_ASSETS, CUSTOM_FIELDS, FROM_ASSET_MEASURES));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class AssetSchemaFields {
        public static final String ACTIVE = "active";
        public static final String BLOCKCHAIN_TX = "blockchainTx";
        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String TITLE_TRANSLATIONS = "titleTranslations";
        public static final String DESCRIPTION = "description";
        public static final String DESCRIPTION_TRANSLATIONS = "descriptionTranslations";
        public static final String AUTHOR_MEMBERSHIP_UUID = "authorMembershipUUID";
        public static final String AUTHOR_MEMBERSHIP = "authorMembership";
        public static final String LANGUAGE = "language";
        public static final String TAGS = "tags";
        public static final String CATEGORY = "category";
        public static final String SUPPORTED_LANGUAGES = "supportedLanguages";

        public static final String ITEMS = "items";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, BLOCKCHAIN_TX, NAME, CREATION_DATE,
                    UPDATE_DATE, DESCRIPTION, CATEGORY, TAGS, ACTIVE, TITLE, AUTHOR_MEMBERSHIP_UUID, LANGUAGE,
                    SUPPORTED_LANGUAGES, ITEMS, TITLE_TRANSLATIONS, DESCRIPTION_TRANSLATIONS));
            EXPAND_FIELDS = Collections.unmodifiableList(
                    Arrays.asList(AUTHOR_MEMBERSHIP, ITEMS, TITLE_TRANSLATIONS, DESCRIPTION_TRANSLATIONS));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }

    }

    public static final class AssetSchemaFilters {
        public static final String PROFILE = "profile";
        public static final String INCLUDE_INACTIVE = "includeInactive";
    }

    public static final class SignatureContainerFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", CREATION_DATE, UPDATE_DATE, "negotiationID",
                    "originalAgreementPDFUUID", "finalSignedAgreementPDFUUID", "nextToSign", "signatureType",
                    "nextSigner");
            EXPAND_FIELDS = Arrays.asList("allUploaded");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String NEGOTIATION_ID = "negotiationID";
        public static final String ORIGINAL_AGREEMENT_PDF_UUID = "originalAgreementPDFUUID";
        public static final String FINAL_SIGNED_AGREEMENT_PDF_UUID = "finalSignedAgreementPDFUUID";
    }

    public static final class ContractFields {

        public static final String AGREEMENT_INFORMATION_MODEL = "agreementInformationModel";
        public static final String AGREEMENT_INFORMATION_MODEL_TEMPLATE_UUID = "agreementInformationModelTemplateUUID";
        public static final String AGREEMENT_DATA = "agreementData";
        public static final String AGREEMENT_UUID = "agreementUUID";
        public static final String ARCHIVED = "archived";
        public static final String ATTACHMENTS = "attachments";
        public static final String BLOCKCHAIN_ENABLED = "blockchainEnabled";
        public static final String CATEGORY = "category";
        public static final String CHANGING_CONTRACT_TYPE_ALLOWED = "changingContractTypeAllowed";
        public static final String CONTRACT_FILES = "contractFiles";
        public static final String CONTRACTING_PARTIES = "contractingParties";
        public static final String CONTRACT_NUMBER = "contractNumber";
        public static final String CONTRACT_TYPE = "contractType";
        public static final String CREATED_FROM_NEGOTIATION_TEMPLATE_UUID = "createdFromNegotiationTemplateUUID";
        public static final String CREATOR = "creator";
        public static final String CURRENCY_CODE = "currencyCode";
        public static final String DESCRIPTION = "description";
        public static final String EFFECTIVE_DATE = "effectiveDate";
        public static final String EXPIRY_DATE = "expiryDate";
        public static final String GOVERNING_LAW = "governingLaw";
        public static final String IMPORTED = "imported";
        public static final String LIFECYCLE_EVENTS = "lifecycleEvents";
        public static final String LIFECYCLE_SETTING = "lifecycleSetting";
        public static final String NEGOTIATION = "negotiation";
        public static final String NEGOTIATION_TEMPLATES = "negotiationTemplates";
        public static final String NEGOTIATION_TEMPLATE_UUIDS = "negotiationTemplateUUIDs";
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String NOTE = "note";
        public static final String OTHER_PARTY_MEMBERS = "otherPartyMembers";
        public static final String OTHER_PARTY_MEMBER_UUIDS = "otherPartyMemberUUIDs";
        public static final String OWN_PARTY_MEMBERS = "ownPartyMembers";
        public static final String OWN_PARTY_MEMBER_UUIDS = "ownPartyMemberUUIDs";
        public static final String OWNER = "owner";
        public static final String OWNER_PROFILE = "ownerProfile";
        public static final String OWNER_PROFILE_UUID = "ownerProfileUUID";
        public static final String PARENT_CONTRACT_UUID = "parentContractUUID";
        public static final String REFERENCE_ID = "referenceId";
        public static final String RENEWABLE = "renewable";
        public static final String TERMINATION_NOTICE_REQUIRED = "terminationNoticeRequired";
        public static final String RISK = "risk";
        public static final String SOURCE_CONTRACT_UUID = "sourceContractUUID";
        public static final String SIGNATURE_CONTAINER_UUID = "signatureContainerUUID";
        public static final String STATUS = "status";
        public static final String SUPPORTING_DOCUMENT_NEGOTIATIONS = "supportingDocumentNegotiations";
        public static final String SUPPORTING_DOCUMENT_NEGOTIATION_UUIDS = "supportingDocumentNegotiationUUIDs";
        public static final String AMENDMENT_CONTRACT_NEGOTIATIONS = "amendmentContractNegotiations";
        public static final String AMENDMENT_CONTRACT_NEGOTIATION_UUIDS = "amendmentContractNegotiationUUIDs";
        public static final String TAGS = "tags";
        public static final String TITLE = "title";
        public static final String VALUE = "value";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, CREATION_DATE, UPDATE_DATE, TITLE,
                    DESCRIPTION, CATEGORY, EFFECTIVE_DATE, EXPIRY_DATE, CURRENCY_CODE, VALUE, STATUS, ARCHIVED,
                    NEGOTIATION_UUID, GOVERNING_LAW, RISK, OWNER, OWNER_PROFILE_UUID, RENEWABLE, REFERENCE_ID,
                    AGREEMENT_UUID, SIGNATURE_CONTAINER_UUID, NOTE, CONTRACT_TYPE, NEGOTIATION_TEMPLATE_UUIDS,
                    SUPPORTING_DOCUMENT_NEGOTIATION_UUIDS, CONTRACT_NUMBER, CREATED_FROM_NEGOTIATION_TEMPLATE_UUID,
                    BLOCKCHAIN_ENABLED, TERMINATION_NOTICE_REQUIRED, PARENT_CONTRACT_UUID, IMPORTED,
                    CHANGING_CONTRACT_TYPE_ALLOWED, SOURCE_CONTRACT_UUID));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(OWN_PARTY_MEMBERS, OTHER_PARTY_MEMBERS,
                    CONTRACT_FILES, ATTACHMENTS, TAGS, CREATOR, NEGOTIATION_TEMPLATES, AGREEMENT_INFORMATION_MODEL,
                    LIFECYCLE_SETTING, LIFECYCLE_EVENTS, AGREEMENT_DATA));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }

        public static final String NEGOTIATION_ID = "negotiationID";
        public static final String ORIGINAL_AGREEMENT_PDF_UUID = "originalAgreementPDFUUID";
        public static final String FINAL_SIGNED_AGREEMENT_PDF_UUID = "finalSignedAgreementPDFUUID";
    }

    public static final class ContractFilters {
        public static final String PROFILE_UUID = "profileUUID";
        public static final String CONTRACT_TYPE = "contractType";
        public static final String INCLUDE_ARCHIVED = "includeArchived";
        public static final String OTHER_PARTY = "otherParty";
        public static final String LINKED_CONTRACT_WITH_NEGOTIATION_TEMPLATE_UUIDS = "linkedContractWithNegotiationTemplateUUIDs";
        public static final String NEGOTIATION_TEMPLATE_UUID = "negotiationTemplateUUID";
        public static final String SUPPORTING_DOCUMENT_NEGOTIATION_UUID = "supportingDocumentNegotiationUUID";
        public static final String TERMINATION_NOTICE_DATE = "terminationNoticeDate";
    }

    public static final class ContractLifecycleSettingFields {
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String RENEWABLE = "renewable";
        public static final String RENEW_START_DATE = "renewStartDate";
        public static final String RENEW_END_DATE = "renewEndDate";
        public static final String RENEW_DURATION_VALUE = "renewDurationValue";
        public static final String RENEW_DURATION_UNIT = "renewDurationUnit";
        public static final String RENEW_OCCURRENCE_LIMIT = "renewOccurrenceLimit";
        public static final String TERMINATION_NOTICE_REQUIRED = "terminationNoticeRequired";
        public static final String TERMINATION_NOTICE_PERIOD_VALUE = "terminationNoticePeriodValue";
        public static final String TERMINATION_NOTICE_PERIOD_UNIT = "terminationNoticePeriodUnit";
        public static final String RENEW_DURATION_IN_MILLISECONDS = "renewDurationInMilliseconds";
        public static final String TERMINATION_NOTICE_PERIOD_IN_MILLISECONDS = "terminationNoticePeriodInMilliseconds";
    }

    public static final class TeamFields {
        public static final String PARTY_TYPE = "partyType";
        public static final String PROFILE = "profile";
        public static final String PROFILE_UUID = "profileUUID";
        public static final String OBJECT_TYPE = "objectType";
        public static final String OBJECT_ID = "objectID";
        public static final String MEMBERS = "members";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, PARTY_TYPE, OBJECT_TYPE, OBJECT_ID,
                    MEMBERS, PROFILE_UUID, CREATION_DATE, UPDATE_DATE));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(PROFILE));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class TeamFilters {

    }

    public static final class KycFields {
        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList("uuid", CREATION_DATE, UPDATE_DATE, "rawRequest", "rawResponse",
                    "rawDocumentUploadRequest", "rawDocumentUploadResponse", "documentUID",
                    "documentVerificationResult", "bundleName", "serviceName", "serviceCallReference",
                    "interpretedResult");
            EXPAND_FIELDS = Arrays.asList("offer", "negotiation");
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

        public static final String SERVICE_CALL_REFERENCE = "serviceCallReference";
        public static final String INTERPRETED_RESULT = "interpretedResult";
    }

    public static class KycFilters {
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String OFFER_UUID = "offerUUID";
        public static final String CLIENT_REFERENCE = "clientReference";
    }

    public static final class IntegrationSettingFields {
        public static final String ACTIVE = "active";
        public static final String INTEGRATION_SERVICE_TYPE = "integrationServiceType";
        public static final String PROFILE = "profile";
        public static final String SETTINGS = "settings";

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(
                    Arrays.asList(UUID, CREATION_DATE, UPDATE_DATE, INTEGRATION_SERVICE_TYPE, SETTINGS, ACTIVE));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(PROFILE));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }

    }

    public static class IntegrationSettingFilters {
        public static final String PROFILE_UUID = "profileUUID";
        public static final String NEGOTIATION_UUID = "negotiation_uuid";
        public static final String SERVICE_TYPE = "service_type";
        public static final String UUID = "uuid";
        public static final String INTEGRATIOM_ENABLED = "integration-enabled";
    }

    public static final class QiiDataFields {

        public static final String HOUSEHOLD_ID = "householdId";
        public static final String MEMBERSHIP = "membership";
        public static final String MEMBERSHIP_UUID = "membershipUUID";
        public static final String MEMBERS = "members";
        public static final String NEGOTIATION = "negotiation";
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String REQUEST_MANAGEMENT_ID = "requestManagementId";
        public static final String REQUEST_MANAGEMENT_LINK_NAME = "requestManagementLinkName";
        public static final String REQUEST_MANAGEMENT_NAME = "requestManagementName";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, HOUSEHOLD_ID, MEMBERSHIP_UUID,
                    NEGOTIATION_UUID, REQUEST_MANAGEMENT_ID, REQUEST_MANAGEMENT_LINK_NAME, REQUEST_MANAGEMENT_NAME));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(MEMBERSHIP, NEGOTIATION, MEMBERS));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static class QiiDataFilters {
        public static final String NEGOTIATION_UUID = "negotiationUUID";
        public static final String MEMBERSHIP_UUID = "membershipUUID";
    }

    public static final class AnalyticRequestFields {
        public static final String METRICS = "metrics";
        public static final String DIMENSIONS = "dimensions";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String OBJECT_ID = "objectID";
    }

    public static final class VersionFields {
        public static final String VERSION = "version";
        public static final String PROGRESSIVE_RELEASE = "progressiveRelease";
        public static final String INSTALLED = "installed";
    }

    public static final class CustomHttpHeaderFields {
        public static final String TOKEN = "XRD-TOKEN";
        public static final String MEMBERSHIP_UUID = "XRD-USER-IDENTIFIER";
    }

    public static final class Uris {
        public static String ADMISSION = Resources.NEGOTIATIONS + "/{" + NegotiationFilters.NEGOTIATION_UUID + "}/"
                + Resources.ADMISSIONS;
        public static String STATUS = Resources.NEGOTIATIONS + "/{" + NegotiationFilters.NEGOTIATION_UUID + "}/"
                + Resources.STATUS;
        public static String MESSAGEDRAFT = Resources.NEGOTIATIONS + "/{" + NegotiationFilters.NEGOTIATION_UUID + "}/"
                + Resources.STATUS + "/" + Resources.MESSAGES + "/" + Resources.DRAFT;
        public static String MESSAGE = Resources.NEGOTIATIONS + "/{" + NegotiationFilters.NEGOTIATION_UUID + "}/"
                + Resources.MESSAGES + "/{" + MessageFilters.MESSAGE_UUID + "}";
        public static String MESSAGES = STATUS + "/" + Resources.MESSAGES;
        public static String FILE = Resources.FILES + "/{" + FILE_NAME + "}";
        public static String FILE_METADATA = FILE + "/" + Resources.METADATA;
        public static String INFO_MESSAGES = Resources.NEGOTIATIONS + "/{" + NegotiationFilters.NEGOTIATION_UUID + "}/"
                + Resources.INFO_MESSAGES;
        public static String INVITATIONS = Resources.NEGOTIATIONS + "/{" + NegotiationFilters.NEGOTIATION_UUID + "}/"
                + Resources.INVITATIONS;
        public static String PASSWORD_CHANGE = Resources.USERS + "/password/change";
        public static String PASSWORD_FORGOT = Resources.USERS + "/password/forgot";
        public static String PASSWORD_RESET = Resources.USERS + "/password/reset";
        public static String PROFILE_CHANGE = Resources.USERS + "/currentmembership/";
        public static String USER_AUTH = Resources.USERS + "/authenticate/";
        public static String MEMBERSHIP_ROLE = Resources.MEMBERSHIPS + "/{" + RestParameters.UUID + "}/"
                + MembershipFields.ROLE + "/{" + MembershipFields.ROLE_NAME + "}";
    }

    public static final class WebHookFields {
        public final static String ENABLED = "enabled";
        public final static String URL = "url";
        public final static String EVENTS = "events";
        public final static String PROFILE = "profile";
        public final static String CREATED_BY = "createdBy";
        public final static String UPDATED_BY = "updatedBy";

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList(UUID, CREATION_DATE, UPDATE_DATE, ENABLED, URL, EVENTS);
            EXPAND_FIELDS = Arrays.asList(PROFILE, CREATED_BY, UPDATED_BY);
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class TrakFields {
        public static final String TYPE = "type";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String STATUS = "status";
        public static final String CREATOR_UUID = "creatorUUID";
        public static final String ASSIGNEE_UUID = "assigneeUUID";
        public static final String APPROVER_UUID = "approverUUID";
        public static final String PROGRESS = "progress";
        public static final String DUE_DATE = "dueDate";
        public static final String START_DATE = "startDate";
        public static final String IS_APPROVAL_REQUIRED = "approvalRequired";
        public static final String CHECK_LIST = "checkList";
        public static final String FILES = "files";
        public static final String VALUE = "value";
        public static final String IS_PROOF_REQUIRED = "proofRequired";
        public static final String CONTRACT_UUID = "contractUUID";
        public static final String CONTRACT = "contract";
        public static final String CONTRACT_ID = "contract.uuid";
        public static final String CREATOR = "creator";
        public static final String CREATOR_ID = "creator.uuid";
        public static final String ASSIGNEE = "assignee";
        public static final String ASSIGNEE_ID = "assignee.uuid";
        public static final String APPROVER = "approver";
        public static final String APPROVER_ID = "approver.uuid";
        public static final String PARENT = "parent";
        public static final String PARENT_UUID = "parentUUID";
        public static final String PARENT_ID = "parent.uuid";
        public static final String EXTERNAL_ID = "externalId";
        public static final String CUSTOM_FIELDS = "customFields";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, TYPE, TITLE, DESCRIPTION, STATUS,
                    DUE_DATE, START_DATE, CREATION_DATE, UPDATE_DATE, VALUE, IS_PROOF_REQUIRED, PROGRESS,
                    IS_APPROVAL_REQUIRED, CONTRACT_UUID, CREATOR_UUID, ASSIGNEE_UUID, APPROVER_UUID, EXTERNAL_ID));
            EXPAND_FIELDS = Collections
                    .unmodifiableList(Arrays.asList(CONTRACT, CREATOR, ASSIGNEE, APPROVER, CHECK_LIST, FILES, PARENT, CUSTOM_FIELDS));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class TrakFilters {

    }

    public static final class TrakResponseFields {
        public static final String NOTE = "note";
        public static final String FILES = "files";
        public static final String PROGRESS = "progress";
        public static final String CHECK_LIST = "checkList";
        public static final String PROOF_FILES = "proofFiles";
        public static final String TRAK = "trak";
        public static final String TRAK_UUID = "trakUUID";
        public static final String COMPLETION_DATE = "completionDate";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections
                    .unmodifiableList(Arrays.asList(UUID, NOTE, CREATION_DATE, UPDATE_DATE, PROGRESS, COMPLETION_DATE));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(TRAK, CHECK_LIST, PROOF_FILES, FILES));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class TrakResponseFilters {
        public static final String TRAK_UUID = "trak.uuid";
        public static final String TRAK_CREATOR_UUID = "trak.creator.uuid";
        public static final String TRAK_ASSIGNEE_UUID = "trak.assignee.uuid";
        public static final String TRAK_APPROVER_UUID = "trak.approver.uuid";
    }

    public static final class TrakApprovalFields {
        public static final String NOTE = "note";
        public static final String FILES = "files";
        public static final String APPROVAL_RESPONSE_TYPE = "approvalResponseType";
        public static final String TRAK_RESPONSE = "trakResponse";
        public static final String TRAK_RESPONSE_UUID = "trakResponseUUID";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections
                    .unmodifiableList(Arrays.asList(UUID, NOTE, CREATION_DATE, UPDATE_DATE, APPROVAL_RESPONSE_TYPE));
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(TRAK_RESPONSE, FILES));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class TrakApprovalFilters {
        public static final String TRAK_RESPONSE_UUID = "trakResponse.uuid";
        public static final String TRAK_UUID = "trakResponse.trak.uuid";
        public static final String TRAK_APPROVER_UUID = "trakResponse.trak.approver.uuid";
    }

    public static final class ClauseFields {
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CATEGORY = "category";
        public static final String TAGS = "tags";
        public static final String STANDARD_TEMPLATE_UUID = "standardTemplateUUID";
        public static final String ALTERNATIVE_TEMPLATES_UUIDS = "alternativeTemplatesUUIDS";
        public static final String CREATOR = "creator";
        public static final String PROFILE = "profile";
        public static final String PUBLICATION_STATUS = "publicationStatus";
        public static final String ARCHIVED = "archived";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            // PRIMITIVE DATA TYPE
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(CREATION_DATE, UPDATE_DATE, UUID, TITLE, DESCRIPTION, CATEGORY, STANDARD_TEMPLATE_UUID, PUBLICATION_STATUS, ARCHIVED));
            // USER DEFINED DATA TYPE AND ARRAY
            EXPAND_FIELDS = Collections.unmodifiableList(Arrays.asList(TAGS, PROFILE, CREATOR, ALTERNATIVE_TEMPLATES_UUIDS));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }
    }

    public static final class UpcomingLifecycleEventFields {
        public static final String EVENT_UUID = "eventUUID";
        public static final String OBJECT_TYPE = "objectType";
        public static final String EVENT_TYPE = "eventType";
        public static final String DATE = "date";
        public static final String MEMBERS = "members";

        public static final List<String> DEFAULT_FIELDS;
        public static final List<String> EXPAND_FIELDS;
        public static final List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Collections.unmodifiableList(Arrays.asList(UUID, EVENT_UUID, OBJECT_TYPE, EVENT_TYPE, DATE));
            EXPAND_FIELDS = Collections.unmodifiableList(Collections.singletonList(MEMBERS));
            VALID_FIELDS = Collections.unmodifiableList(ExCollections.merge(DEFAULT_FIELDS, EXPAND_FIELDS));
        }

    }

    public static final class ExportRequestFields {
        public static final String PROFILE = "profile";
        public static final String REQUESTOR = "requestor";
        public static final String EXPORT_STATUS = "status";
        public static final String OBJECT_TYPE = "objectType";
        public static final String EXPORTED_FILE_URL = "exportedFileUrl";
        public static final String INCLUDE_FILES = "includeFiles";
        public static final String QUERY_FILTERS = "queryFilters";

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;

        static {
            DEFAULT_FIELDS = Arrays.asList(UUID, EXPORT_STATUS, OBJECT_TYPE, EXPORTED_FILE_URL, INCLUDE_FILES, CREATION_DATE, UPDATE_DATE);
            EXPAND_FIELDS = Arrays.asList(PROFILE, REQUESTOR, QUERY_FILTERS);
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }
    }

    public static final class ImportRequestFields {
        public static final String PROFILE = "profile";
        public static final String REQUESTOR = "requestor";
        public static final String USER_UUID = "requestor.uuid";
        public static final String USER_PROFILE_UUID = "profile.uuid";
        public static final String IMPORT_STATUS = "status";
        public static final String IMPORT_FILE = "importFile";
        public static final String OBJECT_TYPE = "objectType";
        public static final String ATTACHED_FILE = "attachedFile";
        public static final String PROPERTY_MAP = "propertyMap";
        public static final String COLUMN_SEPARATOR = "columnSeparator";
        public static final String RESULTS = "results";

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;


        static {
            DEFAULT_FIELDS = Arrays.asList(UUID, IMPORT_STATUS, CREATION_DATE, UPDATE_DATE, IMPORT_FILE, OBJECT_TYPE, COLUMN_SEPARATOR);
            EXPAND_FIELDS = Arrays.asList(PROFILE, REQUESTOR, ATTACHED_FILE, PROPERTY_MAP, RESULTS);
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }
    }

    public static final class PaymentFields {
        public static final String OBJECT_ID = "objectId";
        public static final String OBJECT_TYPE = "objectType";
        public static final String STATUS = "status";
        public static final String PAYMENT_PROVIDER_RESPONSE = "paymentProviderResponse";
        public static final String PLAN_ID = "planId";
    }

    public static final class PaymentRawFields {
        public static final String ALLOWED_PAYMENT_METHODS = "allowedPaymentMethods";
        public static final String PAYMENT_STATUS = "paymentStatus";
        public static final String PAYMENT_KEY = "paymentKey";
        public static final String AMOUNT = "amount";
        public static final String CURRENCY_CODE = "currencyCode";
        public static final String PAYER_TYPE = "payerType";
        public static final String PAYMENT_PROVIDER_RESPONSE = "paymentProviderResponse";
        public static final String PLAN_ID = "planId";

        public static List<String> DEFAULT_FIELDS;
        public static List<String> EXPAND_FIELDS;
        public static List<String> VALID_FIELDS;


        static {
            DEFAULT_FIELDS = Arrays.asList(UUID, ALLOWED_PAYMENT_METHODS, PAYMENT_STATUS, AMOUNT, PLAN_ID, PAYER_TYPE);
            EXPAND_FIELDS = Arrays.asList(PAYMENT_KEY, CURRENCY_CODE, PAYMENT_PROVIDER_RESPONSE);
            VALID_FIELDS = new ArrayList<>(DEFAULT_FIELDS);
            VALID_FIELDS.addAll(EXPAND_FIELDS);
        }

    }

}
