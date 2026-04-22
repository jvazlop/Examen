package org.iesra.procesaalumnos.service

import org.iesra.procesaalumnos.model.Student
import org.iesra.procesaalumnos.interfaces.IEmailGenerator

/**
 * Implementación del generador de correos según las reglas del Rafael Alberti.
 */
class EmailGenerator : IEmailGenerator {

    /**
     * Genera un correo con el formato:
     * primera letra nombre + segundo apellido + segunda letra segundo apellido + @iesrafaelalberti.es
     */
    override fun generate(student: Student): String {
        // Separamos los apellidos por espacios
        val partesApellidos = student.apellidos.trim().split(" ")

        // El enunciado pide usar el "segundo apellido".
        // Si solo tiene uno, usamos ese como fallback.
        val segundoApellido = if (partesApellidos.size > 1) {
            partesApellidos[1].lowercase()
        } else {
            partesApellidos[0].lowercase()
        }

        // 1. Primera letra del nombre en minúscula
        val primeraLetraNombre = student.nombre.trim().first().lowercaseChar()

        // 2. Segunda letra del segundo apellido en minúscula
        // Si el apellido es muy corto (1 letra), usamos un string vacío
        val segundaLetraSegundoApellido = if (segundoApellido.length > 1) {
            segundoApellido[1].lowercaseChar()
        } else {
            ""
        }

        // 3. Construcción final: nombre + apellido completo + letra suelta + dominio
        return "$primeraLetraNombre$segundoApellido$segundaLetraSegundoApellido@iesrafaelalberti.es"
    }
}