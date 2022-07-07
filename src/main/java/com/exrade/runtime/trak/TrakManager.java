package com.exrade.runtime.trak;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.Verb;
import com.exrade.models.contract.*;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.trak.*;
import com.exrade.models.trak.dto.*;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExValidationException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.TrakNotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.trak.persistence.TrakApprovalQuery;
import com.exrade.runtime.trak.persistence.TrakQuery;
import com.exrade.runtime.trak.persistence.TrakResponseQuery;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;

import java.util.*;

import static com.exrade.models.trak.TrakStatus.*;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.runtime.trak
 */
public class TrakManager implements ITrakManager {

    private final PersistentManager persistenceManager = new PersistentManager();
    private final ContractManager contractManager;
    private final MembershipManager membershipManager = new MembershipManager();
    private final NotificationManager notificationManager = new NotificationManager();


    public TrakManager(ContractManager contractManager) {
        if (contractManager == null)
            this.contractManager = new ContractManager();
        else this.contractManager = contractManager;
    }

    @Override
    public Trak createTrak(TrakCreateDTO iTrak) {

        Trak trak = getTrak(new Trak(), iTrak);
        if (iTrak.getParentUUID() != null) {
            //it's parent so create sub trak
            Trak parent = getTrakByUUID(iTrak.getParentUUID());
            trak.setParent(parent);
        }
        //normal trak
        trak.setStatus(TrakStatus.NOT_STARTED);
        trak.setProgress(0);
        trak.setExternalId(iTrak.getExternalId());
        trak.setBlockchainEnabled(iTrak.getBlockchainEnabled());
        persistenceManager.create(trak);


        //set updated contract to trak;
        trak.setContract(contractManager
                .getContractByUUID(trak.getContractUUID()));
        //create life cycle event
        createLifeCycleEvent(trak);

        //sent notification
        notificationManager
                .process(new TrakNotificationEvent(
                        NotificationType.TRAK_CREATED,
                        trak));

        List<Negotiator> members = new ArrayList<>();
        if (trak.getAssignee() != null)
            members.add(trak.getAssignee());
        if (trak.getApprover() != null)
            members.add(trak.getApprover());

        ActivityLogger.log(ContextHelper.getMembership(),
                Verb.CREATE,
                trak, members);
        return trak;
    }

