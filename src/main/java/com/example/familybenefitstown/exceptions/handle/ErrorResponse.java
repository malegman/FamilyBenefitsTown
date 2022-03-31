package com.example.familybenefitstown.exceptions.handle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  @JsonProperty("status")
  private int status;

  @JsonProperty("codeApiError")
  private int codeApiError;
}
