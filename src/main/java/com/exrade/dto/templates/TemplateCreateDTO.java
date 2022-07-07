package com.exrade.dto.templates;

import com.exrade.models.template.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.exrade.util.ValidationMessage.NOT_BLANK;

/**
 * @author Rhidoy
 * @created 30/06/2022
 * @package com.exrade.dto.templates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateDTO {
    @NotBlank(message = NOT_BLANK)
    private String name;
    private String content;
    private String header;
    private String footer;
    @Valid
    private TemplateType templateType;
}
