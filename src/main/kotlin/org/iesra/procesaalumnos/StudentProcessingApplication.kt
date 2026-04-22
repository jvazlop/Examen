package org.iesra.procesaalumnos

import org.iesra.procesaalumnos.cli.CliOptions
import org.iesra.procesaalumnos.model.FileIssue
import org.iesra.procesaalumnos.model.Student
import org.iesra.procesaalumnos.service.FileRepository
import org.iesra.procesaalumnos.service.EmailGenerator
import org.iesra.procesaalumnos.service.GroupAssigner
import org.iesra.procesaalumnos.service.OutputWriter
import java.nio.file.Path

class StudentProcessingApplication {

    fun run(options: CliOptions) {
        // Definir directorio de trabajo (por defecto el actual si no se indica ruta) [cite: 80]
        val inputDir = options.path ?: Path.of(".")

        // Instancia de servicios
        val fileRepository = FileRepository()
        val parser = StudentParser()
        val emailGenerator = EmailGenerator()
        val groupAssigner = GroupAssigner()
        val outputWriter = OutputWriter()

        // Localizar ficheros .txt [cite: 78]
        val files = fileRepository.findInputFiles(inputDir)
        val students = mutableListOf<Student>()
        val issues = mutableListOf<FileIssue>()

        println("Ficheros encontrados: ${files.size}")

        for (file in files) {
            try {
                // 1. Leer y validar fichero [cite: 78]
                val student = parser.parse(file)

                // 2. Generar correo del instituto con formato específico [cite: 75, 78]
                val email = emailGenerator.generate(student)
                student.emailInstituto = email

                // 3. Asignar grupo (validando plazas y límite de 5 alumnos) [cite: 75, 78, 81]
                groupAssigner.assignGroup(student)

                // 4. Añadir a la lista de procesados correctamente
                students.add(student)
                println("OK -> ${file.name}")

            } catch (e: Exception) {
                // Registrar incidencia si el formato es incorrecto [cite: 78, 81]
                issues.add(FileIssue(file.name, e.message ?: "Error desconocido"))
                println("ERROR -> ${file.name}")
            }

            // 5. Mover siempre el fichero a la carpeta 'procesados' [cite: 75, 81, 88]
            fileRepository.moveToProcessed(file, inputDir)
        }

        // --- GENERACIÓN DE ARCHIVOS DE SALIDA ---

        // Generar CSV de correos (<grupo>correos.csv) y TXT de grupos (<grupo>-grupos.txt) [cite: 75, 79]
        outputWriter.writeFiles(
            options.group,
            students,
            groupAssigner.getAllGroups(),
            inputDir
        )

        // Mostrar resumen por consola [cite: 79, 81]
        mostrarResumenPantalla(students, issues, groupAssigner)
    }

    /**
     * Muestra el resumen final del procesamiento por salida estándar [cite: 76, 81]
     */
    private fun mostrarResumenPantalla(
        students: List<Student>,
        issues: List<FileIssue>,
        groupAssigner: GroupAssigner
    ) {
        println("\n" + "=".repeat(30))
        println("RESUMEN DEL PROCESAMIENTO")
        println("Ficheros procesados: ${students.size + issues.size}")
        println("Ficheros con errores: ${issues.size}")
        println("Correos creados correctamente: ${students.size}")

        println("\nResumen de grupos:")
        groupAssigner.getAllGroups().forEach { (nombre, lista) ->
            println("- Grupo-$nombre: ${lista.size} alumnos")
        }

        println("\nIncidencias:")
        // Incidencias de formato/lectura
        issues.forEach { println("- archivo ${it.fileName}: ${it.message}") }
        // Incidencias de asignación de grupos (ej: grupo lleno o no informado)
        groupAssigner.issues.forEach { println("- $it") }
    }
}