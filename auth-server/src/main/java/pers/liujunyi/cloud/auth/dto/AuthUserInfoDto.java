package pers.liujunyi.cloud.auth.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import pers.liujunyi.common.dto.BaseDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/***
 *  AuthUserInfoDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AuthUserInfoDto extends BaseDto {

    /** 用户编码 */
    @NotBlank(message = "用户编号不能为空")
    @Length(max = 20, message = "用户编号最大长度为20.")
    private String userCode;

    /** 登录账户 */
    @NotBlank(message = "登陆账号不能为空")
    @Length(max = 45, message = "登陆账号最大长度为45.")
    private String userAccount;

    /** 登录密码 */
    @NotBlank(message = "登陆密码不能为空")
    @Length(min = 6, message = "登陆密码长度至少6位.")
    private String userPassword;

    /** 用户名称 */
    @Length(max = 20, message = "用户名称最大长度为20.")
    private String userName;

    /** 电子邮箱 */
    @Email(message = "电子邮箱格式不正确")
    @Length(max = 45, message = "电子邮箱最大长度为45.")
    private String userEmail;

    /** 绑定的手机号 */
    @Pattern(regexp = "{mobile.phone.number}", message = "手机号格式不正确.")
    private String bindingPhone;

    /** 性别 1:男   2：女  3：其他 */
    @Max(value = 127, message = "性别最大值为127.")
    private Byte userSex;

    /** 用户状态：0：正常  1：锁定 */
    @Max(value = 127, message = "用户状态最大值为127.")
    private Byte status;

}
