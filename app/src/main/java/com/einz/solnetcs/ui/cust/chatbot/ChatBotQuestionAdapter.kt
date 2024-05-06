package com.einz.solnetcs.ui.cust.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.einz.solnetcs.R
import com.einz.solnetcs.databinding.ItemChatbotQuestionBinding

class ChatBotQuestionAdapter(private val questions: List<ChatBotQuestion>, private val onButtonClicked: (ChatBotResponse) -> Unit, private val loadNextQuestion: (ChatBotQuestion) -> Unit) : RecyclerView.Adapter<ChatBotQuestionAdapter.ChatBotQuestionViewHolder>() {

    inner class ChatBotQuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemChatbotQuestionBinding.bind(view)
        private val responseAdapter = ChatBotResponseAdapter(listOf(), onButtonClicked, loadNextQuestion)

        init {
            binding.rvResponse.layoutManager = LinearLayoutManager(view.context)
            binding.rvResponse.adapter = responseAdapter
        }

        fun bind(question: ChatBotQuestion) {
            binding.tvText.text = question.textString
            responseAdapter.responses = question.responses
            responseAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBotQuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatbot_question, parent, false)
        return ChatBotQuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatBotQuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount() = questions.size
}