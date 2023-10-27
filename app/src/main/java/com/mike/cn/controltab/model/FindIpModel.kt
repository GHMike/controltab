package com.mike.cn.controltab.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * 菜单 model
 */
class FindIpModel(override var itemType: Int) : MultiItemEntity {

    var req: String? = ""///set
    var ssid: String? = ""//wifiname
    var pass: String? = ""//1234
    var id: String? = ""//1234
    var port: String? = ""//9999

}