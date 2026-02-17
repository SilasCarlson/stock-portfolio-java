public class Stock {
    private final String identifier;
    private final int price;

    public Stock(String identifier, int price) {
        this.identifier = identifier;
        this.price = price;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getPrice() {
        return this.price;
    }
}
