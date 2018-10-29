package pers.liujunyi.cloud.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @Value("${spring.datasource.druid.driver-class-name}")
    private String d;

    @GetMapping("t")
    public String test(){
        return d;
    }
}
