package com.exrade.runtime.pdf.signature;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

import java.io.IOException;
import java.io.InputStream;

public class TimestampSignatureImpl implements SignatureInterface {
    private TSAClient tsaClient;
    public TimestampSignatureImpl(TSAClient tsaClient) {
        super();
        this.tsaClient = tsaClient;
    }
    @Override
    public byte[] sign(InputStream paramInputStream) throws IOException {
        return tsaClient.getTimeStampToken(IOUtils.toByteArray(paramInputStream));
    }
}