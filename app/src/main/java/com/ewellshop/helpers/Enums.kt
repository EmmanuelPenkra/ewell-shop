package com.ewellshop.helpers

enum class DataType {
    BOOLEAN,
    INTEGER,
    STRING,
    FLOAT
}

enum class AccountType {
    NEW_OWNER,
    NEW_EMPLOYEE,
    SIGN_IN
}

enum class PinType {
    CREATE,
    CONFIRM,
    AUTHORIZE,
    LOGIN
}

enum class ItemCondition(val value: Int) {
    NEW(0),
    FRESH(1),
    OPEN_BOX(2),
    SLIGHTLY_USED(3),
    REFURBISHED(4),
    USED(5)
}



/***
 * STATES
***/

enum class XBusinessEmployees(val value: Int) {
    MANAGER(0),
    EMPLOYEE(1),
    PAUSED(2),
    REMOVED(3)
}

enum class XBusinessInventory(val value: Int) {
    AVAILABLE(0),
    UNAVAILABLE(1),
    REMOVED(2)
}