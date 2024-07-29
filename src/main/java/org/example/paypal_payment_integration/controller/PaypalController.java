package org.example.paypal_payment_integration.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paypal_payment_integration.service.PaypalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    private final PaypalService paypalService;

    @GetMapping("/")
    public String home(){
        return "index";
    }
    @PostMapping("payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description) {
        try {
            String cancelUrl = "https://localhost:8080/payments/cancel";
            String successUrl = "https://localhost:8080/payments/success";

            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelUrl, successUrl);
            for (Links links: payment.getLinks()){
                if (links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        }catch (PayPalRESTException exception){
            log.error("error occurred ::", exception);

        }
        return new RedirectView("payments/error");
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam ("PaymentId") String paymentId,
                                 @RequestParam ("PayerID") String payerId){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")){
                return "paymentSuccess";
            }
        }catch (PayPalRESTException exception){
            log.error("error occurred::", exception);
        }
        return "paymentSuccess";
    }
    @GetMapping("payments/cancel")
    public String paymentCancel(){
        return "paymentCancel";
    }
    @GetMapping("payments/error")
    public String paymentError(){
        return "paymentError";
    }

}
