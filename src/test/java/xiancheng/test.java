package xiancheng;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {
    @Test
    public void testA(){
        int count = 3;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        muityThread muityThread = new muityThread();
        for (int i = 0; i < count; i++) {
            fixedThreadPool.execute(muityThread);
<<<<<<< .mine
            int aaa = 3;
=======
                int bbb = 3;       
>>>>>>> .theirs
        }
    }
}
