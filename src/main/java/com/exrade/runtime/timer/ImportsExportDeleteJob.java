package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.runtime.imports.IImportManager;
import com.exrade.runtime.imports.ImportManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;

/**
 * @author Rhidoy
 * @created 25/04/2022
 * @package com.exrade.runtime.timer
 * <p>
 * This class will remove the 30 day old import/export request.
 */
public class ImportsExportDeleteJob extends ExradeJob implements Job {

    private static final Logger logger = ExLogger.get();
    private final IImportManager importManager = new ImportManager();

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("ImportsExportDeleteJob started.");
        try {
            setupContext(null);
            importManager.deleteOldImports();
        } catch (Exception ex) {
            logger.warn("ImportsExportDeleteJob failed!", ex);
        }
        logger.info("ImportsExportDeleteJob finished");
    }
}
