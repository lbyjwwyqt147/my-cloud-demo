package pers.liujunyi.cloud.auth.service.auth.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.liujunyi.cloud.auth.dto.AuthUserInfoDto;
import pers.liujunyi.cloud.auth.entity.AuthUserInfo;
import pers.liujunyi.cloud.auth.repository.auth.AuthUserInfoRepository;
import pers.liujunyi.cloud.auth.service.auth.AuthUserInfoService;
import pers.liujunyi.common.util.DozerBeanMapperUtil;

/***
 * 文件名称: AuthUserInfoServiceImpl.java
 * 文件描述: 用户信息　Service
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Service
public class AuthUserInfoServiceImpl implements AuthUserInfoService {

    @Autowired
    private AuthUserInfoRepository userInfoRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public Boolean saveInfo(AuthUserInfoDto userInfoDto) {
        userInfoDto.setUserCode("100000");
        userInfoDto.setStatus((byte)0);
        userInfoDto.setUserType((byte)0);
        String secret = this.bCryptPasswordEncoder.encode(userInfoDto.getUserPassword()); //对明文密码进行加密
        AuthUserInfo userInfo = DozerBeanMapperUtil.copyProperties(userInfoDto, AuthUserInfo.class);
        userInfo.setUserPassword(secret);
        AuthUserInfo save = this.userInfoRepository.save(userInfo);
        return save != null ? true : false;
    }
}
