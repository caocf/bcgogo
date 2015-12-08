package com.bcgogo.appPay.aliPay.utils;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 13:55.
 */
public class AliPayRSAUtil {
    private static final String ALGORITHM = "RSA";

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String sign(String content, String privateKey ,String charset) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return URLEncoder.encode(Base64.encode(signed) , charset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
