package com.exrade.runtime.conf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rhidoy
 * @created 6/22/22
 */
@Component
public class ExSpringConfig {
    @Autowired
    private Environment environment;

    public String getStringProperty(String s) {
        return environment.getProperty(s);
    }

    public Integer getPropertyInt(String s) {
        String value = getStringProperty(s);
        if (StringUtils.isBlank(value))
            return 0;
        return Integer.parseInt(value);
    }

    public Long getPropertyLong(String s) {
        return Long.parseLong(getStringProperty(s));
    }

    public Double getPropertyDouble(String s) {
        return Double.parseDouble(getStringProperty(s));
    }

    public List<String> getPropertyListString(String s) {
        String value = getStringProperty(s);
        if (StringUtils.isBlank(value))
            return new ArrayList<>();
        return Arrays.asList(value.split(","));
    }

    public boolean getPropertyBoolean(String s) {
        return Boolean.parseBoolean(environment.getProperty(s));
    }
}
