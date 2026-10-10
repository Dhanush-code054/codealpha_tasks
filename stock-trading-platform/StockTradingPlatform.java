import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Session-based stock trading simulator with a sample market and transaction ledger. */
public class StockTradingPlatform {
    private static final class Stock {
        private final String symbol;
        private final String company;
        private final double price;
        private Stock(String symbol, String company, double price) { this.symbol = symbol; this.company = company; this.price = price; }
    }

    private static final class Transaction {
        private final String action, symbol;
        private final int shares;
        private final double price;
        private Transaction(String action, String symbol, int shares, double price) { this.action = action; this.symbol = symbol; this.shares = shares; this.price = price; }
    }

    private static final class User {
        private double cash = 10_000.00;
        private final Map<String, Integer> holdings = new LinkedHashMap<>();
        private final List<Transaction> transactions = new ArrayList<>();
    }

    private final Map<String, Stock> market = new LinkedHashMap<>();
    private final User user = new User();

    private StockTradingPlatform() {
        market.put("AAPL", new Stock("AAPL", "Apple", 192.35));
        market.put("MSFT", new Stock("MSFT", "Microsoft", 420.72));
        market.put("NVDA", new Stock("NVDA", "NVIDIA", 875.28));
        market.put("AMZN", new Stock("AMZN", "Amazon", 178.15));
        market.put("GOOGL", new Stock("GOOGL", "Alphabet", 164.50));
    }

    public static void main(String[] args) { new StockTradingPlatform().run(); }

    private void run() {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("=== Stock Trading Simulator ===");
            System.out.printf("Starting cash: $%,.2f%n", user.cash);
            while (true) {
                System.out.println("\n1. Market data  2. Buy shares  3. Sell shares  4. Portfolio  5. Transactions  6. Exit");
                int choice = readInt(input, "Choose an option: ", 1, 6);
                switch (choice) {
                    case 1 -> showMarket();
                    case 2 -> trade(input, true);
                    case 3 -> trade(input, false);
                    case 4 -> showPortfolio();
                    case 5 -> showTransactions();
                    case 6 -> { System.out.println("Thanks for trading. This simulator uses fictional sample prices."); return; }
                    default -> throw new IllegalStateException("Unexpected menu choice");
                }
            }
        }
    }

    private void showMarket() {
        System.out.println("\n=== Sample Market ===");
        System.out.printf("%-8s %-14s %12s%n", "Symbol", "Company", "Price");
        for (Stock stock : market.values()) System.out.printf("%-8s %-14s $%,11.2f%n", stock.symbol, stock.company, stock.price);
        System.out.println("Prices are fixed sample data for this session.");
    }

    private void trade(Scanner input, boolean buying) {
        showMarket();
        String symbol;
        while (true) {
            System.out.print("Stock symbol: ");
            symbol = input.nextLine().trim().toUpperCase();
            if (market.containsKey(symbol)) break;
            System.out.println("Unknown symbol. Choose one from the market list.");
        }
        int shares = readInt(input, "Whole shares: ", 1, 1_000_000);
        Stock stock = market.get(symbol);
        double total = stock.price * shares;
        if (buying) {
            if (total > user.cash) { System.out.printf("Insufficient cash. Trade costs $%,.2f; available: $%,.2f.%n", total, user.cash); return; }
            user.cash -= total;
            user.holdings.merge(symbol, shares, Integer::sum);
            user.transactions.add(new Transaction("BUY", symbol, shares, stock.price));
            System.out.printf("Bought %d %s share(s) for $%,.2f.%n", shares, symbol, total);
        } else {
            int owned = user.holdings.getOrDefault(symbol, 0);
            if (shares > owned) { System.out.printf("You own %d %s share(s); cannot sell %d.%n", owned, symbol, shares); return; }
            user.cash += total;
            int remaining = owned - shares;
            if (remaining == 0) user.holdings.remove(symbol); else user.holdings.put(symbol, remaining);
            user.transactions.add(new Transaction("SELL", symbol, shares, stock.price));
            System.out.printf("Sold %d %s share(s) for $%,.2f.%n", shares, symbol, total);
        }
    }

    private void showPortfolio() {
        double holdingsValue = 0;
        System.out.println("\n=== Portfolio ===");
        System.out.printf("Cash: $%,.2f%n", user.cash);
        if (user.holdings.isEmpty()) System.out.println("No stocks held.");
        else {
            System.out.printf("%-8s %8s %14s %14s%n", "Symbol", "Shares", "Price", "Market value");
            for (Map.Entry<String, Integer> entry : user.holdings.entrySet()) {
                Stock stock = market.get(entry.getKey());
                double value = stock.price * entry.getValue();
                holdingsValue += value;
                System.out.printf("%-8s %8d $%13.2f $%13.2f%n", stock.symbol, entry.getValue(), stock.price, value);
            }
        }
        double totalValue = user.cash + holdingsValue;
        double change = totalValue - 10_000.00;
        System.out.printf("Holdings: $%,.2f | Total portfolio value: $%,.2f | Change from start: %s$%,.2f%n", holdingsValue, totalValue, change >= 0 ? "+" : "-", Math.abs(change));
    }

    private void showTransactions() {
        System.out.println("\n=== Transaction History ===");
        if (user.transactions.isEmpty()) { System.out.println("No trades yet."); return; }
        System.out.printf("%-6s %-8s %8s %12s %12s%n", "Action", "Symbol", "Shares", "Price", "Total");
        for (Transaction t : user.transactions) System.out.printf("%-6s %-8s %8d $%11.2f $%11.2f%n", t.action, t.symbol, t.shares, t.price, t.price * t.shares);
    }

    private static int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try { int value = Integer.parseInt(input.nextLine().trim()); if (value >= min && value <= max) return value; }
            catch (NumberFormatException ignored) { }
            System.out.printf("Enter a whole number from %d to %d.%n", min, max);
        }
    }
}
