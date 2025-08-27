package co.com.pragma.crediya.model.user;

import java.util.UUID;

public record Role(
		UUID id,
		String name,
		String description) {
}
