package com.exrade.runtime.kyc;

import com.exrade.core.ExLogger;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.io.IOException;
import java.io.InputStream;

public class ResponseInterceptor extends LoggingInInterceptor {
	public static String responseXml = "";

	public ResponseInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			InputStream is = message.getContent(InputStream.class);
			CachedOutputStream bos = new CachedOutputStream();
			IOUtils.copy(is, bos);
			bos.flush();
			message.setContent(InputStream.class, bos.getInputStream());
			is.close();
			bos.registerCallback(new LoggingCallback());
			bos.close();
		} 
		catch (IOException e) {
			System.out.println("ResponseInterceptor: handleMessage(): " + e.getMessage());
		}
	}

	private class LoggingCallback implements CachedOutputStreamCallback {
		public void onFlush(CachedOutputStream cos) {
		}

		public void onClose(CachedOutputStream cos) {
			try {
				StringBuilder builder = new StringBuilder();
				cos.writeCacheTo(builder, limit);
				responseXml = builder.toString();
				//System.out.println("Response XML: \n" + responseXml);
				ExLogger.get().debug("Response XML: {}", responseXml);
			} 
			catch (Exception e) {
			}
		}
	}
}
