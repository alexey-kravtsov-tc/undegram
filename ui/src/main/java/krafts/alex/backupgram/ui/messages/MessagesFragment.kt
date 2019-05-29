package krafts.alex.backupgram.ui.messages

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import krafts.alex.backupgram.ui.R
import krafts.alex.tg.entity.Message
import krafts.alex.tg.repo.MessagesRepository

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [MessagesFragment.OnListFragmentInteractionListener] interface.
 */
class MessagesFragment : Fragment() {


    //TODO: view model

    private var listener: OnListFragmentInteractionListener? = null

    // TODO: Customize parameters


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages_list, container, false)

        val adapt = TgMessagesRecyclerViewAdapter(emptyList(), listener) //TODO: listadapter paging


        MessagesRepository(context!!).getAllRemoved().observe(this, Observer {
            it?.let { adapt.setAll(it) }
        }
        )

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = adapt
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(msg: Message?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                MessagesFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
