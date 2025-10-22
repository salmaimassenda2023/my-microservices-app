package com.services.orderservice.order;

import com.services.orderservice.customers.customer.CustomerClient;
import com.services.orderservice.customers.payement.PaymentClient;
import com.services.orderservice.customers.payement.PaymentRequest;
import com.services.orderservice.customers.product.ProductClient;
import com.services.orderservice.customers.product.ProductPurchaseRequest;
import com.services.orderservice.exception.BusinessException;
import com.services.orderservice.kafka.OrderConfirmation;
import com.services.orderservice.kafka.OrderProducer;
import com.services.orderservice.orderLine.OrderLineRequest;
import com.services.orderservice.orderLine.OrderLineService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        var customerResponse = this.customerClient.findCustomerById(request.customerId());
        if (customerResponse.getBody() == null) {
            throw new BusinessException("Cannot create order:: No customer exists with the provided ID");
        }
        var customer = customerResponse.getBody();


        var purchasedProducts = productClient.purchaseProducts(request.products()).getBody();
        List<ProductPurchaseRequest> productRequests = purchasedProducts.stream()
                .map(product -> new ProductPurchaseRequest(product.productId(), product.quantity()))
                .toList();

        var order = this.repository.save(mapper.toOrder(request));

        for (ProductPurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        productRequests
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
