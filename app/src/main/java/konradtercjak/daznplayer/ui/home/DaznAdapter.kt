package konradtercjak.daznplayer.ui.home

import android.view.*
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import konradtercjak.daznplayer.*
import konradtercjak.daznplayer.databinding.PhotoItemBinding
import konradtercjak.daznplayer.model.*
import konradtercjak.daznplayer.util.*
import java.text.SimpleDateFormat
import java.util.*


class DaznAdapter(private val vm: DaznViewModel) :
    ListAdapter<DaznItem, DaznAdapter.PhotoHolder>(DiffCallback()) {
    var yesterdayFormat = SimpleDateFormat("'Yesterday,' HH:mm", Locale.US)
    var tomorrowFormat = SimpleDateFormat("'Tomorrow,' HH:mm", Locale.US)

    var todayFormat= SimpleDateFormat("'Today,' HH:mm", Locale.US)
    var otherdayFormat= SimpleDateFormat("dd.MM.yyyy", Locale.US)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        return PhotoHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.disposableImage?.dispose()
        val item = getItem(position)
        val url = item.imageUrl

        with(holder.binding) {
            photoIv.setImageResource(R.drawable.placeholder)
            titleTv.text = item.title
            subtitleTv.text = item.subtitle
            val date =item.date

            dateTv.text = when {
                date.isToday() -> todayFormat.format(item.date)
                date.isTomorrow() -> tomorrowFormat.format(item.date)
                date.isYesterday() -> yesterdayFormat.format(item.date)
                else -> otherdayFormat.format(item.date)
            }
        }

        vm.bind(url, holder,item)
    }

    class PhotoHolder(val binding: PhotoItemBinding) : ViewHolder(binding.root) {
        var disposableImage: coil.request.Disposable? = null

        companion object {
            fun from(parent: ViewGroup): PhotoHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PhotoItemBinding.inflate(layoutInflater, parent, false)

                return PhotoHolder(binding)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DaznItem>() {

        override fun areItemsTheSame(oldItem: DaznItem, newItem: DaznItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DaznItem, newItem: DaznItem) =
            oldItem == newItem

    }
}
