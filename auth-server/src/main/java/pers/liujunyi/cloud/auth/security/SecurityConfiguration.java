package pers.liujunyi.cloud.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import pers.liujunyi.common.filter.SimpleCORSFilter;

import javax.annotation.Resource;


/***
 * 文件名称: SecurityConfiguration.java
 * 文件描述: web安全配置
 * 公 司:
 * 内容摘要:
 * 其他说明:　
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
        auth.parentAuthenticationManager(authenticationManagerBean());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/assets/**");
        web.ignoring().antMatchers("/favicon.ico");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers()   //　requestMatchers 配置　数组
                .antMatchers("/oauth/**", "/login", "/home")
                .and()
                .authorizeRequests()         //authorizeRequests　配置权限　顺序为先配置需要放行的url 在配置需要权限的url，最后再配置.anyRequest().authenticated()
                .antMatchers("/oauth/**").authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll();
        //http.addFilterBefore(simpleCORSFilter, SecurityContextPersistenceFilter.class);
    }



    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}