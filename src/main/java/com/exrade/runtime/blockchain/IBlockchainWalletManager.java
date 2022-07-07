package com.exrade.runtime.blockchain;

import com.exrade.models.blockchain.BlockchainWallet;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IBlockchainWalletManager {

	BlockchainWallet createBlockchainWallet(BlockchainWallet iBlockchainWallet);

	BlockchainWallet getBlockchainWallet(String iTransactionId);
	
	List<BlockchainWallet> listBlockchainWallets(QueryFilters iFilters);
	
	void deleteBlockchainWallet(String uuid);
	
}
