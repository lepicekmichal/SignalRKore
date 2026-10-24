package eu.lepicekmichal.signalrkore.msgpack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

fun hex(value: String): ByteArray = value.split(' ').filter { it.isNotEmpty() }.map { it.toInt(16).toByte() }.toByteArray()

class MessagePackTest {

    @Serializable
    data class Person(val firstName: String, val lastName: String, val age: Int = 0)

    @Serializable
    data class DotNetPerson(
        @SerialName("FirstName") val firstName: String,
        @SerialName("Age") val age: Int,
    )

    @Serializable
    enum class Color {
        Red,

        @SerialName("DarkBlue")
        Blue,
    }

    @Serializable
    data class Everything(
        val text: String,
        val number: Long,
        val flag: Boolean,
        val nothing: String?,
        val color: Color,
        val bytes: ByteArray,
        val time: Instant,
        val tags: List<String>,
        val counts: Map<String, Int>,
        val nested: Person?,
    ) {
        override fun equals(other: Any?): Boolean =
            other is Everything && text == other.text && number == other.number && flag == other.flag && nothing == other.nothing &&
                color == other.color && bytes.contentEquals(other.bytes) && time == other.time && tags == other.tags &&
                counts == other.counts && nested == other.nested

        override fun hashCode(): Int = text.hashCode()
    }

    @Serializable
    @JvmInline
    value class Id(val value: Int)

    @Serializable
    sealed class Shape {
        @Serializable
        @SerialName("circle")
        data class Circle(val radius: Double) : Shape()
    }

    @Serializable
    object Empty

    private fun <T> assertEncoded(expected: String, serializer: KSerializer<T>, value: T) {
        assertEquals(expected, MessagePack.encodeToByteArray(serializer, value).toHex())
        assertEquals(value, MessagePack.decodeFromByteArray(serializer, hex(expected.chunked(2).joinToString(" "))))
    }

    @Test
    fun integersUseTheSmallestFormat() {
        assertEncoded("00", Int.serializer(), 0)
        assertEncoded("7f", Int.serializer(), 127)
        assertEncoded("cc80", Int.serializer(), 128)
        assertEncoded("ccff", Int.serializer(), 255)
        assertEncoded("cd0100", Int.serializer(), 256)
        assertEncoded("ce00010000", Int.serializer(), 65536)
        assertEncoded("cf0000000100000000", Long.serializer(), 4294967296)
        assertEncoded("ff", Int.serializer(), -1)
        assertEncoded("e0", Int.serializer(), -32)
        assertEncoded("d0df", Int.serializer(), -33)
        assertEncoded("d1ff7f", Int.serializer(), -129)
        assertEncoded("d280000000", Int.serializer(), Int.MIN_VALUE)
        assertEncoded("d38000000000000000", Long.serializer(), Long.MIN_VALUE)
        assertEncoded("11", Byte.serializer(), 0x11)
        assertEncoded("d1cfc7", Short.serializer(), -12345)
    }

