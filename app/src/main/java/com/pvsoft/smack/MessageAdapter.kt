package com.pvsoft.smack

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pvsoft.smack.Models.Message
import com.pvsoft.smack.Services.UserDataService
import org.w3c.dom.Text

/**
 * Created by minhp on 1/14/2018.
 */
class MessageAdapter constructor(context: Context, messageList: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val context = context
    val messageList = messageList

    override fun getItemCount(): Int {
        return messageList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindMessage(context, messageList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val messageUserImg = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val messageBodyLbl = itemView?.findViewById<TextView>(R.id.messageBodyLbl)
        val userNameLbl = itemView?.findViewById<TextView>(R.id.messageUserNameLbl)
        val timeStampLbl = itemView?.findViewById<TextView>(R.id.timeStampLbl)

        fun bindMessage(context: Context, message: Message) {
            val imageResourceId = context.resources.getIdentifier(message.userAvatarName, "drawable", context.packageName)
            val imageColor = message.userAvatarColor
            messageUserImg?.setImageResource(imageResourceId)
            messageUserImg?.setBackgroundColor(UserDataService.returnAvatarColor(imageColor))

            messageBodyLbl?.text = message.message
            userNameLbl?.text = message.userName
            timeStampLbl?.text = message.timeStamp
        }
    }
}