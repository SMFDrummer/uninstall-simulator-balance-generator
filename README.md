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

## File Location

On Windows, the save file is typically located at:

`%AppData%\Roaming\Godot\app_userdata\Uninstall Simulator\release\balance.dat`

Note:
- The game uses the Godot `app_userdata` directory, not the game install directory.
- The file is written in overwrite mode.
- Values must be within 0 to 2147483647.
- Generated files are fully compatible with the game's validation logic.

---

## Usage

### Method 1 (Recommended)

Place `balance.exe` inside:

...\Uninstall Simulator\release\

Then run with cmd locate in this folder:

`balance.exe 999999999`

The program will generate and overwrite `balance.dat` in the same directory.

---

### Method 2 (Manual Replace)

1. Run with cmd:

`balance.exe <money>`

2. Copy the generated `balance.dat`
3. Manually overwrite the file at:

`%AppData%\Roaming\Godot\app_userdata\Uninstall Simulator\release\`

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

`g++ balance.cpp -O2 -std=c++17 -o balance.exe`

### Visual Studio

Create a Console Application project and compile normally.

---

## Project Structure

- balance.cpp — core generator implementation
- README.md — documentation
- Balance.kt — kotlin implementation

---

## Disclaimer

This project is not affiliated with the developers of Uninstall Simulator.

It is provided for educational and file format research purposes only.

Use responsibly.

---

## License

MIT License
