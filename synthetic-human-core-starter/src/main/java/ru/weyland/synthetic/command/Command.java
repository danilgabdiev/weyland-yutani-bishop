package ru.weyland.synthetic.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Command {

    @NotNull(message = "description is required")
    @Size(
            max = 1000,
            message = "Description must be less than 1000 characters"
    )
    private String description;

    @NotNull(message = "commandType is required: COMMON or CRITICAL")
    private CommandType commandType;

    @NotNull(message = "author is required")
    @Size(
            max = 100,
            message = "Author name must be less than 100 characters"
    )
    private String author;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?Z?$",
            message = "Time must match ISO 8601, e.g. 2025-07-18T21:00:00Z"
    )
    private String time;

    @Override
    public String toString() {
        return String.format(
                "Command[description='%s', commandType=%s, author='%s', time='%s']",
                description, commandType, author, time
        );
    }
}
