package org.iesra.procesaalumnos.service

import org.iesra.procesaalumnos.model.Student

class EmailGenerator {

    fun generate(student: Student): String {

        val partes = student.apellidos.split(" ")

        val segundoApellido = if (partes.size > 1) partes[1] else partes[0]

        val primeraLetraNombre = student.nombre.first().lowercaseChar()

        val segundaLetraSegundoApellido =
            if (segundoApellido.length > 1)
                segundoApellido[1].lowercaseChar()
            else
                ""

        return "$primeraLetraNombre${segundoApellido.lowercase()}$segundaLetraSegundoApellido@iesrafaelalberti.es"
    }
}