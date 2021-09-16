package com.tsp.test.slider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseIntArray
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.tsp.android.common.ext.loadUrl
import com.tsp.android.hiui.slider.HiSliderView
import com.tsp.android.jgs.R
import com.tsp.test.model.Subcategory

class SliderTestActivity : AppCompatActivity() {

    private val SPAN_COUNT = 3

    lateinit var viewModel: SliderViewModel

    private lateinit var sliderView: HiSliderView

    private val subcategoryListCache = mutableMapOf<String, List<Subcategory>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider_test)
        sliderView = findViewById(R.id.slider_view)
        viewModel = ViewModelProvider(this)[SliderViewModel::class.java]
        observer()
    }


    private fun observer() {
        viewModel.queryCategoryList().observe(this) {
            if (it == null) return@observe

            sliderView.bindMenuView(
                itemCount = it.size,
                onBindView = { holder, position ->
                    val category = it[position]
                    holder.findViewById<TextView>(R.id.menu_item_title)?.text =
                        category.categoryName
                },
                onItemClick = { holder, position ->
                    val category = it[position]
                    val categoryId = category.categoryId
                    querySubcategoryList(categoryId)
//                    Toast.makeText(this," touch $category",Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun querySubcategoryList(categoryId: String) {
        viewModel.querySubcategoryList(categoryId).observe(this) {
            if (it == null) return@observe
            decoration.clear()
            groupSpanSizeOffset.clear()
            subcategoryList.clear()
            subcategoryList.addAll(it)
            if (layoutManager.spanSizeLookup != spanSizeLookUp) {
                layoutManager.spanSizeLookup = spanSizeLookUp
            }
            sliderView.bindContentView(
                itemCount = it.size,
                itemDecoration = decoration,
                layoutManager = layoutManager,
                onBindView = { holder, position ->
                    val subcategory = it[position]
                    holder.findViewById<ImageView>(R.id.content_item_image)
                        ?.loadUrl(subcategory.subcategoryIcon)
                    holder.findViewById<TextView>(R.id.content_item_title)?.text =
                        subcategory.subcategoryName
                },
                onItemClick = { holder, position ->
                    //是应该跳转到类目的商品列表页的
                    val subcategory = it[position]
                    Toast.makeText(
                        this,
                        " touch ${subcategory.subcategoryName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

        }
    }

    private val subcategoryList = mutableListOf<Subcategory>()

    private val layoutManager = GridLayoutManager(this, SPAN_COUNT)

    private val groupSpanSizeOffset = SparseIntArray()

    private val decoration = CategoryItemDecoration({ position ->
        subcategoryList[position].groupName
    }, SPAN_COUNT)


    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {

        override fun getSpanSize(position: Int): Int {
            var spanSize = 1
            val groupName: String = subcategoryList[position].groupName
            val nextGroupName: String? =
                if (position + 1 < subcategoryList.size) subcategoryList[position + 1].groupName else null

            if (TextUtils.equals(groupName, nextGroupName)) {
                spanSize = 1
            } else {
                //当前位置和 下一个位置 不再同一个分组
                //1 .要拿到当前组 position （所在组）在 groupSpanSizeOffset 的索引下标
                //2 .拿到 当前组前面一组 存储的 spansizeoffset 偏移量
                //3 .给当前组最后一个item 分配 spansize count
                val indexOfKey = groupSpanSizeOffset.indexOfKey(position)
                val size = groupSpanSizeOffset.size()
                val lastGroupOffset = if (size <= 0) 0
                else if (indexOfKey >= 0) {
                    //说明当前组的偏移量记录，已经存在了 groupSpanSizeOffset ，这个情况发生在上下滑动，
                    if (indexOfKey == 0) 0 else groupSpanSizeOffset.valueAt(indexOfKey - 1)
                } else {
                    //说明当前组的偏移量记录，还没有存在于 groupSpanSizeOffset ，这个情况发生在 第一次布局的时候
                    //得到前面所有组的偏移量之和
                    groupSpanSizeOffset.valueAt(size - 1)
                }
                //          3       -     (6     +    5               % 3  )第几列=0  ，1 ，2
                spanSize = SPAN_COUNT - (position + lastGroupOffset) % SPAN_COUNT
                if (indexOfKey < 0) {
                    //得到当前组 和前面所有组的spansize 偏移量之和
                    val groupOffset = lastGroupOffset + spanSize - 1
                    groupSpanSizeOffset.put(position, groupOffset)
                }
            }
            return spanSize
        }
    }
}