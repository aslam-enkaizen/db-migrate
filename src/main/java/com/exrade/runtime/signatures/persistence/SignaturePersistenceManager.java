package com.exrade.runtime.signatures.persistence;

import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExPersistentException;
import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.platform.persistence.IConnectionManager;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.List;

public class SignaturePersistenceManager extends PersistentManager {
	
	public SignaturePersistenceManager() {
		this(ConnectionManager.getInstance());
	}
	
	public SignaturePersistenceManager(IConnectionManager iConnectionManager) {
		super(iConnectionManager);
	}
	
	public List<NegotiationSignatureContainer> find(QueryFilters iFilters){
		OrientSqlBuilder queryBuilder = new SignatureQuery();
		return listObjects(queryBuilder, iFilters);
	}
	
	public NegotiationSignatureContainer readbyUUID(String negotiationID){
		NegotiationSignatureContainer negSigContainer = null;
		OObjectDatabaseTx db = connectionManager.getObjectConnection();
		String nquery = "select * from "
				+ NegotiationSignatureContainer.class.getSimpleName()
				+ " where negotiationID like '" + negotiationID + "' order by @rid desc";
		try {
			List<NegotiationSignatureContainer> result = db
					.query(new OSQLSynchQuery<NegotiationSignatureContainer>(nquery));
			if (result.size() > 0) {
				negSigContainer = result.get(0);
			}
//			} else if (result.size() > 1) {
//				throw new ExPersistentException(ErrorKeys.UUID_DUPLICATE);
//			}
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.DB_READ_GENERIC,	ex);
		} finally {
			db.close();
		}
		return negSigContainer;
	}
}