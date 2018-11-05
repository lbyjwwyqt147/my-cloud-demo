package pers.liujunyi.cloud.auth.security.hander;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pers.liujunyi.common.exception.ErrorCodeEnum;
import pers.liujunyi.common.restful.ResultUtil;
import pers.liujunyi.common.util.DateTimeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * 文件名称: CustomLoginFailHandler.java
 * 文件描述: 登陆失败后　处理类
 * 公 司:
 * 内容摘要:
 * 其他说明:　登陆失败后　会进入到此类
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Slf4j
@Component
public class CustomLoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.info("================= 登录失败 =======================");
        Map<String, String> map =  new HashMap<>();
        map.put("status", ErrorCodeEnum.LOGIN_FAIL.getCode());
        log.info(e.getLocalizedMessage());
        map.put("path", httpServletRequest.getServletPath());
        map.put("message", e.getLocalizedMessage());
        map.put("timestamp", DateTimeUtils.getCurrentDateTimeAsString());
        ResultUtil.writeJavaScript(httpServletResponse, map);

    }
}
