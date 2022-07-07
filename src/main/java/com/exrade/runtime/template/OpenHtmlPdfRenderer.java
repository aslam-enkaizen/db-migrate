package com.exrade.runtime.template;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.pdf.signature.TSAClient;
import com.exrade.runtime.pdf.signature.TimestampSignatureImpl;
import com.google.common.base.Strings;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder.PdfAConformance;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class OpenHtmlPdfRenderer implements IPdfRenderer {

	@Override
	public byte[] render(byte[] cleanedHtmlData, String templateBaseDir) throws Exception {
		byte[] renderedPdfData = null;

		String cssPath = "";
		File templateDir = new File(templateBaseDir);
		if (templateDir.exists()) {
			cssPath = templateBaseDir;
			if (!cssPath.endsWith(File.separator))
				cssPath = templateDir.toURI().toString();
		} else
			cssPath = Thread.currentThread().getContextClassLoader().getResource(getTemplateMediaDir()).toString();

		ExLogger.get().debug("CSS base dir:" + cssPath);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		PdfRendererBuilder builder = new PdfRendererBuilder();
		builder.useFastMode();

		enablePdfAConformance(builder, cssPath);

		builder.withProducer("trakti.com");
		builder.withW3cDocument(html5ParseDocument(cleanedHtmlData, cssPath), cssPath);
		builder.toStream(outputStream);
		try {
			builder.run();
			renderedPdfData = outputStream.toByteArray();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				ExLogger.get().error("Error in closing output stream", e);
			}
		}

		return renderedPdfData;
	}

	@Override
	public byte[] signPdf(byte[] pdfContent, String name, String location, String reason) {
		if (ExConfiguration.getPropertyAsBoolean("tsa.enabled")) {
			byte[] signedPdfContent;
			PDDocument pdf = null;
			try {
				PDSignature signature = new PDSignature();
		    	signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
		    	signature.setSubFilter(COSName.getPDFName("ETSI.RFC3161"));
		    	signature.setSignDate(Calendar.getInstance());

		    	if(!Strings.isNullOrEmpty(name))
		    		signature.setName(name);
		    	if(!Strings.isNullOrEmpty(location))
		    		signature.setLocation(location);
		    	if(!Strings.isNullOrEmpty(reason))
		    		signature.setReason(reason);
		    	signature.setType(COSName.getPDFName("DocTimeStamp"));

		    	pdf = PDDocument.load(pdfContent);
		    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    	TSAClient tsaClient = new TSAClient(new URL(ExConfiguration.getStringProperty("tsa.url")),
						ExConfiguration.getStringProperty("tsa.username"),
						ExConfiguration.getStringProperty("tsa.password"), digest);
		    	pdf.addSignature(signature, new TimestampSignatureImpl(tsaClient));

		    	//PDStream pdStream = new PDStream(pdf);
				//signedPdfContent = pdStream.toByteArray();

		    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				pdf.saveIncremental(outputStream);
				signedPdfContent = outputStream.toByteArray();

				outputStream.close();
				pdf.close();
			} catch (IOException | NoSuchAlgorithmException e) {
				return pdfContent;
			}

	    	return signedPdfContent;
		}
		else {
			return pdfContent;
		}
	}

	private org.w3c.dom.Document html5ParseDocument(byte[] cleanedHtmlData, String baseDir) throws IOException {
		Document doc;

		doc = Jsoup.parse(new ByteArrayInputStream(cleanedHtmlData), "UTF-8", baseDir);

		ExLogger.get().debug(doc.html());
		// Should reuse W3CDom instance if converting multiple documents.
		return new W3CDom().fromJsoup(doc);
	}

	private String getTemplateMediaDir() {
		String templateBaseDir = TemplateUtil.PDF_TAMPLATE_BASE_DIR_V2;

		if (templateBaseDir.startsWith("/"))
			return templateBaseDir.substring(1);
		else
			return templateBaseDir;
	}

	private void enablePdfAConformance(PdfRendererBuilder builder, String cssPath) {
		ExLogger.get().debug("css path: " + cssPath);

		PdfAConformance conform = PdfAConformance.PDFA_1_B;
		builder.usePdfVersion(conform.getPart() == 1 ? 1.4f : 1.5f);
		builder.usePdfAConformance(conform);
		// builder.useFont(new
		// File(Thread.currentThread().getContextClassLoader().getResource(getTemplateMediaDir()
		// + "media/fonts/noto/NotoSerif-Regular.ttf").getFile()), "Noto Serif");
		//builder.useFont(new File(cssPath + "media/fonts/noto/NotoSerif-Regular.ttf"), "Noto Serif");
		//builder.useFont(new File(cssPath + "media/fonts/noto/NotoSerif-Bold.ttf"), "Noto Serif");
		//builder.useFont(new File(cssPath + "media/fonts/noto/NotoSerif-Italic.ttf"), "Noto Serif");
		//builder.useFont(new File(cssPath + "media/fonts/noto/NotoSerif-BoldItalic.ttf"), "Noto Serif");
		//builder.useFont(new File(cssPath + "media/fonts/noto/NotoEmoji-Regular.ttf"), "Noto Emoji");
		//builder.useFont(new File(cssPath + "media/fonts/DejaVuSans.ttf"), "DejaVu Sans");
		// builder.useCacheStore(CacheStore.PDF_FONT_METRICS, cache);

		try {
			InputStream colorProfile = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(getTemplateMediaDir() + "media/colorspaces/sRGB.icc");
			byte[] colorProfileBytes = IOUtils.toByteArray(colorProfile);
			builder.useColorProfile(colorProfileBytes);
		} catch (IOException e) {
			ExLogger.get().error("Error in closing output stream", e);
		}

	}

}
