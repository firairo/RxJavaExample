package com.lexshi.rxjavaexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonObservable = findViewById<Button>(R.id.button_observable)
        val buttonFlowable = findViewById<Button>(R.id.button_flowable)

    //first example
        val observable = Observable.just(1,2,3)

        val dispose = observable.subscribe {
            Log.e("TAG", "new data $it")
        }

    //observable example
        val dataObservable = dataSourceObserver()
            .subscribeOn(Schedulers.io()) // Подписываемся на данные в новом потоке и получаем их
            .observeOn(AndroidSchedulers.mainThread()) //Отдаем данные в Main поток
            .subscribe ({ // Делаем что-то с данными в Main потоке
                buttonObservable.text = "buttonObservable next int $it"
                Log.e("TAG", "main thread buttonObservable next int $it")
            }, {
                Log.e("TAG", "it ${it.localizedMessage}")
            }, {

            })



    //flowable example
        val dataFlowable = dataSourceFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                buttonFlowable.text = "buttonFlowable next int $it"
                Log.e("TAG", "main thread buttonFlowable next int $it")

            }, {
                Log.e("TAG", "it ${it.localizedMessage}")
                Toast.makeText(applicationContext, "it ${it.localizedMessage}", Toast.LENGTH_LONG).show()


            }, {

            })


    }


    private fun dataSourceObserver(): Observable<Int> {
        return Observable.create { subscriber ->
            for (i in 0..100){
                subscriber.onNext(i)
            }
        }
    }

    // BackpressureStrategy - Стратегия если много данных. Есть MISSING, ERROR, BUFFER, DROP, LATEST.
    // MISSING - Signals a MissingBackpressureException in case the downstream can't keep up.
    // ERROR - Выкидываем ошибку
    // BUFFER - кэшируем и постепенно выдаем
    // DROP - скипаем часть данных
    // LATEST - выводим последние

    private fun dataSourceFlowable(): Flowable<Int> {
        return Flowable.create ({ subscriber ->
            for (i in 0..1000000){
                subscriber.onNext(i)
            }

            subscriber.onComplete()
        }, BackpressureStrategy.BUFFER)
    }
}