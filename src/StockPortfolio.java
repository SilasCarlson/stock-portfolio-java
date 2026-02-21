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
        this.stocks.add(new Stock("XRTY", 72));
        this.stocks.add(new Stock("PLMX", 513));
        this.stocks.add(new Stock("QZON", 248));
        this.stocks.add(new Stock("VTRX", 89));
        this.stocks.add(new Stock("NBLR", 631));
        this.stocks.add(new Stock("CYTX", 157));
        this.stocks.add(new Stock("HMDL", 420));
        this.stocks.add(new Stock("KRON", 305));
        this.stocks.add(new Stock("ZENT", 976));
        this.stocks.add(new Stock("LUMA", 134));
        this.stocks.add(new Stock("RAVN", 212));
        this.stocks.add(new Stock("TGLX", 487));
        this.stocks.add(new Stock("MERC", 163));
        this.stocks.add(new Stock("VYNE", 298));
        this.stocks.add(new Stock("CORT", 541));
        this.stocks.add(new Stock("BLYT", 77));
        this.stocks.add(new Stock("DAXR", 654));
        this.stocks.add(new Stock("OMNI", 389));
        this.stocks.add(new Stock("ZAPR", 905));
        this.stocks.add(new Stock("KYTE", 144));

        // other
        this.inventory = new HashMap<>();
        this.isRunning = false;
        this.balance = 2500;
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
            // get all the lines then go line by line and create stocks according to the data
            List<String> lines = Files.readAllLines(file);
            int lineCount = 0;
            for (String line : lines) {
                if (line != null && !line.isBlank()) {
                    lineCount++;

                    // the first line just contains the balance
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

        // we must use a string builder here since it is more efficient
        StringBuilder save = new StringBuilder();
        for (Map.Entry<Stock, Integer> entry : this.inventory.entrySet()) {
            save.append(entry.getKey().identifier()).append(",").append(entry.getValue()).append("\n");
        }

        // convert the string builder to a string and include the balance
        String fileContents = this.balance + "\n" + save;

        try {
            Files.writeString(file, fileContents);
        } catch (IOException e) {
            System.err.println("An error occurred when trying to save your portfolio" + e.getMessage());
        }
    }

    private void displayStocks() {
        System.out.printf("You balance: $%d%n", this.balance);

        // display stocks
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

            // do this in a try for input sanitation
            try {
                int stockIndex = scanner.nextInt();

                System.out.println("How many shares would you like to buy?");

                int stockCount = scanner.nextInt();

                if (stockIndex <= count) {
                    stockIndex--;
                    if (this.balance >= this.stocks.get(stockIndex).price() * stockCount) {
                        this.balance -= this.stocks.get(stockIndex).price() * stockCount;
                        // try and add this to the hashmap if it is already in there then increase it
                        // if it is not then the putIfAbsent will already put it in so we don't need to worry about that
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

        // display stocks
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

            // do this in a try for input sanitation
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
