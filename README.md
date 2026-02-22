# Uninstall Simulator Balance Tool

A small command-line utility for generating valid `balance.dat` save files used by **Uninstall Simulator**.

This project reimplements the game's balance file format based on analysis of the original `MoneyManager` logic.

---

## Overview

The tool generates a valid `balance.dat` file that passes:

- Memory integrity validation
- File signature verification
- Version checks

The implementation matches the behavior of the original C# logic used in the game.

---

## File Format Specification

The `balance.dat` file contains 5 little-endian 32-bit signed integers (20 bytes total):

Offset | Field
------ | -----
0      | File version (always 1)
4      | XOR key
8      | Obfuscated balance (value XOR key)
12     | Checksum
16     | File signature

All operations use 32-bit signed integer overflow semantics.

---

## Build

### MinGW / g++

g++ balance.cpp -O2 -std=c++17 -o balance.exe

### Visual Studio

Create a Console Application project and compile normally.

---

## Usage

balance.exe <money>

Example:

balance.exe 999999999

This generates a valid `balance.dat` file in the current directory.

---

## Behavior

- The file is written in overwrite mode.
- Values must be within 0 to 2147483647.
- Generated files are fully compatible with the game's validation logic.

---

## Project Structure

- balance.cpp — core generator implementation
- README.md — documentation
- balance.kt — kotlin implementation

---

## Disclaimer

This project is not affiliated with the developers of Uninstall Simulator.

It is provided for educational and file format research purposes only.

Use responsibly.

---

## License

MIT License
