package com.example.gardenwater

import java.lang.ref.WeakReference

class MyThread(val weakReference: WeakReference<MainActivity>): Thread(Runnable {

})