package com.taskify.commonweb;

import com.taskify.commoncore.CommonLibCoreApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@ComponentScan("com.taskify.commonweb")
@Configuration
@Import(CommonLibCoreApplication.class)
public class CommonLibWebApplication {
}
