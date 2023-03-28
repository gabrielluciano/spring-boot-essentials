package academy.devdojo.springbootessentials.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class AnimePutRequestBody {

    @NotNull(message = "The anime id cannot be null")
    private Long id;
    @NotBlank(message = "The anime name cannot be blank")
    private String name;
}
