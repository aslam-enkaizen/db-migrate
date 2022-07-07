package com.exrade.util;

import com.exrade.models.processmodel.protocol.Condition;
import com.exrade.models.processmodel.protocol.ProtocolBehaviour;
import com.exrade.models.processmodel.protocol.Transition;
import com.exrade.models.processmodel.protocol.actions.Action;
import com.exrade.models.processmodel.protocol.events.Event;
import com.exrade.models.processmodel.protocol.impl.TransitionImpl;
import com.exrade.models.processmodel.protocol.stages.AbstractStage;
import com.exrade.models.processmodel.protocol.stages.Stage;
import com.exrade.models.processmodel.protocol.stages.impl.StageImpl;
import com.exrade.platform.persistence.PersistentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtocolBehaviourUtil {
	ProtocolBehaviour protocol;

	public ProtocolBehaviourUtil(ProtocolBehaviour iProtocol) {
		protocol = iProtocol;
	}

	public Transition createTransition(String iTransitionName,
			AbstractStage source, AbstractStage target, Action action,
			String kindOf, Event iTrigger) {
		return createTransition(iTransitionName, source, target, action, kindOf, iTrigger, new ArrayList<Condition>());
	}
	
	public Transition createTransition(String iTransitionName,
			AbstractStage source, AbstractStage target, Action action,
			String kindOf, Event iTrigger,Condition iGuard) {
		return createTransition(iTransitionName, source, target, action, kindOf, iTrigger, Arrays.asList(iGuard));
	}
		
	public Transition createTransition(String iTransitionName,
			AbstractStage source, AbstractStage target, Action action,
			String kindOf, Event iTrigger,List<Condition> iGuards) {
		Transition t = PersistentManager.newDbInstance(
				TransitionImpl.class);
		t.setName(iTransitionName);
		// t.setLabel(iTransition.getLabel());
		t.setSource(source);
		t.setTarget(target);
		if (action != null) {
			action.setKindOf(kindOf);
			t.setEffect(action);
		}
		t.setTrigger(iTrigger);
		t.setGuards(iGuards);
		protocol.getTransition().add(t);
		return t;
	}

	public Stage createStage(String name, Action action, String actionName,
			String kindOf) {
		Stage stage = PersistentManager.newDbInstance(
				StageImpl.class);
		stage.setName(name);
		if (action != null) {
			action.setKindOf(kindOf);
			action.setName(actionName);
			stage.setDoAction(action);
		}
		return stage;
	}
}
