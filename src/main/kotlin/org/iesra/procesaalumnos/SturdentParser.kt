package org.iesra.procesaalumnos.service

import org.iesra.procesaalumnos.model.Student
import java.io.File
import org.iesra.procesaalumnos.interfaces.IStudentParser

class StudentParser : IStudentParser {

    override fun parse(file: File): Student {
        val lines = file.readLines()
        var nombre: String? = null
        var apellidos: String? = null
        var email: String? = null
        var grupo: String? = null

        for (line in lines) {
            when {
                line.startsWith("Nombre:") -> nombre = line.substringAfter("Nombre:").trim()
                line.startsWith("Apellido:") -> apellidos = line.substringAfter("Apellido:").trim()
                line.startsWith("email") -> email = line.substringAfter(";").trim()
                line.startsWith("Grupo") -> grupo = line.substringAfter("=").trim()
            }
        }

        if (nombre == null || apellidos == null || email == null) {
            throw IllegalArgumentException("Formato incorrecto")
        }

        return Student(nombre, apellidos, email, grupo)
    }
}