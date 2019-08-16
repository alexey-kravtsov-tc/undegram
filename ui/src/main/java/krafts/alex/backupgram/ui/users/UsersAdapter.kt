package krafts.alex.backupgram.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.squareup.picasso.Picasso
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.chatList.ChatListFragmentDirections
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.ChatWithLastMessage
import krafts.alex.tg.entity.User
import krafts.alex.tg.entity.UserWithSessions
import java.io.File

class UsersAdapter : PagedListAdapter<UserWithSessions, UserViewHolder>(UserDiffCallback()) {

    class UserDiffCallback : DiffUtil.ItemCallback<UserWithSessions>() {
        override fun areItemsTheSame(
            oldItem: UserWithSessions, newItem: UserWithSessions
        ): Boolean = oldItem.user.id == newItem.user.id

        override fun areContentsTheSame(
            oldItem: UserWithSessions, newItem: UserWithSessions
        ): Boolean = oldItem.sessionsTime == newItem.sessionsTime
            && oldItem.start == newItem.start
            && oldItem.finish == newItem.finish
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) } ?: holder.clear()
    }
}