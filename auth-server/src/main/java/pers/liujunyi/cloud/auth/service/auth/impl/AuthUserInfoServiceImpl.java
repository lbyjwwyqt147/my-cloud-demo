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

@Service
public class AuthUserInfoServiceImpl implements AuthUserInfoService {

    @Autowired
    private AuthUserInfoRepository userInfoRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public Boolean saveInfo(AuthUserInfoDto userInfoDto) {
        String secret = this.bCryptPasswordEncoder.encode(userInfoDto.getUserPassword()); //对明文密码进行加密
        AuthUserInfo userInfo = DozerBeanMapperUtil.copyProperties(userInfoDto, AuthUserInfo.class);
        userInfo.setUserPassword(secret);
        AuthUserInfo save = this.userInfoRepository.save(userInfo);
        return save != null ? true : false;
    }
}
