package me.neitexx.configuration.api.reflect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.neitexx.configuration.api.DefaultConfiguration;
import me.neitexx.configuration.api.annotation.ConfigurationPath;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@Getter
@RequiredArgsConstructor
public class FieldWithPathHandler implements FieldHandler {

    private final DefaultConfiguration configuration;

    @Override
    public void read(@NotNull final Field field) {
        val path = field.getAnnotation(ConfigurationPath.class).path();
        val reflectHandler = getConfiguration().getReflectHandler();

        reflectHandler.executeAccessible(accessibleObject -> {
            getConfiguration().set(path, reflectHandler.getFieldValue(this, accessibleObject));
        }, field);
    }

    @Override
    public void write(@NotNull final Field field) {
        val path = field.getAnnotation(ConfigurationPath.class).path();
        val reflectHandler = getConfiguration().getReflectHandler();

        reflectHandler.executeAccessible(accessibleObject -> {
            reflectHandler.setFieldValue(this, accessibleObject, getConfiguration().get(path));
        }, field);
    }

}
