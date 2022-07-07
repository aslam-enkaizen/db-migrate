package com.exrade.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rhidoy
 * @created 7/1/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaypalPaymentMethodDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String note;
}
