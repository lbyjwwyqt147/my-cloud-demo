package pers.liujunyi.cloud.auth.entity;

import lombok.Data;
import pers.liujunyi.common.entity.BaseEntity;

import javax.persistence.Entity;
import java.util.Date;

/***
 * 用户信息
 */
@Data
@Entity
public class AuthUserInfo extends BaseEntity {

    /** 用户编码 */
    private String userCode;

    /** 登录账户 */
    private String userAccount;

    /** 登录密码 */
    private String userPassword;

    /** 用户名称 */
    private String userName;

    /** 电子邮箱 */
    private String userEmail;

    /** 绑定的手机号 */
    private String bindingPhone;

    /** 性别 1:男   2：女  3：其他 */
    private Byte userSex;

    /** 最后修改密码时间 */
    private Date lastPasswordResetDate;

    /** 用户状态：0：正常  1：锁定 */
    private Byte status;

    /** 最后登陆时间 */
    private Date lastLoginDate;
}
