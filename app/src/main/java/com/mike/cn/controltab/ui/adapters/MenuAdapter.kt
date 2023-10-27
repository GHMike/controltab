package com.mike.cn.controltab.ui.adapters

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mike.cn.controltab.R
import com.mike.cn.controltab.model.MenuInfoModel
import com.mike.cn.controltab.tools.FileHelper

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
        val resId = FileHelper().getMipmapResId(context, item.image!!)
        if (resId != R.mipmap.ic_launcher_round) {
            Glide.with(context).load(resId).error(R.mipmap.ic_launcher_round)
                .into(holder.getView(R.id.iv_Image))
        } else {
            Glide.with(context).load(item.image).error(R.mipmap.ic_launcher_round)
                .into(holder.getView(R.id.iv_Image))
        }

    }

}