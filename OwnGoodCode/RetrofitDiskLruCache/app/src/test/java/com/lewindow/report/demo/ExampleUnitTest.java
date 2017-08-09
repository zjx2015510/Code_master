package com.lewindow.report.demo;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = ExampleUnitTest.class.getSimpleName();

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void text() {
        List<String> list = new ArrayList<>();
        list.add("zhang1");
        list.add("lisi1");
        list.add("zhang2");
        Observable<Object> ob = Observable.fromArray(list).map(new Function<List<String>, Object>() {
            @Override
            public Object apply(List<String> strings) throws Exception {
                System.out.println("ExampleUnitTest apply: " + strings);
                System.out.println("ExampleUnitTest Thread_name: " + Thread.currentThread().getName());
                return strings.get(0);
            }
        });

        Observable<Object> ob2 = Observable.fromArray(list).map(new Function<List<String>, Object>() {
            @Override
            public Object apply(List<String> strings) throws Exception {
                System.out.println("ExampleUnitTest apply2: " + strings);
                System.out.println("ExampleUnitTest Thread_name2: " + Thread.currentThread().getName());
                return strings.get(0);
            }
        });

        Observable.concat(ob, ob2).firstElement().subscribe();
    }

    private Observable<byte[]> observable1() {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                System.out.println("我是第二个");
            }
        });
    }

    @Test
    public void test2() {
        Observable.create(new ObservableOnSubscribe<Integer>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                System.out.println("Observable emit 1" + "\n");
                e.onNext(1);
                e.onComplete();
                System.out.println("Observable emit 2" + "\n");
                e.onNext(2);
                System.out.println("Observable emit 3" + "\n");
                e.onNext(3);
                e.onComplete();
                System.out.println("Observable emit 4" + "\n");
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() { // 第三步：订阅

            // 第二步：初始化Observer
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println("Observable onNext :" + integer + "\n");
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("onError : value : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete" + "\n");
            }
        });

    }


    @Test
    public void test3() {
        final byte[] data = new byte[2];
        Observable<byte[]> ob1 = Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                if (data != null) {
                    System.out.println("subscribe 1");
                    e.onNext(data);
                } else {
                    System.out.println("subscribe 2");
                    e.onComplete();
                }
            }
        });
        Observable<byte[]> ob2 = Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                System.out.println("subscribe 3:");
                e.onNext(new byte[102]);
            }
        });

        Observable.concat(ob1, ob2).subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] s) throws Exception {
                System.out.println("subscribe 4  :" + s.length);
            }
        });
    }

    @Test
    public void test4() {
        List<String> list = new ArrayList<>();
        list.add("zhang1");
        list.add("lisi1");
        list.add("zhang2");
        Observable.fromArray(list.toArray()).flatMap(new Function<Object, ObservableSource<byte[]>>() {
            @Override
            public ObservableSource<byte[]> apply(Object o) throws Exception {
                System.out.println("结果：" + o);
                return new ObservableSource<byte[]>() {
                    @Override
                    public void subscribe(Observer<? super byte[]> observer) {

                    }
                };
            }
        }).subscribe();
    }


}