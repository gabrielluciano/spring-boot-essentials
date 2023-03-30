package academy.devdojo.springbootessentials.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {

    @NotBlank(message = "The anime name cannot be blank")
    private String name;
}
