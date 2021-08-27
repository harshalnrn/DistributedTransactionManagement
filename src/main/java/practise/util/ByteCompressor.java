package practise.util;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;


/*in-general : inputstream represent flow of data from source
          : outputStream represnts flow of data to destination*/

public class ByteCompressor {


    public static byte[] gzipEncode(byte[] b) throws Exception{
        //Source here is byte[]  //desination is also byte[]

        //represents streams of destination which is a byte array.
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream=new GZIPOutputStream(os); //os passed as reference
        //writing input byte array into destination.
        gzipOutputStream.write(b); //compressing input byte[] and writes to byte buffer of os
        gzipOutputStream.flush();
        gzipOutputStream.close();
        return os.toByteArray(); //byte buffer to byte[]
    }

    public static byte[] gzipDecode(byte[] bytes) throws Exception{
//source is byte[]

        //now to uncompress we need to first read using the source GzipInputStream
        GZIPInputStream gzipInputStream=  new GZIPInputStream(new ByteArrayInputStream(bytes)); //ByteArrayInputStream used to read from byte[]


        //represents output stream for byte[] (i.e buffer)
        ByteArrayOutputStream bo=new ByteArrayOutputStream(); //will be used to write into byte[]
        byte[] buffer = new byte[1024]; //by default has 1024 bytes.
        int noOfBytesRead=0;

//itiratively reading-writing chunk by chunk, until noOfBytes<0
        while (noOfBytesRead>=0) {
            //reads uncompressed data into buffer until res<0
            //buffer length  represent chunk size to be read from compressed. (i.e 1024 bytes read in each itiration)


            noOfBytesRead = gzipInputStream.read(buffer,0,buffer.length); //reading into buffer array chunk by chunk, where last parameter is the buffer length.
            //res<0 once inputStream is entirely read.
            if (noOfBytesRead > 0) {
                bo.write(buffer, 0, noOfBytesRead); //together writing from buffer array source which has the decompress data, and populate outputStreams buffer,  chunk by chunk
            }
        }

        gzipInputStream.close();
        return bo.toByteArray(); //convert buffer to byte

    }



    public static byte[] unZip(byte[] compressed) throws Exception{
        //first read chunk to decompress

        //write this read chunk via approrpaite output stream

        int noOfBytesRead=0;
        GZIPInputStream gzipInputStream=new GZIPInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        //stream lenght cannot be figured hence. hence just reading wont help, where we need to read chunk by chunk with fixed size.
        byte[] uncompressed=new byte[1024];

        while(noOfBytesRead>=0){
            noOfBytesRead=gzipInputStream.read(uncompressed,0,uncompressed.length); //keeping reading with offset 0, where we reset the elements in array
            if(noOfBytesRead>0){

                bo.write(uncompressed,0,noOfBytesRead);
            }
        }
        return  bo.toByteArray();
    }
}
