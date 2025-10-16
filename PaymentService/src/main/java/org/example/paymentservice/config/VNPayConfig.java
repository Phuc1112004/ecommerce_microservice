package org.example.paymentservice.config;


public class VNPayConfig {

    // URL của VNPay Sandbox
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // URL VNPay production (sau khi deploy)
    // public static final String vnp_PayUrl = "https://pay.vnpay.vn/vpcpay.html";

    // Mã website VNPay cung cấp
    public static final String vnp_TmnCode = "0WZQ4FRB";

    // Hash secret key VNPay cung cấp
    public static final String vnp_HashSecret = "3AUH81O8ZCAEG4Q09SXS7KGVYRLB9SF4";

    // URL trả về sau khi thanh toán xong
    public static final String vnp_ReturnUrl = "http://localhost:8080/api/vnpay/return";
//vnp_Params.put("vnp_ReturnUrl", "http://127.0.0.1:8080/api/vnpay/return");

    // Version VNPay
    public static final String vnp_Version = "2.1.0";

    // Command mặc định
    public static final String vnp_Command = "pay";

    // Mã tiền tệ
    public static final String vnp_CurrCode = "VND";



//    private static final String vnp_Version = "2.1.0";
//    private static final String vnp_Command = "pay";
//    private static final String vnp_TmnCode = "0WZQ4FRB"; // thay bằng code VNPay
//    private static final String vnp_HashSecret = "382XHXVSOR08ZYV1MYU7G1X5DJL6UC3V"; // key bảo mật
//    private static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
//    private static final String vnp_Returnurl = "http://localhost:8080/api/vnpay/return";

}

