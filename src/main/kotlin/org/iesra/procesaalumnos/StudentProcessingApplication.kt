package org.iesra.procesaalumnos

import org.iesra.procesaalumnos.cli.CliOptions
import org.iesra.procesaalumnos.model.FileIssue
import org.iesra.procesaalumnos.model.Student
import org.iesra.procesaalumnos.service.FileRepository
import org.iesra.procesaalumnos.service.EmailGenerator
import java.nio.file.Path

class StudentProcessingApplication {

    fun run(options: CliOptions) {

        val inputDir = options.path ?: Path.of(".")

        val fileRepository = FileRepository()
        val parser = StudentParser()

        val files = fileRepository.findInputFiles(inputDir)

        val students = mutableListOf<Student>()
        val issues = mutableListOf<FileIssue>()

        println("Ficheros encontrados: ${files.size}")

        for (file in files) {

            try {
                val student = parser.parse(file)
                students.add(student)

                println("OK -> ${file.name}")

                val emailGenerator = EmailGenerator()
                val email = emailGenerator.generate(student)

                student.emailInstituto = email

                students.add(student)

            } catch (e: Exception) {
                issues.add(FileIssue(file.name, e.message ?: "Error desconocido"))
                println("ERROR -> ${file.name}")
            }

            // mover siempre, aunque falle
            fileRepository.moveToProcessed(file, inputDir)
        }

        println()
        println("Alumnos válidos: ${students.size}")
        println("Errores: ${issues.size}")
    }
}