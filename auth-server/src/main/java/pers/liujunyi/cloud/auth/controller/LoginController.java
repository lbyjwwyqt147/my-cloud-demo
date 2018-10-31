package pers.liujunyi.cloud.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.liujunyi.cloud.auth.dto.AuthUserInfoDto;
import pers.liujunyi.cloud.auth.service.auth.AuthUserInfoService;
import pers.liujunyi.common.annotation.ApiVersion;
import pers.liujunyi.common.controller.BaseController;
import pers.liujunyi.common.restful.RestfulVo;
import pers.liujunyi.common.restful.ResultUtil;

import javax.validation.Valid;

/**
 *
 */
@RestController
public class LoginController extends BaseController {

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
}
