package services.hash;

import java.util.Objects;

import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;

/**
 * Created by mickal on 02/02/2016.
 */
public class HashGeneratorImpl implements HashGenerator {

    private static int DEFAULT = 512;

    /**
     * Uses 512 bits as default if nothing else is used.
     * @param string
     * @return
     */
    //@Override
    public byte[] generate(final String string) {
        return generate(string, DEFAULT);
    }

    /**
     * Uses 512 bits as default if nothing else is used.
     * @param bytes
     * @return
     */
    //@Override
    public byte[] generate(final byte[] bytes) {
        return generate(bytes, DEFAULT);
    }

    /**
     *
     * @param string
     * @param bits - Can only be 224, 256, 384 or 512 as per SHA3 definition
     * @return
     */
    //@Override
    public byte[] generate(final String string, final int bits) {
        Objects.requireNonNull(string, "String to digest cannot be null!");

        DigestSHA3 md = new DigestSHA3(bits);
        md.update(string.getBytes());

        byte[] digest =  md.digest();
        return digest;
    }

    /**
     *
     * @param bytes
     * @param bits - Can only be 224, 256, 384 or 512 as per SHA3 definition
     * @return
     */
    //@Override
    public byte[] generate(final byte[] bytes, final int bits){
        Objects.requireNonNull(bytes, "bytes to digest cannot be null!");

        DigestSHA3 md = new DigestSHA3(bits);
        md.update(bytes);

        byte[] digest =  md.digest();
        return digest;
    }

    //@Override
    public byte[] hashToBits(final byte[] bytes, final int bits){
        Objects.requireNonNull(bytes, "bytes cannot be null");
        //Required.isTrue(bits > 0 && bits <= 512, "bits need to be 0 > 512");
        //Required.isTrue(512 % bits == 0, "bits need to be a multiple of 8");

        byte[] newBytes = this.generate(bytes, 512);

        int useBytes = bits / 8;
        byte[] croppedArray = new byte[useBytes];

        System.arraycopy(newBytes, 0, croppedArray, 0, useBytes);

        return croppedArray;
    }
}