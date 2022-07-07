package com.exrade.runtime.conf;

import com.exrade.TraktiBeV3Application;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
//@PropertySource("classpath:application.yml")
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExConfiguration {

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public static PropertySourcesPlaceholderConfigurer properties() {
//        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("/application.yml"));
//        return propertySourcesPlaceholderConfigurer;
//    }

    public static String getStringProperty(String iProperty) {
        return TraktiBeV3Application.getStringProperty(iProperty);
    }

    public static int getIntProperty(String iProperty) {
        String value = getStringProperty(iProperty);
        if (StringUtils.isBlank(value))
            return 0;
        return Integer.parseInt(value);
    }

    public static long getLongProperty(String iProperty) {
        return Long.parseLong(getStringProperty(iProperty));
    }

    public static double getDoubleProperty(String iProperty) {
        return Double.parseDouble(getStringProperty(iProperty));
    }

    /**
     * Given a root prefix, it returns the sub configuration as a map of key-value.
     * Nested complex parameters are joined.
     *
     * @param root key
     * @return Map<String, String>
     */
    public static Map<String, String> getConfiguration(String root) {
//        Set<Entry<String, ConfigValue>> configurationSet = Play.application().configuration().getConfig(root).entrySet();
//
//        Map<String, String> configurationMap = new HashMap<String, String>();
//        for (Entry<String, ConfigValue> entry : configurationSet) {
//            configurationMap.put(entry.getKey(), (String) entry.getValue().unwrapped().toString());
//        }
//        return configurationMap;

        //todo update it
        return new HashMap<>();

    }

    public static List<String> getPropertyAsStringList(String iRoot) {
       String value = getStringProperty(iRoot);
        if (StringUtils.isBlank(value))
            return new ArrayList<>();
        return Arrays.asList(value.split(","));
    }

    public static String[] getPropertyAsStringArray(String iRoot) {
        List<String> items = getPropertyAsStringList(iRoot);
        return items.toArray(new String[0]);
    }

    public static boolean getPropertyAsBoolean(String iProperty) {
        return Boolean.parseBoolean(getStringProperty(iProperty));
    }

    public static boolean IsDebugEnabled() {
        boolean isDebugEnabled = false;

        try {
            isDebugEnabled = getPropertyAsBoolean(configKeys.DEBUG_ENABLED);
        } catch (Exception ex) {
        }

        return isDebugEnabled;
    }

    public static class configKeys {
        public static final String MAIL_USERNAME = "mail.auth.user";
        public static final String MAIL_PASSWORD = "mail.auth.pass";
        public static final String MAIL_FROM = "mail.from";
        public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
        public static final String MAIL_SMTP_HOST = "mail.smtp.host";
        public static final String MAIL_SMTP_PORT = "mail.smtp.port";
        public static final String MAIL_NOTIFICATION_INTERVAL = "mail.notification.interval";//in minute separated by comma
        public static final String MAIL_FROM_NAME = "mail.from.name";
        public static final String DB_SELECTED = "db.selected";
        public static final String DB_CACHE1 = "cache.level1.enabled";
        public static final String DB_CACHE2 = "cache.level2.enabled";
        public static final String DEBUG_ENABLED = "debug.enabled";
        public static final String PAYMENT_KEY = "payment.key";
        public static final String PAYMENT_DUMMY = "payment.dummy";
    }

}
