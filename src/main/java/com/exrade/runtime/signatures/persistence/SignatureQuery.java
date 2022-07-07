package com.exrade.runtime.signatures.persistence;

import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.rest.RestParameters.SignatureContainerFields;

import java.text.MessageFormat;

public class SignatureQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + NegotiationSignatureContainer.class.getSimpleName() + " where 1 = 1 ";

		if (iFilters.isNotNull(SignatureQFilters.NEGOTIATION_UUID)){
			query += and(MessageFormat.format("negotiationID = ''{0}''", iFilters.get(SignatureQFilters.NEGOTIATION_UUID)));
		}
		
		if (iFilters.isNotNull(SignatureQFilters.SIGNATURE_PENDING)){
			if(iFilters.isTrue(SignatureQFilters.SIGNATURE_PENDING))
				query += and("nextToSign is not null and negotiationID not in (select negotiationID from negotiationSignatureContainer where nextToSign is null)");
			else
				query += and("nextToSign is null");
		}
		
		if (iFilters.isNotNull(SignatureQFilters.CREATED_AFTER_INCLUSIVE)){
			String fileQuery = "select fileUUID from Files where fileExtension = 'pdf' ";
			fileQuery += and(condition(FileMetadata.CREATION_DATE, iFilters.get(SignatureQFilters.CREATED_AFTER_INCLUSIVE),Operator.GTEQ));
			
			query += and(SignatureContainerFields.ORIGINAL_AGREEMENT_PDF_UUID + " in ("  + fileQuery + ")");
			//originalAgreementPDFUUID in (select fileUUID from Files where fileExtension = 'pdf' and creationDate >= date('2018-08-06 23:59:59', 'yyyy-MM-dd HH:mm:ss'))
		}
		
		return query;
	}

	public static final class SignatureQFilters {
		public static final String SIGNATURE_PENDING = "signaturePending";
		public static final String CREATED_AFTER_INCLUSIVE = "createdAfterInclusive";
		public static final String NEGOTIATION_UUID = "negotiationID";
	}
}
