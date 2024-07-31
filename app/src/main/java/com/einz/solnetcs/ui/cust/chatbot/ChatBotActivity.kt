package com.einz.solnetcs.ui.cust.chatbot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.einz.solnetcs.R
import com.einz.solnetcs.databinding.ActivityChatBotBinding
import com.einz.solnetcs.ui.cust.info.FaqActivity
import com.einz.solnetcs.ui.cust.new_ticket.NewTicketActivity
import com.einz.solnetcs.ui.cust.setting.ChangeAlamatActivity
import com.einz.solnetcs.ui.cust.setting.ChangePasswordActivity
import com.einz.solnetcs.ui.cust.setting.ChangePhoneActivity

class ChatBotActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBotBinding
    private lateinit var questionAdapter: ChatBotQuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize your adapters
        questionAdapter = ChatBotQuestionAdapter(listOf(), ::onButtonClicked, ::loadNextQuestion)

        // Set the adapters to your RecyclerViews
        binding.rvQuestion.layoutManager = LinearLayoutManager(this)
        binding.rvQuestion.adapter = questionAdapter

        var firstQuestion = greeting_Question

        val isProblem = intent.getBooleanExtra("isProblem", false)
        if(isProblem) {
            firstQuestion = questionRestartModem
        }
        else {
            firstQuestion = greeting_Question
        }



        // Load the first question
        loadQuestion(firstQuestion)
    }

    private fun onButtonClicked(response: ChatBotResponse) {
        // Handle button click
        when (response.buttonRedirect) {
            1 -> {
                val intent = Intent(this, NewTicketActivity::class.java)
                if(response.problem != null){
                    intent.putExtra("problem", response.problem)
                }
                startActivity(intent)
            }
            2 -> {
                val intent = Intent(this, FaqActivity::class.java)
                startActivity(intent)
            }
            3 -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            4 -> {
                val intent = Intent(this, ChangeAlamatActivity::class.java)
                startActivity(intent)
            }
            5 -> {
                val intent = Intent(this, ChangePhoneActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadNextQuestion(question: ChatBotQuestion) {
        questionAdapter = ChatBotQuestionAdapter(listOf(question), ::onButtonClicked, ::loadNextQuestion)
        binding.rvQuestion.adapter = questionAdapter
    }

    private fun loadQuestion(question: ChatBotQuestion) {
        // Load a question into the question RecyclerView
        questionAdapter = ChatBotQuestionAdapter(listOf(question), ::onButtonClicked, ::loadNextQuestion)
        binding.rvQuestion.adapter = questionAdapter
    }
}