package com.nagopy.android.aplin

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import com.nagopy.android.aplin.databinding.FragmentAppListBinding
import com.nagopy.android.aplin.databinding.ListItemBinding
import com.nagopy.android.aplin.loader.AppInfo
import java.text.Collator
import java.util.*
import kotlin.collections.ArrayList

class AppListFragment : Fragment(), MainViewModel.OnSearchTextListener {

    val injector = KodeinInjector()

    val mainViewModelFactory: MainViewModel.Factory by injector.instance()
    val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(activity!!, mainViewModelFactory).get(MainViewModel::class.java)
    }

    val appListViewModelFactory: AppListViewModel.Factory by injector.instance()
    val appListViewModel: AppListViewModel by lazy {
        ViewModelProviders.of(this, appListViewModelFactory).get(AppListViewModel::class.java)
    }

    lateinit var navigator: Navigator
    lateinit var recycledViewPool: RecyclerView.RecycledViewPool
    lateinit var adapter: AppListAdapter

    lateinit var binding: FragmentAppListBinding
    val category: Category by lazy {
        arguments!!.getSerializable("category") as Category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(appKodein())
        navigator = injector.kodein().value.with(activity as Activity).instance()
        recycledViewPool = injector.kodein().value.with(activity as Activity).instance()
        adapter = AppListAdapter(
                mainViewModel.getLoadedApplicationList().filter(category.predicate)
                , { navigator.startApplicationDetailSettings(it.packageName) })
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.addOnSearchTextListener(this)
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.removeOnSearchTextListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAppListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            recycledViewPool = this@AppListFragment.recycledViewPool
            adapter = this@AppListFragment.adapter
        }
    }

    override fun onSearchTextChange(newText: String?) {
        val adapter = binding.recyclerView.adapter as AppListAdapter
        adapter.filter.filter(newText)
    }

    class AppViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val binding = ListItemBinding.bind(parent)
    }

    class AppListAdapter(val appList: List<AppInfo>
                         , val onItemClickListener: (AppInfo) -> Unit) : RecyclerView.Adapter<AppViewHolder>(), Filterable {

        var filteredAppList = ArrayList<AppInfo>(appList)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            val vh = AppViewHolder(v)
            vh.binding.root.setOnClickListener {
                onItemClickListener.invoke(filteredAppList[vh.adapterPosition])
            }
            return vh
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            holder.binding.appInfo = filteredAppList[position]
        }

        override fun getItemCount(): Int {
            return filteredAppList.size
        }

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtered: List<AppInfo>
                if (constraint.isNullOrEmpty()) {
                    filtered = appList
                } else {
                    filtered = appList.filter {
                        it.packageName.contains(constraint!!, true)
                                || it.label.contains(constraint, true)
                    }
                }
                return FilterResults().apply { values = filtered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.let {
                    val newList = it.values as List<AppInfo>
                    appList.forEach {
                        if (newList.contains(it)) {
                            // 表示するアプリ
                            if (!filteredAppList.contains(it)) {
                                //  非表示→表示
                                filteredAppList.add(it)
                                filteredAppList.sortWith(ALPHA_COMPARATOR)
                                val index = filteredAppList.indexOf(it)
                                notifyItemInserted(index)
                                notifyItemRangeChanged(index, filteredAppList.size)
                            }
                        } else {
                            // 非表示にするアプリ
                            if (filteredAppList.contains(it)) {
                                // 表示→非表示
                                val index = filteredAppList.indexOf(it)
                                filteredAppList.removeAt(index)
                                notifyItemRemoved(index)
                                notifyItemRangeChanged(index, filteredAppList.size)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(category: Category): AppListFragment {
            val f = AppListFragment()
            f.arguments = Bundle().apply {
                putSerializable("category", category)
            }
            return f
        }

        // from android-8.1.0_r1.0, ApplicationsState.java
        val ALPHA_COMPARATOR = object : Comparator<AppInfo> {

            val collator = Collator.getInstance()

            override fun compare(app1: AppInfo, app2: AppInfo): Int {
                var compareResult = collator.compare(app1.label, app2.label)
                if (compareResult != 0) {
                    return compareResult
                }
                compareResult = collator.compare(app1.packageName, app2.packageName)
                if (compareResult != 0) {
                    return compareResult
                }
                //return object1.info.uid - object2.info.uid
                return 0
            }

        }
    }

}
