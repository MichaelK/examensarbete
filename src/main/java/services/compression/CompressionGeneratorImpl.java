package services.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by mickal on 05/02/2016.
 */
public class CompressionGeneratorImpl implements CompressionGenerator {

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
            //LOGGER.error("Failed to close outputStream {0}", outputStream);
            //LOGGER.error(ioe.getMessage());
        }
        byte[] output = outputStream.toByteArray();
        return output;
    }

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
                //LOGGER.error("Failed to inflate data to buffer {0}", inflater);
                //LOGGER.error(dfe.getMessage());
            }
        }
        try{
            outputStream.close();
        }catch(IOException ioe){
            //LOGGER.error("Failed close outputStream {0}", outputStream);
            //LOGGER.error(ioe.getMessage());
        }
        byte[] output = outputStream.toByteArray();
        return output;
    }
}
