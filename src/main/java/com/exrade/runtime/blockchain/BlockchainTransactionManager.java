package com.exrade.runtime.blockchain;

import com.exrade.models.blockchain.BlockchainTransaction;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.blockchain.persistence.BlockchainTransactionQuery;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.timer.TimeProvider;

import java.util.List;

public class BlockchainTransactionManager implements IBlockchainTransactionManager {

	private PersistentManager persistenceManager = new PersistentManager();
	
	@Override
	public BlockchainTransaction createBlockchainTransaction(BlockchainTransaction iBlockchainTransaction) {
		iBlockchainTransaction.setCreationDate(TimeProvider.now());
		persistenceManager.create(iBlockchainTransaction);
		
		return getBlockchainTransaction(iBlockchainTransaction.getTransactionId());
	}

	@Override
	public BlockchainTransaction updateBlockchainTransaction(BlockchainTransaction iBlockchainTransaction) {
		BlockchainTransaction existingTransaction = getBlockchainTransaction(iBlockchainTransaction.getTransactionId());
		existingTransaction.setContractAddress(iBlockchainTransaction.getContractAddress());
		existingTransaction.setCustomFields(iBlockchainTransaction.getCustomFields());
		existingTransaction.setStatus(iBlockchainTransaction.getStatus());
		existingTransaction.setUpdateDate(TimeProvider.now());
		persistenceManager.update(existingTransaction);
		
		return getBlockchainTransaction(existingTransaction.getTransactionId());
	}

	@Override
	public BlockchainTransaction getBlockchainTransaction(String iTransactionId) {
		QueryFilters filters = QueryFilters.create(RestParameters.BlockchainTransactionFields.TRANSACTION_ID, iTransactionId);
		return persistenceManager.readObject(new BlockchainTransactionQuery(), filters);
	}

	@Override
	public List<BlockchainTransaction> listBlockchainTransactions(QueryFilters iFilters) {
		return persistenceManager.listObjects(new BlockchainTransactionQuery(), iFilters);
	}

}
