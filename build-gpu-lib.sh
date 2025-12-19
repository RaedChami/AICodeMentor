#!/bin/bash
set -e

LIBGPU_DIR="libgpu"
BUILD_DIR="target/java-llama.cpp"

if [ ! -d "$BUILD_DIR" ]; then
    git clone https://github.com/kherud/java-llama.cpp "$BUILD_DIR"
fi

cd "$BUILD_DIR"

mvn compile -Dmaven.compiler.release=25
mkdir -p build
cd build

cmake .. -DJLLAMA_BUILD_JNI=ON -DGGML_CUDA=ON
cmake --build . --config Release -j 4

cd ..
mkdir -p "../../$LIBGPU_DIR"
find . -name "libjllama.so" -exec cp {} "../../$LIBGPU_DIR/" \;

echo "lib GPU compilée dans $LIBGPU_DIR"