package app.gatherround.places

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Place(
    val id: Int,
    val title: String,
    val address: String,
    val coords: Coordinates? = null,
    // @Serializable(with = SubwayDeserializer::class)
    val subway: String,
) {
    @Serializable
    data class Coordinates(
        val lat: Double?,
        val lon: Double?
    )

    /*
    object SubwayDeserializer : KSerializer<List<String>> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Subway", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: List<String>) {
            encoder.encodeString(value.joinToString(", "))
        }

        override fun deserialize(decoder: Decoder): List<String> {
            val subwayString = decoder.decodeString()
            return subwayString.split(",").map { it.trim() }
        }
    }
     */
}