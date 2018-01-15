package com.tasomaniac.devdrawer.widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.IdRes
import android.view.View
import android.widget.RemoteViews
import com.tasomaniac.devdrawer.R
import com.tasomaniac.devdrawer.configure.ConfigureActivity
import com.tasomaniac.devdrawer.data.Widget
import com.tasomaniac.devdrawer.settings.OpacityPreferences
import javax.inject.Inject

class RemoveViewsCreator @Inject constructor(
    private val app: Application,
    private val widgetResources: WidgetResources,
    private val opacityPreferences: OpacityPreferences
) {

  fun create(widget: Widget) = RemoteViews(app.packageName, R.layout.app_widget).apply {

    fun RemoteViews.setupConfigureButton(@IdRes buttonId: Int) {
      setContentDescription(buttonId, app.getString(R.string.widget_content_description_configure, widget))
      val intent = ConfigureActivity.createIntent(app, widget.appWidgetId)
          .toPendingActivity(app)
      setOnClickPendingIntent(buttonId, intent)
    }

    fun remoteAdapter(context: Context): Intent {
      return Intent(context, WidgetViewsService::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.appWidgetId)
        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
      }
    }

    if (widget.name.isEmpty()) {
      setViewVisibility(R.id.widgetHeader, View.GONE)
    } else {
      setViewVisibility(R.id.widgetHeader, View.VISIBLE)
      setTextViewText(R.id.widgetTitle, widget.name)
      setTextColor(R.id.widgetTitle, widgetResources.foregroundColor)

      setupConfigureButton(R.id.widgetConfigure)
      setImageViewResource(R.id.widgetConfigure, widgetResources.settingsIcon)
    }

    val shadeColor = opacityPreferences.backgroundColor
    setInt(R.id.shade, "setBackgroundColor", shadeColor)
    setViewVisibility(R.id.shade, if (shadeColor == 0) View.GONE else View.VISIBLE)

    setRemoteAdapter(R.id.widgetAppList, remoteAdapter(app))

    setEmptyView(R.id.widgetAppList, R.id.widgetEmpty)
    setTextColor(R.id.widgetEmpty, widgetResources.foregroundColor)
    setTextViewCompoundDrawablesRelative(R.id.widgetEmpty, 0, 0, widgetResources.settingsIcon, 0)
    setupConfigureButton(R.id.widgetEmpty)

    val intentTemplate = ClickHandlingActivity.intent(app).toPendingActivity(app)
    setPendingIntentTemplate(R.id.widgetAppList, intentTemplate)

  }

}