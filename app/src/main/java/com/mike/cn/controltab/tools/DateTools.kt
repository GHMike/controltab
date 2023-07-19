package com.mike.cn.controltab.tools

import java.util.*

class DateTools {


    private fun getDayOfWeekName(dayOfWeek: Int): String? {
        val daysOfWeek = arrayOf("", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        return daysOfWeek[dayOfWeek]
    }


    public fun getNowDateDayWeek(): String {
        // 获取当前日期和时间
        val calendar: Calendar = Calendar.getInstance()
        val currentDate: Date = calendar.getTime()


        // 获取月份、日期和星期几
        val year: Int = calendar.get(Calendar.YEAR)  //
        val month: Int = calendar.get(Calendar.MONTH) + 1 // 月份从0开始计数，所以需要加1

        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK)


        // 星期几的名称
        val dayOfWeekName = getDayOfWeekName(dayOfWeek)

        return year.toString() + "年" + month + "月" + day + "日 " + dayOfWeekName
    }
}