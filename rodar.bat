@echo off
rem Compila tudo e executa um comando.  Uso: rodar testes ^| simular [--sem-rede] ^| grafo [pacote] ^| diff [...]
chcp 65001 >nul
cd /d "%~dp0"
if exist out rmdir /s /q out
javac --release 17 -encoding UTF-8 -nowarn -d out -sourcepath "src;test;ferramentas" ferramentas\ru\ferramentas\Cli.java
if errorlevel 1 exit /b 1
java -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp out ru.ferramentas.Cli %*
