package net.oskarstrom.dashloader.core.annotations;

import net.oskarstrom.dashloader.core.Dashable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegistryTag {
	Class<? extends Dashable<?>> value();
}
