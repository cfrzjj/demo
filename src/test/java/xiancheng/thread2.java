package xiancheng;

import java.util.concurrent.locks.ReentrantLock;

public class thread2 implements Runnable{
    ReentrantLock lock = new ReentrantLock();
    @Override
    public void run() {
        this.saleThread2();
    }
    public  void saleThread2(){
        lock.lock();
        for (int i = 0; i < 50; i++) {
            System.out.println("saleThread"+i+"============="+Thread.currentThread().getName());
        }
    }
}

