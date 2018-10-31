package pers.liujunyi.cloud.auth.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pers.liujunyi.cloud.auth.entity.AuthUserInfo;

/***
 * 文件名称: AuthUserInfoRepository.java
 * 文件描述: 用户信息　Repository
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface AuthUserInfoRepository extends JpaRepository<AuthUserInfo, Long>, JpaSpecificationExecutor<AuthUserInfo> {

    /**
     * 根据　userAccount　获取用户信息数据
     * @param  userAccount  账号
     * @return
     */
    AuthUserInfo findFirstByUserAccount(String userAccount);

    /**
     *  根据　userCode　获取用户信息数据
     * @param  userCode  用户编号
     * @return
     */
    AuthUserInfo findFirstByUserCode(String userCode);

    /**
     * 根据 userAccount  userPassword 获取用户信息
     * @param userAccount 账号
     * @param userPassword 密码
     * @return
     */
    AuthUserInfo findByUserAccountAndAndUserPassword(String userAccount, String userPassword);
}
