package com.tasomaniac.devdrawer.main

import com.tasomaniac.devdrawer.data.WidgetDao
import com.tasomaniac.devdrawer.data.Widget
import com.tasomaniac.devdrawer.widget.WidgetDataResolver
import io.reactivex.Flowable
import javax.inject.Inject

class MainUseCase @Inject constructor(
    private val widgetDao: WidgetDao,
    private val widgetDataResolver: WidgetDataResolver) {

  fun observeWidgets(): Flowable<List<WidgetListData>> {
    return widgetDao.allWidgetsWithPackages()
        .map { widgets ->
          widgets.map {
            val widgetData = it.packageNames
                .mapNotNull(widgetDataResolver::resolve)
            WidgetListData(Widget(it.appWidgetId, it.name), widgetData)
          }
        }
  }
}
