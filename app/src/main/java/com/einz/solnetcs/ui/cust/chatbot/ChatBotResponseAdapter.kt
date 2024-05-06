package com.einz.solnetcs.ui.cust.chatbot

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.einz.solnetcs.R
import com.einz.solnetcs.databinding.ItemChatbotResponseBinding
import com.einz.solnetcs.ui.cust.info.FaqActivity
import com.einz.solnetcs.ui.cust.new_ticket.NewTicketActivity
import com.einz.solnetcs.ui.cust.setting.ChangeAlamatActivity
import com.einz.solnetcs.ui.cust.setting.ChangePasswordActivity
import com.einz.solnetcs.ui.cust.setting.ChangePhoneActivity

class ChatBotResponseAdapter(var responses: List<ChatBotResponse>, private val onButtonClicked: (ChatBotResponse) -> Unit, private val loadNextQuestion: (ChatBotQuestion) -> Unit) : RecyclerView.Adapter<ChatBotResponseAdapter.ChatBotResponseViewHolder>() {

    inner class ChatBotResponseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemChatbotResponseBinding.bind(view)

        fun bind(response: ChatBotResponse) {
            binding.tvText.text = response.responseText
            if (response.button) {
                binding.btnAction.visibility = View.VISIBLE
                binding.btnAction.text = getButtonText(response.buttonRedirect)
                binding.btnAction.setOnClickListener {
                    onButtonClicked(response)
                }
            } else {
                binding.btnAction.visibility = View.GONE
                itemView.setOnClickListener {
                    response.redirect?.let { nextQuestion ->
                        loadNextQuestion(nextQuestion)
                    }
                }
            }


        }

        private fun getButtonText(buttonRedirect: Int): String {
            // Replace this with your own logic to get the button text based on the buttonRedirect value
            when (buttonRedirect) {
                1 -> {
                    return "Buat Tiket Laporan"
                }
                2 -> {
                    return "Info Lainya"
                }
                3 -> {
                    return "Ubah Password"
                }
                4 -> {
                    return "Ubah Alamat"
                }
                5 -> {
                    return "Ubah Nomor HP"
                }
            }
            return "Button $buttonRedirect"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBotResponseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatbot_response, parent, false)
        return ChatBotResponseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatBotResponseViewHolder, position: Int) {
        val response = responses[position]
        holder.bind(response)
    }

    override fun getItemCount() = responses.size
}