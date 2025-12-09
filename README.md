# Generador de Integrales

Guía rápida para compilar y distribuir la aplicación.

## Requisitos previos
- **Java 22** (o versión compatible definida en `pom.xml`).
- **Apache Maven 3.9+**.

### Instalar Maven en Windows
1. Descarga la versión binaria desde [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).
2. Descomprime el ZIP en una ruta sin espacios, por ejemplo `C:\tools\maven`.
3. Crea la variable de entorno `MAVEN_HOME` apuntando a esa carpeta y agrega `%MAVEN_HOME%\bin` a `PATH`.
4. Abre una consola nueva y verifica la instalación:
   ```powershell
   mvn -v
   ```
   Si obtienes *CommandNotFoundException*, revisa las variables de entorno y reinicia la consola.

## Construir el JAR
En la raíz del proyecto ejecuta:
```bash
mvn clean package
```
Se generarán dos artefactos:
- `target/integrales-generator-1.0-0.jar` (solo tu código).
- `target/integrales-generator-1.0-0-shaded.jar` (incluye dependencias y manifiesto con `Main-Class`).

## Ejecutar

```bash
java -jar target/integrales-generator-1.0-0-shaded.jar
```

## Crear un instalador `.exe` en Windows

Requisitos: JDK 21+ (incluye `jpackage`) y Maven en el `PATH`.

1. Abre PowerShell en la raíz del proyecto.
2. Ejecuta el script de ayuda:
   ```powershell
   .\scripts\win\build-windows-exe.ps1
   ```
   - Compila el JAR con dependencias (`*-shaded.jar`).
   - Ejecuta `jpackage` y deja el instalador en `dist/GeneradorIntegrales-<version>.exe`.

Comando manual (opcional):
```powershell
mvn clean package
jpackage --type exe --name GeneradorIntegrales `
  --input target `
  --main-jar integrales-generator-1.0-0-shaded.jar `
  --main-class com.main.Main `
  --app-version 1.0.0 `
  --dest dist
```
