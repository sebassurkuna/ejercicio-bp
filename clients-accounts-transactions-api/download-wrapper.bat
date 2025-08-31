@echo off
echo Descargando gradle-wrapper.jar...
powershell -Command "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.10.2/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle/wrapper/gradle-wrapper.jar'"
echo Gradle wrapper descargado exitosamente.
