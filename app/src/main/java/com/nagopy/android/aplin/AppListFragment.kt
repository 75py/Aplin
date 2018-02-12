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
import timber.log.Timber
import java.text.Collator
import java.util.*
import kotlin.collections.ArrayList

class AppListFragment : Fragment(), MainViewModel.OnSearchTextListener, MainViewModel.OnAppInfoChangeListener {

    val injector = KodeinInjector()

    lateinit var mainViewModelFactory: MainViewModel.Factory
    val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(activity!!, mainViewModelFactory).get(MainViewModel::class.java)
    }

    val appListViewModelFactory: AppListViewModel.Factory by injector.instance()
    val appListViewModel: AppListViewModel by lazy {
        ViewModelProviders.of(this, appListViewModelFactory).get(AppListViewModel::class.java)
    }

    lateinit var recycledViewPool: RecyclerView.RecycledViewPool
    lateinit var adapter: AppListAdapter

    lateinit var binding: FragmentAppListBinding
    val category: Category by lazy {
        arguments!!.getSerializable("category") as Category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(appKodein())
        mainViewModelFactory = injector.kodein().value.with(activity as Activity).instance()
        recycledViewPool = injector.kodein().value.with(activity as Activity).instance()
        adapter = AppListAdapter(getAppList(), {
            Timber.d("onclick %s", it)
            Timber.d("%s", category)
            mainViewModel.onAppClick(it.packageName)
        })
        mainViewModel.addOnAppInfoChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.addOnSearchTextListener(this)
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.removeOnSearchTextListener(this)
    }

    override fun onDestroy() {
        mainViewModel.removeOnAppInfoChangeListener(this)
        super.onDestroy()
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

    override fun onAppChange(packageName: String) {
        Timber.d("%s onAppChange %s", category, packageName)
        adapter.appList = mainViewModel.getLoadedApplicationList().filter(category.predicate).sortedWith(ALPHA_COMPARATOR)

        val newAppInfo = adapter.appList.find { it.packageName == packageName }
        Timber.d("%s, newAppInfo:%s", packageName, newAppInfo)
        val oldAppInfo = adapter.filteredAppList.find { it.packageName == packageName }
        Timber.d("%s, oldAppInfo:%s", packageName, oldAppInfo)

        if (newAppInfo == null) {
            // このカテゴリ外のアプリ
            if (oldAppInfo == null) {
                // もともと表示されていなかった
            } else {
                // もともと表示されていた
                val index = adapter.filteredAppList.indexOf(oldAppInfo)
                adapter.filteredAppList.removeAt(index)
                adapter.notifyItemRemoved(index)
            }
        } else {
            // このカテゴリのアプリ
            if (oldAppInfo == null) {
                // もともと表示されていなかった
                adapter.updateFilter()
            } else {
                // もともと表示されていた
                val index = adapter.filteredAppList.indexOf(oldAppInfo)
                adapter.filteredAppList.removeAt(index)
                adapter.notifyItemRemoved(index)
                adapter.updateFilter()
            }
        }
    }

    override fun onAppRemove(packageName: String) {
        adapter.appList = getAppList()
        val oldAppInfo = adapter.filteredAppList.find { it.packageName == packageName }
        if (oldAppInfo != null) {
            val index = adapter.filteredAppList.indexOf(oldAppInfo)
            adapter.filteredAppList.removeAt(index)
            adapter.notifyItemChanged(index)
        }
    }

    override fun onSearchTextChange(newText: String?) {
        adapter.updateFilter(newText)
    }

    class AppViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val binding = ListItemBinding.bind(parent)
    }

    private fun getAppList() = mainViewModel.getLoadedApplicationList().filter(category.predicate).sortedWith(ALPHA_COMPARATOR)

    class AppListAdapter(var appList: List<AppInfo>
                         , val onItemClickListener: (AppInfo) -> Unit) : RecyclerView.Adapter<AppViewHolder>(), Filterable {

        var filteredAppList = ArrayList<AppInfo>(appList)
        var filterText = ""

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return AppViewHolder(v)
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            holder.binding.appInfo = filteredAppList[position]
            holder.binding.root.setOnClickListener {
                Timber.v("onClick %s", filteredAppList)
                onItemClickListener.invoke(filteredAppList[holder.adapterPosition])
            }
        }

        override fun getItemCount(): Int {
            return filteredAppList.size
        }

        fun updateFilter(text: String? = filterText) {
            filter.filter(text)
        }

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                filterText = constraint?.toString() ?: "" // cache

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
                            Timber.d("表示するアプリ, %s", it.packageName)

                            if (!filteredAppList.contains(it)) {
                                Timber.d("非表示→表示 %s", it.packageName)

                                filteredAppList.add(it)
                                filteredAppList.sortWith(ALPHA_COMPARATOR)
                                val index = filteredAppList.indexOf(it)
                                notifyItemInserted(index)
                                notifyItemRangeChanged(index, filteredAppList.size)
                            }
                        } else {
                            Timber.d("非表示にするアプリ %s", it.packageName)

                            if (filteredAppList.contains(it)) {
                                Timber.d("表示→非表示, %s", it.packageName)

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
