package pers.liujunyi.cloud.auth.service.auth;

import pers.liujunyi.cloud.auth.dto.AuthUserInfoDto;


/***
 * 文件名称: AuthUserInfoService.java
 * 文件描述: 用户信息　Service
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface AuthUserInfoService {

    /**
     * 保存用户信息
     * @param userInfoDto
     * @return
     */
    Boolean saveInfo(AuthUserInfoDto userInfoDto);
}
