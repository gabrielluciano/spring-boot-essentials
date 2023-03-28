package academy.devdojo.springbootessentials.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class AnimePostRequestBody {

    @NotBlank(message = "The anime name cannot be blank")
    private String name;
}
