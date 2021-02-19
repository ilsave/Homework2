package com.example.gardenwater
//
//import java.util.concurrent.ThreadPoolExecutor
//import java.util.concurrent.locks.ReentrantLock
//
////i was trying to create and full my ownn class from developer com
//
//internal class PausableThreadPoolExecutor(): ThreadPoolExecutor() {
//    private var isPaused:Boolean = false
//    private val pauseLock = ReentrantLock()
//    private val unpaused = pauseLock.newCondition()
//
//    protected override fun beforeExecute(t:Thread, r:Runnable) {
//        super.beforeExecute(t, r)
//        pauseLock.lock()
//        try
//        {
//            while (isPaused) unpaused.await()
//        }
//        catch (ie:InterruptedException) {
//            t.interrupt()
//        }
//        finally
//        {
//            pauseLock.unlock()
//        }
//    }
//    fun pause() {
//        pauseLock.lock()
//        try
//        {
//            isPaused = true
//        }
//        finally
//        {
//            pauseLock.unlock()
//        }
//    }
//    fun resume() {
//        pauseLock.lock()
//        try
//        {
//            isPaused = false
//            unpaused.signalAll()
//        }
//        finally
//        {
//            pauseLock.unlock()
//        }
//    }
//}