    @Override
    public List<Trak> listTraks(Map<String, String> iFilters) {
        QueryFilters filters = QueryFilters.create(iFilters);
        filters.putIfNotEmpty(RestParameters.UUID, iFilters.get(RestParameters.UUID));
        filters.putIfNotEmpty(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
        filters.putIfNotEmpty(RestParameters.TrakFields.CREATOR_UUID, iFilters.get(RestParameters.TrakFields.CREATOR_UUID));
        filters.putIfNotEmpty(RestParameters.TrakFields.ASSIGNEE_UUID, iFilters.get(RestParameters.TrakFields.ASSIGNEE_UUID));
        filters.putIfNotEmpty(RestParameters.TrakFields.CONTRACT_UUID, iFilters.get(RestParameters.TrakFields.CONTRACT_UUID));
        filters.putIfNotNull(RestParameters.TrakFields.START_DATE, iFilters.get(RestParameters.TrakFields.START_DATE));
        filters.putIfNotNull(RestParameters.TrakFields.DUE_DATE, iFilters.get(RestParameters.TrakFields.DUE_DATE));
        filters.putIfNotEmpty(RestParameters.TrakFields.STATUS, iFilters.get(RestParameters.TrakFields.STATUS));
        filters.putIfNotEmpty(RestParameters.TrakFields.TYPE, iFilters.get(RestParameters.TrakFields.TYPE));
        filters.putIfNotEmpty(RestParameters.TrakFields.EXTERNAL_ID, iFilters.get(RestParameters.TrakFields.EXTERNAL_ID));
        filters.putIfNotEmpty(OrientSqlBuilder.QueryParameters.SORT, iFilters.get(OrientSqlBuilder.QueryParameters.SORT));

        if (iFilters.containsKey(RestParameters.TrakFields.PARENT_UUID))
            filters.put(RestParameters.TrakFields.PARENT_UUID, iFilters.get(RestParameters.TrakFields.PARENT_UUID));

        //returning only allowed trak
        List<Trak> allowedTrak = new ArrayList<>();
        for (Trak trak : getTrakList(filters)) {
            try {
                checkGetPermission(trak);
            } catch (ExAuthorizationException e) {
                continue;
            }
            allowedTrak.add(trak);
        }
        return allowedTrak;
    }

    @Override
    public Trak updateTrak(TrakUpdateDTO iTrak) {
        Trak trak = this.getTrakByUUID(iTrak.getUuid());
        //set status
        if (iTrak.getStatus() != null)
            trak.setStatus(iTrak.getStatus());
        //set progress
        if (iTrak.getProgress() != null)
            trak.setProgress(iTrak.getProgress());
        getTrak(trak, iTrak);
        persistenceManager.update(trak);

        //update lifeCycleEvent
        updateLifeCycleEvent(trak);

        //sent notification
        notificationManager
                .process(new TrakNotificationEvent(
                        NotificationType.TRAK_UPDATED,
                        trak));

        List<Negotiator> members = new ArrayList<>();
        if (trak.getAssignee() != null)
            members.add(trak.getAssignee());
        if (trak.getApprover() != null)
            members.add(trak.getApprover());
        ActivityLogger.log(ContextHelper.getMembership(), Verb.UPDATED,
                trak, members);
        ExLogger.get().info("Updated trak: {} - {}",
                trak.getUuid(), trak.getTitle());

        //trak updated, now calling smart contract
        //contractManager.initSmartContract(trak.getContract());

        return trak;
    }

    private Trak getTrakByUUID(String iTrakUUID) {
        Trak trak = persistenceManager.readObjectByUUID(Trak.class, iTrakUUID);
        if (trak == null)
            throw new ExNotFoundException("Trak not found for Id " + iTrakUUID);
        return trak;
    }

    @Override
    public Trak getTrak(String iTrakUUID) {
        Trak trak = persistenceManager.readObjectByUUID(Trak.class, iTrakUUID);
        if (trak == null)
            throw new ExNotFoundException("Trak not found for Id " + iTrakUUID);
        checkGetPermission(trak);
        return trak;
    }

    @Override
    public void deleteTrak(String iTrakUUID) {
        Trak trak = this.getTrakByUUID(iTrakUUID);
        if (!trak.getCreatorUUID().equals(ContextHelper.getMembershipUUID()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
        persistenceManager.delete(trak);
        ActivityLogger.log(ContextHelper.getMembership(), Verb.DELETE, trak, Arrays.asList(trak.getAssignee(), trak.getApprover()));
        ExLogger.get().info("Deleted trak: {}", iTrakUUID);
    }

    @Override
    public TrakResponse createTrakResponse(TrakResponseCreateDTO dto) {
        Trak trak = getTrakByUUID(dto.getTrakUUID());

        //check membership eligibility
        checkTrakPermission(trak, dto.getMemberUuid());

        TrakResponse response = dto.getResponse();

        //check trak status started or in progress
        if (!(trak.getStatus().equals(TrakStatus.NOT_STARTED) ||
                trak.getStatus().equals(TrakStatus.IN_PROGRESS)))
            throw new ExValidationException(ErrorKeys.TRAK_RESPONSE_CREATE_ERROR);

        //now checking any trak response already active or not
        if (checkTrakResponsePermission(trak.getUuid()))
            throw new ExValidationException(ErrorKeys.TRAK_RESPONSE_ALREADY_ACTIVE);

        //now checking any trak response approval is null
        if (checkTrakResponsePermissionForApproval(trak.getUuid()))
            throw new ExValidationException(ErrorKeys.TRAK_APPROVAL_NOT_COMPETE);


        //update trak status to in progress
        trak.setStatus(TrakStatus.IN_PROGRESS);
        setTrakProgress(trak, response);

        //now add track to trak response
        response.setTrak(trak);
        response = persistenceManager.create(response);
        updateParentTrak(response.getTrak());


        //sent notification
        notificationManager
                .process(new TrakNotificationEvent(NotificationType.TRAK_RESPONSE_CREATED,
                        response));

        ActivityLogger.log(ContextHelper.getMembership(), Verb.CREATE,
                response,
                Arrays.asList(trak.getCreator(), trak.getApprover()));
        ExLogger.get().info("Created trak response: {} - {}",
                response.getUuid(), response.getNote());

        return response;
    }

    @Override
    public List<TrakResponse> listTrakResponse(String trakUUID, String membershipUUID) {
        QueryFilters filters = QueryFilters.create(new HashMap<>());
        filters.putIfNotEmpty(RestParameters.TrakResponseFilters.TRAK_UUID, trakUUID);
        filters.putIfNotEmpty(RestParameters.TrakResponseFilters.TRAK_ASSIGNEE_UUID, membershipUUID);
        List<TrakResponse> responses = persistenceManager.listObjects(new TrakResponseQuery(), filters);
        //returning only allowed trak response
        List<TrakResponse> allowedResponses = new ArrayList<>();
        for (TrakResponse trakResponse : responses) {
            try {
                checkGetPermission(trakResponse.getTrak());
            } catch (ExAuthorizationException e) {
                continue;
            }
            allowedResponses.add(trakResponse);
        }
        return allowedResponses;
    }

    @Override
    public TrakResponse updateTrakResponse(TrakResponseCreateDTO dto) {
        //get trak checklist
        TrakResponse oldResponse = getTrakResponseByUUID(dto.getResponseUUID());

        //check membership eligibility
        checkTrakPermission(oldResponse.getTrak(), dto.getMemberUuid());

        TrakResponse response = dto.getResponse();
        if (response.getNote() != null)
            oldResponse.setNote(response.getNote());
        if (response.getCompletionDate() != null)
            oldResponse.setCompletionDate(response.getCompletionDate());
        else
            oldResponse.setCompletionDate(TimeProvider.now());

        setTrakProgress(oldResponse.getTrak(), response);

        if (response.getProgress() != null) {
            oldResponse.setProgress(response.getProgress());
        }

        if (response.getCheckList() != null && !response.getCheckList().isEmpty()) {
            oldResponse.setCheckList(response.getCheckList());
        }
        if (response.getFiles() != null && !response.getFiles().isEmpty()) {
            oldResponse.setFiles(response.getFiles());
        }

        if (response.getProofFiles() != null && !response.getProofFiles().isEmpty())
            oldResponse.setProofFiles(response.getProofFiles());

        oldResponse = persistenceManager.update(oldResponse);
        updateParentTrak(oldResponse.getTrak());

        //sent notification
        notificationManager
                .process(new TrakNotificationEvent(NotificationType.TRAK_RESPONSE_UPDATED,
                        oldResponse));

        ActivityLogger.log(ContextHelper.getMembership(), Verb.UPDATED,
                oldResponse,
                Arrays.asList(oldResponse.getTrak().getCreator(),
                        oldResponse.getTrak().getApprover())
        );
        ExLogger.get().info("Updated trak response: {} - {}",
                oldResponse.getUuid(), oldResponse.getNote());

        return oldResponse;
    }

    @Override
    public TrakResponse getTrakResponseByUUID(String trakUUID, String responseUUID) {
        TrakResponse response = getTrakResponseByUUID(responseUUID);

        //checking permission
        if (!response.getTrak().getUuid().equals(trakUUID))
            throw new ExNotFoundException("Trak response not found for trak id " + trakUUID);
        checkGetPermission(response.getTrak());

        return response;
    }

    @Override
    public List<TrakApproval> listTrakApproval(String trakUUID, String responseUUID, String membershipUUID) {
        QueryFilters filters = QueryFilters.create(new HashMap<>());
        filters.putIfNotEmpty(RestParameters.TrakApprovalFilters.TRAK_RESPONSE_UUID,
                responseUUID);
        filters.putIfNotEmpty(RestParameters.TrakApprovalFilters.TRAK_UUID,
                trakUUID);
        filters.putIfNotEmpty(RestParameters.TrakApprovalFilters.TRAK_APPROVER_UUID,
                membershipUUID);
        List<TrakApproval> approvals = persistenceManager.listObjects(new TrakApprovalQuery(), filters);

        //returning only allowed trak response
        List<TrakApproval> allowedApprovals = new ArrayList<>();
        for (TrakApproval trakApproval : approvals) {
            try {
                checkGetPermission(trakApproval.getTrakResponse().getTrak());
            } catch (ExAuthorizationException e) {
                continue;
            }
            allowedApprovals.add(trakApproval);
        }
        return allowedApprovals;
    }

    @Override
    public TrakApproval createTrakApproval(TrakApprovalCreateDTO dto) {
        TrakResponse trakResponse = getTrakResponseByUUID(dto.getResponseUUID());
        //check membership eligibility
        if (!trakResponse.getTrak().getApproverUUID().equals(dto.getMemberUuid()) ||
                !trakResponse.getTrak().getUuid().equals(dto.getTrakUUID()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

        //set trak response
        dto.getApproval().setTrakResponse(trakResponse);
        return createTrakApproval(dto.getApproval());

    }

    @Override
    public TrakApproval updateTrakApproval(TrakApprovalCreateDTO dto) {
        //get trak checklist
        TrakApproval oldApproval = getTrakApprovalByUUID(dto.getApprovalUUID());

        //check membership eligibility
        if (!oldApproval.getTrakResponse().getTrak().getApproverUUID().equals(dto.getMemberUuid()) ||
                !oldApproval.getTrakResponse().getTrak().getUuid().equals(dto.getTrakUUID()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

        TrakApproval approval = dto.getApproval();
        if (approval.getNote() != null)
            oldApproval.setNote(approval.getNote());

        if (approval.getFiles() != null && !approval.getFiles().isEmpty())
            oldApproval.setFiles(approval.getFiles());

        if (approval.getApprovalResponseType() != null)
            oldApproval.setApprovalResponseType(approval.getApprovalResponseType());

        oldApproval = persistenceManager.update(oldApproval);


        //sent notification
        notificationManager
                .process(new TrakNotificationEvent(NotificationType.TRAK_APPROVAL_UPDATED,
                        oldApproval));

        ActivityLogger.log(ContextHelper.getMembership(), Verb.UPDATED,
                oldApproval,
                Arrays.asList(oldApproval.getTrakResponse().getTrak().getCreator(),
                        oldApproval.getTrakResponse().getTrak().getAssignee())
        );
        ExLogger.get().info("Updated trak response approval: {} - {}",
                oldApproval.getUuid(), oldApproval.getNote());

        return oldApproval;
    }

    @Override
    public TrakApproval getTrakApprovalByUUID(String trakUUID, String responseUUID, String approvalUUID) {
        TrakApproval approval = getTrakApprovalByUUID(approvalUUID);

        //checking permission
        if (!approval.getTrakResponse().getUuid().equals(responseUUID) ||
                !approval.getTrakResponse().getTrak().getUuid().equals(trakUUID))
            throw new ExNotFoundException("Approval not found for trak id " + trakUUID +
                    " and response id " + responseUUID);
        checkGetPermission(approval.getTrakResponse().getTrak());

        return approval;
    }

    private TrakResponse getTrakResponseByUUID(String iUUID) {
        TrakResponse response = persistenceManager.readObjectByUUID(TrakResponse.class, iUUID);
        if (response == null)
            throw new ExNotFoundException("TrakResponse not found for Id " + iUUID);
        return response;
    }

    private TrakApproval getTrakApprovalByUUID(String iUUID) {
        TrakApproval response = persistenceManager.readObjectByUUID(TrakApproval.class, iUUID);
        if (response == null)
            throw new ExNotFoundException("TrakApproval not found for Id " + iUUID);
        return response;
    }

    private boolean isParticipant(List<ContractingParty> parties) {
        for (ContractingParty contractingParty : parties) {
            if (contractingParty.getPartyType() == ContractingPartyType.PARTICIPANT)
                return true;
        }
        return false;
    }

    private void checkGetPermission(Trak trak) {
//        if (isParticipant(trak.getContract().getContractingParties()))
        //when the user is participant then return only the trak which he is involved
        checkContributorPermission(trak.getTrakContributorProfileUUIDs());
//        else checkCreatePermission(ContextHelper.getMembershipUUID(),trak.getContractMembers());
    }

    private void checkContributorPermission(List<String> contributor) {
        if (contributor.contains(ContextHelper.getMembership().getProfile().getUuid()))
            return;
        throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
    }

    private void checkCreatePermission(String uuid, List<IContractMember> members) {
        for (IContractMember member : members)
            if (member.getUuid().equals(uuid) ||
                    Security.isProfileAdministrator())
                return;

        throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
    }

    private Trak getTrak(Trak trak, TrakBaseDTO dto) {
        Contract contract = contractManager
                .getContractByUUID(dto.getContractUUID());
        if (contract == null || contract.getContractMembers() == null)
            throw new ExNotFoundException("Contract not found");

//        if (dto.getType() != null)
        trak.setType(dto.getType());
//        if (dto.getTitle() != null)
        trak.setTitle(dto.getTitle());
//        if (dto.getDescription() != null)
        trak.setDescription(dto.getDescription());
//        if (dto.getStatus() != null)
//        trak.setStatus(dto.getStatus());
//        if (dto.getDueDate() != null)
        trak.setDueDate(dto.getDueDate());

        trak.setStartDate(dto.getStartDate());
//        if (dto.getCheckList() != null && !dto.getCheckList().isEmpty())

        trak.setProofRequired(dto.isProofRequired());
        trak.setCheckList(dto.getCheckList());
//        if (dto.getFiles() != null && !dto.getFiles().isEmpty())
        trak.setFiles(dto.getFiles());
//        if (dto.getValue() != null)
        trak.setValue(dto.getValue());
        trak.setApprovalRequired(dto.isApprovalRequired());

        //set contract
        trak.setContract(contract);

        //now set negotiator
        if (dto.getCreatorUUID() != null) {
            checkCreatePermission(dto.getCreatorUUID(), contract.getContractMembers());
            trak.setCreator(membershipManager
                    .findByUUID(dto.getCreatorUUID(), true));
        }

        if (dto.getAssigneeUUID() != null) {
            checkCreatePermission(dto.getAssigneeUUID(), contract.getContractMembers());
            trak.setAssignee(membershipManager
                    .findByUUID(dto.getAssigneeUUID(), true));
        }

        //set approved
        if (dto.getApproverUUID() != null) {
            checkCreatePermission(dto.getApproverUUID(), contract.getContractMembers());
            trak.setApprover(membershipManager
                    .findByUUID(dto.getApproverUUID(), true));
        }

        return trak;
    }

    private void createLifeCycleEvent(Trak trak) {

        //create start event
        ContractLifecycleEvent event = null;
        if (trak.getStartDate() != null) {
            event = new ContractLifecycleEvent();
            event.setTrakUUID(trak.getUuid());
            event.setStartDate(trak.getStartDate());
            event.setEndDate(trak.getDueDate());
            event.setCreator(trak.getCreator());
            event.setEventType(ContractLifecycleEventType.TRAK_START);
            trak.getContract().getLifecycleEvents().add(event);
        }
        if (trak.getDueDate() != null) {
            //create end event
            event = new ContractLifecycleEvent();
            event.setTrakUUID(trak.getUuid());
            event.setStartDate(trak.getDueDate());
            event.setEndDate(trak.getDueDate());
            event.setCreator(trak.getCreator());
            event.setEventType(ContractLifecycleEventType.TRAK_DUE);
            trak.getContract().getLifecycleEvents().add(event);
        }
        //update contract
        if (event != null)
            persistenceManager.update(trak.getContract());
    }

    private void updateLifeCycleEvent(Trak trak) {
        if (trak.getStartDate() != null && trak.getDueDate() != null) {
            for (ContractLifecycleEvent event
                    : trak.getContract().getLifecycleEvents()) {
                if (event.getTrakUUID().equals(trak.getUuid()))
                    if (event.getEventType()
                            .equals(ContractLifecycleEventType.TRAK_START)) {
                        //update start event
                        event.setStartDate(trak.getStartDate());
                        event.setEndDate(trak.getDueDate());
                        event.setCreator(trak.getCreator());
                    } else if (event.getEventType()
                            .equals(ContractLifecycleEventType.TRAK_DUE)) {
                        //create end event
                        event = new ContractLifecycleEvent();
                        event.setStartDate(trak.getDueDate());
                        event.setEndDate(trak.getDueDate());
                        event.setCreator(trak.getCreator());
                    }
            }
        }

    }

    private boolean checkTrakResponsePermission(String trakUUID) {
        //first get list of trak for trak response with trakId
        for (TrakResponse response : getTrakResponses(trakUUID)) {
            if (response.getProgress() < 100)
                return true;
        }
        return false;
    }

    private boolean checkTrakResponsePermissionForApproval(String trakUUID) {
        //first get list of trak for trak response with trakId
        for (TrakApproval response : getTrakApprovals(trakUUID)) {
            if (response.getApprovalResponseType() == null)
                return true;
        }
        return false;
    }

    private List<TrakResponse> getTrakResponses(String trakUUID) {
        QueryFilters filters = QueryFilters.create(new HashMap<>());
        filters.put(RestParameters.TrakResponseFilters.TRAK_UUID, trakUUID);
        return persistenceManager.listObjects(new TrakResponseQuery(), filters);
    }

    private List<TrakApproval> getTrakApprovals(String trakUUID) {
        QueryFilters filters = QueryFilters.create(new HashMap<>());
        filters.put(RestParameters.TrakApprovalFilters.TRAK_UUID, trakUUID);
        return persistenceManager.listObjects(new TrakApprovalQuery(), filters);
    }

    private TrakApproval createTrakApproval(TrakApproval approval) {

        if (approval.getTrakResponse().getTrak().getProgress() != 100)
            throw new ExValidationException(ErrorKeys.TRAK_APPROVAL_CREATE_ERROR);

        //set trak status
        setTrakStatusForApproval(approval.getApprovalResponseType(),
                approval.getTrakResponse().getTrak());

        //now add track to trak response
        approval = persistenceManager.create(approval);


        //sent notification
        notificationManager
                .process(new TrakNotificationEvent(NotificationType.TRAK_APPROVAL_CREATED,
                        approval));

        ActivityLogger.log(ContextHelper.getMembership(), Verb.CREATE,
                approval,
                Arrays.asList(approval.getTrakResponse().getTrak().getCreator(),
                        approval.getTrakResponse().getTrak().getAssignee()));
        ExLogger.get().info("Created trak response approval: {} - {}",
                approval.getUuid(), approval.getNote());

        return approval;
    }

    private void setTrakStatusForApproval(ApprovalResponseType type, Trak trak) {
        //set trak status to pending approval
        TrakStatus status = PENDING_APPROVAL;
        if (type != null) {
            switch (type) {
                case ACCEPTED:
                    status = COMPLETED;
                    break;
                case REJECTED:
                    status = REJECTED;
                    break;
                default:
                    status = IN_PROGRESS;
                    //todo create trak response
                    break;
            }
        }
        trak.setStatus(status);
    }

    private void checkTrakPermission(Trak trak, String membershipUuid) {
        if (!(trak.getAssigneeUUID().equals(membershipUuid)
                || Security.isProfileAdministrator(trak.getAssignee().getProfile().getUuid())))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
    }

    private void setTrakProgress(Trak trak, TrakResponse response) {
        if (response.getProgress() != null) {

            if (response.getProgress() == 100) {
                if (!trak.isApprovalRequired())
                    trak.setStatus(COMPLETED);
                else
                    trak.setStatus(PENDING_APPROVAL);
            }

            trak.setProgress(response.getProgress());
        }
    }

    private void updateParentTrak(Trak trak) {
        //getting parent trak from trak
        Trak parentTrak = trak.getParent();
        if (parentTrak == null) {
            //when null checking it from database by getting full trak object
            Trak tempTrak = persistenceManager.readObjectByUUID(Trak.class, trak.getUuid());
            if (tempTrak != null && tempTrak.getParent() != null) {
                parentTrak = tempTrak.getParent();
            }
        }

        //need to update parent task progress all the time
        if (parentTrak != null) {
            //now getting all the sub traks for the parent
            QueryFilters filters = QueryFilters.create(new HashMap<>());
            filters.put(RestParameters.TrakFields.PARENT_UUID, parentTrak.getUuid());

            //completed one based on response status beacuse the latest completed status not updated yet
            int completedTask = 0;
            List<Trak> subTraks = getTrakList(filters);
            for (Trak subTrak : subTraks) {
                if (subTrak.getStatus().equals(COMPLETED))
                    completedTask++;
            }

            if (completedTask == subTraks.size()) {
                parentTrak.setProgress(100);
                parentTrak.setStatus(COMPLETED);
            } else {
                float progress = (float) completedTask / subTraks.size();
                parentTrak.setProgress((int) (progress * 100));
                parentTrak.setStatus(IN_PROGRESS);
            }
        }

        persistenceManager.update(parentTrak);

    }

    private List<Trak> getTrakList(QueryFilters filters) {
        return persistenceManager.listObjects(new TrakQuery(), filters);
    }

}
