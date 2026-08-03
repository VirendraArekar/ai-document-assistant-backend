package com.virendra.aiassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

@SpringBootApplication
public class AiAssistantApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AiAssistantApplication.class);
        app.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) ctx -> {
            ConfigurableEnvironment env = ctx.getEnvironment();
            MutablePropertySources sources = env.getPropertySources();
            // Prefer explicit value from application.properties or application.yml packaged with the app.
            // Load classpath application.properties first, then application.yml, and set as a system property
            // so it takes precedence over OS environment variables.
            try {
                // Try application.properties
                org.springframework.core.io.ClassPathResource propRes = new org.springframework.core.io.ClassPathResource("application.properties");
                if (propRes.exists()) {
                    java.util.Properties p = new java.util.Properties();
                    try (java.io.InputStream is = propRes.getInputStream()) {
                        p.load(is);
                    }
                    String port = p.getProperty("server.port");
                    if (port != null && !port.isEmpty()) {
                        System.setProperty("server.port", port);
                    }
                } else {
                    // Try application.yml
                    org.springframework.core.io.ClassPathResource ymlRes = new org.springframework.core.io.ClassPathResource("application.yml");
                    if (ymlRes.exists()) {
                        org.springframework.beans.factory.config.YamlPropertiesFactoryBean yaml = new org.springframework.beans.factory.config.YamlPropertiesFactoryBean();
                        yaml.setResources(ymlRes);
                        java.util.Properties props = yaml.getObject();
                        if (props != null) {
                            String port = props.getProperty("server.port");
                            if (port != null && !port.isEmpty()) {
                                System.setProperty("server.port", port);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                // ignore - if we can't read bundled properties, let Spring use its normal resolution
            }
        });
        app.run(args);
    }

}
