package uk.co.epsilontechnologies.surgecheck;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractIT {

    static {
        setEnv("ENV.NAME", "test");
    }

    public static void setEnv(String key, String value) {
        try {
            final Class[] classes = Collections.class.getDeclaredClasses();
            final Map<String, String> env = System.getenv();
            for (final Class clazz : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(clazz.getName())) {
                    final Field field = clazz.getDeclaredField("m");
                    field.setAccessible(true);
                    final Object obj = field.get(env);
                    final Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.put(key, value);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
