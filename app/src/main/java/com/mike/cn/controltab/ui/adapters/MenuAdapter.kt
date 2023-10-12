package com.mike.cn.controltab.ui.adapters

import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel

/**
 * 菜单适配器
 */
class MenuAdapter() : BaseMultiItemQuickAdapter<MenuInfoModel, BaseViewHolder>() {

    init {
        addItemType(0, R.layout.item_menu)
        addItemType(1, R.layout.item_menu2)
    }

    override fun convert(holder: BaseViewHolder, item: MenuInfoModel) {

        holder.setText(R.id.tv_Name, item.name ?: "")
        Glide.with(context).load(item.image).error(R.mipmap.ic_launcher_round)
            .into(holder.getView(R.id.iv_Image))


    }

}