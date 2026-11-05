public class Coupon {
    private String code;
    private double discountRate; // Örn: 0.20 (%20 indirim)
    private double minSubtotal;

    public Coupon(String code, double discountRate, double minSubtotal) {
        this.code = code;
        if (discountRate > 0.50) {
            this.discountRate = 0.50; // En fazla %50 kuralı
        } else if (discountRate < 0) {
            this.discountRate = 0;
        } else {
            this.discountRate = discountRate;
        }
        this.minSubtotal = minSubtotal;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public double getMinSubtotal() {
        return minSubtotal;
    }
}
