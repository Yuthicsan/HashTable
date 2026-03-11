import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UC2 {

    // productId -> stock count
    private ConcurrentHashMap<String, AtomicInteger> inventory;

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingList;

    public UC2() {
        inventory = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock
    public String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        return stock.get() + " units available";
    }

    // Purchase item
    public synchronized String purchaseItem(String productId, int userId) {

        AtomicInteger stock = inventory.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();

            return "Success for user " + userId +
                    ", " + remaining + " units remaining";
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);

            return "Added to waiting list, position #" + queue.size();
        }
    }

    // View waiting list
    public void showWaitingList(String productId) {
        Queue<Integer> queue = waitingList.get(productId);

        if (queue == null || queue.isEmpty()) {
            System.out.println("No waiting users");
            return;
        }

        System.out.println("Waiting List: " + queue);
    }

    // Demo
    public static void main(String[] args) {

        UC2 manager = new UC2();

        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println(manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 22222));

        // Stock finished
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 88888));

        manager.showWaitingList("IPHONE15_256GB");
    }
}