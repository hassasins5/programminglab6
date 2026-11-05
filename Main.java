public class Main {
    public static void main(String[] args) {
        System.out.println("----- TEST 1: MUSTERI VE SIPARISLERIN OLUSTURULMASI -----");
        // İki müşteri oluşturuluyor (Adres composition ile oluşturuluyor)
        Customer customer1 = new Customer("Ahmet Yilmaz", "05551112233", "Mevlana Cad. No:10", "Talas", "Kayseri");
        Customer customer2 = new Customer("Ayse Demir", "05334445566", "Bagdat Cad. No:25", "Kadikoy", "Istanbul");

        // İki sipariş oluşturuluyor (Order number'lar otomatik 1001, 1002 olarak atanacak)
        Order order1 = new Order(customer1);
        Order order2 = new Order(customer2);

        System.out.println("Created " + customer1.getName() + " -> Order #" + order1.getOrderNumber());
        System.out.println("Created " + customer2.getName() + " -> Order #" + order2.getOrderNumber());

        try {
            System.out.println("\n----- TEST 2: URUN EKLEME (AYNI URUNU IKI KEZ EKLEME DAHIL) -----");
            MenuItem burger1 = new MenuItem("Burger", 120.0, 1);
            MenuItem pizza = new MenuItem("Pizza", 180.0, 1);
            MenuItem burger2 = new MenuItem("Burger", 120.0, 2); // Aynı ürün tekrar ekleniyor

            order1.addItem(burger1);
            order1.addItem(pizza);
            order1.addItem(burger2); // Burger miktarı 1 + 2 = 3 olacak

            System.out.println("Order 1 icerigi:");
            for (MenuItem item : order1.getItems()) {
                System.out.println(" -> " + item);
            }

            System.out.println("\n----- TEST 3: QUANTITY GUNCELLEME VE URUN SILME -----");
            order1.updateQuantity("Pizza", 2); // Pizza miktarını 2 yap
            System.out.println("Pizza adedi 2 olarak guncellendi.");

            MenuItem cola = new MenuItem("Cola", 40.0, 1);
            order1.addItem(cola);
            System.out.println("Cola eklendi, ardindan siliniyor...");
            order1.removeItem("Cola");

            System.out.println("\n----- TEST 4: GECERLI KUPON ILE CHECKOUT (ORDER 1) -----");
            // %20 indirim, minimum sepet 200 TL
            Coupon validCoupon = new Coupon("DISCOUNT20", 0.20, 200.0);
            order1.applyCoupon(validCoupon);
            order1.checkout();

            System.out.println("\n----- TEST 5: KOSULU SAGLAMAYAN KUPON ILE CHECKOUT (ORDER 2) -----");
            MenuItem soup = new MenuItem("Mercimek Corbasi", 110.0, 1);
            order2.addItem(soup);

            // Min subtotal 300 TL olan bir kupon (Sepet 110 TL olduğu için kupon uygulanmayacak)
            Coupon strictCoupon = new Coupon("BIGDEAL", 0.30, 300.0);
            order2.applyCoupon(strictCoupon);
            order2.checkout();

        } catch (Exception e) {
            System.out.println("Beklenmeyen hata: " + e.getMessage());
        }

        System.out.println("\n----- TEST 6: CUSTOM EXCEPTION'LARI TETIKLEME VE YAKALAMA -----");

        // 1. InvalidQuantityException Testi
        try {
            System.out.println("Tetikleniyor: InvalidQuantityException...");
            MenuItem invalidItem = new MenuItem("Su", 10.0, 0); // Miktar 0 verildi
        } catch (InvalidQuantityException e) {
            System.out.println("-> Yakalandi: " + e.getMessage());
        }

        // 2. ItemNotFoundException Testi
        try {
            System.out.println("\nTetikleniyor: ItemNotFoundException...");
            order1.removeItem("OlmayanUrun");
        } catch (ItemNotFoundException e) {
            System.out.println("-> Yakalandi: " + e.getMessage());
        }

        // 3. MinimumOrderException Testi
        try {
            System.out.println("\nTetikleniyor: MinimumOrderException...");
            Order order3 = new Order(customer1);
            order3.addItem(new MenuItem("Cay", 25.0, 1)); // 25 TL < 100 TL MIN_ORDER
            order3.checkout();
        } catch (MinimumOrderException | InvalidQuantityException e) {
            System.out.println("-> Yakalandi: " + e.getMessage());
        }

        System.out.println("\n----- TEST 7: STATIK ISTATISTIKLERIN YAZDIRILMASI -----");
        System.out.println("Completed Orders: " + Order.getCompletedOrders());
        System.out.println("Total Revenue   : " + String.format("%.2f", Order.getTotalRevenue()) + " TL");
    }
}
