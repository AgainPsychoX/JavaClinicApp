package pl.edu.ur.pz.clinicapp.localization;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaFxBuiltInsLocalizationFix {
    private static final Logger logger = Logger.getLogger(JavaFxBuiltInsLocalizationFix.class.getName());

    // Same as ControlResources.BASE_NAME
    private static final String BASE_NAME = "com/sun/javafx/scene/control/skin/resources/controls";

    /**
     * Hacky way to localize JavaFx built-in controls, by injecting custom resources
     * to resource bundle by putting it in bundles cache.
     *
     * Inspired by <a href="https://stackoverflow.com/a/48773353/4880243">StackOverflow answer</a>
     * by Oleg Kurbatov in topic of "Localizing JavaFx Controls".
     *
     * This method relies on hacky reflection and ignoring modules encapsulation. Will generate error like:
     * > Unable to make java.util.ResourceBundle$CacheKey(...) accessible:
     * > module java.base does not "opens java.util" to module pl.edu.ur.pz.clinicapp
     * or
     * > class pl.foo.bar.JavaFxBuiltInsLocalizationFix (in module pl.foo.bar)
     * > cannot access class com.sun.javafx.scene.control.skin.resources.ControlResources (in module javafx.controls)
     * > because module javafx.controls does not export com.sun.javafx.scene.control.skin.resources to module pl.foo.bar
     * Solution: add following lines to VM options:
     * --add-opens java.base/java.util=pl.foo.bar
     * --add-exports javafx.controls/com.sun.javafx.scene.control.skin.resources=pl.foo.bar
     *
     * @param locale Locale to use.
     */
    static public void injectLocalizationForJavaFxBuiltInControls(Locale locale) {
        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);
        String resourceName = control.toResourceName(control.toBundleName(BASE_NAME, locale), "properties");
        ResourceBundle bundle;
        try {
            final var stream = JavaFxBuiltInsLocalizationFix.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new NullPointerException("Couldn't find resource '%s'".formatted(resourceName));
            }
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                bundle = new PropertyResourceBundle(reader);
            }
            // TODO: retry by finding base locale (if any) by stripping locale details

            // Create new cache key
            Class<?> cacheKeyClass = Class.forName("java.util.ResourceBundle$CacheKey");
            Constructor<?> cacheKeyClassConstructor = cacheKeyClass.getDeclaredConstructor(String.class, Locale.class, Module.class, Module.class);
            cacheKeyClassConstructor.setAccessible(true);
            Object cacheKey = cacheKeyClassConstructor.newInstance(BASE_NAME, locale, ControlResources.class.getModule(), ControlResources.class.getModule());

            // Put it into the cache
            Method putBundleInCache = ResourceBundle.class.getDeclaredMethod("putBundleInCache", cacheKeyClass, ResourceBundle.class, ResourceBundle.Control.class);
            putBundleInCache.setAccessible(true);
            putBundleInCache.invoke(null, cacheKey, bundle, control);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to inject localization for JavaFx built-in controls", e);
            Platform.exit();
        }
    }
}
