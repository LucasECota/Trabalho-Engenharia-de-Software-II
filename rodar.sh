#!/bin/sh
# Compila tudo e executa um comando.  Uso: ./rodar.sh testes | simular [--sem-rede] | grafo [pacote] | diff [...]
cd "$(dirname "$0")" || exit 1
rm -rf out
javac --release 17 -encoding UTF-8 -nowarn -d out -sourcepath "src:test:ferramentas" ferramentas/ru/ferramentas/Cli.java || exit 1
java -Dstdout.encoding=UTF-8 -cp out ru.ferramentas.Cli "$@"
