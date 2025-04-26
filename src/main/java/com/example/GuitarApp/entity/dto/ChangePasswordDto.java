package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.util.validators.annotation.NotEqualsField;
import com.example.GuitarApp.util.validators.annotation.ValidOldPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotEqualsField(first = "oldPassword", second = "newPassword", message = "{validation.not_equals_passwords}")
public class ChangePasswordDto{

    @ValidOldPassword
    String oldPassword;

    //    TODO: uncomment in prod
//    @ValidPassword
    String newPassword;
}
