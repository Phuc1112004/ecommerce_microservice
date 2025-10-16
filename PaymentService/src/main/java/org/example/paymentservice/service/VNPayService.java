package org.example.paymentservice.service;


import org.example.paymentservice.config.VNPayConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayService {

    @Transactional
    public String createPaymentUrl(Long orderId, Long amount, String ipAddress, String orderInfo) throws Exception {
        // Xử lý ký tự đặc biệt trong OrderInfo
        String orderInfoSafe = orderInfo.replace("#", "_"); // thay # bằng _

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_CurrCode", VNPayConfig.vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderInfo", orderInfoSafe);
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));

        // Sắp xếp key alphabetically
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        // Build hashData + query cùng lúc (encode UTF-8)
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String key = fieldNames.get(i);
            String value = vnp_Params.get(key);

            if (value != null && !value.isEmpty()) {
                String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);

                // hashData (encode giống query, KHÔNG để raw)
                hashData.append(key).append("=").append(encodedValue);

                // query string
                query.append(encodedKey).append("=").append(encodedValue);



//                String encodedValueForHash = URLEncoder.encode(value, StandardCharsets.UTF_8);
//                String encodedValueForQuery = URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
//                String encodedKeyForQuery = URLEncoder.encode(key, StandardCharsets.UTF_8);
//
//                // Dùng cho hash
//                hashData.append(key).append("=").append(encodedValueForHash);
//
//                // Dùng cho query string gửi lên VNPay
//                query.append(encodedKeyForQuery).append("=").append(encodedValueForQuery);



                if (i < fieldNames.size() - 1) { // tránh & thừa
                    hashData.append("&");
                    query.append("&");
                }
            }
        }


// Tạo vnp_SecureHash
        String vnp_SecureHash = hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
//
// Log đầy đủ
        System.out.println("=== HASH DATA ===\n" + hashData);
        System.out.println("=== SECURE HASH ===\n" + vnp_SecureHash);
        System.out.println("=== PAYMENT URL ===\n" + VNPayConfig.vnp_PayUrl + "?" + query);

        return VNPayConfig.vnp_PayUrl + "?" + query;

    }


    public String hashAllFields(Map<String, String> fields) throws Exception {
        // Loại bỏ vnp_SecureHash, vnp_SecureHashType nếu còn
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        // Sắp xếp alphabetically
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

//        for (int i = 0; i < fieldNames.size(); i++) {
//            String key = fieldNames.get(i);
//            String value = fields.get(key);
//            if (value != null && !value.isEmpty()) {
//                String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
//                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
//
//                // hashData (encode giống query, KHÔNG để raw)
//                hashData.append(key).append("=").append(encodedValue);
//
//                // query string
//                query.append(encodedKey).append("=").append(encodedValue);
//
//                if (i < fieldNames.size() - 1) { // tránh & thừa
//                    hashData.append("&");
//                    query.append("&");
//                }
//            }
//        }

        for (int i = 0; i < fieldNames.size(); i++) {
            String key = fieldNames.get(i);
            String value = fields.get(key);
            if (value != null && !value.isEmpty()) {
                // **DÙNG NGUYÊN BẢN, KHÔNG ENCODE**
//                hashData.append(key).append("=").append(value);

//                String decodedValue = java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
//                hashData.append(key).append("=").append(decodedValue);

                hashData.append(key).append("=").append(value);

                if (i < fieldNames.size() - 1) hashData.append("&");
            }
        }



        System.out.println("=== HASH DATA RECEIVED ===\n" + hashData); // log kiểm tra
        return hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
    }


    public String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

}
