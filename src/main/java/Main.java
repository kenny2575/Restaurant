import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final int COUNT_OF_CLIENTS = 5;
    private static final int COUNT_OF_WAITERS = 2;
    private static final int COUNT_OF_COOKS = 1;
    static ReentrantLock locker = new ReentrantLock();
    static Condition condition = locker.newCondition();
    static Condition orderCondition = locker.newCondition();
    static Condition startCookCondition = locker.newCondition();
    static Condition stopCookCookCondition = locker.newCondition();
    static Condition letEatCondition = locker.newCondition();
    static int countOfServed = 0;

    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        Runnable clientBehavior = () -> {
            locker.lock();
            String name = Thread.currentThread().getName();
            System.out.println(name + " come in");
            if (!restaurant.hasWaiter()) {
                try {
                    System.out.println("Wait for available waiter");
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(name + " placed an order");
            restaurant.addOrder();
            orderCondition.signalAll();

            try {
                letEatCondition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("eat");
            System.out.println("come out");
            countOfServed++;
            locker.unlock();
        };

        for (int i = 0; i < COUNT_OF_CLIENTS; i++) {
            new Thread(clientBehavior, "Client 0" + i).start();
        }

        Runnable waiterBehavior = () -> {

            while (countOfServed < COUNT_OF_CLIENTS) {
                locker.lock();
                System.out.println("Waiter on work");
                restaurant.waiterOnWork();
                condition.signal();

                if (!restaurant.hasOrder()) {
                    try {
                        orderCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Get order");
                startCookCondition.signal();
                try {
                    stopCookCookCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Bring the dish");
                letEatCondition.signal();
                locker.unlock();
            }

        };

        for (int i = 0; i < COUNT_OF_WAITERS; i++) {
            new Thread(waiterBehavior, "Waiter 01").start();
        }

        Runnable cookBehavior = () -> {

            while (countOfServed < COUNT_OF_CLIENTS) {
                locker.lock();
                System.out.println("Cook on work");
                if (!restaurant.hasOrder()) {
                    System.out.println("Wait orders");
                    try {
                        startCookCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Start cooking");
                stopCookCookCondition.signal();
                System.out.println("Stop cooking");
                locker.unlock();
            }

        };
        for (int i = 0; i < COUNT_OF_COOKS; i++) {
            new Thread(cookBehavior, "Cook 01").start();
        }
    }
}
