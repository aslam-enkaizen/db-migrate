package com.exrade.runtime.template;

public interface IPdfRenderer {

	byte[] render(byte[] cleanedHtmlData, String templateBaseDir) throws Exception;

	byte[] signPdf(byte[] pdfContent, String name, String location, String reason);

}