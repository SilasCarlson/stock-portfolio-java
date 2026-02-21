import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StockPortfolio {
    private final ArrayList<Stock> stocks;
    private final HashMap<Stock, Integer> inventory;
    private boolean isRunning;
    private int balance;

    public StockPortfolio() {
        // initialize the stocks array and append all stock data
        this.stocks = new ArrayList<>();
        this.stocks.add(new Stock("NVDA", 184));
        this.stocks.add(new Stock("MSFT", 395));

        // other
        this.inventory = new HashMap<>();
        this.isRunning = false;
        this.balance = 1000;
    }

    private Stock getStockWithIdentifier(String identifier) {
        for (Stock stock : this.stocks) {
            if (stock.identifier().equalsIgnoreCase(identifier)) return stock;
        }

        return null;
    }

    private void load() {
        Path file = Paths.get("save.txt");

        try {
            List<String> lines = Files.readAllLines(file);
            int lineCount = 0;
            for (String line : lines) {
                if (line != null && !line.isBlank()) {
                    lineCount++;

                    if (lineCount == 1) {
                        this.balance = Integer.parseInt(line);
                        continue;
                    }

                    // load it
                    String[] lineSplit = line.split(",");
                    Stock stock = getStockWithIdentifier(lineSplit[0]);
                    int count = Integer.parseInt(lineSplit[1]);

                    if (stock != null) this.inventory.put(stock, count);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred when trying to load your portfolio" + e.getMessage());
        }
    }

    private void save() {
        Path file = Paths.get("save.txt");

        StringBuilder save = new StringBuilder();
        for (Map.Entry<Stock, Integer> entry : this.inventory.entrySet()) {
            save.append(entry.getKey().identifier()).append(",").append(entry.getValue()).append("\n");
        }

        String fileContents = this.balance + "\n" + save;

        try {
            Files.writeString(file, fileContents);
        } catch (IOException e) {
            System.err.println("An error occurred when trying to save your portfolio" + e.getMessage());
        }
    }

    private void displayStocks() {
        System.out.printf("You balance: $%d%n", this.balance);
        int count = 0;
        for (Stock stock : this.stocks) {
            count++;
            System.out.printf("%d. %s is listed at $%d%n", count, stock.identifier(), stock.price());
        }

        System.out.println("Would you like to buy some stocks? (Y/N)");

        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
            System.out.println("Which stock would you like to buy?");

            try {
                int stockIndex = scanner.nextInt();

                System.out.println("How many shares would you like to buy?");

                int stockCount = scanner.nextInt();

                if (stockIndex <= count) {
                    stockIndex--;
                    if (this.balance >= this.stocks.get(stockIndex).price() * stockCount) {
                        this.balance -= this.stocks.get(stockIndex).price() * stockCount;
                        if (this.inventory.putIfAbsent(this.stocks.get(stockIndex), stockCount) != null) {
                            this.inventory.replace(this.stocks.get(stockIndex), this.inventory.get(this.stocks.get(stockIndex)) + stockCount);
                        }
                    } else System.out.println("You do not have enough money!");
                } else System.out.println("Invalid input!");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input!");
            }
        }
    }

    private void displayInventory() {
        System.out.printf("You balance: $%d%n", this.balance);
        this.inventory.forEach(( stock, count) ->
            System.out.printf("%s - You have %d shares worth $%d%n", stock.identifier(), count, stock.price() * count));
    }

    public void start() {
        this.isRunning = true;
        this.run();
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);

        while (this.isRunning) {
            System.out.println("Menu");
            System.out.println("1. View Stocks");
            System.out.println("2. View Inventory");
            System.out.println("3. Load");
            System.out.println("4. Save and Quit");

            try {
                int userInput = scanner.nextInt();

                switch (userInput) {
                    case 1:
                        this.displayStocks();
                        break;

                    case 2:
                        this.displayInventory();
                        break;

                    case 3:
                        this.load();
                        break;

                    case 4:
                        this.save();
                        this.isRunning = false;
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input!");
                scanner.nextLine(); // prevents infinite loop and clears bad input
            }
        }

        scanner.close();
    }
}
