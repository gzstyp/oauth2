package com.fwtai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * (登录认证)先在这里认证登录成功之后再去这个类 OAuth2AuthorizationServer 再一次认证,认证成功后跳转到授页面(此时需要点按钮授权)
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-06 15:21
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder (){
        return new BCryptPasswordEncoder();
    }

    // 模拟操作 http://127.0.0.1:8080/oauth/authorize?client_id=client&response_type=code
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
            .withUser("admin").password(getPasswordEncoder().encode("123456")).roles("ADMIN")
            .and()
            .withUser("user").password(getPasswordEncoder().encode("123456")).roles("USER");
    }
}