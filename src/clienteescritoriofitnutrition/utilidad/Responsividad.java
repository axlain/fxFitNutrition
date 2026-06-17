package clienteescritoriofitnutrition.utilidad;

import java.util.Locale;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

/**
 * Escalado proporcional de tipografia: fija el -fx-font-size del nodo segun el
 * ancho de la ventana, acotado entre [min, max]. Los hijos que usen unidades
 * "em" en el CSS crecen/encogen de forma proporcional. Reutilizable por el
 * shell (dashboard + modulos) y por los formularios modales.
 */
public class Responsividad {

    public static void aplicar(final Region nodo, final double base,
            final double anchoRef, final double min, final double max) {

        nodo.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> obs, Scene anterior, Scene nueva) {
                if (nueva == null) {
                    return;
                }
                escalar(nodo, nueva.getWidth(), base, anchoRef, min, max);
                nueva.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> o, Number antes, Number ahora) {
                        escalar(nodo, ahora.doubleValue(), base, anchoRef, min, max);
                    }
                });
            }
        });
    }

    private static void escalar(Region nodo, double ancho, double base,
            double anchoRef, double min, double max) {

        double tam = base * (ancho / anchoRef);
        if (tam < min) {
            tam = min;
        } else if (tam > max) {
            tam = max;
        }
        nodo.setStyle("-fx-font-size: " + String.format(Locale.US, "%.1f", tam) + "px;");
    }
}
