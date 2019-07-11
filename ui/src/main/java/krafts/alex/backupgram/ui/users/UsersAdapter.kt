package krafts.alex.backupgram.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.squareup.picasso.Picasso
import krafts.alex.backupgram.ui.BackApp
import krafts.alex.backupgram.ui.R
import krafts.alex.backupgram.ui.chatList.ChatListFragmentDirections
import krafts.alex.backupgram.ui.utils.CircleTransform
import krafts.alex.tg.entity.User
import java.io.File

class UsersAdapter(
    private var values: List<User>
) : RecyclerView.Adapter<UserViewHolder>() {

    fun setAll(items: List<User>) {
        values = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = values[position]

        holder.name.text = item.firstName + " " + item.lastName

        item.photoBig?.let {
            if (it.downloaded) {
                Picasso.get()
                    .load(File(it.localPath))
                    .placeholder(R.drawable.ic_users)
                    .transform(CircleTransform())
                    .into(holder.avatar)
            } else {
                BackApp.client?.loadImage(it.fileId)
            }
        }

        holder.avatar.transitionName = "avatar${item.id}"

        with(holder.view) {
            tag = item
            setOnClickListener { v ->
                (v.tag as? User)?.let {
                    Navigation.findNavController(v).navigate(
                        ChatListFragmentDirections.actionChatDetails(it.id.toLong()),
                        FragmentNavigator.Extras.Builder().addSharedElement(
                            holder.avatar, context.getString(R.string.avatar_transition)
                        ).build()
                    )
                }
            }

        }
    }

    override fun getItemCount(): Int = values.size
}