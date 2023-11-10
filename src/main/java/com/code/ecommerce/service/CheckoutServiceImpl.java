package com.code.ecommerce.service;

import com.code.ecommerce.dao.CustomerRepository;
import com.code.ecommerce.dto.PaymentInfo;
import com.code.ecommerce.dto.Purchase;
import com.code.ecommerce.dto.PurchaseResponse;
import com.code.ecommerce.entity.Customer;
import com.code.ecommerce.entity.Order;
import com.code.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService{
   private CustomerRepository customerRepository;

   @Autowired
    public CheckoutServiceImpl(CustomerRepository customerRepository,
                               @Value("${stripe.key.secret}") String secretKey)
   {
        this.customerRepository = customerRepository;

       Stripe.apiKey=secretKey;
    }

    @Override
    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {


       Order order = purchase.getOrder();


       String orderTrackingNumber = generateOrderTrackingNumber();
       order.setOrderTrackingNumber(orderTrackingNumber);



        Set<OrderItem> orderItems = purchase.getOrderItems();
       orderItems.forEach(item -> order.add(item));


       order.setBillingAddress(purchase.getBillingAddress());
       order.setShippingAddress(purchase.getShippingAddress());

        System.out.println(purchase);
       Customer customer= purchase.getCustomer();

       String theEmail = customer.getEmail();
       Customer customerFromDB = customerRepository.findByEmail(theEmail);
       if(customerFromDB != null){
          customer=customerFromDB;
       }

       customer.add(order);

       customerRepository.save(customer);

       return new PurchaseResponse(orderTrackingNumber);
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
       List<String> paymentMethodTypes = new ArrayList<>();
       paymentMethodTypes.add("card");

       Map<String, Object> params = new HashMap<>();
       params.put("amount", paymentInfo.getAmount());
       params.put("currency", paymentInfo.getCurrency());
       params.put("payment_method_types", paymentMethodTypes);
       params.put("description","Luv2shop purchase");
       params.put("receipt_email",paymentInfo.getReceiptEmail());

       return PaymentIntent.create(params);
    }

    private String generateOrderTrackingNumber() {
       String randomTrackingNumber =UUID.randomUUID().toString();
       System.out.println(randomTrackingNumber);
       return randomTrackingNumber;

    }
}
