package com.fomjar.dist;

import com.fomjar.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestDist {

//    @Autowired
//    private Dist dist;

    @Test
    public void testLock() throws InterruptedException {
//        String name = "123";
//        for (int i = 0; i < 3; i++) {
//            this.dist.lock(() -> {
//                System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
//                try { Thread.sleep(100L); }
//                catch (InterruptedException e) { e.printStackTrace(); }
//            }, name + "-" + i, 5000);
//        }
//        Thread.sleep(2000L);
    }

}