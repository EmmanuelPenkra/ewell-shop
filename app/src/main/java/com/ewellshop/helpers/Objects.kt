package com.ewellshop.helpers

import java.io.Serializable

data class Order (
    var id: Int
) : Serializable

data class Item (
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    val price: Double,
    var condition: ItemCondition?,
    var state: XBusinessInventory
) : Serializable

data class OrderItem (
    val id: Int
)

data class Setting (
    val id: Int,
    val main: String,
    val sub: String,
    val divider: Boolean = false
)

data class Location (
    val id: Int,
    val name: String,
    val parent: Int? = null
)