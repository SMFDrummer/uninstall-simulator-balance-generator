#include <iostream>
#include <fstream>
#include <cstdint>
#include <random>

using namespace std;

static const int32_t FILE_VERSION = 1;
static const int32_t MAGIC_CONST = 0x5F3759DF;

static inline int32_t rotl(int32_t value, int bits)
{
	return (value << bits) | (value >> (32 - bits));
}

static int32_t computeChecksum(int32_t value, int32_t key)
{
	int32_t num = value;
	num = (num * 397) ^ key;
	num = num ^ MAGIC_CONST;
	return rotl(num, 7);
}

static int32_t computeFileSignature(
									int32_t version,
									int32_t xorKey,
									int32_t obfuscated,
									int32_t checksum)
{
	int32_t num = version;
	num = (num * 31) ^ xorKey;
	num = (num * 31) ^ obfuscated;
	num = (num * 31) ^ checksum;
	num = num ^ MAGIC_CONST;
	return rotl(num, 11);
}

static void writeIntLE(ofstream& file, int32_t value)
{
	file.put(static_cast<char>(value & 0xFF));
	file.put(static_cast<char>((value >> 8) & 0xFF));
	file.put(static_cast<char>((value >> 16) & 0xFF));
	file.put(static_cast<char>((value >> 24) & 0xFF));
}

int main(int argc, char* argv[])
{
	if (argc < 2)
	{
		cout << "Usage: balance.exe <money>\n";
		return 0;
	}
	
	int64_t input = stoll(argv[1]);
	if (input < 0 || input > INT32_MAX)
	{
		cout << "Money must be between 0 and " << INT32_MAX << endl;
		return 0;
	}
	
	int32_t value = static_cast<int32_t>(input);
	
	random_device rd;
	mt19937 gen(rd());
	uniform_int_distribution<int32_t> dist(INT32_MIN, INT32_MAX);
	
	int32_t xorKey = dist(gen);
	if (xorKey == 0)
		xorKey = 1597463007;
	
	int32_t obfuscated = value ^ xorKey;
	int32_t checksum = computeChecksum(value, xorKey);
	int32_t signature =
	computeFileSignature(FILE_VERSION, xorKey, obfuscated, checksum);
	
	ofstream file("balance.dat", ios::binary | ios::trunc);
	
	writeIntLE(file, FILE_VERSION);
	writeIntLE(file, xorKey);
	writeIntLE(file, obfuscated);
	writeIntLE(file, checksum);
	writeIntLE(file, signature);
	
	file.close();
	
	cout << "balance.dat generated successfully.\n";
	cout << "Money: " << value << endl;
	
	return 0;
}
