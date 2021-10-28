package dev.quantumfusion.dashloader.core.objects;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(Identifier.class)
public record IdentifierDash(String text) implements Dashable<Identifier> {

	public IdentifierDash(Identifier identifier) {
		this(identifier.text);
	}

	@Override
	public Identifier export(DashRegistryReader registry) {
		return new Identifier(text);
	}
}
