package com.base.library.permission


abstract class PermissionHandler {

    /** 权限通过的时候(或者权限已经授权)调用  */
    abstract fun onGranted()

    /** 权限被拒绝但没有勾选不再询问的时候调用  */
    open fun onDenied() {}

    /**
     * 权限被拒绝并勾选不再询问的时候调用
     * @return 如果要覆盖原有提示则返回true
     */
    open fun onNeverAsk(): Boolean {
        return false
    }
}