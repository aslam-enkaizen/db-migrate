package com.exrade.runtime.kyc;

import com.exrade.core.ExLogger;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.io.OutputStream;

public class RequestInterceptor extends LoggingOutInterceptor {

	public static String requestXml = "";

	public RequestInterceptor() {
		super(Phase.PRE_STREAM);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		OutputStream out = message.getContent(OutputStream.class);
		final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(out);
		message.setContent(OutputStream.class, newOut);
		newOut.registerCallback(new LoggingCallback(message, out));
	}

	private class LoggingCallback implements CachedOutputStreamCallback {
		
		private final Message message;
        private final OutputStream origStream;
        
        LoggingCallback(final Message message, final OutputStream os) {
        		this.message = message;
            this.origStream = os;
		}
        
		public void onFlush(CachedOutputStream cos) {
		}

		public void onClose(CachedOutputStream cos) {
			try {
				StringBuilder builder = new StringBuilder();
				cos.writeCacheTo(builder, limit);
				requestXml = builder.toString();
				//System.out.println("Request XML: \n" + requestXml);
				ExLogger.get().debug("Request XML: {}", requestXml);
				cos.lockOutputStream();
                cos.resetOut(null, false);
			} 
			catch (Exception e) {
			}
			message.setContent(OutputStream.class, origStream);
		}
	}
}