    @Test
    fun integerOutOfRangeFails() {
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Byte.serializer(), hex("cc 80")) }
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Int.serializer(), hex("ce ff ff ff ff")) }
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Long.serializer(), hex("cf ff ff ff ff ff ff ff ff")) }
    }

    @Test
    fun unsignedIntegers() {
        assertEncoded("ccff", UByte.serializer(), UByte.MAX_VALUE)
        assertEncoded("cdffff", UShort.serializer(), UShort.MAX_VALUE)
        assertEncoded("ceffffffff", UInt.serializer(), UInt.MAX_VALUE)
        assertEncoded("cfffffffffffffffff", ULong.serializer(), ULong.MAX_VALUE)
        assertEncoded("2a", UInt.serializer(), 42u)
    }

    @Test
    fun floatingPoint() {
        assertEncoded("cb4004000000000000", Double.serializer(), 2.5)
        assertEncoded("ca40200000", Float.serializer(), 2.5f)
        assertEquals(42.0, MessagePack.decodeFromByteArray(Double.serializer(), hex("2a")))
        assertEquals(2.5f, MessagePack.decodeFromByteArray(Float.serializer(), hex("cb 40 04 00 00 00 00 00 00")))
    }

    @Test
    fun booleansAndNil() {
        assertEncoded("c3", Boolean.serializer(), true)
        assertEncoded("c2", Boolean.serializer(), false)
        assertEncoded("c0", String.serializer().nullable, null)
        assertEncoded("a161", String.serializer().nullable, "a")
    }

    @Test
    fun strings() {
        assertEncoded("a0", String.serializer(), "")
        assertEncoded("a3787a79", String.serializer(), "xzy")
        assertEncoded("a3c3a96c", String.serializer(), "él")
        assertEquals("d920" + "61".repeat(32), MessagePack.encodeToByteArray(String.serializer(), "a".repeat(32)).toHex())
        assertEquals("da0100" + "61".repeat(256), MessagePack.encodeToByteArray(String.serializer(), "a".repeat(256)).toHex())
        assertEquals("a".repeat(70000), MessagePack.decodeFromByteArray(String.serializer(), MessagePack.encodeToByteArray(String.serializer(), "a".repeat(70000))))
    }

    @Test
    fun charsAreIntegers() {
        assertEncoded("63", Char.serializer(), 'c')
        assertEquals('c', MessagePack.decodeFromByteArray(Char.serializer(), hex("a1 63")))
    }

    @Test
    fun byteArraysAreBinary() {
        assertEquals("c403010203", MessagePack.encodeToByteArray(ByteArraySerializer(), byteArrayOf(1, 2, 3)).toHex())
        assertContentEquals(byteArrayOf(1, 2, 3), MessagePack.decodeFromByteArray(ByteArraySerializer(), hex("c4 03 01 02 03")))
        assertContentEquals(byteArrayOf(1, 2, 3), MessagePack.decodeFromByteArray(ByteArraySerializer(), hex("93 01 02 03")))
        assertEquals("c50100" + "00".repeat(256), MessagePack.encodeToByteArray(ByteArraySerializer(), ByteArray(256)).toHex())
        assertEquals("c0", MessagePack.encodeToByteArray(ByteArraySerializer().nullable, null).toHex())
    }

    @Test
    fun instantsAreTimestamps() {
        assertEncoded("d6ff00000000", Instant.serializer(), Instant.fromEpochSeconds(0))
        assertEncoded("d6ff5f5e1000", Instant.serializer(), Instant.fromEpochSeconds(1600000000))
        assertEncoded("d7ff0000000400000000", Instant.serializer(), Instant.fromEpochSeconds(0, 1))
        assertEncoded("d7ff773594005f5e1000", Instant.serializer(), Instant.fromEpochSeconds(1600000000, 500_000_000))
        assertEncoded("c70cff00000000ffffffffffffffff", Instant.serializer(), Instant.fromEpochSeconds(-1))
        assertEncoded("c70cff0000000000000004a817c800", Instant.serializer(), Instant.fromEpochSeconds(20_000_000_000))
    }

    @Test
    fun instantsFromIsoStrings() {
        assertEquals(
            Instant.fromEpochSeconds(1600000000),
            MessagePack.decodeFromByteArray(Instant.serializer(), MessagePack.encodeToByteArray(String.serializer(), "2020-09-13T12:26:40Z")),
        )
    }

    @Test
    fun enumsAreStrings() {
        assertEncoded("a3526564", Color.serializer(), Color.Red)
        assertEncoded("a84461726b426c7565", Color.serializer(), Color.Blue)
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Color.serializer(), hex("a4 42 6c 75 65")) }
    }

    @Test
    fun collections() {
        assertEncoded("9401020304", ListSerializer(Int.serializer()), listOf(1, 2, 3, 4))
        assertEncoded("90", ListSerializer(Int.serializer()), emptyList())
        assertEncoded("dc0010" + "00".repeat(16), ListSerializer(Int.serializer()), List(16) { 0 })
        assertEncoded("9292010291c0", ListSerializer(ListSerializer(Int.serializer().nullable)), listOf(listOf(1, 2), listOf(null)))
        assertEncoded(
            "82a56170706c65a662616e616e61a36b6579a576616c7565",
            MapSerializer(String.serializer(), String.serializer()),
            mapOf("apple" to "banana", "key" to "value"),
        )
        assertEncoded("8101a161", MapSerializer(Int.serializer(), String.serializer()), mapOf(1 to "a"))
    }

    @Test
    fun classesAreMapsKeyedByName() {
        assertEncoded(
            "83a966697273744e616d65a44a6f686ea86c6173744e616d65a3446f65a36167651e",
            Person.serializer(),
            Person("John", "Doe", 30),
        )
        assertEncoded("82a946697273744e616d65a44a6f686ea341676514", DotNetPerson.serializer(), DotNetPerson("John", 20))
    }

    @Test
    fun keysAreCaseSensitive() {
        val bytes = MessagePack.encodeToByteArray(DotNetPerson.serializer(), DotNetPerson("John", 20))

        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Person.serializer(), bytes) }
    }

    @Test
    fun classPropertiesInAnyOrderAndUnknownKeysSkipped() {
        val bytes = hex("84 a3 61 67 65 1e a5 65 78 74 72 61 92 01 81 a1 61 c3 a8 6c 61 73 74 4e 61 6d 65 a3 44 6f 65 a9 66 69 72 73 74 4e 61 6d 65 a4 4a 6f 68 6e")

        assertEquals(Person("John", "Doe", 30), MessagePack.decodeFromByteArray(Person.serializer(), bytes))
        assertFailsWith<SerializationException> {
            MessagePack { ignoreUnknownKeys = false }.decodeFromByteArray(Person.serializer(), bytes)
        }
    }

    @Test
    fun classesFromArraysArePositional() {
        assertEquals(Person("John", "Doe", 30), MessagePack.decodeFromByteArray(Person.serializer(), hex("94 a4 4a 6f 68 6e a3 44 6f 65 1e c3")))
    }

    @Test
    fun defaultsAreEncodedUnlessDisabled() {
        assertEquals(3, MessagePack.encodeToByteArray(Person.serializer(), Person("John", "Doe"))[0].toInt() and 0x0F)
        assertEquals(2, (MessagePack { encodeDefaults = false }).encodeToByteArray(Person.serializer(), Person("John", "Doe"))[0].toInt() and 0x0F)
        assertEquals(Person("John", "Doe"), MessagePack.decodeFromByteArray(Person.serializer(), hex("82 a9 66 69 72 73 74 4e 61 6d 65 a4 4a 6f 68 6e a8 6c 61 73 74 4e 61 6d 65 a3 44 6f 65")))
    }

    @Test
    fun everythingRoundTrips() {
        val value = Everything(
            text = "text",
            number = 3333333333,
            flag = true,
            nothing = null,
            color = Color.Blue,
            bytes = byteArrayOf(-1, 0, 1),
            time = Instant.fromEpochSeconds(1600000000, 123_000_000),
            tags = listOf("a", "b"),
            counts = mapOf("a" to 1),
            nested = Person("John", "Doe", 30),
        )

        assertEquals(value, MessagePack.decodeFromByteArray(Everything.serializer(), MessagePack.encodeToByteArray(Everything.serializer(), value)))
    }

    @Test
    fun valueClassesPolymorphismAndObjects() {
        assertEncoded("2a", Id.serializer(), Id(42))
        assertEncoded("80", Empty.serializer(), Empty)
        assertEncoded("82a474797065a6636972636c65a576616c756581a6726164697573cb3ff8000000000000", Shape.serializer(), Shape.Circle(1.5))
    }

    @Test
    fun malformedDataFails() {
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Int.serializer(), hex("2a 2a")) }
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(String.serializer(), hex("a5 61 62")) }
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(ListSerializer(Int.serializer()), hex("dd ff ff ff ff")) }
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(String.serializer(), hex("2a")) }
        assertFailsWith<SerializationException> { MessagePack.decodeFromByteArray(Int.serializer(), ByteArray(0)) }
    }
}
