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
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import com.nagopy.android.aplin.databinding.FragmentAppListBinding
import com.nagopy.android.aplin.databinding.ListItemBinding
import com.nagopy.android.aplin.loader.AppInfo

class AppListFragment : Fragment() {

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

    lateinit var binding: FragmentAppListBinding
    val category: Category by lazy {
        arguments!!.getSerializable("category") as Category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(appKodein())
        navigator = injector.kodein().value.with(activity as Activity).instance()
        recycledViewPool = injector.kodein().value.with(activity as Activity).instance()
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
            adapter = AppListAdapter(
                    mainViewModel.getLoadedApplicationList().filter(category.predicate)
                    , navigator)
        }
    }

    class AppViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        val binding = ListItemBinding.bind(parent)
    }

    class AppListAdapter(val appList: List<AppInfo>
                         , val navigator: Navigator) : RecyclerView.Adapter<AppViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            val vh = AppViewHolder(v)
            vh.binding.root.setOnClickListener {
                navigator.startApplicationDetailSettings(appList[vh.adapterPosition].packageName)
            }
            return vh
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            holder.binding.appInfo = appList[position]
        }

        override fun getItemCount(): Int {
            return appList.size
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
    }

}
