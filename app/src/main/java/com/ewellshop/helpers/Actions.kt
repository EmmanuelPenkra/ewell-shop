package com.ewellshop.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.View.MeasureSpec
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ListView
import com.bumptech.glide.Glide
import com.ewellshop.EwellShop

class Actions {

//    var USER_INFO = hashMapOf<String, Any>()
    var context: Context? = EwellShop.appContext
    var storage: SharedPreferences = context?.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)!!
    var dbHelper: Database = Database(context!!)

    fun saveInfo(key: String, value: Any){
//        USER_INFO[key] = value
        with(storage.edit()){
            when (value){
                is Int -> {
                    putInt(key, value)
                }
                is String -> {
                    putString(key, value)
                }
                is Boolean -> {
                    putBoolean(key, value)
                }
                is Float -> {
                    putFloat(key, value)
                }
                else -> {

                }
            }
            apply()
        }
    }

    fun getInfo(key: String, type: DataType): Any? {
//        if (USER_INFO.containsKey(key)) return USER_INFO[key]
        /*USER_INFO[key] =*/ return when (type){
            DataType.BOOLEAN -> storage.getBoolean(key, false) //BOOLEAN
            DataType.INTEGER -> storage.getInt(key, 0) //INTEGER
            DataType.STRING -> storage.getString(key, "") //STRING
            DataType.FLOAT -> storage.getFloat(key, 0F) //FLOAT
        }
//        return USER_INFO[key]
    }

    fun removeInfo(key: String) {
//        USER_INFO.remove(key)
        storage.edit().remove(key).apply()
    }

    fun removeAll() {
//        USER_INFO.clear()
        storage.edit().clear().apply()
    }

    fun messagePhone(c: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("sms:$phoneNumber")
        c.startActivity(intent)
    }

    fun callPhone(c: Context, phoneNumber: String?) {
        c.startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("sms:", phoneNumber, null)))
    }

    fun hideKeyboard(activity: Activity, view: View) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun db_insert(table: String, values: ContentValues){
        // Gets the data repository in write mode
        dbHelper.writableDatabase.insert(table, null, values)
    }
//
    fun db_select(
    table: String, columns: Array<String>?, where: String?, params: Array<String>?,
    sortOrder: String?
) : ArrayList<HashMap<String, String>> {
        val items = arrayListOf<HashMap<String, String>>()
        with(dbHelper.readableDatabase.query(table, columns, where, params, null, null, sortOrder)) {
            while (moveToNext()) {
                columns?.let {
                    var item = hashMapOf<String, String>()
                    for (column in it){
                        val id = getColumnIndexOrThrow(column)
                        item[column] = getString(id)
                    }
                    items.add(item)
                }
            }
        }
        return items
    }

//    fun db_select(rawQuery: String, params: Array<String>?, columns: Array<String>?) : ArrayList<HashMap<String, String>> {
//        return db_get_items(dbHelper.readableDatabase.rawQuery(rawQuery, params), columns)
//    }

//    fun db_get_items(cursor: Cursor, columns: Array<String>?) : ArrayList<HashMap<String, String>>{
//
//    }

    fun db_delete(table: String, where: String?, params: Array<String>?){
        dbHelper.readableDatabase.delete(table, where, params)
    }

    fun db_update(table: String, values: ContentValues, where: String?, params: Array<String>?){
        dbHelper.writableDatabase.update(table, values, where, params)
    }

    companion object {
        fun showImage(url: String, imageView: ImageView, default: Int = 0 /* R.drawable
                .ic_profile_user */){
            Glide.with(EwellShop.appContext!!)
                .load(url)
//                .error(default)
//                .skipMemoryCache(true)
                .into(imageView)
        }

        fun showAlert(
            context: Context,
            title: String?,
            message: String?,
            action: String? = "Okay",
            listener: DialogInterface.OnClickListener? = null,
            showCancel: Boolean? = false,
            cancellable: Boolean = true
        ) {
            val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
            //                .setMessage(message);
//        builder.setItems(actions, listener)
            builder.setPositiveButton(action, listener)
            if (showCancel!!) builder.setNegativeButton("Cancel", null)
            val dialog = builder.create()

            dialog.setCancelable(cancellable)
            dialog.setCanceledOnTouchOutside(cancellable)

            dialog.show()
        }

        fun showAlert(
            context: Context,
            title: String?,
            message: String?,
            items: ArrayList<String>,
            listener: DialogInterface.OnClickListener
        ){
            val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)

            builder.setItems(items.toTypedArray(), listener)
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        fun setListViewHeightBasedOnChildren(listView: ListView) {
            val listAdapter = listView.adapter
            val numberOfItems = listAdapter.count

            // Get total height of all items.
            var totalItemsHeight = 0
            for (itemPos in 0 until numberOfItems) {
                val item = listAdapter.getView(itemPos, null, listView)
                val px = 500 * listView.resources.displayMetrics.density
                item.measure(
                    MeasureSpec.makeMeasureSpec(px.toInt(), MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                totalItemsHeight += item.measuredHeight
            }

            // Get total height of all item dividers.
            val totalDividersHeight = listView.dividerHeight *
                    (numberOfItems - 1)
            // Get padding
            val totalPadding = listView.paddingTop + listView.paddingBottom

            // Set list height.
            val params = listView.layoutParams
            params.height = totalItemsHeight + totalDividersHeight + totalPadding
            listView.layoutParams = params
            listView.requestLayout()
        }
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

}