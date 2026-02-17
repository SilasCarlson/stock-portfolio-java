import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;

public class StockPortfolio {
    private final ArrayList<Stock> stocks;
    private final HashMap<Stock, Integer> inventory;
    private boolean isRunning;

    public StockPortfolio() {
        // initialize the stocks array and append all stock data
        this.stocks = new ArrayList<>();
        this.stocks.add(new Stock("NVDA", 184));
        this.stocks.add(new Stock("MSFT", 395));

        // other
        this.inventory = new HashMap<>();
        this.isRunning = false;
    }

    public void displayStocks() {
        int count = 0;
        for (Stock stock : this.stocks) {
            count++;
            System.out.printf("%d. %s is listed at $%d%n", count, stock.getIdentifier(), stock.getPrice());
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
                    if (this.inventory.putIfAbsent(this.stocks.get(stockIndex), stockCount) != null) {
                        this.inventory.replace(this.stocks.get(stockIndex), this.inventory.get(this.stocks.get(stockIndex)) + stockCount);
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input!");
            }
        }
    }

    public void displayInventory() {
        this.inventory.forEach(( stock, count) -> {
            System.out.printf("%s - You have %d shares worth $%d%n", stock.getIdentifier(), count, stock.getPrice() * count);
        });
    }

    public void start() {
        this.isRunning = true;
        this.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (this.isRunning) {
            System.out.println("Menu");
            System.out.println("1. View Stocks");
            System.out.println("2. View Inventory");
            System.out.println("3. Quit");

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
