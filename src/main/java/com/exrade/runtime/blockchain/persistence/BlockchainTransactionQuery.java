package com.exrade.runtime.blockchain.persistence;

import com.exrade.models.blockchain.BlockchainTransaction;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.BlockchainTransactionFields;

public class BlockchainTransactionQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + BlockchainTransaction.class.getSimpleName()+ " where 1 = 1 ";
		
		
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(BlockchainTransactionFields.CONTRACT_ADDRESS)){
			query += andEq(BlockchainTransactionFields.CONTRACT_ADDRESS, iFilters.get(BlockchainTransactionFields.CONTRACT_ADDRESS));
		}
		
		if (iFilters.isNotNull(BlockchainTransactionFields.OBJECT_ID)){
			query += andEq(BlockchainTransactionFields.OBJECT_ID, iFilters.get(BlockchainTransactionFields.OBJECT_ID));
		}
		
		if (iFilters.isNotNull(BlockchainTransactionFields.OBJECT_TYPE)){
			query += andEq(BlockchainTransactionFields.OBJECT_TYPE, iFilters.get(BlockchainTransactionFields.OBJECT_TYPE));
		}
		
		if (iFilters.isNotNull(BlockchainTransactionFields.TRANSACTION_ID)){
			query += andEq(BlockchainTransactionFields.TRANSACTION_ID, iFilters.get(BlockchainTransactionFields.TRANSACTION_ID));
		}
		
		if (iFilters.isNotNull(BlockchainTransactionFields.STATUS)){
			query += andEq(BlockchainTransactionFields.STATUS, iFilters.get(BlockchainTransactionFields.STATUS));
		}
		
		for(String key : iFilters.keySet()) {
			if(key.startsWith("customFields")) {
				query += andEq(key, iFilters.get(key));
			}
		}
		
		return query;
	}

}
