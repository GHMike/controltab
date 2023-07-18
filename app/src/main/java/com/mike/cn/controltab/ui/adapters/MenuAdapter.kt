package com.mike.cn.controltab.ui.adapters

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
    }

    override fun convert(holder: BaseViewHolder, item: MenuInfoModel) {

        holder.setText(R.id.tv_Name, item.name ?: "")


    }

}