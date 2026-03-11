import java.util.*;

public class UC1 {

    // username -> userId
    private HashMap<String, Integer> usernameToUserId;

    // username -> attempt count
    private HashMap<String, Integer> attemptFrequency;

    public UC1() {
        usernameToUserId = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Track attempt
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        // Check if username exists
        return !usernameToUserId.containsKey(username);
    }

    // Register username
    public boolean registerUsername(String username, int userId) {

        if (usernameToUserId.containsKey(username)) {
            return false; // already taken
        }

        usernameToUserId.put(username, userId);
        return true;
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; i <= 5; i++) {

            String candidate = username + i;

            if (!usernameToUserId.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        // Replace underscore with dot
        if (username.contains("_")) {

            String alt = username.replace("_", ".");

            if (!usernameToUserId.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String most = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                most = entry.getKey();
            }
        }

        return most + " (" + max + " attempts)";
    }

    // Demo
    public static void main(String[] args) {

        UC1 checker = new UC1();

        // Register some users
        checker.registerUsername("john_doe", 1);
        checker.registerUsername("admin", 2);

        System.out.println(checker.checkAvailability("john_doe"));   // false
        System.out.println(checker.checkAvailability("jane_smith")); // true

        System.out.println(checker.suggestAlternatives("john_doe"));

        // Simulate multiple attempts
        for(int i=0;i<5;i++)
            checker.checkAvailability("admin");

        for(int i=0;i<3;i++)
            checker.checkAvailability("john_doe");

        System.out.println("Most attempted: " + checker.getMostAttempted());
    }
}