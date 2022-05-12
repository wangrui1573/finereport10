package com.fr.decision.privilege.encrpt;;
import com.fr.base.Base64;
import com.fr.base.ServerConfig;
import com.fr.decision.privilege.encrpt.AbstractPasswordValidator;
import com.fr.log.FineLoggerFactory;
import java.io.UnsupportedEncodingException;
public class Base64PasswordValidator extends AbstractPasswordValidator {
    public Base64PasswordValidator() {

    }
    public String encode(String originText) {
        try {
            return Base64.encode(originText.getBytes(ServerConfig.getInstance().getServerCharset()));
        } catch (UnsupportedEncodingException var3) {
            FineLoggerFactory.getLogger().debug(var3.getMessage());
            return "";
        }
    }
}