package com.mike.cn.controltab.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * 菜单 model
 */
class MenuInfoModel(override val itemType: Int) : MultiItemEntity {

    var name: String? = ""
    var code: String? = ""
    var image: String? = ""
}