package com.model;

/**
 * Catálogo simple de métodos de resolución para que el usuario indique
 * con qué técnica desea abordar la integral y el sistema pueda validar
 * si aplica o no.
 */
public enum MetodoResolucion {
    SUSTITUCION("Sustitución"),
    FRACCIONES_PARCIALES("Fracciones parciales"),
    TRIGONOMETRICA("Sustitución trigonométrica"),
    INTEGRACION_POR_PARTES("Integración por partes"),
    FORMULA_DIRECTA("Fórmula directa / tabla");

    private final String descripcion;

    MetodoResolucion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
