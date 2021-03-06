package pers.liujunyi.cloud.auth.security.hander;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import pers.liujunyi.common.exception.DescribeException;
import pers.liujunyi.common.exception.ErrorCodeEnum;
import pers.liujunyi.common.restful.ResultUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 * 文件名称: PermitAuthenticationFilter.java
 * 文件描述: 自定义过滤器验证token 返回自定义数据格式
 * 公 司:
 * 内容摘要:
 * 其他说明:　
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Slf4j
@Component
public class PermitAuthenticationFilter extends OAuth2AuthenticationProcessingFilter {

    private static final String BEARER_AUTHENTICATION = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "authorization";
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    private boolean stateless = true;
    OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
    @Autowired
    private TokenStore tokenStore;


    public PermitAuthenticationFilter() {
        DefaultTokenServices dt = new DefaultTokenServices();
        dt.setTokenStore(tokenStore);
        oAuth2AuthenticationManager.setTokenServices(dt);
        this.setAuthenticationManager(oAuth2AuthenticationManager);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info(" ================== =========================== ===================");
        log.info("当前访问的URL地址：" + request.getRequestURI());
        try {
            Authentication authentication = this.tokenExtractor.extract(request);
            if (authentication == null) {
                if (this.stateless && this.isAuthenticated()) {
                    // SecurityContextHolder.clearContext();
                }
                log.info("当前访问的URL地址：" + request.getRequestURI() + "不进行拦截...");
                filterChain.doFilter(request, response);
            } else {
                log.info("************　开始验证token　..........................   ");
                String accessToken = request.getParameter("access_token");
                String headerToken = request.getHeader(HEADER_AUTHORIZATION);
                Map<String, String> map =  new HashMap<>();
                map.put("status", "403");
                AtomicBoolean error = new AtomicBoolean(false);
                if (StringUtils.isNotBlank(accessToken)) {
                    try {
                        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
                        log.info("token =" + oAuth2AccessToken.getValue());
                    } catch (InvalidTokenException e){
                        error.set(true);
                        map.put("message",e.getMessage());
                        log.info("** 无校的token信息.　** ");
                        // throw new AccessDeniedException("无校的token信息.");
                    }

                } else if (StringUtils.isNotBlank(headerToken) && headerToken.startsWith(BEARER_AUTHENTICATION)){
                    try {
                        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(headerToken.split(" ")[0]);
                        log.info("token =" + oAuth2AccessToken.getValue());
                    } catch (InvalidTokenException e){
                        error.set(true);
                        map.put("message",e.getMessage());
                        log.info("** 无校的token信息.　** ");
                        // throw new AccessDeniedException("无校的token信息.");
                    }

                } else {
                    error.set(true);
                    map.put("message", "参数无token.");
                    log.info("** 参数无token.　** ");
                    //throw new AccessDeniedException("参数无token.");
                }
                if (!error.get()){
                    filterChain.doFilter(request, response);
                } else {
                    map.put("path", request.getServletPath());
                    map.put("timestamp", String.valueOf(LocalDateTime.now()));
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    ResultUtil.writeJavaScript(response, map);
                }
            }
        } catch (Exception e) {
            throw new DescribeException(ErrorCodeEnum.ERROR);
        }

    }

    @Override
    public void destroy() {

    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }
}