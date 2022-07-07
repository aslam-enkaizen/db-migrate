package com.exrade.dto.userprofile;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 29, 2022
 */
@Data
@NoArgsConstructor
public class UserDTO {
    @NotBlank
    public String firstName;

    @NotBlank
    public String lastName;

    @NotBlank
    public String timezone;

    @NotBlank
    public String language;

    public String avatar;
}
