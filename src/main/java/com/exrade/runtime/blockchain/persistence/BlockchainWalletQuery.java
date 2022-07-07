package com.exrade.runtime.blockchain.persistence;

import com.exrade.models.blockchain.BlockchainWallet;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.BlockchainWalletFields;

public class BlockchainWalletQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + BlockchainWallet.class.getSimpleName()+ " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(BlockchainWalletFields.OWNER)){
			query += andEq(BlockchainWalletFields.OWNER + ".uuid", iFilters.get(BlockchainWalletFields.OWNER));
		}
		
		if (iFilters.isNotNull(BlockchainWalletFields.ADDRESS)){
			query += andEq(BlockchainWalletFields.ADDRESS, iFilters.get(BlockchainWalletFields.ADDRESS));
		}
		
		return query;
	}

}
