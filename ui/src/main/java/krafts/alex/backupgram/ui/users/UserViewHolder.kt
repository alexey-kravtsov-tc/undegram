package krafts.alex.backupgram.ui.users

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.ChatArgument
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.chatList.ChatListFragmentDirections
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.backupgram.ui.utils.toPeriodString
import krafts.alex.tg.entity.UserWithSessions
import java.io.File

class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
    private val avatar: ImageView = view.avatar
    private val time: TextView = view.time
    private val timeLine: UserTimeLine = view.timeline

    fun bind(item: UserWithSessions) {
        name.text = title(item)

        item.user.photoBig?.let {
            if (it.downloaded && File(it.localPath).exists()) {
                Picasso.get()
                    .load(File(it.localPath))
                    .placeholder(R.drawable.ic_users)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .transform(CircleTransform())
                    .error(R.drawable.ic_settings)
                    .into(avatar)
            } else {
//                BackApp.client.loadImage(it.fileId)
            }
        }
//            ?: BackApp.client.getChatInfo(item.user.id.toLong())

        avatar.transitionName = "avatar${item.user.id}"

        time.text = item.sessionsTime.toPeriodString()

        timeLine.showTimeline(item.sessions, item.start, item.finish)

        with(view) {
            tag = item
            setOnClickListener { v ->
                (v.tag as? UserWithSessions)?.let {
                    Navigation.findNavController(v).navigate(
                        ChatListFragmentDirections.actionChatDetails(
                            ChatArgument(
                                it.user.id.toLong(),
                                title(it),
                                it.user.photoBig?.localPath
                            )
                        ),
                        FragmentNavigator.Extras.Builder().addSharedElement(
                            avatar, context.getString(R.string.avatar_transition)
                        ).build()
                    )
                }
            }

        }
    }

    private fun title(item: UserWithSessions) =
        item.user.firstName + " " + item.user.lastName

    fun clear() {
        name.text = null
        avatar.setImageResource(R.drawable.ic_users)
    }
}