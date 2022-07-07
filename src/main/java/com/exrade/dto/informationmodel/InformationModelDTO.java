package com.exrade.dto.informationmodel;

import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.models.processmodel.ModelPrivacyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exrade.util.ValidationMessage.NOT_BLANK;

/**
 * @author Rhidoy
 * @created 7/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationModelDTO {
    @NotBlank(message = NOT_BLANK)
    private String name;
    private String language = "en";
    private List<String> supportedLanguages = new ArrayList<>();
    @NotBlank(message = NOT_BLANK)
    private String title;
    private String description;
    private Map<String, String> titleTranslations = new HashMap<>();
    private Map<String, String> descriptionTranslations = new HashMap<>();
    private int modelVersion = 0;
    private ModelPrivacyLevel privacyLevel = ModelPrivacyLevel.PUBLIC;
    private String category;
    @NotBlank(message = NOT_BLANK)
    private String template;
    private PublishStatus publishStatus;
    private boolean archived;
    private List<Object> tags = new ArrayList<>();
    private List<Attribute> items = new ArrayList<>();
    private HashMap<String, Object> customFields = new HashMap<>();
}
