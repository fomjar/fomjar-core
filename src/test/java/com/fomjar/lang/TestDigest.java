package com.fomjar.lang;

import com.fomjar.lang.Digest;
import org.junit.Test;

public class TestDigest {

    @Test
    public void test() {
        String password = "password";
        System.out.println(Digest.md2(password));
        System.out.println(Digest.md5(password));
        System.out.println(Digest.sha1(password));
        System.out.println(Digest.sha256(password));
        System.out.println(Digest.sha384(password));
        System.out.println(Digest.sha512(password));
        System.out.println(Digest.crc32(password));
    }

}
