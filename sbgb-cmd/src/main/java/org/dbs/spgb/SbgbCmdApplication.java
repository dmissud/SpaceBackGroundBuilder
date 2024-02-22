package org.dbs.spgb;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class SbgbCmdApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbgbCmdApplication.class, args);
    }

    @Bean
    public PromptProvider promptProvider() {
        return () -> new AttributedString("dbs shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}