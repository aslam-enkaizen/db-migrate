package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.analytic.AnalyticResponse;

import java.text.ParseException;
import java.util.Map;

public interface AnalyticAPI {
	AnalyticResponse getResult(ExRequestEnvelope requestEnvelope, Map<String, String> params)  throws ParseException;
}
