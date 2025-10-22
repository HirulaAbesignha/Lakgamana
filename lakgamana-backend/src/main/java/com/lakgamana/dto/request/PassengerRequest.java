package com.lakgamana.dto.request;

import com.lakgamana.entity.enums.Gender;
import com.lakgamana.entity.enums.IdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
 

public class PassengerRequest {

    @NotBlank(message = "Passenger name is required")
    @Size(max = 100, message = "Passenger name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be positive")
    private Integer age;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private IdType idType;

    private String idNumber;

    public PassengerRequest() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public IdType getIdType() { return idType; }
    public void setIdType(IdType idType) { this.idType = idType; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
}
