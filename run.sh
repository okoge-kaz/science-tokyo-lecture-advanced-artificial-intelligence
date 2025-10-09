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
  rm sources.txt
  echo "✅ Compilation complete."
}

run_report() {
  local report_dir=$1
  local class_name=${2:-$MAIN_CLASS_NAME}
  local main_class="${MAIN_CLASS_PREFIX}${report_dir#report}.${class_name}"

  if [[ ! -f "$SRC_DIR/${report_dir}/${class_name}.java" ]]; then
    echo "❌ ${report_dir}/${class_name}.java not found."
    exit 1
  fi

  echo "🚀 Running ${main_class} ..."
  java -cp "$BIN_DIR" $JAVA_OPTS "${main_class}"
}

case "$1" in
  compile)
    compile_all
    ;;
  report01|report02)
    compile_all
    run_report "$1"
    ;;
  report03)
    compile_all
    if [[ "$2" == "undx" ]]; then
      run_report "$1" "TSUndxMggM"
    else
      run_report "$1"
    fi
    ;;
  report04)
    compile_all
    if [[ "$2" == "arex" ]]; then
      run_report "$1" "TSArexJggM"
    else
      run_report "$1"
    fi
    ;;
  clean)
    echo "🧹 Cleaning build directory..."
    rm -rf "$BIN_DIR"/*
    ;;
  *)
    echo "Usage: $0 {compile|clean|report01|report02|report03 [undx]|report04 [arex]}"
    echo ""
    echo "Examples:"
    echo "  $0 report03        # Runs TSRexNJggM"
    echo "  $0 report03 undx   # Runs TSUndxMggM"
    echo "  $0 report04        # Runs TSRexNJggM"
    echo "  $0 report04 arex   # Runs TSArexJggM"
    exit 1
    ;;
esac
