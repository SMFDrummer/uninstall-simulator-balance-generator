import java.io.File
import java.util.*

object Balance {

    private const val FILE_VERSION = 1
    private const val MAGIC_CONST = 0x5F3759DF

    @JvmStatic
    fun main() {
        print("请输入货币数量: ")
        val input = readlnOrNull()?.toLongOrNull()

        if (input == null || input < 0 || input > Int.MAX_VALUE) {
            println("输入无效，必须是 0 ~ ${Int.MAX_VALUE}")
            return
        }

        val value = input.toInt()
        val bytes = generateBalanceFile(value)

        println("\n生成的 balance.dat 十六进制数据：")
        println(bytes.joinToString(" ") { "%02X".format(it) })

        File("balance.dat").writeBytes(bytes)

        // 额外校验
        println("\n校验解码余额: ${decodeBalance(bytes)}")
    }

    fun generateBalanceFile(value: Int): ByteArray {
        val rng = Random()
        var xorKey = rng.nextInt()
        if (xorKey == 0) {
            xorKey = 1597463007
        }

        val obfuscated = value xor xorKey
        val checksum = computeChecksum(value, xorKey)
        val signature = computeFileSignature(FILE_VERSION, xorKey, obfuscated, checksum)

        val buffer = ByteArray(20)

        writeIntLE(buffer, 0, FILE_VERSION)
        writeIntLE(buffer, 4, xorKey)
        writeIntLE(buffer, 8, obfuscated)
        writeIntLE(buffer, 12, checksum)
        writeIntLE(buffer, 16, signature)

        return buffer
    }

    private fun computeChecksum(value: Int, key: Int): Int {
        var num = value
        num = (num * 397) xor key
        num = num xor MAGIC_CONST
        return rotl(num, 7)
    }

    private fun computeFileSignature(
        version: Int,
        xorKey: Int,
        obfuscated: Int,
        checksum: Int
    ): Int {
        var num = version
        num = (num * 31) xor xorKey
        num = (num * 31) xor obfuscated
        num = (num * 31) xor checksum
        num = num xor MAGIC_CONST
        return rotl(num, 11)
    }

    private fun rotl(value: Int, bits: Int): Int {
        return (value shl bits) or (value shr (32 - bits))
    }

    private fun writeIntLE(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xFF).toByte()
        buffer[offset + 1] = ((value shr 8) and 0xFF).toByte()
        buffer[offset + 2] = ((value shr 16) and 0xFF).toByte()
        buffer[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun readIntLE(buffer: ByteArray, offset: Int): Int {
        return (buffer[offset].toInt() and 0xFF) or
                ((buffer[offset + 1].toInt() and 0xFF) shl 8) or
                ((buffer[offset + 2].toInt() and 0xFF) shl 16) or
                ((buffer[offset + 3].toInt() and 0xFF) shl 24)
    }

    // 用于测试生成结果是否正确
    fun decodeBalance(buffer: ByteArray): Int {
        val xorKey = readIntLE(buffer, 4)
        val obfuscated = readIntLE(buffer, 8)
        return obfuscated xor xorKey
    }
}