import java.util.ArrayList;

public class Order {
    // Statik alanlar
    private static int nextOrderNumber = 1001;
    public static final double MIN_ORDER = 100.0;     // 100 TL
    public static final double DELIVERY_FEE = 20.0;   // 20 TL
    private static int completedOrders = 0;
    private static double totalRevenue = 0.0;

    // Nesne alanları
    private int orderNumber;
    private Customer customer;
    private ArrayList<MenuItem> items;
    private Coupon coupon;
    private boolean isCompleted;

    public Order(Customer customer) {
        this.orderNumber = nextOrderNumber++;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.coupon = null;
        this.isCompleted = false;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    // a. addItem: Aynı isimde ürün varsa miktarı artırılır
    public void addItem(MenuItem item) {
        for (MenuItem existing : items) {
            if (existing.getName().equalsIgnoreCase(item.getName())) {
                try {
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                } catch (InvalidQuantityException e) {
                    // Pozitif iki sayının toplamı pozitif olacağından buraya düşmez
                }
                return;
            }
        }
        // Dışarıdan gelen referansın bağımsız olması için yeni kopya eklenir
        try {
            items.add(new MenuItem(item.getName(), item.getPrice(), item.getQuantity()));
        } catch (InvalidQuantityException e) {
            // Kontrol zaten MenuItem oluşturulurken yapıldı
        }
    }

    // b. removeItem
    public void removeItem(String name) throws ItemNotFoundException {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equalsIgnoreCase(name)) {
                items.remove(i);
                return;
            }
        }
        throw new ItemNotFoundException("Item with name '" + name + "' was not found in Order #" + orderNumber);
    }

    // b. updateQuantity
    public void updateQuantity(String name, int quantity) throws ItemNotFoundException, InvalidQuantityException {
        for (MenuItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.setQuantity(quantity);
                return;
            }
        }
        throw new ItemNotFoundException("Item with name '" + name + "' was not found in Order #" + orderNumber);
    }

    // c. getSubtotal
    public double getSubtotal() {
        double subtotal = 0.0;
        for (MenuItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    // c. getItems: Dışarıdan değiştirilememesi için listenin kopyasını döner
    public ArrayList<MenuItem> getItems() {
        ArrayList<MenuItem> copyList = new ArrayList<>();
        for (MenuItem item : items) {
            try {
                copyList.add(new MenuItem(item.getName(), item.getPrice(), item.getQuantity()));
            } catch (InvalidQuantityException ignored) {}
        }
        return copyList;
    }

    // 5. applyCoupon
    public void applyCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    // 7. checkout
    public double checkout() throws MinimumOrderException {
        if (isCompleted) {
            System.out.println("Warning: Order #" + orderNumber + " is already completed and cannot be checked out again.");
            return 0.0;
        }

        double subtotal = getSubtotal();
        if (subtotal < MIN_ORDER) {
            throw new MinimumOrderException("Order total (" + String.format("%.2f", subtotal) 
                    + " TL) is below the minimum order amount of " + MIN_ORDER + " TL!");
        }

        double discount = 0.0;
        if (coupon != null) {
            if (subtotal >= coupon.getMinSubtotal()) {
                discount = subtotal * coupon.getDiscountRate();
            } else {
                System.out.println("Note: Coupon '" + coupon.getCode() + "' could not be applied. Minimum subtotal required: " 
                        + coupon.getMinSubtotal() + " TL");
            }
        }

        double finalTotal = subtotal - discount + DELIVERY_FEE;

        // Fiş yazdırma (Receipt)
        System.out.println("\n==================== RECEIPT ====================");
        System.out.println("Order Number  : " + orderNumber);
        System.out.println("Customer Name : " + customer.getName());
        System.out.println("Address       : " + customer.getAddress());
        System.out.println("-------------------------------------------------");
        System.out.println("Items:");
        for (MenuItem item : items) {
            System.out.println(" - " + item);
        }
        System.out.println("-------------------------------------------------");
        System.out.println(String.format("Subtotal        : %.2f TL", subtotal));
        System.out.println(String.format("Coupon Discount : -%.2f TL", discount));
        System.out.println(String.format("Delivery Fee    : %.2f TL", DELIVERY_FEE));
        System.out.println(String.format("FINAL TOTAL     : %.2f TL", finalTotal));
        System.out.println("=================================================\n");

        // İstatistikleri güncelle
        completedOrders++;
        totalRevenue += finalTotal;
        isCompleted = true;

        return finalTotal;
    }

    // Statik Getter Metotları
    public static int getCompletedOrders() {
        return completedOrders;
    }

    public static double getTotalRevenue() {
        return totalRevenue;
    }
}
