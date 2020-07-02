package com.fomjar.dist;

/*
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestDist {

    @Autowired
    private Dist dist;

    @Test
    public void testLock() throws InterruptedException {
        String name = "123";
        for (int i = 0; i < 3; i++) {
            this.dist.lock(() -> {
                System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
                try { Thread.sleep(100L); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }, name + "-" + i, 5000);
        }
        Thread.sleep(2000L);
    }

}
*/