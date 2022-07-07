package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.activity.ObjectType;
import com.exrade.models.imports.Import;
import com.exrade.models.imports.ImportStatus;
import com.exrade.models.notification.NotificationType;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.imports.IImportManager;
import com.exrade.runtime.imports.ImportManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.ImportNotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.ExportImportUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;

/**
 * @author Rhidoy
 * @created 15/04/2022
 * @package com.exrade.runtime.timer
 * <p>
 * This class do the imports job from the import data provided.
 */
public class ImportsJob extends ExradeJob implements Job {

    private static final Logger logger = ExLogger.get();
    private final IContractManager contractManager = new ContractManager();
    private final IFileStorageController fileManager = FileStorageProvider.getFileStorageController();
    private final IImportManager importManager = new ImportManager();
    private final NotificationManager notificationManager=new NotificationManager();

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("ImportsJob started.");
        String uuid="";
        try {
            JobDataMap data = context.getJobDetail().getJobDataMap();
            uuid =(String) data.get(RestParameters.UUID);
            Import importEvent = importManager.read(uuid);
            //updating the import status
            importEvent.setStatus(ImportStatus.IN_PROGRESS);
            importManager.update(importEvent);

            setupContext(importEvent.getRequestor().getUuid());
            //doing contract import
            if (importEvent.getObjectType().equals(ObjectType.CONTRACT)) {
                ExportImportUtil.doContractImport(importEvent, contractManager, fileManager);
            }

            //import success now update status
            importEvent.setStatus(ImportStatus.FINISHED);
            importManager.update(importEvent);
            //sent notification
            notificationManager
                    .process(new ImportNotificationEvent(
                            NotificationType.IMPORT_FINISHED,
                            importEvent));
        } catch (Exception ex) {
            logger.warn("ImportJob failed! for uuid "+uuid, ex);
        }
        logger.info("ImportJob finished for uuid "+uuid);
    }
}
