package com.fwtai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * 认证服务器,表示资源所有者,所以要对客户端授权,先走类 WebSecurityConfig 通过用户名和密码登录成功后再到这里认证
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-03 0:58
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
/*
    配置认证服务器
        配置客户端信息：ClientDetailsServiceConfigurer
            inMemory：内存配置
            withClient：客户端标识
            secret：客户端安全码
            authorizedGrantTypes：客户端授权类型
            scopes：客户端授权范围
            redirectUris：注册回调地址
    配置 Web 安全
    通过 GET 请求访问认证服务器获取授权码
        端点：/oauth/authorize
    通过 POST 请求利用授权码访问认证服务器获取令牌
        端点：/oauth/token

附：默认的端点 URL

    /oauth/authorize：授权端点
    /oauth/token：令牌端点
    /oauth/confirm_access：用户确认授权提交端点
    /oauth/error：授权服务错误信息端点
    /oauth/check_token：用于资源服务访问的令牌解析端点
    /oauth/token_key：提供公有密匙的端点，如果你使用 JWT 令牌的话

#
配置认证服务器
 */
@Configuration
@EnableAuthorizationServer//该注解表示是认证服务器
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //配置客户端的信息,实际项目中下面的信息是存在数据库里或配置文件里
    // 客户端请求认证服务器，所以要配置客户端信息,即我要知道你是谁!
    // 模拟操作 http://127.0.0.1:8080/oauth/authorize?client_id=client&response_type=code 认证成功后会跳转到 http://www.yinlz.com 且拿到code=Xxxxx,最终的url是 http://www.yinlz.com?code=zpl21T
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception{
        clients.inMemory()
            //可以看截图，注意的是 client 和 123456 和 code=响应返回的字符串,授权码只能使用一次
            .withClient("client").secret(passwordEncoder.encode("123456"))
            //.authorizedGrantTypes("password","authorization_code","refresh_token","client_credentials","implicit")
            .authorizedGrantTypes("authorization_code")//使用的是授权码模式,它跟QQ的登录是一样的，先是弹出QQ登录然后，QQ登录成功后有复选框提示可以访问你的QQ头像或昵称之类的复选框
            .scopes("all")
            // 响应返回给回调注册redirectUris地址的code授权码再请求认证服务器的接口/oauth/token拿到token令牌 验证可以看本项目的截图 'QQ的授权码模式03-通过code获取token的示例.png'
            .redirectUris("http://www.yinlz.com");
    }
}