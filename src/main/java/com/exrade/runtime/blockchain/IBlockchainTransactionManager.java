package com.exrade.runtime.blockchain;

import com.exrade.models.blockchain.BlockchainTransaction;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IBlockchainTransactionManager {

	BlockchainTransaction createBlockchainTransaction(BlockchainTransaction iBlockchainTransaction);

	BlockchainTransaction updateBlockchainTransaction(BlockchainTransaction iBlockchainTransaction);
	
	BlockchainTransaction getBlockchainTransaction(String iTransactionId);
	
	List<BlockchainTransaction> listBlockchainTransactions(QueryFilters iFilters);
	
}
