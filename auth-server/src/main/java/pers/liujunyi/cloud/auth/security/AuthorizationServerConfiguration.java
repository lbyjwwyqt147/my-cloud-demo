package pers.liujunyi.cloud.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/***
 * 文件名称: AuthorizationServerConfiguration.java
 * 文件描述: 身份授权认证服务配置
 * 公 司:
 * 内容摘要:
 * 其他说明:　配置客户端、token存储方式等
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Configuration
@EnableAuthorizationServer //  注解开启验证服务器 提供/oauth/authorize,/oauth/token,/oauth/check_token,/oauth/confirm_access,/oauth/error
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static final String REDIRECT_URL = "https://www.baidu.com/";
    private static final String CLIEN_ID_THREE = "client_3";  //客户端3
    private static final String CLIENT_SECRET = "secret";   //secret客户端安全码
    private static final String GRANT_TYPE_PASSWORD = "password";   // 资源所有者（即用户）密码模式
    private static final String AUTHORIZATION_CODE = "authorization_code"; //授权码模式  授权码模式使用到了回调地址，是最为复杂的方式，通常网站中经常出现的微博，qq第三方登录，都会采用这个形式。
    private static final String REFRESH_TOKEN = "refresh_token";  // 获取access token时附带的用于刷新新的token模式
    private static final String IMPLICIT = "implicit"; //简化授权模式
    private static final String GRANT_TYPE = "client_credentials";  //客户端凭据（客户端ID以及Key）模式
    private static final String SCOPE_READ = "read";
    private static final String SCOPE_WRITE = "write";
    private static final String TRUST = "trust";
    @Value("${security.jwt.access-token-validity}")
    private Integer accessTokenValiditySeconds;          // 客户端的access_token的有效时间值(单位:秒)
    @Value("${security.jwt.refresh-token-validity}")
    private Integer refreshTokenValiditySeconds;        // 客户端的refresh_token的有效时间值(单位:秒)
    /** jwt 密匙 key 以此生成秘钥，以此进行签名 */
    @Value("${security.jwt.signing.key}")
    private String defaultJwtSigningKey;
    private static final String RESOURCE_ID = "resource_id";    //指定哪些资源是需要授权验证的


    @Autowired
    private AuthenticationManager authenticationManager;   //认证方式
    @Resource(name = "userService")
    private UserDetailsService userDetailsService;


    /**
     * 配置客户端详情服务
     * @param configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        String secret = new BCryptPasswordEncoder().encode(CLIENT_SECRET);  // 用 BCrypt 对密码编码
        //配置3个个客户端,一个用于password认证、一个用于client认证、一个用于authorization_code认证
        configurer.inMemory()  // 使用in-memory存储
                .withClient(CLIEN_ID_THREE)    //client_id用来标识客户的Id  客户端3
                .resourceIds(RESOURCE_ID)
                .authorizedGrantTypes(AUTHORIZATION_CODE, GRANT_TYPE, REFRESH_TOKEN, GRANT_TYPE_PASSWORD, IMPLICIT)  //允许授权类型
                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)  //允许授权范围
                .authorities("ROLE_CLIENT")  //客户端可以使用的权限
                .secret(secret)  //secret客户端安全码
                //.redirectUris(REDIRECT_URL)  //指定可以接受令牌和授权码的重定向URIs
                .autoApprove(true) // 为true 则不会被重定向到授权的页面，也不需要手动给请求授权,直接自动授权成功返回code
                .accessTokenValiditySeconds(accessTokenValiditySeconds)   // 客户端的access_token的有效时间值(单位:秒)  若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds) // 客户端的refresh_token的有效时间值(单位:秒)      若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).
                .scopes("all");  // scopes的值就是all（全部权限），read，write等权限。就是第三方访问资源的一个权限，访问范围。

    }

    /**
     * 配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()) // 实现的Access Token类型设置
                .authenticationManager(authenticationManager) // 认证管理器。若rant Type设置为password，则需设置一个AuthenticationManager对象
                .accessTokenConverter(accessTokenConverter()) // Access Token的编码器。也就是JwtAccessTokenConverter
                .userDetailsService(userDetailsService) //若是我们实现了UserDetailsService,来管理用户信息，那么得设我们的userDetailsService对象;必须注入userDetailsService否则根据refresh_token无法加载用户信息
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)  //支持GET  POST  请求获取token
                .reuseRefreshTokens(true) //开启刷新token
                .tokenServices(tokenServices());

    }


    /**
     * 配置令牌端点(Token Endpoint)的安全约束.
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .realm(RESOURCE_ID)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()") //isAuthenticated():排除anonymous   isFullyAuthenticated():排除anonymous以及remember-me
                .allowFormAuthenticationForClients(); //允许表单认证  这段代码在授权码模式下会导致无法根据code　获取token　
    }


    /**
     * JwtAccessTokenConverter来怼令牌进行编码和解码。
     * 适用JwtAccessTokenConverter可以自定义秘签（SigningKey）。
     * SigningKey用处就是在授权认证服务器生成进行签名编码，在资源获取服务器根据SigningKey解码校验。
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
            /**
             * 自定义一些token返回的信息
             * @param accessToken
             * @param authentication
             * @return
             */
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                String grantType = authentication.getOAuth2Request().getGrantType();
                //只有如下两种模式才能获取到当前用户信息
                if ("authorization_code".equals(grantType) || "password".equals(grantType)) {
                    String userName = authentication.getUserAuthentication().getName();
                    // 自定义一些token 信息 会在获取token返回结果中展示出来
                    final Map<String, Object> additionalInformation = new HashMap<>();
                    additionalInformation.put("userName", userName);
                    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                }
                OAuth2AccessToken token = super.enhance(accessToken, authentication);
                return token;
            }
        };
        converter.setSigningKey(StringUtils.isBlank(defaultJwtSigningKey) ? "BCrypt" : defaultJwtSigningKey); // 添加jwtSigningKey，以此生成秘钥，以此进行签名，只有jwtSigningKey才能获取信息。
        return converter;
    }


    /**
     * 声明TokenStore实现
     * 详细了解 参考：https://blog.csdn.net/DuShiWoDeCuo/article/details/78929333
     * @return TokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        // TokenStore 几种方式:
        // 1:InMemoryTokenStore 基于内存 保存到内存进而实现保存Access Token 默认
        // 2：JdbcTokenStore  基于JDBC的实现，令牌（Access Token）会保存到数据库。这个方式，可以在多个服务之间实现令牌共享。 OAuth2默认给出了表结构
        // 基于JDBC的实现 return new JdbcTokenStore(dataSource);
        // 3:JwtTokenStore jwt全称 JSON Web Token。这个实现方式不用管如何进行存储（内存或磁盘），因为它可以把相关信息数据编码存放在令牌里。JwtTokenStore 不会保存任何数据，但是它在转换令牌值以及授权信息方面与 DefaultTokenServices 所扮演的角色是一样的。OAuth2提供了JwtAccessTokenConverter实现，添加jwtSigningKey，以此生成秘钥，以此进行签名，只有jwtSigningKey才能获取信息。
        // 基于jwt实现令牌（Access Token）  return new JwtTokenStore(accessTokenConverter());
        // 4:RedisTokenStore 基于Redis的实现，令牌（Access Token）会保存到Redis
        // 基于Redis的实现 returnnew RedisTokenStore(redisConnectionFactory);
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * 重写 默认的 DefaultTokenServices
     * 默认的DefaultTokenServices。包含了一些有用实现，可以使用它来修改令牌的格式和令牌的存储等，但是生成的token是随机数。
     * @return
     */
    @Bean
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7)); // 7天
        return defaultTokenServices;
    }
}