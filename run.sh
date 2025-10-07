#!/bin/bash
set -e


SRC_DIR="src"
BIN_DIR="bin"
MAIN_CLASS_PREFIX="report"
MAIN_CLASS_NAME="TSRexNJggM"
JAVA_OPTS=""


compile_all() {
  echo "🧩 Compiling all Java sources..."
  find "$SRC_DIR" -name "*.java" > sources.txt
  javac -d "$BIN_DIR" @sources.txt
  echo "✅ Compilation complete."
}

run_report() {
  local report_dir=$1
  local main_class="${MAIN_CLASS_PREFIX}${report_dir#report}.${MAIN_CLASS_NAME}"

  if [[ ! -f "$SRC_DIR/${report_dir}/${MAIN_CLASS_NAME}.java" ]]; then
    echo "❌ ${report_dir}/${MAIN_CLASS_NAME}.java not found."
    exit 1
  fi

  echo "🚀 Running ${main_class} ..."
  java -cp "$BIN_DIR" $JAVA_OPTS "${main_class}"
}


case "$1" in
  compile)
    compile_all
    ;;
  report01|report02|report03|report04)
    compile_all
    run_report "$1"
    ;;
  clean)
    echo "🧹 Cleaning build directory..."
    rm -rf "$BIN_DIR"/*
    ;;
  *)
    echo "Usage: $0 {compile|clean|report01|report02|report03|report04}"
    exit 1
    ;;
esac
