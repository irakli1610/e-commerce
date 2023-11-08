package com.code.ecommerce.service;

import com.code.ecommerce.dao.CustomerRepository;
import com.code.ecommerce.dto.Purchase;
import com.code.ecommerce.dto.PurchaseResponse;
import com.code.ecommerce.entity.Customer;
import com.code.ecommerce.entity.Order;
import com.code.ecommerce.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService{
   private CustomerRepository customerRepository;

   @Autowired
    public CheckoutServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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

    private String generateOrderTrackingNumber() {
       String randomTrackingNumber =UUID.randomUUID().toString();
       System.out.println(randomTrackingNumber);
       return randomTrackingNumber;

    }
}
