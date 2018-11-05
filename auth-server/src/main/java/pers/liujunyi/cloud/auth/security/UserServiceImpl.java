package pers.liujunyi.cloud.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pers.liujunyi.cloud.auth.entity.AuthUserInfo;
import pers.liujunyi.cloud.auth.repository.auth.AuthUserInfoRepository;
import pers.liujunyi.common.exception.DescribeException;
import pers.liujunyi.common.exception.ErrorCodeEnum;

import java.util.HashSet;
import java.util.Set;

/***
 * 文件名称: UserServiceImpl.java
 * 文件描述: 用户身份验证
 * 公 司:
 * 内容摘要:
 * 其他说明:　当用户登录时会进入此类的loadUserByUsername方法对用户进行验证，验证成功后会被保存在当前回话的principal对象中
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Slf4j
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private AuthUserInfoRepository authUserInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //获取用户信息
        AuthUserInfo userInfo = this.authUserInfoRepository.findFirstByUserAccount(userName);
        if (userInfo == null) {
            log.info("登录用户【" + userName + "】不存在.");
            //throw new UsernameNotFoundException("登录用户【" + userName + "】不存在.");
            throw new DescribeException(ErrorCodeEnum.LOGIN_INCORRECT);
        }
        if (userInfo.getStatus() == 1) {
            throw new DescribeException(ErrorCodeEnum.USER_LOCK);
        }
        User userDetail = new User(userInfo.getUserAccount(), userInfo.getUserPassword(), getAuthority());
        return userDetail;
    }

    /**
     * 用户拥有的权限角色
     * @return
     */
    private Set<GrantedAuthority> getAuthority() {
        Set<GrantedAuthority> grantedAuths = new HashSet<>();
        //模拟一个权限角色
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return grantedAuths;
    }
}
