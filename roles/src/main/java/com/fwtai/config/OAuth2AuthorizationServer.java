package com.fwtai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

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

    // 步骤1，连接数据源
    @Primary //该注解表示覆盖spring默认的数据库方式
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    //步骤2，告诉认证服务器token要从数据库拿取
    @Bean
    public TokenStore tokenStore(){
        return new JdbcTokenStore(dataSource());
    }

    //步骤3，因为我们用的是jdbc，所以要配置客户端信息,即 ClientDetailsServiceConfigurer
    @Bean
    public ClientDetailsService jdbcClientDetails(){
        return new JdbcClientDetailsService(dataSource());
    }

    //步骤4，配置客户端的信息,实际项目中下面的信息是存在数据库里或配置文件里
    // 客户端请求认证服务器，所以要配置客户端信息,即我要知道你是谁!

    // 模拟操作 http://127.0.0.1:8080/oauth/authorize?client_id=client&response_type=code 认证成功后会跳转到 http://www.yinlz.com 且拿到code=Xxxxx,最终的url是 http://www.yinlz.com?code=zpl21T
    // 验证可以看本项目的截图 'QQ的授权码模式03-通过code获取token的示例.png'
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception{
        //步骤4，还要告诉 ClientDetailsServiceConfigurer 要走的jdbc处理,即
        clients.withClientDetails(jdbcClientDetails());
    }

    //步骤5,端点配置,配置获取访问token?
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception{
        endpoints.tokenStore(tokenStore());
    }
}