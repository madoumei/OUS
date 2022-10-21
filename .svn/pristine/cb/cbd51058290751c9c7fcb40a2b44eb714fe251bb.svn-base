package com.utils;

import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class CommonUtils
{
	private static Blowfish cipher = null;
	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class);
	
	public static boolean validateEmail(String email)
	{
		boolean isExist = false; 
		/*在正则表达式中\w表示任意单个字符范围是a-z,A-Z,0-9,因为在java中\本来就是转义符号，如果只写为\w则会发生歧义，甚至错�?, 
		 * 因此要写为：\\w+的意思就是出现一次以上，�?以\\w+就代表任意长度的字符串，但不包括其他特殊字符 ，如_,-,$,&,*�?*/ 
		//(\\w+.)+表示服务器可能有多级域名，[a-z]{2,3}表示�?多有2-3个域名�?? 
		/*
		 * Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); 
		 * Matcher m = p.matcher(email); boolean b = m.matches(); */
		//第一种表示方�? 
		if((Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}",email))) 
		{
			isExist=true; 
		}
		else 
		{ 
			LOGGER.warn("invalid email!");
		}
		return isExist; 
	}
	
	public static boolean validatePhone(String phone)
	{
		boolean isValid = false;
		/*
		 * 手机号码�?
		 * 130�?132号段为联通手机号段；133号段为电信手机号段；中国移动占有其余6个子号段，有134�?139号段�?
		 * 按照电信号码使用的原则，�?般达到理论容量的50%，即意味�?号码资源利用率接近饱和了。按照中国移动现�?6个号段分析，移动的用户号码容量实际上限应该在3亿左右；而联通的上限�?2亿上下�??
		 * 而电信的上限�?1亿上下�??
		 * 
		 * 电信
		 * 中国电信手机号码�?头数�?
		 * 2G/3G号段（CDMA2000网络�?133�?153�?180�?181�?189
		 * 4G号段 177
		 * 
		 * 
		 * 联�??
		 * 中国联�?�手机号码开头数�?
		 * 2G号段（GSM网络�?130�?131�?132�?155�?156
		 * 3G上网�?145
		 * 3G号段（WCDMA网络�?185�?186
		 * 4G号段 176�?185[1] 
		 * 
		 * 
		 * 
		 * 移动
		 * 中国移动手机号码�?头数�?
		 * 2G号段（GSM网络）有134x�?0-8）�??135�?136�?137�?138�?139�?150�?151�?152�?158�?159�?182�?183�?184�?
		 * 3G号段（TD-SCDMA网络）有157�?187�?188
		 * 3G上网�? 147
		 * 4G号段 178
		 * 补充
		 * 14号段以前为上网卡专属号段，如中国联�?�的�?145，中国移动的�?147等等�?
		 * 170号段为虚拟运营商专属号段�?170号段�? 11 位手机号前四位来区分基础运营商，其中 �?1700�? 为中国电信的转售号码标识，�??1705�? 为中国移动，�?1709�? 为中国联通�??
		 * 卫星通信 1349
		 */
		if((Pattern.matches("^((13[0-9])|(15[^4,\\D])|(17[0,6-8])|(18[0,5-9]))\\d{8}$",phone))) 
		{
			isValid = true; 
		}
		else 
		{ 
			LOGGER.warn("invalid phone!");
		}
		return isValid;
	}
	
    private static final char[] zeroArray =
            "0000000000000000000000000000000000000000000000000000000000000000".toCharArray();

    /**
     * Pads the supplied String with 0's to the specified length and returns
     * the result as a new String. For example, if the initial String is
     * "9999" and the desired length is 8, the result would be "00009999".
     * This type of padding is useful for creating numerical values that need
     * to be stored and sorted as character data. Note: the current
     * implementation of this method allows for a maximum <tt>length</tt> of
     * 64.
     *
     * @param string the original String to pad.
     * @param length the desired length of the new padded String.
     * @return a new String padded with the required number of 0's.
     */
    public static String zeroPadString(String string, int length) {
        if (string == null || string.length() > length) {
            return string;
        }
        StringBuilder buf = new StringBuilder(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }
	
	
    /**
     * Formats a Date as a fifteen character long String made up of the Date's
     * padded millisecond value.
     *
     * @return a Date encoded as a String.
     */
    public static String dateToMillis(Date date) {
        return zeroPadString(Long.toString(date.getTime()), 15);
    }
    
    /**
     * Returns a Blowfish cipher that can be used for encrypting and decrypting passwords.
     * The encryption key is stored as the Jive property "passwordKey". If it's not present,
     * it will be automatically generated.
     *
     * @return the Blowfish cipher, or <tt>null</tt> if Openfire is not able to create a Cipher;
     *      for example, during setup mode.
     */
    private static synchronized Blowfish getCipher(String keyString) {
        if (cipher != null) {
            return cipher;
        }
        try {
            cipher = new Blowfish(keyString);
        }
        catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        }
        return cipher;
    }
    
    /**
     * Returns an encrypted version of the plain-text password. Encryption is performed
     * using the Blowfish algorithm. The encryption key is stored as the Jive property
     * "passwordKey". If the key is not present, it will be automatically generated.
     *
     * @param password the plain-text password.
     * @return the encrypted password.
     * @throws UnsupportedOperationException if encryption/decryption is not possible;
     *      for example, during setup mode.
     */
    public static String encryptPassword(String password, String keyString) {
        if (password == null) {
            return null;
        }
        Blowfish cipher = getCipher(keyString);
        if (cipher == null) {
            throw new UnsupportedOperationException();
        }
        return cipher.encryptString(password);
    }

    /**
     * Returns a decrypted version of the encrypted password. Encryption is performed
     * using the Blowfish algorithm. The encryption key is stored as the Jive property
     * "passwordKey". If the key is not present, it will be automatically generated.
     *
     * @param encryptedPassword the encrypted password.
     * @return the encrypted password.
     * @throws UnsupportedOperationException if encryption/decryption is not possible;
     *      for example, during setup mode.
     */
    public static String decryptPassword(String encryptedPassword, String keyString) {
        if (encryptedPassword == null) {
            return null;
        }
        Blowfish cipher = getCipher(keyString);
        if (cipher == null) {
            throw new UnsupportedOperationException();
        }
        return cipher.decryptString(encryptedPassword);
    }
	
	public static int evaluateHottest(int viewNum, int shareNum, int replyNum, Date postCreateTime)
	{
		return evaluateHottestByHackerNews(viewNum, shareNum, replyNum, postCreateTime);
	}
	
	public static int evaluateHottestByOwn(int viewNum, int shareNum, int replyNum, Date postCreateTime)
	{
		int result = 0;
		Date now = new Date();
		long seconds = (now.getTime() - postCreateTime.getTime())/1000;
		long days = seconds / 60 / 60 / 24;
		double score = (viewNum+shareNum+replyNum)/3.0;
		score *= Math.pow(0.8, days);
		result = (int)(score * seconds);
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 11-1, 3);
		Date benchDate = new Date(cal.getTimeInMillis());
		seconds = (postCreateTime.getTime() - benchDate.getTime())/1000;
		result +=  (int)seconds;
		return result;
	}
	
	public static int evaluateHottestByHackerNews(int viewNum, int shareNum, int replyNum, Date postCreateTime)
	{
		int result = 0;
		Date now = new Date();
		long seconds = (now.getTime() - postCreateTime.getTime())/1000;
		long hours = seconds / 60 / 60;
		double score = (viewNum+shareNum+replyNum+1);
		score *= 1.0 / Math.pow(hours+24, 1.8);
		result = (int)(score * 100000000);
		return result;
	}

    /**
     * Queue of random number generator objects to be used when creating session
     * identifiers. If the queue is empty when a random number generator is
     * required, a new random number generator object is created. This is
     * designed this way since random number generators use a sync to make them
     * thread-safe and the sync makes using a a single object slow(er).
     */
    private final static Queue<SecureRandom> randoms = new ConcurrentLinkedQueue<SecureRandom>();
	
    protected static void getRandomBytes(byte bytes[]) {

        SecureRandom random = randoms.poll();
        if (random == null) {
            random = createSecureRandom();
        }
        random.nextBytes(bytes);
        randoms.add(random);
    }


    /**
     * Create a new random number generator instance we should use for
     * generating session identifiers.
     */
    private static SecureRandom createSecureRandom() {

        SecureRandom result = null;

        if (result == null) {
            // Invalid provider / algorithm
            try {
                result = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
            }
        }

        if (result == null) {
            // Nothing works - use platform default
            result = new SecureRandom();
        }

        // Force seeding to take place
        result.nextInt();

        return result;
    }
	
    public static String generateSessionId(int sessionIdLength)
    {
        byte random[] = new byte[16];

        // Render the result as a String of hexadecimal digits
        // Start with enough space for sessionIdLength and medium route size
        StringBuilder buffer = new StringBuilder(2 * sessionIdLength + 20);

        int resultLenBytes = 0;

        while (resultLenBytes < sessionIdLength) {
            getRandomBytes(random);
            for (int j = 0;
            j < random.length && resultLenBytes < sessionIdLength;
            j++) {
                byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                byte b2 = (byte) (random[j] & 0x0f);
                if (b1 < 10)
                    buffer.append((char) ('0' + b1));
                else
                    buffer.append((char) ('A' + (b1 - 10)));
                if (b2 < 10)
                    buffer.append((char) ('0' + b2));
                else
                    buffer.append((char) ('A' + (b2 - 10)));
                resultLenBytes++;
            }
        }

        return buffer.toString();
    }
}