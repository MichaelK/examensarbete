package services.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by mickal on 01/04/2016.
 */
public class CompressionGeneratorImpl implements CompressionGenerator {

    /**
     * Compress a byte array. See "docs.oracle.com" and class "Deflater" for more information.
     * @param data
     * @return
     */
    @Override
    public byte[] compress(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try{
            outputStream.close();
        }catch(IOException ioe){
            System.out.println("Error trying to compress. " + ioe);
        }
        byte[] output = outputStream.toByteArray();
        return output;
    }

    /**
     * Decompress a byte array. See "docs.oracle.com" and class "Inflater" for more information.
     * @param data
     * @return
     */
    @Override
    public byte[] decompress(byte[] data){
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            try{
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }catch(DataFormatException dfe){
                System.out.println("Error trying to compress. " + dfe);
            }
        }
        try{
            outputStream.close();
        }catch(IOException ioe){
            System.out.println("Error trying to decompress. " + ioe);
        }
        byte[] output = outputStream.toByteArray();
        return output;
    }
}
