package me.neitexx.configuration.api.reflect;

import me.neitexx.configuration.api.DefaultConfiguration;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public interface FieldHandler {

    DefaultConfiguration getConfiguration();

    void read(@NotNull Field field);

    void write(@NotNull Field field);

}
