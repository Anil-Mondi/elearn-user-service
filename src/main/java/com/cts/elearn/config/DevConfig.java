package com.cts.elearn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevConfig {
    // ✅ No JPA, no DB, no repo scanning
}