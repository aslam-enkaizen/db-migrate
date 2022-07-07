package com.exrade.runtime.trak;

import com.exrade.core.ExLogger;
import com.exrade.models.contract.Contract;
import com.exrade.models.contract.ContractingParty;
import com.exrade.models.contract.ContractingPartyType;
import com.exrade.models.informationmodel.Order;
import com.exrade.models.informationmodel.OrderItem;
import com.exrade.models.informationmodel.PaymentPattern;
import com.exrade.models.trak.*;
import com.exrade.models.trak.dto.TrakCreateDTO;
import com.exrade.runtime.blockchain.OrderSmartContract;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.DateUtil;
import com.google.common.base.Strings;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrakOrchestrator {

	private static Logger logger = ExLogger.get();
	IContractManager contractManager;
	ITrakManager trakManager;
	OrderSmartContract orderSmartContract;

	public TrakOrchestrator() {
		trakManager = new TrakManager((ContractManager) contractManager);
		orderSmartContract = new OrderSmartContract();
	}

	public void buildTraks(Contract contract) {
		logger.info("Creating traks for contract: {}", contract.getUuid());
		if (contract.getAgreementInformationModel() != null && contract.getAgreementInformationModel().getTemplate() !=null)
			try {
				createOrderTrak(contract, contract.isBlockchainEnabled());
				createPaymentTrak(contract, contract.isBlockchainEnabled());

			} catch (Exception ex) {
				logger.warn("Creating traks failed for contract: " + contract.getUuid(), ex);
			}

		logger.info("Finished creating traks for contract: {}", contract.getUuid());
	}

	public void handleTrakUpdate(Contract contract, TrakResponse trakResponse) {
		logger.info("Handling trak update for contract: {}", contract.getUuid());
		try {
			if (trakResponse.getTrak().getStatus() == TrakStatus.COMPLETED && contract.isBlockchainEnabled()
					&& trakResponse.getTrak().getBlockchainEnabled()) {

				if (trakResponse.getTrak().getType() == TrakType.DELIVERY) {
					logger.info(
							"Closing Order Item. Contract: {}, Trak: {}, CompletionDate: {}, StartDate: {}, DueDate: {}",
							contract.getUuid(), trakResponse.getTrak().getUuid(), trakResponse.getCompletionDate(),
							trakResponse.getTrak().getStartDate(), trakResponse.getTrak().getDueDate());

					long actualDelivery = DateUtil.daysDiff(trakResponse.getCompletionDate(),
							trakResponse.getTrak().getStartDate());

					String tx = orderSmartContract.closeItem(trakResponse.getTrak().getUuid(), actualDelivery);

					logger.info("Closed Order Item. Contract: {}, Trak: {}, Tx: {}", contract.getUuid(),
							trakResponse.getTrak().getUuid(), tx);
				}

				/*
				 * List<Trak> traks = getOrderTraks(contract, TrakType.DELIVERY); boolean
				 * alldeliveryCompleted = true; for (Trak trak : traks) { if(trak.getStatus() ==
				 * TrakStatus.COMPLETED) { alldeliveryCompleted = false; break; } }
				 * 
				 * if(alldeliveryCompleted) { orderSmartContract.closeOrder(contract.getUuid());
				 * }
				 */
			}

		} catch (Exception ex) {
			logger.warn("Handling trak update failed for contract: " + contract.getUuid(), ex);
		}

		logger.info("Finished handling trak update for contract: {}", contract.getUuid());
	}

	public void handleTrakUpdate(Contract contract, TrakApproval trakApproval) {
		logger.info("Handling trak update for contract: {}", contract.getUuid());
		try {
			if (trakApproval.getApprovalResponseType() == ApprovalResponseType.ACCEPTED
					&& contract.isBlockchainEnabled()
					&& trakApproval.getTrakResponse().getTrak().getBlockchainEnabled()) {

				if (trakApproval.getTrakResponse().getTrak().getType() == TrakType.DELIVERY) {
					logger.info(
							"Closing Order Item. Contract: {}, Trak: {}, CompletionDate: {}, StartDate: {}, DueDate: {}",
							contract.getUuid(), trakApproval.getTrakResponse().getTrak().getUuid(),
							trakApproval.getTrakResponse().getCompletionDate(),
							trakApproval.getTrakResponse().getTrak().getStartDate(),
							trakApproval.getTrakResponse().getTrak().getDueDate());

					long actualDelivery = DateUtil.daysDiff(trakApproval.getTrakResponse().getCompletionDate(),
							trakApproval.getTrakResponse().getTrak().getStartDate());

					String tx = orderSmartContract.closeItem(trakApproval.getTrakResponse().getTrak().getUuid(),
							actualDelivery);

					logger.info("Closed Order Item. Contract: {}, Trak: {}, Tx: {}", contract.getUuid(),
							trakApproval.getTrakResponse().getTrak().getUuid(), tx);
				}

				/*
				 * List<Trak> traks = getOrderTraks(contract, TrakType.DELIVERY); boolean
				 * alldeliveryCompleted = true; for (Trak trak : traks) { if(trak.getStatus() ==
				 * TrakStatus.COMPLETED) { alldeliveryCompleted = false; break; } }
				 * 
				 * if(alldeliveryCompleted) { orderSmartContract.closeOrder(contract.getUuid());
				 * }
				 */
			}

		} catch (Exception ex) {
			logger.warn("Handling trak update failed for contract: " + contract.getUuid(), ex);
		}

		logger.info("Finished handling trak update for contract: {}", contract.getUuid());
	}

	private void createOrderTrak(Contract contract, boolean activateSmartContract) {
		try {
			logger.info("Creating Trak for Order. Contract: {}", contract.getUuid());

			// get list of orders name and time value from contract template
			Order order = InformationModelUtil
					.getOrderInfoForTrak(contract.getAgreementInformationModel().getTemplate());

			// only one trak of type order is supported for a contract
			if (order != null && order.getTrakEnabled()) {
				boolean blockchainEnabled = activateSmartContract && order.getBlockchainEnabled();
				Trak orderTrak = createTrak(contract, null, order.getName(), order.getTotalPrice(), TrakType.ORDER,
						TimeProvider.now(), order.getDeliveryDate(), order.getDeliveryInDays(), null, order.getSender(),
						order.getReceiver(), blockchainEnabled, order.getConfirmationRequired());

				String orderTx = null;
				if (blockchainEnabled) {
					orderTx = createOrderInBlockchain(contract, orderTrak);
				}

				for (OrderItem orderItem : order.getItems()) {
					Trak trak = createTrak(contract, orderTrak, orderItem.getName(), orderItem.getTotalPrice(),
							TrakType.DELIVERY, TimeProvider.now(), orderItem.getDeliveryDate(),
							orderItem.getDeliveryInDays(), orderItem.getSerialNumber(), order.getSender(),
							order.getReceiver(), blockchainEnabled, order.getConfirmationRequired());

					if (blockchainEnabled && !Strings.isNullOrEmpty(orderTx)) {
						addOrderItemInBlockchain(contract, trak, orderItem);
					}
				}

				if (activateSmartContract && order.getBlockchainEnabled() && !Strings.isNullOrEmpty(orderTx)) {
					activateOrderInBlockchain(contract);
				}
			}
		} catch (Exception ex) {
			ExLogger.get().warn("Failed to create trak from contract: " + contract.getUuid(), ex);
		}
	}

	private void createPaymentTrak(Contract contract, boolean activateSmartContract) {
		try {
			logger.info("Creating Trak for Payment. Contract: {}", contract.getUuid());

			// get payment pattern from contract template
			PaymentPattern paymentPattern = InformationModelUtil
					.getPaymentPatternForTrak(contract.getAgreementInformationModel().getTemplate());

			if (paymentPattern != null && paymentPattern.getTrakEnabled()) {
				boolean blockchainEnabled = activateSmartContract && paymentPattern.getBlockchainEnabled();

				// only one trak of type order is supported for a contract
				Trak paymentTrak = createTrak(contract, null, "Payment", paymentPattern.getAmount(), TrakType.PAYMENT,
						paymentPattern.getStartDate(), paymentPattern.getEndDate(), null, null,
						paymentPattern.getSender(), paymentPattern.getReceiver(), blockchainEnabled,
						paymentPattern.getConfirmationRequired());

				logger.info("Created Trak for Payment. Contract: {}, Trak: {}", contract.getUuid(),
						paymentTrak.getUuid());
				/*
				 * String orderTx = null; if (activateSmartContract) { orderTx =
				 * createOrderInBlockchain(contract, orderTrak); }
				 */

				/*
				 * if (activateSmartContract && !Strings.isNullOrEmpty(orderTx)) {
				 * activateOrderInBlockchain(contract); }
				 */
			}
		} catch (Exception ex) {
			ExLogger.get().warn("Failed to create trak from contract: " + contract.getUuid(), ex);
		}
	}

	private Trak createTrak(Contract contract, Trak parent, String title, Double value, TrakType type, Date startDate,
			Date deliveryDate, Integer deliveryInDays, Integer serialNumber, ContractingPartyType assigneeParty,
			ContractingPartyType approverParty, Boolean blockchainEnabled, Boolean confirmationRequired) {
		TrakCreateDTO dto = new TrakCreateDTO();
		dto.setTitle(title);
		dto.setValue(value);
		dto.setType(type);
		dto.setStartDate(startDate);
		dto.setBlockchainEnabled(blockchainEnabled);

		if (deliveryDate != null) {
			dto.setDueDate(deliveryDate);
		} else if (deliveryInDays != null) {
			dto.setDueDate(DateUtils.addDays(TimeProvider.now(), deliveryInDays));
		}

		if (serialNumber != null)
			dto.setExternalId(serialNumber.toString());

		if (parent != null)
			dto.setParentUUID(parent.getUuid());

		dto.setContractUUID(contract.getUuid());
		dto.setApprovalRequired(confirmationRequired);
		for (ContractingParty contractingParty : contract.getContractingParties()) {
			if (contractingParty.getPartyType().equals(ContractingPartyType.OWNER)) {
				dto.setCreatorUUID(contractingParty.getMembers().get(0).getUuid());
			}

			if (assigneeParty != null && contractingParty.getPartyType().equals(assigneeParty)) {
				dto.setAssigneeUUID(contractingParty.getMembers().get(0).getUuid());
			} else if (confirmationRequired && assigneeParty != null
					&& contractingParty.getPartyType().equals(approverParty)) {
				dto.setApproverUUID(contractingParty.getMembers().get(0).getUuid());
			}
		}

		return trakManager.createTrak(dto);
	}

	private String createOrderInBlockchain(Contract contract, Trak orderTrak) {
		logger.info("Creating Order. Contract: {}, Trak: {}, ExternalId: {}, Name: {}, TotalPrice: {}",
				contract.getUuid(), orderTrak.getUuid(), orderTrak.getExternalId(), orderTrak.getTitle(),
				orderTrak.getValue());

		String referenceId = null;
		if (!Strings.isNullOrEmpty(contract.getParentContractUUID()))
			referenceId = contract.getParentContractUUID();

		String orderTx = orderSmartContract.createOrder(referenceId, contract.getUuid(), orderTrak.getTitle(),
				orderTrak.getDescription(), orderTrak.getAssignee().getProfile().getWalletAddress(),
				orderTrak.getApprover().getProfile().getWalletAddress(), orderTrak.getValue());
		logger.info("Created Order. Contract: {}, Trak: {}, ExternalId: {}, Tx: {}", contract.getUuid(),
				orderTrak.getUuid(), orderTrak.getExternalId(), orderTx);

		return orderTx;
	}

	private String addOrderItemInBlockchain(Contract contract, Trak trak, OrderItem item) {
		logger.info(
				"Creating Order Item. Contract: {}, Trak: {}, ExternalId: {}, Name: {}, Quantity: {}, Price: {}, TotalPrice: {}, DeliveryInDays: {}, SerialNumber: {}",
				contract.getUuid(), trak.getUuid(), trak.getExternalId(), item.getName(), item.getQuantity(),
				item.getUnitPrice(), item.getTotalPrice(), item.getDeliveryInDays(), item.getSerialNumber());

		Integer deliveryInDays = null;

		if (item.getDeliveryInDays() != null) {
			deliveryInDays = item.getDeliveryInDays();
		} else if (item.getDeliveryDate() != null) {
			deliveryInDays = (int) DateUtil.daysDiff(item.getDeliveryDate(), TimeProvider.now());
		}

		String tx = orderSmartContract.addItem(contract.getUuid(), trak.getUuid(), item.getName(), item.getQuantity(),
				item.getUnitPrice(), item.getTotalPrice(), deliveryInDays);
		logger.info("Created Order Item. Contract: {}, Trak: {}, ExternalId: {}, Tx: {}", contract.getUuid(),
				trak.getUuid(), trak.getExternalId(), tx);

		return tx;
	}

	private String activateOrderInBlockchain(Contract contract) {
		String orderActivationTx = orderSmartContract.activateOrder(contract.getUuid());
		logger.info("Activated Order. Contract: {}, Tx: {}", contract.getUuid(), orderActivationTx);

		return orderActivationTx;
	}

	private List<Trak> getOrderTraks(Contract contract, TrakType type) {
		Map<String, String> filters = new HashMap<>();
		filters.put(RestParameters.TrakFields.CONTRACT_UUID, contract.getUuid());
		filters.put(RestParameters.TrakFields.TYPE, type.toString());

		return trakManager.listTraks(filters);
	}
}
