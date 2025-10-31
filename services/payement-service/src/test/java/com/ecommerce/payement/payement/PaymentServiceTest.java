package com.ecommerce.payement.payement;

import static org.junit.jupiter.api.Assertions.*;



import com.ecommerce.payement.notification.NotificationProducer;
import com.ecommerce.payement.notification.PaymentNotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName(" PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock private PaymentRepository repository;
    @Mock private PaymentMapper mapper;
    @Mock private NotificationProducer notificationProducer;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest request;
    private Payment payment;

    @BeforeEach
    void setUp() {
        // Customer info
        Customer customer = new Customer(
                "1",
                "Salma",
                "Imassenda",
                "salma@example.com"
        );

        // PaymentRequest
        request = new PaymentRequest(
                1,
                BigDecimal.valueOf(2000),
                PaymentMethod.CREDIT_CARD,
                1,
                "REF123",
                customer
        );

        // Payment entity returned by repository
        payment = new Payment();
        payment.setId(1);
        payment.setAmount(request.amount());
        payment.setOrderId(request.orderId());
    }


    @Nested
    @DisplayName(" createPayment() method")
    class CreatePaymentTests {

        @Test
        @DisplayName(" Should create payment and send notification")
        void shouldCreatePaymentSuccessfully() {
            // GIVEN
            when(mapper.toPayment(request)).thenReturn(payment);
            when(repository.save(payment)).thenReturn(payment);

            // WHEN
            Integer paymentId = paymentService.createPayment(request);

            // THEN
            assertNotNull(paymentId);
            assertEquals(1, paymentId);

            verify(mapper, times(1)).toPayment(request);
            verify(repository, times(1)).save(payment);
            verify(notificationProducer, times(1)).sendNotification(any(PaymentNotificationRequest.class));
        }

        @Test
        @DisplayName("Should handle exception when repository fails")
        void shouldHandleRepositoryException() {
            // GIVEN
            when(mapper.toPayment(request)).thenReturn(payment);
            when(repository.save(payment)).thenThrow(new RuntimeException("DB unavailable"));

            // WHEN + THEN
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> paymentService.createPayment(request));

            assertEquals("DB unavailable", exception.getMessage());
            verify(mapper, times(1)).toPayment(request);
            verify(repository, times(1)).save(payment);
            verify(notificationProducer, never()).sendNotification(any(PaymentNotificationRequest.class));
        }
    }
}
