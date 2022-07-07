package com.exrade.dto.negotiation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.exrade.util.ValidationMessage.NOT_BLANK;

/**
 * @author Rhidoy
 * @created 7/4/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    @NotBlank(message = NOT_BLANK)
    private String message;
    private List<String> files;
}
