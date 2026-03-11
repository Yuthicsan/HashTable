import java.util.*;

public class UC3 {

    // DNS Entry class
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private int capacity;

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public UC3(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > UC3.this.capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {

            hits++;
            long end = System.nanoTime();

            System.out.println("Cache HIT (" +
                    (end - start) / 1_000_000.0 + " ms)");

            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED");
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 300));

        long end = System.nanoTime();

        System.out.println("Cache MISS (" +
                (end - start) / 1_000_000.0 + " ms)");

        return ip;
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {

        Random r = new Random();

        return "172.217.14." + (200 + r.nextInt(20));
    }

    // Cleanup expired entries
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (this) {

                    Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                    while (it.hasNext()) {

                        Map.Entry<String, DNSEntry> entry = it.next();

                        if (entry.getValue().isExpired()) {
                            it.remove();
                        }
                    }
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    // Demo
    public static void main(String[] args)

        UC3 dnsCache = new UC3(5);

        System.out.println("IP: " + dnsCache.resolve("google.com"));
        System.out.println("IP: " + dnsCache.resolve("google.com"));
        System.out.println("IP: " + dnsCache.resolve("openai.com"));
        System.out.println("IP: " + dnsCache.resolve("google.com"));

        dnsCache.getCacheStats();
    }
}