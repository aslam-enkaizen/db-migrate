package com.exrade.dto.worksgroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.exrade.util.ValidationMessage.NOT_BLANK;

/**
 * @author Rhidoy
 * @created 29/06/2022
 * @package com.exrade.dto.worksgroup
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDTO {
    @NotBlank(message = NOT_BLANK)
    public String message;
    public List<String> files;
}
