package com.config.qicool.common.utils;

import com.utils.MD5;
import org.acegisecurity.providers.encoding.Md5PasswordEncoder;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataAccessException;

import java.security.SecureRandom;


public class PrivateSecurityRealm {

	 /**
     * {@link PasswordEncoder} based on SHA-256 and random salt generation.
     *
     * <p>
     * The salt is prepended to the hashed password and returned. So the encoded password is of the form
     * <tt>SALT ':' hash(PASSWORD,SALT)</tt>.
     *
     * <p>
     * This abbreviates the need to store the salt separately, which in turn allows us to hide the salt handling
     * in this little class. The rest of the Acegi thinks that we are not using salt.
     */
    /*package*/ static final PasswordEncoder CLASSIC = new PasswordEncoder() {
        private final PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);

        public String encodePassword(String rawPass, Object _) throws DataAccessException {
            return hash(rawPass);
        }

        public boolean isPasswordValid(String encPass, String rawPass, Object _) throws DataAccessException {
            // pull out the sale from the encoded password
            int i = encPass.indexOf(':');
            if(i<0) return false;
            String salt = encPass.substring(0,i);
            return encPass.substring(i+1).equals(passwordEncoder.encodePassword(rawPass,salt));
        }

        /**
         * Creates a hashed password by generating a random salt.
         */
        private String hash(String password) {
            String salt = generateSalt();
            return salt+':'+passwordEncoder.encodePassword(password,salt);
        }

        /**
         * Generates random salt.
         */
        private String generateSalt() {
            StringBuilder buf = new StringBuilder();
            SecureRandom sr = new SecureRandom();
            for( int i=0; i<6; i++ ) {// log2(52^6)=34.20... so, this is about 32bit strong.
                boolean upper = sr.nextBoolean();
                char ch = (char)(sr.nextInt(26) + 'a');
                if(upper)   ch=Character.toUpperCase(ch);
                buf.append(ch);
            }
            return buf.toString();
        }
    };
    
    static final PasswordEncoder MD5_CLASSIC = new PasswordEncoder() {
        private final PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

        public String encodePassword(String rawPass, Object _) throws DataAccessException {
        	return MD5.crypt(rawPass);
        }

        public boolean isPasswordValid(String encPass, String rawPass, Object _) throws DataAccessException {
        	return encPass.equals(MD5.crypt(rawPass));
/*            // pull out the sale from the encoded password
            int i = encPass.indexOf(':');
            if(i<0) return false;
            String salt = encPass.substring(0,i);
            return encPass.substring(i+1).equals(passwordEncoder.encodePassword(rawPass,salt));*/
        }

        /**
         * Creates a hashed password by generating a random salt.
         */
        private String hash(String password) {
            String salt = generateSalt();
            return salt+':'+passwordEncoder.encodePassword(password,salt);
        }

        /**
         * Generates random salt.
         */
        private String generateSalt() {
            StringBuilder buf = new StringBuilder();
            SecureRandom sr = new SecureRandom();
            for( int i=0; i<6; i++ ) {// log2(52^6)=34.20... so, this is about 32bit strong.
                boolean upper = sr.nextBoolean();
                char ch = (char)(sr.nextInt(26) + 'a');
                if(upper)   ch=Character.toUpperCase(ch);
                buf.append(ch);
            }
            return buf.toString();
        }
    };

    /**
     * {@link PasswordEncoder} that uses jBCrypt.
     */
    private static final PasswordEncoder JBCRYPT_ENCODER = new PasswordEncoder() {
        public String encodePassword(String rawPass, Object _) throws DataAccessException {
            return BCrypt.hashpw(rawPass,BCrypt.gensalt());
        }

        public boolean isPasswordValid(String encPass, String rawPass, Object _) throws DataAccessException {
            return BCrypt.checkpw(rawPass,encPass);
        }
    };

    /**
     * Combines {@link #JBCRYPT_ENCODER} and {@link #CLASSIC} into one so that we can continue
     * to accept {@link #CLASSIC} format but new encoding will always done via {@link #JBCRYPT_ENCODER}.
     */
    public static final PasswordEncoder PASSWORD_ENCODER = new PasswordEncoder() {
        /*
            CLASSIC encoder outputs "salt:hash" where salt is [a-z]+, so we use unique prefix '#jbcyrpt"
            to designate JBCRYPT-format hash.

            '#' is neither in base64 nor hex, which makes it a good choice.
         */
        public String encodePassword(String rawPass, Object salt) throws DataAccessException {
            return JBCRYPT_HEADER+JBCRYPT_ENCODER.encodePassword(rawPass,salt);
        }

        public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
            if (encPass.startsWith(JBCRYPT_HEADER))
                return JBCRYPT_ENCODER.isPasswordValid(encPass.substring(JBCRYPT_HEADER.length()),rawPass,salt);
            else if(encPass.length() == 32)
            	return MD5_CLASSIC.isPasswordValid(encPass, rawPass, salt);
            else
                return CLASSIC.isPasswordValid(encPass,rawPass,salt);
        }

        private static final String JBCRYPT_HEADER = "#jbcrypt:";
    };
    
    public static String fromPlainPassword(String rawPassword) {
        return PASSWORD_ENCODER.encodePassword(rawPassword,null);
    }

    public static boolean isPasswordCorrect(String passHash, String candidate) {
        return PASSWORD_ENCODER.isPasswordValid(passHash, candidate,null);
    }
	
}
