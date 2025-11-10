package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeCategory;
import com.unnamed.conectareparo.entity.PledgeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for updating {@link com.unnamed.conectareparo.entity.Pledge} via http request.
 */
@Schema(description = "Data Transfer Object for updating Pledge via http request.")
public record PledgeUpdateDto(
        @Schema(description = "The name of the volunteer making the pledge.", example = "John Doe")
        String volunteerName,
        @Schema(description = "The contact information of the volunteer.", example = "Email: john@doe.com or Phone: +123456789")
        String volunteerContact,
        @Schema(description = "A brief description of the pledge.", example = "Eu consigo ajudar com o conserto de computadores.")
        String description,
        @Schema(description = "The type of pledge being made.", example = "MATERIAL")
        PledgeCategory type,
        @Schema(description = "An update status for the pledge.", example = "OFFERED")
        PledgeStatus status
) {
}