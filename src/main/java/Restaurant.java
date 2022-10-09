import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private static final int COUNT_OF_CLIENTS = 5;
    private static final int COUNT_OF_WAITERS = 2;
    private static final int COUNT_OF_COOKS = 1;

    private int availableWaiters;
    private int ordersInRow;

    void waiterOnWork(){
        availableWaiters++;
    }

    boolean hasWaiter(){
        return (availableWaiters > 0);
    }

    void addOrder(){
        ordersInRow++;
    }

    boolean hasOrder() {
        return (ordersInRow > 0);
    }
}
