package pers.liujunyi.cloud.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.liujunyi.cloud.auth.dto.AuthUserInfoDto;
import pers.liujunyi.cloud.auth.service.auth.AuthUserInfoService;
import pers.liujunyi.common.annotation.ApiVersion;
import pers.liujunyi.common.controller.BaseController;
import pers.liujunyi.common.restful.RestfulVo;
import pers.liujunyi.common.restful.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 */
@RestController
public class LoginController extends BaseController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AuthUserInfoService userInfoService;

    /**
     * 注册用户
     * @param userInfoDto
     * @return
     */
    @PostMapping("/user/register")
    @ApiVersion(1)
    public RestfulVo userRegister(@Valid AuthUserInfoDto userInfoDto) {
        boolean success = this.userInfoService.saveInfo(userInfoDto);
        return ResultUtil.restfulInfo(success);
    }

    /**
     * 用户登陆
     * @param userAccount 登陆账号
     * @param userPassword　登陆密码
     * @return
     */
    @PostMapping("/user/login")
    @ApiVersion(1)
    public RestfulVo userLogin(@NotNull(message = "登录名不能为空") String userAccount, @NotNull(message = "密码不能为空") String userPassword) {

        //登录 身份认证
        // 这句代码会自动执行咱们自定义的 "MyUserDetailService.java" 身份认证类
        //1: 将用户名和密码封装成UsernamePasswordAuthenticationToken  new UsernamePasswordAuthenticationToken(userAccount, userPwd)
        //2: 将UsernamePasswordAuthenticationToken传给AuthenticationManager进行身份认证   authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAccount, userPwd));
        //3: 认证完毕，返回一个认证后的身份： Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAccount, userPwd));
        //4: 认证后，存储到SecurityContext里   SecurityContext securityContext = SecurityContextHolder.getContext();securityContext.setAuthentication(authentication);


        //UsernamePasswordAuthenticationToken继承AbstractAuthenticationToken实现Authentication
        //当在页面中输入用户名和密码之后首先会进入到UsernamePasswordAuthenticationToken验证(Authentication)，注意用户名和登录名都是页面传来的值
        //然后生成的Authentication会被交由AuthenticationManager来进行管理
        //而AuthenticationManager管理一系列的AuthenticationProvider，
        //而每一个Provider都会通UserDetailsService和UserDetail来返回一个
        //以UsernamePasswordAuthenticationToken实现的带用户名和密码以及权限的Authentication
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAccount, userPassword));
            //将身份 存储到SecurityContext里
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", securityContext); // 这个非常重要，否则验证后将无法登陆
            return ResultUtil.success("登录成功.");
        } catch (AuthenticationException e){
            e.printStackTrace();
            return ResultUtil.fail("用户或者密码错误.");
        }
    }
}
