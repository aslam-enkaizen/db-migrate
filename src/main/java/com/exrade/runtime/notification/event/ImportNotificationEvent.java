package com.exrade.runtime.notification.event;

import com.exrade.models.imports.Import;
import com.exrade.models.notification.NotificationType;
/**
 * @author Rhidoy
 * @created 22/04/2022
 * @package com.exrade.notification.event
 * <p>
 * This class hold the data for sending notification when import finished.
 */
public class ImportNotificationEvent extends NotificationEvent<Import> {
    public ImportNotificationEvent(NotificationType source, Import payload) {
        super(source, payload);
    }
}
