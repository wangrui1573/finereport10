package com.fr.decision.privilege.encrpt;

import com.fr.security.SecurityToolbox;

public class CustomSHA256PasswordValidator extends AbstractPasswordValidator {
    public CustomSHA256PasswordValidator() {
    }

    @Override
    public String encode(String originUserName, String originPassword) { //把 用户名+密码 加密成 SHA256字符
        String unionPwd = originUserName + originPassword;
        return SecurityToolbox.sha256(unionPwd);
    }
}