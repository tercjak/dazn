package konradtercjak.daznplayer.model

import com.squareup.moshi.*
import java.util.*

sealed class DaznItem(
    open val id: Int,
    open val title: String,
    open val subtitle: String,
    open val date: Date,
    open val imageUrl: String,
)


@JsonClass(generateAdapter = true)
data class DaznEvent(
    @field:Json(name = "id") override val id: Int,
    @field:Json(name = "title") override val title: String,
    @field:Json(name = "subtitle") override val subtitle: String,
    @field:Json(name = "date") override val date: Date,
    @field:Json(name = "imageUrl") override val imageUrl: String,
    @field:Json(name = "videoUrl") val videoUrl: String,
) : DaznItem(id, title, subtitle, date, imageUrl)

@JsonClass(generateAdapter = true)
data class DaznSchedule(
    @field:Json(name = "id") override val id: Int,
    @field:Json(name = "title") override val title: String,
    @field:Json(name = "subtitle") override val subtitle: String,
    @field:Json(name = "date") override val date: Date,
    @field:Json(name = "imageUrl") override val imageUrl: String,
): DaznItem(id, title, subtitle, date, imageUrl)

