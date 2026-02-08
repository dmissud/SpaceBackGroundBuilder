package org.dbs.spgb.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
class ContextDescriptionListener {

    private final Environment env;
    private final ApplicationContext applicationContext;

    @Value("${server.port}")
    private String serverPort;
    private String serverName = "";


    @EventListener(ApplicationReadyEvent.class)
    public void handleContextStart() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ContextDescription.class);

        for (Object bean : beans.values()) {
            log.info(bean.toString());
            logUrls();
        }
    }

    public ContextDescriptionListener(Environment env, ApplicationContext applicationContext) {
        this.env = env;
        this.applicationContext = applicationContext;
    }

    public void logUrls() {
        try {
            retrieveSpringProfile();
            retrieveAllInetAddress();
        } catch (UnknownHostException | SocketException e) {
            LoggerFactory.getLogger(ContextDescriptionListener.class).error("Failed to determine host.", e);
        }

        LoggerFactory.getLogger(ContextDescriptionListener.class).info("Remote URL: http://{}:{}", serverName, serverPort);
    }

    private void retrieveAllInetAddress() throws UnknownHostException, SocketException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        serverName = inetAddress.getHostName();
        Collections.list(NetworkInterface.getNetworkInterfaces()).forEach(networkInterface -> Collections.list(networkInterface.getInetAddresses()).forEach(address -> {
            if (address instanceof Inet4Address) {
                LoggerFactory.getLogger(ContextDescriptionListener.class).info("Remote URL ({}): http://{}:{}", networkInterface.getDisplayName(), address.getHostAddress(), serverPort);
            }
        }));
    }

    private void retrieveSpringProfile() {
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            LoggerFactory.getLogger(ContextDescriptionListener.class).info("Active Spring profile: {}", profile);
        }
    }
}