package org.iesra.procesaalumnos.service

import java.io.File
import java.nio.file.Path

class FileRepository : IFileRepository {

    override fun findInputFiles(path: Path): List<File> {
        val dir = path.toFile()
        if (!dir.exists() || !dir.isDirectory) {
            throw IllegalArgumentException("La ruta no es válida")
        }

        return dir.listFiles { file ->
            file.isFile &&
                    file.extension == "txt" &&
                    !file.name.contains("-grupos")
        }?.toList() ?: emptyList()
    }

    override fun moveToProcessed(file: File, basePath: Path) {
        val processedDir = basePath.resolve("procesados").toFile()
        if (!processedDir.exists()) {
            processedDir.mkdir()
        }
        val destination = File(processedDir, file.name)
        file.renameTo(destination)
    }
}