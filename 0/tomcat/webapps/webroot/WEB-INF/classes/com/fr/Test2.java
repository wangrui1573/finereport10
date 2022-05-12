import com.fr.cert.token.JwtBuilder;
import com.fr.cert.token.Jwts;
import com.fr.cert.token.SignatureAlgorithm;
import sun.misc.BASE64Encoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class Test2 {

    public static void main(String[] args) {
        //数字签名有效时长 
        long validTime = 30 * 60 * 1000;
        //数字签名内容，以访问资源的相对路径作为内容 
        String path = "GettingStarted.cpt";
        //数字签名用的HS256的密钥 
        String key = createSecret();
        //生成fine_digital_signature
        String fine_digital_signature = createJwt("", "", path, validTime, key);
        //输出密钥
        System.out.println(key);
        //输出fine_digital_signature
        System.out.println(fine_digital_signature);
    }

    private static String createJwt(String issuer, String id, String subject, long validTime, String key) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date currentTime = new Date();
        Date expirationTime = new Date(currentTime.getTime() + validTime);
        JwtBuilder builder = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(currentTime)
                .setExpiration(expirationTime)
                .setId(id)
                .signWith(signatureAlgorithm, key);
        return builder.compact();
    }

    private static String createSecret() {
        try {
            //secret可以自定义的
            String secret = "2222222";
            String message = "";
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256Hmac.init(secret_key);
            BASE64Encoder encoder = new BASE64Encoder();
            String hash = encoder.encode(sha256Hmac.doFinal(message.getBytes()));
            return hash;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

}