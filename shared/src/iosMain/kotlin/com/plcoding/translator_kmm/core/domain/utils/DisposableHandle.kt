package com.plcoding.translator_kmm.core.domain.utils


fun interface DisposableHandle: kotlinx.coroutines.DisposableHandle

//fun DisposableHandle(block: () -> Unit): DisposableHandle {
//    return object : DisposableHandle{
//        override fun dispose() {
//            block()
//        }
//    }
//}
