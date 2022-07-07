package com.exrade.runtime.common;

import com.exrade.models.activity.ObjectType;
import com.exrade.runtime.informationmodel.InformationModelSyncOperationController;
import com.exrade.runtime.negotiation.NegotiationSyncOperationController;
import com.exrade.runtime.review.ReviewSyncOperationController;

import java.util.HashMap;
import java.util.Map;

public class SyncOperationControllerFactory {

	private static final SyncOperationControllerFactory INSTANCE = new SyncOperationControllerFactory();

	private static final Map<String, ISyncOperationController> controllers = new HashMap<>();

	public static SyncOperationControllerFactory getInstance() {
		return INSTANCE;
	}

	public ISyncOperationController createSyncOperationController(
			String uuid, ObjectType type) {
		//System.out.println("tms>>before: " + tms.size());

		// synchronize creation of the controller object
		if (!controllers.containsKey(uuid)) {
			synchronized (this) {
				ISyncOperationController controller = null;
				if(ObjectType.NEGOTIATION == type) {
					controller = new NegotiationSyncOperationController();
				}
				else if(ObjectType.REVIEW == type) {
					controller = new ReviewSyncOperationController();
				}
				else if(ObjectType.INFORMATION_MODEL_TEMPLATE == type) {
					controller = new InformationModelSyncOperationController();
				}
				
				if (controller != null && !controllers.containsKey(uuid))
					controllers.put(uuid, controller);
			}
		}
		//System.out.println("tms>>after: " + tms.size());
		return controllers.get(uuid);
	}

}
