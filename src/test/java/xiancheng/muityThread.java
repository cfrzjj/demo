package xiancheng;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class muityThread implements Runnable{
    ReentrantLock lock = new ReentrantLock();
    ReentrantLock lock1 = new ReentrantLock();
    @Override
    public void run() {
        this.sale();
    }

    public  void sale(){
        lock.lock();
        for (int i = 0; i < 50; i++) {
            synchronized (this){
                int count1 = 3;
                ExecutorService fixedThreadPool = new ThreadPoolExecutor(10,
                        20,
                        10,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(10),
                        new ThreadRename("thread2"));

//                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
                thread2 thread2 = new thread2();
                for (int j = 0; j < count1; j++) {
                    fixedThreadPool.execute(thread2);
                }
            }
            System.out.println("count======");
        }

    }
}
