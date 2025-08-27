package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RoleEntity {

	@Id
	private UUID id;

	private String name;

	private String description;

}
