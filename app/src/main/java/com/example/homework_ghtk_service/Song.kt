
import java.io.Serializable

data class Song(
    var title: String = "",
    var image: Int? = null,
    var url: Int? = null,
    var artist: String = "",
) : Serializable