package com.fomjar.core.algo;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public abstract class Digest {

    public static String md2(byte[] bytes)                      {return DigestUtils.md2Hex(bytes);}
    public static String md2(InputStream is) throws IOException {return DigestUtils.md2Hex(is);}
    public static String md2(String s)                          {return DigestUtils.md2Hex(s);}

    public static String md5(byte[] bytes)                      {return DigestUtils.md5Hex(bytes);}
    public static String md5(InputStream is) throws IOException {return DigestUtils.md5Hex(is);}
    public static String md5(String s)                          {return DigestUtils.md5Hex(s);}

    public static String sha1(byte[] bytes)                         {return DigestUtils.sha1Hex(bytes);}
    public static String sha1(InputStream is) throws IOException    {return DigestUtils.sha1Hex(is);}
    public static String sha1(String s)                             {return DigestUtils.sha1Hex(s);}

    public static String sha256(byte[] bytes)                       {return DigestUtils.sha256Hex(bytes);}
    public static String sha256(InputStream is) throws IOException  {return DigestUtils.sha256Hex(is);}
    public static String sha256(String s)                           {return DigestUtils.sha256Hex(s);}

    public static String sha384(byte[] bytes)                       {return DigestUtils.sha384Hex(bytes);}
    public static String sha384(InputStream is) throws IOException  {return DigestUtils.sha384Hex(is);}
    public static String sha384(String s)                           {return DigestUtils.sha384Hex(s);}

    public static String sha512(byte[] bytes)                       {return DigestUtils.sha512Hex(bytes);}
    public static String sha512(InputStream is) throws IOException  {return DigestUtils.sha512Hex(is);}
    public static String sha512(String s)                           {return DigestUtils.sha512Hex(s);}

    public static String crc32(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return Long.toHexString(crc32.getValue());
    }
    public static String crc32(InputStream is) throws IOException {
        CheckedInputStream cis = new CheckedInputStream(is, new CRC32());
        byte[] buf = new byte[1024];
        while(0 <= cis.read(buf));
        return Long.toHexString(cis.getChecksum().getValue());
    }
    public static String crc32(String s) {return Digest.crc32(s.getBytes());}

}
