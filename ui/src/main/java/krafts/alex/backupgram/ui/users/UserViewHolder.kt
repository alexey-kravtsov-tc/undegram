package krafts.alex.backupgram.ui.users

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.chatList.ChatListFragmentDirections
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.User
import krafts.alex.tg.entity.UserWithSessions
import java.io.File

class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    private val avatar: ImageView = view.avatar

    fun bind(item: UserWithSessions) {
        name.text = item.user.firstName + " " + item.user.lastName

        item.user.photoBig?.let {
            if (it.downloaded) {
                Picasso.get()
                    .load(File(it.localPath))
                    .placeholder(R.drawable.ic_users)
                    .transform(CircleTransform())
                    .into(avatar)
            } else {
                BackApp.client?.loadImage(it.fileId)
            }
        }

        avatar.transitionName = "avatar${item.user.id}"

        with(view) {
            tag = item
            setOnClickListener { v ->
                (v.tag as? UserWithSessions)?.let {
                    Navigation.findNavController(v).navigate(
                        ChatListFragmentDirections.actionChatDetails(it.user.id.toLong()),
                        FragmentNavigator.Extras.Builder().addSharedElement(
                            avatar, context.getString(R.string.avatar_transition)
                        ).build()
                    )
                }
            }

        }
    }

    fun clear() {
        name.text = null
        avatar.setImageResource(R.drawable.ic_users)
    }
}