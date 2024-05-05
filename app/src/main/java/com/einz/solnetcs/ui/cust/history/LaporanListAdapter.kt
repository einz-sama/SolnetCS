package com.einz.solnetcs.ui.cust.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.einz.solnetcs.data.model.Laporan
import com.einz.solnetcs.databinding.ItemLaporanBinding
import com.einz.solnetcs.ui.cust.history.LaporanAdapter.MyViewHolder.Companion.DIFF_CALLBACK
import com.einz.solnetcs.util.formatFirebaseTimestamp

class LaporanAdapter (
    private val onClick: (Laporan) -> Unit
) : ListAdapter<Laporan, LaporanAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemLaporanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val list = getItem(position)
        holder.bind(list)
    }

    class MyViewHolder(
        private val binding: ItemLaporanBinding,
        private val onClick: (Laporan) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(laporan: Laporan) {
            binding.apply {
                tvJenis.text = laporan.kategori
                tvDescription.text = laporan.deskripsi
                tvLocation.text = laporan.alamat
                tvDate.text = formatFirebaseTimestamp(laporan.timestamp)

                root.setOnClickListener {
                    onClick(laporan)

                }
            }

        }

        companion object {

            val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Laporan>() {
                override fun areItemsTheSame(
                    oldItem: Laporan,
                    newItem: Laporan
                ): Boolean {
                    return oldItem.idLaporan == newItem.idLaporan
                }

                override fun areContentsTheSame(
                    oldItem: Laporan,
                    newItem: Laporan
                ): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}