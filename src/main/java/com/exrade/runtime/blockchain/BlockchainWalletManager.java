package com.exrade.runtime.blockchain;

import com.exrade.models.blockchain.BlockchainWallet;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.blockchain.persistence.BlockchainWalletQuery;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;

import java.util.List;

public class BlockchainWalletManager implements IBlockchainWalletManager {

	private PersistentManager persistenceManager = new PersistentManager();
	
	@Override
	public BlockchainWallet createBlockchainWallet(BlockchainWallet iBlockchainWallet) {
		iBlockchainWallet.setCreationDate(TimeProvider.now());
		return persistenceManager.create(iBlockchainWallet);
	}

	@Override
	public BlockchainWallet getBlockchainWallet(String uuid) {
		return persistenceManager.readObjectByUUID(BlockchainWallet.class, uuid);
	}

	@Override
	public List<BlockchainWallet> listBlockchainWallets(QueryFilters iFilters) {
		return persistenceManager.listObjects(new BlockchainWalletQuery(), iFilters);
	}
	
	@Override
	public void deleteBlockchainWallet(String uuid) {
		BlockchainWallet wallet = getBlockchainWallet(uuid);
		checkBlockchainWalletModificationAuthorization(wallet);
		persistenceManager.delete(wallet);
	}
	
	private void checkBlockchainWalletModificationAuthorization(BlockchainWallet iBlockchainWallet){
		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null || !iBlockchainWallet.isOwner(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
	}

}
