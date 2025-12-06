package uz.pdp.ecommerce2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderRequest {

    @NotBlank(message = "shippingAddress must not be blank")
    private String shippingAddress;

    private String paymentMethod;
    private String notes;


    @JsonCreator
    public OrderRequest(
            @JsonProperty("shippingAddress") String shippingAddress,
            @JsonProperty("paymentMethod") String paymentMethod,
            @JsonProperty("notes") String notes) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }
}